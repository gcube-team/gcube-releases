package org.gcube.portal.social.networking.ws;

import org.gcube.portal.social.networking.swagger.config.Bootstrap;

import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;


@SwaggerDefinition(
		info = @Info(
				description = "This is the official documentation of the second version (v. 2.0) of the Social-Networking RESTful interface.",
				version = "V 2.0",
				title = "Social Networking RESTful Service",
				contact = @Contact(
						name = "Costantino Perciante", 
						email ="costantino.perciante@isti.cnr.it"
						),
						extensions = {
					@Extension(name = "extra-contact", properties = {
							@ExtensionProperty(name = "name", value = "Massimiliano Assante"),
							@ExtensionProperty(name = "email", value = "massimiliano.assante@isti.cnr.it")
					}),
					@Extension(name = "development-host", properties = {
							@ExtensionProperty(name = "url", value = "https://socialnetworking-d-d4s.d4science.org"),
					})
				}
				),
				externalDocs=@ExternalDocs(url="https://wiki.gcube-system.org/gcube/Social_Networking_Service", value="Wiki page at gCube"),
				host="socialnetworking1.d4science.org",
				basePath="social-networking-library-ws/rest",
				securityDefinition=@SecurityDefinition(
						apiKeyAuthDefinitions={
								@ApiKeyAuthDefinition(key=Bootstrap.GCUBE_TOKEN_IN_QUERY_DEF, description="A token bound to a user (or app identifier) and a context", in=ApiKeyLocation.HEADER, name="gcube-token"),
								@ApiKeyAuthDefinition(key=Bootstrap.GCUBE_TOKEN_IN_HEADER_DEF, description="A token bound to a user (or app identifier) and a context", in=ApiKeyLocation.QUERY, name="gcube-token")
						}),
						schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS}
		)
public interface SNLApiConfig {

}
