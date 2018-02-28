package component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

public class StmtNode{
	
	private Statement stmt = null;
	private List<StmtNode> children = null;
//	private StmtNode father = null;
	
	public StmtNode(Statement stmt) {
		this.stmt = stmt;
		this.children = new ArrayList<StmtNode>();
	}

//	public StmtNode getFather() {
//		return father;
//	}
//
//	public void setFather(StmtNode father) {
//		this.father = father;
//	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	public List<StmtNode> getChildren() {
		return children;
	}

//	public void setChildren(List<StmtNode> children) {
//		this.children = children;
//	}
	
	public void addChild(StmtNode node){
		this.children.add(node);
	}
	
	public void addChildren(List<StmtNode> list){
		this.children.addAll(list);
	}
	
	public void removeChild(StmtNode node){
		this.children.remove(node);
	}
	
	@Override
	public String toString() {
		return this.stmt == null ? "null" : stmt.toString();
	}
}
