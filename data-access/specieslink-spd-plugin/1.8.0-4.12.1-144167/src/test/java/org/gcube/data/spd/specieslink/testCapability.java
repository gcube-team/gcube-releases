package org.gcube.data.spd.specieslink;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;


public class testCapability {


	public static void main(String[] args) throws Exception {

		Calendar now = Calendar.getInstance();
		Coordinate coord = new Coordinate(-33, -53);

		Operator op1 = Operator.LE;
		Operator op2 = Operator.GE;		

		Condition p1 = new Condition(Conditions.DATE, now, op1);		
		Condition p2 = new Condition(Conditions.COORDINATE, coord, op2);

		SpeciesLinkPlugin b =  new SpeciesLinkPlugin();

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'SpeciesLink' ");
		//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/EUBrazilOpenBio/SpeciesLab");
		ScopeProvider.instance.set("/gcube/devsec");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		//		System.out.println(resources.size());

		if(resources.size() != 0) {	   
			try {
				b.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		OccurrencesCapabilityImpl a = new OccurrencesCapabilityImpl();

		LocalWrapper<String> wrap1 = new LocalWrapper<String>();
		wrap1.add("http://rs.tdwg.org/dwc/dwcore/ScientificName%20like%20%22Carcharodon%20carcharias%22%20and%20http://rs.tdwg.org/dwc/dwcore/CollectionCode%20like%20%22OBIS_BR%22");
		wrap1.add("http://rs.tdwg.org/dwc/dwcore/ScientificName%20like%20%22Carcharodon%20carcharias%22%20and%20http://rs.tdwg.org/dwc/dwcore/CollectionCode%20like%20%22MCP-Fosseis%22"); 

		LocalReader<String> keys = new LocalReader<String>(wrap1);
//		a.getOccurrencesByProductKeys(new ClosableWriter<OccurrencePoint>() {
//
//			@Override
//			public boolean write(StreamException error) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean write(OccurrencePoint t) {
//				System.out.println(t.getId() + " - " + t.getScientificName());
//				return true;
//			}
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public void close() {
//				// TODO Auto-generated method stub
//
//			}
//		}, keys);


//		LocalWrapper<String> wrap = new LocalWrapper<String>();
//		wrap.add("257852023");
//		//		wrap.add("170380533");
//		//		wrap.add("8303641");
//
//		LocalReader<String> list = new LocalReader<String>(wrap);
//		System.out.println("********** getOccurrencesByIds ************");
//		a.getOccurrencesByIds(new ClosableWriter<OccurrencePoint>(){
//
//			@Override
//			public boolean write(StreamException error) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public boolean write(OccurrencePoint t) {
//				System.out.println(t.getScientificName());
//				return true;
//			}
//
//			@Override
//			public boolean isAlive() {
//				// TODO Auto-generated method stub
//				return true;
//			}
//
//			@Override
//			public void close() {
//				// TODO Auto-generated method stub
//
//			}
//		}, list);

		//		Calendar now = Calendar.getInstance();
		//		Coordinate c = new Coordinate(-33, -53);
		//		Property p1 = new Property(Properties.DateTo, now);
		//		Property p2 = new Property(Properties.CoordinateFrom, c);


		a.searchByScientificName("ulei", new ObjectWriter<OccurrencePoint>(){

			@Override
			public boolean isAlive() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean write(OccurrencePoint ri) {
				System.out.println(ri.getId() + " - " + ri.getScientificName()+ " - " + ri.getScientificNameAuthorship());
				return false;
			}

			@Override
			public boolean write(StreamException arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		}, p1, p2);




		//		LocalWrapper<String> wrap = new LocalWrapper<String>();
		//		wrap.add("145763063");
		//		wrap.add("170380533");
		//		wrap.add("8303641");
		//		LocalReader<String> list = new LocalReader<String>(wrap);
		//		AbstractWrapper<OccurrencePoint> wrapper = new AbstractWrapper<OccurrencePoint>() {
		//
		//			@Override
		//			public boolean isClosed() {
		//				// TODO Auto-generated method stub
		//				return false;
		//			}
		//
		//			@Override
		//			public String getLocator() throws Exception {
		//				// TODO Auto-generated method stub
		//				return null;
		//			}
		//
		//			@Override
		//			public void close() {
		//
		//
		//			}
		//
		//			@Override
		//			public boolean add(OccurrencePoint result) throws InvalidRecordException,
		//			WrapperAlreadyDisposedException {
		//				System.out.println(result);
		//				try{
		//					System.out.println("OccurenceId " + result.getId());
		//				}catch (Exception e) {
		//					// TODO: handle exception
		//				}
		//
		//				return false;
		//			}
		//		};
		//		
		//
		//		a.getOccurrencesByIds(new Writer<OccurrencePoint>(wrapper), list);

	}
}

