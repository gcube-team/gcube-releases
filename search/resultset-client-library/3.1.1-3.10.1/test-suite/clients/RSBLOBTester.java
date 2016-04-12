import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;

import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBLOBGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.security.KeyGenerator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBIterator;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSBLOBWriter;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSWriterCreationParams;

/**
 * Testing RS BLOB client.
 * 
 * @author Konstantinos Tsakalozos
 */
public class RSBLOBTester {

	String inFile = null;
	String outFile = null;
	RSResourceType RSType = null;
	RSResourceType MKlocalType = null;
	int recsperpart = 20;
	int bytesperpart = 102400;
	boolean verbose = false;
	boolean makelocal = false;

	/**
	 * Main function.
	 * @param args	type --help to see the options
	 */
	public static void main(String[] args) {

		RSBLOBTester tester = new RSBLOBTester();
		try {
			tester.PerformTest(args);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	/**
	 * Print Usage
	 */
	private void printUsage() {
		System.err
				.println("Usage: java RSBLOBTester [Options] \n"
						+ "Options:\n"
						+ "-of <output Filename>\n"
						+ "-verbose \t\t Console output\n"
						+ "-type #id \t\t 0 for RSLocalType (default) or 1 for RSWSRFType\n"
						+ "-makelocal #id \t\t 0 for RSLocalType (default) or 1 for RSWSRFType \n"
						+ " \t\t\t try make local along with the test selected\n"
						+ "-endpoint <service URL> \t\t in case of RSWSRFType\n"
						+ "-test #id \t\t Test to perform \n"
						+ "\t\t\t 0 for no extra functionality (default) \n"
						+ "\t\t\t 1 for new style RS creation \n"
						+ "\t\t\t 2 for access leasing test \n"
						+ "\t\t\t 3 for encryption test \n"
						+ "\t\t\t 4 for encoding test \n");
	}

	/**
	 * Performs the tests after parsing args
	 * @param args test arguments
	 */
	private void PerformTest(String[] args) {
		try {
			String RStypeID = null;
			String MKlocaltypeID = null;
			String WSRFEndPoint = null;
			String testID = "0";
			for (int i = 0; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("--help")) {
					printUsage();
					System.exit(1);
				}
				Print("Test ID" + testID);
				if (args[i].equalsIgnoreCase("-of")) {
					outFile = args[i + 1];
				}
				if (args[i].equalsIgnoreCase("-type")) {
					RStypeID = args[i + 1];
				}
				if (args[i].equalsIgnoreCase("-endpoint")) {
					WSRFEndPoint = args[i + 1];
				}
				if (args[i].equalsIgnoreCase("-test")) {
					testID = args[i + 1];
					Print("Test ID" + testID);
				}
				if (args[i].equalsIgnoreCase("-verbose")) {
					verbose = true;
				}
				if (args[i].equalsIgnoreCase("-makelocal")) {
					MKlocaltypeID = args[i + 1];
					makelocal = true;
				}
			}

			RSType = CreateRSResourceType(RStypeID, WSRFEndPoint);
			MKlocalType = CreateMakeLocalRSResourceType(MKlocaltypeID,
					WSRFEndPoint);

			if (testID.equalsIgnoreCase("0")) {
				PlainTest();
			}
			if (testID.equalsIgnoreCase("1")) {
				NewCreationTest();
			}
			if (testID.equalsIgnoreCase("2")) {
				AccessTest();
			}
			if (testID.equalsIgnoreCase("3")) {
				EncryptionTest();
			}
			if (testID.equalsIgnoreCase("4")) {
				EncodingTest();
			}
			System.out.println("Test Passed");

		} catch (Exception x) {
			System.out
					.println("Run with --help paametre to see the commandline options\n");
			x.printStackTrace();
		}

	}

	/**
	 * Test the Encoding
	 * @throws Exception
	 */
	private void EncodingTest() throws Exception {
		// Writer
		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setAccessReads(100);
		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter(initParams);
		fillRS(writer);

		String epr = writer.getRSLocator(RSType).getLocator();

		Print(epr);
		// Reader
		Print("Starting rs re-reader ");
		RSLocator l = new RSLocator(epr);
		RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(l);
		// persistRS(reader);

		Print("Making local");
		persistRS(reader.makeLocal(MKlocalType));

	}

	/**
	 * Test the encryption
	 * @throws Exception
	 */
	private void EncryptionTest() throws Exception {
		// Writer
		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		KeyGenerator kg = new KeyGenerator();
		KeyPair pair = kg.GenKeyPair();
		initParams.setPrivKey(pair.getPrivate());
		initParams.setPubKey(pair.getPublic());
		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter(initParams);
		fillRS(writer);

		String epr = writer.getRSLocator(RSType).getLocator();

		// Reader
		Print("Starting rs re-reader (2)");
		RSLocator l = new RSLocator(epr);
		l.setPrivKey(pair.getPrivate());
		RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(l);
		// persistRS(reader);

		Print("Making local");
		persistRS(reader.makeLocal(MKlocalType));

	}

	/**
	 * Test the Access leasing
	 * @throws Exception
	 */
	private void AccessTest() throws Exception {
		// Writer
		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setAccessReads(3);
		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter(initParams);
		Print("Writer access : " + writer.getAccessLeasing());
		fillRS(writer);
		String epr = writer.getRSLocator(RSType).getLocator();

		Print(epr);

		// Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		// Print("Reader access : "+ reader.getAccessLeasing());
		RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(l);
		persistRS(reader.makeLocal(MKlocalType));

		try {
			Print("Starting rs reader");
			l = new RSLocator(epr);
			// Print("Reader access : "+ reader.getAccessLeasing());
			reader = RSBLOBReader.getRSBLOBReader(l);
			persistRS(reader.makeLocal(MKlocalType));
		} catch (Exception e) {
			Print("Read failed. This is correct!");
		}
		/*
		 * if (RSType instanceof RSResourceWSRFType) {
		 * Print("Read failed. This is correct!"); } else {
		 * Print("Read failed ??????????????"); throw x; }
		 */

	}

	/**
	 * Test new way of creation
	 * @throws Exception
	 */
	private void NewCreationTest() throws Exception {
		// Writer
		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter(initParams);
		fillRS(writer);

		String epr = writer.getRSLocator(RSType).getLocator();

		// Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(l);
		persistRS(reader.makeLocal(MKlocalType));

	}

	/**
	 * Test old style RS
	 * @throws Exception
	 */
	private void PlainTest() throws Exception {
		// Writer
		// String content = ReadContent(inFile);
		Print("Starting rs writer");
		RSBLOBWriter writer = RSBLOBWriter
				.getRSBLOBWriter(new PropertyElementBase[] { new PropertyElementGC(
						"foo") });
		fillRS(writer);

		String epr = writer.getRSLocator(RSType).getLocator();

		// Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSBLOBReader reader = RSBLOBReader.getRSBLOBReader(l);
		persistRS(reader.makeLocal(MKlocalType));

	}

	/**
	 * Write RS to file
	 * @param reader the RS reader to read from
	 * @throws Exception when could not persist
	 */
	public void persistRS(RSBLOBReader reader) throws Exception{
		RSBLOBIterator iterator;
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(outFile));

			iterator = reader.getRSIterator();
			int cnt = 0;
			while (iterator.hasNext()) {
				ResultElementBLOBGeneric blob = (ResultElementBLOBGeneric) iterator
						.next(ResultElementBLOBGeneric.class);

				if (blob != null) {
					Print("ID: "
							+ (blob
									.getRecordAttributes(ResultElementBLOBGeneric.RECORD_ID_NAME)[0])
									.getAttrValue());
					Print("FromID: "
							+ (blob
									.getRecordAttributes(ResultElementBLOBGeneric.RECORD_COLLECTION_NAME)[0])
									.getAttrValue());
					InputStream istream = blob.getContentOfBLOB();
					streamToFile(istream, out);
					istream.close();
					istream = null;
				} else {
					Print("To "
							+ cnt
							+ " blob pou epistrefei o iterator einai null. PROSEKSE kai auto...");
				}
				cnt++;
			}
			out.close();
			out = null;

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Place the input stream to a file.
	 * @param instream input stream
	 * @param out output stream
	 * @throws Exception writing failed
	 */
	public void streamToFile(InputStream instream, OutputStream out)
			throws Exception {
		try {
			byte[] buf = new byte[8096];
			int len;
			int sum = 0;
			while ((len = instream.read(buf)) >= 0) {
				sum += len;
				out.write(buf, 0, len);
			}

		} catch (Exception e) {
			if (instream != null)
				instream.close();
			if (out != null)
				out.close();
			Print("Could not persist stream. Throwing Exception");
			throw e;
		}
	}

	/**
	 * Write data to the RS
	 * @param writer the writer to fill
	 * @throws Exception when could not fill the RS
	 */
	public void fillRS(RSBLOBWriter writer) throws Exception {
		byte[] barray = new byte[1024];
		for (int j = 0; j < 1024; j++)
			barray[j] = 'a';

		for (int i = 0; i < 1; i++) {
			ByteArrayInputStream instream = new ByteArrayInputStream(barray);
			ResultElementBLOBGeneric blob = new ResultElementBLOBGeneric(String
					.valueOf(i), "colid", null, instream);
			writer.addResults(blob);
		}
		writer.close();
	}

	/**
	 * Make a RS local.
	 * @param stypeID is it going to be local or remote
	 * @param endPoint the end point
	 * @return the new RS type
	 * @throws Exception
	 */
	private RSResourceType CreateMakeLocalRSResourceType(String stypeID,
			String endPoint) throws Exception {
		if (stypeID == null)
			return new RSResourceLocalType();

		if (stypeID.equalsIgnoreCase("0"))
			return new RSResourceLocalType();
		else if (stypeID.equalsIgnoreCase("1"))
			return new RSResourceWSRFType(endPoint);
		else
			// default case
			return new RSResourceLocalType();

	}

	/**
	 * Create an RS 
	 * @param stypeID is it going to be local or remote
	 * @param endPoint the end point
	 * @return the new RS type
	 * @throws Exception
	 */
	private RSResourceType CreateRSResourceType(String stypeID, String endPoint)
			throws Exception {
		if (stypeID == null)
			return new RSResourceLocalType();

		if (stypeID.equalsIgnoreCase("0"))
			return new RSResourceLocalType();
		else if (stypeID.equalsIgnoreCase("1"))
			return new RSResourceWSRFType(endPoint);
		else
			// default case
			return new RSResourceLocalType();

	}

	/**
	 * Print when we need a verbose output
	 * @param string string to print
	 */
	private void Print(String string) {
		if (verbose)
			System.out.println(string);
	}

}
