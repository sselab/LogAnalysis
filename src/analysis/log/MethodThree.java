//package analysis.log;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//
//import component.Segment;
//import kmeans.kmeans;
//import kmeans.kmeans_data;
//import kmeans.kmeans_param;
//
//public class MethodThree {
//	public static void threeMethod(String dirPath,String type){
//		List<Segment> segmentList = Method.getAllSegment(dirPath, type);
//		//利用list将每个模板编号映射到0到模板数量-1的整数，并构造矩阵
//		List<Integer> allEleList = new ArrayList<Integer>();
//		double[][] matrix = makeMtrix(segmentList,allEleList);
//		
//		//计数非零节点
//		int notZeroNum = 0;
//		for(int i = 0; i < matrix.length; ++i)
//		for(int j  =0; j < matrix.length; ++j){
//				notZeroNum ++;
//		}
//		
//		//利用kmeans算法聚类，分为矩阵大小/非零数大小个类
//		kmeans_data data = new kmeans_data(matrix, matrix.length, matrix.length);
//		kmeans_param param = new kmeans_param();
//		//设置聚类中心点的初始化模式为随机模式
//		param.initCenterMehtod = kmeans_param.CENTER_RANDOM; 
//		int cluster_num = matrix.length * matrix.length / notZeroNum;
//		kmeans.doKmeans(cluster_num, data, param);
//		
//		//打印每个模板对应的类的序号
//		for (int lable : data.labels) {
//			System.out.print(lable + "  ");
//		}
//		System.out.println();
//		
//		//利用list保存每个类簇
//		List<List<Integer>> clusterList = new ArrayList<List<Integer>>();
//		List<Integer> cluster = new ArrayList<Integer>();
//		for(int i = 0; i < cluster_num ; ++i){
//			cluster.clear();
//			for(int j = 0; j < data.labels.length; ++j){
//				if(data.labels[j] == i){
//					cluster.add(allEleList.get(j));
//				}
//			}
//			clusterList.add(new ArrayList<Integer>(cluster));
//		}
//		
//		//打印最后的分类结果
//		System.out.println(clusterList);
//		
//		//随机从每个类选一个供专家判断
//		for(List<Integer> clu : clusterList){
//			Random random = new Random(System.currentTimeMillis());
//			System.out.println(clu.get(random.nextInt(clu.size())));
//		}
//	}
//	
//	private static double[][] makeMtrix(List<Segment> segmentList,List<Integer> allEleList){
//		Set<Integer> allEleSet = new LinkedHashSet<Integer>();
//		for(Segment seg : segmentList){
//			allEleSet.addAll(seg.getLogSegment());
//		}
//		allEleList.addAll(allEleSet);
//		
//		//构造矩阵，使矩阵对称  
//		double[][] matrix = new double[allEleSet.size()][allEleSet.size()];
//		for(int i = 0; i < matrix.length; ++i)
//		for(int j = 0; j < matrix.length; ++j)
//			matrix[i][j] = 0;
//		//如日志片段为<a-b-c>,value，则M[a][b] += value,M[b][c] += value
//		for(Segment seg : segmentList){
//			double value = seg.getValue();
//			Iterator<Integer> it = seg.getLogSegment().iterator();
//			Integer preEle = null;
//			while(it.hasNext()){
//				Integer ele = it.next();
//				if(preEle == null){
//					preEle = ele;
//				}else{
//					int i = allEleList.indexOf(preEle);
//					int j = allEleList.indexOf(ele);
//					matrix[i][j] += value;
//					matrix[j][i] += value;
//					preEle = ele;
//				}
//			}
//		}
//		
//		//获取每行最大值，并将矩阵标准化，每个元素/每行最大值
//		double[] maxs = new double[matrix.length];
//		for(int i = 0; i < maxs.length; ++i){
//			maxs[i] = 0.0;
//		}
//		for(int i = 0; i < matrix.length; ++i)
//		for(int j  =0; j < matrix.length; ++j){
//			if(matrix[i][j] != 0){
//				if(matrix[i][j] > maxs[i]){
//					maxs[i] = matrix[i][j];
//				}
//			}
//		}
//		//矩阵标准化，都除以当前行的最大值
//		for(int i = 0; i < matrix.length; ++i)
//		for(int j  =0; j < matrix.length; ++j){
//			matrix[i][j] /= maxs[i];
//		}
//		return matrix;
//	}
//}
