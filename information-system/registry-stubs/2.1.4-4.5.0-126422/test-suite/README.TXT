ISRegistry test-suite

Decription
A client for the IS Registry allowing to add/delete profiles from the IS

Usage
source removeGCUBEResource.sh <RegistryFactory URI> <resource ID> <scope> <resource type>
source registerGCUBEResource.sh <RegistryFactory URI> <profile file> <scope> <resource type>

Example
source removeGCUBEResource.sh http://node4.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/informationsystem/registry/RegistryFactory 778ba410-8d94-11de-889b-bed95c57e445 /d4science.research-infrastructures.eu/EM RunningInstance
source registerGCUBEResource.sh http://node4.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/informationsystem/registry/RegistryFactory ./samples/RIProfile.xml /d4science.research-infrastructures.eu/EM RunningInstance

Author Manuele Simi
Last Update 16/10/2009
