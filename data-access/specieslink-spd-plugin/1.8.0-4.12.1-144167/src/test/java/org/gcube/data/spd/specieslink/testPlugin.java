package org.gcube.data.spd.specieslink;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class testPlugin {

//	valid url example: http://tapir.cria.org.br/tapirlink/tapir.php/specieslink?op=search&start=0&limit=10&filter=http://rs.tdwg.org/dwc/dwcore/ScientificName%20like%20%22carcharodon%22&model=http://rs.tdwg.org/tapir/cs/dwc/1.4/model/dw_core_geo_cur.xml
	
	public static void main(String[] args) throws Exception {

//		Calendar now = Calendar.getInstance();
//		Coordinate coord = new Coordinate(-33, -53);
//
//		Operator op1 = Operator.GE;
//		Operator op2 = Operator.LE;				
//		Condition p1 = new Condition(Conditions.DATE, now, op1);		
//		Condition p2 = new Condition(Conditions.COORDINATE, coord, op2);
	
//		Property p3 = new Property(Properties.DateFrom, now);
		
		SpeciesLinkPlugin a = new SpeciesLinkPlugin();

		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'SpeciesLink' ");
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/EUBrazilOpenBio/SpeciesLab");
		ScopeProvider.instance.set("/gcube/devsec");
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

//		System.out.println(resources.size());
		
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		a.searchByScientificName("polyantha", new ObjectWriter<ResultItem>() {
//
//			public boolean put(ResultItem ri) {
//				System.out.println(ri.toString());
//				return true;
//			}
//
//			public void close() {
//			}
//
//			@Override
//			public boolean put(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public boolean write(ResultItem ri) {
//				System.out.println(ri.toString());
//				return false;
//			}
//
//			@Override
//			public boolean write(StreamException arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		}, p1, p2);
		


		a.searchByScientificName("ros", new ObjectWriter<ResultItem>() {

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(ResultItem ri) {
//				System.out.println(ri.getRank());
//				System.out.println(ri.getCitation());
//				System.out.println(ri.getCredits());
				System.out.println(ri.getId() + " - " + ri.getScientificName()+ " - " + ri.getScientificNameAuthorship());
//				System.out.println(ri.getId());
//				System.out.println(ri.getRank());
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
}
