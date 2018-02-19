package org.gcube.data.spd.utils;

import javax.xml.bind.JAXBException;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.ResultElement;
import org.glassfish.jersey.server.ChunkedOutput;

public class ResultStreamingThread<T extends ResultElement> extends Thread{

	private JerseyWriter<T, String> writer;
	private ChunkedOutput<String> output;
	private Long startTime;
	
	public ResultStreamingThread(Class<T> clazz) {
		output = new ChunkedOutput<String>(String.class);
		writer = new JerseyWriter<T,String>(output){

			@Override
			public String convert(T input) {
				try {
					return "<Result>"+Bindings.toXml(input)+"</Result>";
				} catch (JAXBException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String header() {
				return "<Results>";
			}

			@Override
			public String footer() {
				return "</Results>";
			}
			
			
		};
	}


	@Override
	public void run() {
		this.startTime = System.currentTimeMillis();
		while (!writer.isClosed()){
    		try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}

	public JerseyWriter<T, String> getWriter() {
		return writer;
	}


	public ChunkedOutput<String> getOutput() {
		return output;
	}


	public long getStartTime() {
		return startTime;
	}
	
}

