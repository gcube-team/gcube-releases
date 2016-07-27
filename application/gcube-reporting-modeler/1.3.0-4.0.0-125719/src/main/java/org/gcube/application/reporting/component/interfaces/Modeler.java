/**
 * (c) 2013 FAO / UN (project: gcube-reporting-modeler)
 */
package org.gcube.application.reporting.component.interfaces;

import org.gcube.application.reporting.ReportsModeler;
import org.gcube.application.reporting.component.ReportSequence;

/**
 * Common interface shared (with different bound type) by {@link ReportsModeler} and {@link ReportSequence}
 *
 * History:
 *
 * ------------- --------------- -----------------------
 * Date			 Author			 Comment
 * ------------- --------------- -----------------------
 * 21/nov/2013   F. Fiorellato     Creation.
 *
 * @version 1.0
 * @since 21/nov/2013
 */
public interface Modeler<COMPONENT extends ReportComponent> {
	boolean add(COMPONENT component);
}
