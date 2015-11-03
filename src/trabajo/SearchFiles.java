package trabajo;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Hashtable;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Simple command-line based search demo. */
public class SearchFiles {

  private SearchFiles() {}
  private static Hashtable<String, String> nombres = new Hashtable<String, String>();

  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
	nameDictionary();
    String usage =
      "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] -infoNeeds [infoNeedsFile] -output [resultsFile]";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }

    String index = "index";
    String field = "contents";
    String infoNeeds = "Datos/InfoNeeds/necesidadesInformacionElegidas.xml";
    String output = "Datos/InfoNeeds/output.txt";
    
    for(int i = 0;i < args.length;i++) {
      if ("-index".equals(args[i])) {
        index = args[i+1];
        i++;
      } else if ("-infoNeeds".equals(args[i])) {
    	infoNeeds = args[i+1];
        i++;
      } else if ("-output".equals(args[i])) {
    	output = args[i+1];
        i++;
      } 
    }
    
    String[][] queryString = parseInfoNeeds(infoNeeds);
    
    IndexReader reader = DirectoryReader.open(FSDirectory.open((new File(index)).toPath()));
    IndexSearcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new SpanishAnalyzer();
    QueryParser parser = new QueryParser(field, analyzer);
    
    PrintWriter writer = new PrintWriter(output, "UTF-8");
    for (int i = 0; i < queryString.length; i++) {

      String line = queryString[i][1];

      /*if (line == null || line.length() == -1) {
        break;
      }*/

      line = line.trim();
      /*if (line.length() == 0) {
        break;
      }*/
      
      
      BooleanQuery b = new BooleanQuery();
      String normalized = parser.parse(line).toString(field);
      findNames(b, normalized);
      findYear(b, normalized);
      Query query = parser.parse(line + " " + b.toString());
      
      System.out.println("Searching for: " + query.toString(field));

      doPagingSearch(searcher, query, queryString[i][0], writer);

      /*if (queryString != null) {
        break;
      }*/
    }
    reader.close();
    writer.close();
  }

  private static String[][] parseInfoNeeds(String infoNeeds) {
	String[][] result = null;
	try {
		DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
		DocumentBuilder DB = DBF.newDocumentBuilder();
		org.w3c.dom.Document xmlDoc = DB.parse(new File(infoNeeds));
		NodeList identifier = xmlDoc.getElementsByTagName("identifier");
		NodeList text = xmlDoc.getElementsByTagName("text");
		result = new String[identifier.getLength()][2];
		for(int i = 0; i < identifier.getLength(); i++) {
			System.out.println();
			result[i][0] = identifier.item(i).getTextContent();
			result[i][1] = text.item(i).getTextContent();
		}
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
    } catch (SAXException e) {	
  	// TODO Auto-generated catch block
		e.printStackTrace();
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return result;
}

private static void nameDictionary() {
	  nombres.put("javier", "");
	  nombres.put("jorge", "");
	  nombres.put("victor", "");
	  nombres.put("hector", "");
}

private static void findYear(BooleanQuery b, String line) {
	Scanner sc = new Scanner(line);
	int menor = 10000;
	int mayor = 999;
	while (sc.hasNext()) {
		if (sc.hasNextInt()) {
			int i = sc.nextInt();
			if (i > mayor && i < 10000) {
				mayor = i;
			} 
			if (i < menor && i > 999) {
				menor = i;
			}
		} else {
			sc.next();
		}
    }
	while (menor <= mayor) {
		b.add(new TermQuery(new Term("date", Integer.toString(menor))),
				BooleanClause.Occur.SHOULD);
		menor++;
	}
}

private static void findNames(BooleanQuery b, String line) {
	Scanner sc = new Scanner(line);
	while (sc.hasNext()) {
		String token = sc.next();
		if (nombres.containsKey(token)) {
			b.add(new TermQuery(new Term("creator", token)),
					BooleanClause.Occur.SHOULD);
		}
	}
}

/**
   * This demonstrates a typical paging search scenario, where the search engine presents 
   * pages of size n to the user. The user can then go to the next page if interested in
   * the next hits.
   * 
   * When the query is executed for the first time, then only enough results are collected
   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
   * is executed another time and all hits are collected.
   * 
   */
  public static void doPagingSearch(IndexSearcher searcher, Query query, String id,
		  PrintWriter writer) throws IOException {
 
    // Collect enough docs to show 5 pages
    TopDocs results = searcher.search(query, 9999);
    ScoreDoc[] hits = results.scoreDocs;
    
    int numTotalHits = results.totalHits;
    System.out.println(numTotalHits + " total matching documents");

    int start = 0;
        
    for (int i = start; i < numTotalHits/*numTotalHits*/; i++) {

        Document doc = searcher.doc(hits[i].doc);
        Path path = Paths.get(doc.get("path"));
        System.out.printf("%s\t%s\n",id, path.getFileName());
        writer.printf("%s\t%s\n",id, path.getFileName());
        String modified = doc.get("modified");
        /*if (path != null) {
          System.out.println((i+1) + ". " + path);
          System.out.println("  modified: " + new Date(Long.parseLong(modified)).toString());
          System.out.println(searcher.explain(query, hits[i].doc));
        } else {
          System.out.println((i+1) + ". " + "No path for this document");
        }*/
                  
      }
  }

}
