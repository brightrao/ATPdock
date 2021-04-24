package _BioStruct;

import java.util.Vector;

public class Ligands {
	private String ligtype;
	private String ligandsPDB;
	private double labdis;
	
	private Vector<Ligand> ligands;
	
	private Ligand allligs;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Ligands ligs = new Ligands(null, "/nfs/amino-home/hujunum/library/pdbchain/1XEFA_atp.pdb", 3.9);
		Vector<Ligand> ligsPos = ligs.getLigands();
		for (int i = 0; i < ligsPos.size(); i++){
			System.out.println(ligsPos.get(i).toString());
		}
	}
	
	public Ligands(String ligtype, String ligandsPDB, double labdis){
		this.ligtype = ligtype;
		this.ligandsPDB = ligandsPDB;
		this.labdis = labdis;
		
		loadLigands();
	}
	
	public Ligands(String ligtype, Ligand allligs, double labdis){
		this.ligtype = ligtype;
		this.allligs = allligs;
		this.labdis = labdis;
		
		loadLigands();
	}
	
	public String getLigtype() {
		return ligtype;
	}

	public int size(){
		return ligands.size();
	}
	
	public Vector<Ligand> getLigands() {
		return ligands;
	}
	
	public Ligand get(int index){
		return ligands.get(index);
	}

	// search the corrLigandsPDB file contains how many Ligands;
	private void loadLigands(){
		ligands = new Vector<Ligand>();
		
		if (null == allligs)
			allligs = new Ligand(ligtype.getBytes(), ligandsPDB);
		// judge how many ligands existed in corrLigandsPDB
		Vector<Point3D> allpoints = allligs.getPoints();
		double gapdis = labdis > 4.0 ? labdis : 4.0; // angstrom
		Vector<Vector<Point3D>> ligs = new Vector<Vector<Point3D>>();
		Vector<Point3D> firstLig = new Vector<Point3D>();
		firstLig.add(allpoints.get(0));
		ligs.add(firstLig);
		
		for (int i = 1; i < allpoints.size(); i++){
			Point3D ithpoi = allpoints.get(i);
			
			boolean isPocFind = false;
			for (int j = 0; j < ligs.size(); j++){
				if (ithpoi.distOu(ligs.get(j)) < gapdis){
					ligs.get(j).add(ithpoi);
					isPocFind = true;
				}
			}
			
			if (!isPocFind){
				Vector<Point3D> tmpLig = new Vector<Point3D>();
				tmpLig.add(ithpoi);
				ligs.add(tmpLig);
			}
		}
		
		for (int i = 0; i < ligs.size(); i++){
			ligands.add(new Ligand(ligs.get(i), ligtype.getBytes()));
		}
	}
}
