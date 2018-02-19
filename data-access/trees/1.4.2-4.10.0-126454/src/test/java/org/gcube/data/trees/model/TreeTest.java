/**
 * 
 */
package org.gcube.data.trees.model;

import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.io.XMLBindings.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Node.State;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.junit.Test;


/**
 * @author Fabio Simeoni
 * @author Federico De Faveri (defaveri@isti.cnr.it)
 *
 */
public class TreeTest {

	
	public static void roundDOMTrip(Tree r) throws Exception {
		Tree r2 = fromElement(toElement(r));
		assertEquals(r,r2);
	}
	
	public static void roundXMLTrip(Tree r) throws Exception {
		String xml = XMLBindings.toString(r);
		System.out.println(xml);
		assertEquals(r,fromStream(new StringReader(xml)));
	}
	
	@Test
	public void dates() throws Exception {
		
		Date date = new Date();
		Tree root = t(e("a",date));
		String dateval = root.child(L,"a").value();
		Date date2 = toDate(dateval);
		assertEquals(date,date2);
	}
	
	@Test 
	public void ancestors() {
		
		Tree root = t("1",
						e("a",1),
						e("b",
							n("2")),
						e("c",
							n("3",
									e("d",l("4",3)))));
		
		assertEquals(Arrays.asList(root.child("c"),root), 
				root.child(InnerNode.class,"c").child("d").ancestors());
		assertEquals(0, root.ancestors().size());
	}
	
	@Test(expected=IllegalStateException.class)
	public void empty() throws Exception {
		
		Tree root = t();

		assertNull(root.id());
		assertEquals(0,root.attributes().size());
		assertEquals(0,root.edges().size());
		assertEquals(0,root.labels().size());
		assertEquals(0,root.edges("a").size());
		
		roundDOMTrip(root);
		roundXMLTrip(root);
		
		assertNull(root.attribute("a")); //not exists

	}
	
	@Test
	public void id() throws Exception {
		
		Tree root = t("1",e("a",1),e("b",n("2")),e("c",n("3",e("d",l("4",3)))));
		
		assertEquals("1",root.id());
		assertEquals("2",root.child("b").id());
		assertEquals("3",root.child("c").id());
		assertEquals("4",((InnerNode) root.child("c")).child("d").id());
		
		roundDOMTrip(root);
		roundXMLTrip(root);
	}
	
	final static String NS="http://acme.org";
	final static String NS2="http://acme.org.two";
	
	@Test
	public void namespaces() throws Exception {
		
		Tree root = t(
						e(q(NS,"a"),3),
						e("a",true),
						e("a",new Date()),
						e("a","text"),
						e("a","<xml>text</xml>"),
						e("b",new Object()),
						e(q(NS2,"a"),n()));
		
		assertEquals(7,root.edges().size());
		assertEquals(4,root.edges("a").size());
		assertEquals(1,root.edges(NS,"a").size());
		assertEquals(1,root.edges(NS2,"a").size());
		
		roundDOMTrip(root);
		roundXMLTrip(root);
	}
	
	@Test
	public void attributes() throws Exception {
		
		Tree root = attr(t(),a("one",1),a(q(NS,"two"),2),a(q(NS,"three"),3));

		assertEquals(3,root.attributes().size());
		assertEquals("1",root.attribute(q("one")));
		assertEquals("2",root.attributes().get(q("pre",NS,"two")));
		assertEquals("3",root.attribute(q(NS,"three")));
		
		roundDOMTrip(root);
		roundXMLTrip(root);

	}

	@Test 
	public void escapedLeaf() throws Exception {
		
		Tree root = t(
				e("a","<b>toescape</b>"),
				e("b","nottoescape")
		);
		
		roundDOMTrip(root);
		roundXMLTrip(root);
	}
	
	@Test
	public void cloneroots() {
		
		Tree root = attr(t("1",
							e("a",l("2",5)),
							e("b",attr(
									n("3",e("c",4)),
								  a("foo",0))),
							e("c",5)),
					a("x",0)
				);
		
		Tree clone = t(root);
		
		assertEquals(root.child("a"), clone.child("a"));
		assertEquals(root.child("b"), clone.child("b"));
		
		clone.removeAttribute("x");
		
		assertTrue(root.hasAttribute("x"));
		
	}
	
	
	@Test
	public void full() throws Exception {
		
		Tree root = t(
						e("a","3"),
						e("a",true),
						e("a",new Date()),
						e("a","text"),
						e("a","<xml>text</xml>"),
						e("a",n()));
		
		assertEquals(1,root.labels().size());
		assertEquals(6,root.edges().size());
		List<Edge> aEdges = root.edges("a"); 
		assertEquals(6, aEdges.size());
		assertNotNull(aEdges.get(5).target());
		assertTrue(aEdges.get(5).target() instanceof InnerNode);
		assertEquals(0, ((InnerNode) aEdges.get(5).target()).edges().size());
		
		roundDOMTrip(root);
	}
	


	@Test
	public void remove() {
		
		Tree root = t(
				e(q(NS,"a"),"3"),
				e("a",true),
				e("a",new Date()),
				e("a","text"),
				e(q(NS2,"a"),n()));
		
		int n = root.edges().size();
		
		root.remove(Arrays.asList(root.edges().get(1)));
		
		assertEquals(n-1,root.edges().size());
		
		root.remove(root.edges());
		
		assertEquals(0,root.edges().size());
		
	}
	
	@Test
	public void add() {
		
		Tree root = t(
				e(q(NS,"a"),"3"),
				e("a",true),
				e("a",new Date()),
				e("a","text"),
				e("b",new Object()),
				e(q(NS2,"a"),n()));
		
		int n = root.edges().size();
	
		root.add(Arrays.asList(e("c",false)));
		
		assertEquals(n+1,root.edges().size());
		
	}
	
	
	@Test
	public void collectionID() throws Exception {
		
		Tree root = t("123",e("a",3));
		root.setSourceId("somecollid");
		roundDOMTrip(root);
	}
	
	@Test 
	public void fragmentIO() throws Exception {
		
		Tree root = t("1",e("a",n("2",e("b",l("3",0)))));
		Node n = root.child("a");
		StringWriter w = new StringWriter();
		nodeToStream(n,w);
		assertEquals(n,nodeFromStream(new StringReader(w.toString())));
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void descendant() throws Exception {
		
		Tree root = t("1",e("a",n("2",e("b",l("3",0)))));
		assertEquals("2", root.descendant("2").id());
		assertEquals("3", root.descendant("2","3").id());
		root.descendant("2","3","4");
	}
	
	@Test
	public void parent() throws Exception {
		Tree root = t("123",e("a",3));
		assertEquals(root, root.child("a").parent());
	}
	
	@Test
	public void encoding() throws Exception {
	
		Tree root = attr(t("1",
							e("a",
								n("2",
								  e("b",
									   n("$2"))
								)),
							e("a",
								n("a1",
									e("c",n(
											e("d","..."),
											e("d",attr(
													l("<xml>..</xml>"),
												  a("w",".."))))
									)
								)),
							e("b",
								attr(
									n("1:/2"),
								a("w","..."))
								)),
				   a("x","http://org.acme:8080"),a("y","<a>...</a>"));
		StringWriter w = new StringWriter();
		toStream(root, w);
		roundXMLTrip(root);
	}

	@Test
	public void noupdate() throws Exception {
		Tree root = t("1",
						e("a",
							n("2",
								e("b",
									l("3",0)))));
		
		root.setSourceId("12345");
		
		
		Tree clone = new Tree(root);
		assertEquals(null,root.delta(clone));	//no changes to clone...
		
	}
	
	@Test
	public void leafChange() throws Exception {
		
		Tree root = t("12345","1",
						e("a",
							n("2",
								e("b",l("3",0)),
								e("a",l("4",0)),
								e("c",l("5",0)))));
		
		Tree future = t(root);
		future.child(N,"a").remove("3");
		future.descendant(L,"2","4").value("1");
		future.descendant(N,"2").add(e("b",l(2)));
		
		Tree delta = root.delta(future); //changes to clone...
		
//		System.out.println(future);
//		System.out.println(delta);
		
		assertEquals(State.MODIFIED,delta.state());  //...changes to original
		assertEquals(State.MODIFIED,delta.child("a").state());
		assertEquals(State.DELETED,delta.descendant("2","3").state());
		assertEquals(State.MODIFIED,delta.descendant("2","4").state());
		assertEquals("1",delta.descendant(L,"2","4").value());
		assertEquals(State.NEW,delta.child(N,"a").children("b").get(1).state());
		assertEquals("2",delta.child(N,"a").children(L,"b").get(1).value());
		assertEquals(0,delta.children("c").size());
		
		roundXMLTrip(root);
	}
	
	@Test
	public void _null() throws Exception {
		
		
		Leaf l = l("3");
		l.value(null);
		
		assertNull(l.value()); //preserve null for clients but not internally
		
		Tree root = attr(
					t(e("a",l)),
					a("x",0));
					
		root.setAttribute("x",null);
		
		assertNull(root.attribute("x")); //ditto
		
		String xml = XMLBindings.toString(root);
		
		System.out.println(xml);
		Tree roundTripped = fromStream(new StringReader(xml));
		
		System.out.println(xml);
		
		assertEquals(t(e("a",l(null))),roundTripped);
		
	}
	
	@Test
	public void innerChange() throws Exception {
		
		Tree root = t("12345","1",
						e("a",
							attr(n("2",
								e("b",l("3",0)),
								e("a",l("4",0))),
							a("foo","0"))),
						e("b",l("5","modifyme")),
						e("c",l("6","removeme")));
		
		
		Tree future = t(root);
		InnerNode newNode = n(e("y",0));
		future.remove("2");
		future.child(L,"b").value("newvalue");
		future.remove("6");
		future.add(e("x",newNode));
		
		Tree delta = root.delta(future); //changes to clone...
		
		System.out.println("delta:\n"+delta);
		
		assertEquals(State.MODIFIED,delta.state());  //...changes to original
		assertEquals(State.DELETED,delta.child("a").state()); //node removal
		assertEquals(0,delta.child("a").attributes().size()); //attribute clearance
		assertEquals(0,delta.child(N,"a").edges().size()); //attribute clearance
		assertEquals(State.NEW,delta.child("x").state()); //node addition
		assertEquals(State.NEW,delta.child(N,"x").child("y").state()); //new propagation
		
		roundXMLTrip(root);
		
		//simulate network transfer
		roundXMLTrip(delta);
		
		//update in place
		root.update(delta);
		
		System.out.println("after update:\n"+root);
		
		assertEquals(future,root);
		
	}
	
	@Test
	public void changeattribute() throws Exception {
		
		Tree root = t("1",
				e("a",
					attr(n("2",
						e("b",
							l("3",0)),
						e("a",
							 l("4",0))),
					a("foo1","0"),a("foo2","0"),a("foo3","0"))));
		
		Tree future = t(root);
		
		future.child("a").removeAttribute("foo1");  //removed attribute
		future.child("a").setAttribute("foo2","1"); //changed attribute
		future.child("a").setAttribute("foo4","0"); //new attribute
		
		Tree delta = root.delta(future);
		
		System.out.println("delta:\n"+delta);
		
		assertEquals(State.MODIFIED,delta.state());
		assertEquals(3,delta.child("a").attributes().size());
		assertEquals("nil",delta.child("a").attribute("foo1"));
		assertEquals("1",delta.child("a").attribute("foo2"));
		assertEquals("0",delta.child("a").attribute("foo4"));
		
		try {
			delta.child("a").attribute("foo3");
			fail();
		}
		catch(IllegalStateException e) {
			System.out.println("caught expected "+e.getClass().getSimpleName());
		}

		//simulate network transfer
		roundXMLTrip(delta);
		
		//update in place
		root.update(delta);
		
		System.out.println("after update:\n"+root);
		
		assertEquals(future,root);
	}
	
	@Test
	public void deleteroot() throws Exception {
		
		Tree root = t("1",
				e("a",
					attr(n("2",
						e("b",
							l("3",0)),
						e("a",
							 l("4",0))),
					a("foo","0"))));
		
		System.out.println("before deletion:\n"+root);
		
		Tree future = t(root);
		
		future.delete();
		
		System.out.println("deleted:\n"+future);
		
		assertEquals(State.DELETED,future.state());
	
		//simulate network transfer
		roundXMLTrip(future);
		
		//update in place
		root.update(future);
		
		System.out.println("after update:\n"+root);
		
		assertEquals(future,root);
	}
	
	@Test(expected=IllegalStateException.class)
	public void badupdate() {

		Tree root = t(//no identifier here
				e("a",
					attr(n("2",
						e("b",
							l("3",0)),
						e("a",
							 l("4",0))),
					a("foo","0"))));
		
		Tree clone = t(root);
		root.delta(clone);
		
	}
	
	@Test
	public void regexpmatch() {
		
		Tree root = t("1",
				e("a",
					attr(n("2",
						e("b1",
							l("3",0)),
						e("b2",
							l("3",0)),
						e("a",
							 l("4",0))),
					a("foo","0"))));
		
		assertEquals(1, root.edges(".*").size());
		assertEquals(3, root.child(N,"a").edges(".*").size());
		assertEquals(2, root.child(N,"a").children("^b.*").size());
	}
	
	@Test
	public void find() {
		
		Node anode2 = n(
				e("b",l(0)),
				e("b",l(0)),
				e("b",n(e("c",l(0)))),
				e("a", l(0)));
		
		Tree root = t(e("a",anode2),e("a",0));
		
		assertEquals(Arrays.asList(root),root.find(new String[0]));
		assertEquals(Arrays.asList(anode2,l(0),l(0)),root.find("a"));
		assertEquals(Arrays.asList(l(0),l(0),n(e("c",l(0)))),root.find("a","b"));
		assertEquals(Arrays.asList(l(0)),root.find("a","b","c"));
	}
	
	@Test
	public void sizes() {
		
		int leafSize = 500;
		String value = RandomStringUtils.randomAscii(leafSize);
		assertEquals(leafSize,l(value).size());
		Node node = n(e("e",l(value)),e("e",l(value)));
		assertEquals(leafSize*2,node.size());
		Tree root = t(e("e",node),e("e",node),e("e",node),e("e",node));
		System.out.println(root.size());
		assertEquals(leafSize*8,root.size());
		assertEquals(3,root.size()/1024);

		
	}
	
	@Test
	public void examples() throws Exception {
		
		Tree t = t();System.out.println(t);roundXMLTrip(t);
		t = t("id");System.out.println(t);roundXMLTrip(t);
		t = t("source","id");System.out.println(t);roundXMLTrip(t);
		t = t("source","id", e("a",1));System.out.println(t);
		t = t("source","id1",e("a",l("id2","foo")));System.out.println(t);
		t = t("source","id1", e("a",n(e("b",1))));System.out.println(t);
		t = attr(t(),a("a1","3"),a("a2","foo"));System.out.println(t);
		t = t(e("a",attr(l(3),a("b1","..."),a("b2","..."))));System.out.println(t);
		t = t(e(q("http://acme.org","a"),1));System.out.println(t);
		t = attr(
				t(e("a",attr(
						l("..."),
						a(q("http://acme.org","b1"),"..."),a("b2","...")))),
				a("a1","3"),a("a2","foo"));System.out.println(t);
				
		InnerNode n = t;
		List<Node> bChildren = n.children("a.*");
		System.out.println(bChildren);
	}
}
