package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import util.Constants;

import com.google.common.base.Strings;

public class Individual {
	private static final Logger LOGGER = Logger.getLogger(Individual.class);
	private String uuid;
	private String fname;
	private String lname;
	private String language;
	private String email;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Individual(String uuid, String fname, String lname, String language,
			String email) {
		super();
		this.uuid = uuid;
		this.fname = fname;
		this.lname = lname;
		this.language = language;
		this.email = email;
	}
	
	public static String createIndividualIfNotExist(Node indivdualNode, Connection conn) throws SQLException {
		if(null == indivdualNode || null == conn) return "";
		String uuid = indivdualNode.selectSingleNode(Constants.FIELD_UUID).getText();
		String fname = indivdualNode.selectSingleNode(Constants.FIELD_FNAME).getText();
		String lname = indivdualNode.selectSingleNode(Constants.FIELD_LNAME).getText();
		String lang = indivdualNode.selectSingleNode(Constants.FIELD_LANGUAGE).getText();
		String email = indivdualNode.selectSingleNode(Constants.FIELD_EMAIL).getText();
		if(!Strings.isNullOrEmpty(uuid)) {
			String sql = "SELECT uuid FROM individual WHERE uuid='" + uuid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.isBeforeFirst()) return uuid;
			sql = "INSERT INTO individual(uuid,fname,lname,language,email) VALUES(?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uuid);
			pstmt.setString(2, fname);
			pstmt.setString(3, lname);
			pstmt.setString(4, lang);
			pstmt.setString(5, email);
			//uuid,fname,lname,language,email
			LOGGER.debug(pstmt.toString());
			pstmt.execute();
			pstmt.close();
		}
		return uuid;
	}
	
	public static void assignUser(Node payloadNode, Node creatorNode, Connection conn) throws SQLException {
		if(null == payloadNode || null == creatorNode || null == conn) return;
		String cre_uuid = creatorNode.selectSingleNode(Constants.FIELD_UUID).getText();
		String accountId = payloadNode.selectSingleNode(Constants.FIELD_ACCOUNT + "/" + Constants.FIELD_ACCOUNT_IDENTIFIER).getText();
		List<Node> attrEntryNodes = payloadNode.selectNodes(Constants.FIELD_USER + "/" + Constants.FIELD_ATTR + "/" + Constants.FIELD_ENTRY);
		String usr_uuid = payloadNode.selectSingleNode(Constants.FIELD_USER + "/" + Constants.FIELD_UUID).getText();
		String sql = "INSERT INTO order_assignment VALUES(?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, accountId);
		pstmt.setString(2, cre_uuid);
		pstmt.setString(3, usr_uuid);
		for(Node entryNode : attrEntryNodes) {
			pstmt.setString(4, entryNode.selectSingleNode(Constants.FIELD_ENTRY_KEY).getText());
			pstmt.setString(5, entryNode.selectSingleNode(Constants.FIELD_ENTRY_VALUE).getText());
			LOGGER.debug(pstmt.toString());
			pstmt.execute();
		}
	}
}
