package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import util.Constants;

import com.google.common.base.Strings;

public class Order {
	private static final Logger LOGGER = Logger.getLogger(Order.class);

	private static final String insertItemSql = "INSERT INTO order_item VALUES(?,?,?)";
	private static final String updateItemSql = "UPDATE order_item SET quantity=? WHERE accountId=? AND unit=?";
	private static class Item {
		private String unit;
		private int quantity;
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public Item(String unit, int quantity) {
			super();
			this.unit = unit;
			this.quantity = quantity;
		}
		@Override
		public String toString() {
			return "Item [unit=" + unit + ", quantity=" + quantity + "]";
		}
		
	}
	private String accountId;
	private String edition;
	private String pricingDuration;
	private long lastModifiedTime;
	private List<Item> items;
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	private String creUuid;
	private String status;
	
	public static final String ACTIVE = "active";
	public static final String SUSPENDED = "suspended";
	public static final String CANCELED = "canceled";
	public static final String FREE_TRIAL = "free_trial";
	public static final String FREE_TRIAL_EXPIRED = "free_trial_expired";
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreUuid() {
		return creUuid;
	}
	public void setCreUuid(String creUuid) {
		this.creUuid = creUuid;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public String getPricingDuration() {
		return pricingDuration;
	}
	public void setPricingDuration(String pricingDuration) {
		this.pricingDuration = pricingDuration;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "Order [accountId=" + accountId + ", edition=" + edition
				+ ", pricingDuration=" + pricingDuration
				+ ", lastModifiedTime=" + lastModifiedTime + ", items=" + items
				+ ", creUuid=" + creUuid + ", status=" + status + "]";
	}
	public Order(String accountId, String creUuid, long lastModifiedTime, 
			String edition, String pricingDuration, String status) {
		super();
		this.accountId = accountId;
		this.edition = edition;
		this.pricingDuration = pricingDuration;
		this.lastModifiedTime = lastModifiedTime;
		this.creUuid = creUuid;
		this.status = status;
		this.items = new LinkedList<Item>();
	}
	public List<Item> getItems() {
		return items;
	}
	
	public void addItem(String unit, int quantity) {
		this.items.add(new Item(unit, quantity));
	}
	
	public static void createOrder(Node orderDoc, String com_uuid, String cre_uuid, long curTime, Connection conn) throws SQLException {
		if(null == orderDoc || null == conn || Strings.isNullOrEmpty(cre_uuid) || Strings.isNullOrEmpty(com_uuid)) {
			return;
		}
		String edition = orderDoc.selectSingleNode(Constants.FIELD_EDITION).getText();
		String pricing = orderDoc.selectSingleNode(Constants.FIELD_PRICING).getText();
		List<Node> items = orderDoc.selectNodes(Constants.FIELD_ITEM);
		String accountId = com_uuid + "+" + curTime;
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO orders(accountId, cre_uuid, lastModifiedTime, edition, pricingDuration, status) VALUES(?,?,?,?,?,?)");
			pstmt.setString(1, accountId);
			pstmt.setString(2, cre_uuid);
			pstmt.setLong(3, curTime);
			pstmt.setString(4, edition);
			pstmt.setString(5, pricing);
			pstmt.setString(6, ACTIVE);
			LOGGER.debug(pstmt.toString());
			pstmt.execute();
			PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO order_item(accountId,unit,quantity) VALUES(?,?,?)");
			pstmt2.setString(1, accountId);
			for(Node item : items) {
				int quantity = Integer.parseInt(item.selectSingleNode(Constants.FIELD_QUANTITY).getText());
				String unit = item.selectSingleNode(Constants.FIELD_UNIT).getText();
//				if("USER".equalsIgnoreCase(unit)) {
//					pstmt.setInt(7, quantity);
//				} else if("MEGABYTE".equalsIgnoreCase(unit)) {
//					pstmt.setInt(8, quantity);
//				}
				pstmt2.setString(2, unit);
				pstmt2.setInt(3, quantity);
				LOGGER.debug(pstmt2.toString());
				pstmt2.execute();
			}
			pstmt2.close();
			pstmt.close();
	}
	
	public static void changeOrder(Node payloadNode, long curTime, Connection conn) throws SQLException {
		if(null == payloadNode || null == conn) return;
		String accountIdentifier = payloadNode.selectSingleNode(Constants.FIELD_ACCOUNT_IDENTIFIER).getText();
		if(Strings.isNullOrEmpty(accountIdentifier)) return;
		LOGGER.debug("accountIdentifier: " + accountIdentifier);
		StringBuilder sql = new StringBuilder("UPDATE orders SET ");
		Node editionNode = payloadNode.selectSingleNode(Constants.FIELD_ORDER+"/"+Constants.FIELD_EDITION);
		if(null != editionNode) {
			sql.append("edition='").append(editionNode.getText()).append("'");
		}
		Node pricingNode = payloadNode.selectSingleNode(Constants.FIELD_ORDER+"/"+Constants.FIELD_EDITION);
		if(null != pricingNode) {
			sql.append(",pricingDuration='").append(pricingNode.getText()).append("'");
		}
		sql.append(",lastModifiedTime=").append(curTime);
		sql.append(" WHERE accountId=?");
		PreparedStatement pstmt = conn.prepareStatement(sql.toString());
		pstmt.setString(1,  accountIdentifier);
		LOGGER.debug(pstmt.toString());
		pstmt.execute();
		pstmt.close();
		List<Node> items = payloadNode.selectNodes(Constants.FIELD_ORDER+"/"+Constants.FIELD_ITEM);
		for(Node itemNode : items) {
			int quantity = Integer.parseInt(itemNode.selectSingleNode(Constants.FIELD_QUANTITY).getText());
			String unit = itemNode.selectSingleNode(Constants.FIELD_UNIT).getText();
			
			PreparedStatement pstmt2 = conn.prepareStatement(updateItemSql);
			pstmt2.setInt(1, quantity);
			pstmt2.setString(2, accountIdentifier);
			pstmt2.setString(3, unit);
			LOGGER.debug(pstmt2.toString());
			int affected = pstmt2.executeUpdate();
			pstmt2.close();
			if(affected == 0) {
				PreparedStatement pstmt3 = conn.prepareStatement(insertItemSql);
				pstmt3.setString(1, accountIdentifier);
				pstmt3.setString(2, unit);
				pstmt3.setInt(3, quantity);
				LOGGER.debug(pstmt3.toString());
				pstmt3.execute();
				pstmt3.close();
			}
		}
	}
	
	public static List<Order> getAllOrder(Connection conn, String uuid) throws SQLException {
		List<Order> orders = new LinkedList<Order>();
		if(null == conn) return orders;
			Statement stmt = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM orders WHERE accountId LIKE '"+uuid+"%'");
			while(rs.next()) {
				ResultSet rs2 = stmt2.executeQuery("SELECT * FROM order_item WHERE accountId LIKE '"+uuid+"%'");
				Order order = new Order(
					rs.getString("accountId"), 
					rs.getString("cre_uuid"), 
					rs.getLong("lastModifiedTime"), 
					rs.getString("edition"), 
					rs.getString("pricingDuration"), 
					rs.getString("status")
				);
				while(rs2.next()) {
					order.addItem(rs2.getString("unit"), rs2.getInt("quantity"));
				}
				orders.add(order);
				LOGGER.debug(order);
			}
			stmt2.close();
			stmt.close();
			return orders;
	}

}
