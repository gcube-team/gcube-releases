#
# Takes from the IS the list of all GHNs alive from maximum 40 minutes
#
let $now := current-dateTime()
for $GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource
let $RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $lastTestTimeDuration := xs:dateTime($now) - xs:dateTime($GHNs/Profile/GHNDescription/LastUpdate)
let $totalminutes := ceiling($lastTestTimeDuration div xdt:dayTimeDuration('PT1M'))
where $GHNs/Profile/GHNDescription/Type eq 'Dynamic'
   and $totalminutes le 40
   and count($RIs) gt 0
return
 <RIONGHN>
   {$GHNs/ID}
   {$GHNs/Profile/GHNDescription/Name}
   <ProfileXML>
       <RIDescriptors>
       {
       for $RI in $RIs
       return
        <RI>
            {$RI/ID}
            <Service>{$RI/Profile/string(ServiceClass)}.{$RI/Profile/string(ServiceName)}</Service>
            {$RI/Profile/Accounting}
        </RI>
       }
       </RIDescriptors>
   </ProfileXML>
 </RIONGHN>