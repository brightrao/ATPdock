package _searcher;

import java.util.HashMap;

import _util._File;

public class FastaSeacher {
	private static String queryFa;
	private static String libFa;
	private static String saveFa;
	
	// search the queryFa corresponding information 
	// from libFa saveFa;
	public static void main(String[] args) {
		if (3 != args.length){
			System.out.println("e.g.:\n"
					+ "queryFa\n"
					+ "libFa\n"
					+ "saveFa");
			System.exit(-1);
		}
		
		queryFa = args[0];
		libFa = args[1];
		saveFa = args[2];
		
		HashMap<String, String> libHm = _File.loadFasta(libFa);
		HashMap<String, String> queryHm = _File.loadFasta(queryFa);
		HashMap<String, String> saveHm = new HashMap<String, String>();
		
		Object[] ids = queryHm.keySet().toArray();
		for (int i = 0; i < ids.length; i++){
			String content = libHm.get(ids[i]);
			if (null == content){
				System.out.println(ids[i] + " is not included in "+libFa);
			}
			
			saveHm.put((String)ids[i], content);
		}
		
		_File.writeToFASTAFile(saveHm, saveFa);
		
		System.out.println("HAVE A GOOD DAY!");
	}

}
