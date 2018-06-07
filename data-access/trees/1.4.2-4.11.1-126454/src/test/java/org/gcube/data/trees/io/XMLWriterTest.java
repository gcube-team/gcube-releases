package org.gcube.data.trees.io;

import static junit.framework.Assert.*;
import static org.gcube.data.trees.data.Nodes.*;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.data.trees.Constants;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.junit.Test;


public class XMLWriterTest {
	
	static String tree_ns_decl = "xmlns:t='"+Constants.TREE_NS+"'";
	static String sample_ns =  "http://acme.org";
	static String sample_ns_2 =  "http://acme2.org";
	static String sample_ns_decl =  "xmlns:s='"+sample_ns+"'";
	static String xsi_ns_decl="xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
			
	@Test
	public void writeEmpty() {
		
		Tree t = t();
		assertEquals(t(),writeAndParse(t));
					
	}
	
	@Test
	public void writeId() {
		
		Tree t = t("1");
		assertEquals(t,writeAndParse(t));
					
	}
	
	@Test
	public void writeSourceId() {
		
		Tree t = t();
		t.setSourceId("1");
		
		assertEquals(t,writeAndParse(t));
					
	}
	
	@Test
	public void writeAttributes() {
		
		Tree t = attr(t(),
					a("a1","1"),a(q(sample_ns,"a2"),"2"));
		
		assertEquals(t,writeAndParse(t));
							
	}
	
	@Test
	public void writeChildren() {
		
		Tree t =  t(e("one",1),e(q("s",sample_ns,"two"),n(e("three",n()))));
		assertEquals(t,writeAndParse(t));
		
	}
	
	@Test
	public void writeCustomChildren() {
		
		QName myRoot = q("ns1",sample_ns,"myroot");
		WriteOptions options = new WriteOptions();
		options.setRootElement(myRoot);
		options.prefixes().put("ns2", sample_ns_2);
		Tree t =  t(e("one",1),e(q(sample_ns_2,"two"),n(e("three",n()))));
		assertEquals(t,writeAndParse(t,options));
		
	}
	
	@Test
	public void writeNilLeaf() {
		
		Tree t = t(e("nil",l(null)));
		assertEquals(t,writeAndParse(t));
	}
	
	@Test
	public void writeInnerNode() {
		
		Node n  = n();
		assertEquals(n,writeAndParse(n));
	}
	
	@Test
	public void writeLeaf() {
		
		Node n  = l("any");
		assertEquals(n,writeAndParse(n));
	}
	
	@Test
	@SuppressWarnings("all")
	public void writeThroughOldAPITest() throws Exception {
		
		Tree t = t();
		t.setSourceId("1");
		
		String xml = Bindings.toText(t);
		
		Tree expected = XMLBindings.fromString(xml);
		
		assertEquals(expected,t);
	}
	
	static XMLReader readerFor(String xml) {
		return new XMLReader(new StreamSource(new StringReader(xml)));
	}
	
	static Tree writeAndParse(Tree tree) {
		return writeAndParse(tree,new WriteOptions());
	}
	
	static Tree writeAndParse(Tree tree, WriteOptions options) {
		StringWriter w = new StringWriter();
		new XMLWriter(new StreamResult(w),options).write(tree);
		System.err.println(w.toString());
		return readerFor(w.toString()).read();
	}
	
	static Node writeAndParse(Node node) {
		StringWriter w = new StringWriter();
		new XMLWriter(new StreamResult(w),new WriteOptions()).writeNode(node);
		System.err.println(w.toString());
		return readerFor(w.toString()).readNode();
	}
	
}
