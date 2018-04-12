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
package gr.forth.ics.isl.gwt.xsearch.server.gcubeSearch;

import gr.forth.ics.isl.xsearch.configuration.Resources;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * Class that contains the functions of GCubeSearch that are used from XSearch.
 *
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class GCubeSearch {
    private Logger logger=Resources.initializeLogger(this.getClass().getName());

    public GCubeSearch() {
    }

    /**
     * Returns an ASLSession.
     *
     * @return an ASLSession
     */
    public ASLSession getASLSession(HttpServletRequest httpReq) {
        String sessionID = httpReq.getSession().getId();
        String user = (String) httpReq.getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

        if (user == null) {
            user = "test.user";
        }

        ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
        return session;
    }

    /**
     * Check whether the porlet has been loaded after a search or not
     *
     * @return true if the user performed a search, false otherwise
     */
    public boolean isSearchActive(ASLSession session) {
        return (session.getAttribute(SessionConstants.activePresentationQueryNo) != null);
    }

    /**
     * Returns the results from Consumer.
     *
     * @param aslSession the asl session
     * @param resConsumer the results Consumer
     * @return
     */
    public ArrayList<String> getResultsFromConsumer(int numOfResConsume, int startOffset, ASLSession aslSession) {

        ArrayList<String> results = new ArrayList<>();

//        if (resConsumer != null) {
//            // first param sets the num of Results we want to Retrive
//            // Second param sets from which to start, eg. [0, 14]
//            results = (ArrayList<String>) resConsumer.getResultsToText(numOfResConsume, startOffset, aslSession);
//
//        }

        return results;
    }

    /**
     * Initializes a RecordWriter.
     *
     * @param defs RecordDefinition[] in order to init RecordWriter.
     * @return an instance of RecordWriter<GenericRecord>
     */
    public RecordWriter<GenericRecord> initRecordWriter(RecordDefinition[] defs) {
        RecordWriter<GenericRecord> writer = null;

        try{
            writer = new RecordWriter<>(new LocalWriterProxy(),
                    defs, RecordWriter.DefaultBufferCapacity,
                    RecordWriter.DefaultConcurrentPartialCapacity,
                    RecordWriter.DefaultMirrorBufferFactor, 1, TimeUnit.DAYS);
        }catch(GRS2WriterException ex){
            this.logger.error("Failed to initialize RecordWriter<GenericRecord>\n"+ex.toString());
        }

        return writer;
    }

    /**
     * Creates a RecordDefinition table with two fields, the snippet and the title
     *
     * @return a new instance of RecordDefinition[] with snippet and title fields.
     */
    public RecordDefinition[] createRecordDefinitionTable() {

        // A record can contain a number of different field definitions
        RecordDefinition[] defs = new RecordDefinition[]{
            new GenericRecordDefinition((new FieldDefinition[]{
                new StringFieldDefinition("title"),
                new StringFieldDefinition("snippet"),}))};

        return defs;
    }
}
