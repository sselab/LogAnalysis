package analysis.src;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;


public class FieldAnalysis extends ASTVisitor {
	
	static final Logger logger = Logger.getLogger(FieldAnalysis.class);
	
	//分析时需要的上下文
//	private String packageName = null;
	private String className = null;
	//类成员变量
	Map<String,String> fields = null;
	
	public Map<String, String> getFields() {
		return fields;
	}

	public FieldAnalysis(String packageName,String className) {
//		this.packageName = packageName;
		this.className = className;
		fields = new HashMap<String,String>();
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		
		//分析类的每个成员变量
		for(Object fragment : node.fragments()){
			VariableDeclaration varDeclaration = (VariableDeclaration)fragment;
			ASTNode parentNode = node.getParent();
			//忽略当前类中内部类的成员变量
			if(parentNode instanceof TypeDeclaration){
				String parentName = ((TypeDeclaration) parentNode).getName().toString();
				//如果当前类是一个一级内部类
				if(parentNode.getParent() instanceof TypeDeclaration){
					parentName = ((TypeDeclaration)parentNode.getParent()).getName().toString() + 
							"." + parentName;
				}
				//忽略匿名内部类中的成员变量
				if(parentName.equals(this.className)){
//					String varName = packageName + "." + className + "." + varDeclaration.getName().toString();
					String varName = varDeclaration.getName().toString();
					fields.put(varName,node.getType().toString());
				}
			}
			
		}
		return true;
	}
	

}
