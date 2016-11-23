/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface StreamPagingLoaderListener {
	
	public void onStreamStartLoading();
	public void onStreamUpdate(int streamSize, int currentStartItem, int currentEndItem);
	public void onStreamLoadingComplete();

}
