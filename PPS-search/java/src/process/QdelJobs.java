package process;

import _util.StreamGobbler;

public class QdelJobs {
	private static String[] jobIDs;
	
	public static void main(String[] args) throws Exception {
		if (1 != args.length){
			System.out.println("e.g.:"
					+ "jobIDs (String) (e.g.: 11196671#11196699#...#11196768)");
			System.exit(-1);
		}
		
		jobIDs = args[0].split("#");
		
		for (int i = 0; i < jobIDs.length; i++){
			String qdelcmd = "qdel -a "+jobIDs[i];
			System.out.println(qdelcmd);
			
			Process process = Runtime.getRuntime().exec(qdelcmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();
			process.waitFor();
			process.destroy();
			
			Thread.sleep(200);
		}
	}

}
