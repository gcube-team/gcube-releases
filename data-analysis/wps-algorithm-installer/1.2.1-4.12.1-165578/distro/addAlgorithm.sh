#!/bin/sh
INFRA_ENV=$( egrep ^INFRA_REFERENCE= /usr/local/bin/algorithms-updater | cut -d = -f 2 )/software
if [ ! -z $9 ] ; then
    INFRA_ENV=$9
fi
echo $# arguments to $0: $*
java -cp ../tomcat/webapps/wps/WEB-INF/lib/*:../tomcat/lib/*:./*:../wps_algorithms/algorithms/$INFRA_ENV  org.gcube.dataanalysis.wps.mapper.DataMinerUpdater -a$1 -l../wps_algorithms/algorithms/$INFRA_ENV -t$2 -i$3 -c../tomcat/webapps/wps/ecocfg/ -s$4 -e$5 -k$6 -u$7 -d$8
