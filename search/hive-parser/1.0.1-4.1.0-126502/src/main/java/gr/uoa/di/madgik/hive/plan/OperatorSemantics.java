package gr.uoa.di.madgik.hive.plan;

import gr.uoa.di.madgik.hive.representation.TableDesc;

import java.util.HashMap;

public abstract class OperatorSemantics {

	public static String getInputType(String source) throws Exception {
		if (source.startsWith("file:/"))
			return "Path";
		else if (source.startsWith("ftp://"))
			return "FTP";
		else if (source.startsWith("jdbc:"))
			return "JDBC";
		else if (source.startsWith("tm://"))
			return "TM";
		else
			throw new Exception("Unkown input type for source: " + source);
	}

	public static String getOutputType(String sink) throws Exception {
		if (sink.startsWith("file://"))
			return "Path";
		else if (sink.startsWith("ftp://"))
			return "FTP";
		else if (sink.startsWith("jdbc:"))
			return "JDBC";
		else
			throw new Exception("Unkown input type for source: " + sink);
	}

	public static HashMap<String, String> createSourceOpearatorArgs(String inputType, TableDesc desc) {
		HashMap<String, String> args = new HashMap<String, String>();

		args.put("delimiter", desc.getDelimiter());

		if (inputType.equals("Path")) {
		} else if (inputType.equals("FTP")) {
		} else if (inputType.equals("JDBC")) {
		} else if (inputType.equals("TM")) {
			args.put("GCubeActionScope", desc.getSource().substring(desc.getSource().indexOf("/", 5)));
		} else {

		}

		return args;
	}
}
