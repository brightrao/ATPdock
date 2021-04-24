package _runner;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import _util.ConfigUtil;
import _util.StreamGobbler;
import _util._File;
import _util._Log;
import _util._Math;
import _util._Str;

public class OpenBabelRunner {
	
	public static void main(String[] args) throws Exception{
		if (4 != args.length && 7 != args.length){
			System.out.println("e.g., \n"
					+ "input_format (e.g., pdb)\n"
					+ "input_path (String)(Path)\n"
					+ "output_format (e.g., mol2, sdf)\n"
					+ "output_path (String)(Path)\n"
					+ "\n"
					+ "input_format (e.g., pdb)\n"
					+ "ligNameFa (String)(Path)\n"
					+ "corrInputFolder (String) (path)\n"
					+ "lig_input_suffix (String) (e.g., _lig.pdb)\n"
					+ "output_format (e.g., mol2, sdf)\n"
					+ "savefolder (String) (path)\n"
					+ "savesuffix (String) (e.g., _lig.sdf)");
			System.exit(-1);
		}
		if (args.length == 4){
			String input_format = args[0];
			String input_path = args[1];
			String output_format = args[2];
			String output_path = args[3];
			
			transferLigInfoTypes(input_format, input_path, output_format, output_path);
		}
		
		if (args.length == 7){
			String input_format = args[0];
			String ligNameFa = args[1];
			String corrInputFolder = args[2];
			String lig_input_suffix = args[3];
			String output_format = args[4];
			String savefolder = args[5];
			if (!new File(savefolder).exists())
				new File(savefolder).mkdirs();
			String savesuffix = args[6];
			
			HashMap<String, String> faHm = _File.loadFasta(ligNameFa, false);
			Object[] ids = faHm.keySet().toArray();
			for (int i = 0; i < ids.length; i++){
				String input_path = corrInputFolder + "/" + ids[i] + lig_input_suffix;
				String output_path = savefolder + "/" + ids[i] + savesuffix;
				transferLigInfoTypes(input_format, input_path, output_format, output_path);
			}
		}
		
		System.out.println("HAVE A GOOD DAY!");
		
	}
	
	public static void transferLigInfoTypes(String fromBabelFormat, String fromInfoPath, String toBabelFormat, String toInfoPath) throws Exception{
		String openbabel_bin = ConfigUtil.getConfig("OPENBABEL_BIN_FOLDER");
		if (null == openbabel_bin){
			System.out.println("Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			_Log.visitedLog("BlastRunner : Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			System.exit(-1);
		}
		
		String cmd = openbabel_bin+"/babel -i"+fromBabelFormat+" "+ fromInfoPath + " -o"+toBabelFormat+" " + toInfoPath;
		System.out.println(cmd);
		
		Process process;
		process = Runtime.getRuntime().exec(cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();

		process.waitFor();
		process.destroy();
	}
	
	/**
	 * @return 1024-bits binary fingerprint of a ligand pdb
	 */
	public static String getLigandFingerprint(String ligand_pdb)throws Exception{
		String openbabel_bin = ConfigUtil.getConfig("OPENBABEL_BIN_FOLDER");
		if (null == openbabel_bin){
			System.out.println("Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			_Log.visitedLog("BlastRunner : Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			System.exit(-1);
		}
		
		long suffix = new Date().getTime();
		String cmd = openbabel_bin+"/babel -ipdb "+ ligand_pdb + " -ofpt ./fpt." + suffix;
		
		System.out.println(cmd);
		
		long stime = new Date().getTime();
		Process process;
		process = Runtime.getRuntime().exec(cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();

		process.waitFor();
		process.destroy();
		long etime = new Date().getTime();
		System.out.println("Using time : " + (etime-stime)/1000 + "-s");
		
		System.out.println();
		
		// parse the file
		if (new File("./fpt." + suffix).isFile()){
			StringBuffer fptSb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("./fpt." + suffix));
			br.readLine();
			String l = br.readLine();
			while (null != l){
				String tmp = l.replaceAll(" ", "").trim();
				for (int i = 0; i < tmp.length(); i++){
					fptSb.append(_Math.hexadecimal2binary(tmp.charAt(i)));
				}
				
				l = br.readLine();
			}
			br.close();
			
			String fpt = fptSb.toString().trim();
			if (fpt.length() != 1024){
				System.out.println(fptSb.toString());
				System.out.println("Please check "+ ligand_pdb + "fpt.length() != 1024");
				fpt = _Str.replenishEndWithChar(fpt, 1024, '0');
			}
			new File("./fpt." + suffix).delete();
			
			return fpt;
		}else{
			return null;
		}
	}
	
	public static String getLigandInchi(String ligandBabelFormat, String ligandInfo)throws Exception{
		String openbabel_bin = ConfigUtil.getConfig("OPENBABEL_BIN_FOLDER");
		if (null == openbabel_bin){
			System.out.println("Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			_Log.visitedLog("OpenBabelRunner : Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			System.exit(-1);
		}
		
		long suffix = new Date().getTime();
		String cmd = openbabel_bin+"/babel -i"+ligandBabelFormat+" "+ ligandInfo + " -oinchi ./inchi." + suffix;
		
		System.out.println(cmd);
		
		long stime = new Date().getTime();
		Process process;
		process = Runtime.getRuntime().exec(cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();

		process.waitFor();
		process.destroy();
		long etime = new Date().getTime();
		System.out.println("Using time : " + (etime-stime)/1000 + "-s");
		
		// parse the file
		if (new File("./inchi." + suffix).isFile()){
			StringBuffer inchiSb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("./inchi." + suffix));
			String l = br.readLine();
			while (null != l){
				inchiSb.append(l);
				
				l = br.readLine();
			}
			br.close();
			
			String inchi = inchiSb.toString().trim();
			new File("./inchi." + suffix).delete();
			
			return inchi;
		}else{
			return null;
		}
	}
	
	public static String getLigandInchi(String ligandBabelFormat, String ligandInfo, String suffix)throws Exception{
		String openbabel_bin = ConfigUtil.getConfig("OPENBABEL_BIN_FOLDER");
		if (null == openbabel_bin){
			System.out.println("Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			_Log.visitedLog("OpenBabelRunner : Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			System.exit(-1);
		}
		
		String cmd = openbabel_bin+"/babel -i"+ligandBabelFormat+" "+ ligandInfo + " -oinchi ./inchi." + suffix;
		
		System.out.println(cmd);
		
		long stime = new Date().getTime();
		Process process;
		process = Runtime.getRuntime().exec(cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();

		process.waitFor();
		process.destroy();
		long etime = new Date().getTime();
		System.out.println("Using time : " + (etime-stime)/1000 + "-s");
		
		// parse the file
		if (new File("./inchi." + suffix).isFile()){
			StringBuffer inchiSb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("./inchi." + suffix));
			String l = br.readLine();
			while (null != l){
				inchiSb.append(l);
				
				l = br.readLine();
			}
			br.close();
			
			String inchi = inchiSb.toString().trim();
			new File("./inchi." + suffix).delete();
			
			return inchi;
		}else{
			return null;
		}
	}
	
	/**
	 * @param ligandBabelFormat: you can obtain it from http://openbabel.org/wiki/Babel. e.g.: pdb, mol2, mol, smi, and so on.
	 * @param ligandInfo : ligand information
	 * @return 1024-bits binary fingerprint of a ligand pdb
	 */
	public static String getLigandFingerprint(String ligandBabelFormat, String ligandInfo)throws Exception{
		String openbabel_bin = ConfigUtil.getConfig("OPENBABEL_BIN_FOLDER");
		if (null == openbabel_bin){
			System.out.println("Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			_Log.visitedLog("BlastRunner : Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			System.exit(-1);
		}
		
		long suffix = new Date().getTime();
		String cmd = openbabel_bin+"/babel -i"+ligandBabelFormat+" "+ ligandInfo + " -ofpt ./fpt." + suffix;
		
		System.out.println(cmd);
		
		long stime = new Date().getTime();
		Process process;
		process = Runtime.getRuntime().exec(cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();

		process.waitFor();
		process.destroy();
		long etime = new Date().getTime();
		System.out.println("Using time : " + (etime-stime)/1000 + "-s");
		
		System.out.println();
		
		// parse the file
		if (new File("./fpt." + suffix).isFile()){
			StringBuffer fptSb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("./fpt." + suffix));
			br.readLine();
			String l = br.readLine();
			while (null != l){
				String tmp = l.replaceAll(" ", "").trim();
				for (int i = 0; i < tmp.length(); i++){
					fptSb.append(_Math.hexadecimal2binary(tmp.charAt(i)));
				}
				
				l = br.readLine();
			}
			br.close();
			
			String fpt = fptSb.toString().trim();
			if (fpt.length() != 1024){
				System.out.println("Please check "+ ligandInfo + "fpt.length() != 1024");
				System.exit(-1); //TODO delete here
			}
			new File("./fpt." + suffix).delete();
			
			return fpt;
		}else{
			return null;
		}
	}
	
	/**
	 * @param ligandBabelFormat: you can obtain it from http://openbabel.org/wiki/Babel. e.g.: pdb, mol2, mol, smi, and so on.
	 * @param ligandInfo : ligand information
	 * @return 1024-bits binary fingerprint of a ligand pdb
	 */
	public static String getLigandFingerprint(String ligandBabelFormat, String ligandInfo, String suffix)throws Exception{
		String openbabel_bin = ConfigUtil.getConfig("OPENBABEL_BIN_FOLDER");
		if (null == openbabel_bin){
			System.out.println("Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			_Log.visitedLog("BlastRunner : Please ensure the Config params: OPENBABEL_BIN_FOLDER");
			System.exit(-1);
		}
		
		String cmd = openbabel_bin+"/babel -i"+ligandBabelFormat+" "+ ligandInfo + " -ofpt ./fpt." + suffix;
		if (!new File(ligandInfo).isFile()){
			_File.writeToFile(ligandInfo, "./origi."+suffix, false);
			
			cmd = openbabel_bin+"/babel -i"+ligandBabelFormat+" "+ "./origi."+suffix + " -ofpt ./fpt." + suffix;
		}
		
//		System.out.println(cmd);
		
		long stime = new Date().getTime();
		Process process;
		process = Runtime.getRuntime().exec(cmd);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();

		process.waitFor();
		process.destroy();
		long etime = new Date().getTime();
//		System.out.println("Using time : " + (etime-stime)/1000 + "-s");
		
//		System.out.println();
		
		// parse the file
		if (new File("./fpt." + suffix).isFile()){
			StringBuffer fptSb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("./fpt." + suffix));
			br.readLine();
			String l = br.readLine();
			while (null != l){
				String tmp = l.replaceAll(" ", "").trim();
				for (int i = 0; i < tmp.length(); i++){
					fptSb.append(_Math.hexadecimal2binary(tmp.charAt(i)));
				}
				
				l = br.readLine();
			}
			br.close();
			
			String fpt = fptSb.toString().trim();
			if (fpt.length() != 1024){
				System.out.println("Please check "+ ligandInfo + "fpt.length() != 1024");
				System.exit(-1); //TODO delete here
			}
			new File("./fpt." + suffix).delete();
			if (new File("./origi."+suffix).isFile())
				new File("./origi."+suffix).delete();
			
			return fpt;
		}else{
			return null;
		}
	}
}
