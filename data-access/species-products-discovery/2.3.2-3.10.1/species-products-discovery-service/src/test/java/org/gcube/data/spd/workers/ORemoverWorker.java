package org.gcube.data.spd.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class ORemoverWorker extends Worker<String, String> {


	public ORemoverWorker(ClosableWriter<String> writer) {
		super(writer);
	}

	@Override
	protected void execute(String input, ObjectWriter<String> outputWriter) {
		outputWriter.write(input.replaceAll("o", ""));
	}

}
