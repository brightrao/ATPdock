How to prepare ATPdock work environment?(Must be Linux System)
1 Accessing basefile folder, perform bellow opration
    1.1 install mgltools program, which is available http://mgltools.scripps.edu/downloads
	    1.1.1 Accessing /mgltools_x86_64Linux2_1.5.6/bin, copy 'pythonsh' file to basefile folder.
	    1.1.2 enter /mgltools_x86_64Linux2_1.5.6/MGLToolsPckgs/AutoDockTools/Utilities24, copy 'prepare_ligand4.py' and 'prepare_receptor4.py' to basefile folder.
    1.2 install OpenBabel program, which is available http://openbabel.org/wiki/Category:Installation
2 Download pocket-ligand database(PLDB) from xxxx, compress in ATPdock/PPS-search, obtain ATPdock/PPS-search/database
3 qualify Linux system has python3 version, and include 'os', 'math', 'numpy', 'random', 'subprocess', 'sys', 'shutil' package. 
  If not, using 'pip3 install xxxx' command install python revelant package.
------------------------------------------------------------------------------------
How to prepare docking file?
creating a folder, it contain three file, e.g., pdb.pdb, tem.txt and pdb.site.
userpath is the folder path.
1. pdb.pdb is receptor structure

2. tem.txt has two lines
     first line is sequence identity, when searching template pocket.
     second line is searched ligand type,~ATP~~ADP~,eg. if not, NULL.

3. pdb.site is binding residues type and index, every line means every pocket, user can define multiple binding pockets of the protein.
     for example, a protein have two binding pockets, there are two lines.
     V10 G38 C39 G40 R61 P89 G90 D91 G92 K93
     E65 I66 V67 N104 R106
   
   If you want to use ATPbind predict binding pockets.
     Because ATPbind depend on several large database(more than 5G disk memory), and it can be using by webserver, ATPdock standalone program has not support ATPbind standalone program.
     You can access ATPbind server(https://zhanglab.ccmb.med.umich.edu/ATPbind/) ,obtain ATP binding residues of the receptor, write pdb.site according under fasta.
------------------------------------------------------------------------------------
How to run ATPdock?
cd xx/xx/ATPdock
python3 ATPdock.py userpath 
------------------------------------------------------------------------------------
