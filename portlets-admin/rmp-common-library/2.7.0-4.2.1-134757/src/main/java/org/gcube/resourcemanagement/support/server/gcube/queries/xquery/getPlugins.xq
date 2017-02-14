for $_outer in collection("/db/Properties")//Document
where ($_outer//Document/Data/child::*[local-name()='ServiceClass']/text() = 'ContentManagement'
and exists($_outer/Data/child::*[local-name()='Plugin']/name))
return 
    <CMPlugins>
                {
                    for $plugin in $_outer/Data/child::*[local-name()='Plugin']
                    return 
                    <Plugin>
                        {
                            for $elem in $plugin/parameters/child::*
                            return 
                                <Entry>
                                    {$plugin/name}
                                    {$plugin/description}
                                    <namespace>{namespace-uri($elem)}</namespace>
                                    <Type>{local-name($elem)}</Type>
                                    <Params>
                                        {
                                            for $p in $elem/child::*
                                            return
                                            <param>
                                                <param-name>{$p/name()}</param-name>
                                                <param-definition>{$p/text()}</param-definition>
                                            </param>
                                        }
                                     </Params>
                                </Entry>
                        }
                    </Plugin>
                }
    </CMPlugins>