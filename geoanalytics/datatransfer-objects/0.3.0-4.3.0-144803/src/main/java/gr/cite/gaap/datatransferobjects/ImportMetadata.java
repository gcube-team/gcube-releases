package gr.cite.gaap.datatransferobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportMetadata {
	
	private static Logger logger = LoggerFactory.getLogger(ImportMetadata.class);
	private String user;
	private String title;

	private String abstractField;
	private String purpose;
	private List<String> keywords;
	
	private String limitation;	
	private String graphicOverview;	

	private String distributorOrganisationName;
	private String distributorIndividualName;
	private String distributorOnlineResource;

	private String providerOrganisationName;
	private String providerIndividualName;
	private String providerOnlineResource;
	
	
	
	public ImportMetadata() {
		super();
		logger.trace("Initialized default contructor for ImportMetadata");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAbstractField() {
		return abstractField;
	}

	public void setAbstractField(String abstractField) {
		this.abstractField = abstractField;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getLimitation() {
		return limitation;
	}

	public void setLimitation(String limitation) {
		this.limitation = limitation;
	}

	public String getGraphicOverview() {
		return graphicOverview;
	}

	public void setGraphicOverview(String graphicOverview) {
		this.graphicOverview = graphicOverview;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDistributorOrganisationName() {
		return distributorOrganisationName;
	}

	public void setDistributorOrganisationName(String distributorOrganisationName) {
		this.distributorOrganisationName = distributorOrganisationName;
	}

	public String getDistributorIndividualName() {
		return distributorIndividualName;
	}

	public void setDistributorIndividualName(String distributorIndividualName) {
		this.distributorIndividualName = distributorIndividualName;
	}

	public String getDistributorOnlineResource() {
		return distributorOnlineResource;
	}

	public void setDistributorOnlineResource(String distributorOnlineResource) {
		this.distributorOnlineResource = distributorOnlineResource;
	}

	public String getProviderOrganisationName() {
		return providerOrganisationName;
	}

	public void setProviderOrganisationName(String providerOrganisationName) {
		this.providerOrganisationName = providerOrganisationName;
	}

	public String getProviderIndividualName() {
		return providerIndividualName;
	}

	public void setProviderIndividualName(String providerIndividualName) {
		this.providerIndividualName = providerIndividualName;
	}

	public String getProviderOnlineResource() {
		return providerOnlineResource;
	}

	public void setProviderOnlineResource(String providerOnlineResource) {
		this.providerOnlineResource = providerOnlineResource;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	public void print(){
		System.out.println("user = " + user);
		System.out.println("title = " + title);		
		System.out.println("abstractField = " + abstractField);
		System.out.println("purpose = " + purpose);	
		
		System.out.print("Keywords = ");
		for(String s : keywords){
			System.out.print(s + " ");
		}
		System.out.println();
		
		System.out.println("limitation = " + limitation);	
		System.out.println("graphicOverview = " + graphicOverview);	
		System.out.println("distributorOrganisationName = " + distributorOrganisationName);
		System.out.println("distributorIndividualName = " + distributorIndividualName);
		System.out.println("distributorOnlineResource = " + distributorOnlineResource);
		System.out.println("providerOrganisationName = " + providerOrganisationName);
		System.out.println("providerIndividualName = " + providerIndividualName);
		System.out.println("providerOnlineResource = " + providerOnlineResource);
	}
}
