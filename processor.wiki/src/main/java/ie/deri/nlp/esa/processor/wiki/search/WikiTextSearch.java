package ie.deri.nlp.esa.processor.wiki.search;


import ie.deri.nlp.esa.lucene.AnalyzerFactory;
import ie.deri.nlp.esa.lucene.Searcher;
import ie.deri.nlp.esa.processor.wiki.index.WikiTextIndexer;
import ie.deri.nlp.esa.reader.wikiarticle.WikiArticle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.search.TopScoreDocCollector;


/**
 * @author naggarw
 *
 */

public class WikiTextSearch {

	private static final String WIKI_TEXT_CONTENT_FIELD = WikiTextIndexer.WikiEntityLucDocCreator.Fields.TextContent.toString();
	private static final String WIKI_TITLE_FIELD = WikiTextIndexer.WikiEntityLucDocCreator.Fields.Title.toString();


	private Properties config = null;

	private Searcher searcher;
	private int wikiArticlesHits = 1000;

	
	public WikiTextSearch(String configFile){
		loadConfig(configFile);
		initialize();
	}

	private void initialize(){
		String indexPath = this.config.getProperty("wikiTextIndexPath");
		this.searcher = new Searcher(indexPath);

		wikiArticlesHits = new Integer(this.config.getProperty("wikiArticlesHits"));		
	}

	public void loadConfig(String configFilePath) {
		if(this.config == null) {
			try {
				this.config =  new Properties();
				this.config.load(new FileInputStream(configFilePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public List<WikiResult> searchText(String query){
	
		ArrayList<WikiResult> wikiResults = new ArrayList<WikiResult>();

		TopScoreDocCollector docCollector = this.searcher.search(query, wikiArticlesHits, WIKI_TEXT_CONTENT_FIELD, AnalyzerFactory.getAnalyzer());
		org.apache.lucene.search.ScoreDoc[] scoreDocs = docCollector.topDocs().scoreDocs;


		for(int docNo = 0; docNo < scoreDocs.length; ++docNo) {
			int docID = scoreDocs[docNo].doc;
			double score = scoreDocs[docNo].score;

			org.apache.lucene.document.Document document = searcher.getDocumentWithDocID(docID);
			wikiResults.add(new WikiResult(new WikiArticle(document.get(WIKI_TEXT_CONTENT_FIELD), document.get(WIKI_TITLE_FIELD)), score));
		}

		return wikiResults;
	}


	public static void main(String[] args) {
		//		String configFile = "resources/load/ie.deri.nlp.entityRanking.properties";
		String configFile = args[0];
		WikiTextSearch wikiEntitySearch = new WikiTextSearch(configFile);

		//		List<WikiResult> results = wikiEntitySearch.searchText2005("apple");
		//		List<WikiResult> results = wikiEntitySearch.searchEntity(args[1]);
		List<WikiResult> results = wikiEntitySearch.searchText(args[1]);

		int count = 1;
		for(WikiResult wikiResult : results)
			System.out.println(count++ + ":" + wikiResult.getWikiArticle().getTitle());

	}
	
	public class WikiResult{
		private WikiArticle wikiArticle = null;
		private double score = 0.0;

		public WikiResult(WikiArticle wikiArticle, double score){
			this.wikiArticle = wikiArticle;
			this.score = score;
		}

		public WikiArticle getWikiArticle() {
			return wikiArticle;
		}
		public void setWikiArticle(WikiArticle wikiArticle) {
			this.wikiArticle = wikiArticle;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}

	}

	
}