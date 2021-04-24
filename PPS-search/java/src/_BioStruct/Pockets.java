package _BioStruct;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import _util._NumFormat;
import _util._Str;

public class Pockets {
	private String protname;
	private String ligtype;
	private String proteinchainPDB;
	private String corrLigandsPDB;
	private double labdis;
	
	private Protein prot;
	private Ligands ligs;
	private Vector<Vector<String>> pocAbsInfo;
	private Vector<Vector<Residue>> pockets;
	private Vector<Double> pocketsMaxDisBetweenTwoSelfAtoms;
	private Vector<Double> pocketsMaxMinDisBetweenTwoSelfAtoms;
	private Vector<Ligand> used4PocLigs;
	
	// proteinchainPDB : the three dimension single protein chain PDB 
	// corrSingleLigandPDB : the single/multiple corresponding ligands PDB
	// LAB_DIS : judge the label distance between the protein and ligand pdb
	public Pockets(String protname, String ligtype, String proteinchainPDB,
			String corrLigandsPDB, double labdis){
		this.protname = protname;
		this.ligtype = ligtype;
		this.proteinchainPDB = proteinchainPDB;
		this.corrLigandsPDB = corrLigandsPDB;
		this.labdis = labdis;
		
		used4PocLigs = new Vector<Ligand>();
		prot = new Protein(protname, proteinchainPDB, true);
		matchPocket();
	}
	
	public Pockets(String protname, String ligtype, Protein prot,
			String corrLigandsPDB, double labdis){
		this.protname = protname;
		this.ligtype = ligtype;
		this.corrLigandsPDB = corrLigandsPDB;
		this.labdis = labdis;
		this.prot = prot;
		
		used4PocLigs = new Vector<Ligand>();
		matchPocket();
	}
	
	public Pockets(String protname, String ligtype, Protein prot,
			Ligands ligs, double labdis){
		this.protname = protname;
		this.ligtype = ligtype;
		this.ligs = ligs;
		this.labdis = labdis;
		this.prot = prot;
		
		used4PocLigs = new Vector<Ligand>();
		matchPocket();
	}
	
	public Ligands getLigands(){
		return this.ligs;
	}
	
	public Protein getProtein(){
		return this.prot;
	}
	
	public Double getAveDisBetweenTwoPockets(){
		if (pockets.size() > 1){
			double ans = 0.0;
			int num = 0;
			for (int i = 0; i < pockets.size(); i++){
				for (int j = i+1; j < pockets.size(); j++){
					double dis = getMinDisBetweenTwoPocs(pockets.get(i), pockets.get(j));
					ans += dis;
					
					num++;
				}
			}
			
			ans /= 1.0*num;
			
			return ans;
		}
		
		return null;
	}
	
	public Double getMinDisBetweenTwoPockets(){
		if (pockets.size() > 1){
			double ans = Double.MAX_VALUE;
			for (int i = 0; i < pockets.size(); i++){
				for (int j = i+1; j < pockets.size(); j++){
					double dis = getMinDisBetweenTwoPocs(pockets.get(i), pockets.get(j));
					if (dis < ans){
						ans = dis;
					}
				}
			}
			
			return ans;
		}
		
		return null;
	}
	
	public static double getMinDisBetweenTwoPocs(Vector<Residue> poc1, Vector<Residue> poc2){
		double ans = Double.MAX_VALUE;
		for (int i = 0; i < poc1.size(); i++){
			for (int j = 0; j < poc2.size(); j++){
				double dis = poc1.get(i).distNearestOu(poc2.get(j));
				if (dis < ans){
					ans = dis;
				}
			}
		}
		
		return ans;
	}
	
	/**
	 * @return max(min_{i}(dis(res_{i}, res_{j})_{j=1,j!=i}^{N})) for each pockets.
	 */
	public Vector<Double> getPocketsMaxMinDisBetweenSelfAtoms(){
		if (null == pocketsMaxMinDisBetweenTwoSelfAtoms){
			pocketsMaxMinDisBetweenTwoSelfAtoms = new Vector<Double>();
			for (int i = 0; i < pockets.size(); i++){
				Vector<Residue> poc = this.pockets.get(i);
				double maxMinDisBetweenTwoRess = 0.0;
				for (int j = 0; j < poc.size(); j++){
					double minDisBetweenTwoRess = Double.MAX_VALUE;
					for (int k = 0; k < poc.size(); k++){
						if (j ==k) continue;
						double dis = poc.get(j).distNearestOu(poc.get(k));
						if (dis < minDisBetweenTwoRess){
							minDisBetweenTwoRess = dis;
						}
					}
					
					if (minDisBetweenTwoRess > maxMinDisBetweenTwoRess){
						maxMinDisBetweenTwoRess = minDisBetweenTwoRess;
					}
				}
				
				pocketsMaxMinDisBetweenTwoSelfAtoms.add(maxMinDisBetweenTwoRess);
			}
		}
		
		return pocketsMaxMinDisBetweenTwoSelfAtoms;
	}
	
	/**
	 * @return the max distance between any two atoms of each pocket
	 */
	public Vector<Double> getPocketsMaxDisBetweenTwoSelfAtoms(){
		if (null == pocketsMaxDisBetweenTwoSelfAtoms){
			pocketsMaxDisBetweenTwoSelfAtoms = new Vector<Double>();
			for (int i = 0; i < pockets.size(); i++){
				Vector<Residue> poc = this.pockets.get(i);
				double maxDisBetweenTwoRess = 0.0;
				for (int j = 0; j < poc.size(); j++){
					for (int k = 0; k < poc.size(); k++){
						double dis = poc.get(j).distNearestOu(poc.get(k));
						if (dis > maxDisBetweenTwoRess){
							maxDisBetweenTwoRess = dis;
						}
					}
				}
				
				pocketsMaxDisBetweenTwoSelfAtoms.add(maxDisBetweenTwoRess);
			}
		}
		
		return pocketsMaxDisBetweenTwoSelfAtoms;
	}
	
	public Pockets(Vector<Vector<String>> pocAbsInfo, Vector<Vector<Residue>> pockets,
			String protname, Protein prot){
		this.pockets = pockets;
		this.pocAbsInfo = pocAbsInfo;
		this.protname = protname;
		this.prot = prot;
	}
	
	public Vector<Vector<Residue>> getPockets() {
		return pockets;
	}

	public Vector<Vector<String>> getPocAbsInfo() {
		return pocAbsInfo;
	}
	
	public int size(){
		return pockets.size();
	}
	
	public Vector<Residue> get(int index){
		return pockets.get(index);
	}
	
	public Vector<String> getAbsInfo(int index){
		return pocAbsInfo.get(index);
	}
	
	public String getProteinName() {
		return protname;
	}

	public String getLigandType() {
		return ligtype;
	}
	
	// per ligand constitute one pocket
	private void matchPocket(){
		pocAbsInfo = new Vector<Vector<String>>();
		pockets = new Vector<Vector<Residue>>();
		
		if (null == prot)
			prot = new Protein(protname, proteinchainPDB, true);
		if (null == ligs)
			ligs = new Ligands(ligtype, corrLigandsPDB, labdis);
		for (int i = 0; i < ligs.size(); i++){
			Vector<String> absInfo = new Vector<String>();
			Vector<Residue> pocket = new Vector<Residue>();
			for (int j = 0; j < prot.size(); j++){
				if (prot.get(j).distNearestOu(ligs.get(i)) <= labdis){
					absInfo.add((""+prot.getAA(j))+(j+1)); // the index start from 1.
					pocket.add(prot.get(j));
				}
			}
			
			if (pocket.size() > 0){
				pocAbsInfo.add(absInfo);
				pockets.add(pocket);
				
				used4PocLigs.add(ligs.get(i));
			}
		}
	}
	
	public Vector<Ligand> getUsed4PocLigs(){
		return used4PocLigs;
	}
	
	public void save(String partSavePath)throws Exception{
		for (int i = 0; i < pockets.size(); i++){
			FileWriter fw = new FileWriter(partSavePath+"_BS"+(i+1)+".pdb");
			saveOnePocket(fw, pockets.get(i), pocAbsInfo.get(i));
			fw.close();
		}
	}
	
	private void saveOnePocket(FileWriter fw, Vector<Residue> pocket, Vector<String> absInfo)throws Exception{
		if (pocket.size() != absInfo.size()){
			System.out.println("Pockets.saveOnePocket: pocket.size() != absInfo.size()"+pocket.size()+"\t"+ absInfo.size());
			System.exit(-1);
		}
		
		// ascent sort indexes
		int[] indexes = new int[absInfo.size()];
		for (int i = 0; i < pocket.size(); i++)
			indexes[i] = i;
		for (int i = 1; i < pocket.size(); i++){
			for (int j = 0; j < i; j++){
				int pos_iRes = Integer.parseInt(absInfo.get(indexes[i]).substring(1).trim());
				int pos_jRes = Integer.parseInt(absInfo.get(indexes[j]).substring(1).trim());
				if (pos_iRes < pos_jRes){
					int tmp = indexes[i];
					indexes[i] = indexes[j];
					indexes[j] = tmp;
				}
			}
		}
		
		int atomPos = 1;
		for(int resPos = 0; resPos < pocket.size(); resPos++){
			Residue res = pocket.get(indexes[resPos]);
			char aa = absInfo.get(indexes[resPos]).charAt(0);
			int resIndex = res.getPosInProt();
			if (-1 == resIndex)
				resIndex = resPos+1;
			
			Vector<Integer> resAtomInds = res.getAtomsPosInProt();
			Vector<Point3D> resInfo = res.getPoints();
			Vector<String> resAtomInfo = res.getAtomtypes();
			for (int j = 0; j < resInfo.size(); j++){
				String atom_name = resAtomInfo.get(j);
				int _atomPos = atomPos;
				if (null != resAtomInds)
					_atomPos = resAtomInds.get(j);
				
				fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+_atomPos, 5));
				fw.write("  "+_Str.replenishEndWithSpace(atom_name, 3)+" ");
				
				// write three word amino acid
				String aminoacid = oneWordToThree(aa);
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
				
				atomPos++;
			}
		}
	}
	
	public void saveApocFormat(String savePath)throws Exception{
		// save protein structure
		if (null == prot)
			prot = new Protein(protname, proteinchainPDB, true);
		prot.save(savePath);
		
		// save pockets
		FileWriter fw = new FileWriter(savePath, true);
		for (int i = 0; i < pockets.size(); i++){
			String pocname = protname + "_" + _Str.replenishHeadWithChar("" + (i+1), 4, '0');
			fw.write("PKT         "+_Str.replenishHeadWithSpace(""+pockets.get(i).size(), 3)+"     1000       "+pocname+"\n");
			saveOnePocket(fw, pockets.get(i), pocAbsInfo.get(i));
			fw.write("TER\n");
		}
		
		fw.close();
	}
	
	public boolean saveApocFormat(String savePath, int minResNum)throws Exception{
		// save protein structure
		if (null == prot)
			prot = new Protein(protname, proteinchainPDB, true);
		prot.save(savePath);
		
		// save pockets
		boolean isContainPoc = false;
		FileWriter fw = new FileWriter(savePath, true);
		for (int i = 0; i < pockets.size(); i++){
			if (minResNum > pockets.get(i).size()){
				continue;
			}
			
			System.out.println("pockets.get(i).size() = "+pockets.get(i).size());
			isContainPoc = true;
			String pocname = protname + "_" + _Str.replenishHeadWithChar("" + (i+1), 4, '0');
			fw.write("PKT         "+_Str.replenishHeadWithSpace(""+pockets.get(i).size(), 3)+"     1000       "+pocname+"\n");
			saveOnePocket(fw, pockets.get(i), pocAbsInfo.get(i));
			fw.write("TER\n");
		}
		fw.close();
		
		if (!isContainPoc){
			new File(savePath).delete();
		}
		
		return isContainPoc;
	}
	
	private String oneWordToThree(char aa){
		if (aa=='A' || aa=='a') return "ALA";
		if (aa=='C' || aa=='c') return "CYS";
		if (aa=='D' || aa=='d') return "ASP";
		if (aa=='E' || aa=='e') return "GLU";
		if (aa=='F' || aa=='f') return "PHE";
		if (aa=='G' || aa=='g') return "GLY";
		if (aa=='H' || aa=='h') return "HIS";
		if (aa=='I' || aa=='i') return "ILE";
		if (aa=='K' || aa=='k') return "LYS";
		if (aa=='L' || aa=='l') return "LEU";
		if (aa=='M' || aa=='m') return "MET";
		if (aa=='N' || aa=='n') return "ASN";
		if (aa=='P' || aa=='p') return "PRO";
		if (aa=='Q' || aa=='q') return "GLN";
		if (aa=='R' || aa=='r') return "ARG";
		if (aa=='S' || aa=='s') return "SER";
		if (aa=='T' || aa=='t') return "THR";
		if (aa=='V' || aa=='v') return "VAL";
		if (aa=='W' || aa=='w') return "TRP";
		if (aa=='Y' || aa=='y') return "TYR";
		
		return "ALA";
	}
	
	public static void main(String[] args)throws Exception {
		
		
		System.out.println("HAVE A GOOD DAY!!!");
	}

}
