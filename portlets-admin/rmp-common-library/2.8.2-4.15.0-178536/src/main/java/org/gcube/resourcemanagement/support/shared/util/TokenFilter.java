package org.gcube.resourcemanagement.support.shared.util;


	import java.io.IOException;

	import javax.servlet.Filter;
	import javax.servlet.FilterChain;
	import javax.servlet.FilterConfig;
	import javax.servlet.ServletException;
	import javax.servlet.ServletRequest;
	import javax.servlet.ServletResponse;

	import org.gcube.common.authorization.library.provider.SecurityTokenProvider;

	public class TokenFilter  implements Filter {

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			SecurityTokenProvider.instance.reset();
			chain.doFilter(request, response);
			SecurityTokenProvider.instance.reset();
		}

		@Override
		public void destroy() {
			
		}

	}

