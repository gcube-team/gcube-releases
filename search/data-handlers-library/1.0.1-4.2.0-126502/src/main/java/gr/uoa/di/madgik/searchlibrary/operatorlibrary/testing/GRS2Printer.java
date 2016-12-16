package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class GRS2Printer {
	private static int tabs = 5;
	private static IRecordReader<Record> reader;

	public static void print(URI uri, int tabs) throws Exception {
		GRS2Printer.tabs = tabs;
		print(uri);
	}

	public static void print(URI uri) throws Exception {
		reader = new ForwardReader<Record>(uri, 2000);
	}
	
	public static void compute() throws GRS2ReaderException {
		RecordDefinition[] defs = reader.getRecordDefinitions();

		RecordDefinition def = defs[0];

		String out = "";

		System.out
				.println("\n---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < def.getDefinitionSize(); i++) {
			out = "- " + def.getDefinition(i).getName() + "(" + def.getDefinition(i).getMimeType() + ")";
			System.out.print(out);
			for (int j = 0; j < tabs - out.length() / 8; j++) {
				System.out.print("\t");
			}
		}
		System.out.println("\n");
		int cnt = 0;
		while (true) {
			try {
				if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
					break;

				Record rec = reader.get(1, TimeUnit.MINUTES);

				if (rec == null) {
					if (reader.getStatus() == Status.Open)
						break;
					else
						continue;
				}
				out = "";
				String line = ""; 
				for (int i = 0; i < def.getDefinitionSize(); i++) {
					if (def.getDefinition(i).getMimeType().equals("text/plain")) {
						if (rec.getField(i).getInputStream() != null)
							out = inputStreamToString(rec.getField(i).getInputStream());
					} else
						out = rec.getField(i).getFieldDefinition().getMimeType();
					out += " ";
					out = out.length() > tabs * 8 ? out.substring(0, (tabs - 1) * 8) + "..." : out;
					line += out;
					for (int j = 0; j < tabs - out.length() / 8; j++)
						line += "\t";
				}
				System.out.println(line);
				cnt++;
				rec.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Total lines: " + cnt);
	}

	public static String inputStreamToString(InputStream inputStream) throws IOException {
		InputStream in = inputStream;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}

		return sb.toString();
	}
}
