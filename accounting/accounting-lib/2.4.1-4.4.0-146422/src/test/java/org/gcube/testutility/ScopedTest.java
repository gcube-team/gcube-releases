/**
 * 
 */
package org.gcube.testutility;

//import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.junit.Before;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ScopedTest {

	@Before
	public void before() throws Exception{
		SecurityTokenProvider.instance.reset();
		
		SecurityTokenProvider.instance.set("");
	}
	
}
