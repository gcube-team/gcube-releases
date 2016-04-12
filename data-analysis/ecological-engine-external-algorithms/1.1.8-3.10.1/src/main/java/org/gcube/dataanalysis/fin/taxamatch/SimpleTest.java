package org.gcube.dataanalysis.fin.taxamatch;
import org.gcube.dataanalysis.taxamatch.fin.func_Taxamatch;



public class SimpleTest {

	public static void main(String[] args) throws Exception{
		func_Taxamatch func = new func_Taxamatch();
		
		String EQUAL = "EQUAL";
		String genus = "Gadus";
		String species = "morhua";
		String ip = "biodiversity.db.i-marine.research-infrastructures.eu";
		String user = "postgres";
		String password = "0b1s@d4sc13nc3";
		String db = "fishbase";
		
		String[] matches = func.func_Taxamatch(genus, species, EQUAL, EQUAL, ip, user, password, db);
		
		System.out.println("Match: "+matches[0]);
	}
	
}
