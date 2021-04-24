package _util;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Vector;

import _BioStruct.Point3D;

public class _Vector {
	
	public static void main(String[] args) {
		double[] a = {1, 2};
		double[] b = {1.5, 3.5};
		double[][] ans = vector_a_mul_bT(a, b);
		for (int i = 0; i < a.length; i++){
			for (int j = 0; j < b.length; j++){
				System.out.print(ans[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public static double[] bias_vector(double[] a, double[] b){
		int feature_num = a.length;
		double[] ans = new double[feature_num];
		for (int i = 0; i < a.length; i++){
			ans[i] = a[i] - b[i];
		}
		
		return ans;
	}
	
	public static double[][] vector_a_mul_bT(double[] a, double[] b){
		int row = a.length;
		int col = b.length;
		double[][] ans = new double[row][col];
		for (int i = 0; i < row; i++){
			for (int j = 0; j < col; j++){
				ans[i][j] = a[i]*b[j]; 
			}
		}
		
		return ans;
	}
	
	public static double vector_a_dotmul_b(double[] a, double[] b){
		int row = a.length;
		double ans = 0.0;
		for (int i = 0; i < row; i++){
			ans += a[i]*b[i]; 
		}
		
		return ans;
	}
	
	public static double[] vector_a_mul_constB(double[] a, double B){
		int row = a.length;
		double[] ans = new double[row];
		for (int i = 0; i < row; i++){
			ans[i] = a[i] * B; 
		}
		
		return ans;
	}
	
	public static double[] vector_a_add_b(double[] a, double[] b){
		int row = a.length;
		double[] ans = new double[row];
		for (int i = 0; i < row; i++){
			ans[i] = a[i] + b[i]; 
		}
		
		return ans;
	}
	
	public static double[] vector_a_sub_b(double[] a, double[] b){
		int row = a.length;
		double[] ans = new double[row];
		for (int i = 0; i < row; i++){
			ans[i] = a[i] - b[i]; 
		}
		
		return ans;
	}
	
	public static void saveByCols(double[] v, String savePath)throws Exception{
		FileWriter fwFileWriter = new FileWriter(savePath);
		for (int i= 0; i < v.length; i++){
			fwFileWriter.write(v[i] + "\n");
		}
		fwFileWriter.close();
	}
	
	public static Vector<double[]> mean(Vector<double[]> a, Vector<double[]> b){
		Vector<double[]> ansVector = new Vector<double[]>();
		for (int i = 0; i < a.size(); i++){
			double[] tmpA = a.get(i);
			double[] tmpB = b.get(i);
			double[] tmp = new double[tmpA.length];
			for (int j = 0; j < tmpA.length; j++){
				tmp[j] = (tmpA[j] + tmpB[j])/2;
			}
			ansVector.add(tmp);
		}
		return ansVector;
	}
	
	public static Vector<double[]> simple_move(Vector<double[]> vd, double[] move_direct){
		Vector<double[]> ansVector = new Vector<double[]>();
		for (int i = 0; i < vd.size(); i++){
			double[] tmp = vd.get(i);
			for (int j = 0; j < tmp.length; j++){
				tmp[j] -= move_direct[j];
			}
			ansVector.add(tmp);
		}
		return ansVector;
	}
	
	public static boolean isContain(int[] arr, int a){
		for (int i = 0; i < arr.length; i++){
			if (a==arr[i]){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isContain(Integer[] arr, Integer a){
		for (int i = 0; i < arr.length; i++){
			if (arr[i].equals(a)){
				return true;
			}
		}
		return false;
	}
	
	public static int indexOf(Object[] arr, Object a){
		int ans = -1;
		for (int i = 0; i < arr.length; i++){
			if (arr[i].equals(a)){
				ans = i;
				break;
			}
		}
		return ans;
	}
	
	public static boolean isContain(Vector<String> arr, String a){
		for (int i = 0; i < arr.size(); i++){
			if (a.equals(arr.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isContain(Vector<Integer> arr, Integer a){
		if (null == arr)
			return false;
		
		for (int i = 0; i < arr.size(); i++){
			if (arr.get(i).equals(a)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isContain(Vector<Point3D> arr, Point3D a){
		for (int i = 0; i < arr.size(); i++){
			if (arr.get(i).equals(a)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isContain(String[] arr, String a){
		for (int i = 0; i < arr.length; i++){
			if (a.equals(arr[i])){
				return true;
			}
		}
		return false;
	}
	
	public static boolean[] getImgaeArr(boolean[] arr){
		boolean[] ans = new boolean[arr.length];
		for (int i = 0; i < arr.length; i++){
			ans[i] = arr[i];
		}
		
		return ans;
	}
	
	public static Vector<int[]> getImageVecIntArr(Vector<int[]> vecIntArr){
		Vector<int[]> ans = new Vector<int[]>();
		for (int i = 0; i < vecIntArr.size(); i++){
			int[] oArr = vecIntArr.get(i);
			int[] nArr = new int[oArr.length];
			for (int j = 0; j < oArr.length; j++)
				nArr[i] = oArr[i];
			
			ans.add(nArr);
		}
		
		return ans;
	}
	
	public static double biggest(double[] arr) {
		double ans = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > ans) {
				ans = arr[i];
			}
		}
		return ans;
	}
	
	public static int biggest(int[] arr) {
		int ans = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > ans) {
				ans = arr[i];
			}
		}
		return ans;
	}
	
	public static double biggest(Vector<Double> arr) {
		double ans = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i) > ans) {
				ans = arr.get(i);
			}
		}
		return ans;
	}
	
	public static double smallest(double[] arr) {
		double ans = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < ans) {
				ans = arr[i];
			}
		}
		return ans;
	}
	
	public static double smallest(Vector<Double> arr) {
		double ans = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i) < ans) {
				ans = arr.get(i);
			}
		}
		return ans;
	}

	public static int biggestPos(double[] arr) {
		int ans = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > arr[ans]) {
				ans = i;
			}
		}
		return ans;
	}

	public static double[] normalizeToOne(double[] a) {
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
			if (a[i] < min) {
				min = a[i];
			}
		}

		if (min == max) {
			for (int i = 0; i < a.length; i++) {
				a[i] = 1.0 / a.length;
			}
		} else {
			for (int i = 0; i < a.length; i++) {
				a[i] = (a[i] - min) / (max - min);
			}
		}
		return a;
	}

	public static HashMap<String, Double> normalizeToOne(HashMap<String, Double> AAIndex_map) {
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		Object[] AAarr = AAIndex_map.keySet().toArray();
		for (int i = 0; i < AAarr.length; i++) {
			double value = AAIndex_map.get(AAarr[i]) ;
			if (value > max) {
				max = value;
			}
			if (value < min) {
				min = value;
			}
		}

		if (min == max) {
			for (int i = 0; i < AAarr.length; i++) {
				AAIndex_map.put((String)AAarr[i], 1.0 / AAarr.length);
			}
		} else {
			for (int i = 0; i < AAarr.length; i++) {
				double value = AAIndex_map.get(AAarr[i]) ;
				AAIndex_map.put((String)AAarr[i], (value - min) / (max - min));
			}
		}
		return AAIndex_map;
	}
	
	public static HashMap<String, Double> gauss_normalize(HashMap<String, Double> AAIndex_map) {
		Object[] AAarr = AAIndex_map.keySet().toArray();
		double[] tmp_var = new double[AAarr.length];
		for (int i = 0; i < AAarr.length; i++) {
			double value = AAIndex_map.get(AAarr[i]) ;
			tmp_var[i] = value;
		}
		double mean = _Math.average(tmp_var);
		double sqrt_var = _Math.standart_deviation(tmp_var);
		for (int i = 0; i < AAarr.length; i++) {
			AAIndex_map.put((String)AAarr[i], (tmp_var[i] - mean) / sqrt_var);
		}
		return AAIndex_map;
	}
	
	public static double[] gauss_normalize(double[] arr) {
		double mean = _Math.average(arr);
		double sqrt_var = _Math.standart_deviation(arr);
		
		double[] ans = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			ans[i] = (arr[i] - mean) / sqrt_var;
		}
		return ans;
	}
	
	public static double[] normalizeToSumEqualOne(double[] a) {
		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}

		if (0.0 == sum) {
			for (int i = 0; i < a.length; i++) {
				a[i] = 1.0 / a.length;
			}
		} else {
			for (int i = 0; i < a.length; i++) {
				a[i] = a[i] / sum;
			}
		}
		return a;
	}
	
	public static double[] select(double[] original, int[] selectPos,
			boolean isSelectePosStartZero) {
		double[] ans = new double[selectPos.length];
		for (int i = 0; i < selectPos.length; i++) {
			if (isSelectePosStartZero) {
				ans[i] = original[selectPos[i]];
			} else {
				ans[i] = original[selectPos[i] - 1];
			}
		}
		return ans;
	}
	
	public static double[] select(double[] original, Vector<Integer> selectPos,
			boolean isSelectePosStartZero) {
		double[] ans = new double[selectPos.size()];
		for (int i = 0; i < selectPos.size(); i++) {
			if (isSelectePosStartZero) {
				ans[i] = original[selectPos.get(i)];
			} else {
				ans[i] = original[selectPos.get(i) - 1];
			}
		}
		return ans;
	}

	public static double[] vectorCat(double[]... ds) {
		int ansLen = 0;
		int paramLen = ds.length;
		for (int i = 0; i < paramLen; i++) {
			ansLen += ds[i].length;
		}

		double[] ans = new double[ansLen];
		int pos = 0;
		for (int i = 0; i < paramLen; i++) {
			for (int j = 0; j < ds[i].length; j++) {
				ans[pos] = ds[i][j];
				pos++;
			}
		}
		return ans;
	}

	public static double[] vectorCat(double[] a, double[] b) {
		double[] ans = new double[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			ans[i] = a[i];
		}
		for (int j = 0; j < b.length; j++) {
			ans[a.length + j] = b[j];
		}
		return ans;
	}

	public static double[] vectorCat(double[] a, double b) {
		double[] ans = new double[a.length + 1];
		for (int i = 0; i < a.length; i++) {
			ans[i] = a[i];
		}
		ans[a.length] = b;
		
		return ans;
	}
	
	public static double[] vectorCat(double b, double[] a) {
		double[] ans = new double[a.length + 1];
		ans[0] = b;
		for (int i = 1; i < ans.length; i++) {
			ans[i] = a[i-1];
		}
		
		return ans;
	}
	
	public static double distance(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++)
			sum += (a[i] - b[i]) * (a[i] - b[i]);

		return Math.sqrt(sum);
	}

	public static double junh_bigger_equal_MeanValue(double[] value, double threshold){
		double ans = 0;
		for (int i = 0; i < value.length; i++){
			if (value[i] >= threshold)
				ans += value[i];
		}
		
		return ans / value.length;
	}
	
	public static double junh_bigger_equal_NumPercent(double[] value, double threshold){
		int ans = 0;
		for (int i = 0; i < value.length; i++){
			if (value[i] >= threshold)
				ans ++;
		}
		
		return 1.0 * ans / value.length;
	}
	
	public static double junh_smaller_MeanValue(double[] value, double threshold){
		double ans = 0;
		for (int i = 0; i < value.length; i++){
			if (value[i] < threshold)
				ans += value[i];
		}
		
		return ans / value.length;
	}
	
	public static double[] toArray(Vector<Double> vd){
		int size = vd.size();
		double[] ans = new double[size];
//		System.out.print("toArray:\t");
		for (int i = 0; i < size; i++){
//			System.out.print(vd.get(i)+"\t");
			ans[i] = vd.get(i);
		}
//		System.out.println();
		
		return ans;
	}

	public static Integer[] toIntArray(Vector<Integer> vd){
		int size = vd.size();
		Integer[] ans = new Integer[size];
//		System.out.print("toArray:\t");
		for (int i = 0; i < size; i++){
//			System.out.print(vd.get(i)+"\t");
			ans[i] = vd.get(i);
		}
//		System.out.println();
		
		return ans;
	}
	
	public static String[] toStrArray(Vector<String> vd){
		int size = vd.size();
		String[] ans = new String[size];
//		System.out.print("toArray:\t");
		for (int i = 0; i < size; i++){
//			System.out.print(vd.get(i)+"\t");
			ans[i] = vd.get(i);
		}
//		System.out.println();
		
		return ans;
	}
	
	public static Vector<Object> arr2Vec(Object[] arr){
		Vector<Object> ans = new Vector<Object>();
		for (int i = 0; i < arr.length; i++){
			ans.add(arr[i]);
		}
		
		return ans;
	}
	
	public static Vector<String> arr2Vec(String[] arr){
		Vector<String> ans = new Vector<String>();
		for (int i = 0; i < arr.length; i++){
			ans.add(arr[i]);
		}
		
		return ans;
	}
	
	public static String toString(Vector<Double> vd){
		StringBuffer ans = new StringBuffer();
		int size = vd.size();
		for (int i = 0; i < size; i++){
			ans.append(_NumFormat.formatDouble(vd.get(i), "#0.000")+"\t");
		}
		return ans.toString().trim();
	}
	
	public static String toString(double[] arr){
		StringBuffer ans = new StringBuffer();
		int size = arr.length;
		for (int i = 0; i < size; i++){
			ans.append(_NumFormat.formatDouble(arr[i], "#0.000")+"\t");
		}
		return ans.toString().trim();
	}
	
	public static double[] descentSort(double[] a){
		double[] ans = new double[a.length];
		for (int i = 0; i < a.length; i++)
			ans[i] = a[i];
		for (int i = 1; i < a.length; i++){
			for (int j = 0; j < i; j++){
				if (ans[i] > ans[j]){
					double tmp = ans[i];
					ans[i] = ans[j];
					ans[j] = tmp;
				}
			}
		}
		
		return ans;
	}
	
	public static double[] ascentSort(double[] a){
		double[] ans = new double[a.length];
		for (int i = 0; i < a.length; i++)
			ans[i] = a[i];
		for (int i = 1; i < a.length; i++){
			for (int j = 0; j < i; j++){
				if (ans[i] < ans[j]){
					double tmp = ans[i];
					ans[i] = ans[j];
					ans[j] = tmp;
				}
			}
		}
		
		return ans;
		
	}
	
	public static Integer[] ascentSort(Integer[] a){
		Integer[] ans = new Integer[a.length];
		for (int i = 0; i < a.length; i++)
			ans[i] = a[i];
		for (int i = 1; i < a.length; i++){
			for (int j = 0; j < i; j++){
				if (ans[i] < ans[j]){
					Integer tmp = ans[i];
					ans[i] = ans[j];
					ans[j] = tmp;
				}
			}
		}
		
		return ans;
		
	}
	
	/**
	 * @param origVec 
	 * @param fromIndex
	 * @param toIndex
	 * @return the subset in oriVec from fromIndex (inclusive) to toIndex (exclusive). if fromIndex >= toIndex, return null;
	 */
	public static Vector<Object> subset(final Vector<Object> origVec, int fromIndex, int toIndex){
		if (toIndex <= fromIndex || toIndex > origVec.size() || fromIndex < 0){
			return null;
		}
		
		Vector<Object> ans = new Vector<Object>();
		for (int i = fromIndex; i < toIndex; i++){
			ans.add(origVec.get(i));
		}
		
		return ans;
	}
	
	/**
	 * @param origVec 
	 * @param fromIndex
	 * @param toIndex
	 * @return the subset in oriVec from fromIndex (inclusive) to toIndex (exclusive). if fromIndex >= toIndex, return null;
	 */
	public static Vector<Point3D> subset4Point3DVec(final Vector<Point3D> origVec, int fromIndex, int toIndex){
		if (toIndex <= fromIndex || toIndex > origVec.size() || fromIndex < 0){
			return null;
		}
		
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = fromIndex; i < toIndex; i++){
			ans.add(origVec.get(i));
		}
		
		return ans;
	}
	
	/**
	 * @param a : the vector a
	 * @param b : the vector b
	 * @param len : compare len [0, len)
	 * @return if the [0, len) contents is same, return true, otherwise return false;
	 */
	public static boolean isSame(double[] a, double[] b, int len){
		if (a.length < len || b.length < len){
			return false;
		}
		
		for (int i = 0; i < len; i++){
			if (a[i] != b[i]){
				return false;
			}
		}
		
		return true;
	}
	
	public static int sameElementNum(final Vector<Integer> a, final Vector<Integer> b){
		int ans = 0;
		
		if (null == a || a.isEmpty() || null == b || b.isEmpty()){
			return 0;
		}
		
		boolean[] aSec = new boolean[a.size()];
		boolean[] bSec = new boolean[b.size()];
		for (int i = 0; i < aSec.length; i++){
			if (aSec[i])
				continue;
			for (int j = 0; j < bSec.length; j++){
				if (bSec[j])
					continue;
				if (a.get(i).equals(b.get(j))){
					ans++;
					
					aSec[i] = true;
					bSec[j] = true;
					
					break;
				}
			}
		}
		
		return ans;
	}
	
	/**
	 * Firstly, for each point p_i, we search the minimum distance d_i other point.
	 * then, we calculate the max d_i.
	 * Calculate the max(min_i(p_i, p_j); j=1...i-1, i+1, ..., N) distance
	 * @param arr
	 * @return
	 */
	public static double maxMinDistanceBetweenAnyPointPair(Vector<Point3D> arr){
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < arr.size()-1; i++){
			double min = Double.MAX_VALUE;
			for (int j = 0; j < arr.size(); j++){
				if (i == j) continue;
				
				double dis = arr.get(i).distOu(arr.get(j));
				if (dis < min)
					min = dis;
			}
			
			if (min > max)
				max = min;
		}
		
		return max;
	}

	public static Point3D mean(Vector<Point3D> points){
		Point3D ans = new Point3D(0.0, 0.0, 0.0);
		for (int i = 0; i < points.size(); i++){
			ans.add(points.get(i));
		}
		
		ans.normalize(points.size());
		return ans;
	}
	
	/****************************************************************
	 * @param arr : the object array
	 * @param split_num : the split number
	 * @return key is two elements, the first is the under bound (inclusive), 
	 * the second is the upper bound (exclusive), the mean of them is the histgram x axis
	 * value is the unnormalized density. 
	 ****************************************************************/
	public static HashMap<double[], Integer> hist(Vector<Double> arr, int split_num){
		double max = _Vector.biggest(arr);
		double min = _Vector.smallest(arr);
		double step = (max - min) / split_num;
		
		HashMap<double[], Integer> ans = new HashMap<double[], Integer>();
		for (int i = 0; i < split_num; i++){
			double key_under_bound = min+step*i;
			double key_upper_bound = min+step*(i+1);
			double[] key = new double[2];
			key[0] = key_under_bound;
			key[1] = key_upper_bound;
			
			if (i+1 == split_num)
				key_upper_bound += Double.MIN_VALUE;
			
			int value = 0;
			for (int j = 0; j < arr.size(); j++){
				if (arr.get(j) >= key_under_bound 
						&& arr.get(j) < key_upper_bound){
					value++;
				}
			}
			
			ans.put(key, value);
		}
		
		
		return ans;
	}
}
