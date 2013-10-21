package ie.deri.nlp.esa.main.relatedness;

public class ESAMain {

	public static void main(String[] args) {
//		String configPath = "resources/load/com.ibm.bluej.watson.wikiAnchorTitleIndex.properties";
		
		String configFile = args[0];
		ESA esa = new ESA(configFile);
		
		double score = esa.getScore("love", "like");
		
		System.out.println(score);
		
	}
}
