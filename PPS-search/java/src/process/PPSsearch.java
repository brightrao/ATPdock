package process;

import java.io.*;
import java.util.*;

import _BioStruct.Protein;
import _BioStruct.Residue;
import _util.StreamGobbler;
import _util._File;
import _util._Log;
import _util._NumFormat;
import _util._Str;
import _util._Vector;
import process.GenerateDataset.ConstructPocLigLib;

public class PPSsearch {

	private static String workfolder;
	private static String querypdb;
	private static String[] querysites;
	private static String searchengine;
	private static String careLigTypes;   // "~ATP~~ADP~"
	private static String seqidencut;
	private static int jobnum;	// total number of sub-jobs
	private static int jobind;   // the index of this sub-job
	private static String job_name;
	
	// TEMPORY
	private static String protseq = null;
	private static double seqid_cut = 1.0;
	
	// STATISTICAL 
	private static final int REPORT_RECORD_NUM = 10; 
	
	public static void main(String[] args) throws Exception{
		if (9 != args.length){
			System.out.println("workfolder   (String)(path)");
			System.out.println("querypdb     (String)(path)");
			System.out.println("querysites   (String)(e.g., \"D19 H20 G21 A161 L162\")");
			System.out.println("searchengine (String)(e.g., PPS-align, APoc, or PPS-align+APoc)");
			System.out.println("careLigTypes (String)(e.g., ~ATP~~ADP~ or NULL)");
			System.out.println("seqidencut   (String)(e.g., 0.3, 0.6, 0.9, or 1.0)");
			System.out.println("jobnum       (Integer)(>=1)");
			System.out.println("jobind       (Integer)(0<=jobid<jobnum)");
			System.out.println("job_name     (String)(PSSH152548999_99999)");
			System.exit(-1);
		}
		workfolder = args[0];
		querypdb = args[1];
		querysites = args[2].trim().split(" +|;+|,+|-+|=+|~+");
		searchengine = args[3].trim();
		careLigTypes = args[4].trim();
		seqidencut = args[5];
		jobnum = Integer.parseInt(args[6]);
		jobind = Integer.parseInt(args[7]);
		job_name = args[8];
		
		jobnum = jobnum>0 ? jobnum : 1;
		
		System.out.println("aaa");
		
		if (!new File(workfolder+"/status.txt").isFile()) {
			_File.writeToFile("job_running", workfolder+"/status.txt", false);
		}
		
		// See the current job statue
		try {
			String current_statue = getJobstatues();
			if (current_statue.contains("job_error") || current_statue.contains("job_finished")) {
				return;
			}
		}catch(Exception e) {
			saveJobstatus("job_error");
			return;
		}
		
		System.out.println("bbb");
		
		// Construct the APoc format pocket structure
		String errmsg = generatePocStructAPoc();
		if (null != errmsg) {
			saveErrorHtml(workfolder+"/index.html", errmsg);
			saveJobstatus("job_error");
			return;
		}
		
		System.out.println("ccc");
		
		// Running the {jobid}-th job, generate a path of "workfolder+"/search_list"+i+".txt"" file
		try {
			run_jobid_thSubJob();
		}catch(Exception e) {
			saveErrorHtml(workfolder+"/index.html", "Error occuring in the stage of \"Searching database\", please report this error to Jun Hu (junh_cs@126.com)");
			saveJobstatus("job_error");
			return;
		}
		
		System.out.println("ddd");
		
		boolean isAllJobFinished = true;
		for (int i = 0; i < jobnum; i++) {
			if (!new File(workfolder+"/search_list"+i+".txt").isFile()) {
				isAllJobFinished = false;
				break;
			}else {
				String content = _File.load2Str(workfolder+"/search_list"+i+".txt");
				if (0 == content.length()) {
					isAllJobFinished = false;
					break;
				}
			}
		}
		
		System.out.println("eeee");
		
		if (isAllJobFinished) {
			combineDataAndSaveHtml();
			
			// Remove useless files
			removeUselessFiles();
			saveJobstatus("job_finished");
		}
	}
	
	private static void combineDataAndSaveHtml()throws Exception {
		// Union workfolder+"/search_list"+i+".txt"
		double[] scores = new double[REPORT_RECORD_NUM];
		String[] infos = new String[REPORT_RECORD_NUM];
		
		for (int jd = 0; jd < jobnum; jd++) {
			BufferedReader br = new BufferedReader(new FileReader(workfolder+"/search_list"+jd+".txt"));
			String l = br.readLine();
			while (null != l) {
				String[] lc = l.split("~ZJUT~");
				double score = Double.parseDouble(lc[1]);
				String info = l;
				
				int i;
				for (i = scores.length-1; i >= 0; i--){
					if (score > scores[i]){
						if (0 == i) continue;
						infos[i] = infos[i-1];
						scores[i] = scores[i-1];
					}else break;
				}
				
				if (i == scores.length-1) {
				}else {
					scores[i+1] = score;
					infos[i+1] = info;
				}
				
				
				l = br.readLine();
			}
			br.close();
		}
		
		try {
			FileWriter fw = new FileWriter(workfolder+"/search_list.txt");
			for (int i = 0; i < infos.length; i++) {
				if (null == infos[i] || infos[i].length() <= 0) {
					continue;
				}
				fw.write(infos[i] + "\n");
			}
			fw.close();
		}catch(Exception e) {
			saveErrorHtml(workfolder+"/index.html", "Error occuring in the stage of \"Union search_list.txt\", please report this error to Jun Hu (junh_cs@126.com)");
			saveJobstatus("job_error");
			return;
		}
		
		try {
			// Copy database protein and ligand pdb files
			for (int i = 0; i < infos.length; i++) {
				if (null == infos[i] || infos[i].length() <= 0) {
					continue;
				}
				String[] lc = infos[i].split("~ZJUT~");
				String pocname = lc[0];
				String[] lcc = lc[0].split("_");
				String protname = lcc[0];
				
				String db_lig_pdb = FixParams.LIBPOC_FATHER_FOLDER+"/lig/" + pocname+".pdb";
				_File.copy(db_lig_pdb, workfolder + "/" + pocname + ".pdb");
				
				if (!new File(workfolder + "/" + protname + ".pdb").isFile()) {
					Protein prot = new Protein(protname, 
							FixParams.LIBPOC_FATHER_FOLDER + "/poc/" + pocname + ".apoc.pdb", 
							true, 
							false);
					prot.save(workfolder + "/" + protname + ".pdb", protname.charAt(protname.length()-1), true);
				}
			}
		}catch(Exception e) {
			saveErrorHtml(workfolder+"/index.html", "Error occuring in the stage of \"Copy structures of similar database records\", please report this error to Jun Hu (junh_cs@126.com)");
			saveJobstatus("job_error");
			return;
		}
		
		// Save success HTML
		saveSuccessHtml(workfolder+"/index.html");
	}
	
	private static void run_jobid_thSubJob() throws Exception {
		int step = PocSimSea.DB_RECORD_NUM / jobnum + 1;
		int start_ind = jobind*step;
		int end_ind = (jobind+1)*step>PocSimSea.DB_RECORD_NUM ? PocSimSea.DB_RECORD_NUM : (jobind+1)*step;
		
		System.out.println(step + " " + start_ind + " " + end_ind);
		
		String apocpdb = workfolder+"/pocket.apoc.pdb";
		seqid_cut = 1.0;
		try {
			seqid_cut = Double.parseDouble(seqidencut);
			if (seqid_cut < 0.3) {
				seqid_cut = 0.3;
			}
			if (seqid_cut > 1.0) {
				seqid_cut = 1.0;
			}
		}catch(Exception e) {}
		
		int isPPSalign_or_APoc = 1;
		if (searchengine.contains("PPS-align") && !searchengine.contains("APoc")) {
			isPPSalign_or_APoc = 1;
		}
		if (!searchengine.contains("PPS-align") && searchengine.contains("APoc")) {
			isPPSalign_or_APoc = 2;
		}
		if (searchengine.contains("PPS-align") && searchengine.contains("APoc")) {
			isPPSalign_or_APoc = 3;
		}
		
		System.out.println("isPPSalign_or_APoc = " + isPPSalign_or_APoc);
		System.out.println(protseq);
		
		PocSimSea pocsea = new PocSimSea("PDB52", protseq, apocpdb, seqid_cut, isPPSalign_or_APoc, REPORT_RECORD_NUM, start_ind, end_ind, careLigTypes);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < REPORT_RECORD_NUM; i++) {
			String searched_poc_name = pocsea.getIthPocname(i);
			if (searched_poc_name == null) {
				break;
			}
			String searched_bsites = pocsea.getIthPocBSITEs(i);
			Double searched_poc_sim_sco = pocsea.getIthPocScore(i);
			Double searched_poc_seqid_sco = pocsea.getIthPocSeqIDScore(i);
			sb.append(searched_poc_name + "~ZJUT~" 
					+ _NumFormat.formatDouble(searched_poc_sim_sco, "#0.000") + "~ZJUT~"
					+ _NumFormat.formatDouble(searched_poc_seqid_sco, "#0.000") + "~ZJUT~" 
					+ searched_bsites + "\n");
		}
		
		_File.writeToFile(sb.toString().trim(), workfolder+"/search_list"+jobind+".txt", false);
	}
	
	/**
	 * @return error message, null means no error
	 */
	private static String generatePocStructAPoc() {
		Protein prot = new Protein("PDB52", querypdb, true, false);
		protseq = prot.getSeq();
		
		Vector<Character> pocaa = new Vector<Character>();
		Vector<Residue> pocress = new Vector<Residue>();
		for (int i = 0; i < querysites.length; i++) {
			String isite = querysites[i];
			char isite_aa = isite.charAt(0);
			int isite_IndexInOrigPDB = Integer.parseInt(isite.substring(1));  // the index in the original PDB
			
			
			boolean isMatched = false;
			for (int j = 0; j < prot.size(); j++) {
				int IndexInOrigPDB = prot.get(j).getIndexInOrigPDB();
				char aa = prot.getAA(j);
				
				if (IndexInOrigPDB == isite_IndexInOrigPDB && !(""+isite_aa).equalsIgnoreCase(""+aa)) {
					return "The amino acide type "+isite_aa+" of site \""+isite+"\" is not matched "
							+ "in the "+IndexInOrigPDB+"-th residue " +aa+ " of input protein structure";
				}
				if (IndexInOrigPDB == isite_IndexInOrigPDB && (""+isite_aa).equalsIgnoreCase(""+aa)) {
					pocress.add(prot.get(j));
					pocaa.add(prot.getAA(j));
					
					isMatched = true;
					break;
				}
			}
			
			if (!isMatched) {
				return "The site \""+isite+"\" does not exist in the input protein structure";
			}
		}
		
		ConstructPocLigLib.savePocketbyAPocNeededFormat("TEMPL", prot, pocaa,
				pocress, workfolder+"/pocket.apoc.pdb");
		
		return null;
	}
	
	private static void removeUselessFiles(){
		for (int i = 0; i < jobnum; i++) {
			if (new File(workfolder+"/search_list"+i+".txt").isFile()) {
				new File(workfolder+"/search_list"+i+".txt").delete();
			}
		}
		
		if (new File(workfolder+"/pocket.apoc.pdb").isFile()) {
			new File(workfolder+"/pocket.apoc.pdb").delete();
		}
	}
	
	/**
	 * @param htmlPath
	 * @param errmsg
	 */
	private static void saveErrorHtml(String htmlPath, String errmsg) {
		try {
			String oContent = _File.load2Str(htmlPath);
			String[] oArray = oContent.split("<meta http-equiv=\"refresh\" content=\"5\">");
			String zhanglabHtmlHead = oArray[0];
			String zhanglabHtmlTail = oArray[1];
			{// prepare the zhanglabHtmlTail
				String[] tailArray = zhanglabHtmlTail.split("<!----- end of text -------->");
				zhanglabHtmlTail = "<!----- end of text -------->\n" + tailArray[1];
			}

			StringBuffer zhanglabHtmlContent = new StringBuffer("\n");
			zhanglabHtmlContent.append("<font face=\"Times New Roman\" class=\"STYLE10\">\r\n" + 
					"[<a href=\"http://zhanglab.ccmb.med.umich.edu/PPS-search\">Back to PPS-search server</a>]\r\n" + 
					"</font><br/>\r\n" + 
					"<h1 align=\"center\" class=\"STYLE5\">ERROR of PSSH1541555881_642144</h1>\r\n" + 
					"<font face=\"Times New Roman\" class=\"STYLE10\">\r\n" +
					errmsg +
					"<br><br>\r\n" + 
					"\r\n" + 
					"Please check your input information and resubmit your correct job!<br/></font>");
			
			StringBuffer contentBuf = new StringBuffer();
			contentBuf.append(zhanglabHtmlHead);
			contentBuf.append(zhanglabHtmlContent.toString());
			contentBuf.append(zhanglabHtmlTail);
			
			String wholeStr = contentBuf.toString();
			wholeStr = wholeStr.substring(0, wholeStr.length());
			_File.writeToFile(wholeStr, htmlPath, false);
		}catch(Exception e) {
			_Log.dayRunLog(e.getMessage(), new Date());
		}
	}
	
	/**
	 * @param job_status : you can choose:
	 * 	job_finished job_error 
	 */
	private static void saveJobstatus(String job_status) {
		_File.writeToFile(job_status, workfolder+"/status.txt", false);
	}
	
	private static String getJobstatues() throws Exception {
		return _File.load2Str(workfolder+"/status.txt").trim();
	}
	
	/**
	 * @param htmlPath
	 * @param errmsg
	 * @throws Exception 
	 */
	private static void saveSuccessHtml(String htmlPath) throws Exception {
		Protein queryprot = new Protein("QUERY", querypdb, true, false);
		int[] orig_indexes_inOriginalPDB = new int[queryprot.size()];
		for (int i = 0; i < orig_indexes_inOriginalPDB.length; i++) {
			orig_indexes_inOriginalPDB[i] = queryprot.get(i).getIndexInOrigPDB();
		}
		
		int[] query_bsites_inOriginalPDB = new int[querysites.length];
		for (int i = 0; i < querysites.length; i++) {
			String isite = querysites[i];
			int isite_IndexInOrigPDB = Integer.parseInt(isite.substring(1));  // the index in the original PDB
			query_bsites_inOriginalPDB[i] = isite_IndexInOrigPDB;
		}
		
		String first_pocname = null;
		{
			// Generate superpose structure
			BufferedReader br = new BufferedReader(new FileReader(workfolder+"/search_list.txt"));
			String l = br.readLine();
			while (null != l) {
				try {
					String[] lc = l.split("~ZJUT~");
					String pocname = lc[0];
					
					if (null == first_pocname) {
						first_pocname = pocname;
					}
					
					String q_apocpdb = workfolder+"/pocket.apoc.pdb";
					String db_apocpdb = FixParams.LIBPOC_FATHER_FOLDER + "/poc/" + pocname + ".apoc.pdb";
					
					String save_sup = workfolder+"/pocket_"+pocname+".sup.pdb";
					
					String ali_info = null;
					if (searchengine.equals("PPS-align")) {
						ali_info = runPPSalign4Superpose(q_apocpdb, db_apocpdb, save_sup);
					}else if (searchengine.equals("APoc")) {
						ali_info = runAPoc4Superpose(q_apocpdb, db_apocpdb, save_sup);
					}else {
						ali_info = runPPSalign4Superpose(q_apocpdb, db_apocpdb, save_sup);
					}
					
					_File.writeToFile(ali_info, workfolder+"/pocket_"+pocname+".ali.txt", false);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				l = br.readLine();
			}
			br.close();
		}

		
//		try {
//			String oContent = _File.load2Str(htmlPath);
//			String[] oArray = oContent.split("<meta http-equiv=\"refresh\" content=\"5\">");
//			String zhanglabHtmlHead = oArray[0];
//			String zhanglabHtmlTail = oArray[1];
//			{// prepare the zhanglabHtmlTail
//				String[] tailArray = zhanglabHtmlTail.split("<!----- end of text -------->");
//				zhanglabHtmlTail = "<!----- end of text -------->\n" + tailArray[1];
//			}
//
//			StringBuffer zhanglabHtmlContent = new StringBuffer("\n");
//			
//			zhanglabHtmlContent.append("" + "<center>\n" + "<table width=\"90%\" bgcolor=\"#F6F6F6\">\n"
//					+ "<tr><td align=\"middle\"><span class=\"STYLE2\">PPS-search RESULT PAGE</span><br/><br/></td></tr>\n"
//					+ "<tr><td align=\"left\" class=\"STYLE10\">\n"
//					+ "\t<span class=\"STYLE5\"><b>Job ID:</b></span>\n" + "\t<br />" + job_name + "<br /><br />\n"
//					+ "\t<span class=\"STYLE5\"><b>Protein Sequence:</b></span>\n" + "\t<br />\n");
//			
//			{ // protein sequence information
//				StringBuffer sequenceContentBuf = new StringBuffer();
//				sequenceContentBuf.append("\t<font style=\"font-family:'Courier New', Courier, monospace\">\n");
////				sequenceContentBuf.append("\t<font face=\"Times New Roman\" class=\"STYLE10\">\n");
//				
//				Vector<String> seq_lines = new Vector<String>();
//				Vector<Integer> aanum_lines = new Vector<Integer>(); // count the amino acid number in each line of seq_lines
//				StringBuffer seq_line_sb = new StringBuffer();
//				int aanum = 0;
//				for (int i = 0; i < protseq.length(); i++) {
//					if (_Vector.isContain(query_bsites_inOriginalPDB, orig_indexes_inOriginalPDB[i])) {
//						seq_line_sb.append("<span style=\"color:red;\">" + protseq.charAt(i) + "</span>");
//					} else {
//						seq_line_sb.append(protseq.charAt(i));
//					}
//					aanum++;
//					
//					if ((i + 1) % 80 == 0) {
//						seq_lines.add(seq_line_sb.toString());
//						seq_line_sb = new StringBuffer();
//						
//						aanum_lines.add(aanum);
//						aanum = 0;
//					}
//				}
//				if (seq_line_sb.length() != 0) {
//					seq_lines.add(seq_line_sb.toString());
//					seq_line_sb = new StringBuffer();
//					
//					aanum_lines.add(aanum);
//					aanum = 0;
//				}
//				
//				Vector<String> index_lines = new Vector<String>();
//				int aaind_inReindexStartFrom0 = 0;
//				for (int i = 0; i < aanum_lines.size(); i++) {
//					StringBuffer sb = new StringBuffer();
//					for (int j = 0; j < aanum_lines.get(i); j+=10) {
//						sb.append(replenishEndWithChar(
//								""+orig_indexes_inOriginalPDB[aaind_inReindexStartFrom0], 10, "&nbsp;"));
//						aaind_inReindexStartFrom0 += 10;
//						if (aaind_inReindexStartFrom0 >= orig_indexes_inOriginalPDB.length) {
//							aaind_inReindexStartFrom0 = orig_indexes_inOriginalPDB.length - 1;
//						}
//					}
//					index_lines.add(sb.toString());
//				}
//				
//				for (int i = 0; i < aanum_lines.size(); i++) {
//					sequenceContentBuf.append(index_lines.get(i));
//					sequenceContentBuf.append("<br />\n");
//					sequenceContentBuf.append(seq_lines.get(i));
//					sequenceContentBuf.append("<br />\n");
//				}
//				
//				sequenceContentBuf.append("\t<br /></font>\n"
//						+ "\t</td></tr>\n");
//
//				zhanglabHtmlContent.append(sequenceContentBuf.toString());
//			} // END protein sequence information
//			
//			{// query sites information
//				StringBuffer querysite_sb = new StringBuffer();
//				querysite_sb.append("<tr><td align=\"left\">\n");
//				querysite_sb.append("<span class=\"STYLE5\">Inputted Binding Sites:</span>\n");
//				querysite_sb.append("<br />\n");
////				querysite_sb.append("<font style=\"font-family:'Courier New', Courier, monospace\">\n");
//				querysite_sb.append("<font face=\"Times New Roman\" class=\"STYLE10\">\n");
//				
//				StringBuffer sb = new StringBuffer();
//				for (int i = 0; i < querysites.length; i++) {
//					sb.append(querysites[i] + " ");
//					if (sb.length() >= 79) {
//						querysite_sb.append(sb.toString().replaceAll(" ", "&nbsp;"));
//						querysite_sb.append("<br />\n");
//						sb = new StringBuffer();
//					}
//				}
//				if (sb.length() != 0) {
//					querysite_sb.append(sb.toString().replaceAll(" ", "&nbsp;"));
//					querysite_sb.append("\n");
//					sb = new StringBuffer();
//				}
//				querysite_sb.append("<br /><br /></font>\n");
//				querysite_sb.append("</td></tr>\n");
//				
//				zhanglabHtmlContent.append(querysite_sb.toString());
//			}// END query sites information
//			
//			{ // Search engineer
//				StringBuffer searchengineer_sb = new StringBuffer();
//				searchengineer_sb.append("<tr><td align=\"left\">\n");
//				searchengineer_sb.append("<span class=\"STYLE5\">Search Engine:</span>\n");
//				searchengineer_sb.append("<br />\n");
////				searchengineer_sb.append("<font style=\"font-family:'Courier New', Courier, monospace\">\n");
//				searchengineer_sb.append("<font face=\"Times New Roman\" class=\"STYLE10\">\n");
//				if (searchengine.equals("PPS-align")) {
//					searchengineer_sb.append("<a href=\"https://zhanglab.ccmb.med.umich.edu/PPS-align/\" target=_blank>PPS-align</a>");
//				}else if (searchengine.equals("APoc")) {
//					searchengineer_sb.append("<a href=\"http://pwp.gatech.edu/cssb/apoc/\" target=_blank>APoc</a>");
//				}else if (searchengine.equals("PPS-align+APoc")){
//					searchengineer_sb.append("<a href=\"https://zhanglab.ccmb.med.umich.edu/PPS-align/\" target=_blank>PPS-align</a> + <a href=\"http://pwp.gatech.edu/cssb/apoc/\" target=_blank>APoc</a>");
//				}else{
//					searchengineer_sb.append(searchengine);
//				}
//				searchengineer_sb.append("<br /><br /></font>\n");
//				searchengineer_sb.append("</td></tr>\n");
//				
//				zhanglabHtmlContent.append(searchengineer_sb.toString());
//			} // END Search engineer 
//			
//			{ // Sequence identity cutoff
//				StringBuffer seqidcut_sb = new StringBuffer();
//				seqidcut_sb.append("<tr><td align=\"left\">\n");
//				seqidcut_sb.append("<span class=\"STYLE5\">Sequence Identity Cutoff:</span>\n");
//				seqidcut_sb.append("<br />\n");
////				seqidcut_sb.append("<font style=\"font-family:'Courier New', Courier, monospace\">\n");
//				seqidcut_sb.append("<font face=\"Times New Roman\" class=\"STYLE10\">\n");
//				seqidcut_sb.append(_NumFormat.formatDouble(seqid_cut, "#0.00"));
//				seqidcut_sb.append("<br /><br /></font>\n");
//				seqidcut_sb.append("</td></tr>\n");
//				
//				zhanglabHtmlContent.append(seqidcut_sb.toString());
//			} // Sequence identity cutoff 
//			
//			{ 
//				// 3D view
//				StringBuffer similar_record_buf = new StringBuffer();
//				similar_record_buf.append("\t<tr><td align=\"left\"><span class=\"STYLE5\">Similar Record Information:</span></td></tr>\n");
//				similar_record_buf.append("</table>\n\n");
//				
//				similar_record_buf.append("<table width=\"90%\">\n");
//				similar_record_buf.append("<script type=\"text/javascript\" src=\"../../jmol/JSmol.min.js\"></script>\n"
//						+ "<script type=\"text/javascript\" src=\"../../jmol/Jmol2.js\"></script>\n");
//
//				/*1  */similar_record_buf.append("<tr>\n");
//				/*1.1*/similar_record_buf.append("<td valign=\"top\" align =\"left\">\n");
//				/*****/similar_record_buf.append("<b> Pocket structural alignment</b> (Query in yellow and template in red)\n<br/>");
//				/*****/similar_record_buf.append("<script type=text/javascript>\n"); 
//				/*****/similar_record_buf.append("jmolInitialize(\"../../jmol/\"); jmolSetAppletColor(\"#000000\"); jmolApplet(400,\"load pocket_"+first_pocname+".sup.pdb; spin on; select :Q; color yellow; select :T; color red; \");document.write(\"<br/><center>\");jmolButton(\"Reset\",\"Reset to initial orientation\");jmolCheckbox(\"set spin x 10; set spin y 10; spin on\", \"spin off\", \"Spin On/Off\", false);document.write(\"</center>\");\n");
//				/*****/similar_record_buf.append("</script>\n"); 
//				/*1.1*/similar_record_buf.append("</td>\n");
//				/*1.2*/similar_record_buf.append("<td valign=\"top\" align = \"left\">\n");
//				/*1.2.1*/similar_record_buf.append("<table border=\"0\"  style=\"font-family:Arial;font-size:13px;\">\n");
//				/*********/similar_record_buf.append("<tr bgcolor=\"#DEDEDE\">"
//						+ "<th>Click to View</th>"
//						+ "<th>Rank</th>"
//						+ "<th>Similar Score<sup>a</sup></th>"
//						+ "<th>Sequence Identity<sup>b</sup></th>"
//						+ "<th>Pocket Sup<sup>c</sup></th>"
//						+ "<th>Alignment Info<sup>d</sup></th>"
//						+ "<th>Corr Protein<sup>e</sup></th>"
//						+ "<th>Corr Lig<sup>f</sup></th>"
//						+ "<th>Corr Sites<sup>g</sup></th></tr>\n");
//				
//				BufferedReader br = new BufferedReader(new FileReader(workfolder+"/search_list.txt"));
//				String l = br.readLine();
//				int count = 1;
//				while (null != l) {
//					String[] lc = l.split("~ZJUT~");
//					String pocname = lc[0];
//					String[] lc0c = lc[0].split("_");
//					String protname = lc0c[0];
//					String ligtype = lc0c[1];
//					double score = Double.parseDouble(lc[1]);
//					double seqid_score = Double.parseDouble(lc[2]);
//					String[] bsites = lc[3].split(" ");
//					
//					if (count%2 == 0) {
//				/****1****/similar_record_buf.append("<tr bgcolor=\"#DEDEDE\">\n");	
//					}else {
//				/****1****/similar_record_buf.append("<tr>\n");	
//					}
//					
//				
//				/****2****/similar_record_buf.append("<td align=\"center\">\n");
//				/*********/similar_record_buf.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
//				if (1 == count) {
//				/*********/similar_record_buf.append("jmolRadio(\"load pocket_"+pocname+".sup.pdb; spin on; select :Q; color yellow; select :T; color red; set radius 0.90; set spin x 5; set spin y 5; spin off;\", \"\", true);\n");
//				}else {
//				/*********/similar_record_buf.append("jmolRadio(\"load pocket_"+pocname+".sup.pdb; spin on; select :Q; color yellow; select :T; color red; set radius 0.90; set spin x 5; set spin y 5; spin off;\", \"\", false);\n");
//				}
//				/*********/similar_record_buf.append("</script>\n");
//				/****2****/similar_record_buf.append("</td>\n");
//				/****3****/similar_record_buf.append("<font face=\"Monospace\">\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append(count+"\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append(_NumFormat.formatDouble(score, "#0.00")+"\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append(_NumFormat.formatDouble(seqid_score, "#0.00")+"\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append("<a href=\"./pocket_"+pocname+".sup.pdb\" target=_blank>link</a>\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append("<a href=\"./pocket_"+pocname+".ali.txt\" target=_blank>link</a>\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append("<a href=\"./"+protname+".pdb\" target=_blank>"+protname+"</a>\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append("<a href=\"./"+pocname+".pdb\" target=_blank>"+ligtype+"</a>\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
//				FileWriter bsites_fw = new FileWriter(workfolder+"/"+pocname+".bsites");
//				for (int kk = 0; kk < bsites.length; kk++) {
//					bsites_fw.write(bsites[kk] + "\n");
//				}
//				bsites_fw.close();
//				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"center\">\n");
//				/****4****/similar_record_buf.append("<a href=\"./"+pocname+".bsites\" target=_blank>link</a>\n");
//				/****4****/similar_record_buf.append("</td>\n");
//				
////				/****4****/similar_record_buf.append("<td valign=\"middle\" align=\"left\">\n");
////				StringBuffer fsas = new StringBuffer();
////				StringBuffer ssb = new StringBuffer();
////				for (int kk = 0; kk < bsites.length; kk++) {
////					ssb.append(bsites[kk] + " ");
////					if (ssb.length() >= 40) {
////						fsas.append(ssb.toString().replaceAll(" ", "&nbsp;"));
////						fsas.append("<br />\n");
////						ssb = new StringBuffer();
////					}
////				}
////				if (ssb.length() != 0) {
////					fsas.append(ssb.toString().replaceAll(" ", "&nbsp;"));
////					fsas.append("<br />\n");
////					ssb = new StringBuffer();
////				}
////				/****4****/similar_record_buf.append(fsas.toString() + "\n");
////				/****4****/similar_record_buf.append("</td>\n");
//				
//				/****3****/similar_record_buf.append("</font>\n");
//				/****1****/similar_record_buf.append("</tr>\n");
//					count++;
//					l = br.readLine();
//				}
//				br.close();
//				
//				/*1.2.1*/similar_record_buf.append("</table>");
//				
//				
//				StringBuffer tmp_sb = new StringBuffer();
//				if (searchengine.equals("PPS-align")) {
//					tmp_sb.append("<a href=\"https://zhanglab.ccmb.med.umich.edu/PPS-align/\" target=_blank>PPS-align</a>");
//				}else if (searchengine.equals("APoc")) {
//					tmp_sb.append("<a href=\"http://pwp.gatech.edu/cssb/apoc/\" target=_blank>APoc</a>");
//				}else if (searchengine.equals("PPS-align+APoc")){
//					tmp_sb.append("<a href=\"https://zhanglab.ccmb.med.umich.edu/PPS-align/\" target=_blank>PPS-align</a> + <a href=\"http://pwp.gatech.edu/cssb/apoc/\" target=_blank>APoc</a>");
//				}else{
//					tmp_sb.append(searchengine);
//				}
//				
//				similar_record_buf.append("<p>");
//				similar_record_buf.append("(a) \"Similar Score\" is the similar score, which is calculated by "+tmp_sb+", between query pocket and the corresponding database pocket.<br />\n");
//				similar_record_buf.append("(b) \"Sequence Identity\" is the sequence identity between your inputted protein and the corresponding database protein.<br />\n");
//				similar_record_buf.append("(c) \"Pocket Sup\" is the superposition between the query and corresponding database pockets, which is generated by "+tmp_sb + "<br />\n");
//				similar_record_buf.append("(d) \"Alignment Info\" is the alignment information between the query and corresponding database pockets, which is generated by "+tmp_sb + "<br />\n");
//				similar_record_buf.append("(e) \"Corr Protein\" is the corresponding protein structure information<br />\n");
//				similar_record_buf.append("(f) \"Corr Lig\" is the corresponding Ligand structure information<br />\n");
//				similar_record_buf.append("(g) \"Corr Sites\" is the corresponding information of the pocket sites<br />\n");
//				similar_record_buf.append("</p>");
//				
//				
//				/*1.2*/similar_record_buf.append("</td>");
//				/*1  */similar_record_buf.append("</tr>");
//				/*222*/similar_record_buf.append("<tr><td>");
//				/*****/similar_record_buf.append("<br /><br />");
//				/*222*/similar_record_buf.append("</td></tr>");
//				
//				zhanglabHtmlContent.append(similar_record_buf.toString());
//			} // END KEY code
//			
//			{// Notice
//				StringBuffer notice_sb = new StringBuffer();
//				
//				notice_sb.append("<tr>");
//				notice_sb.append("<td>");
//				notice_sb.append("<br /><br />");
//				notice_sb.append("[<a href=\"https://zhanglab.ccmb.med.umich.edu/PPS-search\">Back to Server</a>]");
//				notice_sb.append("</td>");
//				notice_sb.append("</tr>");
//				
//				zhanglabHtmlContent.append(notice_sb.toString());
//			}// END Notice
//			
//			zhanglabHtmlContent.append("</table>");
//			StringBuffer contentBuf = new StringBuffer();
//			contentBuf.append(zhanglabHtmlHead);
//			contentBuf.append(zhanglabHtmlContent.toString());
//			contentBuf.append(zhanglabHtmlTail);
//			
//			String wholeStr = contentBuf.toString();
//			wholeStr = wholeStr.substring(0, wholeStr.length());
//			_File.writeToFile(wholeStr, htmlPath, false);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
	}

	private static String replenishEndWithChar(String str, int total_len, String ch){
		int oLen = str.length();
		if (oLen > total_len)
			return str;
		
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		for (int i = 0; i < total_len-oLen; i++)
			sb.append(ch);
		
		return sb.toString();
	}
	
	/**
	 * @param aPocPath : path
	 * @param bPocPath : path
	 * @return ans[0] = PPS-score; ans[1] = runing time (s).
	 * @throws Exception
	 */
	public static String runPPSalign4Superpose(String aPocPath, String bPocPath, String save_sup)throws Exception{
		String lsalign_cmd = FixParams.PPSalign_exe + " " + aPocPath + " " + bPocPath 
				+ " -oSup " + save_sup;
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
		
		return tmpSb.toString();
	}
	
	/**
	 * @param aPocPath : path
	 * @param bPocPath : path
	 * @return ans[0] = PPS-score; ans[1] = runing time (s).
	 * @throws Exception
	 */
	public static String runAPoc4Superpose(String aPocPath, String bPocPath, String save_sup)throws Exception{
		String lsalign_cmd = FixParams.APoc_exe + " " + aPocPath + " " + bPocPath + " -fa 0 -plen 1";
		
		System.out.println(lsalign_cmd);
		
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
		
		String outputStr = tmpSb.toString();
		if (!outputStr.contains("Index Ch1 Resid1  AA1 Ch2 Resid2  AA2 Distance Cos(theta)")){
			System.out.println();
			System.out.println(lsalign_cmd);
			System.out.println(outputStr);
			System.out.println();
			throw new Exception("APoc cannot work normally for \n" + aPocPath + "\n" + bPocPath);
		}
		
		String[] outLines = outputStr.split("\n");
		int ind = 0;
		for (ind = 0; ind < outLines.length; ind++){
			if (outLines[ind].contains("i          t(i)         u(i,1)         u(i,2)         u(i,3)")){
				break;
			}
		}
		ind++;
		
		// load rotation matrix 
		double[][] PocRotatedMtx = new double[3][4];
		String rot0Info = outLines[ind].trim(); 
		String[] rot0Vec = rot0Info.split(" +");
		PocRotatedMtx[0][0] = Double.parseDouble(rot0Vec[1]);
		PocRotatedMtx[0][1] = Double.parseDouble(rot0Vec[2]);
		PocRotatedMtx[0][2] = Double.parseDouble(rot0Vec[3]);
		PocRotatedMtx[0][3] = Double.parseDouble(rot0Vec[4]);
		ind++;
		
		
		String rot1Info = outLines[ind].trim(); 
		String[] rot1Vec = rot1Info.split(" +");
		PocRotatedMtx[1][0] = Double.parseDouble(rot1Vec[1]); 
		PocRotatedMtx[1][1] = Double.parseDouble(rot1Vec[2]);
		PocRotatedMtx[1][2] = Double.parseDouble(rot1Vec[3]);
		PocRotatedMtx[1][3] = Double.parseDouble(rot1Vec[4]);
		ind++;
		
		
		String rot2Info = outLines[ind].trim(); 
		String[] rot2Vec = rot2Info.split(" +");
		PocRotatedMtx[2][0] = Double.parseDouble(rot2Vec[1]); 
		PocRotatedMtx[2][1] = Double.parseDouble(rot2Vec[2]);
		PocRotatedMtx[2][2] = Double.parseDouble(rot2Vec[3]);
		PocRotatedMtx[2][3] = Double.parseDouble(rot2Vec[4]);
		ind++;
		
		FileWriter fw = new FileWriter(save_sup);
		{// save rotated aPocPath
			BufferedReader br = new BufferedReader(new FileReader(aPocPath));
			String l = br.readLine();
			boolean isPocContent = false;
			while (null != l) {
				if (!isPocContent && l.startsWith("TER")) {
					isPocContent = true;
					l = br.readLine();
					continue;
				}
				if (!l.startsWith("ATOM")) {
					l = br.readLine();
					continue;
				}
				
				if (isPocContent) {
					double ox = Double.parseDouble(l.substring(30, 38));
					double oy = Double.parseDouble(l.substring(38, 46));
					double oz = Double.parseDouble(l.substring(46, 54));
					
					double nx = PocRotatedMtx[0][0]
							+PocRotatedMtx[0][1]*ox
							+PocRotatedMtx[0][2]*oy
							+PocRotatedMtx[0][3]*oz;
					double ny = PocRotatedMtx[1][0]
							+PocRotatedMtx[1][1]*ox
							+PocRotatedMtx[1][2]*oy
							+PocRotatedMtx[1][3]*oz;
					double nz = PocRotatedMtx[2][0]
							+PocRotatedMtx[2][1]*ox
							+PocRotatedMtx[2][2]*oy
							+PocRotatedMtx[2][3]*oz;
					
					String newl = l.substring(0, 21) 
							+ "Q" 
							+ l.substring(22, 30) 
							+ _Str.replenishHeadWithSpace(_NumFormat.formatDouble(nx, "0.000"), 8)
							+ _Str.replenishHeadWithSpace(_NumFormat.formatDouble(ny, "0.000"), 8)
							+ _Str.replenishHeadWithSpace(_NumFormat.formatDouble(nz, "0.000"), 8)
							+ "  1.00  0.00";
					
					fw.write(newl + "\n");
				}
				
				l = br.readLine();
			}
			br.close();
			fw.write("TER\n");
		}// END save rotated aPocPath
		{ // Start save bPocPath
			BufferedReader br = new BufferedReader(new FileReader(bPocPath));
			String l = br.readLine();
			boolean isPocContent = false;
			while (null != l) {
				if (!isPocContent && l.startsWith("TER")) {
					isPocContent = true;
					l = br.readLine();
					continue;
				}
				if (!l.startsWith("ATOM")) {
					l = br.readLine();
					continue;
				}
				
				if (isPocContent) {
					String newl = l.substring(0, 21) 
							+ "T" 
							+ l.substring(22);
					
					fw.write(newl + "\n");
				}
				l = br.readLine();
			}
			br.close();
			fw.write("TER\n");
		}
		fw.close();
		return tmpSb.toString();
	}
}
