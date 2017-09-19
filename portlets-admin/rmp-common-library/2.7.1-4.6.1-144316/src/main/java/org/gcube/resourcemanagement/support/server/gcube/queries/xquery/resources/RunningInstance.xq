for $profiles in collection('/db/Profiles/RunningInstance')//Resource
let $ghns := collection('/db/Profiles/GHN')//Resource
let $_ghn-name := for $ghn in $ghns
    where $ghn/ID/string() eq $profiles/Profile/GHN/@UniqueID/string()
    return $ghn/Profile/GHNDescription/Name/string()
let $ghn-name := if (empty($_ghn-name)) then $profiles/Profile/GHN/@UniqueID/string() else $_ghn-name
let $scopes := string-join( $profiles/Scopes//Scope/text(), ';')
let $subtype := $profiles/Profile/ServiceClass/text()
<RES_SUBTYPE ISdefault =''/> 
    return 
<RESOURCE/>
