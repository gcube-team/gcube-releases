/**
 * 
 */
package org.gcube.data.oai.tmplugin;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.gcube.data.oai.tmplugin.binders.OAIDCBinder;
import org.gcube.data.oai.tmplugin.repository.Repository;
import org.gcube.data.oai.tmplugin.repository.Set;
import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;

/**
 * @author Fabio Simeoni
 * 
 */
public class TestUtils {

	public static final String repourl = "http://repourl";
	public static final String reponame = "mockrepository";
	public static final String prefix = "prefix";

	public static final Set set1 = new Set("1", "one", "set one");
	public static final Set set2 = new Set("2", "two", "set two");

	// helper
	public static Repository newMock() {

		Repository mockRepo = mock(Repository.class);

		when(mockRepo.name()).thenReturn(reponame);
		when(mockRepo.url()).thenReturn(repourl);
		when(mockRepo.description()).thenReturn("a mock repository");

		return mockRepo;
	}

	// helper
	public static OAIBinder binderWith(Repository repo) {

		// a provider that serves mocks
		RepositoryProvider mockProvider = mock(RepositoryProvider.class);
		when(mockProvider.newRepository((WrapRepositoryRequest) any(Request.class))).thenReturn(repo);

		return new OAIBinder(mockProvider);
	}
	
	// helper
	public static OAIDCBinder OAIDCbinderWith(Repository repo) {

		// a provider that serves mocks
		RepositoryProvider mockProvider = mock(RepositoryProvider.class);
		when(mockProvider.newRepository((WrapRepositoryRequest) any(Request.class))).thenReturn(repo);

		return new OAIDCBinder(mockProvider);
	}
}
