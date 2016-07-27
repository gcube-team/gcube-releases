package org.gcube.application.aquamaps.images;



import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.application.aquamaps.images.exceptions.ImageNotFoundException;
import org.gcube.application.aquamaps.images.exceptions.InvalidRequestException;
import org.gcube.application.aquamaps.images.model.ProductType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3028949525505728303L;

	protected static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
	
	protected static Common common=Common.get();
	
	protected abstract InputStream handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException,ImageNotFoundException,InvalidRequestException,Exception;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream is=null;
			try {
				is=handleRequest(req, resp);
				IOUtils.copy(is, resp.getOutputStream());
			}catch(IOException e){
				logger.debug("IOException ",e);
				is=common.getImageNotFound();
				IOUtils.copy(is, resp.getOutputStream());
			}catch(ImageNotFoundException e){
				logger.debug("ImageNotFound ",e);
				is=common.getImageNotFound();
				IOUtils.copy(is, resp.getOutputStream());
			}catch(InvalidRequestException e){
				logger.debug("BAD Request ",e);
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}catch(Exception e){
				logger.error("Unexpected error ",e);
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}finally{
				if(is!=null) IOUtils.closeQuietly(is);
				try{
					IOUtils.closeQuietly(resp.getOutputStream());
				}catch (Throwable t){
					logger.error("",t);
				}
			}		
	}
	
	
	
	private static final String SPECIES_PARAM="species";
	
	private static final String PRODUCT_PARAM="product"; 
		
	private static final String SUITABLE="suitable";
	private static final String SUITABLE_2050="suitable/2050";
	private static final String NATIVE="native";
	private static final String NATIVE_2050="native/2050";
	
	
	protected static final boolean hasScientificName(HttpServletRequest req){
		return req.getParameter(SPECIES_PARAM)!=null;
	}
	
	protected static final String getScientificName(HttpServletRequest req){
		return req.getParameter(SPECIES_PARAM).toLowerCase();
	}
	
	protected static final int getHSPECId(HttpServletRequest req) throws InvalidRequestException{
		String mapType=req.getPathInfo().substring(1);
		logger.debug("request Hspec is "+mapType);
		Map<String,String> config=common.getLastConfiguration();		
		if(mapType.equals(SUITABLE)) return Integer.parseInt(config.get(Common.SUITABLE_PROP));
		else if(mapType.equals(SUITABLE_2050)) return Integer.parseInt(config.get(Common.SUITABLE_2050_PROP));
		else if(mapType.equals(NATIVE)) return Integer.parseInt(config.get(Common.NATIVE_PROP));
		else if(mapType.equals(NATIVE_2050))return Integer.parseInt(config.get(Common.SUITABLE_2050_PROP));
		else try{
			return Integer.parseInt(mapType);
		}catch (Exception e) {
			throw new InvalidRequestException();
		}
	}
	
	protected static final ProductType getProductType(HttpServletRequest req) throws InvalidRequestException{
		if(req.getParameter(PRODUCT_PARAM)!=null){
			try{
				return ProductType.valueOf(req.getParameter(PRODUCT_PARAM));
			}catch(Exception e){
				throw new InvalidRequestException();
			}
		}else return ProductType.IMAGE;
	}
}
