///**
// * 
// */
//package org.gcube.data.oai.tmplugin;
//
//import static java.util.Arrays.asList;
//import static org.gcube.data.oai.tmplugin.TestUtils.binderWith;
//import static org.gcube.data.oai.tmplugin.TestUtils.newMock;
//import static org.gcube.data.oai.tmplugin.TestUtils.repourl;
//import static org.gcube.data.oai.tmplugin.TestUtils.set1;
//import static org.gcube.data.oai.tmplugin.TestUtils.set2;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Matchers.anyListOf;
//import static org.mockito.Mockito.when;
//
//import java.util.List;
//
//import org.gcube.data.oai.tmplugin.repository.Repository;
//import org.gcube.data.oai.tmplugin.requests.RequestBinder;
//import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
//import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
//import org.junit.Test;
//
//
///**
// * @author Fabio Simeoni
// *
// */
//public class BinderTest {
//
//	static RequestBinder binder = new RequestBinder();
//
//	@Test
//	public void binderWrapsSetsCorrectly() throws Exception {
//
//		Repository repo = newMock();
//
//		when(repo.getSetsWith(anyListOf(String.class))).thenReturn(asList(set1,set2));
//
//		OAIBinder sut = binderWith(repo);
//
//		WrapSetsRequest request = new WrapSetsRequest(repourl);
//
//		List<? extends OAISource> sources = sut.bind(binder.bind(request));
//
//		assertNotNull(sources);
//
//		//collections are created for each available set
//		assertEquals(2,sources.size());
//
//		//verify the first one
//		OAISource source = sources.get(0);
//
//		assertEquals(set1.id(),source.id());
//		assertEquals(set1.name(),source.name());
//		assertEquals(set1.description(),source.description());
//
//		assertNotNull(source.reader());
//		assertNotNull(source.lifecycle());
//
//	}
//
//
//	@Test
//	public void binderWrapsRepositoryCorrectly() throws Exception {
//
//		Repository mockRepository = newMock();
//
//		OAIBinder sut = binderWith(mockRepository);
//
//		WrapRepositoryRequest request = new WrapRepositoryRequest("id",repourl);
//		request.setName("foo");
//		request.setDescription("foo");
////		request.addSets("set1id","set2id");
//		request.setContentXPath("content");
//		request.addAlternativesXPath("alternative");
//		request.setTitleXPath("title");
//
//		List<OAISource> sources = sut.wrapRepository(request);
//
//		assertNotNull(sources);
//
//		//only one source regardless of sets
//		assertEquals(1,sources.size());
//
//		//verify the first one
//		OAISource source = sources.get(0);
//
//		System.out.println(source);
//
//		assertEquals(request.getId(),source.id());
//		assertEquals("foo",source.name());
//		assertEquals("foo",source.description());
//
//	}
//
//}
