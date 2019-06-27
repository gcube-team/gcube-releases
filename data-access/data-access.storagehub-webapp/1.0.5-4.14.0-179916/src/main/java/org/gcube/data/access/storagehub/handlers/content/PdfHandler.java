package org.gcube.data.access.storagehub.handlers.content;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import org.gcube.common.storagehub.model.annotations.MimeTypeHandler;
import org.gcube.common.storagehub.model.items.PDFFileItem;
import org.gcube.common.storagehub.model.items.nodes.PDFContent;
import org.gcube.common.storagehub.model.types.ItemAction;

import com.itextpdf.text.pdf.PdfReader;

@MimeTypeHandler("application/pdf")
public class PdfHandler implements ContentHandler {

	public static final String NUMBER_OF_PAGES 		= "xmpTPg:NPages";
	public static final String PRODUCER 			= "producer";
	public static final String VERSION 				= "version";
	public static final String AUTHOR 				= "Author";
	public static final String TITLE 				= "dc:title";
	
	PDFContent content = new PDFContent();


	@Override
	public void initiliseSpecificContent(InputStream is, String fileName) throws Exception {
		PdfReader reader = new PdfReader(is);
		content.setNumberOfPages(Long.valueOf(reader.getNumberOfPages()));
		content.setVersion(String.valueOf(reader.getPdfVersion()));
		HashMap<String, String> fileInfo = reader.getInfo();
		content.setAuthor(fileInfo.containsKey(AUTHOR)?fileInfo.get(AUTHOR):"n/a");
		content.setProducer(fileInfo.containsKey(PRODUCER)?fileInfo.get(PRODUCER):"n/a");
		content.setTitle(fileInfo.containsKey(TITLE)?fileInfo.get(TITLE):"n/a");
	}

	@Override
	public PDFContent getContent() {
		return content;
	}
	
	public PDFFileItem buildItem(String name, String description, String login) {
		PDFFileItem item =  new PDFFileItem();
		Calendar now = Calendar.getInstance();
		item.setName(name);
		item.setTitle(name);
		item.setDescription(description);
		//item.setCreationTime(now);
		item.setHidden(false);
		item.setLastAction(ItemAction.CREATED);
		item.setLastModificationTime(now);
		item.setLastModifiedBy(login);
		item.setOwner(login);
		item.setContent(this.content);
		return item;
	}


}
