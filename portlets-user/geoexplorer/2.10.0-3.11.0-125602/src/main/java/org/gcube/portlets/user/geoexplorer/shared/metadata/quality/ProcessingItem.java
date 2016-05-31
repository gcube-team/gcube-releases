package org.gcube.portlets.user.geoexplorer.shared.metadata.quality;

import java.io.Serializable;
import java.util.Collection;

import org.gcube.portlets.user.geoexplorer.shared.metadata.citation.CitationItem;

public class ProcessingItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1709520234825420028L;


	/**
     * Information to identify the processing package that produced the data.
     *
     * @return Identifier of the processing package that produced the data.
     */
	private String identifier;
    

    /**
     * Reference to document describing processing software.
     *
     * @return Document describing processing software.
     */
    private Collection<? extends CitationItem> softwareReference;

    /**
     * Additional details about the processing procedures.
     *
     * @return Processing procedures.
     */
    private String procedureDescription;

    /**
     * Reference to documentation describing the processing.
     *
     * @return Documentation describing the processing.
     */
    private Collection<? extends CitationItem> documentation;

    /**
     * Parameters to control the processing operations, entered at run time.
     *
     * @return Parameters to control the processing operations.
     */
    private String runTimeParameters;

    /**
     * Details of the methodology by which geographic information was derived from the
     * instrument readings.
     *
     * @return Methodology by which geographic information was derived from the
     * instrument readings.
     */
    private Collection<? extends AlgorithmItem> algorithm;
    
    public ProcessingItem(){
    }
    

	public ProcessingItem(String identifier,
			Collection<? extends CitationItem> softwareReference,
			String procedureDescription,
			Collection<? extends CitationItem> documentation,
			String runTimeParameters,
			Collection<? extends AlgorithmItem> algorithm) {
		super();
		this.identifier = identifier;
		this.softwareReference = softwareReference;
		this.procedureDescription = procedureDescription;
		this.documentation = documentation;
		this.runTimeParameters = runTimeParameters;
		this.algorithm = algorithm;
	}


	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public String getProcedureDescription() {
		return procedureDescription;
	}

	public void setProcedureDescription(String procedureDescription) {
		this.procedureDescription = procedureDescription;
	}

	public Collection<? extends CitationItem> getDocumentation() {
		return documentation;
	}

	public void setDocumentation(Collection<? extends CitationItem> documentation) {
		this.documentation = documentation;
	}

	public String getRunTimeParameters() {
		return runTimeParameters;
	}

	public void setRunTimeParameters(String runTimeParameters) {
		this.runTimeParameters = runTimeParameters;
	}

	public Collection<? extends AlgorithmItem> getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Collection<? extends AlgorithmItem> algorithm) {
		this.algorithm = algorithm;
	}


	public Collection<? extends CitationItem> getSoftwareReference() {
		return softwareReference;
	}


	public void setSoftwareReference(
			Collection<? extends CitationItem> softwareReference) {
		this.softwareReference = softwareReference;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessingItem [identifier=");
		builder.append(identifier);
		builder.append(", softwareReference=");
		builder.append(softwareReference);
		builder.append(", procedureDescription=");
		builder.append(procedureDescription);
		builder.append(", documentation=");
		builder.append(documentation);
		builder.append(", runTimeParameters=");
		builder.append(runTimeParameters);
		builder.append(", algorithm=");
		builder.append(algorithm);
		builder.append("]");
		return builder.toString();
	}

}
