#!/bin/sh

JAVA_FLAGS=-Dcom.sun.management.jmxremote
if [ "$GLOBUS_LOCATION" = "" ]
then
  echo "Env. variable GLOBUS_LOCATION is not set";
  exit 1;
fi

if [ "$JAVA_HOME" = "" ]
then
  echo "Env. variable JAVA_HOME is not set";
  exit 1;
fi

$GLOBUS_LOCATION/etc/globus-devel-env.sh
for i in $JAVA_HOME/lib/*.jar
do
  export CLASSPATH="$CLASSPATH":"$i"
done

java ${JAVA_FLAGS} org.gcube.informationsystem.cache.ISCacheManager &
pid=`ps -ef | grep java | grep org.gcube.informationsystem.cache.ISCacheManager | awk '{ print $2 }' | head -1`
jconsole -J-Djava.class.path=$CLASSPATH ${pid} &
