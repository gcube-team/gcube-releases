4 examples are provided as explained on the WIKI. To run the example, use the following commands :

The code running in the testing host needs to have a port in which it can accept incoming connections. This is the second argument in the command line. 
Specifying 0 means that a random port can be used. Otherwise you need to specify a non firewalled port.
 
Example of submitting a job to the Grid.
To run this example you need to provide a user proxy in the folder Examples/UserProxy. (IMPORTANT currently the transport of this proxy is NOT be secure)
WIKI : http://technical.wiki.d4science.research-infrastructures.eu/documentation/index.php/WorkflowGridAdaptor

./StartGridAdaptorTest.sh <host machine running this example> <port in this machine that can receive incoming tcp connection or 0 for random> Examples/grid/resources.test.txt <ftp | gcube depending on the infrastructure set up>

Example of submitting JDL described jobs in d4science nodes.
WIKI : http://technical.wiki.d4science.research-infrastructures.eu/documentation/index.php/WorkfowJDLAdaptor

./StartJDLAdaptorTest.sh <host machine running this example> <port in this machine that can receive incoming tcp connection or 0 for random> Examples/jdl/resources.example2.txt <ftp | gcube depending on the infrastructure set up>
./StartJDLAdaptorTest.sh <host machine running this example> <port in this machine that can receive incoming tcp connection or 0 for random> Examples/jdl/resources.example3.txt <ftp | gcube depending on the infrastructure set up>
./StartJDLAdaptorTest.sh <host machine running this example> <port in this machine that can receive incoming tcp connection or 0 for random> Examples/jdl/resources.example4.txt <ftp | gcube depending on the infrastructure set up>

Example of submitting a job to the Hadoop infrastructure
WIKI : http://technical.wiki.d4science.research-infrastructures.eu/documentation/index.php/WorkfowHadoopAdaptor

./StartHadoopAdaptorTest.sh <host machine running this example> <port in this machine that can receive incoming tcp connection or 0 for random> Examples/hadoop/resources.test1.txt <ftp | gcube depending on the infrastructure set up>
./StartHadoopAdaptorTest.sh <host machine running this example> <port in this machine that can receive incoming tcp connection or 0 for random> Examples/hadoop/resources.test2.txt <ftp | gcube depending on the infrastructure set up>
