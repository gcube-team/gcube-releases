package org.gcube.common.scope;

import static org.junit.Assert.*;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.junit.Test;

public class BeanTest {

	
	@Test
	public void beansAreParsedCorrectly() {
		
		String infra ="/infra";
		ScopeBean infraBean = new ScopeBean(infra);
		assertEquals("infra",infraBean.name());
		assertTrue(infraBean.is(Type.INFRASTRUCTURE));
		assertNull(infraBean.enclosingScope());
		assertEquals(infra,infraBean.toString());
		assertEquals(infraBean,new ScopeBean(infra));
		
		String vo =infra+"/vo";
		ScopeBean vobean = new ScopeBean(vo);
		assertEquals("vo",vobean.name());
		assertTrue(vobean.is(Type.VO));
		assertEquals(infraBean,vobean.enclosingScope());
		assertEquals(vo,vobean.toString());
		
		String vre = vo+"/vre";
		ScopeBean vrebean = new ScopeBean(vre);
		assertEquals("vre",vrebean.name());
		assertTrue(vrebean.is(Type.VRE));
		assertEquals(vobean,vrebean.enclosingScope());
		assertEquals(vre,vrebean.toString());
		
	}
}
