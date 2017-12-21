package org.gcube.data.trees.io;

import static junit.framework.Assert.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.io.XMLBindings.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.gcube.data.trees.Constants;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.junit.Test;
import org.w3c.dom.Element;


public class XMLReaderTest {
	
	static String tree_ns_decl = "xmlns:t='"+Constants.TREE_NS+"'";
	static String sample_ns =  "http://acme.org";
	static String sample_ns_decl =  "xmlns:s='"+sample_ns+"'";
	static String xsi_ns_decl="xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			
	@Test
	public void readEmpty() {
		
		Tree t = readerFor("<any/>").read();
		Tree expected = t();
		assertEquals(expected,t);
					
	}
	
	@Test
	public void readId() {
		
		Tree t = readerFor("<any "+tree_ns_decl+" t:id='1'/>").read();
		Tree expected = t("1");
		assertEquals(expected,t);
					
	}
	
	@Test
	public void readSourceId() {
		
		Tree t = readerFor("<any "+tree_ns_decl+" t:source='1'/>").read();
		
		Tree expected = t();
		expected.setSourceId("1");
		
		assertEquals(expected,t);
					
	}
	
	@Test
	public void readAttributes() {
		
		Tree t = readerFor("<any "+tree_ns_decl+" a1='1' "+sample_ns_decl+" s:a2='2'/>").read();
		
		Tree expected = attr(t(),
					a("a1","1"),a(q(sample_ns,"a2"),"2"));
		
		assertEquals(expected,t);
		
		//prefixes are preserved (prefixes are not checked in equality tests for qnames)
		for (QName a : t.attributes().keySet())
			if (a.getLocalPart().equals("a2"))
				assertEquals("s",a.getPrefix());
					
	}
	
	@Test(expected=RuntimeException.class)
	public void readLeafRootError() {
		
		readerFor("<any>text</any>").read();
		
	}
	
	@Test(expected=RuntimeException.class)
	public void readMixedContextError() {
		
		readerFor("<any>text<some/></any>").read();
		
	}
	@Test
	public void readChildren() {
		
		Tree t = readerFor("<any> <one>1</one> <s:two "+sample_ns_decl+"><three/></s:two></any>").read();
		Tree expected = t(e("one",1),e(q("s",sample_ns,"two"),n(e("three",n()))));
		assertEquals(expected,t);
		System.out.println(t);
		
		//prefixes are preserved (prefixes are not checked in equality tests for qnames)
		for (QName l : t.labels())
			if (l.getLocalPart().equals("two"))
				assertEquals("s",l.getPrefix());
	}
	
	@Test
	public void readNilLeaf() {
		
		Tree t = readerFor("<any><nil "+xsi_ns_decl+" xsi:nil='true'/></any>").read();
		Tree expected = t(e("nil",l(null)));
		assertEquals(expected,t);
	}
	
	@Test
	public void readInnerNode() {
		
		Node t = readerFor("<any/>").readNode();
		Node expected = n();
		assertEquals(expected,t);
	}
	
	@Test
	public void readLeaf() {
		
		Node t = readerFor("<any>foo</any>").readNode();
		Node expected = l("foo");
		assertEquals(expected,t);
	}
	
	@Test
	@SuppressWarnings("all")
	public void readThroughOldAPITest() throws Exception {
		
		Tree t = t();
		t.setSourceId("1");
		
		String xml = XMLBindings.toString(t);
		
		Tree expected = Bindings.fromReader(new StringReader(xml));
		
		assertEquals(expected,t);
	}
	
	static XMLReader readerFor(String xml) {
		return new XMLReader(new StreamSource(new StringReader(xml)));
	}
	
	@Test
	public void parseValentinaTree() throws Exception {
		
		InputStream stream = new FileInputStream("src/test/resources/tree.xml");
		XMLReader reader = new XMLReader(new StreamSource(stream));
		Tree tree = reader.read();
		Element e = toElement(tree);
		
		Tree roundtripped = fromElement(e);
		assertEquals(tree, roundtripped);
	}
	
}
