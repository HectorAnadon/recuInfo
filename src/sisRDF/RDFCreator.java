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
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.semanticweb.skos.SKOSConcept;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
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
	
	public RDFCreator() {
		model = ModelFactory.createDefaultModel();
		spaRes = model.createResource("http://languages/spanish");
		enRes = model.createResource("http://languages/english");
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
				for(int i=0; i < identifierList.getLength(); i++ ) {
					if (i==0) {
						document = model.createResource(identifierList.item(i).getTextContent()).addProperty(RDF.type, "URI");
					}else if(i==1){
						//TODO: Agnadir TFG/TFM/TESIS
					}else{
						document.addProperty(SKOSConceptImpl, identifierList.item(i).getTextContent());
					}
				}
				
				if (xmlDoc.getElementsByTagName("dc:title").item(0) != null) {
					document.addProperty(DC.title, xmlDoc.getElementsByTagName("dc:title").item(0).getTextContent());
				}
				
				NodeList creatorList = xmlDoc.getElementsByTagName("dc:creator");
				String creator, creatorURI;
				for (int i=0; i<creatorList.getLength(); i++){
					creator = creatorList.item(i).getTextContent();
					creator.replaceAll(" ", "");
					creator.replaceAll(",", "");
					creatorURI = "http://creators/" + creator;
					Resource creatorRes;
					if(namesSet.contains(creatorURI)){
						creatorRes = model.getResource(creatorURI);
					}else{
						namesSet.add(creatorURI);
						creatorRes = model.createResource(creatorURI);
					}
					document.addProperty(DC.creator, creatorRes);
				}
				
				if (xmlDoc.getElementsByTagName("dc:date").item(0) != null) {
					document.addProperty(DC.date, xmlDoc.getElementsByTagName("dc:date").item(0).getTextContent());
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
		RDFCreator pene = new RDFCreator();
        pene.parser("Datos/recordsdc");
        Model model = pene.getModel();
        // write the model in the standar output
        model.write(System.out); 
    }
	
}
