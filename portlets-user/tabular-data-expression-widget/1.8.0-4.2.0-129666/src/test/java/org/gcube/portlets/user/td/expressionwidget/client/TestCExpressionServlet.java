package org.gcube.portlets.user.td.expressionwidget.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.junit.Test;

public class TestCExpressionServlet {

	@Test
	public void test() {
		HttpURLConnection cs = null;

		String path = "ExpressionWidget/CExpressionMap";
		URL url = null;
		try {
			url = new URL("http://127.0.0.1:8888/" + path);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		C_Expression exp = new C_Expression();
		// Save the object's instance
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(buffer);
			out.writeObject(exp);
		} catch (IOException e) {
			System.out.println("IOException :" + e.getLocalizedMessage());
			return;
		}

		// Prepare the header
		byte[] serobj = buffer.toByteArray();

		try {
			cs = (HttpURLConnection) url.openConnection();

			cs.setConnectTimeout(0);
			cs.setReadTimeout(0);
			cs.setRequestMethod("POST");
			cs.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			cs.setDoOutput(true);
			cs.setDoInput(true);
			cs.setUseCaches(false);
			cs.setAllowUserInteraction(false);
			cs.setRequestProperty("Content-Type", "application/java-object");
			cs.setRequestProperty("Content-Language", "en-US");
			cs.setRequestProperty("Content-Length",
					String.valueOf(serobj.length));
			OutputStream sout = cs.getOutputStream();
			sout.write(serobj);

			int HTTPcodicerisposta = cs.getResponseCode();
			System.out.println("Codice Risposta: " + HTTPcodicerisposta);
			if (HTTPcodicerisposta == 200) {
				ObjectInputStream oin = new ObjectInputStream(
						cs.getInputStream());
				Expression serverExpression = null;

				try {
					serverExpression = (Expression) oin.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("Read Object error :"
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}
				System.out.println(serverExpression);

			} else {
				System.out.println("Codice Risposta: " + HTTPcodicerisposta);

			}

		} catch (IOException e1) {
			System.out.println("Error: "+e1.getLocalizedMessage());
			e1.printStackTrace();
		} finally {
			cs.disconnect();

		}
	}

}
