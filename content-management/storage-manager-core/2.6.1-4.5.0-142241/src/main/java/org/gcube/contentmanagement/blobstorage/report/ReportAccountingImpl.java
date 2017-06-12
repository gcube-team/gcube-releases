package org.gcube.contentmanagement.blobstorage.report;

	/**
	 * Void implementation of Report interface
	 * @author Roberto Cirillo (ISTI-CNR)
	 *
	 */
	@Deprecated
	public class ReportAccountingImpl implements Report {

	@Override
	public void init(String consumerId, String resourceScope) {
	}
	
	
	@Override
	public void timeUpdate() {
	}
	
	@Override
	public void ultimate(String owner, String uri, String operation, String size ) {
	}

	@Override
	public void send() {
	}
	
}
