package _runner;

import java.io.File;
import java.util.Date;

import _util.ConfigUtil;
import _util.StreamGobbler;
import _util._Log;

/**
 * the original cd-hit runner is at https://github.com/weizhongli/cdhit
 * @author JunH
 *
 */
public class CDHitRunner {
	
	public static void main(String[] args)throws Exception{
//		String folder = "/nfs/amino-home/hujunum/SiteSea/fasta/";
//		File[] list = new File(folder).listFiles();
//		for (int i = 0; i < list.length; i++){
//			String path = list[i].getAbsolutePath();
//			if (path.endsWith("_Seq.fa")){
//				CDHitRunner.cut(path, 0.9, path+"_90");
//			}
//			
//		}
		
		if (3 != args.length){
			System.out.println("String orig_fa;");
			System.out.println("double idcut;");
			System.out.println("String save_fa;");
			System.exit(-1);
		}
		
		String orig_fa = args[0];
		Double idcut = Double.parseDouble(args[1]);
		String save_fa = args[2];
		CDHitRunner.cut(orig_fa, idcut, save_fa);
		
		System.out.println("HAVE A GOOD DAY");
	}

	public static void cut(String origFa, double idcut, String cuttedFa)throws Exception{
//		Choose of word size:
//		-n 5 for thresholds 0.7 ~ 1.0
//		-n 4 for thresholds 0.6 ~ 0.7
//		-n 3 for thresholds 0.5 ~ 0.6
//		-n 2 for thresholds 0.4 ~ 0.5
		
		int wordSize;
		if (0.4 <= idcut && idcut < 0.5)
			wordSize = 2;
		else if (0.5 <= idcut && idcut < 0.6)
			wordSize = 3;
		else if (0.6 <= idcut && idcut < 0.7)
			wordSize = 4;
		else if (0.7 <= idcut && idcut < 1.0)
			wordSize = 5;
		else {
			System.out.println("CD-HIT Do Not Accept idcut < 0.4, please use thr web : http://weizhongli-lab.org/cd-hit/");
			return;
		}
		
		String CDHIT_EXE_PATH = ConfigUtil.getConfig("CDHIT_EXE_PATH");
		if (null == CDHIT_EXE_PATH){
			System.out.println("The Config File does not contain CDHIT_EXE_PATH");
			_Log.dayRunLog("The Config File does not contain CDHIT_EXE_PATH", new Date());
			System.exit(-1);
		}
		
		try {
			String cmd = CDHIT_EXE_PATH + " -i " + origFa + " -o " + cuttedFa + " -c " + idcut + " -n " + wordSize;
			Process process = Runtime.getRuntime().exec(cmd);
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();
			
			process.waitFor();
			process.destroy();
		} catch (Exception e) {
			throw e;
		}
	}
}
