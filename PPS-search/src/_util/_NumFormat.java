package _util;

import java.text.DecimalFormat;

public class _NumFormat {
	
	public static String formatInteger(int val, String format){
		DecimalFormat d4f = new DecimalFormat();
		d4f.applyPattern(format);
		
		return d4f.format(val);
	}
	
	public static String formatDouble(double val, String format){
		DecimalFormat d4f = new DecimalFormat();
		d4f.applyPattern(format);
		
		return d4f.format(val);
	}
	
}
