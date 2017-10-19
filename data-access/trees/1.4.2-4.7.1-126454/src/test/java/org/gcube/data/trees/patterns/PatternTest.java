/**
 * 
 */
package org.gcube.data.trees.patterns;

import static junit.framework.Assert.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.patterns.Patterns.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;

import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.junit.Test;

public class PatternTest{
	
	@Test
	public void basics() throws Exception {
		
		Tree tree = t(e("a",3));
		
		//anything goes
		Pattern p = any(); 
		Tree clone = new Tree(tree);
		p.prune(clone);
		
		roundtrip(p);
		assertTrue(p.matches(tree));
		assertEquals(tree,clone);
		
		//any tree goes
		p = tree();
		clone = new Tree(tree);
		p.prune(clone);
		roundtrip(p);
		assertTrue(p.matches(tree));
		assertEquals(tree,clone);
		
		//cut anything below a matching node
		p=cut(tree(one("a",num())));
		p.prune(tree);
		roundtrip(p,true);
		p.matches(tree);
		assertEquals(t(),tree);
		
	}
	
	@Test(expected=Exception.class)
	public void onlyOne() throws Exception {
		
		Tree tree = t(
						e("a",n(
								e("b",1))),
						e("c",2),
						e("c",3));
		
		
		//one
		Pattern p = tree(one("a",any()));
		Tree clone = new Tree(tree);
		p.prune(clone);
		
		roundtrip(p);
		assertTrue(p.matches(tree));
		assertEquals(t(e("a",n(e("b",1)))),clone);
		
		//but not the other
		p = tree(one("c",num())); 
		assertFalse(p.matches(tree));
		p.prune(tree);
		
	}
	
	@Test(expected=Exception.class)
	public void zeroOrOne() throws Exception {
		
		Tree tree = t(
				e("a",n(
						e("b",1))),
				e("c",2),
				e("c",3));
		
		//one
		Pattern p = tree(opt("a",any()));
		roundtrip(p);
		
		Tree clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(e("a",n(e("b",1)))),clone);
		
		//zero
		p = tree(opt("b",any()));
		clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(),clone);
		
		//too many
		p = tree(opt("c",any()));
		
		assertFalse(p.matches(tree));
		p.prune(tree);
	}
	
	@Test(expected=Exception.class)
	public void oneOrMore() throws Exception {
		
		Tree tree = t(
				e("a",n(
						e("b",1))),
				e("c",2),
				e("c",3),
				e("c","four"));
		
		//one
		Pattern p = tree(atleast("a",any()));
		roundtrip(p);
		
		Tree clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(e("a",n(e("b",1)))),clone);
		
		//many
		p = tree(atleast("c",num()));
		clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(e("c",2),e("c",3)),clone);
		
		//zero
		p = tree(atleast("b",any()));
		assertFalse(p.matches(tree));
		p.prune(tree);
	}
	
	
	
	@Test
	public void zeroOrMore() throws Exception {
		
		Tree tree = t(
				e("a",n(
						e("b",1))),
				e("c",2),
				e("c",3),
				e("c","four"));
		
		//zero
		Pattern p = tree(many("b",any()));
		roundtrip(p);
		
		Tree clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(),clone);
		
		//many
		p = tree(many("c",num()));
		clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(e("c",2),e("c",3)),clone);
	}
	
	@Test(expected=Exception.class)
	public void zeroOrAll() throws Exception {
		
		Tree tree = t(
				e("a",1),
				e("a",2),
				e("c",3),
				e("c",4),
				e("c","five"));
		
		//zero
		Pattern p = tree(only("b",any()));
		roundtrip(p);
		
		Tree clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(),clone);
		
		//many
		p = tree(only("a",num()));
		
		clone = new Tree(tree);
		p.prune(clone);
		
		assertTrue(p.matches(tree));
		assertEquals(t(e("a",1),e("a",2)),clone);
		
		//only
		p = tree(only("c",num()));
		p.prune(clone);
		
		assertFalse(p.matches(tree));
		p.prune(tree);
	}
	
	@Test
	public void greedy() throws Exception {
		
		Tree tree = t(
				e("a",1),
				e("a",2),
				e("a","three"));
		
		Pattern p = tree(atleast("a",num()),tail());
		Tree clone = new Tree(tree);
		p.prune(clone);
		assertEquals(tree,clone);
	}
	
	@Test
	public void tails() throws Exception {
		
		Tree tree = t(
				e("b",n(
						e("x",1),
						e("x",2),
						e("y",3))),
				e("c",3),
				e("c",4),
				e("c","five"));
		
		//zero
		Pattern p = tree(atleast("c",num()),tail());
		Tree clone = new Tree(tree);
		p.prune(clone);
		roundtrip(p);
		assertTrue(p.matches(tree));
		assertEquals(tree,clone);
		
		p = tree(atleast("b",tree(
				atleast("x",num(more(1))),tail()))
				,tail());
		clone = new Tree(tree);
		p.prune(clone);
		assertTrue(p.matches(tree));
		assertEquals(tree, clone);
		
		
	}
	
	@Test 
	public void uris() throws Exception {
			
		Tree tree = t(e("a",new URI("http://acme.org")));
		Pattern p = tree(one("a",uri(matches(".*acme\\.org.*"))));
		roundtrip(p);
		assertTrue(p.matches(tree));
			
	}
	
	@Test
	public void resolve() throws Exception {
		
		Tree tree = t("1",e("otheredge",n()),e("edge"+1,n("11",e("test","test"))));
		Tree clone = t(tree);
		Pattern p = hasPath("1","11");
		p.prune(clone);
		assertEquals(t("1",e("edge"+1,n("11",e("test","test")))),clone);
		
		tree = t("1",e("val",l("val",10)));;
		p = hasPath("1","val");
		clone = t(tree);
		p.prune(clone);
		assertEquals(tree,clone);
	}
	
	@Test 
	public void serialise() throws Exception {
			
		Pattern p = tree(one("a",uri(matches(".*acme\\.org.*"))));
		
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(s);
		os.writeObject(p);
		os.close();
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(s.toByteArray()));
		Pattern p2 = (Pattern) is.readObject();
		is.close();
		assertEquals(p,p2);
			
	}
	
	@Test
	public void wikiexample() throws Exception {
	
		Date d = new Date();
		Tree tree = t(
				       e("a",-1),
				       e("a",1),
				       e("a",2),
				       e("b","..."),
				       e("b",n(
				    		   e("b1","..."))),
				       e("c",n(
				    		  e("c1",d),
				    		  e("c2","..."))),
				       e("d","..."));
		
		Pattern p = tree(
	               many("a",num(more(0))),
	               many("b",tree()),
				   one("c", tree(
						   	  one("c1",date()))));
		
		assertTrue(p.matches(tree));
	    
	    Tree pruned = t(
			       e("a",1),
			       e("a",2),
			       e("b",n(
			    		   e("b1","..."))),
			       e("c",n(
			    		  e("c1",d))));
	    
	    p.prune(tree);
	    
	    assertEquals(pruned,tree);
	    
		p = id("12345",tree(
			   one("a",any()),
               one("b",text(either(is("abc"),is("efg")))),
               atleast("c",bool(is(true))),
               opt("d",tree()),
               many("e",date(futureDate())),
               many("f", uri(matches("^http.*"))),
               one("g", num(all(less(5),more(10)))),
               one("h", text()),
               one("j",text(not(matches("somestring")))),
               one("k",id("12345",tree())),
               only("l", uri())
               ));

		
		Tree tree2 = t(
		         e("a",n(
		             e("b",-1),
		             e("c","..."),
		             e("d","..."),
		             e("e","..."))),
		           e("a",n(
		             e("b","notanumber"),
		             e("c","..."),
		             e("d","..."),
		             e("e","...")))
		    );
		
		p = tree(many("a",
				   tree(
						one("b",num()),tail())));
		
		p.prune(tree2);
		assertEquals(t(
		         e("a",n(
			             e("b",-1),
			             e("c","..."),
			             e("d","..."),
			             e("e","...")))), tree2);
		
		p  = tree(
				many("a",num(all(less(5),more(10)))),
				one(q("http://acme.org","b"),bool(is(true))),
				atleast("c",date(pastDate())));
		roundtrip(p, true);
	}
	
	@Test
	public void ids() throws Exception {
		
		Tree tree = t("1",e("a",n("2",e("b",3))));
		

	            		   
		Pattern idp = id("1",tree(one("a",any())));
		assertTrue(idp.matches(tree));
		roundtrip(idp,true);
		
		idp = id(text(not(is("wrong"))),tree(one("a",any())));
		assertTrue(idp.matches(tree));
		
		idp = id(text(),tree(one("a",any())));
		assertTrue(idp.matches(tree));
		
		idp = id(num(less(2)),tree(one("a",any())));
		assertTrue(idp.matches(tree));
		
		idp=id("1",tree(atleast(any,id("2",tree()))));
		assertTrue(idp.matches(tree));
		
		idp = hasPath("1","2");
		assertTrue(idp.matches(tree));
		
		roundtrip(idp);

	}

	@Test
	public void allConstraint() throws Exception {
		
		Tree t = t(e("a",3),e("b",4));
		
		Pattern p = tree(one("a",num(all(more(2),less(4)))));
		roundtrip(p);
		
		p.prune(t);
		assertEquals(t(e("a",3)),t);
		
		//more than 2 constraints with nesting
		p = tree(one("a",num(all(more(1),all(more(2),less(4))))));
		roundtrip(p);
		
		p.prune(t);
		assertEquals(t(e("a",3)),t);
		
		//invalid pattern
		try {
			p = tree(one("a",num(all(more(3),less(4)))));
			p.prune(t);
			fail();
		}
		catch(Exception e) {}
	}
	
	@Test
	public void eitherConstraint() throws Exception {
		
		Tree t = t(e("a",3),e("b",4));
		
		Pattern p = tree(one("a",num(either(more(2),less(3)))));
		roundtrip(p);
		
		p.prune(t);
		assertEquals(t(e("a",3)),t);
		
		//more than 2 constraints with nesting
		p = tree(one("a",num(either(more(5),either(more(6),less(4))))));
		roundtrip(p);
		
		p.prune(t);
		assertEquals(t(e("a",3)),t);
		
		//invalid pattern
		try {
			p = tree(one("a",num(either(more(5),less(2)))));
			p.prune(t);
			fail();
		}
		catch(Exception e) {}
	}
	
	@Test
	public void conditions() throws Exception {
		
		Tree t = t("1",
					e("a",n("2",e("b",3))),
					e("b",n("2",e("b",3)))
				);
		
		Pattern idp = tree(cond(one("a",any())),tail());
		
		idp.prune(t);
		//System.out.println(tree);
		assertEquals(t("1",e("b",n("2",e("b",3)))),t);
		
	}
	
	@Test
	public void federico() throws Exception {
		
		Tree tree = t(
				e("metadata",n(
						e("schema","dc"),
						e("lang","eng"),
						e("other1",3),
						e("other2",4))),
				e("metadata",n(
						e("schema","dc"),
						e("lang","it"),
						e("other1",3),
						e("other2",4))),
				e("other","5")
			);
		
		StringWriter w = new StringWriter();
		XMLBindings.toStream(tree, w);
		System.out.println(w.toString());
		
		Pattern idp = tree(
								atleast("metadata",
										tree(
											one("schema",text(is("dc"))),
											one("lang",text(is("it"))),
											tail()
											)));
		
		roundtrip(idp);
		
		idp.prune(tree);
		
		Tree expected = t(
				e("metadata",n(
						e("schema","dc"),
						e("lang","it"),
						e("other1",3),
						e("other2",4))));
		
		assertEquals(expected, tree);
		
	}
	
	@Test
	public void lucio() throws Exception {
		
		Date d = new Date();
		Tree tree = t(
				e("metadata",n(
						e("schema","dc"),
						e("lang","eng"),
						e("lastUpdate",d),
						e("other2",4))),
				e("metadata",n(
						e("schema","dc"),
						e("lang","it"),
						e("lastUpdate",d),
						e("other2",4))),
				e("other","5")
			);
		
		Pattern idp = tree(
								atleast("metadata",
										tree(
											cond(one("schema",text(is("dc")))),
											cond(one("lang",text(is("it")))),
											one("lastUpdate",date())
											)));
		
		roundtrip(idp);
		
		idp.prune(tree);
		
		Tree expected = t(e("metadata",n(e("lastUpdate",d))));
		
		assertEquals(expected, tree);
		
	}

	public static void roundtrip(Pattern p, boolean ... print) throws Exception {
		StringWriter w = new StringWriter();
		getMarshaller().marshal(p, w);
		if (print.length>0 && print[0]==true) 
			System.out.println(w.toString());
		Pattern p2 = (Pattern) getUnMarshaller().unmarshal(new StringReader(w.toString()));
		if (print.length>0 && print[0]==true) 
			System.out.println(p2);
		assertEquals(p,p2);
	}
}
