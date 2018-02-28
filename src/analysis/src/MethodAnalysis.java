package analysis.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import component.MethodNode;

public class MethodAnalysis extends ASTVisitor {
	//分析函数需要的上下文
	private String packageName = null;
	private String className = null;
	private List<MethodNode> methodNodeList = null;
	//当前分析函数的名称
	private String currentMethodName = null;
	
	public MethodAnalysis(String packageName,String className) {
		this.packageName = packageName;
		this.className = className;
		methodNodeList = new ArrayList<MethodNode>();
	}
	
	public List<MethodNode> getMethodNodeList() {
		return methodNodeList;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		//忽略虚函数
		Block methodBlock = node.getBody();
		if(methodBlock == null){
			return true;
		}
		ASTNode parentNode = node.getParent();
		//忽略匿名内部类中的函数
		if(parentNode instanceof TypeDeclaration){
			String parentName = ((TypeDeclaration) parentNode).getName().toString(); 
			//如果当前类是一级内部类
			if(parentNode.getParent() instanceof TypeDeclaration){
				parentName = ((TypeDeclaration) parentNode.getParent()).getName().toString() 
						+ "." + parentName;
			}
			
			//忽略内部类中的函数
			if(parentName.equals(className)){
				MethodNode methodNode = new MethodNode();
				this.currentMethodName = node.getName().toString();
//				logger.debug("当前方法:" + this.currentMethodName);
				//构造methodNode节点
				methodNode.setPackageName(this.packageName);
				methodNode.setClassName(this.className);
				methodNode.setSimpleName(this.currentMethodName);
				//获取函数参数
//				List<String> paraTypes = new ArrayList<String>();
//				for (Object obj : node.parameters()) {
//					SingleVariableDeclaration parameter = (SingleVariableDeclaration) obj;
//					paraTypes.add(parameter.getType().toString());
//				}
//				methodNode.setParaTypes(paraTypes);
				
				//获取函数中的所有语句
//				Map<String,String> methodContext = getMethodContext(node.getBody().statements());
//				for (Object obj : node.parameters()) {
//					SingleVariableDeclaration para= (SingleVariableDeclaration) obj;
//					methodContext.put(para.getName().toString(), para.getType().toString());
//				}
				
				//获得分析函数语句时需要的所有上下文（类成员变量和类中定义的局部变量和参数）
//				Context context = new Context();
//				context.addVars(this.fields);
//				context.addVars(methodContext);
//				methodNode.setContext(context);
				
				//获得所有的日志模板 
//				LogAnalysis logAnalysis = new LogAnalysis(this.currentMethodName);
//				node.accept(logAnalysis);
//				methodNode.setLogNodeList(logAnalysis.getLogNodeList());
				
				//获得日志的所有语句
				methodNode.setNode(node);
				
				methodNodeList.add(methodNode);
			}
		}
		
		return true;
	}
	
	/**
	 * Description 递归分析方法的每个块的语句，获得所有定义的变量
	 * @param stmtList 一个语句块中的所有语句
	 * @param methodName 当前分析的方法名
	 * @return 返回当前分析方法中的所有局部变量<名称，类型名>
	 * @author Lunus
	 * @Date 2016-3-22
	 */
	@SuppressWarnings("unchecked")
	private Map<String,String> getMethodContext(List<Statement> stmtList){
		if(stmtList == null)
			return null;
		Map<String,String> methodContext = new HashMap<String,String>();
		for(Statement stmt : stmtList){
			if(stmt instanceof VariableDeclarationStatement){
				String varType = ((VariableDeclarationStatement) stmt).getType().toString();
				for(Object obj : ((VariableDeclarationStatement) stmt).fragments()){
					String varName = ((VariableDeclarationFragment)obj).getName().toString();
					methodContext.put(varName,varType);
				}
			}else if(stmt instanceof Block){
				methodContext.putAll(getMethodContext(((Block) stmt).statements()));
				
			}else if(stmt instanceof DoStatement){
				methodContext.putAll(getMethodContext(((Block)((DoStatement) stmt).getBody()).statements()));
				
			}else if(stmt instanceof ForStatement){
				if(((ForStatement) stmt).getBody() instanceof Block){
					methodContext.putAll(getMethodContext(((Block)((ForStatement) stmt).getBody()).statements()));
				}else{
					List<Statement> list = new ArrayList<Statement>();
					list.add(((ForStatement) stmt).getBody());
					methodContext.putAll(getMethodContext(list));
				}
				
			}else if(stmt instanceof EnhancedForStatement){
				if(((EnhancedForStatement) stmt).getBody() instanceof Block){
					methodContext.putAll(getMethodContext(((Block)((EnhancedForStatement) stmt).getBody()).statements()));
				}else{
					List<Statement> list = new ArrayList<Statement>();
					list.add(((EnhancedForStatement) stmt).getBody());
					methodContext.putAll(getMethodContext(list));
				}
				
			}else if(stmt instanceof IfStatement){
				List<Statement> ifList = new ArrayList<Statement>();
				List<Statement> elseList = new ArrayList<Statement>();
				Statement ifStmt = ((IfStatement) stmt).getThenStatement();
				Statement elseStmt = ((IfStatement) stmt).getElseStatement();
				if(ifStmt instanceof Block){
					methodContext.putAll(getMethodContext(((Block) ifStmt).statements()));
				}else{
					ifList.add(ifStmt);
					methodContext.putAll(getMethodContext(ifList));
				}
				if(elseStmt != null){
					if(elseStmt instanceof Block){
						methodContext.putAll(getMethodContext(((Block) elseStmt).statements()));
					}else{
						ifList.add(elseStmt);
						methodContext.putAll(getMethodContext(elseList));
					}
				}
				
			}else if(stmt instanceof SwitchStatement){
				methodContext.putAll(getMethodContext(((SwitchStatement) stmt).statements()));
				
			}else if(stmt instanceof SynchronizedStatement){
				methodContext.putAll(getMethodContext(((SynchronizedStatement) stmt).getBody().statements()));
			}else if(stmt instanceof TryStatement){
				methodContext.putAll(getMethodContext(((TryStatement) stmt).getBody().statements()));
				Block finallyBlock = ((TryStatement) stmt).getFinally();
				if(finallyBlock != null)
					methodContext.putAll(getMethodContext(finallyBlock.statements()));
				for(Object obj : ((TryStatement) stmt).catchClauses()){
					methodContext.putAll(getMethodContext(((CatchClause)obj).getBody().statements()));
				}
				
			}else if(stmt instanceof  WhileStatement){
				if(((WhileStatement) stmt).getBody() instanceof Block){
					methodContext.putAll(getMethodContext(((Block)((WhileStatement) stmt).getBody()).statements()));
				}else{
					List<Statement> list = new ArrayList<Statement>();
					list.add(((WhileStatement) stmt).getBody());
					methodContext.putAll(getMethodContext(list));
				}
				
			}else{
				//其它情况不包含变量定义
			}
		}
		return methodContext;
	}

}
