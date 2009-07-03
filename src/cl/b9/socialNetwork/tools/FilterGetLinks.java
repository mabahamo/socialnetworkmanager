/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.tools;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.StringFilter;

/**
 *
 * @author Manuel
 */
class FilterGetLinks implements NodeFilter{

    NodeFilter filter;
    public FilterGetLinks(){
        filter = new StringFilter("Publicaciones tipo", false);
        filter = new HasChildFilter(filter);
    }

    public boolean accept(Node arg0) {
       return filter.accept(arg0);
    }

}
