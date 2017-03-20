package gr.cite.commons.util.datarepository.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;
import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;
import gr.cite.commons.util.datarepository.elements.RepositoryFile.State;
import gr.cite.commons.util.datarepository.inject.FileSystemDataRepositoryProvider;
import gr.cite.commons.util.datarepository.utils.DataRepositoryUtils;

@Path("/datarepository")
public class FileSystemDataRepositoryResource {
	private static final Logger logger = LoggerFactory.getLogger(FileSystemDataRepositoryResource.class);

	private static final String GET_FILE = "getFile";
	
	private final FileSystemDataRepositoryProvider dataRepositoryProvider;
	private final String publicUrl;

	@Inject
	public FileSystemDataRepositoryResource(FileSystemDataRepositoryProvider dataRepositoryProvider, @Named("publicUrl") String publicUrl) {
		this.dataRepositoryProvider = dataRepositoryProvider;
		this.publicUrl = publicUrl;
	}

	@POST
	@Path("insertBase64")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_XML)
	public Response insertBase64File(String base64String) {

		RepositoryFile repositoryFile = new RepositoryFile();

		String metadata = base64String.substring(0, base64String.indexOf(","));

		String encoding = metadata.substring(metadata.indexOf(";") + 1);
		if (!encoding.equals("base64")) {
			throw new WebApplicationException(Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build());
		}

		String type = metadata.substring(metadata.indexOf(":") + 1, metadata.indexOf(";"));
		repositoryFile.setDataType(type);

		String imageDataBytes = base64String.substring(base64String.indexOf(",") + 1);

		try (InputStream stream = new ByteArrayInputStream(Base64.decodeBase64(imageDataBytes.getBytes()))) {
			repositoryFile.setInputStream(stream);
			DataRepository fileDataRepository = dataRepositoryProvider.get();
			String id = fileDataRepository.persist(repositoryFile);
			repositoryFile.setId(id);

			return Response.ok(repositoryFile).build();

		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			throw new WebApplicationException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e);
		}
	}

	@POST
	@Path("insertInputStream")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_XML)
	public Response insertInputStream(InputStream input, @QueryParam("datatype") String dataType) {
		RepositoryFile repositoryFile = new RepositoryFile();

		repositoryFile.setDataType(dataType);
		try (InputStream inputStream = new ByteArrayInputStream(IOUtils.toByteArray(input))) {
			repositoryFile.setInputStream(inputStream);
			DataRepository fileDataRepository = dataRepositoryProvider.get();
			String id = fileDataRepository.persist(repositoryFile);
			repositoryFile.setId(id);

			return Response.ok(repositoryFile).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path(GET_FILE + "/{id}")
	public Response getFile(@PathParam("id") String id) {

		try {
			RepositoryFile repositoryFile = dataRepositoryProvider.get().retrieve(id);
			if (repositoryFile == null) {
				throw new WebApplicationException(Status.NOT_FOUND);
			} else {
				MediaType type = MediaType.valueOf(repositoryFile.getDataType());
				
				logger.info("Retrieved file with local image: " + repositoryFile.getLocalImage().toString());
				//ResponseBuilder response = Response.ok(new File(Resources.getResource(repositoryFile.getLocalImage().toString()).toURI()));
				ResponseBuilder response = Response.ok(new File(repositoryFile.getLocalImage()));
				
				response.type(type);
				response.header("Content-Disposition",
						"inline; filename=\"" + repositoryFile.getOriginalName() + "\"");
				return response.build();
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("getBase64URI/{id}")
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public Response getFileInBase64URI(@PathParam("id") String id) {
		try {
			RepositoryFile repositoryFile = dataRepositoryProvider.get().retrieve(id);
			StringBuffer buffer = new StringBuffer("data:");
			buffer.append(repositoryFile.getDataType());
			buffer.append(";base64,");
			buffer.append(new String(Base64.encodeBase64(Files.toByteArray(new File(repositoryFile.getLocalImage())))));

			return Response.ok(buffer.toString()).build();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new WebApplicationException(e);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@POST
	@Path("removeFile/{id}")
	public Response removeFile(@PathParam("id") String id) {
		try {
			dataRepositoryProvider.get().delete(id);
			return Response.ok().build();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	private URI createPublicLocalImageUrl(String id) {
		return UriBuilder.fromPath(publicUrl).path(GET_FILE).path(id).build();
	}
	
	@POST
	@Path(DataRepositoryUtils.PERSIST)
	@Consumes(MediaType.APPLICATION_JSON)
	public RepositoryFile persist(RepositoryFile file) throws Exception {
		String id = dataRepositoryProvider.get().persist(file);
		dataRepositoryProvider.get().close(file);
		RepositoryFile newRf = new RepositoryFile();
		newRf.setDataType(file.getDataType());
		newRf.setId(file.getId());
		newRf.setLocalImage(createPublicLocalImageUrl(id));
		newRf.setOriginalName(file.getOriginalName());
		newRf.setPermanent(file.isPermanent());
		newRf.setSize(file.getSize());
		newRf.setTimestamp(file.getTimestamp());
		newRf.setUri(file.getUri());
		newRf.markPersisted();
		return newRf;
	}

	@POST
	@Path(DataRepositoryUtils.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	public RepositoryFile update(RepositoryFile file) throws Exception {
		String id = dataRepositoryProvider.get().update(file);
		file.setLocalImage(createPublicLocalImageUrl(id));
		return file;
	}

	@GET
	@Path(DataRepositoryUtils.RETRIEVE + "/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public RepositoryFile retrieve(@PathParam("id") String id) throws Exception {
		RepositoryFile file = dataRepositoryProvider.get().retrieve(id);
		if(file == null) {
			logger.warn("Could not find file " + id);
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		RepositoryFile newRf = new RepositoryFile();
		newRf.setDataType(file.getDataType());
		newRf.setId(file.getId());
		newRf.setLocalImage(createPublicLocalImageUrl(id));
		newRf.setOriginalName(file.getOriginalName());
		newRf.setPermanent(file.isPermanent());
		newRf.setSize(file.getSize());
		newRf.setTimestamp(file.getTimestamp());
		newRf.setUri(file.getUri());
		if(file.getState() == State.PERSISTED)
			newRf.markPersisted();
		return newRf;
	}

	@POST
	@Path(DataRepositoryUtils.DELETE + "/{id}")
	public void delete(@PathParam("id") String id) throws Exception {
		dataRepositoryProvider.get().delete(id);
	}

	@GET
	@Path(DataRepositoryUtils.LIST_IDS)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> listIds() throws Exception {
		return dataRepositoryProvider.get().listIds();
	}

	@POST
	@Path(DataRepositoryUtils.PERSIST_TO_FOLDER)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String persistToFolder(List<RepositoryFile> files) throws Exception {
		return dataRepositoryProvider.get().persistToFolder(files);
	}

	@POST
	@Path(DataRepositoryUtils.UPDATE_TO_FOLDER + "/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateToFolder(RepositoryFile file, @PathParam("id") String folderId) throws Exception {
		return dataRepositoryProvider.get().updateToFolder(file, folderId);
	}

	@POST
	@Path(DataRepositoryUtils.UPDATE_TO_FOLDER_MULTIPLE_FILES + "/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateToFolder(List<RepositoryFile> files, @PathParam("id") String folderId) throws Exception {
		return dataRepositoryProvider.get().updateToFolder(files, folderId);
	}

	@POST
	@Path(DataRepositoryUtils.ADD_TO_FOLDER + "/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String addToFolder(RepositoryFile file, @PathParam("id") String folderId) throws Exception {
		return dataRepositoryProvider.get().addToFolder(file, folderId);
	}

	@POST
	@Path(DataRepositoryUtils.ADD_TO_FOLDER_MULTIPLE_FILES + "/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String addToFolder(List<RepositoryFile> files, @PathParam("id") String folderId) throws Exception {
		return dataRepositoryProvider.get().addToFolder(files, folderId);
	}

	@GET
	@Path(DataRepositoryUtils.LIST_FOLDER + "/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> listFolder(@PathParam("id") String folderId) throws Exception {
		return dataRepositoryProvider.get().listFolder(folderId);
	}

	@GET
	@Path(DataRepositoryUtils.GET_TOTAL_SIZE)
	public Response getTotalSize() {
		return Response.ok(String.valueOf(dataRepositoryProvider.get().getTotalSize())).build();
	}

	@GET
	@Path(DataRepositoryUtils.GET_LAST_SWEEP)
	public Response getLastSweep() {
		Long lastSweep = dataRepositoryProvider.get().getLastSweep();
		return lastSweep != null ? Response.ok(String.valueOf(lastSweep)).build() : Response.noContent().build();
	}

	@GET
	@Path(DataRepositoryUtils.GET_SWEEP_SIZE_REDUCTION)
	public Response getSweepSizeReduction() {
		Long reduction = dataRepositoryProvider.get().getSweepSizeReduction();
		return reduction != null ? Response.ok(String.valueOf(reduction)).build() : Response.noContent().build();
	}

	@POST
	@Path(DataRepositoryUtils.CLOSE)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response close(RepositoryFile file) throws Exception {
		return Response.ok(String.valueOf(dataRepositoryProvider.get().close(file))).build();
	}
}
