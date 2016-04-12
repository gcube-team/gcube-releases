
for $resource in collection("/db/Properties")//Document
where $resource/ID/string() eq '<RES_ID/>'
return $resource