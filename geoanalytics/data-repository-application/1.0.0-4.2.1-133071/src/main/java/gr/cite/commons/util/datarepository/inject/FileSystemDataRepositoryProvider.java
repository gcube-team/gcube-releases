package gr.cite.commons.util.datarepository.inject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Provider;

import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.filesystem.FileSystemDataRepository;

public class FileSystemDataRepositoryProvider implements Provider<DataRepository> {
	private static final Logger logger = LoggerFactory.getLogger(FileSystemDataRepositoryProvider.class);
	private FileSystemDataRepository dataRepository;

	public FileSystemDataRepositoryProvider() {
		this.dataRepository = new FileSystemDataRepository();
		Properties properties = new Properties();

		try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("fileRepositoryConfig.properties");) {
			properties.load(inputStream);

			dataRepository.setConfig(Maps.fromProperties(properties));
			
			postConstructCalls();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * call methods with {@link PostConstruct} annotation
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private void postConstructCalls() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Method> methods = Arrays.asList(FileSystemDataRepository.class.getDeclaredMethods());
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(PostConstruct.class)) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				method.invoke(this.dataRepository);
				
				break;
			}
		}
	}

	@Override
	public DataRepository get() {
		return dataRepository;
	}
}
