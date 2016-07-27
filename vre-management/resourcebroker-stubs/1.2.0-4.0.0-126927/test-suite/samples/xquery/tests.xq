

let $now := current-dateTime()
for 
$GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource 
let
$RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $lastTestTimeDuration := xs:dateTime($now) - xs:dateTime($GHNs/Profile/GHNDescription/LastUpdate)
let $totalminutes := ceiling($lastTestTimeDuration div xdt:dayTimeDuration('PT1M'))
where $GHNs/Profile/GHNDescription/Type	eq 'Dynamic' and $totalminutes le 40
order by count($RIs)					 
return $GHNs

#for $GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource return $GHNs

#for $ghnID in distinct-values(collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource/Profile/GHN/string(@UniqueID)) let $x := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$ghnID]  return concat($ghnID,':',count($x))


#let $GHNs = collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource
#let $RIs  = collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource
#$RIs/Profile/GHN/string(@UniqueID)=$GHNs/ID


let $now := current-dateTime()
for 
$GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource 
let
$RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $lastTestTimeDuration := xs:dateTime($now) - xs:dateTime($GHNs/Profile/GHNDescription/LastUpdate)
let $totalminutes := ceiling($lastTestTimeDuration div xdt:dayTimeDuration('PT1M'))
order by count($RIs)					 
return 
concat('<RIONGHN>','<GHNID>',$GHNs/ID, '</GHNID>','<AllocatedRI>', count($RIs),'</AllocatedRI>', '<LastUpdate>', $GHNs/Profile/GHNDescription/LastUpdate,'</LastUpdate>','<UpdateMinutesElapsed>',$totalminutes,'</UpdateMinutesElapsed>','</RIONGHN>')

######################################################################
# DON'T TOUCH ME - the only one that is working :P
# SEEMS TO WORK - returns couples (ghnID, numberOfAllocatedRI)
# Builds two stuctures GHN and RunningInstance.
# returns also the minutes of last update
######################################################################
let $now := current-dateTime()
for 
$GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource 
let
$RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $lastTestTimeDuration := xs:dateTime($now) - xs:dateTime($GHNs/Profile/GHNDescription/LastUpdate)
let $totalminutes := ceiling($lastTestTimeDuration div xdt:dayTimeDuration('PT1M'))
where $GHNs/Profile/GHNDescription/Type	eq 'Dynamic' and $totalminutes le 40
order by count($RIs)					 
return 
concat('<RIONGHN>','<GHNID>',$GHNs/ID, '</GHNID>','<AllocatedRI>', count($RIs),'</AllocatedRI>', '<LastUpdate>', $GHNs/Profile/GHNDescription/LastUpdate,'</LastUpdate>','<UpdateMinutesElapsed>',$totalminutes,'</UpdateMinutesElapsed>','</RIONGHN>')



######################################################################
# for each RI the corresponding GHN on which it is running
# needed to the subscriber of RI creation notifications.
######################################################################
#for $result in collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource
#order by $result/ID
#return concat ('<UpdateRI>','<RiID>',$result/ID,'</RiID>','<GHNID>',$result/Profile/GHN/string(@UniqueID),'</GHNID>','</UpdateRI>')


let $now := current-dateTime()
for 
$GHNs in collection("/db/Profiles/GHN")//Document/Data/child::*[local-name()='Profile']/Resource 
let
$RIs := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$GHNs/ID]
let $lastTestTimeDuration := xs:dateTime($now) - xs:dateTime($GHNs/Profile/GHNDescription/LastUpdate)
let $totalminutes := ceiling($lastTestTimeDuration div xdt:dayTimeDuration('PT1M'))
where $GHNs/Profile/GHNDescription/Type	eq 'Dynamic' 
order by count($RIs)					 
return 
concat('<RIONGHN>','<GHNID>',$GHNs/ID, '</GHNID>','<AllocatedRI>', count($RIs),'</AllocatedRI>', '<LastUpdate>', $GHNs/Profile/GHNDescription/LastUpdate,'</LastUpdate>','<UpdateMinutesElapsed>',$totalminutes,'</UpdateMinutesElapsed>','</RIONGHN>')







######################################################################
# DON'T TOUCH ME - the only one that is working :P
# SEEMS TO WORK - returns couples (ghnID, numberOfAllocatedRI)
######################################################################
#for $ghnID in distinct-values(collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource/Profile/GHN/string(@UniqueID)) 
let $x := collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource[Profile/GHN/string(@UniqueID)=$ghnID]  
return concat('<RIONGHN>','<GHNID>',$ghnID,'</GHNID>','<AllocatedRI>',count($x),'</AllocatedRI>','</RIONGHN>')






######################################################################
# TRASH
######################################################################


#for $Profile in collection("/db/Profiles")//Document/Data/child::*[local-name()='Profile']/Resource return $Profile/ID

## Returns the list of GHN
# for $result in collection("/db/Profiles/GHN") return $result

## list of GHN in the query scope having type 'Dynamic'
#for $result in collection("/db/Profiles/GHN") where $result/Document/Data/child::*[local-name()='Profile']/Resource/Profile/GHNDescription/Type eq 'Dynamic' return $result

# GETS the IDs of GHN 
# for $result in collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource $result/Profile/GHN/string(@UniqueID) return concat($result/Profile/GHN/string(@UniqueID), ':', count($result/Profile/GHN/string(@UniqueID)))

## couple (numberOfRI, GHNID)
#for $result in collection("/db/Profiles/GHN") where $result/Document/Data/child::*[local-name()='Profile']/Resource/Profile/GHNDescription/Type eq 'Dynamic' return concat(count($result/Document/Data/child::*[local-name()='Profile']/Resource/child::*), ':' , $result/Document/Data/child::*[local-name()='Profile']/Resource/ID)


#for $result in collection("/db/Profiles/GHN") where $result/Document/Data/child::*[local-name()='Profile']/Resource/Profile/GHNDescription/Type eq 'Dynamic' and $result/Profile/GHN/string(@UniqueID) eq '7d830500-08a8-11de-8137-ac18be98e88e' return count($result/Document/Data/child::*[local-name()='Profile']/Resource/child::*)



# ALL GHN
#for $result in collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource 
#let $retval := $result/Profile/GHN/string(@UniqueID) 
#order by $retval return $retval


# GET DISTINCT NAMES OF GHN
#for $result in distinct-values(collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource/Profile/GHN/string(@UniqueID)) order by $result return $result

