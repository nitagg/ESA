package ie.deri.nlp.esa.core.utils;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class BasicUtils {

	public static String list2String(List<String> list){
		String concatanatedString = "";
		for(String item: list)
			concatanatedString = item + " "+ concatanatedString;

		return concatanatedString.trim();
	}

	public static List<String> map2SortedList(Map<String, Double> map){
		ArrayList<String> sortedList = new ArrayList<String>();
		ArrayList<Double> values = new ArrayList<Double>(map.values());
		Collections.sort(values);

		int count = values.size()-1;
		while(count >-1){
			Double val = values.get(count);
			for(String key: map.keySet()){
				if(val == map.get(key)){
					sortedList.add(key);
					break;
				}
			}
			count--;
		}
		return sortedList;
	}

}
