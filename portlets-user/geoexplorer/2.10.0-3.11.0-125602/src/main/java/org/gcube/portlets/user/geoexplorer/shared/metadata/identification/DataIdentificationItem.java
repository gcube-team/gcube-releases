package org.gcube.portlets.user.geoexplorer.shared.metadata.identification;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.gcube.portlets.user.geoexplorer.shared.metadata.extent.ExtentItem;


public class DataIdentificationItem extends IdentificationItem implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1804571001181169201L;

	/**
     * Language(s) used within the dataset.
     *
     * @return Language(s) used.
     */
    List<Locale> language;

    /**
     * Full name of the character coding standard(s) used for the dataset.
     *
     * @return Name(s) of the character coding standard(s) used.
     *
     */
    List<String> characterSet;

    /**
     * Main theme(s) of the dataset.
     *
     * @return Main theme(s).
     *
     */
    List<String> topicCategory;

    /**
     * Description of the dataset in the producer's processing environment, including items
     * such as the software, the computer operating system, file name, and the dataset size.
     *
     */
    String environmentDescription;

    /**
     * Additional extent information including the bounding polygon, vertical, and temporal
     * extent of the dataset.
     *
     * @return Additional extent information.
     */
    List<ExtentItem> extent;

    /**
     * Any other descriptive information about the dataset.
     *
     */
    String supplementalInformation;
    
    
    public DataIdentificationItem(){
    	
    }


	public DataIdentificationItem(List<Locale> language,
			List<String> characterSet, List<String> topicCategory,
			String environmentDescription, List<ExtentItem> extent,
			String supplementalInformation) {
		super();
		this.language = language;
		this.characterSet = characterSet;
		this.topicCategory = topicCategory;
		this.environmentDescription = environmentDescription;
		this.extent = extent;
		this.supplementalInformation = supplementalInformation;
	}


	public List<Locale> getLanguage() {
		return language;
	}

	public void setLanguage(List<Locale> language) {
		this.language = language;
	}



	public String getEnvironmentDescription() {
		return environmentDescription;
	}

	public void setEnvironmentDescription(String environmentDescription) {
		this.environmentDescription = environmentDescription;
	}

	public List<ExtentItem> getExtent() {
		return extent;
	}

	public void setExtent(List<ExtentItem> extent) {
		this.extent = extent;
	}

	public String getSupplementalInformation() {
		return supplementalInformation;
	}

	public void setSupplementalInformation(String supplementalInformation) {
		this.supplementalInformation = supplementalInformation;
	}

	public List<String> getCharacterSet() {
		return characterSet;
	}


	public void setCharacterSet(List<String> characterSet) {
		this.characterSet = characterSet;
	}


	public List<String> getTopicCategory() {
		return topicCategory;
	}


	public void setTopicCategory(List<String> topicCategory) {
		this.topicCategory = topicCategory;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("DataIdentificationItem [language=");
		builder.append(language);
		builder.append(", characterSet=");
		builder.append(characterSet);
		builder.append(", topicCategory=");
		builder.append(topicCategory);
		builder.append(", environmentDescription=");
		builder.append(environmentDescription);
		builder.append(", extent=");
		builder.append(extent);
		builder.append(", supplementalInformation=");
		builder.append(supplementalInformation);
		builder.append("]");
		return builder.toString();
	}
}
