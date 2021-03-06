package ie.deri.nlp.esa.reader.wikiarticle;




import ie.deri.nlp.esa.core.utils.BasicFileTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;


public class WikiXMLFileReader {

	private static final Pattern digitPattern = Pattern.compile("^\\d+$");
	private static final Pattern yearPattern = Pattern.compile("\\s*[12][0-9]{3}\\s*");
	//	private static final Pattern linkPattern = Pattern.compile("\\[\\[(.*?)]]");

	private String xmlFilePath = null;	
	private final int MinArticleLength = 200;
	private int artcleTitleWeight = 3;


	public WikiXMLFileReader(String xmlFilePath){
		if(isXMLFile(xmlFilePath)) 		
			this.xmlFilePath = xmlFilePath;
	}

	public boolean setArtcleTitleWeight(final int artcleTitleWeight){
		this.artcleTitleWeight = artcleTitleWeight;
		return true;
	}
	
	private boolean isXMLFile(String xmlFilePath) {
		if(xmlFilePath.endsWith(".xml"))
			return true;
		System.err.println("File not found or File is not .xml file");		
		return false;	
	}

	
	public Iterator<WikiArticle> getWikiArticleIter() throws IOException{
		return new XMLFileWikiArticleIter(BasicFileTools.getBufferedReaderFile(xmlFilePath));
	}


	private class XMLFileWikiArticleIter implements Iterator<WikiArticle> {

		private BufferedReader reader;
		private WikiArticle article = null;
		
		
		public XMLFileWikiArticleIter(BufferedReader reader) throws IOException {
			this.reader = reader;
			toNext();
		}

		private String processArticleContent(String articleContent, String articleTitle){
			for(int count = 0; count < artcleTitleWeight; count++)
				articleContent = articleContent + " " + articleTitle;
			
			return articleContent;
		}
		
		public void toNext() throws IOException{
			tryNext();
			if(article!=null)
				while(article.getContent() == null){
					tryNext();
					if(article==null)
						break;
				}
		}
		public void tryNext() throws IOException {
			String line = null;
			String articleTitle = null;
			String articleContent = null;
			boolean article = false;
			while((line=reader.readLine())!=null) {	
				if((line.contains("<title>")) && (line.contains("</title>")))
					articleTitle = line.substring(line.indexOf("<title>") +"<title>".length(), line.indexOf("</title>"));				
				if(line.contains("<page"))
					article = true;
				if(article)
					articleContent = articleContent +"\n"+line;				
				if(line.contains("</page>")){
					article = false;
					break;
				}
			}
			
			if(articleTitle !=null){
				String cleanArticleContent = cleanArticleContent(articleContent, articleTitle);
				if(cleanArticleContent == null)
					this.article = new WikiArticle(cleanArticleContent, articleTitle);
				else
					this.article = new WikiArticle(this.processArticleContent(cleanArticleContent, articleTitle), articleTitle);
//				this.article = new WikiArticle(cleanArticleContent + articleTitle +" "+articleTitle +" "+articleTitle, articleTitle);
			}
			else{
				this.article = null;
			}
		}

		public boolean hasNext() {
			return article != null;
		}

		public WikiArticle next() {
			if(article != null) {
				WikiArticle currentArticle = article ;
				try {
					toNext();				
				} catch(Exception x) {
					throw new RuntimeException(x);
				}
				return currentArticle;
			} else {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}		

		// return true if title is a absolute article not any nameSpace type e.g. Category 
		private Boolean isNameSpace(String title){
			Matcher matcher = digitPattern.matcher(title);
			if(matcher.find())
				return true;

			matcher = yearPattern.matcher(title);
			if(matcher.find())
				return true;

			for(String key: WikiNamespaces.values){
				if(title.toLowerCase().contains(key.toLowerCase()))
					return true;
			}
			return false;
		}

		private String cleanArticleContent(String content, String title) {
			//			System.out.println("title: "+title);
			if(isNameSpace(title))
				return null;
			if(title.toLowerCase().contains("(disambiguation)"))
				return null;
			if(title.toLowerCase().startsWith("list"))
				return null;

			BufferedReader reader = new BufferedReader(new StringReader(content));
			StringBuilder doc = null;
			String s;
			try {
				while ((s = reader.readLine()) != null) {
					if (s.contains("<text")) 
						doc = new StringBuilder();
					else if (s.contains("</text>")) {
						if (doc != null) {
							String cleanWiki = cleanText(doc.toString());
							doc = null;
							// remove all redirect articles
							if(cleanWiki.toLowerCase().contains("#REDIRECT".toLowerCase()))
								return null;							

							HashSet<String> uniqueTokens = new HashSet<String>(Arrays.asList(cleanWiki.split(" ")));
							//							
							if(uniqueTokens.size() < MinArticleLength)
								return null;

							return cleanWiki.trim();
						}
					}
					if (doc != null) 
						doc.append(s).append(System.getProperty("line.separator"));					
				}
			} catch (IOException e) {				
				e.printStackTrace();
			}

			return null;
		}

		private String cleanText(String s) {
			return StringEscapeUtils.unescapeHtml3(s.replaceAll("\\{\\{[^\\}]*\\}\\}", " ").replaceAll("[='\\[\\]\\|]", " ").replaceAll("^[\\*\\:#]+", " ").replaceAll("<[^>]*>", " "));
		}	
	}

	private static class WikiNamespaces {				
		private static String [] list = {"Media:","Special:","Talk:","User:","User_talk:","Wikipedia:","Wikipedia_talk:",
			"File:","File_talk:","MediaWiki:","MediaWiki_talk:","Template:","Template_talk:","Help:","Help_talk:",
			"Category:","Category_talk:","Portal:","Portal_talk:","Book:","Book_talk:"};

		public static HashSet<String> values = new HashSet<String>(Arrays.asList(list));
	}

	public static void main(String[] args) {
		String xmlFilePath = "/Users/nitagg/deri/eclipse/data/Wiki_dump/testWiki.xml";
		WikiXMLFileReader reader = new WikiXMLFileReader(xmlFilePath);
		Iterator<WikiArticle> articleIter = null;
		try {
			articleIter = reader.getWikiArticleIter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while(articleIter.hasNext())
			System.out.println(articleIter.next().getTitle());

	}

}
