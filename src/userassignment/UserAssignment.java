package userassignment;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Individual;
import model.XmlResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;

import subscription.OrderStatusChange;
import util.AppUtil;
import util.Constants;
import util.H2DBUtil;

/**
 * Servlet implementation class UserAssignment
 */
@WebServlet("/UserAssignment")
public class UserAssignment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(UserAssignment.class);   
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAssignment() {
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
		if(AppUtil.isUserAssignment(subDoc)) {
			try {
				conn = H2DBUtil.getInstance().getConnection();
				conn.setAutoCommit(false);
				Node payloadNode = subDoc.selectSingleNode(Constants.EVENT_FIELD_PAYLOAD+"/"+Constants.FIELD_ACCOUNT);
				Node creatorNode = subDoc.selectSingleNode(Constants.EVENT_FIELD_CREATOR);
				Individual.assignUser(payloadNode, creatorNode, conn);
				conn.commit();
				xmlres.success = true;
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

}
