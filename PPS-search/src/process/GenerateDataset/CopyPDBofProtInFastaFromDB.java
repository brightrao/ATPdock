package process.GenerateDataset;

import java.io.File;
import java.util.HashMap;

import _util._File;

public class CopyPDBofProtInFastaFromDB {

	private static String seqfa = "/data/hujun/academic/ATPdock/data/db_atpbind_info.list_remove_NW40_4ATPbindTrain_cd40.fa";
	private static String save_folder = "/data/hujun/academic/ATPdock/data/pdb_pdb"; 
	
	private static final String db_folder = "/data/hujun/academic/ATPdock/database/prot"; 
	
	public static void main(String[] args) {
		
		if (!new File(save_folder).exists()) {
			new File(save_folder).mkdirs();
		}
		
		HashMap<String, String> hm = _File.loadFasta(seqfa, false);
		Object[] ids = hm.keySet().toArray();
		
		for (int i = 0; i < ids.length; i++) {
			String name = (String)ids[i];
			
			String old_path = db_folder + "/" + name + ".pdb";
			String save_path = save_folder + "/" + name + ".pdb";
			
			System.out.println(name + " is copying");
			_File.copy(old_path, save_path);
		}
		
		
		System.out.println("END");
	}

}
