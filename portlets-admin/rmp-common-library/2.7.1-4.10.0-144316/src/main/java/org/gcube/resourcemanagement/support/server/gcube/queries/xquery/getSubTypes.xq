let $type := '<RES_TYPE ISdefault ='MetadataCollection'/>'
let $subtypes :=
    for $_profiles in collection('/db/Profiles/<RES_TYPE ISdefault ='MetadataCollection'/>')//Resource
    let $elem := if ($type eq "Service")
            then $_profiles//Resource/Profile/Class
            else if ($type eq "RunningInstance")
            then $_profiles//Resource/Profile/ServiceClass
            else if ($type eq "GenericResource")
            then $_profiles//Resource/Profile/SecondaryType
            else if ($type eq "GHN")
            then $_profiles//Resource/Profile/Site/Domain
            else if ($type eq "MetadataCollection")
            then $_profiles//Resource/Profile/MetadataFormat/Name
            else if ($type eq "RuntimeResource")
            then $_profiles//Resource/Profile/Category
            else if ($type eq "Collection" and ($_profiles//Resource/Profile/IsUserCollection/string(@value) eq 'true'))
            then "User"
            else if ($type eq "Collection" and ($_profiles//Resource/Profile/IsUserCollection/string(@value) eq 'false'))
            then "System"
            else ""
    return $elem
<!--
    return $elem
 -->
for $subtype in distinct-values($subtypes)
return
     <SUBTYPE/>