package org.gcube.data.obisplugin;

import java.util.Arrays;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.obisplugin.capabilities.OccurrencesCapabilityImpl;
import org.gcube.data.spd.obisplugin.search.ResultItemSearch;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.junit.Test;

public class ObisTest {

	@Test
	public void search() throws Exception{
		/*ObisPlugin plugin= new ObisPlugin();
		plugin.initialize(new DatabaseCredential("jdbc:postgresql://geoserver2.i-marine.research-infrastructures.eu/obis", "postgres", "0b1s@d4sc13nc3"));
		plugin.getOccurrencesInterface().searchByScientificName("Architeuthis dux", writer);*/
		ClosableWriter<OccurrencePoint> writer = new ClosableWriter<OccurrencePoint>() {

			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public boolean write(OccurrencePoint arg0) {
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				return false;
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub
				
			}

		};
			

		
		OccurrencesCapabilityImpl impl = new OccurrencesCapabilityImpl("http://api.iobis.org/");
		//impl.searchByScientificName("Cetacea", writer);
		impl.getOccurrencesByProductKeys(writer, Arrays.asList("3422||513384||geometry=POLYGON((30.000000%2020.000000,90.000000%2020.000000,90.000000%20180.000000,30.000000%2020.000000))").iterator() );
	}
	
	@Test
	public void searchRI() throws Exception{
		/*ObisPlugin plugin= new ObisPlugin();
		plugin.initialize(new DatabaseCredential("jdbc:postgresql://geoserver2.i-marine.research-infrastructures.eu/obis", "postgres", "0b1s@d4sc13nc3"));
		plugin.getOccurrencesInterface().searchByScientificName("Architeuthis dux", writer);*/
		ObjectWriter<ResultItem> writer = new ObjectWriter<ResultItem>() {

			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public boolean write(ResultItem ri) {
				System.out.println(ri);
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				return false;
			}
		};
		
		//, new Condition(Conditions.COORDINATE, new Coordinate(20, 30) , Operator.GT)
		ResultItemSearch search = new ResultItemSearch("http://api.iobis.org/", "Gamidae");
		
		search.search(writer, 50);
		
	}
}
