<!--
    PARAMS:
        SERVICE_CLASS
        SERVICE_NAME
        SERVICE_VERSION
        PKG_NAME
        
    Results will be of the form:
    <RequirementQuery>
    <Results>
        <Result>
            <GHNRequirements>
                <Requirement category="MEM_RAM_AVAILABLE" operator="le" requirement="" value="1000"/>
      <Requirement category="PLATFORM" operator="eq" requirement="" value="i368"/>
      <Requirement category="OS" operator="exist" requirement="" value="Linux"/>
     </GHNRequirements>
        </Result>
    </Results>
    </RequirementQuery>
-->
for $service in collection(/db/Profiles/Service)//Document/Data/child::*[local-name()='Profile']/Resource
where
$service/Profile/Class = '<SERVICE_CLASS/>'
and
$service/Profile/Name = '<SERVICE_NAME/>'
and
$service/Profile/Version = '<SERVICE_VERSION/>'
and
(
    (
        $service/Profile/Packages/Main/Name = '<PKG_NAME/>'
    )
     or
    (
        $service/Profile/Packages/Software/Name = '<PKG_NAME/>'
    )
)
return
<RequirementQuery>
{
  <Results>
    {
    for $main in $service/Profile/Packages/Main
    return if ($main/Name="<PKG_NAME/>")
    then <Result>{$main/GHNRequirements}</Result>
    else ""
  }
  {
        for $sw in $service/Profile/Packages/Software
    return if ($sw/Name="<PKG_NAME/>")
    then <Result>{$sw/GHNRequirements}</Result>
    else ""
  }
  </Results>
}
</RequirementQuery>