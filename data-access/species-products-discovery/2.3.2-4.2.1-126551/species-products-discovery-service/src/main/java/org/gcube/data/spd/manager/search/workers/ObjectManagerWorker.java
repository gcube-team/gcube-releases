package org.gcube.data.spd.manager.search.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.ResultElementWriterManager;


public class ObjectManagerWorker<I extends ResultElement> extends Worker<I, I> {

	ResultElementWriterManager<I> writerManager;
	
	public ObjectManagerWorker(ClosableWriter<I> writer, ResultElementWriterManager<I> writerManager) {
		super(writer);
		this.writerManager = writerManager;
	}

	@Override
	protected void execute(I input, ObjectWriter<I> outputWriter) {
		if (writerManager.filter(input))
			outputWriter.write(writerManager.enrich(input));
	}


}
