package org.gcube.vremanagement.resourcemanager.impl.state.observers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedAnyResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedGHN;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedRunningInstance;
import org.gcube.vremanagement.resourcemanager.impl.resources.types.MultiKeysMap;
import org.gcube.vremanagement.resourcemanager.impl.state.RawScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import javax.xml.namespace.QName;

/**
 * 
 * Serializer for {@link ScopeState}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Serializer extends ScopeObserver {
	
	private final static String namespace = "http://gcube-system.org/namespaces/resourcemanager/manager/xsd/state";
	
	/**
	 * Serializes the notified list on the file system
	 * @param scopeState the list to serialize
	 */
	@Override
	protected synchronized void scopeChanged(final ScopeState scopeState) {			
        try {
        	if(!scopeState.isDisposed()){
        		logger.debug("Serializer: the scopeState is changed. need store the change ");
        		store(scopeState);
        	}else{
        		logger.debug("Serralizer: the scopeState is disposed doesn't need to serialize it");
        	}
             //scopeState.setLastOperationPerformed(OPERATION.SERIALIZED); //if we record this, the others observer manage the list with no need
             //we do not notify the others obs here, since the serialization does not imply any change in the list
        } catch (IOException e) {
        	logger.fatal("Cannot serialize the resource's list", e);
		}

	}
	
	public synchronized static void store(final ScopeState scopeState) throws IOException {
		
		QNameMap qmap = new QNameMap();
		qmap.registerMapping(new QName(namespace, ""), RawScopeState.class);
		StaxDriver driver = new StaxDriver(qmap);
		driver.setRepairingNamespace(false);
		XStream stream = new XStream(driver);
		prepareStream(stream);
		
		FileOutputStream fs = new FileOutputStream(getSerializationFile(scopeState.getScope()));
        stream.toXML(scopeState.getRawScopeState(), fs);
        fs.close();
	}

	/**
	 * Loads the list of {@link ScopedResource} from the local file system
	 * @param the actual scope
	 * @return the resource list, if any
	 * @throws IOException if the list was not found on the file system
	 */
	public synchronized static void load(ScopeState scopeState, GCUBEScope scope) throws IOException {
		File persistentFile = getSerializationFile(scope);
		if (! persistentFile.exists()) {
			//for backward compatibility with < 2.0 (to remove soon)
			persistentFile = ServiceContext.getContext().getPersistentFile("ScopedResourceList.xml", true);
			if (! persistentFile.exists())
				throw new IOException();
		}
			
		
		//try to load the local list of resources
		QNameMap qmap = new QNameMap();
		qmap.registerMapping(new QName(namespace, ""), RawScopeState.class);
		StaxDriver driver = new StaxDriver(qmap);		
		driver.setRepairingNamespace(false);
		XStream stream = new XStream(driver);
		prepareStream(stream);		
		FileInputStream fis=new FileInputStream((persistentFile));
		RawScopeState state = (RawScopeState) stream.fromXML(fis);
		fis.close();
		//a bit of sanity checks....
		if ((state == null) || (state.getScope() == null) || (state.getScope().getName().compareTo(scope.getName()) != 0))
			throw new IOException();

		//inject the state
		scopeState.setRawScopeState(state);
	}
	
	private static void prepareStream(XStream stream) {
		stream.processAnnotations(RawScopeState.class);			
		stream.alias(RawScopeState.class.getSimpleName(), RawScopeState.class);
		
		stream.processAnnotations(ScopedResource.class);
		stream.alias(ScopedResource.class.getSimpleName(), ScopedResource.class);
		
		stream.alias(ScopedGHN.class.getSimpleName(), ScopedGHN.class);
		stream.processAnnotations(ScopedGHN.class);
		
		stream.alias(ScopedRunningInstance.class.getSimpleName(), ScopedRunningInstance.class);
		stream.processAnnotations(ScopedRunningInstance.class);
		
		stream.alias(ScopedDeployedSoftware.class.getSimpleName(), ScopedDeployedSoftware.class);
		stream.processAnnotations(ScopedDeployedSoftware.class);
		
		stream.alias(ScopedAnyResource.class.getSimpleName(), ScopedAnyResource.class);
		stream.processAnnotations(ScopedAnyResource.class);
		
		stream.alias("ResourceList", MultiKeysMap.class);
		stream.alias("ResourceData", Map.class);
		stream.alias("Scope", GCUBEScope.class);
		stream.alias("ScopeType", GCUBEScope.Type.class);
		stream.alias("VirtualNode", VirtualNode.class);
	}



	protected static File getSerializationFile(GCUBEScope scope) {	
		return ServiceContext.getContext().getPersistentFile("SerializedResourceList"+scope.toString().replace('/', '-'), true);
	}
}
