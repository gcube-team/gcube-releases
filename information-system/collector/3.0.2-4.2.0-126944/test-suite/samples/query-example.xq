declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';
declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';
 for $result in collection("/db/Profiles/RunningInstance")//Document/Data/is:Profile/Resource 
 where ($result/Profile/DeploymentData/Status/string() eq "ready") and ($result//ServiceName/string() eq "IS-Registry") 
 	and ($result//ServiceClass/string() eq "InformationSystem") 
 return $result

