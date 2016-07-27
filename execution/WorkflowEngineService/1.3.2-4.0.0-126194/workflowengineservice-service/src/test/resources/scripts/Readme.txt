The client assumes the following hierarchy

baseDir
|
|__lib
|   |__EnvironmentProvider.jar
|   |__GCubeEnvironmentProvider.jar
|   |__InformationSystem.jar
|   |__org.gcube.execution.workflowengine.service.stubs.jar
|   |__StorageSystem.jar
|   |__WorkflowEngineServiceClient.jar
|
|__test
|   |__condorExamples
|   |      |__job.finalize.submit
|   |      |__job.setup.submit
|   |      |__job.work1.submit
|   |      |__job.work2.submit
|   |      |__resources1.test.txt
|   |      |__resources.dag.test.txt
|   |      |__resources.test.txt
|   |      |__simple.c
|   |      |__submit1.condor
|   |      |__submit.condor
|   |      |__submit.dag.condor
|   |
|   |__gridExamples
|   |      |__resources.test.txt
|   |      |__test.jdl
|   |      |__test.sh
|   |      |__userProxy
|   |
|   |__hadoopExamples
|   |      |__ExampleText.txt
|   |      |__resources.test2.txt
|   |
|   |__jdlExamples
|          |__Concat.sh
|          |__jdlExample2.jdl
|          |__jdlExample3.jdl
|          |__jdlExample4.jdl
|          |__job.sh
|          |__resources.example2.txt
|          |__resources.example3.txt
|          |__resources.example4.txt
|    
|__RetrieveFile.sh
|__StartCondorAdaptorTest.sh
|__StartGridAdaptorTest.sh
|__StartHadoopAdaptorTest.sh
|__StartJDLAdaptorTest.sh
|__StartJobMonitor.sh

You should have gCore container installed and have set GLOBUS_LOCATION. In $GLOBUS_LOCATION/lib the jars of collection management stubs, content management stubs and the content layer library must be present.
Edit the gCore container to support the scopes you want to perform the tests in. Also make sure that the scope defined in the respective resource files much one of the supported ones depending on your container configuration.

The template for execution one of the example is the following:

./StartCondorAdaptorTest.sh <location of the resource file> <path to file that will store the execution identifier>
./StartGridAdaptorTest.sh <location of the resource file> <path to file that will store the execution identifier>
./StartJDLAdaptorTest.sh <location of the resource file> <path to file that will store the execution identifier>
./StartHadoopAdaptorTest.sh <location of the resource file> <path to file that will store the execution identifier>
./StartJobMonitor.sh <path to file that stores the execution identifier> <true to retrieve the created plan, false otherwise> <true to continue monitoring the execution, false to check and exit>
./RetrieveFile.sh <object identifier> <scope of execution that created the object>

Calling any of the above scripts without arguments you will get a help printout

An example of a series of invocations is the following:

[gpapanikos@dl22 wsclient]$ ./StartJDLAdaptorTest.sh
	initializing gCube container environment
	invoking script : /home/gpapanikos/gCore/etc/globus-devel-env.sh
	Usage:
	Two arguments are needed
	1) the path of the resource file. The syntax of the resource file is the following:
	        scope : <the scope to use>
	        jdl : <path to the jdl file>
	        chokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)
	        chokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)
	        storePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)
	        <name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the path / id / url to retrieve the paylaod from>
	        <name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the paath / id / url to retrieve the paylaod from>
	        [...]2) the path of the output file that will contain the execution identifier
[gpapanikos@dl22 wsclient]$ ./StartJDLAdaptorTest.sh jdlExamples/resources.example2.txt /tmp/ExecutionIdentifier.txt
	initializing gCube container environment
	invoking script : /home/gpapanikos/gCore/etc/globus-devel-env.sh
	Initializing Information System
	Initializing Storage System
	resources file used : jdlExamples/resources.example2.txt
	output execution id : /tmp/ExecutionIdentifier.txt
	Locating Workflow Engine
	Selected Workflow Engine http://dl22.di.uoa.gr:8080/wsrf/services/gcube/execution/workflowengine
	Submiting execution
	Execution ID : 69098a23-28b9-49c9-815f-634217eb1ac9
[gpapanikos@dl22 wsclient]$ ./StartJobMonitor.sh
	initializing gCube container environment
	invoking script : /home/gpapanikos/gCore/etc/globus-devel-env.sh
	Usage:
	Three arguments are needed
	1) the path of the file that containing the execution identifier
	2) true | false whether the execution plan should be retrieved
	3) true | false whether the program should continue monitoring the execution until it is completed
[gpapanikos@dl22 wsclient]$ ./StartJobMonitor.sh /tmp/ExecutionIdentifier.txt false true
	initializing gCube container environment
	invoking script : /home/gpapanikos/gCore/etc/globus-devel-env.sh
	Contacting : http://dl22.di.uoa.gr:8080/wsrf/services/gcube/execution/workflowengine
	Execution identifier : 69098a23-28b9-49c9-815f-634217eb1ac9
	Scope : /gcube/devsec
	Sending Request
	Processing Report
	...
	...
	...
	Execution has completed
	Job output :
	key : job.err subkey : Not available StorageSystem ID : c341cc00-5381-11df-a895-8a721f837bfd
	key : job.out subkey : Not available StorageSystem ID : c2d80db0-5381-11df-a895-8a721f837bfd
	key : job.output subkey : Not available StorageSystem ID : c3b17dc0-5381-11df-a895-8a721f837bfd
[gpapanikos@dl22 wsclient]$ ./RetrieveFile.sh
	initializing gCube container environment
	invoking script : /home/gpapanikos/gCore/etc/globus-devel-env.sh
	Usage:
	Two arguments are needed
	1) The object identifier
	2) The scope of the execution that created the identifier
[gpapanikos@dl22 wsclient]$ ./RetrieveFile.sh c341cc00-5381-11df-a895-8a721f837bfd /gcube/devsec
	initializing gCube container environment
	invoking script : /home/gpapanikos/gCore/etc/globus-devel-env.sh
	Initializing Information System
	Initializing Storage System
	Contacting Storage System
	Retrieved content stored in /tmp/65d720bc-909e-41c7-afbf-f805c01d9599.ss.tmp
[gpapanikos@dl22 wsclient]$ cat /tmp/65d720bc-909e-41c7-afbf-f805c01d9599.ss.tmp
	hello World of stdErr
	Hello_World_Of_Diligent...
