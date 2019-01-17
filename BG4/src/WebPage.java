
import java.util.ArrayList;

public class WebPage {
	public String url;
	public String title;
	public WordCounter counter;
	public double score=0;
	
	public WebPage(String title,String url) {
		this.url=url;
		this.title=title;
		this.counter=new WordCounter(url);
	}
	
	public void setScore(ArrayList<Keyword>keywords) throws Exception {
		//this.score=0;
		
		for(Keyword k: keywords) {
			this.score+= counter.countKeyword(k.name)*k.weight;
		}
	}

}

