import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.KeyPair;
import java.util.Calendar;
import java.util.Date;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.PropertyElementGC;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBase;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementBean;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementXBean;
import org.gcube.common.searchservice.searchlibrary.resultset.security.KeyGenerator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceLocalType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSWriterCreationParams;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSXMLWriter;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.*;
/**
 * Test the RS XML
 * @author Konstantinos Tsakalozos
 *
 */
public class RSXMLTester {

	String inFile = null;
	String outFile = null;
	RSResourceType RSType = null;
	RSResourceType MKlocalType = null;
	int recsperpart = 20;
	int bytesperpart = 102400;
	boolean verbose = false;
	boolean makelocal = false;
	/**
	 * @param args see usage to see the args
	 */
	public static void main(String[] args) {

		RSXMLTester tester = new RSXMLTester();
		try{
			tester.PerformTest(args);
		}catch(Exception x){
			x.printStackTrace();
		}
	}

	private void printUsage() {
		System.err.println(
				"Usage: java RSXMLTester [Options] \n" 
				+ "Options:\n" 
				+ "-if <content Filename>\n"
				+ "-of <output Filename>\n"
				+ "-recsperpart <records per part>\n"
				+ "-partsize <bytes per part>\n"
				+ "-verbose \t\t Console output\n"
				+ "-type #id \t\t 0 for RSLocalType (default) or 1 for RSWSRFType\n" 
				+ "-makelocal #id \t\t try make local along with the test selected\n" 
				+ "-endpoint <service URL> \t\t in case of RSWSRFType\n" 
				+ "-test #id \t\t Test to perform \n" +
				"\t\t\t 0 for no extrafunctionality (default) \n" +
				"\t\t\t 1 for new style RS creation \n" +
				"\t\t\t 2 for access leasing test \n" + 
				"\t\t\t 3 for RS Forward test \n" +
				"\t\t\t 4 for time leasing test \n" +
				"\t\t\t 5 for forward+access leasing test (the last reader should remove the RS as he reads it) \n" +
				"\t\t\t 6 for encryption test \n" +
				"\t\t\t 7 for scope test \n" +  
				"\t\t\t 8 for makelocal test \n" +  
				"\t\t\t 9 for clone test\n" +   
				"\t\t\t 10 for pool test\n"   +   
				"\t\t\t 11 for long RS test\n"  +
				"\t\t\t 12 for XStream Bean test\n"    +
				"\t\t\t 13 for Bean test\n"  
		);
	}

	private void PerformTest(String []args){
		try{
			String RStypeID = null;
			String MKlocaltypeID = null;
			String WSRFEndPoint = null;
			String testID = "0";
			for (int i = 0 ; i < args.length; i++){
				if (args[i].equalsIgnoreCase("--help")){
					printUsage();
					System.exit(1);
				}
				if (args[i].equalsIgnoreCase("-if")){
					inFile = args[i+1]; 
				}
				if (args[i].equalsIgnoreCase("-of")){
					outFile = args[i+1]; 
				}
				if (args[i].equalsIgnoreCase("-type")){
					RStypeID = args[i+1];
				}
				if (args[i].equalsIgnoreCase("-endpoint")){
					WSRFEndPoint = args[i+1];
				}
				if (args[i].equalsIgnoreCase("-test")){
					testID = args[i+1];
				}
				if (args[i].equalsIgnoreCase("-recsperpart")){
					recsperpart =  Integer.parseInt(args[i+1]);
				}
				if (args[i].equalsIgnoreCase("-verbose")){
					verbose = true;
				}
				if (args[i].equalsIgnoreCase("-makelocal")){
					MKlocaltypeID = args[i+1];
					makelocal = true;
				}
				if (args[i].equalsIgnoreCase("-partsize")){
					bytesperpart =  Integer.parseInt(args[i+1]);
				}
			}

			RSType = CreateRSResourceType(RStypeID, WSRFEndPoint);
			MKlocalType = CreateMakeLocalRSResourceType(MKlocaltypeID, WSRFEndPoint);

			if (testID.equalsIgnoreCase("0")){
				PlainTest();
			}
			if (testID.equalsIgnoreCase("1")){
				NewCreationTest();
			}
			if (testID.equalsIgnoreCase("2")){
				AccessTest();
			}
			if (testID.equalsIgnoreCase("3")){
				ForwardTest();
			}
			if (testID.equalsIgnoreCase("4")){
				TimeTest();
			}
			if (testID.equalsIgnoreCase("5")){
				EraseTest();
			}
			if (testID.equalsIgnoreCase("6")){
				EncryptionTest();
			}
			if (testID.equalsIgnoreCase("7")){
				ScopeTest();
			}
			if (testID.equalsIgnoreCase("8")){
				MakeLocalTest();
			}
			if (testID.equalsIgnoreCase("9")){
				CloneTest();
			}
			if (testID.equalsIgnoreCase("10")){
				PoolTest();
			}
			if (testID.equalsIgnoreCase("11")){
				LongTest();
			}
			if (testID.equalsIgnoreCase("12")){
				XBeanTest();
			}
			if (testID.equalsIgnoreCase("13")){
				BeanTest();
			}
			System.out.println("Test Passed");

		}catch(Exception x){
			System.out.println("Run with --help paametre to see the commandline options\n");
			x.printStackTrace();
		}

	}


	private void CloneTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		FileWriter fww=new FileWriter(outFile+".1.orig");
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		RSXMLReader newreader = reader.cloneRS();
		fww=new FileWriter(outFile+".1.clone");
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=newreader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!newreader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
//		KeyGenerator kg = new KeyGenerator();
//		KeyPair pair = kg.GenKeyPair();
//		initParams.setPrivKey(pair.getPrivate());
//		initParams.setPubKey(pair.getPublic());
		initParams.setForward(true);
		writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader");
		l = new RSLocator(epr);
//		l.setPrivKey(pair.getPrivate());
		reader=RSXMLReader.getRSXMLReader(l);
		fww=new FileWriter(outFile+".2.orig");
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		newreader = reader.cloneRS();
		fww=new FileWriter(outFile+".2.clone");
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=newreader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!newreader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();
	
	}

	private void MakeLocalTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();


		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();
		

		Print("Make local");
		RSXMLReader newreader = reader.makeLocal(MKlocalType);
		fww = null;
		bww = null;
		fww=new FileWriter(outFile+".local");
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=newreader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!newreader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();
		
		Print("Make enchanced");
		GCUBEScope OK_scope = GCUBEScope.getScope("/gcube/devsec");
		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setAccessReads(45);
		initParams.setForward(true);
		newreader = reader.makeLocal(MKlocalType,initParams,OK_scope);
		fww = null;
		bww = null;
		fww=new FileWriter(outFile+".local.extra");
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=newreader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!newreader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();
		
		
	}

	private void MakeLocalTest(RSXMLReader reader) throws Exception{
		if (!makelocal) return;
		try{
			//Print("In make local "+reader.getRSLocator().getPrivKey().toString());
			RSXMLReader newreader = reader.makeLocal(MKlocalType);
			FileWriter fww = null;
			BufferedWriter bww = null;
			fww=new FileWriter(outFile+".local");
			bww=new BufferedWriter(fww);
			int q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=newreader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!newreader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			
			Print("Make local test failed");
			throw x;
		}
		Print("Make local test was a success");
		reader.getFirstPart();
		return;
	}
	
	private void ScopeTest() throws Exception{
		GCUBEScope OK_scope = GCUBEScope.getScope("/gcube/devsec");
		GCUBEScope WRONG_scope = GCUBEScope.getScope("/gcube/foo");
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();

		String epr=writer.getRSLocator(RSType, OK_scope).getLocator();

		//if (true) return;
		//Reader
		RSLocator l = null;
		RSXMLReader reader = null;
		FileWriter fww = null;
		BufferedWriter bww = null;
		int q=0;

		try{
			Print("Starting rs reader No Scope");
			l = new RSLocator(epr);
			reader = RSXMLReader.getRSXMLReader(l);
			MakeLocalTest(reader);
			fww = new FileWriter(outFile);
			bww = new BufferedWriter(fww);
			q = 0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			Print("Read failed. This is correct!");
		}

		Print("Starting rs re-reader WRONG scope");
		try{
			l = new RSLocator(epr);
			l.setScope(WRONG_scope);
			reader=RSXMLReader.getRSXMLReader(l);
			MakeLocalTest(reader);
			fww=new FileWriter(outFile);
			bww=new BufferedWriter(fww);
			q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			Print("Read failed. This is correct!");
		}

		Print("Starting rs re-reader OK scope");
		l = new RSLocator(epr);
		l.setScope(OK_scope);
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

	}


	private void EncryptionTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		KeyGenerator kg = new KeyGenerator();
		KeyPair pair = kg.GenKeyPair();
		initParams.setPrivKey(pair.getPrivate());
		initParams.setPubKey(pair.getPublic());
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		RSLocator l = null;
		RSXMLReader reader = null;
		FileWriter fww = null;
		BufferedWriter bww = null;
		int q=0;

		try{
			Print("Starting rs reader (1)");
			l = new RSLocator(epr);
			reader = RSXMLReader.getRSXMLReader(l);
			MakeLocalTest(reader);
			fww = new FileWriter(outFile);
			bww = new BufferedWriter(fww);
			q = 0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			Print("Read failed. This is correct!");
		}

		Print("Starting rs re-reader (2)");
		l = new RSLocator(epr);
		l.setPrivKey(pair.getPrivate());
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

	}

	private void EraseTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setAccessReads(2);
		initParams.setForward(true);
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<300;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader (1)");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();
 
		Print("Starting rs re-reader (2)");
		try{
			l = new RSLocator(epr);
			reader=RSXMLReader.getRSXMLReader(l);
			fww=new FileWriter(outFile);
			bww=new BufferedWriter(fww);
			q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			if (RSType instanceof RSResourceWSRFType) {
				Print("Read failed. This is correct!");
			} else {
				Print("Read failed ??????????????");
				throw x;
			}
		}


		try{
			Print("Starting rs re-reader (3)");
			l = new RSLocator(epr);
			reader=RSXMLReader.getRSXMLReader(l);
			fww=new FileWriter(outFile);
			bww=new BufferedWriter(fww);
			q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			Print("Read failed. This is correct!");
		}
		System.out.println("Now check under /tmp/resultset that the newelly" +
		" created RS does not have 300 records");
	}

	private void TimeTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setExpire_date(new Date(Calendar.getInstance().getTimeInMillis() + 60000));
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader (1)");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		try{
			Print("Sleeping!");
			Thread.sleep(60000);
		}catch(Exception ex	){}

		try{
			Print("Starting rs re-reader (2)");
			l = new RSLocator(epr);
			reader=RSXMLReader.getRSXMLReader(l);
			fww=new FileWriter(outFile);
			bww=new BufferedWriter(fww);
			q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			Print("Read failed. This is correct!");
		}
	}



	private void ForwardTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setForward(true);
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader (1)");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs re-reader (2)");
		l = new RSLocator(epr);
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
			try{
				reader.getPreviousPart();
			}catch(Exception x){
				Print("Get previews part failed. This is orrect!");
			}
		}
		Print("parts="+q);
		bww.close();
		fww.close();

	}


	private void AccessTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		initParams.setAccessReads(2);
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader (1)");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs re-reader (2)");
		try{
			l = new RSLocator(epr);
			reader=RSXMLReader.getRSXMLReader(l);
			fww=new FileWriter(outFile);
			bww=new BufferedWriter(fww);
			q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			if (RSType instanceof RSResourceWSRFType) {
				Print("Read failed. This is correct!");
			} else {
				Print("Read failed ??????????????");
				throw x;
			}
		}

		Print("Starting rs re-reader (3)");
		try{
			l = new RSLocator(epr);
			reader=RSXMLReader.getRSXMLReader(l);
			fww=new FileWriter(outFile);
			bww=new BufferedWriter(fww);
			q=0;
			while (true){
				q+=1;
				ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
				Print("ResultLength: " + res.length);
				for(int i=0;i<res.length;i+=1){
					bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
				}
				if(!reader.getNextPart()) break;
			}
			Print("parts="+q);
			bww.close();
			fww.close();
		}catch(Exception x){
			Print("Read failed. This is correct!");
		}

	}


	private void NewCreationTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);

		Print("Starting rs writer");
		RSWriterCreationParams initParams = new RSWriterCreationParams();
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(initParams);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();

		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs re-reader");
		l = new RSLocator(epr);
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

	}


	private void PlainTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);
		FileWriter fw=new FileWriter(outFile);
		BufferedWriter bw=new BufferedWriter(fw);

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
			bw.write(content);
		}
		writer.close();
		bw.close();
		fw.close();
		String epr=writer.getRSLocator(RSType).getLocator();


		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs re-reader");
		l = new RSLocator(epr);
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();


	}

	private void PoolTest() throws Exception{
		// Writer
		
        PoolConfig config = new PoolConfig();
        PoolObjectConfig objConf = new PoolObjectConfig();
        objConf.FlowControl = false;
        objConf.MaxSize = 4;
        objConf.MinSize = 2;

        objConf.ObjectType = RSPoolObject.PoolObjectType.WriterXML;
        objConf.ResourceType = RSPoolObject.PoolObjectResourceType.WSRFType;
        objConf.ServiceEndPoint = null;
        config.add(objConf); 
        // add this object in the pool
//        RSPool pool = new RSWriterFactory(config);
		
        
		String content = ReadContent(inFile);
		FileWriter fw=new FileWriter(outFile);
		BufferedWriter bw=new BufferedWriter(fw);

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<30;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
			bw.write(content);
		}
		writer.close();
		bw.close();
		fw.close();
		String epr=writer.getRSLocator(RSType).getLocator();


		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs re-reader");
		l = new RSLocator(epr);
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();


	}

	
	private String ReadContent(String inputFile) throws Exception{
		FileReader fr = new FileReader(inputFile);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder buf = new StringBuilder();
		String line = br.readLine();
		while(line != null){
			buf.append(line);
			line = br.readLine();
		}
		br.close();
		fr.close();
		return buf.toString();
	}

	private void LongTest() throws Exception{
		// Writer
		String content = ReadContent(inFile);
		FileWriter fw=new FileWriter(outFile);
		BufferedWriter bw=new BufferedWriter(fw);

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<300;i+=1){
			writer.addResults(new ResultElementGeneric("id" + i,"foo",content));	
			bw.write(content);
		}
		writer.close();
		bw.close();
		fw.close();
		String epr=writer.getRSLocator(RSType).getLocator();


		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		FileWriter fww=new FileWriter(outFile);
		BufferedWriter bww=new BufferedWriter(fww);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();

		Print("Starting rs slow re-reader");
		l = new RSLocator(epr);
		reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		fww=new FileWriter(outFile);
		bww=new BufferedWriter(fww);
		q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementGeneric.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				if (i%recsperpart == 0){
					Thread.sleep(2000000);
				}
				bww.write(((ResultElementGeneric)res[i]).getPayload() + "\n");
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
		bww.close();
		fww.close();


	}
	
	private void BeanTest() throws Exception{
		// Writer

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<300;i+=1){
			DemoBean demo = new DemoBean();
			demo.setHello("Hello "+i);
			ResultElementBean bean = new ResultElementBean();
			bean.setBean(demo);
			writer.addResults(bean);	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();


		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementBean.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				ResultElementBean bean = ((ResultElementBean)res[i]);
				DemoBean demo = (DemoBean)bean.getBean();
				Print(demo.getHello());
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
	}

	private void XBeanTest() throws Exception{
		// Writer

		Print("Starting rs writer");
		RSXMLWriter writer=RSXMLWriter.getRSXMLWriter(new PropertyElementBase[]{new PropertyElementGC("foo")});
		writer.setRecsPerPart(recsperpart);
		writer.setPartSize(bytesperpart);
		for(int i=0;i<300;i+=1){
			DemoBean demo = new DemoBean();
			demo.setHello("Hello "+i);
			ResultElementXBean bean = new ResultElementXBean();
			bean.setBean(demo);
			writer.addResults(bean);	
		}
		writer.close();
		String epr=writer.getRSLocator(RSType).getLocator();


		//Reader
		Print("Starting rs reader");
		RSLocator l = new RSLocator(epr);
		RSXMLReader reader=RSXMLReader.getRSXMLReader(l);
		MakeLocalTest(reader);
		int q=0;
		while (true){
			q+=1;
			ResultElementBase[] res=reader.getResults(ResultElementXBean.class);
			Print("ResultLength: " + res.length);
			for(int i=0;i<res.length;i+=1){
				ResultElementXBean bean = ((ResultElementXBean)res[i]);
				DemoBean demo = (DemoBean)bean.getBean();
				Print(demo.getHello());
			}
			if(!reader.getNextPart()) break;
		}
		Print("parts="+q);
	}


	
	private RSResourceType CreateMakeLocalRSResourceType(String stypeID, String endPoint)  throws Exception {
		if (stypeID == null)
			return new RSResourceLocalType();

		if (stypeID.equalsIgnoreCase("0"))
			return new RSResourceLocalType();
		else if (stypeID.equalsIgnoreCase("1"))
			return new RSResourceWSRFType(endPoint);
		else  //default case
			return new RSResourceLocalType();

	}


	
	private RSResourceType CreateRSResourceType(String stypeID, String endPoint) throws Exception {
		if (stypeID == null)
			return new RSResourceLocalType();

		if (stypeID.equalsIgnoreCase("0"))
			return new RSResourceLocalType();
		else if (stypeID.equalsIgnoreCase("1"))
			return new RSResourceWSRFType(endPoint);
		else  //default case
			return new RSResourceLocalType();

	}

	private void Print(String string) {
		if (verbose)
			System.out.println(string);
	}

}

