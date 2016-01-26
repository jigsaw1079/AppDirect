package subscription;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.XmlResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;

import com.google.common.base.Strings;

import util.AppUtil;
import util.Constants;
import util.H2DBUtil;

/**
 * Servlet implementation class OrderCancelation
 */
@WebServlet("/OrderCancelation")
public class OrderCancelation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String sql = "UPDATE orders SET status='CANCEL', lastModifiedTime=? WHERE com_uuid=? AND creationTime=?";
	private static final Logger LOGGER = Logger.getLogger(OrderCancelation.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderCancelation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String urlStr = request.getParameter(Constants.REQ_FIELD_URL);
		LOGGER.debug("callback url: " + urlStr);
		PrintWriter pw = response.getWriter();
		//urlStr = "https://www.appdirect.com/rest/api/events/dummyCancel";
		Document subDoc = AppUtil.getXMLEvent(urlStr);
		Connection conn = null;
		XmlResponse xmlres = new XmlResponse();
		if(AppUtil.isSubscriptionCancel(subDoc)) {
			try {
				conn = H2DBUtil.getInstance().getConnection();
				Node payloadNode = subDoc.selectSingleNode(Constants.EVENT_FIELD_PAYLOAD+"/"+Constants.FIELD_ACCOUNT);
				cancelOrder(payloadNode, System.currentTimeMillis(), conn, xmlres);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				H2DBUtil.closeDBConnection(conn);
			}
		} else {
			xmlres.success = false;
		}
		pw.print(xmlres.toXmlDocument());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	protected void cancelOrder(Node payloadNode, long curTime, Connection conn, XmlResponse xmlres) {
		if(null == payloadNode || null == conn) return;
		String accountIdentifier = payloadNode.selectSingleNode(Constants.FIELD_ACCOUNT_IDENTIFIER).getText();
		if(Strings.isNullOrEmpty(accountIdentifier)) return;
		String[] strs = accountIdentifier.split("\\+");
		xmlres.accountIdentifier = accountIdentifier;
		LOGGER.debug("accountIdentifier: " + accountIdentifier);
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1,  curTime);
			pstmt.setString(2,  strs[0]);
			pstmt.setLong(3, Long.parseLong(strs[1]));
			pstmt.execute();
			pstmt.close();
			System.out.println(pstmt.toString());
			xmlres.success = true;
			xmlres.message = "subscription is cancelled!";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			xmlres.success = false;
			xmlres.message = "Something wrong with DB!";
			e.printStackTrace();
		}
	}
}
