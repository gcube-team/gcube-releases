import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 */

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 12, 2014
 * 
 */
public class StorageIDResolverTest {
	
	public static String hostname = "http://dev.d4science.org/uri-resolver/smp?";

	// CASE IS NULL
//	public static final String uri = hostname+"fileName=HCAF_2050&contentType=text%2Fcsv&smp-uri=smp%3A%2F%2FShare%2Fcd8cb73f-feb6-4072-864c-3bb57f81ad56%2FHCAF+2050%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevVwnFtqGknsyTZoV3fuG3iZ3%2BAsJaJDUH7F%2FELBV1lV8smBnSfc4vhDULwoWY6CWZ2tGj15BzeBI%3D";
//	public static String filename = "HCAF_2050";
//	public static String ext = "csv";

	// CASE OK
//	 public static final String uri = hostname+"fileName=gattino02.jpg&contentType=audio%2Fmpeg&smp-uri=smp%3A%2F%2FHome%2Ffrancesco.mangiacrapa%2FWorkspace%2Fgattino02.jpg%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevVwnFtqGknsyTZoV3fuG3iZ3%2BAsJaJDUH7F%2FELBV1lV8smBnSfc4vhDULwoWY6CWZ2tGj15BzeBI%3D";
//	 public static String filename ="gattino02";
//	 public static String ext = "jpg";
	 
	 
//	 ANOTHER CASE OK
//	 public static final String uri = hostname+"fileName=org.gcube.portlets-user.messages-0.4.0-0.notifica&contentType=application%2Fx-gtar&smp-uri=smp%3A%2F%2FHome%2Ffrancesco.mangiacrapa%2FWorkspace%2FSharedFolder1%2Forg.gcube.portlets-user.messages-0.4.0-0.notifica%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevVwnFtqGknsyTZoV3fuG3iZ3%2BAsJaJDUH7F%2FELBV1lV8smBnSfc4vhDULwoWY6CWZ2tGj15BzeBI%3D";
//	 public static String filename ="org.gcube.portlets-user.messages-0.4.0-0.notifica";
//	 public static String ext = "war";
	 
	 
	 //SIZE 0
	 public static final String uri = hostname+"fileName=Untitled_Document&contentType=application%2Foctet-stream&smp-uri=smp%3A%2F%2FHome%2Ffrancesco.mangiacrapa%2FWorkspace%2FUntitled+Document%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevVwnFtqGknsyTZoV3fuG3iZ3%2BAsJaJDUH7F%2FELBV1lV8smBnSfc4vhDULwoWY6CWZ2tGj15BzeBI%3D";
	 public static String filename ="Untitled_Document";
	 public static String ext = "txt";

	public static void main(String[] args) {

		startTest();
	}

	public static void startTest() {

		InputStream inputStream = null;
		FileOutputStream outputStream = null;

		String url = uri;

		System.out.println(url);

		try {

			try {
				System.out.println("Validating..");
				boolean isValid = HttpRequestUtil.urlExists(url+"&validation=true");
				
				if(!isValid){
					System.out.println("url not valid, return!");
					return;
				}
				
				System.out.println("URL is valid, continue..");

				
				// DOWNLOAD URL
				URLConnection connection = new URL(url).openConnection();
				// InputStream response = connection.getInputStream();
				// read this file into InputStream
				inputStream = connection.getInputStream();

				System.out.println(" Total file size to read (in bytes) : "
						+ inputStream.available());

				// write the inputStream to a FileOutputStream
				outputStream = new FileOutputStream(new File(
						"/home/francesco-mangiacrapa/Desktop/UriResolverDownloads/"
								+ filename + "." + ext));

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				outputStream.flush();

				System.out.println("DOWNLOAD COMPLETED!");

			} catch (IOException e) {
				e.printStackTrace();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
