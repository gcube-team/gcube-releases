package org.gcube.data.access.fs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class AddUserToVRes {


	List<String> DORNE_VRE = Arrays.asList("mister.brown",
			"jesus.santamariafernandez",
			"noah.matovu",
			"andrea.rossi",
			"francesco.mangiacrapa",
			"mister.pink",
			"kostas.kakaletris",
			"massimiliano.assante",
			"mister.white",
			"mister.blonde",
			"aureliano.gentile",
			"mister.blue",
			"mister.orange",
			"lucio.lelii");

	List<String> PARTHENOS_VRE = Arrays.asList("francesco.mangiacrapa",
			"massimiliano.assante",
			"costantino.perciante",
			"luca.frosini",
			"pasquale.pagano",
			"alessia.bardi",
			"roberto.cirillo");

	List<String> PRE_VRE = Arrays.asList("fabio.sinibaldi",
			"valentina.marioli",
			"statistical.manager",
			"roberto.cirillo",
			"francesco.mangiacrapa",
			"leonardo.candela",
			"costantino.perciante",
			"mariaantonietta.digirolamo",
			"gantzoulatos",
			"massimiliano.assante",
			"lucio.lelii",
			"panagiota.koltsida",
			"ngalante",
			"efthymios",
			"nikolas.laskaris",
			"andrea.dellamico",
			"gianpaolo.coro",
			"giancarlo.panichi",
			"kostas.kakaletris",
			"scarponi",
			"andrea.rossi",
			"pasquale.pagano",
			"mister.blue",
			"m.assante",
			"yannis.marketakis",
			"grsf.publisher",
			"kgiannakelos",
			"mister.pink",
			"luca.frosini",
			"dkatris",
			"paolo.fabriani",
			"mister.brown",
			"mister.white",
			"mister.orange",
			"gabriele.giammatteo");

	@Test
	public void add() throws Exception{
		String group = "pred4s-preprod-preVRE";
		URL addGroupUrl = new URL("http://storagehub.pre.d4science.net/storagehub/workspace/groups/"+group+"?gcube-token=a6cec25b-3844-4901-83f3-95eee83319ba-980114272");
		
		for (String user : PRE_VRE) {
			try {
				HttpURLConnection connection =(HttpURLConnection)addGroupUrl.openConnection();
				connection.setRequestMethod("PUT");
				StringBuilder postData = new StringBuilder();
				postData.append("userId");
				postData.append("=");
				postData.append(user);
				/*postData.append("&");
				postData.append("password");
				postData.append("=");
				postData.append("pwd"+user.hashCode());*/
				byte[] postDataBytes = postData.toString().getBytes("UTF-8");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				connection.setDoOutput(true);
				connection.getOutputStream().write(postDataBytes);
				Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

				for (int c; (c = in.read()) >= 0;)
					System.out.print((char)c);
			}catch (Exception e) {
				System.out.println("error for user "+user);
				e.printStackTrace();
			}
		}
	}
}
