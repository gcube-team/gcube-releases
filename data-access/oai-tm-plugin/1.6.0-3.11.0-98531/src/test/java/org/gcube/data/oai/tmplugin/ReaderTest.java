//package org.gcube.data.oai.tmplugin;
//
//import static java.util.Arrays.*;
//import static org.gcube.data.oai.tmplugin.TestUtils.*;
//import static org.gcube.data.streams.dsl.Streams.*;
//import static org.gcube.data.trees.data.Nodes.*;
//import static org.gcube.data.trees.patterns.Patterns.*;
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import java.util.List;
//
//import org.gcube.data.oai.tmplugin.repository.Repository;
//import org.gcube.data.oai.tmplugin.repository.Set;
//import org.gcube.data.streams.Stream;
//import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
//import org.gcube.data.trees.data.Tree;
//import org.gcube.data.trees.patterns.Pattern;
//import org.junit.Test;
//
//public class ReaderTest {
//
//
//	@Test
//	public void readerReadsInvididualTreesCorrectly() throws Exception {
//
//		List<Set> sets = asList(set1,set2);
//		Tree tree = t(e("a",3),e("b",5));
//		//		
//		//		Tree tree = new Tree(Utils.idEncoder("55"));
//		//		
//		//		System.out.println(tree.id());
//		//		tree.add(e("a",3));
//		//		tree.add(e("b",5));
//
//		Repository repo = newMock();
//		when(repo.get("test", sets)).thenReturn(tree);
//
//		OAISource source = mock(OAISource.class);
//		when(source.id()).thenReturn("mocksource");
//
//
//		OAIReader sut = new OAIReader(source,repo,sets);
//
//		Pattern pattern = tree(one("b",num()));
//
//		Tree result = sut.get("test",pattern);
//
//		Tree expected = t(e("b",5));
//
//		//		System.out.println(result);
//		//		System.out.println(expected);
//
//		assertNotNull(result);
//		assertEquals(expected, tree);
//
//	}
//
//	@Test
//	public void readerReadsManyTreesCorrectly() throws InvalidTreeException {
//
//		List<Set> sets = asList(set1,set2);
//		Tree tree1 = t(e("a",3),e("b",5));
//		Tree tree2 = t(e("a",3),e("b","five"));
//
//		Stream<Tree> stream = convert(asList(tree1,tree2));
//
//		Repository repo = newMock();
//		when(repo.getAllIn(sets)).thenReturn(stream);
//
//		OAISource source = mock(OAISource.class);
//		when(source.id()).thenReturn("mocksource");
//
//
//		OAIReader sut = new OAIReader(source,repo,sets);
//
//
//		Pattern pattern = tree(one("b",num()));
//
//		Stream<Tree> result = null;
//		try {
//			result = sut.get(pattern);
//			assertNotNull(result);
//			if (result!=null){
//				assertTrue(result.hasNext());
//				result.next();
//			}
//			//			assertFalse(result.hasNext());
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}
//}
