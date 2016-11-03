package org.gcube.common.scope.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.scan.ClasspathScanner;
import org.gcube.common.scan.ClasspathScannerFactory;
import org.gcube.common.scan.matchers.NameMatcher;
import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scope.api.ServiceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scans the classpath for {@link ServiceMap}s.
 * 
 * @author Fabio Simeoni
 * 
 */
class ServiceMapScanner {

	private static Logger log = LoggerFactory.getLogger(ServiceMapScanner.class);

	/**
	 * The path used to find service map configuration files.
	 */
	static final String mapConfigPattern = ".*\\.servicemap";

	/**
	 * Scans the classpath for {@link ServiceMap}s.
	 */
	static Map<String, ServiceMap> maps() {

		Map<String, ServiceMap> maps = new HashMap<String, ServiceMap>();

		try {

			Set<String> resources = getMapNames();

			JAXBContext context = JAXBContext.newInstance(DefaultServiceMap.class);
			Unmarshaller um = context.createUnmarshaller();

			for (String resource : resources) {
				
				URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
				log.info("loading {} ", url);
				DefaultServiceMap map = (DefaultServiceMap) um.unmarshal(url);

				ServiceMap current = maps.get(map.scope());
				if (current != null && current.version() != null)
					if (current.version().compareToIgnoreCase(map.version()) == 1) {
						log.warn("discarding {} because older (v.{}) than one previously loaded (v.{}) for {} ",
								new Object[] { url, map.version(), current.version(), map.scope() });
						continue;
					} else
						log.info("overwriting older map (v.{}) with newer map (v.{}) for {} ",
								new Object[] { current.version(), map.version(), map.scope() });

				maps.put(map.scope(), map);
			}
		} catch (Exception e) {
			throw new RuntimeException("could not load service maps", e);
		}

		return maps;
	}

	private static Set<String> getMapNames() {
		
		ClasspathScanner scanner = ClasspathScannerFactory.scanner();
		Set<String> names = new HashSet<String>();
		for (ClasspathResource r : scanner.scan(new NameMatcher(mapConfigPattern)))
			names.add(r.name());
		return names;
	}
}
