package _util;

public class BLOSUM62 {
	private static String aaTitle = "ARNDCQEGHILKMFPSTWYVBJZX*";
	private static int min = -4;
	private static int max = 11;
	private static int[][] oArr =
			// A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  J  Z  X  *
		    {{ 4,-1,-2,-2, 0,-1,-1, 0,-2,-1,-1,-1,-1,-2,-1, 1, 0,-3,-2, 0,-2,-1,-1,-1,-4},
		     {-1, 5, 0,-2,-3, 1, 0,-2, 0,-3,-2, 2,-1,-3,-2,-1,-1,-3,-2,-3,-1,-2, 0,-1,-4},
		     {-2, 0, 6, 1,-3, 0, 0, 0, 1,-3,-3, 0,-2,-3,-2, 1, 0,-4,-2,-3, 4,-3, 0,-1,-4},
		     {-2,-2, 1, 6,-3, 0, 2,-1,-1,-3,-4,-1,-3,-3,-1, 0,-1,-4,-3,-3, 4,-3, 1,-1,-4},
		     { 0,-3,-3,-3, 9,-3,-4,-3,-3,-1,-1,-3,-1,-2,-3,-1,-1,-2,-2,-1,-3,-1,-3,-1,-4},
		     {-1, 1, 0, 0,-3, 5, 2,-2, 0,-3,-2, 1, 0,-3,-1, 0,-1,-2,-1,-2, 0,-2, 4,-1,-4},
		     {-1, 0, 0, 2,-4, 2, 5,-2, 0,-3,-3, 1,-2,-3,-1, 0,-1,-3,-2,-2, 1,-3, 4,-1,-4},
		     { 0,-2, 0,-1,-3,-2,-2, 6,-2,-4,-4,-2,-3,-3,-2, 0,-2,-2,-3,-3,-1,-4,-2,-1,-4},
		     {-2, 0, 1,-1,-3, 0, 0,-2, 8,-3,-3,-1,-2,-1,-2,-1,-2,-2, 2,-3, 0,-3, 0,-1,-4},
		     {-1,-3,-3,-3,-1,-3,-3,-4,-3, 4, 2,-3, 1, 0,-3,-2,-1,-3,-1, 3,-3, 3,-3,-1,-4},
		     {-1,-2,-3,-4,-1,-2,-3,-4,-3, 2, 4,-2, 2, 0,-3,-2,-1,-2,-1, 1,-4, 3,-3,-1,-4},
		     {-1, 2, 0,-1,-3, 1, 1,-2,-1,-3,-2, 5,-1,-3,-1, 0,-1,-3,-2,-2, 0,-3, 1,-1,-4},
		     {-1,-1,-2,-3,-1, 0,-2,-3,-2, 1, 2,-1, 5, 0,-2,-1,-1,-1,-1, 1,-3, 2,-1,-1,-4},
		     {-2,-3,-3,-3,-2,-3,-3,-3,-1, 0, 0,-3, 0, 6,-4,-2,-2, 1, 3,-1,-3, 0,-3,-1,-4},
		     {-1,-2,-2,-1,-3,-1,-1,-2,-2,-3,-3,-1,-2,-4, 7,-1,-1,-4,-3,-2,-2,-3,-1,-1,-4},
		     { 1,-1, 1, 0,-1, 0, 0, 0,-1,-2,-2, 0,-1,-2,-1, 4, 1,-3,-2,-2, 0,-2, 0,-1,-4},
		     { 0,-1, 0,-1,-1,-1,-1,-2,-2,-1,-1,-1,-1,-2,-1, 1, 5,-2,-2, 0,-1,-1,-1,-1,-4},
		     {-3,-3,-4,-4,-2,-2,-3,-2,-2,-3,-2,-3,-1, 1,-4,-3,-2,11, 2,-3,-4,-2,-2,-1,-4},
		     {-2,-2,-2,-3,-2,-1,-2,-3, 2,-1,-1,-2,-1, 3,-3,-2,-2, 2, 7,-1,-3,-1,-2,-1,-4},
		     { 0,-3,-3,-3,-1,-2,-2,-3,-3, 3, 1,-2, 1,-1,-2,-2, 0,-3,-1, 4,-3, 2,-2,-1,-4},
		     {-2,-1, 4, 4,-3, 0, 1,-1, 0,-3,-4, 0,-3,-3,-2, 0,-1,-4,-3,-3, 4,-3, 0,-1,-4},
		     {-1,-2,-3,-3,-1,-2,-3,-4,-3, 3, 3,-3, 2, 0,-3,-2,-1,-2,-1, 2,-3, 3,-3,-1,-4},
		     {-1, 0, 0, 1,-3, 4, 4,-2, 0,-3,-3, 1,-1,-3,-1, 0,-1,-2,-2,-2, 0,-3, 4,-1,-4},
		     {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-4},
		     {-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4, 1}};
	/**
	 * @param rowAA
	 * @param colAA
	 * @return the original value : BLOSUM62['rowAA']['colAA'], 
	 */
	public static int get(char rowAA, char colAA){
		return oArr[aaTitle.indexOf(rowAA)][aaTitle.indexOf(colAA)];
	}
	
	public static double getNorm(char rowAA, char colAA){
		int oVal = oArr[aaTitle.indexOf(rowAA)][aaTitle.indexOf(colAA)]; 
		return 1.0*(oVal-min)/(max-min);
	}
	
//	# This is the BLOSUM62 background distribution. 
//  # TM-SITE JSDmod says:
//  # Denote the background frequency by Q, which can be estimated using a large set of random sequences.
//	my %blsm_bck= #construct a HashMap
//	    (
//	     'A'=>0.078, 'R'=>0.051, 'N'=>0.041, 'D'=>0.052, 'C'=>0.024, 'Q'=>0.034, 'E'=>0.059, 'G'=>0.083, 
//	     'H'=>0.025, 'I'=>0.062, 'L'=>0.092, 'K'=>0.056, 'M'=>0.024, 'F'=>0.044, 'P'=>0.043, 'S'=>0.059, 
//	     'T'=>0.055, 'W'=>0.014, 'Y'=>0.034, 'V'=>0.072, 'B'=>0.041, 'J'=>0.000, 'Z'=>0.034, 'X'=>0.083, '*'=>0.000);
											//		A		R		N		D		C		Q		E		G
	private static double[] blsm_back_AA_Freqs = {	0.078, 	0.051, 	0.041, 	0.052, 	0.024, 	0.034, 	0.059, 	0.083, 
											//		H		I		L		K		M		F		P		S		
													0.025, 	0.062, 	0.092, 	0.056, 	0.024, 	0.044, 	0.043, 	0.059, 
											//		T		W		Y		V		B		J		Z		X		*	    - 		~
													0.055, 	0.014, 	0.034, 	0.072, 	0.041, 	0.0001,	0.034, 	0.083, 	0.0001, 0.0001, 0.0001};
	private static String blsm_back_AAs_Title = "ARNDCQEGHILKMFPSTWYVBJZX*-~";
	public static double getBlsmBackAAFreqs(char aa){
		return blsm_back_AA_Freqs[blsm_back_AAs_Title.indexOf(aa)];
	}
	
}
