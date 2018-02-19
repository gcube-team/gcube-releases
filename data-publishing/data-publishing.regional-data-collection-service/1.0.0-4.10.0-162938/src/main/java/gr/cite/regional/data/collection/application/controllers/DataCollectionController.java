package gr.cite.regional.data.collection.application.controllers;

import gr.cite.regional.data.collection.application.core.EntityDtoMapper;
import gr.cite.regional.data.collection.application.dtos.*;
import gr.cite.regional.data.collection.application.tabman.TabmanManager;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdProcessing;
import gr.cite.regional.data.collection.dataaccess.dsd.Field;
import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataaccess.services.DataCollectionService;
import gr.cite.regional.data.collection.dataaccess.services.DataSubmissionService;
import gr.cite.regional.data.collection.dataaccess.services.UserReferenceService;
import gr.cite.regional.data.collection.dataaccess.types.DataSubmissionStatusType;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
@RequestMapping("/" + DataCollectionController.DATA_COLLECTION_ENDPOINT)
public class DataCollectionController extends BaseController {
	private static final Logger logger = LogManager.getLogger(DataCollectionController.class);
	static final String DATA_COLLECTION_ENDPOINT = "dataCollections";
	private static final String DATA_COLLECTION_AS_CSV_FILE_ENDPOINT = "{id}/dataSubmissions/data/file";
	
	private String hostname;
	private EntityDtoMapper entityDtoMapper;
	private DataCollectionService dataCollectionService;
	private DataSubmissionService dataSubmissionService;
	private DsdProcessing dsdProcessing;
	private UserReferenceService userReferenceService;

	@Autowired
	public DataCollectionController(DataCollectionService dataCollectionService, DataSubmissionService dataSubmissionService, DsdProcessing dsdProcessing,
									String hostname, EntityDtoMapper entityDtoMapper, UserReferenceService userReferenceService) {
		this.dataCollectionService = dataCollectionService;
		this.dataSubmissionService = dataSubmissionService;
		this.hostname = hostname;
		this.entityDtoMapper = entityDtoMapper;
		this.dsdProcessing = dsdProcessing;
		this.userReferenceService = userReferenceService;
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DataCollectionDto>> getDataCollections(
			@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate,
            @RequestParam(value = "domain", required = false) String domainLabel,
			HttpServletRequest request ) throws ServiceException {
		logger.debug("Get DataCollections");

		String scope = request.getHeader("gcube-user-scope");

		if(scope != null) {
			UserReference userReference = new UserReference();
			userReference.setEmail(request.getHeader("userEmail"));
			userReference.setFullName(request.getHeader("userFullname"));
			userReference.setLabel(request.getHeader("username"));
			userReference.setUri(scope);
			this.userReferenceService.createUserReferenceIfNotExists(userReference);
			domainLabel = scope;
		}
		
		List<DataCollection> dataCollections;
		// TODO filter with queryParams
		if (domainLabel != null) {
			dataCollections = this.dataCollectionService.getDataCollectionsByDomain(domainLabel);
		} else if (label != null) {
			dataCollections = this.dataCollectionService.getDataCollectionByLabel(label);
		} else {
			dataCollections = this.dataCollectionService.getAllDataCollections();
		}
		
		List<DataCollectionDto> dataCollectionDtos = this.entityDtoMapper.entitiesToDtos(dataCollections, DataCollectionDto.class);
		return ResponseEntity.ok(dataCollectionDtos);
	}
	
	@RequestMapping(value = "/{id}/dataSubmissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DataSubmissionDto>> getDataSubmissionsOfDataCollection(@PathVariable("id") Integer id) throws ServiceException {
		logger.debug("Get DataSubmissions of DataCollection " + id);
		
		//Set<DataSubmission> dataSubmissions = this.dataCollectionService.getDataCollectionDataSubmissions(id);
		List<DataSubmission> dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionId(id);
		List<DataSubmissionDto> dataSubmissionDtos = this.entityDtoMapper.entitiesToDtos(dataSubmissions, DataSubmissionDto.class);
		
		return ResponseEntity.ok(dataSubmissionDtos);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataCollectionDto> getDataCollection(@PathVariable("id") Integer id) {
		logger.debug("Get DataCollection " + id);
		
		DataCollection dataCollection = this.dataCollectionService.getDataCollection(id);
		return dataCollection == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(this.entityDtoMapper.entityToDto(dataCollection, DataCollectionDto.class));
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addDataCollection(@RequestBody DataCollectionDto dataCollectionDto) throws ServiceException {
		logger.debug("Add DataCollection");
		
		DataCollection dataCollectionEntity = this.entityDtoMapper.dtoToEntity(dataCollectionDto, DataCollection.class);
		dataCollectionEntity = this.dataCollectionService.addDataCollection(dataCollectionEntity);
		
		//URI location = UriComponentsBuilder.fromHttpUrl(this.hostname).pathSegment(DataCollectionController.DATA_COLLECTION_ENDPOINT, "{id}")
		//		.buildAndExpand(dataCollectionEntity.getId()).toUri();
		//
		//return ResponseEntity.created(location).body("Data Collection " + dataCollectionEntity.getLabel() + " [" + dataCollectionEntity.getId() + "] successfully created");
		return ResponseEntity.ok("Data Collection " + dataCollectionEntity.getLabel() + " [" + dataCollectionEntity.getId() + "] successfully created");
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataCollectionDto> updateDataCollection(@PathVariable("id") Integer id, @RequestBody DataCollectionDto dataCollectionDto) throws ServiceException {
		logger.debug("Update DataCollection");
		
		dataCollectionDto.setId(id);
		
		DataCollection dataCollectionEntity = this.dataCollectionService.updateDataCollection(this.entityDtoMapper.dtoToEntity(dataCollectionDto, DataCollection.class));
		return ResponseEntity.ok(this.entityDtoMapper.entityToDto(dataCollectionEntity, DataCollectionDto.class));
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteDataCollection(@PathVariable("id") Integer id) throws ServiceException {
		logger.debug("Delete DataCollection " + id);
		
		this.dataCollectionService.deleteDataCollection(id);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "/{id}/tabman/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity importToTabman(@PathVariable("id") Integer id, @RequestBody TabmanDto tabmanDto, HttpServletRequest request) throws ServiceException {
		logger.debug("Importing dataCollection " + id + " to tabman");
		Task task;
		DataCollection dataCollection;

		String scope = request.getHeader("scope");
		logger.debug("Scope: " + scope);
		String token = request.getHeader("token");
		String host = request.getHeader("discovered_host");
		byte[] encodedBytes = host.getBytes();
		String decodedHost = new String(Base64.getDecoder().decode(host));
		String fileURL = decodedHost + DATA_COLLECTION_ENDPOINT + "/" + DATA_COLLECTION_AS_CSV_FILE_ENDPOINT;
		List<DataSubmission> dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionId(id);
		if( !dataSubmissions.isEmpty()) {
			if( dataSubmissions.stream().filter(ds -> {
				return ds.getStatus() == DataSubmissionStatusType.VALIDATED.code();
			}).collect(Collectors.toList()).isEmpty() ) {
				throw new ServiceException("Export to Tabman aborted, no Data Submission is validated!");
			}
		}

		dataCollection = this.dataCollectionService.getDataCollection(id);

		try {
			List<String> fieldNames = this.getFieldNamesOrdered(dataCollection.getDataModel().getDefinition());
			task = TabmanManager.exportDataCollectionToTabman(dataCollection, fieldNames, scope, token, fileURL.replace("{id}", id.toString()), tabmanDto);
		} catch (NoSuchTabularResourceException | NoSuchOperationException | NoSuchTaskException | InterruptedException e) {
			e.printStackTrace();
			throw new ServiceException("An error occurred while exporting data collection [" + id + "] to tabman");
		}

		if(!task.getStatus().name().equals(TaskStatus.SUCCEDED.name()))
			throw new ServiceException("Export to tabman for Data Collection [" + id + "]: " + task.getStatus().name() + ", " + task.getErrorCause().getMessage());
		else {
			try {
				this.updateDataCollectionForSuccessfulTabmanExport(dataCollection, task);
			} catch (JAXBException e) {
				logger.error("Error on updating Data Collection [" + dataCollection.getId() + "] for successful Tabman export", e);
				e.printStackTrace();
			}
		}

		return ResponseEntity.ok("Export to tabman for Datacollection [" + id + "]: " + task.getStatus().name());
	}

	@RequestMapping(value = "/{id}/dataSubmissions/data", method = RequestMethod.GET, produces = "text/csv")
	public ResponseEntity<String> getDataCollectionSubmissionsAsCsv(@PathVariable("id") Integer id) throws ServiceException {
		List<DataSubmission> dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionId(id, true);
		DataCollection dataCollection = this.dataCollectionService.getDataCollection(id);

		List<String> fieldNames = getFieldNamesOrdered(dataCollection.getDataModel().getDefinition());
		List<Cdt> dataSubmissionsData = new ArrayList<>();
		dataSubmissions.forEach(dataSubmission -> dataSubmissionsData.addAll(dataSubmission.getData()));

		String csv = constructDataSubmissionCsv(fieldNames, dataSubmissionsData);

		return ResponseEntity.ok(csv);
	}

	@RequestMapping(value = "/{id}/dataSubmissions/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataCollectionDataDto> getDataCollectionSubmissionsJSON(@PathVariable("id") Integer id) throws ServiceException {
		List<DataSubmission> dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionId(id, true);
		DataCollection dataCollection = this.dataCollectionService.getDataCollection(id);

		List<String> headers = getFieldNamesOrdered(dataCollection.getDataModel().getDefinition());
		List<Cdt> dataSubmissionsData = new ArrayList<>();
		dataSubmissions.forEach(dataSubmission -> dataSubmissionsData.addAll(dataSubmission.getData()));

		DataCollectionDataDto dataCollectionData = new DataCollectionDataDto();
		dataCollectionData.setAttributes(dataCollection.getAttributes());
		dataCollectionData.setHeaders(headers);
		dataCollectionData.setData(this.entityDtoMapper.entitiesToDtos(dataSubmissionsData, CdtDto.class));

		return ResponseEntity.ok(dataCollectionData);
	}

	@RequestMapping(value = "/"+DATA_COLLECTION_AS_CSV_FILE_ENDPOINT, method = RequestMethod.GET, produces = "text/csv")
	public void getFileOfDataSubmissions(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		List<DataSubmission> dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionId(id, true);
		DataCollection dataCollection = this.dataCollectionService.getDataCollection(id);

		List<String> fieldNames = getFieldNamesOrdered(dataCollection.getDataModel().getDefinition());
		List<Cdt> dataSubmissionsData = new ArrayList<>();
		dataSubmissions.stream().filter(dataSubmission -> {
			return dataSubmission.getStatus() == DataSubmissionStatusType.VALIDATED.code();
		}).forEach(dataSubmission -> dataSubmissionsData.addAll(dataSubmission.getData()));

		String csv = constructDataSubmissionCsv(fieldNames, dataSubmissionsData);

		File file = new File( dataCollection.getLabel() + dataCollection.getId().toString() + ".csv" );
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(csv);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("An error occurred while creating the csv file");
		}

		response.setContentType("text/csv");
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ServiceException("An error occurred while creating the stream for the csv file");
		}

		try {
			input = new BufferedInputStream(fis);
			output = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[1048576];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} catch (IOException e) {
			logger.error("There are errors in reading/writing the stream " + e.getMessage());
//			throw new ServiceException("An error occurred while creating the stream for the csv file");
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			if (input != null)
				try {
					input.close();
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
		}
	}

	private List<String> getFieldNamesOrdered(String dsd) {
		return this.dsdProcessing.getDefinitionForExcelAddIn(dsd).getFields().stream()
				.sorted(Comparator.comparingInt(Field::getOrder)).map(Field::getLabel).collect(Collectors.toList());
	}

	private String constructDataSubmissionCsv(List<String> fieldNames, List<Cdt> data) {
		String csvHeaders = fieldNames.stream().collect(Collectors.joining(",", "", "\n"));
		String csvData = data.stream()
				.map(cdt -> fieldNames.stream().map(field -> cdt.getData().get(field).toString()).collect(Collectors.joining(",")))
				.collect(Collectors.joining("\n"));
		return csvHeaders + csvData;
	}

	private void updateDataCollectionForSuccessfulTabmanExport( DataCollection dataCollection, Task task ) throws JAXBException, ServiceException {
		TabmanInfoDto tabmanInfo = new TabmanInfoDto();
		tabmanInfo.setExportDate( Date.from( Instant.now() ) );
		tabmanInfo.setResourceId( task.getTabularResourceId().getValue() );

		AttributesDto attributes = new AttributesDto();
		if( dataCollection.getAttributes() != null )
			attributes = AttributesDto.fromXml(dataCollection.getAttributes());

		attributes.setTabmanInfo(tabmanInfo);

		DataCollection dataCollectionWithTabmanInfo = new DataCollection();
		dataCollectionWithTabmanInfo.setId( dataCollection.getId() );

		dataCollectionWithTabmanInfo.setAttributes( AttributesDto.toXml(attributes) );

		this.dataCollectionService.updateDataCollection( dataCollectionWithTabmanInfo );
	}

}
