package subscription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import util.H2DBUtil;

import model.Company;
import model.Individual;

public class GetAllUsers implements RequestHandler {
	private static final Logger LOGGER = Logger.getLogger(GetAllUsers.class);
	private static final String SQL_QUERY = "SELECT * FROM company";
	private static ObjectMapper mapper = new ObjectMapper();
	@Override
	public String process(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<Company> users = new LinkedList<>();
		String ret = "";
		Connection conn = null;
		try {
			conn = H2DBUtil.getInstance().getConnection();
			if(null != conn) {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SQL_QUERY);
				while(rs.next()) {
					users.add(new Company(
						rs.getString("country"), 
						rs.getString("name"), 
						rs.getString("phone"), 
						rs.getString("uuid"),
						rs.getString("website"),
						rs.getString("email")
					));
				}
			}
			ret = mapper.writeValueAsString(users);
			LOGGER.debug(ret);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			H2DBUtil.closeDBConnection(conn);
			return ret;
		}
	}

}
