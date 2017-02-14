package org.gcube.data.analysis.dataminermanagercl.shared.data;

import java.io.Serializable;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;


/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class OutputData implements Serializable {

	private static final long serialVersionUID = -3039151542008171640L;
	private ComputationId computationId;
	private Resource resource;

	public OutputData() {
		super();
	}

	public OutputData(ComputationId computationId, Resource resource) {
		super();
		this.computationId = computationId;
		this.resource = resource;
	}

	public ComputationId getComputationId() {
		return computationId;
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return "OutputData [computationId=" + computationId + ", resource="
				+ resource + "]";
	}

}
