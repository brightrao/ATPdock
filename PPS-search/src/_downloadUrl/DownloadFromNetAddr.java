package _downloadUrl;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.net.URL;

public class DownloadFromNetAddr {
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		if (2 != args.length){
			System.out.println("e.g., \n" +
					"Net_Address (String) (\"http://csbio.njust.edu.cn/members/student_hujun.html\")\n" +
					"SavePath (String) (\"D:/111.del\")");
			
			System.exit(-1);
		}
		boolean istag = download(args[0], args[1]);
		
		System.out.println(istag);
	}

	public static boolean download(String urlpath, String savepath){
		try{
			URL url = new URL(urlpath);
	        BufferedInputStream in = new BufferedInputStream(url.openStream());
	        int line1;
	        StringBuffer sb1 = new StringBuffer();
	        while((line1=in.read())!=-1){
	            sb1.append((char)line1);
	        }
	        String str1 = sb1.toString();
	        
	        FileWriter fw = new FileWriter(savepath);
	        fw.write(str1);
	        fw.close();
		}catch(Exception e){
			return false;
		}
        
        return true;
	}
	
}
