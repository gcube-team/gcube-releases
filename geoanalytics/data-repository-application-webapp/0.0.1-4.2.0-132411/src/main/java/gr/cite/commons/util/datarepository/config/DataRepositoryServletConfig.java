package gr.cite.commons.util.datarepository.config;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import gr.cite.commons.inject.JacksonJsonProviderProvider;
import gr.cite.commons.inject.ObjectMapperProvider;
import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.config.DataRepositoryApplicationConfiguration;
import gr.cite.commons.util.datarepository.inject.FileSystemDataRepositoryProvider;
import gr.cite.commons.util.datarepository.resource.FileSystemDataRepositoryResource;
import gr.cite.repo.auth.webapp.inject.servlets.SamlSecureServletConfig;

public class DataRepositoryServletConfig extends SamlSecureServletConfig<DataRepositoryApplicationConfiguration> {

	public static final String PROPERTIES_FILE_PATH = Thread.currentThread().getContextClassLoader()
			.getResource("data-repo-config.yaml") != null
					? Thread.currentThread().getContextClassLoader().getResource("data-repo-config.yaml").getPath()
					: "data-repo-config.yaml";

	@Override
	protected Class<DataRepositoryApplicationConfiguration> getClazz() {
		return DataRepositoryApplicationConfiguration.class;
	}

	@Override
	protected String getConfiguationFilePath() {
		return PROPERTIES_FILE_PATH;
	}

	@Override
	protected List<Module> getModules() {
		Module module = new Module() {

			@Override
			public void configure(Binder binder) {
				binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Scopes.SINGLETON);
				binder.bind(JacksonJsonProvider.class).toProvider(JacksonJsonProviderProvider.class)
						.in(Scopes.SINGLETON);

				binder.bind(DataRepository.class).toProvider(FileSystemDataRepositoryProvider.class);
				
				binder.bind(String.class).annotatedWith(Names.named("publicUrl")).toInstance(getConfiguration().getPublicUrl());
				
				binder.bind(FileSystemDataRepositoryResource.class).in(Scopes.SINGLETON);
			}

		};

		return Lists.newArrayList(module);
	}

	@Override
	protected String getName() {
		return "data-repository-application";
	}

	@Override
	protected Boolean isSecure() {
		return false;
	}
}
