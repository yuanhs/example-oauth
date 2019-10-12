

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class OAuthCallBackServlet
 */
@WebServlet("/OAuthCallBackServlet")
public class OAuthCallBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String CLIENT_ID="0e29b6e0859794dd1617c73aa7bbdd40741cff4ec270f8322e94458f75e1515e";
	private static final String CLIENT_SECRET="1f68f389b52a05d9e22f87c6a5e0250c155ef8b9d70950240831f65e97a9a8d6";
	private static final String REDIRECT_URI = "http://172.20.10.3:8080/oauth-test/OAuthCallBackServlet";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OAuthCallBackServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");
//		String state = request.getParameter("state");
		
		
		System.out.println("auth code=" + code);
//		System.out.println("auth state=" + state);
		
		StringBuffer sbUrl = new StringBuffer();
		sbUrl.append("https://gitlab.peakor.com/oauth/token?");
		sbUrl.append("client_id=" + CLIENT_ID);
		sbUrl.append("&client_secret=" + CLIENT_SECRET);
		sbUrl.append("&grant_type=authorization_code");
		sbUrl.append("&code=" + code);
		sbUrl.append("&redirect_uri=" + REDIRECT_URI);

		System.out.println("get token request url=" + sbUrl.toString());
		
		HttpClient httpclient = new DefaultHttpClient();
		httpclient = WebClientDevWrapper.wrapClient(httpclient);

		HttpPost httppost = new HttpPost(sbUrl.toString());
		HttpResponse httpResponse = httpclient.execute(httppost);
		int statusCode =  httpResponse.getStatusLine().getStatusCode();
		System.out.println("httpResponse status code :" +statusCode);
        StringBuilder sb = new StringBuilder();
		if (statusCode == 200) {
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));   
		        String line = null;
		        
		        try {
					while ((line = reader.readLine()) != null) {   
						sb.append(line);   
					}
		        } catch (IOException e) {   
					e.printStackTrace();
				} finally {
					try {
						instream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		        
			}
			
		}

		System.out.println("response : " + sb.toString());
		
		
		ObjectMapper mapper = new ObjectMapper(); 
		Map<String, String> mp = mapper.readValue(sb.toString(), new TypeReference<Map<String, String>>(){});
		String accessToken = mp.get("access_token");
		System.out.println("access_token=" + accessToken);
		
		
		
//		response.sendRedirect("index.jsp");

		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
