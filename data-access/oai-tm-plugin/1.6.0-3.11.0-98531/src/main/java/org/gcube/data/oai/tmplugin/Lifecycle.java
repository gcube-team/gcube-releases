package org.gcube.data.oai.tmplugin;

import java.util.Timer;
import java.util.TimerTask;

import org.gcube.data.oai.tmplugin.repository.Summary;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.gcube.data.tmf.impl.LifecycleAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class Lifecycle extends LifecycleAdapter {

	private static final long serialVersionUID = -7621722603411835052L;

	private static final long startDelay= 60*1000; //delay of 1 minute
	private static final long periodRepeating= (((60*1000)*60)*24)*7; // repeat period of 7 days 

	private static Logger log = LoggerFactory.getLogger(Lifecycle.class);

	private OAISource source;

	public Lifecycle(OAISource source) {

		log.info("source " + source);
		this.source=source;
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.tmf.impl.LifecycleAdapter#reconfigure(org.w3c.dom.Element)
	 */
	@Override
	public void reconfigure(Element request) throws InvalidRequestException, Exception {
		super.reconfigure(request);
	}

	/**{@inheritDoc}*/
	@Override
	public void init() throws Exception {
		log.info("initialising source {}", source.id());

		Timer timer= new Timer();
		timer.schedule(new UpdateTask(), startDelay, periodRepeating);
	}


	/**{@inheritDoc}*/
	public void resume() {
		log.info("resuming collection {}",source.id());

		Timer timer= new Timer();
		timer.schedule(new UpdateTask(), startDelay, periodRepeating);
	}

	private class UpdateTask extends TimerTask { 

		@Override
		public void run() {

			try{
				log.info("source.reader() "+source.reader());
				Summary pair = (source.reader()).summary();
				log.trace("cardinality is {} and lastUpdate is {}",pair.cardinality(),pair.lastUpdate().getTime());
				source.setCardinality(pair.cardinality());
				source.setLastUpdate(pair.lastUpdate());
			}catch (Exception e) {
				log.warn("error computing collection summary for collection {}",source.id(),e);
			}
			log.info("computed summary for collection "+source.id());
		}
	}
}
