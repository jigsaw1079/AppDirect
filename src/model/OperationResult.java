package model;

public class OperationResult {
	public boolean success;
	public String accountIdentifier;
	public String errorCode;
	public String message;
	
	public OperationResult(){}
	public OperationResult(
			boolean success,
			String accountIdentifier,
			String errorCode,
			String message) {
		this.success = success;
		this.accountIdentifier = accountIdentifier;
		this.errorCode = errorCode;
		this.message = message;
	}
	public static OperationResult getDefaultStatus() {
		return new OperationResult(true, "dummy_account", "", "default message");
	}
}
