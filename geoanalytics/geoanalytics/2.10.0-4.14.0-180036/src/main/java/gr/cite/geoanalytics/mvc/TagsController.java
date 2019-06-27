package gr.cite.geoanalytics.mvc;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.geoanalytics.util.http.CustomException;
import gr.cite.geoanalytics.util.http.CustomResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class TagsController {

	@Autowired	private LayerManager layerManager;
	@Autowired	private SecurityContextAccessor securityContextAccessor;

	private static final Logger logger = LoggerFactory.getLogger(TagsController.class);

	@RequestMapping(value = "tags/listTags", method = RequestMethod.GET, produces = { "application/json" })
	public @ResponseBody ResponseEntity<?> listTags(HttpServletRequest request) throws Exception {
		logger.debug("Requesting all Tags");

		List<Tag> tags = null;

		try {
			tags = layerManager.listAllTags();
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve tags. Please try again later.", e);
		}

		logger.debug("Tags have been retrieved successfully!");

		return new CustomResponseEntity<List<Tag>>(HttpStatus.OK, tags);
	}

	@RequestMapping(value = "tags/createTag", method = RequestMethod.POST, consumes = { "application/json" }, produces = {"application/json"})
	public @ResponseBody ResponseEntity<?> createTag(@RequestBody Map<String, String> tagInfo, HttpServletRequest request) throws Exception {
		logger.debug("Creating Tag with info : " + tagInfo);

		String name = tagInfo.get("name");
		String description = tagInfo.get("description");
		Principal creator = securityContextAccessor.getPrincipal();

		Tag tag = null;

		try {
			Assert.notNull(name, "Tag name cannot be empty.");
			Assert.hasLength(name, "Tag name cannot be empty.");

			layerManager.checkTagNotExists(name);
			tag = new Tag().withName(name).withDescription(description).withCreator(creator);
			layerManager.createTag(tag);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), "Creation failed. " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			return new CustomResponseEntity<String>(HttpStatus.BAD_REQUEST, "Creation failed. Tag name cannot be empty.", e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create tag with name \"" + name + "\"", e);
		}

		logger.debug("Tag has been created successfully");

		return new CustomResponseEntity<String>(HttpStatus.OK, tag.getId().toString());
	}

	@RequestMapping(value = "tags/deleteTag", method = RequestMethod.POST, consumes = { "application/json" }, produces = {"application/json"})
	public @ResponseBody ResponseEntity<?> deleteTag(@RequestBody String tagId, HttpServletRequest request) throws Exception {
		logger.debug("Deleting Tag with id : " + tagId);

		try {
			Tag tag = layerManager.findTagById(tagId);
			layerManager.deleteTag(tag);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete tag", e);
		}

		logger.debug("Tag has been deleted successfully!");

		return new CustomResponseEntity<String>(HttpStatus.OK, "Tag has been deleted successfully!");
	}

	@RequestMapping(value = "tags/editTag", method = RequestMethod.POST, consumes = { "application/json" }, produces = {"application/json"})
	public @ResponseBody ResponseEntity<?> editTag(@RequestBody Map<String, String> tagInfo, HttpServletRequest request) throws Exception {
		logger.debug("Editing Tag with info : " + tagInfo);

		UUID id = UUID.fromString(tagInfo.get("id"));
		String name = tagInfo.get("name");
		String description = tagInfo.get("description");

		try {
			Assert.notNull(name, "Tag name cannot be empty.");
			Assert.hasLength(name, "Tag name cannot be empty.");

			Tag tag = layerManager.findTagById(id);

			if (Objects.equals(tag.getName(), name) && Objects.equals(tag.getDescription(), description)) {
				throw new CustomException(HttpStatus.BAD_REQUEST, "No changes were submitted.");
			}

			if (!tag.getName().equals(name)) {
				layerManager.checkTagNotExists(name);
			}

			layerManager.editTag(tag, name, description);
		} catch (CustomException e) {
			return new CustomResponseEntity<String>(e.getStatusCode(), "Update failed. " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			return new CustomResponseEntity<String>(HttpStatus.BAD_REQUEST, "Update failed. Tag name cannot be empty.", e);
		} catch (Exception e) {
			return new CustomResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update tag information.", e);
		}

		logger.debug("Tag information has been updated successfully!");

		return new CustomResponseEntity<String>(HttpStatus.OK, "Tag information has been updated successfully!");
	}
}