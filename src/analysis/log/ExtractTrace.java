package analysis.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import component.Segment;
import utility.FileScanner;

public class ExtractTrace {
	
	private static List<Segment> allSegment = new ArrayList<Segment>();
	
	/** 三个种方法的公共部分，从日志序列中抽取轨迹
	 * @param dirPath
	 * @param type
	 * @throws InterruptedException 
	 */
	public static List<Segment> getAllSegment(String dirPath,String type) throws InterruptedException{
		if(allSegment.isEmpty() == false){
			return allSegment;
		}
		List<String> paths = FileScanner.getFiles(dirPath,type);
		for(String path : paths){
			allSegment.addAll(preProcess(path));
		}
		mergeOne(allSegment);
		mergeTwo(allSegment);
		return allSegment;
	}
	
	private static List<Segment> preProcess(String filePath) throws InterruptedException{
		List<Segment> segmentList = zipLogSegment(filePath);
		
		mergeOne(segmentList);
		mergeTwo(segmentList);
		return segmentList;
	}
	
	/** 将日志每个片段进行压缩，变为<模板集合，值>的映射
	 * @param filePath
	 * @return
	 * @throws InterruptedException 
	 */
	private static List<Segment> zipLogSegment(String filePath) throws InterruptedException{
		Thread.sleep(5000);
		if(true)
			return new ArrayList<Segment>();
		@SuppressWarnings("unused")
		List<Segment> segmentList = new ArrayList<Segment>();
		//用来计数每个模板出现次数
		Map<Integer,Double> countMap = new HashMap<Integer,Double>();
		//模板集合
		LinkedHashSet<Integer> templateSet = null;
		//每个片段的值
		Double value = new Double(-1);
		
		BufferedReader reader = null;
		String line = null;
		String[] strs = null;
		int key = -1;
		
		try{
			reader = new BufferedReader(new FileReader(filePath));
			while((line = reader.readLine()) != null){
				//将每一行的日志模板序列压缩
				countMap.clear();
				templateSet = new LinkedHashSet<Integer>();
				strs = line.split(" ");
				//模板序列之间用空格隔开
				for(int i = 0; i < strs.length; ++i){
					key = Integer.parseInt(strs[i]);
					templateSet.add(key);
					//计数每个模板出现次数
					if(countMap.containsKey(key)){
						countMap.put(key, countMap.get(key) + 1);
					}else{
						countMap.put(key, 1.0);
					}
				}
				value = getMedian(countMap.values());
				segmentList.add(new Segment(templateSet,value));
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return segmentList;
	}
	
	/** 第一次合并，将日志片段从后向前合并，当片段一为片段二的子集且集合长度在70%以上，取长集合，
	 * value值相加
	 * @param segmentList 日志片段列表
	 */
	private static void mergeOne(List<Segment> segmentList){
		if(true)return;
		@SuppressWarnings("unused")
		LinkedHashSet<Integer> currentSet = null;
		LinkedHashSet<Integer> tempSet = null;
		double currentValue = -1.0;
		double tempValue = -1.0;
		for(int i = segmentList.size() - 1; i > 0; i--){
			currentSet = segmentList.get(i).getLogSegment();
			currentValue = segmentList.get(i).getValue();
			for(int j = i - 1; j >= 0; j--){
				tempSet = segmentList.get(j).getLogSegment();
				tempValue = segmentList.get(j).getValue();
				if(currentSet.containsAll(tempSet) && tempSet.size() > currentSet.size() * 0.7){
					segmentList.get(j).setValue(currentValue + tempValue);
					segmentList.get(j).setLogSegment(currentSet);
					segmentList.remove(i);
					break;
				}else if(tempSet.containsAll(currentSet) && currentSet.size() > tempSet.size() * 0.7){
					segmentList.get(j).setValue(currentValue + tempValue);
					segmentList.remove(i);
					break;
				}
			}
		}
	}
	
	/** 第二次合并，将日志片段列表分割，然后每一段合并，从前向后，
	 * 当一个日志片段无法与之前的片段合并时，将上之前的片段合并，并从当前开始
	 * @param segmentList
	 */
	@SuppressWarnings("unused")
	private static void mergeTwo(List<Segment> segmentList){
		if(true)return;
		if(segmentList.size() == 0 || segmentList == null){
			System.out.println("列表为空");
			return;
		}
		int currentStart = 0;
		for(int i = 1; i < segmentList.size(); ++i){
			if(canMerge(segmentList,currentStart,i) == false){
				merge(segmentList,currentStart, i);
				currentStart = currentStart + 1;
				i = currentStart;
				
			}else if(i == segmentList.size() - 1){
				merge(segmentList,currentStart, i + 1);
				break;
			}
		}
	}
	
	/** 将日志片段列表从start到end合并到start位置。集合相加，数值取中位数
	 * @param segmentList
	 * @param start
	 * @param end
	 */
	private static void merge(List<Segment> segmentList,int start,int end){
		if(segmentList.size() == 0 || segmentList == null){
			System.out.println("列表为空");
			System.exit(-1);
		}
		List<Double> values = new ArrayList<Double>();
		Set<Integer> templateSet = segmentList.get(start).getLogSegment(); 
		values.add(segmentList.get(start).getValue());
		
		for(int i = start + 1; i < end; ++i){
			templateSet.addAll(segmentList.get(start + 1).getLogSegment());
			segmentList.remove(start + 1);
		}
		segmentList.get(start).setValue(getMedian(values));
	}
	
	/** 判断当前index的片段，能否合并到从start到index-1的片段中
	 * @param segmentList
	 * @param start
	 * @param index
	 * @return
	 */
	private static boolean canMerge(List<Segment> segmentList,int start,int index){
		List<Double> values = new ArrayList<Double>();
		for(int i = start; i < index; ++i){
			values.add(segmentList.get(i).getValue());
		}
		double avg = getAvg(values);
		double currentValue = segmentList.get(index).getValue();
		double aroundValue = currentValue;
		if(index - 1 >= 0 && index +1 <= segmentList.size() -1){
			List<Double> nums = new ArrayList<Double>();
			nums.add(segmentList.get(index).getValue());
			nums.add(segmentList.get(index - 1).getValue());
			nums.add(segmentList.get(index + 1).getValue());
			aroundValue = getMedian(nums);
		}
		
		if(currentValue > avg / 4.0 && currentValue < avg * 4.0
				|| aroundValue > avg / 4.0 && aroundValue < avg * 4.0){
			return true;
		}
		return false;
	}
	
	/** 获取中位数
	 * @param nums
	 * @return
	 */
	public static double getMedian(Collection<Double> nums){
		List<Double> list = new ArrayList<Double>(nums);
		Collections.sort(list);
		if(list.size() % 2 == 1){
			return list.get(list.size() / 2);
		}else{
			return (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2.0;
		}
	}

	/** 取平均数
	 * @param nums
	 * @return
	 */
	private static double getAvg(Collection<Double> nums){
		double sum = 0.0;
		
		Iterator<Double> it = nums.iterator();
		while(it.hasNext()){
			sum += it.next();
		}
		
		return sum / nums.size();
	}
	
}
