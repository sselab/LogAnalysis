//package analysis.log;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//
//import component.Segment;
//
//public class MethodTwo {
//	
//	public static List<Segment> samplingMethod(String dirPath,String type){
//		//处理所有日志并获得最后的<片段集合，value>列表
//		List<Segment> segmentList = ExtractTrace.getAllSegment(dirPath, type);
//		List<Segment> abnormalSegList = new ArrayList<Segment>();
//		//针对每个片段判断是否为异常片段
//		for(Segment seg : segmentList){
//			List<Segment> subList = getSubList(segmentList,seg);
//			if(isAbnormal(subList,seg) == true){
//				abnormalSegList.add(seg);
//			}else{
//				System.out.println(seg);
//			}
//		}
//		
//		System.out.println(abnormalSegList);
//		return abnormalSegList;
//	}
//	
//	/** 获得除本身以外的前50%值大小的片段序列
//	 * @param segList
//	 * @param segment
//	 * @return
//	 */
//	private static List<Segment> getSubList(List<Segment> segList,Segment segment){
//		List<Segment> subList = new ArrayList<Segment>(segList);
//		//去除自身
//		for(int i = subList.size() - 1; i >= 0; --i){
//			if(segment == subList.get(i)){
//				subList.remove(i);
//				break;
//			}
//		}
//		//获得剩余每个片段出现次数
//		List<Double> sizeList = new ArrayList<Double>();
//		for(Segment seg : subList){
//			sizeList.add(seg.getValue());
//		}
//		//获得出现次数的中位数，根据和中位数的比较确定需要删除的片段
//		Double median = ExtractTrace.getMedian(sizeList);
//		for(int i = subList.size() - 1; i >= 0; --i){
//			if(subList.get(i).getValue() < median){
//				subList.remove(i);
//			}
//		}
//		return subList;
//	}
//	
//	/**根据当前的片段列表构造图，如果segment可在图中出现，则非异常，否则为异常
//	 * @param segList 片段列表
//	 * @param segment 需要判断的片段
//	 * @return
//	 */
//	private static boolean isAbnormal(List<Segment> segList,Segment segment){
//		List<Integer> eleList = new ArrayList<Integer>();
//		//得到所有元素的集合，并将每个元素映射到0到set.size-1的一个整数，内容保存到eleList中，并利用片段列表构造图
//		double[][] matrix = makeGraph(segList, eleList);
//		//判断当前片段是否在图中，存在则非异常，否则为异常
//		Iterator<Integer> it = segment.getLogSegment().iterator();
//		Integer preEle = null;
//		while(it.hasNext()){
//			Integer ele = it.next();
//			if(preEle == null){
//				preEle = ele;
//			}else{
//				int i = eleList.indexOf(preEle);
//				int j = eleList.indexOf(ele);
//				preEle = ele;
//				//如果有任意一条边不在图中，则为异常
//				if(i == -1 || j == -1 || matrix[i][j] == 0){
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//	
//	private static double[][] makeGraph(List<Segment> segList,List<Integer> eleList){
//		//得到所有元素的集合，并将每个元素映射到0到set.size-1的一个整数
//		Set<Integer> set = new LinkedHashSet<Integer>();
//		for(Segment seg : segList){
//			set.addAll(seg.getLogSegment());
//		}
//		eleList.addAll(set);	
//		
//		//将日志片段构造成一个图
//		double[][] matrix = new double[set.size()][set.size()];
//		for(int i = 0; i < matrix.length; ++i)
//		for(int j = 0; j < matrix.length; ++j)
//			matrix[i][j] = 0.0;
//		//片段的模板出现顺序作为图的边
//		for(Segment seg : segList){
//			Integer preEle = null; 
//			Iterator<Integer> it = seg.getLogSegment().iterator();
//			while(it.hasNext()){
//				Integer ele = it.next();
//				if(preEle == null){
//					preEle = ele;
//				}else{
//					matrix[eleList.indexOf(preEle)][eleList.indexOf(ele)] = 1.0;
//					preEle = ele;
//				}
//			}
//		}
//		return matrix;
//	}
//}
