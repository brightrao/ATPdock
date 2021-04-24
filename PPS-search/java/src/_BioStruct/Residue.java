package _BioStruct;

import java.util.Vector;

import _util._NumFormat;

public class Residue {
	private Vector<Integer> atomPosInProt; 	// save the corresponding atom information
	private Vector<String> atomtypes;	// save the corresponding atom inforation
	private Vector<Point3D> points;
	private int PosInProt = -1;		// reord the residue index in the corresponding Protein (the index is start from 1)
	private int IndexInOrigPDB = -99999999; // reord the residue index in the corresponding Protein PDB file
	private int Ca_index = 1;	// record the Ca index in the Residue
	private int Cb_index = 1;
	private Double dis2Oxyz;
	
	public Residue(Vector<Point3D> points, int Ca_index){
		this.points = points;
		this.Ca_index = Ca_index;
		
		if (points.size() == 1){
			this.Ca_index = 0;
		}
	}
	
	public Residue(Vector<Point3D> points, int Ca_index, int Cb_index){
		this.points = points;
		this.Ca_index = Ca_index;
		this.Cb_index = Cb_index;
		if (points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Vector<Point3D> points, Vector<String> atomtypes, int Ca_index, int Cb_index){
		this.points = points;
		this.atomtypes = atomtypes;
		this.Ca_index = Ca_index;
		this.Cb_index = Cb_index;
		
		if (points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Vector<Point3D> points, Vector<String> atomtypes, int Ca_index, int Cb_index, int PosInProt){
		this.points = points;
		this.atomtypes = atomtypes;
		this.Ca_index = Ca_index;
		this.Cb_index = Cb_index;
		this.PosInProt = PosInProt;
		
		if (points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Vector<Point3D> points, Vector<String> atomtypes, int Ca_index, int Cb_index, int PosInProt, Vector<Integer> atomPosInProt, int IndexInOrigPDB){
		this.points = points;
		this.atomtypes = atomtypes;
		this.Ca_index = Ca_index;
		this.Cb_index = Cb_index;
		this.PosInProt = PosInProt;
		this.atomPosInProt = atomPosInProt;
		this.IndexInOrigPDB = IndexInOrigPDB;
		
		if (points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Vector<Point3D> points, Vector<String> atomtypes){
		this.points = points;
		this.atomtypes = atomtypes;
		
		if (points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Vector<Point3D> points, Vector<String> atomtypes, int PosInProt){
		this.points = points;
		this.atomtypes = atomtypes;
		this.PosInProt = PosInProt;
		
		if (points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Residue other){
		this.points = other.points;
		
		if (this.points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Residue(Residue other, boolean isMemCopy){
		if (isMemCopy){
			this.atomPosInProt = null;
			if (other.atomPosInProt != null){
				this.atomPosInProt = new Vector<Integer>();
				for (int i = 0; i < other.atomPosInProt.size(); i++){
					this.atomPosInProt.add(new Integer(other.atomPosInProt.get(i)));
				}
			}
			
			this.atomtypes = null;
			if (other.atomtypes != null){
				this.atomtypes = new Vector<String>();
				for (int i = 0; i < other.atomtypes.size(); i++){
					this.atomtypes.add(new String(other.atomtypes.get(i)));
				}
			}
			
			this.points = null;
			if (other.points != null){
				this.points = new Vector<Point3D>();
				for (int i = 0; i < other.points.size(); i++){
					this.points.add(new Point3D(other.points.get(i)));
				}
			}
			
			this.PosInProt = other.PosInProt;
			this.Ca_index = other.Ca_index;
			this.Cb_index = other.Cb_index;
			if (null != other.dis2Oxyz)
				this.dis2Oxyz = new Double(other.dis2Oxyz);
		}else{
			this.atomPosInProt = other.atomPosInProt;
			this.atomtypes = other.atomtypes;
			this.points = other.points;
			this.PosInProt = other.PosInProt;
			this.Ca_index = other.Ca_index;
			this.Cb_index = other.Cb_index;
			this.dis2Oxyz = other.dis2Oxyz;
		}
		
		if (this.points.size() == 1){
			this.Ca_index = 0;
			this.Cb_index = 0;
		}
	}
	
	public Vector<Point3D> setPoints(Vector<Point3D> points){
		Vector<Point3D> origPoints = this.points;
		this.points = points;
		
		return origPoints;
	}
	
	public void changePOSthPointXYZCoordinate(int pos, Point3D xyz){
		this.points.set(pos, xyz);
	}
	
	public Vector<String> getAtomtypes(){
		return atomtypes;
	}
	
	public Double getDis2Oxyz(){
		if (null == dis2Oxyz){
			dis2Oxyz = this.distOu(new Point3D(0.0, 0.0, 0.0));
		}
		
		return dis2Oxyz;
	}
	
	public String getAtomtype(int index){
		return atomtypes.get(index);
	}
	
	public int getCa_index() {
		return Ca_index;
	}

	public int getCb_index() {
		return Cb_index;
	}
	
	public int size(){
		return this.points.size();
	}
	
	public Vector<Point3D> getPoints() {
		return points;
	}

	public int getPosInProt(){
		return PosInProt;
	}
	
	public int getIndexInOrigPDB() {
		return IndexInOrigPDB;
	}
	
	public Vector<Integer> getAtomsPosInProt(){
		return this.atomPosInProt;
	}
	
	public Point3D getCbCoordinate(){
		if (0 > Cb_index || points.size() <= Cb_index)
			return null;
		return points.get(Cb_index);
	}
	
	public Point3D getCaCoordinate(){
		if (0 > Ca_index || points.size() <= Ca_index)
			return null;
		return points.get(Ca_index);
	}
	
	public int getCaIndex() {
		return Ca_index;
	}
	
	public Point3D getPoint(int index){
		return points.get(index);
	}
	
	public Point3D getCentralCoordinate(){
		Point3D ans = new Point3D(0.0, 0.0, 0.0);
		for (int i = 0; i < points.size(); i++){
			ans.add(points.get(i));
		}
		
		ans.normalize(points.size());
		return ans;
	}
	
	public double distNearestOu(Residue other){
		double nearestDis = Double.MAX_VALUE;
		Vector<Point3D> other_points = other.getPoints();
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
				}
			}
		}
		return nearestDis;
	}
	
	public boolean isMinDisLessOrEqualThanThres(Vector<Residue> ress, double thres){
		for (int r = 0; r < ress.size(); r++){
			Vector<Point3D> other_points = ress.get(r).getPoints();
			for (int i = 0; i < other_points.size(); i++){
				for (int j = 0; j < points.size(); j++){
					double tmp = points.get(j).distOu(other_points.get(i)); 
					
					if (tmp <= thres){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @param point
	 * @param extraRes
	 * @param thres
	 * @return
	 */
	public boolean isMinOuDisLessOrEqualThres(Vector<Point3D> other_points, double thres){
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp <= thres){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @param point
	 * @param extraRes
	 * @param thres
	 * @return
	 */
	public boolean isMinOuDisLargeThanThres(Vector<Point3D> other_points, double thres){
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp <= thres){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public double distNearestOu(Ligand lig){
		double nearestDis = Double.MAX_VALUE;
		Vector<Point3D> other_points = lig.getPoints();
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
				}
			}
		}
		return nearestDis;
	}
	
	/*
	 * calculate the distance without considering the hydrogen atom
	 */
	public double distNearestOuIgnoreHydrogenAtom(Ligand lig){
		double nearestDis = Double.MAX_VALUE;
		Vector<Point3D> other_points = lig.getPoints();
		for (int i = 0; i < other_points.size(); i++){
			if (lig.getAtomType(i).trim().equalsIgnoreCase("H"))
				continue;
			
			for (int j = 0; j < points.size(); j++){
				if (this.atomtypes.get(j).trim().equalsIgnoreCase("H"))
					continue;
				
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
				}
			}
		}
		return nearestDis;
	}
	
	/**
	 * @param lig : the ligand information
	 * @return the atom pair which nearest atom pair between the residue with the ligand, 
	 * 			i.e., {atom_index_in_res (start from 0)}\t{atom_type_in_res}\t{atom_index_in_lig (start from 0)}\t{atom_type_in_lig}\t{the distance between the atom pair (Angstrom)}
	 * 			e.g., 12	N	5	C	2.8
	 */
	public String distNearestInfo(Ligand lig){
		double nearestDis = Double.MAX_VALUE;
		int resAtomInd = 0;
		String resAtomType = "null";
		int ligAtomInd = 0;
		String ligAtomType = "null";
		
		Vector<Point3D> other_points = lig.getPoints();
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
					resAtomInd = j;
					if (null != atomtypes){
						resAtomType = atomtypes.get(resAtomInd).trim();
					}
					
					ligAtomInd = i;
					if (null != lig.getAtomTypes()){
						ligAtomType = lig.getAtomTypes().get(ligAtomInd);
					}
				}
			}
		}
		
		return ""+resAtomInd+"\t"+resAtomType+"\t"+ligAtomInd+"\t"+ligAtomType+"\t"+_NumFormat.formatDouble(nearestDis, "#0.000");
	}
	
	/**
	 * @param lig : the ligand information
	 * @return the atom pair which nearest atom pair between the residue with the ligand, 
	 * 			i.e., {atom_index_in_res (start from 0)}\t{atom_type_in_res}\t{atom_index_in_lig (start from 0)}\t{atom_type_in_lig}\t{the distance between the atom pair (Angstrom)}
	 * 			e.g., 12	N	5	C	2.8
	 */
	public String distNearestInfoWithoutH(Ligand lig){
		double nearestDis = Double.MAX_VALUE;
		int resAtomInd = 0;
		String resAtomType = "null";
		int ligAtomInd = 0;
		String ligAtomType = "null";
		
		Vector<Point3D> other_points = lig.getPoints();
		for (int i = 0; i < other_points.size(); i++){
			String _ligAtomType = "null";
			if (null != lig.getAtomTypes()){
				_ligAtomType = lig.getAtomTypes().get(i);
			}
			if ("H".equalsIgnoreCase(_ligAtomType.substring(0, 1)))
				continue;
			
			for (int j = 0; j < points.size(); j++){
				String _resAtomType = "null";
				if (null != atomtypes){
					_resAtomType = atomtypes.get(j).trim();
				}
				if ("H".equalsIgnoreCase(_resAtomType.substring(0, 1)))
					continue;
				
				
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
					resAtomInd = j;
					ligAtomInd = i;
					
					resAtomType = _resAtomType;
					ligAtomType = _ligAtomType;
				}
			}
		}
		
		return ""+resAtomInd+"\t"+resAtomType+"\t"+ligAtomInd+"\t"+ligAtomType+"\t"+_NumFormat.formatDouble(nearestDis, "#0.000");
	}
	
	/**
	 * @param other_points
	 * @return
	 */
	public double distNearestOu(Vector<Point3D> other_points){
		double nearestDis = Double.MAX_VALUE;
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
				}
			}
		}
		return nearestDis;
	}
	
	/**
	 * @param other_points
	 * @param save_corr_res_atom_type : need new before use
	 * @return
	 */
	public double distNearestOu(Vector<Point3D> other_points, StringBuffer save_corr_res_atom_type){
		double nearestDis = Double.MAX_VALUE;
		String _res_atom_type = null;
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
					_res_atom_type = this.atomtypes.get(j);
				}
			}
		}
		
		save_corr_res_atom_type.append(_res_atom_type);
		return nearestDis;
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
	
	public double distCaOu(Residue other){
		return this.getCaCoordinate().distOu(other.getCaCoordinate());
	}
	
	public double farestDistOu(Point3D point){
		double fastestDis = -Double.MAX_VALUE;
		for (int j = 0; j < points.size(); j++){
			double tmp = points.get(j).distOu(point); 
			if (tmp > fastestDis){
				fastestDis = tmp;
			}
		}
		return fastestDis;
	}
	
	public void centralize(Point3D centerXYZ){
		for (int i = 0; i < points.size(); i++){
			points.get(i).sub(centerXYZ);
		}
	}
	
	/**
	 * 
	 */
	public double distNearestInfo(final Residue res, int[] this_and_other_atom_indexes,  String[] this_and_other_atom_types){
		double nearestDis = Double.MAX_VALUE;
		int this_res_atom_index = 0;
		String this_res_atom_type = "null";
		int other_res_atom_index = 0;
		String other_res_atom_type = "null";
		
		Vector<Point3D> other_points = res.getPoints();
		Vector<String> other_atomtypes = res.getAtomtypes();
		for (int i = 0; i < other_points.size(); i++){
			for (int j = 0; j < points.size(); j++){
				double tmp = points.get(j).distOu(other_points.get(i)); 
				if (tmp < nearestDis){
					nearestDis = tmp;
					this_res_atom_index = j;
					if (null != atomtypes){
						this_res_atom_type = atomtypes.get(this_res_atom_index).trim();
					}
					
					other_res_atom_index = i;
					if (null != other_atomtypes){
						other_res_atom_type = other_atomtypes.get(other_res_atom_index).trim();
					}
				}
			}
		}
		if (null != this_and_other_atom_indexes){
			this_and_other_atom_indexes[0] = this_res_atom_index;
			this_and_other_atom_indexes[1] = other_res_atom_index;
		}
		if (null != this_and_other_atom_types){
			this_and_other_atom_types[0] = this_res_atom_type;
			this_and_other_atom_types[1] = other_res_atom_type;
		}
		
		return nearestDis;
	}
	
}
