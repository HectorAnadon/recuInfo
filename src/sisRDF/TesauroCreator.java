package sisRDF;

import java.net.URI;

import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skosapibinding.SKOSManager;

public class TesauroCreator {
	
	public TesauroCreator (String URIfile) {
		SKOSManager man;
		try {
			man = new SKOSManager();
			SKOSDataset dataset = man.loadDataset(URI.create(URIfile));
			for (SKOSConcept concept : dataset.getSKOSConcepts()) {
			   System.out.println("Concept: " + concept.getURI());
			}
		} catch (SKOSCreationException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main (String[] a){
		TesauroCreator tc = new TesauroCreator("file:/home/victor/workspace/RecInformacion/RecuInfo/Tesauro.xml");
	}
	
}
