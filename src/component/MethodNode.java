package component;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodNode {
	private String packageName = null;
	private String className = null;
	private String methodName = null;
	private MethodDeclaration node = null;
	public MethodDeclaration getNode() {
		return node;
	}
	public void setNode(MethodDeclaration node) {
		this.node = node;
	}
//	//参数类型
//	private List<String> paraTypes = null;
//	//函数中的上下文
//	private Context context = null;
//	//每个函数的日志节点
//	private List<LogNode> logNodeList = null;
//	//函数调用轨迹
//	List<List<MethodNode>> methodInvoTraceList = null;
	
//	public List<List<MethodNode>> getMethodInvoTraceList() {
//		return methodInvoTraceList;
//	}
//	public void setMethodInvoTraceList(List<List<MethodNode>> methodInvoTraceList) {
//		this.methodInvoTraceList = methodInvoTraceList;
//	}
//	public List<LogNode> getLogNodeList() {
//		return logNodeList;
//	}
//	public void setLogNodeList(List<LogNode> logNodeList) {
//		this.logNodeList = logNodeList;
//	}
	/**
	 * Description 获取函数的绝对名称package.class.name
	 * @return
	 * @Date 2016年4月18日
	 */
	public String getMethodName() {
		return this.packageName + "." + this.className + "." + methodName;
	}
	public String getSimpleName(){
		return this.methodName;
	}
	public void setSimpleName(String methodName) { 
		this.methodName = methodName;
	}
//	public List<String> getParaTypes() {
//		return paraTypes;
//	}
//	public void setParaTypes(List<String> paraTypes) {
//		this.paraTypes = paraTypes;
//	}
//	public Context getContext() {
//		return context;
//	}
//	public void setContext(Context context) {
//		this.context = context;
//	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public String toString() {
		return this.getMethodName() + '\n';
//		return this.getMethodName() + paraTypes.toString() + ':' + context.toString() + '\n';
	}
	
}
