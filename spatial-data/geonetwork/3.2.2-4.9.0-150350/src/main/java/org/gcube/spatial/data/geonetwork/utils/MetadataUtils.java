package org.gcube.spatial.data.geonetwork.utils;

import java.net.URI;
import java.util.Iterator;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.distribution.DigitalTransferOptions;

public class MetadataUtils {

	public static String getGeoServerUrl(Metadata meta){
		Iterator<? extends DigitalTransferOptions> it=meta.getDistributionInfo().getTransferOptions().iterator();
		while(it.hasNext()){
			DigitalTransferOptions opts=it.next();			
			for(OnlineResource online:opts.getOnLines()){
				URI uri=online.getLinkage();
				String geourl=uri.toString();				
				if (geourl.length() > 0) {
					int interr = geourl.indexOf("?");
					if (interr > 0)
						geourl = geourl.substring(0, interr);

					if (geourl.endsWith("/wms") || geourl.endsWith("/gwc") || geourl.endsWith("/wfs"))
						return geourl.substring(0, geourl.length() - 4);
				}

			}
		}
		return null;
	}
}
