#!/bin/sh
# StartJDLAdaptorTest.sh

#if [ $# -lt 4 ]
#then
#	echo "at least four arguments expected"
#	exit 1
#fi

if [ "$4" = "gcube" ]
then
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
fi

CP=.:./lib/ExecutionEngine.jar:./lib/grs2.jar:./lib/InformationSystem.jar:./lib/StorageSystem.jar:./lib/MadgikCommons.jar:./lib/WorkflowEngine.jar:./lib/EnvironmentProvider.jar:./lib/LocalEnvironmentProvider.jar:./lib/FTPEnvironmentProvider.jar:./lib/GCubeEnvironmentProvider.jar

if [ -z "$CLASSPATH" ] ; then
  CLASSPATH=$CP
else
  CLASSPATH=$CP:$CLASSPATH
fi

java -cp $CLASSPATH gr.uoa.di.madgik.workflow.test.TestJDLAdaptor $*
