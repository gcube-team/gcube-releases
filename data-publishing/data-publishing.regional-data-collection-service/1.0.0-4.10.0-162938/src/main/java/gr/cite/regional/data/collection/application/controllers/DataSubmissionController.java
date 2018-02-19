package gr.cite.regional.data.collection.application.controllers;

import gr.cite.regional.data.collection.application.core.EntityDtoMapper;
import gr.cite.regional.data.collection.application.dtos.AttributesDto;
import gr.cite.regional.data.collection.application.dtos.CdtDto;
import gr.cite.regional.data.collection.application.dtos.DataSubmissionDataDto;
import gr.cite.regional.data.collection.application.dtos.DataSubmissionDto;
import gr.cite.regional.data.collection.application.dtos.TabmanDto;
import gr.cite.regional.data.collection.application.dtos.TabmanInfoDto;
import gr.cite.regional.data.collection.application.tabman.TabmanManager;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdProcessing;
import gr.cite.regional.data.collection.dataaccess.dsd.Field;
import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.services.DataSubmissionService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
@RequestMapping("/" + DataSubmissionController.DATA_SUBMISSION_ENDPOINT)
public class DataSubmissionController extends BaseController {
	static final String DATA_SUBMISSION_ENDPOINT = "dataSubmissions";
	private static final String DATA_SUBMISSION_AS_CSV_FILE_ENDPOINT = "{id}/data/file";
	private static final Logger logger = LogManager.getLogger(DataSubmissionController.class);
	
	private String hostname;
	private EntityDtoMapper entityDtoMapper;
	
	private DsdProcessing dsdProcessing;
	private DataSubmissionService dataSubmissionService;
	
	@Autowired
	public DataSubmissionController(DataSubmissionService dataSubmissionService, DsdProcessing dsdProcessing, String hostname, EntityDtoMapper entityDtoMapper) {
		this.hostname = hostname;
		this.entityDtoMapper = entityDtoMapper;
		
		this.dataSubmissionService = dataSubmissionService;
		this.dsdProcessing = dsdProcessing;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataSubmissionDto> addDataSubmission(@RequestBody DataSubmissionDto dataSubmissionDto) throws ServiceException {
		//AttributesDto attributesDto = dataSubmissionDto.getAttributes();
		DataSubmission dataSubmissionEntity = this.entityDtoMapper.dtoToEntity(dataSubmissionDto, DataSubmission.class);
		
		//dataSubmissionEntity.setAttributes(serializeAttributesToXml(attributesDto));
		
		dataSubmissionEntity = this.dataSubmissionService.addDataSubmission(dataSubmissionEntity);
		
		/*URI location = UriComponentsBuilder.fromHttpUrl(this.hostname).pathSegment(DataSubmissionController.DATA_SUBMISSION_ENDPOINT, "{id}")
				.buildAndExpand(dataSubmissionEntity.getId()).toUri();
		return ResponseEntity.created(location).body("Data Submission [" + dataSubmissionEntity.getId() + "] successfully inserted");*/
		
		DataSubmissionDto newDataSubmission = new DataSubmissionDto();
		newDataSubmission.setId(dataSubmissionEntity.getId());
		return ResponseEntity.ok(newDataSubmission);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> updateDataSubmission(@PathVariable("id") Integer id, @RequestBody DataSubmissionDto dataSubmissionDto) throws ServiceException {
		dataSubmissionDto.setId(id);
		//AttributesDto attributesDto = dataSubmissionDto.getAttributes();
		DataSubmission dataSubmissionEntity = this.entityDtoMapper.dtoToEntity(dataSubmissionDto, DataSubmission.class);
		//dataSubmissionEntity.setAttributes(serializeAttributesToXml(attributesDto));
		
		dataSubmissionEntity = this.dataSubmissionService.updateDataSubmissionAndData(dataSubmissionEntity);
		
		/*URI location = UriComponentsBuilder.fromHttpUrl(this.hostname).pathSegment(DataSubmissionController.DATA_SUBMISSION_ENDPOINT, "{id}")
				.buildAndExpand(dataSubmissionEntity.getId()).toUri();
		return ResponseEntity.created(location).body("Data Submission [" + dataSubmissionEntity.getId() + "] successfully replaced");*/
		return ResponseEntity.ok("Data Submission [" + dataSubmissionEntity.getId() + "] successfully replaced");
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataSubmissionDto> getDataSubmission(@PathVariable("id") Integer id) throws ServiceException {
		DataSubmission dataSubmission = this.dataSubmissionService.getDataSubmission(id);
		return ResponseEntity.ok(this.entityDtoMapper.entityToDto(dataSubmission, DataSubmissionDto.class));
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DataSubmissionDto>> getDataSubmissions(@RequestParam(name = "dataCollection", required = false) Integer dataCollectionId,
																	  @RequestParam(name = "owner", required = false) Integer ownerId) throws ServiceException {
		
		List<DataSubmission> dataSubmissions;
		if (dataCollectionId == null) {
			dataSubmissions = this.dataSubmissionService.getAllDataSubmissions();
		} else if (ownerId == null) {
			dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionId(dataCollectionId);
		} else {
			dataSubmissions = this.dataSubmissionService.getDataSubmissionsByDataCollectionAndOwner(dataCollectionId, ownerId);
		}
		dataSubmissions.forEach(dataSubmission -> dataSubmission.getDataCollection().setDataModel(null));
		
		return ResponseEntity.ok(this.entityDtoMapper.entitiesToDtos(dataSubmissions, DataSubmissionDto.class));
	}
	
	@RequestMapping(value = "/{id}/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataSubmissionDataDto> getDataSubmissionData(@PathVariable("id") Integer id) throws ServiceException {
		DataSubmission dataSubmission = this.dataSubmissionService.getDataSubmission(id);
		
		List<String> headers = getFieldNamesInOrder(dataSubmission.getDataCollection().getDataModel().getDefinition());
		
		DataSubmissionDataDto dataSubmissionData = new DataSubmissionDataDto();
		dataSubmissionData.setHeaders(headers);
		dataSubmissionData.setData(this.entityDtoMapper.entitiesToDtos(dataSubmission.getData(), CdtDto.class));
		
		return ResponseEntity.ok(dataSubmissionData);
	}
	
	@RequestMapping(value = "/{id}/data", method = RequestMethod.GET, produces = "text/csv")
	public ResponseEntity<String> getDataSubmissionAsCsv(@PathVariable("id") Integer id) throws ServiceException {
		DataSubmission dataSubmission = this.dataSubmissionService.getDataSubmission(id);
		
		List<String> fieldNames = getFieldNamesInOrder(dataSubmission.getDataCollection().getDataModel().getDefinition());
		
		String csv = constructDataSubmissionCsv(fieldNames, dataSubmission.getData());
		
		return ResponseEntity.ok(csv);
	}
	
	@RequestMapping(value = "/" + DATA_SUBMISSION_AS_CSV_FILE_ENDPOINT, method = RequestMethod.GET, produces = "text/csv")
	public void getFileOfDataSubmissions(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		DataSubmission dataSubmission = this.dataSubmissionService.getDataSubmission(id);
		
		if (dataSubmission.getStatus() != DataSubmissionStatusType.VALIDATED.code()) {
			throw new ServiceException("Non validated file");
		}
		
		List<String> fieldNames = getFieldNamesInOrder(dataSubmission.getDataCollection().getDataModel().getDefinition());
		
		String csv = constructDataSubmissionCsv(fieldNames, dataSubmission.getData());
		
		File file = new File("DataSubmission" + dataSubmission.getId().toString() + ".csv");
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
		String fileURL = decodedHost + DATA_SUBMISSION_ENDPOINT + "/" + DATA_SUBMISSION_AS_CSV_FILE_ENDPOINT;
		logger.debug("fileURL: " + fileURL);
		DataSubmission dataSubmission = this.dataSubmissionService.getDataSubmission(id);
		if (dataSubmission.getStatus() != DataSubmissionStatusType.VALIDATED.code()) {
			throw new ServiceException("The submission is not validated");
		}
		
		try {
			List<String> fieldNames = this.getFieldNamesInOrder(dataSubmission.getDataCollection().getDataModel().getDefinition());
			task = TabmanManager.exportDataSubmissionToTabman(dataSubmission, fieldNames, scope, token, fileURL.replace("{id}", id.toString()), tabmanDto);
		} catch (NoSuchTabularResourceException | NoSuchOperationException | NoSuchTaskException | InterruptedException e) {
			e.printStackTrace();
			throw new ServiceException("An error occurred while exporting data collection [" + id + "] to tabman");
		}
		
		if (!task.getStatus().name().equals(TaskStatus.SUCCEDED.name())) {
			throw new ServiceException("Export to tabman for Data Submission [" + id + "]: " + task.getStatus().name() + ", " + task.getErrorCause().getMessage());
		} else {
			try {
				updateDataSubmissionForSuccessfulTabmanExport(dataSubmission, task);
			} catch (JAXBException | ServiceException e) {
				logger.error("Error on updating Data Submission [" + dataSubmission.getId() + "] for successful Tabman export", e);
			}
		}
		
		return ResponseEntity.ok("Export to tabman for Datacollection [" + id + "]: " + task.getStatus().name());
	}
	
	private void updateDataSubmissionForSuccessfulTabmanExport(DataSubmission dataSubmission, Task task) throws JAXBException, ServiceException {
		TabmanInfoDto tabmanInfo = new TabmanInfoDto();
		tabmanInfo.setExportDate(Date.from(Instant.now()));
		tabmanInfo.setResourceId(task.getTabularResourceId().getValue());
		
		AttributesDto attributes = new AttributesDto();
		if( dataSubmission.getAttributes() != null )
			attributes = AttributesDto.fromXml(dataSubmission.getAttributes());

		attributes.setTabmanInfo(tabmanInfo);
		
		DataSubmission dataSubmissionWithTabmanInfo = new DataSubmission();
		dataSubmissionWithTabmanInfo.setId(dataSubmission.getId());
		
		dataSubmissionWithTabmanInfo.setAttributes(AttributesDto.toXml(attributes));
		
		this.dataSubmissionService.updateDataSubmission(dataSubmissionWithTabmanInfo);
	}
	
	private List<String> getFieldNamesInOrder(String dsd) {
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
	
	@RequestMapping(value = "/{id}/status", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataSubmissionDto> changeDataSubmissionStatus(@PathVariable("id") Integer id, @RequestBody DataSubmissionDto dataSubmissionDto) throws ServiceException {
		dataSubmissionDto.setId(id);
		DataSubmission dataSubmissionEntity = this.entityDtoMapper.dtoToEntity(dataSubmissionDto, DataSubmission.class);
		
		dataSubmissionEntity = this.dataSubmissionService.updateDataSubmission(dataSubmissionEntity);
		
		return ResponseEntity.ok(this.entityDtoMapper.entityToDto(dataSubmissionEntity, DataSubmissionDto.class));
	}
	
	
}

