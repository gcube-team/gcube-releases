package org.gcube.datacatalogue.grsf_manage_widget.shared;

/**
 * A similar grsf record.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SimilarGRSFRecord extends GenericRecord{

	private static final long serialVersionUID = 6501670015333073045L;
	private boolean suggestedMerge;

	public SimilarGRSFRecord() {
		super();
	}

	public SimilarGRSFRecord(String knowledgeBaseId, String description,
			String shortName, String title, String url,
			String semanticIdentifier, String domain) {
		super(knowledgeBaseId, description, shortName, title, url, semanticIdentifier,
				domain);
	}

	public boolean isSuggestedMerge() {
		return suggestedMerge;
	}

	public void setSuggestedMerge(boolean suggestedMerge) {
		this.suggestedMerge = suggestedMerge;
	}

	@Override
	public String toString() {
		return "SimilarGRSFRecord [record=" + super.toString() + ", suggestedMerge=" + suggestedMerge + "]";
	}
}
