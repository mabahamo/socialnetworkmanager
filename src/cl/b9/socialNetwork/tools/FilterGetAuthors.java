/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.tools;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;

/**
 *
 * @author Manuel
 */
public class FilterGetAuthors implements NodeFilter{

    private NodeFilter filter;
    public FilterGetAuthors(){
        filter = new AndFilter(new TagNameFilter("P"), new HasAttributeFilter("class", "NormalP"));
        //filter = new HasParentFilter(filter);
        //filter = new AndFilter(filter, new NotFilter(new TagNameFilter("SPAN")));
    }

    public boolean accept(Node arg0) {
        if (arg0.toPlainTextString().trim().length() < 5){
            return false;
        }
        return filter.accept(arg0);
    }

}
