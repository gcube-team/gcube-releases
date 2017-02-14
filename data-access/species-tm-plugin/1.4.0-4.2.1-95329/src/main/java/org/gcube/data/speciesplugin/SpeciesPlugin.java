package org.gcube.data.speciesplugin;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.gcube.data.speciesplugin.requests.SpeciesRequest;
import org.gcube.data.speciesplugin.utils.SpeciesUpdateScheduler;
import org.gcube.data.tmf.api.Environment;
import org.gcube.data.tmf.api.PluginLifecycle;
import org.gcube.data.tmf.api.Property;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 */
public class SpeciesPlugin implements PluginLifecycle {
	
	protected static final String REQUEST_SAMPLE = "<speciesRequest><name>Parachela collection</name>" +
			"<description>Parachela collection from Itis</description><scientificNames repeatable=\"true\">Parachela</scientificNames>" +
			"<datasources repeatable=\"true\">ITIS</datasources><strictMatch>true</strictMatch><refreshPeriod>5</refreshPeriod>" +
			"<timeUnit>MINUTES</timeUnit></speciesRequest>";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name() {
		return "species-tree-plugin";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String description() {
		return "Species Discovery Service plugin";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Property> properties() {
		return Arrays.asList(new Property("A request sample", "requestSample", REQUEST_SAMPLE));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpeciesBinder binder() {
		return new SpeciesBinder();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> requestSchemas() {	
		String sampleDataSchema = Utils.toSchema(SpeciesRequest.class);		
		return Collections.singletonList(sampleDataSchema);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAnchored() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Environment environment) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(Environment environment) {
		ScheduledThreadPoolExecutor scheduler = SpeciesUpdateScheduler.getInstance();
		scheduler.shutdownNow();
	}
	
}
