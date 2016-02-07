package sisRDF;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class SemanticSearcher {
	public static void ejecutarConsulta1(Model model, PrintWriter writer) {
		//definimos la consulta (tipo query)
		String queryString = "PREFIX gr12: <http://recInfo/gr12/terms/>"
		+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
		+ "Select distinct ?doc WHERE {"
				+ "?doc gr12:creator ?creator."
				+ "?doc gr12:keyword ?key."

				+ "{?key skos:broader ?key2."
				+ "?key2 skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"música\")}"
				+ "UNION"
				+ " {"
				+ "?key skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"música\")}"
				+ " ?creator gr12:name ?name"
				+ " FILTER regex(?name, \"javier\", \"i\") }";;
				System.out.println(queryString);
		
		//ejecutamos la consulta y obtenemos los resultados
		  Query query = QueryFactory.create(queryString) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
			    ResultSet results = qexec.execSelect() ;
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
			      Resource doc = soln.getResource("doc");  
			      //Literal date = soln.getLiteral("date");
			      //Resource key = soln.getResource("key");
			      System.out.println("13-2\t" + "oai_zaguan.unizar.es_"+
			    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
			      writer.println("13-2\t" + "oai_zaguan.unizar.es_"+
			    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
			      
			      
			      //System.out.println(key);
			    }
			  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta2(Model model, PrintWriter writer) {
		//definimos la consulta (tipo query)
		String queryString = "PREFIX gr12: <http://recInfo/gr12/terms/>"
		+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
		+ "Select distinct ?doc WHERE {"
				+ "?doc gr12:keyword ?key."

				+ "{?key skos:broader ?key2."
				+ "?key2 skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"guerra de Independencia\")}"
				+ "UNION"
				+ " {"
				+ "?key skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"guerra de Independencia\")}}";
		System.out.println(queryString);

		
		//ejecutamos la consulta y obtenemos los resultados
		  Query query = QueryFactory.create(queryString) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
			    ResultSet results = qexec.execSelect() ;
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
			      Resource doc = soln.getResource("doc");  
			      //Literal date = soln.getLiteral("date");
			      //Resource key = soln.getResource("key");
			      System.out.println("02-4\t" + "oai_zaguan.unizar.es_"+
			    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
			      writer.println("02-4\t" + "oai_zaguan.unizar.es_"+
			    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
			      //System.out.println(key);
			    }
			  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta3(Model model, PrintWriter writer) {
		String queryString = "PREFIX gr12: <http://recInfo/gr12/terms/>"
		+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
		+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
		+ "Select distinct ?tes WHERE {"
				+ "?tes rdf:type gr12:Tesis."
				+ "?tes gr12:keyword ?key."

				+ "{?key skos:broader ?key2."
				+ "?key2 skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"energías renovables\")}"
				+ "UNION"
				+ " {"
				+ "?key skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"energías renovables\")}"
				+ "?tes dc:date ?date."
				+ "FILTER (?date > \"2009\")"
				+ "FILTER (?date < \"2016\")}";
		System.out.println(queryString);
		
		//ejecutamos la consulta y obtenemos los resultados
		  Query query = QueryFactory.create(queryString) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
			    ResultSet results = qexec.execSelect() ;
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
			      Resource doc = soln.getResource("tes");  
			      //Literal date = soln.getLiteral("date");
			      //Resource key = soln.getResource("key");
			      System.out.println("09-3\t" + "oai_zaguan.unizar.es_"+
			    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
			      writer.println("09-3\t" + "oai_zaguan.unizar.es_"+
			    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
			      //System.out.println(key);
			    }
			  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta4(Model model, PrintWriter writer) {
		String queryString = "PREFIX gr12: <http://recInfo/gr12/terms/>"
		+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
		+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
		+ "Select distinct ?doc WHERE {"
				+ "?doc dc:date ?date."
				+ "?doc gr12:keyword ?key."

				+ "{?key skos:broader ?key2."
				+ "?key2 skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"videojuego\")}"
				+ "UNION"
				+ " {"
				+ "?key skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"videojuego\")}"
				+ "FILTER (?date > \"2009\")}";
		System.out.println(queryString);
		
		//ejecutamos la consulta y obtenemos los resultados
		  Query query = QueryFactory.create(queryString) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      Resource doc = soln.getResource("doc");  
		      //Literal date = soln.getLiteral("date");
		      //Resource key = soln.getResource("key");
		      System.out.println("07-2\t" + "oai_zaguan.unizar.es_"+
		    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
		      writer.println("07-2\t" + "oai_zaguan.unizar.es_"+
		    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
		      //System.out.println(key);
		    }
		  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta5(Model model, PrintWriter writer) {
		String queryString = "PREFIX gr12: <http://recInfo/gr12/terms/>"
		+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
		+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
		+ "Select distinct ?doc WHERE {"
				+ "?doc gr12:keyword ?key."

				+ "{{?key skos:broader ?key2."
				+ "?key2 skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"construcción\")}"
				+ "UNION"
				+ " {"
				+ "?key skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"construcción\")}}"
				+ "UNION"
				+ "{{?key skos:broader ?key2."
				+ "?key2 skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"decoración\")}"
				+ "UNION"
				+ " {"
				+ "?key skos:prefLabel ?prefLabel."
				+ "FILTER regex(?prefLabel,\"decoración\")}}"
				+ "}";
		System.out.println(queryString);

		
		//ejecutamos la consulta y obtenemos los resultados
		  Query query = QueryFactory.create(queryString) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      Resource doc = soln.getResource("doc");  
		      //Literal date = soln.getLiteral("date");
		      //Resource key = soln.getResource("key");
		      System.out.println("05-5\t" + "oai_zaguan.unizar.es_"+
		    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
		      writer.println("05-5\t" + "oai_zaguan.unizar.es_"+
		    		  doc.asResource().getURI().replaceAll("[^0-9]", "") +".xml");
		      //System.out.println(key);
		    }
		  } finally { qexec.close() ; }
	}
	
	public static void main (String args[]) {
		String rdfPath = "modelo.rdf";
		String outputPath = "resultsSemantica.txt";
		String infoNeedsPath = "Datos/infoneedsSemantica.xml";
		for(int i=0;i<args.length;i++) {
	      if ("-rdf".equals(args[i])) {
	    	  rdfPath = args[i+1];
	        i++;
	      } else if ("-output".equals(args[i])) {
	    	outputPath = args[i+1];
	        i++;
	      } else if ("-infoNeeds".equals(args[i])) {
	    	  infoNeedsPath = args[i+1];
	        i++;
	      }
	    }
		Model model = RDFDataMgr.loadModel(rdfPath);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputPath, "UTF-8");
		           
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ejecutarConsulta1(model,writer);
		ejecutarConsulta2(model,writer);
		ejecutarConsulta3(model,writer);
		ejecutarConsulta4(model,writer);
		ejecutarConsulta5(model,writer);
		writer.close();
	}

}
