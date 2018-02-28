package analysis.log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import component.Segment;

public class MethodOne {
	static final Logger logger = Logger.getLogger(MethodOne.class);
	public static List<Double> ourMethod(String dirPath,String type) throws InterruptedException{
		List<Double> resultList = new ArrayList<Double>();
		//集合对应value的平均值
		double ATF = 0.0;
		//文件预处理得到片段列表
		logger.debug("轨迹抽取开始");
		List<Segment> segmentList = ExtractTrace.getAllSegment(dirPath, type);
		logger.debug("轨迹抽取完成");
		//得到所有元素的集合，并将每个元素映射到0到set.size-1的一个整数，
		//内容保存到eleList中，同时计算ATF
		Set<Integer> set = new LinkedHashSet<Integer>();
		set.add(0);
		for(Segment seg : segmentList){
			set.addAll(seg.getLogSegment());
			ATF += seg.getValue();
		}
		ATF /= segmentList.size();
		List<Integer> eleList = new ArrayList<Integer>(set);
		
		//利用片段的顺序构造图，matrix[i][j]表达边i->j出现的次数，nums[i]表示节点i出现的次数
		double[][] matrix = new double[eleList.size()][eleList.size()];
		double[] nums = new double[matrix.length];
		for(int i = 0; i < matrix.length; ++i)
		for(int j = 0; j < matrix.length; ++j)
			matrix[i][j] = 0.0;
		for(int i = 0; i < nums.length; ++i)
			nums[i] = 0.0;
		
		//起始节点指向所有的异常片段
		nums[0] += segmentList.size(); 
		//填充图中边的信息
		for(Segment seg : segmentList){
			Iterator<Integer> it = seg.getLogSegment().iterator();
			Integer preEle = null;
			while(it.hasNext()){
				Integer ele = it.next();
				//计算节点出现次数
				nums[eleList.indexOf(ele)] += 1.0;
				if(preEle == null){
					preEle = ele;
				}else{
					//边i->j每出现一次，matrix[i][j]+1;
					int i = eleList.indexOf(preEle);
					int j = eleList.indexOf(ele);
					matrix[i][j] += 1.0;
					preEle = ele;
				}
			}
		}
		//边0到一个片段头节点的次数等于该头节点的出现次数
		for(int i = 1; i < nums.length; ++i)
			matrix[0][i] += nums[i];
		
		//计算每个序列的异常度
		for(Segment seg : segmentList){
			//当前片段对应的value值
			double TF = seg.getValue();
			//当前片段出现的概率
			double  probability = 1.0;
			double result = 1.0;
			
			LinkedHashSet<Integer> eleSet = new LinkedHashSet<Integer>();
			//加入0节点，指向所有片段的头部
			eleSet.add(0);
			eleSet.addAll(seg.getLogSegment());
			Iterator<Integer> it = eleSet.iterator();
			Integer preEle = null;
			while(it.hasNext()){
				Integer ele = it.next();
				if(preEle == null){
					preEle = ele;
				}else{
					//每个边的概率，边出现次数 / 头部出现次数
					int i = eleList.indexOf(preEle);
					int j = eleList.indexOf(ele);
					probability *= matrix[i][j] / nums[i];;
//					System.out.println(eleList.get(i) + "->" + eleList.get(j));
					preEle = ele;
				}
			}
			//片段异常度计算公式
			result = ATF / TF * (eleSet.size() - 1) * Math.log(probability);
//			System.out.println("概率 = " + probability);
			System.out.println(result);
			resultList.add(result);
		}
		return getResult();
	}
	
	
	public static List<Double> getResult(){
		List<Double> list = new ArrayList<Double>();
		
		return list;
	}
}
