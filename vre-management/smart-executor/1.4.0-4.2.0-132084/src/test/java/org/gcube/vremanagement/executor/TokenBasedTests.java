/**
 * 
 */
package org.gcube.vremanagement.executor;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Before;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class TokenBasedTests {

	@Before
	public void before(){
		SecurityTokenProvider.instance.set("7c66c94c-7f6e-49cd-9a34-909cd3832f3e-98187548");
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
	}
	
}
