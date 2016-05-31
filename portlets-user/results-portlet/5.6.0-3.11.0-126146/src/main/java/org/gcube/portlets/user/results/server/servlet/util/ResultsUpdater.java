package org.gcube.portlets.user.results.server.servlet.util;




import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.search.library.impl.ResultSetConsumer;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.util.DisableButtons;
import org.gcube.portlets.user.results.client.model.ResultNumber;
import org.gcube.portlets.user.results.server.servlet.NewresultsetServiceImpl;

public class ResultsUpdater extends Thread {

	private ResultSetConsumer consumer;
	private int resultsPerPage;
	private ASLSession session;
	private boolean isBrowse = false;
	private boolean keepGoing = true;


	public ResultsUpdater(ResultSetConsumerI consumer, int resultsPerPage, ASLSession session, boolean isBrowse) {
		this.consumer = (ResultSetConsumer) consumer;
		this.resultsPerPage = resultsPerPage;
		this.session = session;
		this.isBrowse = isBrowse;
	}

	public void run() {
	
	}

	public void setKeepGoing(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}

}

