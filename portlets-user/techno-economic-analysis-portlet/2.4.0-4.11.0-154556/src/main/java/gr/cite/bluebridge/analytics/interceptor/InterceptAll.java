package gr.cite.bluebridge.analytics.interceptor;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.web.portlet.handler.HandlerInterceptorAdapter;

public class InterceptAll extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandleResource(ResourceRequest request, ResourceResponse response, Object handler) throws Exception {		
		return super.preHandleResource(request, response, handler);
	}

	@Override
	public void afterResourceCompletion(ResourceRequest request, ResourceResponse response, Object handler, Exception ex) throws Exception {
		super.afterResourceCompletion(request, response, handler, ex);
	}
}
