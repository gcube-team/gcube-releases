/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ConsoleMessage.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.console;

import java.util.Date;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ConsoleMessage extends BaseModelData {

	private static final long serialVersionUID = 1422116123911146319L;

	public ConsoleMessage(final Object caller, final ConsoleLogSeverity type, final String message) {
		this(caller.getClass(), type, message);
	}

	public ConsoleMessage(final Class<?> invoker, final ConsoleLogSeverity type, final String message) {
		this(invoker.getName(), type, message);
	}

	public ConsoleMessage(final String invoker, final ConsoleLogSeverity type, final String message) {
		super();
		DateTimeFormat timeFormatter = DateTimeFormat.getFormat("dd-MM-yy hh:mm:ss");
		String timestamp = timeFormatter.format(new Date());
		set("timestamp", timestamp);
		set("invoker", invoker);
		set("type", type);
		set("message", message);
	}

}
