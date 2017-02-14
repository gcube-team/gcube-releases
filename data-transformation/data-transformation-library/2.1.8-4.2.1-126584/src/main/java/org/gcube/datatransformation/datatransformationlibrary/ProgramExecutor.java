package org.gcube.datatransformation.datatransformationlibrary;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DTSExceptionWrapper;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.deployer.DTSProgramDeployer;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.Transformer;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;

/**
 * @author Dimitris Katris, NKUA
 *
 * Loads dynamically and executes a {@link Program}.
 */
public class ProgramExecutor {

	/**
	 * Logs operations performed by {@link ProgramExecutor} class.
	 */
	private static Logger log = LoggerFactory.getLogger(ProgramExecutor.class);
	
	/**
	 * If initialized then the program is first deployed and then loaded and executed.
	 */
	private static DTSProgramDeployer deployer;
	
	/**
	 * Initializes the {@link DTSProgramDeployer}.
	 * 
	 * @param DTS_LIBS_PATH The path in which the deployer will put the {@link Program}'s jar files.
	 * @throws Exception If an error occurs in initialization of the {@link DTSProgramDeployer}.
	 */
	public static void initializeDeployer(String DTS_LIBS_PATH) throws Exception{
		deployer = new DTSProgramDeployer(DTS_LIBS_PATH);
	}
	
	/**
	 * Loads and executes the {@link Program} contained in the {@link Transformer}.
	 * 
	 * @param sources The {@link DataSource}s from which the {@link Program} will get {@link DataElement}s.
	 * @param transformer The {@link Transformer} which contains the {@link Program} that will execute the transformationUnit.
	 * @param programParameters The {@link Parameter}s which will be passed to the {@link Program}.
	 * @param targetContentType The {@link ContentType} to which the {@link Program} will transform the {@link DataElement}s.
	 * @param sink The {@link DataSink} in which the {@link Program} will store the transformed {@link DataElement}s.
	 * @throws Exception If an error occurred in loading the {@link Program}.
	 */
	public static void transformDataWithProgram(final ArrayList<DataSource> sources, final Transformer transformer, final List<Parameter> programParameters, final ContentType targetContentType, final DataSink sink) throws Exception {
		if(transformer==null || transformer.getProgramClass()==null || transformer.getProgramClass().trim().length()==0){
			log.error("Transformer not specified");
			Exception e = new Exception("Transformer not specified");
			sink.append(new DTSExceptionWrapper(e));
			throw e;
		}
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if(deployer==null){
			log.warn("DTS Program deployer is not configured. Continuing without using it...");
		}else{
			if(transformer.getSoftwarePackages()!=null && transformer.getSoftwarePackages().size()>0){
				deployer.deployPackages(transformer.getSoftwarePackages());
			}else{
				log.trace("Transformer does not contain any packages");
			}
			cl=deployer.getClassLoader();
		}
		
		try {
			log.info("Going to use the program "+transformer.getProgramClass()+"...");
			final Program program = (Program)Class.forName(transformer.getProgramClass(), true, cl).newInstance();
			Thread transformerThread = new Thread(){
				public void run(){
					Thread.currentThread().setName("Program Executor");
					try{
						log.debug("Program starts transforming data...");
						program.transform(sources, programParameters, targetContentType, sink);
						log.debug("Program finished transforming data...");
					} catch (java.lang.Throwable err) {
						log.error("Uncaught error in program "+transformer.getProgramClass(), err);
						sink.append(new DTSExceptionWrapper(err));
					} finally {
						log.debug("Closing the sink and sources just in case...");
						try {
							if(sink!=null){sink.close();}
							for(DataSource source: sources){
								try {source.close();} catch (Exception e1) {}
							}
						} catch (Exception e1) {}
					}
				}
			};
			transformerThread.setContextClassLoader(cl);
			transformerThread.start();
			return;//Normal procedure. Instantiate the program pass params and returning...
		} catch (java.lang.Throwable err){
			sink.append(new DTSExceptionWrapper(err));
			log.error("Could not invoke "+transformer.getProgramClass(), err);
			throw new Exception("Could not invoke "+transformer.getProgramClass(), err);
		}
	}

}
