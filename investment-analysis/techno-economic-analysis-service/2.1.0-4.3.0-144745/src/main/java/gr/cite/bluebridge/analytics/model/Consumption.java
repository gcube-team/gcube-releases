package gr.cite.bluebridge.analytics.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Consumption {
	private List<Monthly> monthly;
	private List<Daily> daily;
	
	// Monthly Food Consumption
	
	public static class Monthly{
		private String month;
	    private double food;
	    
	    public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public double getFood() {
			return food;
		}
		public void setFood(double food) {
			this.food = food;
		}
	}

	// Daily Food Consumption
	
	public static class Daily{
	    private String day;
	    private int bm ;
	    private int bmdead;
	    private double fcre;	
	    private double fcrb;
	    private double food;
	    private double mortality;
	    
	    public double getMortality() {
			return mortality;
		}
		public void setMortality(double mortality) {
			this.mortality = mortality;
		}
		
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public int getBm() {
			return bm;
		}
		public void setBm(int bm) {
			this.bm = bm;
		}
		public double getFcre() {
			return fcre;
		}
		public void setFcre(double fcre) {
			this.fcre = fcre;
		}
		public double getFcrb() {
			return fcrb;
		}
		public void setFcrb(double fcrb) {
			this.fcrb = fcrb;
		}
		public double getFood() {
			return food;
		}
		public void setFood(double food) {
			this.food = food;
		}
		public int getBmdead() {
			return bmdead;
		}
		public void setBmdead(int bmdead) {
			this.bmdead = bmdead;
		}
	}
	
	public Map<Integer, Integer> getFeedNeedPerMonth(){
		Map<Integer, Integer> feedNeedPerMonth = new TreeMap<>();	
		Map<Integer, Integer> years = new HashMap<>();		
		
		for(Daily dailyConsumption : daily){
			String[] date = dailyConsumption.getDay().split("/");
			
			int year = Integer.parseInt(date[2]);			
			if(!years.containsKey(year)){
				years.put(year, 12*years.size());
			}			
			int month = Integer.parseInt(date[1]) + years.get(year);
			
			int feedNeed = 0;
			if(feedNeedPerMonth.containsKey(month)){
				feedNeed = feedNeedPerMonth.get(month);
			}			
			feedNeed += dailyConsumption.getFood();
			feedNeedPerMonth.put(month, feedNeed);	
		}
		
//		for(Map.Entry<Integer, Integer> entry : feedNeedPerMonth.entrySet()){
//			System.out.println("Month = " + entry.getKey() + " Feed Need = " + entry.getValue());
//		}
		
		return feedNeedPerMonth;
	}
	
	public Map<Integer, Double> getFeedNeedPerMonth1(){
		Map<Integer, Double> feedNeedPerMonth = new TreeMap<>();	
		Map<Integer, Integer> years = new HashMap<>();		
		
		for(Monthly monthlyConsumption : monthly){
			String[] date = monthlyConsumption.getMonth().split("/");
			
			int year = Integer.parseInt(date[1]);			
			if(!years.containsKey(year)){
				years.put(year, 12*years.size());
			}			
			int month = Integer.parseInt(date[0]) + years.get(year);			

			feedNeedPerMonth.put(month, monthlyConsumption.getFood());	
		}
		
//		for(Map.Entry<Integer, Double> entry : feedNeedPerMonth.entrySet()){
//			System.out.println("Month = " + entry.getKey() + " Feed Need = " + entry.getValue());
//		}
		
		return feedNeedPerMonth;
	}			
	
	public int getTotalBiomassPerGeneration(){
		return daily.get(daily.size() - 1).getBm();
	}
	
	public static Consumption getFixedData(){
		
		
		ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);   


		
		String data = "{'daily':[ " +
							"{'day': '01/01/16', 'bm':1634875, 'fcre':0.00, 'fcrb':0.00, 'food':97, 'bmdead':124 }," +
							"{'day': '02/01/16', 'bm':1692606, 'fcre':3.41, 'fcrb':3.43, 'food':100, 'bmdead':126 }," +
							"{'day': '03/01/16', 'bm':1752375, 'fcre':2.56, 'fcrb':2.57, 'food':104, 'bmdead':130 }," +
							"{'day': '04/01/16', 'bm':1814255, 'fcre':2.27, 'fcrb':2.28, 'food':107, 'bmdead':135 }," +
							"{'day': '05/01/16', 'bm':1878319, 'fcre':2.13, 'fcrb':2.14, 'food':111, 'bmdead':140 }," +
							"{'day': '06/01/16', 'bm':1944646, 'fcre':2.05, 'fcrb':2.05, 'food':115, 'bmdead':145 }," +
							"{'day': '07/01/16', 'bm':2013316, 'fcre':1.99, 'fcrb':1.99, 'food':119, 'bmdead':150 }," +
							"{'day': '08/01/16', 'bm':2084410, 'fcre':1.95, 'fcrb':1.95, 'food':123, 'bmdead':155 }," +
							"{'day': '09/01/16', 'bm':2158014, 'fcre':1.92, 'fcrb':1.92, 'food':128, 'bmdead':161 }," +
							"{'day': '10/01/16', 'bm':2234217, 'fcre':1.89, 'fcrb':1.90, 'food':132, 'bmdead':166 }," +
							"{'day': '11/01/16', 'bm':2313112, 'fcre':1.88, 'fcrb':1.88, 'food':137, 'bmdead':172 }," +
							"{'day': '12/01/16', 'bm':2394792, 'fcre':1.86, 'fcrb':1.86, 'food':142, 'bmdead':178 }," +
							"{'day': '13/01/16', 'bm':2479357, 'fcre':1.85, 'fcrb':1.85, 'food':147, 'bmdead':185 }," +
							"{'day': '14/01/16', 'bm':2566907, 'fcre':1.84, 'fcrb':1.84, 'food':152, 'bmdead':191 }," +
							"{'day': '15/01/16', 'bm':2657550, 'fcre':1.83, 'fcrb':1.83, 'food':157, 'bmdead':198 }," +
							"{'day': '16/01/16', 'bm':2751481, 'fcre':1.82, 'fcrb':1.82, 'food':163, 'bmdead':117 }," +
							"{'day': '17/01/16', 'bm':2837855, 'fcre':1.83, 'fcrb':1.83, 'food':168, 'bmdead':121 }," +
							"{'day': '18/01/16', 'bm':2926942, 'fcre':1.84, 'fcrb':1.84, 'food':173, 'bmdead':125 }," +
							"{'day': '19/01/16', 'bm':3018825, 'fcre':1.84, 'fcrb':1.85, 'food':178, 'bmdead':128 }," +
							"{'day': '20/01/16', 'bm':3113592, 'fcre':1.85, 'fcrb':1.85, 'food':184, 'bmdead':133 }," +
							"{'day': '21/01/16', 'bm':3211334, 'fcre':1.86, 'fcrb':1.86, 'food':190, 'bmdead':137 }," +
							"{'day': '22/01/16', 'bm':3312145, 'fcre':1.86, 'fcrb':1.86, 'food':196, 'bmdead':141 }," +
							"{'day': '23/01/16', 'bm':3416120, 'fcre':1.87, 'fcrb':1.87, 'food':202, 'bmdead':145 }," +
							"{'day': '24/01/16', 'bm':3523359, 'fcre':1.87, 'fcrb':1.87, 'food':208, 'bmdead':150 }," +
							"{'day': '25/01/16', 'bm':3633965, 'fcre':1.87, 'fcrb':1.88, 'food':215, 'bmdead':155 }," +
							"{'day': '26/01/16', 'bm':3748042, 'fcre':1.88, 'fcrb':1.88, 'food':222, 'bmdead':160 }," +
							"{'day': '27/01/16', 'bm':3865701, 'fcre':1.88, 'fcrb':1.88, 'food':228, 'bmdead':165 }," +
							"{'day': '28/01/16', 'bm':3987054, 'fcre':1.88, 'fcrb':1.89, 'food':236, 'bmdead':170 }," +
							"{'day': '29/01/16', 'bm':4112216, 'fcre':1.89, 'fcrb':1.89, 'food':243, 'bmdead':175 }," +
							"{'day': '30/01/16', 'bm':4241307, 'fcre':1.89, 'fcrb':1.89, 'food':251, 'bmdead':181 }," +
							"{'day': '31/01/16', 'bm':4374450, 'fcre':1.89, 'fcrb':1.90, 'food':259, 'bmdead':186 }," +
							"{'day': '01/02/16', 'bm':4511629, 'fcre':1.89, 'fcrb':1.90, 'food':267, 'bmdead':337 }," +
							"{'day': '02/02/16', 'bm':4670942, 'fcre':1.89, 'fcrb':1.89, 'food':276, 'bmdead':349 }," +
							"{'day': '03/02/16', 'bm':4835882, 'fcre':1.88, 'fcrb':1.88, 'food':286, 'bmdead':361 }," +
							"{'day': '04/02/16', 'bm':5006645, 'fcre':1.87, 'fcrb':1.87, 'food':296, 'bmdead':374 }," +
							"{'day': '05/02/16', 'bm':5183438, 'fcre':1.86, 'fcrb':1.87, 'food':306, 'bmdead':387 }," +
							"{'day': '06/02/16', 'bm':5366475, 'fcre':1.86, 'fcrb':1.86, 'food':317, 'bmdead':401 }," +
							"{'day': '07/02/16', 'bm':5555974, 'fcre':1.85, 'fcrb':1.85, 'food':328, 'bmdead':415 }," +
							"{'day': '08/02/16', 'bm':5752165, 'fcre':1.85, 'fcrb':1.85, 'food':340, 'bmdead':430 }," +
							"{'day': '09/02/16', 'bm':5955284, 'fcre':1.84, 'fcrb':1.84, 'food':352, 'bmdead':445 }," +
							"{'day': '10/02/16', 'bm':6165576, 'fcre':1.84, 'fcrb':1.84, 'food':364, 'bmdead':461 }," +
							"{'day': '11/02/16', 'bm':6383293, 'fcre':1.83, 'fcrb':1.83, 'food':377, 'bmdead':477 }," +
							"{'day': '12/02/16', 'bm':6608698, 'fcre':1.83, 'fcrb':1.83, 'food':391, 'bmdead':494 }," +
							"{'day': '13/02/16', 'bm':6842062, 'fcre':1.82, 'fcrb':1.83, 'food':404, 'bmdead':512 }," +
							"{'day': '14/02/16', 'bm':7083667, 'fcre':1.82, 'fcrb':1.82, 'food':419, 'bmdead':530 }," +
							"{'day': '15/02/16', 'bm':7333803, 'fcre':1.81, 'fcrb':1.82, 'food':433, 'bmdead':549 }," +
							"{'day': '16/02/16', 'bm':7593016, 'fcre':1.76, 'fcrb':1.76, 'food':128, 'bmdead':324 }," +
							"{'day': '17/02/16', 'bm':7666852, 'fcre':1.76, 'fcrb':1.76, 'food':129, 'bmdead':328 }," +
							"{'day': '18/02/16', 'bm':7741406, 'fcre':1.76, 'fcrb':1.76, 'food':130, 'bmdead':331 }," +
							"{'day': '19/02/16', 'bm':7816686, 'fcre':1.76, 'fcrb':1.76, 'food':131, 'bmdead':334 }," +
							"{'day': '20/02/16', 'bm':7892697, 'fcre':1.76, 'fcrb':1.76, 'food':133, 'bmdead':337 }," +
							"{'day': '21/02/16', 'bm':7969447, 'fcre':1.76, 'fcrb':1.76, 'food':134, 'bmdead':341 }," +
							"{'day': '22/02/16', 'bm':8046944, 'fcre':1.76, 'fcrb':1.76, 'food':135, 'bmdead':344 }," +
							"{'day': '23/02/16', 'bm':8125194, 'fcre':1.76, 'fcrb':1.76, 'food':137, 'bmdead':347 }," +
							"{'day': '24/02/16', 'bm':8204205, 'fcre':1.76, 'fcrb':1.76, 'food':138, 'bmdead':351 }," +
							"{'day': '25/02/16', 'bm':8283985, 'fcre':1.76, 'fcrb':1.76, 'food':139, 'bmdead':354 }," +
							"{'day': '26/02/16', 'bm':8364540, 'fcre':1.76, 'fcrb':1.76, 'food':141, 'bmdead':358 }," +
							"{'day': '27/02/16', 'bm':8445878, 'fcre':1.76, 'fcrb':1.76, 'food':142, 'bmdead':361 }," +
							"{'day': '28/02/16', 'bm':8528008, 'fcre':1.76, 'fcrb':1.76, 'food':143, 'bmdead':365 }," +
							"{'day': '29/02/16', 'bm':8610936, 'fcre':1.75, 'fcrb':1.76, 'food':145, 'bmdead':368 }," +
							"{'day': '01/03/16', 'bm':8694298, 'fcre':1.75, 'fcrb':1.76, 'food':146, 'bmdead':744 }," +
							"{'day': '02/03/16', 'bm':8762446, 'fcre':1.76, 'fcrb':1.76, 'food':147, 'bmdead':750 }," +
							"{'day': '03/03/16', 'bm':8831128, 'fcre':1.76, 'fcrb':1.77, 'food':148, 'bmdead':756 }," +
							"{'day': '04/03/16', 'bm':8900348, 'fcre':1.77, 'fcrb':1.77, 'food':150, 'bmdead':762 }," +
							"{'day': '05/03/16', 'bm':8970111, 'fcre':1.77, 'fcrb':1.78, 'food':151, 'bmdead':768 }," +
							"{'day': '06/03/16', 'bm':9040420, 'fcre':1.77, 'fcrb':1.78, 'food':152, 'bmdead':774 }," +
							"{'day': '07/03/16', 'bm':9111280, 'fcre':1.78, 'fcrb':1.78, 'food':153, 'bmdead':780 }," +
							"{'day': '08/03/16', 'bm':9182696, 'fcre':1.78, 'fcrb':1.79, 'food':154, 'bmdead':786 }," +
							"{'day': '09/03/16', 'bm':9254671, 'fcre':1.78, 'fcrb':1.79, 'food':155, 'bmdead':793 }," +
							"{'day': '10/03/16', 'bm':9327211, 'fcre':1.79, 'fcrb':1.79, 'food':157, 'bmdead':799 }," +
							"{'day': '11/03/16', 'bm':9400319, 'fcre':1.79, 'fcrb':1.80, 'food':158, 'bmdead':805 }," +
							"{'day': '12/03/16', 'bm':9474000, 'fcre':1.80, 'fcrb':1.80, 'food':159, 'bmdead':812 }," +
							"{'day': '13/03/16', 'bm':9548258, 'fcre':1.80, 'fcrb':1.80, 'food':160, 'bmdead':818 }," +
							"{'day': '14/03/16', 'bm':9623099, 'fcre':1.80, 'fcrb':1.81, 'food':162, 'bmdead':825 }," +
							"{'day': '15/03/16', 'bm':9698525, 'fcre':1.81, 'fcrb':1.81, 'food':163, 'bmdead':831 }," +
							"{'day': '16/03/16', 'bm':9775133, 'fcre':1.81, 'fcrb':1.81, 'food':164, 'bmdead':248 }," +
							"{'day': '17/03/16', 'bm':9854216, 'fcre':1.81, 'fcrb':1.82, 'food':166, 'bmdead':250 }," +
							"{'day': '18/03/16', 'bm':9933940, 'fcre':1.81, 'fcrb':1.82, 'food':167, 'bmdead':252 }," +
							"{'day': '19/03/16', 'bm':10014308, 'fcre':1.82, 'fcrb':1.82, 'food':168, 'bmdead':254 }," +
							"{'day': '20/03/16', 'bm':10095327, 'fcre':1.82, 'fcrb':1.83, 'food':170, 'bmdead':257 }," +
							"{'day': '21/03/16', 'bm':10177001, 'fcre':1.82, 'fcrb':1.83, 'food':171, 'bmdead':259 }," +
							"{'day': '22/03/16', 'bm':10259335, 'fcre':1.82, 'fcrb':1.83, 'food':172, 'bmdead':261 }," +
							"{'day': '23/03/16', 'bm':10342336, 'fcre':1.83, 'fcrb':1.83, 'food':174, 'bmdead':263 }," +
							"{'day': '24/03/16', 'bm':10426009, 'fcre':1.83, 'fcrb':1.84, 'food':175, 'bmdead':265 }," +
							"{'day': '25/03/16', 'bm':10510358, 'fcre':1.83, 'fcrb':1.84, 'food':177, 'bmdead':267 }," +
							"{'day': '26/03/16', 'bm':10595390, 'fcre':1.83, 'fcrb':1.84, 'food':178, 'bmdead':269 }," +
							"{'day': '27/03/16', 'bm':10681109, 'fcre':1.84, 'fcrb':1.84, 'food':179, 'bmdead':271 }," +
							"{'day': '28/03/16', 'bm':10767522, 'fcre':1.84, 'fcrb':1.85, 'food':181, 'bmdead':274 }," +
							"{'day': '29/03/16', 'bm':10854634, 'fcre':1.84, 'fcrb':1.85, 'food':182, 'bmdead':276 }," +
							"{'day': '30/03/16', 'bm':10942451, 'fcre':1.84, 'fcrb':1.85, 'food':184, 'bmdead':278 }," +
							"{'day': '31/03/16', 'bm':11030979, 'fcre':1.85, 'fcrb':1.85, 'food':185, 'bmdead':280 }," +
							"{'day': '01/04/16', 'bm':11118389, 'fcre':1.85, 'fcrb':1.86, 'food':187, 'bmdead':2116 }," +
							"{'day': '02/04/16', 'bm':11207372, 'fcre':1.85, 'fcrb':1.86, 'food':188, 'bmdead':2133 }," +
							"{'day': '03/04/16', 'bm':11297066, 'fcre':1.85, 'fcrb':1.86, 'food':190, 'bmdead':2151 }," +
							"{'day': '04/04/16', 'bm':11387478, 'fcre':1.86, 'fcrb':1.86, 'food':191, 'bmdead':2168 }," +
							"{'day': '05/04/16', 'bm':11478613, 'fcre':1.86, 'fcrb':1.87, 'food':193, 'bmdead':2186 }," +
							"{'day': '06/04/16', 'bm':11570477, 'fcre':1.86, 'fcrb':1.87, 'food':194, 'bmdead':2204 }," +
							"{'day': '07/04/16', 'bm':11663076, 'fcre':1.86, 'fcrb':1.87, 'food':196, 'bmdead':2222 }," +
							"{'day': '08/04/16', 'bm':11756415, 'fcre':1.87, 'fcrb':1.88, 'food':198, 'bmdead':2240 }," +
							"{'day': '09/04/16', 'bm':11850501, 'fcre':1.87, 'fcrb':1.88, 'food':199, 'bmdead':2259 }," +
							"{'day': '10/04/16', 'bm':11945340, 'fcre':1.87, 'fcrb':1.88, 'food':201, 'bmdead':2277 }," +
							"{'day': '11/04/16', 'bm':12040937, 'fcre':1.87, 'fcrb':1.88, 'food':202, 'bmdead':2296 }," +
							"{'day': '12/04/16', 'bm':12137298, 'fcre':1.88, 'fcrb':1.89, 'food':204, 'bmdead':2315 }," +
							"{'day': '13/04/16', 'bm':12234431, 'fcre':1.88, 'fcrb':1.89, 'food':206, 'bmdead':2334 }," +
							"{'day': '14/04/16', 'bm':12332340, 'fcre':1.88, 'fcrb':1.89, 'food':207, 'bmdead':2353 }," +
							"{'day': '15/04/16', 'bm':12431032, 'fcre':1.88, 'fcrb':1.89, 'food':209, 'bmdead':2372 }," +
							"{'day': '16/04/16', 'bm':12530514, 'fcre':1.88, 'fcrb':1.90, 'food':211, 'bmdead':2392 }," +
							"{'day': '17/04/16', 'bm':12630791, 'fcre':1.89, 'fcrb':1.90, 'food':212, 'bmdead':2411 }," +
							"{'day': '18/04/16', 'bm':12731871, 'fcre':1.89, 'fcrb':1.90, 'food':214, 'bmdead':2431 }," +
							"{'day': '19/04/16', 'bm':12833758, 'fcre':1.89, 'fcrb':1.90, 'food':216, 'bmdead':2451 }," +
							"{'day': '20/04/16', 'bm':12936478, 'fcre':1.89, 'fcrb':1.91, 'food':217, 'bmdead':2454 }," +
							"{'day': '21/04/16', 'bm':13040020, 'fcre':1.89, 'fcrb':1.91, 'food':219, 'bmdead':2474 }," +
							"{'day': '22/04/16', 'bm':13144390, 'fcre':1.90, 'fcrb':1.91, 'food':221, 'bmdead':2494 }," +
							"{'day': '23/04/16', 'bm':13249595, 'fcre':1.90, 'fcrb':1.91, 'food':223, 'bmdead':2514 }," +
							"{'day': '24/04/16', 'bm':13355642, 'fcre':1.90, 'fcrb':1.91, 'food':224, 'bmdead':2535 }," +
							"{'day': '25/04/16', 'bm':13462536, 'fcre':1.90, 'fcrb':1.92, 'food':226, 'bmdead':2556 }," +
							"{'day': '26/04/16', 'bm':13570286, 'fcre':1.90, 'fcrb':1.92, 'food':228, 'bmdead':2577 }," +
							"{'day': '27/04/16', 'bm':13678898, 'fcre':1.91, 'fcrb':1.92, 'food':230, 'bmdead':2598 }," +
							"{'day': '28/04/16', 'bm':13788379, 'fcre':1.91, 'fcrb':1.92, 'food':232, 'bmdead':2619 }," +
							"{'day': '29/04/16', 'bm':13898735, 'fcre':1.91, 'fcrb':1.93, 'food':233, 'bmdead':2641 }," +
							"{'day': '30/04/16', 'bm':14009974, 'fcre':1.91, 'fcrb':1.93, 'food':235, 'bmdead':2662 }," +
							"{'day': '01/05/16', 'bm':14121341, 'fcre':1.91, 'fcrb':1.93, 'food':237, 'bmdead':3446 }," +
							"{'day': '02/05/16', 'bm':14272915, 'fcre':1.91, 'fcrb':1.93, 'food':240, 'bmdead':3484 }," +
							"{'day': '03/05/16', 'bm':14426115, 'fcre':1.91, 'fcrb':1.92, 'food':242, 'bmdead':3522 }," +
							"{'day': '04/05/16', 'bm':14580958, 'fcre':1.90, 'fcrb':1.92, 'food':245, 'bmdead':3561 }," +
							"{'day': '05/05/16', 'bm':14737463, 'fcre':1.90, 'fcrb':1.92, 'food':248, 'bmdead':3600 }," +
							"{'day': '06/05/16', 'bm':14895184, 'fcre':1.89, 'fcrb':1.91, 'food':231, 'bmdead':4102 }," +
							"{'day': '07/05/16', 'bm':15037165, 'fcre':1.89, 'fcrb':1.91, 'food':233, 'bmdead':4142 }," +
							"{'day': '08/05/16', 'bm':15180498, 'fcre':1.89, 'fcrb':1.91, 'food':235, 'bmdead':4183 }," +
							"{'day': '09/05/16', 'bm':15325197, 'fcre':1.88, 'fcrb':1.90, 'food':238, 'bmdead':4224 }," +
							"{'day': '10/05/16', 'bm':15471273, 'fcre':1.88, 'fcrb':1.90, 'food':240, 'bmdead':4265 }," +
							"{'day': '11/05/16', 'bm':15618741, 'fcre':1.88, 'fcrb':1.90, 'food':242, 'bmdead':4307 }," +
							"{'day': '12/05/16', 'bm':15767613, 'fcre':1.88, 'fcrb':1.90, 'food':244, 'bmdead':4349 }," +
							"{'day': '13/05/16', 'bm':15917903, 'fcre':1.87, 'fcrb':1.90, 'food':247, 'bmdead':4392 }," +
							"{'day': '14/05/16', 'bm':16069625, 'fcre':1.87, 'fcrb':1.89, 'food':249, 'bmdead':4435 }," +
							"{'day': '15/05/16', 'bm':16222813, 'fcre':1.87, 'fcrb':1.89, 'food':251, 'bmdead':4456 }," +
							"{'day': '16/05/16', 'bm':16375509, 'fcre':1.87, 'fcrb':1.90, 'food':334, 'bmdead':6451 }," +
							"{'day': '17/05/16', 'bm':16604219, 'fcre':1.87, 'fcrb':1.89, 'food':339, 'bmdead':6544 }," +
							"{'day': '18/05/16', 'bm':16836142, 'fcre':1.86, 'fcrb':1.88, 'food':343, 'bmdead':6615 }," +
							"{'day': '19/05/16', 'bm':17071303, 'fcre':1.86, 'fcrb':1.88, 'food':348, 'bmdead':6710 }," +
							"{'day': '20/05/16', 'bm':17309746, 'fcre':1.85, 'fcrb':1.87, 'food':353, 'bmdead':6807 }," +
							"{'day': '21/05/16', 'bm':17551516, 'fcre':1.84, 'fcrb':1.87, 'food':358, 'bmdead':6904 }," +
							"{'day': '22/05/16', 'bm':17796661, 'fcre':1.84, 'fcrb':1.86, 'food':363, 'bmdead':7003 }," +
							"{'day': '23/05/16', 'bm':18045227, 'fcre':1.83, 'fcrb':1.86, 'food':368, 'bmdead':7104 }," +
							"{'day': '24/05/16', 'bm':18297261, 'fcre':1.83, 'fcrb':1.85, 'food':373, 'bmdead':7206 }," +
							"{'day': '25/05/16', 'bm':18552813, 'fcre':1.82, 'fcrb':1.85, 'food':378, 'bmdead':7310 }," +
							"{'day': '26/05/16', 'bm':18811957, 'fcre':1.82, 'fcrb':1.84, 'food':384, 'bmdead':7389 }," +
							"{'day': '27/05/16', 'bm':19074717, 'fcre':1.81, 'fcrb':1.84, 'food':389, 'bmdead':7495 }," +
							"{'day': '28/05/16', 'bm':19341145, 'fcre':1.81, 'fcrb':1.83, 'food':395, 'bmdead':7603 }," +
							"{'day': '29/05/16', 'bm':19611291, 'fcre':1.80, 'fcrb':1.83, 'food':400, 'bmdead':7712 }," +
							"{'day': '30/05/16', 'bm':19885207, 'fcre':1.80, 'fcrb':1.82, 'food':406, 'bmdead':7823 }," +
							"{'day': '31/05/16', 'bm':20162946, 'fcre':1.79, 'fcrb':1.82, 'food':411, 'bmdead':7935 }," +
							"{'day': '01/06/16', 'bm':20439993, 'fcre':1.80, 'fcrb':1.83, 'food':711, 'bmdead':12618 }," +
							"{'day': '02/06/16', 'bm':20957877, 'fcre':1.79, 'fcrb':1.82, 'food':729, 'bmdead':12945 }," +
							"{'day': '03/06/16', 'bm':21488874, 'fcre':1.78, 'fcrb':1.81, 'food':748, 'bmdead':13281 }," +
							"{'day': '04/06/16', 'bm':22032654, 'fcre':1.77, 'fcrb':1.80, 'food':738, 'bmdead':14288 }," +
							"{'day': '05/06/16', 'bm':22549024, 'fcre':1.76, 'fcrb':1.79, 'food':755, 'bmdead':14633 }," +
							"{'day': '06/06/16', 'bm':23077486, 'fcre':1.76, 'fcrb':1.79, 'food':773, 'bmdead':14985 }," +
							"{'day': '07/06/16', 'bm':23618355, 'fcre':1.75, 'fcrb':1.78, 'food':791, 'bmdead':15314 }," +
							"{'day': '08/06/16', 'bm':24171891, 'fcre':1.74, 'fcrb':1.77, 'food':810, 'bmdead':15683 }," +
							"{'day': '09/06/16', 'bm':24738389, 'fcre':1.74, 'fcrb':1.77, 'food':829, 'bmdead':16061 }," +
							"{'day': '10/06/16', 'bm':25318188, 'fcre':1.73, 'fcrb':1.76, 'food':848, 'bmdead':16414 }," +
							"{'day': '11/06/16', 'bm':25911565, 'fcre':1.72, 'fcrb':1.75, 'food':868, 'bmdead':16809 }," +
							"{'day': '12/06/16', 'bm':26518838, 'fcre':1.72, 'fcrb':1.75, 'food':888, 'bmdead':17214 }," +
							"{'day': '13/06/16', 'bm':27140368, 'fcre':1.71, 'fcrb':1.74, 'food':909, 'bmdead':17592 }," +
							"{'day': '14/06/16', 'bm':27776454, 'fcre':1.70, 'fcrb':1.74, 'food':931, 'bmdead':18016 }," +
							"{'day': '15/06/16', 'bm':28427436, 'fcre':1.70, 'fcrb':1.73, 'food':952, 'bmdead':18450 }," +
							"{'day': '16/06/16', 'bm':29089019, 'fcre':1.68, 'fcrb':1.71, 'food':582, 'bmdead':23539 }," +
							"{'day': '17/06/16', 'bm':29486759, 'fcre':1.68, 'fcrb':1.71, 'food':590, 'bmdead':23839 }," +
							"{'day': '18/06/16', 'bm':29889919, 'fcre':1.67, 'fcrb':1.71, 'food':598, 'bmdead':24185 }," +
							"{'day': '19/06/16', 'bm':30298612, 'fcre':1.67, 'fcrb':1.71, 'food':606, 'bmdead':24493 }," +
							"{'day': '20/06/16', 'bm':30712874, 'fcre':1.67, 'fcrb':1.70, 'food':614, 'bmdead':24848 }," +
							"{'day': '21/06/16', 'bm':31132822, 'fcre':1.67, 'fcrb':1.70, 'food':623, 'bmdead':25165 }," +
							"{'day': '22/06/16', 'bm':31558492, 'fcre':1.66, 'fcrb':1.70, 'food':631, 'bmdead':25530 }," +
							"{'day': '23/06/16', 'bm':31989960, 'fcre':1.66, 'fcrb':1.70, 'food':640, 'bmdead':25900 }," +
							"{'day': '24/06/16', 'bm':32427352, 'fcre':1.66, 'fcrb':1.70, 'food':649, 'bmdead':26231 }," +
							"{'day': '25/06/16', 'bm':32870702, 'fcre':1.65, 'fcrb':1.70, 'food':657, 'bmdead':26611 }," +
							"{'day': '26/06/16', 'bm':33320139, 'fcre':1.65, 'fcrb':1.69, 'food':666, 'bmdead':26950 }," +
							"{'day': '27/06/16', 'bm':33775698, 'fcre':1.65, 'fcrb':1.69, 'food':676, 'bmdead':27341 }," +
							"{'day': '28/06/16', 'bm':34237511, 'fcre':1.65, 'fcrb':1.69, 'food':685, 'bmdead':27689 }," +
							"{'day': '29/06/16', 'bm':34705616, 'fcre':1.65, 'fcrb':1.69, 'food':694, 'bmdead':28091 }," +
							"{'day': '30/06/16', 'bm':35180146, 'fcre':1.64, 'fcrb':1.69, 'food':704, 'bmdead':28449 }," +
							"{'day': '01/07/16', 'bm':35670878, 'fcre':1.64, 'fcrb':1.69, 'food':696, 'bmdead':19125 }," +
							"{'day': '02/07/16', 'bm':36158248, 'fcre':1.64, 'fcrb':1.68, 'food':676, 'bmdead':20354 }," +
							"{'day': '03/07/16', 'bm':36613793, 'fcre':1.63, 'fcrb':1.68, 'food':685, 'bmdead':20622 }," +
							"{'day': '04/07/16', 'bm':37075067, 'fcre':1.63, 'fcrb':1.68, 'food':693, 'bmdead':20894 }," +
							"{'day': '05/07/16', 'bm':37542139, 'fcre':1.63, 'fcrb':1.68, 'food':702, 'bmdead':21169 }," +
							"{'day': '06/07/16', 'bm':38015084, 'fcre':1.63, 'fcrb':1.68, 'food':711, 'bmdead':21448 }," +
							"{'day': '07/07/16', 'bm':38494028, 'fcre':1.63, 'fcrb':1.68, 'food':720, 'bmdead':21676 }," +
							"{'day': '08/07/16', 'bm':38978994, 'fcre':1.63, 'fcrb':1.67, 'food':729, 'bmdead':21962 }," +
							"{'day': '09/07/16', 'bm':39470057, 'fcre':1.62, 'fcrb':1.67, 'food':738, 'bmdead':22251 }," +
							"{'day': '10/07/16', 'bm':39967294, 'fcre':1.62, 'fcrb':1.67, 'food':747, 'bmdead':22544 }," +
							"{'day': '11/07/16', 'bm':40470839, 'fcre':1.62, 'fcrb':1.67, 'food':757, 'bmdead':22784 }," +
							"{'day': '12/07/16', 'bm':40980715, 'fcre':1.62, 'fcrb':1.67, 'food':766, 'bmdead':23084 }," +
							"{'day': '13/07/16', 'bm':41497002, 'fcre':1.62, 'fcrb':1.67, 'food':776, 'bmdead':23388 }," +
							"{'day': '14/07/16', 'bm':42019780, 'fcre':1.62, 'fcrb':1.67, 'food':786, 'bmdead':23696 }," +
							"{'day': '15/07/16', 'bm':42549130, 'fcre':1.62, 'fcrb':1.67, 'food':796, 'bmdead':24009 }," +
							"{'day': '16/07/16', 'bm':43095663, 'fcre':1.61, 'fcrb':1.66, 'food':767, 'bmdead':13796 }," +
							"{'day': '17/07/16', 'bm':43603539, 'fcre':1.61, 'fcrb':1.66, 'food':776, 'bmdead':13963 }," +
							"{'day': '18/07/16', 'bm':44117395, 'fcre':1.61, 'fcrb':1.66, 'food':785, 'bmdead':14132 }," +
							"{'day': '19/07/16', 'bm':44637302, 'fcre':1.61, 'fcrb':1.66, 'food':795, 'bmdead':14303 }," +
							"{'day': '20/07/16', 'bm':45163331, 'fcre':1.61, 'fcrb':1.66, 'food':804, 'bmdead':14476 }," +
							"{'day': '21/07/16', 'bm':45695554, 'fcre':1.61, 'fcrb':1.66, 'food':813, 'bmdead':14652 }," +
							"{'day': '22/07/16', 'bm':46234045, 'fcre':1.61, 'fcrb':1.66, 'food':823, 'bmdead':14829 }," +
							"{'day': '23/07/16', 'bm':46778877, 'fcre':1.61, 'fcrb':1.66, 'food':833, 'bmdead':15009 }," +
							"{'day': '24/07/16', 'bm':47330124, 'fcre':1.60, 'fcrb':1.65, 'food':842, 'bmdead':15190 }," +
							"{'day': '25/07/16', 'bm':47887862, 'fcre':1.60, 'fcrb':1.65, 'food':852, 'bmdead':15374 }," +
							"{'day': '26/07/16', 'bm':48452236, 'fcre':1.60, 'fcrb':1.65, 'food':862, 'bmdead':15492 }," +
							"{'day': '27/07/16', 'bm':49023256, 'fcre':1.60, 'fcrb':1.65, 'food':873, 'bmdead':15680 }," +
							"{'day': '28/07/16', 'bm':49601001, 'fcre':1.60, 'fcrb':1.65, 'food':883, 'bmdead':15870 }," +
							"{'day': '29/07/16', 'bm':50184700, 'fcre':1.60, 'fcrb':1.65, 'food':873, 'bmdead':16911 }," +
							"{'day': '30/07/16', 'bm':50745881, 'fcre':1.60, 'fcrb':1.65, 'food':883, 'bmdead':17106 }," +
							"{'day': '31/07/16', 'bm':51313332, 'fcre':1.60, 'fcrb':1.65, 'food':893, 'bmdead':17303 }," +
							"{'day': '01/08/16', 'bm':51882508, 'fcre':1.60, 'fcrb':1.65, 'food':856, 'bmdead':22116 }," +
							"{'day': '02/08/16', 'bm':52412453, 'fcre':1.60, 'fcrb':1.65, 'food':865, 'bmdead':22352 }," +
							"{'day': '03/08/16', 'bm':52947802, 'fcre':1.60, 'fcrb':1.65, 'food':874, 'bmdead':22589 }," +
							"{'day': '04/08/16', 'bm':53488610, 'fcre':1.60, 'fcrb':1.65, 'food':883, 'bmdead':22830 }," +
							"{'day': '05/08/16', 'bm':54034931, 'fcre':1.60, 'fcrb':1.65, 'food':892, 'bmdead':23073 }," +
							"{'day': '06/08/16', 'bm':54586900, 'fcre':1.60, 'fcrb':1.65, 'food':901, 'bmdead':23241 }," +
							"{'day': '07/08/16', 'bm':55144497, 'fcre':1.60, 'fcrb':1.65, 'food':910, 'bmdead':23489 }," +
							"{'day': '08/08/16', 'bm':55707780, 'fcre':1.60, 'fcrb':1.65, 'food':919, 'bmdead':23739 }," +
							"{'day': '09/08/16', 'bm':56276806, 'fcre':1.60, 'fcrb':1.65, 'food':929, 'bmdead':23992 }," +
							"{'day': '10/08/16', 'bm':56850990, 'fcre':1.60, 'fcrb':1.65, 'food':915, 'bmdead':24891 }," +
							"{'day': '11/08/16', 'bm':57397910, 'fcre':1.60, 'fcrb':1.65, 'food':924, 'bmdead':25142 }," +
							"{'day': '12/08/16', 'bm':57950081, 'fcre':1.60, 'fcrb':1.65, 'food':933, 'bmdead':25395 }," +
							"{'day': '13/08/16', 'bm':58507553, 'fcre':1.60, 'fcrb':1.66, 'food':942, 'bmdead':25650 }," +
							"{'day': '14/08/16', 'bm':59070376, 'fcre':1.60, 'fcrb':1.66, 'food':951, 'bmdead':25909 }," +
							"{'day': '15/08/16', 'bm':59638602, 'fcre':1.61, 'fcrb':1.66, 'food':960, 'bmdead':26169 }," +
							"{'day': '16/08/16', 'bm':60216047, 'fcre':1.61, 'fcrb':1.66, 'food':1000, 'bmdead':22669 }," +
							"{'day': '17/08/16', 'bm':60842215, 'fcre':1.61, 'fcrb':1.66, 'food':1010, 'bmdead':22913 }," +
							"{'day': '18/08/16', 'bm':61474886, 'fcre':1.61, 'fcrb':1.66, 'food':1020, 'bmdead':23160 }," +
							"{'day': '19/08/16', 'bm':62114127, 'fcre':1.61, 'fcrb':1.66, 'food':1031, 'bmdead':23410 }," +
							"{'day': '20/08/16', 'bm':62760007, 'fcre':1.61, 'fcrb':1.66, 'food':1042, 'bmdead':23662 }," +
							"{'day': '21/08/16', 'bm':63412232, 'fcre':1.61, 'fcrb':1.66, 'food':1027, 'bmdead':24278 }," +
							"{'day': '22/08/16', 'bm':64033793, 'fcre':1.61, 'fcrb':1.66, 'food':1037, 'bmdead':24525 }," +
							"{'day': '23/08/16', 'bm':64661437, 'fcre':1.61, 'fcrb':1.66, 'food':1048, 'bmdead':24775 }," +
							"{'day': '24/08/16', 'bm':65295224, 'fcre':1.61, 'fcrb':1.66, 'food':1058, 'bmdead':25028 }," +
							"{'day': '25/08/16', 'bm':65935212, 'fcre':1.61, 'fcrb':1.66, 'food':1068, 'bmdead':25283 }," +
							"{'day': '26/08/16', 'bm':66581464, 'fcre':1.61, 'fcrb':1.66, 'food':1079, 'bmdead':25540 }," +
							"{'day': '27/08/16', 'bm':67234040, 'fcre':1.61, 'fcrb':1.66, 'food':1089, 'bmdead':25801 }," +
							"{'day': '28/08/16', 'bm':67893002, 'fcre':1.61, 'fcrb':1.66, 'food':1100, 'bmdead':26063 }," +
							"{'day': '29/08/16', 'bm':68558413, 'fcre':1.61, 'fcrb':1.67, 'food':1111, 'bmdead':26329 }," +
							"{'day': '30/08/16', 'bm':69230434, 'fcre':1.61, 'fcrb':1.67, 'food':1122, 'bmdead':26498 }," +
							"{'day': '31/08/16', 'bm':69909031, 'fcre':1.61, 'fcrb':1.67, 'food':1133, 'bmdead':26768 }," +
							"{'day': '01/09/16', 'bm':70595986, 'fcre':1.61, 'fcrb':1.67, 'food':1144, 'bmdead':25326 }," +
							"{'day': '02/09/16', 'bm':71263535, 'fcre':1.61, 'fcrb':1.67, 'food':1154, 'bmdead':25574 }," +
							"{'day': '03/09/16', 'bm':71937387, 'fcre':1.62, 'fcrb':1.67, 'food':1165, 'bmdead':25826 }," +
							"{'day': '04/09/16', 'bm':72617706, 'fcre':1.62, 'fcrb':1.67, 'food':1176, 'bmdead':25975 }," +
							"{'day': '05/09/16', 'bm':73304449, 'fcre':1.62, 'fcrb':1.67, 'food':1188, 'bmdead':26230 }," +
							"{'day': '06/09/16', 'bm':73997677, 'fcre':1.62, 'fcrb':1.67, 'food':1199, 'bmdead':26488 }," +
							"{'day': '07/09/16', 'bm':74697451, 'fcre':1.62, 'fcrb':1.67, 'food':1210, 'bmdead':26748 }," +
							"{'day': '08/09/16', 'bm':75403833, 'fcre':1.62, 'fcrb':1.68, 'food':1222, 'bmdead':27011 }," +
							"{'day': '09/09/16', 'bm':76116886, 'fcre':1.62, 'fcrb':1.68, 'food':1233, 'bmdead':27276 }," +
							"{'day': '10/09/16', 'bm':76836781, 'fcre':1.62, 'fcrb':1.68, 'food':1214, 'bmdead':27433 }," +
							"{'day': '11/09/16', 'bm':77527558, 'fcre':1.62, 'fcrb':1.68, 'food':1225, 'bmdead':27579 }," +
							"{'day': '12/09/16', 'bm':78224535, 'fcre':1.62, 'fcrb':1.68, 'food':1236, 'bmdead':27836 }," +
							"{'day': '13/09/16', 'bm':78927768, 'fcre':1.63, 'fcrb':1.68, 'food':1247, 'bmdead':28097 }," +
							"{'day': '14/09/16', 'bm':79637312, 'fcre':1.63, 'fcrb':1.68, 'food':1258, 'bmdead':28359 }," +
							"{'day': '15/09/16', 'bm':80353225, 'fcre':1.63, 'fcrb':1.69, 'food':1270, 'bmdead':28625 }," +
							"{'day': '16/09/16', 'bm':81042594, 'fcre':1.64, 'fcrb':1.70, 'food':2358, 'bmdead':61862 }," +
							"{'day': '17/09/16', 'bm':82319674, 'fcre':1.65, 'fcrb':1.71, 'food':2396, 'bmdead':62885 }," +
							"{'day': '18/09/16', 'bm':83617311, 'fcre':1.65, 'fcrb':1.71, 'food':2400, 'bmdead':63443 }," +
							"{'day': '19/09/16', 'bm':84886087, 'fcre':1.66, 'fcrb':1.71, 'food':2436, 'bmdead':64455 }," +
							"{'day': '20/09/16', 'bm':86174189, 'fcre':1.66, 'fcrb':1.72, 'food':2473, 'bmdead':65358 }," +
							"{'day': '21/09/16', 'bm':87481788, 'fcre':1.66, 'fcrb':1.72, 'food':2511, 'bmdead':66401 }," +
							"{'day': '22/09/16', 'bm':88809305, 'fcre':1.67, 'fcrb':1.73, 'food':2549, 'bmdead':67331 }," +
							"{'day': '23/09/16', 'bm':90157697, 'fcre':1.67, 'fcrb':1.73, 'food':2551, 'bmdead':67623 }," +
							"{'day': '24/09/16', 'bm':91475830, 'fcre':1.67, 'fcrb':1.74, 'food':2589, 'bmdead':68531 }," +
							"{'day': '25/09/16', 'bm':92813183, 'fcre':1.68, 'fcrb':1.74, 'food':2627, 'bmdead':69585 }," +
							"{'day': '26/09/16', 'bm':94170035, 'fcre':1.68, 'fcrb':1.75, 'food':2665, 'bmdead':70655 }," +
							"{'day': '27/09/16', 'bm':95546807, 'fcre':1.69, 'fcrb':1.75, 'food':2704, 'bmdead':71603 }," +
							"{'day': '28/09/16', 'bm':96944919, 'fcre':1.69, 'fcrb':1.76, 'food':2705, 'bmdead':71439 }," +
							"{'day': '29/09/16', 'bm':98303517, 'fcre':1.70, 'fcrb':1.76, 'food':2743, 'bmdead':72493 }," +
							"{'day': '30/09/16', 'bm':99681245, 'fcre':1.70, 'fcrb':1.77, 'food':2781, 'bmdead':73419 }," +
							"{'day': '01/10/16', 'bm':101078227, 'fcre':1.70, 'fcrb':1.77, 'food':2820, 'bmdead':74502 }," +
							"{'day': '02/10/16', 'bm':102494881, 'fcre':1.71, 'fcrb':1.78, 'food':2860, 'bmdead':75453 }," +
							"{'day': '03/10/16', 'bm':103933149, 'fcre':1.71, 'fcrb':1.78, 'food':2858, 'bmdead':74751 }," +
							"{'day': '04/10/16', 'bm':105345952, 'fcre':1.72, 'fcrb':1.79, 'food':2897, 'bmdead':75822 }," +
							"{'day': '05/10/16', 'bm':106777905, 'fcre':1.72, 'fcrb':1.79, 'food':2936, 'bmdead':76908 }," +
							"{'day': '06/10/16', 'bm':108229424, 'fcre':1.73, 'fcrb':1.80, 'food':2976, 'bmdead':77851 }," +
							"{'day': '07/10/16', 'bm':109702860, 'fcre':1.73, 'fcrb':1.80, 'food':2973, 'bmdead':76724 }," +
							"{'day': '08/10/16', 'bm':111141885, 'fcre':1.74, 'fcrb':1.81, 'food':3012, 'bmdead':77785 }," +
							"{'day': '09/10/16', 'bm':112599731, 'fcre':1.74, 'fcrb':1.81, 'food':3051, 'bmdead':78860 }," +
							"{'day': '10/10/16', 'bm':114076810, 'fcre':1.74, 'fcrb':1.82, 'food':3091, 'bmdead':79784 }," +
							"{'day': '11/10/16', 'bm':115573209, 'fcre':1.75, 'fcrb':1.82, 'food':3132, 'bmdead':80887 }," +
							"{'day': '12/10/16', 'bm':117091925, 'fcre':1.75, 'fcrb':1.83, 'food':3126, 'bmdead':79260 }," +
							"{'day': '13/10/16', 'bm':118574783, 'fcre':1.76, 'fcrb':1.83, 'food':3166, 'bmdead':80319 }," +
							"{'day': '14/10/16', 'bm':120076541, 'fcre':1.76, 'fcrb':1.84, 'food':3206, 'bmdead':81215 }," +
							"{'day': '15/10/16', 'bm':121597263, 'fcre':1.77, 'fcrb':1.84, 'food':3247, 'bmdead':82299 }," +
							"{'day': '16/10/16', 'bm':123167581, 'fcre':1.76, 'fcrb':1.83, 'food':1466, 'bmdead':53005 }," +
							"{'day': '17/10/16', 'bm':123808918, 'fcre':1.76, 'fcrb':1.83, 'food':1473, 'bmdead':53304 }," +
							"{'day': '18/10/16', 'bm':124453572, 'fcre':1.76, 'fcrb':1.84, 'food':1481, 'bmdead':53605 }," +
							"{'day': '19/10/16', 'bm':125101559, 'fcre':1.77, 'fcrb':1.84, 'food':1489, 'bmdead':53907 }," +
							"{'day': '20/10/16', 'bm':125752896, 'fcre':1.77, 'fcrb':1.84, 'food':1496, 'bmdead':54211 }," +
							"{'day': '21/10/16', 'bm':126407787, 'fcre':1.77, 'fcrb':1.85, 'food':1504, 'bmdead':54331 }," +
							"{'day': '22/10/16', 'bm':127066065, 'fcre':1.77, 'fcrb':1.85, 'food':1512, 'bmdead':54637 }," +
							"{'day': '23/10/16', 'bm':127727748, 'fcre':1.78, 'fcrb':1.85, 'food':1520, 'bmdead':54945 }," +
							"{'day': '24/10/16', 'bm':128392853, 'fcre':1.78, 'fcrb':1.86, 'food':1528, 'bmdead':55255 }," +
							"{'day': '25/10/16', 'bm':129064061, 'fcre':1.78, 'fcrb':1.86, 'food':1484, 'bmdead':52903 }," +
							"{'day': '26/10/16', 'bm':129701217, 'fcre':1.78, 'fcrb':1.86, 'food':1492, 'bmdead':53186 }," +
							"{'day': '27/10/16', 'bm':130341690, 'fcre':1.79, 'fcrb':1.87, 'food':1499, 'bmdead':53278 }," +
							"{'day': '28/10/16', 'bm':130985303, 'fcre':1.79, 'fcrb':1.87, 'food':1506, 'bmdead':53563 }," +
							"{'day': '29/10/16', 'bm':131632072, 'fcre':1.79, 'fcrb':1.87, 'food':1514, 'bmdead':53849 }," +
							"{'day': '30/10/16', 'bm':132282013, 'fcre':1.80, 'fcrb':1.88, 'food':1521, 'bmdead':54137 }," +
							"{'day': '31/10/16', 'bm':132935140, 'fcre':1.80, 'fcrb':1.88, 'food':1529, 'bmdead':54427 }," +
							"{'day': '01/11/16', 'bm':133604113, 'fcre':1.80, 'fcrb':1.88, 'food':882, 'bmdead':42075 }," +
							"{'day': '02/11/16', 'bm':133901079, 'fcre':1.80, 'fcrb':1.88, 'food':884, 'bmdead':42182 }," +
							"{'day': '03/11/16', 'bm':134198692, 'fcre':1.80, 'fcrb':1.88, 'food':886, 'bmdead':42289 }," +
							"{'day': '04/11/16', 'bm':134496953, 'fcre':1.80, 'fcrb':1.89, 'food':888, 'bmdead':42397 }," +
							"{'day': '05/11/16', 'bm':134795863, 'fcre':1.81, 'fcrb':1.89, 'food':890, 'bmdead':42504 }," +
							"{'day': '06/11/16', 'bm':135097825, 'fcre':1.81, 'fcrb':1.89, 'food':838, 'bmdead':40212 }," +
							"{'day': '07/11/16', 'bm':135376000, 'fcre':1.81, 'fcrb':1.90, 'food':839, 'bmdead':40306 }," +
							"{'day': '08/11/16', 'bm':135654735, 'fcre':1.81, 'fcrb':1.90, 'food':841, 'bmdead':40401 }," +
							"{'day': '09/11/16', 'bm':135934033, 'fcre':1.82, 'fcrb':1.90, 'food':843, 'bmdead':40497 }," +
							"{'day': '10/11/16', 'bm':136213893, 'fcre':1.82, 'fcrb':1.90, 'food':845, 'bmdead':40592 }," +
							"{'day': '11/11/16', 'bm':136494520, 'fcre':1.82, 'fcrb':1.91, 'food':846, 'bmdead':40485 }," +
							"{'day': '12/11/16', 'bm':136775713, 'fcre':1.82, 'fcrb':1.91, 'food':848, 'bmdead':40581 }," +
							"{'day': '13/11/16', 'bm':137057473, 'fcre':1.83, 'fcrb':1.91, 'food':850, 'bmdead':40676 }," +
							"{'day': '14/11/16', 'bm':137339801, 'fcre':1.83, 'fcrb':1.92, 'food':852, 'bmdead':40772 }," +
							"{'day': '15/11/16', 'bm':137622699, 'fcre':1.83, 'fcrb':1.92, 'food':853, 'bmdead':40868 }," +
							"{'day': '16/11/16', 'bm':137916819, 'fcre':1.83, 'fcrb':1.92, 'food':855, 'bmdead':30314 }," +
							"{'day': '17/11/16', 'bm':138184378, 'fcre':1.84, 'fcrb':1.93, 'food':857, 'bmdead':30379 }," +
							"{'day': '18/11/16', 'bm':138452449, 'fcre':1.84, 'fcrb':1.93, 'food':858, 'bmdead':30445 }," +
							"{'day': '19/11/16', 'bm':138721034, 'fcre':1.84, 'fcrb':1.93, 'food':860, 'bmdead':30511 }," +
							"{'day': '20/11/16', 'bm':138990133, 'fcre':1.84, 'fcrb':1.93, 'food':862, 'bmdead':30577 }," +
							"{'day': '21/11/16', 'bm':139259747, 'fcre':1.85, 'fcrb':1.94, 'food':863, 'bmdead':30643 }," +
							"{'day': '22/11/16', 'bm':139529877, 'fcre':1.85, 'fcrb':1.94, 'food':865, 'bmdead':30709 }," +
							"{'day': '23/11/16', 'bm':139800525, 'fcre':1.85, 'fcrb':1.94, 'food':867, 'bmdead':30775 }," +
							"{'day': '24/11/16', 'bm':140071691, 'fcre':1.85, 'fcrb':1.95, 'food':868, 'bmdead':30842 }," +
							"{'day': '25/11/16', 'bm':140343376, 'fcre':1.86, 'fcrb':1.95, 'food':870, 'bmdead':30908 }," +
							"{'day': '26/11/16', 'bm':140615581, 'fcre':1.86, 'fcrb':1.95, 'food':872, 'bmdead':30975 }," +
							"{'day': '27/11/16', 'bm':140888308, 'fcre':1.86, 'fcrb':1.96, 'food':874, 'bmdead':31042 }," +
							"{'day': '28/11/16', 'bm':141163448, 'fcre':1.86, 'fcrb':1.96, 'food':819, 'bmdead':29217 }," +
							"{'day': '29/11/16', 'bm':141416499, 'fcre':1.87, 'fcrb':1.96, 'food':820, 'bmdead':29276 }," +
							"{'day': '30/11/16', 'bm':141669997, 'fcre':1.87, 'fcrb':1.96, 'food':822, 'bmdead':29334 }," +
							"{'day': '01/12/16', 'bm':141947416, 'fcre':1.87, 'fcrb':1.97, 'food':823, 'bmdead':5920 }," +
							"{'day': '02/12/16', 'bm':142223433, 'fcre':1.87, 'fcrb':1.97, 'food':825, 'bmdead':5932 }," +
							"{'day': '03/12/16', 'bm':142499987, 'fcre':1.88, 'fcrb':1.97, 'food':826, 'bmdead':5944 }," +
							"{'day': '04/12/16', 'bm':142777079, 'fcre':1.88, 'fcrb':1.97, 'food':828, 'bmdead':5956 }," +
							"{'day': '05/12/16', 'bm':143054709, 'fcre':1.88, 'fcrb':1.98, 'food':830, 'bmdead':5968 }," +
							"{'day': '06/12/16', 'bm':143332879, 'fcre':1.88, 'fcrb':1.98, 'food':831, 'bmdead':5980 }," +
							"{'day': '07/12/16', 'bm':143611589, 'fcre':1.88, 'fcrb':1.98, 'food':833, 'bmdead':5991 }," +
							"{'day': '08/12/16', 'bm':143890841, 'fcre':1.89, 'fcrb':1.98, 'food':835, 'bmdead':6003 }," +
							"{'day': '09/12/16', 'bm':144170636, 'fcre':1.89, 'fcrb':1.98, 'food':836, 'bmdead':6015 }," +
							"{'day': '10/12/16', 'bm':144450975, 'fcre':1.89, 'fcrb':1.99, 'food':838, 'bmdead':6027 }," +
							"{'day': '11/12/16', 'bm':144731858, 'fcre':1.89, 'fcrb':1.99, 'food':839, 'bmdead':6039 }," +
							"{'day': '12/12/16', 'bm':145013288, 'fcre':1.90, 'fcrb':1.99, 'food':841, 'bmdead':6051 }," +
							"{'day': '13/12/16', 'bm':145295264, 'fcre':1.90, 'fcrb':1.99, 'food':843, 'bmdead':6063 }," +
							"{'day': '14/12/16', 'bm':145577789, 'fcre':1.90, 'fcrb':1.99, 'food':844, 'bmdead':6075 }," +
							"{'day': '15/12/16', 'bm':145860862, 'fcre':1.90, 'fcrb':2.00, 'food':846, 'bmdead':6087 }," +
							"{'day': '16/12/16', 'bm':146149933, 'fcre':1.90, 'fcrb':2.00, 'food':848, 'bmdead':653 }," +
							"{'day': '17/12/16', 'bm':146454195, 'fcre':1.91, 'fcrb':2.00, 'food':849, 'bmdead':654 }," +
							"{'day': '18/12/16', 'bm':146759090, 'fcre':1.91, 'fcrb':2.00, 'food':851, 'bmdead':656 }," +
							"{'day': '19/12/16', 'bm':147064621, 'fcre':1.91, 'fcrb':2.00, 'food':853, 'bmdead':657 }," +
							"{'day': '20/12/16', 'bm':147370787, 'fcre':1.91, 'fcrb':2.01, 'food':855, 'bmdead':659 }," +
							"{'day': '21/12/16', 'bm':147677811, 'fcre':1.91, 'fcrb':2.01, 'food':797, 'bmdead':440 }," +
							"{'day': '22/12/16', 'bm':147961164, 'fcre':1.91, 'fcrb':2.01, 'food':799, 'bmdead':441 }," +
							"{'day': '23/12/16', 'bm':148245060, 'fcre':1.92, 'fcrb':2.01, 'food':801, 'bmdead':441 }," +
							"{'day': '24/12/16', 'bm':148529501, 'fcre':1.92, 'fcrb':2.01, 'food':802, 'bmdead':442 }," +
							"{'day': '25/12/16', 'bm':148814488, 'fcre':1.92, 'fcrb':2.01, 'food':804, 'bmdead':443 }," +
							"{'day': '26/12/16', 'bm':149100021, 'fcre':1.92, 'fcrb':2.02, 'food':805, 'bmdead':444 }," +
							"{'day': '27/12/16', 'bm':149386102, 'fcre':1.92, 'fcrb':2.02, 'food':807, 'bmdead':445 }," +
							"{'day': '28/12/16', 'bm':149672733, 'fcre':1.93, 'fcrb':2.02, 'food':808, 'bmdead':446 }," +
							"{'day': '29/12/16', 'bm':149959913, 'fcre':1.93, 'fcrb':2.02, 'food':810, 'bmdead':447 }," +
							"{'day': '30/12/16', 'bm':150247644, 'fcre':1.93, 'fcrb':2.02, 'food':811, 'bmdead':447 }," +
							"{'day': '31/12/16', 'bm':150535927, 'fcre':1.93, 'fcrb':2.02, 'food':813, 'bmdead':448 }," +
							"{'day': '01/01/17', 'bm':150819818, 'fcre':1.93, 'fcrb':2.03, 'food':814, 'bmdead':5395 }," +
							"{'day': '02/01/17', 'bm':151127652, 'fcre':1.93, 'fcrb':2.03, 'food':816, 'bmdead':5407 }," +
							"{'day': '03/01/17', 'bm':151436114, 'fcre':1.94, 'fcrb':2.03, 'food':818, 'bmdead':5418 }," +
							"{'day': '04/01/17', 'bm':151745205, 'fcre':1.94, 'fcrb':2.03, 'food':819, 'bmdead':5429 }," +
							"{'day': '05/01/17', 'bm':152054927, 'fcre':1.94, 'fcrb':2.03, 'food':821, 'bmdead':5440 }," +
							"{'day': '06/01/17', 'bm':152365281, 'fcre':1.94, 'fcrb':2.03, 'food':823, 'bmdead':5452 }," +
							"{'day': '07/01/17', 'bm':152676269, 'fcre':1.94, 'fcrb':2.03, 'food':824, 'bmdead':5463 }," +
							"{'day': '08/01/17', 'bm':152987891, 'fcre':1.94, 'fcrb':2.04, 'food':826, 'bmdead':5474 }," +
							"{'day': '09/01/17', 'bm':153300149, 'fcre':1.94, 'fcrb':2.04, 'food':828, 'bmdead':5486 }," +
							"{'day': '10/01/17', 'bm':153613043, 'fcre':1.95, 'fcrb':2.04, 'food':830, 'bmdead':5497 }," +
							"{'day': '11/01/17', 'bm':153926577, 'fcre':1.95, 'fcrb':2.04, 'food':831, 'bmdead':5509 }," +
							"{'day': '12/01/17', 'bm':154239830, 'fcre':1.95, 'fcrb':2.04, 'food':771, 'bmdead':6440 }," +
							"{'day': '13/01/17', 'bm':154526609, 'fcre':1.95, 'fcrb':2.04, 'food':773, 'bmdead':6452 }," +
							"{'day': '14/01/17', 'bm':154813920, 'fcre':1.95, 'fcrb':2.04, 'food':774, 'bmdead':6465 }," +
							"{'day': '15/01/17', 'bm':155101766, 'fcre':1.95, 'fcrb':2.04, 'food':776, 'bmdead':6477 }," +
							"{'day': '16/01/17', 'bm':155395245, 'fcre':1.95, 'fcrb':2.05, 'food':777, 'bmdead':1390 }," +
							"{'day': '17/01/17', 'bm':155667436, 'fcre':1.95, 'fcrb':2.05, 'food':778, 'bmdead':1393 }," +
							"{'day': '18/01/17', 'bm':155940102, 'fcre':1.96, 'fcrb':2.05, 'food':780, 'bmdead':1395 }," +
							"{'day': '19/01/17', 'bm':156213247, 'fcre':1.96, 'fcrb':2.05, 'food':781, 'bmdead':1398 }," +
							"{'day': '20/01/17', 'bm':156486870, 'fcre':1.96, 'fcrb':2.05, 'food':782, 'bmdead':1400 }," +
							"{'day': '21/01/17', 'bm':156760972, 'fcre':1.96, 'fcrb':2.05, 'food':784, 'bmdead':1402 }," +
							"{'day': '22/01/17', 'bm':157035554, 'fcre':1.96, 'fcrb':2.05, 'food':785, 'bmdead':1405 }," +
							"{'day': '23/01/17', 'bm':157310617, 'fcre':1.96, 'fcrb':2.06, 'food':787, 'bmdead':1407 }," +
							"{'day': '24/01/17', 'bm':157586162, 'fcre':1.97, 'fcrb':2.06, 'food':788, 'bmdead':1410 }," +
							"{'day': '25/01/17', 'bm':157862190, 'fcre':1.97, 'fcrb':2.06, 'food':789, 'bmdead':1412 }," +
							"{'day': '26/01/17', 'bm':158138701, 'fcre':1.97, 'fcrb':2.06, 'food':791, 'bmdead':1415 }," +
							"{'day': '27/01/17', 'bm':158415696, 'fcre':1.97, 'fcrb':2.06, 'food':792, 'bmdead':1417 }," +
							"{'day': '28/01/17', 'bm':158693177, 'fcre':1.97, 'fcrb':2.06, 'food':793, 'bmdead':1420 }," +
							"{'day': '29/01/17', 'bm':158971143, 'fcre':1.97, 'fcrb':2.07, 'food':795, 'bmdead':1422 }," +
							"{'day': '30/01/17', 'bm':159249597, 'fcre':1.98, 'fcrb':2.07, 'food':796, 'bmdead':1425 }," +
							"{'day': '31/01/17', 'bm':159528538, 'fcre':1.98, 'fcrb':2.07, 'food':798, 'bmdead':1427 }," +
							"{'day': '01/02/17', 'bm':159802723, 'fcre':1.98, 'fcrb':2.07, 'food':799, 'bmdead':6675 }," +
							"{'day': '02/02/17', 'bm':160099843, 'fcre':1.98, 'fcrb':2.07, 'food':800, 'bmdead':6687 }," +
							"{'day': '03/02/17', 'bm':160397515, 'fcre':1.98, 'fcrb':2.07, 'food':802, 'bmdead':6700 }," +
							"{'day': '04/02/17', 'bm':160695740, 'fcre':1.98, 'fcrb':2.07, 'food':803, 'bmdead':6713 }," +
							"{'day': '05/02/17', 'bm':160994038, 'fcre':1.98, 'fcrb':2.07, 'food':741, 'bmdead':7206 }," +
							"{'day': '06/02/17', 'bm':161265230, 'fcre':1.98, 'fcrb':2.08, 'food':742, 'bmdead':7218 }," +
							"{'day': '07/02/17', 'bm':161536879, 'fcre':1.99, 'fcrb':2.08, 'food':743, 'bmdead':7231 }," +
							"{'day': '08/02/17', 'bm':161808985, 'fcre':1.99, 'fcrb':2.08, 'food':744, 'bmdead':7243 }," +
							"{'day': '09/02/17', 'bm':162081548, 'fcre':1.99, 'fcrb':2.08, 'food':746, 'bmdead':7256 }," +
							"{'day': '10/02/17', 'bm':162354571, 'fcre':1.99, 'fcrb':2.08, 'food':747, 'bmdead':7268 }," +
							"{'day': '11/02/17', 'bm':162628053, 'fcre':1.99, 'fcrb':2.08, 'food':748, 'bmdead':7281 }," +
							"{'day': '12/02/17', 'bm':162901995, 'fcre':1.99, 'fcrb':2.08, 'food':749, 'bmdead':7294 }," +
							"{'day': '13/02/17', 'bm':163176399, 'fcre':1.99, 'fcrb':2.08, 'food':751, 'bmdead':7306 }," +
							"{'day': '14/02/17', 'bm':163451265, 'fcre':2.00, 'fcrb':2.09, 'food':752, 'bmdead':7319 }," +
							"{'day': '15/02/17', 'bm':163726593, 'fcre':2.00, 'fcrb':2.09, 'food':753, 'bmdead':7331 }," +
							"{'day': '16/02/17', 'bm':164002384, 'fcre':2.00, 'fcrb':2.09, 'food':754, 'bmdead':7344 }," +
							"{'day': '17/02/17', 'bm':164278640, 'fcre':2.00, 'fcrb':2.09, 'food':756, 'bmdead':7357 }," +
							"{'day': '18/02/17', 'bm':164555361, 'fcre':2.00, 'fcrb':2.09, 'food':757, 'bmdead':7370 }," +
							"{'day': '19/02/17', 'bm':164832548, 'fcre':2.00, 'fcrb':2.09, 'food':758, 'bmdead':7382 }," +
							"{'day': '20/02/17', 'bm':165110201, 'fcre':2.00, 'fcrb':2.09, 'food':760, 'bmdead':7395 }," +
							"{'day': '21/02/17', 'bm':165388321, 'fcre':2.00, 'fcrb':2.09, 'food':761, 'bmdead':7408 }," +
							"{'day': '22/02/17', 'bm':165666910, 'fcre':2.01, 'fcrb':2.10, 'food':762, 'bmdead':7421 }," +
							"{'day': '23/02/17', 'bm':165945968, 'fcre':2.01, 'fcrb':2.10, 'food':763, 'bmdead':7433 }," +
							"{'day': '24/02/17', 'bm':166225495, 'fcre':2.01, 'fcrb':2.10, 'food':765, 'bmdead':7446 }," +
							"{'day': '25/02/17', 'bm':166505493, 'fcre':2.01, 'fcrb':2.10, 'food':766, 'bmdead':7459 }," +
							"{'day': '26/02/17', 'bm':166785962, 'fcre':2.01, 'fcrb':2.10, 'food':767, 'bmdead':7472 }," +
							"{'day': '27/02/17', 'bm':167066904, 'fcre':2.01, 'fcrb':2.10, 'food':769, 'bmdead':7485 }," +
							"{'day': '28/02/17', 'bm':167348318, 'fcre':2.01, 'fcrb':2.10, 'food':770, 'bmdead':7498 }," +
							"{'day': '01/03/17', 'bm':167623696, 'fcre':2.01, 'fcrb':2.10, 'food':687, 'bmdead':14021 }," +
							"{'day': '02/03/17', 'bm':167832069, 'fcre':2.01, 'fcrb':2.11, 'food':688, 'bmdead':14039 }," +
							"{'day': '03/03/17', 'bm':168040700, 'fcre':2.02, 'fcrb':2.11, 'food':689, 'bmdead':14058 }," +
							"{'day': '04/03/17', 'bm':168249590, 'fcre':2.02, 'fcrb':2.11, 'food':690, 'bmdead':14077 }," +
							"{'day': '05/03/17', 'bm':168458738, 'fcre':2.02, 'fcrb':2.11, 'food':691, 'bmdead':14095 }," +
							"{'day': '06/03/17', 'bm':168668144, 'fcre':2.02, 'fcrb':2.11, 'food':692, 'bmdead':14114 }," +
							"{'day': '07/03/17', 'bm':168877810, 'fcre':2.02, 'fcrb':2.11, 'food':692, 'bmdead':14133 }," +
							"{'day': '08/03/17', 'bm':169087735, 'fcre':2.02, 'fcrb':2.12, 'food':693, 'bmdead':14152 }," +
							"{'day': '09/03/17', 'bm':169297920, 'fcre':2.03, 'fcrb':2.12, 'food':694, 'bmdead':14170 }," +
							"{'day': '10/03/17', 'bm':169508365, 'fcre':2.03, 'fcrb':2.12, 'food':695, 'bmdead':14189 }," +
							"{'day': '11/03/17', 'bm':169719071, 'fcre':2.03, 'fcrb':2.12, 'food':696, 'bmdead':14208 }," +
							"{'day': '12/03/17', 'bm':169930037, 'fcre':2.03, 'fcrb':2.12, 'food':697, 'bmdead':14227 }," +
							"{'day': '13/03/17', 'bm':170141264, 'fcre':2.03, 'fcrb':2.13, 'food':698, 'bmdead':14246 }," +
							"{'day': '14/03/17', 'bm':170352753, 'fcre':2.03, 'fcrb':2.13, 'food':698, 'bmdead':14265 }," +
							"{'day': '15/03/17', 'bm':170564503, 'fcre':2.04, 'fcrb':2.13, 'food':699, 'bmdead':14284 }," +
							"{'day': '16/03/17', 'bm':170786732, 'fcre':2.04, 'fcrb':2.13, 'food':700, 'bmdead':4086 }," +
							"{'day': '17/03/17', 'bm':171012977, 'fcre':2.04, 'fcrb':2.13, 'food':701, 'bmdead':4092 }," +
							"{'day': '18/03/17', 'bm':171239522, 'fcre':2.04, 'fcrb':2.13, 'food':702, 'bmdead':4097 }," +
							"{'day': '19/03/17', 'bm':171466367, 'fcre':2.04, 'fcrb':2.13, 'food':703, 'bmdead':4103 }," +
							"{'day': '20/03/17', 'bm':171693512, 'fcre':2.04, 'fcrb':2.14, 'food':704, 'bmdead':4108 }," +
							"{'day': '21/03/17', 'bm':171920959, 'fcre':2.04, 'fcrb':2.14, 'food':705, 'bmdead':4114 }," +
							"{'day': '22/03/17', 'bm':172148706, 'fcre':2.05, 'fcrb':2.14, 'food':706, 'bmdead':4119 }," +
							"{'day': '23/03/17', 'bm':172376755, 'fcre':2.05, 'fcrb':2.14, 'food':707, 'bmdead':4125 }," +
							"{'day': '24/03/17', 'bm':172605106, 'fcre':2.05, 'fcrb':2.14, 'food':708, 'bmdead':4130 }," +
							"{'day': '25/03/17', 'bm':172833759, 'fcre':2.05, 'fcrb':2.14, 'food':709, 'bmdead':4136 }," +
							"{'day': '26/03/17', 'bm':173062715, 'fcre':2.05, 'fcrb':2.14, 'food':710, 'bmdead':4142 }," +
							"{'day': '27/03/17', 'bm':173291975, 'fcre':2.05, 'fcrb':2.15, 'food':710, 'bmdead':4147 }," +
							"{'day': '28/03/17', 'bm':173521537, 'fcre':2.05, 'fcrb':2.15, 'food':711, 'bmdead':4153 }," +
							"{'day': '29/03/17', 'bm':173751404, 'fcre':2.06, 'fcrb':2.15, 'food':712, 'bmdead':4158 }," +
							"{'day': '30/03/17', 'bm':173981315, 'fcre':2.06, 'fcrb':2.15, 'food':661, 'bmdead':4424 }," +
							"{'day': '31/03/17', 'bm':174192237, 'fcre':2.06, 'fcrb':2.15, 'food':662, 'bmdead':4430 }," +
							"{'day': '01/04/17', 'bm':174374712, 'fcre':2.06, 'fcrb':2.15, 'food':663, 'bmdead':33136 }," +
							"{'day': '02/04/17', 'bm':174558788, 'fcre':2.06, 'fcrb':2.15, 'food':663, 'bmdead':33178 }," +
							"{'day': '03/04/17', 'bm':174743051, 'fcre':2.06, 'fcrb':2.16, 'food':664, 'bmdead':33219 }," +
							"{'day': '04/04/17', 'bm':174927503, 'fcre':2.06, 'fcrb':2.16, 'food':665, 'bmdead':33261 }," +
							"{'day': '05/04/17', 'bm':175112143, 'fcre':2.07, 'fcrb':2.16, 'food':665, 'bmdead':33302 }," +
							"{'day': '06/04/17', 'bm':175296971, 'fcre':2.07, 'fcrb':2.16, 'food':666, 'bmdead':33343 }," +
							"{'day': '07/04/17', 'bm':175481989, 'fcre':2.07, 'fcrb':2.17, 'food':667, 'bmdead':33385 }," +
							"{'day': '08/04/17', 'bm':175667195, 'fcre':2.07, 'fcrb':2.17, 'food':668, 'bmdead':33427 }," +
							"{'day': '09/04/17', 'bm':175852590, 'fcre':2.07, 'fcrb':2.17, 'food':668, 'bmdead':33468 }," +
							"{'day': '10/04/17', 'bm':176038175, 'fcre':2.07, 'fcrb':2.17, 'food':669, 'bmdead':33510 }," +
							"{'day': '11/04/17', 'bm':176223949, 'fcre':2.08, 'fcrb':2.17, 'food':670, 'bmdead':33552 }," +
							"{'day': '12/04/17', 'bm':176409913, 'fcre':2.08, 'fcrb':2.18, 'food':670, 'bmdead':33593 }," +
							"{'day': '13/04/17', 'bm':176596066, 'fcre':2.08, 'fcrb':2.18, 'food':671, 'bmdead':33635 }," +
							"{'day': '14/04/17', 'bm':176782410, 'fcre':2.08, 'fcrb':2.18, 'food':672, 'bmdead':33677 }," +
							"{'day': '15/04/17', 'bm':176968944, 'fcre':2.08, 'fcrb':2.18, 'food':672, 'bmdead':33719 }," +
							"{'day': '16/04/17', 'bm':177155668, 'fcre':2.08, 'fcrb':2.18, 'food':673, 'bmdead':33761 }," +
							"{'day': '17/04/17', 'bm':177342583, 'fcre':2.09, 'fcrb':2.19, 'food':674, 'bmdead':33803 }," +
							"{'day': '18/04/17', 'bm':177529688, 'fcre':2.09, 'fcrb':2.19, 'food':675, 'bmdead':33845 }," +
							"{'day': '19/04/17', 'bm':177716985, 'fcre':2.09, 'fcrb':2.19, 'food':675, 'bmdead':33888 }," +
							"{'day': '20/04/17', 'bm':177904472, 'fcre':2.09, 'fcrb':2.19, 'food':676, 'bmdead':33930 }," +
							"{'day': '21/04/17', 'bm':178092151, 'fcre':2.09, 'fcrb':2.19, 'food':677, 'bmdead':33972 }," +
							"{'day': '22/04/17', 'bm':178280022, 'fcre':2.09, 'fcrb':2.20, 'food':677, 'bmdead':34014 }," +
							"{'day': '23/04/17', 'bm':178468084, 'fcre':2.10, 'fcrb':2.20, 'food':678, 'bmdead':34057 }," +
							"{'day': '24/04/17', 'bm':178656338, 'fcre':2.10, 'fcrb':2.20, 'food':679, 'bmdead':34099 }," +
							"{'day': '25/04/17', 'bm':178844784, 'fcre':2.10, 'fcrb':2.20, 'food':680, 'bmdead':34142 }," +
							"{'day': '26/04/17', 'bm':179033422, 'fcre':2.10, 'fcrb':2.20, 'food':680, 'bmdead':34184 }," +
							"{'day': '27/04/17', 'bm':179222253, 'fcre':2.10, 'fcrb':2.21, 'food':681, 'bmdead':34227 }," +
							"{'day': '28/04/17', 'bm':179411276, 'fcre':2.10, 'fcrb':2.21, 'food':682, 'bmdead':34269 }," +
							"{'day': '29/04/17', 'bm':179600762, 'fcre':2.10, 'fcrb':2.21, 'food':611, 'bmdead':34042 }," +
							"{'day': '30/04/17', 'bm':179765589, 'fcre':2.11, 'fcrb':2.21, 'food':611, 'bmdead':34080 }," +
							"{'day': '01/05/17', 'bm':179920812, 'fcre':2.11, 'fcrb':2.21, 'food':612, 'bmdead':43865 }," +
							"{'day': '02/05/17', 'bm':180116782, 'fcre':2.11, 'fcrb':2.21, 'food':612, 'bmdead':43924 }," +
							"{'day': '03/05/17', 'bm':180312954, 'fcre':2.11, 'fcrb':2.22, 'food':613, 'bmdead':43982 }," +
							"{'day': '04/05/17', 'bm':180509330, 'fcre':2.11, 'fcrb':2.22, 'food':614, 'bmdead':44041 }," +
							"{'day': '05/05/17', 'bm':180705909, 'fcre':2.11, 'fcrb':2.22, 'food':614, 'bmdead':44100 }," +
							"{'day': '06/05/17', 'bm':180902691, 'fcre':2.11, 'fcrb':2.22, 'food':615, 'bmdead':44159 }," +
							"{'day': '07/05/17', 'bm':181099677, 'fcre':2.11, 'fcrb':2.22, 'food':616, 'bmdead':44217 }," +
							"{'day': '08/05/17', 'bm':181296866, 'fcre':2.12, 'fcrb':2.22, 'food':616, 'bmdead':44276 }," +
							"{'day': '09/05/17', 'bm':181494259, 'fcre':2.12, 'fcrb':2.23, 'food':617, 'bmdead':44335 }," +
							"{'day': '10/05/17', 'bm':181691856, 'fcre':2.12, 'fcrb':2.23, 'food':618, 'bmdead':44395 }," +
							"{'day': '11/05/17', 'bm':181889658, 'fcre':2.12, 'fcrb':2.23, 'food':618, 'bmdead':44454 }," +
							"{'day': '12/05/17', 'bm':182087664, 'fcre':2.12, 'fcrb':2.23, 'food':619, 'bmdead':44513 }," +
							"{'day': '13/05/17', 'bm':182285875, 'fcre':2.12, 'fcrb':2.23, 'food':620, 'bmdead':44572 }," +
							"{'day': '14/05/17', 'bm':182484290, 'fcre':2.12, 'fcrb':2.23, 'food':620, 'bmdead':44632 }," +
							"{'day': '15/05/17', 'bm':182682911, 'fcre':2.12, 'fcrb':2.24, 'food':621, 'bmdead':44691 }," +
							"{'day': '16/05/17', 'bm':182860466, 'fcre':2.13, 'fcrb':2.24, 'food':1499, 'bmdead':66022 }," +
							"{'day': '17/05/17', 'bm':183421604, 'fcre':2.13, 'fcrb':2.25, 'food':1504, 'bmdead':66248 }," +
							"{'day': '18/05/17', 'bm':183984441, 'fcre':2.13, 'fcrb':2.25, 'food':1509, 'bmdead':66476 }," +
							"{'day': '19/05/17', 'bm':184548980, 'fcre':2.13, 'fcrb':2.25, 'food':1513, 'bmdead':66704 }," +
							"{'day': '20/05/17', 'bm':185115228, 'fcre':2.14, 'fcrb':2.25, 'food':1444, 'bmdead':66932 }," +
							"{'day': '21/05/17', 'bm':185644732, 'fcre':2.14, 'fcrb':2.25, 'food':1448, 'bmdead':67148 }," +
							"{'day': '22/05/17', 'bm':186175726, 'fcre':2.14, 'fcrb':2.26, 'food':1452, 'bmdead':67365 }," +
							"{'day': '23/05/17', 'bm':186708214, 'fcre':2.14, 'fcrb':2.26, 'food':1456, 'bmdead':67582 }," +
							"{'day': '24/05/17', 'bm':187242201, 'fcre':2.14, 'fcrb':2.26, 'food':1460, 'bmdead':67800 }," +
							"{'day': '25/05/17', 'bm':187777975, 'fcre':2.14, 'fcrb':2.26, 'food':1465, 'bmdead':67733 }," +
							"{'day': '26/05/17', 'bm':188315257, 'fcre':2.15, 'fcrb':2.27, 'food':1469, 'bmdead':67952 }," +
							"{'day': '27/05/17', 'bm':188854052, 'fcre':2.15, 'fcrb':2.27, 'food':1473, 'bmdead':68171 }," +
							"{'day': '28/05/17', 'bm':189394365, 'fcre':2.15, 'fcrb':2.27, 'food':1477, 'bmdead':68391 }," +
							"{'day': '29/05/17', 'bm':189936198, 'fcre':2.15, 'fcrb':2.27, 'food':1482, 'bmdead':68611 }," +
							"{'day': '30/05/17', 'bm':190479556, 'fcre':2.15, 'fcrb':2.27, 'food':1486, 'bmdead':68832 }," +
							"{'day': '31/05/17', 'bm':191024734, 'fcre':2.15, 'fcrb':2.28, 'food':1414, 'bmdead':68764 }," +
							"{'day': '01/06/17', 'bm':191492027, 'fcre':2.17, 'fcrb':2.30, 'food':4175, 'bmdead':112044 }," +
							"{'day': '02/06/17', 'bm':193148108, 'fcre':2.17, 'fcrb':2.30, 'food':4211, 'bmdead':112785 }," +
							"{'day': '03/06/17', 'bm':194818445, 'fcre':2.18, 'fcrb':2.30, 'food':4247, 'bmdead':113827 }," +
							"{'day': '04/06/17', 'bm':196503160, 'fcre':2.18, 'fcrb':2.31, 'food':4284, 'bmdead':114879 }," +
							"{'day': '05/06/17', 'bm':198202980, 'fcre':2.18, 'fcrb':2.31, 'food':4242, 'bmdead':115336 }," +
							"{'day': '06/06/17', 'bm':199861315, 'fcre':2.19, 'fcrb':2.31, 'food':4277, 'bmdead':116369 }," +
							"{'day': '07/06/17', 'bm':201533457, 'fcre':2.19, 'fcrb':2.32, 'food':4313, 'bmdead':117411 }," +
							"{'day': '08/06/17', 'bm':203220141, 'fcre':2.19, 'fcrb':2.32, 'food':4268, 'bmdead':117842 }," +
							"{'day': '09/06/17', 'bm':204872070, 'fcre':2.20, 'fcrb':2.32, 'food':4302, 'bmdead':118869 }," +
							"{'day': '10/06/17', 'bm':206537673, 'fcre':2.20, 'fcrb':2.33, 'food':4337, 'bmdead':119589 }," +
							"{'day': '11/06/17', 'bm':208216747, 'fcre':2.20, 'fcrb':2.33, 'food':4373, 'bmdead':120631 }," +
							"{'day': '12/06/17', 'bm':209910043, 'fcre':2.21, 'fcrb':2.34, 'food':4324, 'bmdead':121040 }," +
							"{'day': '13/06/17', 'bm':211560173, 'fcre':2.21, 'fcrb':2.34, 'food':4358, 'bmdead':122062 }," +
							"{'day': '14/06/17', 'bm':213223202, 'fcre':2.21, 'fcrb':2.34, 'food':4392, 'bmdead':123092 }," +
							"{'day': '15/06/17', 'bm':214899563, 'fcre':2.22, 'fcrb':2.35, 'food':4427, 'bmdead':123802 }," +
							"{'day': '16/06/17', 'bm':216561471, 'fcre':2.21, 'fcrb':2.34, 'food':1711, 'bmdead':152407 }," +
							"{'day': '17/06/17', 'bm':217121410, 'fcre':2.21, 'fcrb':2.34, 'food':1715, 'bmdead':152909 }," +
							"{'day': '18/06/17', 'bm':217683023, 'fcre':2.21, 'fcrb':2.35, 'food':1720, 'bmdead':153078 }," +
							"{'day': '19/06/17', 'bm':218245981, 'fcre':2.21, 'fcrb':2.35, 'food':1724, 'bmdead':153582 }," +
							"{'day': '20/06/17', 'bm':218810286, 'fcre':2.22, 'fcrb':2.35, 'food':1729, 'bmdead':154087 }," +
							"{'day': '21/06/17', 'bm':219376279, 'fcre':2.22, 'fcrb':2.36, 'food':1733, 'bmdead':154257 }," +
							"{'day': '22/06/17', 'bm':219943628, 'fcre':2.22, 'fcrb':2.36, 'food':1738, 'bmdead':154765 }," +
							"{'day': '23/06/17', 'bm':220512334, 'fcre':2.22, 'fcrb':2.36, 'food':1742, 'bmdead':155274 }," +
							"{'day': '24/06/17', 'bm':221083765, 'fcre':2.22, 'fcrb':2.37, 'food':1658, 'bmdead':154422 }," +
							"{'day': '25/06/17', 'bm':221611224, 'fcre':2.23, 'fcrb':2.37, 'food':1662, 'bmdead':154898 }," +
							"{'day': '26/06/17', 'bm':222139832, 'fcre':2.23, 'fcrb':2.38, 'food':1666, 'bmdead':155376 }," +
							"{'day': '27/06/17', 'bm':222669937, 'fcre':2.23, 'fcrb':2.38, 'food':1670, 'bmdead':155512 }," +
							"{'day': '28/06/17', 'bm':223201197, 'fcre':2.23, 'fcrb':2.38, 'food':1674, 'bmdead':155992 }," +
							"{'day': '29/06/17', 'bm':223733616, 'fcre':2.23, 'fcrb':2.39, 'food':1678, 'bmdead':156473 }," +
							"{'day': '30/06/17', 'bm':224267543, 'fcre':2.24, 'fcrb':2.39, 'food':1682, 'bmdead':156609 }]} ";
		
		Consumption consumption = null;
		try {
			consumption = mapper.readValue(data, Consumption.class);
		} catch (Exception e) {
			e.printStackTrace();
		}			
		
		return consumption;		
	}
	
	public List<Monthly> getMonthly() {
		return monthly;
	}

	public void setMonthly(List<Monthly> monthly) {
		this.monthly = monthly;
	}

	public List<Daily> getDaily() {
		return daily;
	}

	public void setDaily(List<Daily> daily) {
		this.daily = daily;
	}	
}
