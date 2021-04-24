package _runner;

import _util.StreamGobbler;

public class LinuxCmdRunner {

	public static void main(String[] args) {

	}
	
	public static void chmodRun(String folderPath){
		String permition = "777";
		
		String cmd = "chmod -R "+permition+" "+folderPath;
		
		try{
			Process process = Runtime.getRuntime().exec(cmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			process.waitFor();
			process.destroy();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void chmodFileRun(String filepath){
		String permition = "777";
		
		String cmd = "chmod "+permition+" "+filepath;
		
		try{
			Process process = Runtime.getRuntime().exec(cmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			process.waitFor();
			process.destroy();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void chmodRun(String folderPath, String permition){
		String cmd = "chmod -R "+permition+" "+folderPath;
		
		try{
			Process process = Runtime.getRuntime().exec(cmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			process.waitFor();
			process.destroy();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
