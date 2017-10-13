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
public class UriResolverStressTest {

	// public static final String hostserviceURI =
	// "http://dev.d4science.org/uri-resolver/smp?smp-uri=smp%3A%2F%2F";
	// public static final String fileName = "apache-tomcat-";
	// public static final String fileExtension = ".tar.gz";
	//
	// public static final String smpURI =
	// "Share%2FFFb6ca1678-9237-4e30-8d6b-268e330b6053%2Fapache-tomcat-6.0.41.tar.gz%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevSW1i4CWmmE07DVZLaoR9ZU3BAfo3xUYQEBCy28i2fxnrGYnbmjfm6hRCxd%2Fheeyp";
	// public static final String contentType =
	// "contentType=application%2Fx-gzip";

	public static final String hostserviceURI = "http://localhost:8080/uri-resolver/smp?smp-uri=";
	public static final String fileName = "gattino02";
	public static final String fileExtension = ".jpg";

	public static final String smpURI = "smp%3A%2F%2FHome%2Ffrancesco.mangiacrapa%2FWorkspace%2Fgattino02.jpg%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevVwnFtqGknsyTZoV3fuG3iZ3%2BAsJaJDUH7F%2FELBV1lV8smBnSfc4vhDULwoWY6CWZ2tGj15BzeBI%3D";
	public static final String contentType = "application%2Fx-gzip";

	public static boolean isValidation = true;
	
//	http://dev.d4science.org/uri-resolver/smp?fileName=gattino02.jpg&contentType=audio%2Fmpeg&smp-uri=smp%3A%2F%2FHome%2Ffrancesco.mangiacrapa%2FWorkspace%2Fgattino02.jpg%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevVwnFtqGknsyTZoV3fuG3iZ3%2BAsJaJDUH7F%2FELBV1lV8smBnSfc4vhDULwoWY6CWZ2tGj15BzeBI%3D
	// http://dev.d4science.org/uri-resolver/smp?fileName=apache-tomcat-6.0.41.tar.gz&contentType=application%2Fx-gzip&smp-uri=smp%3A%2F%2FShare%2FFFb6ca1678-9237-4e30-8d6b-268e330b6053%2Fapache-tomcat-6.0.41.tar.gz%3F5ezvFfBOLqb3YESyI%2FkesN4T%2BZD0mtmc%2F4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi%2FTEYl7d%2BF4sKR7EwqeONAlQygGb2MgXevSW1i4CWmmE07DVZLaoR9ZU3BAfo3xUYQEBCy28i2fxnrGYnbmjfm6hRCxd%2Fheeyp
	public static void main(String[] args) {

		startThead("test");
	}

	/**
	 * 
	 */
	public void startStressTest() {
		int j = 0;

		while (true) {

			for (int i = 0; i < 5; i++) {
				String name = j + "-" + i;
				startThead(name);
			}

			j++;

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void startThead(final String name) {

		new Thread(name) {
	

			@Override
			public void run() {

				InputStream inputStream = null;
				FileOutputStream outputStream = null;

				String url = hostserviceURI + smpURI + 
						"&fileName="+fileName+ fileExtension + 
						"&contentType="+contentType;
				
				
				if(isValidation){
					url+="&validation=true";
				}
				System.out.println(url);

				try {
					
					//VALIDATE URL
					if(isValidation){
						
						try {
							System.out.println("isValidation");
							HttpRequestUtil.urlExists(url);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
	
					//DOWNLOAD URL
					URLConnection connection = new URL(url).openConnection();
					// InputStream response = connection.getInputStream();
					// read this file into InputStream
					inputStream = connection.getInputStream();

					System.out.println("Thread " + name
							+ " Total file size to read (in bytes) : "
							+ inputStream.available());

					// write the inputStream to a FileOutputStream
					outputStream = new FileOutputStream(new File(
							"/home/francesco-mangiacrapa/Desktop/UriResolverDownloads/"
									+ fileName + name + fileExtension));

					int read = 0;
					byte[] bytes = new byte[1024];

					while ((read = inputStream.read(bytes)) != -1) {
						outputStream.write(bytes, 0, read);
					}

					outputStream.flush();

					System.out.println("Thread " + name
							+ " DOWNLOAD COMPLETED!");

				} catch (IOException e) {
					e.printStackTrace();
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

		}.start();

	}

}
