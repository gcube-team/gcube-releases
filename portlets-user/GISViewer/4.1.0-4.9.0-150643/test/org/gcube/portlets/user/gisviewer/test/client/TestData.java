package org.gcube.portlets.user.gisviewer.test.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class TestData extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String nome, cognome, a, b, c, d, e, f, g, h, i;
	int eta;
	
	public TestData() {			
	}
	
	public TestData(String nome, String cognome, int eta) {
	    set("nome", nome);
	    set("cognome", cognome);
	    set("eta", eta);
	}
	
	
	
	public TestData(String nome, String cognome, int eta, String a, String b,
			String c, String d, String e, String f, String g, String h,
			String i) {
		super();
		set("nome", nome);
		set("cognome", cognome);
		set("a", a);
		set("b", b);
		set("c", c);
		set("d", d);
		set("e", e);
		set("f", f);
		set("g", g);
		set("h", h);
		set("i", i);
		set("eta", eta);
	}

	public String getCognome() {
		return cognome;
	}
	
	public int getEta() {
		return eta;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	public void setEta(int eta) {
		this.eta = eta;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public static List<TestData> getTestData() {
		List<TestData> persone = new ArrayList<TestData>();
		persone.add(new TestData("ciccio", "ceras", 31, "asd", "asd", "asd", "asd", "asd", "asd", "asd", "asd", "asd"));
		persone.add(new TestData("pinco", "pallino", 25, "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa"));
		persone.add(new TestData("moira", "orfei", 22, "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa"));
		persone.add(new TestData("silvio", "berlusconi", 89, "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa"));
		persone.add(new TestData("pippo", "pluto", 13, "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa"));
		persone.add(new TestData("pippo", "pippo", 37, "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa", "asd", "dsa"));
		return persone;
	}

	public static void main(String[] args) {
		String str = "ALGORITHM CITATION : Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008. | HCAF GENERATION TIME : 1970_01_01_01_00_00_000_CET | HCAF TITLE : Default  | HSPEC TITLE : Test Venus_SuitableRange | HSPEN GENERATION TIME : 1970_01_01_01_00_00_000_CET | ALGORITHM : SuitableRange | HSPEN TITLE : Default | HSPEC GENERATION TIME : 2012_03_13_11_25_55_188_CET |";
		
		// try split by pipe
		String[] splitPipe = str.split("\\|");
		System.out.println("split lenght: "+splitPipe.length);
		
		if (splitPipe.length==1)
			// no map case
			System.out.println("Description: "+str);
		else {
			// map case
			System.out.println("Metadata list");
			for (String metadata: splitPipe) {
				// try split by ":"
				String[] splitPoints = metadata.split(":");
				if (splitPoints.length==0)
					System.out.println("	- "+metadata);
				else
					System.out.println("	- "+getCapitalWords(splitPoints[0]) + "-"+metadata.substring(splitPoints[0].length()+1).trim());
			}
		}
	}

	public static String getCapitalWords(String string) {
		String ris = "";
			
		boolean precUnderscore = true;
		for (int i=0; i<string.length(); i++) {
			char c = string.charAt(i);
			
			if (c == '_') {
				precUnderscore = true;
				ris += " ";
			} else {
				ris += (precUnderscore ? Character.toUpperCase(c) : Character.toLowerCase(c));
				if (precUnderscore == true)
					precUnderscore = false;
			}
		}
		return ris.trim();
	}
}













