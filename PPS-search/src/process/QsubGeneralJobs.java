package process;

import java.io.File;
import java.io.FileWriter;

import _runner.LinuxCmdRunner;
import _util.StreamGobbler;

public class QsubGeneralJobs {
	private static String qsubfolder = "";
	private static String job_name = "";
	private static String workfolder = "";
	private static String generalCmd = "java ";
	
	public static void main(String[] args) throws Exception {
		if (4 != args.length){
			System.out.println("e.g., \n"
					+ "qsubfolder (folder path)\n"
					+ "job_name (file name)\n"
					+ "workfolder (cmd corresponding folder path, e.g., ../***/java)\n"
					+ "generalCmd (need qsub cmd, e.g., \"java _Amain/Test paramA paramB\")");
			System.exit(-1);
		}
		qsubfolder = args[0];
		if (!new File(qsubfolder).exists()){
			new File(qsubfolder).mkdirs();
		}
		
		job_name = args[1];
		workfolder = args[2];
		generalCmd = args[3];
			
		submit(qsubfolder, job_name, workfolder, generalCmd, "xiaogenz");
	}

	/****************************************************************
	 * @param qsubfolder
	 * @param job_name
	 * @param workfolder
	 * @param generalCmd
	 * @param queue: default or urgent
	 * @throws Exception
	 ***************************************************************/
	public static void submit(String qsubfolder, String job_name, String workfolder, String generalCmd, String queue, String username)throws Exception{
		int currentlyQsubedJobsNum = QstatJobs.getUserJobsNum(username);
		int curretlyTotalJobsNum = QstatJobs.getTotalJobsNum();
		while (currentlyQsubedJobsNum >= 288 && curretlyTotalJobsNum > 288){
			System.out.println("Now, the cluster has " + currentlyQsubedJobsNum+"/"+curretlyTotalJobsNum + " running/waiting jobs of "+username+"/All");
			
			currentlyQsubedJobsNum = QstatJobs.getUserJobsNum(username);
			curretlyTotalJobsNum = QstatJobs.getTotalJobsNum();
			Thread.sleep(1000);
		}
		
		FileWriter fw = new FileWriter(qsubfolder+"/"+job_name+".pbs");
		//fw.write("#!/bin/bash\n\n");
		fw.write("#PBS -N " + job_name + "\n");
		fw.write("#PBS -e " + qsubfolder+"/"+job_name + ".err\n");
		fw.write("#PBS -o " + qsubfolder+"/"+job_name + ".out\n");
		fw.write("#PBS -l nodes=1:ppn=1,walltime=720:00:00\n");
		fw.write("#PBS -q "+queue+"\n\n");
		
		fw.write("export PATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/bin:$PATH\n");
		fw.write("export JAVA_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64\n");
		fw.write("export JRE_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre\n");
		fw.write("export CLASSPATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/lib:/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre/lib:$CLASSPATH\n");
		fw.write("\ncd "+workfolder+"\n");
			
		String cmd = generalCmd;
		fw.write(cmd + "\n");
		fw.close();
				
		String qsubcmd = "qsub "+qsubfolder+"/"+job_name+".pbs";
		System.out.println(qsubcmd);
		Process process = Runtime.getRuntime().exec(qsubcmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();
		process.waitFor();
		process.destroy();
		
		Thread.sleep(200);
	}
	
	/****************************************************************
	 * @param qsubfolder
	 * @param job_name
	 * @param workfolder
	 * @param generalCmd
	 * @param queue: default or urgent
	 * @throws Exception
	 ***************************************************************/
	public static void submit(String qsubfolder, String job_name, String workfolder, String generalCmd, String queue, int maxjobnum, String username)throws Exception{
		int currentlyQsubedJobsNum = QstatJobs.getUserJobsNum(username);
		int curretlyTotalJobsNum = QstatJobs.getTotalJobsNum();
		while (currentlyQsubedJobsNum >= maxjobnum){
			System.out.println("Now, the cluster has " + currentlyQsubedJobsNum+"/"+curretlyTotalJobsNum + " running/waiting jobs of "+username+"/All");
			
			currentlyQsubedJobsNum = QstatJobs.getUserJobsNum(username);
			curretlyTotalJobsNum = QstatJobs.getTotalJobsNum();
			Thread.sleep(1000);
		}
		
		FileWriter fw = new FileWriter(qsubfolder+"/"+job_name+".pbs");
		//fw.write("#!/bin/bash\n\n");
		fw.write("#PBS -N " + job_name + "\n");
		fw.write("#PBS -e " + qsubfolder+"/"+job_name + ".err\n");
		fw.write("#PBS -o " + qsubfolder+"/"+job_name + ".out\n");
		fw.write("#PBS -l nodes=1:ppn=1,walltime=720:00:00\n");
		fw.write("#PBS -q "+queue+"\n\n");
		
		fw.write("export PATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/bin:$PATH\n");
		fw.write("export JAVA_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64\n");
		fw.write("export JRE_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre\n");
		fw.write("export CLASSPATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/lib:/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre/lib:$CLASSPATH\n");
		fw.write("\ncd "+workfolder+"\n");
			
		String cmd = generalCmd;
		fw.write(cmd + "\n");
		fw.close();
				
		String qsubcmd = "qsub "+qsubfolder+"/"+job_name+".pbs";
		System.out.println(qsubcmd);
		Process process = Runtime.getRuntime().exec(qsubcmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();
		process.waitFor();
		process.destroy();
		
		Thread.sleep(200);
	}
	
	public static void submit(String qsubfolder, String job_name, String workfolder, String generalCmd, String username)throws Exception{
		int currentlyQsubedJobsNum = QstatJobs.getUserJobsNum(username);
		while (currentlyQsubedJobsNum >= 288){
			System.out.println("Now, the cluster has " + currentlyQsubedJobsNum + " running/waiting jobs of "+username);
			
			currentlyQsubedJobsNum = QstatJobs.getUserJobsNum(username);
			Thread.sleep(100000);
		}
		
		FileWriter fw = new FileWriter(qsubfolder+"/"+job_name+".pbs");
		//fw.write("#!/bin/bash\n\n");
		fw.write("#PBS -N " + job_name + "\n");
		fw.write("#PBS -e " + qsubfolder+"/"+job_name + ".err\n");
		fw.write("#PBS -o " + qsubfolder+"/"+job_name + ".out\n");
		fw.write("#PBS -l nodes=1:ppn=1,walltime=720:00:00\n");
		fw.write("#PBS -q urgent\n\n");
		
		fw.write("export PATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/bin:$PATH\n");
		fw.write("export JAVA_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64\n");
		fw.write("export JRE_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre\n");
		fw.write("export CLASSPATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/lib:/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre/lib:$CLASSPATH\n");
		fw.write("\ncd "+workfolder+"\n");
			
		String cmd = generalCmd;
		fw.write(cmd + "\n");
		fw.close();
				
		String qsubcmd = "qsub "+qsubfolder+"/"+job_name+".pbs";
		System.out.println(qsubcmd);
		Process process = Runtime.getRuntime().exec(qsubcmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();
		process.waitFor();
		process.destroy();
		
		Thread.sleep(200);
	}
	
	public static void directlyRun(String shellfolder, String job_name, String workfolder, String generalCmd)throws Exception{
		FileWriter fw = new FileWriter(shellfolder+"/"+job_name+".sh");
		fw.write("#!/bin/bash\n\n");
		
		fw.write("export PATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/bin:$PATH\n");
		fw.write("export JAVA_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64\n");
		fw.write("export JRE_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre\n");
		fw.write("export CLASSPATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/lib:/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre/lib:$CLASSPATH\n");
		fw.write("\ncd "+workfolder+"\n");
			
		String cmd = generalCmd;
		fw.write(cmd + "\n");
		fw.close();
		
		LinuxCmdRunner.chmodRun(shellfolder);
		
		String shcmd = shellfolder+"/"+job_name+".sh";
		System.out.println(shcmd);
		Process process = Runtime.getRuntime().exec(shcmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();
		process.waitFor();
		process.destroy();
	}
}
