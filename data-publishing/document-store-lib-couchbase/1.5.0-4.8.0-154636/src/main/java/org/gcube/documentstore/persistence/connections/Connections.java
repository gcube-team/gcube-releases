package org.gcube.documentstore.persistence.connections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Connections {
	
	public static Map<Nodes, Connection> connectionsMap = Collections.synchronizedMap(new HashMap<Nodes, Connection>());
}
