package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.common.base.Strings;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class AppUtil {
	private static final String APP_KEY = "appdirectcode-71211";
	private static final String APP_SECRET = "nl36D7sk3VUqVG0H";

	private static final String APP_DUMMY_KEY = "Dummy";
	private static final String APP_DUMMY_SECRET = "secret";
	
	public static final OAuthConsumer consumer = new DefaultOAuthConsumer(APP_KEY, APP_SECRET);
	public static final OAuthConsumer dummyConsumer = new DefaultOAuthConsumer(APP_DUMMY_KEY, APP_DUMMY_SECRET);
	
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	
	private static final SAXReader reader = new SAXReader();
	
	private static HttpURLConnection createSignedConnenction(String urlStr, String method) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			consumer.sign(conn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public static String getXMLEvent(String urlStr, String method) {
		HttpURLConnection conn = createSignedConnenction(urlStr, method);
		StringBuilder ret = new StringBuilder();
		if(null != conn) {
			try {
				conn.connect();
				int responseCode = conn.getResponseCode();
				if(responseCode == 200) {
					BufferedReader rd = new BufferedReader(
		                       new InputStreamReader(conn.getInputStream()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						ret.append(line);
					}
					rd.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		try {
//			URL url = new URL(urlStr);
//			conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod(method);
//			consumer.sign(conn);
//			conn.connect();
//			int responseCode = conn.getResponseCode();
//			if(responseCode == 200) {
//				BufferedReader rd = new BufferedReader(
//	                       new InputStreamReader(conn.getInputStream()));
//				String line = "";
//				while ((line = rd.readLine()) != null) {
//					ret.append(line);
//				}
//				rd.close();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAuthMessageSignerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAuthExpectationFailedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAuthCommunicationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return ret.toString();
	}
	
	public static Document getXMLEvent(final String urlStr) {
		Document document = null;
		if(Strings.isNullOrEmpty(urlStr)) return document;
		try {
			document = reader.read(new URL(urlStr));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}
	
	public static void sendXMLResponse(Document doc, String urlStr, String method)  {
		HttpURLConnection conn = createSignedConnenction(urlStr, method);
		if(null != conn) {
			try {
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(doc.asXML());
				wr.flush();
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Document createResponseXml(boolean success, String accountID, String errorCode, String msg) {
		Document doc = null;
	         doc = DocumentHelper.createDocument();
	         Element root = doc.addElement("result");
	         Element successElement = root.addElement("success");
	         successElement.addText(success ? "true" : "false");
	         if(!success) {
		         Element errorCodeElement = root.addElement("errorCode");
		         errorCodeElement.addText(errorCode);
	         }
        	 Element accountIDElement = root.addElement("accountID");
        	 accountIDElement.addText(accountID);
	         Element msgElement = root.addElement("message");
	         msgElement.addText(msg);
	         return doc;
	}
	
	public static boolean checkEventType(Document doc, String type) {
		if(null == doc || null == doc.selectSingleNode(Constants.EVENT_FIELD_TYPE)|| Strings.isNullOrEmpty(type)) return false;
		return type.equals(doc.selectSingleNode(Constants.EVENT_FIELD_TYPE).getText());
	}
	
	public static boolean isSubscriptionCancel(Document doc) {
		return checkEventType(doc, Constants.SUBSCRIPTION_CANCEL);
	}
	
	public static boolean isSubscriptionOrder(Document doc) {
		return checkEventType(doc, Constants.SUBSCRIPTION_ORDER);
	}
	
	public static boolean isSubscriptionChange(Document doc) {
		return checkEventType(doc, Constants.SUBSCRIPTION_CHANGE);
	}
	
	public static boolean isUserAssignment(Document doc) {
		return checkEventType(doc, Constants.USER_ASSIGNMENT);
	}
	
	public static boolean isUserUnassignment(Document doc) {
		return checkEventType(doc, Constants.USER_UNASSIGNMENT);
	}
}
