package org.gcube.data.harmonization.occurrence;

import java.util.Arrays;
import java.util.List;

public class ReadWriteTest {

	private static List<String> occurrenceKeys=Arrays.asList("GBIF:Carcharodon%20carcharias||82||412||51016495",
			"GBIF:Carcharodon%20carcharias||108||563||51195248||",
			"GBIF:Carcharodon%20carcharias||348||13400||59258855||",
			"GBIF:Carcharodon%20carcharias||51||1602||53516957||",
			"Obis:609834-129----");
	
	public static void main (String[] args) throws Exception{
//		GCUBEScope scope=GCUBEScope.getScope("/gcube/devsec");
//		String user = "fabio.sinibaldi";
//		
//		
//		ScopeProvider.instance.set(scope.toString());
//		
//		
//		final OccurrenceReader reader=new CSVReader();
//		final ResultWrapper<OccurrencePoint> wrapper=new ResultWrapper<OccurrencePoint>(scope);
//		final String filePath="/home/fabio/Downloads/Occurrences_239.xml";
//		
//		Thread t=new Thread(){
//			@Override
//			public void run() {
//				InputStream is=null;
//				try {
//					is=new FileInputStream(filePath);
//					reader.streamFile(is,wrapper);
//				} catch (InternalErrorException e) {
//					e.printStackTrace();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}finally{
//					if(is!=null)IOUtils.closeQuietly(is);
//					try{
//						wrapper.close();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}
//		};
//		t.start();
//		
//		Stream<OccurrencePoint> stream=pipe(convert(new URI(wrapper.getLocator())).of(GenericRecord.class).withDefaults()).through(new ResultGenerator<OccurrencePoint>());
//		System.out.println(org.gcube.data.spd.client.formats.DarwinCore.getDarwinCoreFile(stream).getAbsolutePath());
	}
	
}
