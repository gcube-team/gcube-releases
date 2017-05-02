<!--
    Retrieves the descriptor of a GHN given its ID.
    Used inside UpdateThread.
    
    PARAMS:
        MAXWAIT
        PARAM_GHNID
 -->
for $GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource
let $RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $totalminutes := hours-from-dateTime($GHNs/Profile/GHNDescription/LastUpdate/text())
where $GHNs/Profile/GHNDescription/Type eq 'Dynamic'
	and $totalminutes le <MAXWAIT ISdefault ='40'/>
	and $GHNs/ID eq '<PARAM_GHNID/>'
return 
 <RIONGHN>
	{$GHNs/ID}
	<AllocatedRI>{count($RIs)}</AllocatedRI>
	{$GHNs/Profile/GHNDescription/LastUpdate}
	<UpdateMinutesElapsed>{$totalminutes}</UpdateMinutesElapsed>
	<ProfileXML>
        {$GHNs}
	</ProfileXML>
 </RIONGHN>