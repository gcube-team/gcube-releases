package org.gcube.data.spd.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class TrimWorker extends Worker<String, String> {


	public TrimWorker(ClosableWriter<String> writer) {
		super(writer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(String input, ObjectWriter<String> outputWriter) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outputWriter.write(input.trim());		
	}

	
}
