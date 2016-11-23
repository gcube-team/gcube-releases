package gr.cite.commons.util.datarepository.application;

import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.config.DataRepositoryApplicationConfiguration;
import gr.cite.commons.util.datarepository.inject.FileSystemDataRepositoryProvider;
import gr.cite.commons.util.datarepository.resource.FileSystemDataRepositoryResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.UrlConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.IOException;

import com.google.common.io.Resources;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class DataRepositoryApplication extends Application<DataRepositoryApplicationConfiguration> {

	@Override
	public void initialize(Bootstrap<DataRepositoryApplicationConfiguration> bootstrap) {
		
		String url = Resources.getResource("repo-config.yaml").toString();
		UrlConfigurationSourceProvider ucsp= new UrlConfigurationSourceProvider();
		
		try {
			ucsp.open(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bootstrap.setConfigurationSourceProvider(ucsp);

	}

	@Override
	public void run(DataRepositoryApplicationConfiguration configuration, Environment environment) throws Exception {
		Injector injector = Guice.createInjector(new Module() {

			@Override
			public void configure(Binder binder) {
				binder.bind(DataRepository.class).toProvider(FileSystemDataRepositoryProvider.class);
			}
		});

		environment.jersey().register(new FileSystemDataRepositoryResource(
				injector.getInstance(FileSystemDataRepositoryProvider.class), configuration.getPublicUrl()));
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			new DataRepositoryApplication().run(new String[] { "server", Resources.getResource("repo-config.yaml").toString() });
		} else {
			new DataRepositoryApplication().run(args);
		}
	}
}
