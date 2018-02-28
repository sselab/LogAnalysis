package analysis.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

import component.LogNode;
import component.MethodNode;

public class ExpandMethodInvoAnalysis extends ASTVisitor{
	
	static final Logger logger = Logger.getLogger(ExpandMethodInvoAnalysis.class);
	//所有函数节点的列表
	private List<MethodNode> methodNodeList = null; 
	//当前的语句属于的函数节点，包含分析函数需要的上下文
	private MethodNode methodNode = null;
	//需要返回的每条语句对应的函数轨迹
	private List<MethodNode> invoList = null;
	
	public ExpandMethodInvoAnalysis(MethodNode methodNode,List<MethodNode> methodNodeList){
		this.methodNodeList = methodNodeList;
		this.methodNode = methodNode;
		this.invoList = new ArrayList<MethodNode>();
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodInvocation node) {
//		logger.debug(node.toString());
		List<MethodNode> candidateList = new ArrayList<MethodNode>();
		String name = node.getName().toString();
		//如果是日志语句
		LogNode logNode = null;
		switch(name){
			case "info" : case "debug" : case "error" : case "warn" : case "fatal" : case "trace" :
				logNode = new LogNode();
//				logNode.setName(methodNode.getMethodName() + "." + name);
				logNode.setInfo(node.arguments().toString());
				logNode.setLevel(name);
				logNode.setNum(LogNode.getCurrentLogNum());
				invoList.add(logNode);
				return true;
		}
		//匹配函数名
		for(MethodNode methodNode : methodNodeList){
			if(methodNode.getSimpleName().equals(name)){
				candidateList.add(methodNode);
			}
		}
		//匹配参数个数
//		Iterator<MethodNode> it = candidateList.iterator();
//		while(it.hasNext()){
//			if(it.next().getParaTypes().size() != node.arguments().size()){
//				it.remove();
//			}
//		}
		//匹配参数类型
		List<Expression> argList = node.arguments();
		List<String> typeList = new ArrayList<String>();
		for(Expression e : argList){
			typeList.add(getExpressionType(e));
		}
		
		String type = null;
		for(int i = 0; i < candidateList.size(); ++i){
			for(int j = 0; j < typeList.size(); ++j){
				type = typeList.get(j);
//				if(candidateList.get(i).getParaTypes().get(j).equals(type) == false){
//					candidateList.remove(i);
//					i--;
//					break;
//				}
			}
		}
		//匹配调用函数的对象的类型
//		type = getExpressionType(node.getExpression());
//		for(int i = 0; i < candidateList.size(); ++i){
//			if(candidateList.get(i).getClassName().equals(type) == false){
//				candidateList.remove(i);
//				i--;
//				continue;
//			}
//		}
		
		//*********************************其他匹配细则
		if(candidateList.size() == 1){
			invoList.add(candidateList.get(0));
//			logger.debug("匹配成功:" + candidateList.get(0).getMethodName());
		}else if(candidateList.size() > 1){
			//如果匹配结果有多个，随机选一个
			MethodNode randomOne = candidateList.get(new Random().nextInt(candidateList.size())); 
			invoList.add(randomOne);
		}
		return true;
	}
	
	private String getExpressionType(Expression e){
		if(e == null){
			return "";
		}else if(e instanceof BooleanLiteral){
			return "boolean";
		}else if(e instanceof CharacterLiteral){
			return "char";
		}else if(e instanceof NumberLiteral){
			return "int";
		}else if(e instanceof StringLiteral){
			return "String";
		}else if(e instanceof Name){
			return getNameType((Name) e);
		}else if(e instanceof ArrayAccess){
			return getArrayAccessType((ArrayAccess) e);
		}else if(e instanceof ArrayCreation){
			return ((ArrayCreation) e).getType().toString();
		}else if(e instanceof CastExpression){
			return ((CastExpression) e).getType().toString();
		}else if(e instanceof ClassInstanceCreation){
			return ((ClassInstanceCreation) e).getType().toString();
		}else if(e instanceof ConditionalExpression){
			return getExpressionType(((ConditionalExpression) e).getThenExpression());
		}else if(e instanceof FieldAccess){
			return getNameType(((FieldAccess) e).getName());
		}else if(e instanceof ParenthesizedExpression){
			return getExpressionType(((ParenthesizedExpression) e).getExpression());
		}else if(e instanceof TypeLiteral){
			return "Class";
		}else if(e instanceof VariableDeclarationExpression){
			return ((VariableDeclarationExpression) e).getType().toString();
		}
		
		return "";
	}
	
	private String getNameType(Name name){
//		if(name instanceof SimpleName){
//			return this.methodNode.getContext().getType(name.toString());
//		}
		return "";
	}
	
	private String getArrayAccessType(ArrayAccess e){
		String name = getExpressionType(e.getArray());
		if(name.endsWith("[]")){
			return name.split("\\[")[0];
		}
		return "";
	}
	public List<MethodNode> getInvoList(){
		return this.invoList;
	}
}
