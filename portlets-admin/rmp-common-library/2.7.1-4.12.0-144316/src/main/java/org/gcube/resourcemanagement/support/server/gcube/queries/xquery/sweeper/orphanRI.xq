let $ghns := collection('/db/Profiles/GHN')//Resource/ID
let $ris := collection('/db/Profiles/RunningInstance')//Resource
for $ri in $ris
let $counter := index-of(($ghns//ID/string()), $ri/Profile/GHN/@UniqueID/string())
where empty($counter)
return
<RESOURCE/>