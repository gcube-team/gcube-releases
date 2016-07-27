/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.textextractor.entities;

import java.security.NoSuchAlgorithmException;
import org.gcube.semantic.annotator.utils.MD5Generator;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class ExtractedEntity {

    public final String en_name;
    public final String fr_name;
    public String uri_localName = "";

    public ExtractedEntity(String en_name_, String fr_name_) throws Exception {
        this.en_name = en_name_ != null ? normalize(en_name_.trim()) : "";
        this.fr_name = fr_name_ != null ? normalize(fr_name_.trim()) : "";
        if (this.isEmpty()) {
            throw new Exception("Both labels are empty!");
        }
        setLocalName();
    }

    @Override
    public String toString() {
        return "en name : " + this.en_name + ", fr name : " + this.fr_name;
    }

    @Override
    public boolean equals(Object o) {
        return ((ExtractedEntity) o).en_name.equals(this.en_name);
    }

    public boolean isEmpty() {
        return this.en_name.isEmpty()
                && this.fr_name.isEmpty();
    }

    private void setLocalName() throws NoSuchAlgorithmException {
        if (!this.en_name.isEmpty()) {
            this.uri_localName = MD5Generator.getMD5Sum(this.en_name);
        } else {
            this.uri_localName = MD5Generator.getMD5Sum(this.fr_name);
        }
    }

    private String normalize(String fr_name) {
//        String s = "È,É,Ê,Ë,Û,Ù,Ï,Î,À,Â,Ô,è,é,ê,ë,û,ù,ï,î,à,â,ô";
        
        fr_name = fr_name.replaceAll("[èéêë]", "e");
        fr_name = fr_name.replaceAll("[ûù]", "u");
        fr_name = fr_name.replaceAll("[ïî]", "i");
        fr_name = fr_name.replaceAll("[àâ]", "a");
        fr_name = fr_name.replaceAll("Ô", "o");
        fr_name = fr_name.replaceAll("ç", "c");

        fr_name = fr_name.replaceAll("[ÈÉÊË]", "E");
        fr_name = fr_name.replaceAll("[ÛÙ]", "U");
        fr_name = fr_name.replaceAll("[ÏÎ]", "I");
        fr_name = fr_name.replaceAll("[ÀÂ]", "A");
        fr_name = fr_name.replaceAll("Ô", "O");
        fr_name = fr_name.replaceAll("Ç", "C");

        return fr_name;

    }

}
