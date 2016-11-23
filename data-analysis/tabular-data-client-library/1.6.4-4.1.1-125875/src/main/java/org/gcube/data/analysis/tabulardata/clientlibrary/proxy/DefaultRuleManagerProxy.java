package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.calls.jaxws.JAXWSUtils.Empty;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.RuleManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.MapObject;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRuleManagerProxy implements RuleManagerProxy {

	ProxyDelegate<RuleManager> delegate;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultRuleManagerProxy.class); 
	
	public DefaultRuleManagerProxy(ProxyDelegate<RuleManager> config) {
		this.delegate = config;
	}

	
	@Override
	public long saveRule(final String name, final String description,
			final Expression rule, final RuleType type) {
		Call<RuleManager, Long> call = new Call<RuleManager, Long>() {

			@Override
			public Long call(RuleManager endpoint) throws Exception {
				return endpoint.saveRule(name, description, rule, type);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error saving rule",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<RuleDescription> getRules() {
		Call<RuleManager, List<RuleDescription>> call = new Call<RuleManager,  List<RuleDescription>>() {

			@Override
			public  List<RuleDescription> call(RuleManager endpoint) throws Exception {
				return endpoint.getRules();
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error getting rules",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<RuleDescription> getRulesByScope(final RuleScope scope) {
		Call<RuleManager, List<RuleDescription>> call = new Call<RuleManager,  List<RuleDescription>>() {

			@Override
			public  List<RuleDescription> call(RuleManager endpoint) throws Exception {
				return endpoint.getRulesByScope(scope);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error getting rules", e);
			throw again(e).asServiceException();
		}
	}


	@Override
	public void remove(final Long id) throws NoSuchRuleException {
		Call<RuleManager, Empty> call = new Call<RuleManager, Empty>() {

			@Override
			public  Empty call(RuleManager endpoint) throws Exception {
				endpoint.remove(id);
				return new Empty();
			}
		};
		try{
			delegate.make(call);
		}catch (NoSuchRuleException nsr) {
			throw nsr;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error removing rule", e);
			throw again(e).asServiceException();
		}
		
	}


	@Override
	public void updateColumnRule(final RuleDescription descriptor) throws NoSuchRuleException{
		Call<RuleManager, Empty> call = new Call<RuleManager, Empty>() {

			@Override
			public  Empty call(RuleManager endpoint) throws Exception {
				endpoint.updateColumnRule(descriptor);
				return new Empty();
			}
		};
		try{
			delegate.make(call);
		}catch (NoSuchRuleException nsr) {
			throw nsr;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error updating rules");
			throw again(e).asServiceException();
		}
		
	}


	@Override
	public List<RuleDescription> getApplicableBaseColumnRules(
			final Class<? extends DataType> dataTypeClass) {
		Call<RuleManager, List<RuleDescription>> call = new Call<RuleManager,  List<RuleDescription>>() {

			@Override
			public  List<RuleDescription> call(RuleManager endpoint) throws Exception {
				return endpoint.getApplicableBaseColumnRules(dataTypeClass);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error getting applicable rules");
			throw again(e).asServiceException();
		}
	}


	@Override
	public TaskInfo applyColumnRule(final Long tabularResourceId, final String columnId,
			final List<Long> ruleIds) throws NoSuchRuleException,
			NoSuchTabularResourceException {
		Call<RuleManager, TaskInfo> call = new Call<RuleManager,  TaskInfo>() {

			@Override
			public  TaskInfo call(RuleManager endpoint) throws Exception {
				return endpoint.applyColumnRule(tabularResourceId, columnId, ruleIds);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchRuleException nsr) {
			throw nsr;
		}catch (NoSuchTabularResourceException nstr) {
			throw nstr;
		}catch (Exception e) {
			logger.error("error applying rules");
			throw again(e).asServiceException();
		}
	}


	@Override
	public AppliedRulesResponse getAppliedRulesByTabularResourceId(final Long id)
			throws NoSuchTabularResourceException {
		Call<RuleManager, AppliedRulesResponse> call = new Call<RuleManager,  AppliedRulesResponse>() {

			@Override
			public  AppliedRulesResponse call(RuleManager endpoint) throws Exception {
				return endpoint.getAppliedRulesByTabularResourceId(id);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException nstr) {
			throw nstr;
		}catch (Exception e) {
			logger.error("error applying rules");
			throw again(e).asServiceException();
		}
	}

	@Override
	public void detachColumnRules(final Long tabularResourceId, final String columnId,
			final List<Long> ruleIds) throws NoSuchTabularResourceException {
		Call<RuleManager, Empty> call = new Call<RuleManager,  Empty>() {

			@Override
			public  Empty call(RuleManager endpoint) throws Exception {
				endpoint.detachColumnRules(tabularResourceId, columnId, ruleIds);
				return new Empty();
			}
		};
		try{
			delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException nstr) {
			throw nstr;
		}catch (Exception e) {
			logger.error("error detaching rules");
			throw again(e).asServiceException();
		}
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.WRAPPED)
	public RuleDescription share(final Long entityId, final AuthorizationToken... authTokens)
			throws NoSuchRuleException, InternalSecurityException {
		Call<RuleManager, RuleDescription> call = new Call<RuleManager, RuleDescription>() {

			@Override
			public RuleDescription call(RuleManager endpoint) throws Exception {
				return endpoint.share(entityId, authTokens);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchRuleException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}


	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.WRAPPED)
	public RuleDescription unshare(final Long entityId, final AuthorizationToken... authTokens)
			throws NoSuchRuleException, InternalSecurityException {
		Call<RuleManager, RuleDescription> call = new Call<RuleManager, RuleDescription>() {

			@Override
			public RuleDescription call(RuleManager endpoint) throws Exception {
				return endpoint.unshare(entityId, authTokens);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchRuleException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}


	@Override
	public void detachTableRules(final Long tabularResourceId, final List<Long> ruleIds) throws NoSuchTabularResourceException {
		Call<RuleManager, Empty> call = new Call<RuleManager,  Empty>() {

			@Override
			public  Empty call(RuleManager endpoint) throws Exception {
				endpoint.detachTableRules(tabularResourceId, ruleIds);
				return new Empty();
			}
		};
		try{
			delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTabularResourceException nstr) {
			throw nstr;
		}catch (Exception e) {
			logger.error("error detaching rules");
			throw again(e).asServiceException();
		}
	}


	@Override
	public TaskInfo applyTableRule(final Long tabularResourceId, 
			final Map<String, String> mappingPlacelhoderIdColumnId, final Long ruleId)
			throws NoSuchRuleException, NoSuchTabularResourceException {
		Call<RuleManager, TaskInfo> call = new Call<RuleManager,  TaskInfo>() {

			@Override
			public  TaskInfo call(RuleManager endpoint) throws Exception {
				return endpoint.applyTableRule(tabularResourceId,new MapObject<String, String>(mappingPlacelhoderIdColumnId), ruleId);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchRuleException nsr) {
			throw nsr;
		}catch (NoSuchTabularResourceException nstr) {
			throw nstr;
		}catch (Exception e) {
			logger.error("error applying rules");
			throw again(e).asServiceException();
		}
	}
}
