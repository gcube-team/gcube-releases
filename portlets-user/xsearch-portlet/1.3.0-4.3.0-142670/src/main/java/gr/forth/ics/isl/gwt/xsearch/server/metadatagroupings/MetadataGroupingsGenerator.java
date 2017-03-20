/* 
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
package gr.forth.ics.isl.gwt.xsearch.server.metadatagroupings;


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A Class that implements the functionality of meta-data Groupings.
 * It contains a structure that constructs and stores the metadataGroups.
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class MetadataGroupingsGenerator {

    ArrayList<MetadataGroup> metadataGroups;

    public MetadataGroupingsGenerator() {
        this.metadataGroups = new ArrayList<MetadataGroup>();
    }

    /**
     * Add a new metatada to metadataGroups
     * @param newMetadataGroupName metadata group name
     * @param EntityName the entity name of that metadata
     * @param docId the doc Id is included the metadata
     */
    public void addMetadata(String newMetadataGroupName, String EntityName, int docId) {
        // Create new Entity 
        Metadata entity = new Metadata(EntityName);
        entity.addDocId(docId);

        addCategory(metadataGroups, entity, newMetadataGroupName);
    }

    /**
     * Add a new metadataGroup
     * @param allMetadataGroups an arrayList which contains all the metadata Groups
     * @param metadata metadata want to add
     * @param metadataGroupName metadataGroup name
     */
    public void addCategory(ArrayList<MetadataGroup> allMetadataGroups, Metadata metadata, String metadataGroupName) {
        ArrayList<Metadata> entitiesList2 = new ArrayList<>();

        for (int k = 0; k < allMetadataGroups.size(); k++) {
            if (allMetadataGroups.get(k).getMetadataGroupName().equals(metadataGroupName)) {
                //if type has been found
                for (int i = 0; i < allMetadataGroups.get(k).getMetadatas().size(); i++) {
                    if (allMetadataGroups.get(k).getMetadatas().get(i).getMetadataName().toLowerCase().trim().equals(metadata.getMetadataName().toLowerCase().trim())) {
                        Integer docId = metadata.getDocIds().get(metadata.getDocIds().size() - 1);

                        //if a word was found, check if the docId has been added
                        if (!allMetadataGroups.get(k).getMetadatas().get(i).getDocIds().contains(docId)) {
                            allMetadataGroups.get(k).getMetadatas().get(i).addDocId(docId);
                        }
                        return;
                    }
                }

                //the word does not belong, so add it
                allMetadataGroups.get(k).getMetadatas().add(metadata);
                return;
            }

        }

        // create the metadata and the element
        entitiesList2.add(metadata);
        allMetadataGroups.add(new MetadataGroup(metadataGroupName, entitiesList2));

    }

    /**
     * 
     * @return all metadataGroups
     */
    public ArrayList<MetadataGroup> getCategories() {
        return metadataGroups;
    }

    /**
     * Get's the results of entity mining and creates a representation of them in Json.
     *
     * @param query the submitted query
     * @param resultsStartOffset the offset of the first result in order to update the references to correspond at the proper documents
     * @return the JSON Object
     */
    public String createMetadataGroupingsJSONString(String query, int resultsStartOffset) {

        JSONArray jsonCategories = new JSONArray();

        for (int i = 0; i < metadataGroups.size(); i++) {
            JSONArray jsonEntitiesArray = new JSONArray();
            JSONObject jsonCategory = new JSONObject();

            for (int j = 0; j < metadataGroups.get(i).getMetadatas().size(); j++) {
                /*
                 * System.out.println("\tEntity: " + metadataGroups.get(i).getEntities().get(j).getName() + " - " + metadataGroups.get(i).getEntities().get(j).getDocIds());
                 */

                // Passes Entity's name to json format
                JSONObject jsonEntity = new JSONObject();
                jsonEntity.put("MetadataName", metadataGroups.get(i).getMetadatas().get(j).getMetadataName());

                // Passes Entity's doclist to json format;
                JSONArray jsonDocArray = new JSONArray();
                for (int k = 0; k < metadataGroups.get(i).getMetadatas().get(j).getDocIds().size(); k++) {
                    jsonDocArray.add(metadataGroups.get(i).getMetadatas().get(j).getDocIds().get(k) + resultsStartOffset);
                }
                jsonEntity.put("DocList", jsonDocArray);

                // Passes Entity's rank value to json format;
                jsonEntity.put("Rank", metadataGroups.get(i).getMetadatas().get(j).getRank());

                // Adds jsonEntity object to Entities Json Object
                jsonEntitiesArray.add(jsonEntity);
            }

            // Passes Category's entities
            jsonCategory.put("Metadatas", jsonEntitiesArray);

            // Passes Category's name to json format
            jsonCategory.put("MetadataGroupName", metadataGroups.get(i).getMetadataGroupName());

            // Passes Category's num of Different Documents to json format
            jsonCategory.put("NumOfDiffDocs", metadataGroups.get(i).getNum_of_different_docs());

            // Passes Category's rank value to json Format
            jsonCategory.put("Rank", metadataGroups.get(i).getRank());

            // Passes Category to metadataGroups array at json Format
            jsonCategories.add(jsonCategory);
        }

        JSONObject MinedCategories = new JSONObject();
        MinedCategories.put("MetadataGroups", jsonCategories);
        MinedCategories.put("Query", query);
        StringWriter out = new StringWriter();
        try {
            MinedCategories.writeJSONString(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jsonText = out.toString();
        //System.out.print("Mining JSON string: \n" + jsonText);

        return ("{ \"MetadataGroupingResults\":" + jsonText + "}");
    }
}
