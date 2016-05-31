package org.gcube.common.resources.gcore;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.common.resources.gcore.Resources.*;
import static org.gcube.common.resources.gcore.Software.Profile.GenericPackage.Type.*;
import static org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.Requirement.OpType.*;
import static org.gcube.common.resources.gcore.TestUtils.*;

import java.net.URI;

import org.gcube.common.resources.gcore.Software;
import org.gcube.common.resources.gcore.Software.Profile.GenericPackage;
import org.gcube.common.resources.gcore.Software.Profile.PluginPackage;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage.Function;
import org.gcube.common.resources.gcore.Software.Profile.ServicePackage.PortType;
import org.gcube.common.resources.gcore.Software.Profile.SoftwarePackage.PackageDependency;
import org.junit.Test;
import org.w3c.dom.Element;

public class SoftwareTest {

	@Test
	public void bindSoftware() throws Exception {

		Software service = unmarshal(Software.class, "service.xml");

		print(service);

		validate(service);
		
		Software clone = unmarshal(Software.class, "service.xml");
		
		assertEquals(service,clone);
		
		validate(service);
		
	}
	
	
	private Software minimalSoftware() {
		
		Software software = new Software();
		
		software.scopes().add("/some/scope");
		
		software.newProfile().softwareClass("class").softwareName("name");
		
		ServicePackage service = software.profile().packages().add(ServicePackage.class);
		
		service.name("testName").version("testVersion").archive("archive").portTypes().add().name("name");
		
		//not minimal but conveniently done here
		PortType pt = service.portTypes().add();
		pt.name("name2").newSecurity().newDescriptor().appendChild(pt.security().descriptor().getOwnerDocument().createElement("descriptor"));
		pt.newWsdl().appendChild(pt.wsdl().getOwnerDocument().createElement("wsdl"));
		
		//not minimal but conveniently done here
		Function f = service.functions().add();
		Element body = f.name("name").newBody();
		body.appendChild(body.getOwnerDocument().createElement("body"));
		f.formalParameters().addAll(asList("param1","param2"));
		
		return software;
		
	}
	
	@Test
	public void buildMinimalSoftware() throws Exception {
			
			Software software = minimalSoftware();
			
			print(software);
			
			validate(software);
	}

	
	@Test
	public void buildMaximalSoftware() throws Exception {
		
		Software software = minimalSoftware();
		
        software.profile().description("description");
        
        software.profile().newConfiguration().newStaticConfiguration().configurations().add().
        									description("desc").file("file").label("label").isDefault(true);
        
        software.profile().configuration().staticConfiguration().configurations().add().
											description("desc2").file("file2").label("label2").isDefault(false);
        
        validate(software);
        
        software.profile().configuration().staticConfiguration().newTemplate().
        													params().add().name("name").description("descr").
        													allowedValues().add().
        															description("description").
        															label("label").
        																 isDefault(true).literal("lit");
		
        software.profile().configuration().staticConfiguration().template().
        								params().add().name("name2").description("descr2").
        								allowedValues().add().
        									description("description2").
        									label("label2").
        									isDefault(false).literal("lit2");
		
        
        
        software.profile().dependencies().add().serviceName("name").serviceClass("class").version("version");
        software.profile().dependencies().add().serviceName("name2").serviceClass("class2").version("version2");
        
        GenericPackage generic = software.profile().packages().add(GenericPackage.class);
        
        generic.name("testName").type(application).version("testVersion").description("description").files().add("string");
        
        validate(software);
        
        software.profile().packages().add(GenericPackage.class).
        		name("testName2").version("testVersion2").description("description2").uri(URI.create("http://acme.org"));
        
        generic.newCoordinates().artifactId("id").groupId("gid").version("version").classifier("classifier");
		generic.newTargetPlatform().name("name").version((short)1);
		generic.multiVersion(false);
		
		generic.entryPoints().addAll(asList("entry1","entry2"));
		
		generic.ghnRequirements().add().category("cat").key("key").requirement("req").operator(exist).value("val");
		generic.ghnRequirements().add().category("cat2").key("key2").requirement("req2").operator(exist).value("val2");

		generic.installScripts().addAll(asList("installone","installtwo"));
		generic.uninstallScripts().addAll(asList("uninstallone","uninstalltwo"));
		generic.rebootScripts().addAll(asList("rebootone","reboottwo"));
		
		PackageDependency pDependency= generic.dependencies().add();
		pDependency.newService().packageClass("pClass").packageName("pName").version("1.1.1");
		pDependency.dependencyPackage("pack1").version("1.1.1").newScope();
		
		Element packageRoot=generic.newSpecificData();
		packageRoot.appendChild(packageRoot.getOwnerDocument().createElement("nothingData"));
		
        software.profile().packages().add(GenericPackage.class).
								name("testName2").version("testVersion2").
								description("description2").uri(URI.create("http://acme.org"));

        Element root = software.profile().newSpecificData();
        root.appendChild(root.getOwnerDocument().createElement("nothing"));
        
        print(software);
		
		validate(software);
		
		
	}
	
	@Test
	public void buildPlugin() throws Exception {
		
		Software software = new Software();
		
		software.scopes().add("/some/scope");
		
		PluginPackage plugin = software.newProfile().softwareClass("class").softwareName("name").
							  packages().add(PluginPackage.class);
		
		plugin.name("testName").version("version").newTargetService().servicePackage("package").version("version").
														newService().serviceClass("class").serviceName("name").version("version");
	
		plugin.entryPoint("entry");
		plugin.files().addAll(asList("file1","file2"));
		
		print(software);
		
		validate(software);
	}
	
}
