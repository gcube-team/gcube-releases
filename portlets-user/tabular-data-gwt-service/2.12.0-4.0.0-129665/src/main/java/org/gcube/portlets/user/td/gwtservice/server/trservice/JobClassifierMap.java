package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.service.operation.JobClassifier;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class JobClassifierMap {
	public static JobSClassifier map(JobClassifier jobClassifier) {
		if (jobClassifier == null) {
			return JobSClassifier.UNKNOWN;
		}

		switch (jobClassifier) {
		case DATAVALIDATION:
			return JobSClassifier.DATAVALIDATION;
		case POSTPROCESSING:
			return JobSClassifier.POSTPROCESSING;
		case PREPROCESSING:
			return JobSClassifier.PREPROCESSING;
		case PROCESSING:
			return JobSClassifier.PROCESSING;
		default:
			return JobSClassifier.UNKNOWN;

		}
	}
}
