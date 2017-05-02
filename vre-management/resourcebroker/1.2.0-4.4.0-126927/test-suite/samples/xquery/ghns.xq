#
# Takes from the IS the list of all GHNs alive from maximum 40 minutes
#
for $GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource
let $RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $totalminutes := hours-from-dateTime($GHNs/Profile/GHNDescription/LastUpdate/text())
where $GHNs/Profile/GHNDescription/Type eq 'Dynamic'
   and $totalminutes le 40
   and count($RIs) gt 0
return
 <RIONGHN>
   {$GHNs/ID}
   <AllocatedRI>{count($RIs)}</AllocatedRI>
   {$GHNs/Profile/GHNDescription/LastUpdate}
   <UpdateMinutesElapsed>{$totalminutes}</UpdateMinutesElapsed>
   <ProfileXML>
       {$GHNs/Scopes}
       {$GHNs/Profile/GHNDescription}
       {$GHNs/Profile/Site}
   </ProfileXML>
 </RIONGHN>