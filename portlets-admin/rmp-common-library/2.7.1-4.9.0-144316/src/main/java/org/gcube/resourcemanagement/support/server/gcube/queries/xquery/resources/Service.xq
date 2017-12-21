for $profiles in collection('/db/Profiles/Service')//Resource
let $scopes := string-join( $profiles/Scopes//Scope/text(), ';')
let $subtype := $profiles//Resource/Profile/Class/text()
<RES_SUBTYPE ISdefault =''/> 
    return 
<RESOURCE/>