package model;

import org.dom4j.Document;

import util.AppUtil;

public class XmlResponse {
	public boolean success;
	public String message;
	public String accountIdentifier;
	public String errorCode;
	
	public Document toXmlDocument() {
		return AppUtil.createResponseXml(success, accountIdentifier, errorCode, message);
	}
	
	public XmlResponse(boolean success, String message,
			String accountIdentifier, String errorCode) {
		super();
		this.success = success;
		this.message = message;
		this.accountIdentifier = accountIdentifier;
		this.errorCode = errorCode;
	}

	public XmlResponse() {
		this(false, "", "dummy-account", "UNKOWN_ERROR");
	}
}
