package _util;

import java.util.HashMap;

public class AtomFixInfo {
	private static HashMap<String, Double> atomRelativeMassHm;
	private static HashMap<String, Double> atomVarDerWaalsRadiusHm;
	
	public static void main(String[] args) {

	}

	private static void loadRelativeMassHm(){
		atomRelativeMassHm = new HashMap<String, Double>();
		String[] lc = relativeMassInfo.split("\n");
		for (int i = 0; i < lc.length; i++){
			String info = lc[i].replace('\t', ' ').trim();
			String[] inc = info.split(" +");
			String key = inc[0].toUpperCase();
			Double value = Double.parseDouble(inc[1]);
			
			atomRelativeMassHm.put(key, value);
		}
	}
	
	private static void loadVarDerWaalRadiusHm(){
		atomVarDerWaalsRadiusHm = new HashMap<String, Double>();
		String[] lc = varDerWaalRadius.split("\n");
		for (int i = 0; i < lc.length; i++){
			String info = lc[i].replace('\t', ' ').trim();
			String[] inc = info.split(" +");
			if (inc.length < 2){
				continue;
			}
			
			String key = inc[0].toUpperCase();
			Double value = Double.parseDouble(inc[1]);
			
			atomVarDerWaalsRadiusHm.put(key, value);
		}
	}
	
	public static String stayEngCh(String str){
		String engChs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz";
		
		StringBuffer ans = new StringBuffer();
		for (int i = 0; i < str.length(); i++){
			if (engChs.contains(""+str.charAt(i))){
				ans.append(""+str.charAt(i));
			}
			
		}
		
		return ans.toString();
	}
	
	public static double getRelativeMass(String atomType){
		if (null == atomRelativeMassHm)
			loadRelativeMassHm();
		
		Double ans = atomRelativeMassHm.get(stayEngCh(atomType.toUpperCase()));
		if (null == ans){
			ans = Double.MAX_VALUE;
//			System.out.println("atomType = " + atomType);
		}
		return ans;
	}
	
	public static double getVarDerWaalsRadius(String atomType){
		if (null == atomVarDerWaalsRadiusHm)
			loadVarDerWaalRadiusHm();
		
		Double ans = atomVarDerWaalsRadiusHm.get(stayEngCh(atomType.toUpperCase()));
		if (null == ans){
			ans = 3.9;	// return default	
		}
		return ans;
	}
	
	
	public static final String relativeMassInfo = 
			"H                   1.007947        \n" +  
			"He                  4.0026022   \n" +  
			"Li                  6.9412      \n" +  
			"Be                  9.0121823   \n" +  
			"B                   10.8117     \n" +  
			"C                   12.01078    \n" +  
			"N                   14.00672    \n" +  
			"O                   15.99943    \n" +  
			"F                   18.99840325 \n" +  
			"Ne                  20.17976    \n" +  
			"Na                  22.989769282\n" +  
			"Mg                  24.30506    \n" +  
			"Al                  26.98153868 \n" +  
			"Si                  28.08553    \n" +  
			"P                   30.9737622  \n" +  
			"S                   32.0655     \n" +  
			"Cl                  35.4532     \n" +  
			"Ar                  39.9481     \n" +  
			"K                   39.09831    \n" +  
			"Ca                  40.0784     \n" +  
			"Sc                  44.9559126  \n" +  
			"Ti                  47.8671     \n" +  
			"V                   50.94151    \n" +  
			"Cr                  51.99616    \n" +  
			"Mn                  54.9380455  \n" +  
			"Fe                  55.8452     \n" +  
			"Co                  58.9331955  \n" +  
			"Ni                  58.69342    \n" +  
			"Cu                  63.5463     \n" +  
			"Zn                  65.4094     \n" +  
			"Ga                  69.7231     \n" +  
			"Ge                  72.641      \n" +  
			"As                  74.921602   \n" +  
			"Se                  78.963      \n" +  
			"Br                  79.9041     \n" +  
			"Kr                  83.7982     \n" +  
			"Rb                  85.46783    \n" +  
			"Sr                  87.621      \n" +  
			"Y                   88.905852   \n" +  
			"Zr                  91.2242     \n" +  
			"Nb                  92.906382   \n" +  
			"Mo                  95.942      \n" +  
			"Tc                  97.9072     \n" +  
			"Ru                  101.072     \n" +  
			"Rh                  102.905502  \n" +  
			"Pd                  106.421     \n" +  
			"Ag                  107.86822   \n" +  
			"Cd                  112.4118    \n" +  
			"In                  114.8183    \n" +  
			"Sn                  118.7107    \n" +  
			"Sb                  121.7601    \n" +  
			"Te                  127.603     \n" +  
			"I                   126.904473  \n" +  
			"Xe                  131.2936    \n" +  
			"Cs                  132.90545192\n" +  
			"Ba                  137.3277\n" +  
			"La                  138.905477 \n" +  
			"Ce                  140.1161   \n" +  
			"Pr                  140.907652 \n" +  
			"Nd                  144.2423   \n" +  
			"Pm                  145        \n" +  
			"Sm                  150.362    \n" +  
			"Eu                  151.9641   \n" +  
			"Gd                  157.253    \n" +  
			"Tb                  158.925352 \n" +  
			"Dy                  162.5001   \n" +  
			"Ho                  164.930322 \n" +  
			"Er                  167.2593   \n" +  
			"Tm                  168.934212 \n" +  
			"Yb                  173.043    \n" +  
			"Lu                  174.9671   \n" +  
			"Hf                  178.492    \n" +  
			"Ta                  180.947882 \n" +  
			"W                   183.841    \n" +  
			"Re                  186.2071   \n" +  "Ra                  186.2071   \n" + 
			"Os                  190.233    \n" +  
			"Ir                  192.2173   \n" +  
			"Pt                  195.0849   \n" +  
			"Au                  196.9665694\n" +  
			"Hg                  200.592    \n" +  
			"Tl                  204.38332  \n" +  
			"Pb                  207.21     \n" +  
			"Bi                  208.980401 \n" +  
			"Po                  208.9824   \n" +  
			"At                  209.9871   \n" +  
			"Rn                  222.0176   \n" +  
			"Fr                  223        \n" +  
			"Re                  226        \n" +  
			"Ac                  227        \n" +  
			"Th                  232.038062 \n" +  
			"Pa                  231.035882 \n" +  
			"U                   238.028913 \n" +  
			"Np                  237        \n" +  
			"Pu                  244        \n" +  
			"Am                  243        \n" +  
			"Cm                  247        \n" +  
			"Bk                  247        \n" +  
			"Cf                  251        \n" +  
			"Es                  252        \n" +  
			"Fm                  257        \n" +  
			"Md                  258        \n" +  
			"No                  259        \n" +  
			"Lr                  262        \n" +  
			"Rf                  261        \n" +  
			"Db                  262        \n" +  
			"Sg                  266        \n" +  
			"Bh                  264        \n" +  
			"Hs                  277        \n" +  
			"Mt                  268        \n" +  
			"Ds                  281        \n" +  
			"Rg                  272        \n" +  
			"Cn                  285        \n" +  
			"Uut                 284        \n" +  
			"Uuq                 289        \n" +  
			"Uup                 288        \n" +  
			"Uuh                 292        \n" +  
			"Uus                 291        \n" +  
			"Uuo                 293        ";
	
	
	// the var der waal radius information is come from "Van der Waals Radii of Elements" (S. S. Batsanov)
	public static final String varDerWaalRadius = 			
			"H                   1.2     \n" +  
			"He                  1.4     \n" +  
			"Li                  2.2     \n" +  
			"Be                  1.9     \n" +  
			"B                   1.8     \n" +  
			"C                   1.7     \n" +  
			"N                   1.6     \n" +  
			"O                   1.55    \n" +  
			"F                   1.5     \n" +  
			"Ne                  1.5     \n" +  
			"Na                  2.4     \n" +  
			"Mg                  2.2     \n" +  
			"Al                  2.1     \n" +  
			"Si                  2.1     \n" +  
			"P                   1.95    \n" +  
			"S                   1.8     \n" +  
			"Cl                  1.8     \n" +  
			"Ar                  1.9     \n" +  
			"K                   2.8     \n" +  
			"Ca                  2.4     \n" +  
			"Sc                  2.3     \n" +  
			"Ti                  2.15    \n" +  
			"V                   2.05    \n" +  
			"Cr                  2.05    \n" +  
			"Mn                  2.05    \n" +  
			"Fe                  2.05    \n" +  
			"Co                  2.0     \n" +  
			"Ni                  2.0     \n" +  
			"Cu                  2.0     \n" +  
			"Zn                  2.1     \n" +  
			"Ga                  2.1     \n" +  
			"Ge                  2.1     \n" +  
			"As                  2.05    \n" +  
			"Se                  1.9     \n" +  
			"Br                  1.9     \n" +  
			"Kr                  2.0     \n" +  
			"Rb                  2.9     \n" +  
			"Sr                  2.55    \n" +  
			"Y                   2.4     \n" +  
			"Zr                  2.3     \n" +  
			"Nb                  2.152   \n" +  
			"Mo                  2.1     \n" +  
			"Tc                  2.05    \n" +  
			"Ru                  2.05    \n" +  
			"Rh                  2.0     \n" +  
			"Pd                  2.05    \n" +  
			"Ag                  2.1     \n" +  
			"Cd                  2.2     \n" +  
			"In                  2.2     \n" +  
			"Sn                  2.25    \n" +  
			"Sb                  2.2     \n" +  
			"Te                  2.1     \n" +  
			"I                   2.1     \n" +  
			"Xe                  2.2     \n" +  
			"Cs                  3.0     \n" +  
			"Ba                  2.7     \n" +  
			"La                  2.5     \n" +  
			"Ce                  2.35    \n" +  
			"Pr                  2.39    \n" +  
			"Nd                  2.29    \n" +  
			"Pm                  2.36    \n" +  
			"Sm                  2.29    \n" +  
			"Eu                  2.33    \n" +  
			"Gd                  2.37    \n" +  
			"Tb                  2.21    \n" +  
			"Dy                  2.29    \n" +  
			"Ho                  2.16    \n" +  
			"Er                  2.35    \n" +  
			"Tm                  2.27    \n" +  
			"Yb                  2.42    \n" +  
			"Lu                  2.21    \n" +  
			"Hf                  2.25    \n" +  
			"Ta                  2.2     \n" +  
			"W                   2.1     \n" +  
			"Re                  2.05    \n" +  
			"Os                  2.0     \n" +  
			"Ir                  2.0     \n" +  
			"Pt                  2.05    \n" +  
			"Au                  2.1     \n" +  
			"Hg                  2.05    \n" +  
			"Tl                  2.2     \n" +  
			"Pb                  2.3     \n" +  
			"Bi                  2.3     \n" +  
			"Po                  2.29    \n" +  
			"At                  2.36    \n" +  
			"Rn                  2.43    \n" +  
			"Fr                  2.56    \n" +  
			"Re                  2.43    \n" +  "Ra                  2.43    \n" +
			"Ac                  2.6     \n" +  
			"Th                  2.4     \n" +  
			"Pa                  2.43    \n" +  
			"U                   2.3     \n" +  
			"Np                  2.21    \n" +  
			"Pu                  2.56    \n" +  
			"Am                  2.56    \n" +  
			"Cm                  2.56    \n" +  
			"Bk                  2.56    \n" +  
			"Cf                  2.56    \n" +  
			"Es                  2.56    \n" +  
			"Fm                  2.56    \n" +  
			"Md                          \n" +  
			"No                          \n" +  
			"Lr                          \n" +  
			"Rf                          \n" +  
			"Db                          \n" +  
			"Sg                          \n" +  
			"Bh                          \n" +  
			"Hs                          \n" +  
			"Mt                          \n" +  
			"Ds                          \n" +  
			"Rg                          \n" +  
			"Cn                          \n" +  
			"Uut                         \n" +  
			"Uuq                         \n" +  
			"Uup                         \n" +  
			"Uuh                         \n" +  
			"Uus                         \n" +  
			"Uuo                         ";
}
