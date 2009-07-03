/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.tools;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.TagNameFilter;

/**
 *
 * @author Manuel
 */
public class AlternateFilterGetAuthors implements NodeFilter{

    private NodeFilter filter;
    public AlternateFilterGetAuthors(){
        filter = new TagNameFilter("LI");
        //filter = new HasParentFilter(filter);
        //filter = new AndFilter(filter, new NotFilter(new TagNameFilter("SPAN")));
    }

    public boolean accept(Node arg0) {
        return filter.accept(arg0);
    }

}
