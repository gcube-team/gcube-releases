declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry';
for $profiles in collection('/db/Profiles/GenericResource')//Document/Data/ic:Profile/Resource
let $scopes := string-join( $profiles/Scopes//Scope/text(), ';')
let $subtype := $profiles/Profile/SecondaryType/text()
<RES_SUBTYPE ISdefault =''/> 
    return 
<RESOURCE/>