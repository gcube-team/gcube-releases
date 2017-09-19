# Given a couple of (type, subtype) retrieves
# the list of all resources contained in such category.
# Parameters:  
#   RES_TYPE the main type of the resource
# This query is used by forms

for $profiles in collection('/db/Profiles/<RES_TYPE ISdefault ='GHN' />')//Resource
let $ghn-name := $profiles//Resource/Profile/GHN/@UniqueID/string()
let $gcf-version := $profiles//Resource/Profile/GHNDescription/RunTimeEnv/Variable[Key/text() = 'gCF-version']/Value/text()
let $ghn-version := $profiles//Resource/Profile/GHNDescription/RunTimeEnv/Variable[Key/text() = 'GHN-distribution-version']/Value/text()
let $scopes := string-join( $profiles//Resource/Scopes//Scope/text(), ';')
let $subtype := 
        if ($profiles//Resource/Type eq "Service")
        then $profiles//Resource/Profile/Class/text()
        else if ($profiles//Resource/Type eq "RunningInstance")
        then $profiles//Resource/Profile/ServiceClass/text()
        else if ($profiles//Resource/Type eq "GenericResource")
        then $profiles//Resource/Profile/SecondaryType/text()
        else if ($profiles//Resource/Type eq "GHN")
        then $profiles//Resource/Profile/Site/Domain/text()
        else if ($profiles//Resource/Type eq "MetadataCollection")
        then $profiles//Resource/Profile/MetadataFormat/Name/text()
        else if ($profiles//Resource/Type eq "Collection" and ($profiles//Resource/Profile/IsUserCollection/string(@value) eq 'true'))
        then "User"
        else if ($profiles//Resource/Type eq "Collection" and ($profiles//Resource/Profile/IsUserCollection/string(@value) eq 'false'))
        then "System"
        else ""
    where $subtype eq '<RES_SUBTYPE ISdefault='isti.cnr.it'/>'
    return 
<RESOURCE/>