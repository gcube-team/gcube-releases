package gr.cite.repo.auth.webapp.inject.providers;

import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.config.Security;
import gr.cite.repo.auth.filters.CustomSecurityFilter;

import java.util.List;

import javax.servlet.Filter;

public class SecurityFilterWrapper {
		private final List<String> protectedUrls;
		private final Filter filter;

		public SecurityFilterWrapper(SamlSecurityConfiguration configuration) {
			Security securityConfiguration = configuration.getSecurity();

			String unauthorizedLocation = securityConfiguration
					.getUnauthorizedLocation();

			boolean includeTarget = securityConfiguration.getIncludeTarget();

			CustomSecurityFilter securityFilter = new CustomSecurityFilter(
					unauthorizedLocation, includeTarget);

			this.protectedUrls = securityConfiguration.getProtectedUrls();
			this.filter = securityFilter;
		}

		public List<String> getProtectedUrls() {
			return this.protectedUrls;
		}

		public Filter getFilter() {
			return filter;
		}

	}