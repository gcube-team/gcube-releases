package org.gcube.data.spd.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class SplitterWorker extends Worker<String, String> {


	public SplitterWorker(ClosableWriter<String> writer) {
		super(writer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(String input, ObjectWriter<String> outputWriter) {
		for (String string: input.split(";"))
			outputWriter.write(string);
	}

	
	
}
