package _util;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;

public class _Log {
	private static String logfolder = "./DefaultLog";
	
	private static String abslogname = "ABSTRACT.log";
	
	// write the total abstract log records of the program running
	@SuppressWarnings("deprecation")
	public static synchronized void visitedLog(String record){
		String tmp = ConfigUtil.getConfig("LOG_FOLDER_DIR");
		if (null != tmp)
			logfolder = tmp;
		
		if (!new File(logfolder).exists())
			new File(logfolder).mkdirs();
		Date date = new Date();
		int year = date.getYear()+1900;
		int month = date.getMonth()+1;
		int day = date.getDate();
		int hour = date.getHours();
		int minute = date.getMinutes();
		String visitedTime =_Str.replenishHeadWithChar(""+year, 4, '0')+"-"
				+ _Str.replenishHeadWithChar(""+month, 2, '0')+ "-"
				+ _Str.replenishHeadWithChar(""+day, 2, '0')+" " 
				+ _Str.replenishHeadWithChar(""+hour, 2, '0')+" : " + _Str.replenishHeadWithChar(""+minute, 2, '0');
		try{
			FileWriter fw = new FileWriter(logfolder+System.getProperty("file.separator")+abslogname, true);
			fw.write(visitedTime+"\t"+record+"\n");
			fw.close();
		}catch(Exception e){
			System.out.println("Exception : ");
			e.printStackTrace();
		}
		
	}
	
	// write the total visit log records of the program running
	@SuppressWarnings("deprecation")
	public static synchronized void visitedLog(String ip, Date date, String usrname, String email, String runInfoAbs){
		String tmp = ConfigUtil.getConfig("LOG_FOLDER_DIR");
		if (null != tmp)
			logfolder = tmp;
		
		if (!new File(logfolder).exists())
			new File(logfolder).mkdirs();
		
		if (!new File(logfolder+System.getProperty("file.separator")+abslogname).exists()){
			try{
				FileWriter fw = new FileWriter(logfolder+System.getProperty("file.separator")+abslogname);
				fw.write("Time\tUsrname\tEmail\tIP\trunInfoAbs\n");
				fw.close();
			}catch(Exception e){
				System.out.println("Exception : ");
				e.printStackTrace();
			}
		}
		
		try{
			FileWriter fw = new FileWriter(logfolder+System.getProperty("file.separator")+abslogname, true);
			fw.write(MessageFormat.format("{0}\t{1}\t{2}\t{3}\t{4}\n", date.toLocaleString(),
					usrname, email, ip, runInfoAbs));
			fw.close();
		}catch(Exception e){
			System.out.println("Exception : ");
			e.printStackTrace();
		}
		
	}
	
	// write each day log records for every day after the program running
	@SuppressWarnings("deprecation") // the coder ensure the warning is right.
	public static synchronized void dayRunLog(String record, Date date){
		String tmp = ConfigUtil.getConfig("LOG_FOLDER_DIR");
		if (null != tmp)
			logfolder = tmp;
		
		if (!new File(logfolder).exists())
			new File(logfolder).mkdirs();
		
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#00");
		String year_month_day = ""+(date.getYear()+1900)+df.format(date.getMonth()+1)+df.format(date.getDate());
		
		int hour = date.getHours();
		int minute = date.getMinutes();
		String visitedTime = _Str.replenishHeadWithChar(""+hour, 2, '0')+" : " + _Str.replenishHeadWithChar(""+minute, 2, '0');
		try{
			FileWriter fw = new FileWriter(logfolder+System.getProperty("file.separator")+year_month_day, true);
			fw.write(visitedTime+"\t"+record+"\n");
			fw.close();
		}catch(Exception e){
			System.out.println("Exception : ");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{
		Date date = new Date();
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#00");
		
		System.out.println(""+(date.getYear()+1900)+df.format(date.getMonth()+1)+df.format(date.getDate()));
		System.out.println(""+(date.getYear()+1900)+(date.getMonth()+1)+(date.getDate()));
		
		System.out.println();
	}
}
