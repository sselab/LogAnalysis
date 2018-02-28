package analysis.src;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import component.MethodNode;

public class ClassAnalysis extends ASTVisitor {
	
	static final Logger logger = Logger.getLogger(ClassAnalysis.class);
	//当前分析java文件的包名
	private String PackageName = null;
	//当前分析函数的类名
	private String currentClassName = null; 
	//当前已分析完的函数列表
	private List<MethodNode> methodNodeList = null;
	
	public ClassAnalysis() {
		this.PackageName = "default";
		this.methodNodeList = new ArrayList<MethodNode>();
	}
	
	public List<MethodNode> getMethodNodeList() {
		return methodNodeList;
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		this.PackageName = node.getName().toString();
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		currentClassName = node.getName().toString();
		//如果是类中的一级内部类（即与成员变量一个范围内的内部类定义）
		if(node.getParent() instanceof TypeDeclaration){
			currentClassName = ((TypeDeclaration)node.getParent()).getName().toString() + "." + node.getName().toString();
		}
		
		//跳过接口的函数分析
		if(node.isInterface() == false){
			//分析类中的方法
			MethodAnalysis methodAnalysis = new MethodAnalysis(PackageName,currentClassName);
			node.accept(methodAnalysis);
			this.methodNodeList.addAll(methodAnalysis.getMethodNodeList());
		}
		
		return true;
	}

}
