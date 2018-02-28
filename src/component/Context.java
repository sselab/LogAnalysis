package component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Context {
	//<变量名，类型>
	private Map<String,String> vars = new HashMap<String,String>(); 

	public Map<String, String> getVars() {
		return vars;
	}

	public void addVars(Map<String, String> vars) {
		this.vars.putAll(vars);
	}
	
	public String getType(String name){
		Iterator<String> it = vars.keySet().iterator();
		while(it.hasNext()){
			if(it.next().equals(name))
				return vars.get(name);
		}
		return "";
	}
	@Override
	public String toString() {
		return vars.toString();
	}
}
