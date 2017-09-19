package org.gcube.portlets.user.newsfeed.server;
/**
 * custom class to read propety file once
 * @author massi
 *
 */
public class CustomConfiguration {
	private int refreshTime;
	private String vreLabel;
	private boolean showTimelineSource;
	
	public CustomConfiguration(int refreshTime, String vreLabel,
			boolean showTimelineSource) {
		super();
		this.refreshTime = refreshTime;
		this.vreLabel = vreLabel;
		this.showTimelineSource = showTimelineSource;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public String getVreLabel() {
		return vreLabel;
	}

	public void setVreLabel(String vreLabel) {
		this.vreLabel = vreLabel;
	}

	public boolean isShowTimelineSource() {
		return showTimelineSource;
	}

	public void setShowTimelineSource(boolean showTimelineSource) {
		this.showTimelineSource = showTimelineSource;
	}

	@Override
	public String toString() {
		return "CustomConfiguration [refreshTime=" + refreshTime
				+ ", vreLabel=" + vreLabel + ", showTimelineSource="
				+ showTimelineSource + "]";
	}
	
}
