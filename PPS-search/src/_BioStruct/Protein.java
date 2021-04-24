package _BioStruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import _util.AminoAcid;
import _util.TMscore;
import _util._File;
import _util._Log;
import _util._NumFormat;
import _util._Sort;
import _util._Str;


public class Protein {
	private String name;
	private String seq;
	private Vector<Residue> residues;
	
	private boolean[] isSurfaceInRaw = null; // Tag the surface residue;
	private int[] isSurfaceInDetail = null; // Tag the surface residue;
	private char[] ss_types = null; 
	
	public static final String SS_TYPEs = "NCHTS";
	
	public Protein(String name, String PDBFilePath, boolean isLoadHatom){
		this.name = name;
		loadProteinInfoFromPDB(PDBFilePath, true, isLoadHatom);
	}
	
	/**
	 * @param name
	 * @param PDBFilePath
	 * @param careTERorNot true means read the file terminal to TER or the end, false means only the end.
	 */
	public Protein(String name, String PDBFilePath, boolean careTERorNot, boolean isLoadHatom){
		this.name = name;
		loadProteinInfoFromPDB(PDBFilePath, careTERorNot, isLoadHatom);
	}
	
	public Protein(String name, String seq, Vector<Residue> residues){
		this.name = name;
		this.seq = seq;
		this.residues = residues;
	}
	
	public Protein(Protein prot, boolean isMemCopy){
		this.name = prot.name;
		this.seq = prot.seq;
		
		if (isMemCopy){
			residues = new Vector<Residue>();
			for (int i = 0; i < prot.getResidues().size(); i++){
				Residue oRes = prot.get(i);
				Residue nRes = new Residue(oRes, true);
				
				residues.add(nRes);
			}
		}else{
			this.residues = prot.residues;
		}
	}
	
	public void changePOSthResidue(int pos, Residue res){
		this.residues.set(pos, res);
	}
	
	private void loadProteinInfoFromPDB(String PDBFilePath, boolean careTERorNot, boolean isLoadHatom){
		/**
		 COLUMNS        DATA  TYPE    FIELD        DEFINITION
-------------------------------------------------------------------------------------
 1 -  6        Record name   "ATOM  "
 7 - 11        Integer       serial       Atom  serial number.
13 - 16        Atom          name         Atom name.
17             Character     altLoc       Alternate location indicator.
18 - 20        Residue name  resName      Residue name.
22             Character     chainID      Chain identifier.
23 - 26        Integer       resSeq       Residue sequence number.
27             AChar         iCode        Code for insertion of residues.
31 - 38        Real(8.3)     x            Orthogonal coordinates for X in Angstroms.
39 - 46        Real(8.3)     y            Orthogonal coordinates for Y in Angstroms.
47 - 54        Real(8.3)     z            Orthogonal coordinates for Z in Angstroms.
55 - 60        Real(6.2)     occupancy    Occupancy.
61 - 66        Real(6.2)     tempFactor   Temperature  factor.
77 - 78        LString(2)    element      Element symbol, right-justified.
79 - 80        LString(2)    charge       Charge  on the atom.
		 */
		
		
		residues = new Vector<Residue>();
		StringBuffer seq = new StringBuffer();
		
		try{
			BufferedReader br = new  BufferedReader(new FileReader(PDBFilePath));
			int pre_index_seq = Integer.MIN_VALUE;
			String line = br.readLine();
			
			while (null != line){
				if (careTERorNot && line.startsWith("TER"))
					break;
				
				if (line.startsWith("ATOM") 
						&& line.length() < 16) {
					System.out.println(line);
				}
				
				if (line.startsWith("ATOM") && 
						line.substring(13, 16).equals("CA ")){
					int now_index = Integer.parseInt(line.substring(22, 26).trim());
					
					if (now_index != pre_index_seq){
						pre_index_seq = now_index;
						
						char aa = threeWordToOne(line.substring(17, 20));
						seq.append(aa);
					}
				}
				
				line = br.readLine();
			}
			
			br.close();
			this.seq = seq.toString();
			
			Vector<Integer> atomIndex = new Vector<Integer>();
			Vector<String> atomtypes = new Vector<String>();
			Vector<Point3D> points = new Vector<Point3D>();
			int ca_pos_in_res = 0;
			int cb_pos_in_res = 0;
			
			int pre_index = Integer.MIN_VALUE;
			br = new  BufferedReader(new FileReader(PDBFilePath));
			line = br.readLine();
			String lastLine = line; 
			boolean isFirstATOM = true;
			boolean isContainCA = false;
			int tmp_pos = 0; // record the CA/CB index in the atom
			int res_pos = 1; // record the residue index in the protein
			int atom_pos = 1;	// record the atom index in the protein
			while (null != line){
				if (careTERorNot && line.startsWith("TER"))
					break;
				
				if (line.startsWith("ATOM")){
					
					if (!isLoadHatom 
							&& line.length() >= 78
							&& "H".equalsIgnoreCase(line.substring(76, 78).trim())){
						lastLine = line;
						line = br.readLine();
						continue;
					}
					
					double x = Double.parseDouble(line.substring(30, 38));
					double y = Double.parseDouble(line.substring(38, 46));
					double z = Double.parseDouble(line.substring(46, 54));
					Point3D point = new Point3D(x, y, z);
					String atomtype = line.substring(13, 16).trim();
					
					int now_index = Integer.parseInt(line.substring(22, 26).trim());
					if (now_index != pre_index){
						if (isContainCA){
							if (pre_index != Integer.MIN_VALUE){
								Residue tmpResidue = new Residue(points, atomtypes, ca_pos_in_res, cb_pos_in_res, res_pos, atomIndex, pre_index);
								residues.add(tmpResidue);
								res_pos++;
							}
							points = new Vector<Point3D>();
							points.add(point);
							atomtypes = new Vector<String>();
							atomtypes.add(atomtype);
							atomIndex = new Vector<Integer>();
							atomIndex.add(atom_pos);
							
							tmp_pos = 0;
							isContainCA = false;
							// add 20161215
							if (line.substring(13, 16).equals("CA ")){
								isContainCA = true;
							}
						}else{
							if (isFirstATOM){
								points.add(point);
								atomtypes.add(atomtype);
								atomIndex.add(atom_pos);
							}
						}
						pre_index = now_index;
					}else{
						points.add(point);
						atomtypes.add(atomtype);
						atomIndex.add(atom_pos);
					}
					
					if (line.substring(13, 16).equals("CA ")){
						isContainCA = true;
						ca_pos_in_res = tmp_pos;
						
						if (isFirstATOM){
							pre_index = now_index;
						} 
					}
					
					if (line.substring(13, 16).equals("CB ")){
						cb_pos_in_res = tmp_pos;
					}
					
					isFirstATOM = false;
					
					tmp_pos++;
					atom_pos++;
				}
				
				lastLine = line;
				line = br.readLine();
			}
			
			if (isContainCA || (lastLine.length() >= 16 && lastLine.substring(13, 16).equals("CA "))){
				Residue tmpResidue = new Residue(points, atomtypes, ca_pos_in_res, ca_pos_in_res, res_pos, atomIndex, pre_index);
				residues.add(tmpResidue);
			}

//			if (residues.size() != seq.length()){
//				System.out.println("Protein says:"+name+" residues.size() != seq.length() "
//						+ residues.size()+"!="+ seq.length());
//				System.exit(-1);
//			}
			
			br.close();
		}catch (Exception e){
			System.out.println("Protein says:"+name+" cannot load!");
			e.printStackTrace();
		}
	}
	
	private char threeWordToOne(String acid){
		acid = acid.toUpperCase();

		if (acid.equals("ALA"))	return 'A';
		if (acid.equals("CYS"))	return 'C';
		if (acid.equals("ASP"))	return 'D';
		if (acid.equals("GLU"))	return 'E';
		if (acid.equals("PHE"))	return 'F';
		if (acid.equals("GLY"))	return 'G';
		if (acid.equals("HIS"))	return 'H';
		if (acid.equals("ILE"))	return 'I';
		if (acid.equals("LYS"))	return 'K';
		if (acid.equals("LEU"))	return 'L';
		if (acid.equals("MET"))	return 'M';
		if (acid.equals("ASN"))	return 'N';
		if (acid.equals("PRO"))	return 'P';
		if (acid.equals("GLN"))	return 'Q';
		if (acid.equals("ARG"))	return 'R';
		if (acid.equals("SER"))	return 'S';
		if (acid.equals("THR"))	return 'T';
		if (acid.equals("VAL"))	return 'V';
		if (acid.equals("TRP"))	return 'W';
		if (acid.equals("TYR"))	return 'Y';
		
		return 'X';
	}
	
	public String getName() {
		return name;
	}

	public String getSeq() {
		return seq.trim();
	}

	public Vector<Residue> getResidues() {
		return residues;
	}
	
	public Residue get(int index){
		return residues.get(index);
	}
	
	public char getAA(int index){
		if (index < 0 || index >= seq.length())
			return 'A';
		return seq.charAt(index);
	}
	
	public double[][] getCaXYZs(){
		double[][] ans = new double[residues.size()][3];
		for (int i = 0; i < ans.length; i++){
			double[] ca = residues.get(i).getCaCoordinate().toArray();
			ans[i] = ca;
		}
		
		return ans;
	}
	
	public double[] getCenterXYZ(){
		int protein_atom_num = 0;
		double x_sum = 0.0;
		double y_sum = 0.0;
		double z_sum = 0.0;
		for (int i = 0; i < residues.size(); i++){
			Residue residue = residues.get(i);
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
	
	public double[] getCaCenterXYZ(){
		int protein_ca_num = 0;
		double x_sum = 0.0;
		double y_sum = 0.0;
		double z_sum = 0.0;
		for (int i = 0; i < residues.size(); i++){
			Residue residue = residues.get(i);
			x_sum += residue.getCaCoordinate().getX();
			y_sum += residue.getCaCoordinate().getY();
			z_sum += residue.getCaCoordinate().getZ();
			protein_ca_num++;
		}
		
		double[] ans = new double[3];
		ans[0] = x_sum / protein_ca_num;
		ans[1] = y_sum / protein_ca_num;
		ans[2] = z_sum / protein_ca_num;
		
		return ans;
	}
	
	public Point3D getCenterPt(){
		double[] xyz = getCenterXYZ();
		return new Point3D(xyz[0], xyz[1], xyz[2]);
	}
	
	public Point3D getCaCenterPt(){
		double[] xyz = getCaCenterXYZ();
		return new Point3D(xyz[0], xyz[1], xyz[2]);
	}
	
	public int size(){
		return residues.size();
	}
	
	public void save(String savePath){
		try{
			FileWriter fw = new FileWriter(savePath);
			// save protein structure
			Protein prot = this;
			int atomPos = 1;
			for (int resPos= 0; resPos < prot.size(); resPos++){
				Residue res = prot.get(resPos);
				char aa = prot.getAA(resPos);
				int resIndex = res.getPosInProt();
				if (-1 == resIndex)
					resIndex = resPos+1;
				
				Vector<Point3D> resInfo = res.getPoints();
				Vector<String> resAtomInfo = res.getAtomtypes();
				for (int j = 0; j < resInfo.size(); j++){
					String atom_name = resAtomInfo.get(j);
					
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// save protein structure
		Protein prot = this;
		int atomPos = 1;
		for (int resPos = 0; resPos < prot.size(); resPos++) {
			Residue res = prot.get(resPos);
			char aa = prot.getAA(resPos);
			int resIndex = res.getPosInProt();
			if (-1 == resIndex)
				resIndex = resPos + 1;

			Vector<Point3D> resInfo = res.getPoints();
			Vector<String> resAtomInfo = res.getAtomtypes();
			for (int j = 0; j < resInfo.size(); j++) {
				String atom_name = resAtomInfo.get(j);

				sb.append("ATOM  " + _Str.replenishHeadWithSpace("" + atomPos, 5));
				sb.append("  " + _Str.replenishEndWithSpace(atom_name, 3) + " ");

				// write three word amino acid
				String aminoacid = AminoAcid.oneWordToThree(aa);
				sb.append(aminoacid);
				sb.append(" A");
				sb.append(_Str.replenishHeadWithSpace("" + (resIndex), 4));
				sb.append("    ");
				String x_str = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(resInfo.get(j).getX(), "#0.000"), 8);
				String y_str = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(resInfo.get(j).getY(), "#0.000"), 8);
				String z_str = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(resInfo.get(j).getZ(), "#0.000"), 8);
				sb.append(x_str + y_str + z_str);
				sb.append("  1.00 00.00           "); // fix

				sb.append(atom_name.charAt(0) + "  \n");
				atomPos++;
			}
		}
		sb.append("TER\n");

		return sb.toString();
	}
	
	/****************************************************************
	 * @param start_ind : inclusive
	 * @param end_ind : exclusive
	 * @return
	 ***************************************************************/
	public String toString(int start_ind, int end_ind) {
		StringBuffer sb = new StringBuffer();
		// save protein structure
		Protein prot = this;
		int atomPos = 1;
		for (int resPos = 0; resPos < prot.size(); resPos++) {
			if (resPos < start_ind || resPos >= end_ind){
				continue;
			}
			
			Residue res = prot.get(resPos);
			char aa = prot.getAA(resPos);
			int resIndex = res.getPosInProt();
			if (-1 == resIndex)
				resIndex = resPos + 1;

			Vector<Point3D> resInfo = res.getPoints();
			Vector<String> resAtomInfo = res.getAtomtypes();
			for (int j = 0; j < resInfo.size(); j++) {
				String atom_name = resAtomInfo.get(j);

				sb.append("ATOM  " + _Str.replenishHeadWithSpace("" + atomPos, 5));
				sb.append("  " + _Str.replenishEndWithSpace(atom_name, 3) + " ");

				// write three word amino acid
				String aminoacid = AminoAcid.oneWordToThree(aa);
				sb.append(aminoacid);
				sb.append(" A");
				sb.append(_Str.replenishHeadWithSpace("" + (resIndex), 4));
				sb.append("    ");
				String x_str = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(resInfo.get(j).getX(), "#0.000"), 8);
				String y_str = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(resInfo.get(j).getY(), "#0.000"), 8);
				String z_str = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(resInfo.get(j).getZ(), "#0.000"), 8);
				sb.append(x_str + y_str + z_str);
				sb.append("  1.00 00.00           "); // fix

				sb.append(atom_name.charAt(0) + "  \n");
				atomPos++;
			}
		}
		sb.append("TER\n");

		return sb.toString();
	}
	
	public void save(String savePath, char chain){
		try{
			FileWriter fw = new FileWriter(savePath);
			// save protein structure
			Protein prot = this;
			int atomPos = 1;
			for (int resPos= 0; resPos < prot.size(); resPos++){
				Residue res = prot.get(resPos);
				char aa = prot.getAA(resPos);
				int resIndex = res.getPosInProt();
				if (-1 == resIndex)
					resIndex = resPos+1;
				
				Vector<Point3D> resInfo = res.getPoints();
				Vector<String> resAtomInfo = res.getAtomtypes();
				for (int j = 0; j < resInfo.size(); j++){
					String atom_name = resAtomInfo.get(j);
					
					fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+atomPos, 5));
					fw.write("  "+_Str.replenishEndWithSpace(atom_name, 3)+" ");
					
					// write three word amino acid
					String aminoacid = AminoAcid.oneWordToThree(aa);
					fw.write(aminoacid);
					fw.write(" "+chain);
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
			fw.write("TER\n");
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
			_Log.dayRunLog("SSFL save function cannot work normally.", new Date());
			_Log.dayRunLog(e.getMessage(), new Date());
		}
	}
	
	public void saveCa(String savePath, char chain){
		try{
			FileWriter fw = new FileWriter(savePath);
			// save protein structure
			Protein prot = this;
			int atomPos = 1;
			for (int resPos= 0; resPos < prot.size(); resPos++){
				Residue res = prot.get(resPos);
				char aa = prot.getAA(resPos);
				int resIndex = res.getPosInProt();
				if (-1 == resIndex)
					resIndex = resPos+1;
				
				Vector<Point3D> resInfo = res.getPoints();
				Vector<String> resAtomInfo = res.getAtomtypes();
				for (int j = 0; j < resInfo.size(); j++){
					if (j != res.getCa_index()){
						continue;
					}
					
					String atom_name = resAtomInfo.get(j);
					
					fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+atomPos, 5));
					fw.write("  "+_Str.replenishEndWithSpace(atom_name, 3)+" ");
					
					// write three word amino acid
					String aminoacid = AminoAcid.oneWordToThree(aa);
					fw.write(aminoacid);
					fw.write(" "+chain);
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
			fw.write("TER\n");
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
			_Log.dayRunLog("SSFL save function cannot work normally.", new Date());
			_Log.dayRunLog(e.getMessage(), new Date());
		}
	}
	
	public void save(String savePath, char chain, boolean needRemoveOneCaInTwoCa){
		try{
			FileWriter fw = new FileWriter(savePath);
			// save protein structure
			Protein prot = this;
			int atomPos = 1;
			for (int resPos= 0; resPos < prot.size(); resPos++){
				Residue res = prot.get(resPos);
				char aa = prot.getAA(resPos);
				int resIndex = res.getPosInProt();
				if (-1 == resIndex)
					resIndex = resPos+1;
				
				Vector<Point3D> resInfo = res.getPoints();
				Vector<String> resAtomInfo = res.getAtomtypes();
				for (int j = 0; j < resInfo.size(); j++){
					String atom_name = resAtomInfo.get(j);
					if (needRemoveOneCaInTwoCa
							&& j != res.getCa_index() 
							&& atom_name.trim().equalsIgnoreCase("CA")
							){
						continue;
					}
					
					
					fw.write("ATOM  "+_Str.replenishHeadWithSpace(""+atomPos, 5));
					fw.write("  "+_Str.replenishEndWithSpace(atom_name, 3)+" ");
					
					// write three word amino acid
					String aminoacid = AminoAcid.oneWordToThree(aa);
					fw.write(aminoacid);
					fw.write(" "+chain);
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
			fw.write("TER\n");
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
			_Log.dayRunLog("SSFL save function cannot work normally.", new Date());
			_Log.dayRunLog(e.getMessage(), new Date());
		}
	}
	
	public boolean isMinOuDisLessOrEqualThres(Vector<Point3D> points, double thres){
		for (int i = 0; i < residues.size(); i++){
			Residue res = residues.get(i);
			if (res.distNearestOu(points) <= thres)
				return true;
		}
		return false;
	}
	
	public boolean isMinOuDisLessOrEqualThres(Point3D point, double thres){
		for (int i = 0; i < residues.size(); i++){
			Residue res = residues.get(i);
			if (res.distOu(point) <= thres)
				return true;
		}
		return false;
	}
	
	/**
	 * @param point
	 * @param extraRes
	 * @param thres
	 * @return
	 * @descript: we do not consider the resiude information in ress
	 */
	public boolean isMinOuDisLessOrEqualThres(Point3D point, Vector<Residue> extraRes, double thres){
		for (int i = 0; i < residues.size(); i++){
			Residue res = residues.get(i);
			boolean isInExtra = false;
			for (int j = 0; j < extraRes.size(); j++){
				if (res.getAtomsPosInProt() == extraRes.get(j).getAtomsPosInProt()){
					isInExtra = true;
					break;
				}
			}

			if (isInExtra){
				continue;
			}
			
			if (res.distOu(point) <= thres)
				return true;
		}
		return false;
	}
	
	// may be it is translation function
	public void centralize(Point3D centerXYZ){
		for (int i = 0; i < residues.size(); i++){
			residues.get(i).centralize(centerXYZ);
		}
	}
	
	public static void main(String[] args) throws Exception{
//		if (3 != args.length){
//			System.out.println("e.g.:\n"
//					+ "protfasta\n"
//					+ "pdbchainfolder\n"
//					+ "savefolder");
//			System.exit(-1);
//		}
//
//		String protfasta = args[0];
//		String pdbchainfolder = args[1];
//		String savefolder = args[2];
//		
//		if (!new File(savefolder).exists()){
//			new File(savefolder).mkdirs();
//		}
//		
//		HashMap<String, String> hm = _File.loadFasta(protfasta, false);
//		Object[] ids = hm.keySet().toArray();
//		for (int i = 0; i < ids.length; i++){
//			if (new File(savefolder+"/"+ids[i]+".pdb").isFile()){
//				continue;
//			}
//			System.out.println(ids[i]+"\tis processing...");
//			Protein prot = new Protein((String)ids[i], pdbchainfolder+"/"+ids[i]+".pdb");
//			prot.save(savefolder+"/"+ids[i]+".pdb");
//		}
//		
//		System.out.println("HAVE A GOOD DAY!");
		
//		Protein prot = new Protein("1wd4A", "C:/Users/JunH/Desktop/1wd4A.pdb");
//		int seqlen = prot.getSeq().length();
//		int structlen = prot.size();
//		System.out.println(seqlen + " " + structlen);
//		for (int i = 0; i < seqlen; i++){
//			Residue res = prot.get(i); 
//			//if (res.getCa_index() >= res.size() || res.getCa_index() < 0){
//				System.out.println("Error : " + (i+1) + "\t" + res.getCa_index() + "/" + res.size());
//			//}
//		}
		
		Protein prot = new Protein("1t2eA", "C:/Users/JunH/Desktop/1t2eA.pdb", false);
		System.out.println(prot.toString());
		
		String bsites = "G10, G12, M13, I14, F33, D34, I35, V36, Y66, T78, A79, G80, F81, T82, I104, V124, T125, N126, V128, L149, L153, H181, P238";
		String[] bsitess = bsites.split(", ");
		Vector<String> bsitesVec = new Vector<String>();
		for (int i = 0; i < bsitess.length; i++){
			bsitesVec.add(bsitess[i]);
		}
		
		SSFL ssfl = new SSFL("C:/Users/JunH/Desktop/1t2eA.pdb", bsitesVec);
		ssfl.size();
	}
	
	/**
	 * @param decoy : the another decoy comformation of this protein 
	 * @return RMSD
	 */
	public double generalRMSD(Protein decoy){
		double rmsd = 0.0;
		
		for (int i = 0; i < this.size(); i++){
			rmsd += this.get(i).getCaCoordinate().distOuPow2( decoy.get(i).getCaCoordinate() );
		}
		
		rmsd = Math.sqrt(rmsd / size());
		
		return rmsd;
	}
	
	/**
	 * @param decoy
	 * @return its not the normalized TMscore
	 */
	public double generalRawTMscore(Protein decoy){
		
		double tmsco = 0.0;
		
		double d0 = 0.5;
		if (decoy.size() > 15) {
			d0 = 1.24 * Math.pow((decoy.size() - 15), (1.0 / 3.0)) - 1.8;
		}
		if (d0 < 0.5) {
			d0 = 0.5;
		}
		
		double d0Pow2 = d0*d0;
		
		for (int i = 0; i < this.size(); i++){
			double i_ca_disPow2 = this.get(i).getCaCoordinate().distOuPow2( decoy.get(i).getCaCoordinate() );
			
			tmsco += 1.0 / (1.0 + i_ca_disPow2/d0Pow2);
		}
		
		tmsco /= this.size();
		
		return tmsco;
	}
	
	public boolean isSurfaceInRaw(int ithAA){
		if (isSurfaceInRaw == null){
			getResidue_RawOutsideSurfaceInfo_FastVersion(3.8);
		}
		
		return isSurfaceInRaw[ithAA];
	}
	
	public int isSurfaceInDetail(int ithAA){
		if (isSurfaceInDetail == null){
			getResidue_DetailOutsideSurfaceInfo_FastVersion(2.0);
		}
		
		return isSurfaceInDetail[ithAA];
	}
	
	/** 
	 * 0 - NotKnow (N), 1->coil (C), 2->helix (H), 3->turn (T), 4->strand (S) 
	 * @param ithAA
	 * @return
	 */
	public char getSStype(int ithAA){
		if (ss_types == null){
			this.make_secstr();
		}
		
		return ss_types[ithAA];
	}
	
	/**
	 * @param dis_between_ca : generally is 3.8 angstrom
	 * @return
	 */
	public boolean[] getResidue_RawOutsideSurfaceInfo(double dis_between_ca){
		if (null != isSurfaceInRaw){
			return isSurfaceInRaw;
		}
		
		Point3D o_xyz = this.getCaCenterPt();
		
		double d_raw = this.getMaxDistanceBetweenTwoCa();
		double r_raw = d_raw / 2.0;
		
		double grid_box_step_angle = 2.0 * Math.asin(dis_between_ca/2.0/r_raw);
		double cos_grid_box_step_angle = Math.cos(grid_box_step_angle);
		
		double[] disPow2_2O_arr = new double[size()];					// Record the distance between Residue-Center
		double[][] ROR_angle_cos_vals = new double[size()][size()];	// Record the cosine value of Residue-Center_Residue angle
		
		for (int i = 0; i < size(); i++){
			Residue i_res = residues.get(i);
			Point3D i_ca = i_res.getCaCoordinate();
			
			Point3D __i_ca =  Point3D.sub(i_ca, o_xyz);
			
			for (int j = i+1; j < size(); j++){
				Residue j_res = residues.get(j);
				Point3D j_ca = j_res.getCaCoordinate();
				Point3D __j_ca =  Point3D.sub(j_ca, o_xyz);
				
				double cos = __i_ca.distCos(__j_ca);
				ROR_angle_cos_vals[i][j] = cos;
				ROR_angle_cos_vals[j][i] = cos;
			}
			
			disPow2_2O_arr[i] = __i_ca.distOuPow2(new Point3D(0, 0, 0));
		}
		
		// Search the surface
		
		int[] index = _Sort.insertDescendSortIndex(disPow2_2O_arr);
		
		isSurfaceInRaw = new boolean[size()];
		for (int i = 0; i < size(); i++){
			isSurfaceInRaw[i] = true;
		}
		
		for (int i = 0; i < size(); i++){
			int i_ind = index[i];
			if (true == isSurfaceInRaw[i_ind]){
				for (int j = i+1; j < size(); j++){
					int j_ind = index[j];
					
					if (true == isSurfaceInRaw[j_ind]){
						if (cos_grid_box_step_angle < ROR_angle_cos_vals[i_ind][j_ind]){
							isSurfaceInRaw[j_ind] = false;
						}
					}
				}
				
			}
		}
		
		return isSurfaceInRaw;
	}
	
	/**
	 * 
	 * 
	 * @param dis_between_ca : generally is 3.8 angstrom
	 * @return
	 */
	public boolean[] getResidue_RawOutsideSurfaceInfo_FastVersion(double dis_between_ca){
		if (null != isSurfaceInRaw){
			return isSurfaceInRaw;
		}
		
		Point3D o_xyz = this.getCaCenterPt();
		
		double d_raw = this.getMaxDistanceBetweenTwoCa();
		double r_raw = d_raw / 2.0;
		
		double grid_box_step_angle = 2.0 * Math.asin(dis_between_ca/2.0/r_raw);
		double cos_grid_box_step_angle = Math.cos(grid_box_step_angle);
		
		double[] disPow2_2O_arr = new double[size()];					// Record the distance between Residue-Center
		
		Point3D[] __ca_xyzs = new Point3D[size()];
		
		for (int i = 0; i < size(); i++){
			Residue i_res = residues.get(i);
			Point3D i_ca = i_res.getCaCoordinate();
			
			Point3D __i_ca =  Point3D.sub(i_ca, o_xyz);
			__ca_xyzs[i] = __i_ca;
			
			disPow2_2O_arr[i] = __i_ca.distOuPow2(new Point3D(0, 0, 0));
		}
		
		// Search the surface
		
		int[] index = _Sort.insertDescendSortIndex(disPow2_2O_arr);
		
		isSurfaceInRaw = new boolean[size()];
		for (int i = 0; i < size(); i++){
			isSurfaceInRaw[i] = true;
		}
		
		for (int i = 0; i < size(); i++){
			int i_ind = index[i];
			if (true == isSurfaceInRaw[i_ind]){
				for (int j = i+1; j < size(); j++){
					int j_ind = index[j];
					
					if (true == isSurfaceInRaw[j_ind]){
						
						double ROR_angle_cos_val = __ca_xyzs[i_ind].distCos(__ca_xyzs[j_ind]);
						
						if (cos_grid_box_step_angle < ROR_angle_cos_val){
							isSurfaceInRaw[j_ind] = false;
						}
					}
				}
				
			}
		}
		
		return isSurfaceInRaw;
	}
	
	/**
	 * 
	 * 
	 * @param dis_between_ca : generally is 3.8 angstrom
	 * @return 	ans[i]=0 means this is not surface;
	 * 			ans[i]=1 means this is out surface;
	 * 			ans[i]=2 means this is inter surface;
	 */
	public int[] getResidue_DetailOutsideSurfaceInfo_FastVersion(double dis_between_ca){
		if (null != isSurfaceInDetail){
			return isSurfaceInDetail;
		}
		
		Point3D o_xyz = this.getCaCenterPt();
		
		double d_raw = this.getMaxDistanceBetweenTwoCa();
		double r_raw = d_raw / 2.0;
		
		double grid_box_step_angle = 2.0 * Math.asin(dis_between_ca/2.0/r_raw);
		double cos_grid_box_step_angle = Math.cos(grid_box_step_angle);
		
		double[] dis_2O_arr = new double[size()];					// Record the distance between Residue-Center
		
		Point3D[] __ca_xyzs = new Point3D[size()];
		
		for (int i = 0; i < size(); i++){
			Residue i_res = residues.get(i);
			Point3D i_ca = i_res.getCaCoordinate();
			
			Point3D __i_ca =  Point3D.sub(i_ca, o_xyz);
			__ca_xyzs[i] = __i_ca;
			
			dis_2O_arr[i] = Math.sqrt(__i_ca.distOuPow2(new Point3D(0, 0, 0)));
		}
		
		// Search the surface
		
		int[] index = _Sort.insertDescendSortIndex(dis_2O_arr);
		
		isSurfaceInDetail = new int[size()];
		for (int i = 0; i < size(); i++){
			isSurfaceInDetail[i] = 1;
		}
		
		for (int i = 0; i < size(); i++){
			int i_ind = index[i];
			if (1 == isSurfaceInDetail[i_ind]){
				Vector<Integer> i_arr = new Vector<Integer>();
				for (int j = i+1; j < size(); j++){
					int j_ind = index[j];
					
					if (1 == isSurfaceInDetail[j_ind]){
						
						double ROR_angle_cos_val = __ca_xyzs[i_ind].distCos(__ca_xyzs[j_ind]);
						
						if (cos_grid_box_step_angle < ROR_angle_cos_val){
							i_arr.add(j_ind);
						}
					}
				}
				
				if (i_arr.size() <= 0){
					continue;
				}
				
				int end_ind = i_arr.get(0);
				for (int k = 1; k < i_arr.size(); k++){
					int k_ind = i_arr.get(k);
					if (dis_2O_arr[end_ind] - dis_2O_arr[k_ind] < dis_between_ca*2){ 
						isSurfaceInDetail[end_ind] = 0;
						end_ind = k_ind;
					}else{
						isSurfaceInDetail[end_ind] = 2;
						isSurfaceInDetail[k_ind] = 2;
						end_ind = k_ind;
					}
				
				}
				
				if (i_arr.size() != 0){
					if (dis_2O_arr[ i_arr.get( i_arr.size()-1 ) ] < dis_between_ca*2 ){
						isSurfaceInDetail[end_ind] = 0;
					}else isSurfaceInDetail[end_ind] = 2;
				}
				
			}
		}
		
		return isSurfaceInDetail;
	}
	
	public double getMaxDistanceBetweenTwoCa(){
		double ans = 0.0;
		for (int i = 0; i < size(); i++){
			for (int j = i+1; j < size(); j++){
				double disPow2 = residues.get(i).getCaCoordinate().distOuPow2( residues.get(j).getCaCoordinate() );
				
				if (disPow2 > ans){
					ans = disPow2;
				}
			}
		}
		
		return Math.sqrt(ans);
	}
	
	public double[] getMaxDistBetweenTwoCa_Second_Level_TypeII(double ang, Vector<Point3D[]> corr_pairs){
		Point3D O = this.getCaCenterPt();
		
		double ans1 = 0.0;
		
		Point3D aCa = null;
		Point3D bCa = null;
		for (int i = 0; i < size(); i++){
			Point3D I = residues.get(i).getCaCoordinate();
			Point3D I_O = Point3D.sub(O, I);
			
			for (int j = i+1; j < size(); j++){
				Point3D J =  residues.get(j).getCaCoordinate();
				Point3D O_J = Point3D.sub(J, O);
				
				if (I_O.distCos(O_J) > 0.90){
					double disPow2 = I.distOuPow2( J );
					
					if (disPow2 > ans1){
						ans1 = disPow2;
						aCa = I;
						bCa = J;
					}
				}
			}
		}
		{
			Point3D[] tmp = new Point3D[2];
			tmp[0] = aCa;
			tmp[1] = aCa;
			corr_pairs.add(tmp);
		}
		
		Point3D direct1 = Point3D.sub(aCa, bCa);
		
		double ans2 = 0.0;
		Point3D cCa = null;
		Point3D dCa = null;
		for (int i = 0; i < size(); i++){
			Point3D I = residues.get(i).getCaCoordinate();
			Point3D I_O = Point3D.sub(O, I);
			
			for (int j = i+1; j < size(); j++){
				Point3D J =  residues.get(j).getCaCoordinate();
				Point3D O_J = Point3D.sub(J, O);
				
				if (I_O.distCos(O_J) > 0.90 || I_O.distCos(O_J) < -0.9){
					Point3D direct2 = Point3D.sub(I, J);
					
					double abs_cos = Math.abs(Point3D.innerMultiple(direct1, direct2))
							/ (direct1.distOu(new Point3D()) * direct2.distOu(new Point3D()));
					
					if (abs_cos < Math.cos(ang)){	// TODO NOTE that
						double disPow2 = I.distOuPow2( J );
						if (disPow2 > ans2){
							ans2 = disPow2;
							cCa = I;
							dCa = J;
						}
					}
				}
			}
		}
		{
			Point3D[] tmp = new Point3D[2];
			tmp[0] = cCa;
			tmp[1] = dCa;
			corr_pairs.add(tmp);
		}
		
		double[] ans = new double[2];
		ans[0] = Math.sqrt(ans1);
		ans[1] = Math.sqrt(ans2);
		
		return ans;
	}
	
	public double[] getMaxDistBetweenTwoCa_Second_Level(double ang, Vector<Point3D[]> corr_pairs){
		double ans1 = 0.0;
		
		Point3D aCa = null;
		Point3D bCa = null;
		for (int i = 0; i < size(); i++){
			for (int j = i+1; j < size(); j++){
				double disPow2 = residues.get(i).getCaCoordinate().distOuPow2( residues.get(j).getCaCoordinate() );
				
				if (disPow2 > ans1){
					ans1 = disPow2;
					aCa = residues.get(i).getCaCoordinate();
					bCa = residues.get(j).getCaCoordinate();
				}
			}
		}
		{
			Point3D[] tmp = new Point3D[2];
			tmp[0] = aCa;
			tmp[1] = aCa;
			corr_pairs.add(tmp);
		}
		
		Point3D direct1 = Point3D.sub(aCa, bCa);
		
		double ans2 = 0.0;
		Point3D cCa = null;
		Point3D dCa = null;
		for (int i = 0; i < size(); i++){
			for (int j = i+1; j < size(); j++){
				Point3D direct2 = Point3D.sub(residues.get(i).getCaCoordinate(), residues.get(j).getCaCoordinate());
				
				double abs_cos = Math.abs(Point3D.innerMultiple(direct1, direct2))
						/ (direct1.distOu(new Point3D()) * direct2.distOu(new Point3D()));
				
				if (abs_cos < Math.cos(ang)){	// TODO NOTE that
					double disPow2 = residues.get(i).getCaCoordinate().distOuPow2( residues.get(j).getCaCoordinate() );
					if (disPow2 > ans2){
						ans2 = disPow2;
						cCa = residues.get(i).getCaCoordinate();
						dCa = residues.get(j).getCaCoordinate();
					}
				}
			}
		}
		{
			Point3D[] tmp = new Point3D[2];
			tmp[0] = cCa;
			tmp[1] = dCa;
			corr_pairs.add(tmp);
		}
		
		double[] ans = new double[2];
		ans[0] = Math.sqrt(ans1);
		ans[1] = Math.sqrt(ans2);
		
		return ans;
	}
	
	//0 - NotKnow (N), 1->coil (C), 2->helix (H), 3->turn (T), 4->strand (S) 
	private void make_secstr(){
		int len = residues.size();
		int[] sec = new int[len];
	  int j1, j2, j3, j4, j5;
		double d13, d14, d15, d24, d25, d35;
	  for(int i=0; i<len; i++){ 	
			sec[i]=1;
	    j1=i-2;
	    j2=i-1;
	    j3=i;
	    j4=i+1;
	    j5=i+2;		
	        
	    if(j1>=0 && j5<len){
				d13 = residues.get(j1).getCaCoordinate().distOu( residues.get(j3).getCaCoordinate() );
				d14 = residues.get(j1).getCaCoordinate().distOu( residues.get(j4).getCaCoordinate() );
				d15 = residues.get(j1).getCaCoordinate().distOu( residues.get(j5).getCaCoordinate() );
				d24 = residues.get(j2).getCaCoordinate().distOu( residues.get(j4).getCaCoordinate() );
				d25 = residues.get(j2).getCaCoordinate().distOu( residues.get(j5).getCaCoordinate() );
				d35 = residues.get(j3).getCaCoordinate().distOu( residues.get(j5).getCaCoordinate() );
				sec[i] = sec_str(d13, d14, d15, d24, d25, d35);			
			}    
	  } 
	    
	  ss_types = new char[len];
	  for (int i = 0; i < len; i++){
//		0 - NotKnow (N), 1->coil (C), 2->helix (H), 3->turn (T), 4->strand (S) 
	    switch(sec[i]){
	    	case 1:
	    		ss_types[i] = 'C';
	    		break;
	    	case 2:
	    		ss_types[i] = 'H';
	    		break;
	    	case 3:
	    		ss_types[i] = 'T';
	    		break;
	    	case 4:
	    		ss_types[i] = 'S';
	    		break;
	    	default:
	    		ss_types[i] = 'N';
	    }
	  }
	}
	
	private int sec_str(double dis13, double dis14, double dis15, double dis24, double dis25, double dis35)
	{
		int s=1;
		
		double delta=2.1;
		if(Math.abs(dis15-6.37)<delta)
		{
			if(Math.abs(dis14-5.18)<delta)
			{
				if(Math.abs(dis25-5.18)<delta)
				{
					if(Math.abs(dis13-5.45)<delta)
					{
						if(Math.abs(dis24-5.45)<delta)
						{
							if(Math.abs(dis35-5.45)<delta)
							{
								s=2; //helix						
								return s;
							}
						}
					}
				}
			}
		}

		delta=1.42;
		if(Math.abs(dis15-13)<delta)
		{
			if(Math.abs(dis14-10.4)<delta)
			{
				if(Math.abs(dis25-10.4)<delta)
				{
					if(Math.abs(dis13-6.1)<delta)
					{
						if(Math.abs(dis24-6.1)<delta)
						{
							if(Math.abs(dis35-6.1)<delta)
							{
								s=4; //strand
								return s;
							}
						}
					}
				}
			}
		}

		if(dis15 < 8)
		{
			s=3; //turn
		}	  


		return s;
	}
}
