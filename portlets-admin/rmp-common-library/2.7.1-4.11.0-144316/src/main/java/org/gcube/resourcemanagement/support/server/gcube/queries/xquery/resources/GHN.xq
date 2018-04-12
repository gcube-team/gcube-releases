for $profiles in collection('/db/Profiles/GHN')//Resource
let $gcf-version := $profiles//Resource/Profile/GHNDescription/RunTimeEnv/Variable[Key/text() = 'gCF-version']/Value/text() | $profiles//Resource/Profile/GHNDescription/RunTimeEnv/Variable[Key/text() = 'SmartGears']/Value/text()
let $ghn-version := $profiles//Resource/Profile/GHNDescription/RunTimeEnv/Variable[Key/text() = 'GHN-distribution-version']/Value/text()  | $profiles//Resource/Profile/GHNDescription/RunTimeEnv/Variable[Key/text() = 'SmartGearsDistribution']/Value/text()
let $scopes := string-join( $profiles/Scopes//Scope/text(), ';')
let $subtype := $profiles//Resource/Profile/Site/Domain/text()
<RES_SUBTYPE ISdefault =''/> 
    return 
<RESOURCE/>
