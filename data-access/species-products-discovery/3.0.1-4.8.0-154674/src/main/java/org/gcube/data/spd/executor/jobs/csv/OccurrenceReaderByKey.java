package org.gcube.data.spd.executor.jobs.csv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.spd.manager.OccurrenceWriterManager;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceReaderByKey implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(OccurrenceReaderByKey.class);

	private final LocalWrapper<OccurrencePoint> ocWrapper;
	private Stream<String> stream;
	private Map<String, AbstractPlugin> plugins;

	public OccurrenceReaderByKey(LocalWrapper<OccurrencePoint> ocWrapper, Stream<String> stream, Map<String, AbstractPlugin> plugins) {
		this.ocWrapper = ocWrapper;
		this.stream = stream;
		this.plugins = plugins;
	}

	@Override
	public void run() {
		HashMap<String, OccurrenceWriterManager> managerPerProvider = new HashMap<String, OccurrenceWriterManager>();
		try{
			while(this.stream.hasNext()){
				String key = this.stream.next();
				try{
					String provider = Util.getProviderFromKey(key);
					if (!managerPerProvider.containsKey(provider))
						managerPerProvider.put(provider, new OccurrenceWriterManager(provider));

					Writer<OccurrencePoint> ocWriter = new Writer<OccurrencePoint>(ocWrapper, managerPerProvider.get(provider) );
					String id = Util.getIdFromKey(key);
					AbstractPlugin plugin = plugins.get(provider);
					if (plugin==null) throw new UnsupportedPluginException();
					if (!plugin.getSupportedCapabilities().contains(Capabilities.Occurrence)) 
						throw new  UnsupportedCapabilityException();
					ocWriter.register();
					plugin.getOccurrencesInterface().getOccurrencesByProductKeys(ocWriter, Collections.singletonList(id).iterator());
				}catch (Exception e) {
					logger.warn("error getting occurrence points with key "+key, e);
				}
			}
		}catch(Exception e){
			logger.error("Error reading keys",e);
			try {
				ocWrapper.add(new StreamBlockingException(""));
			} catch (Exception e1) {
				logger.error("unexpected error", e1);
			} 
		}
		try {
			ocWrapper.disableForceOpenAndClose();
		} catch (Exception e) {
			logger.warn("error closing the local reader", e);
		}
	}
}

