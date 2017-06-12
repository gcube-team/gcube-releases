package it.eng.edison.usersurvey_portlet.server.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * The Class CSVUtils.
 */
public class CSVUtils {

    /** The Constant DEFAULT_SEPARATOR. */
    private static final char DEFAULT_SEPARATOR = ',';

    /**
     * Write line.
     *
     * @param w the w
     * @param values the values
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    /**
     * Write line.
     *
     * @param w the w
     * @param values the values
     * @param separators the separators
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    /**
     * Follow CV sformat.
     *
     * @param value the value
     * @return the string
     */
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    /**
     * Write line.
     *
     * @param w the w
     * @param values the values
     * @param separators the separators
     * @param customQuote the custom quote
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());


    }

}