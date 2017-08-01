package org.gcube.data.trees.io;

import static org.gcube.data.trees.Constants.*;
import static org.gcube.data.trees.data.Nodes.*;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class WriteOptions {

	private boolean writeXMLDeclaration;
	public static final QName DEFAULT_ROOT = q("t",TREE_NS, "root");
	private QName rootElement = DEFAULT_ROOT;
	private Map<String,String> prefixes = new HashMap<String, String>();
	
	public boolean isWriteXMLDeclaration() {
		return writeXMLDeclaration;
	}
	public QName rootElement() {
		return rootElement;
	}
	
	public void setWriteXMLDeclaration(boolean writeXMLDeclaration) {
		this.writeXMLDeclaration = writeXMLDeclaration;
	}
	
	public void setRootElement(QName rootElement) {
		this.rootElement = rootElement;
	}
	
	public Map<String,String> prefixes() {
		return prefixes;
	}
}
