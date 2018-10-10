package org.gcube.data.spd.manager.search.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.utils.QueryRetryCall;
import org.gcube.data.spd.utils.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonNameMapping extends Worker<String, String> {



	private AbstractPlugin plugin;
	   
	Logger logger = LoggerFactory.getLogger(CommonNameMapping.class);
	

	
	public CommonNameMapping(ClosableWriter<String> writer, AbstractPlugin plugin){
		super(writer);
		this.plugin = plugin;
	}

	
	@Override
	protected void execute(final String input, final ObjectWriter<String> outputWriter) {
		logger.debug("retieving mapping for "+input);
		
		try {
			new QueryRetryCall(){
				@Override
				protected VOID execute() throws ExternalRepositoryException {
					plugin.getMappingInterface().getRelatedScientificNames(outputWriter, input);
					return VOID.instance();
				}
			}.call();
		} catch (Exception e) {
			outputWriter.write(new StreamNonBlockingException(plugin.getRepositoryName(), input));
		}		
		
	}

	
}
