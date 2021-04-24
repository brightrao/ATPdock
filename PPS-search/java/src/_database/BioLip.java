package _database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import _BioStruct.Ligand;
import _BioStruct.Point3D;
import _BioStruct.Protein;
import _BioStruct.Residue;
import _runner.OpenBabelRunner;
import _util.ConfigUtil;
import _util._Distance;
import _util._File;
import _util._Log;
import _util._Math;
import _util._NumFormat;
import _util._Str;
import _util._Vector;

public class BioLip {
	private HashMap<String, StringBuffer> annotationInfoHm; // key = 1xefA, value = annotation information
	private HashMap<String, Integer> LigAMtxHm;
	private int maxValInLigMtxHm;
	private int minValInLigMtxHm;
	
	// temp 
	private HashMap<String, String> seq2nameHm;
	
	/*
Each line in the annotation file (BioLiP.tar.bz2, BioLiP_nr.tar.bz2) annotates for each ligand-protein interaction site in BioLiP. 
The columns are separated by the tab key, i.e., "\t", so that you can easily import the data into MySQL database with command like this:

load data local infile 'BioLiP.dat' into TABLE biolip FIELDS TERMINATED BY '\t';



Example entry: 
966c    A       1.90    BS06    RS2     A       1       N180 L181 A182 V215 H218 E219 H222 H228 L235 Y237 P238 S239 Y240 T241   N73 L74 A75 V108 H111 E112 H115 H121 L128 Y130 P131 S132 Y133 T134      M236 E219;E219  M129 E112;E112  3.4.24.-        0004222,0006508,0008237,0008270,0031012         ki=23nM (RS2)   Ki=23nM (RS2)           P03956  10074939        RWEQTHLTYRIENYTPDLPRADVDHAIEKAFQLWSNVTPLTFTKVSEGQADIMISFVRGDHRDNSPFDGPGGNLAHAFQPGPGIGGDAHFDEDERWTNNFREYNLHRVAAHELGHSLGLSHSTDIGALMYPSYTFSGDVQLAQDDIDGIQAIYGRSQ

The columns are (from left to right):
01	PDB ID
02	PDB chain
03	Resolution
04	Binding site number code
05	Ligand ID
06	Ligand chain
07	Ligand serial number
08  Binding site residues (with PDB residue numbering)
09  Binding site residues (with residue re-numbered starting from 1)
10	Catalytic site residues (different sites are separated by ';') (with PDB residue numbering)
11  Catalytic site residues (different sites are separated by ';') (with residue re-numbered starting from 1)
12	EC number
13	GO terms
14	Binding affinity by manual suervey of the original literature. The information in '()' is the PubMed ID
15	Binding affinity provided by the Binding MOAD database. The information in '()' is the ligand information in Binding MOAD
16	Binding affinity provided by the PDBbind-CN database. The information in '()' is the ligand information in PDBbind-CN
17	Binding affinity provided by the BindingDB database
18	UniProt ID
19	PubMed ID
20	Receptor sequence

The ligand-protein complex structure for each line entry be obtained by using the two PDB files:
1. the receptor structure file under the "receptor" folder (name is formed with columns 01,02: i.e., 0102.pdb)
2. the corresponding ligand structure file under the "ligand" folder (name is formed with columns 01,05,06,07: i.e., 01_05_06_07.pdb)
	 */
	
	public static void main(String[] args) throws Exception{
		BioLip biolip = new BioLip();
		System.out.println("Bilip database contains " + biolip.getAllProteinNames().length+" protein chains!");
		System.out.println();
		
//		double[] minMaxDisBetweenProtWithLig = biolip.calculateTheMinAndMaxDisBetweenProtWithLigInBioLip();
//		System.out.println("Minimized Distance Between Protein with Ligand is " + minMaxDisBetweenProtWithLig[0]);
//		System.out.println("Maximized Distance Between Protein with Ligand is " + minMaxDisBetweenProtWithLig[1]);
		
//		System.out.println(biolip.getAssciatedDistributionHashMapBetweenProtAtomWithLigAtom());
		System.out.println("ATP\t" + biolip.getAveStdDisBetweenLSBCenterCentralizedLigOverWithLBS().get("ATP"));
		
		System.out.println("Have A Good Day");
	}
	
	public BioLip(){
		if (null == this.annotationInfoHm){
			annotationInfoHm = new HashMap<String, StringBuffer>();
			try{
				String annotationFilePath = ConfigUtil.getConfig("BIOLIP_ANNOTATION_FILE");
				if (null == annotationFilePath){
					System.out.println("Please check the config file items : BIOLIP_ANNOTATION_FILE");
					_Log.dayRunLog("Please check the config file items : BIOLIP_ANNOTATION_FILE", new Date());
					System.exit(-1);
				}
				
				BufferedReader br  = new BufferedReader(new FileReader(annotationFilePath));
				String l = br.readLine();
				while (null != l){
					String[] lc = l.split("\t");
					String key = (lc[0]+lc[1]);
					
					StringBuffer tmp = annotationInfoHm.get(key);
					if (null == tmp){
						tmp = new StringBuffer();
					}
					tmp.append(l+"###");
					annotationInfoHm.put(key, tmp);
					
					l = br.readLine();
				}
				br.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public BioLip(boolean isNeedLoadDataBase){
		if (isNeedLoadDataBase){
			if (null == this.annotationInfoHm){
				annotationInfoHm = new HashMap<String, StringBuffer>();
				try{
					String annotationFilePath = ConfigUtil.getConfig("BIOLIP_ANNOTATION_FILE");
					if (null == annotationFilePath){
						System.out.println("Please check the config file items : BIOLIP_ANNOTATION_FILE");
						_Log.dayRunLog("Please check the config file items : BIOLIP_ANNOTATION_FILE", new Date());
						System.exit(-1);
					}
					
					BufferedReader br  = new BufferedReader(new FileReader(annotationFilePath));
					String l = br.readLine();
					while (null != l){
						String[] lc = l.split("\t");
						String key = (lc[0]+lc[1]);
						
						StringBuffer tmp = annotationInfoHm.get(key);
						if (null == tmp){
							tmp = new StringBuffer();
						}
						tmp.append(l+"###");
						annotationInfoHm.put(key, tmp);
						
						l = br.readLine();
					}
					br.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public Object[] getAllProteinNames(){
		Object[] ids = annotationInfoHm.keySet().toArray();
		return ids;
	}
	
	public Object[] getCD90ProteinNames(){
		String biolip_cd90_prot_seqfa = ConfigUtil.getConfig("BIOLIP_CD90_PROT_SEQFA_FILE");
		if (null == biolip_cd90_prot_seqfa){
			System.out.println("Please check the config file items : BIOLIP_CD90_PROT_SEQFA_FILE");
			_Log.dayRunLog("Please check the config file items : BIOLIP_CD90_PROT_SEQFA_FILE", new Date());
			System.exit(-1);
		}
		
		HashMap<String, String> seqCd90Hm = _File.loadFasta(biolip_cd90_prot_seqfa, false);
		Object[] ids = seqCd90Hm.keySet().toArray();
		return ids;
	}
	
	/**
	 * @param protname (pdbID{chain}), e.g. : 1xefA
	 * @return the annotation infomation
	 */
	public String[] getAnnotationInfo(String protname){
		String key = protname;
		
		StringBuffer infos = this.annotationInfoHm.get(key);
		
		if (null == infos){
			return null;
		}
		
		return infos.toString().split("###");
	}
	
	/**
	 * @param pocname (pdbID{chain}_BS01), e.g. : 1xefA_BS01
	 * @return the annotation infomation
	 */
	public String getAnnotationInfo8Pocname(String pocname){
		if (pocname.length() < 5){
			System.out.println(pocname);
		}
		
		String key = pocname.substring(0, 5);
		
		StringBuffer infos = this.annotationInfoHm.get(key);
		
		if (null == infos){
			return null;
		}
		String[] anns = infos.toString().split("###");
		String ann = null;
		for (int i = 0; i < anns.length; i++){
			String[] annItems = anns[i].split("\t");
			String pn = annItems[0]+annItems[1]+"_"+annItems[3];
			
			if (pn.equals(pocname)){
				ann = anns[i];
				break;
			}
		}
		
		return ann;
	}
	
	public String getProtName8Seq(String seq){
		if (null == seq2nameHm){
			seq2nameHm = new HashMap<String, String>();
			
			Object[] ids = annotationInfoHm.keySet().toArray();
			for (int i = 0; i < ids.length; i++){
				String protname = (String)ids[i];
				String protseq = this.getProtSeq(protname);
				
				seq2nameHm.put(protseq, protname);
			}
		}
		
		return seq2nameHm.get(seq);
	}
	
	public String getProtSeq(String protname){
		String key = protname;
		
		StringBuffer infos = this.annotationInfoHm.get(key);
		if (null == infos){
			return null;
		}
		String[] anns = infos.toString().split("###");
		String[] lc = anns[0].split("\t");
		
		return lc[19];
	}
	
	/**
	 * @param annotationInfo
	 * @return just name, not path
	 */
	public String getLigFileName8AnnotationInfo(String annotationInfo){
		String[] annItems = annotationInfo.split("\t");
		return annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6];
	}
	
	/**
	 * @param annotationInfo
	 * @return ligand type
	 */
	public String getLigType8AnnotationInfo(String annotationInfo){
		String[] annItems = annotationInfo.split("\t");
		return annItems[4];
	}
	
	public String getProteinName8AnnotationInfo(String annotationInfo){
		String[] annItems = annotationInfo.split("\t");
		return annItems[0]+annItems[1];
	}
	
	/**
	 * @param annotationInfo
	 * @return the poc name
	 */
	public String getPocName8AnnotationInfo(String annotationInfo){
		String[] annItems = annotationInfo.split("\t");
		return annItems[0]+annItems[1]+"_"+annItems[3];
	}
	
	/**
	 * @param annotationInfo
	 * @return the binding sites start from 0, the pdb file is renum to start 1
	 */
	public Integer[] getBSITEs8AnnotationInfo(String annotationInfo){
//		01	PDB ID
//		02	PDB chain
//		03	Resolution
//		04	Binding site number code
//		05	Ligand ID
//		06	Ligand chain
//		07	Ligand serial number
//		08  Binding site residues (with PDB residue numbering)
//		09  Binding site residues (with residue re-numbered starting from 1)
		
		String[] annItems = annotationInfo.split("\t");
		String[] bsites_strs = annItems[8].split(" +");
		Integer[] bsites = new Integer[bsites_strs.length];
		for (int i = 0; i < bsites_strs.length; i++){
			int tmp = Integer.parseInt(bsites_strs[i].substring(1)) - 1;
			bsites[i] = tmp;
		}
		
		return bsites;
	}
	
	/**
	 * @param annotationInfo
	 * @return the binding sites start from 1
	 */
	public Vector<String> getBSITEs_StartFrom1_8AnnotationInfo(String annotationInfo){
//		01	PDB ID
//		02	PDB chain
//		03	Resolution
//		04	Binding site number code
//		05	Ligand ID
//		06	Ligand chain
//		07	Ligand serial number
//		08  Binding site residues (with PDB residue numbering)
//		09  Binding site residues (with residue re-numbered starting from 1)
		
		String[] annItems = annotationInfo.split("\t");
		String[] bsites_strs = annItems[8].split(" +");
		Vector<String> bsites = new Vector<String>();
		for (int i = 0; i < bsites_strs.length; i++){
			bsites.add(bsites_strs[i]);
		}
		
		return bsites;
	}
	
	/**
	 * @param protname (pdbID{chain}), e.g. : 1xefA
	 * @return the corresponding pdb file path
	 */
	public String getProteinPDBFile(String protname){
		String key = protname;
		
		String pdbfolder = ConfigUtil.getConfig("BIOLIP_PDB_FOLDER");
		if (null == pdbfolder){
			System.out.println("Please check the config file items : BIOLIP_PDB_FOLDER");
			_Log.dayRunLog("Please check the config file items : BIOLIP_PDB_FOLDER", new Date());
			System.exit(-1);
		}
		
		if (!new File(pdbfolder+"/"+key+".pdb").isFile()){
			key = key.substring(0, 4).toLowerCase()+key.substring(4, 5).toUpperCase();
			if (!new File(pdbfolder+"/"+key+".pdb").isFile()){
				return null;
			}
		}
		
		
		return pdbfolder+"/"+key+".pdb";
	}
	
	/**
	 * @param protname (pdbID{chain}), e.g. : 1xefA
	 * @return the corresponding pdb file path
	 */
	public static String getProteinPDBpath(String protname){
		String key = protname;
		
		String pdbfolder = ConfigUtil.getConfig("BIOLIP_PDB_FOLDER");
		if (null == pdbfolder){
			System.out.println("Please check the config file items : BIOLIP_PDB_FOLDER");
			_Log.dayRunLog("Please check the config file items : BIOLIP_PDB_FOLDER", new Date());
			System.exit(-1);
		}
		
		if (!new File(pdbfolder+"/"+key+".pdb").isFile()){
			key = key.substring(0, 4).toLowerCase()+key.substring(4, 5).toUpperCase();
			if (!new File(pdbfolder+"/"+key+".pdb").isFile()){
				return null;
			}
		}
		
		
		return pdbfolder+"/"+key+".pdb";
	}

	/**
	 * @return 	key = ligand type, (e.g., ATP or CA);
	 * 			vale = {ligand_type}\t{corr_poc_num_in_biolip}\t{1024_bits_fingerprint}
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, String> getLigtype_CorrPocNum_AverageFPTs() throws Exception{
		String ligtype2avefptPath = ConfigUtil.getConfig("BIOLIP_LIGTYPE_CORRPOCNUM_AVERAGE_FPT");
		if (null == ligtype2avefptPath){
			System.out.println("Please check the config file items : BIOLIP_LIGTYPE_CORRPOCNUM_AVERAGE_FPT");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGTYPE_CORRPOCNUM_AVERAGE_FPT", new Date());
			System.exit(-1);
		}

		HashMap<String, String> ans = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(ligtype2avefptPath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			
			ans.put(lc[0], l);
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	/**
	 * @return 	key = ligand type, (e.g., ATP or CA);
	 * 			vale = {ligand_type}\t{corr_poc_num_in_biolip}\t{the sample pdb path}
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, String> getLigtype_CorrPocNum_SamplePDB() throws Exception{
		String ligtype2pdbPath = ConfigUtil.getConfig("BIOLIP_LIGTYPE_CORRPOCNUM_SAMPLEPDB");
		if (null == ligtype2pdbPath){
			System.out.println("Please check the config file items : BIOLIP_LIGTYPE_CORRPOCNUM_SAMPLEPDB");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGTYPE_CORRPOCNUM_SAMPLEPDB", new Date());
			System.exit(-1);
		}

		HashMap<String, String> ans = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(ligtype2pdbPath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			
			ans.put(lc[0], l);
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	/**
	 * @param ligfilename e.g.: 2n0u_48M_A_1
	 * @return
	 */
	public String getLigandPDBFile(String ligfilename){
		String key = ligfilename;
				
		String ligfolder = ConfigUtil.getConfig("BIOLIP_LIGAND_FOLDER");
		if (null == ligfolder){
			System.out.println("Please check the config file items : BIOLIP_LIGAND_FOLDER");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGAND_FOLDER", new Date());
			System.exit(-1);
		}
		
		if (!new File(ligfolder+"/"+key+".pdb").isFile()){
			return null;
		}
		
		return ligfolder+"/"+key+".pdb";
	}
	
	/**
	 * @param ligfilename e.g.: 2n0u_48M_A_1
	 * @return
	 */
	public static String getLigandPDBpath(String ligfilename){
		String key = ligfilename;
				
		String ligfolder = ConfigUtil.getConfig("BIOLIP_LIGAND_FOLDER");
		if (null == ligfolder){
			System.out.println("Please check the config file items : BIOLIP_LIGAND_FOLDER");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGAND_FOLDER", new Date());
			System.exit(-1);
		}
		
		if (!new File(ligfolder+"/"+key+".pdb").isFile()){
			return null;
		}
		
		return ligfolder+"/"+key+".pdb";
	}
	
	/**
	 * @param lig1type : the ligand type (e.g., ATP, CA, and SO4)
	 * @param lig2type : the ligand type (e.g., ATP, CA, and SO4)
	 * @return the association value 
	 * @description: 	here we must save two files:
					 	one is saving the biolip's ligands fingerprint information 
					 		(format is "prot_BSno	1024_bits_fingerprint")
						the other is saving the ligands association matrix 
						1. using lig1fpt and lig2fpt to search the most two similar ligands.
						2. search ligands association matrix using the two similar ligands.
	 * @construct ligand-association-matrix (LigAMtx): 
	 * 				1. one protein may binding more than one ligands. (ground-truth information)
	 */
	public double getLigAssociationMtxValue(String lig1type, String lig2type)throws Exception{
		String LigAMtxPath = ConfigUtil.getConfig("BIOLIP_LIGAMTX_PATH");
		if (null == LigAMtxPath){
			System.out.println("Please check the config file items : BIOLIP_LIGAMTX_PATH");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGAMTX_PATH", new Date());
			System.exit(-1);
		}
		
		if (!new File(LigAMtxPath).isFile()){
			constructLigAMtxFile(LigAMtxPath);
		}
		
		if (null != LigAMtxHm){
			LigAMtxHm = new HashMap<String, Integer>();
			maxValInLigMtxHm = -Integer.MAX_VALUE;
			minValInLigMtxHm = Integer.MAX_VALUE;
			BufferedReader br = new BufferedReader(new FileReader(LigAMtxPath));
			String l = br.readLine();
			while (null != l){
				String[] lc = l.split("\t");
				Integer val = Integer.parseInt(lc[1]);
				
				if (val > maxValInLigMtxHm)
					maxValInLigMtxHm = val;
				if (val < minValInLigMtxHm)
					minValInLigMtxHm = val;
				
				LigAMtxHm.put(lc[0], val);
				l = br.readLine();
			}
			br.close();
		}
		
		double ans = 0.0;
		Integer val = LigAMtxHm.get(lig1type+"-"+lig2type);
		if (null == val){
			val = LigAMtxHm.get(lig2type+"-"+lig1type);
		}
		if (null != val){
			ans = 1.0*(val - minValInLigMtxHm) / (maxValInLigMtxHm - minValInLigMtxHm);
		}
		
		return ans;
	}
	
	/**
	 * @param protname (pdbID{chain}), e.g. : 1xefA
	 * @return ligPocNames: protname+"_"+annItems[3] e.g., 1xefA_BS01
	 */
	public String[] getPocName8Protname(String protname){
		String key = protname;
		StringBuffer infos = this.annotationInfoHm.get(key);
		if (null == infos){
			return null;
		}
		String[] anns = infos.toString().split("###");
		String[] ans = new String[anns.length];
		for (int i = 0; i <ans.length; i++){
			String[] annItems = anns[i].split("\t");
			ans[i] = protname+"_"+annItems[3];
		}
		
		return ans;
	}
	
	/***
	 * @return 	key = ligPocNames: protname+"_"+annItems[3] e.g., 1xefA_BS01
	 * 			value = the corresponding ligand fingerprint 1024bit
	 */
	public HashMap<String, String> getLigFpt()throws Exception{
		// search the ligand fingerprint file
		String ligFptPath = ConfigUtil.getConfig("BIOLIP_LIGFPT_PATH");
		if (null == ligFptPath){
			System.out.println("Please check the config file items : BIOLIP_LIGFPT_PATH");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGFPT_PATH", new Date());
			System.exit(-1);
		}
		
		if (!new File(ligFptPath).isFile()){
			constructLigFptFile(ligFptPath);
		}
		
		HashMap<String, String> ans = new HashMap<String, String>();
		
		BufferedReader br = new BufferedReader(new FileReader(ligFptPath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split(" +");
			if (lc.length != 2){
				System.out.println("Please notice here: _implement.BioLipParser.searchSimilarLigType");
				continue;
			}
			
			ans.put(lc[0], lc[1]);
			
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	/**
	 * @param ligPocNames: protname+"_"+annItems[3] e.g., 1xefA_BS01
	 * @return the corresponding ligand fingerprint 1024bit
	 */
	public String getLigFPT8ligPocName(String ligPocName)throws Exception{
		// search the ligand fingerprint file
		String ligFptPath = ConfigUtil.getConfig("BIOLIP_LIGFPT_PATH");
		if (null == ligFptPath){
			System.out.println("Please check the config file items : BIOLIP_LIGFPT_PATH");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGFPT_PATH", new Date());
			System.exit(-1);
		}
		
		if (!new File(ligFptPath).isFile()){
			constructLigFptFile(ligFptPath);
		}
		
		String fpt = null;
		BufferedReader br = new BufferedReader(new FileReader(ligFptPath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split(" +");
			if (lc.length != 2){
				System.out.println("Please notice here: _implement.BioLipParser.searchSimilarLigType");
				continue;
			}
			
			if (lc[0].equalsIgnoreCase(ligPocName)){
				fpt = lc[1];
				break;
			}
			
			l = br.readLine();
		}
		br.close();
		
		return fpt;
	}
	
	/**
	 * @param ligfpts (1024-bits) may more than one
	 * @return the ligand type (e.g., ATP, CA, and SO4)
	 */
	public String[] searchSimilarLigTypes4EachFpt(String... ligfpts)throws Exception{
		// search the ligand fingerprint file
		String ligFptPath = ConfigUtil.getConfig("BIOLIP_LIGFPT_PATH");
		if (null == ligFptPath){
			System.out.println("Please check the config file items : BIOLIP_LIGFPT_PATH");
			_Log.dayRunLog("Please check the config file items : BIOLIP_LIGFPT_PATH", new Date());
			System.exit(-1);
		}
		
		if (!new File(ligFptPath).isFile()){
			constructLigFptFile(ligFptPath);
		}
		
		String[] ligPocNames = new String[ligfpts.length];
		double[] maxLigTCs = new double[ligfpts.length];
		for (int i = 0; i < ligfpts.length; i++)
			maxLigTCs[i] = -Double.MAX_VALUE;
		BufferedReader br = new BufferedReader(new FileReader(ligFptPath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split(" +");
			if (lc.length != 2){
				System.out.println("Please notice here: _implement.BioLipParser.searchSimilarLigType");
				continue;
			}
			
			for (int i = 0; i < ligfpts.length; i++){
				double Tc = _Str.tanimotoCoefficient(ligfpts[i], lc[1]);
				
				if (Tc > maxLigTCs[i]){
					maxLigTCs[i] = Tc;
					ligPocNames[i] = lc[0];
				}
			}
			
			l = br.readLine();
		}
		br.close();
		
		// map the binding pocket name to ligand type
		String[] ligtypes = new String[ligfpts.length];
		for (int i = 0; i < ligPocNames.length; i++){
			String[] tmp = ligPocNames[i].split("_");
			String protname = tmp[0];
			String pocNo = tmp[1];
			String[] anns = this.getAnnotationInfo(protname);
			for (int j = 0; j < anns.length; j++){
				String[] annItems = anns[j].split("\t");
				if (pocNo.equalsIgnoreCase(annItems[3])){
					ligtypes[i] = annItems[4];
					break;
				}
			}
		}
		
		return ligtypes;
	}
	
	private void constructLigFptFile(String saveLigFpt)throws Exception{
		Object[] protNames = getAllProteinNames();
		for (int i = 0; i < protNames.length; i++){
			String protname = (String)protNames[i];
			String[] anns = getAnnotationInfo(protname);
			for (int j = 0; j < anns.length; j++){
				String[] annItems = anns[j].split("\t");
				
				String ligPDB = getLigandPDBFile(annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6]);
				if (null == ligPDB){
					System.out.println("Biolip do not contain "+annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6]);
					continue;
				}
				
				System.out.println(annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6]);
				String ligfpt = OpenBabelRunner.getLigandFingerprint(ligPDB);
				if (null == ligfpt){
					continue;
				}
				
				_File.writeToFile(""+protname+"_"+annItems[3]+"     "+ligfpt+"\n", saveLigFpt, true);
			}
		}
	}
	
	private void constructLigAMtxFile(String saveLigAMtx)throws Exception{
		// construct the ligand-association-matrix information
		HashMap<String, Integer> ligAMtxHm = new HashMap<String, Integer>();
		
		Object[] protNames = getAllProteinNames();
		for (int i = 0; i < protNames.length; i++){
			String[] anns = getAnnotationInfo((String)protNames[i]);
			for (int j = 0; j < anns.length; j++){
				String[] jthAnnsInfo = anns[j].split("\t");
				String jthLigType = jthAnnsInfo[4];
				for (int k = j; k < anns.length; k++){
					String[] kthAnnsInfo = anns[k].split("\t");
					String kthLigType = kthAnnsInfo[4];
					
					String key1 = jthLigType + "-" + kthLigType;
					String key2 = kthLigType + "-" + jthLigType;
					Integer tmp1 = ligAMtxHm.get(key1);
					Integer tmp2 = ligAMtxHm.get(key2);
					if (null != tmp1){
						tmp1 += 1;
						ligAMtxHm.put(key1, tmp1);
					}else if (null != tmp2){
						tmp2 += 1;
						ligAMtxHm.put(key2, tmp2);
					}else{
						tmp1 = new Integer(1);
						ligAMtxHm.put(key1, tmp1);
					}
				}
			}
		}
		
		this.LigAMtxHm = ligAMtxHm;
		
		int maxVal = -Integer.MAX_VALUE;
		int minVal = Integer.MAX_VALUE;
		// save the LigAMtx file 
		FileWriter fw = new FileWriter(saveLigAMtx);
		Object[] ligAMtxIds = ligAMtxHm.keySet().toArray();
		for (int i = 0; i < ligAMtxIds.length; i++){
			// we save 8 pairs LigAMtx information on each line
			fw.write(ligAMtxIds[i]+"\t"+ligAMtxHm.get(ligAMtxIds[i])+"\n");
			
			if (ligAMtxHm.get(ligAMtxIds[i]) > maxVal){
				maxVal = ligAMtxHm.get(ligAMtxIds[i]);
			}
			if (ligAMtxHm.get(ligAMtxIds[i]) < minVal){
				minVal = ligAMtxHm.get(ligAMtxIds[i]);
			}
		}
		fw.close();
		
		this.maxValInLigMtxHm = maxVal;
		this.minValInLigMtxHm = minVal;
	}
	
	public Double getAssciatedValueBetweenProtAtomWithLigAtom(String protAtomType, String ligAtomType)throws Exception{
		String AssMtxA2AFilePath = ConfigUtil.getConfig("ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP");
		String AssMtxR2AFilePath = ConfigUtil.getConfig("ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
		if (null == AssMtxA2AFilePath || null == AssMtxR2AFilePath){
			System.out.println("The configure file does not contain : ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
			_Log.dayRunLog("The configure file does not contain : ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP", new Date());
			System.exit(-1);
		}
		
		if (!new File(AssMtxA2AFilePath).isFile()){
			generateAssociatedMtx(AssMtxA2AFilePath, AssMtxR2AFilePath);
		}
		
		BufferedReader br = new BufferedReader(new FileReader(AssMtxA2AFilePath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			
			if (lc[0].equalsIgnoreCase(protAtomType+"-"+ligAtomType)){
				return Double.parseDouble(lc[1]);
			}
			
			l = br.readLine();
		}
		br.close();
		
		return null;
	}
	
	public HashMap<String, Double> getAssciatedMtxBetweenProtAtomWithLigAtom()throws Exception{
		String AssMtxA2AFilePath = ConfigUtil.getConfig("ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP");
		String AssMtxR2AFilePath = ConfigUtil.getConfig("ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
		if (null == AssMtxA2AFilePath || null == AssMtxR2AFilePath){
			System.out.println("The configure file does not contain : ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
			_Log.dayRunLog("The configure file does not contain : ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP", new Date());
			System.exit(-1);
		}
		
		if (!new File(AssMtxA2AFilePath).isFile()){
			generateAssociatedMtx(AssMtxA2AFilePath, AssMtxR2AFilePath);
		}
		
		HashMap<String, Double> ans = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new FileReader(AssMtxA2AFilePath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			
			ans.put(lc[0], Double.parseDouble(lc[1]));
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	public HashMap<String, Double> getAssciatedMtxBetweenProtResWithLigAtom()throws Exception{
		String AssMtxA2AFilePath = ConfigUtil.getConfig("ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP");
		String AssMtxR2AFilePath = ConfigUtil.getConfig("ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
		if (null == AssMtxA2AFilePath || null == AssMtxR2AFilePath){
			System.out.println("The configure file does not contain : ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
			_Log.dayRunLog("The configure file does not contain : ASSOCIATED_MTX_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_MTX_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP", new Date());
			System.exit(-1);
		}
		
		if (!new File(AssMtxR2AFilePath).isFile()){
			generateAssociatedMtx(AssMtxA2AFilePath, AssMtxR2AFilePath);
		}
		
		HashMap<String, Double> ans = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new FileReader(AssMtxR2AFilePath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			
			ans.put(lc[0], Double.parseDouble(lc[1]));
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	/**
	 * @param dis
	 * @return writing for Assciated Distribution, return the index of dis 
	 */
	private int getAssciatedDistributionIndexByDistance(double dis){
		double step = 0.39;
		for (int i = 0; i < 10; i++){
			if (dis>=i*step && dis<(i+1)*step){
				return i;
			}
		}
		
		return -1; 
	}

	public Double getAssciatedDistributionValBetweenProtWithLig(HashMap<String, double[]> distriHm,
			String protRes2LigAtomKey, double dis){
		double[] distriArr = distriHm.get(protRes2LigAtomKey);
		if (null == distriArr){
			return null; 
		}
		
		int index = getAssciatedDistributionIndexByDistance(dis);
		if (index == -1) return 0.0;
		return distriArr[index];
	}
	
	public HashMap<String, double[]> getAssciatedDistributionHashMapBetweenProtResWithLigAtom()throws Exception{
		String AssDisA2AFilePath = ConfigUtil.getConfig("ASSOCIATED_DISTRIBUTION_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP");
		String AssDisR2AFilePath = ConfigUtil.getConfig("ASSOCIATED_DISTRIBUTION_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
		if (null == AssDisA2AFilePath || null == AssDisR2AFilePath){
			System.out.println("The configure file does not contain : ASSOCIATED_DISTRIBUTION_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_DISTRIBUTION_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
			_Log.dayRunLog("The configure file does not contain : ASSOCIATED_DISTRIBUTION_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_DISTRIBUTION_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP", new Date());
			System.exit(-1);
		}
		
		if (!new File(AssDisR2AFilePath).isFile()){
			generateAssociatedDistribution(AssDisA2AFilePath, AssDisR2AFilePath);
		}
		
		HashMap<String, double[]> ans = new HashMap<String,  double[]>();
		BufferedReader br = new BufferedReader(new FileReader(AssDisR2AFilePath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			String[] lcc = lc[1].split(",");
			double[] arr = new double[lcc.length];
			for (int i = 0; i < arr.length; i++){
				arr[i] = Double.parseDouble(lcc[i]);
			}
			
			ans.put(lc[0], arr);
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	public HashMap<String, double[]> getAssciatedDistributionHashMapBetweenProtAtomWithLigAtom()throws Exception{
		String AssDisA2AFilePath = ConfigUtil.getConfig("ASSOCIATED_DISTRIBUTION_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP");
		String AssDisR2AFilePath = ConfigUtil.getConfig("ASSOCIATED_DISTRIBUTION_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
		if (null == AssDisA2AFilePath || null == AssDisR2AFilePath){
			System.out.println("The configure file does not contain : ASSOCIATED_DISTRIBUTION_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_DISTRIBUTION_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP");
			_Log.dayRunLog("The configure file does not contain : ASSOCIATED_DISTRIBUTION_BETWEEN_PROTATOM_WITH_LIGATOM_BY_BIOLIP, ASSOCIATED_DISTRIBUTION_BETWEEN_PROTRES_WITH_LIGATOM_BY_BIOLIP", new Date());
			System.exit(-1);
		}
		
		if (!new File(AssDisA2AFilePath).isFile()){
			generateAssociatedDistribution(AssDisA2AFilePath, AssDisR2AFilePath);
		}
		
		HashMap<String, double[]> ans = new HashMap<String,  double[]>();
		BufferedReader br = new BufferedReader(new FileReader(AssDisA2AFilePath));
		String l = br.readLine();
		while (null != l){
			String[] lc = l.split("\t");
			String[] lcc = lc[1].split(",");
			double[] arr = new double[lcc.length];
			for (int i = 0; i < arr.length; i++){
				arr[i] = Double.parseDouble(lcc[i]);
			}
			
			ans.put(lc[0], arr);
			l = br.readLine();
		}
		br.close();
		
		return ans;
	}
	
	private void generateAssociatedDistribution(String saveAssDistributionBetweenProtAtomWithLigAtom, String saveAssDistributionBetweenProtResWithLigAtom){
		HashMap<String, int[]> assDisRes2Atom = new HashMap<String, int[]>();
		HashMap<String, int[]> assDisAtom2Atom = new HashMap<String, int[]>();
		
		StringBuffer exceptionSb = new StringBuffer();
		Object[] ids = annotationInfoHm.keySet().toArray();
		for (int i = 0; i < ids.length; i++){
			System.out.println("The "+(i+1)+"-th protein "+ids[i]);
			
			String protname = (String)ids[i];
			String[] annInfos = getAnnotationInfo(protname);
			
			String protpdb = getProteinPDBFile(protname);
			if (null == protpdb){
				continue;
			}
			
			Protein prot = new Protein(protname, protpdb, true);
			for (int j = 0; j < annInfos.length; j++){
				String[] annItems = annInfos[j].split("\t");
				
				String ligname = annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6];
				String ligpath = this.getLigandPDBFile(ligname);
				Ligand lig = new Ligand("Null".getBytes(), ligpath);
				
				String[] lbs = annItems[8].split(" +");
				for (int k = 0; k < lbs.length; k++){
					int index = Integer.parseInt(lbs[k].substring(1)) - 1;
					
					Residue res = prot.get(index);
					String resType = "" + prot.getAA(index);
					
					String assinfo = res.distNearestInfo(lig);
					String[] assinfos = assinfo.split("\t");
					double dis = Double.parseDouble(assinfos[4]);
					if (dis > 3.9){
						System.out.println(protname+ " - " + ligname);
						exceptionSb.append(protname+ " - " + ligname+"\t"+assinfo+"\n");
						continue;
					}
					
					int ind = this.getAssciatedDistributionIndexByDistance(dis);
					if (-1 == ind) continue;
					if (!assinfos[1].equalsIgnoreCase("null") && !assinfos[3].equalsIgnoreCase("null")){
						String res2AtomKey = resType + "-" + assinfos[3];
						int[] res2AtomTmp = assDisRes2Atom.get(res2AtomKey);
						if (null == res2AtomTmp)
							res2AtomTmp = new int[10];
						res2AtomTmp[ind]++;
						
						assDisRes2Atom.put(res2AtomKey, res2AtomTmp);
						
						String atom2AtomKey = assinfos[1] + "-" + assinfos[3];
						int[] atom2AtomTmp = assDisAtom2Atom.get(atom2AtomKey);
						if (null == atom2AtomTmp)
							atom2AtomTmp = new int[10];
						atom2AtomTmp[ind]++;
						
						assDisAtom2Atom.put(atom2AtomKey, atom2AtomTmp);
					}
				}
			}
		}
		
		System.out.println("Exception dis > 3.9 \n"+exceptionSb.toString());
		try {
			FileWriter atom2AtomFw = new FileWriter(saveAssDistributionBetweenProtAtomWithLigAtom);
			Object[] atom2AtomKeys = assDisAtom2Atom.keySet().toArray();
			for (int i = 0; i < atom2AtomKeys.length; i++){
				int[] distri = assDisAtom2Atom.get(atom2AtomKeys[i]);
				int total = 0;
				for (int j = 0; j < distri.length; j++)
					total += distri[j];
				
				double[] _distriVal = new double[distri.length];
				for (int j = 0; j < _distriVal.length; j++)
					_distriVal[j] = 1.0*distri[j]/total;
				
				atom2AtomFw.write(""+atom2AtomKeys[i]+"\t");
				for (int j = 0; j < _distriVal.length-1; j++){
					atom2AtomFw.write(_NumFormat.formatDouble(_distriVal[j], "#0.00000000")+",");
				}
				atom2AtomFw.write(_NumFormat.formatDouble(_distriVal[_distriVal.length-1], "#0.00000000")+"\n");
			}
			atom2AtomFw.close();
			
			FileWriter res2AtomFw = new FileWriter(saveAssDistributionBetweenProtResWithLigAtom);
			Object[] res2AtomKeys = assDisRes2Atom.keySet().toArray();
			for (int i = 0; i < res2AtomKeys.length; i++){
				int[] distri = assDisRes2Atom.get(res2AtomKeys[i]);
				
				int total = 0;
				for (int j = 0; j < distri.length; j++)
					total += distri[j];
				
				double[] _distriVal = new double[distri.length];
				for (int j = 0; j < _distriVal.length; j++)
					_distriVal[j] = 1.0*distri[j]/total;
				
				res2AtomFw.write(""+res2AtomKeys[i]+"\t");
				for (int j = 0; j < _distriVal.length-1; j++){
					res2AtomFw.write(_NumFormat.formatDouble(_distriVal[j], "#0.00000000")+",");
				}
				res2AtomFw.write(_NumFormat.formatDouble(_distriVal[_distriVal.length-1], "#0.00000000")+"\n");
			}
			res2AtomFw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void generateAssociatedMtx(String saveAssMtxBetweenProtAtomWithLigAtom, String saveAssMtxBetweenProtResWithLigAtom){
		HashMap<String, Integer> assMtxRes2Atom = new HashMap<String, Integer>();
		HashMap<String, Integer> assMtxAtom2Atom = new HashMap<String, Integer>();
		
		StringBuffer exceptionSb = new StringBuffer();
		Object[] ids = annotationInfoHm.keySet().toArray();
		for (int i = 0; i < ids.length; i++){
			System.out.println("The "+(i+1)+"-th protein "+ids[i]);
			
			String protname = (String)ids[i];
			String[] annInfos = getAnnotationInfo(protname);
			
			String protpdb = getProteinPDBFile(protname);
			if (null == protpdb){
				continue;
			}
			
			Protein prot = new Protein(protname, protpdb, true);
			for (int j = 0; j < annInfos.length; j++){
				String[] annItems = annInfos[j].split("\t");
				
				String ligname = annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6];
				String ligpath = this.getLigandPDBFile(ligname);
				Ligand lig = new Ligand("Null".getBytes(), ligpath);
				
				String[] lbs = annItems[8].split(" +");
				for (int k = 0; k < lbs.length; k++){
					int index = Integer.parseInt(lbs[k].substring(1)) - 1;
					
					Residue res = prot.get(index);
					String resType = "" + prot.getAA(index);
					
					String assinfo = res.distNearestInfo(lig);
					String[] assinfos = assinfo.split("\t");
					double dis = Double.parseDouble(assinfos[4]);
					if (dis > 3.9){
						System.out.println(protname+ " - " + ligname);
						exceptionSb.append(protname+ " - " + ligname+"\t"+assinfo+"\n");
						continue;
					}
					
					if (!assinfos[1].equalsIgnoreCase("null") && !assinfos[3].equalsIgnoreCase("null")){
						String res2AtomKey = resType + "-" + assinfos[3];
						Integer res2AtomTmp = assMtxRes2Atom.get(res2AtomKey);
						if (null == res2AtomTmp)
							res2AtomTmp = new Integer(0);
						res2AtomTmp++;
						
						System.out.println("R2A\t"+res2AtomKey+"\t"+res2AtomTmp);
						assMtxRes2Atom.put(res2AtomKey, res2AtomTmp);
						
						String atom2AtomKey = assinfos[1] + "-" + assinfos[3];
						Integer atom2AtomTmp = assMtxAtom2Atom.get(atom2AtomKey);
						if (null == atom2AtomTmp)
							atom2AtomTmp = new Integer(0);
						atom2AtomTmp++;
						
						System.out.println("A2A\t"+atom2AtomKey+"\t"+atom2AtomTmp);
						assMtxAtom2Atom.put(atom2AtomKey, atom2AtomTmp);
					}
				}
			}
		}
		
		System.out.println("Exception dis > 3.9 \n"+exceptionSb.toString());
		
		Object[] atom2AtomKeys = assMtxAtom2Atom.keySet().toArray();
		int maxCountAtom2Atom = -Integer.MAX_VALUE;
		int minCountAtom2Atom = Integer.MAX_VALUE;
		for (int i = 0; i < atom2AtomKeys.length; i++){
			int count = assMtxAtom2Atom.get(atom2AtomKeys[i]);
			
			if (count > maxCountAtom2Atom){
				maxCountAtom2Atom = count;
			}
			
			if (count < minCountAtom2Atom){
				minCountAtom2Atom = count;
			}
		}
		
		Object[] res2AtomKeys = assMtxRes2Atom.keySet().toArray();
		int maxCountRes2Atom = -Integer.MAX_VALUE;
		int minCountRes2Atom = Integer.MAX_VALUE;
		for (int i = 0; i < res2AtomKeys.length; i++){
			int count = assMtxRes2Atom.get(res2AtomKeys[i]);
			
			if (count > maxCountRes2Atom){
				maxCountRes2Atom = count;
			}
			
			if (count < minCountRes2Atom){
				minCountRes2Atom = count;
			}
		}
		
		try {
			FileWriter atom2AtomFw = new FileWriter(saveAssMtxBetweenProtAtomWithLigAtom);
			for (int i = 0; i < atom2AtomKeys.length; i++){
				int count = assMtxAtom2Atom.get(atom2AtomKeys[i]);
				
				atom2AtomFw.write(""+atom2AtomKeys[i]+"\t"+_NumFormat.formatDouble(1.0*(count-minCountAtom2Atom)/(maxCountAtom2Atom-minCountAtom2Atom), "#0.00000000")+"\n");
			}
			atom2AtomFw.close();
			
			FileWriter res2AtomFw = new FileWriter(saveAssMtxBetweenProtResWithLigAtom);
			for (int i = 0; i < res2AtomKeys.length; i++){
				int count = assMtxRes2Atom.get(res2AtomKeys[i]);
				
				res2AtomFw.write(""+res2AtomKeys[i]+"\t"+_NumFormat.formatDouble(1.0*(count-minCountRes2Atom)/(maxCountRes2Atom-minCountRes2Atom), "#0.00000000")+"\n");
			}
			res2AtomFw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return [0] = the minimized distance; [1] = the maximized distance
	 */
	public double[] calculateTheMinAndMaxDisBetweenProtWithLigInBioLip(){
		double minDis = Double.MAX_VALUE;
		double maxDis = -Double.MAX_VALUE;
		
		StringBuffer exceptionSb = new StringBuffer();
		Object[] ids = annotationInfoHm.keySet().toArray();
		for (int i = 0; i < ids.length; i++){
			
			String protname = (String)ids[i];
			String[] annInfos = getAnnotationInfo(protname);
			
			String protpdb = getProteinPDBFile(protname);
			if (null == protpdb){
				System.out.println("The "+(i+1)+"-th protein "+ids[i] + " is absent.");
				continue;
			}
			System.out.print("The "+(i+1)+"-th protein "+ids[i]);
			
			Protein prot = new Protein(protname, protpdb, true);
			for (int j = 0; j < annInfos.length; j++){
				String[] annItems = annInfos[j].split("\t");
				
				String ligname = annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6];
				String ligpath = this.getLigandPDBFile(ligname);
				Ligand lig = new Ligand("Null".getBytes(), ligpath);
				
				String[] lbs = annItems[8].split(" +");
				for (int k = 0; k < lbs.length; k++){
					int index = Integer.parseInt(lbs[k].substring(1)) - 1;
					
					Residue res = prot.get(index);
					double dis = res.distNearestOu(lig);
					if (dis < minDis){
						minDis = dis;
					}
					if (dis > maxDis){
						maxDis = dis;
					}
					
					System.out.print("dis = " + dis + "\tmaxDis = " + maxDis + "\tminDis = " + minDis + "\n");
					if (dis > 10){
						System.out.println(protname+" - "+ ligname);
						exceptionSb.append(protname+" - "+ ligname + "\t" + dis);
//						System.exit(-1);
					}
				}
			}
		}
		
		System.out.println("Distance > 10 Exception : \n" + exceptionSb.toString());
		
		double[] ans = new double[2];
		ans[0] = minDis;
		ans[1] = maxDis;
		
		return ans;
	}
	
	/**
	 * @description : statistic the average distance
	 * 					 between the center point of ligand with the ligand-binding site for each pocket of each ligand type.
	 * @return : key = ligand type; value = the average distance
	 * @throws Exception 
	 */
	public HashMap<String, Double> getAveDistBetweenLigCenterWithLBS() throws Exception{
		String AveDistBetweenLigWithLBS = ConfigUtil.getConfig("BIOLIP_AVERAGE_DISTANCE_BETWEEN_LIGAND_WITH_LBS");
		if (null == AveDistBetweenLigWithLBS){
			System.out.println("The configure file does not contain : BIOLIP_AVERAGE_DISTANCE_BETWEEN_LIGAND_WITH_LBS");
			_Log.dayRunLog("The configure file does not contain : BIOLIP_AVERAGE_DISTANCE_BETWEEN_LIGAND_WITH_LBS", new Date());
			System.exit(-1);
		}
		
		HashMap<String, Double> ans = new HashMap<String, Double>();
		if (new File(AveDistBetweenLigWithLBS).isFile()){
			BufferedReader br = new BufferedReader(new FileReader(AveDistBetweenLigWithLBS));
			String l = br.readLine();
			while (null != l){
				String[] lc = l.split("\t");
				
				ans.put(lc[0], Double.parseDouble(lc[1]));
				l = br.readLine();
			}
			br.close();
		}else{
			HashMap<String, Integer> count = new HashMap<String, Integer>();
			Object[] ids = annotationInfoHm.keySet().toArray();
			for (int i = 0; i < ids.length; i++){
				String protname = (String)ids[i];
				String[] annInfos = getAnnotationInfo(protname);
				String protpdb = getProteinPDBFile(protname);
				if (null == protpdb){
					System.out.println("The "+(i+1)+"-th protein "+ids[i] + " is absent.");
					continue;
				}
				System.out.println("The "+(i+1)+"-th protein "+ids[i]);
				
				Protein prot = new Protein(protname, protpdb, true);
				for (int j = 0; j < annInfos.length; j++){
					String[] annItems = annInfos[j].split("\t");
					String ligtype = annItems[4];
					
					String ligname = annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6];
					String ligpath = this.getLigandPDBFile(ligname);
					Ligand lig = new Ligand(ligtype.getBytes(), ligpath);
					Point3D ligCenterPos = lig.getCenterPoint3D();
					
					String[] lbs = annItems[8].split(" +");
					double[] diss = new double[lbs.length];
					for (int k = 0; k < lbs.length; k++){
						int index = Integer.parseInt(lbs[k].substring(1)) - 1;
						
						Residue res = prot.get(index);
						double dis = res.distOu(ligCenterPos);
						
						diss[k] = dis;
					}
					
					Double tmp = ans.get(ligtype);
					Integer tcount = count.get(ligtype);
					if (null == tmp){
						tmp = new Double(0.0);
						tcount = new Integer(0);
					}
					tmp += _Math.average(diss);
					tcount++;
					
					ans.put(ligtype, tmp);
					count.put(ligtype, tcount);
				}
			}
			
			FileWriter fw = new FileWriter(AveDistBetweenLigWithLBS);
			Object[] ligTypes = ans.keySet().toArray();
			for (int i = 0; i < ligTypes.length; i++){
				double tmp = ans.get(ligTypes[i]);
				int tcount = count.get(ligTypes[i]);
				tmp /= tcount; 
				
				ans.put((String)ligTypes[i], tmp);
				fw.write((String)ligTypes[i]+"\t"+_NumFormat.formatDouble(tmp, "#0.000000")+"\n");
			}
			fw.close();
		}
		
		return ans;
	}
	
	/**
	 * @description : 	1. The Ligand is centralized by the center point of the LBSs.
	 * 					2. calculating the distances between the centralized Ligand with LBSs.
	 * 					3. calculating the average standard dev of the corresponding distances for each ligand type.
	 * @return  key = ligType; value = the value
	 * @throws Exception
	 */
	public HashMap<String, Double> getAveStdDisBetweenLSBCenterCentralizedLigOverWithLBS() throws Exception{
		String stdInfoBetweenLigWithLBS = ConfigUtil.getConfig("BIOLIP_STD_DISTANCE_BETWEEN_CENTRALIZED_LIGAND_WITH_LBS");
		if (null == stdInfoBetweenLigWithLBS){
			System.out.println("The configure file does not contain : BIOLIP_STD_DISTANCE_BETWEEN_CENTRALIZED_LIGAND_WITH_LBS");
			_Log.dayRunLog("The configure file does not contain : BIOLIP_STD_DISTANCE_BETWEEN_CENTRALIZED_LIGAND_WITH_LBS", new Date());
			System.exit(-1);
		}
		
		HashMap<String, Double> ans = new HashMap<String, Double>();
		if (new File(stdInfoBetweenLigWithLBS).isFile()){
			BufferedReader br = new BufferedReader(new FileReader(stdInfoBetweenLigWithLBS));
			String l = br.readLine();
			while (null != l){
				String[] lc = l.split("\t");
				
				Double tmp = Double.parseDouble(lc[1]);
				ans.put(lc[0], tmp);
				l = br.readLine();
			}
			br.close();
		}else{
			HashMap<String, Integer> count = new HashMap<String, Integer>();
			Object[] ids = annotationInfoHm.keySet().toArray();
			for (int i = 0; i < ids.length; i++){
				String protname = (String)ids[i];
				String[] annInfos = getAnnotationInfo(protname);
				String protpdb = getProteinPDBFile(protname);
				if (null == protpdb){
					System.out.println("The "+(i+1)+"-th protein "+ids[i] + " is absent.");
					continue;
				}
				System.out.println("The "+(i+1)+"-th protein "+ids[i] + " is processing...");
				
				
				Protein prot = new Protein(protname, protpdb, true);
				for (int j = 0; j < annInfos.length; j++){
					String[] annItems = annInfos[j].split("\t");
					String ligtype = annItems[4];
					
					String ligname = annItems[0]+"_"+annItems[4]+"_"+annItems[5]+"_"+annItems[6];
					String ligpath = this.getLigandPDBFile(ligname);
					Ligand lig = new Ligand(ligtype.getBytes(), ligpath);
					
					String[] lbs = annItems[8].split(" +");
					Residue[] ress = new Residue[lbs.length];
					Point3D oGridCenterPos = new Point3D(0.0, 0.0, 0.0);
					for (int k = 0; k < lbs.length; k++){
						int index = Integer.parseInt(lbs[k].substring(1)) - 1;
						Residue res = prot.get(index);
						ress[k] = res;
						
						oGridCenterPos.add(res.getCaCoordinate());
					}
					oGridCenterPos.normalize(ress.length);
					
					// centralize to oGridCenterPos
					lig.centralize(oGridCenterPos);
					double[] diss = new double[lbs.length];
					for (int k = 0; k < lbs.length; k++){
						diss[k] = ress[k].distNearestOu(lig);
					}
					
					Double tmp = ans.get(ligtype);
					Integer tcount = count.get(ligtype);
					if (null == tmp){
						tmp = new Double(0.0);
						tcount = new Integer(0);
					}
					tmp += _Math.standart_deviation(diss);
					tcount++;
					
					ans.put(ligtype, tmp);
					count.put(ligtype, tcount);
				}
			}
			
			FileWriter fw = new FileWriter(stdInfoBetweenLigWithLBS);
			Object[] ligTypes = ans.keySet().toArray();
			for (int i = 0; i < ligTypes.length; i++){
				double tmp = ans.get(ligTypes[i]);
				int tcount = count.get(ligTypes[i]);
				
				if (((String)ligTypes[i]).equalsIgnoreCase("ATP")){
					System.out.println((String)ligTypes[i]+"\t"+_NumFormat.formatDouble(tmp, "#0.000000")
							+"\n tcount = " + tcount);
				}
				
				tmp /= tcount; 
				ans.put((String)ligTypes[i], tmp);
				fw.write((String)ligTypes[i]+"\t"+_NumFormat.formatDouble(tmp, "#0.000000")+"\n");
			}
			fw.close();
		}
		
		return ans;
	}
}
