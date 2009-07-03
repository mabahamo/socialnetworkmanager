/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.b9.socialNetwork.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

/**
 *
 * @author Manuel
 */
public class PublicacionesDCC {

    private static HashSet<String> bookwords = new HashSet<String>();
    private static TreeSet<String> autores = new TreeSet<String>();
    private static TreeSet<String> books = new TreeSet<String>();

    public static void main(String args[]){
        //2008,http://www.dcc.uchile.cl/1877/multipropertyvalues-29860-30242.html
        //2007,http://www.dcc.uchile.cl/1877/multipropertyvalues-29860-29988.html
        //2006,http://www.dcc.uchile.cl/1877/multipropertyvalues-29860-29989.html
        HashMap<String,String> links[] = new HashMap[3];
        links[0] = getLinks(2008,"http://www.dcc.uchile.cl/1877/multipropertyvalues-29860-30242.html");
        links[1] = getLinks(2007,"http://www.dcc.uchile.cl/1877/multipropertyvalues-29860-29988.html");
        links[2] = getLinks(2006,"http://www.dcc.uchile.cl/1877/multipropertyvalues-29860-29989.html");


        //palabras que identifican el nombre de un libro, y que por ningun motivo puede formar parte del nombre de una persona
        bookwords.add("x-tree");
        bookwords.add("groupware");
        bookwords.add("context");
        bookwords.add("flexible");
        bookwords.add("parallel");
        bookwords.add("software");
        bookwords.add("trees");
        bookwords.add("compressed");
        bookwords.add("text");
        bookwords.add("scalable");
        bookwords.add("robust");
        bookwords.add("declarative");
        bookwords.add("lighting");
        bookwords.add("formal");
        bookwords.add("structure");
        bookwords.add("investigating");
        bookwords.add("complexity");


        for(int i=0;i<links.length;i++){
            Iterator<String> it = links[i].keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                System.out.println((2008-i) + " - " + key);
                extractAuthors(links[i].get(key));
            }

        }


        Iterator<String> it = autores.iterator();
        while(it.hasNext()){
            System.out.println("AUTHOR> " + it.next());
        }

        it = books.iterator();
        while(it.hasNext()){
            System.out.println("BOOKS: " + it.next());
        }

    }

    private static HashMap<String, String> getLinks(int year, String url) {
        System.out.println("Buscando publicaciones del " + year);
        try {
            Parser parser = new Parser(url);
            FilterGetLinks filter = new FilterGetLinks();
            NodeList list = parser.parse(filter);
            HashMap<String, String> links = new HashMap<String,String>();
            for(int i=0;i<list.size();i++){
                LinkTag lt = (LinkTag)list.elementAt(i);
                String aux = lt.getLink();
                System.out.println(lt.getLinkText() + "\t --> " + aux);
                links.put(lt.getLinkText(), aux.replaceAll("article-", "printer-"));
            }
            return links;
        }
        catch(Exception ex){
            
        }
        return null;
    }

    private static void extractAuthors(String url) {
        try {
            System.out.println("Extrayendo autores y publicaciones desde " + url);
            Parser parser = new Parser(url);
            NodeList list = parser.parse(new FilterGetAuthors());
            if (list.size() == 0){
                System.out.println("No se detectaron elementos, probando filtro alternativo");
                parser = new Parser(url);
                list = parser.parse(new AlternateFilterGetAuthors());
            }
            System.out.println("Se encontraton " + list.size() + " elementos");
            for(int i=0;i<list.size();i++){
                String aux = list.elementAt(i).toPlainTextString();
                if (aux.trim().startsWith("1. ")){
                    //caso especial el documento esta separado con saltos de linea
                    //y con numeros al inicio de cada linea
                    String[] parse = aux.split("\r\n|\r|\n");
                    int count = 1;
                    for(int j=0;j<parse.length;j++){
                        if (parse[j].length()>1){
                            String line = parse[j];
                            line = line.replaceAll(count + ". ", "");
                            System.out.println("Parseo alternativo: " + line);
                            parseAuthors(line);
                            count++;
                        }
                    }
                    return;
                }
                else {
                    System.out.println(i + ": " + aux);
                    String[] aux2 = parseAuthors(aux);
                }
                
            }
        }
        catch(Exception ex){

        }
    }

    private static String[] parseAuthors(String line) {
        line = repair(line);
        String[] aux = line.split(",");
        for(int i=0;i<aux.length;i++){
            if (aux[i].contains(" and ") || aux[i].contains(" y ")){
                String[] test = parseAnd(aux[i]);
                if (isAuthor(test[0]) && isAuthor(test[1])){
                    if (test[0].length()>1){
                        System.out.println("> Author: " + test[0].trim());
                        autores.add(test[0].trim());
                    }
                    System.out.println("> Author: " + test[1].trim());
                    autores.add(test[1].trim());
                    String book = parseBook(aux[i+1]);
                    System.out.println("> Publicacion: " + book);
                    books.add(book);
                    return aux;
                }
            }
            if (isAuthor(aux[i])){
                System.out.println("> Author: " + aux[i].trim());
                autores.add(aux[i].trim());
            }
            else {
                String book = parseBook(aux[i]);
                System.out.println("> Publicacion: " + book);
                books.add(book);
                return aux;
            }

        }
        return aux;
    }

    private static boolean isAuthor(String string) {
        if (string.split(" ").length > 4){
            return false;
        }
        for(String word: bookwords){
            if (string.toLowerCase().contains(word)){
                return false;
            }
        }

        return true;
    }

    private static String repair(String line) {
        line = line.replaceAll("&amp;", " and ");
        line = line.replaceAll("Sánchez, J. Flores H. Saenz, M.", "J. Sánchez, H. Flores, M. Saenz");
        line = line.replaceAll("Sánchez, J. Flores, H\\.", " J. Sánchez, H. Flores");
        line = line.replaceAll("Sánchez, J. Sáenz, M. Salinas, A\\.", "J. Sánchez, M. Sáenz, A. Salinas");
        line = line.replaceAll("Jaime Sánchez, Saenz, M\\.", "Jaime Sánchez, M. Saenz");
        line = line.replaceAll("Sánchez, J\\.", "J. Sánchez");
        line = line.replaceAll("Alarcón, P\\.", "P. Alarcón");
        line = line.replaceAll("Flores, H\\.", "H. Flores");
        line = line.replaceAll("Saenz, M\\.", "M. Sáenz");
        line = line.replaceAll("Sáenz, M\\.", "M. Sáenz");
        line = line.replaceAll("Elías M\\.", "M. Elías");
        line = line.replaceAll("Aguayo F\\.", "F. Aguayo");
        line = line.replaceAll("Aguayo, F\\.,", "F. Aguayo,");
        line = line.replaceAll("Aguayo, F\\.", "F. Aguayo,");
        line = line.replaceAll("Miranda, J\\.", "J. Miranda");
        line = line.replaceAll("Zúñiga, M\\.", "M. Zúñiga");
        line = line.replaceAll("Ochoa, S\\.", "S. Ochoa,");
        line = line.replaceAll("Jaime Sánchez, Elias M. Science","Jaime Sánchez, M. Elias, Science");
        line = line.replaceAll("Jaime Sánchez, Iván Galaz, I., AudioStoryTeller", "Jaime Sánchez, Iván Galaz, AudioStoryTeller");
        line = line.replaceAll("Salinas, A\\.", "A. Salinas");
        line = line.replaceAll("Pérez, L\\.", "L. Pérez");
        line = line.replaceAll("José A, Pino", "José A. Pino");
        line = line.replaceAll("Roberto Opazo.", "Roberto Opazo,");
        line = line.replaceAll("Micciancio.", "Micciancio,");
        line = line.replaceAll("Ricardo Baeza-Yates ; Barbara Poblete","Ricardo Baeza-Yates , Barbara Poblete");
        line = line.replaceAll(" \\(editores","");
        line = line.replaceAll(", \\(Eds\\),", ",");
        line = line.replaceAll("\\(Eds\\)", "");
        line = line.replaceAll("\\(eds\\)", "");
        line = line.replaceAll("\\(Eds\\.\\)", "");
        line = line.replaceAll("\\(Editor\\)", "");
        line = line.replaceAll(" EDITORS", "");
        return line;
    }

    private static String[] parseAnd(String string) {
        String aux = string.replaceAll(", and ", ",");
        aux = aux.replaceAll(" and ", ",");
        aux = aux.replaceAll(" y ", ",");
        return aux.split(",");
    }

    private static String parseBook(String string) {
        String[] poList = string.split("\r\n|\r|\n");
        if (poList.length>=1){
            return clean(poList[0]);
        }
        return clean(string);
    }

    private static String clean(String string) {
        string = string.trim();
        if (string.startsWith("\"") && string.endsWith("\"")){
            string = string.substring(1,string.length()-2);
        }
        return string;
    }
}
