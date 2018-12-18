<!--
    Notice: the <Resources> node must be removed in using the resource grid factory.
    -->
let $profiles := collection('/db/Profiles/<RES_TYPE ISdefault ='RunningInstance'/>')//Resource[ID/string() eq '<RES_ID/>']
let $relatedghn := collection('/db/Profiles/GHN')//Resource[ID/string() eq $profiles/Profile/GHN/@UniqueID/string()]
let $ghn-name := if (empty($relatedghn/Profile/GHNDescription/Name/string())) 
    then $profiles/Profile/GHN/@UniqueID/string() 
    else $relatedghn/Profile/GHNDescription/Name/string()
for $ri in $profiles
return
<RESOURCE/>