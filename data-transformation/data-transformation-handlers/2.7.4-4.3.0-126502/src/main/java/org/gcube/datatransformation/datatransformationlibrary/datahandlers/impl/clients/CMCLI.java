//package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.clients;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.Reader;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//import java.util.UUID;
//import java.util.concurrent.Future;
//
//import org.gcube.common.core.informationsystem.client.RPDocument;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.contentmanagement.contentmanager.smsplugin.util.GCubeCollections;
//import org.gcube.contentmanagement.contentmanager.stubs.CollectionReference;
//import org.gcube.contentmanagement.contentmanager.stubs.calls.iterators.RemoteIterator;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentWriter;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.ViewReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.MetadataProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.util.Collections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.views.MetadataView;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeAlternative;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeElement;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeMetadata;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubePart;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.MetadataElements;
//import org.gcube.contentmanagement.storagelayer.storagemanagementservice.stubs.protocol.SMSURLConnection;
//import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
//
//
//public class CMCLI {
//	/**
//	 * @param args
//	 * @throws MalformedURLException 
//	 */
//	public static void main(String[] args) throws MalformedURLException {
//		try {
//			System.setProperty("java.protocol.handler.pkgs", "org.gcube.contentmanagement.storagelayer.storagemanagementservice.stubs.protocol");
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//					System.in));
//			System.out.print("Scope (/gcube/devNext (default) or /d4science.research-infrastructures.eu/Ecosystem: ");
//			String scopestr = in.readLine();
//			if (scopestr.equals(""))
//				scopestr = "/gcube/devNext";
//			
//			GCUBEScope scope = GCUBEScope.getScope(scopestr);
//			String str = "";
//			while (!str.equalsIgnoreCase("q")) {
//				System.out.print("\n" +
//						"Q) Quit\n"+
//						"1) List collections\t" +
//						"2) Find collection by name\t" +
//						"3) Create collection\t" +
//						"4) Delete collection\n" +
//						"5) List collection docs\t" +
//						"6) Get main doc\t" +
//						"7) Get doc content\t" +
//						"8) Get main+meta doc\t" +
//						"9) Add new doc\t" +
//						"10) Del doc\n" +
//						"11) List col views\t" +
//						"12) Create meta view\t" +
//						"13) List meta documents\t" +
//						"14) Add alternative\t" +
//						"15) Add part\t" +
//						"16) Add metadata\t" +
//						"17) Del metadata\n" +
//						"18) List alternatives of a doc\t" +
//						"19) Get alternative\t" +
//						"20) Del metadata view\t" +
//						"21) Get single document from view\t" +
//						"22) Collections with views\n" +
//						"23) Get IDs of a collection\t" +
//						"24) Show views cardinalities\t" +
//						"25) Test single collection\t" +
//						"26) Test view cardinality\t" +
//						"\n" +
//				"");
//				str = in.readLine();
//				process(in, str, scope);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static void process(BufferedReader in, String cmd, GCUBEScope scope) throws IOException{
//		if (cmd.equalsIgnoreCase("1")){ //List collections
//			System.out.println("Collections in "+scope+" :");
//			try {
//				for (Collection collection:Collections.list(scope)){
//					System.out.println(collection);
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("2")){ //Find collection by name
//
//			System.out.print("Collection name: ");
//			String collectionName = in.readLine();
//
//			System.out.println("Collections in "+scope+" with name \""+collectionName+"\":");
//			try {
//				for (Collection collection:Collections.findByName(scope, collectionName)){
//					System.out.println(collection);
//				}
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}else if (cmd.equalsIgnoreCase("3")){ //Create collection
//
//			System.out.print("Collection name: ");
//			String collectionName = in.readLine();
//			System.out.print("Collection description: ");
//			String collectionDescription = in.readLine();
//			boolean userCollection = true;
//
//			//we don't want to propagate the request to others CM
//			boolean propagateRequest = false;
//
//			//we want the collection to be readable and writable
//			boolean readable = true;
//			boolean writable = true;
//
//			//finally we create the collection
//			List<CollectionReference> collectionReferences;
//			try {
//				collectionReferences = GCubeCollections.createGCubeCollection(propagateRequest, collectionName, collectionDescription, userCollection, readable, writable, scope);
//
//				System.out.println("Created collections:");
//				for (CollectionReference collectionReference:collectionReferences){
//					System.out.println("Collection id: "+collectionReference.getCollectionID());
//					if (collectionReference.getReader()!=null) System.out.println("Reader EPR: "+collectionReference.getReader());
//					if (collectionReference.getWriter()!=null) System.out.println("Writer EPR: "+collectionReference.getWriter());
//					System.out.println();
//				}
//				System.out.println("Collections in "+scope+" with name \""+collectionName+"\":");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("4")){ //Delete collection
//
//			System.out.print("Collection ID: ");
//			String collectionId = in.readLine();
//
//			//we want to delete the collection profile
//			boolean deleteProfile = true;
//
//			System.out.println("Requesting delete:");
//			List<Future<RPDocument>> results;
//			try {
//				results = GCubeCollections.deleteGCubeCollection(collectionId, deleteProfile, scope);
//
//				System.out.println("Results:");
//				for (Future<RPDocument> result:results){
//					RPDocument doc = result.get();
//					System.out.println("Deleted delegate for collection "+doc.getKey());
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else if (cmd.equalsIgnoreCase("5")){ //List docs of a collection
//
//			System.out.print("Collection ID: ");
//			String collectionId = in.readLine();
//			DocumentProjection dp = Projections.document().with(Projections.NAME);//.with(Projections.METADATA);
////			DocumentProjection dp = Projections.document();
//			DocumentReader cmReader;
//			try {
//				System.out.println("Collection ID: "+ collectionId );
//				cmReader = new DocumentReader(collectionId, scope);
//				RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//
//				int i = 0;
//				System.out.println("Collection documents:");
//
//				while(documentIterator.hasNext()){
//					GCubeDocument document = documentIterator.next();
//
//					//					String collid = document.collectionID();
//					String uri = document.uri().toURL().toString();
//					String id = document.id();
//					int parts = document.parts().size();
//					int alternatives = document.alternatives().size();
//					int metadata = document.metadata().size();
//					int annotations = document.annotations().size();
//
//					System.out.printf("%d id: %s URL: %s parts: %d alternatives: %d metadata: %d annotations: %d %n", i, id, uri, parts, alternatives, metadata, annotations);
//					i++;
//					if (i > 5){
//						System.out.println("There are more than "+i+" documents.");
//						break;
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("6")){ //get main doc
//
//			System.out.print("Collection ID: ");
//			String collectionId = in.readLine();
//			System.out.print("Document ID: ");
//			String documentId = in.readLine();
//			try {
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.LENGTH,
//						Projections.opt(Projections.BYTESTREAM_URI),
//						Projections.NAME
//				);
//
//				//then we retrieve the document, we are requesting only the main document
//				GCubeDocument document = cmReader.get(documentId, dp);
//
//				System.out.println("Retrieved document:");
//				System.out.println("id: " + document.id());
//				System.out.println("name: " + document.name());
//				System.out.println("length: " + document.length());
//				System.out.println("URL: " + document.uri().toURL().toString());
//				if (document.bytestreamURI()!=null)
//					System.out.println("contentURL: " + document.bytestreamURI().toURL().toString());
//				System.out.println();
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("7")){ //Get doc content
//
//			System.out.print("Collection ID: ");
//			String collectionId = in.readLine();
//			System.out.print("Document ID: ");
//			String documentId = in.readLine();
//			System.out.print("Output file: ");
//			String outputfile = in.readLine();
//			//we instantiate the CMReader
//			try {
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
////				DocumentProjection dp = Projections.document().with(Projections.LENGTH,
////						Projections.opt(Projections.BYTESTREAM_URI),Projections.opt(Projections.BYTESTREAM),
////						Projections.NAME
////				);
//				DocumentProjection dp = Projections.document().with(
//						Projections.opt(Projections.BYTESTREAM_URI),Projections.opt(Projections.BYTESTREAM),
//						Projections.NAME
//				);
//
//				//then we retrieve the document, we are requesting only the main document
//				GCubeDocument document = cmReader.get(documentId, dp);
//
//				System.out.println("Retrieved document:");
//				System.out.println("id: " + document.id());
//				System.out.println("name: " + document.name());
//				System.out.println("document URL: " + document.uri().toURL().toString());
//				System.out.println();
//
//				System.out.println("Retrieving content...");
//
//				InputStream ins = getContnetStream(document, scope);
//				if (ins == null){
//					System.out.println("Document has no content.");					
//				}else{
//					//finally we copy the content to a tmp file
//					File f=new File(outputfile);
//					OutputStream out=new FileOutputStream(f);
//
//					byte[] buf = new byte[1024];
//					int len;
//					while ((len = ins.read(buf)) > 0){
//						out.write(buf, 0, len);
//					}
//
//					out.close();
//					ins.close();
//				}
//				System.out.println("File is created.");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("8")){ //Get main+meta doc
//			//devNext
//			//Collection ID: cba3b770-f1e2-11dd-836f-ed8453cc0b6c
//			//Document ID: d2540ed0-f1e2-11dd-836f-ed8453cc0b6c
//
//			System.out.print("Collection ID: ");
//			String collectionId = in.readLine();
//			System.out.print("Document ID: ");
//			String documentId = in.readLine();
//			//we instantiate the CMReader
//			try {
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
////				DocumentProjection dp = Projections.document().with(Projections.LENGTH,
////						Projections.MIME_TYPE,
////						Projections.NAME,
////						Projections.METADATA
////				);
//				DocumentProjection dp = Projections.document().with(
//						Projections.MIME_TYPE,
//						Projections.NAME,
//						Projections.METADATA
//				);
//
//				//then we retrieve the document, we are requesting only the main document
//				GCubeDocument document = cmReader.get(documentId, dp);
//
//				System.out.println("Retrieved document:");
//				System.out.println("id: " + document.id());
//				System.out.println("name: " + document.name());
//				System.out.println("mimeType: " + document.mimeType());
//				System.out.println("language: " + document.language());
//				System.out.println();
//
//				for (GCubeMetadata metadata:document.metadata()){
//					System.out.println("Metadata:");
//					System.out.println(" id: " + metadata.id());
//					System.out.println(" mimeType: " + metadata.mimeType());
//					System.out.println(" length: " + metadata.length());
//					System.out.println(" Metadata info:");
//					System.out.println("  metadata name: " + metadata.name());
//					System.out.println("  schema name: " + metadata.schemaName());
//					System.out.println("  schema URI: " + metadata.schemaURI().toString());
//					System.out.println("  language: " + metadata.language());
//					byte[] content = metadata.bytestream();
//					String payload = new String(content);
//					System.out.println("  payload: " + payload);
//					System.out.println();
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("9")){ //Add a new doc
//
//			DocumentWriter cmWriter;
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Doc name: ");
//				String docName = in.readLine();
//				System.out.print("Mime type: ");
//				String mimetype = in.readLine();
//				System.out.print("File: ");
//				String fname = in.readLine();
//
//				//we instantiate the CMWriter
//				cmWriter = new DocumentWriter(collectionId, scope);
//
//				//we create an instance of gcube document
//				GCubeDocument myNewDocument = new GCubeDocument();
//
//				//then we ask for adding the new document
//				File file = new File(fname);
//				FileInputStream fis = new FileInputStream(file);
//				BufferedInputStream bis = new BufferedInputStream(fis);
//
//				myNewDocument.setMimeType(mimetype);
//				myNewDocument.setBytestream(bis);
//				myNewDocument.setName(docName);
//				String myNewDocumentId = cmWriter.add(myNewDocument);
//
//				System.out.println("document created, id: " + myNewDocumentId);
//
//				//finally we retrieve the created document
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document();
//
//				GCubeDocument document = cmReader.get(myNewDocumentId,dp);
//
//				System.out.println("Created document:");
//				System.out.println("id: " + document.id());
//				System.out.println("name: " + document.name());
//				System.out.println("mimeType: " + document.mimeType());
//				System.out.println("length: " + document.length());
//				//				System.out.println("contentURL: " + document.getContentURL());
//
//				//				System.out.println("parts: " + document.getParts().size());
//				//				System.out.println("alternatives:" + document.getAlternatives().size());
//				System.out.println("metadata: " + document.metadata().size());
//				//				System.out.println("annotations: " + document.getAnnotations().size());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("10")){ //Del doc
//
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Doc ID: ");
//				String documentId = in.readLine();
//
//				DocumentWriter cmWriter = new DocumentWriter(collectionId, scope);
//
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//				GCubeDocument document = cmReader.get(documentId,dp);
//
//				cmWriter.delete(document);
//				System.out.println("document removed.");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("11")){ //List views
//
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//
//				//we instantiate a collection manager.
//				List<Collection> collections = Collections.findById(scope, collectionId);
//				if (collections.isEmpty()){
//					System.out.print("Collection not found.");
//				}else{
//					MetadataView metadataView = new MetadataView(scope);
//					metadataView.setCollectionId(collectionId);
//					List<MetadataView> views = metadataView.findSimilar();
//					System.out.println("found "+views.size()+" views:");
//					for (MetadataView view:views) System.out.println(view);
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("12")){ //Create metadata view
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("View name: ");
//				String viewname = in.readLine();
//				String viewdesc = viewname+"_desc";
//				System.out.print("View desc: "+viewdesc);
//				System.out.print("View metadata schema name: ");
//				String viewmetaname = in.readLine();
//				System.out.print("View metadata schema URI: ");
//				String viewmetaschema = in.readLine();
//
//				MetadataView myView = new MetadataView(scope);
//				//				myView.setCollectionId(collectionId);
//				myView.setCollectionId(collectionId);
//				myView.setName(viewname);
//				myView.setDescription(viewdesc);
//				myView.setProjection(Locale.ENGLISH, viewmetaname, new URI(viewmetaschema));
//				myView.setIndexable(true);
//				myView.setUserCollection(true);
//				myView.setEditable(true);
//
//				myView.setIndexable(false);
//
//				myView.publishAndBroadcast();
//
//				System.out.println("View created: "+myView+" with ID: "+myView.id());
//
//				MetadataView tView = new MetadataView(scope);
//				tView.setId(myView.id());
//				List<MetadataView> newView = tView.findSimilar();
//
//				if (newView.isEmpty()){
//					System.out.println("Metadata view not there!");					
//				}else{
//					MetadataView view = newView.get(0);
//					ViewReader reader = view.reader();
//
//					MetadataProjection mp = Projections.metadata();
//					System.out.println("Metadata:");
//					RemoteIterator<GCubeDocument> iterator = reader.get(mp);
//					while(iterator.hasNext()){
//						GCubeDocument doc = iterator.next();
//						MetadataElements elements = doc.metadata();
//						for (GCubeMetadata e: elements){
//							System.out.println(e);
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("13")){ //List meta documents
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Metadata View ID: ");
//				String metadataviewId = in.readLine();
//
//				MetadataView tView = new MetadataView(scope);
//				tView.setCollectionId(collectionId);
//				tView.setId(metadataviewId);
//				List<MetadataView> newView = tView.findSimilar();
//
//				MetadataView view = newView.get(0);
//				ViewReader reader = view.reader();
//				MetadataProjection mp = Projections.metadata();
//				System.out.println("View ID:"+view.id());
//				System.out.println("View Schema:"+view.schemaName());
//				System.out.println("View Lang:"+view.language());
//				System.out.println("View Cardinality:"+view.cardinality());
//				System.out.println("Metadata:");
//
//
//				RemoteIterator<GCubeDocument> iterator = reader.get(
//						Projections.metadata().withValue(Projections.SCHEMA_URI, view.schemaURI())
//				);
//				int i = 0;
//				while(iterator.hasNext()){
//					GCubeDocument doc = iterator.next();
//					System.out.println("**********DOCUEMNT " + i + " START****************");
//					System.out.println(doc);
//					System.out.println("**********DOCUEMNT END****************");
//					MetadataElements elements = doc.metadata();
//					for (GCubeMetadata e: elements){
//						System.out.println("************* METADATA START ****************");
//						System.out.println(e);
//						System.out.println(e.uri());						
//						System.out.println("************* METADATA END ****************");
//					}
//					i++;
//					if (i > 6){
//						System.out.println("We have more than "+i+" documents in this collection.");
//						break;
//					}
//
//				}
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("14")){ //Add alternative
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Doc ID: ");
//				String documentId = in.readLine();
//				System.out.print("Representation doc: ");
//				String fname = in.readLine();
//
//
//				//we create the alternative to add
//				GCubeAlternative alternative = new GCubeAlternative();
//				alternative.setMimeType("text/xml");
//				alternative.setType("testalternativerole");
//				//then we ask for adding the new document
//				File file = new File(fname);
//				FileInputStream fis = new FileInputStream(file);
//				BufferedInputStream bis = new BufferedInputStream(fis);
//				alternative.setBytestream(bis);
//
//				////////////////////
//				DocumentWriter cmWriter = new DocumentWriter(collectionId, scope);
//				GCubeDocument document = new GCubeDocument(documentId);
//				document.trackChanges();
//				document.alternatives().add(alternative);
//				cmWriter.update(document);
//				System.out.println("document updated");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("15")){ //Add part
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Doc ID: ");
//				String documentId = in.readLine();
//				System.out.print("Part doc: ");
//				String fname = in.readLine();
//				System.out.print("Part mimetype: ");
//				String partmimetype = in.readLine();
//
//				//we create the alternative to add
//				GCubePart part = new GCubePart();
//				part.setMimeType(partmimetype);
//				//then we ask for adding the new document
//				File file = new File(fname);
//				FileInputStream fis = new FileInputStream(file);
//				BufferedInputStream bis = new BufferedInputStream(fis);
//
//				part.setBytestream(bis);
//
//				DocumentWriter cmWriter = new DocumentWriter(collectionId, scope);
//
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//				GCubeDocument document = cmReader.get(documentId,dp);
//
//				document.setMimeType("multipart/mixed");
//				document.parts().add(part);
//				cmWriter.update(document);
//
//				System.out.println("document updated");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("16")){ //Add metadata
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Doc ID: ");
//				String documentId = in.readLine();
//				System.out.print("Metadata doc: ");
//				String fname = in.readLine();
//				System.out.print("Metadata schema name: ");
//				String metaschemaname = in.readLine();
//				System.out.print("Metadata schema URI: ");
//				String metaschemaURI = in.readLine();
//				System.out.print("Metadata lang: EN\n");
//				//				String metalang = in.readLine();
//
//				//we create the alternative to add
//				GCubeMetadata meta = new GCubeMetadata();
//				meta.setSchemaName(metaschemaname);
//				meta.setSchemaURI(new URI(metaschemaURI));
//				meta.setLanguage(Locale.ENGLISH);
//				//then we ask for adding the new document
//				File file = new File(fname);
//				FileInputStream fis = new FileInputStream(file);
//				BufferedInputStream bis = new BufferedInputStream(fis);
//
//				meta.setBytestream(bis);
//
//				DocumentWriter cmWriter = new DocumentWriter(collectionId, scope);
//
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//				GCubeDocument document = cmReader.get(documentId,dp);
//
//				document.metadata().add(meta);
//				cmWriter.update(document);
//
//				System.out.println("document updated");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else if (cmd.equalsIgnoreCase("17")){ //Del metadata
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Doc ID: ");
//				String documentId = in.readLine();
//				System.out.print("Metadata schema name: ");
//				String metaschemaname = in.readLine();
//				System.out.print("Metadata schema URI: ");
//				String metaschemaURI = in.readLine();
//				System.out.print("Metadata lang: EN\n");
//				//				String metalang = in.readLine();
//
//				//we create the alternative to add
//				GCubeMetadata meta = new GCubeMetadata();
//				meta.setSchemaName(metaschemaname);
//				meta.setSchemaURI(new URI(metaschemaURI));
//				meta.setLanguage(Locale.ENGLISH);
//				//then we ask for adding the new document
//
//				DocumentWriter cmWriter = new DocumentWriter(collectionId, scope);
//
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//				GCubeDocument document = cmReader.get(documentId,dp);
//
//				document.metadata().remove(meta);
//				cmWriter.update(document);
//
//				System.out.println("document updated");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("18")){ //List alternatives of a doc
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Document ID: ");
//				String documentId = in.readLine();
//
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.ALTERNATIVE);
//				GCubeDocument document = cmReader.get(documentId,dp);
//
//				Iterator<GCubeAlternative> altiter = document.alternatives().iterator();
//				while (altiter.hasNext()) {
//					GCubeAlternative alternative = (GCubeAlternative) altiter.next();
//					System.out.println("Alternative ID: "+alternative.id());
//					System.out.println("Mimetype: "+alternative.mimeType());
//					System.out.println("Role: "+alternative.type());
//				}
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("19")){ //Get alternative
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("Document ID: ");
//				String documentId = in.readLine();
//				System.out.print("Alternative ID: ");
//				String alternativeId = in.readLine();
//				System.out.print("Output file: ");
//				String outputfile = in.readLine();
//
//				DocumentReader cmReader = new DocumentReader(collectionId, scope);
//				DocumentProjection dp = Projections.document().with(Projections.ALTERNATIVE);
//				GCubeDocument document = cmReader.get(documentId,dp);
//				GCubeAlternative alternative = document.alternatives().get(alternativeId);
//				System.out.println("Alternative ID: "+alternative.id());
//				System.out.println("Mimetype: "+alternative.mimeType());
//				System.out.println("Role: "+alternative.type());
//
//
//				InputStream ins = getContnetStream(alternative, scope);
//				if (ins == null){
//					System.out.println("Alternative has no content.");					
//				}else{
//					//finally we copy the content to a tmp file
//					File f=new File(outputfile);
//					OutputStream out=new FileOutputStream(f);
//
//					byte[] buf = new byte[1024];
//					int len;
//					while ((len = ins.read(buf)) > 0){
//						out.write(buf, 0, len);
//					}
//
//					out.close();
//					ins.close();
//				}
//				System.out.println("File is created.");
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("20")){ //Del metadata view
//			try {
//				System.out.print("Collection ID: ");
//				String collectionId = in.readLine();
//				System.out.print("View ID: ");
//				String viewid = in.readLine();
//
//				MetadataView myView = new MetadataView(scope);
//				//				myView.setCollectionId(collectionId);
//				myView.setCollectionId(collectionId);
//				myView.setId(viewid);
//
//
//				myView.publishAndBroadcast();
//
//				System.out.println("View created: "+myView);
//
//				MetadataView tView = new MetadataView(scope);
//				tView.setId(myView.id());
//				List<MetadataView> newView = tView.findSimilar();
//
//				if (newView.isEmpty()){
//					System.out.println("Metadata view not there!");					
//				}else{
//					MetadataView view = newView.get(0);
//					ViewReader reader = view.reader();
//
//					MetadataProjection mp = Projections.metadata();
//					System.out.println("Metadata:");
//					RemoteIterator<GCubeDocument> iterator = reader.get(mp);
//					while(iterator.hasNext()){
//						GCubeDocument doc = iterator.next();
//						MetadataElements elements = doc.metadata();
//						for (GCubeMetadata e: elements){
//							System.out.println(e);
//						}
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("21")){ //Get sin
//			//devNext
//			//Collection ID: cba3b770-f1e2-11dd-836f-ed8453cc0b6c
//			//Document ID: d2540ed0-f1e2-11dd-836f-ed8453cc0b6c
//
//
//			System.out.print("View ID: ");
//			String viewid = in.readLine();
//			System.out.print("Doc ID: ");
//			String docId = in.readLine();
//			try{
//				MetadataView tView = new MetadataView(scope);
//				tView.setId(viewid);
//				List<MetadataView> newView = tView.findSimilar();
//
//				MetadataView view = newView.get(0);
//				ViewReader reader = view.reader();
//				MetadataProjection mp = Projections.metadata();
//				System.out.println("View ID:"+view.id());
//				System.out.println("View Schema:"+view.schemaName());
//				System.out.println("View Lang:"+view.language());
//				System.out.println("View Cardinality:"+view.cardinality());
//				System.out.println("Metadata:");
//
//				GCubeDocument document = reader.get(docId, mp);
//
//				System.out.println("Retrieved document:");
//				System.out.println("id: " + document.id());
//				System.out.println("name: " + document.name());
//				System.out.println("mimeType: " + document.mimeType());
//				System.out.println();
//
//				for (GCubeMetadata metadata:document.metadata()){
//					System.out.println("Metadata:");
//					System.out.println(" id: " + metadata.id());
//					System.out.println(" creationTime: " + metadata.creationTime());
//					System.out.println(" mimeType: " + metadata.mimeType());
//					System.out.println(" length: " + metadata.length());
//					System.out.println(" Metadata info:");
//					System.out.println("  metadata name: " + metadata.name());
//					System.out.println("  schema name: " + metadata.schemaName());
//					System.out.println("  schema URI: " + metadata.schemaURI().toString());
//					System.out.println("  language: " + metadata.language());
//					System.out.println();
//					System.out.println("Retrieving metadata content...");
//
//					InputStream ins = getContnetStream(metadata, scope);
//					if (ins == null){
//						System.out.println("Document has no content.");					
//					}else{
//						System.out.println("Content: " + convertStreamToString(ins));					
//						ins.close();
//					}
//					System.out.println("Done");
//
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("22")){ //List collections with views
//			System.out.println("Collections in "+scope+" :");
//			try {
//				for (Collection collection:Collections.list(scope)){
////					System.out.println(collection);
//					MetadataView metadataView = new MetadataView(scope);
//					metadataView.setCollectionId(collection.getId());
//					List<MetadataView> views = metadataView.findSimilar();
//					System.out.println("\nCollection "+collection.getName() + " " + collection.getId() +" has "+views.size()+" views");
//					if (views.size() > 0){
//						System.out.println("******************");
//						for (MetadataView view:views) System.out.println(view);
//						System.out.println("******************");						
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("23")){ //Get sin
//			//devNext
//			//Collection ID: cba3b770-f1e2-11dd-836f-ed8453cc0b6c
//			//Document ID: d2540ed0-f1e2-11dd-836f-ed8453cc0b6c
//
//
//			System.out.print("Collection ID: ");
//			String colid = in.readLine();
//			try{
//				DocumentReader cmReader = new DocumentReader(colid, scope);
//				File tempIDsStorage = null;
//				tempIDsStorage = File.createTempFile("DTS", ".tmp");
//				System.out.println("File storing gDoc IDs: " + tempIDsStorage.getName());
//			    BufferedWriter out = new BufferedWriter(new FileWriter(tempIDsStorage));
//				DocumentProjection dp = Projections.document().with(Projections.NAME);
//				RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//				int i = 0;
//				String seperator = UUID.randomUUID().toString();
//
//				while(documentIterator.hasNext()){
//					i++;
//					if ((i % 100) == 0)
//						System.out.println("Pre-fetched IDs for " + i +" docs.");
//					GCubeDocument document = documentIterator.next();
//				    out.write(document.id()+"\n");
//				    out.write(document.uri().toString()+"\n");
//				    out.write(document.name()+"\n");
//				    out.write(seperator+"\n");
//				    
//				}
//			    out.close();
//			    System.out.println("Done prefetching IDs and staff...");
//			    
//			    String id, uri, name, str;
//			    boolean firstline =  true;
//			    BufferedReader inb = new BufferedReader(new FileReader(tempIDsStorage));
//			    while (( id = inb.readLine()) != null){
//			    	if (id.isEmpty())
//			    		break;
//			    	uri = inb.readLine();
//			    	name = "";
//			    	
//			    	str = inb.readLine();
//			    	firstline = true;
//			    	while(!str.equals(seperator)){
//			    		if (!firstline)
//			    			name += "\n";
//			    		name += str;
//			    		firstline = false;
//			    		str = inb.readLine();
//			    	}
//			    	System.out.println("ID: "+id+ " URI: "+ uri +"\nName: "+name);
//			    	System.out.println("-----------------------------------------");
//		        }
//			    inb.close();
//			    
//				System.out.println("Done");
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if (cmd.equalsIgnoreCase("24")){ //Show views cardinality
//			try {
//				for (Collection collection:Collections.list(scope)){
//						System.out.print(collection.getName() + "\t\t" + collection.getId()+ "\t\t");
//						MetadataView metadataView = new MetadataView(scope);
//						metadataView.setCollectionId(collection.getId());
//						List<MetadataView> views = metadataView.findSimilar();
//						for (MetadataView view:views){ 
//							System.out.print(view.id()+ "\t\t" +view.schemaName()+ "\t\t" + view.cardinality()+ "\t\t");
//						}
//						System.out.println(" ");						
//					}	
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		} else if (cmd.equalsIgnoreCase("25")){ //Test single collection
//			System.out.print("Collection ID to test: ");
//			String colid = in.readLine();
//
//			try {
//				for (Collection collection:Collections.list(scope)){
//					if (collection.getId().equals(colid)){
//					System.out.print(collection.getName() + "\t" + collection.getId()+ "\n");
//					MetadataView metadataView = new MetadataView(scope);
//					metadataView.setCollectionId(collection.getId());
//					List<MetadataView> views = metadataView.findSimilar();
//					for (MetadataView view:views){ 
//						System.out.print(view.id()+ "\t" +view.schemaName()+ "\t" + view.cardinality()+ "\n");
//						ViewReader reader = view.reader();
//						MetadataProjection mp = Projections.metadata();
//
//						RemoteIterator<GCubeDocument> iterator = reader.get(
////								Projections.metadata().withValue(Projections.SCHEMA_URI, view.schemaURI())
//								mp
//						);
//						while(iterator.hasNext()){
//							GCubeDocument doc = iterator.next();
//							System.out.println("Doc ID: " + doc.id());
//							MetadataElements elements = doc.metadata();
//							for (GCubeMetadata e: elements){
//								if (!e.schemaName().equals(view.schemaName()))
//									continue;
//								System.out.println("Meta doc ID: " + e.id());
//								InputStream ins = getContnetStream(e, scope);
//								if (ins == null){
//									System.out.println("Document has no content.");					
//								}else{
//									System.out.println("Content: " + convertStreamToString(ins));					
//									ins.close();
//								}
//							}
//							break; //only a single document;
//						}
//
//						
//					}
//					System.out.println(" ");						
//					}
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		} else if (cmd.equalsIgnoreCase("26")){ //Test view cardinality
//			System.out.print("Meta collection ID to test: ");
//			String colid = in.readLine();
//
//			try {
//					MetadataView metadataView = new MetadataView(scope);
//					metadataView.setId(colid);					
//					List<MetadataView> views = metadataView.findSimilar();
//					for (MetadataView view:views){ 
//						System.out.print(view.id()+ "\t" +view.schemaName()+ "\t" + view.cardinality()+ "\n");
//						ViewReader reader = view.reader();
//
//						RemoteIterator<GCubeDocument> iterator = reader.get(
//								Projections.metadata().withValue(Projections.SCHEMA_URI, view.schemaURI())
//						);
//						int counter = 0;
//						while(iterator.hasNext()){
//							GCubeDocument doc = iterator.next();
////							System.out.println("Doc ID: " + doc.id());
//							MetadataElements elements = doc.metadata();
//							for (GCubeMetadata e: elements){
//								if (!e.schemaName().equals(view.schemaName()))
//									continue;
//								
////								System.out.println("Meta doc ID: " + e.id());
///*								InputStream ins = getContnetStream(e, scope);
//								if (ins == null){
//									System.out.println("Document has no content.");					
//								}else{
//									System.out.println("Content: " + convertStreamToString(ins));					
//									ins.close();
//								}*/
//								counter++;
//							}
//						}
//						System.out.println("Real cardinality: "+counter);						
//						
//					}
//					System.out.println(" ");						
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//	}
//
//	private static InputStream getContnetStream(GCubeElement document, GCUBEScope scope) throws MalformedURLException, IOException {
//		if (document.bytestreamURI() != null){
//			if (document.bytestreamURI().getScheme().equals("sms")){
//				try {
//					return SMSURLConnection.openConnection(document.bytestreamURI(), scope.toString()).getInputStream();
//				} catch (URISyntaxException e) {
//					System.out.print("Cannot get stream for metadata, "+document.id());
//					return null;
//				}
//			}else{
//				return document.bytestreamURI().toURL().openStream();
//			}
//		}else if (document.bytestream() != null){
//			byte[] content = document.bytestream();
//			ByteArrayInputStream ins = new ByteArrayInputStream(content);
//			return ins;
//		}else
//			return null;
//	}
//
//	public static String convertStreamToString(InputStream is)	throws IOException {
//		/*
//		 * To convert the InputStream to String we use the
//		 * Reader.read(char[] buffer) method. We iterate until the
//		 * Reader return -1 which means there's no more data to
//		 * read. We use the StringWriter class to produce the string.
//		 */
//		if (is != null) {
//			Writer writer = new StringWriter();
//
//			char[] buffer = new char[1024];
//			try {
//				Reader reader = new BufferedReader(
//						new InputStreamReader(is, "UTF-8"));
//				int n;
//				while ((n = reader.read(buffer)) != -1) {
//					writer.write(buffer, 0, n);
//				}
//			} finally {
//				is.close();
//			}
//			return writer.toString();
//		} else {        
//			return "";
//		}
//	}
//
//}
//	
