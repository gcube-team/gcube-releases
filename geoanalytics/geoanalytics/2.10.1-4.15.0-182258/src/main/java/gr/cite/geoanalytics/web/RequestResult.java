/**
 * 
 */
package gr.cite.geoanalytics.web;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author vfloros
 *
 */
public class RequestResult {
	private String url = "";
	private InputStream is = null;
	private OutputStream os = null;
	private boolean success;
	private long timing = 0L;
	private String layerID = "";
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public InputStream getIs() {
		return is;
	}
	public void setIs(InputStream is) {
		this.is = is;
	}
	public OutputStream getOs() {
		return os;
	}
	public void setOs(OutputStream os) {
		this.os = os;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public long getTiming() {
		return timing;
	}
	public void setTiming(long timing) {
		this.timing = timing;
	}
	public String getLayerID() {
		return layerID;
	}
	public void setLayerID(String layerID) {
		this.layerID = layerID;
	}
}
