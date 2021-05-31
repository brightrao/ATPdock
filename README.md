# ATPdock: 
A template-based method for ATP-specific protein-ligand docking.

## Pre-requisite:
    - Python, Java
    - MGLTools software (http://mgltools.scripps.edu/downloads)
    - Open Babel software (http://openbabel.org/wiki/Category:Installation)
    - PL-DB database (https://github.com/brightrao/PL-DB1/tree/master, https://github.com/bright197/PL-DB2, https://github.com/brightzjut/PL-DB3)
    - Linux system

## Installation:

*Download this repository at https://github.com/brightrao/ATPdock.git.

1. Accessing basefile folder, perform bellow opration.

    1.1 install MGLTools program, which is available http://mgltools.scripps.edu/downloads. 
      
      1.1.1 accessing /mgltools_x86_64Linux2_1.5.6/bin, copy 'pythonsh' file to basefile folder.
      
      1.1.2 accessing /mgltools_x86_64Linux2_1.5.6/MGLToolsPckgs/AutoDockTools/Utilities24, copy 'prepare_ligand4.py' and 'prepare_receptor4.py' to basefile folder.

    1.2 install Open Babel program, which is available http://openbabel.org/wiki/Category:Installation.
    
2. Creating pocket-ligand database(PLDB).

    2.1 Download pocket-ligand database(PLDB) from three address,
        https://github.com/brightrao/PL-DB1/tree/master, 
        https://github.com/bright197/PL-DB2, 
        https://github.com/brightzjut/PL-DB3. 		
    2.2 Creating a new "Database" folder under the path "ATPdock/PPS-search/" and put these files in this folder.
     
    2.3 Extracting the total compressed file, merge 10 compressed files about pocket, i.e. poc1 to poc10, into one file and name it "poc".
     
    2.4 In ATPdock/PPS-search/database, "database" folder should contain three file, i.e. poc, lig folder and "db_poclig_info_cd99.list" file.
    
3. Qualify Linux system has python3 version, and include 'os', 'math', 'numpy', 'random', 'subprocess', 'sys', 'shutil' package. If a packege does not exist, using 'pip3 install xxxx' command install python revelant package. "xxxx" is name of the package.
  
## Docking files preparation:

Creating a folder, it contain three file, e.g., pdb.pdb, tem.txt and pdb.site. userpath is the absolute path to the docking folder.

    1. pdb.pdb is receptor structure
    2. tem.txt has two lines
     first line is sequence identity, when searching template pocket.   
     second line is searched ligand type,~ATP~~ADP~,eg. if not, NULL.
    3. pdb.site is binding residues type and index, every line means every pocket, user can define multiple binding pockets of the protein.
      for example, a protein have two binding pockets, there are two lines.
      V10 G38 C39 G40 R61 P89 G90 D91 G92 K93
      E65 I66 V67 N104 R106
   
Note that, because ATPbind depend on several large database(more than 5G disk memory), and it can be using by webserver, ATPdock standalone program has not support ATPbind standalone program. You can access ATPbind server(https://zhanglab.ccmb.med.umich.edu/ATPbind/), obtain ATP binding residues of the receptor, write pdb.site according under fasta.
     
## Run example
~~~
  $ python3 ATPdock.py userpath
~~~
The outputted "ATPx" folder, "x" is a number. In the folder, "final.pdb" is the docking result.

## Update History:

- First release 2021-05-31

## References

[1] Liang Rao, Ning-Xin Jia, Jun Hu, Dong-Jun Yu, and Gui-Jun Zhang. ATPdock: a template-based method for ATP-specific protein-ligand docking. Bioinformatics. sumitted.
