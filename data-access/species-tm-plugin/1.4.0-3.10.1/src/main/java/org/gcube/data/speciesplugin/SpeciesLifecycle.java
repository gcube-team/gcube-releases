/**
 * 
 */
package org.gcube.data.speciesplugin;

import java.io.File;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gcube.common.clients.exceptions.InvalidRequestException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.data.speciesplugin.requests.RequestBinder;
import org.gcube.data.speciesplugin.requests.SpeciesRequest;
import org.gcube.data.speciesplugin.store.SpeciesNeoStore;
import org.gcube.data.speciesplugin.store.SpeciesStore;
import org.gcube.data.speciesplugin.utils.DirDeleter;
import org.gcube.data.speciesplugin.utils.SpeciesService;
import org.gcube.data.speciesplugin.utils.SpeciesUpdateScheduler;
import org.gcube.data.tmf.impl.LifecycleAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class SpeciesLifecycle extends LifecycleAdapter {

	private static final long serialVersionUID = 1L;

	/**
	 * The storage location.
	 */
	public static final String STORAGE_LOCATION = "store";

	protected static RequestBinder db = new RequestBinder();

	private static Logger log = LoggerFactory.getLogger(SpeciesLifecycle.class);

	private final SpeciesSource source;

	protected List<String> scientificNames;
	protected List<String> datasources;
	protected boolean strictMatch;

	protected long lastUpdate = -1;
	protected transient ScheduledFuture<?> scheduled;

	protected long updatePeriod;
	protected TimeUnit timeUnit;


	/**
	 * Creates an instance for a given {@link SpeciesSource}
	 * @param source the source
	 */
	public SpeciesLifecycle(SpeciesSource source, List<String> scientificNames, List<String> datasources, boolean strictMatch, long updatePeriod, TimeUnit timeUnit) {
		this.source = source;
		this.scientificNames = scientificNames;
		this.datasources = datasources;
		this.strictMatch = strictMatch;
		this.updatePeriod = updatePeriod;
		this.timeUnit = timeUnit;
	}

	/**{@inheritDoc}*/
	@Override
	public void init() throws Exception {

		try {
			log.info("initialising source "+source.id());
			File location = source.environment().file(STORAGE_LOCATION);
			source.store().start(location);
			loadCollection();
			scheduleRefresh();
		} catch(Exception e)
		{
			log.error("An error occurred initializing the source", e);
			throw new Exception("Error occured during species retrieving", e);
		}
	}

	protected void scheduleRefresh()
	{
		log.trace("scheduling refresh");
		if (updatePeriod<=0 || timeUnit == null) {
			log.trace("scheduleRefresh aborted because of invalid parameters updatePeriod: "+updatePeriod+" timeUnit: "+timeUnit);
			return;
		}

		final GCUBEScope scope = GCUBEScopeManager.DEFAULT.getScope();
		Runnable refresher = new Runnable(){
			public void run() {
				log.trace("Refresh task setting scope {}", scope);
				GCUBEScopeManager.DEFAULT.setScope(scope);
				refreshCollection();
			}
		}; 

		long initialDelay = calculateInitialRefreshDelay();

		log.trace("scheduling refresh for source "+source.id()+" initialDelay: "+initialDelay+" updatePeriod: "+updatePeriod+" timeUnit: "+timeUnit);

		scheduled = SpeciesUpdateScheduler.getInstance().scheduleAtFixedRate(refresher, initialDelay, updatePeriod, timeUnit);
	}

	protected long calculateInitialRefreshDelay()
	{
		//first scheduling
		if (lastUpdate < 0) return updatePeriod;

		//already scheduled
		if (scheduled != null) return updatePeriod;

		//first scheduling after shutdown
		long elapsedTime = System.currentTimeMillis() - lastUpdate;
		log.trace("elapsed time from the last update: {}", elapsedTime);

		long expectedDelayMillis = timeUnit.toMillis(elapsedTime) - elapsedTime;
		log.trace("expected delay in millis {}", expectedDelayMillis);

		expectedDelayMillis = (expectedDelayMillis<0)?0:expectedDelayMillis;

		long expectedDelay = timeUnit.convert(expectedDelayMillis, TimeUnit.MILLISECONDS);
		return (expectedDelay>=0)?expectedDelay:updatePeriod;	
	}

	protected void rescheduleRefresh()
	{
		unscheduleRefresh();
		scheduleRefresh();
	}

	protected void refreshCollection() 
	{
		log.trace("starting collection {} refresh",source.id());
		try {
			File tmpFolder = Utils.createTempDirectory();
			log.trace("tmp store location: "+tmpFolder);

			SpeciesStore tmpStore = new SpeciesNeoStore(source.id());
			tmpStore.start(tmpFolder);

			log.trace("filling tmp store");
			SpeciesService client = new SpeciesService(tmpStore);
			client.createCollection(scientificNames, datasources, strictMatch);

			log.trace("stopping tmp store");
			tmpStore.stop();

			// give time to shutdown
			try {
				TimeUnit.MILLISECONDS.sleep(3000);
			} catch (InterruptedException e) {
				log.warn("could not wait for shutdown to complete", e);
			}

			log.trace("store switch");
			File oldStore = source.switchStore(tmpFolder);

			log.trace("scheduling old directory delete ({})", oldStore);
			SpeciesUpdateScheduler.getInstance().schedule(new DirDeleter(oldStore), 5, TimeUnit.MINUTES);

			lastUpdate = System.currentTimeMillis();
		} catch(Exception e)
		{
			log.error("An error occurred refreshing the collection for source id "+source.id(), e);
		}
	}

	protected void loadCollection() throws Exception {
		log.trace("loading collection");
		SpeciesStore store = source.store();
		SpeciesService client = new SpeciesService(store);
		client.createCollection(scientificNames, datasources, strictMatch);
	}

	/**{@inheritDoc}*/
	@Override
	public void reconfigure(Element DOMRequest) throws InvalidRequestException {
		log.info("reconfiguring source "+source.id());
		SpeciesRequest request = null;
		try {
			request = db.bind(DOMRequest);

			log.trace("request {}", request);

			if (request.getRefreshPeriod()>0 && request.getTimeUnit()!=null) {
				this.updatePeriod = request.getRefreshPeriod();
				this.timeUnit = request.getTimeUnit();
				rescheduleRefresh();
			}

			boolean refresh = false;
			if (request.getScientificNames().size()!=0) {
				scientificNames = request.getScientificNames();
				refresh = true;
			}

			if (request.getDatasources().size()!=0) {
				datasources = request.getDatasources();
				refresh = true;
			}

			if (refresh) {
				refreshCollection();
				rescheduleRefresh();
			}
		} catch (Exception e) {
			log.error("Error reconfiguring source "+source.id(), e);
		}
	}

	/**{@inheritDoc}*/
	@Override
	public void terminate() {
		log.info("removing source "+source.id());
		unscheduleRefresh();
		source.store().delete();
	}

	/**{@inheritDoc}*/
	@Override
	public void stop() {
		log.info("stopping source "+source.id()+" on container shutdown");
		unscheduleRefresh();
		source.store().stop();
	}

	protected void unscheduleRefresh()
	{
		if (scheduled!=null) {
			scheduled.cancel(true);
			scheduled = null;
		}
	}
}
