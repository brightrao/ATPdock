package _BioStruct;

import java.io.FileWriter;
import java.util.Date;
import java.util.Vector;

import _util.AminoAcid;
import _util._Log;
import _util._Math;
import _util._NumFormat;
import _util._Str;
import _util._Vector;

public class SSFL {
	// the description on "Protein-ligand binding site recognition using complementary 
	// binding-specific substructure comparison and sequence profile alignment"
	// SSFL means the structures of a Sub-Sequence from the First binding residue to the Last binding residue.
	
	// input 
	private Protein prot;
	private Vector<String> pocAbsInfo; // each pocket info which you can get from Pockets class
	                                   // "E1, "C12", D15, ...", 'AA'+'The index (start from 1)'

	// output
	private String seq;
	private Vector<Residue> ssfl;
	private Vector<Integer> newBsitesInds;	// index start from 0 on ssfl
	
	public SSFL(String proteinchainPDB, Vector<String> pocAbsInfo){
		this.pocAbsInfo = pocAbsInfo;
		prot = new Protein(null, proteinchainPDB, true);
		
		run();
	}
	
	public SSFL(Protein prot, Vector<String> pocAbsInfo){
		this.pocAbsInfo = pocAbsInfo;
		this.prot = prot;
		
		run();
	} 
	
	public SSFL(Protein prot, String[] _pocAbsInfo){
		this.prot = prot;
		Vector<String> pocAbsInfoVec = new Vector<String>();
		for (int i = 0; i < _pocAbsInfo.length; i++)
			pocAbsInfoVec.add(_pocAbsInfo[i]);
		
		this.pocAbsInfo = pocAbsInfoVec;
		
		run();
	}
	
	public String getSeq() {
		return seq;
	}

	public char getAA(int index){
		return seq.charAt(index);
	}
	
	public int size(){
		return ssfl.size();
	}
	
	public Vector<Residue> getSSFL() {
		return ssfl;
	}
	
	public Residue get(int index){
		return ssfl.get(index);
	}
	
	public Vector<Integer> getNewBsitesIndsInSSFL(){
		return newBsitesInds;
	}
	
	public double[] getCenterXYZ(){
		int protein_atom_num = 0;
		double x_sum = 0.0;
		double y_sum = 0.0;
		double z_sum = 0.0;
		for (int i = 0; i < ssfl.size(); i++){
			Residue residue = ssfl.get(i);
			Vector<Point3D> atoms= residue.getPoints();
			int atom_num = atoms.size();
			for (int j = 0; j < atom_num; j++){
				x_sum += atoms.get(j).getX();
				y_sum += atoms.get(j).getY();
				z_sum += atoms.get(j).getZ();
			}
			protein_atom_num += atom_num;
		}
		
		double[] ans = new double[3];
		ans[0] = x_sum / protein_atom_num;
		ans[1] = y_sum / protein_atom_num;
		ans[2] = z_sum / protein_atom_num;
		
		return ans;
	}
	
	public void save(String savePath){
		try{
			FileWriter fw = new FileWriter(savePath);
			int atomPos = 1;
			for(int resPos = 0; resPos < ssfl.size(); resPos++){
				Residue res = ssfl.get(resPos);
				char aa = seq.charAt(resPos);
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
					fw.write("  1.00 00.00           ");
					fw.write(atom_name.charAt(0)+"  \n");
					
					atomPos++;
				}
			}
			fw.write("TER\n");
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
			_Log.dayRunLog("SSFL save function cannot work normally.", new Date());
			_Log.dayRunLog(e.getMessage(), new Date());
		}
		
	}
	
	public void saveCa(String savePath)throws Exception{
		FileWriter fw = new FileWriter(savePath);
		int atomPos = 1;
		for(int resPos = 0; resPos < ssfl.size(); resPos++){
			Residue res = ssfl.get(resPos);
			char aa = seq.charAt(resPos);
			int resIndex = res.getPosInProt();
			if (-1 == resIndex)
				resIndex = resPos+1;
			
			Vector<Integer> resAtomInds = res.getAtomsPosInProt();
			Vector<Point3D> resInfo = res.getPoints();
			for (int j = 0; j < resInfo.size(); j++){
				if (j != res.getCaIndex())
					continue;
				int _atomPos = atomPos;
				if (null != resAtomInds)
					_atomPos = resAtomInds.get(j);
				
				fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+_atomPos, 5));
				fw.write("  CA  ");
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
				fw.write("  1.00 00.00           ");
				if (j == res.getCaIndex())
					fw.write("C  \n");
				else fw.write("   \n");
				
				atomPos++;
			}
		}
		fw.close();
	}
	
	private void run(){
		ssfl = new Vector<Residue>();
		newBsitesInds = new Vector<Integer>();
		
		int[] brPOSs = new int[pocAbsInfo.size()];
		for (int i = 0; i < brPOSs.length; i++){
			brPOSs[i] = Integer.parseInt(pocAbsInfo.get(i).substring(1))-1;
		}
		brPOSs = _Math.ascendSort(brPOSs);
		
		int firstBRpos = brPOSs[0];
		int lastBRpos = brPOSs[brPOSs.length-1];
		StringBuffer _seq = new StringBuffer();
		for (int i = firstBRpos, j = 0; i <= lastBRpos && i < prot.size(); i++, j++){
			_seq.append(prot.getAA(i));
			ssfl.add(prot.get(i));
			
			if (_Vector.isContain(brPOSs, i)){
				newBsitesInds.add(j);
			}
		}
		seq = _seq.toString();
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
	
	public static void main(String[] args){
		
		System.out.println(_Str.replenishHeadWithSpace(
				_NumFormat.formatDouble(-121.123, "#0.000"), 8));
	}
}
