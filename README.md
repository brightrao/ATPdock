# ATPdock: 
A template-based method for ATP-specific protein-ligand docking.

## Pre-requisite:
    - Python, Java, Perl
    - MGLTools software (http://mgltools.scripps.edu/downloads)
    - Open Babel software (http://openbabel.org/wiki/Category:Installation)
    - PL-DB database (https://github.com/brightrao/PL-DB1/tree/master, https://github.com/bright197/PL-DB2, https://github.com/brightzjut/PL-DB3)
    - Linux system

## Installation:

*Download this repository at https://github.com/brightrao/ATPdock.git and PL-DB database from 3 network address. Then, uncompress it and run the following command lines on Linux System.
~~~
  $ cd ./tools/
  $ tar zxvf blast-2.2.26.tar.gz
  $ tar zxvf junh_BlastpgpSSITEOutputPARSER.tar.gz
  $ tar zxvf psipred321.tar.gz
  $ cd ..
  $ java -jar FileUnion.jar ./model/dbs/ ./model/dbs/dbs.mod
  $ java -jar FileUnion.jar ./model/dbp/ ./model/dbp/targetdbpplus.mod
~~~

*The file of “Config.properties” should be set as follows:
~~~
TARGETDBPPLUS_PRED_MODEL=./model/dbp/targetdbpplus.mod
DBS_PRED_MODEL=./model/dbs/dbs.mod
PSIPRED321_FOLDER_DIR=./tools/psipred321
BLAST_BIN_DIR=./tools/blast-2.2.26
BLASTPGP_EXE_PATH=./tools/blast-2.2.26/blastpgp
BLASTPGP_SSITE_OUTPUT_PARSER_DIR=./tools/junh_BlastpgpSSITEOutputPARSER

BLASTPGP_DB_PATH=xx/nr
SANN_RUNNER_PATH=yy/SANN/sann/bin/sann.sh
~~~

Note that, "xx" should be the absolute path of the downloaed NCBI nr90 database. "yy" should be the absolute path of the installed SANN software.

## Run example
~~~
  $ java -jar TargetDBP+.jar ./example/
~~~
The outputted probability of belonging to the class of DBPs of query sequences could be found at "./example/querys.jun_res"

## Update History:

- First release 2021-05-31

## References

[1] Liang Rao, Ning-Xin Jia, Jun Hu, Dong-Jun Yu, and Gui-Jun Zhang. ATPdock: a template-based method for ATP-specific protein-ligand docking. Bioinformatics. sumitted.
