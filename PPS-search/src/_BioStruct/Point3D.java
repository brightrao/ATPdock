package _BioStruct;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class Point3D {
	private double x;
	private double y;
	private double z;
	
	public Point3D(){
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}
	
	public Point3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(double[] xyz){
		if (xyz.length != 3){
			System.out.println("Warning : Point3D(double xyz)");
		}
		
		this.x = xyz[0];
		this.y = xyz[1];
		this.z = xyz[2];
	}
	
	public Point3D(Point3D point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	public Double get(int index){
		Double ans = null;
		if (0 == index)
			ans = x;
		if (1 == index)
			ans = y;
		if (2 == index)
			ans = z;
		
		return ans;
	}
	
	public boolean isO(){
		if (x==0.0 && y==0.0 && z==0.0){
			return true;
		}
		return false;
	}
	
	public boolean isO_almost(double thres){
		if (x<=thres && y<=thres && z<=thres){
			return true;
		}
		return false;
	}
	
	public double distOu(Point3D other){
		double ans = 0.0;
		ans = (x-other.x)*(x-other.x)+(y-other.y)*(y-other.y)+(z-other.z)*(z-other.z);
		ans = Math.sqrt(ans);
		return ans;
	}
	
	public double distOuPow2(Point3D other){
		double ans = 0.0;
		ans = (x-other.x)*(x-other.x)+(y-other.y)*(y-other.y)+(z-other.z)*(z-other.z);
		return ans;
	}
	
	public double distOu(Vector<Point3D> points){
		double nearestDis = Double.MAX_VALUE;
		for (int j = 0; j < points.size(); j++){
			double tmp = points.get(j).distOu(this); 
			if (tmp < nearestDis){
				nearestDis = tmp;
			}
		}
		return nearestDis;
	}
	
	public double distOuPow2(Vector<Point3D> points){
		double nearestDis = Double.MAX_VALUE;
		for (int j = 0; j < points.size(); j++){
			double tmp = points.get(j).distOuPow2(this); 
			if (tmp < nearestDis){
				nearestDis = tmp;
			}
		}
		return nearestDis;
	}
	
	public boolean isMinOuDisLessThres(Vector<Point3D> points, double thres){
		for (int i = 0; i < points.size(); i++){
			double tmp = points.get(i).distOu(this);
			if (tmp < thres){
				return true;
			}
		}
		return false;
	}
	
	public Point3D copy(){
		Point3D pos = new Point3D(x, y, z);
		return pos;
	}
	
	public void add(Point3D point){
		this.x += point.getX();
		this.y += point.getY();
		this.z += point.getZ();
	}
	
	public static Point3D add(final Point3D a, final Point3D b){
		double x = a.getX() + b.getX();
		double y = a.getY() + b.getY();
		double z = a.getZ() + b.getZ();
		
		return new Point3D(x, y, z);
	}
	
	public void sub(Point3D point){
		this.x -= point.getX();
		this.y -= point.getY();
		this.z -= point.getZ();
	}
	
	public static Point3D sub(final Point3D a, final Point3D b){
		double x = a.getX() - b.getX();
		double y = a.getY() - b.getY();
		double z = a.getZ() - b.getZ();
		
		return new Point3D(x, y, z);
	}
	
	public static Point3D multipleConst(final Point3D a, final Double c){
		double x = a.getX() * c;
		double y = a.getY() * c;
		double z = a.getZ() * c;
		
		return new Point3D(x, y, z);
	}
	
	public static double innerMultiple(final Point3D a, final Point3D b){
		double x = a.getX() * b.getX();
		double y = a.getY() * b.getY();
		double z = a.getZ() * b.getZ();
		
		return x+y+z;
	}
	
	public void normalize(double N){
		this.x /= N;
		this.y /= N;
		this.z /= N;
	}
	
	public static Point3D normalize(final Point3D a, final double N){
		double x = a.getX()/N;
		double y = a.getY()/N;
		double z = a.getZ()/N;
		
		return new Point3D(x, y, z);
	}
	
	public double distCos(Point3D other){
		double ans = 0.0;
		ans = x*other.x + y*other.y + z*other.z;
		ans = ans / (this.distOu(new Point3D(0, 0, 0)) * other.distOu(new Point3D(0, 0, 0)));
		return ans;
	}
	
	public double[] toArray(){
		double[] ans = new double[3];
		ans[0] = x; ans[1] = y; ans[2] = z;
		
		return ans;
	}
	
	public boolean equals(Point3D pt){
		if (x == pt.getX() && y == pt.getY() && z == pt.getZ()){
			return true;
		}else return false;
	}
	
	public String toString(){
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#0.000");
		return ""+df.format(x)+"\t"+df.format(y)+"\t"+df.format(z);
	}
	
	public static Point3D argmaxMulDisToResidues(final Vector<Point3D> posVec, final Point3D... poss){
		double maxMulDis = -Double.MAX_VALUE;
		int corrIndex = 0;
		for (int i = 0; i < posVec.size(); i++){
			Point3D candidate = posVec.get(i);
			double tmpMulDis = 1.0;
			for (int j = 0; j < poss.length; j++){
				Point3D obj = poss[j];
				double dis = obj.distOu(candidate);
				tmpMulDis *= dis;
			}
			
			if (tmpMulDis > maxMulDis){
				maxMulDis = tmpMulDis;
				corrIndex = i;
			}
		}
		
		Point3D ans = posVec.get(corrIndex);
		return ans;
		
	}
	
	public static Point3D generateUnitVector8Random() {
		long randseed = new Date().getTime();
		Random rand = new Random(randseed);

		double acos_theta = 1 - 2.0 * rand.nextDouble();	// (-1, 1]
		double asin_theta = Math.sqrt(1.0 - acos_theta * acos_theta);
		double aphi = 2.0 * Math.PI * rand.nextDouble(); // [0, 2*PI]
		double awx = asin_theta * Math.cos(aphi);
		double awy = asin_theta * Math.sin(aphi);
		double awz = acos_theta;

		return new Point3D(awx, awy, awz);
	}
	
	public static void main(String[] args) throws Exception{
		for (int i = 0; i < 20; i++){
			Point3D pt = generateUnitVector8Random();
			Thread.sleep(1000);
			double len = pt.distOu(new Point3D(0, 0, 0));
			System.out.println(pt.toString() + "\tLen = " + len);
		}
	}
}
