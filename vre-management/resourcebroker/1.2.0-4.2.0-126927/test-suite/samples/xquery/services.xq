#
# The list of all services in a scope.
# Takes from the IS the description of Services.
# NOTE: Suggested to use VM param -Xmx512M
# 
for $service in collection("/db/Profiles/Service")//Document/Data/child::*[local-name()='Profile']/Resource
return 
<ServiceDescr>
  <Service>
    {$service/ID}
    {$service/Profile/Class}
    {$service/Profile/Name}
    <Version>{fn:replace($service/Profile/Version/string(), "\.", "")}</Version>
  </Service>
  <Packages>
    {
    for $main in $service/Profile/Packages/Main
    return
    <Main>
        {$main/Name}
        <Version>{fn:replace($main/Version/string(), "\.", "")}</Version>
    </Main>
  }
  {
    for $sw in $service/Profile/Packages/Software
    return
    <Package>
        {$sw/Name}
        <Version>{fn:replace($sw/Version/string(), "\.", "")}</Version>
    </Package>
  }
  </Packages>
</ServiceDescr>