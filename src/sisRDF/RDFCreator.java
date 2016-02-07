package sisRDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.semanticweb.skos.SKOSConcept;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.DOAP;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import uk.ac.manchester.cs.skos.SKOSConceptImpl;
import uk.ac.manchester.cs.skos.properties.SKOSAltLabelPropertyImpl;

public class RDFCreator {

	private TreeSet<String> namesSet = new TreeSet<String>();
	private TreeSet<String> publishersSet = new TreeSet<String>();
	private static Model model;
	private Scanner scanner;
	private final String identifier = "dc:identifier";
	private final String publisher = "dc:publisher";
	private Resource spaRes;
	private Resource enRes;
	private Resource documentSchema;
	private Resource tfg;
	private Resource tfm;
	private Resource tesis;
	private Property name;
	private Property surname;
	private Property creatorSchema;
	private Resource person;
	private Property descriptionSchema;
	private String[] keywords = {"musica", "sonido", "rock", "banda", "instrumento",
			"concierto", "guerra de independencia", "ejercito", "dictadura", "batalla", "victoria", "derrota",
			"energías renovables", "energía geotérmica", "energía solar", "medio ambiente", "biomasa", 
			"parque eólico", "energía eólica", "videojuego", "unreal tournament", "unity", "motor gráfico",
			"simulador", "multijugado", "diseño", "programacion", "implementación", "arquitectura de red",
			"centralizado", "contrucción", "arquitecto", "arquitectura", "restauración", "rehabilitación",
			"decoración", "elemento decorativo", "arte mueble", "escultura", "historia", "histórico", "gótico", 
			"edad media", "castillo", "yacimiento", "medieval", "siglo"};
	private Resource[] keywordResource = new Resource[keywords.length];
	private Property keywordProperty;
	
	public RDFCreator() {
		model = RDFDataMgr.loadModel("gr12.ttl");
		RDFDataMgr.read(model, "thesaurus.rdf") ;
		spaRes = model.createResource("http://recInfo/gr12/terms/languages/Spanish");
		enRes = model.createResource("http://recInfo/gr12/terms/languages/English");
		documentSchema = model.createResource("http://recInfo/gr12/terms/Document");
		tfg = model.createResource("http://recInfo/gr12/terms/Tfg");
		tfg.addProperty(RDFS.subClassOf, documentSchema);
		tfm = model.createResource("http://recInfo/gr12/terms/Tfm");
		tfm.addProperty(RDFS.subClassOf, documentSchema);
		tesis = model.createResource("http://recInfo/gr12/terms/Tesis");
		tesis.addProperty(RDFS.subClassOf, documentSchema);
		name = model.createProperty("http://recInfo/gr12/terms/name");
		surname = model.createProperty("http://recInfo/gr12/terms/surname");
		creatorSchema = model.createProperty("http://recInfo/gr12/terms/creator");
		person = model.createResource("http://recInfo/gr12/terms/Person");
		descriptionSchema = model.createProperty("http://recInfo/gr12/terms/description");
		
		String keywordPrefix = "http://RecInfo/gr12/Tesauro#";
		for(int i=0; i < keywords.length; i++) {
			keywordResource[i] = model.createResource(keywordPrefix + remove1(keywords[i].trim()));
		}
		keywordProperty = model.createProperty("http://recInfo/gr12/terms/keyword");
	}
	
	public Model getModel() {
		return model;
	}
	
	public static String remove1(String input) {
	    // Cadena de caracteres original a sustituir.
	    String original = "áàäéèëíìïóòöúùuñ";
	    // Cadena de caracteres ASCII que reemplazarán los originales.
	    String ascii = "aaaeeeiiiooouuun";
	    String output = input;
	    for (int i=0; i<original.length(); i++) {
	        // Reemplazamos los caracteres especiales.
	        output = output.replace(original.charAt(i), ascii.charAt(i));
	    }//for i
	    return output;
	}//remove1
	
	public void parser (String path){
		File dir = new File(path);
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			for(File f : files){
				parser(f);
			}
		}else{
			System.err.println("ERROR. El path introducido debe ser un directorio.");
			System.exit(1);
		}
	}

	private void parser(File f) {
		 try {  
	        	DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
				DocumentBuilder DB = DBF.newDocumentBuilder();
				org.w3c.dom.Document xmlDoc = DB.parse(f);
				Resource document = null;
				NodeList identifierList = xmlDoc.getElementsByTagName(identifier);
				if(identifierList.item(1).getTextContent().toLowerCase().contains("tfg")){
					document = model.createResource(identifierList.item(0).getTextContent(), tfg);
				}else if(identifierList.item(1).getTextContent().toLowerCase().contains("tfm")){
					document = model.createResource(identifierList.item(0).getTextContent(), tfm);
				}else if(identifierList.item(1).getTextContent().toLowerCase().contains("tesis")){
					document = model.createResource(identifierList.item(0).getTextContent(), tesis);
				}else {
					document = model.createResource(identifierList.item(0).getTextContent(), documentSchema);
				}
				
				if (xmlDoc.getElementsByTagName("dc:title").item(0) != null) {
					document.addProperty(DC.title, xmlDoc.getElementsByTagName("dc:title").item(0).getTextContent());
					String text = xmlDoc.getElementsByTagName("dc:title").item(0).getTextContent().toLowerCase();
					for (int i = 0; i < keywords.length; i++) {
						if (text.contains(keywords[i])) {
							document.addProperty(keywordProperty, keywordResource[i]);
						}
					}
				}
				
				NodeList creatorList = xmlDoc.getElementsByTagName("dc:creator");
				String creator, creatorURI, creatorName, creatorSurname;
				for (int i=0; i<creatorList.getLength(); i++){
					creator = creatorList.item(i).getTextContent();
					creatorSurname = creator.split(",")[0];
					if(creator.split(",").length > 1) {
						creatorName = creator.split(",")[1].trim();
					} else {
						creatorName = "John";
					}
					creator = creator.replaceAll(" ", "");
					creator = creator.replaceAll(",", "");
					creatorURI = "http://recInfo/gr12/terms/creators/" + creator;
					Resource creatorRes;
					if(namesSet.contains(creatorURI)){
						creatorRes = model.getResource(creatorURI);
					}else{
						namesSet.add(creatorURI);
						creatorRes = model.createResource(creatorURI, person);
						//creatorRes = model.addProperty(RDF.type, person);
						creatorRes.addProperty(name, creatorName);
						creatorRes.addProperty(surname, creatorSurname);
					}
					document.addProperty(creatorSchema, creatorRes);
				}
				
				if (xmlDoc.getElementsByTagName("dc:date").item(0) != null) {
					document.addProperty(DC.date, xmlDoc.getElementsByTagName("dc:date").item(0).getTextContent());
				}
				
				if (xmlDoc.getElementsByTagName("dc:description").item(0) != null) {
					document.addProperty(descriptionSchema, xmlDoc.getElementsByTagName("dc:description").item(0).getTextContent());
					String text = xmlDoc.getElementsByTagName("dc:description").item(0).getTextContent().toLowerCase();
					for (int i = 0; i < keywords.length; i++) {
						if (text.contains(keywords[i])) {
							document.addProperty(keywordProperty, keywordResource[i]);
						}
					}
				}
				
				if (xmlDoc.getElementsByTagName("dc:language").item(0) != null && 
						xmlDoc.getElementsByTagName("dc:language").item(0).getTextContent().contains("spa")) {
					document.addProperty(DC.language, spaRes);
				}
				
				if (xmlDoc.getElementsByTagName("dc:publisher").item(0) != null) {
					String publisher = xmlDoc.getElementsByTagName("dc:publisher").item(0).getTextContent();
					String[] publishers = publisher.split(", ");
					for (String s : publishers){
						s = s.replaceAll(" ", "");
						String publisherURI = "http://publishers/" + s;
						Resource publisherRes;
						if(publishersSet.contains(publisherURI)){
							publisherRes = model.getResource(publisherURI);
						}else{
							publishersSet.add(publisherURI);
							publisherRes = model.createResource(publisherURI);
						}
						document.addProperty(DC.publisher, publisherRes);
					}
				}
				
	          } catch (Exception e) {
	        	  e.printStackTrace();
	          }
		
	}
	
	public static void ejecutarConsulta1() {
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
			      //System.out.println(key);
			    }
			  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta2() {
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
			      //System.out.println(key);
			    }
			  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta3() {
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
			      //System.out.println(key);
			    }
			  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta4() {
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
		      //System.out.println(key);
		    }
		  } finally { qexec.close() ; }
	}
	
	public static void ejecutarConsulta5() {
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
		      //System.out.println(key);
		    }
		  } finally { qexec.close() ; }
	}
		
	
	public static void main (String args[]) {
		RDFCreator prueba = new RDFCreator();
        prueba.parser("Datos/recordsdc");
        Model model = prueba.getModel();
        // write the model in the standar output
        //model.write(System.out); 
        try {
			PrintWriter writer = new PrintWriter("a", "UTF-8");
			model.write(writer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("\n\n===============================================================================\n\n");
        ejecutarConsulta1();
        ejecutarConsulta2();
        ejecutarConsulta3();
        ejecutarConsulta4();
        ejecutarConsulta5();
    }
	
}
