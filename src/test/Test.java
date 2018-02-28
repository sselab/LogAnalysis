package test;

public class Test {
	public static void main(String[] args){
		if(true){
			int xx = 10;
		}
		father f = new son();
		fun1(f);
		int x = 1;
//		switch(x){
////			default : break;
//			case 1 : x = 20;
//			x = 30;break;
//			case 2 : x = 40;
//			x = 50;
//		}
//		System.out.println(x);
//		
//		BufferedReader reader = null;
//		BufferedWriter writer = null;
//		try{
//			reader = new BufferedReader(new FileReader(""));
//			writer = new BufferedWriter(new FileWriter(""));
//		}catch(IOException e){
//			e.printStackTrace();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		finally{
//			try {
//				reader.close();
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	private static void fun1(father f){
		System.out.println("father");
	}
	
	private static void fun1(son s){
		System.out.println("son");
	}
	
	
	
}

class father{

}

class son extends father{
	
}