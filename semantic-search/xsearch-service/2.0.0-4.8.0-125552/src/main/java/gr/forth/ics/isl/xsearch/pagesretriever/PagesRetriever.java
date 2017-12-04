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

package gr.forth.ics.isl.xsearch.pagesretriever;

import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.SearchResult;
import java.util.ArrayList;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class PagesRetriever {

    private ArrayList<SearchResult> wseResults;
    private int maxNumOfResultsFromWSE;

    public PagesRetriever(ArrayList<SearchResult> wseResults, int maxNumOfResultsFromWSE) {

        this.wseResults = wseResults;
        this.maxNumOfResultsFromWSE = maxNumOfResultsFromWSE;

        int num_of_results = wseResults.size();
        int results_per_thread = 5;

        retrieveContent(num_of_results, results_per_thread);
    }

    private void retrieveContent(int num_of_results, int results_per_thread) {
        if (maxNumOfResultsFromWSE == 100) {
            results_per_thread = 10;
        }

        if (maxNumOfResultsFromWSE == 200) {
            results_per_thread = 20;
        }

        if (maxNumOfResultsFromWSE == 300) {
            results_per_thread = 30;
        }

        if (maxNumOfResultsFromWSE == 400) {
            results_per_thread = 40;
        }

        if (maxNumOfResultsFromWSE == 500) {
            results_per_thread = 50;
        }

        ArrayList<SearchResult> sublist1 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist2 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist3 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist4 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist5 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist6 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist7 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist8 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist9 = new ArrayList<SearchResult>();
        ArrayList<SearchResult> sublist10 = new ArrayList<SearchResult>();

        if (num_of_results <= results_per_thread) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, num_of_results));
        } else if (num_of_results <= (results_per_thread * 2)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, num_of_results));
        } else if (num_of_results <= (results_per_thread * 3)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), num_of_results));
        } else if (num_of_results <= (results_per_thread * 4)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), num_of_results));
        } else if (num_of_results <= (results_per_thread * 5)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), (results_per_thread * 4)));
            sublist5 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 4), num_of_results));
        } else if (num_of_results <= (results_per_thread * 6)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), (results_per_thread * 4)));
            sublist5 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 4), (results_per_thread * 5)));
            sublist6 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 5), num_of_results));
        } else if (num_of_results <= (results_per_thread * 7)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), (results_per_thread * 4)));
            sublist5 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 4), (results_per_thread * 5)));
            sublist6 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 5), (results_per_thread * 6)));
            sublist7 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 6), num_of_results));
        } else if (num_of_results <= (results_per_thread * 8)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), (results_per_thread * 4)));
            sublist5 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 4), (results_per_thread * 5)));
            sublist6 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 5), (results_per_thread * 6)));
            sublist7 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 6), (results_per_thread * 7)));
            sublist8 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 7), num_of_results));
        } else if (num_of_results <= (results_per_thread * 9)) {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), (results_per_thread * 4)));
            sublist5 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 4), (results_per_thread * 5)));
            sublist6 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 5), (results_per_thread * 6)));
            sublist7 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 6), (results_per_thread * 7)));
            sublist8 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 7), (results_per_thread * 8)));
            sublist9 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 8), num_of_results));
        } else {
            sublist1 = new ArrayList<SearchResult>(wseResults.subList(0, results_per_thread));
            sublist2 = new ArrayList<SearchResult>(wseResults.subList(results_per_thread, (results_per_thread * 2)));
            sublist3 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 2), (results_per_thread * 3)));
            sublist4 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 3), (results_per_thread * 4)));
            sublist5 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 4), (results_per_thread * 5)));
            sublist6 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 5), (results_per_thread * 6)));
            sublist7 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 6), (results_per_thread * 7)));
            sublist8 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 7), (results_per_thread * 8)));
            sublist9 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 8), (results_per_thread * 9)));
            sublist10 = new ArrayList<SearchResult>(wseResults.subList((results_per_thread * 9), num_of_results));
        }

        GetPagesContent thread1 = new GetPagesContent(sublist1);
        thread1.start();

        GetPagesContent thread2 = new GetPagesContent(sublist2);
        thread2.start();

        GetPagesContent thread3 = new GetPagesContent(sublist3);
        thread3.start();

        GetPagesContent thread4 = new GetPagesContent(sublist4);
        thread4.start();

        GetPagesContent thread5 = new GetPagesContent(sublist5);
        thread5.start();

        GetPagesContent thread6 = new GetPagesContent(sublist6);
        thread6.start();

        GetPagesContent thread7 = new GetPagesContent(sublist7);
        thread7.start();

        GetPagesContent thread8 = new GetPagesContent(sublist8);
        thread8.start();

        GetPagesContent thread9 = new GetPagesContent(sublist9);
        thread9.start();

        GetPagesContent thread10 = new GetPagesContent(sublist10);
        thread10.start();

        boolean finish = false;
        while (finish == false) {
            if (thread1.finish && thread2.finish && thread3.finish && thread4.finish && thread5.finish && thread6.finish && thread7.finish && thread8.finish && thread9.finish && thread10.finish) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    IOSLog.writeErrorToLog(ex, "PagesRetriever");
                    System.out.println("*** ERROR WHILE TRYING TO SLEEP FOR ONE SECOND!");
                }
            }
        }

        wseResults = new ArrayList<SearchResult>(thread1.getPages());
        wseResults.addAll(thread2.getPages());
        wseResults.addAll(thread3.getPages());
        wseResults.addAll(thread4.getPages());
        wseResults.addAll(thread5.getPages());
        wseResults.addAll(thread6.getPages());
        wseResults.addAll(thread7.getPages());
        wseResults.addAll(thread8.getPages());
        wseResults.addAll(thread9.getPages());
        wseResults.addAll(thread10.getPages());

    }

    public int getMaxNumOfResultsFromWSE() {
        return maxNumOfResultsFromWSE;
    }

    public void setMaxNumOfResultsFromWSE(int maxNumOfResultsFromWSE) {
        this.maxNumOfResultsFromWSE = maxNumOfResultsFromWSE;
    }

    public ArrayList<SearchResult> getWseResults() {
        return wseResults;
    }

    public void setWseResults(ArrayList<SearchResult> wseResults) {
        this.wseResults = wseResults;
    }
    
    
}
