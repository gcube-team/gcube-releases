#!/bin/bash

if [ $# -ne 6 ]
then
  echo "ftp | gcube | container and workflow | execution argument expected"
  exit 1
fi

cd ~/MadgikExecution

if [ $4 == "container" ]
then

	export GCORE_START_OPTIONS="-Xms512M -Xmx512M"

	if [ $5 == "execution" ]
	then
		echo "stoping container"
		$GLOBUS_LOCATION/bin/gcore-stop-container > tmpOut 2>&1
		cat tmpOut
		echo "undeploying service"
		$GLOBUS_LOCATION/bin/gcore-undeploy-service org.gcube.execution.executionengine.service > tmpOut 2>&1
		cat tmpOut
		rm nohup.out
		echo "copying stubs"
		cp lib/org.gcube.execution.executionengine.service.stubs.jar $GLOBUS_LOCATION/lib
		echo "copying additional jars"
		cp lib/FTPEnvironmentProvider.jar $GLOBUS_LOCATION/lib
		cp lib/GCubeEnvironmentProvider.jar $GLOBUS_LOCATION/lib
		cp lib/LocalEnvironmentProvider.jar $GLOBUS_LOCATION/lib
		cp lib/WorkflowEngine.jar $GLOBUS_LOCATION/lib
		echo "deploying service"
		$GLOBUS_LOCATION/bin/gcore-deploy-service lib/org.gcube.execution.executionengine.service.gar > tmpOut 2>&1
		cat tmpOut
		rm tmpOut
		echo "starting container"
		nohup $GLOBUS_LOCATION/bin/gcore-start-container -p $6 > nohup.out 2>&1 &
	else
		echo "stoping container"
		$GLOBUS_LOCATION/bin/gcore-stop-container > tmpOut 2>&1
		cat tmpOut
		echo "undeploying service"
		$GLOBUS_LOCATION/bin/gcore-undeploy-service org.gcube.execution.workflowengine.service > tmpOut 2>&1
		cat tmpOut
		rm nohup.out
		echo "copying stubs"
		cp lib/org.gcube.execution.workflowengine.service.stubs.jar $GLOBUS_LOCATION/lib
		echo "copying additional jars"
		cp lib/FTPEnvironmentProvider.jar $GLOBUS_LOCATION/lib
		cp lib/GCubeEnvironmentProvider.jar $GLOBUS_LOCATION/lib
		cp lib/LocalEnvironmentProvider.jar $GLOBUS_LOCATION/lib
		cp lib/WorkflowEngine.jar $GLOBUS_LOCATION/lib
		echo "deploying service"
		$GLOBUS_LOCATION/bin/gcore-deploy-service lib/org.gcube.execution.workflowengine.service.gar > tmpOut 2>&1
		cat tmpOut
		rm tmpOut
		echo "starting container"
		nohup $GLOBUS_LOCATION/bin/gcore-start-container -p $6 > nohup.out 2>&1 &
	fi
else
	PSOUT=`ps -ef | grep "java.* -cp .:./lib/ExecutionEngine.jar" | grep -v "grep" | awk '{print $2}'`
	if [ -n "$PSOUT" ]; then
		echo "Killing previous process"
		echo $PSOUT
		kill -9 $PSOUT
	fi
	rm nohup.out
	nohup ./StartExecutionEngineBoundary.sh $1 $2 $3 $4 > nohup.out 2>&1 &
fi


