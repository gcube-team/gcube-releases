<!--
    Retrieves the list of RunningInstances on a GHN.
    Params:
        RES_ID - the GHN id on which lookup the RIs
    Result sample:
    <Resource>
        <ID>8ced4e40-ecf1-11df-95dd-c203e806a114</ID>
        <ServiceName>ResourceManager</ServiceName>
        <ServiceClass>VREManagement</ServiceClass>
        <ServiceVersion>1.0.1</ServiceVersion>
        <MainVersion>2.00.00</MainVersion>
        <Status>ready</Status>
    </Resource>
 -->
for $ris in collection('/db/Profiles/RunningInstance')//Resource
where $ris//Resource/Profile/GHN/@UniqueID/string() eq '<RES_ID/>'
return 
<RESOURCE/>
