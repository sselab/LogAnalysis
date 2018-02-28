package component;

public class StmtTree {
	
	private StmtNode root = null;
	
	public StmtTree() {
		this.root = new StmtNode(null);
	}
	
	public StmtNode getRoot(){
		return this.root;
	}

}