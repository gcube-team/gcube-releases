package gr.cite.gaap.datatransferobjects;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import gr.cite.gaap.datatransferobjects.request.GeoNetworkMetadataDTO;
import gr.cite.geoanalytics.util.http.CustomException;

public class GeoNetworkPublishDataDTO {

	private GeoNetworkMetadataDTO geoNetworkMetadataDTO;
	private String layerId;

	public GeoNetworkMetadataDTO getGeoNetworkMetadataDTO() {
		return geoNetworkMetadataDTO;
	}

	public void setGeoNetworkMetadataDTO(GeoNetworkMetadataDTO geoNetworkMetadataDTO) {
		this.geoNetworkMetadataDTO = geoNetworkMetadataDTO;
	}

	public String getLayerId() {
		return layerId;
	}

	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	public void validate() throws Exception {
		try {
			Assert.hasLength(layerId, "No layer is selected");
			Assert.notNull(geoNetworkMetadataDTO, "Missing GeoNetwork metadata");
			geoNetworkMetadataDTO.validate();
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
