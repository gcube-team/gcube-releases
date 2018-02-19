package gr.cite.regional.data.collection.application.controllers;

import gr.cite.regional.data.collection.application.core.EntityDtoMapper;
import gr.cite.regional.data.collection.application.dtos.DomainDto;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataaccess.services.DomainService;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/domains")
public class DomainController extends BaseController {
	private static final Logger logger = LogManager.getLogger(DomainController.class);
	
	private String hostname;
	private EntityDtoMapper entityDtoMapper;
	
	private DomainService domainService;
	
	@Autowired
	public DomainController(DomainService domainService, String hostname, EntityDtoMapper entityDtoMapper) {
		this.hostname = hostname;
		this.domainService = domainService;
		this.entityDtoMapper = entityDtoMapper;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DomainDto> getDomain(@PathVariable("id") Integer id) {
		Domain domain = this.domainService.getDomain(id);
		return domain == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(this.entityDtoMapper.entityToDto(domain, DomainDto.class));
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DomainDto>> getDomains(@RequestParam(value = "label", required = false) String label) throws ServiceException {
		List<Domain> domains = new ArrayList<>();
		if (label != null) {
			domains.add(this.domainService.getOrCreateDomainByLabel(label));
		} else {
			domains = this.domainService.getDomains();
		}
		return ResponseEntity.ok(this.entityDtoMapper.entitiesToDtos(domains, DomainDto.class));
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addDomain(@RequestBody DomainDto domainDto, HttpServletRequest request) throws ServiceException {
		Domain domainEntity = this.entityDtoMapper.dtoToEntity(domainDto, Domain.class);
		this.domainService.addDomain(domainEntity);
		return ResponseEntity.created(URI.create(this.hostname + "/" + domainEntity.getId())).build();
	}
	
}
