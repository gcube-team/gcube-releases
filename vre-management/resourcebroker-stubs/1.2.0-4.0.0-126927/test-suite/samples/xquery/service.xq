#
# The list of all services in a scope.
# Takes from the IS the description of Services.
# NOTE: Suggested to use VM param -Xmx512M
# Retrieves all the Services matching the parameters.
# Will be used to find requirements for them.
# 
for $service in collection("/db/Profiles/Service")//Document/Data/child::*[local-name()='Profile']/Resource
where 
$service/Profile/Class = 'Search'
and
$service/Profile/Name = 'ResultSetLibrary'
and
(
    (
        $service/Profile/Packages/Main/Name = 'ResultSetLibrary'
    )
     or
    (
        $service/Profile/Packages/Software/Name = 'ResultSetLibrary'
    )
)
return 
<ServiceDescr>
  <Service>
    <ServiceID>{$service/ID/string()}</ServiceID>
    <ServiceClass>{$service/Profile/Class/string()}</ServiceClass>
    <ServiceName>{$service/Profile/Name/string()}</ServiceName>
    <ServiceVersion>{$service/Profile/Version/string()}</ServiceVersion>
    {
    for $main in $service/Profile/Packages/Main
    where $main/Name = 'ResultSetLibrary'
    return
    <Package>
        <PackageType>Main</PackageType>
        <PackageName>{$main/Name/string()}</PackageName>
        <PackageVersion>{$main/Version/string()}</PackageVersion>
    </Package>
  }
  {
    for $sw in $service/Profile/Packages/Software
    where $sw/Name = 'ResultSetLibrary'
    return
    <Package>
        <PackageType>Package</PackageType>
        <PackageName>{$sw/Name/string()}</PackageName>
        <PackageVersion>{$sw/Version/string()}</PackageVersion>
    </Package>
  }
 </Service>
</ServiceDescr>