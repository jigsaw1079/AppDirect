package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import util.Constants;

import com.google.common.base.Strings;

public class Company {
	private static final Logger LOGGER = Logger.getLogger(Company.class);
	private String country;
	private String name;
	private String phone;
	private String uuid;
	private String website;
	private String email;
	public Company(String country, String name, String phone, String uuid,
			String website, String email) {
		super();
		this.country = country;
		this.name = name;
		this.phone = phone;
		this.uuid = uuid;
		this.website = website;
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public static String createAccountIfNotExist(Node clientNode, Connection conn) throws SQLException {
		if(null == clientNode) return "";
		String uuid = clientNode.selectSingleNode(Constants.FIELD_UUID).getText();
		String country = clientNode.selectSingleNode(Constants.FIELD_COUNTRY).getText();
		String email = clientNode.selectSingleNode(Constants.FIELD_EMAIL).getText();
		String name = clientNode.selectSingleNode(Constants.FIELD_NAME).getText();
		String phone = clientNode.selectSingleNode(Constants.FIELD_PHONE).getText();
		String website = clientNode.selectSingleNode(Constants.FIELD_WEBSITE).getText();
		if(!Strings.isNullOrEmpty(uuid)) {
			String sql = "SELECT uuid FROM company WHERE uuid='" + uuid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.isBeforeFirst()) return uuid;
			sql = "INSERT INTO company(uuid,country,email,name,phone,website) VALUES(?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uuid);
			pstmt.setString(2, country);
			pstmt.setString(3, email);
			pstmt.setString(4, name);
			pstmt.setString(5, phone);
			pstmt.setString(6, website);
			//uuid,country,email,name,phone,website
			LOGGER.debug(pstmt.toString());
			pstmt.execute();
			pstmt.close();
		}
		return uuid;
	}	
}
