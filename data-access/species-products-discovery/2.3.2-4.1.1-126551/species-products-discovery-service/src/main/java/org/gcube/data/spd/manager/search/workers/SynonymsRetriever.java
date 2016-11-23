package org.gcube.data.spd.manager.search.workers;

import org.gcube.common.core.types.VOID;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.utils.QueryRetryCall;


public class SynonymsRetriever extends Worker<String, String> {

	private AbstractPlugin plugin;
	
	public SynonymsRetriever(ClosableWriter<String> writer,AbstractPlugin plugin) {
		super(writer);
		this.plugin = plugin;
	}

	@Override
	protected void execute(final String input, final ObjectWriter<String> outputWriter) {
		logger.debug("executing expander for "+input+" in plugin "+plugin.getRepositoryName());
		outputWriter.write(input);
		try {
			new QueryRetryCall() {

				@Override
				protected VOID execute() throws ExternalRepositoryException {
					plugin.getExpansionInterface().getSynonyms(outputWriter, input);	
					return new VOID();
				}
				
			}.call();
		} catch (MaxRetriesReachedException e) {
			logger.error("error retrieving synonyms",e);
			outputWriter.write(new StreamBlockingException(plugin.getRepositoryName()));
		}			
	}

}
