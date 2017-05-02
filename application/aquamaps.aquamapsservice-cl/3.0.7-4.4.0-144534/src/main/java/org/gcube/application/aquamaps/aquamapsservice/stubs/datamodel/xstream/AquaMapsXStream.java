package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream;


import java.io.EOFException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BulkItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Cell;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.ItemResources;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ComputationalInfrastructure;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ExecutionEnvironment;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class AquaMapsXStream extends XStream {

	private static AquaMapsXStream XMLinstance=null;
	private static AquaMapsXStream JSONinstance=null;
	
	
	static {
		XMLinstance =new AquaMapsXStream();
		//***Process Annotations			
		XMLinstance.processAnnotations(new Class[]{
				Envelope.class,
				Species.class,
				AquaMapsObject.class,
				Cell.class,
				Job.class,
				Submitted.class,
				Area.class,
				BoundingBox.class,
				Field.class,
				Filter.class,
				Perturbation.class,
				Resource.class,
				ExecutionEnvironment.class,
				ComputationalInfrastructure.class,
				BulkItem.class,
				ItemResources.class
		});
		//***Register Converters
		XMLinstance.registerConverter(new EnvelopeConverter());
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(AquaMapsXStream.class);
	
	private AquaMapsXStream(JettisonMappedXmlDriver jettisonMappedXmlDriver) {
		super(jettisonMappedXmlDriver);
	}

	private AquaMapsXStream() {
		super();
	}
	
	public static AquaMapsXStream getXMLInstance(){return XMLinstance;}
	
//	public static AquaMapsXStream getJSONInstance(){
//		if(JSONinstance==null){
//			JSONinstance =new AquaMapsXStream(new JettisonMappedXmlDriver());
//			JSONinstance.setMode(XStream.NO_REFERENCES);
//			//***Process Annotations			
//			JSONinstance.processAnnotations(new Class[]{
//					Envelope.class,
//					Species.class,
//					AquaMapsObject.class,
//					Cell.class,
//					Job.class,
//					Submitted.class,
//					Area.class,
//					BoundingBox.class,
//					Field.class,
//					Filter.class,
//					Perturbation.class,
//					Resource.class,
//			});
//			//***Register Converters
//			JSONinstance.registerConverter(new EnvelopeConverter());
//		}
//		return JSONinstance;
//	}
	
	
	
	
	
	public static void serialize(String path,Object toSerialize)throws Exception{		
		ObjectOutputStream stream=AquaMapsXStream.getXMLInstance().createObjectOutputStream(new FileWriter(path));
		stream.writeObject(toSerialize);
		stream.flush();
		stream.close();
		logger.debug("Wrote File "+path);
	}

	public static Object deSerialize(String path)throws Exception{
		logger.debug("Loading object from file "+path);
		ObjectInputStream is=null;
		Object toReturn=null;
		try{
			is=AquaMapsXStream.getXMLInstance().createObjectInputStream(new FileReader(path));
			while(true){
				toReturn=is.readObject();
			}
		}catch(EOFException e){
			if(is!=null)is.close();
		}
		if(toReturn==null) throw new Exception("Unable to load object from path "+path+", no objects found");
		else return toReturn;
	}
	
	
	
}
