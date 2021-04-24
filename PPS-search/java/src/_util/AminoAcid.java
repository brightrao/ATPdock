package _util;

import java.util.Vector;

public class AminoAcid {
	public static String oneWordToThree(char aa){
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
	
	public static char threeWordToOne(String acid){
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
	
	public static final String AMINOACIDS = "ALA "
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
	
	public static final String AA_LIST = "ACDEFGHIKLMNPQRSTVWY";
	public static Vector<String[]> AAs_atoms = new Vector<String[]>();
	static {
		String[] A_atoms = {"N", "CA", "C", "O", "CB"};
		String[] C_atoms = {"N", "CA", "C", "O", "CB", "SG"};
		String[] D_atoms = {"N", "CA", "C", "O", "CB", "CG", "OD1", "OD2"};
		String[] E_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD", "OE1", "OE2"};
		String[] F_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD1", "CD2", "CE1", "CE2", "CZ"};
		String[] G_atoms = {"N", "CA", "C", "O"};
		String[] H_atoms = {"N", "CA", "C", "O", "CB", "CG", "ND1", "CD2", "CE1", "NE2"};
		String[] I_atoms = {"N", "CA", "C", "O", "CB", "CG1", "CG2", "CD1"};
		String[] K_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD", "CE", "NZ"};
		String[] L_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD1", "CD2"};
		String[] M_atoms = {"N", "CA", "C", "O", "CB", "CG", "SD", "CE"};
		String[] N_atoms = {"N", "CA", "C", "O", "CB", "CG", "OD1", "ND2"};
		String[] P_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD"};
		String[] Q_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD", "OE1", "NE2"};
		String[] R_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD", "NE", "CZ", "NH1", "NH2"};
		String[] S_atoms = {"N", "CA", "C", "O", "CB", "OG"};
		String[] T_atoms = {"N", "CA", "C", "O", "CB", "OG1", "CG2"};
		String[] V_atoms = {"N", "CA", "C", "O", "CB", "CG1", "CG2"};
		String[] W_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD1", "CD2", "NE1", "CE2", "CE3", "CZ2", "CZ3", "CH2"};
		String[] Y_atoms = {"N", "CA", "C", "O", "CB", "CG", "CD1", "CD2", "CE1", "CE2", "CZ", "OH"};

		AAs_atoms.add(A_atoms);
		AAs_atoms.add(C_atoms);
		AAs_atoms.add(D_atoms);
		AAs_atoms.add(E_atoms);
		AAs_atoms.add(F_atoms);
		AAs_atoms.add(G_atoms);
		AAs_atoms.add(H_atoms);
		AAs_atoms.add(I_atoms);
		AAs_atoms.add(K_atoms);
		AAs_atoms.add(L_atoms);
		AAs_atoms.add(M_atoms);
		AAs_atoms.add(N_atoms);
		AAs_atoms.add(P_atoms);
		AAs_atoms.add(Q_atoms);
		AAs_atoms.add(R_atoms);
		AAs_atoms.add(S_atoms);
		AAs_atoms.add(T_atoms);
		AAs_atoms.add(V_atoms);
		AAs_atoms.add(W_atoms);
		AAs_atoms.add(Y_atoms);
	}
}
