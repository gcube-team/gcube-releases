package org.gcube.execution.rr.bridge.scope;
import org.gcube.common.scope.impl.ScopeBean;


public class ICScopeHelper implements ScopeHelper {

	/* (non-Javadoc)
	 * @see org.gcube.execution.rr.bridge.ScopeHelper#getVOScope(java.lang.String)
	 */
	@Override
	public String getVOScope(String scope){
		ScopeBean bean = new ScopeBean(scope);
		
		if(bean.is(ScopeBean.Type.VO))
			return scope;
		else if(bean.is(ScopeBean.Type.VRE))
			return bean.enclosingScope().toString();
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.execution.rr.bridge.ScopeHelper#isInfraScope(java.lang.String)
	 */
	@Override
	public Boolean isInfraScope(String scope){
		ScopeBean bean = new ScopeBean(scope);
		
		return bean.is(ScopeBean.Type.INFRASTRUCTURE);
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.execution.rr.bridge.ScopeHelper#isVOScope(java.lang.String)
	 */
	@Override
	public Boolean isVOScope(String scope){
		ScopeBean bean = new ScopeBean(scope);
		
		return bean.is(ScopeBean.Type.VO);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.execution.rr.bridge.ScopeHelper#isVREScope(java.lang.String)
	 */
	@Override
	public Boolean isVREScope(String scope){
		ScopeBean bean = new ScopeBean(scope);
		
		return bean.is(ScopeBean.Type.VRE);
	}
	
	@Override
	public String getEnclosingScope(String scope){
		ScopeBean bean = new ScopeBean(scope);
		
		return bean.enclosingScope().toString();
	}

}
