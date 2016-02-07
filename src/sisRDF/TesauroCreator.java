package sisRDF;

import java.net.URI;

import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSStorageException;
import org.semanticweb.skosapibinding.SKOSFormatExt;
import org.semanticweb.skosapibinding.SKOSManager;

public class TesauroCreator {
	
	SKOSDataset dataset;
	
	public TesauroCreator (String URIfile) {
		SKOSManager man;
		try {
			man = new SKOSManager();
			dataset = man.loadDatasetFromPhysicalURI(URI.create(URIfile));
			try {
				man.save(dataset, SKOSFormatExt.RDFXML, URI.create("file:/home/victor/workspace/RecInformacion/RecuInfo/thesaurus.rdf"));
			} catch (SKOSStorageException e) {
				e.printStackTrace();
			}
//			for (SKOSConcept concept : dataset.getSKOSConcepts()) {
//			   System.out.println("Concept: " + concept.getURI());
//			}
		} catch (SKOSCreationException e) {
			e.printStackTrace();
		}
	}
	
	public SKOSDataset getDataSet() {
		return dataset;
	}
	
	
	public static void main (String[] a){
		TesauroCreator tc = new TesauroCreator("file:/home/victor/workspace/RecInformacion/RecuInfo/Tesauro.xml");
	}
	
}
