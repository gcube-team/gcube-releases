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
package gr.forth.ics.isl.gwt.xsearch.server;

import gr.forth.ics.isl.xsearch.configuration.Resources;
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;

/**
 * Class for analyzing text with HTML/XML contents
 * 
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class HTMLTag {

    private String sourceCode = "";
    private URL url;
    private URLConnection con;
    private BufferedReader in;
    private boolean error = false;
    private static Logger logger=Resources.initializeLogger(HTMLTag.class.getName());

    /**
     * 
     * @param theUrl The URL of the page that you want to analyze
     */
    public HTMLTag(URL theUrl) {

        url = theUrl;

        try {
            con = url.openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            con.setConnectTimeout(90000);
            con.setReadTimeout(90000);
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));


            if (con.getContentType().toLowerCase().contains("text/html")) {

                String input;

                if (in != null) {
                    while ((input = in.readLine()) != null) {
                        sourceCode = sourceCode + input + "\n";
                    }
                    in.close();

                } else {
                    sourceCode = "";
                }

            } else {
                sourceCode = "";
            }
        } catch (IOException ex) {
            logger.error("*** ERROR READING URL: " + ex.getMessage());
            sourceCode = "";
            error = true;
        }

    }

    /**
     * 
     * @param theUrl The URL of the page that you want to analyze
     * @param xmlContent True if the URL of the page that you want to analyze is an XML page
     */
    public HTMLTag(URL theUrl, boolean xmlContent) {

        url = theUrl;

        try {
            con = url.openConnection();

            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            con.setConnectTimeout(90000);
            con.setReadTimeout(90000);
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));


            String input;

            if (in != null) {
                while ((input = in.readLine()) != null) {
                    sourceCode = sourceCode + input + "\n";
                }
                in.close();

            } else {
                sourceCode = "";
            }
        } catch (IOException ex) {
            logger.error("*** ERROR READING URL: " + ex.getMessage());
            error = true;
        }

    }

    /**
     * 
     * @param theSourceCode A string that you want to analyze.
     */
    public HTMLTag(String theSourceCode) {
        if (theSourceCode == null) {
            logger.warn("Attention! Null String for HTMLTag object!");
            sourceCode = "";
        } else {
            sourceCode = theSourceCode;
        }
    }

    /**
     * Finds and returns the name of the first tag (e.g. "div")
     * 
     * @return The name of the first tag (e.g. "div" or "span" or ...) 
     */
    public String getFirstTag() {
        int i1 = sourceCode.indexOf("<", 0);
        if (i1 == -1) {
            return null;
        }

        int i2 = sourceCode.indexOf(">", i1);
        if (i2 == -1) {
            return null;
        }

        int i3 = sourceCode.indexOf(" ", i1);
        if (i3 == -1) {
            return sourceCode.substring(i1 + 1, i2);
        }

        if (i2 < i3) {
            return sourceCode.substring(i1 + 1, i2);
        } else {
            return sourceCode.substring(i1 + 1, i3);
        }
    }

    /**
     * Finds and returns the name of the first tag (e.g. "div")
     * 
     * @param fromIndex Start searching from that position (value must be between 0 and contents' total length)
     * @return The name of the first tag (e.g. "div" or "span" or ...) 
     */
    public String getFirstTag(int fromIndex) {
        int i1 = sourceCode.indexOf("<", fromIndex);
        if (i1 == -1) {
            return null;
        }

        int i2 = sourceCode.indexOf(">", i1);
        if (i2 == -1) {
            return null;
        }

        int i3 = sourceCode.indexOf(" ", i1);
        if (i3 == -1) {
            return sourceCode.substring(i1 + 1, i2);
        }

        if (i2 < i3) {
            return sourceCode.substring(i1 + 1, i2);
        } else {
            return sourceCode.substring(i1 + 1, i3);
        }
    }

    /**
     * Finds and returns the position of the first occurrence of a tag
     *  
     * @param theTag  The name of the tag (e.g. "div") 
     * @return The position of the first occurrence of tag
     */
    public int getFirstTagIndex(String theTag) {

        int i1, i2, i3;
        i1 = sourceCode.indexOf("<");
        while (i1 != -1) {
            i2 = sourceCode.indexOf(">", i1);
            if (i2 == -1) {
                return -1;
            } else {
                i3 = sourceCode.indexOf(" ", i1);
                if (i3 == -1) {
                    if (sourceCode.substring(i1 + 1, i2).toUpperCase().compareTo(theTag.toUpperCase()) == 0) {
                        return i1;
                    } else {
                        i1 = sourceCode.indexOf("<", i1 + 1);
                    }
                } else {
                    if (i2 < i3) {
                        if (sourceCode.substring(i1 + 1, i2).toUpperCase().compareTo(theTag.toUpperCase()) == 0) {
                            return i1;
                        } else {
                            i1 = sourceCode.indexOf("<", i1 + 1);
                        }
                    } else {
                        if (sourceCode.substring(i1 + 1, i3).toUpperCase().compareTo(theTag.toUpperCase()) == 0) {
                            return i1;
                        } else {
                            i1 = sourceCode.indexOf("<", i1 + 1);
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Finds and returns the position of the first occurrence of a tag
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The position of the first occurrence of tag
     */
    public int getFirstTagIndex(String theTag, int fromIndex) {

        int i1, i2, i3;

        i1 = sourceCode.indexOf("<", fromIndex);
        while (i1 != -1) {
            i2 = sourceCode.indexOf(">", i1);
            if (i2 == -1) {
                return -1;
            } else {
                i3 = sourceCode.indexOf(" ", i1);
                if (i3 == -1) {
                    if (sourceCode.substring(i1 + 1, i2).toUpperCase().compareTo(theTag.toUpperCase()) == 0) {
                        return i1;
                    } else {
                        i1 = sourceCode.indexOf("<", i1 + 1);
                    }
                } else {
                    if (i2 < i3) {
                        if (sourceCode.substring(i1 + 1, i2).toUpperCase().compareTo(theTag.toUpperCase()) == 0) {
                            return i1;
                        } else {
                            i1 = sourceCode.indexOf("<", i1 + 1);
                        }
                    } else {
                        if (sourceCode.substring(i1 + 1, i3).toUpperCase().compareTo(theTag.toUpperCase()) == 0) {
                            return i1;
                        } else {
                            i1 = sourceCode.indexOf("<", i1 + 1);
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Finds and returns the position of the first occurrence of a tag that contains a particular text in its attributes' text
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag to contain in its attributes' text
     * @return The position of the first occurrence of the tag 
     */
    public int getFirstTagIndexContains(String theTag, String containment) {
        int index = getFirstTagIndex(theTag);
        String tagContent = getFirstTagContent(theTag, index);
        while (index != -1) {
            if (tagContent == null) {
                index = getFirstTagIndex(theTag, index + 1);
                tagContent = getFirstTagContent(theTag, index);
            } else {

                if (tagContent.toUpperCase().contains(containment.toUpperCase())) {
                    return index;
                }

                index = getFirstTagIndex(theTag, index + 1);
                tagContent = getFirstTagContent(theTag, index);
            }
        }

        return -1;
    }

    /**
     * Finds and returns the position of the first occurrence of a tag that contains a particular text in its attributes' text
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag to contain in its attributes' text
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The position of the first occurrence of the tag 
     */
    public int getFirstTagIndexContains(String theTag, String containment, int fromIndex) {

        int index = getFirstTagIndex(theTag, fromIndex);
        String tagContent = getFirstTagContent(theTag, index);
        while (index != -1) {
            if (tagContent == null) {
                index = getFirstTagIndex(theTag, index + 1);
                tagContent = getFirstTagContent(theTag, index);
            } else {

                if (tagContent.toUpperCase().contains(containment.toUpperCase())) {
                    return index;
                }

                index = getFirstTagIndex(theTag, index + 1);
                tagContent = getFirstTagContent(theTag, index);
            }
        }

        return -1;
    }

    /**
     * Finds and returns the position of the first occurrence of a tag that its attributes' text is equal to a particular text
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag's attributes text to be equal to
     * @return The position of the first occurrence of the tag 
     */
    public int getFirstTagIndexContentEquals(String theTag, String containment) {

        int index = getFirstTagIndex(theTag, 0);
        String tagContent = getFirstTagContent(theTag, index);
        while (index != -1) {
            if (tagContent == null) {
                return -1;
            }
            if (tagContent.toUpperCase().contentEquals(containment.toUpperCase())) {
                return index;
            }

            index = getFirstTagIndex(theTag, index + 1);
            tagContent = getFirstTagContent(theTag, index + 1);
        }

        return -1;
    }

    /**
     * Finds and returns the position of the first occurrence of a tag that its attributes' text is equal to a particular text
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag's attributes text to be equal to
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The position of the first occurrence of the tag 
     */
    public int getFirstTagIndexContentEquals(String theTag, String containment, int fromIndex) {

        int index = getFirstTagIndex(theTag, fromIndex);
        String tagContent = getFirstTagContent(theTag, index);
        while (index != -1) {
            if (tagContent == null) {
                return -1;
            }
            if (tagContent.toUpperCase().contentEquals(containment.toUpperCase())) {
                return index;
            }

            index = getFirstTagIndex(theTag, index + 1);
            tagContent = getFirstTagContent(theTag, index + 1);
        }

        return -1;
    }

    /**
     * Finds and returns the text that contains all the attributes of the first retrieved tag. 
     * For example, for the tag: <a target="_blank" title="hello" href="http://hello"> 
     * this functions returns: target="_blank" title="hello" href="http://hello"
     * 
     * @param theTag The name of the tag (e.g. "a")
     * @return The text that contains all the attributes of the first retrieved tag
     */
    public String getFirstTagContent(String theTag) {
        int i1 = getFirstTagIndex(theTag);

        if (i1 == -1) {
            return null;
        } else {
            int i2 = sourceCode.indexOf(">", i1);
            int i3 = sourceCode.indexOf(" ", i1);
            if (i3 == -1) {
                return null;
            } else if (i2 == -1) {
                return null;
            } else if (i2 < i3) {
                return "";
            } else {
                return sourceCode.substring(i3 + 1, i2);
            }
        }
    }

    /**
     * Finds and returns the text that contains all the attributes of the first retrieved tag. 
     * For example, for the tag: <a target="_blank" title="hello" href="http://hello"> 
     * this functions returns: target="_blank" title="hello" href="http://hello"
     * 
     * @param theTag The name of the tag (e.g. "a")
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The text that contains all the attributes of the first retrieved tag
     */
    public String getFirstTagContent(String theTag, int fromIndex) {
        int i1 = getFirstTagIndex(theTag, fromIndex);

        if (i1 == -1) {
            return null;
        } else {
            int i2 = sourceCode.indexOf(">", i1);
            int i3 = sourceCode.indexOf(" ", i1);
            if (i3 == -1) {
                return null;
            } else if (i2 == -1) {
                return null;
            } else if (i2 < i3) {
                return "";
            } else {
                return sourceCode.substring(i3 + 1, i2);
            }
        }
    }

    /**
     * Finds and returns the text that contains all the attributes of the first retrieved tag. 
     * For example, for the tag: <a target="_blank" title="hello" href="http://hello"> 
     * this functions returns: target="_blank" title="hello" href="http://hello"
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag to contain in its attributes' text
     * @return he text that contains all the attributes of the first retrieved tag
     */
    public String getFirstTagContentContains(String theTag, String containment) {

        int i1 = getFirstTagIndexContains(theTag, containment);
        if (i1 != -1) {
            return getFirstTagContent(theTag, i1);
        } else {
            return null;
        }
    }

    /**
     * Finds and returns the text that contains all the attributes of the first retrieved tag. 
     * For example, for the tag: <a target="_blank" title="hello" href="http://hello"> 
     * this functions returns: target="_blank" title="hello" href="http://hello"
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag to contain in its attributes' text
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return he text that contains all the attributes of the first retrieved tag
     */
    public String getFirstTagContentContains(String theTag, String containment, int fromIndex) {

        int i1 = getFirstTagIndexContains(theTag, containment, fromIndex);
        if (i1 != -1) {
            return getFirstTagContent(theTag, i1);
        } else {
            return null;
        }
    }

    /**
     * Finds and returns the text that contains all the inner data of the first retrieved tag. 
     * For example, for the tag: <div><span class="a">hello</span></div> 
     * this functions returns: <span class="a">hello</span>
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @return The text that contains all the inner data of the first retrieved tag
     */
    public String getFirstTagData(String theTag) {
        int i1 = getFirstTagIndex(theTag);

        if (i1 == -1) {
            return null;
        }

        int i2 = sourceCode.indexOf(">", i1);
        if (i2 == -1) {
            return null;
        }

        int i3 = getFirstTagIndex("/" + theTag, i2);
        if (i3 == -1) {
            return null;
        }

        int i4 = getFirstTagIndex(theTag, i2);
        if (i4 == -1) {
            return sourceCode.substring(i2 + 1, i3);
        }
        if (i3 < i4) {
            return sourceCode.substring(i2 + 1, i3);
        }

        while (i3 > i4) {
            i3 = getFirstTagIndex("/" + theTag, i3 + 2);
            i4 = getFirstTagIndex(theTag, i4 + 1);
            if (i3 == -1) {
                return null;
            }
            if (i4 == -1) {
                return sourceCode.substring(i2 + 1, i3);
            }
            if (i3 < i4) {
                return sourceCode.substring(i2 + 1, i3);
            }
        }
        return null;
    }

    /**
     * Finds and returns the text that contains all the inner data of the first retrieved tag. 
     * For example, for the tag: <div><span class="a">hello</span></div> 
     * this functions returns: <span class="a">hello</span>
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The text that contains all the inner data of the first retrieved tag
     */
    public String getFirstTagData(String theTag, int fromIndex) {
        int i1 = getFirstTagIndex(theTag, fromIndex);

        if (i1 == -1) {
            return null;
        }

        int i2 = sourceCode.indexOf(">", i1);
        if (i2 == -1) {
            return null;
        }

        int i3 = getFirstTagIndex("/" + theTag, i2);
        if (i3 == -1) {
            return null;
        }

        int i4 = getFirstTagIndex(theTag, i2);
        if (i4 == -1) {
            return sourceCode.substring(i2 + 1, i3);
        }
        if (i3 < i4) {
            return sourceCode.substring(i2 + 1, i3);
        }

        while (i3 > i4) {
            i3 = getFirstTagIndex("/" + theTag, i3 + 2);
            i4 = getFirstTagIndex(theTag, i4 + 1);
            if (i3 == -1) {
                return null;
            }
            if (i4 == -1) {
                return sourceCode.substring(i2 + 1, i3);
            }
            if (i3 < i4) {
                return sourceCode.substring(i2 + 1, i3);
            }
        }
        return null;
    }

    /**
     * Finds and returns the text that contains all the inner data of the first retrieved tag. 
     * For example, for the tag: <div><span class="a">hello</span></div> 
     * this functions returns: <span class="a">hello</span>
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag to contain in its attributes' text
     * @return The text that contains all the inner data of the first retrieved tag
     */
    public String getFirstTagDataContains(String theTag, String containment) {
        int i1 = getFirstTagIndexContains(theTag, containment);
        if (i1 != -1) {
            return getFirstTagData(theTag, i1);
        } else {
            return null;
        }

    }

    /**
     * Finds and returns the text that contains all the inner data of the first retrieved tag. 
     * For example, for the tag: <div><span class="a">hello</span></div> 
     * this functions returns: <span class="a">hello</span>
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag to contain in its attributes' text
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The text that contains all the inner data of the first retrieved tag
     */
    public String getFirstTagDataContains(String theTag, String containment, int fromIndex) {
        int i1 = getFirstTagIndexContains(theTag, containment, fromIndex);
        if (i1 != -1) {
            return getFirstTagData(theTag, i1);
        } else {
            return null;
        }

    }

    /**
     * Finds and returns the text that contains all the inner data of the first retrieved tag. 
     * For example, for the tag: <div><span class="a">hello</span></div> 
     * this functions returns: <span class="a">hello</span>
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag's attributes text to be equal to
     * @return The text that contains all the inner data of the first retrieved tag
     */
    public String getFirstTagDataContentEquals(String theTag, String containment) {

        int i1 = getFirstTagIndexContentEquals(theTag, containment);
        if (i1 != -1) {
            return getFirstTagContent(theTag, i1);
        } else {
            return null;
        }
    }

    /**
     * Finds and returns the text that contains all the inner data of the first retrieved tag. 
     * For example, for the tag: <div><span class="a">hello</span></div> 
     * this functions returns: <span class="a">hello</span>
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param containment The text that you want the tag's attributes text to be equal to
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The text that contains all the inner data of the first retrieved tag
     */
    public String getFirstTagDataContentEquals(String theTag, String containment, int fromIndex) {

        int i1 = getFirstTagIndexContentEquals(theTag, containment, fromIndex);
        if (i1 != -1) {
            return getFirstTagContent(theTag, i1);
        } else {
            return null;
        }
    }

    /**
     * Finds the number of occurrences of a tag.
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @return The number of occurrences of a tag
     */
    public int getNumOfTags(String theTag) {
        int fromIndex = 0;
        int toIndex = sourceCode.length();
        int i1 = getFirstTagIndex(theTag, fromIndex);
        if (i1 == -1) {
            return 0;
        }
        if (i1 > toIndex) {
            return 0;
        }

        int sum = 1;
        while (i1 <= toIndex) {
            i1 = getFirstTagIndex(theTag, i1 + 1);
            if (i1 == -1) {
                break;
            }
            if (i1 <= toIndex) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Finds the number of occurrences of a tag.
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The number of occurrences of a tag
     */
    public int getNumOfTags(String theTag, int fromIndex) {
        int i1 = getFirstTagIndex(theTag, fromIndex);
        int toIndex = sourceCode.length();
        if (i1 == -1) {
            return 0;
        }
        if (i1 > toIndex) {
            return 0;
        }

        int sum = 1;
        while (i1 <= toIndex) {
            i1 = getFirstTagIndex(theTag, i1 + 1);
            if (i1 == -1) {
                break;
            }
            if (i1 <= toIndex) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Finds the number of occurrences of a tag.
     * 
     * @param theTag The name of the tag (e.g. "div")
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @param toIndex End searching at that position (value must be between @start and content's total length)
     * @return The number of occurrences of a tag
     */
    public int getNumOfTags(String theTag, int fromIndex, int toIndex) {
        int i1 = getFirstTagIndex(theTag, fromIndex);
        if (i1 == -1) {
            return 0;
        }
        if (i1 > toIndex) {
            return 0;
        }

        int sum = 1;
        while (i1 <= toIndex) {
            i1 = getFirstTagIndex(theTag, i1 + 1);
            if (i1 == -1) {
                break;
            }
            if (i1 <= toIndex) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Finds and returns the text that contains all the inner data between two tags.
     * 
     * @param tag1 tag1 The name of the first tag (e.g. "div")
     * @param tag2 The name of the last tag (e.g. "div")
     * @return The text that contains all the inner data between two tags
     */
    public String getDataFromTagToTag(String tag1, String tag2) {
        int i1 = getFirstTagIndex(tag1);
        int i2 = getFirstTagIndex(tag2, i1);

        if (i1 == -1) {
            return null;
        }
        if (i2 == -1) {
            return null;
        }

        int i3 = sourceCode.indexOf(">", i1);
        return sourceCode.substring(i3 + 1, i2);
    }

    /**
     * Finds and returns the text that contains all the inner data between two tags.
     * 
     * @param tag1 The name of the first tag (e.g. "div")
     * @param tag2 The name of the last tag (e.g. "div")
     * @param fromIndex Start searching from that position (value must be between 0 and content's total length)
     * @return The text that contains all the inner data between two tags
     */
    public String getDataFromTagToTag(String tag1, String tag2, int fromIndex) {
        String fromTag = tag1;
        String toTag = tag2;

        int i1 = getFirstTagIndex(fromTag, fromIndex);
        int i2 = getFirstTagIndex(toTag, i1);

        if (i1 == -1) {
            return null;
        }
        if (i2 == -1) {
            return null;
        }

        return sourceCode.substring(i1, i2);
    }

    /**
     * Static function that removes all the tags (without their inner data) from a text.
     * For example for the text: <div class="a">hello <b>nick</b></div> 
     * this function will return: hello nick
     * 
     * @param text The text from which you want to remove all tags
     * @return The text without tags
     */
    public static String removeTags(String text) {
        if (text == null) {
            return null;
        }
        int i1 = text.indexOf("<");
        if (i1 == -1) {
            return text;
        }

        while (i1 != -1) {
            int i2 = text.indexOf(">", i1);
            if (i2 == -1) {
                return text;
            }

            String aux1 = text.substring(i1, i2 + 1);
            text = text.replace(aux1, "");
            i1 = text.indexOf("<");
        }

        return text;
    }

    /**
     * Static function that removes all occurrences of a particular tag (without its inner data) from a text.
     * For example, in case we want to remove all 'b' tags,  for the text: <div class="a">hello <b>nick</b>. How <b>are</b> you?</div> 
     * this function will return: <div class="a">hello nick</b>. How are</b> you?</div> 
     * And if we call again that function for the tag '/b', we will get: <div class="a">hello nick. How are you?</div>
     * 
     * @param tag The name of the tag (e.g. "div")
     * @param text The text from which you want to remove the particular tag
     * @return The text without all occurrences of a particular tag
     */
    public static String removeTagButNoText(String tag, String text) {
        if (text == null) {
            return null;
        }
        int i1 = text.toLowerCase().indexOf("<" + tag.toLowerCase());
        if (i1 == -1) {
            return text;
        }

        while (i1 != -1) {
            int i2 = text.indexOf(">", i1);
            if (i2 == -1) {
                return text;
            }

            String aux1 = text.substring(i1, i2 + 1);
            text = text.replace(aux1, "");
            i1 = text.indexOf("<" + tag.toLowerCase());
        }

        text = text.replace("</" + tag.toLowerCase() + ">", "");
        return text;
    }

    /**
     * Static function that removes all occurrences of a particular tag (and its inner data) from a text.
     * For example, in case we want to remove all 'b' tags,  for the text: <div class="a">hello <b>nick</b>. How <b>are</b> you?</div> 
     * this function will return: <div class="a">hello nick</b>. How are</b> you?</div> 
     * And if we call again that function for the tag '/b', we will get: <div class="a">hello nick. How are you?</div>
     * 
     * @param tag The name of the tag (e.g. "div")
     * @param text The text from which you want to remove the particular tag
     * @return The text without all occurrences of a particular tag
     */
    public static String removeTag(String tag, String text) {
        if (tag == null) {
            return null;
        }

        HTMLTag tagger = new HTMLTag(text);

        int pos = tagger.getFirstTagIndex(tag);
        while (pos != -1) {

            String data_to_remove = tagger.getFirstTagData(tag, pos - 1);
            if (data_to_remove == null) {
                data_to_remove = "";
            }
            text = text.replace(data_to_remove, "");
            tagger = new HTMLTag(text);

            pos = tagger.getFirstTagIndex(tag, pos + 2);
        }

        return text;
    }

    /**
     * Static function that removes all the tags (without their inner data) from a text, apart from "b" tags.
     * For example for the text: <div class="a"><i>hello</i> <b>nick</b></div> 
     * this function will return: hello <b>nick</b>
     * 
     * @param text The text from which you want to remove all tags apart from "b" tags
     * @return The text without all other tags
     */
    public static String removeTagsNoTagB(String text) {
        if (text == null) {
            return null;
        }

        int i1 = text.indexOf('<');
        if (i1 == -1) {
            return text;
        }

        while (i1 != -1) {

            int i2 = text.indexOf('>', i1);
            if (i2 == -1) {
                return text;
            }
            String aux1 = text.substring(i1, i2 + 1);

            if (aux1.toUpperCase().equals("<B>") || aux1.toUpperCase().equals("</B>") || aux1.toUpperCase().equals("<STRONG>") || aux1.toUpperCase().equals("</STRONG>")) {
                i1 = text.indexOf('<', i2);
            } else {
                text = text.replace(aux1, "");
                i1 = text.indexOf('<');
            }
        }

        return text;

    }

    /**
     * Static function that returns the content of the 'href' attribute from the text of an 'a' tag
     * 
     * @param tagA The text of the 'a' tag from which you want to get the 'href' attribute
     * @return The content of the 'href' attribute
     */
    public static String getHref(String tagA) {
        String href = "HREF=";

        int i1 = tagA.toUpperCase().indexOf(href);
        if (i1 == -1) {
            return null;
        }
        if (tagA.charAt(i1 + 5) == '\"') {
            int i2 = tagA.indexOf("\"", i1 + 6);
            if (i2 == -1) {
                return null;
            }
            return tagA.substring(i1 + 6, i2);
        } else {
            int i2 = tagA.indexOf(" ", i1 + 6);
            if (i2 == -1) {
                return null;
            }
            return tagA.substring(i1 + 5, i2);
        }
    }

    /**
     * Static function that returns the content of an attribute from the text of a tag
     * 
     * @param attr The attribute from which you want to get the content
     * @param tagContent The text of the tag from which you want to get the content of the attribute @attr
     * @return The content of the attribute
     */
    public static String getContentAttribute(String attr, String tagContent) {

        int i1 = tagContent.toLowerCase().indexOf(attr.toLowerCase());
        if (i1 == -1) {
            return null;
        }
        boolean other = false;
        int i2 = tagContent.indexOf("\"", i1 + 1);

        if (i2 == -1) {
            i2 = tagContent.indexOf("'", i1 + 1);
            other = true;
        }

        int i3;
        if (!other) {
            i3 = tagContent.indexOf("\"", i2 + 1);
        } else {
            i3 = tagContent.indexOf("'", i2 + 1);
        }

        if (i2 == -1 || i3 == -1) {
            return null;
        }

        return tagContent.substring(i2 + 1, i3);

    }

    /**
     * Static function that reads a file path and returns the contents of the file.
     * 
     * @param filepath The path of the file you want to read
     * @return The contents of the file
     */
    public static String readFile(String filepath) {
        String contents = "";

        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF8"))){
            String line;
            while ((line = in.readLine()) != null) {
                contents += line + "\n";
            }
            in.close();
        } catch (IOException ex) {
            logger.error("ERROR READING FILE '" + filepath + "'\n: " + ex.getMessage());
        }

        return contents;
    }

    /**
     * 
     * @return The URL of the current HTMLTag object
     */
    public URL getUrl() {
        return url;
    }

    /**
     * 
     * @return The contents of the current HTMLTag object
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * 
     * @return If an error has occurred to the current HTMLTag object
     */
    public boolean isError() {
        return error;
    }

    /**
     * 
     * @param error True if an error has occurred to the current HTMLTag object
     */
    public void setError(boolean error) {
        this.error = error;
    }
}
