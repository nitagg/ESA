package ie.deri.nlp.esa.evaluation.correlation;

import ie.deri.nlp.esa.core.utils.BasicFileTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;



public class SpearmanRankCorrelation {

	SpearmansCorrelation correlation = null;

	public SpearmanRankCorrelation(){
		correlation = new SpearmansCorrelation();
	}

		private double[] textToArray(String filePath, int rowNo){
		
		BufferedReader reader = BasicFileTools.getBufferedReaderFile(filePath);
		String line = null;
		ArrayList<Double> list = new ArrayList<Double>();
		try {
			while((line = reader.readLine()) != null){
				List<String> cells = Arrays.asList(line.split("\t"));
//				System.out.println(cells.get(rowNo-1));
				list.add(new Double(cells.get(rowNo-1)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] array = new double[list.size()];

		for(int count = 0; count < list.size(); count++){
			array[count] = list.get(count); 
		}

		return array;
	}
	
	public double calculateCorrelation(String goldStandardFilePath, int GSFileRowNo, String testFilePath, int testFileRowNo ) {

		double[] GS_Scores = this.textToArray(goldStandardFilePath, GSFileRowNo);
		double[] results_Scores = this.textToArray(testFilePath, testFileRowNo);
		
		return correlation.correlation(GS_Scores, results_Scores);
	}


	public static void main(String[] args) {
		SpearmanRankCorrelation correlation = new SpearmanRankCorrelation();
		String GS_FilePath = "/Users/nitagg/Work/Entity Linking/Experiments/Results/wordnet353.txt";
		String results_FilePath = "/Users/nitagg/Work/Entity Linking/Experiments/Results/results.wordnet353.2005gitindex.txt";
		
		double d = correlation.calculateCorrelation(GS_FilePath, 3, results_FilePath, 3);
		System.out.println(d);
	}
}


