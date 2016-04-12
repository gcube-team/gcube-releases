#!/bin/sh
# AQUAMAPS_SUITABLE
cd $1

java -Xmx1024M -classpath ./:./aquamapsnode.jar:./c3p0-0.9.1.2.jar:./commons-collections-3.1.jar:./dom4j-1.6.1.jar:./ecologicalDataMining.jar:./hibernate3.jar:./jaxen-1.1.2.jar:./jta-1.1.jar:./log4j-1.2.16.jar:./postgresql-8.4-702.jdbc4.jar:./slf4j-api-1.6.0.jar:./slf4j-log4j12-1.6.0.jar:./xpp3_min-1.1.4c.jar:./xstream-1.3.1.jar org.gcube.dataanalysis.executor.nodes.algorithms.AquamapsSuitableNode $2 execution.output
