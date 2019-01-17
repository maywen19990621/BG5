import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
@WebServlet("/test")
public class test extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public test() {

		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//add keywords
		String k = request.getParameter("keyword");
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		keywords.add(new Keyword(k, 10));
		keywords.add(new Keyword("應屆畢業", 1));
		keywords.add(new Keyword("研究所", 1));
		keywords.add(new Keyword("大安區", 2));
		keywords.add(new Keyword("寒假", 3));
		keywords.add(new Keyword("暑假", 3));
		keywords.add(new Keyword("長期", 3));
		keywords.add(new Keyword("短假", 3));
		keywords.add(new Keyword("文山區", 4));
		keywords.add(new Keyword("新店區", 4));
		keywords.add(new Keyword("實習", 5));
		keywords.add(new Keyword("政大", 5));
		keywords.add(new Keyword("大學生",5));
		keywords.add(new Keyword("徵才", 5));
		keywords.add(new Keyword("人力銀行", 5));
		
		//search in google and put all the results' urls in a searchR array
		GoogleQuery googleQuery = new GoogleQuery(k);
		ArrayList<result> searchR = new ArrayList<result>();
		googleQuery.query();
		searchR = googleQuery.getSearchR();

		//for all result
		//find the children from their hyperlink
		ArrayList<WebNode> nodelist = new ArrayList<WebNode>();
		for (int a = 0; a < searchR.size(); a++) {
			String url = searchR.get(a).url;
			String title=searchR.get(a).title;
			int x = url.indexOf("//") + 2;
			int y = url.indexOf("/", x);
			String shorturl = url.substring(0, y);
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e1) {
			}

			if (doc != null) {
				Element body = doc.body();
				Elements es = body.select("a");
				ArrayList<String> children = new ArrayList<String>();

				for (Iterator it = es.iterator(); it.hasNext();) {
					Element e = (Element) it.next();
					String u = e.attr("href");
					if (u.startsWith("http")) {
						children.add(u);
					} else if (u.startsWith("/")) {
						u = shorturl + u;
						children.add(u);
					}
				}

				
				//create a webpage and a webtree of the result's url.
				WebPage rootPage = new WebPage(title,url);
				WebTree tree = new WebTree(rootPage);

				// add all children into the webnode of the webpage
				int i = 0;
				while (i < children.size()) {
					tree.root.addChild(new WebNode(new WebPage(title,children.get(i))));
					i++;
				}
				//calculate the score of the webnode, which is the webpage score plus the children scores
				tree.root.calNodeScore(keywords);
				//put the webnode of the result's url into a nodelist in order to rank its score later
				nodelist.add(tree.root);
			} else {
				WebPage rootPage = new WebPage(title,url);
				WebTree tree = new WebTree(rootPage);
				tree.root.nullnodescore(keywords);
				nodelist.add(tree.root);
			}

		}

		//rank all the webnodes by their scores
		int low = 0;
		int high = nodelist.size() - 1;
		Quicksort.quickSort(nodelist, low, high);
		PrintWriter writer = response.getWriter();

		// build HTML code
		String htmlRespone = "<html>\r\n " + "<head>\r\n" + " <meta charset=\"UTF-8\" />\r\n"
				+ " <title>UMEB</title>\r\n" + "<style>\r\n" + ".Website {\r\n" + "  background-color:#FFE4E1;\r\n"
				+ "  color:white;\r\n" + "  margin: 10px;\r\n" + "  padding: 10px;\r\n" + "\r\n" + "} \r\n"
				+ "</style>\r\n" + "</head>" + "<body>\r\n" + "\r\n" + "<font face=\"Arial\" > "
				+ "<br><form name=\"search result\" action=\"test\" method=\"post\"> <div align=center>\r\n"
				+ "<input type=\"text\" name=\"keyword\" width=200px/>\r\n" + "<input type=\"submit\" value=\"Submit\" /></div>\r\n"
				+ "</font><br>";

		for (int i = nodelist.size() - 1; i >= 0; i--) {
			htmlRespone += "<div class=\"Website\">\r\n"
					+ " <font face=\"Arial\" color=\"#E9AC38\" size=\"5\"><a href=\"" + nodelist.get(i).webPage.url
					+ "\">" + nodelist.get(i).title + "</a></font>\r\n"
					+ "  <div><font face=\"Arial\" color=\"#4EB198\" size=\"2\">" + nodelist.get(i).nodeScore
					+ nodelist.get(i).webPage.url + "</font></div>\r\n" + "<p>"
					+ "</p>" + " </div>";
		}

		htmlRespone += "</body>\r\n" + "</html>";

		writer.println(htmlRespone);

	}

}
