package ie.deri.nlp.esa.lucene;


import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;



public class Indexer {

	private Directory index = null;
	private IndexWriterConfig config; 
	private IndexWriter writer;	

	public Indexer(IndexWriterConfig config, Directory index) {
		this.config = config;
		this.index = index;
		openIndexWriter();		
	}

	private void openIndexWriter() {
		if(writer!=null)
			closeIndexer();
		try {
			writer = new IndexWriter(index, config);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		writer.setSimilarity(new NTFIDF());
	}

	public void closeIndexer(){
		try {
			writer.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addDoc(Document doc) {
		try {
			writer.addDocument(doc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IndexWriter getWriter() {
		return writer;
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