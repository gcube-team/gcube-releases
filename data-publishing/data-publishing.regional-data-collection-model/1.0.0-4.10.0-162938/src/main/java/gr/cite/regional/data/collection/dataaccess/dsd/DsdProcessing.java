package gr.cite.regional.data.collection.dataaccess.dsd;

import gr.cite.regional.data.collection.dataaccess.constraints.AttributeCodelistConstraint;
import gr.cite.regional.data.collection.dataaccess.constraints.AttributeMandatoryConstraint;
import gr.cite.regional.data.collection.dataaccess.constraints.ConstraintDefinition;
import org.sdmxsource.sdmx.api.factory.ReadableDataLocationFactory;
import org.sdmxsource.sdmx.api.manager.parse.StructureParsingManager;
import org.sdmxsource.sdmx.api.model.StructureWorkspace;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.RepresentationBean;
import org.sdmxsource.sdmx.api.model.beans.base.TextTypeWrapper;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.reference.MaintainableRefBean;
import org.sdmxsource.sdmx.api.util.ReadableDataLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DsdProcessing {
	private ReadableDataLocationFactory rdlFactory;
	private StructureParsingManager structureParsingManager;
	
	@Autowired
	public DsdProcessing(ReadableDataLocationFactory rdlFactory, StructureParsingManager structureParsingManager) {
		this.rdlFactory = rdlFactory;
		this.structureParsingManager = structureParsingManager;
	}
	
	public DsdTemplate getDefinition(String dsdXml) {
		DsdTemplate dsdTemplate = new DsdTemplate();
		
		SdmxBeans structures = parse(dsdXml);
		
		//retrieveConcepts(structures);
		//retrieveCodelists(structures);
		ColumnMapper columnMapper = new ColumnMapper();
		List<Column> columns = retrieveDataStructures(structures);
		columnMapper.setColumns(columns);
		
		TableTemplate tableTemplate = new TableTemplate();
		tableTemplate.setColumnMapper(columnMapper);
		
		List<String> headers = columns.stream().map(Column::getLabel).collect(Collectors.toList());
		tableTemplate.setHeaders(headers);
		
		dsdTemplate.setTableTemplate(tableTemplate);
		
		return dsdTemplate;
	}
	
	public DataModelDefinition getDefinitionForExcelAddIn(String dsdXml) {
		DataModelDefinition dataModelDefinition = new DataModelDefinition();
		
		List<Field> fields = retrieveFields(parse(dsdXml));
		dataModelDefinition.setFields(fields);
		return dataModelDefinition;
	}
	
	public List<DsdField> getFields(String dsdXml) {
		SdmxBeans structures = parse(dsdXml);
		List<Column> columns = retrieveDataStructures(structures);
		
		return columns.stream().map(column -> {
			DsdField field = new DsdField();
			field.setId(column.getId());
			field.setLabel(column.getLabel());
			return field;
		}).collect(Collectors.toList());
	}
	
	public List<ConstraintDefinition> extractInherentConstraints(String dsdXml) {
		List<ConstraintDefinition> constraints = new ArrayList<>();
		
		List<Field> fields = retrieveFields(parse(dsdXml));
		fields.forEach(field -> {
			if (field.isMandatory()) {
				AttributeMandatoryConstraint constraint = new AttributeMandatoryConstraint();
				constraint.setLabel("Inherent mandatory constraint");
				constraint.setConstraintType("attributeMandatory");
				
				constraint.setFieldId(field.getId());
				constraint.setField(field.getLabel());
				
				constraint.getTrigger().add(ConstraintDefinition.Trigger.ON_VALIDATE);
				constraint.setErrorMessage(field.getLabel() + " is mandatory");
				
				constraints.add(constraint);
			}
			
			if (field.getCodelist() != null) {
				AttributeCodelistConstraint constraint = new AttributeCodelistConstraint();
				constraint.setConstraintType("attributeCodelist");
				constraint.setLabel("Inherent codelist constraint");
				
				constraint.setFieldId(field.getId());
				constraint.setField(field.getLabel());
				
				constraint.setCodelistId(field.getCodelist().getId());
				constraint.setCodelist(field.getCodelist().getLabel());
				
				String idField = field.getCodelist().getFields().get(0);
				String nameOrDescriptionField = field.getCodelist().getFields().stream().filter(codeField -> !"id".equals(codeField)).findFirst().orElse("id");
				
				constraint.setPersistField(idField);
				constraint.setDisplayField(nameOrDescriptionField);
				
				
				constraint.getTrigger().addAll(Arrays.asList(ConstraintDefinition.Trigger.ON_VALIDATE, ConstraintDefinition.Trigger.ON_CHANGE));
				constraint.setErrorMessage(field.getLabel() + " is a " + field.getCodelist().getLabel() + " codelist field");
				
				constraints.add(constraint);
			}
		});
		
		return constraints;
	}
	
	private SdmxBeans parse(String dsdXml) {
		ReadableDataLocation rdl = this.rdlFactory.getReadableDataLocation(dsdXml.getBytes());
		StructureWorkspace workspace = this.structureParsingManager.parseStructures(rdl);
		
		return workspace.getStructureBeans(false);
	}
	
	
	private Map<String, String> retrieveConcepts(SdmxBeans structures) {
		Map<String, String> concepts = new HashMap<>();
		
		structures.getConceptSchemes().forEach(conceptScheme ->
			conceptScheme.getItems().forEach(conceptBean ->
				concepts.put(conceptBean.getId(), conceptBean.getName())
			)
		);
		
		return concepts;
	}
	
	private List<Field> retrieveFields(SdmxBeans structures) {
		List<Field> fields = new ArrayList<>();
		
		Map<String, String> concepts = retrieveConcepts(structures);
		
		structures.getDataStructures().forEach(structure -> {
			
			structure.getDimensions().forEach(dimensionBean -> {
				Field field = new Field();
				
				field.setId(dimensionBean.getId());
				field.setOrder(dimensionBean.getPosition());
				
				String conceptId = dimensionBean.getConceptRef().getChildReference().getId();
				ConceptSchemeBean concept = getConceptById(conceptId, dimensionBean.getConceptRef().getMaintainableReference(), structures);
				
				
				RepresentationBean representationBean = concept.getItemById(conceptId).getCoreRepresentation();
				if (representationBean != null) {
					String codelistId = representationBean.getRepresentation().getMaintainableId();
					MaintainableRefBean codelistMaintainableRefBean = representationBean.getRepresentation().getMaintainableReference();
					Codelist codelist = getCodelistById(codelistId, codelistMaintainableRefBean, structures);
					
					field.setCodelist(codelist);
				}
				
				
				field.setLabel(concepts.get(dimensionBean.getConceptRef().getChildReference().getId()));
				fields.add(field);
			});
			
			structure.getAttributes().forEach(attributeBean -> {
				Field field = new Field();
				
				field.setId(attributeBean.getId());
				field.setOrder(fields.size() + 1);
				field.setLabel(concepts.get(attributeBean.getConceptRef().getChildReference().getId()));
				field.setMandatory(attributeBean.isMandatory());
				
				fields.add(field);
			});
			
			Field field = new Field();
			field.setId(structure.getPrimaryMeasure().getId());
			field.setOrder(fields.size() + 1);
			field.setLabel(concepts.get(structure.getPrimaryMeasure().getConceptRef().getChildReference().getId()));
			fields.add(field);
			
		});
		return fields;
	}
	
	private List<Column> retrieveDataStructures(SdmxBeans structures) {
		List<Column> columns = new ArrayList<>();
		Map<String, String> concepts = retrieveConcepts(structures);
		structures.getDataStructures().forEach(structure -> {
			
			structure.getDimensions().forEach(dimensionBean -> {
				Column column = new Column();
				
				column.setId(dimensionBean.getId());
				column.setOrder(dimensionBean.getPosition());
				column.setLabel(concepts.get(dimensionBean.getConceptRef().getChildReference().getId()));
				columns.add(column);
			});
			
			structure.getAttributes().forEach(attributeBean -> {
				Column column = new Column();
				
				column.setId(attributeBean.getId());
				column.setOrder(columns.size() + 1);
				column.setLabel(concepts.get(attributeBean.getConceptRef().getChildReference().getId()));
				columns.add(column);
			});
			
			Column column = new Column();
			column.setId(structure.getPrimaryMeasure().getId());
			column.setOrder(columns.size() + 1);
			column.setLabel(concepts.get(structure.getPrimaryMeasure().getConceptRef().getChildReference().getId()));
			columns.add(column);
			
		});
		return columns;
	}
	
	private List<Codelist> retrieveCodelists(SdmxBeans structures) {
		structures.getCodelists().forEach(codelistBean -> {
			System.out.println(codelistBean.getId());
			System.out.println(codelistBean.getName());
			codelistBean.getItems().forEach(codeBean -> {
				System.out.println("--------------------- TEXT TYPES ---------------------");
				codeBean.getAllTextTypes().forEach(textType -> {
					System.out.println(textType.getValue());
				});
				
				System.out.println("--------------------- DESCRIPTIONS ---------------------");
				codeBean.getDescriptions().forEach(description -> {
					System.out.println(description.getValue());
				});
			});
		});
		return null;
	}
	
	private ConceptSchemeBean getConceptById(String id, MaintainableRefBean maintainableRefBean, SdmxBeans structures) {
		return structures.getConceptSchemes(maintainableRefBean).stream()
				.filter(conceptSchemeBean -> conceptSchemeBean.getItemById(id) != null).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No concept with id [" + id + "] found"));
	}
	
	private Codelist getCodelistById(String id, MaintainableRefBean maintainableRefBean, SdmxBeans structures) {
		Codelist codelist = new Codelist();
		codelist.setId(id);
		
		CodelistBean codelistBean = structures.getCodelists(maintainableRefBean).stream()
				.filter(code-> code.getId().equals(id)).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No codelist with id [" + id + "] found"));
		
		codelist.setLabel(codelistBean.getName());
		
		List<String> headers;
		if (codelistBean.getItems().size() > 0) {
			CodeBean codeBean = codelistBean.getItems().get(0);
			
			Stream<String> names = codeBean.getNames().stream().map(name -> "name_" + name.getLocale());
			Stream<String> descriptions = codeBean.getDescriptions().stream().map(description -> "description_" + description.getLocale());
			Stream<String> fields = Stream.concat(names, descriptions);
			
			headers = Stream.concat(Arrays.stream(new String[] {"id"}), fields).collect(Collectors.toList());
			codelist.setFields(headers);
		}
		
		List<List<String>> values = codelistBean.getItems().stream().map(codeBean -> {
			String codeId = codeBean.getId();
			Stream<String> codeNames = codeBean.getNames().stream().map(TextTypeWrapper::getValue);
			Stream<String> codeDescriptions = codeBean.getDescriptions().stream().map(TextTypeWrapper::getValue);
			
			Stream<String> fieldValues = Stream.concat(codeNames, codeDescriptions);
			
			return Stream.concat(Arrays.stream(new String[] {codeId}), fieldValues).collect(Collectors.toList());
		}).filter(codes -> codes.size() == codelist.getFields().size())
				.collect(Collectors.toList());
		
		codelist.setValues(values);
		
		
		List<Code> codes = codelistBean.getItems().stream().map(codeBean -> {
				String codeId = codeBean.getId();
				
				Map<String, String> names = codeBean.getNames().stream()
						.collect(Collectors.toMap(name -> "name_" + name.getLocale(), TextTypeWrapper::getValue));
				Map<String, String> descriptions = codeBean.getDescriptions().stream()
						.collect(Collectors.toMap(description -> "description_" + description.getLocale(), TextTypeWrapper::getValue));
				
				Map<String, String> fields = Stream.of(names, descriptions)
					.map(Map::entrySet)
					.flatMap(Collection::stream)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				
				Code code = new Code();
				code.setId(codeId);
				code.setFields(fields);
				
				return code;
			}).collect(Collectors.toList());
		
		codelist.setCodes(codes);
		
		return codelist;
	}
	
	
}