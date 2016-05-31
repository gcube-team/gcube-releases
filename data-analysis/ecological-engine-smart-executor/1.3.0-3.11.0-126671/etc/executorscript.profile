<?xml version="1.0" encoding="UTF-8"?>
<Resource xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <ID/>
    <Type>Service</Type>
    <Profile>
        <Description>A Plugin Executing S.O. Scripts</Description>
        <Class>ExecutorPlugins</Class>
        <Name>ExecutorScript</Name>
        <Version>1.0.0</Version>
        <Packages>
            <Plugin>
            	<Name>plugin</Name>
                <Version>1.0.0</Version>
            	<TargetService>
            		<Service>
            			<Class>VREManagement</Class>
           				<Name>Executor</Name>
            			<Version>1.1.0</Version>
            		</Service>
            		<Package>main</Package>
          			<Version>1.0.0</Version>
            	</TargetService>
                <EntryPoint>org.gcube.dataanalysis.executor.plugin.ScriptPluginContext</EntryPoint>
                <Files><File>org.gcube.dataanalysis.executor.executorscriptplugin.jar</File></Files>
            </Plugin>
        </Packages>
    </Profile>
</Resource>