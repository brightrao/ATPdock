package process;

import java.io.*;
import java.util.*;

import _BioStruct.Ligand;
import _BioStruct.Point3D;
import _util.*;

/**
 * This class is to find the top N similar pocket from the library
 * @author Administrator
 *
 */
public class PocSimSea {
	private String poc_name;
	private String q_seq;
	private String q_apoc;
	private double seqid_cut;
	private int isPPSalign_or_APoc;  // 1 : PPSalign; 2 : APoc, otherwise PPS-align+APoc
	private int topN;
	private int start_ind;
	private int end_ind;
	
	// TEMP
	private double[] scores = null;
	private String[] protnames = null;
	private String[] poc_names = null;
	private String[] pocbsiteses = null;
	private double[] seqid_scores = null;
	
	public static final int DB_RECORD_NUM = 36484; //3000; //36484;  // the number of lines in FixParams.LIBPOC_FATHER_FOLDER+"/db_poclig_info_cd99.list"
	
	public PocSimSea(String poc_name, String q_seq, String q_apoc, 
			double seqid_cut, int isPPSalign_or_APoc, int topN, 
			int start_ind, int end_ind) throws Exception {
		this.poc_name = poc_name;
		this.q_seq = q_seq;
		this.q_apoc = q_apoc;
		this.seqid_cut = seqid_cut;
		this.isPPSalign_or_APoc = isPPSalign_or_APoc;
		this.topN = topN;
		this.start_ind = start_ind;
		this.end_ind = end_ind;
		
		scores = new double[topN];
		protnames = new String[topN];
		poc_names = new String[topN];
		pocbsiteses = new String[topN];
		seqid_scores = new double[topN];
		for (int i = 0; i < topN; i++) {
			scores[i] = -Double.MAX_VALUE;
			seqid_scores[i] = -Double.MAX_VALUE;
		}
		
		searchBestTempl();
	}
	
	public int getValueOfTopN() {
		return topN;
	} 
	
	/**
	 * @param i : start from 0
	 * @return
	 */
	public String getIthLigMol2(int i) {
		if (i < 0 || i > topN) {
			return null;
		}
		
		String lig_mol2 = FixParams.LIBPOC_FATHER_FOLDER+"/lig/" + poc_names[i]+".mol2";
		return lig_mol2;
	}
	
	/**
	 * @param i : start from 0
	 * @return
	 */
	public String getIthPocname(int i) {
		if (i < 0 || i > topN) {
			return null;
		}
		
		return poc_names[i];
	}
	
	/**
	 * @param i : start from 0
	 * @return
	 */
	public String getIthProtname(int i) {
		if (i < 0 || i > topN) {
			return null;
		}
		
		return protnames[i];
	}
	
	/**
	 * @param i : start from 0
	 * @return "Y15 G17 K18 A19 K20 I22 H75 K88 V90 I92 K129 E185 D198"
	 */
	public String getIthPocBSITEs(int i) {
		if (i < 0 || i > topN) {
			return null;
		}
		
		return pocbsiteses[i];
	}
	
	/**
	 * @param i : start from 0
	 * @return
	 */
	public Double getIthPocScore(int i) {
		if (i < 0 || i > topN) {
			return null;
		}
		
		return scores[i];
	}
	
	/**
	 * @param i : start from 0
	 * @return
	 */
	public Double getIthPocSeqIDScore(int i) {
		if (i < 0 || i > topN) {
			return null;
		}
		
		return seqid_scores[i];
	}
	
	private void searchBestTempl() throws Exception {
		
		if (end_ind <= start_ind) {
			end_ind = Integer.MAX_VALUE;
		}
		
		BufferedReader br = new BufferedReader(new FileReader(FixParams.LIBPOC_FATHER_FOLDER+"/db_poclig_info_cd99.list"));
		String l = br.readLine();
		int count = 0;
		while (null != l) {
			if (count < start_ind || count >= end_ind) {
				l = br.readLine();
				count++;
				continue;
			}
			
			String[] lc = l.split("\t+");
			
			String protname = lc[1];
			String pocname = lc[1]+"_"+lc[2]+"_"+lc[3];
			String pocbsites = lc[4];
			String seq = lc[5];
			
			NWAlign nwalign = new NWAlign(poc_name, q_seq, protname, seq);
			double seqid = nwalign.getSeqIdenticalNorm8MinOrigSeqLen();
			if (seqid > seqid_cut){
				l = br.readLine();
				count++;
				continue;
			}
									
			String apoc = FixParams.LIBPOC_FATHER_FOLDER + "/poc/" + pocname + ".apoc.pdb";
			String mol2 = FixParams.LIBPOC_FATHER_FOLDER + "/lig/" + pocname + ".mol2";
			if (!new File(mol2).isFile()) {
				System.out.println(mol2 + "\t is not existed!");
				l = br.readLine();
				count++;
				continue;
			}
			
//			System.out.println("The " + count + "-th template pocket (" + pocname + ") is processing...");
			try {
				double[] sco_time = null;
				if (1 == isPPSalign_or_APoc) {
					sco_time = runPPSalign(apoc, q_apoc);
				}else if (2 == isPPSalign_or_APoc){
					sco_time = runAPoc(apoc, q_apoc);
				}else {
					double[] pps_align_sco_time = runPPSalign(apoc, q_apoc);
					double[] apoc_sco_time = runAPoc(apoc, q_apoc);
					sco_time = new double[2];
					sco_time[0] = 0.5*(pps_align_sco_time[0]+apoc_sco_time[0]) / (Math.exp(Math.abs(pps_align_sco_time[0] - apoc_sco_time[0])));
					sco_time[1] = pps_align_sco_time[1] + apoc_sco_time[1];
				}
				
				orderlyInsert(protname, pocname, pocbsites, sco_time[0], seqid);
			}catch(Exception e) {
//				e.printStackTrace();
				l = br.readLine();
				Thread.sleep((long)(50 * Math.random()));
				count++;
				continue;
			}
			count++;
			l = br.readLine();
		}
		
		br.close();
	}
	
	/**
	 * @param aPocPath : path
	 * @param bPocPath : path
	 * @return ans[0] = PPS-score; ans[1] = runing time (s).
	 * @throws Exception
	 */
	public static double[] runPPSalign(String aPocPath, String bPocPath)throws Exception{
		long start_time = new Date().getTime();
		
		String lsalign_cmd = FixParams.PPSalign_exe + " " + aPocPath + " " + bPocPath;
		Process process;
		process = Runtime.getRuntime().exec(lsalign_cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		errorGobbler.start();
		
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		BufferedReader outputBr = new BufferedReader(isr);
		StringBuffer tmpSb = new StringBuffer();
		String line = null;
		while ((line = outputBr.readLine()) != null) {
			tmpSb.append(line+"\n");
		}
		outputBr.close();
		process.waitFor();
		process.destroy();
		
		long end_time = new Date().getTime();
		
		String[] outLines = tmpSb.toString().split("\n");
		if (outLines.length < 3){
			throw new Exception("PPS-align cannot work normally for \n" + aPocPath + "\n" + bPocPath);
		}
		
		String[] lc = outLines[2].trim().split(" +");
		double PPSsco8Q = Double.parseDouble(lc[2]);
		double PPSsco8T = Double.parseDouble(lc[3]);
		double pps_sco = 0.5*(PPSsco8Q+PPSsco8T) / (Math.exp(Math.abs(PPSsco8Q - PPSsco8T)));
		
		int ind = 3;
		for (; ind < outLines.length; ind++) {
			if (outLines[ind].contains("i              t[i]         u[i][0]         u[i][1]         u[i][2]")) {
				break;
			}
		}
		ind++;
		
		double[] ans = new double[2];
		ans[0] = pps_sco;
		ans[1] = (double)(end_time - start_time) / 1000.0;
		
		return ans;
	}

	/**
	 * @param aPocPath : path
	 * @param bPocPath : path
	 * @return ans[0] = PS-score; ans[1] = runing time (s).
	 * @throws Exception
	 */
	public static double[] runAPoc(String templ_PocPath, String query_PocPath)throws Exception{
		long start_time = new Date().getTime();
		
		String lsalign_cmd = FixParams.APoc_exe + " " + templ_PocPath + " " + query_PocPath + " -fa 0";
		Process process;
		process = Runtime.getRuntime().exec(lsalign_cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		errorGobbler.start();
		
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		BufferedReader outputBr = new BufferedReader(isr);
		StringBuffer tmpSb = new StringBuffer();
		String line = null;
		while ((line = outputBr.readLine()) != null) {
			tmpSb.append(line+"\n");
		}
		outputBr.close();
		process.waitFor();
		process.destroy();
		
		long end_time = new Date().getTime();
		
		String outputStr = tmpSb.toString();
		if (!outputStr.contains("Index Ch1 Resid1  AA1 Ch2 Resid2  AA2 Distance Cos(theta)")){
			throw new Exception("APoc cannot work normally for \n" + templ_PocPath + "\n" + query_PocPath);
		}
		
		double ps_score = 0.0;
		
		String[] outLines = outputStr.split("\n");
		int ind = 0;
		for (ind = 0; ind < outLines.length; ind++){
			if (outLines[ind].startsWith("PS-score =")) {
				ps_score = Double.parseDouble(outLines[ind].substring("PS-score = ".length(), outLines[ind].indexOf(',')).trim());
			}
			
			if (outLines[ind].contains("i          t(i)         u(i,1)         u(i,2)         u(i,3)")){
				break;
			}
		}
		ind++;
		
		double[] ans = new double[2];
		ans[0] = ps_score;
		ans[1] = (double)(end_time - start_time) / 1000.0;
		
		return ans;
	}
	
	private boolean orderlyInsert(final String protname, final String poc_name, final String pocbsites, 
			final double score, final double seqid_score){
		int i;
		for (i = scores.length-1; i >= 0; i--){
			if (score > scores[i]){
				if (0 == i) continue;
				protnames[i] = protnames[i-1];
				poc_names[i] = poc_names[i-1];
				pocbsiteses[i] = pocbsiteses[i-1];
				scores[i] = scores[i-1];
				seqid_scores[i] = seqid_scores[i-1];
			}else break;
		}
		
		if (i == scores.length-1)
			return false;
		
		scores[i+1] = score;
		protnames[i+1] = protname;
		poc_names[i+1] = poc_name;
		pocbsiteses[i+1] = pocbsites;
		seqid_scores[i+1] = seqid_score;
		
		return true;
	}
}
