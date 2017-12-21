/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */

package gr.forth.ics.isl.xsearch;

import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class IOSLog {

    public static void writeToLog(String text) {

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(Resources.LOG, true));
            bw.write(text);
            bw.flush();
        } catch (IOException ioe) {
            System.out.println("Error updating query log: " + ioe.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe2) {
                }
            }
        }
    }

    public static void writeErrorToLog(Exception e, String inclass) {

        BufferedWriter bw = null;
        
        try {
            bw = new BufferedWriter(new FileWriter(Resources.LOG, true));
            bw.write("\n" + Resources.SYSTEMNAME + "\t" + IOSLog.getCurrentDate() + "\tEXCEPTION (in "+inclass+"): " + e.getMessage());
            bw.flush();
        } catch (IOException ioe) {
            System.out.println("Error updating query log: " + ioe.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe2) {
                }
            }
        }

    }
    
    public static void writeErrorToLog(Exception e, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        BufferedWriter bw = null;
        
        try {
            bw = new BufferedWriter(new FileWriter(Resources.LOG, true));
            bw.write("\n" + Resources.SYSTEMNAME + "\t" + IOSLog.getCurrentDate() + "\t" + ip + "\tEXCEPTION: " + e.getMessage().replaceAll("\n", ""));
            bw.flush();
        } catch (IOException ioe) {
            System.out.println("Error updating query log: " + ioe.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe2) {
                }
            }
        }

    }
    
    
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateText = dateFormat.format(date);
        return dateText;
    }
}
