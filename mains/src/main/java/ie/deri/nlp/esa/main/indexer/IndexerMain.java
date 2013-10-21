package ie.deri.nlp.esa.main.indexer;

import ie.deri.nlp.esa.processor.wiki.index.WikiTextIndexer;

public class IndexerMain {


	public static void main(String[] args) {
//		String configPath = "resources/load/com.ibm.bluej.watson.wikiAnchorTitleIndex.properties";
		
		String configPath = args[0];
		WikiTextIndexer indexer = new WikiTextIndexer(configPath);
		
		System.out.println("indexing start");
		indexer.index();
		System.out.println("indexing done");		
	}
	
}
