<Resource>
        <!-- INFORMATION ABOUT THE RI -->
        <RIID>{$ri/ID/string()}</RIID>
        <ServiceStatus>{$ri/Profile/DeploymentData/Status/string()}</ServiceStatus>
        <ActivationTime>{$ri/Profile/DeploymentData/ActivationTime/@value/string()}</ActivationTime>
        <RIVersion>{$ri/Profile/Version/string()}</RIVersion>
        <!-- INFORMATION about GHN -->
        <GHNID>{$ghn-id}</GHNID>
        <GHNName>{$ghn/Profile/GHNDescription/Name/string()}</GHNName>
        <GHNSite>{$ghn/Profile/Site/Domain/string()}</GHNSite>
        <GHNStatus>{$ghn/Profile/GHNDescription/Status/string()}</GHNStatus>
        <GHNLoad15Min>{$ghn/Profile/GHNDescription/Load/@Last15Min/string()}</GHNLoad15Min>
        <GHNLoad5Min>{$ghn/Profile/GHNDescription/Load/@Last15Min/string()}</GHNLoad5Min>
        <GHNLoad1Min>{$ghn/Profile/GHNDescription/Load/@Last15Min/string()}</GHNLoad1Min>
        <GHNActivationTime>{$ghn/Profile/GHNDescription/ActivationTime/string()}</GHNActivationTime>
        <GHNLastUpdate>{$ghn/Profile/GHNDescription/LastUpdate/string()}</GHNLastUpdate>
        </Resource>
return
<Resources>
    {$relatedris}
</Resources> 