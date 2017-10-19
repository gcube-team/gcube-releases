package org.gcube.smartgears.extensions.resource;

import static org.gcube.smartgears.Constants.application_xhtml;
import static org.gcube.smartgears.Constants.frontpage_file_path;
import static org.gcube.smartgears.extensions.HttpExtension.Method.GET;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_error;
import static org.gcube.smartgears.provider.ProviderFactory.provider;
import static org.gcube.smartgears.utils.Utils.closeSafely;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.ApiSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ApiResource} of {@link RemoteResource} at {@link #mapping}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class FrontPageResource extends ApiResource {

	// the variable replacement pattern
	private static Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");

	// log on behalf of extension
	private static final Logger log = LoggerFactory.getLogger(RemoteResource.class);

	private static final long serialVersionUID = 1L;

	public static final String mapping = "/";

	private static final ApiSignature signature = handles(mapping).with(method(GET).produces(application_xhtml));

	FrontPageResource() {
		super(signature);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		InputStream page = getClass().getResourceAsStream(frontpage_file_path);

		if (page == null) {
			log.error("invalid distribution: missing {}", frontpage_file_path);
			application_error.fire("invalid distribution: missing " + frontpage_file_path);
		}

		Map<String, String> values = values();

		BufferedReader reader = null;
		try {

			String line = null;
			reader = new BufferedReader(new InputStreamReader(page));
			while ((line = reader.readLine()) != null)
				resp.getWriter().write(interpolate(line, values));

		} catch (Exception e) {
			application_error.fire("could not read " + frontpage_file_path, e);
		} finally {
			closeSafely(reader);
		}

	}

	private Map<String, String> values() {

		Map<String, String> values = new HashMap<String, String>();

		values.put("profile_link", ProfileResource.mapping.substring(1,ProfileResource.mapping.length()));
		values.put("config_link", ConfigurationResource.mapping.substring(1,ConfigurationResource.mapping.length()));
		
		values.put("name", context().name());
		values.put("version", context().configuration().version());

		String infrastructure = context().container().configuration().infrastructure();
		StringBuilder voValue = new StringBuilder();

		Collection<String> scopes = context().profile(GCoreEndpoint.class).scopes().asCollection(); 
		Set<String> vos = new HashSet<String>();
		
		//pre-process
		for (String scope : scopes) {
			ScopeBean bean = new ScopeBean(scope);
			switch (bean.type()) {
				case INFRASTRUCTURE:
					infrastructure = bean.name();
					break;
				case VO:
					vos.add(bean.name());
					break;
				case VRE:
					vos.add(bean.enclosingScope().name());
					infrastructure=bean.enclosingScope().enclosingScope().name();
				}
		}
		
		//build vo value
		int i = 0;
		int max = vos.size()-1;
		for (String vo : vos) {
			String voPrefix = i == 0 ? "" : (i==max?" and ":", ");
			voValue.append(voPrefix+"<em>" + vo + "</em>");
			i++;
		}

		values.put("infra", infrastructure);
		values.put("vos", voValue.toString());
		
		values.put("status", context().lifecycle().state().toString());
		
		values.put("smartgears-version", provider().smartgearsConfiguration().version());
		
		return values;
	}

	public static String interpolate(String text, Map<String, String> replacements) {

		Matcher matcher = pattern.matcher(text);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String replacement = replacements.get(matcher.group(1));
			if (replacement != null) {
				matcher.appendReplacement(buffer, ""); // safer in case replacements include some of the variable
														// characters
				buffer.append(replacement);
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

}
