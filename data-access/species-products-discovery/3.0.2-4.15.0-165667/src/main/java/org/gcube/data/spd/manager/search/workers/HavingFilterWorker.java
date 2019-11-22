package org.gcube.data.spd.manager.search.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.dataaccess.spd.havingengine.HavingStatement;

public class HavingFilterWorker<T> extends Worker<T, T> {

	private HavingStatement<T> having;
	
	public HavingFilterWorker(ClosableWriter<T> writer, HavingStatement<T> having) {
		super(writer);
		this.having = having;
	}

	@Override
	protected void execute(T input, ObjectWriter<T> outputWriter) {
		if (having.accept(input))
			outputWriter.write(input);
		else logger.trace("object discarded by having clause");
	}

}
