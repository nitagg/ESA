package ie.deri.nlp.esa.reader.wikiarticle;


public class WikiArticle {

	private String content = null;
	private String title = null;	
	private String entities = null;
	
	
	public WikiArticle(String contennt, String title){
		this.content = contennt;
		this.title = title;
	}
	
	public WikiArticle(String contennt, String title, String entities){
		this.content = contennt;
		this.title = title;
		this.entities = entities;
	}
	
	public String getEntities() {
		return entities;
	}

	public void setEntities(String entities) {
		this.entities = entities;
	}

	public String getContent() {
		return content;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
