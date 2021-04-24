package process.GenerateDataset;

import java.io.*;
import java.util.*;

import _BioStruct.*;
import _database.BioLip;
import _runner.CDHitRunner;
import _runner.OpenBabelRunner;
import _util.AminoAcid;
import _util.AtomFixInfo;
import _util._File;
import _util._Log;
import _util._NumFormat;
import _util._Str;

public class ConstructPocLigLib {
	
	private static BioLip biolip;
	private static String save_poc_lig_prot_info = "/data/hujun/academic/PPSalign_workshop/database/db_poclig_info_cd99.list";
	
	private static String savefolder = "/data/hujun/academic/PPSalign_workshop/database";
	private static String poc_folder = savefolder + "/poc";
	private static String lig_folder = savefolder + "/lig";
	
	public static void main(String[] args) throws Exception {
		init();
		
		FileWriter fw = new FileWriter(save_poc_lig_prot_info+"tmp.fa");
		
		biolip = new BioLip();
		Object[] ids = biolip.getAllProteinNames();
		int num = 0;
		for (int i = 0; i < ids.length; i++){
			String protname = (String)ids[i];
			
			System.out.println(protname + " is processing...");
			
			String protseq = biolip.getProtSeq(protname);
			if (protseq.length() < 30){ // Igonre fragment
				continue;
			}
						
			String[] anns = biolip.getAnnotationInfo(protname);
			for (int j = 0; j < anns.length; j++){
				String ligtype = biolip.getLigType8AnnotationInfo(anns[j]);
				if (ligtype.equalsIgnoreCase("NUC")
						|| ligtype.equalsIgnoreCase("III")
						|| ligtype.equalsIgnoreCase("FE")
						|| ligtype.equalsIgnoreCase("FE2")
						|| ligtype.equalsIgnoreCase("CA")
						|| ligtype.equalsIgnoreCase("MG")
						|| ligtype.equalsIgnoreCase("MN")
						|| ligtype.equalsIgnoreCase("ZN")
						|| ligtype.equalsIgnoreCase("NA")
						|| ligtype.equalsIgnoreCase("K")
						|| ligtype.equalsIgnoreCase("CO")) {
					continue;
				}
				
				Vector<String> bsites_from1 = biolip.getBSITEs_StartFrom1_8AnnotationInfo(anns[j]);
				if (bsites_from1.size() >= 4){
					fw.write(">"+protname+"\n"+protseq+"\n");
					num++;
					break;
				}
			}
		}
		fw.close();
		
		System.out.println(num);
		
		// Cd-Hit sequence identity 99%
		CDHitRunner.cut(save_poc_lig_prot_info+"tmp.fa", 0.99, save_poc_lig_prot_info+".fa");
		HashMap<String, String> hm = _File.loadFasta(save_poc_lig_prot_info+".fa", false);
		new File(save_poc_lig_prot_info+"tmp.fa").delete();
		new File(save_poc_lig_prot_info+".fa").delete();
		
		FileWriter fw1 = new FileWriter(save_poc_lig_prot_info);
		Object[] _ids = hm.keySet().toArray();
		for (int i = 0; i < _ids.length; i++){
			String protname = (String)_ids[i];
			
			System.out.println("Stage 2: " + protname + " is processing...");
			
			String protseq = biolip.getProtSeq(protname);
			Protein prot = new Protein(protname, biolip.getProteinPDBFile(protname), false);
			boolean isMissResInMid = hasMissResidueInMiddle(prot);
			
			String pdbseq = prot.getSeq();
			if (pdbseq.length() < 30){
				continue;
			}
			boolean isSameSeq = protseq.equalsIgnoreCase(pdbseq);
			
			boolean isOK = false;
			String[] anns = biolip.getAnnotationInfo(protname);
			for (int j = 0, l = 1; j < anns.length; j++){
				String ligtype = biolip.getLigType8AnnotationInfo(anns[j]);
				
				if (ligtype.equalsIgnoreCase("NUC")
						|| ligtype.equalsIgnoreCase("III")
						|| ligtype.equalsIgnoreCase("FE")
						|| ligtype.equalsIgnoreCase("FE2")
						|| ligtype.equalsIgnoreCase("CA")
						|| ligtype.equalsIgnoreCase("MG")
						|| ligtype.equalsIgnoreCase("MN")
						|| ligtype.equalsIgnoreCase("ZN")
						|| ligtype.equalsIgnoreCase("NA")
						|| ligtype.equalsIgnoreCase("K")
						|| ligtype.equalsIgnoreCase("CO")) {
					continue;
				}
				
				String ligpdb = biolip.getLigandPDBFile( biolip.getLigFileName8AnnotationInfo(anns[j]) );
				
				Vector<String> bsites_from1 = null;
				String seq = null;
				if (!isSameSeq){
					Ligand lig = new Ligand(ligpdb);
					bsites_from1 = calculateBsite(prot, lig);
					seq = pdbseq;
				}else{
					bsites_from1 = biolip.getBSITEs_StartFrom1_8AnnotationInfo(anns[j]);
					seq = protseq;
				}
				
				if (bsites_from1.size() < 4){
					continue;
				}
				
				
				String poc_name = protname+"_"+ligtype+"_BS" + _Str.replenishHeadWithChar(""+l, 2, '0');
				Ligand lig = new Ligand("LIG".getBytes(), ligpdb, false);
				if (lig.size() < 4) {
					continue;
				}
				
				StringBuffer _bsites_from1 = new StringBuffer();
				for (int k = 0; k < bsites_from1.size(); k++){
					_bsites_from1.append(bsites_from1.get(k)+" ");
				}
				
				_File.copy(ligpdb, lig_folder+"/"+poc_name+".pdb");
				OpenBabelRunner.transferLigInfoTypes("pdb", lig_folder+"/"+poc_name+".pdb", "mol2", lig_folder+"/"+poc_name+".mol2");
				
				/*if (ligtype.equalsIgnoreCase("ATP"))*/{
					if (isMissResInMid){
						fw1.write("discontinuous\t" + protname 
								+ "\t" +ligtype
								+ "\tBS" + _Str.replenishHeadWithChar(""+l, 2, '0') 
								+ "\t" + _bsites_from1.toString().trim() 
								+ "\t" + seq + "\n");
					}else{
						fw1.write("continuous\t" + protname 
								+ "\t" +ligtype
								+ "\tBS" + _Str.replenishHeadWithChar(""+l, 2, '0') 
								+ "\t" + _bsites_from1.toString().trim() 
								+ "\t" + seq + "\n");
					}
					
					
					// Save pocket structure
					Vector<Character> pocaa = new Vector<Character>();
					Vector<Residue> pocress = new Vector<Residue>();
					for (int k = 0; k < bsites_from1.size(); k++) {
						int ind = Integer.parseInt(bsites_from1.get(k).substring(1)) - 1;
						pocress.add(prot.get(ind));
						pocaa.add(prot.getAA(ind));
					}
					
					savePocketbyAPocNeededFormat(poc_name, prot, pocaa,
							pocress, poc_folder+"/"+poc_name+".apoc.pdb");
					l++;
				}
				
				isOK = true;
			}
		}
		fw1.close();
		
		System.out.println("Protein number is " + _ids.length);
	}
	
	// return index is start from 1
	private static Vector<String> calculateBsite(Protein prot, Ligand lig){
		Vector<String> ans = new Vector<String>();
		
		String seq = prot.getSeq();
		for (int i = 0; i < prot.size(); i++){
			Residue i_res = prot.get(i);
			
			if (isInteracted(i_res, lig)){
				ans.add(""+seq.charAt(i)+(i+1));
			}
		}
		
		return ans;
	}
	
	private static boolean hasMissResidueInMiddle(Protein prot)throws Exception{
		for (int i = 1; i < prot.size(); i++){
			Point3D prev_res_ca = prot.get(i-1).getCaCoordinate();
			Point3D res_ca = prot.get(i).getCaCoordinate();
			
			double dis = prev_res_ca.distOu(res_ca);
			if (dis > 3.8*2){ // average distance between to Ca atoms is 3.8 angstrom.
				return true;
			}
		}
		return false;
	}
	
	private static boolean isInteracted(Residue a_res, Ligand lig) {
		boolean ans = false;
		for (int i = 0; i < a_res.size(); i++){
			Point3D i_pt = a_res.getPoint(i);
			String i_at = a_res.getAtomtype(i).trim();
			if ("H".equalsIgnoreCase(i_at))
				continue;
			
			double i_vdwr = AtomFixInfo.getVarDerWaalsRadius(i_at);
			
			for (int j = 0; j < lig.size(); j++){
				Point3D j_pt = lig.getPoint(j);
				String j_at = lig.getAtomType(j).trim();
				if ("H".equalsIgnoreCase(j_at))
					continue;
				
				double j_vdwr = AtomFixInfo.getVarDerWaalsRadius(j_at);
				
				double dis = i_pt.distOu(j_pt);
				double thres = 3.9; // angstrom
				if (i_vdwr != 3.9 && j_vdwr != 3.9) {
					thres = i_vdwr + j_vdwr + 0.5;
				}
				
				if (dis < thres){
					ans = true;
					break;
				}
			}
			
			if (true == ans)
				break;
		}
	
		return ans;
	}
	
	private static void init() {
		if (!new File(savefolder).exists()) {
			new File(savefolder).mkdirs();
		}
		if (!new File(poc_folder).exists()) {
			new File(poc_folder).mkdirs();
		}
		if (!new File(lig_folder).exists()) {
			new File(lig_folder).mkdirs();
		}
	}
	
	public static void savePocketbyAPocNeededFormat(String pocname, Protein prot, Vector<Character> pocAAs, 
			Vector<Residue> pocress, String savePath){
		try{
			FileWriter fw = new FileWriter(savePath);
			{
				// save protein structure
				for (int resPos= 0; resPos < prot.size(); resPos++){
					Residue res = prot.get(resPos);
					char aa = prot.getAA(resPos);
					int resIndex = res.getPosInProt();
					if (-1 == resIndex)
						resIndex = resPos+1;
					
					Vector<Point3D> resInfo = res.getPoints();
					Vector<String> resAtomTypes = res.getAtomtypes();
					Vector<Integer> resAtomIndexes = res.getAtomsPosInProt();
					for (int j = 0; j < resInfo.size(); j++){
						String atom_name = resAtomTypes.get(j);
						int atomPos = resAtomIndexes.get(j);
						fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+atomPos, 5));
						fw.write("  "+_Str.replenishEndWithSpace(atom_name, 3)+" ");
						
						// write three word amino acid
						String aminoacid = AminoAcid.oneWordToThree(aa);
						fw.write(aminoacid);
						fw.write(" A");
						fw.write(_Str.replenishHeadWithSpace(""+(resIndex), 4));
						fw.write("    ");
						String x_str = _Str.replenishHeadWithSpace(
								_NumFormat.formatDouble(resInfo.get(j).getX(), "#0.000"), 8);
						String y_str = _Str.replenishHeadWithSpace(
								_NumFormat.formatDouble(resInfo.get(j).getY(), "#0.000"), 8);
						String z_str = _Str.replenishHeadWithSpace(
								_NumFormat.formatDouble(resInfo.get(j).getZ(), "#0.000"), 8);
						fw.write(x_str+y_str+z_str);
						fw.write("  1.00 00.00           "); // fix 
						
						fw.write(atom_name.charAt(0)+"  \n");
					}
				}
				fw.write("TER\n");
			}
			{
				// save Pocket structure
				fw.write("PKT        "+_Str.replenishHeadWithSpace(""+pocress.size(), 4)+"     1000       "+pocname.trim()+"\n");
				for (int resPos= 0; resPos < pocress.size(); resPos++){
					Residue res = pocress.get(resPos);
					char aa = pocAAs.get(resPos);
					int resIndex = res.getPosInProt();
					if (-1 == resIndex)
						resIndex = resPos+1;
					
					Vector<Point3D> resInfo = res.getPoints();
					Vector<String> resAtomTypes = res.getAtomtypes();
					Vector<Integer> resAtomIndexes = res.getAtomsPosInProt();
					for (int j = 0; j < resInfo.size(); j++){
						String atom_name = resAtomTypes.get(j);
						int atomPos = resAtomIndexes.get(j);
						fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+atomPos, 5));
						fw.write("  "+_Str.replenishEndWithSpace(atom_name, 3)+" ");
						
						// write three word amino acid
						String aminoacid = AminoAcid.oneWordToThree(aa);
						fw.write(aminoacid);
						fw.write(" A");
						fw.write(_Str.replenishHeadWithSpace(""+(resIndex), 4));
						fw.write("    ");
						String x_str = _Str.replenishHeadWithSpace(
								_NumFormat.formatDouble(resInfo.get(j).getX(), "#0.000"), 8);
						String y_str = _Str.replenishHeadWithSpace(
								_NumFormat.formatDouble(resInfo.get(j).getY(), "#0.000"), 8);
						String z_str = _Str.replenishHeadWithSpace(
								_NumFormat.formatDouble(resInfo.get(j).getZ(), "#0.000"), 8);
						fw.write(x_str+y_str+z_str);
						fw.write("  1.00 00.00           "); // fix 
						
						fw.write(atom_name.charAt(0)+"  \n");
					}
				}
				fw.write("TER\n");
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
			_Log.dayRunLog("SSFL save function cannot work normally.", new Date());
			_Log.dayRunLog(e.getMessage(), new Date());
		}
	}
}
