package org.gcube.data.publishing.gCatFeeder.catalogues.gCat;

import org.gcube.data.publishing.gCatFeeder.catalogues.CatalogueController;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.PublishReport;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.CatalogueInteractionException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.ControllerInstantiationFault;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.PublicationException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.WrongObjectFormatException;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;
import org.gcube.data.publishing.gCatFeeder.tests.BaseCataloguePluginTest;
import org.gcube.data.publishing.gCataFeeder.catalogues.gCat.GCatPlugin;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class Interactions extends BaseCataloguePluginTest{

	private static class CustomTargetFormat implements CatalogueFormatData{

		private String fileName;

		public CustomTargetFormat(String fileName) {
			super();
			this.fileName = fileName;
		}

		@Override
		public String toCatalogueFormat() {
			try {
				return convertStreamToString(this.getClass().getResourceAsStream((fileName)));
			}catch(Exception e) {
				throw new RuntimeException("Unable to read file "+fileName,e);
			}
		}
		
		static String convertStreamToString(java.io.InputStream is) {
		    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
		}
	}






	public CatalogueController getController() throws ControllerInstantiationFault {
		GCatPlugin plugin=new GCatPlugin();	
		return plugin.instantiateController(new CatalogueInstanceDescriptor());
	}



	@Test(expected=ControllerInstantiationFault.class)	
	public void testFailInstance() throws ControllerInstantiationFault {
		GCatPlugin plugin=new GCatPlugin();
		plugin.instantiateController(new CatalogueInstanceDescriptor().setUrl("http://no.where.com"));
	}


	@Test
	public void duplicates() {
		Assume.assumeTrue(isTestInfrastructureEnabled());
			try {
				publish(getController(),"full.json");
				publish(getController(),"full.json");
			}catch(Exception e) {
				Assert.fail(e.getMessage());
			}		
	}


	@Test
	public void testDM() throws CatalogueInteractionException, WrongObjectFormatException, PublicationException, InternalConversionException, ControllerInstantiationFault {
		Assume.assumeTrue(isTestInfrastructureEnabled());
		publish(getController(),"full-algorithm.json");
	}
	
	@Test(expected=WrongObjectFormatException.class)
	public void empties() throws CatalogueInteractionException, WrongObjectFormatException, PublicationException, ControllerInstantiationFault, InternalConversionException {
		Assume.assumeTrue(isTestInfrastructureEnabled());
			publish(getController(),"empties.json");
	}


	@Test(expected=WrongObjectFormatException.class)
	public void missingProfile() throws CatalogueInteractionException, WrongObjectFormatException, PublicationException, ControllerInstantiationFault, InternalConversionException {
		Assume.assumeTrue(isTestInfrastructureEnabled());
			publish(getController(),"missingProfile.json");
	}


	@Test(expected=WrongObjectFormatException.class) 
	public void noitem() throws CatalogueInteractionException, WrongObjectFormatException, PublicationException, ControllerInstantiationFault, InternalConversionException {
		Assume.assumeTrue(isTestInfrastructureEnabled());
			publish(getController(),"noItem.json");
	}

	@Test
	public void item() {
		Assume.assumeTrue(isTestInfrastructureEnabled());
		try {
			publish(getController(),"onlyItem.json");
		} catch (WrongObjectFormatException | PublicationException | ControllerInstantiationFault | InternalConversionException e) {
			Assert.fail(e.getMessage());
		}
	}


	private static PublishReport publish(CatalogueController controller, String filename) throws CatalogueInteractionException, WrongObjectFormatException, PublicationException, InternalConversionException {
		return controller.publishItem(new CustomTargetFormat(filename));
	}
}
