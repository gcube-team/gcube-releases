package gr.uoa.di.madgik.grsbroker.helpmanagers;

public class FileMapping {
	private String savePath;
	private String realName;

	public FileMapping(String savePath, String realName) {
		super();
		this.savePath = savePath;
		this.realName = realName;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Override
	public String toString() {
		return this.savePath + " " + this.realName;
	}
}
