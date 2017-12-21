package org.gcube.spatial.data.geonetwork.iso.tpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ISOMetadataByTemplate {


	private static Configuration cfg;


	static {
		try{
			// Create your Configuration instance, and specify if up to what FreeMarker
			// version (here 2.3.25) do you want to apply the fixes that are not 100%
			// backward-compatible. See the Configuration JavaDoc for details.
			cfg = new Configuration(Configuration.VERSION_2_3_25);

			// Specify the source where the template files come from. Here I set a
			// plain directory for it, but non-file-system sources are possible too:

			File localDir=new File("./");		
			System.out.println("Current folder info ");
			System.out.println(localDir.getAbsolutePath());
			System.out.println(Arrays.toString(localDir.list()));
			
			
			
			cfg.setDirectoryForTemplateLoading(new File("src/xmlTemplates"));

			// Set the preferred charset template files are stored in. UTF-8 is
			// a good choice in most applications:
			cfg.setDefaultEncoding("UTF-8");

			// Sets how errors will appear.
			// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

			// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
			cfg.setLogTemplateExceptions(false);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}



	public static final String createXML(MetadataDescriptor desc) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException{
		Writer out=null;
		try{
			Template temp = cfg.getTemplate("BaseTemplate.ftlx");
			File output=File.createTempFile("ISO_", ".xml");
			out=new OutputStreamWriter(new FileOutputStream(output));
			temp.process(desc, out);
			return output.getAbsolutePath();		
		}finally{
			if(out!=null)
				IOUtils.closeQuietly(out);				
		}

	}



}
