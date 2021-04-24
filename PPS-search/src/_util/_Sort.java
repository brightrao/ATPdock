package _util;

import java.util.Vector;

public class _Sort {

	public static int[] quickAscendSortIndex(final double[] arr, int[] indexes, int left, int right){
		if (null == indexes){
			indexes = new int[arr.length]; 
			for (int i = 0; i < indexes.length; i++)
				indexes[i] = i;
		}
		
		int tmp = indexes[left];
		int p = left;
		int i = left, j = right;
		for (; i <= j;){
			for (; j >= p && arr[indexes[j]] >= arr[tmp]; ){
				j--;
			}
			if (j >= p){
				indexes[p] = indexes[j];
				p = j;
			}
			
			if (arr[indexes[i]] <= arr[tmp] && i <= p){
				i++;
			}
			if (i <= p){
				indexes[p] = indexes[i];
				p = i;
			}
		}
		
		indexes[p] = tmp;
		if (p-left > 1){
			quickAscendSortIndex(arr, indexes, left, p-1);
		}
		
		if (right-p>1){
			quickAscendSortIndex(arr, indexes, p+1, right);
		}
		
		return indexes;
	}
	
	public static int[] quickDescendSortIndex(final double[] arr, int[] indexes, int left, int right){
		if (null == indexes){
			indexes = new int[arr.length]; 
			for (int i = 0; i < indexes.length; i++)
				indexes[i] = i;
		}
		
		int tmp = indexes[left];
		int p = left;
		int i = left, j = right;
		for (; i <= j;){
			for (; j >= p && arr[indexes[j]] <= arr[tmp]; ){
				j--;
			}
			if (j >= p){
				indexes[p] = indexes[j];
				p = j;
			}
			
			if (arr[indexes[i]] >= arr[tmp] && i <= p){
				i++;
			}
			if (i <= p){
				indexes[p] = indexes[i];
				p = i;
			}
		}
		
		indexes[p] = tmp;
		if (p-left > 1){
			quickDescendSortIndex(arr, indexes, left, p-1);
		}
		
		if (right-p>1){
			quickDescendSortIndex(arr, indexes, p+1, right);
		}
		
		return indexes;
	}
	
	/**
	 * @param arr : the true value array
	 * @return : the sorted indexes array, it means that the biggest value of the original array is arr[ans[0]]
	 */
	public static int[] insertDescendSortIndex(final double[] arr){
		int[] indexes = new int[arr.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i; j++){
				if (arr[indexes[i]] > arr[indexes[j]]){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		return indexes;
	}
	
	/**
	 * @param arr
	 * @param sortTopN
	 * @return only return the sortTopN indexes
	 */
	public static int[] insertDescendSortIndex(final double[] arr, int sortTopN){
		int[] indexes = new int[arr.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i && j < sortTopN; j++){
				if (arr[indexes[i]] > arr[indexes[j]]){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		if (indexes.length > sortTopN){
			int[] ans = new int[sortTopN];
			for (int i = 0; i < sortTopN; i++){
				ans[i] = indexes[i];
			}
			
			return ans;
		}
		
		return indexes;
	}
	
	/**
	 * @param arr
	 * @param sortTopN
	 * @return only return the sortTopN indexes
	 */
	public static int[] insertDescendSortIndexV2(final double[] arr, int sortTopN){
		int[] indexes = new int[arr.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i && j < sortTopN; j++){
				if (arr[indexes[i]] >= arr[indexes[j]]){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		if (indexes.length > sortTopN){
			int[] ans = new int[sortTopN];
			for (int i = 0; i < sortTopN; i++){
				ans[i] = indexes[i];
			}
			
			return ans;
		}
		
		return indexes;
	}
	
	/**
	 * @param arr
	 * @param sortTopN, if <= 0 means all of them
	 * @return only return the sortTopN indexes
	 */
	public static int[] insertAscendSortIndex(final Vector<Double> arr, int sortTopN){
		sortTopN = sortTopN <= 0 ? arr.size() : sortTopN;
		
		int[] indexes = new int[arr.size()];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i && j < sortTopN; j++){
				if (arr.get(indexes[i]) < arr.get(indexes[j])){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		if (indexes.length > sortTopN){
			int[] ans = new int[sortTopN];
			for (int i = 0; i < sortTopN; i++){
				ans[i] = indexes[i];
			}
			
			return ans;
		}
		
		return indexes;
	}
	
	/**
	 * @param arr
	 * @param sortTopN, if <= 0 means all of them
	 * @return only return the sortTopN indexes
	 */
	public static int[] insertDescendSortIndex(final Vector<Double> arr, int sortTopN){
		sortTopN = sortTopN <= 0 ? arr.size() : sortTopN;
		
		int[] indexes = new int[arr.size()];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i && j < sortTopN; j++){
				if (arr.get(indexes[i]) > arr.get(indexes[j])){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		if (indexes.length > sortTopN){
			int[] ans = new int[sortTopN];
			for (int i = 0; i < sortTopN; i++){
				ans[i] = indexes[i];
			}
			
			return ans;
		}
		
		return indexes;
	}
	
	/**
	 * @param arr
	 * @param sortTopN, if <= 0 means all of them
	 * @return only return the sortTopN indexes
	 */
	public static int[] insertAscendSort4VecInt(final Vector<Integer> arr, int sortTopN){
		sortTopN = sortTopN <= 0 ? arr.size() : sortTopN;
		
		int[] indexes = new int[arr.size()];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i && j < sortTopN; j++){
				if (arr.get(indexes[i]) < arr.get(indexes[j])){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		if (indexes.length > sortTopN){
			int[] ans = new int[sortTopN];
			for (int i = 0; i < sortTopN; i++){
				ans[i] = indexes[i];
			}
			
			return ans;
		}
		
		return indexes;
	}
	
	/**
	 * @param arr
	 * @param sortTopN
	 * @return only return the sortTopN indexes
	 */
	public static int[] insertAscendSortIndex(final double[] arr, int sortTopN){
		int[] indexes = new int[arr.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i && j < sortTopN; j++){
				if (arr[indexes[i]] < arr[indexes[j]]){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		if (indexes.length > sortTopN){
			int[] ans = new int[sortTopN];
			for (int i = 0; i < sortTopN; i++){
				ans[i] = indexes[i];
			}
			
			return ans;
		}
		
		return indexes;
	}
	
	/**
	 * @param arr : the true value array
	 * @return : the sorted indexes array, it means that the smallest value of the original array is arr[ans[0]]
	 */
	public static int[] insertAscendSortIndex(final double[] arr){
		int[] indexes = new int[arr.length];
		for (int i = 0; i < indexes.length; i++)
			indexes[i] = i;
		
		for (int i = 1; i < indexes.length; i++){
			for (int j = 0; j < i; j++){
				if (arr[indexes[i]] < arr[indexes[j]]){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		return indexes;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] arr = {0.1, 0.0, 0.2, 0.5, 0.3};
		int[] sortedIndex = quickDescendSortIndex(arr, null, 0, arr.length-1);
		for (int i = 0; i < sortedIndex.length; i++)
			System.out.println(arr[sortedIndex[i]]);
	}

}
