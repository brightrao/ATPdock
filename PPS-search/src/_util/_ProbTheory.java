package _util;

public class _ProbTheory {
	/**
	 * 
	 * @return Jensen¨CShannon divergence (JSD): lamta*D(P||M)+(1-lamta)*D(Q||M), where M=(P+Q)/2, D(X||M)=sum_i {P_i*log(P_i/M_i)}
	 */
	public static double JSD(double[] P, double[] Q, Double lamta)throws Exception{
		if (null == lamta)
			lamta = 0.5;
		
		double[] M = new double[P.length];
		for (int i = 0; i < P.length; i++)
			M[i] = lamta*P[i]+(1-lamta)*Q[i];
		
		return lamta*KLD(P, M)+(1-lamta)*KLD(Q, M);
	}
	
	/**
	 * @param P : the discrete probability distribution
	 * @param Q : the discrete probability distribution
	 * @return Kullback¨CLeibler divergence: D(X||M)=sum_i {P_i*log(P_i/M_i)}
	 */
	public static double KLD(double[] P, double[] Q)throws Exception{
		double kld = 0.0;
		for (int i = 0; i < P.length; i++){
			if (0 == Q[i]){
				throw new Exception("_ProbTheory.KLD Q distribution contains \"0\".");
			}
				
			if (0 != P[i])
				kld += P[i]*Math.log(P[i]/Q[i]);
		}
		
		return kld;
	}
	
	public static void main(String[] args)throws Exception{
		double[] P = {2, 3, 4, 1, 1};
		double[] Q = {3, 3, 2, 1, 0};
		
		System.out.println(_ProbTheory.JSD(P, Q, null));
	}
}
