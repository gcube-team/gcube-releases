for $outer in collection("/db/Properties")//Document
let $scopes := string-join( $outer//Document/Data/child::*[local-name()='Scope']/text(), ';')
where $outer//Document/Data/child::*[local-name()='ServiceClass']/text() eq '<RES_SUBTYPE/>'
return 
<WSRESOURCE/>
