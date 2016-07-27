package gr.uoa.di.madgik.hive.parse.responses;

public class LoadResponse extends ParseResponse{
	private String fromPath;
	private String toPath;
	/**
	 * @return the fromPath
	 */
	public String getFromPath() {
		return fromPath;
	}
	/**
	 * @param fromPath the fromPath to set
	 */
	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}
	/**
	 * @return the toPath
	 */
	public String getToPath() {
		return toPath;
	}
	/**
	 * @param toPath the toPath to set
	 */
	public void setToPath(String toPath) {
		this.toPath = toPath;
	}
}
