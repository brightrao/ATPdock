package _util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	InputStream is;
	String type;
	String recordFile;
	
	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
		this.recordFile = null;
	}
	
	public StreamGobbler(InputStream is, String type, String recordFile) {
		this.is = is;
		this.type = type;
		this.recordFile = recordFile;
	}

	public void run() {
		try {
			if (null == recordFile){
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (type.equals("Error")){
//						System.out.print(line+"\n");
					}else {
//						System.out.print(line+"\n");
					}
				}
			}else{
				// If you want to immediately read this "recordFile" file, there is a potential synchronized protein.
				// you can copy the following code to the original path.
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (type.equals("Error")){
//						System.out.print(line+"\n");
					}else {
//						System.out.print(line+"\n");
						FileWriter fw = new FileWriter(recordFile, true);
						fw.write(line+"\n");
						fw.close();
					}
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
}