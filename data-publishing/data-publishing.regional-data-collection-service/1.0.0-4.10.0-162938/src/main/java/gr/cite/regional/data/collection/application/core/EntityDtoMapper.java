package gr.cite.regional.data.collection.application.core;

import gr.cite.regional.data.collection.application.dtos.AttributesDto;
import gr.cite.regional.data.collection.application.dtos.DataCollectionDto;
import gr.cite.regional.data.collection.application.dtos.DataSubmissionDto;
import gr.cite.regional.data.collection.application.dtos.Dto;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.Entity;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.ExpressionMap;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.builder.ConfigurableMapExpression;
import org.modelmapper.builder.ReferenceMapExpression;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.SourceGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {
	private static final Logger logger = LoggerFactory.getLogger(EntityDtoMapper.class);
	private ModelMapper modelMapper;
	
	private Converter<String, AttributesDto> attributesXmlToAttributesDto = context -> {
		AttributesDto attributesDto = new AttributesDto();
		String attributes = context.getSource();
		try {
			attributesDto = AttributesDto.fromXml(attributes);
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}
		return attributesDto;
	};
	
	private Converter<AttributesDto, String> attributesDtoToAttributesXml = context -> {
		String attributesXml = null;
		AttributesDto attributes = context.getSource();
		try {
			attributesXml = AttributesDto.toXml(attributes);
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}
		return attributesXml;
	};
	
	@Autowired
	public EntityDtoMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
		
		TypeMap<DataSubmission, DataSubmissionDto> dataSubmissionEntityToDtoTypeMap = this.modelMapper.createTypeMap(DataSubmission.class, DataSubmissionDto.class)
				.addMappings(mapper -> mapper.using(this.attributesXmlToAttributesDto)
						.map(DataSubmission::getAttributes, DataSubmissionDto::setAttributes));
		dataSubmissionEntityToDtoTypeMap.addMappings(mapper -> mapper.skip(DataSubmissionDto::setDataCollection));
		
		this.modelMapper.createTypeMap(DataSubmissionDto.class, DataSubmission.class)
				.addMappings(mapper -> mapper.using(this.attributesDtoToAttributesXml)
						.map(DataSubmissionDto::getAttributes, DataSubmission::setAttributes));
		
		this.modelMapper.createTypeMap(DataCollection.class, DataCollectionDto.class)
				.addMappings(mapper -> mapper.using(this.attributesXmlToAttributesDto)
						.map(DataCollection::getAttributes, DataCollectionDto::setAttributes));
		
		this.modelMapper.createTypeMap(DataCollectionDto.class, DataCollection.class)
				.addMappings(mapper -> mapper.using(this.attributesDtoToAttributesXml)
						.map(DataCollectionDto::getAttributes, DataCollection::setAttributes));
	}
	
	public <U extends Entity, V extends Dto> V entityToDto(U entity, Class<V> dtoClass) {
		return this.modelMapper.map(entity, dtoClass);
	}
	
	public <U extends Dto, V extends Entity> V dtoToEntity(U dto, Class<V> entityClass) {
		return this.modelMapper.map(dto, entityClass);
	}
	
	public <U, V extends Dto> List<V> entitiesToDtos(Collection<U> entities, Class<V> dtoClass) {
		return entities.stream().map(entity -> this.modelMapper.map(entity, dtoClass)).collect(Collectors.toList());
	}
	
	public <U extends Dto, V extends Entity> List<V> dtosToEntities(Collection<U> dtos, Class<V> entityClass) {
		return dtos.stream().map(dto -> this.modelMapper.map(dto, entityClass)).collect(Collectors.toList());
	}
}
