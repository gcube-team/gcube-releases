package org.gcube.data.spd.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;

public class AddWordWorker extends Worker<String, String>{

	String word; 
	
	public AddWordWorker(ClosableWriter<String> writer, String word) {
		super(writer);
		this.word = word;
	}


	@Override
	protected void execute(String input, ObjectWriter<String> outputWriter) {
		outputWriter.write(word+input);		
	}

	
	
}
