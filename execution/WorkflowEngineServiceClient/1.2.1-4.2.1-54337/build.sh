#!/bin/bash

if [ -z "$GLOBUS_LOCATION" ]
then
export GLOBUS_LOCATION=`pwd`/../Dependencies/gCore
fi
ant jar -Ddist.jar.name=WorkflowEngineServiceClient.jar \
	-Denvironment.provider.lib=../EnvironmentProvider/dist/EnvironmentProvider.jar \
	-Dinformation.system.lib=../InformationSystem/dist/InformationSystem.jar \
	-Dstorage.system.lib=../StorageSystem/dist/StorageSystem.jar \
	-Dworkflow.engine.stubs=../WorkflowEngineService/dist/org.gcube.execution.workflowengine.service.stubs.jar
	