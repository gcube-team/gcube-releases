package helpers;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.utils.Locators;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RS2Feed {
	private RecordDefinition[] defs = null;
	private RecordWriter<GenericRecord> writer = null;

	public RS2Feed() throws GRS2WriterException {
		defs = new RecordDefinition[] { new GenericRecordDefinition(
				(new FieldDefinition[] { new StringFieldDefinition("Rowset") })) };
		writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), defs,
				RecordWriter.DefaultBufferCapacity,
				RecordWriter.DefaultConcurrentPartialCapacity,
				RecordWriter.DefaultMirrorBufferFactor, 1, TimeUnit.DAYS);
	}

	public void feedIndex(String rowsetFile) throws IOException {
		StringBuilder payload = new StringBuilder();
		FileInputStream fstream = new FileInputStream(rowsetFile);
				//"src/test/resources/ftRowsets_ft2.xml");
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console

			payload.append(strLine);
			if (strLine.startsWith("</ROWSET>")) {
				GenericRecord rec = new GenericRecord();
				try {
					rec.setFields(new Field[] { new StringField(payload
							.toString()) });

					int hours = 1;
					while (!writer.put(rec, 1, TimeUnit.HOURS)) {
						hours++;
						if (hours > 25) {
							writer.close();
							writer = null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				payload = new StringBuilder();
			} else
				payload.append("\n");
		}
		br.close();
	}

	public String getOutput() {
		try {
			URI TCPLocator = Locators.localToTCP(writer.getLocator());
			return TCPLocator.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (Exception e) {
		}

	}

	static String uri = new String();
	static Object sync = new Object();

	public static void main(String[] args) throws Exception {
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(
				"jazzman.di.uoa.gr", new ArrayList<PortRange>(), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());

		(new Thread() {
			public void run() {
				RS2Feed rs2 = null;
				try {
					rs2 = new RS2Feed();
				} catch (GRS2WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				synchronized (sync) {
					if (rs2 != null) {
						uri = rs2.getOutput();
						System.out.println(uri);
					} else {
						System.out.println("rs2 was null");
					}
					sync.notify();
				}
				if (rs2 != null) {
					try {
						rs2.feedIndex("src/test/resources/ftRowsets_ft2.xml");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						rs2.close();
					}
				}
			}
		}).start();

		synchronized (sync) {
			while (uri.isEmpty())
				sync.wait();
		}

		// ForwardReader<GenericRecord> reader = null;
		// reader = new ForwardReader<GenericRecord>(URI.create(uri));
		//
		// int i = 0;
		// while (!(reader.getStatus() == Status.Dispose || (reader.getStatus()
		// == Status.Close && reader.availableRecords() == 0))) {
		// Record rec = reader.get(60, TimeUnit.SECONDS); // XXX TIMEOUT 10mins?
		// System.out.println(++i + " " + rec.getID());
		// }

		try {
			Thread.sleep(100 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
