package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FileScanner {
	
	static final Logger logger = Logger.getLogger(FileScanner.class);
	
	
	public static List<String> getJavaFiles(String dirPath){
		List<String> filePaths = getFiles(dirPath,".java");
		return filePaths.subList(0, 3000);
	}

	/**
	 * Description: 获得目录下的后缀为.xxx的文件路径列表
	 * @param dirPath dirPath 需要扫描的根目录
	 * @param type type 需要扫描的文件后缀(.xxx)
	 * @return 文件路径列表
	 * @author Lunus
	 * @Date 2016-3-21
	 */
	public static List<String> getFiles(String dirPath,String type){
		File dir = new File(dirPath);
		if(dir.isDirectory() == false){
			return null;
		}
		List<String> filePaths = new ArrayList<String>();
		
		File files[] = dir.listFiles();
		for(File file : files){
			if(file.isDirectory()){
				filePaths.addAll(getFiles(file.getAbsolutePath(),type));
			}else if(file.getPath().endsWith(type)){
				filePaths.add(file.getAbsolutePath());
			}
		}
		return filePaths;
	}
	
}
