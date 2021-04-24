package _util;

public class _Str {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String replenishHeadWithSpace(String str, int total_len){
		int oLen = str.length();
		if (oLen > total_len)
			return str;
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < total_len-oLen; i++)
			sb.append(' ');
		sb.append(str);
		
		return sb.toString();
	}
	
	public static String replenishHeadWithChar(String str, int total_len, char ch){
		int oLen = str.length();
		if (oLen > total_len)
			return str;
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < total_len-oLen; i++)
			sb.append(ch);
		sb.append(str);
		
		return sb.toString();
	}
	
	public static String replenishEndWithSpace(String str, int total_len){
		int oLen = str.length();
		if (oLen > total_len)
			return str;
		
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		for (int i = 0; i < total_len-oLen; i++)
			sb.append(' ');
		
		return sb.toString();
	}
	
	public static String replenishEndWithChar(String str, int total_len, char ch){
		int oLen = str.length();
		if (oLen > total_len)
			return str;
		
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		for (int i = 0; i < total_len-oLen; i++)
			sb.append(ch);
		
		return sb.toString();
	}
	
	public static double tanimotoCoefficient(String FPTa, String FPTb){
		int len = FPTa.length() > FPTb.length() ? FPTb.length() : FPTa.length();
		
		int aasum = 0;
        int bbsum = 0;
        int absum = 0;
        
        for(int i = 0; i < len; i++){
        	double a = 0;
        	if ('1' == FPTa.charAt(i))
        		a = 1;
        	
        	double b = 0;
        	if ('1' == FPTb.charAt(i))
        		b = 1;
        	
        	aasum += a*a;
        	bbsum += b*b;
        	absum += a*b;
        } 
               
        return 1.0*absum / (aasum+bbsum-absum);   
	}
}
