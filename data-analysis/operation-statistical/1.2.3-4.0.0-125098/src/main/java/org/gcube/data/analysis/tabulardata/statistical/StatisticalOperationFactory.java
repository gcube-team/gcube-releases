package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableResourceCreatorWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.BooleanParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MapParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

@Singleton
public class StatisticalOperationFactory extends TableResourceCreatorWorkerFactory {

	private static OperationId OPERATION_ID=new OperationId(10001);

	
	public static SimpleStringParameter USER=new SimpleStringParameter("user", "User", "Username", Cardinality.ONE);
	public static SimpleStringParameter ALGORITHM=new SimpleStringParameter("algorithm", "Algorithm", "SM Algorithm to execute", Cardinality.ONE);
	public static MapParameter SM_ENTRIES=new MapParameter("smEntries", "SM Entries", "Input parameters required by SM algorithm", Cardinality.ONE, String.class, Object.class);

	public static SimpleStringParameter DESCRIPTION=new SimpleStringParameter("description", "Description", "Description of the experiment", Cardinality.OPTIONAL);
	public static SimpleStringParameter TITLE=new SimpleStringParameter("title", "Title", "Title of the experiment", Cardinality.OPTIONAL);
	public static BooleanParameter CLEAR_DATASPACE=new BooleanParameter("clear","Clear Dataspace","Remove all generated resources from dataspace",Cardinality.OPTIONAL);
	public static BooleanParameter REMOVE_EXPORTED=new BooleanParameter("remove","Remove Exported","Remove table from dataspace",Cardinality.OPTIONAL);
	
	
	
	
	private static List<Parameter> parameters=new ArrayList<Parameter>();


	static{
		parameters.add(USER);
		parameters.add(ALGORITHM);
		parameters.add(SM_ENTRIES);
		parameters.add(DESCRIPTION);
		parameters.add(TITLE);
		parameters.add(CLEAR_DATASPACE);
		parameters.add(REMOVE_EXPORTED);
		parameters.add(ExportToStatisticalOperationFactory.toEscapeFieldNamesParam);
	}
	
	
	private ExportToStatisticalOperationFactory exportFactory;
	private ImportFromStatisticalOperationFactory importFactory;
	private CubeManager cubeManager;
	
	@Inject
	public StatisticalOperationFactory(
			ExportToStatisticalOperationFactory exportFactory,
			ImportFromStatisticalOperationFactory importFactory,
			CubeManager cubeManager) {
		super();
		this.exportFactory = exportFactory;
		this.importFactory = importFactory;
		this.cubeManager = cubeManager;
	}






	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		StatisticalManagerFactory factory =null;
		Home home=null;
		try{
			factory = Common.getSMFactory();
		}catch (Exception e){
			throw new InvalidInvocationException(invocation,Constants.SERVICE_NOT_FOUND,e);
		}				
		checkSMParameters(factory,invocation);
		String user=(String) invocation.getParameterInstances().get(USER.getIdentifier());
		try{
		home = HomeLibrary.getHomeManagerFactory().getHomeManager()
				.getHome(user);
		}catch(Exception e){
			throw new InvalidInvocationException(invocation,"Unable to contact user's home library",e);
		}
		return new StatisticalOperation(invocation, factory, exportFactory, importFactory, home,cubeManager);
	}

	
	
	


	private static void checkSMParameters(StatisticalManagerFactory factory,OperationInvocation invocation)throws InvalidInvocationException{
		try{
			String algorithmId=OperationHelper.getParameter(ALGORITHM, invocation);
			Map<String,Object> entries=(Map<String, Object>) invocation.getParameterInstances().get(SM_ENTRIES.getIdentifier());
			if(!Common.isSMAlgorithmAvailable(algorithmId)) throw new InvalidInvocationException(invocation, Constants.ALGORITHM_NOT_FOUND);
			SMParameters params=factory.getAlgorithmParameters(algorithmId);
			
			for(SMParameter param:params.list()){
				if(!entries.containsKey(param.name())&&entries.get(param.name())!=null)
					throw new InvalidInvocationException(invocation,"Requested algorithm parameter "+param.name()+" not specified or null.");
			}
		}catch(InvalidInvocationException e){
			throw e;
		}catch(Exception e){
			throw new InvalidInvocationException(invocation, "Unable to check parameters for selected algorithm");
		}
	}




	@Override
	protected String getOperationName() {
		return "Statistical Operation";
	}

	@Override
	protected String getOperationDescription() {
		return "Execute a Statistical Manager experiment against the selected target table";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		String algorithmId=OperationHelper.getParameter(ALGORITHM, invocation);
		return String.format("Execute %s Algorithm",algorithmId);
	}






	@Override
	protected OperationType getOperationType() {
		return OperationType.RESOURCECREATOR;
	}
}
