package component;

public class LogNode extends MethodNode {
	private static int CURRENT_LOG_NUM = 1;
	
//	private String name = null;
	private String level = null;
	private int num = 0;
	private String info = null;
	
	public static int getCurrentLogNum(){
		return CURRENT_LOG_NUM++;
	}
	
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}


	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		String[] strs = info.substring(1, info.length()-1).split(" \\+ ");
		
		StringBuilder cons = new StringBuilder();
		for(String str : strs){
			if(str.endsWith("\"") && str.startsWith("\"")){
				cons.append(str);
			}else{
				cons.append("[" + str + "]");
			}
		}
		return num + "-" + level + "-" + cons.toString();
	}

}
