package _BioStruct;

public class Atom {
	private Point3D xyz;
	private String full_type;
	private String abbr_type;
	private double vdw_radius;
	private double mass;

	public Atom(){}
	
	public Atom(Atom atom){
		xyz = atom.xyz;
		full_type = atom.full_type;
		abbr_type = atom.abbr_type;
		vdw_radius = atom.vdw_radius;
		mass = atom.mass;
	}
	
	public Atom(Point3D xyz,
		String full_type,
		String abbr_type,
		double vdw_radius,
		double mass){
		
		this.xyz = xyz;
		this.full_type = full_type;
		this.abbr_type = abbr_type;
		this.vdw_radius = vdw_radius;
		this.mass = mass;
		
	}
	
	
	public Point3D getXyz() {
		return xyz;
	}

	public String getFull_type() {
		return full_type;
	}

	public String getAbbr_type() {
		return abbr_type;
	}

	public double getVDWRadius() {
		return vdw_radius;
	}

	public double getMass() {
		return mass;
	}

	public double vdwForceType1(Atom anthor){
		double ans = 0.0;
		// TODO
		
		return ans;
	}
	
	
	public static double vdwForceType1(double r_a1, double r_a2, double dis){
		// TODO
		return 0.0;
	}
}
