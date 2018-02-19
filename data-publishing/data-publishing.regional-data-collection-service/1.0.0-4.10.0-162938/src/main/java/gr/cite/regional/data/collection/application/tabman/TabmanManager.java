package gr.cite.regional.data.collection.application.tabman;

import gr.cite.regional.data.collection.application.dtos.TabmanDto;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.Licence;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.*;

import java.util.*;

public final class TabmanManager {
    private static final Logger logger = LogManager.getLogger(TabmanManager.class);

    public static final String ENCODING = "encoding";
    public static final String HASHEADER = "hasHeader";
    public static final String SEPARATOR = "separator";
    public static final String URL = "url";

    private TabmanManager() {}

    public static Task exportDataCollectionToTabman(DataCollection dataCollection, List<String> fieldNames, String scope, String token, String fileURL, TabmanDto tabmanDto) throws NoSuchTabularResourceException, NoSuchOperationException, NoSuchTaskException, InterruptedException {

    String definition = dataCollection.getDataModel().getDefinition();

    ScopeProvider.instance.set(scope);
    SecurityTokenProvider.instance.set(token);

    TabularDataService service = TabularDataServiceFactory.getService();
    TabularResource tabularResource = service.createTabularResource();
    Collection<TabularResourceMetadata<? >> metadata = new ArrayList<>();
    metadata.add(new NameMetadata(dataCollection.getLabel()+ "_" + dataCollection.getId().toString() + ".csv"));
    metadata.add(new AgencyMetadata(tabmanDto.getAgency()));
    if( tabmanDto.getRights() != null )
        metadata.add( new RightsMetadata(tabmanDto.getRights()) );
    if( tabmanDto.getDescription() != null )
        metadata.add( new DescriptionMetadata(tabmanDto.getDescription()) );
    if( tabmanDto.getValidFrom() != null ) {
        Calendar validFromCal = Calendar.getInstance();
        validFromCal.setTime(tabmanDto.getValidFrom());

        metadata.add( new ValidSinceMetadata( validFromCal ) );
    }
    if( tabmanDto.getValidUntiTo() != null ) {
        Calendar validUntilTo = Calendar.getInstance();
        validUntilTo.setTime(tabmanDto.getValidUntiTo());

        metadata.add(new ValidUntilMetadata(validUntilTo));
    }
//		Calendar date;
    metadata.add(new LicenceMetadata(Licence.AttributionNonCommercialShareAlike));
    tabularResource.setAllMetadata(metadata);

    Map<String, Object> parameterInstances = new HashMap<String, Object>();
    parameterInstances.put(URL, fileURL);
    parameterInstances.put(SEPARATOR, ",");
    parameterInstances.put(ENCODING, "UTF-8");
    parameterInstances.put(HASHEADER, true);
    parameterInstances.put("fieldMask", getFieldMaskFromFields(fieldNames));
    parameterInstances.put("skipError",  true);
    Task task = service.execute(new OperationExecution(100, parameterInstances), tabularResource.getId() );

    return taskMonitor(task);
    }

    public static Task exportDataSubmissionToTabman(DataSubmission dataSubmission, List<String> fieldNames, String scope, String token, String fileURL, TabmanDto tabmanDto) throws NoSuchTabularResourceException, NoSuchOperationException, NoSuchTaskException, InterruptedException {
        String definition = dataSubmission.getDataCollection().getDataModel().getDefinition();

        ScopeProvider.instance.set(scope);
        SecurityTokenProvider.instance.set(token);

        TabularDataService service = TabularDataServiceFactory.getService();
        TabularResource tabularResource = service.createTabularResource();
        Collection<TabularResourceMetadata<? >> metadata = new ArrayList<>();
        metadata.add( new NameMetadata("DataSubmission_" + dataSubmission.getId() + ".csv" ) );
        metadata.add( new AgencyMetadata( tabmanDto.getAgency() ) );
        if( tabmanDto.getRights() != null )
            metadata.add( new RightsMetadata(tabmanDto.getRights()) );
        if( tabmanDto.getDescription() != null )
            metadata.add( new DescriptionMetadata(tabmanDto.getDescription()) );
        if( tabmanDto.getValidFrom() != null ) {
            Calendar validFromCal = Calendar.getInstance();
            validFromCal.setTime(tabmanDto.getValidFrom());

            metadata.add( new ValidSinceMetadata( validFromCal ) );
        }
        if( tabmanDto.getValidUntiTo() != null ) {
            Calendar validUntilTo = Calendar.getInstance();
            validUntilTo.setTime(tabmanDto.getValidUntiTo());

            metadata.add(new ValidUntilMetadata(validUntilTo));
        }
//		Calendar date;
        metadata.add(new LicenceMetadata(Licence.AttributionNonCommercialShareAlike));
        tabularResource.setAllMetadata(metadata);

        Map<String, Object> parameterInstances = new HashMap<String, Object>();
        parameterInstances.put(URL, fileURL);
        parameterInstances.put(SEPARATOR, ",");
        parameterInstances.put(ENCODING, "UTF-8");
        parameterInstances.put(HASHEADER, true);
        parameterInstances.put("fieldMask", getFieldMaskFromFields(fieldNames));
        parameterInstances.put("skipError",  true);
        Task task = service.execute(new OperationExecution(100, parameterInstances), tabularResource.getId() );

        return taskMonitor(task);

    }

    private static Task taskMonitor(Task task) throws InterruptedException, NoSuchTaskException {
        while(!task.getStatus().isFinal() ) {
            printTaskDetails(task);
            Thread.sleep(3000);
            TabularDataService service = TabularDataServiceFactory.getService();
            task = service.getTask(task.getId());
        }

        printTaskDetails(task);

        return task;
    }

    private static void printTaskDetails(Task task){
        logger.info("Status final: " + task.getStatus().isFinal());
        logger.info("Status name: " + task.getStatus().name());
        logger.info("Status progress: " + task.getProgress());
        logger.info("Status result: " + task.getResult().toString());
        if(task.getErrorCause() != null)
            logger.info("Status error cause message: " + task.getErrorCause().getMessage());
    }

    private static List<Boolean> getFieldMaskFromFields(List<String> fields) {
        List<Boolean> fieldMask = new ArrayList<Boolean>();
        fields.forEach(f -> fieldMask.add(true));
//		return fields.stream().map(f-> true).collect(Collectors.toList());
        return fieldMask;
    }
}