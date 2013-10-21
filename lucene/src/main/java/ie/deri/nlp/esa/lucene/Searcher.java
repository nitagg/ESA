package ie.deri.nlp.esa.lucene;



import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;


public class Searcher {
	private final static int maxClauseCount = 6144;
	private IndexReader reader;
	private IndexSearcher searcher;
	
	public Searcher(String indexPath){
		BooleanQuery.setMaxClauseCount(maxClauseCount);		
		setIndexReader(getIndex(indexPath));
		setIndexSearcher();		
	}

	public Searcher(IndexReader reader){
//		BooleanQuery.setMaxClauseCount(maxClauseCount);		
		setIndexReader(reader);
		setIndexSearcher();		
	}
	
	private void setIndexReader(IndexReader reader) {
			this.reader = reader;
	}

	private void setIndexReader(Directory indexDir) {
		try {
			this.reader = IndexReader.open(indexDir);
			indexDir.close();			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void setIndexSearcher() {
		this.searcher = new IndexSearcher(this.reader);
//		this.searcher.setSimilarity(new NTFIDF());
	}

	public boolean closeIndex() {		
		try {
			searcher.close();
			reader.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Directory getIndex(String indexPath) {
		Directory index = null;
		try {
			index = new SimpleFSDirectory(new File(indexPath + 
					System.getProperty("file.separator")));		
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return index;
	}


	private TopScoreDocCollector search(Query query, int lucHits) {
		TopScoreDocCollector collector = TopScoreDocCollector.create(lucHits, true);
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return collector;				
	}

	public TopScoreDocCollector termQuerySearch(String queryString, String fieldName, int lucHits) {		
		TermQuery query = new TermQuery(new Term(fieldName, queryString));
		return search(query, lucHits);
	}
	
	private static void escapeStrings(String[] queryStrings) {
		for(int i=0; i<queryStrings.length; i++) {
			queryStrings[i] = QueryParser.escape(queryStrings[i]);
		}
	}
	
	public TopScoreDocCollector multiFieldSearch(String[] queryStrings, String[] fields, BooleanClause.Occur[] flags, Analyzer analyzer, int lucHits){		
		escapeStrings(queryStrings);
		Query query = null;
		try {
			query = MultiFieldQueryParser.parse(Version.LUCENE_35, queryStrings, fields, flags, analyzer);			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return search(query, lucHits);
	}

	public Document getDocumentWithDocID(int docID) {
		try {
			return searcher.doc(docID);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TopScoreDocCollector search(String queryString, int lucHits, String fieldName, Analyzer analyzer) {
		queryString = QueryParser.escape(queryString.trim());		
		if(queryString.equalsIgnoreCase("")) 
			return null;
		Query query = null;
		try {
			QueryParser queryParser = new QueryParser(Version.LUCENE_35, fieldName, analyzer);			
			query = queryParser.parse(queryString);
		} catch (ParseException e) {
			e.printStackTrace();
		}						
		return search(query, lucHits);	
	}

	public double getIDF(Term term){
		int docFreq = 1;
		try {
			docFreq = this.reader.docFreq(term);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double idf = Math.log(this.reader.maxDoc() / (docFreq + 1.0)) + 1;
		
		return idf;
	}
	
	public class NTFIDF extends Similarity {

		
		private static final long serialVersionUID = 1L;

		@Override
		public float coord(int overlap, int maxOverlap) {
			return 1;
		}

		@Override
		public float idf(int docFreq, int numDocs) {
			return (float) (Math.log(numDocs/(docFreq+1.0)));
		}

		@Override
		public float queryNorm(float sumOfSquaredWeights) {

			return 1;
		}

		@Override
		public float sloppyFreq(int distance) {
			return 1;
		}

		@Override
		public float tf(float freq) {
			return (float) Math.log(1 + freq);
		}

		@Override
		public float computeNorm(String arg0, FieldInvertState arg1) {
			return 1;
		}
	}
	
	
}
