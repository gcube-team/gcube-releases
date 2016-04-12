package org.gcube.application.aquamaps.publisher;

public class StoreConfiguration {

	public static enum StoreMode{
		UPDATE_EXISTING,USE_EXISTING
	}
	
	private StoreMode mode=StoreMode.USE_EXISTING;
	private UpdateConfiguration updateSettings=new UpdateConfiguration(true, true, true);
	public StoreConfiguration(StoreMode mode, UpdateConfiguration updateSettings) {
		super();
		this.mode = mode;
		this.updateSettings = updateSettings;
	}
	public StoreMode getMode() {
		return mode;
	}
	public UpdateConfiguration getUpdateSettings() {
		return updateSettings;
	}
	
	
	
}
