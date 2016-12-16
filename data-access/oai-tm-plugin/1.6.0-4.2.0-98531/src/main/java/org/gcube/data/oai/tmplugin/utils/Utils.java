package org.gcube.data.oai.tmplugin.utils;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class Utils {

	public static String idEncoder(String original){
		return original.replaceAll("/",(int)'|'+"sep"+((int)'/')+"sep"+(int)'#');
	}

	public static String idDecoder(String original){
//		System.out.println("idDecoder " + original);
		return original.replaceAll((int)'|'+"sep"+((int)'/')+"sep"+(int)'#',"/");
	}
	
	
	
	public static String toSchema(Class<?> clazz) {

		final StringWriter writer = new StringWriter();

		SchemaOutputResolver resolver = new SchemaOutputResolver() {

			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				Result result = new StreamResult(writer);
				result.setSystemId("anything");
				return result;
			}
		};

		try {
			JAXBContext ctxt = JAXBContext.newInstance(clazz);
			ctxt.generateSchema(resolver);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}

	
	//uri resolver
	public static String resolver(String contentUri) {
		
//		Resource Identifier	doi:10.1594/WDCC/CLM_C20_3_D3
//		Resource Identifier	urn:nbn:de:tib-10.1594/WDCC/CLM_C20_3_D36
		
//		Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
//	    Matcher matcher = pattern.matcher(contentUri);
	
		String res = null;
		String[] ident = contentUri.split(":");
		String prefix = ident[0];
		
		if (prefix.equals(Resolver.DOIPREFIX)){
			res = Resolver.DOI + ident[1];
		}else if (prefix.equals(Resolver.URNPREFIX)){
			res = Resolver.URN + contentUri;
		}else
			return contentUri;
		
		return res;
		
	}

	
}
