package gr.cite.gaap.datatransferobjects.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FunctionResponse {
	private List<UUID> layerIDs = new ArrayList<UUID>();

	public List<UUID> getLayerIDs() {
		return layerIDs;
	}

	public void setLayerIDs(List<UUID> layerIDs) {
		this.layerIDs = layerIDs;
	}
}
