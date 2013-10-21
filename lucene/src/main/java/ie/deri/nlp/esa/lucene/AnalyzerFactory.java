package ie.deri.nlp.esa.lucene;




import java.lang.reflect.InvocationTargetException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;



public class AnalyzerFactory {

	private static String getAnalyzerClassName(String analyzerType) {
		return "org.apache.lucene.analysis" + "." + analyzerType + "Analyzer";		
	}	

	public static Analyzer getAnalyzer() {

		return new StandardAnalyzer(Version.LUCENE_35);
	}	
	
	public static Analyzer getAnalyzer( String analyzerType) {
		try {
			try {
				return (Analyzer) Class.forName(getAnalyzerClassName(analyzerType)).getConstructor(Version.class).newInstance(Version.LUCENE_35);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new StandardAnalyzer(Version.LUCENE_35);
	}	
	
	
	//TODO: create a subclass to mention all of the analyzers by name 
	
	public static class AnalyzerType{
		public String StandardAnalyzer = "Standard";
		public String SnowBallAnalyzer = "Snowball";
		
	}

}	

