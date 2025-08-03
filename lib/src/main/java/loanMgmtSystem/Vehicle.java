package loanMgmtSystem;

import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Vehicle extends LoanData {

	@Property()
	@JsonProperty("loan_domain")
	private final String loan_domain = "Vehicle";

	@Property()
	@JsonProperty("loan_tenure")
	private final String loan_tenure = "7";

	public Vehicle(@JsonProperty("borrower_id") String borrower_id, @JsonProperty("borrower_name") String borrower_name,
			@JsonProperty("interest_rate") String interest_rate,
			@JsonProperty("principal_amount") String principal_amount) {
		super(borrower_id, borrower_name, interest_rate, principal_amount);
		// Not needed as it is hard-coded for the instance of Vehicle.
		// this.loan_domain = loan_domain;
		// this.loan_tenure = loan_tenure;

	}

	@Override
	public String getLoanDomain() {
		return this.loan_domain;
	}

	@Override
	public String getLoanTenure() {
		return this.loan_tenure;
	}

}
