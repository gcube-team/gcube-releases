package org.gcube.portlets.user.geoexplorer.shared.metadata.quality;

import java.io.Serializable;
import java.util.Collection;

import org.geotoolkit.metadata.iso.quality.DefaultScope;

public class LineageItem implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -110220527009784701L;
	
	
	/**
     * General explanation of the data producer's knowledge about the lineage of a dataset.
     * Should be provided only if {@linkplain DefaultScope#getLevel scope level} is
     */
    private String statement;

    /**
     * Information about an event in the creation process for the data specified by the scope.
     */
    private Collection<ProcessStepItem> processStep;

//    /**
//     * Information about the source data used in creating the data specified by the scope.
//     */
//    private Collection<Source> sources;

    /**
     * Constructs an initially empty lineage.
     */
    public LineageItem() {
    }

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * @return Information about an event in the creation process.
	 */
	public Collection<ProcessStepItem> getProcessStep() {
		return processStep;
	}

	public void setProcessStep(Collection<ProcessStepItem> processStep) {
		this.processStep = processStep;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LineageItem [statement=");
		builder.append(statement);
		builder.append(", processStep=");
		builder.append(processStep);
		builder.append("]");
		return builder.toString();
	}

}
