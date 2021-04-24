package process.GenerateDataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import _util._File;

public class SelectInfoFromListBasedFasta {

	private static String fa_path = "/data/hujun/academic/ATPdock/data/db_atpbind_info.list_remove_NW40_4ATPbindTrain_cd40.fa";
	private static String list = "/data/hujun/academic/ATPdock/data/db_atpbind_info.list";
	private static String save_list = "/data/hujun/academic/ATPdock/data/db_atpbind_info.list_remove_NW40_4ATPbindTrain_cd40.list"; 
	
	public static void main(String[] args) throws Exception {
		HashMap<String, String> hm = _File.loadFasta(fa_path, false);
		
		FileWriter fw = new FileWriter(save_list);
		
		BufferedReader br = new BufferedReader(new FileReader(list));
		String l = br.readLine();
		while (null != l) {
			String[] lc = l.split("	+|\t+");
			
			String name = lc[1];
			if (null != hm.get(name)) {
				fw.write(l + "\n");
			}
			
			l = br.readLine();
		}
		br.close();
		fw.close();
	}

}
