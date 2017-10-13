for $profiles in collection('/db/Profiles/GenericResource')//Resource,  $wsresource in collection('/db/Properties')//Document 
let $scopes := string-join( $profiles/Scopes//Scope/text(), ';')
let $subtype := $profiles//Resource/Profile/SecondaryType/text()
where $subtype = "DataSource" and $profiles//Resource/ID eq $wsresource/SourceKey and $wsresource/Data//child::*[local-name()='ServiceName']/string() eq 'tree-manager-service'
<RES_SUBTYPE ISdefault =''/> 
    return 
<RESOURCE/>
   