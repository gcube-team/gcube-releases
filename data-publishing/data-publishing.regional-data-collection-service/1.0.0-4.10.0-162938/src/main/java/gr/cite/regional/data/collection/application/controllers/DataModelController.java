package gr.cite.regional.data.collection.application.controllers;

import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition;
import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintsWrapper;
import gr.cite.regional.data.collection.application.core.EntityDtoMapper;
import gr.cite.regional.data.collection.dataaccess.entities.Properties;
import gr.cite.regional.data.collection.dataaccess.dsd.DataModelDefinition;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdField;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdTemplate;
import gr.cite.regional.data.collection.dataaccess.dsd.Field;
import gr.cite.regional.data.collection.dataaccess.dsd.DsdProcessing;
import gr.cite.regional.data.collection.application.dtos.DataModelDto;
import gr.cite.regional.data.collection.application.dtos.FieldDetails;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataaccess.entities.Constraint;
import gr.cite.regional.data.collection.dataaccess.services.ConstraintsService;
import gr.cite.regional.data.collection.dataaccess.services.DataModelService;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
@RequestMapping("/" + DataModelController.DATA_MODEL_ENDPOINT)
public class DataModelController extends BaseController {
	private static final Logger logger = LogManager.getLogger(DataModelController.class);
	static final String DATA_MODEL_ENDPOINT = "dataModels";
	
	private String hostname;
	private EntityDtoMapper entityDtoMapper;
	
	private DataModelService dataModelService;
	private ConstraintsService constraintsService;
	private DsdProcessing dsdProcessing;
	
	@Autowired
	public DataModelController(DataModelService dataModelService, DsdProcessing dsdProcessing, String hostname, EntityDtoMapper entityDtoMapper,
			ConstraintsService constraintsService) {
		this.dataModelService = dataModelService;
		this.dsdProcessing = dsdProcessing;
		this.hostname = hostname;
		this.entityDtoMapper = entityDtoMapper;
		this.constraintsService = constraintsService;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addDataModel(@RequestBody DataModelDto dataModelDto) throws ServiceException {
		
		DataModel insertedDataModel = this.dataModelService.addDataModel(this.entityDtoMapper.dtoToEntity(dataModelDto, DataModel.class));
		
		List<ConstraintDefinition> constraintsDefinitions = new ArrayList<>();
		try {
			constraintsDefinitions = this.dsdProcessing.extractInherentConstraints(insertedDataModel.getDefinition());
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		List<Constraint> constraints = constraintsDefinitions.stream()
				.map(constraintDefinition -> {
					Constraint constraint = new Constraint();
					//constraint.setConstraint(objectMapper.convertValue(constraintDto, new TypeReference<Map<String, Object>>(){}));
					constraint.setConstraintType(constraintDefinition.getConstraintType());
					constraint.setConstraint(constraintDefinition);
					constraint.setDataModel(insertedDataModel);

					return constraint;
				}).collect(Collectors.toList());
		
		if (!constraints.isEmpty()) {
			this.constraintsService.addConstraints(constraints);
		}
		
//		URI location = UriComponentsBuilder.fromHttpUrl(this.hostname).pathSegment(DataCollectionController.DATA_COLLECTION_ENDPOINT, "{id}")
//				.buildAndExpand(dataModel.getId()).toUri();
//
//		ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.created(location);
//		if (constraintsExtractionException != null) {
//			responseBuilder = responseBuilder.header(HttpHeaders.WARNING, constraintsExtractionException.getMessage());
//		}
//		return responseBuilder.body("Data Model " + dataModel.getLabel() + " [" + dataModel.getId() + "] successfully created");
		return ResponseEntity.ok("Data Model " + insertedDataModel.getLabel() + " [" + insertedDataModel.getId() + "] successfully created");
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataModelDto> updateDataModel(@PathVariable("id") Integer id, @RequestBody DataModelDto dataModelDto) {
		dataModelDto.setId(id);
		DataModel dataModel = this.entityDtoMapper.dtoToEntity(dataModelDto, DataModel.class);
		
		dataModel = this.dataModelService.updateDataModel(dataModel);
		
		return ResponseEntity.ok(this.entityDtoMapper.entityToDto(dataModel, DataModelDto.class));
	}
	
	@RequestMapping(value = "/{id}/properties", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Properties> getDataModelProperties(@PathVariable("id") Integer dataModelId) {
		DataModel dataModel = this.dataModelService.getDataModel(dataModelId);
		return ResponseEntity.ok(dataModel.getProperties());
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteDataModel(@PathVariable("id") Integer id) throws ServiceException {
		logger.debug("Delete data model");
		
		this.dataModelService.deleteDataModel(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DataModelDto>> getAllDataModels(HttpServletRequest request) throws ServiceException {
		logger.debug("Get all data models");
		String scope = request.getHeader("gcube-user-scope");
		
		List<DataModel> dataModels = this.dataModelService.getAllDataModels();
		List<DataModelDto> dataModelDtos = dataModels.stream().filter(dm -> dm.getDomain().getLabel().equals(scope)).map(dm -> {
			DataModelDto dmDto = this.entityDtoMapper.entityToDto(dm, DataModelDto.class);
			try {
				dmDto.setActiveDataCollectionPeriod(this.dataModelService.isDataCollectionPeriodActiveByDataModelId(dm.getId()));
			} catch (ServiceException e) {
				logger.debug("Something went wrong while retrieving data models");
			}
			return dmDto;
		}).collect(Collectors.toList());
		return ResponseEntity.ok(dataModelDtos);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataModelDto> getDataModel(@PathVariable("id") Integer id) {
		logger.debug("Get data model by id: " + id);
		
		DataModel dataModel = this.dataModelService.getDataModel(id);
		return ResponseEntity.ok(this.entityDtoMapper.entityToDto(dataModel, DataModelDto.class));
	}
	
	@RequestMapping(value = "/{id}/definition", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DsdTemplate> getDataModelDefinition(@PathVariable("id") Integer id) {
		logger.debug("Get data model definition by id: " + id);
		
		DataModel dataModel = this.dataModelService.getDataModel(id);
		DsdTemplate dsdTemplate = this.dsdProcessing.getDefinition(dataModel.getDefinition());
		
		return ResponseEntity.ok(dsdTemplate);
	}
	
	@RequestMapping(value = "/{id}/definition/excel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DataModelDefinition> getDataModelDefinitionForExcel(@PathVariable("id") Integer id) throws ServiceException {
		logger.debug("Get data model definition by id: " + id);
		
		DataModel dataModel = this.dataModelService.getDataModel(id);
		
		DataModelDefinition definition = this.dsdProcessing.getDefinitionForExcelAddIn(dataModel.getDefinition());
		
		definition.getFields().forEach(field -> {
			if (dataModel.getProperties() != null && dataModel.getProperties().getStaticFields().contains(field.getLabel())) {
				field.setStaticField(true);
			}
		});
		
		Map<String, List<ConstraintDefinition>> constraints = new HashMap<>();
		this.constraintsService.getConstraintsByDataModelId(id).stream()
				//.map(constraint -> objectMapper.convertValue(constraint.getConstraint(), ConstraintDefinition.class))
				.map(Constraint::getConstraint)
				.forEach(constraintDefinition -> {
					if (constraintDefinition.getConstraintType().startsWith("attribute")) {
						constraints.computeIfAbsent("attribute", k -> new ArrayList<>());
						constraints.get("attribute").add(constraintDefinition);
					} else if (constraintDefinition.getConstraintType().startsWith("entity")) {
						constraints.computeIfAbsent("entity", k -> new ArrayList<>());
						constraints.get("entity").add(constraintDefinition);
					}
				});
		definition.setConstraints(constraints);
		
		return ResponseEntity.ok(definition);
	}
	
	@RequestMapping(value = "/{id}/definition/{fieldId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FieldDetails> getDataModelDefinitionForAdmin(@PathVariable("id") Integer id, @PathVariable("fieldId") String fieldId) throws ServiceException {
		
		DataModel dataModel = this.dataModelService.getDataModel(id);
		DataModelDefinition definition = this.dsdProcessing.getDefinitionForExcelAddIn(dataModel.getDefinition());
		
		Field field = definition.getFields().stream().filter(f -> f.getId().equals(fieldId)).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No field with id [" + fieldId + "] in definition"));
		
		FieldDetails fieldDetails = new FieldDetails();
		fieldDetails.setCodelistId(field.getCodelist().getId());
		fieldDetails.setCodelistLabel(field.getCodelist().getLabel());
		fieldDetails.setFields(field.getCodelist().getFields());
		
		return ResponseEntity.ok(fieldDetails);
	}
	
	@RequestMapping(value = "/{id}/definition/fields", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DsdField>> getDataModelDefinitionFields(@PathVariable("id") Integer id) {
		logger.debug("Get data model fields by id: " + id);
		
		DataModel dataModel = this.dataModelService.getDataModel(id);
		List<DsdField> fields= this.dsdProcessing.getFields(dataModel.getDefinition());
		
		return ResponseEntity.ok(fields);
	}
	
	@RequestMapping(value = "/{id}/constraints", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addConstraint(@PathVariable("id") Integer id, @RequestBody ConstraintDefinition constraintDefinition) throws ServiceException, IOException {
		logger.debug("Add new constraintDefinition");
		
		//Map<String, Object> jsonMap = objectMapper.convertValue(constraintDefinition, new TypeReference<Map<String, Object>>(){});
		
		Constraint constraintEntity = new Constraint();
		constraintEntity.setConstraint(constraintDefinition);
		constraintEntity.setConstraintType(constraintDefinition.getConstraintType());
		
		DataModel dm = new DataModel();
		dm.setId(id);
		constraintEntity.setDataModel(dm);
		
		constraintEntity = this.constraintsService.addConstraint(constraintEntity);
		
		URI location = UriComponentsBuilder.fromHttpUrl(this.hostname).pathSegment(DataModelController.DATA_MODEL_ENDPOINT, "{id}", "constraints", "{constraintId}")
				.buildAndExpand(id, constraintEntity.getId()).toUri();
		return ResponseEntity.created(location).body("Constraint of type " + constraintEntity.getConstraintType() + " [" + constraintEntity.getId() + "] successfully created");
	}
	
	@RequestMapping(value = "/{id}/constraints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConstraintsWrapper> getAllConstraintsOfDataModel(@PathVariable("id") Integer id) throws ServiceException, IOException {
		logger.debug("Getting all constraints of dataModel");
		
		
		List<ConstraintDefinition> constraints = this.constraintsService.getConstraintsByDataModelId(id)
				.stream()
				.map(constraintEntity -> {
					ConstraintDefinition constraintDefinition = objectMapper.convertValue(constraintEntity.getConstraint(), ConstraintDefinition.class);
					constraintDefinition.setId(constraintEntity.getId());
					return constraintDefinition;
				}).collect(Collectors.toList());
		
		boolean activeDataCollectionPeriod = this.dataModelService.isDataCollectionPeriodActiveByDataModelId(id);
		
		return ResponseEntity.ok(new ConstraintsWrapper(activeDataCollectionPeriod, constraints));
	}
	
	@RequestMapping(value = "/{id}/constraints/{constraintId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteConstraint(@PathVariable("id") Integer dataModelId, @PathVariable("constraintId") int constraintId) throws ServiceException, IOException {
		logger.debug("Delete constraint of dataModel");
		this.constraintsService.deleteConstraint(constraintId);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value = "/{id}/constraints/{constraintId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateConstraint(@PathVariable("id") Integer id, @PathVariable("constraintId") Integer constraintId, @RequestBody ConstraintDefinition constraintDefinition) throws ServiceException, IOException {
		logger.debug("Update a constraint");
		
		Constraint constraintEntity = new Constraint();
		constraintEntity.setId(constraintId);
		constraintEntity.setConstraintType(constraintDefinition.getConstraintType());
		constraintEntity.setConstraint(constraintDefinition);

		DataModel dm = new DataModel();
		dm.setId(id);
		constraintEntity.setDataModel(dm);

		/*Map<String, Object> jsonMap = objectMapper.convertValue(constraintDefinition, new TypeReference<Map<String, Object>>() {});
		constraintEntity.setConstraint(jsonMap);*/
		
		//constraintEntity = this.constraintsService.updateConstraintFromDto(constraintDefinition);
		constraintEntity = this.constraintsService.updateConstraint(constraintEntity);

		return ResponseEntity.ok("Constraint [" + constraintEntity.getId() + "] successfully updated");
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity test() {
		String dsdXml = null;
		try {
			dsdXml = Files.readAllLines(Paths.get("/home/kapostolopoulos/Desktop/fao/DSD/Structures.xml")).stream().collect(Collectors.joining(""));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		List<ConstraintDefinition> constraints = this.dsdProcessing.extractInherentConstraints(dsdXml);
		return ResponseEntity.ok(constraints);
	}
}