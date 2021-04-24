package _util;

import java.util.Random;

public class _Random extends Random {

	private static final long serialVersionUID = 8040991746668918195L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] ans = new _Random(1001).randperm(6, 10);
		for (int i = 0; i < ans.length; i++){
			System.out.println(ans[i]);
		}
	}

	public _Random(long seed){
		super(seed);
	}
	
	public _Random(){
	}
	
	/**
	 * re-sort the numbers between 0 (include) and bound (exclude) to disorder these number
	 * 
	 * @param bound : need larger than 0
	 * @return
	 */
	public int[] randperm(int bound){
		int[] ans = new int[bound];
		for (int i = 0; i < bound; i++) ans[i] = i;
		
		// Switch bound*1.2 times
		for (int i = 0; i < bound*1.2; i++){
			int aInd = this.nextInt(bound);
			int bInd = this.nextInt(bound);
			if (aInd != bInd){
				int tmp = ans[aInd];
				ans[aInd] = ans[bInd];
				ans[bInd] = tmp;
			}
		}
		
		return ans;
	}
	
	/**
	 * re-sort the numbers between lower_bound (include) and upper_bound (exclude) to disorder these number
	 * 
	 * @param lower_bound
	 * @param upper_bound
	 * @return
	 */
	public int[] randperm(int lower_bound, int upper_bound){
		int n = upper_bound - lower_bound;
		
		int[] ans = randperm(n);
		for (int i = 0; i < ans.length; i++){
			ans[i] += lower_bound;
		}
		
		return ans;
	}
}
