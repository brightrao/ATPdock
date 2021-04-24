import java.io.File;
import java.io.FileWriter;

import _util.StreamGobbler;
import _util._File;
import process.PPSsearch;
import process.QstatJobs;

public class submit {
	private static String fatherfolder = "/iobio/rl/ATPdock/example";
	private static double seqidencut = 0.3;
	private static String caretype = "~ATP~~ADP~~AMP~~GDP~~GTP~";
	private static String searchengine = "APoc";
	
	public static void main(String[] args) throws Exception {
		if (4 != args.length){
			System.out.println("fatherfolder (String)");
			System.out.println("seqidencut (double), suggested 0.3");
			System.out.println("caretype (String)");
			System.out.println("searchengine (String)");
			System.exit(-1);
		}
		
		fatherfolder = args[0];
		seqidencut = Double.parseDouble(args[1]);
		caretype = args[2];
		searchengine = args[3];
				
		if (!new File(fatherfolder).exists()) {
			System.out.println("There is no " + fatherfolder);
			System.exit(-1);
		}
								
		String protpdb = fatherfolder + "/pdb.pdb";
		String querysites = _File.load2Str(fatherfolder+"/pdb.site");

		
		int last_xiegang_ind = fatherfolder.lastIndexOf('/');
		String job_name = fatherfolder.substring(last_xiegang_ind+1);		
		System.out.println(job_name + " is processing...");
		
		String[] __args = new String[9];
		__args[0] = fatherfolder;
		__args[1] = protpdb;
		__args[2] = querysites;
		__args[3] = searchengine;
		__args[4] = caretype;
//		__args[4] = "~ATP~~ADP~~AMP~~GDP~~GTP~";
//		__args[4] = "NULL";
		__args[5] = ""+seqidencut;
		__args[6] = "1";
		__args[7] = "0";
		__args[8] = job_name;
		
		PPSsearch.main(__args);
		
	}

}
