package loanMgmtSystem;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import java.util.Objects;

@DataType()
public abstract class LoanData {

	@Property()
	private final String borrower_id;

	@Property()
	private final String borrower_name;

	@Property()
	private final String interest_rate;

	@Property()
	private final String principal_amount;

	public LoanData(@JsonProperty("borrower_id") final String borrower_id,
			@JsonProperty("borrower_name") final String borrower_name,
			@JsonProperty("interest_rate") final String interest_rate,
			@JsonProperty("principal_amount") final String principal_amount) {

		this.borrower_id = borrower_id;
		this.borrower_name = borrower_name;
		this.interest_rate = interest_rate;
		this.principal_amount = principal_amount;
	}

	@JsonProperty("borrower_id")
	public String getBorroweID() {
		return this.borrower_id;
	}

	@JsonProperty("borrower_name")
	public String getBorroweName() {
		return this.borrower_name;
	}

	@JsonProperty("interest_rate")
	public String getInterestRate() {
		return this.interest_rate;
	}

	@JsonProperty("principal_amount")
	public String getPrincipalAmount() {
		return this.principal_amount;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}

		LoanData other = (LoanData) obj;

		return Objects.deepEquals(
				new String[] { getBorroweID(), getBorroweName(), getInterestRate(), getPrincipalAmount() },
				new String[] { other.getBorroweID(), other.getBorroweName(), other.getInterestRate(),
						other.getPrincipalAmount() });
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBorroweID(), getBorroweName(), getInterestRate(), getPrincipalAmount());
	}

	@Override
	public String toString() {
		return "{\n" +
		           "  \"type\": \"" + this.getClass().getSimpleName() + "\",\n" 
		           + "  \"borrower_id\": \"" + this.borrower_id + "\",\n"
		           + "  \"borrower_name\": \"" + this.borrower_name + "\",\n"
		           + "  \"interest_rate\": \"" + this.interest_rate + "\",\n"
		           + "  \"principal_amount\": \"" + this.principal_amount + "\",\n"
		           + "  \"loan_domain\": \"" + getLoanDomain() + "\",\n"
//		           + "  \"loan_tenure\": \"" + getLoanTenure() + "\"\n"
		           + "}";
	}

	public abstract String getLoanDomain();

	public abstract String getLoanTenure();

}
