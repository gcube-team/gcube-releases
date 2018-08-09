package org.gcube.portlets.user.geoexplorer.shared.metadata.quality;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.geoexplorer.shared.metadata.ResponsiblePartyItem;

public class ProcessStepItem implements Serializable{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -3416915982335261933L;


	/**
     * Description of the event, including related parameters or tolerances.
     *
     * @return Description of the event.
     */
    private String description;
    

    /**
     * Requirement or purpose for the process step.
     *
     * @return Requirement or purpose for the process step, or {@code null}.
     */
    private String rationale;
    
	/**
     * Date and time or range of date and time on or over which the process step occurred.
     * <p>
     * <TABLE WIDTH="80%" ALIGN="center" CELLPADDING="18" BORDER="4" BGCOLOR="#FFE0B0">
     *   <TR><TD>
     *     <P align="justify"><B>Warning:</B> The return type of this method may change
     *     in GeoAPI 3.1 release. It may be replaced by a type matching more closely
     *     either ISO 19108 (<cite>Temporal Schema</cite>) or ISO 19103.</P>
     *   </TD></TR>
     * </TABLE>
     *
     * @return Date on or over which the process step occurred, or {@code null}.
     */
    private Date dateTime;


    /**
     * Identification of, and means of communication with, person(s) and
     * organization(s) associated with the process step.
     *
     * @return Means of communication with person(s) and organization(s) associated
     *         with the process step.
     */
    private List<ResponsiblePartyItem>  processor;


//    /**
//     * Information about the source data used in creating the data specified by the scope.
//     *
//     * @return Information about the source data used in creating the data.
//     */
//    @UML(identifier="source", obligation=OPTIONAL, specification=ISO_19115)
//    Collection<? extends Source> getSources();
//
//    /**
//     * Description of the product generated as a result of the process step.
//     *
//     * @return Product generated as a result of the process step.
//     *
//     * @since 2.3
//     */
//    @UML(identifier="output", obligation=OPTIONAL, specification=ISO_19115_2)
//    Collection<? extends Source> getOutputs();

    /**
     * Comprehensive information about the procedure by which the algorithm was applied
     * to derive geographic data from the raw instrument measurements, such as datasets,
     * software used, and the processing environment.
     *
     * @return Procedure by which the algorithm was applied to derive geographic data
     *         from the raw instrument measurements
     *
     * @since 2.3
     */
    private ProcessingItem processingInformation;


    public ProcessStepItem() {
	}
    
    

	public ProcessStepItem(String description, String rationale, Date dateTime,
			List<ResponsiblePartyItem> processor,
			ProcessingItem processingInformation) {
		this.description = description;
		this.rationale = rationale;
		this.dateTime = dateTime;
		this.processor = processor;
		this.processingInformation = processingInformation;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getRationale() {
		return rationale;
	}


	public void setRationale(String rationale) {
		this.rationale = rationale;
	}


	public Date getDateTime() {
		return dateTime;
	}


	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}


	public Collection<ResponsiblePartyItem> getProcessor() {
		return processor;
	}


	public void setProcessor(List<ResponsiblePartyItem> processor) {
		this.processor = processor;
	}


	public ProcessingItem getProcessingInformation() {
		return processingInformation;
	}


	public void setProcessingInformation(ProcessingItem processingInformation) {
		this.processingInformation = processingInformation;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessStepItem [description=");
		builder.append(description);
		builder.append(", rationale=");
		builder.append(rationale);
		builder.append(", dateTime=");
		builder.append(dateTime);
		builder.append(", processor=");
		builder.append(processor);
		builder.append(", processingInformation=");
		builder.append(processingInformation);
		builder.append("]");
		return builder.toString();
	}
	
	

}
