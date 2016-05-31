package org.gcube.portlets.user.joinvre.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface JoinServiceAsync {

	void getVREs(AsyncCallback< LinkedHashMap<VRECategory, ArrayList<VRE>>> callback);
	
	void joinVRE(Long vreID, AsyncCallback<Boolean> callback);
}
