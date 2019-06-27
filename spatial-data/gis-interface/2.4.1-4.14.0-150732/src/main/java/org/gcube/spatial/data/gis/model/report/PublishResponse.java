package org.gcube.spatial.data.gis.model.report;


import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.opengis.metadata.Metadata;

public class PublishResponse extends Report{

	private Metadata passedMetadata;
	private Metadata publishedMetadata;
	private long returnedMetaId;
	
	
	public PublishResponse() {
		// TODO Auto-generated constructor stub
	}

	public PublishResponse(Metadata passedMetadata){
		setPassedMetadata(passedMetadata);
	}
	
	
	/**
	 * @return the passedMetadata
	 */
	public Metadata getPassedMetadata() {
		return passedMetadata;
	}

	/**
	 * @param passedMetadata the passedMetadata to set
	 */
	public void setPassedMetadata(Metadata passedMetadata) {
		this.passedMetadata = new DefaultMetadata(passedMetadata);
	}

	/**
	 * @return the publishedMetadata
	 */
	public Metadata getPublishedMetadata() {
		return publishedMetadata;
	}

	/**
	 * @param publishedMetadata the publishedMetadata to set
	 */
	public void setPublishedMetadata(Metadata publishedMetadata) {
		this.publishedMetadata = publishedMetadata;
	}

	/**
	 * @return the returnedMetaId
	 */
	public long getReturnedMetaId() {
		return returnedMetaId;
	}

	/**
	 * @param returnedMetaId the returnedMetaId to set
	 */
	public void setReturnedMetaId(long returnedMetaId) {
		this.returnedMetaId = returnedMetaId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PublishResponse [passedMetadata=");
		builder.append(passedMetadata);
		builder.append(", publishedMetadata=");
		builder.append(publishedMetadata);
		builder.append(", returnedMetaId=");
		builder.append(returnedMetaId);
		builder.append(", report=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	
	
	
}
