
for $_outer in collection("/db/Properties")//Document
where ($_outer//Document/Data/child::*[local-name()='ServiceClass']/text() = 'DataAccess'
and exists($_outer/Data/child::*[local-name()='Plugin']/name))
return 
    <TMPlugins>
                {
                    for $plugin in $_outer/Data/child::*[local-name()='Plugin']
                    return 
                    <Plugin>
                       <Entry>
                       {$plugin/name}
                       {$plugin/description}
                       <namespace>{namespace-uri($plugin)}</namespace>
                       <Type>treeManagerPlugin</Type>
                       <Params>
                        {
                            for $elem in $plugin/child::*[local-name()='property']
                            return          
                                            <param>
                                                <param-name>{$elem/name/text()}</param-name>
                                                <param-definition>{$elem/value/text()}</param-definition>
                                            </param>
                        }
                        </Params>
						</Entry>
                    </Plugin>
                }
    </TMPlugins>