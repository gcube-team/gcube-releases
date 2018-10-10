package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.AquaMapArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.AreasArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.EnvelopeWeightsArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FileArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.PerturbationArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.SpeciesArray;

@XmlRootElement(namespace=aquamapsTypesNS)
public class Job {
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String name;
	@XmlElement(namespace=aquamapsTypesNS)
	private int id;
	@XmlElement(namespace=aquamapsTypesNS)
	private AquaMapArray aquaMapList;
	@XmlElement(namespace=aquamapsTypesNS)
	private String status;
	@XmlElement(namespace=aquamapsTypesNS)
	private Resource hspec;
	@XmlElement(namespace=aquamapsTypesNS)
	private Resource hspen;
	@XmlElement(namespace=aquamapsTypesNS)
	private Resource hcaf;
	@XmlElement(namespace=aquamapsTypesNS)
	private SpeciesArray selectedSpecies;
	@XmlElement(namespace=aquamapsTypesNS,name="envelopCustomization")
	private PerturbationArray envelopeCustomization;
	@XmlElement(namespace=aquamapsTypesNS)
	private AreasArray selectedAreas;
	@XmlElement(namespace=aquamapsTypesNS)
	private EnvelopeWeightsArray weights;
	@XmlElement(namespace=aquamapsTypesNS)
	private String author;
	@XmlElement(namespace=aquamapsTypesNS)
	private long date;
	@XmlElement(namespace=aquamapsTypesNS)
	private FileArray relatedResources;
	@XmlElement(namespace=aquamapsTypesNS)
	private boolean gis;
	@XmlElement(namespace=aquamapsTypesNS)
	private String groupId;
	
	public Job() {
		// TODO Auto-generated constructor stub
	}

	
	
	public Job(String name, int id, AquaMapArray aquaMapList, String status,
			Resource hspec, Resource hspen, Resource hcaf,
			SpeciesArray selectedSpecies,
			PerturbationArray envelopeCustomization, AreasArray selectedAreas,
			EnvelopeWeightsArray weights, String author, long date,
			FileArray relatedResources, boolean gis, String groupId) {
		super();
		this.name = name;
		this.id = id;
		this.aquaMapList = aquaMapList;
		this.status = status;
		this.hspec = hspec;
		this.hspen = hspen;
		this.hcaf = hcaf;
		this.selectedSpecies = selectedSpecies;
		this.envelopeCustomization = envelopeCustomization;
		this.selectedAreas = selectedAreas;
		this.weights = weights;
		this.author = author;
		this.date = date;
		this.relatedResources = relatedResources;
		this.gis = gis;
		this.groupId = groupId;
	}



	/**
	 * @return the name
	 */
	public String name() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public int id() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void id(int id) {
		this.id = id;
	}

	/**
	 * @return the aquaMapList
	 */
	public AquaMapArray aquaMapList() {
		return aquaMapList;
	}

	/**
	 * @param aquaMapList the aquaMapList to set
	 */
	public void aquaMapList(AquaMapArray aquaMapList) {
		this.aquaMapList = aquaMapList;
	}

	/**
	 * @return the status
	 */
	public String status() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void status(String status) {
		this.status = status;
	}

	/**
	 * @return the hspec
	 */
	public Resource hspec() {
		return hspec;
	}

	/**
	 * @param hspec the hspec to set
	 */
	public void hspec(Resource hspec) {
		this.hspec = hspec;
	}

	/**
	 * @return the hspen
	 */
	public Resource hspen() {
		return hspen;
	}

	/**
	 * @param hspen the hspen to set
	 */
	public void hspen(Resource hspen) {
		this.hspen = hspen;
	}

	/**
	 * @return the hcaf
	 */
	public Resource hcaf() {
		return hcaf;
	}

	/**
	 * @param hcaf the hcaf to set
	 */
	public void hcaf(Resource hcaf) {
		this.hcaf = hcaf;
	}

	/**
	 * @return the selectedSpecies
	 */
	public SpeciesArray selectedSpecies() {
		return selectedSpecies;
	}

	/**
	 * @param selectedSpecies the selectedSpecies to set
	 */
	public void selectedSpecies(SpeciesArray selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}

	/**
	 * @return the envelopeCustomization
	 */
	public PerturbationArray envelopeCustomization() {
		return envelopeCustomization;
	}

	/**
	 * @param envelopeCustomization the envelopeCustomization to set
	 */
	public void envelopeCustomization(PerturbationArray envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}

	/**
	 * @return the selectedAreas
	 */
	public AreasArray selectedAreas() {
		return selectedAreas;
	}

	/**
	 * @param selectedAreas the selectedAreas to set
	 */
	public void selectedAreas(AreasArray selectedAreas) {
		this.selectedAreas = selectedAreas;
	}

	/**
	 * @return the weights
	 */
	public EnvelopeWeightsArray weights() {
		return weights;
	}

	/**
	 * @param weights the weights to set
	 */
	public void weights(EnvelopeWeightsArray weights) {
		this.weights = weights;
	}

	/**
	 * @return the author
	 */
	public String author() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void author(String author) {
		this.author = author;
	}

	/**
	 * @return the date
	 */
	public long date() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void date(long date) {
		this.date = date;
	}

	/**
	 * @return the relatedResources
	 */
	public FileArray relatedResources() {
		return relatedResources;
	}

	/**
	 * @param relatedResources the relatedResources to set
	 */
	public void relatedResources(FileArray relatedResources) {
		this.relatedResources = relatedResources;
	}

	/**
	 * @return the gis
	 */
	public boolean gis() {
		return gis;
	}

	/**
	 * @param gis the gis to set
	 */
	public void gis(boolean gis) {
		this.gis = gis;
	}

	/**
	 * @return the groupId
	 */
	public String groupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void groupId(String groupId) {
		this.groupId = groupId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Job [name=");
		builder.append(name);
		builder.append(", id=");
		builder.append(id);
		builder.append(", aquaMapList=");
		builder.append(aquaMapList);
		builder.append(", status=");
		builder.append(status);
		builder.append(", hspec=");
		builder.append(hspec);
		builder.append(", hspen=");
		builder.append(hspen);
		builder.append(", hcaf=");
		builder.append(hcaf);
		builder.append(", selectedSpecies=");
		builder.append(selectedSpecies);
		builder.append(", envelopeCustomization=");
		builder.append(envelopeCustomization);
		builder.append(", selectedAreas=");
		builder.append(selectedAreas);
		builder.append(", weights=");
		builder.append(weights);
		builder.append(", author=");
		builder.append(author);
		builder.append(", date=");
		builder.append(date);
		builder.append(", relatedResources=");
		builder.append(relatedResources);
		builder.append(", gis=");
		builder.append(gis);
		builder.append(", groupId=");
		builder.append(groupId);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
