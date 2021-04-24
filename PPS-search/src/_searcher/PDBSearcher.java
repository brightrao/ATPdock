package _searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import _util._File;

public class PDBSearcher {
	private String pdbDBfolder;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		HashMap<String, String> proHm = _File.loadFasta("/nfs/amino-home/hujunum/StructPNB/seqfa/NUC5tr/GDPseq.fasta");
//		Object[] ids = proHm.keySet().toArray();
//		FileWriter fw = new FileWriter("/home/hujunum/pro_id.list", true);
//		for (int i = 0; i < ids.length; i++){
//			String name = (String)ids[i];
//			fw.write(name.substring(0, 4)+"\n");
//			System.out.println(name.substring(0, 4));
//		}
//		fw.close();
		
		PDBSearcher pdbSor = new PDBSearcher("/home/hujunum/library/PDB");
		String info = pdbSor.searchMulChains("/nfs/amino-home/hujunum/StructPNB/seqfa/NUC5tr/ATPseq.fasta", 
				"/home/hujunum/library/PDBchain");
		
		System.out.println(info);
		
		System.out.println("HAVE A GOOD DAY!");
	}
	
	public PDBSearcher(String pdbDBfolder){
		this.pdbDBfolder = pdbDBfolder;
		
		if (!new File(pdbDBfolder).exists()){
			System.out.println("Sorry! the folder \"" + pdbDBfolder + "\" is not existed.");
		}
	}
	
	public String searchSingleChain(String pdb4bitsID, char chain, String savefolder)throws Exception{
		if (!new File(pdbDBfolder).exists()){
			System.out.println("Sorry! the folder \"" + pdbDBfolder + "\" is not existed.");
			return "Sorry! the folder \"" + pdbDBfolder + "\" is not existed.";
		}
		
		if (!new File(savefolder).exists()){
			new File(savefolder).mkdirs();
		}
		
		if (!new File(pdbDBfolder+System.getProperty("file.separator")+pdb4bitsID.toLowerCase()+".pdb").isFile()){
			System.out.println("Sorry! the folder \"" + 
					pdbDBfolder+System.getProperty("file.separator")+pdb4bitsID.toLowerCase()+".pdb" + "\" is not existed.");
			return "Sorry! the folder \"" + pdbDBfolder + "\" is not existed.";
		}
		
		extractPC3D(pdbDBfolder+System.getProperty("file.separator")+pdb4bitsID.toLowerCase()+".pdb", 
				chain, 
				savefolder+System.getProperty("file.separator")+pdb4bitsID+chain+".pdb");
		
		return "Search OK";
	}
	
	public boolean isExist(String pdbID){
		if (!new File(this.pdbDBfolder+System.getProperty("file.separator")+pdbID.substring(0,4)+".pdb").isFile()){
			return false;
		}
		
		return true;
	}
	
	// faPath content: >1xefA\n......
	// return not found protein chains
	public String searchMulChains(String faPath, String savefolder)throws Exception{
		StringBuffer ans = new StringBuffer();
		
		if (!new File(savefolder).exists()){
			new File(savefolder).mkdirs();
		}
		
		HashMap<String, String> proHm = _File.loadFasta(faPath);
		Object[] names = proHm.keySet().toArray();
		for (int i = 0; i < names.length; i++){
			String name = (String)names[i];
			System.out.println("Searching " + name + "'s pdb ...");
			
			String tmp = searchSingleChain(name.substring(0, 4), name.toUpperCase().charAt(4), savefolder);
			if (tmp.startsWith("Sorry!"))
				ans.append(name+"\n");
		}
		
		return ans.toString();
	}
	
	// PC means Protein Chain
	// 3D means three dimensional information
	private static void extractPC3D(String pdbFilePath, char chain, String saveProteinXYZFilePath) throws Exception {
		FileWriter fw = new FileWriter(saveProteinXYZFilePath);
		
		BufferedReader br = new  BufferedReader(new FileReader(pdbFilePath));
		String line = br.readLine();
		
		boolean isStartChain = false;
		int pre_atom_index = -99999;
		while (null != line){
			line = line.toUpperCase();
			if (line.length() > 20){
				String threeWordAA = line.substring(17, 20);
				if (line.startsWith("ATOM") && 
					AMINOACIDS.contains(threeWordAA) && 
					line.charAt(21) == chain){
					int now_atom_index = Integer.parseInt(line.substring(5, 12).trim());
					if (pre_atom_index == -99999 ||
							now_atom_index - pre_atom_index > 0){
						fw.write(line+"\n");
						pre_atom_index = now_atom_index;
						
						isStartChain = true;
					}
				}
				
				if (isStartChain){
					if (line.startsWith("ATOM") && 
							AMINOACIDS.contains(threeWordAA) && 
							line.charAt(21) != chain){
						break;
					}
				}
			}
			line = br.readLine();
		}
		br.close();
		fw.close();
	}
	
	private static final String AMINOACIDS = "ALA "
			+ "CYS "
			+ "ASP "
			+ "GLU "
			+ "PHE "
			+ "GLY "
			+ "HIS "
			+ "ILE "
			+ "LYS "
			+ "LEU "
			+ "MET "
			+ "ASN "
			+ "PRO "
			+ "GLN "
			+ "ARG "
			+ "SER "
			+ "THR "
			+ "VAL "
			+ "TRP "
			+ "TYR";

}
