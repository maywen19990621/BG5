
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
//
//import javax.swing.text.Document;
//import javax.swing.text.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.IOException;

public class GoogleQuery {
	public String searchKeyword;
	public String url;
	public String content;
	private ArrayList<result> searchR=new ArrayList<result>();
	
	public GoogleQuery(String searchKeyword){
		this.searchKeyword = searchKeyword;
		this.url = "https://www.google.com.tw/search?q=" +searchKeyword + "+實習+政大+徵才";
	}
	
	private String fetchContent() throws IOException {
		String retVal = "";
		URL urlStr = new URL(this.url);
		URLConnection connection = urlStr.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		connection.connect();
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inReader = new InputStreamReader(inputStream,"UTF8");
		BufferedReader bf = new BufferedReader(inReader);
		String line = null;
		while((line = bf.readLine()) != null) {
			retVal += line;
		}
		return retVal;
	}
	
	public void query() throws IOException{
		if(this.content == null) {
			this.content = fetchContent();
		}
		Document document = Jsoup.parse(this.content);
		Elements lis =  document.select("div.g");
		for(Element li : lis) {
			try {
				Element h3 = li.select("h3.r").get(0);
				String title = h3.text();
				String ding = li.toString();
				String citeUrl = ding.substring(ding.indexOf("http"), ding.indexOf("&amp"));
				URLEncodeDecode encode=new URLEncodeDecode();
				String decodedURL = encode.decode(citeUrl);
				result result =new result(title,decodedURL);
				searchR.add(result);
				
			} catch(IndexOutOfBoundsException e) {
				
			}
		}
	}

	public ArrayList<result> getSearchR() {
		return searchR;
	}
	
	

}
