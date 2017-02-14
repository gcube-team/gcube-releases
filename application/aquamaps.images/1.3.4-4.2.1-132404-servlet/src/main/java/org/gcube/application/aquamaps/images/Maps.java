package org.gcube.application.aquamaps.images;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.images.exceptions.ImageNotFoundException;
import org.gcube.application.aquamaps.images.exceptions.InvalidRequestException;
import org.gcube.application.aquamaps.images.model.ProductType;

public class Maps extends BaseServlet{


	
	private static Common common=Common.get();

	/**
	 * 
	 */
	private static final long serialVersionUID = 3628994804078627741L;



	
	
	/**Handles path like
	 * 
	 * 	<HOST>/images/maps/(<ALGORITHM>|<HSPEC_ID>)?species=<SCIENTIFIC_NAME>&product=<PRODUCT_TYPE>
	 *   
	 */
	@Override
	protected InputStream handleRequest(HttpServletRequest req,
			HttpServletResponse resp) throws IOException,
			ImageNotFoundException, InvalidRequestException, Exception {
		
		if(!hasScientificName(req)) throw new InvalidRequestException();
		String scientificName=getScientificName(req);
		
		
		int hspecId=getHSPECId(req);
		ProductType prod=getProductType(req);
		logger.debug("Serving req : "+scientificName+", hspecId: "+hspecId+", prod: "+prod);
		if(prod.equals(ProductType.GIS))resp.setContentType("application/xml");
		else if(prod.equals(ProductType.PIC)) throw new InvalidRequestException();
		else resp.setContentType(Common.IMAGE_JPEG);
		return common.getProduct(prod,hspecId, scientificName);
	}
}
