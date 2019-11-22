<!--
 PARAMETS:
    RES_ID the ID of the Service
 -->
let $service := collection('/db/Profiles/Service')//Resource//Resource[ID/text() eq '<RES_ID/>']
let $ServiceClass := $service/Profile/Class
let $ServiceName := $service/Profile/Name
let $ServiceVersion := $service/Profile/Version
let $riloop := collection('/db/Profiles/RunningInstance')//Resource[Profile/ServiceClass/string() eq $ServiceClass and Profile/ServiceName/string() eq $ServiceName]
let $relatedris := 
    for $ri in $riloop
        let $ghn-id := $ri/Profile/GHN/@UniqueID/string()
        let $ghn := collection('/db/Profiles/GHN')//Resource[ID/string() eq $ghn-id]
    <!-- and $ri//Profile/Version/string() eq $ServiceVersion -->
    return 
<RESOURCE/>