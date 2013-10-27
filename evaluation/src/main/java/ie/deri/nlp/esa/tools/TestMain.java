package ie.deri.nlp.esa.tools;

import java.io.BufferedReader;
import java.io.IOException;

import ie.deri.nlp.esa.core.utils.BasicFileTools;

public class TestMain {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = BasicFileTools.getBufferedReaderFile(args[0]);
		int totalLines = Integer.parseInt(args[1]);
		String line = null;
		int count = 1;

		while((line = reader.readLine()) != null){
			System.out.println(line);
			count++;
			if(count > totalLines)
				break;
		}
	}
}
