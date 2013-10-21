package ie.deri.nlp.esa.core.utils;


import java.util.Map;


public class Vector<X> {
  
	private Map<X, Double> vectorAsMap = null;

	public Vector() {
	}
	
	public Vector(Map<X, Double> vectorAsMap) {
		this.vectorAsMap = vectorAsMap;
	}

	public Map<X,Double> getVectorAsMap() {
		return vectorAsMap;
	}	

	
}
