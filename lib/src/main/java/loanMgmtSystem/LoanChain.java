package loanMgmtSystem;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.owlike.genson.Genson;

@Contract(name = "Loan_Management_System", info = @Info(title = "Loan Management System contract", description = "A Loan Management System example via chaincode", version = "0.0.1-SNAPSHOT"))

@Default
public class LoanChain implements ContractInterface {

	private final Genson genson = new Genson();
	
	// Use a lightweight helper class to extract just the "loan_domain" field
	private static class DomainWrapper {
		public String loan_domain;

		// Required by Genson
		public DomainWrapper() {
		}
	}

	private enum LoanMgmtSystemErrors {
		LoanAgreement_NOT_FOUND, LoanAgreement_ALREADY_EXISTS
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void createLoan(Context ctx, String loanId, String loanJson, String domain) {
		ChaincodeStub stub = ctx.getStub();
		LoanData loan;

		// Check if loanId already exists
		String existing = stub.getStringState(loanId);
		if (existing != null && !existing.isEmpty()) {
			throw new ChaincodeException("Loan with ID '" + loanId + "' already exists.");
		}

		switch (domain.toLowerCase()) {
		case "vehicle":
			loan = genson.deserialize(loanJson, Vehicle.class);
			break;
		/*
		 * case "study": loan = genson.deserialize(loanJson, Study.class); break; case
		 * "gold": loan = genson.deserialize(loanJson, GoldLoan.class); break;
		 */
		default:
			throw new ChaincodeException("Unsupported loan domain: " + domain);
		}

		String newJSON = genson.serialize(loan);
		stub.putStringState(loanId, newJSON);
		stub.setEvent("LoanCreated", loanId.getBytes());
	}

	@Transaction(intent = Transaction.TYPE.EVALUATE)
	public String readLoan(Context ctx, String loanId) {
		ChaincodeStub stub = ctx.getStub();
		String json = stub.getStringState(loanId);
		if (json == null || json.isEmpty()) {
			String errorMessage = String.format("Loan agreement not found", loanId);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, LoanMgmtSystemErrors.LoanAgreement_NOT_FOUND.toString());
		}
		return json;
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void deleteLoan(Context ctx, String loanId) {
		ChaincodeStub stub = ctx.getStub();

		String existing = stub.getStringState(loanId);
		if (existing == null || existing.isEmpty()) {
			throw new ChaincodeException("Loan with ID '" + loanId + "' does not exist.");
		}

		stub.delState(loanId);
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void updateLoanAmount(Context ctx, String loanId, String newAmount) {

		ChaincodeStub stub = ctx.getStub();
		String loanJson = getExistingLoanOrThrow(ctx, loanId);

		String domain = detectDomain(loanJson);

		LoanData loan;
		switch (domain.toLowerCase()) {
		case "vehicle":
			loan = genson.deserialize(loanJson, Vehicle.class);
			break;
		default:
			throw new ChaincodeException("Unsupported domain: " + domain);
		}

		// Create updated loan object
		LoanData updatedLoan = new Vehicle(loan.getBorroweID(), loan.getBorroweName(), loan.getInterestRate(),
				newAmount);

		stub.putStringState(loanId, genson.serialize(updatedLoan));
		stub.setEvent("AmountUpdated", loanId.getBytes());
	}

	@Transaction(intent = Transaction.TYPE.SUBMIT)
	public void updateLoanInterest(Context ctx, String loanId, String newInterest) {

		ChaincodeStub stub = ctx.getStub();
		String loanJson = getExistingLoanOrThrow(ctx, loanId);

		String domain = detectDomain(loanJson);

		LoanData loan;
		switch (domain.toLowerCase()) {
		case "vehicle":
			loan = genson.deserialize(loanJson, Vehicle.class);
			break;
		default:
			throw new ChaincodeException("Unsupported domain: " + domain);
		}

		// Create updated loan object
		LoanData updatedLoan = new Vehicle(loan.getBorroweID(), loan.getBorroweName(), newInterest,
				loan.getPrincipalAmount());

		stub.putStringState(loanId, genson.serialize(updatedLoan));
		stub.setEvent("InterestUpdated", loanId.getBytes());
		System.out.println("Updated JSON (interest): " + genson.serialize(updatedLoan));
	}

	private String detectDomain(String json) {
		Genson genson = new Genson();

		try {
			DomainWrapper wrapper = genson.deserialize(json, DomainWrapper.class);
			if (wrapper.loan_domain == null || wrapper.loan_domain.isEmpty()) {
				throw new ChaincodeException("Missing 'loan_domain' field in loan JSON.");
			}
			return wrapper.loan_domain.toLowerCase(); // normalize for switch
		} catch (Exception e) {
			throw new ChaincodeException("Failed to extract domain from JSON: " + e.getMessage(), e);
		}
	}

	private String getExistingLoanOrThrow(Context ctx, String loanId) {
		ChaincodeStub stub = ctx.getStub();
		String existing = stub.getStringState(loanId);

		if (existing == null || existing.isEmpty()) {
			throw new ChaincodeException("Loan with ID '" + loanId + "' does not exist.");
		}

		return existing;
	}

}
