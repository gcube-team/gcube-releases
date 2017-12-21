import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;

public class testProxy {
	
	public String adaptIS(String a) {
		String ab = a.replace(" ", "é");
		// System.out.println(ab);
		return ab;
	}

	public String adaptCert(String b) {
		String bc = b.replace("é", "\n");
		// System.out.println(bc);
		return bc;
	}
	
	public String removeHeader(String c) {
		String cd = c.replace("-----BEGIN" + "\n" + "CERTIFICATE-----", "-----BEGIN CERTIFICATE-----");
		String ce = cd.replace("-----END" + "\n" + "CERTIFICATE-----", "-----END CERTIFICATE-----");
		String cf = ce.replace("-----BEGIN" + "\n" + "PRIVATE" + "\n" + "KEY-----", "-----BEGIN PRIVATE KEY-----");
		String cg = cf.replace("-----END" + "\n" + "PRIVATE" + "\n" + "KEY-----", "-----END PRIVATE KEY-----");
		return cg;
	}
	
	public static String getScriptFromFile(File file) throws IOException {
	    if (file == null) {
	      return null;
	    }
	    FileInputStream input = new FileInputStream(file);
	    String everything = IOUtils.toString(input);
	    input.close();
	    return everything;
	  }
	
	public String testproxy() throws Exception{
		//VMProvider vmp = new VMProvider();
		File f = new File("/tmp/test");

		
		
		String certPath = this.getScriptFromFile(f);


		certPath = this.adaptIS(certPath);
		certPath = this.adaptCert(certPath);
		certPath = this.removeHeader(certPath);

		
		return certPath;
	}
	
	public String returnID() {
		String line;

		try {
			String userName = System.getProperty("user.name");
			String command = "id -u " + userName;
			Process child = Runtime.getRuntime().exec(command);

			// Get the input stream and read from it
			BufferedReader in = new BufferedReader(new InputStreamReader(child.getInputStream()));
			while ((line = in.readLine()) != null) {
				return line;
			}
			in.close();
		} catch (IOException e) {
		}
		return null;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
//		SecurityTokenProvider.instance.set("2eceaf27-0e22-4dbe-8075-e09eff199bf9-98187548");
//		
		// TODO Auto-generated method stub
		testProxy tp = new testProxy();
		System.out.println(tp.testproxy());
		
		//String a = System.getProperty("user.id");
		//System.out.println(a);
		//System.out.println(tp.returnID());
	
	}
	

}


