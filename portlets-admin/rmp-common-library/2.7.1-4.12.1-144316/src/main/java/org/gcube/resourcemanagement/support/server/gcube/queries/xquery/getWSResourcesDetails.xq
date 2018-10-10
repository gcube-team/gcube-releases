for $_outer in collection("/db/Properties")//Document
let $_scopes := string-join( $_outer//Document/Data/child::*[local-name()='Scope']/text(), ';')
return 
    <WSResource>
        {$_outer//Document/ID}
        {$_outer//Document/Source}
        {$_outer//Document/SourceKey}
        <ServiceClass>{$_outer//Document/Data/child::*[local-name()='ServiceClass']/text()}</ServiceClass>
        <ServiceName>{$_outer//Document/Data/child::*[local-name()='ServiceName']/text()}</ServiceName>
        <SubType>{$_outer//Document/Data/child::*[local-name()='ServiceClass']/text()}</SubType>
        {$_outer//Document/TerminationTimeHuman}
        {$_outer//Document/LastUpdateHuman}
        <RI>{$_outer//Document/Data/child::*[local-name()='RI']/text()}</RI>
        <Type>WSResource</Type>
        <scopes>{$_scopes}</scopes>
    </WSResource>