package ie.deri.nlp.esa.processor.wiki.index;



import ie.deri.nlp.esa.lucene.AnalyzerFactory;
import ie.deri.nlp.esa.lucene.Indexer;
import ie.deri.nlp.esa.reader.wikiarticle.WikiArticle;
import ie.deri.nlp.esa.reader.wikiarticle.WikiXMLFileReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;



public class WikiTextIndexer {

	private static Properties config = new Properties();
	private String wikiXmlFilePath;
	private String wikiEntityIndexPathToWrite;
	private final double BUFFERRAMSIZE = 2000.0;
	private Indexer indexer;

	public WikiTextIndexer(){
		loadConfig();
		openWriter();
	}

	public WikiTextIndexer(String configPath){
		loadConfig(configPath);
		openWriter();
	}

	private void loadConfig(String configPath){
		try {
			config.load(new FileInputStream(configPath));			
			wikiEntityIndexPathToWrite = config.getProperty("wikiTextIndexPath");
			wikiXmlFilePath = config.getProperty("wikiXmlFilePath");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}			

	private void loadConfig(){
		this.loadConfig("resources/load/com.ibm.bluej.watson.wikiAnchorTitleIndex.properties");
	}
	private void openWriter() {		
		try {

			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, AnalyzerFactory.getAnalyzer());
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			Directory index = getIndex(wikiEntityIndexPathToWrite);
			if(IndexReader.indexExists(index)) 
				config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);			
			config.setRAMBufferSizeMB(BUFFERRAMSIZE);
			indexer = new Indexer(config, index);	
		}
		catch (IOException e) {
			e.printStackTrace();
		}			
	}

	private Directory getIndex(String indexPath) {
		Directory index = null;
		try {
			index = new SimpleFSDirectory(new File(indexPath));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return index;
	}	

	public void index() {

		WikiXMLFileReader wikiXMLFileReader = new WikiXMLFileReader(wikiXmlFilePath);

		Iterator<WikiArticle> wikiArticleIter = null;
		try {
			wikiArticleIter = wikiXMLFileReader.getWikiArticleIter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WikiEntityLucDocCreator docCreator = new WikiEntityLucDocCreator();

		while(wikiArticleIter.hasNext()) {
			WikiArticle wikiArticle = wikiArticleIter.next();
			if ((wikiArticle.getTitle() != null) && (wikiArticle.getContent() != null) && (wikiArticle.getTitle().length() > 0) && (wikiArticle.getContent().length() > 0)){

				docCreator.addTitleField(wikiArticle.getTitle());
				docCreator.addTextContentField(wikiArticle.getContent());

				indexer.addDoc(docCreator.getLucDoc());
				docCreator.reset();
			}
		}

		indexer.closeIndexer();		
	}



	public static class WikiEntityLucDocCreator {

		private Document WikiEntityLucDoc = new Document();	

		public enum Fields {
			//WikiTitle, wiki article content that only have entities
			Title, TextContent;	
		}

		public void addTitleField(String title) {
			Field titleField = new Field(Fields.Title.toString(), title, Field.Store.YES, Field.Index.NOT_ANALYZED);
			WikiEntityLucDoc.add(titleField);			
		}

		public void addTextContentField(String textContent) {
			Field textContentField = new Field(Fields.TextContent.toString(), textContent, Field.Store.YES, Field.Index.ANALYZED);
			WikiEntityLucDoc.add(textContentField);			
		}	

		public Document getLucDoc() {		
			return WikiEntityLucDoc;
		}

		public void reset(){
			WikiEntityLucDoc = new Document();	
		}
	}

}


