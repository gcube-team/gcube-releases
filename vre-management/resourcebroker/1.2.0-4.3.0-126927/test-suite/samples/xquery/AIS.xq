for $result in 
collection("/db/Profiles/GenericResource")//Document/Data/child::*[local-name()='Profile']/Resource
where ($result/Profile/SecondaryType/string() eq 'AIS_SCRIPT') 
return 
<ResElem>
{$result}
{$result/Profile/Name}
{$result/Profile/SecondaryType}
</ResElem>

#for $result in 
#collection("/db/Profiles/GenericResource")//Document/Data/is:Profile/Resource 
#where ($result/Profile/SecondaryType/string() eq 'AIS_SCRIPT') 
#return $result