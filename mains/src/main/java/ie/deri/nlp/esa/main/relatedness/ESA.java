package ie.deri.nlp.esa.main.relatedness;




import ie.deri.nlp.esa.core.utils.Vector;
import ie.deri.nlp.esa.core.utils.VectorUtils;
import ie.deri.nlp.esa.processor.wiki.search.WikiTextSearch;
import ie.deri.nlp.esa.processor.wiki.search.WikiTextSearch.WikiResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class ESA {


	private WikiTextSearch wikiTextSearch = null;
	private VectorUtils<String> vectorUtils = null;
	private Properties config = null; 
	
	public ESA(String configFile) {
		wikiTextSearch = new WikiTextSearch(configFile);
		vectorUtils = new VectorUtils<String>();
		
	}

	public void loadConfig(String configFilePath) {
		if(this.config  == null) {
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
		
	
	public double getScore(String text1, String text2) {
//		Vector<String> vector1 = wikiResults2Vector(this.wikiTextEntitySearch.searchText2005(text1));
//		Vector<String> vector2 = wikiResults2Vector(this.wikiTextEntitySearch.searchText2005(text2));

		Vector<String> vector1 = wikiResults2Vector(this.wikiTextSearch.searchText(text1));
		Vector<String> vector2 = wikiResults2Vector(this.wikiTextSearch.searchText(text2));		
		
		double cosineProduct = this.vectorUtils.cosineProduct(vector1, vector2);
		
		return cosineProduct;
	}
	
	
	private Vector<String> wikiResults2Vector(List<WikiResult> wikiResults){
		HashMap<String, Double> resultsMap = new HashMap<String, Double>();
		for(WikiResult wikiResult: wikiResults){
			resultsMap.put(wikiResult.getWikiArticle().getTitle(), wikiResult.getScore());
		}
		return new Vector<String>(resultsMap);
	}

	
}
