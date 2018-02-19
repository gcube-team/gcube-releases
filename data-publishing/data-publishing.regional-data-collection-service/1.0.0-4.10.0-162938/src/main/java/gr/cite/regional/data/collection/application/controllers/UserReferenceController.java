package gr.cite.regional.data.collection.application.controllers;

import gr.cite.regional.data.collection.application.core.EntityDtoMapper;
import gr.cite.regional.data.collection.application.dtos.UserReferenceDto;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataaccess.services.UserReferenceService;
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

import java.net.URI;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/" + UserReferenceController.USER_REFERENCE_ENDPOINT)
public class UserReferenceController extends BaseController {
	private static final Logger logger = LogManager.getLogger(UserReferenceController.class);
	static final String USER_REFERENCE_ENDPOINT = "userReferences";
	
	private String hostname;
	private EntityDtoMapper entityDtoMapper;
	
	private UserReferenceService userReferenceService;

	@Autowired
	public UserReferenceController(UserReferenceService userReferenceService, String hostname, EntityDtoMapper entityDtoMapper) {
		this.hostname = hostname;
		this.entityDtoMapper = entityDtoMapper;
		
		this.userReferenceService = userReferenceService;
	}

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserReferenceDto>> getAllUserReferences() throws ServiceException {
		List<UserReference> userReferrences = this.userReferenceService.getAllUserReferences();
		return ResponseEntity.ok( this.entityDtoMapper.entitiesToDtos( userReferrences, UserReferenceDto.class ) );
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addUserReference(@RequestBody UserReferenceDto userReferenceDto) throws ServiceException {
		UserReference userReferenceEntity = this.entityDtoMapper.dtoToEntity(userReferenceDto, UserReference.class);
		
		userReferenceEntity = this.userReferenceService.addUserReference(userReferenceEntity);
		
		/*URI location = UriComponentsBuilder.fromHttpUrl(this.hostname).pathSegment(UserReferenceController.USER_REFERENCE_ENDPOINT, "{id}")
				.buildAndExpand(userReferenceEntity.getId()).toUri();
		
		return ResponseEntity.created(location).body("User Reference " + userReferenceEntity.getLabel() + " [" + userReferenceEntity.getId() + "] successfully created");*/
		return ResponseEntity.ok("User Reference " + userReferenceEntity.getLabel() + " [" + userReferenceEntity.getId() + "] successfully created");
	}
	
	@RequestMapping(value="/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserReferenceDto> login(@RequestBody UserReferenceDto userReferenceDto) throws ServiceException {
		UserReference userReferenceEntity = this.userReferenceService.createUserReferenceIfNotExists(this.entityDtoMapper.dtoToEntity(userReferenceDto, UserReference.class));
		UserReferenceDto userReference = this.entityDtoMapper.entityToDto(userReferenceEntity, UserReferenceDto.class);
		return ResponseEntity.ok(userReference);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateUserReference(@PathVariable("id") Integer id, @RequestBody UserReferenceDto userReferenceDto) throws ServiceException {
		userReferenceDto.setId(id);
		UserReference userReferenceEntity = this.entityDtoMapper.dtoToEntity(userReferenceDto, UserReference.class);
		
		userReferenceEntity = this.userReferenceService.updateUserReference(userReferenceEntity);
		
		return ResponseEntity.ok("User Reference " + userReferenceEntity.getLabel() + " [" + userReferenceEntity.getId() + "] successfully updated");
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserReferenceDto> getUserReference(@PathVariable("id") Integer id) {
		UserReference userReference = this.userReferenceService.getUserReference(id);
		return ResponseEntity.ok(this.entityDtoMapper.entityToDto(userReference, UserReferenceDto.class));
	}
	
}
