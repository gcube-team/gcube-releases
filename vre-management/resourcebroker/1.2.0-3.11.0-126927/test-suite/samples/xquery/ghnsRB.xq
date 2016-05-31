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
   and $GHNs/Profile/GHNDescription/Name[contains(.,  'strollo')]
return
 <RIONGHN>
   {$GHNs}
 </RIONGHN>