package subscription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import util.H2DBUtil;

import model.Company;
import model.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetAllOrders implements RequestHandler {
	private static final Logger LOGGER = Logger.getLogger(GetAllOrders.class);
	private static final String SQL_QUERY = "SELECT * FROM orders WHERE com_uuid=?";
	private static ObjectMapper mapper = new ObjectMapper();

	@Override
	public String process(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String uuid = request.getParameter("uuid");
		String ret = "[]";
		Connection conn = null;
		try {
			conn = H2DBUtil.getInstance().getConnection();
			LOGGER.debug("get all order for : " + uuid);
			List<Order> orders = Order.getAllOrder(conn, uuid);
			ret = mapper.writeValueAsString(orders);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			H2DBUtil.closeDBConnection(conn);
			return ret;
		}
	}
}
