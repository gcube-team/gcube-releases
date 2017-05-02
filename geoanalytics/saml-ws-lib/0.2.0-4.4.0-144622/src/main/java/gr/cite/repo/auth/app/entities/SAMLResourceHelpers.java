package gr.cite.repo.auth.app.entities;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.io.Files;

public class SAMLResourceHelpers {

	public static String readCertificate(String certificateFilename) throws IOException{
		List<String> certLines = Files.readLines(new File(certificateFilename), Charsets.UTF_8);
		
		Iterator<String> filteredLines = Iterators.filter(certLines.iterator(), new Predicate<String>(){
			public boolean apply(String input) {
				return !input.startsWith("-----");
			}
		});
		
		return Joiner.on("").join(filteredLines);
	}
	
	public static byte[] readPrivateKey(String privateKeyFileName) throws IOException{
		return Files.toByteArray(new File(privateKeyFileName));
	}
}
