package process.GenerateDataset;

import java.io.*;
import java.util.*;

import _BioStruct.*;
import _downloadUrl.DownloadFromNetAddr;
import _runner.CDHitRunner;
import _runner.OpenBabelRunner;
import _util.AminoAcid;
import _util.AtomFixInfo;
import _util.NWAlign;
import _util._File;
import _util._Log;
import _util._NumFormat;
import _util._Str;
import process.FixParams;

public class ConstructPocLigLib_Supp {
	
	private static String save_poc_lig_prot_info = "/data/hujun/academic/PPSalign_workshop/database/db_poclig_info_cd99.list";
	
	private static String savefolder = "/data/hujun/academic/PPSalign_workshop/database";
	private static String lig_folder = savefolder + "/lig";
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(save_poc_lig_prot_info));
		String l = br.readLine();
		while (null != l) {
			String[] lc = l.split("\t+");
			
			String poc_name = lc[1]+"_"+lc[2]+"_"+lc[3];
			try {
				OpenBabelRunner.transferLigInfoTypes("pdb", lig_folder+"/"+poc_name+".pdb", 
						"mol2", lig_folder+"/"+poc_name+".mol2");				
			}catch(Exception e) {
				e.printStackTrace();
				l = br.readLine();
				continue;
			}
			
			l = br.readLine();
		}
		
		br.close();
	}
}
