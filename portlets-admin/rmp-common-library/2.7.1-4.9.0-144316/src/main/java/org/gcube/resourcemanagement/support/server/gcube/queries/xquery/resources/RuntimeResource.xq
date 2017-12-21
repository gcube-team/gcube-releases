for $profiles in collection('/db/Profiles/RuntimeResource')//Resource
let $scopes := string-join( $profiles/Scopes//Scope/text(), ';')
let $subtype := $profiles//Resource/Profile/Category/text()
<RES_SUBTYPE ISdefault =''/> 
    return 
<RESOURCE/>
   