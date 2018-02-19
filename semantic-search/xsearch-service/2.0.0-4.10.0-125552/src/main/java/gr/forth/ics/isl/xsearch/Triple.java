/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.xsearch;

/**
 *
 * @author fafalios
 */
public class Triple {

    public String s;
    public String p;
    public String o;

    public Triple(String s, String p, String o) {
        this.s = s;
        this.p = p;
        this.o = o;
    }

    @Override
    public String toString() {
        return "Triple{" + "s=" + s + ", p=" + p + ", o=" + o + '}';
    }
}
