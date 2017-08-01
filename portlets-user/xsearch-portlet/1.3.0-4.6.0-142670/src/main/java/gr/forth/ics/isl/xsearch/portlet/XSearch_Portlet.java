/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */
package gr.forth.ics.isl.xsearch.portlet;

import java.io.IOException;
import javax.portlet.*;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * XSearch_Portlet Portlet
 *
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
public class XSearch_Portlet extends GenericPortlet {

    /**
     * 
     * @param request RenderRequest
     * @param response RenderResponse 
     * @throws PortletException
     * @throws IOException 
     */
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        ScopeHelper.setContext(request);
        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/XSearch_Portlet_view.jsp");
        dispatcher.include(request, response);

    }

    /**
     * 
     * @param request RenderRequest
     * @param response RenderResponse
     * @throws PortletException
     * @throws IOException 
     */
    public void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        response.setContentType("text/html");

        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/XSearch_Portlet_edit.jsp");
        dispatcher.include(request, response);

    }

    /**
     * 
     * @param request RenderRequest
     * @param response RenderResponse
     * @throws PortletException
     * @throws IOException 
     */
    public void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        response.setContentType("text/html");

        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/XSearch_Portlet_help.jsp");
        dispatcher.include(request, response);

    }

    /**
     * 
     * @param request ActionRequest
     * @param response ActionResponse
     * @throws PortletException
     * @throws IOException 
     */
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {
    }
}
