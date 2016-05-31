package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing;

import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge.GradualMergeOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TestMerge {

	public static void main(String[] args) throws Exception {
		TCPConnectionManager.Init(new ConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		

//		uri1 = new SelectOp(uri1, "[mimeType] <= [id]", new StatsContainer()).compute();
//		uri1 = new SortOp(uri1, new StatsContainer()).compute("id", CompareTokens.DESCENDING_ORDER);
		
//		URI uri2 = testJDBCSource();
//		uri2 = new SelectOp(uri2, "[mimeType] == 'image/jpeg'", new StatsContainer()).compute();
//		uri2 = new SortOp(uri2, new StatsContainer()).compute("id", CompareTokens.DESCENDING_ORDER);
		
		InputLocator in = new InputLocator();
		in.put(TestDataSource.testJDBCSource());

		
		final URI furi = new GradualMergeOp(in.writer.getLocator(), new HashMap<String, String>(), new StatsContainer()).compute();

		new Thread() {
			public void run() {
				try {
					GRS2Printer.print(furi, 6);
//					Thread.sleep(5000);
					GRS2Printer.compute();
				} catch (GRS2ReaderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();		
		
		
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());
		Thread.sleep(5000);
		in.put(TestDataSource.testJDBCSource());


		in.writer.close();
		
//		uri = new MergeOp(new URI[]{uri2, uri1}, new StatsContainer()).compute();
		
//		JoinOp join = ;
//		join.producerDefinitionMap;
//		URI uri = join.compute("id", "id");
		
//		
//		BooleanOperator bool = new BooleanOperator();
//		bool.compareMe(new URI[]{uri}, "<Eq><Token>12</Token><Token>12</Token></Eq>", new StatsContainer());
	}
}

class InputLocator {
	public RecordWriter<GenericRecord> writer; 
	public InputLocator () throws GRS2WriterException {
		RecordDefinition[] defs = new RecordDefinition[] { new GenericRecordDefinition((new FieldDefinition[] { new StringFieldDefinition("Rowset") })) };
		writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), defs, RecordWriter.DefaultBufferCapacity,
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor, 1, TimeUnit.DAYS);
	}
	
	public boolean put(URI uri) throws GRS2WriterException {
		GenericRecord rec = new GenericRecord();
			rec.setFields(new Field[] { new StringField(uri.toASCIIString()) });

			return writer.put(rec);
	}
	
	public void close() throws GRS2WriterException {
		writer.close();
	}
}

