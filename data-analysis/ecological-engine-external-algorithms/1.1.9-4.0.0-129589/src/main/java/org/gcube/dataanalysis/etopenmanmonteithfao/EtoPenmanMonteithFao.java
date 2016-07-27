package org.gcube.dataanalysis.etopenmanmonteithfao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.SessionFactory;

/**
 * This is an algorithm that returns the the calculation of The Penman-Monteith Evapotranspiration Estimation by FAO-56 Method. The input is a general tabular resource with eight columns (date,
 * latitude, altitude, temperature, max temperature, min temperature relative humidity, wind speed, and radiation).
 */
public class EtoPenmanMonteithFao extends StandardLocalExternalAlgorithm {
	// Class Attributes
	String outputtablename;
	String outputtable;

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "This is an algorithm that returns the calculation of The Penman-Monteith Evapotranspiration Estimation by FAO-56 Method. The input is a general tabular resource with nine columns (date, latitude, altitude, temperature, max temperature, min temperature relative humidity, wind speed, and radiation).";
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initialization");
	}

	@Override
	protected void process() throws Exception {
		// Recovering data
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
		try {
			// Getting context variables
			String tablename = getInputParameter("MetTable");
			String columnnames = getInputParameter("MetColumns");
			outputtablename = getInputParameter("OutputTableName");
			outputtable = getInputParameter("OutputTable");
			// Select variables from tables
			String[] columnlist = columnnames.split(AlgorithmConfiguration.getListSeparator());
			List<Object> dayList = DatabaseFactory.executeSQLQuery("select " + columnlist[0] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> latitudeList = DatabaseFactory.executeSQLQuery("select " + columnlist[1] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> altitudeList = DatabaseFactory.executeSQLQuery("select " + columnlist[2] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> tempatureList = DatabaseFactory.executeSQLQuery("select " + columnlist[3] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> maxTempatureList = DatabaseFactory.executeSQLQuery("select " + columnlist[4] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> minTempatureList = DatabaseFactory.executeSQLQuery("select " + columnlist[5] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> relativeHumidityList = DatabaseFactory.executeSQLQuery("select " + columnlist[6] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> windSpeedList = DatabaseFactory.executeSQLQuery("select " + columnlist[7] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> radiationList = DatabaseFactory.executeSQLQuery("select " + columnlist[8] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			// creating output table
			AnalysisLogger.getLogger().info("Creating output table [" + "create table " + outputtable + " (day date, etopmf real)]");
			DatabaseFactory.executeSQLUpdate("create table " + outputtable + " (day date, etopmf real)", dbconnection);
			/** Business Logic **/
			// Estimation of reference evapotranspiration
			/** Variables Declaration **/
			Date day;
			Double etopmf; // It is reference evapotranspiration (mm)
			Double rs; // It is the balance of daily radiation (MJM-²day-¹)
			Double lambda; // is the psychrometric coefficient (kPaºC)
			Double t; // It is the average air temperature (ºC)
			Double maxT; // It is the max air temperature (ºC)
			Double minT; // It is the min air temperature (ºC)
			Double u2; // It is the wind speed at 2m height (ms-¹)
			Double z; // It is the place altitude (m)
			Double patm; // It is atmospheric pressure (kPa)
			Double ru; // It is relative humidity (%)
			Double lat; // It is latitude (decimal coordinates)
			/** Variable Assignments - Variables that is the same for whole Data Set **/
			// altitude
			z = Double.parseDouble(String.valueOf(altitudeList.get(0)));
			// latitude
			lat = Double.parseDouble(String.valueOf(latitudeList.get(0)));
			// etopmf
			etopmf = 0.0D;
			/** Critic on variables values **/
			Boolean isLineWithNullValues = false;
			if (z == null) {
				isLineWithNullValues = true;
			}
			if (lat == null) {
				isLineWithNullValues = true;
			}
			if (!isLineWithNullValues) {
				/** Constants Calculation - Variables that changes everyday **/
				// patm
				patm = (Double) (101.3 * Math.pow(((293 - 0.0065 * z) / (293)), 5.26));
				// lambda
				lambda = (Double) (0.665 * Math.pow(10, -3) * patm);
				// for each line
				for (int i = 0; i < dayList.size(); i++) {
					/** Variable Assignments **/
					// temperature
					t = Double.parseDouble(String.valueOf(tempatureList.get(i)));
					// max temperature
					maxT = Double.parseDouble(String.valueOf(maxTempatureList.get(i)));
					// temperature
					minT = Double.parseDouble(String.valueOf(minTempatureList.get(i)));
					// relative humidity
					ru = Double.parseDouble(String.valueOf(relativeHumidityList.get(i)));
					// wind speed
					u2 = Double.parseDouble(String.valueOf(windSpeedList.get(i)));
					// rn
					rs = Double.parseDouble(String.valueOf(radiationList.get(i)));
					// day
					day = anyStringToDate(String.valueOf(dayList.get(i)));
					/** Critic on variables values **/
					if (t == null) {
						isLineWithNullValues = true;
					}
					if (maxT == null) {
						isLineWithNullValues = true;
					}
					if (minT == null) {
						isLineWithNullValues = true;
					}
					if (maxT < minT) {
						isLineWithNullValues = true;
					}
					if (ru == null) {
						isLineWithNullValues = true;
					}
					if (u2 == null) {
						isLineWithNullValues = true;
					}
					if (day == null) {
						isLineWithNullValues = true;
					}
					if (!isLineWithNullValues) {
						// etopmf = dailyEtoCalculation(day, lat, z, t, maxT, minT, ru, u2, rs, lambda);
						if (i == 0) { // first iteration
							etopmf = dailyEtoCalculation(day, lat, z, t, maxT, minT, ru, u2, rs, lambda);
							if (dayList.size() == 1) { // if first iteration is also
														// the last
								// inserting etopmf
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
								String dateStr = sdf.format(anyStringToDate(String.valueOf(dayList.get(i))));
								AnalysisLogger.getLogger().info("Inserting into table "
										+ "insert into "
										+ outputtable
										+ " (day, etopmf)  values (to_date('"
										+ dateStr
										+ "', 'yyyy/MM'),"
										+ etopmf
										+ ")");
								DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (day,etopmf) values (to_date('" + dateStr + "', 'yyyy/MM')," + etopmf + ")", dbconnection);
								etopmf = new Double(0);
							}
						}
						if (i > 0) { // other iterations
							Date currentDate = anyStringToDate(String.valueOf(dayList.get(i)));
							Date lastDate = anyStringToDate(String.valueOf(dayList.get(i - 1)));
							Calendar currentDateCal = Calendar.getInstance();
							currentDateCal.setTime(currentDate);
							int monthCurrentDate = currentDateCal.get(Calendar.MONTH);
							int yearCurrentDate = currentDateCal.get(Calendar.YEAR);
							Calendar lastDateCal = Calendar.getInstance();
							lastDateCal.setTime(lastDate);
							int monthLastDate = lastDateCal.get(Calendar.MONTH);
							int yearLastDate = lastDateCal.get(Calendar.YEAR);
							if (monthCurrentDate > monthLastDate || yearCurrentDate > yearLastDate) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
								String dateStr = sdf.format(lastDate);
								AnalysisLogger.getLogger().info("Inserting into table "
										+ "insert into "
										+ outputtable
										+ " (day, etopmf)  values (to_date('"
										+ dateStr
										+ "', 'yyyy/MM'),"
										+ etopmf
										+ ")");
								DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (day,etopmf) values (to_date('" + dateStr + "', 'yyyy/MM')," + etopmf + ")", dbconnection);
								etopmf = new Double(0);
								etopmf = dailyEtoCalculation(day, lat, z, t, maxT, minT, ru, u2, rs, lambda);
							} else {
								etopmf = etopmf + dailyEtoCalculation(day, lat, z, t, maxT, minT, ru, u2, rs, lambda);
							}
						}
						if (i != 0 && i == dayList.size() - 1) { // last iteration
							// inserting etopmf
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
							String dateStr = sdf.format(anyStringToDate(String.valueOf(dayList.get(i))));
							AnalysisLogger.getLogger().info("Inserting into table " + "insert into " + outputtable + " (day, etopmf)  values (to_date('" + dateStr + "', 'yyyy/MM')," + etopmf + ")");
							DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (day,etopmf) values (to_date('" + dateStr + "', 'yyyy/MM')," + etopmf + ")", dbconnection);
						}
					}
					/** Set output **/
					// inserting etopmf
					// SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
					// String dateStr = sdf.format(anyStringToDate(String.valueOf(dayList.get(i))));
					// AnalysisLogger.getLogger().info("Inserting into table " + "insert into " + outputtable + " (day, etopmf)  values (to_date('" + dateStr + "', 'dd/mm/yyyy')," + etopmf + ")");
					// DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (day,etopmf) values (to_date('" + dateStr + "', 'dd/mm/yyyy')," + etopmf + ")", dbconnection);
					// Set null to all internal variables
					t = null;
					ru = null;
					rs = null;
					isLineWithNullValues = false;
				}
			} else {
				throw new Exception("No altitude or latitude value defined for this tabular resource.");
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().error(e.getMessage());
			throw e;
		} finally {
			DatabaseUtils.closeDBConnection(dbconnection);
		}
	}

	@Override
	protected void setInputParameters() {
		// First parameter: Internal tabular resource
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, "MetTable", "Meteorological data tabular resource");
		ColumnTypesList columns =
				new ColumnTypesList("MetTable", "MetColumns", "Selected columns for date, latitude, altitude, temperature, max temperature, min temperature relative humidity, wind speed, and radiation", false);
		inputs.add(tinput);
		inputs.add(columns);
		// Second parameter: Output table
		ServiceType randomstring = new ServiceType(ServiceParameters.RANDOMSTRING, "OutputTable", "", "met");
		inputs.add(randomstring);
		DatabaseType.addDefaultDBPars(inputs);
		// Third parameter: Output table name
		addStringInput("OutputTableName", "The name of the output table", "met_");
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> outtemplate = new ArrayList<TableTemplates>();
		outtemplate.add(TableTemplates.GENERIC);
		OutputTable out = new OutputTable(outtemplate, outputtablename, outputtable, "The output table containing all the matches");
		return out;
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
	}

	/**
	 * An auxiliary method that converts string to date.
	 * 
	 * @param value
	 * @return
	 */
	public static Date anyStringToDate(String value) {
		Date date = null;
		try {
			if (isValidFormat("yyyy-MM-dd", value)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = (Date) sdf.parse(value);
			}
			if (isValidFormat("yyyyMMdd", value)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				date = (Date) sdf.parse(value);
			}
			if (isValidFormat("dd/MM/yyyy", value)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				date = (Date) sdf.parse(value);
			}
			if (isValidFormat("dd-MM-yyyy", value)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				date = (Date) sdf.parse(value);
			}
		} catch (Exception e) {
			date = null;
		}
		return date;
	}

	/**
	 * An auxiliary method that validates if a string respect a specified date format.
	 * 
	 * @param format
	 * @param value
	 * @return
	 */
	public static boolean isValidFormat(String format, String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			date = null;
		}
		return date != null;
	}

	/**
	 * An auxiliary method that return the ETo estimation of the day by Penman-Monteith-FAO method.
	 * 
	 * @param day
	 * @param lat
	 * @param z
	 * @param t
	 * @param maxT
	 * @param minT
	 * @param ru
	 * @param u2
	 * @param rn
	 * @param lambda
	 * @return
	 */
	public static Double dailyEtoCalculation(Date day, Double lat, Double z, Double t, Double maxT, Double minT, Double ru, Double u2, Double rs, Double lambda) {
		Double dailyetopmf = new Double(0);
		/** Calculation **/
		// g - It is the total daily flux of heat in the soil (MJM-²day-¹)
		Double g = 0D;
		// delta - It is the slope of the vapor pressure curve in relation to temperature (kPaºC)
		Double delta = (Double) ((4098 * (0.6018 * Math.exp((17.27 * t) / (t + 273.3)))) / Math.pow((t + 237.2), 2));
		// es - It is the saturated steam pressure (kPa)
		Double es = (Double) (0.6108 * Math.exp((17.27 * t) / (t + 237.3)));
		// ea - It is the current steam pressure (kPa)
		Double ea = (Double) ((es * ru) / (100));
		// Net radiation
		Double rn = 0.0D;
		// Estimation of net radiation
		if (rs == null || rs == 0) { // if net radiation is null, the algorithm put an estimate value in this field
			Double rns;
			Double rnl;
			Double krs = 0.16D;
			Double rso;
			Double ra;
			Double dr;
			Double phi;
			Double omega_s;
			Double sigma;
			Double delta_lowercase;
			Integer j;
			Double x;
			/** Calculation of rns **/
			// j
			Calendar cal = null;
			cal = Calendar.getInstance();
			cal.setTime(day);
			j = cal.get(GregorianCalendar.DAY_OF_YEAR);
			// phi
			phi = (Double) ((lat * Math.PI) / 180);
			// delta lowercase
			delta_lowercase = (Double) (0.409 * Math.sin((2 * Math.PI) / (365) * j - 1.39));
			// x
			x = (Double) (1 - Math.pow(Math.tan(phi), 2) * Math.pow(Math.tan(delta_lowercase), 2));
			if (x <= 0) {
				x = 0.00001D;
			}
			// omega s
			omega_s = (Double) ((Math.PI / 2) - Math.atan((-Math.tan(phi) * Math.tan(delta_lowercase)) / (Math.pow(x, 0.5))));
			// dr
			dr = (Double) (1 + 0.033 * Math.cos((2 * Math.PI) / (365) * j));
			// ra
			ra = (Double) (118.08 / Math.PI * dr * (omega_s * Math.sin(phi) * Math.sin(delta_lowercase) + Math.cos(phi) * Math.cos(delta_lowercase) * Math.sin(omega_s)));
			// rs
			rs = (Double) (krs * ra * Math.sqrt(maxT - minT));
			// rns
			rns = (Double) (0.77 * rs);
			/** Calculation of rnl **/
			// rso
			rso = (Double) ((0.75 + 2 * Math.pow(10, -5) * z) * ra);
			// sigma
			sigma = (Double) (4.903 * Math.pow(10, -9));
			// rnl
			rnl = (Double) (sigma * (((Math.pow((maxT + 273.16), 4)) + Math.pow((minT + 273.16), 4)) / 2) * (0.34 - 0.14 * Math.sqrt(ea)) * (1.35 * (rs / rso) - 0.35));
			/** Calculation of rn **/
			// rn
			rn = rns - rnl;
		} else {
			Double rns;
			Double rnl;
			Double krs = 0.16D;
			Double rso;
			Double ra;
			Double dr;
			Double phi;
			Double omega_s;
			Double sigma;
			Double delta_lowercase;
			Integer j;
			Double x;
			/** Calculation of rns **/
			// j
			Calendar cal = null;
			cal = Calendar.getInstance();
			cal.setTime(day);
			j = cal.get(GregorianCalendar.DAY_OF_YEAR);
			// phi
			phi = (Double) ((lat * Math.PI) / 180);
			// delta lowercase
			delta_lowercase = (Double) (0.409 * Math.sin((2 * Math.PI) / (365) * j - 1.39));
			// x
			x = (Double) (1 - Math.pow(Math.tan(phi), 2) * Math.pow(Math.tan(delta_lowercase), 2));
			if (x <= 0) {
				x = 0.00001D;
			}
			// omega s
			omega_s = (Double) ((Math.PI / 2) - Math.atan((-Math.tan(phi) * Math.tan(delta_lowercase)) / (Math.pow(x, 0.5))));
			// dr
			dr = (Double) (1 + 0.033 * Math.cos((2 * Math.PI) / (365) * j));
			// ra
			ra = (Double) (118.08 / Math.PI * dr * (omega_s * Math.sin(phi) * Math.sin(delta_lowercase) + Math.cos(phi) * Math.cos(delta_lowercase) * Math.sin(omega_s)));
			// rns
			rns = (Double) (0.77 * rs);
			/** Calculation of rnl **/
			// rso
			rso = (Double) ((0.75 + 2 * Math.pow(10, -5) * z) * ra);
			// sigma
			sigma = (Double) (4.903 * Math.pow(10, -9));
			// rnl
			rnl = (Double) (sigma * (((Math.pow((maxT + 273.16), 4)) + Math.pow((minT + 273.16), 4)) / 2) * (0.34 - 0.14 * Math.sqrt(ea)) * (1.35 * (rs / rso) - 0.35));
			/** Calculation of rn **/
			// rn
			rn = rns - rnl;
		}
		// main formula
		dailyetopmf = (Double) ((0.408 * delta * (rn - g) + (lambda * 900 * u2 * (es - ea)) / (t + 273)) / (delta + lambda * (1 + 0.34 * u2)));
		if (dailyetopmf == null || Double.isNaN(dailyetopmf)) {
			dailyetopmf = 0.0D;
		}
		return dailyetopmf;
	}
}
