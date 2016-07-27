<!--
    PARAMS: RI_ID the ID of the RI to retrieve.
    RETURNS: The GHN where such RI is running.
  -->
for $result in collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource
where $result/ID eq <RI_ID/>
order by $result/ID
return
<RunningInstance>
    <RiID>{$result/ID/string()}</RiID>
    <GHNID>{$result/Profile/GHN/string(@UniqueID)}</GHNID>
</RunningInstance>
