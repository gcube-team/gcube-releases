declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry';
for $resource in collection('/db/Profiles/<RES_TYPE ISdefault ='' />')//Document/Data/ic:Profile/Resource
where $resource/ID/string() eq '<RES_ID/>'
return $resource