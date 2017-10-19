/**
 * 
 */
package org.gcube.data.trees.generators;

import static java.lang.Math.*;
import static junit.framework.Assert.*;
import static org.gcube.data.trees.generators.TemplateFactory.*;

import java.util.Iterator;

import org.gcube.data.trees.data.Tree;
import org.junit.Test;

public class GenerationTest {

	@Test
	public void formulae() {
		
		double a,b,c;
		
		a=5;b=3;c=1024;
		double result = pow(a,b)*c;
		System.out.println("result="+result);
		
		double derivedA = pow(result/c,1/b);
		System.out.println("derived a="+derivedA);
		assertEquals(a,derivedA,.1);
		
		assertEquals(log(result/c),log(pow(a,b)));
		assertTrue(log(result/c)-b*log(a)<.1);
		double derivedB = log(result/c)/log(a);
		System.out.println("derived b="+derivedB);
		assertEquals(b,derivedB,.1);
		
		double derivedC = result/pow(a,b);
		System.out.println("derived c="+derivedC);
		assertEquals(c,derivedC,.1);
		
		System.out.println(pow(derivedA,derivedB)*derivedC);
	}
	
	@Test
	public void one() {
		
		StructuralTemplate template = aTree().wide(3).deep(2).withValuesOf(1).withIds().inSource("mysource").build();
		
		Tree tree = template.generate();
		
		assertNotNull(tree.id());
		assertNotNull(tree.sourceId());
		
		assertEquals(tree.size(),(int)rint(pow(template.width,template.depth)*template.value));
	}
	
	@Test
	public void many() {
		
		StructuralTemplate template = aTree().withValuesOf(1).build();
		Iterator<Tree> docs = template.generate(100);
		int count=0;
		while (docs.hasNext()) {
			Tree doc = docs.next();
			assertEquals(doc.size(),(int)rint(pow(template.width,template.depth)*template.value));
			count++;
		}
		
		assertEquals(100,count);
		
	}
}
