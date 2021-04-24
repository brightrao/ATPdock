package _util;

import _BioStruct.Point3D;

public class _Distance {

	/**
	 * @function : calculate the inner product of x, y vector*/
	public static double inner_product(double[] x, double[] y) throws Exception{
		double ans = 0.0;
		int xlen = x.length;
		int ylen = y.length;
		if (xlen != ylen){
			throw new Exception("x is not match y!");
		}
		
		for (int i = 0; i < xlen; i++){
			ans += x[i]*y[i];
		}
		
		return ans;
	}
	
	/**
	 * @function : calculate the inner product of x, y vector*/
	public static double inner_product(Point3D xPos, Point3D yPos) throws Exception{
		double ans = 0.0;
		double[] x = xPos.toArray();
		double[] y = yPos.toArray();
		
		int xlen = x.length;
		int ylen = y.length;
		if (xlen != ylen){
			throw new Exception("x is not match y!");
		}
		
		for (int i = 0; i < xlen; i++){
			ans += x[i]*y[i];
		}
		
		return ans;
	}
	
	/**
	 * @function : calculate the cos value of x, y angle*/
	public static double cos_angle(double[] x, double[] y)throws Exception{
		double inner = inner_product(x, y);
		double xDis = Math.sqrt(inner_product(x, x));	//inner product space is l-2 norm space
		double yDis = Math.sqrt(inner_product(y, y));
		
		return inner / (xDis * yDis);
	}
	
	/**
	 * @function : calculate the euclidean distance of x, y vectors.*/
	public static double euclidean_distance(double[] x, double[] y) throws Exception{
		double ans = 0.0;
		int xlen = x.length;
		int ylen = y.length;
		if (xlen != ylen){
			throw new Exception("x is not match y!");
		}
		
		for (int i = 0; i < xlen; i++){
			ans += (x[i] - y[i]) * (x[i] - y[i]);
		}
		
		return Math.sqrt(ans);
	}
	
	public static void main(String[] args) throws Exception{
		double[] x = {-1, 2};
		double[] y = {7, 5};
		double ans1 = inner_product(x, y);
		double ans2 = inner_product(y, x);
		System.out.println(ans1 + " " + ans2);
	}

}
