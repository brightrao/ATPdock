package _BioStruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

public class DNA {
	private Vector<Point3D> points;
	
	public DNA(String... dna_files){
		points = new Vector<Point3D>();
		for (int i = 0; i < dna_files.length; i++){
			if (!new File(dna_files[i]).isFile()){
				continue;
			}
				
			loadDNAStructure(dna_files[i]);
		}
	}
	
	public Vector<Point3D> getPoints(){
		return points;
	}
	
	private void loadDNAStructure(String dna_file){
		try{
			BufferedReader br = new BufferedReader(new FileReader(dna_file));
			String line = br.readLine();
			while (null != line){
				if (line.startsWith("ATOM") || line.startsWith("HETATM")){
					double x = Double.parseDouble(line.substring(31, 38));
					double y = Double.parseDouble(line.substring(39, 46));
					double z = Double.parseDouble(line.substring(47, 54));
					Point3D point = new Point3D(x, y, z);
					
					points.add(point);
				}
				
				line = br.readLine();
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DNA dna_point = new DNA("F:/Academic/TargetDNA/references/PDNA-316/pdbfiles/1A02_dna.pdb");
		Vector<Point3D> points = dna_point.getPoints();
		
		System.out.println(points.size() + "END");
	}

}
