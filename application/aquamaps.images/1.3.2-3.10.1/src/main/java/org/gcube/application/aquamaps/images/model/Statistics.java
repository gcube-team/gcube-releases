package org.gcube.application.aquamaps.images.model;

import org.gcube.application.aquamaps.images.Common;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("Statistics")
public class Statistics {
	
	@XStreamAlias("Configuration")
	public class Configuration{
		private String scope;
		private String suitableId;
		private String nativeId;
		private String suitable2050Id;
		private String native2050Id;
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public String getSuitableId() {
			return suitableId;
		}
		public void setSuitableId(String suitableId) {
			this.suitableId = suitableId;
		}
		public String getNativeId() {
			return nativeId;
		}
		public void setNativeId(String nativeId) {
			this.nativeId = nativeId;
		}
		public String getSuitable2050Id() {
			return suitable2050Id;
		}
		public void setSuitable2050Id(String suitable2050Id) {
			this.suitable2050Id = suitable2050Id;
		}
		public String getNative2050Id() {
			return native2050Id;
		}
		public void setNative2050Id(String native2050Id) {
			this.native2050Id = native2050Id;
		}
		public Configuration(String scope, String suitableId, String nativeId,
				String suitable2050Id, String native2050Id) {
			super();
			this.scope = scope;
			this.suitableId = suitableId;
			this.nativeId = nativeId;
			this.suitable2050Id = suitable2050Id;
			this.native2050Id = native2050Id;
		}
		public Configuration() {
			// TODO Auto-generated constructor stub
		}
	}
	
	private long speciesCount=0;
	private long pictureCount=0;
	private long nativeCount=0;
	private long native2050Count=0;
	private long suitableCount=0;
	private long suitable2050Count=0;
	private long lastUpdateTime=0;
	
	private Configuration configuration;

	public Statistics() {
		// TODO Auto-generated constructor stub
	}
	
	public Statistics(long speciesCount, long pictureCount, long nativeCount,
			long native2050Count, long suitableCount, long suitable2050Count,
			long lastUpdateTime) {
		super();
		this.speciesCount = speciesCount;
		this.pictureCount = pictureCount;
		this.nativeCount = nativeCount;
		this.native2050Count = native2050Count;
		this.suitableCount = suitableCount;
		this.suitable2050Count = suitable2050Count;
		this.lastUpdateTime = lastUpdateTime;
		Common com=Common.get();
		configuration = new Configuration(
				com.getProperty(Common.SCOPE_PROP), 
				com.getProperty(Common.SUITABLE_PROP), 
				com.getProperty(Common.NATIVE_PROP), 
				com.getProperty(Common.SUITABLE_2050_PROP), 
				com.getProperty(Common.NATIVE_2050_PROP));
	}

	public long getSpeciesCount() {
		return speciesCount;
	}

	public void setSpeciesCount(long speciesCount) {
		this.speciesCount = speciesCount;
	}

	public long getPictureCount() {
		return pictureCount;
	}

	public void setPictureCount(long pictureCount) {
		this.pictureCount = pictureCount;
	}

	public long getNativeCount() {
		return nativeCount;
	}

	public void setNativeCount(long nativeCount) {
		this.nativeCount = nativeCount;
	}

	public long getNative2050Count() {
		return native2050Count;
	}

	public void setNative2050Count(long native2050Count) {
		this.native2050Count = native2050Count;
	}

	public long getSuitableCount() {
		return suitableCount;
	}

	public void setSuitableCount(long suitableCount) {
		this.suitableCount = suitableCount;
	}

	public long getSuitable2050Count() {
		return suitable2050Count;
	}

	public void setSuitable2050Count(long suitable2050Count) {
		this.suitable2050Count = suitable2050Count;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	
}
