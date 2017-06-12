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

package gr.forth.ics.isl.xsearch.retriever;

import gr.forth.ics.isl.xsearch.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class GoogleRetriever implements ResultsRetriever {

    @Override
    public void retrieveResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList<SearchResult> getResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setQuery(String query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getQuery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDesiredNumber(int desiredNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDesiredNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParameters(HashMap<String, String> parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HashMap<String, String> getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
