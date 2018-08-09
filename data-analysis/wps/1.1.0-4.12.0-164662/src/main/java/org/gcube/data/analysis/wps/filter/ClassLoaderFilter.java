package org.gcube.data.analysis.wps.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.gcube.data.analysis.wps.repository.GcubeAlgorithmRepository;

public class ClassLoaderFilter implements Filter{


		@Override
		public void init(FilterConfig filterConfig) throws ServletException {

		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response,
				FilterChain chain) throws IOException, ServletException {
			Thread.currentThread().setContextClassLoader(GcubeAlgorithmRepository.getUpdater().getLoader());
			chain.doFilter(request, response);
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	
}
