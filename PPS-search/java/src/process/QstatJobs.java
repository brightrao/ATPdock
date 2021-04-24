package process;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;

import _util.StreamGobbler;
import _util._Log;
import _util._Str;

public class QstatJobs {
	private static String username = "ALL";
	
	public static void main(String[] args) throws Exception {
		if (1 != args.length){
			System.out.println("e.g.:"
					+ "username (String) (e.g.: hujunum), \"ALL\" means statistic the user number");
			System.exit(-1);
		}
		
		username = args[0];
		
		
		StringBuffer info = runQstat();
		String[] infoes = info.toString().split("\n");
		
		if ("ALL".equalsIgnoreCase(username)){
			HashMap<String, Integer> usrNumHm = new HashMap<String, Integer>();
			for (int i = 2; i < infoes.length; i++){
				String[] lc = infoes[i].trim().split(" +");
				String key = lc[2];
				Integer tmp = usrNumHm.get(key);
				if (null == tmp)
					tmp = new Integer(0);
				tmp++;
				usrNumHm.put(key, tmp);
			}
			
			int total_job_num = 0;
			System.out.println("User Name\tHis (Her)Job Num");
			System.out.println("---------\t----------------");
			Object[] ids = usrNumHm.keySet().toArray();
			for (int i = 0; i < ids.length; i++){
				int job_num = usrNumHm.get(ids[i]);
				System.out.println(_Str.replenishEndWithChar(""+ids[i], "User Name".length(), ' ')+"\t"
						+ _Str.replenishEndWithChar(""+job_num, "His (Her)Job Num".length(), ' '));
				
				total_job_num += job_num;
			}
			System.out.println("Total User number is " + ids.length);
			System.out.println("Total Job number is " + total_job_num);
		}else{
			int total_jobs_num = 0;
			int total_runing_job_num = 0;
			int total_waiting_job_num = 0;
			
			HashMap<String, Integer> jobTypeNumHm = new HashMap<String, Integer>();
			HashMap<String, StringBuffer> jobTypeDetailHm = new HashMap<String, StringBuffer>();
			for (int i = 0; i < infoes.length; i++){
				if (infoes[i].contains(username)){
					String[] lc = infoes[i].trim().split(" +");
					
					String jobType = lc[3];
					if (lc[3].length() >= 6){
						jobType = lc[3].substring(0, 6);
					}
					
					Integer tmp = jobTypeNumHm.get(jobType);
					StringBuffer tmpDetail = jobTypeDetailHm.get(jobType);
					if (null == tmp){
						tmp = new Integer(0);
						tmpDetail = new StringBuffer();
					}
					tmp++;
					tmpDetail.append(lc[0].substring(0, lc[0].indexOf('.')==-1 ? lc[0].length() : lc[0].indexOf('.'))+"#");
					jobTypeNumHm.put(jobType, tmp);
					jobTypeDetailHm.put(jobType, tmpDetail);
					
					String state = lc[9];
					if (state.equalsIgnoreCase("R")){
						total_runing_job_num++;
					}else{
						total_waiting_job_num++;
					}
					
					total_jobs_num++;
				}
			}
			
			int job_group_num = jobTypeNumHm.size();
			System.out.println("Total Job Num\tRunning Job Num\tWaiting Job Num\tJob Group Num");
			System.out.println("-------------\t---------------\t---------------\t---------------");
			System.out.println(_Str.replenishEndWithChar(""+total_jobs_num, "Total Job Num".length(), ' ')+"\t"
					+_Str.replenishEndWithChar(""+total_runing_job_num, "Running Job Num".length(), ' ')+"\t"
					+_Str.replenishEndWithChar(""+total_waiting_job_num, "Waiting Job Num".length(), ' ')+"\t"
					+_Str.replenishEndWithChar(""+job_group_num, "Job Group Num".length(), ' '));
			System.out.println("\n");
			
			System.out.println("Job Group ID\tCorresponding Job Num\tGroup Job IDs");
			System.out.println("------------\t-----------------------");
			Object[] ids = jobTypeNumHm.keySet().toArray();
			for (int i = 0; i < ids.length; i++){
				System.out.println(_Str.replenishEndWithChar(""+ids[i], "Job Group ID".length(), ' ')+"\t"
						+ _Str.replenishEndWithChar(""+jobTypeNumHm.get(ids[i]), "Corresponding Job Num".length(), ' ')+"\t"
						+ jobTypeDetailHm.get(ids[i]).toString());
			}
		}
		
		System.out.println("\n\nHave A Good Day!");
	}

	/*******************************************************************
	 * @return the qsub jobs number currently of the fix job
	 *******************************************************************/
	public static int getUserJobsNum(String username)throws Exception{
		StringBuffer info = runQstat(username);
		String[] infoes = info.toString().split("\n");
		
		int ans = 0;
		for (int i = 0; i < infoes.length; i++){
			if (infoes[i].contains(username)){
				ans++;
			}
		}
		
		return ans;
	}
	
	/*******************************************************************
	 * @return the qsub jobs number currently of the fix job
	 *******************************************************************/
	public static int getTotalJobsNum()throws Exception{
		StringBuffer info = runQstat("ALL");
		String[] infoes = info.toString().split("\n");
		
		return infoes.length;
	}
	
	private static StringBuffer runQstat(String username)throws Exception{
		String qstatcmd = "qstat";
		if (!"ALL".equalsIgnoreCase(username))
			qstatcmd += " -u "+ username;
			
		StringBuffer saveresSB = new StringBuffer();
		try {
			Process process;
			process = Runtime.getRuntime().exec(qstatcmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			errorGobbler.start();
			
			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				saveresSB.append(line+"\n");
			}
			
			process.waitFor();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			_Log.dayRunLog(e.getMessage(), new Date());
		}
		
		return saveresSB;
	}
	
	private static StringBuffer runQstat()throws Exception{
		String qstatcmd = "qstat";
		if (!"ALL".equalsIgnoreCase(username))
			qstatcmd += " -u "+ username;
			
		StringBuffer saveresSB = new StringBuffer();
		try {
			Process process;
			process = Runtime.getRuntime().exec(qstatcmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			errorGobbler.start();
			
			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				saveresSB.append(line+"\n");
			}
			
			process.waitFor();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			_Log.dayRunLog(e.getMessage(), new Date());
		}
		
		return saveresSB;
	}
}
