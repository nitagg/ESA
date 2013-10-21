package ie.deri.nlp.esa.reader.wikiarticle;


public class WikiArticle {

	private String content = null;
	private String title = null;	
	
	
	public WikiArticle(String contennt, String title){
		this.content = contennt;
		this.title = title;
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
