package analysis.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger; 
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import component.MethodNode;
import component.StmtNode;
import component.StmtTree;

public class ExpandMethodAnalysis extends ASTVisitor {
	static final Logger logger = Logger.getLogger(ExpandMethodAnalysis.class);
	
	private Map<Statement,List<MethodNode>>  map = null;
	private Map<StmtNode,List<StmtNode>> map2 = null;
	//分析函数需要的上下文
	private String packageName = null;
	private String className = null;
//	private String currentMethodName = null;
	//所有的函数描述节点
	private List<MethodNode> methodNodeList = null;
	//当前函数的描述节点
	private MethodNode methodNode = null;
	
	public ExpandMethodAnalysis(String packageName,String className,List<MethodNode> methodList,MethodNode methodNode) {
		this.packageName = packageName;
		this.className = className;
		this.methodNodeList = methodList;
		this.methodNode = methodNode;
		map = new HashMap<Statement,List<MethodNode>>();
		map2 = new HashMap<StmtNode,List<StmtNode>>();
	}

	/**
	 * Description 分析每一个类的语句，分别匹配函数以得到函数调用轨迹
	 * @param node
	 * @return 
	 * @Date 2016年4月18日
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodDeclaration node) {
		logger.debug("函数结构解析:" + this.methodNode.getMethodName());
		if(this.methodNode.getMethodName().equals("com.cloud.vm.VirtualMachineManagerImpl.orchestrateStart"))
			System.out.println("xxx");;
		//获取函数的语句顺序列表
		List<Statement> stmtTrace = new ArrayList<Statement>(node.getBody().statements());
		//将语句列表存储成一棵树，再根据各种语句块（while，try）展开这颗树
		StmtTree tree = new StmtTree();
		StmtNode treeNode = tree.getRoot();
		for(int i = 0; i < stmtTrace.size(); ++i){
			StmtNode temp = new StmtNode(stmtTrace.get(i));
			treeNode.addChild(temp);
			treeNode = temp;
		}
		spreadStmtTree(tree);
		//将statement语句树按照每条路径进行解析并获得函数调用路径
//		this.methodNode.setMethodInvoTraceList(matchFun(tree));
		//防止遍历到函数中的函数定义
		return false;
	}
	
	
	/** 根据一个函数的语句轨迹得到每个轨迹对应的函数调用轨迹
	 * Description 
	 * @param stmtTraceList
	 * @return 多出的轨迹列表
	 * @author Lunus
	 * @Date 2016-3-28
	 */
	private List<List<MethodNode>> matchFun(StmtTree tree){
		//每条statement轨迹得到的函数调用轨迹
		List<MethodNode> invoTrace = null;
		//所有的函数调用轨迹
		List<List<MethodNode>> invoTraceList = new ArrayList<List<MethodNode>>();
		
		//构造第一个路径，index表示trace每个节点分别是父节点的第几个节点
		List<StmtNode> trace = new ArrayList<StmtNode>();
		List<Integer> indexs = new ArrayList<Integer>();
		trace.add(tree.getRoot());
		indexs.add(0);
		StmtNode node = tree.getRoot();
		while(node.getChildren().size() != 0){
			trace.add(node.getChildren().get(0));
			indexs.add(0);
			node = node.getChildren().get(0);
		}
		while(trace.size() != 1){
			//对每个语句轨迹进行匹配
			invoTrace = new ArrayList<MethodNode>();
			for(int i = 1; i < trace.size(); ++i){
				invoTrace.addAll(match(trace.get(i).getStmt(),this.methodNodeList));
			}
			invoTraceList.add(invoTrace);
			trace = getTrace(trace,indexs) ;
		}
		return invoTraceList;
	}
	
	/**
	 * Description 回溯法获得所有的可能路径
	 * @param trace
	 * @param indexs
	 * @return
	 * @Date 2016年4月22日
	 */
	private List<StmtNode> getTrace(List<StmtNode> trace,List<Integer> indexs){
		if(trace.size() == 1){
			return trace;
		}else if(trace.size() == 0){
			logger.error("节点删除错误");
			return null;
		}
		int size = trace.size();
		trace.remove(size - 1);
		int index = indexs.remove(size - 1);
		List<StmtNode> children = trace.get(size - 2).getChildren();
		//如果轨迹最后节点不是父节点的最后一个子节点，则加入节点的下一个兄弟节点
		if(index != children.size() - 1){
			StmtNode temp = children.get(index + 1);
			trace.add(temp);
			indexs.add(index + 1);
			while(temp.getChildren().size() != 0){
				trace.add(temp.getChildren().get(0));
				indexs.add(0);
				temp = temp.getChildren().get(0);
			}
		}else{
			return getTrace(trace,indexs);
		}
		return trace;
	}
	/** 得到每句statement中可匹配的函数调用
	 * @param stmt 当前分析的语句
	 * @param methodNodeList 所有的函数描述节点
	 * @return 函数调用列表
	 */
	private List<MethodNode> match(Statement stmt, List<MethodNode> methodNodeList){
		if(map.containsKey(stmt)){
			return map.get(stmt);
		}else{
			ExpandMethodInvoAnalysis invoAnalysis = new ExpandMethodInvoAnalysis(this.methodNode,methodNodeList);
			stmt.accept(invoAnalysis);
			List<MethodNode> result = invoAnalysis.getInvoList();
			map.put(stmt, result);
			return result;
		}
	}
	
	
	/**
	 * Description 展开树的所有节点
	 * @param tree
	 * @Date 2016年4月21日
	 */
	private void spreadStmtTree(StmtTree tree){
		List<StmtNode> queue = new ArrayList<StmtNode>();
		queue.add(tree.getRoot());
		while(queue.size() != 0){
			StmtNode node = queue.remove(0);
			List<StmtNode> children = node.getChildren();
			for(int i = 0; i < children.size(); ++i){
				spreadNode(children.get(i),node);
			}
			queue.addAll(node.getChildren());
		}
	}
	
	/**
	 * Description 展开代码块，将如ifelse等有{}的地方展开 
	 * @param stmtList
	 * @Date 2016年4月18日
	 */
	@SuppressWarnings("unchecked")
	private void spreadNode(StmtNode node,StmtNode father){
		if(match(node.getStmt(),this.methodNodeList).size() == 0){
//			System.out.println("**************");
//			System.out.println(node.getStmt());
			return ;
		}
		if(map2.containsKey(node)){
			father.addChildren(map2.get(node));
			father.removeChild(node);
			return;
		}
		Statement statement = node.getStmt();
		Block block = null;
		StmtNode root = null;
		StmtNode currentNode = null;
		if(statement instanceof Block){
			block = (Block) statement;
			spreadStmtList(block.statements(), node, father);
		}else if(statement instanceof SynchronizedStatement){
			block = ((SynchronizedStatement) statement).getBody();
			spreadStmtList(block.statements(), node, father);
		}else if(statement instanceof DoStatement){
			Statement body = ((DoStatement) statement).getBody();
			//可能没有花括号
			if(body instanceof Block){
				spreadStmtList(((Block)body).statements(), node, father);
			}else{
				currentNode = new StmtNode(body);
				root = currentNode;
				father.addChild(root);
				currentNode.addChildren(node.getChildren());
			}
		}else if(statement instanceof ForStatement){
			Statement body = ((ForStatement) statement).getBody();
			//可能没有花括号
			if(body instanceof Block){
				spreadStmtList(((Block)body).statements(), node, father);
			}else{
				currentNode = new StmtNode(body);
				root = currentNode;
				father.addChild(root);
				currentNode.addChildren(node.getChildren());
			}
		}else if(statement instanceof EnhancedForStatement){
			Statement body = ((EnhancedForStatement) statement).getBody();
			//可能没有花括号
			if(body instanceof Block){
				spreadStmtList(((Block)body).statements(), node, father);
			}else{
				currentNode = new StmtNode(body);
				root = currentNode;
				father.addChild(root);
				currentNode.addChildren(node.getChildren());
			}
		}else if(statement instanceof WhileStatement){
			Statement body = ((WhileStatement) statement).getBody();
			//可能没有花括号
			if(body instanceof Block){
				spreadStmtList(((Block)body).statements(), node, father);
			}else{
				currentNode = new StmtNode(body);
				root = currentNode;
				father.addChild(root);
				currentNode.addChildren(node.getChildren());
			}
		}else if(statement instanceof IfStatement){
			//可能没有花括号
			Statement ifBody = ((IfStatement) statement).getThenStatement();
			Statement elseBody = ((IfStatement) statement).getElseStatement();
			StmtNode ifRoot = null;
			StmtNode elseRoot = null;
			if(ifBody instanceof Block){
				spreadStmtList(((Block)ifBody).statements(),node,father);
			}else{
				currentNode = new StmtNode(ifBody);
				ifRoot = currentNode;
				father.addChild(ifRoot);
				currentNode.addChildren(node.getChildren());
			}
			
			if(elseBody != null){
				if(elseBody instanceof Block){
					spreadStmtList(((Block)elseBody).statements(),node,father);
				}else{
					currentNode = new StmtNode(elseBody);
					elseRoot = currentNode;
					father.addChild(elseRoot);
					currentNode.addChildren(node.getChildren());
				}
			}else{
				father.addChildren(node.getChildren());
			}
		}else if(statement instanceof SwitchStatement){
			List<List<Statement>> stmtTraceList = spreadSwitchStatement(statement);
			for(int i = 0; i < stmtTraceList.size(); ++i){
				spreadStmtList(stmtTraceList.get(i), node, father);
			}
		}else if(statement instanceof TryStatement){
			List<Statement> stmtTrace = null;
			List<List<Statement>> stmtTraceList = spreadTryCatchStatement(statement);
			for(int i = 0; i < stmtTraceList.size(); ++i){
				stmtTrace = stmtTraceList.get(i);
				spreadStmtList(stmtTrace, node, father);
			}
		}else{
			//普通语句则不改变树的结构
			return;
		}
		father.removeChild(node);
	}
	
	private void spreadStmtList(List<Statement> stmtTrace,StmtNode node,StmtNode father){
		if(stmtTrace.size() == 0){
			father.addChildren(node.getChildren());
			return;
		}else if(stmtTrace.size() == 1){
			StmtNode temp = new StmtNode(stmtTrace.get(0));
			father.addChild(temp);
			temp.addChildren(node.getChildren());
			return;
		}else{
			StmtNode root = null;
			StmtNode preNode = null;
			StmtNode currentNode = null;
			for(int i = 0; i < stmtTrace.size(); ++i){
				if(i == 0){
					root = new StmtNode(stmtTrace.get(i));
					preNode = root;
					currentNode = root;
				}else{
					currentNode = new StmtNode(stmtTrace.get(i));
					preNode.addChild(currentNode);
					preNode = currentNode;
				}
			}
			father.addChild(root);
			currentNode.addChildren(node.getChildren());
		}
	}
	
	/**
	 * Description 展开tryCatch块代码 
	 * @param methodStmtTrace
	 * @param index
	 * @return 新增的轨迹列表
	 * @Date 2016年4月18日
	 */
	@SuppressWarnings("unchecked")
	private List<List<Statement>> spreadTryCatchStatement(Statement statement){
		List<List<Statement>> traceList = new ArrayList<List<Statement>>();
		//获取try块，和catch块，finally块的语句
		TryStatement tryStmt = (TryStatement) statement;
		List<Statement> tryStmtList = new ArrayList<Statement>(tryStmt.getBody().statements());
		List<CatchClause> catchClausesList = tryStmt.catchClauses();
		List<Statement> finallyStmtList = null;
		if(tryStmt.getFinally() != null){
			finallyStmtList = tryStmt.getFinally().statements();
		}
		
		
		for(int i  = tryStmtList.size() - 1; i >= 0; --i){
			Statement stmt = tryStmtList.get(i);
			if(match(stmt,this.methodNodeList).size() == 0){
				tryStmtList.remove(i);
//				System.out.println("**************");
//				System.out.println(stmt);
			}
		}
		//trycatch块正常运行，不含有catch内容
		traceList.add(new ArrayList<Statement>(tryStmtList));
		if(finallyStmtList != null){
			traceList.get(0).addAll(finallyStmtList);
		}
		//根据try块的规则获取包含catch块的其它片段
		List<Statement> trace = new ArrayList<Statement>();
		for(int i = 0; i < tryStmtList.size(); ++i){
			trace.clear();
			trace.addAll(tryStmtList.subList(0, i + 1));
			for(int j = 0; j < catchClausesList.size(); ++j){
				List<Statement> temp = new ArrayList<Statement>(trace);
				temp.addAll(catchClausesList.get(j).getBody().statements());
				if(finallyStmtList != null){
					temp.addAll(finallyStmtList);
				}
				traceList.add(temp);
			}
		}
		return traceList;
	}
	/**
	 * Description 
	 * @param methodStmtTrace
	 * @param index
	 * @return 新增的轨迹列表
	 * @Date 2016年4月18日
	 */
	@SuppressWarnings("unchecked")
	private List<List<Statement>> spreadSwitchStatement(Statement stmt){
		SwitchStatement tryStmt = (SwitchStatement) stmt;
		List<Statement> switchStmtList = tryStmt.statements();
		//返回值
		List<List<Statement>> traceList = new ArrayList<List<Statement>>();
		List<Statement> trace = new ArrayList<Statement>();
		//根据break来获取switch的每个片段，并忽略其中的case语句
		for(int i = 0; i < switchStmtList.size(); ++i){
			if(i == switchStmtList.size() - 1 && (switchStmtList.get(i) instanceof BreakStatement) == false){
				trace.add(switchStmtList.get(i));
				traceList.add(trace);
				continue;
			}
			if(switchStmtList.get(i) instanceof BreakStatement){
				traceList.add(trace);
				trace = new ArrayList<Statement>();
			}else if(switchStmtList.get(i) instanceof SwitchCase){
				continue;
			}else{
				trace.add(switchStmtList.get(i));
			}
		}
		return traceList;
	}
}

