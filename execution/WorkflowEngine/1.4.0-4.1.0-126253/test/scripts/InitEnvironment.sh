#!/bin/sh
# InitEnvironment.sh

if [ $# -ne 1 ]
then
  echo "ftp | gcube | container single argument expected"
  exit 1
fi

sftp gpapanikos@dl22.di.uoa.gr << EOF
mkdir ./MadgikExecution/
mkdir ./MadgikExecution/lib/
cd ./MadgikExecution/lib/
put ../../../ExecutionEngine/dist/ExecutionEngine.jar
put ../../../gRS2/dist/grs2.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../MadgikCommons/dist/MadgikCommons.jar
put ../../../WorkflowEngine/dist/WorkflowEngine.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
put ../../../WorkflowEngineService/dist/org.gcube.execution.workflowengine.service.gar
put ../../../WorkflowEngineService/dist/org.gcube.execution.workflowengine.service.stubs.jar
cd ..
put ../../../WorkflowEngine/test/scripts/StartGridAdaptorTest.sh
chmod 0744 StartGridAdaptorTest.sh
put ../../../WorkflowEngine/test/scripts/StartCondorAdaptorTest.sh
chmod 0744 StartCondorAdaptorTest.sh
put ../../../WorkflowEngine/test/scripts/StartJDLAdaptorTest.sh
chmod 0744 StartJDLAdaptorTest.sh
put ../../../WorkflowEngine/test/scripts/StartHadoopAdaptorTest.sh
chmod 0744 StartHadoopAdaptorTest.sh
put ../../../WorkflowEngine/test/scripts/Readme.txt
mkdir ./Examples
mkdir ./Examples/jdl/
mkdir ./Examples/grid/
mkdir ./Examples/hadoop/
mkdir ./Examples/UserProxy/
cd ./Examples/jdl/
put ../jdlExamples/jdlExample2.jdl
put ../jdlExamples/jdlExample3.jdl
put ../jdlExamples/jdlExample4.jdl
put ../jdlExamples/job.sh
put ../jdlExamples/sig.txt
put ../jdlExamples/Concat.sh
put ../jdlExamples/resources.example2.txt
put ../jdlExamples/resources.example3.txt
put ../jdlExamples/resources.example4.txt
cd ../grid/
put ../gridExamples/test.jdl
put ../gridExamples/test.sh
put ../gridExamples/wms.cnr.test.config
put ../gridExamples/resources.test.txt
cd ../hadoop/
put ../../../MapReduceCallablesTesting/dist/MapReduceCallablesTesting.jar
put ../hadoopExamples/resources.test1.txt
put ../hadoopExamples/resources.test2.txt
put ../hadoopExamples/All_About_Coffee_by_William_H._Ukers.txt
put ../hadoopExamples/ExampleText.txt
cd ../..
put ../../../WorkflowEngine/test/scripts/Restart.sh
chmod 0744 Restart.sh
mkdir wsclient
cd wsclient
mkdir lib
cd lib
put ../../../WorkflowEngineService/dist/org.gcube.execution.workflowengine.service.stubs.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../WorkflowEngineServiceClient/dist/WorkflowEngineServiceClient.jar
cd ..
put ../../../WorkflowEngineServiceClient/test/scripts/Readme.txt
put ../../../WorkflowEngineServiceClient/test/scripts/RetrieveFile.sh
chmod 0744 RetrieveFile.sh
put ../../../WorkflowEngineServiceClient/test/scripts/StartGridAdaptorTest.sh
chmod 0744 StartGridAdaptorTest.sh
put ../../../WorkflowEngineServiceClient/test/scripts/StartCondorAdaptorTest.sh
chmod 0744 StartCondorAdaptorTest.sh
put ../../../WorkflowEngineServiceClient/test/scripts/StartHadoopAdaptorTest.sh
chmod 0744 StartHadoopAdaptorTest.sh
put ../../../WorkflowEngineServiceClient/test/scripts/StartJDLAdaptorTest.sh
chmod 0744 StartJDLAdaptorTest.sh
put ../../../WorkflowEngineServiceClient/test/scripts/StartJobMonitor.sh
chmod 0744 StartJobMonitor.sh
mkdir test
cd test
mkdir gridExamples
cd gridExamples
put ../../../WorkflowEngineServiceClient/test/gridExamples/resources.test.txt
put ../../../WorkflowEngineServiceClient/test/gridExamples/test.jdl
put ../../../WorkflowEngineServiceClient/test/gridExamples/test.sh
put ../../../WorkflowEngineServiceClient/test/gridExamples/userProxy
put ../../../WorkflowEngineServiceClient/test/gridExamples/wms.cnr.test.config
cd ..
mkdir condorExamples
cd condorExamples
put ../../../WorkflowEngineServiceClient/test/condorExamples/resources.test.txt
put ../../../WorkflowEngineServiceClient/test/condorExamples/simple
put ../../../WorkflowEngineServiceClient/test/condorExamples/simple.c
put ../../../WorkflowEngineServiceClient/test/condorExamples/submit.condor
put ../../../WorkflowEngineServiceClient/test/condorExamples/submit.dag.condor
put ../../../WorkflowEngineServiceClient/test/condorExamples/submit1.condor
put ../../../WorkflowEngineServiceClient/test/condorExamples/resources1.test.txt
put ../../../WorkflowEngineServiceClient/test/condorExamples/resources.dag.test.txt
put ../../../WorkflowEngineServiceClient/test/condorExamples/job.work2.submit
put ../../../WorkflowEngineServiceClient/test/condorExamples/job.work1.submit
put ../../../WorkflowEngineServiceClient/test/condorExamples/job.setup.submit
put ../../../WorkflowEngineServiceClient/test/condorExamples/job.finalize.submit
cd ..
mkdir jdlExamples
cd jdlExamples
put ../../../WorkflowEngineServiceClient/test/jdlExamples/Concat.sh
put ../../../WorkflowEngineServiceClient/test/jdlExamples/jdlExample2.jdl
put ../../../WorkflowEngineServiceClient/test/jdlExamples/jdlExample3.jdl
put ../../../WorkflowEngineServiceClient/test/jdlExamples/jdlExample4.jdl
put ../../../WorkflowEngineServiceClient/test/jdlExamples/job.sh
put ../../../WorkflowEngineServiceClient/test/jdlExamples/resources.example2.txt
put ../../../WorkflowEngineServiceClient/test/jdlExamples/resources.example3.txt
put ../../../WorkflowEngineServiceClient/test/jdlExamples/resources.example4.txt
cd ..
mkdir hadoopExamples
cd hadoopExamples
put ../../../WorkflowEngineServiceClient/test/hadoopExamples/All_About_Coffee_by_William_H._Ukers.txt
put ../../../WorkflowEngineServiceClient/test/hadoopExamples/ExampleText.txt
put ../../../WorkflowEngineServiceClient/test/hadoopExamples/resources.test2.txt
quit
EOF

sftp gpapanikos@dl05.di.uoa.gr << EOF
mkdir ./MadgikExecution/
mkdir ./MadgikExecution/lib/
cd ./MadgikExecution/lib/
put ../../../ExecutionEngine/dist/ExecutionEngine.jar
put ../../../gRS2/dist/grs2.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../MadgikCommons/dist/MadgikCommons.jar
put ../../../WorkflowEngine/dist/WorkflowEngine.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
cd ..
put ../../../ExecutionEngine/test/scripts/StartExecutionEngineBoundary.sh
chmod 0744 StartExecutionEngineBoundary.sh
put ../../../WorkflowEngine/test/scripts/Restart.sh
chmod 0744 Restart.sh
quit
EOF

sftp root@88.197.20.240 << EOF
mkdir ./MadgikExecution/
mkdir ./MadgikExecution/lib/
cd ./MadgikExecution/lib/
put ../../../ExecutionEngine/dist/ExecutionEngine.jar
put ../../../gRS2/dist/grs2.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../MadgikCommons/dist/MadgikCommons.jar
put ../../../WorkflowEngine/dist/WorkflowEngine.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
cd ..
put ../../../ExecutionEngine/test/scripts/StartExecutionEngineBoundary.sh
chmod 0744 StartExecutionEngineBoundary.sh
put ../../../WorkflowEngine/test/scripts/Restart.sh
chmod 0744 Restart.sh
quit
EOF

sftp condor@88.197.20.246 << EOF
mkdir ./MadgikExecution/
mkdir ./MadgikExecution/lib/
cd ./MadgikExecution/lib/
put ../../../ExecutionEngine/dist/ExecutionEngine.jar
put ../../../gRS2/dist/grs2.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../MadgikCommons/dist/MadgikCommons.jar
put ../../../WorkflowEngine/dist/WorkflowEngine.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
cd ..
put ../../../ExecutionEngine/test/scripts/StartExecutionEngineBoundary.sh
chmod 0744 StartExecutionEngineBoundary.sh
put ../../../WorkflowEngine/test/scripts/Restart.sh
chmod 0744 Restart.sh
quit
EOF

sftp gpapanikos@dl13.di.uoa.gr << EOF
mkdir ./MadgikExecution/
mkdir ./MadgikExecution/lib/
cd ./MadgikExecution/lib/
put ../../../ExecutionEngine/dist/ExecutionEngine.jar
put ../../../gRS2/dist/grs2.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../MadgikCommons/dist/MadgikCommons.jar
put ../../../WorkflowEngine/dist/WorkflowEngine.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
cd ..
put ../../../ExecutionEngine/test/scripts/StartExecutionEngineBoundary.sh
chmod 0744 StartExecutionEngineBoundary.sh
put ../../../WorkflowEngine/test/scripts/Restart.sh
chmod 0744 Restart.sh
quit
EOF

sftp gpapanikos@dl14.di.uoa.gr << EOF
mkdir ./MadgikExecution/
mkdir ./MadgikExecution/lib/
cd ./MadgikExecution/lib/
put ../../../ExecutionEngine/dist/ExecutionEngine.jar
put ../../../gRS2/dist/grs2.jar
put ../../../InformationSystem/dist/InformationSystem.jar
put ../../../StorageSystem/dist/StorageSystem.jar
put ../../../MadgikCommons/dist/MadgikCommons.jar
put ../../../WorkflowEngine/dist/WorkflowEngine.jar
put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
cd ..
put ../../../ExecutionEngine/test/scripts/StartExecutionEngineBoundary.sh
chmod 0744 StartExecutionEngineBoundary.sh
put ../../../WorkflowEngine/test/scripts/Restart.sh
chmod 0744 Restart.sh
quit
EOF

# sftp gpapanikos@dl15.di.uoa.gr << EOF
# mkdir ./MadgikExecution/
# mkdir ./MadgikExecution/lib/
# cd ./MadgikExecution/lib/
# put ../../../ExecutionEngine/dist/ExecutionEngine.jar
# put ../../../gRS2/dist/grs2.jar
# put ../../../InformationSystem/dist/InformationSystem.jar
# put ../../../StorageSystem/dist/StorageSystem.jar
# put ../../../MadgikCommons/dist/MadgikCommons.jar
# put ../../../WorkflowEngine/dist/WorkflowEngine.jar
# put ../../../EnvironmentProvider/dist/EnvironmentProvider.jar
# put ../../../FTPEnvironmentProvider/dist/FTPEnvironmentProvider.jar
# put ../../../LocalEnvironmentProvider/dist/LocalEnvironmentProvider.jar
# put ../../../GCubeEnvironmentProvider/dist/GCubeEnvironmentProvider.jar
# put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.gar
# put ../../../ExecutionEngineService/dist/org.gcube.execution.executionengine.service.stubs.jar
# cd ..
# put ../../../ExecutionEngine/test/scripts/StartExecutionEngineBoundary.sh
# chmod 0744 StartExecutionEngineBoundary.sh
# put ../../../WorkflowEngine/test/scripts/Restart.sh
# chmod 0744 Restart.sh
# quit
# EOF

# ./CleanIS.sh
./RestartContainers.sh $1
