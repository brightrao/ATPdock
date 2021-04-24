package _util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import _BioStruct.Protein;

public class _File {

	public static HashMap<String, String> loadFasta(String path){
		HashMap<String, String> proteinSeqs = new HashMap<String, String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String proteinCode = "";
			StringBuffer proteinSeq = new StringBuffer();
			String line = in.readLine();
			while (line != null) {
				if (!line.startsWith("#")) { 
					if (line.startsWith(">")) {
						if (!proteinCode.equals("")) {
							String str = proteinSeq.toString();
							if (str.endsWith("\n"))
								str = str.substring(0, str.length() - 1);
							proteinSeqs.put(proteinCode.trim().toUpperCase(), str);
						}
						proteinCode = line.substring(1, line.length());
						proteinSeq.delete(0, proteinSeq.length());
					} else {
						if (!line.equals("")) {
							proteinSeq.append(line + "\n");
						}
					}
				}
				line = in.readLine();
			}
			if (!proteinCode.equals("")) {
				String str = proteinSeq.toString();
				if (str.endsWith("\n"))
					str = str.substring(0, str.length() - 1);
				proteinSeqs.put(proteinCode.trim().toUpperCase(), str);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proteinSeqs;
	}
	
	public static HashMap<String, StringBuffer> loadFasta8StrBuf(String path){
		HashMap<String, StringBuffer> proteinSeqs = new HashMap<String, StringBuffer>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String proteinCode = "";
			StringBuffer proteinSeq = new StringBuffer();
			String line = in.readLine();
			while (line != null) {
				if (!line.startsWith("#")) { 
					if (line.startsWith(">")) {
						if (!proteinCode.equals("")) {
							String str = proteinSeq.toString();
							if (str.endsWith("\n"))
								str = str.substring(0, str.length() - 1);
							proteinSeqs.put(proteinCode.trim().toUpperCase(), new StringBuffer(str));
						}
						proteinCode = line.substring(1, line.length());
						proteinSeq.delete(0, proteinSeq.length());
					} else {
						if (!line.equals("")) {
							proteinSeq.append(line + "\n");
						}
					}
				}
				line = in.readLine();
			}
			if (!proteinCode.equals("")) {
				String str = proteinSeq.toString();
				if (str.endsWith("\n"))
					str = str.substring(0, str.length() - 1);
				proteinSeqs.put(proteinCode.trim().toUpperCase(), new StringBuffer(str));
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proteinSeqs;
	}
	
	public static HashMap<String, String> loadFasta(String path, boolean isNeedNameToUppercase){
		HashMap<String, String> proteinSeqs = new HashMap<String, String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String proteinCode = "";
			StringBuffer proteinSeq = new StringBuffer();
			String line = in.readLine();
			while (line != null) {
				if (!line.startsWith("#")) { 
					if (line.startsWith(">")) {
						if (!proteinCode.equals("")) {
							String str = proteinSeq.toString();
							if (str.endsWith("\n"))
								str = str.substring(0, str.length() - 1);
							if (isNeedNameToUppercase){
								proteinSeqs.put(proteinCode.trim().toUpperCase(), str);
							}else{
								proteinSeqs.put(proteinCode.trim(), str);
							}
						}
						proteinCode = line.substring(1, line.length());
						proteinSeq.delete(0, proteinSeq.length());
					} else {
						if (!line.equals("")) {
							proteinSeq.append(line + "\n");
						}
					}
				}
				line = in.readLine();
			}
			if (!proteinCode.equals("")) {
				String str = proteinSeq.toString();
				if (str.endsWith("\n"))
					str = str.substring(0, str.length() - 1);
				if (isNeedNameToUppercase){
					proteinSeqs.put(proteinCode.trim().toUpperCase(), str);
				}else{
					proteinSeqs.put(proteinCode.trim(), str);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proteinSeqs;
	}
	
	public static double[][] loadPsipredPSS(String psipred_res_file_path){
		Vector<double[]> tAns = new Vector<double[]>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(psipred_res_file_path));
			br.readLine();	//remove head "# PSIPRED VFORMAT (PSIPRED V3.2)"
			br.readLine();  //remove second line
			String line = br.readLine();
			while (null != line){
				String[] lc = line.trim().split(" +");
				if (6 != lc.length){
					throw new Exception(psipred_res_file_path + " may be not PSIPRED SS Result file");
				}
				double[] tmp = new double[3];
				tmp[0] = Double.parseDouble(lc[3]);
				tmp[1] = Double.parseDouble(lc[4]);
				tmp[2] = Double.parseDouble(lc[5]);
				tAns.add(tmp);
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int size = tAns.size();
		double[][] ans = new double[size][tAns.get(0).length];
		for (int i = 0; i < size; i++){
			ans[i] = tAns.get(i);
		}
		return ans;
	}
	
	public static String loadPsipredPSSstate(String psipred_res_file_path){
		StringBuffer tAns = new StringBuffer();
		try{
			BufferedReader br = new BufferedReader(new FileReader(psipred_res_file_path));
			br.readLine();	//remove head "# PSIPRED VFORMAT (PSIPRED V3.2)"
			br.readLine();  //remove second line
			String line = br.readLine();
			while (null != line){
				String[] lc = line.trim().split(" +");
				if (6 != lc.length){
					throw new Exception(psipred_res_file_path + " may be not PSIPRED SS Result file");
				}
				tAns.append(lc[2]);
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return tAns.toString();
	}
	
	public static double[][] loadNormBlastPSSM(String logistic_normalize_blastpgp_res_file_path){

		Vector<double[]> tAns = new Vector<double[]>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(logistic_normalize_blastpgp_res_file_path));
			String line = br.readLine();
			while (null != line){
				String[] lc = line.trim().split(" +");
				if (20 != lc.length){
					throw new Exception(logistic_normalize_blastpgp_res_file_path + " may be not BLASTPGP Result file");
				}
				double[] tmp = new double[lc.length];
				for (int i = 0; i < lc.length; i++){
					tmp[i] = Double.parseDouble(lc[i]);
				}
				tAns.add(tmp);
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		int size = tAns.size();
		double[][] ans = new double[size][tAns.get(0).length];
		for (int i = 0; i < size; i++){
			ans[i] = tAns.get(i);
		}
		return ans;
	
	}
	
	/****************************************************************
	 * @param jsd_res_file_path
	 * @return L*1
	 * @throws Exception
	 ***************************************************************/
	public static double[][] loadJSDvec(String jsd_res_file_path)throws Exception{
		Vector<double[]> tAns = new Vector<double[]>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(jsd_res_file_path));
			String line = br.readLine();
			while (null != line){
				String[] lc = line.trim().replaceAll("\t", " ").split(" +");
				if (3 > lc.length){
					System.out.println(lc.length + "\n" + line);
					for (int i = 0; i < lc.length; i++) {
						System.out.println(lc[i]);
					}
					br.close();
					throw new Exception(jsd_res_file_path + " may be not JSD Result file");
				}
				double[] tmp = new double[1];
				try{
					tmp[0] = Double.parseDouble(lc[2]);
				}catch(Exception e){
					tmp[0] = 0.0;
				}
				tAns.add(tmp);
				
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
		int size = tAns.size();
		double[][] ans = new double[size][tAns.get(0).length];
		for (int i = 0; i < size; i++){
			ans[i] = tAns.get(i);
		}
		return ans;
	}
	
	public static double[][] loadSANNa3(String a3_res_file_path)throws Exception{
		Vector<double[]> tAns = new Vector<double[]>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(a3_res_file_path));
			br.readLine();	//remove head "# SANN VFORMAT (SANN V1.0 by K. Joo)"
			br.readLine();  //remove second line
			String line = br.readLine();
			while (null != line){
				String[] lc = line.trim().split(" +|\t");
				if (9 != lc.length){
					System.out.println(line);
					br.close();
					throw new Exception(a3_res_file_path + " may be not SANN a3 Result file");
				}
				double[] tmp = new double[3];
				try{
					tmp[0] = Double.parseDouble(lc[3]);
					if (lc[3].equalsIgnoreCase("NaN")){
						tmp[0] = 0.0;
					}
				}catch(Exception e){
					tmp[0] = 0.0;
				}
				try{
					tmp[1] = Double.parseDouble(lc[4]);
					if (lc[4].equalsIgnoreCase("NaN")){
						tmp[1] = 0.0;
					}
				}catch(Exception e){
					tmp[1] = 0.0;
				}
				try{
					tmp[2] = Double.parseDouble(lc[5]);
					if (lc[5].equalsIgnoreCase("NaN")){
						tmp[2] = 0.0;
					}
				}catch(Exception e){
					tmp[2] = 0.0;
				}
				tAns.add(tmp);
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
		int size = tAns.size();
		double[][] ans = new double[size][tAns.get(0).length];
		for (int i = 0; i < size; i++){
			ans[i] = tAns.get(i);
		}
		return ans;
	} 
	
	public static double[][] loadMatrix(String mtxPath){
		 Vector<double[]> _ans = loadRes(mtxPath);
		 return _Matrix.exchangeVDArrToD2Arr(_ans);
	}
	
	// load junh_type res [trueLab posi_prob nega_prob]
	public static Vector<double[]> loadRes(String res_path){
		Vector<double[]> ans = null;
		try{
			ans = new Vector<double[]>();
			BufferedReader br = new BufferedReader(new FileReader(res_path));
			String line = br.readLine();
			while (null != line){
				String[] lc = line.trim().split(" +|\t");
				double[] tmp = new double[lc.length];
				for (int i = 0; i < tmp.length; i++){
					tmp[i] = Double.parseDouble(lc[i]);
				}
				
				ans.add(tmp);
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		return ans;
	}
	
	/**
	 * @param dssp_file : the dssp information file path
	 * @return the TCO (cosine of angle between C=O of residue I and C=O of residue I-1.) information, [one column N rows]
	 * 		   
	 * @throws Exception : File Reading or writing excepotion
	 */
	public static double[] loadDSSPTCOIngoreBreakResidues(String dssp_file)throws Exception{
		Vector<Double> _ans = new Vector<Double>();
		
		BufferedReader br = new BufferedReader(new FileReader(dssp_file));
		String line = br.readLine();
		while (null != line){
			if (line.startsWith("  #  RESIDUE AA STRUCTURE BP1 BP2  ACC")){
				break;
			}
			line = br.readLine();
		}
		line = br.readLine();
		while (null != line){
			String AAs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (AAs.contains(line.substring(13, 14))){
				// add the element
				Double tco = Double.parseDouble(line.substring(85, 91).trim());
				_ans.add(tco);
			}
			line = br.readLine();
		}
		br.close();
		
		double[] ans = new double[_ans.size()];
		for (int i = 0; i < _ans.size(); i++){
			ans[i] = _ans.get(i);
		}
		
		return ans;
	}
	
	/**
	 * @param dssp_file : the dssp information file path
	 * @parma isNorm : true means need (x-mean)/std
	 * @return the ACC (solvent accessibility, number of water molecules in contact with this residue *10. or residue water
	 * 		   exposed surface in Angstrom**2.) information, [one column N rows]
	 * 		   
	 * @throws Exception : File Reading or writing excepotion
	 */
	public static double[] loadDSSPACCIngoreBreakResidues(String dssp_file, boolean isNorm)throws Exception{
		Vector<Double> _ans = new Vector<Double>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(dssp_file));
			String line = br.readLine();
			while (null != line){
				if (line.startsWith("  #  RESIDUE AA STRUCTURE BP1 BP2  ACC")){
					break;
				}
				line = br.readLine();
			}
			line = br.readLine();
			while (null != line){
				String AAs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				if (AAs.contains(line.substring(13, 14))){
					// add the element
					Double acc = Double.parseDouble(line.substring(35, 38).trim());
					_ans.add(acc);
				}
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		double[] ans = new double[_ans.size()];
		
		if (isNorm){
			double mean = _Math.average(_ans);
			double std = _Math.standart_deviation(_ans);
			for (int i = 0; i < _ans.size(); i++){
				ans[i] = (_ans.get(i)-mean)/std;
			}
		}else{
			for (int i = 0; i < _ans.size(); i++){
				ans[i] = _ans.get(i);
			}
		}
		
		return ans;
	}
	
	/**
	 * @param protein_dssp_file_path: represents a dssp format file absolute path
	 * @return : a string whose per element represents the residue secondary structural type
	 * 				(Code	Description
						      H	Alpha helix 	(1,0,0,0,0,0,0,0)
							  B	Beta bridge		(0,1,0,0,0,0,0,0)
							  E	Strand			(0,0,1,0,0,0,0,0)
                              G	Helix-3			(0,0,0,1,0,0,0,0)
                              I	Helix-5			(0,0,0,0,1,0,0,0)
                              T	Turn			(0,0,0,0,0,1,0,0)
                              S	Bend			(0,0,0,0,0,0,1,0)
                              X Others)			(0,0,0,0,0,0,0,1)
      */
	public static double[][] loadDSSPSSIngoreBreakResidues(String dssp_file)throws Exception{
		StringBuffer dssp_res = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(dssp_file));
		String line = br.readLine();
		while (null != line){
			if (line.startsWith("  #  RESIDUE AA STRUCTURE BP1 BP2  ACC")){
				break;
			}
			line = br.readLine();
		}
		line = br.readLine();
		while (null != line){
			String AAs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (AAs.contains(line.substring(13, 14))){
				// add the element
				char dssp = line.charAt(16);
				if (' ' == dssp){
					dssp = 'X';
				}
				dssp_res.append(dssp);
			}
			line = br.readLine();
		}
		br.close();
		
		double[][] ans = new double[dssp_res.length()][8];
		String tmp = "HBEGITSX";
		for (int i = 0; i < dssp_res.length(); i++){
			for (int j = 0; j < 8; j++)
				ans[i][j] = 0.0;
			int pos = tmp.indexOf(dssp_res.charAt(i));
			
			ans[i][pos] = 1.0;
		}
		return ans;
	}
	
	public static String loadSeqFromDSSPIngoreBreakResidues(String dssp_file)throws Exception{
		StringBuffer ans = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(dssp_file));
		String line = br.readLine();
		while (null != line){
			if (line.startsWith("  #  RESIDUE AA STRUCTURE BP1 BP2  ACC")){
				break;
			}
			line = br.readLine();
		}
		line = br.readLine();
		while (null != line){
			String AAs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (AAs.contains(line.substring(13, 14))){
				
				ans.append(line.substring(13, 14));
			}
			line = br.readLine();
		}
		br.close();
		
		return ans.toString();
	}
	
	/**
	 * @param protein_dssp_file_path: represents a dssp format file absolute path
	 * @return : a string whose per element represents the residue secondary structural type
	 * 				(Code	Description
						      H	Alpha helix 	(1,0,0,0,0,0,0,0)
							  B	Beta bridge		(0,1,0,0,0,0,0,0)
							  E	Strand			(0,0,1,0,0,0,0,0)
                              G	Helix-3			(0,0,0,1,0,0,0,0)
                              I	Helix-5			(0,0,0,0,1,0,0,0)
                              T	Turn			(0,0,0,0,0,1,0,0)
                              S	Bend			(0,0,0,0,0,0,1,0)
                              X   Others)		(0,0,0,0,0,0,0,1)
      */
	public static double[][] loadDSSPSS(String dssp_file)throws Exception{
		StringBuffer dssp_res = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(dssp_file));
		String line = br.readLine();
		while (null != line){
			if (line.startsWith("  #  RESIDUE AA STRUCTURE BP1 BP2  ACC")){
				break;
			}
			line = br.readLine();
		}
		line = br.readLine();
		int preIndex = -1;
		while (null != line){
			String AAs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (AAs.contains(line.substring(13, 14))){
				int index = Integer.parseInt(line.substring(6, 6+4).trim());
				if (-1 == preIndex){
					preIndex = index;
				}
				// supplement the space element
				for (int i = 0; i < index - preIndex - 1; i++){
					dssp_res.append('X');
				}
				
				// add the element
				char dssp = line.charAt(16);
				if (' ' == dssp){
					dssp = 'X';
				}
				dssp_res.append(dssp);
				
				preIndex = index;
			}
			line = br.readLine();
		}
		br.close();
		
		double[][] ans = new double[dssp_res.length()][8];
		String tmp = "HBEGITSX";
		for (int i = 0; i < dssp_res.length(); i++){
			for (int j = 0; j < 8; j++)
				ans[i][j] = 0.0;
			int pos = tmp.indexOf(dssp_res.charAt(i));
			
			ans[i][pos] = 1.0;
		}
		return ans;
	}
	
	public static void writeToFASTAFileStr2VecStr(HashMap<String, Vector<String>> contents, char split_ch, String fileName){
		File f = new File(fileName);
		if (!f.isFile()){
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter w = new FileWriter(f);
			Set<String> s = contents.keySet();
			Object[] oArr = s.toArray();
			for (int i=0; i<oArr.length; i++){
				w.write(">" + oArr[i]+ "\n");
				
				Vector<String> tmp = contents.get(oArr[i]);
				for (int j = 0; j < tmp.size()-1; j++){
					w.write(tmp.get(j) + split_ch);
				}
				w.write(tmp.get(tmp.size()-1) + "\n");
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, Vector<String>> loadFaStr2VecStr(String filename, char split_ch)throws Exception{
		HashMap<String, String> tmpHm = _File.loadFasta(filename, false);
		Object[] ids = tmpHm.keySet().toArray();
		
		HashMap<String, Vector<String>> ans = new HashMap<String, Vector<String>>();
		for (int i = 0; i < ids.length; i++){
			String tmp = tmpHm.get(ids[i]);
			
			String[] lc = tmp.split(""+split_ch);
			Vector<String> tmpVec = new Vector<String>();
			for (int j = 0; j < lc.length; j++)
				tmpVec.add(lc[j]);
			
			ans.put((String)ids[i], tmpVec);
		}
		
		return ans;
	}
	
	public static void writeToFASTAStrBufFile(HashMap<String, StringBuffer> contents, String fileName){
		File f = new File(fileName);
		if (!f.isFile()){
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter w = new FileWriter(f);
			Set<String> s = contents.keySet();
			Object[] oArr = s.toArray();
			for (int i=0; i<oArr.length; i++){
				w.write(">" + oArr[i]+ "\n");
				w.write(contents.get(oArr[i]).toString() + "\n");
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFASTAFile(HashMap<String, String> contents, String fileName, boolean isContinue){
		File f = new File(fileName);
		if (!f.isFile()){
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter w = new FileWriter(f, isContinue);
			Set<String> s = contents.keySet();
			Object[] oArr = s.toArray();
			for (int i=0; i<oArr.length; i++){
				w.write(">" + oArr[i]+ "\n");
				w.write(contents.get(oArr[i]) + "\n");
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFASTAFile(HashMap<String, String> contents, String fileName){
		File f = new File(fileName);
		if (!f.isFile()){
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter w = new FileWriter(f);
			Set<String> s = contents.keySet();
			Object[] oArr = s.toArray();
			for (int i=0; i<oArr.length; i++){
				w.write(">" + oArr[i]+ "\n");
				w.write(contents.get(oArr[i]) + "\n");
			}
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(String line, String filepath, boolean isContinue){
		try {
			FileWriter fw = new FileWriter(filepath, isContinue);
			fw.write(line);
			fw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToLIBSVMFile(double[] arr, String libsvmFilepath, boolean isContinue){
		try {
			FileWriter fw = new FileWriter(libsvmFilepath, isContinue);
			if (arr[arr.length-1] == 1.0){
				fw.write("1\t");
			}else{
				fw.write("2\t");
			}
				
			for (int i = 0; i < arr.length-1; i++){
				fw.write((i+1)+":"+_NumFormat.formatDouble(arr[i], "#0.000") + "\t");
			}
			fw.write("\n");
			
			fw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(double[] arr, String filepath, boolean isContinue){
		try {
			FileWriter fw = new FileWriter(filepath, isContinue);
			for (int i = 0; i < arr.length; i++){
				fw.write(_NumFormat.formatDouble(arr[i], "#0.000") + "\t");
			}
			fw.write("\n");
			fw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToFile_1Element1line(double[] arr, String filepath, boolean isContinue){
		try {
			FileWriter fw = new FileWriter(filepath, isContinue);
			for (int i = 0; i < arr.length; i++){
				fw.write(_NumFormat.formatDouble(arr[i], "#0.000000") + "\n");
			}
			fw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(String line, FileWriter fw){
		try {
			fw.write(line);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void delFolder(String folder){
		if (!new File(folder).exists()){
			return;
		}
		
		File[] list = new File(folder).listFiles();
		for (int i = 0; i < list.length; i++){
			if (list[i].isDirectory()){
				delFolder(list[i].getAbsolutePath());
			}
			list[i].delete();
		}
		
		new File(folder).delete();
	}
	
	public static void delSpecialNameContentFilesInFolder(String folder, String content){
		if (!new File(folder).exists()){
			return;
		}
		
		File[] list = new File(folder).listFiles();
		for (int i = 0; i < list.length; i++){
			if (list[i].isDirectory()){
				delSpecialNameContentFilesInFolder(list[i].getAbsolutePath(), content);
			}
			if (list[i].getName().contains(content)){
				System.out.println("Remove "+list[i].getAbsolutePath()+" ... ");
				list[i].delete();
			}
		}
		
		new File(folder).delete();
	}
	
	/**
	 * @param mol2Path : contains one or more ligands mol2 info
	 * @return : mol2 info arr, [i] = the ith ligand mol2 info
	 */
	public static String[] readEachLigsInfoesInMol2File(String mol2Path)throws Exception{
		Vector<StringBuffer> _ans = new Vector<StringBuffer>();
		
		boolean isFirst = true;
		StringBuffer mol2Sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(mol2Path));
		String l = br.readLine();
		while (null != l){
			if (l.startsWith("@<TRIPOS>MOLECULE")){
				if (isFirst){
					isFirst = false;
				}else{
					_ans.add(mol2Sb);
					mol2Sb = new StringBuffer();
				}
			}
			
			mol2Sb.append(l+"\n");
			l = br.readLine();
		}
		br.close();
		
		if (mol2Sb.toString().contains("@<TRIPOS>MOLECULE"))
			_ans.add(mol2Sb);
		
		String[] ans = new String[_ans.size()];
		for (int i = 0; i < ans.length; i++){
			ans[i] = _ans.get(i).toString();
		}
		
		return ans;
	}
	
	public static boolean copy(String fileFrom, String fileTo) {
		try {
			InputStream in = new FileInputStream(fileFrom);
			OutputStream out = new FileOutputStream(fileTo);
			byte[] bt = new byte[1024];
			int count;
			while ((count = in.read(bt)) > 0) {
				out.write(bt, 0, count);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException ex) {
			System.out.println("Copy Error!");
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * re-number the residues indexes
	 * @param opdbchainpath
	 * @param saveModifiedpdbchainpath
	 */
	public static void pdbchainFileModify(String opdbchainpath, String saveModifiedpdbchainpath){
		try{
//			FileWriter fw = new FileWriter(saveModifiedpdbchainpath);
//			
//			BufferedReader br = new  BufferedReader(new FileReader(opdbchainpath));
//			String line = br.readLine();
//			while (null != line){
//				line = line.toUpperCase();
//				String threeWordAA = line.substring(17, 20);
//				if (line.startsWith("ATOM") && 
//					AminoAcid.AMINOACIDS.contains(threeWordAA)){
//					byte[] bits = line.getBytes();
//					bits[16] = ' ';
//					fw.write(new String(bits)+"\n");
//				}
//				line = br.readLine();
//			}
//			br.close();
//			fw.close();
			
			Protein prot = new Protein(null, opdbchainpath, false);
			prot.save(saveModifiedpdbchainpath);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String load2Str(String path)throws Exception{
		BufferedReader in = null;
		String str = null;
		try {
			in = new BufferedReader(new FileReader(path));
			StringBuffer buf = new StringBuffer();
	
			String line = in.readLine();
			while (line != null) {
				// Regular Expression
				buf.append(line);
				buf.append("\n");
				line = in.readLine();
			}
			str = buf.toString();
			if(str.length()>0){
				str = str.substring(0, str.length() - 1);
			}
			
			in.close();
		} catch (Exception e) {
			throw e;
		} 
		return str;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		// TODO Auto-generated method stub
//		String seq = "KIYTKTGDKGFSSTFTGERRPKDDQVFEAVGTTDELSSAIGFALELVTEKGHTFAEELQKIQCTLQDVGSALATPCSSAYTTFKAGPILELEQWIDKYTSQLPPLTAFILPSGGKISSALHFCRAVCRRAERRVVPLVQMGETDANVAKFLNRLSDYLFTLARYAAMKEGNQEKIYMK";
//		double[] tco = _File.loadDSSPTCOIngoreBreakResidues("/home/hujunum/2IDXB.dssp");
//		
//		for (int i = 0; i < tco.length; i++){
//			System.out.print(tco[i]);
//			System.out.println();
//		}
//		System.out.println(""+seq.length()+" : "+tco.length);
		
		_File.pdbchainFileModify("/nfs/amino-home/hujunum/library/pdbchain/3FKQA.pdb", 
				"./PDBCHAINMODIFIED.del");
		
	}

}
