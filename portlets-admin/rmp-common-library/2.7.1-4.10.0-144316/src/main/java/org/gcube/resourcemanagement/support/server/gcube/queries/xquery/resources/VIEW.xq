declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';  
declare namespace wmns= 'http://gcube-system.org/namespaces/contentmanagement/viewmanager';  
for $outer in collection("/db/Properties")//Document, $res in  $outer/Data  where $res/gc:ServiceClass/string() eq 'ContentManagement' 
and count($res//wmns:View)>0  and $res/gc:ServiceName/string() eq 'ViewManager'
return  
<Resource>  
	{$outer//Document/ID}
	{$outer//Document/Source}
	{$outer//Document/SourceKey}
	<ViewName>{$outer//Document/Data/child::*[local-name()='View']/child::*[local-name()='property']/child::*[local-name()='name' and text()='name']/../child::*[local-name()='value']/text()}</ViewName>
	<Cardinality>{$outer//Document/Data/child::*[local-name()='View']/child::*[local-name()='cardinality']/text()}</Cardinality>
	<ViewType>{$outer//Document/Data/child::*[local-name()='View']/child::*[local-name()='type']/text()}</ViewType>
	<RelatedCollectionId>{$outer//Document/Data/child::*[local-name()='View']/child::*[local-name()='collectionID']/text()}</RelatedCollectionId>
	<ServiceClass>{$outer//Document/Data/child::*[local-name()='ServiceClass']/text()}</ServiceClass>
	<ServiceName>{$outer//Document/Data/child::*[local-name()='ServiceName']/text()}</ServiceName>
	<SubType>{$outer//Document/Data/child::*[local-name()='ServiceClass']/text()}</SubType>
	{$outer//Document/TerminationTimeHuman}   {$outer//Document/LastUpdateHuman}  
	<RI>{$outer//Document/Data/child::*[local-name()='RI']/text()}</RI>
	<Type>WSResource</Type>
	<scopes>{$outer//Document/Data/child::*[local-name()='Scope']/text()}</scopes>
</Resource>
