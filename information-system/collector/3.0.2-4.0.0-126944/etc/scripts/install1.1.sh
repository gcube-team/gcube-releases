#! /bin/sh

#wget --no-check-certificate http://grids17.eng.it/engrepository/exist/1.1.1/noarch/eXist-1.1.1-newcore-build4311.tar.gz

tar zxvf eXist-1.1.1-newcore-build4311.tar.gz


java -jar ./eXist-1.1.1-newcore-build4311.jar -p ~/exist1.1

export EXIST_HOME=~/exist1.1

export GLOBUS_OPTIONS="-Dexist.home=$EXIST_HOME $GLOBUS_OPTIONS"

ln -sf $EXIST_HOME/exist.jar $GLOBUS_LOCATION/lib/

ln -sf $EXIST_HOME/lib/core/xmldb.jar $GLOBUS_LOCATION/lib/

ln -sf $EXIST_HOME/lib/core/commons-pool-1.2.jar $GLOBUS_LOCATION/lib/

ln -sf $EXIST_HOME/lib/core/xmlrpc-1.2-patched.jar $GLOBUS_LOCATION/lib/

ln -sf $EXIST_HOME/lib/core/antlr-2.7.6.jar $GLOBUS_LOCATION/lib/

. ${GLOBUS_LOCATION}/etc/globus-devel-env.sh

rm -rf ./eXist-1.1.1-newcore-build4311.jar

