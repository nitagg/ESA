package ie.deri.nlp.esa.evaluation;

import ie.deri.nlp.esa.core.utils.BasicFileTools;
import ie.deri.nlp.esa.main.relatedness.ESA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TextPairsESA {

	private Properties config = null;
	private String GSFilePath;
	private String resultsFilePath; 
	private ESA esa;

	public TextPairsESA(String configFile){
		loadConfig(configFile);
		GSFilePath = this.config.getProperty("GSFilePath");
		resultsFilePath = this.config.getProperty("resultsFilePath");
		esa = new ESA(configFile);
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

	public static void main(String[] args) throws IOException {

		String configFile = args[0];
		TextPairsESA textPairsESA = new TextPairsESA(configFile);

		BufferedReader reader = BasicFileTools.getBufferedReaderFile(textPairsESA.GSFilePath);
		StringBuffer buffer = new StringBuffer();

		String line = null;
		while((line = reader.readLine()) != null){
			List<String> pair = Arrays.asList(line.split("\t"));

			String lable1 = pair.get(0).toLowerCase();
			String lable2 = pair.get(1).toLowerCase();
			
			double score = textPairsESA.esa.getScore(lable1, lable2);

			System.out.println(lable1 +"\t"+ lable2 + "\t" + score);

			buffer.append(pair.get(0) +"\t"+ pair.get(1) + "\t" + score + "\n");

		}
		BasicFileTools.writeFile(textPairsESA.resultsFilePath, buffer.toString());

	}
}
