package analysis.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import analysis.log.MethodOne;
import common.Logger;
import component.MethodNode;
import utility.CompilationUnitUtil;
import utility.FileScanner;



//1.只分析了一级内部类，没有其它内部类和匿名类
//2.函数上下文分析不包含全局变量（静态成员变量）
//3.上下文分析没有一些while（表达式），for(表达式）里的变量定义
//4.Loop中的语句无法循环展开
//5.不包含各种结构化语句中的expression
//6.break，continue语句无法完成解析
public class Main {
	
	static final Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws IOException, InterruptedException{
		List<MethodNode> methodList = new ArrayList<MethodNode>();
		String dirPath = "C:\\Users\\Lunus\\Desktop\\训练集实验集\\apache-cloudstack-4.3.2-src";
		String modelSqeu = "C:\\Users\\Lunus\\Desktop\\对比实验";
		String logPath = "C:\\Users\\Lunus\\Desktop\\对比实验\\CloudStack.log";
		String modelPath = "C:\\Users\\Lunus\\Desktop\\训练集实验集\\model.txt";
		String outputPath = "C:\\Users\\Lunus\\Desktop\\对比实验\\thread.txt";
		List<String> paths = FileScanner.getJavaFiles(dirPath);
		System.out.println(0);
		//解析源代码（遍历源代码的AST表达树）
		for(String path : paths){
			logger.debug("源代码分析:" + path);
			CompilationUnit comp = CompilationUnitUtil.getCompilationUnit(path);
			
			//获得所有方法与上下文
			ClassAnalysis classAnalysis = new ClassAnalysis();
			comp.accept(classAnalysis);
			methodList.addAll(classAnalysis.getMethodNodeList());
			
			//获得所有的日志模板
			LogAnalysis logAnalysis = new LogAnalysis();
			comp.accept(logAnalysis);
			writeToFile(logAnalysis.getLogNodeList(), modelPath);
		}
		logger.debug("源代码分析结束");
		//函数结构解析，并提取可达矩阵
		for(MethodNode methodNode : methodList){
			ExpandMethodAnalysis analysis = new ExpandMethodAnalysis(methodNode.getPackageName(), methodNode.getClassName(),methodList,methodNode);
			methodNode.getNode().accept(analysis);
		}
		logger.debug("函数结构解析结束");
		System.out.println(1);
		//日志分类
		logger.debug("日志分类开始");
//		Classify.classifyLogFile(logPath, modelPath, outputPath);
		logger.debug("日志分类结束");
		
		MethodOne.ourMethod(modelSqeu, ".txt");
	}
	
	
	
	public static void writeToFile(List<? extends Object> list,String path) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for(Object o : list){
			writer.write(o.toString());
			writer.newLine();
		}
		writer.close();
	}
	
}
