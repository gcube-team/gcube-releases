
let $tempcollection := for $outer in collection("/db/Properties")//Document
    return $outer//Document/Data/child::*[local-name()='ServiceClass']
for $elem in distinct-values($tempcollection)
return $elem