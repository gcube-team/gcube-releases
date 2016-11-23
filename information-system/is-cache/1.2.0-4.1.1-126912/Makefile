port=6543
hostname=195.134.66.115

all: jar javadoc

jar:
	ant -Dglobus.location=${GLOBUS_LOCATION} jar

build: 
	ant -Dglobus.location=${GLOBUS_LOCATION} build

clean:
	ant -Dglobus.location=${GLOBUS_LOCATION} clean

javadoc:
	ant -Dglobus.location=${GLOBUS_LOCATION} javadoc

run:
	java -Dcom.sun.management.jmxremote.port=${port} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=${hostname} -Djmx.invoke.getters "org/gcube/commons/is/cache/ISCacheManager"