package sisRDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.DOAP;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import uk.ac.manchester.cs.skos.SKOSConceptImpl;
import uk.ac.manchester.cs.skos.properties.SKOSAltLabelPropertyImpl;

public class RDFCreator {

	private TreeSet<String> namesSet = new TreeSet<String>();
	private TreeSet<String> publishersSet = new TreeSet<String>();
	private Model model;
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
	
	public RDFCreator() {
		model = RDFDataMgr.loadModel("gr12.ttl");
		spaRes = model.createResource("http://recInfo/gr12/terms/languages/Spanish");
		enRes = model.createResource("http://recInfo/gr12/terms/languages/English");
		documentSchema = model.createResource("http://recInfo/gr12/terms/Document");
		tfg = model.createResource("http://recInfo/gr12/terms/Document/Tfg");
		tfg.addProperty(RDFS.subClassOf, documentSchema);
		tfm = model.createResource("http://recInfo/gr12/terms/Document/Tfm");
		tfm.addProperty(RDFS.subClassOf, documentSchema);
		tesis = model.createResource("http://recInfo/gr12/terms/Document/Tesis");
		tesis.addProperty(RDFS.subClassOf, documentSchema);
		name = model.createProperty("http://recInfo/gr12/terms/name");
		surname = model.createProperty("http://recInfo/gr12/terms/surname");
		creatorSchema = model.createProperty("http://recInfo/gr12/terms/creator");
		person = model.createResource("http://recInfo/gr12/terms/Person");
	}
	
	public Model getModel() {
		return model;
	}
	
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
					document.addProperty(DC.description, xmlDoc.getElementsByTagName("dc:description").item(0).getTextContent());
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
	
	public static void main (String args[]) {
		RDFCreator prueba = new RDFCreator();
        prueba.parser("Datos/recordsdc");
        Model model = prueba.getModel();
        // write the model in the standar output
        model.write(System.out); 
    }
	
}
