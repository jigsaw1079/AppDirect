package subscription;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Order;
import model.XmlResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;

import com.google.common.base.Strings;

import util.AppUtil;
import util.Constants;
import util.H2DBUtil;

/**
 * Servlet implementation class OrderChange
 */
@WebServlet("/OrderChange")
public class OrderChange extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String sql = "UPDATE orders SET edition=?, pricingDuration=?, usercount=?, flow_mb=? WHERE com_uuid=? AND creationTime=?";
	private static final Logger LOGGER = Logger.getLogger(OrderChange.class);   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderChange() {
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
		Document subDoc = AppUtil.getXMLEvent(urlStr);
		Connection conn = null;
		XmlResponse xmlres = new XmlResponse();
		if(AppUtil.isSubscriptionChange(subDoc)) {
			try {
				conn = H2DBUtil.getInstance().getConnection();
				conn.setAutoCommit(false);
				Node payloadNode = subDoc.selectSingleNode(Constants.EVENT_FIELD_PAYLOAD+"/"+Constants.FIELD_ACCOUNT);
				Order.changeOrder(payloadNode, System.currentTimeMillis(), conn);
				conn.commit();
				xmlres.success = true;
				xmlres.message = "subscription is changed!";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				xmlres.success = false;
				xmlres.message = "Something wrong with DB!";
			} finally {
				H2DBUtil.closeDBConnection(conn);
			}
		}
		pw.print(xmlres.toXmlDocument().asXML());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
//	protected void changeOrder(Node payloadNode, long curTime, Connection conn, XmlResponse xmlres) {
//		if(null == payloadNode || null == conn) return;
//		String accountIdentifier = payloadNode.selectSingleNode(Constants.FIELD_ACCOUNT_IDENTIFIER).getText();
//		if(Strings.isNullOrEmpty(accountIdentifier)) return;
//		String[] strs = accountIdentifier.split("\\+");
//		xmlres.accountIdentifier = accountIdentifier;
//		LOGGER.debug("accountIdentifier: " + accountIdentifier);
//		StringBuilder sql = new StringBuilder("UPDATE orders SET ");
//		Node editionNode = payloadNode.selectSingleNode(Constants.FIELD_ORDER+"/"+Constants.FIELD_EDITION);
//		if(null != editionNode) {
//			sql.append("edition='").append(editionNode.getText()).append("'");
//		}
//		Node pricingNode = payloadNode.selectSingleNode(Constants.FIELD_ORDER+"/"+Constants.FIELD_EDITION);
//		if(null != pricingNode) {
//			sql.append(",pricingDuration='").append(pricingNode.getText()).append("'");
//		}
//		String pricingDuration = payloadNode.selectSingleNode(Constants.FIELD_ORDER+"/"+Constants.FIELD_PRICING).getText();
//		List<Node> items = payloadNode.selectNodes(Constants.FIELD_ORDER+"/"+Constants.FIELD_ITEM);
//		for(Node item : items) {
//			int quantity = Integer.parseInt(item.selectSingleNode(Constants.FIELD_QUANTITY).getText());
//			String unit = item.selectSingleNode(Constants.FIELD_UNIT).getText();
//			if("USER".equalsIgnoreCase(unit)) {
//				sql.append(",usercount=").append(unit);
//			} else if("MEGABYTE".equalsIgnoreCase(unit)) {
//				sql.append(",flow_mb=").append(unit);
//			}
//		}
//		sql.append(",lastModifiedTime=").append(curTime);
//		sql.append(" WHERE com_uuid=? AND creationTime=?");
//		try {
//			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
//			pstmt.setString(1,  strs[0]);
//			pstmt.setLong(2, Long.parseLong(strs[1]));
//			pstmt.execute();
//			pstmt.close();
//			LOGGER.debug(pstmt.toString());
//			xmlres.success = true;
//			xmlres.message = "subscription is changed!";
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			xmlres.success = false;
//			xmlres.message = "Something wrong with DB!";
//			e.printStackTrace();
//		}
//	}
}
