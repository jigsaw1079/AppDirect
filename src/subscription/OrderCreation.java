package subscription;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Company;
import model.Individual;
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
 * Servlet implementation class OrderCreation
 */
@WebServlet("/OrderCreation")
public class OrderCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(OrderCreation.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderCreation() {
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
		urlStr = "https://www.appdirect.com/rest/api/events/dummyOrder";
		LOGGER.debug("callback url: " + urlStr);
		PrintWriter pw = response.getWriter();
		//urlStr = "https://www.appdirect.com/api/integration/v1/events/dummyOrder";
		Document subDoc = AppUtil.getXMLEvent(urlStr);
		Connection conn = null;
		XmlResponse xmlres = new XmlResponse();
		if(AppUtil.isSubscriptionOrder(subDoc)) {
			try {
				conn = H2DBUtil.getInstance().getConnection();
				if(null != conn) {
					conn.setAutoCommit(false);
					String com_uuid = Company.createAccountIfNotExist(subDoc.selectSingleNode(Constants.EVENT_FIELD_PAYLOAD+"/"+Constants.FIELD_COMPANY), conn);
					String cre_uuid = Individual.createIndividualIfNotExist(subDoc.selectSingleNode(Constants.EVENT_FIELD_CREATOR), conn);
					long curTime = System.currentTimeMillis();
					Order.createOrder(subDoc.selectSingleNode(Constants.EVENT_FIELD_PAYLOAD+"/"+Constants.FIELD_ORDER), com_uuid, cre_uuid, curTime, conn);
					conn.commit();
					xmlres.success = true;
					xmlres.accountIdentifier = com_uuid + ":" + curTime;
					xmlres.message = "subscription is established!";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
	
//	protected void createOrder(Node orderDoc, String com_uuid, String cre_uuid, long curTime, Connection conn) {
//		if(null == orderDoc || null == conn || Strings.isNullOrEmpty(cre_uuid) || Strings.isNullOrEmpty(com_uuid)) {
//			return;
//		}
//		String edition = orderDoc.selectSingleNode(Constants.FIELD_EDITION).getText();
//		String pricing = orderDoc.selectSingleNode(Constants.FIELD_PRICING).getText();
//		List<Node> items = orderDoc.selectNodes(Constants.FIELD_ITEM);
//		try {
//			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO orders(com_uuid, cre_uuid, creationTime, lastModifiedTime, edition, pricingDuration, usercount, flow_mb, status) values(?,?,?,?,?,?,?,?,?)");
//			pstmt.setString(1, com_uuid);
//			//pstmt.setString(1, "dummy-account");
//			pstmt.setString(2, cre_uuid);
//			pstmt.setLong(3, curTime);
//			pstmt.setLong(4, curTime);
//			pstmt.setString(5, edition);
//			pstmt.setString(6, pricing);
//			for(Node item : items) {
//				int quantity = Integer.parseInt(item.selectSingleNode(Constants.FIELD_QUANTITY).getText());
//				String unit = item.selectSingleNode(Constants.FIELD_UNIT).getText();
//				if("USER".equalsIgnoreCase(unit)) {
//					pstmt.setInt(7, quantity);
//				} else if("MEGABYTE".equalsIgnoreCase(unit)) {
//					pstmt.setInt(8, quantity);
//				}
//			}
//			pstmt.setString(9, Order.ACTIVE);
//			System.out.println(pstmt.toString());
//			pstmt.execute();
//			pstmt.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
//	protected String createAccountIfNotExist(Node clientNode, Connection conn) {
//		if(null == clientNode) return "";
//		String uuid = clientNode.selectSingleNode(Constants.FIELD_UUID).getText();
//		if(!Strings.isNullOrEmpty(uuid)) {
//			String sql = "SELECT * FROM users WHERE uuid='" + uuid + "'";
//			try {
//				Statement stmt = conn.createStatement();
//				ResultSet rs = stmt.executeQuery(sql);
//				if(!rs.isBeforeFirst()) {
//					String country = clientNode.selectSingleNode(Constants.FIELD_COUNTRY).getText();
//					String email = clientNode.selectSingleNode(Constants.FIELD_EMAIL).getText();
//					String name = clientNode.selectSingleNode(Constants.FIELD_NAME).getText();
//					String phone = clientNode.selectSingleNode(Constants.FIELD_PHONE).getText();
//					String website = clientNode.selectSingleNode(Constants.FIELD_WEBSITE).getText();
//					sql = "INSERT INTO users values(?,?,?,?,?,?)";
//					PreparedStatement pstmt = conn.prepareStatement(sql);
//					pstmt.setString(1, uuid);
//					pstmt.setString(2, country);
//					pstmt.setString(3, email);
//					pstmt.setString(4, name);
//					pstmt.setString(5, phone);
//					pstmt.setString(6, website);
//					pstmt.execute();
//					pstmt.close();
//				}
//				stmt.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//			}
//		}
//		return uuid;
//	}
	
	
//	protected String createAccountIfNotExist(Node clientNode, Connection conn) {
//	protected OperationStatus createAccount(Document orderDoc) {
//		if(null == orderDoc) {
//			return OperationStatus.getDefaultStatus();
//		}
//		OperationStatus status = new OperationStatus();
//		Node payLoadNode = orderDoc.selectSingleNode(EVENT_FIELD_PAYLOAD);
//		if(null != payLoadNode) {
//
//			Node companyNode = payLoadNode.selectSingleNode(PAYLOAD_FIELD_COMPANY);
//			if(null == companyNode) {
//				String uuid = companyNode.selectSingleNode(COMPANY_FIELD_UUID).getText();
//				if()
//				String country = companyNode.selectSingleNode(COMPANY_FIELD_COUNTRY).getText();
//			}
//			
//			Node editionNode = payLoadNode.selectSingleNode(PAYLOAD_FIELD_ORDER + "/" + ORDER_FIELD_EDITION);
//			if(null != editionNode) {
//				String edition = editionNode.getText();
//				System.out.println(edition + " **************");
//			}
//			List<Node> itemNodes = payLoadNode.selectNodes(PAYLOAD_FIELD_ORDER + "/" + ORDER_FIELD_ITEM);
//			if(null != itemNodes) {
//				for(Node item : itemNodes) {
//					Node quanNode = item.selectSingleNode(ITEM_FIELD_QUANTITY);
//					Node unitNode = item.selectSingleNode(ITEM_FIELD_UNIT);
//					System.out.println(quanNode.getText() + " : " + unitNode.getText());
//				}
//			}
//		}
//		Node returnUrlNode = orderDoc.selectSingleNode(EVENT_FIELD_RETURNURL);
//		return status;
//	}
}
