package org.gcube.portlets.user.joinvre.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.gcube.portlets.user.joinvre.shared.VRE;
import org.gcube.portlets.user.joinvre.shared.VRECategory;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@RemoteServiceRelativePath("JoinService")
public interface JoinService extends RemoteService {
	
	LinkedHashMap<VRECategory, ArrayList<VRE>> getVREs();
	
	Boolean joinVRE(Long vreID);
	
}
