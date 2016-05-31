package org.gcube.data.obisplugin;

import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.obisplugin.ObisPlugin;
import org.gcube.data.spd.obisplugin.pool.DatabaseCredential;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.junit.Test;

public class ObisTest {

	@Test
	public void search() throws Exception{
		ObisPlugin plugin= new ObisPlugin();
		plugin.initialize(new DatabaseCredential("jdbc:postgresql://geoserver2.i-marine.research-infrastructures.eu/obis", "postgres", "0b1s@d4sc13nc3"));
		ObjectWriter<OccurrencePoint> writer = new ObjectWriter<OccurrencePoint>() {

			@Override
			public boolean isAlive() {
				return true;
			}

			@Override
			public boolean write(OccurrencePoint arg0) {
				System.out.println(arg0.toString());
				return true;
			}

			@Override
			public boolean write(StreamException arg0) {
				return false;
			}
		};
		plugin.getOccurrencesInterface().searchByScientificName("Architeuthis dux", writer);
	}
	
}
