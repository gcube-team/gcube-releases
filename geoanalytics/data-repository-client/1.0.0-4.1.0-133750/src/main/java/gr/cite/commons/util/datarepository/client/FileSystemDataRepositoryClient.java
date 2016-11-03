package gr.cite.commons.util.datarepository.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import gr.cite.commons.util.datarepository.api.DataRepositoryClient;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;
import gr.cite.commons.util.datarepository.elements.RepositoryFile.State;
import gr.cite.commons.util.datarepository.utils.DataRepositoryUtils;

public class FileSystemDataRepositoryClient implements DataRepositoryClient {
	private static final Logger logger = LoggerFactory.getLogger(FileSystemDataRepositoryClient.class);

	private static String INSERTBASE64_PATH = "insertBase64";
	private static String INSERTINPUTSTREAM_PATH = "insertInputStream";
	private static String GETBASE64URI_PATH = "getBase64URI";
	private static String GETFILE_PATH = "getFile";
	private static String REMOVEFILE_PATH = "removeFile";

	private static final String TEMP_FOLDER_PREFIX = "dataRepositoryClientTempFolder";
	private static final String TEMP_FILEPREFIX = "";
	private static final String TEMP_FILESUFFIX = "";
	
	private WebResource resource;
	private String hostname;

	@Provider
	public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

		final ObjectMapper defaultObjectMapper = createDefaultMapper();

		@Override
		public ObjectMapper getContext(Class<?> type) {
			return defaultObjectMapper;
		}

		private static ObjectMapper createDefaultMapper() {
			final ObjectMapper mapper = new ObjectMapper();

			return mapper;
		}

	}

	protected static Client defaultClientFactory() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(ObjectMapperProvider.class);
		return Client.create(clientConfig);
	}

	public FileSystemDataRepositoryClient() { }
	
	public FileSystemDataRepositoryClient(String hostname) {
		this(defaultClientFactory(), hostname);
	}

	public FileSystemDataRepositoryClient(Client client, String hostname) {
		this.hostname = hostname;
		resource = client.resource(hostname);
	}

	@Resource(name = "remoteFileRepositoryHostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
		Client cl = defaultClientFactory();
		resource = cl.resource(hostname);
	}
	
	@Override
	public String insertBase64File(String base64uri) {
		ClientResponse response = resource.path(INSERTBASE64_PATH).entity(base64uri, MediaType.MULTIPART_FORM_DATA)
				.accept(MediaType.APPLICATION_XML).post(ClientResponse.class);

		RepositoryFile file = response.getEntity(RepositoryFile.class);
		return UriBuilder.fromPath(this.hostname).path(GETFILE_PATH).path(file.getId()).build().toString();
	}

	@Override
	public String insertBytes(byte[] inputBytes, String dataType) {
		RepositoryFile file = null;
		try (InputStream inputStream = new ByteArrayInputStream(inputBytes)) {
			ClientResponse response = resource.path(INSERTINPUTSTREAM_PATH).queryParam("datatype", dataType)
					.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM).accept(MediaType.APPLICATION_XML)
					.post(ClientResponse.class);

			file = response.getEntity(RepositoryFile.class);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return UriBuilder.fromPath(this.hostname).path(GETFILE_PATH).path(file.getId()).build().toString();
	}

	@Override
	public String insertFileFromUrl(String fileUrl) {
		URL url = null;
		try {
			url = new URL(fileUrl);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		}

		String dataType = null;
		InputStream stream = null;
		try {
			URLConnection con = url.openConnection();
			dataType = con.getContentType();
			stream = con.getInputStream();
		} catch (IllegalArgumentException | IOException e1) {
			logger.error(e1.getMessage(), e1);
		}

		String dataRepositoryUrl = null;
		try {
			dataRepositoryUrl = insertBytes(IOUtils.toByteArray(stream), dataType);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return dataRepositoryUrl;
	}

	@Override
	public String getFileInBase64URI(String fileId) {
		ClientResponse response = resource.path(GETBASE64URI_PATH).path(fileId).get(ClientResponse.class);
		return response.getEntity(String.class);
	}

	@Override
	public String getFileUrl(String fileId) {
		return UriBuilder.fromPath(this.hostname).path(GETFILE_PATH).path(fileId).build().toString();
	}

	@Override
	public void removeFile(String fileId) {
		resource.path(REMOVEFILE_PATH).path(fileId).post(ClientResponse.class);
	}

	@Override
	public String persist(RepositoryFile file) throws Exception {
		//RepositoryFile rf = resource.path(DataRepositoryUtils.PERSIST).entity(file, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(RepositoryFile.class);
		String str = resource.path(DataRepositoryUtils.PERSIST).entity(file, MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class);
		RepositoryFile rf = new ObjectMapper().readValue(new StringReader(str), RepositoryFile.class);
		//file.setLocalImage(new URI("http", rf.getLocalImage().getHost(), rf.getLocalImage().getPath(), rf.getLocalImage().getFragment()));
		file.setLocalImage(rf.getLocalImage());
		if(rf.getState() == State.PERSISTED)
			file.markPersisted();
		file.setId(rf.getId());
		return rf.getId();
	}

	@Override
	public String update(RepositoryFile file) throws Exception {
		return resource.path(DataRepositoryUtils.UPDATE).entity(file, MediaType.APPLICATION_JSON).post(String.class);
	}

	@Override
	public RepositoryFile retrieve(String id) throws Exception {
		//return resource.path(DataRepositoryUtils.RETRIEVE).path(id).accept(MediaType.APPLICATION_JSON).get(RepositoryFile.class);
		String str = resource.path(DataRepositoryUtils.RETRIEVE).path(id).accept(MediaType.APPLICATION_JSON).get(String.class);
		return new ObjectMapper().readValue(new StringReader(str), RepositoryFile.class);
		
	}

	@Override
	public void delete(String id) throws Exception {
		resource.path(DataRepositoryUtils.DELETE).path(id).post();
	}

	@Override
	public List<String> listIds() throws Exception {
		return resource.path(DataRepositoryUtils.LIST_IDS).get(new GenericType<List<String>>() {
		});
	}

	@Override
	public String persistToFolder(List<RepositoryFile> files) throws Exception {
		return resource.path(DataRepositoryUtils.PERSIST_TO_FOLDER).entity(files, MediaType.APPLICATION_JSON)
				.post(String.class);
	}

	@Override
	public String updateToFolder(RepositoryFile file, String folderId) throws Exception {
		return resource.path(DataRepositoryUtils.UPDATE_TO_FOLDER).path(folderId)
				.entity(file, MediaType.APPLICATION_JSON).post(String.class);
	}

	@Override
	public String updateToFolder(List<RepositoryFile> files, String folderId) throws Exception {
		return resource.path(DataRepositoryUtils.UPDATE_TO_FOLDER).path(folderId)
				.entity(files, MediaType.APPLICATION_JSON).post(String.class);
	}

	@Override
	public String addToFolder(RepositoryFile file, String folderId) throws Exception {
		return resource.path(DataRepositoryUtils.ADD_TO_FOLDER).path(folderId).entity(file, MediaType.APPLICATION_JSON)
				.post(String.class);
	}

	@Override
	public String addToFolder(List<RepositoryFile> files, String folderId) throws Exception {
		return resource.path(DataRepositoryUtils.ADD_TO_FOLDER_MULTIPLE_FILES).path(folderId)
				.entity(files, MediaType.APPLICATION_JSON).post(String.class);
	}

	@Override
	public File retrieveFolder(String folderId) throws Exception {  
		List<String> fileIds = listFolder(folderId);
		Path folder = Files.createTempDirectory(TEMP_FOLDER_PREFIX);
		for(String fileId : fileIds) {
			RepositoryFile rf = retrieve(fileId);
			Path f = Files.createFile(folder.resolve(rf.getOriginalName()));
			try (FileOutputStream out = new FileOutputStream(f.toFile())) {
				IOUtils.copy(rf.getInputStream(), out);
			}
		};
		return folder.toFile();
	}

	@Override
	public List<String> listFolder(String folderId) throws Exception {
		return resource.path(DataRepositoryUtils.LIST_FOLDER).path(folderId).get(new GenericType<List<String>>() {
		});
	}

	@Override
	public long getTotalSize() {
		return Long.parseLong(resource.path(DataRepositoryUtils.GET_TOTAL_SIZE).get(ClientResponse.class).getEntity(String.class));
	}

	@Override
	public Long getLastSweep() {
		ClientResponse response = resource.path(DataRepositoryUtils.GET_LAST_SWEEP).get(ClientResponse.class);
		return response.getStatus() == Response.Status.OK.getStatusCode() ? Long.parseLong(response.getEntity(String.class)) : null;
	}

	@Override
	public Long getSweepSizeReduction() {
		ClientResponse response = resource.path(DataRepositoryUtils.GET_SWEEP_SIZE_REDUCTION).get(ClientResponse.class);
		return response.getStatus() == Response.Status.OK.getStatusCode() ? Long.parseLong(response.getEntity(String.class)) : null;
	}

	@Override
	public Long close(RepositoryFile file) throws Exception {
		//return resource.path(DataRepositoryUtils.CLOSE).entity(file, MediaType.APPLICATION_JSON).post(Long.class);
		return -1L;
	}

	public static void main(String[] args) {
		FileSystemDataRepositoryClient client = new FileSystemDataRepositoryClient(
				"http://localhost:8080/data-repository-application/datarepository");

		RepositoryFile file = new RepositoryFile();
		file.setId(UUID.randomUUID().toString());
		file.setDataType("datatype");
		file.setInputStream(new ByteArrayInputStream(new byte[] { 0, 1, 2 }));
		file.setTimestamp(System.currentTimeMillis());

		RepositoryFile file1 = new RepositoryFile();
		file1.setId(UUID.randomUUID().toString());
		file1.setDataType("datatype");
		file1.setInputStream(new ByteArrayInputStream(new byte[] { 0, 1, 2 }));
		file1.setTimestamp(System.currentTimeMillis());

		// ObjectMapper mapper = new ObjectMapper();
		// try {
		// System.out.println(mapper.writeValueAsString(file));
		// } catch (JsonProcessingException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		try {
//			System.out.println(client.persistToFolder(Lists.newArrayList(file, file1)));
			System.out.println(client.listFolder("b537702f-9087-4358-aa9c-85f7d481407c"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
