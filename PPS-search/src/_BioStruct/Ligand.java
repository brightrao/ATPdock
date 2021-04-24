package _BioStruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import _util.AtomFixInfo;
import _util._NumFormat;
import _util._Str;
import _util._Vector;

public class Ligand {
	private String ligname;
	private Vector<Point3D> points;
	private Vector<String> atomTypes = null;
	private byte[] ligtype;

	private Double longRadius;
	private Double shortRadius;
	private Point3D geometricCenter;
	private Vector<Point3D> fourSpePts;
	
	private HashMap<String, Double> atomBondMaxDisHm;	// temp
	
	private Vector<int[]> bondOrderInfo;	// index = bond order; 
											// value[0] = the first bond atom index, 
											// value[1] = the other bond atom index;
	private Vector<Double> bondDisInfo; 	// the bond distance for each bond
	
	private int[][] bondTypeInfo;	// 1 = single
									// 2 = double
									// 3 = triple
									// 4 = am (amide)
									// 5 = ar (aromatic)
									// 6 = du (dummy)
									// 7 = un (unknow) (cannot be determined from the parameter tables)
									// 8 = nc (not connected)
	
	// temp 
	private boolean[] isSurfacePointArr; // save whether the point is surface point or r not. 
	
	private Vector<String> origiMol2Info;	// record the original mol2 string information. Notice the xyz information is not change
	private Vector<Boolean> isXYZInfoLineInMol2;
	
	public static void main(String[] args){
		Protein prot = new Protein("2x6tC", "D:/1.JunH/2x6tC.pdb", true);
		Ligand lig = new Ligand("ADP".getBytes(), "D:/1.JunH/2x6t_ADP_C_1.pdb");
		
		String[] lbs = {"T81", "N169", "S180", "M181", "A182", "V184", "H187", "L200", "F201", "S204", "R209", "F243", "Y272"};
		for (int k = 0; k < lbs.length; k++){
			int index = Integer.parseInt(lbs[k].substring(1)) - 1;
			
			Residue res = prot.get(index);
			double dis = res.distNearestOu(lig);
			
			System.out.println(lbs[k]+"\t"+dis);
		}
		System.out.println();
	}
	
	public Ligand(byte[] ligtype, String... ligand_files){
		points = new Vector<Point3D>();
		for (int i = 0; i < ligand_files.length; i++){
			if (!new File(ligand_files[i]).isFile()){
				continue;
			}
				
			loadLigandStructure(ligand_files[i]);
		}
		
		this.ligtype = ligtype;
	}
	
	public Ligand(byte[] ligtype, String pdbpath, boolean isLoadHydrogenAtom){
		this(ligtype, pdbpath);
		
		if (!isLoadHydrogenAtom){
			// we do not load Hydrogen atom
			Vector<Integer> nonHIndVec = new Vector<Integer>();
			Vector<Point3D> _points = new Vector<Point3D>();
			Vector<String> _atomTypes = new Vector<String>();
			for (int i = 0; i < atomTypes.size(); i++){
				if (!"H".equalsIgnoreCase(atomTypes.get(i))){
					nonHIndVec.add(i);
					_points.add(points.get(i));
					_atomTypes.add(atomTypes.get(i));
				}
			}
			
			this.points = _points;
			this.atomTypes = _atomTypes;
		}
	}
	
	public Ligand(String... ligand_files){
		points = new Vector<Point3D>();
		for (int i = 0; i < ligand_files.length; i++){
			if (!new File(ligand_files[i]).isFile()){
				continue;
			}
				
			loadLigandStructure(ligand_files[i]);
		}
	}
	
	public Ligand(Vector<Point3D> points){
		this.points = points;
	}
	
	public Ligand(Vector<Point3D> points, byte[] ligtype){
		this.points = points;
		this.ligtype = ligtype;
	}
	
	public Ligand(Ligand lig, boolean isMemCopy){
		if (isMemCopy){ // is memory copy
			ligname = lig.getLigName();
			
			Vector<int[]> _bondOrderInfo = lig.getBondOrderInfo();
			if (null != _bondOrderInfo){
				bondOrderInfo = new Vector<int[]>();
				for (int i = 0; i < _bondOrderInfo.size(); i++){
					int[] _t = _bondOrderInfo.get(i);
					int[] t = new int[_t.length];
					for (int j = 0; j < _t.length; j++)
						t[j] = _t[j];
					bondOrderInfo.add(t);
				}
			}
			
			int[][] _bondTypeInfo = lig.getbBondTypeInfo();
			if (null != _bondTypeInfo){
				bondTypeInfo = new int[_bondTypeInfo.length][_bondTypeInfo[0].length];
				for (int i = 0; i < _bondTypeInfo.length; i++)
					for (int j = 0; j < _bondTypeInfo[0].length; j++)
						bondTypeInfo[i][j] = _bondTypeInfo[i][j]; 
			}
			
			Vector<Point3D> _points = lig.getPoints();
			if (null != _points){
				points = new Vector<Point3D>();
				for (int i = 0; i < _points.size(); i++){
					Point3D p = new Point3D(_points.get(i));
					points.add(p);
				}
			}
			
			Vector<String> _atomTypes = lig.getAtomTypes();
			if (null != _atomTypes){
				atomTypes = new Vector<String>();
				for (int i = 0; i < _atomTypes.size(); i++){
					atomTypes.add(_atomTypes.get(i));
				}
			}
			
			byte[] _ligtype = lig.getLigType();
			if (null != _ligtype){
				ligtype = new byte[_ligtype.length];
				for (int i = 0; i < _ligtype.length; i++){
					ligtype[i] = _ligtype[i];
				}
			}
			
//			private Vector<String> origiMol2Info;	// record the original mol2 string information. Notice the xyz information is not change
//			private Vector<Boolean> isXYZInfoLineInMol2;
			Vector<String> _origiMol2Info = lig.getOrigiMol2Info();
			if (null != _origiMol2Info){
				origiMol2Info = new Vector<String>();
				for (int i = 0; i < _origiMol2Info.size(); i++){
					origiMol2Info.add(_origiMol2Info.get(i));
				}
			}
			
			Vector<Boolean> _isXYZInfoLineInMol2 = lig.getIsXYZInfoLineInMol2();
			if (null != _isXYZInfoLineInMol2){
				isXYZInfoLineInMol2 = new Vector<Boolean>();
				for (int i = 0; i < _isXYZInfoLineInMol2.size(); i++){
					isXYZInfoLineInMol2.add(_isXYZInfoLineInMol2.get(i));
				}
			}
			
		}else{
			ligname = lig.getLigName();
			bondOrderInfo = lig.getBondOrderInfo();
			bondTypeInfo = lig.getbBondTypeInfo();
			points = lig.points;
			atomTypes = lig.atomTypes;
			ligtype = lig.ligtype;
		}
	}
	
	public Ligand(String sdfpath, boolean isSdf){
		points = new Vector<Point3D>();
		atomTypes = new Vector<String>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(sdfpath));
			br.readLine();
			br.readLine();
			br.readLine();
			String l = br.readLine();
			while (null != l){
				if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(l.substring(31, 32))){
					double x = Double.parseDouble(l.substring(1, 10));
					double y = Double.parseDouble(l.substring(11, 20));
					double z = Double.parseDouble(l.substring(21, 30));
					
					Point3D pos = new Point3D(x, y, z);
					points.add(pos);
					
					String atomtype = l.substring(30, 34).trim();
					atomTypes.add(atomtype);
				}
				
				l = br.readLine();
			}
			br.close();
		}catch(Exception e){
			
		}
	}
	
	public Ligand(String mol2path, double isMol2, boolean isLoadHydrogenAtom){
		this(mol2path, isMol2);
		
		if (!isLoadHydrogenAtom){
			// we do not load Hydrogen atom
			Vector<Integer> nonHIndVec = new Vector<Integer>();
			Vector<Point3D> _points = new Vector<Point3D>();
			Vector<String> _atomTypes = new Vector<String>();
			for (int i = 0; i < atomTypes.size(); i++){
				if (!"H".equalsIgnoreCase(atomTypes.get(i))){
					nonHIndVec.add(i);
					_points.add(points.get(i));
					_atomTypes.add(atomTypes.get(i));
				}
			}
			
			int[][] _bondTypeInfo = new int[nonHIndVec.size()][nonHIndVec.size()];
			for (int i = 0; i < nonHIndVec.size(); i++){
				for (int j = 0; j < nonHIndVec.size(); j++){
					_bondTypeInfo[i][j] = bondTypeInfo[nonHIndVec.get(i)][nonHIndVec.get(j)];
				}
			}
			
			Vector<int[]> _bondOrderInfo = new Vector<int[]>();
			for (int i = 0; i < bondOrderInfo.size(); i++){
				int[] tmp = bondOrderInfo.get(i);
				
				int ni = -1;
				int nj = -1;
				for (int k = 0; k < nonHIndVec.size(); k++){
					if (tmp[0] == nonHIndVec.get(k))
						ni = k;
					
					if (tmp[1] == nonHIndVec.get(k))
						nj = k;
				}
				
				if (ni != -1 && nj != -1){
					int[] _tmp = new int[2];
					_tmp[0] = ni;
					_tmp[1] = nj;
					
					_bondOrderInfo.add(_tmp);
				}
					
			}
			
			this.points = _points;
			this.atomTypes = _atomTypes;
			this.bondOrderInfo = _bondOrderInfo;
			this.bondTypeInfo = _bondTypeInfo; 
		}
	}
	
	public Ligand(String mol2path, double isMol2){
		points = new Vector<Point3D>();
		atomTypes = new Vector<String>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(mol2path));
			String l = br.readLine();
			while (null != l && !l.startsWith("@<TRIPOS>ATOM")){
				if (l.startsWith("@<TRIPOS>MOLECULE")){
					this.ligname = br.readLine().trim();
				}
				
				l = br.readLine();
			}
				
			l = br.readLine();
			while (null != l){
				if (l.startsWith("@<TRIPOS>")){
					break;
				}
				
				double x = Double.parseDouble(l.substring(17, 26));
				double y = Double.parseDouble(l.substring(27, 36));
				double z = Double.parseDouble(l.substring(37, 46));
				
				Point3D pos = new Point3D(x, y, z);
				points.add(pos);
				
//				String atomtype = l.substring(47, 48).trim();
//				if (atomtype.equals("")) {
//					atomtype = l.substring(48, 49).trim();
//				}
				
				String atomtype = l.substring(9, 12).trim();
				
				atomTypes.add(atomtype);
				
				l = br.readLine();
			}
			br.close();
			
			this.bondOrderInfo = new Vector<int[]>();
			this.bondTypeInfo = new int[points.size()][points.size()];
			BufferedReader br1 = new BufferedReader(new FileReader(mol2path));
			String ll = br1.readLine();
			while (null != ll && !ll.startsWith("@<TRIPOS>BOND")){
				ll = br1.readLine();
			}
				
			ll = br1.readLine();
			while (null != ll){
				if (ll.startsWith("@<TRIPOS>")){
					break;
				}
				
				String[] llc = ll.trim().split(" +|\t+");
				if (llc.length < 4){
					ll = br1.readLine();
					continue;
				}
				int atom1Ind = Integer.parseInt(llc[1]) - 1;
				int atom2Ind = Integer.parseInt(llc[2]) - 1;
				String bondtype = llc[3];
				
				int[] tmp = new int[2];
				tmp[0] = atom1Ind;
				tmp[1] =  atom2Ind;
				bondOrderInfo.add(tmp);
				
				// 1 = 1 (single)
				// 2 = 2 (double)
				// 3 = 3 (triple)
				// 4 = am (amide)
				// 5 = ar (aromatic)
				// 6 = du (dummy)
				// 7 = un (unknow) (cannot be determined from the parameter tables)
				// 8 = nc (not connected)
				if (bondtype.equals("1")){
					bondTypeInfo[atom1Ind][atom2Ind] = 1;
					bondTypeInfo[atom2Ind][atom1Ind] = 1;
				}else if (bondtype.equals("2")){
					bondTypeInfo[atom1Ind][atom2Ind] = 2;
					bondTypeInfo[atom2Ind][atom1Ind] = 2;
				}else if (bondtype.equals("3")){
					bondTypeInfo[atom1Ind][atom2Ind] = 3;
					bondTypeInfo[atom2Ind][atom1Ind] = 3;
				}else if (bondtype.equals("am")){
					bondTypeInfo[atom1Ind][atom2Ind] = 4;
					bondTypeInfo[atom2Ind][atom1Ind] = 4;
				}else if (bondtype.equals("ar")){
					bondTypeInfo[atom1Ind][atom2Ind] = 5;
					bondTypeInfo[atom2Ind][atom1Ind] = 5;
				}else if (bondtype.equals("du")){
					bondTypeInfo[atom1Ind][atom2Ind] = 6;
					bondTypeInfo[atom2Ind][atom1Ind] = 6;
				}else if (bondtype.equals("un")){
					bondTypeInfo[atom1Ind][atom2Ind] = 7;
					bondTypeInfo[atom2Ind][atom1Ind] = 7;
				}else if (bondtype.equals("nc")){
					bondTypeInfo[atom1Ind][atom2Ind] = 8;
					bondTypeInfo[atom2Ind][atom1Ind] = 8;
				}
				
				ll = br1.readLine();
			}
			br1.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setLigandName(String ligName){
		this.ligname = ligName;
	}
	
	public Ligand(String oneLigMol2Info, char tag){
		points = new Vector<Point3D>();
		atomTypes = new Vector<String>();
		origiMol2Info = new Vector<String>();	// record the original mol2 string information. Notice the xyz information is not change
		isXYZInfoLineInMol2 = new Vector<Boolean>();
		
		String[] infoes = oneLigMol2Info.split("\n");
		try{
			int l = 0;
			while (l < infoes.length && !infoes[l].startsWith("@<TRIPOS>ATOM")){
				if (infoes[l].startsWith("@<TRIPOS>MOLECULE")){
					origiMol2Info.add(infoes[l]);
					isXYZInfoLineInMol2.add(false);
					
					this.ligname = infoes[++l];
				}
				
				origiMol2Info.add(infoes[l]);
				isXYZInfoLineInMol2.add(false);
				
				l++;
			}
			origiMol2Info.add(infoes[l]);
			isXYZInfoLineInMol2.add(false);
			
			l++;
			while (l < infoes.length){
				if (infoes[l].startsWith("@<TRIPOS>")){
					break;
				}
				
				double x = Double.parseDouble(infoes[l].substring(17, 26));
				double y = Double.parseDouble(infoes[l].substring(27, 36));
				double z = Double.parseDouble(infoes[l].substring(37, 46));
				
				Point3D pos = new Point3D(x, y, z);
				points.add(pos);
				
				String atomtype = infoes[l].substring(47, 48).trim();
				if (atomtype.equals(""))
					atomtype = infoes[l].substring(48, 49).trim();
				atomTypes.add(atomtype);
				
				origiMol2Info.add(infoes[l]);
				isXYZInfoLineInMol2.add(true);
				
				l++;
			}
			
//			origiMol2Info.add(infoes[l]);
//			isXYZInfoLineInMol2.add(false);
			
			this.bondOrderInfo = new Vector<int[]>();
			this.bondTypeInfo = new int[points.size()][points.size()];
			
			int ll = 0;
			while (ll < infoes.length && !infoes[ll].startsWith("@<TRIPOS>BOND")){
				ll++;
			}
			
			origiMol2Info.add(infoes[ll]);
			isXYZInfoLineInMol2.add(false);
			
			ll++;
			while (ll < infoes.length){
				if (infoes[ll].startsWith("@<TRIPOS>")){
					break;
				}
				
				String[] llc = infoes[ll].trim().split(" +|\t+");
				if (llc.length < 4){
					ll++;
					continue;
				}
				int atom1Ind = Integer.parseInt(llc[1]) - 1;
				int atom2Ind = Integer.parseInt(llc[2]) - 1;
				String bondtype = llc[3];
				
				int[] tmp = new int[2];
				tmp[0] = atom1Ind;
				tmp[1] =  atom2Ind;
				bondOrderInfo.add(tmp);
				
				// 1 = 1 (single)
				// 2 = 2 (double)
				// 3 = 3 (triple)
				// 4 = am (amide)
				// 5 = ar (aromatic)
				// 6 = du (dummy)
				// 7 = un (unknow) (cannot be determined from the parameter tables)
				// 8 = nc (not connected)
				if (bondtype.equals("1")){
					bondTypeInfo[atom1Ind][atom2Ind] = 1;
					bondTypeInfo[atom2Ind][atom1Ind] = 1;
				}else if (bondtype.equals("2")){
					bondTypeInfo[atom1Ind][atom2Ind] = 2;
					bondTypeInfo[atom2Ind][atom1Ind] = 2;
				}else if (bondtype.equals("3")){
					bondTypeInfo[atom1Ind][atom2Ind] = 3;
					bondTypeInfo[atom2Ind][atom1Ind] = 3;
				}else if (bondtype.equals("am")){
					bondTypeInfo[atom1Ind][atom2Ind] = 4;
					bondTypeInfo[atom2Ind][atom1Ind] = 4;
				}else if (bondtype.equals("ar")){
					bondTypeInfo[atom1Ind][atom2Ind] = 5;
					bondTypeInfo[atom2Ind][atom1Ind] = 5;
				}else if (bondtype.equals("du")){
					bondTypeInfo[atom1Ind][atom2Ind] = 6;
					bondTypeInfo[atom2Ind][atom1Ind] = 6;
				}else if (bondtype.equals("un")){
					bondTypeInfo[atom1Ind][atom2Ind] = 7;
					bondTypeInfo[atom2Ind][atom1Ind] = 7;
				}else if (bondtype.equals("nc")){
					bondTypeInfo[atom1Ind][atom2Ind] = 8;
					bondTypeInfo[atom2Ind][atom1Ind] = 8;
				}
				
				origiMol2Info.add(infoes[ll]);
				isXYZInfoLineInMol2.add(false);
				
				ll++;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Ligand(String oneLigMol2Info, char tag, boolean isLoadHydrogenAtom){
		this(oneLigMol2Info, tag);
		
		if (!isLoadHydrogenAtom){
			// we do not load Hydrogen atom
			Vector<Integer> nonHIndVec = new Vector<Integer>();
			Vector<Point3D> _points = new Vector<Point3D>();
			Vector<String> _atomTypes = new Vector<String>();
			for (int i = 0; i < atomTypes.size(); i++){
				if (!"H".equalsIgnoreCase(atomTypes.get(i))){
					nonHIndVec.add(i);
					_points.add(points.get(i));
					_atomTypes.add(atomTypes.get(i));
				}
			}
			
			int[][] _bondTypeInfo = new int[nonHIndVec.size()][nonHIndVec.size()];
			for (int i = 0; i < nonHIndVec.size(); i++){
				for (int j = 0; j < nonHIndVec.size(); j++){
					_bondTypeInfo[i][j] = bondTypeInfo[nonHIndVec.get(i)][nonHIndVec.get(j)];
				}
			}
			
			Vector<int[]> _bondOrderInfo = new Vector<int[]>();
			for (int i = 0; i < bondOrderInfo.size(); i++){
				int[] tmp = bondOrderInfo.get(i);
				
				int ni = -1;
				int nj = -1;
				for (int k = 0; k < nonHIndVec.size(); k++){
					if (tmp[0] == nonHIndVec.get(k))
						ni = k;
					
					if (tmp[1] == nonHIndVec.get(k))
						nj = k;
				}
				
				if (ni != -1 && nj != -1){
					int[] _tmp = new int[2];
					_tmp[0] = ni;
					_tmp[1] = nj;
					
					_bondOrderInfo.add(_tmp);
				}
			}
			
			this.points = _points;
			this.atomTypes = _atomTypes;
			this.bondOrderInfo = _bondOrderInfo;
			this.bondTypeInfo = _bondTypeInfo; 
		}
	}
	
	public String getLigName(){
		return this.ligname;
	}
	
	/**
	 * @return index is order, int[0] is the first atom index of the bond, int[1] is the second atom index of the bond
	 */
	public Vector<int[]> getBondOrderInfo(){
		return this.bondOrderInfo;
	}
	
	public int[][] getbBondTypeInfo(){
		return this.bondTypeInfo;
	}
	
	public Vector<String> getOrigiMol2Info(){
		return origiMol2Info;
	}
	
	public Vector<Boolean> getIsXYZInfoLineInMol2(){
		return isXYZInfoLineInMol2;
	}
	
	public double[] getBondDisInfo(){
		if (null == this.bondDisInfo){
			bondDisInfo = new Vector<Double>();
			
			for (int i = 0; i < bondOrderInfo.size(); i++){
				int[] tmp = bondOrderInfo.get(i);
				int r = tmp[0];
				int c = tmp[1];
				
				double dis = points.get(r).distOu(points.get(c));
				bondDisInfo.add(dis);
			}
		}
		
		double[] ans = new double[bondDisInfo.size()];
		for (int i = 0; i < bondDisInfo.size(); i++)
			ans[i] = bondDisInfo.get(i);
		return ans;
	}
	
	public byte[] getLigType(){
		return this.ligtype;
	}
	
	public Vector<Point3D> getPoints(){
		return points;
	}
	
	public Double getLongRadius() {
		if (null == longRadius)
			this.calApproximateLongShortRadiusAndGeometricCenterOfLig();
		
		return longRadius;
	}

	public Double getShortRadius() {
		if (null == shortRadius)
			this.calApproximateLongShortRadiusAndGeometricCenterOfLig();
		
		return shortRadius;
	}

	public int size(){
		return points.size();
	}
	
	public Point3D getPoint(int index){
		return points.get(index);
	}
	
	public Vector<String> getAtomTypes(){
		return atomTypes;
	}
	
	public String getAtomType(int index){
		return atomTypes.get(index);
	}
	
	/****************************************************************
	 * calculate the max bond distance between atom type A 
	 * and atom type B
	 * 
	 * @return key = {ATOMTYPEA}-{ATOMTYPEB}; value = {max distance, no matter what is the bond type} 
	 ***************************************************************/
	public HashMap<String, Double> getAtomBondMaxDisHm(){
		if (null == atomBondMaxDisHm){
			atomBondMaxDisHm = new HashMap<String, Double>();
			double[] bondDisInfo = getBondDisInfo();
			
			for (int i = 0; i < bondDisInfo.length; i++){
				double dis = bondDisInfo[i];
				
				int[] tmp = bondOrderInfo.get(i);
				int r = tmp[0];
				int c = tmp[1];
				
				String aAtomType = this.atomTypes.get(r);
				String bAtomType = this.atomTypes.get(c);
				String key = (aAtomType+"-"+bAtomType).toUpperCase();
				Double value = atomBondMaxDisHm.get(key);
				if (null == value){
					value = new Double(-Double.MAX_VALUE);
				}
				
				if (dis > value){
					value = dis;
				}
				
				atomBondMaxDisHm.put(key, value);
				String key1 = (bAtomType+"-"+aAtomType).toUpperCase();
				atomBondMaxDisHm.put(key1, value);
			}
		}
		
		return atomBondMaxDisHm;
	}
	
	public int getAtomNumWithout_H_atom(){
		int atomnum_without_H = 0;
		for (int i = 0; i < atomTypes.size(); i++){
			String atomtype = atomTypes.get(i);
			if (!"H".equalsIgnoreCase(atomtype.trim()))
				atomnum_without_H++;
		}
		
		return atomnum_without_H;
	}
	
	public double getMaxMassDisBetweenTwoAtoms(){
		double[] masses = new double[atomTypes.size()];
		for (int i = 0; i < masses.length; i++){
			masses[i] = AtomFixInfo.getRelativeMass(atomTypes.get(i));
		}
		
		return _Vector.biggest(masses);
	}
	
	public Vector<Double> getMassesOfAllAtoms(){
		Vector<Double> ans = new Vector<Double>();
		for (int i = 0; i < atomTypes.size(); i++){
			ans.add(AtomFixInfo.getRelativeMass(atomTypes.get(i)));
		}
		
		return ans;
	}
	
	/**
	 * @return the indexes value in points vec (start from zero)
	 */
	public Vector<Integer> getSurfacePointIndexes(){
		if (null == isSurfacePointArr){
			ensureSurfacePoints();
		}
		
		Vector<Integer> ans = new Vector<Integer>();
		for (int i = 0; i  < isSurfacePointArr.length; i++){
			if (isSurfacePointArr[i]){
				ans.add(i); 
			}
		}
		
		return ans;
	}
	
	/**
	 * @param index : the index in point vector (start from 0)
	 * @return : is surface point of index 
	 */
	public Boolean isSurfacePoint(int index){
		if (null == isSurfacePointArr){
			ensureSurfacePoints();
		}
		
		Boolean ans = null;
		if (index >= 0 && index < isSurfacePointArr.length){
			ans = isSurfacePointArr[index];
		}
		
		return ans;
	}
	
	private void ensureSurfacePoints(){
		isSurfacePointArr = new boolean[points.size()];
		for (int i = 0; i < isSurfacePointArr.length; i++){
			isSurfacePointArr[i] = false;
		}
		
		
		double gridDensityDis = 2;
		Point3D centerPos = this.getCenterPoint3D();
		double gridBoxRadius = this.maxDisBetweenTwoSelfAtoms() / 2.0 + 3.9; // TODO you want to ensure the 3.9
		
		Vector<Point3D> bgGridPoints = new Vector<Point3D>();
		for (double x = centerPos.getX()-gridBoxRadius; x < centerPos.getX()+gridBoxRadius; x += gridDensityDis){
			for (double y = centerPos.getY()-gridBoxRadius; y < centerPos.getY()+gridBoxRadius; y += gridDensityDis){
				for (double z = centerPos.getZ()-gridBoxRadius; z < centerPos.getZ()+gridBoxRadius; z += gridDensityDis){
					Point3D gp = new Point3D(x, y, z);
					// Judge the condition is satisfied.
					double dis = this.distOu(gp);
					if (dis > gridDensityDis 
							&& dis < gridDensityDis*2){
						bgGridPoints.add(gp);
					}
				}
			}
		}
		
		// Judge the point whether is surface point or not.
		for (int i = 0; i < bgGridPoints.size(); i++){
			int nearestResIndex = -1;
			Double nearestDis = Double.MAX_VALUE;
			
			for (int j = 0; j < points.size(); j++){
				double dis = bgGridPoints.get(i).distOu(points.get(j));
				
				if (dis < nearestDis){
					nearestDis = dis;
					nearestResIndex = j;
				}
			}
			
			if (-1 != nearestResIndex){
				isSurfacePointArr[nearestResIndex] = true;
			}else{
				System.out.println("Ligand : impossible thing happens");
			}
		}
	}
	
	public Vector<Point3D> setPoints(Vector<Point3D> points){
		Vector<Point3D> originalPoints = this.points;
		this.points = points;
		
		// recalculate the following parameter
		geometricCenter = null;
		fourSpePts = null;
		
		return originalPoints;
	}
	
	public Point3D getCenterPoint3D(){
		Point3D ans = new Point3D(0.0, 0.0, 0.0);
		for (int i = 0; i < points.size(); i++){
			ans.add(points.get(i));
		}
		
		ans.normalize(points.size());
		return ans;
	}
	
	/**
	 * calculate the approximative center point XYZ
	 * @return
	 */
	public Point3D getGeometricCenter(){
		if (this.geometricCenter == null)
			this.calApproximateLongShortRadiusAndGeometricCenterOfLig();
		
		return geometricCenter;
	}
	
	public Vector<Point3D> getFourSpeAtomPts(){
		if (this.fourSpePts == null)
			this.calApproximateLongShortRadiusAndGeometricCenterOfLig();
		
		return fourSpePts;
	}
	
	private void calApproximateLongShortRadiusAndGeometricCenterOfLig() {
		// search the maximum distance between any two point
		double maxDis = -Double.MAX_VALUE;
		Point3D firstPt = null;
		Point3D secondPt = null;
		for (int i = 0; i < points.size(); i++) {
			for (int j = i + 1; j < points.size(); j++) {
				if (i == j)
					continue;
				Point3D ithPt = points.get(i);
				Point3D jthPt = points.get(j);

				double dis = ithPt.distOu(jthPt);
				if (dis > maxDis) {
					maxDis = dis;
					firstPt = ithPt;
					secondPt = jthPt;
				}
			}
		}
		
		Point3D thirdPt = Point3D.argmaxMulDisToResidues(points, firstPt, secondPt);
		Point3D oPt = new Point3D();
		oPt.add(firstPt);
		oPt.add(secondPt);
		oPt.normalize(2);
		
		double maxShortDis = -Double.MAX_VALUE;
		Point3D corrPoint = null;
		
		double biasXYZLength = thirdPt.distOu(oPt);
		double[] biasXYZDrectVec = Point3D.normalize(Point3D.sub(oPt, thirdPt), biasXYZLength).toArray(); // ||biasXYZDrectVec|| = 1
		double scileNum = biasXYZLength / 2.0;
		for (double i = scileNum / 2; i <= 2 * scileNum && i*2.0 <= maxDis; i++){
			double x = thirdPt.getX() + i*2.0 * biasXYZDrectVec[0];
			double y = thirdPt.getY() + i*2.0 * biasXYZDrectVec[1];
			double z = thirdPt.getZ() + i*2.0 * biasXYZDrectVec[2];
			
			// search the minimum distance point in ligand to (x, y, z)
			double minDis = Double.MAX_VALUE;
			Point3D corrLigPt = null;
			for (int j = 0; j < points.size(); j++){
				double dis = points.get(j).distOu(new Point3D(x, y, z));
				if (dis < minDis){
					minDis = dis;
					corrLigPt = points.get(j);
				}
			}
			
			double dis = thirdPt.distOu(corrLigPt);
			if (dis > maxShortDis){
				maxShortDis = dis;
				corrPoint = new Point3D(x, y, z);
			}else break;
		}
		
		this.longRadius = maxDis / 2;
		this.shortRadius = maxShortDis/2;
		this.geometricCenter = Point3D.multipleConst(Point3D.add(thirdPt, corrPoint), 0.5);
		
		fourSpePts = new Vector<Point3D>();
		fourSpePts.add(firstPt);
		fourSpePts.add(secondPt);
		fourSpePts.add(thirdPt);
		fourSpePts.add(corrPoint);
	}
	
	public double distOu(Point3D point){
		double nearestDis = Double.MAX_VALUE;
		for (int j = 0; j < points.size(); j++){
			double tmp = points.get(j).distOu(point); 
			if (tmp < nearestDis){
				nearestDis = tmp;
			}
		}
		return nearestDis;
	}
	
	/****************************************************************
	 * Notice the function just can be used the ligand is load by 
	 * 		Ligand(String oneLigMol2Info, char tag) constructor
	 * 
	 * @return
	 ***************************************************************/
	public String format8mol2(){
		if (null == origiMol2Info || isXYZInfoLineInMol2 == null){
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0, xyzj = 0; i < origiMol2Info.size(); i++){
			if (false == isXYZInfoLineInMol2.get(i)){
				sb.append(origiMol2Info.get(i)+"\n");
			}else if (xyzj < points.size()){
				String l = origiMol2Info.get(i);
				String nxyz = _Str.replenishHeadWithSpace(_NumFormat.formatDouble(points.get(xyzj).getX(), "#0.0000"), "    2.0360".length())
						+ _Str.replenishHeadWithSpace(_NumFormat.formatDouble(points.get(xyzj).getY(), "#0.0000"), "    2.0360".length())
						+ _Str.replenishHeadWithSpace(_NumFormat.formatDouble(points.get(xyzj).getZ(), "#0.0000"), "    2.0360".length());
				String ll = l.substring(0, 16) + nxyz + l.substring(46);
				sb.append(ll+"\n");
				
				xyzj++;
			}
		}
		
		return sb.toString();
	}
	
	public String format8pdb(){
		if (null == ligtype){
			ligtype = "nul".getBytes();
		}
		
		StringBuffer ans = new StringBuffer();
		for (int i = 0; i < points.size(); i++){
			//HETATM 7590  PG  ATP B1800       1.475  -6.506  12.945  1.00 36.69           P  
			ans.append("HETATM "+_Str.replenishHeadWithSpace(""+(i+1), 4)+
					"      "+_Str.replenishHeadWithSpace(new String(ligtype), 3)+" 88888    ");
			
			String x_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getX(), "#0.000"), 8);
			String y_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getY(), "#0.000"), 8);
			String z_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getZ(), "#0.000"), 8);
			ans.append(x_str+y_str+z_str);
			
			String atomtype = this.atomTypes.get(i);
			if (atomtype.length() > 3) atomtype.substring(0, 3);
			ans.append("  1.00  0.00         "+_Str.replenishHeadWithSpace(atomtype, 3)+"  ");
			ans.append("\n");
		}
		ans.append("TER\n");
		
		ligtype = null;
		return ans.toString();
	}
	
	public String format8pdb(String ligtype){
		
		StringBuffer ans = new StringBuffer();
		for (int i = 0; i < points.size(); i++){
			String atomtype = this.atomTypes.get(i);
			
			//HETATM 7590  PG  ATP B1800       1.475  -6.506  12.945  1.00 36.69           P  
			ans.append("HETATM "+_Str.replenishHeadWithSpace(""+(i+1), 4)+
					" "+_Str.replenishHeadWithSpace(atomtype, 3)+"  "+_Str.replenishHeadWithSpace(ligtype, 3)+" 88888    ");
			
			String x_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getX(), "#0.000"), 8);
			String y_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getY(), "#0.000"), 8);
			String z_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getZ(), "#0.000"), 8);
			ans.append(x_str+y_str+z_str);
			
			if (atomtype.length() > 3) atomtype.substring(0, 3);
			ans.append("  1.00  0.00         "+_Str.replenishHeadWithSpace(atomtype.substring(0, 1), 3)+"  ");
			ans.append("\n");
		}
		ans.append("TER\n");
		
		ligtype = null;
		return ans.toString();
	}
	
	public void translate(double tx, double ty, double tz){
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = 0; i < points.size(); i++){
			double ttx = points.get(i).getX() + tx;
			double tty = points.get(i).getY() + ty;
			double ttz = points.get(i).getZ() + tz;
			
			ans.add(new Point3D(ttx, tty, ttz));
		}
		
		points = ans;
	}
	
	public void centralize(Point3D transVec){
		for (int i = 0; i < points.size(); i++){
			points.get(i).sub(transVec);
		}
		
	}
	
	public String format8pdb(String ligtype, String index){
		if (ligtype.length() > 3){
			ligtype = ligtype.substring(0, 3);
		}
		
		if (index.length() > 5){
			index = index.substring(0, 5);
		}
		
		StringBuffer ans = new StringBuffer();
		for (int i = 0; i < points.size(); i++){
			//HETATM 7590  PG  ATP B1800       1.475  -6.506  12.945  1.00 36.69           P  
			ans.append("HETATM "+_Str.replenishHeadWithSpace(""+(i+1), 4)+
					"      "+_Str.replenishHeadWithSpace(ligtype, 3)+" "+_Str.replenishHeadWithSpace(index, 5)+"    ");
			
			String x_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getX(), "#0.000"), 8);
			String y_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getY(), "#0.000"), 8);
			String z_str = _Str.replenishHeadWithSpace(
					_NumFormat.formatDouble(points.get(i).getZ(), "#0.000"), 8);
			ans.append(x_str+y_str+z_str);
			
			String atomtype = this.atomTypes.get(i);
			if (atomtype.length() > 3) atomtype.substring(0, 3);
			ans.append("  1.00  0.00         "+_Str.replenishHeadWithSpace(atomtype, 3)+"  ");
			ans.append("\n");
		}
		ans.append("TER\n");
		
		
		return ans.toString();
	}
	
	public String toString(){
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#0.000");
		StringBuffer sb = new StringBuffer();
		sb.append("\n-------------------Ligand 3D coordinates--------------------------------\n");
		for (int i = 0; i < points.size(); i++){
			sb.append("\t\t"+points.get(i).toString()+"\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	public double maxDisBetweenTwoSelfAtoms(){
		int size = points.size();
		double maxDis = 0.0;
		for (int i = 0; i < size-1; i++){
			for (int j = i+1; j < size; j++){
				double dis = points.get(i).distOu(points.get(j));
				
				if (dis > maxDis){
					maxDis = dis;
				}
			}
		}
		
		return maxDis;
	}
	
	/**
	 * @return obtain the max distance atom pair index
	 */
	public int[] indexesOfMaxDisedTwoAtoms(){
		int[] ans = new int[2];
		
		int size = points.size();
		double maxDis = 0.0;
		for (int i = 0; i < size-1; i++){
			for (int j = i+1; j < size; j++){
				double dis = points.get(i).distOu(points.get(j));
				
				if (dis > maxDis){
					maxDis = dis;
					ans[0] = i;
					ans[1] = j;
				}
			}
		}
		
		return ans;
	}
	
	public double maxMinDisBetweenTwoSelfAtoms(){
		int size = points.size();
		double maxDis = 0.0;
		
		for (int i = 0; i < size-1; i++){
			double minDis = Double.MAX_VALUE;
			for (int j = 0; j < size; j++){
				if (i == j) continue;
				
				double dis = points.get(i).distOu(points.get(j));
				
				if (dis < minDis){
					minDis = dis;
				}
			}
			
			if (maxDis < minDis){
				maxDis = minDis;
			}
		}
		
		return maxDis;
	}
	
	private void loadLigandStructure(String ligand_file){
		if (null == atomTypes){
			atomTypes = new Vector<String>();
		}
		try{
			BufferedReader br = new BufferedReader(new FileReader(ligand_file));
			String line = br.readLine();
			while (null != line){
				if (line.startsWith("ATOM") || line.startsWith("HETATM")){
//					double x = Double.parseDouble(line.substring(30, 38));
//					double y = Double.parseDouble(line.substring(38, 46));
//					double z = Double.parseDouble(line.substring(46, 54));
					double x = Double.parseDouble(line.substring(30, 38).trim());
					double y = Double.parseDouble(line.substring(39, 46).trim());
					double z = Double.parseDouble(line.substring(47, 54).trim());
					Point3D point = new Point3D(x, y, z);
					points.add(point);
					
					String atomtype = "   ";
					if (line.length() >= 78){
						atomtype = line.substring(12, 16).trim();
//						atomtype = line.substring(75, 78).trim(); 
					}
					atomTypes.add(atomtype);
				}
				
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
