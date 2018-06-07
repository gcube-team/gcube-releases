package gr.cite.additionalemailaddresses.authorize;

import javax.portlet.PortletRequest;

/**
 * @author mnikolopoulos
 *
 */
public interface Authorize {

	<T extends PortletRequest> Boolean hasPermisions(T porteletRequest);
}
