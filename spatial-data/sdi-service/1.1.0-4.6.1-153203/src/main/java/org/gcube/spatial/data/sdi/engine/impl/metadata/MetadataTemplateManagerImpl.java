package org.gcube.spatial.data.sdi.engine.impl.metadata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.MetadataTemplateManager;
import org.gcube.spatial.data.sdi.engine.impl.metadata.templates.AbstractTemplate;
import org.gcube.spatial.data.sdi.engine.impl.metadata.templates.InvalidTemplateInvocationException;
import org.gcube.spatial.data.sdi.engine.impl.metadata.templates.ThreddsOnlineTemplate;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MetadataTemplateManagerImpl implements MetadataTemplateManager {


	private static Configuration cfg;

	private static ArrayList<TemplateDescriptor> templateDescriptors=new ArrayList<>();
	private static HashMap<String,AbstractTemplate> availableTemplates=new HashMap<>();

	private static TemplateCollection collection;
	
	
	@PostConstruct
	public void defaultInit() {
		init(LocalConfiguration.get().getTemplateConfigurationObject());
	}
	
	
	
	public void init(Object configurationObject) {
		
		log.debug("Configuring with {} ",configurationObject);
		
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.25) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		cfg = new Configuration(Configuration.VERSION_2_3_25);

		if(configurationObject instanceof ApplicationContext){
			log.debug("Configuration is Context : {} ",configurationObject);
			cfg.setServletContextForTemplateLoading(((ApplicationContext)configurationObject).application(), 
					LocalConfiguration.get().getProperty(LocalConfiguration.METADATA_TEMPLATE_FOLDER));			
		}else if (configurationObject instanceof File){
			try{
				cfg.setDirectoryForTemplateLoading((File)configurationObject);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
			
		}else throw new RuntimeException("Invalid configuration object");

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		cfg.setLogTemplateExceptions(false);


		//			availableTemplates.add(new TemplateDescriptor("THREDDS-ONLINE", "Thredds online resources", "Template for online resources exposed by thredds.", "http://sdi-d4s.d4science.org"));


		ThreddsOnlineTemplate tpl=new ThreddsOnlineTemplate();
		availableTemplates.put(tpl.getDescriptor().getId(), tpl);
		templateDescriptors.add(tpl.getDescriptor());
		log.debug("Loaded templates : ");
		for(TemplateDescriptor desc: templateDescriptors)
			log.debug(desc.toString());
		
		collection=new TemplateCollection(new HashSet<>(templateDescriptors));
	}

//
//	public static String getTHREDDSLinks(ThreddsLinkRequest req) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException{
//		Writer out=null;
//		try{
//			Template temp = cfg.getTemplate("OnlineResources.ftlx");
//			ByteArrayOutputStream baos=new ByteArrayOutputStream();
//			out=new OutputStreamWriter(baos);
//			temp.process(req, out);
//			out.flush();
//			return baos.toString(StandardCharsets.UTF_8.toString());		
//		}finally{
//			if(out!=null)
//				IOUtils.closeQuietly(out);				
//		}
//	}
//
//
//
//
//	public MetadataTemplateManagerImpl() {
//		// TODO Auto-generated constructor stub
//	}


	@Override
	public TemplateCollection getAvailableTemplates() {
		return collection;
	}

	@Override
	public TemplateApplicationReport applyTemplates(File original, Set<TemplateInvocation> invocations) throws IOException, TransformerException {
		log.debug("Applying template invocations {} to {} ",invocations,original.getAbsolutePath());
		TemplateApplicationReport report=new TemplateApplicationReport();
		report.setRequestedInvocations(invocations);		
		HashSet<String> appliedTemplates=new HashSet<>();
		MetadataHandler handler=new MetadataHandler(original);
		for(TemplateInvocation invocation:invocations){
			try{
				applyTemplate(original, invocation,handler);
				appliedTemplates.add(invocation.getToInvokeTemplateID());
			}catch(Throwable t){
				log.warn("Unable to apply template {} ",invocation.getToInvokeTemplateID());
				log.debug("StackTrace : ",t);
			}
		}		
		log.debug("Writing out result..");
		report.setGeneratedFilePath(handler.writeOut().getAbsolutePath());
		report.setAppliedTemplates(appliedTemplates);
		return report;
	}

	private static void applyTemplate(File original,TemplateInvocation invocation,MetadataHandler handler) throws Exception{
		log.debug("Instantiating "+invocation);
		AbstractTemplate tpl=availableTemplates.get(invocation.getToInvokeTemplateID());
		if(tpl==null) throw new InvalidTemplateInvocationException("Template with ID "+invocation.getToInvokeTemplateID()+" was not found");
		Writer out=null;		
		try{
			Template temp = cfg.getTemplate(tpl.getFileName());
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			out=new OutputStreamWriter(baos);
			temp.process(tpl.getInstantiationRequest(handler,invocation), out);
			out.flush();
			String instantiatedTemplate= baos.toString(StandardCharsets.UTF_8.toString());

			//apply to original
			handler.addContent(instantiatedTemplate, tpl.getInsertionPoint());
		} catch (Exception e) {
			log.error("Unable to apply template. Invocation was {} ",invocation,e);
			throw e;		 
		}finally{
			if(out!=null)
				IOUtils.closeQuietly(out);				
		}
	}

}
