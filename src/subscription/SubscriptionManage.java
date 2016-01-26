package subscription;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Order;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class SubscriptionManage
 */
@WebServlet("/SubscriptionManage")
public class SubscriptionManage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SubscriptionManage.class);
    
	private static final String OP_GET_ALL_USERS = "get_all_users";
	private static final String OP_GET_ALL_ORDERS = "get_all_orders";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubscriptionManage() {
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
		PrintWriter pw = response.getWriter();
		String op = request.getParameter("op");
		RequestHandler rh = null;
		if(OP_GET_ALL_USERS.equalsIgnoreCase(op)) {
			rh = new GetAllUsers();
		} else if(OP_GET_ALL_ORDERS.equalsIgnoreCase(op)) {
			rh = new GetAllOrders();
		}
		String ret = rh.process(request, response);
		LOGGER.debug(ret);
		pw.print(ret);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
