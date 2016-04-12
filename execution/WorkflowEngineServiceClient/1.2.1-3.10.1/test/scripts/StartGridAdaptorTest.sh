#!/bin/sh
# StartGridAdaptorTest.sh

echo "initializing gCube container environment"
if [ -z "${GLOBUS_LOCATION}" ]; then
	echo "ERROR: environment variable GLOBUS_LOCATION not defined"  1>&2
	exit 1
fi

if [ ! -d "${GLOBUS_LOCATION}" ]; then
	echo "ERROR: invalid GLOBUS_LOCATION set: $GLOBUS_LOCATION" 1>&2
	return 1
fi
echo "invoking script : "${GLOBUS_LOCATION}/etc/globus-devel-env.sh
source ${GLOBUS_LOCATION}/etc/globus-devel-env.sh

CP=.:./lib/InformationSystem.jar:./lib/StorageSystem.jar:./lib/EnvironmentProvider.jar:./lib/GCubeEnvironmentProvider.jar:./lib/org.gcube.execution.workflowengine.service.stubs.jar:./lib/WorkflowEngineServiceClient.jar

if [ -z "$CLASSPATH" ] ; then
  CLASSPATH=$CP
else
  CLASSPATH=$CP:$CLASSPATH
fi

java -cp $CLASSPATH org.gcube.execution.workflowengine.service.test.TestGridAdaptor $*
