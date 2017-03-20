#! /bin/sh

#wget --no-check-certificate https://elibrary.isti.cnr.it/svn_public/diligent_GAR/ExternalDependencies/eXist/eXist-1.0rc2.jar

java -jar ./eXist-1.0rc2.jar -p ~/exist

export EXIST_HOME=~/exist

export GLOBUS_OPTIONS="-Dexist.home=$EXIST_HOME $GLOBUS_OPTIONS"

ln -s $EXIST_HOME/exist.jar $GLOBUS_LOCATION/lib/

ln -s $EXIST_HOME/lib/core/xmldb.jar $GLOBUS_LOCATION/lib/

ln -s $EXIST_HOME/lib/core/commons-pool-1.2.jar $GLOBUS_LOCATION/lib/

ln -s $EXIST_HOME/lib/core/xmlrpc-1.2-patched.jar $GLOBUS_LOCATION/lib/

ln -s $EXIST_HOME/lib/core/antlr.jar $GLOBUS_LOCATION/lib/

. ${GLOBUS_LOCATION}/etc/globus-devel-env.sh

#rm -rf ./eXist-1.0rc2.jar

