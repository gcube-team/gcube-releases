for $RI in collection("/db/Profiles/RunningInstance")//Document/Data/child::*[local-name()='Profile']/Resource
return $RI