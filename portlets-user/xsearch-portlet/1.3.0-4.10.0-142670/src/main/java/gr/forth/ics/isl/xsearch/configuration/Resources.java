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
package gr.forth.ics.isl.xsearch.configuration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 * @author Yannis Marketakis (yannismarketakis 'at' gmail 'dot' com)
 */
public class Resources {
    
    /**Creates a new logger (based on log4j) and associates it with a console appender.
     * 
     * @param className the name of the class initializing the logger
     * @return a proper logger for the given class*/
    public static Logger initializeLogger(String className){
        Logger log=Logger.getLogger(className);
        Layout layout=new PatternLayout("%d{ISO8601} %m");
        Appender appender=new ConsoleAppender(layout);
        log.addAppender(appender);
        log.setLevel(Level.ALL);
        return log;
    }
}
