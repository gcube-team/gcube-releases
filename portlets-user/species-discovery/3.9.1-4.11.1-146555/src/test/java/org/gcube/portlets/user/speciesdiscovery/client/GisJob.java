/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.client.proxies.ExecutorClient;
import org.gcube.data.spd.model.service.types.CompleteJobStatus;
import org.gcube.data.spd.model.service.types.JobStatus;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2017
 */
public class GisJob {

	static List<String> keys = new ArrayList<String>();

	public static void main(String[] args) {

		try {

			ScopeProvider.instance.set("/gcube/devsec");

			keys.add("OBIS:161||666236");
			ExecutorClient creator = AbstractPlugin.executor().build();
			Stream<String> keyStream = Streams.convert(keys);
			System.out.println("keys are: "+keys.toString());

			MetadataDetails details= new MetadataDetails("title", "descr", "tile", "author", "credits");
			System.out.println("submittings job...");
			String jobId = creator.createLayer(keyStream, details);
			System.out.println("The job id is: "+jobId);

			CompleteJobStatus status = creator.getStatus(jobId);

			JobStatus sta = status.getStatus();
			while(sta!=JobStatus.COMPLETED && sta!=JobStatus.FAILED){
				Thread.sleep(1000);
				sta = creator.getStatus(jobId).getStatus();
				System.out.println("checking status.."+sta);
			}

			System.out.println("job terminated");

		}catch (Exception e) {
			e.printStackTrace();
		}

	}


}
