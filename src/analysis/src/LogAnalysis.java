package analysis.src;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

import component.LogNode;

public class LogAnalysis extends ASTVisitor { 
	
	private List<LogNode> logNodeList = null;
//	private String methodName = null;
	
	public List<LogNode> getLogNodeList() {
		return this.logNodeList;
	}
	
	public LogAnalysis() {
//		this.methodName = methodName;
		this.logNodeList = new ArrayList<LogNode>();
	}

	@Override
	public boolean visit(MethodInvocation node) {
		String name = node.getName().toString();
		LogNode logNode = null;
		switch(name){
			case "info" : case "debug" : case "error" : case "warn" : case "fatal" : case "trace" :
				logNode = new LogNode();
//				logNode.setName(methodName + "." + name);
				logNode.setInfo(node.arguments().toString());
				logNode.setLevel(name);
				logNode.setNum(LogNode.getCurrentLogNum());
				break;
			default : break;
		}
		if(logNode != null){
			logNodeList.add(logNode);
		}
		return true;
	}

}
