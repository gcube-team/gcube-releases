<Resource>
    {$ris/ID}
    {$ris/Profile/ServiceName}
    {$ris/Profile/ServiceClass}
    <ServiceVersion>{$ris//Resource/@version/string()}</ServiceVersion>
    <MainVersion>{$ris/Profile/Version/text()}</MainVersion>
    <Status>{$ris/Profile/DeploymentData/Status/text()}</Status>
</Resource>