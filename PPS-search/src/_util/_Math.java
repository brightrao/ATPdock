package _util;

import java.util.Vector;

public class _Math {
	
	public static double average(double[] vd){
		double EX = 0.0;
		
		int size = vd.length;
		for (int i = 0; i < size; i++){
			EX += vd[i];
		}
		EX /= size;
		return EX;
	}
	
	public static double average(Vector<Double> vd){
		double EX = 0.0;
		
		int size = vd.size();
		for (int i = 0; i < size; i++){
			EX += vd.get(i);
		}
		EX /= size;
		return EX;
	}
	
	public static double average(double[] vd, int start, int endPlusOne){
		double EX = 0.0;
		
		for (int i = start; i < endPlusOne; i++){
			EX += vd[i];
		}
		EX /= endPlusOne-start;
		return EX;
	}
	
	public static double variance_calculate_Type_I(Vector<Double> vd){
		double DX = 0.0;   //D(X) = E((X - EX)^2)
		double EX = 0.0;
		
		int size = vd.size();
		for (int i = 0; i < size; i++){
			EX += vd.get(i);
		}
		EX /= size;
		
		for (int i = 0; i < size; i++){
			DX = (vd.get(i) - EX)*(vd.get(i) - EX);
		}
		DX /= size;
		
		return DX;
	}
		
	public static double variance_calculate_Type_II(Vector<Double> vd){
		double DX = 0.0;   //D(X) = E(X^2) - (EX)^2
		double EX = 0.0;
		double EX_2 = 0.0;
		
		int size = vd.size();
		for (int i = 0; i < size; i++){
			EX += vd.get(i);
			EX_2 += ((vd.get(i))*(vd.get(i)));
		}
		EX /= size;
		EX_2 /= size;
		DX = EX_2 - EX*EX;
		
		return DX;
	}
	
	public static double variance_calculate_Type_II(double[] vd){
		double DX = 0.0;   //D(X) = E(X^2) - (EX)^2
		double EX = 0.0;
		double EX_2 = 0.0;
		
		int size = vd.length;
		for (int i = 0; i < size; i++){
			EX += vd[i];
			EX_2 += vd[i]*vd[i];
		}
		EX /= size;
		EX_2 /= size;
		DX = EX_2 - EX*EX;
		
		return DX;
	}
	
	public static double standart_deviation(Vector<Double> vd){
		return Math.sqrt(variance_calculate_Type_II(vd));
	}
	
	public static double standart_deviation(double[] vd){
		return Math.sqrt(variance_calculate_Type_II(vd));
	}
	
	// we do not change the original arr content
	public static int[] ascendSort(int[] arr){
		int[] ans = new int[arr.length];
		for (int i = 0; i < ans.length; i++)
			ans[i] = arr[i];
		
		for (int i = 1; i < ans.length; i++){
			for (int j = 0; j < i; j++){
				if (ans[i] < ans[j]){
					int tmp = ans[i];
					ans[i] = ans[j];
					ans[j] = tmp;
				}
			}
		}
		
		return ans;
	}
	
	/**
	 * one hexadecimal bit number to 4 bits binary number 
	 * @param ch : one hexadecimal bit
	 * @return 4 bits binary number
	 */
	public static String hexadecimal2binary(char ch){
		if ('0' == ch){
			return "0000";
		}
		if ('1' == ch){
			return "0001";
		}
		if ('2' == ch){
			return "0010";
		}
		if ('3' == ch){
			return "0011";
		}
		if ('4' == ch){
			return "0100";
		}
		if ('5' == ch){
			return "0101";
		}
		if ('6' == ch){
			return "0110";
		}
		if ('7' == ch){
			return "0111";
		}
		if ('8' == ch){
			return "1000";
		}
		if ('9' == ch){
			return "1001";
		}
		if ('a' == ch || 'A' == ch){
			return "1010";
		}
		if ('b' == ch || 'B' == ch){
			return "1011";
		}
		if ('c' == ch || 'C' == ch){
			return "1100";
		}
		if ('d' == ch || 'D' == ch){
			return "1101";
		}
		if ('e' == ch || 'E' == ch){
			return "1110";
		}
		if ('f' == ch || 'F' == ch){
			return "1111";
		}
		
		return "0000";
	}
	
	public static Integer binary2Decimal(String fourBit){
		int ans = 0;
		for (int i = fourBit.length()-1, j = 0; i >= 0; i--, j++){
			char ch = fourBit.charAt(i);
			if ('0' != ch && '1' != ch){
				return null;
			}
			
			if ('1' == ch)
				ans += Math.pow(2, j);
		}
		return ans;
	}
	
	public static double atof(String s)	{
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d))
		{
			System.err.print("NaN or Infinity in input\n");
			System.exit(1);
		}
		return(d);
	}

	public static int atoi(String s) {
		return Integer.parseInt(s);
	}
	
	public static void main(String[] args){
		System.out.println(binary2Decimal("1111"));
	}
	
	/**
	 * @param xi, yi, zi : the i-th point xyz
	 * @return the center xyz of the ball by using four point (on the suface of the ball) information 
	 * 			using the Cramer's Rule
	 */
	public static double[] calculateTheCenterXYZOfBall(double x1,double y1,double z1,  
            double x2,double y2,double z2,  
            double x3,double y3,double z3,  
            double x4,double y4,double z4)
	{  
	   double a11,a12,a13,a21,a22,a23,a31,a32,a33,b1,b2,b3,d,d1,d2,d3;  
	   a11=2*(x2-x1); a12=2*(y2-y1); a13=2*(z2-z1);  
	   a21=2*(x3-x2); a22=2*(y3-y2); a23=2*(z3-z2);  
	   a31=2*(x4-x3); a32=2*(y4-y3); a33=2*(z4-z3);  
	   b1=x2*x2-x1*x1+y2*y2-y1*y1+z2*z2-z1*z1;  
	   b2=x3*x3-x2*x2+y3*y3-y2*y2+z3*z3-z2*z2;  
	   b3=x4*x4-x3*x3+y4*y4-y3*y3+z4*z4-z3*z3;  
	   d=a11*a22*a33+a12*a23*a31+a13*a21*a32-a11*a23*a32-a12*a21*a33-a13*a22*a31;  
	   d1=b1*a22*a33+a12*a23*b3+a13*b2*a32-b1*a23*a32-a12*b2*a33-a13*a22*b3;  
	   d2=a11*b2*a33+b1*a23*a31+a13*a21*b3-a11*a23*b3-b1*a21*a33-a13*b2*a31;  
	   d3=a11*a22*b3+a12*b2*a31+b1*a21*a32-a11*b2*a32-a12*a21*b3-b1*a22*a31;
	   
	   double[] xyz = new double[3];
	   xyz[0]=d1/d;  
	   xyz[1]=d2/d;  
	   xyz[2]=d3/d;
	   
	   return xyz; 
	}  
}
