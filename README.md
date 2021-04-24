# ATPdock

How to prepare ATPdock work environment?(Must be Linux System)
1 Accessing basefile folder, perform bellow opration
	1.1 install mgltools program, which is available http://mgltools.scripps.edu/downloads
		1.1.1 Accessing /mgltools_x86_64Linux2_1.5.6/bin, copy 'pythonsh' file to basefile folder.
		1.1.2 enter /mgltools_x86_64Linux2_1.5.6/MGLToolsPckgs/AutoDockTools/Utilities24, copy 'prepare_ligand4.py' and 'prepare_receptor4.py' to basefile folder.
	1.2 install OpenBabel program, which is available http://openbabel.org/wiki/Category:Installation
2 Download pocket-ligand database(PLDB) from xxxx, compress in ATPdock/PPS-search, obtain ATPdock/PPS-search/database
3 qualify Linux system has python3 version, and include 'os', 'math', 'numpy', 'random', 'subprocess', 'sys', 'shutil' package. 
  If not, using 'pip3 install xxxx' command install python revelant package.

