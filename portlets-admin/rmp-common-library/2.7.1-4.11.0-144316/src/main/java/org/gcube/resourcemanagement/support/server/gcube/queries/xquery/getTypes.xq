declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry';
let $entry0ValueAuth := collection("/db/Profiles")//Document/Data/ic:Profile/Resource
for $types in distinct-values($entry0ValueAuth/Type/text())
    return <type>{$types}</type>