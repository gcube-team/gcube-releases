# gets from the scope all the running instances having name *Broker*
for $service in collection("/db/Profiles/Service")//Document/Data/child::*[local-name()='Profile']/Resource
where $service/Profile/Name[contains(.,  'ResourceBroker')]
return $service