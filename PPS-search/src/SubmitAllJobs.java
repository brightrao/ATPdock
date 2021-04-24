import java.io.File;
import java.io.FileWriter;

import _util.StreamGobbler;
import _util._File;
import process.QstatJobs;

public class SubmitAllJobs {
	private static String workfolder;
	
	private static final String sev_name = "PPS-search";
	
	public static void main(String[] args) throws Exception {
		if (1 != args.length){
			System.out.println("workfolder (String)");
			System.exit(-1);
		}
		
		workfolder = args[0];
		
//		String useremail = null;
//		if (new File(workfolder+"/email.txt").isFile()){
//			useremail = _File.load2Str(workfolder+"/email.txt");
//		}
//		
//		String useripaddr = null;
//		if (new File(workfolder+"/ip.txt").isFile()){
//			useripaddr = _File.load2Str(workfolder+"/ip.txt");
//		}
		
		String protpdb = null;
		if (new File(workfolder+"/query.pdb").isFile()){
			protpdb = workfolder+"/query.pdb";
		}
		
		String querysites = null;
		if (new File(workfolder+"/query.sites").isFile()){
			querysites = _File.load2Str(workfolder+"/query.sites");
		}
		
		String searchengine = null;	
		if (new File(workfolder+"/search.engine").isFile()){
			searchengine = _File.load2Str(workfolder+"/search.engine");
			if (null == searchengine || searchengine.length() == 0) {
				searchengine = "PPS-align";
			}
		}
		
		String seqidencut = null;	
		if (new File(workfolder+"/seqiden.cut").isFile()){
			seqidencut = _File.load2Str(workfolder+"/seqiden.cut");
			if (null == seqidencut || seqidencut.length() == 0) {
//				seqidencut = "1.0";
				seqidencut = "0.9";
			}
		}
		
		String qsubfolder = "/nfs/amino-home/zhanglabs/"+sev_name+"/Qsubfolder";
		if (!new File(qsubfolder).exists()){
			new File(qsubfolder).mkdirs();
		}
		
		System.out.println(workfolder);
		int start_ind_AB = workfolder.indexOf("PSSH");
		int last_xiegang_ind = workfolder.lastIndexOf('/');
		last_xiegang_ind = last_xiegang_ind < start_ind_AB ? workfolder.length() : last_xiegang_ind;
		String job_name = workfolder.substring(start_ind_AB, last_xiegang_ind);
		String javafolder = "/nfs/amino-home/zhanglabs/"+sev_name+"/java";
		
		int jobnum = 25;
		int currentlyQsubedJobsNum = QstatJobs.getUserJobsNum("zhanglabs");
		if (currentlyQsubedJobsNum > 500) {
			jobnum = 20;
		}else if (currentlyQsubedJobsNum > 1000){
			jobnum = 10;
		}
		
		if (searchengine.contains("APoc")) {
			jobnum-=5;
			jobnum = jobnum > 0 ? jobnum : 1; 
		}
		
		for (int i = 0; i < jobnum; i++) {
			int jobind = i;
			String i_job_name = job_name + "_" + jobind;
			String generalCmd = "java -classpath .:/nfs/amino-home/zhanglabs/"+sev_name+"/java/lib/mail.jar process.PPSsearch " 
					+ workfolder + " "
					+ protpdb + " "
					+ "\""+querysites + "\" "
					+ searchengine + " "
					+ seqidencut + " "
					+ jobnum + " "
					+ jobind + " "
					+ job_name;
			
			SubmitAllJobs.submit(qsubfolder, i_job_name, javafolder, generalCmd);
		}
	}

	public static void submit(String qsubfolder, String job_name, String javafolder, String generalCmd)throws Exception{
		FileWriter fw = new FileWriter(qsubfolder+"/"+job_name+".pbs");
		fw.write("#PBS -N " + job_name + "\n");
		fw.write("#PBS -e " + qsubfolder+"/"+job_name + ".err\n");
		fw.write("#PBS -o " + qsubfolder+"/"+job_name + ".out\n");
		fw.write("#PBS -l nodes=1:ppn=1,walltime=120:00:00\n");
		fw.write("#PBS -l mem=20gb,pmem=20gb\n");
		fw.write("#PBS -q urgent\n\n");
		
		fw.write("export PATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/bin:$PATH\n");
		fw.write("export JAVA_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64\n");
		fw.write("export JRE_HOME=/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre\n");
		fw.write("export CLASSPATH=/usr/lib/jvm/java-1.8.0-oracle.x86_64/lib:/usr/lib/jvm/java-1.8.0-oracle.x86_64/jre/lib:$CLASSPATH\n");
		fw.write("\ncd "+javafolder+"\n");
			
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
		
		Thread.sleep(500);
	}
}
