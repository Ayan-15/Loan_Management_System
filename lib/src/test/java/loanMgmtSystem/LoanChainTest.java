package loanMgmtSystem;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.hyperledger.fabric.shim.ChaincodeException;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.owlike.genson.Genson;

public class LoanChainTest {

	private LoanChain contract;
	private Context ctx;
	private ChaincodeStub stub;

	@BeforeEach
	public void setup() {
		contract = new LoanChain();
		ctx = mock(Context.class);
		stub = mock(ChaincodeStub.class);
		when(ctx.getStub()).thenReturn(stub);
	}

	@Test
	public void testCreateLoan() {
		String loanId = "loan123";
		String json = "{\"borrower_id\":\"B001\",\"borrower_name\":\"Alice\",\"interest_rate\":\"7.5\",\"principal_amount\":\"50000\"}";

		contract.createLoan(ctx, loanId, json, "Vehicle");

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(stub).putStringState(eq(loanId), captor.capture());

		String storedJson = captor.getValue();
		assertTrue(storedJson.contains("\"loan_domain\":\"Vehicle\""));
		assertTrue(storedJson.contains("\"loan_tenure\":\"7\""));
	}

	@Test
	public void testReadLoan() {
		String loanId = "loan123";
		String expectedJson = "{\"borrower_id\":\"B001\",\"borrower_name\":\"Alice\",\"interest_rate\":\"7.5\",\"principal_amount\":\"50000\"}";

		when(stub.getStringState(loanId)).thenReturn(expectedJson);

		String actual = contract.readLoan(ctx, loanId);

		assertEquals(expectedJson, actual);
	}

	@Test
	public void testReadLoan_NotFound() {
		String loanId = "loan999";
		when(stub.getStringState(loanId)).thenReturn("");

		Exception exception = assertThrows(Exception.class, () -> {
			contract.readLoan(ctx, loanId);
		});

		assertTrue(exception.getMessage().contains("Loan not found"));
	}

	@Test
	public void testDeleteLoan_Success() {
		String loanId = "loan123";
		when(stub.getStringState(loanId)).thenReturn("{...}");

		contract.deleteLoan(ctx, loanId);

		verify(stub).delState(loanId);
	}

	@Test
	public void testDeleteLoan_NotFound() {
		String loanId = "loanXYZ";
		when(stub.getStringState(loanId)).thenReturn("");

		Exception exception = assertThrows(ChaincodeException.class, () -> {
			contract.deleteLoan(ctx, loanId);
		});

		assertTrue(exception.getMessage().contains("does not exist"));
	}

	@Test
	public void testUpdateLoanAmount() {
		String loanId = "loan123";
		String newAmount = "60000";

		String originalJson = """
				{
				  "borrower_id":"B001",
				  "borrower_name":"Alice",
				  "interest_rate":"7.5",
				  "principal_amount":"50000",
				  "loan_domain":"Vehicle",
				  "loan_tenure":"7"
				}
				""";

		when(stub.getStringState(loanId)).thenReturn(originalJson);

		contract.updateLoanAmount(ctx, loanId, newAmount);

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(stub).putStringState(eq(loanId), captor.capture());
		verify(stub).setEvent(eq("AmountUpdated"), eq(loanId.getBytes()));

		String updatedJson = captor.getValue();
		assertTrue(updatedJson.contains("\"principal_amount\":\"60000\""));
		assertTrue(updatedJson.contains("\"loan_domain\":\"Vehicle\""));
	}

	@Test
	public void testUpdateLoanInterest() {
		String loanId = "loan123";
		Vehicle originalLoan = new Vehicle("B001", "Alice", "7.5", "50000");
		Genson genson = new Genson();

		// Serialize original loan and mock it in ledger
		String originalJson = genson.serialize(originalLoan);
		when(stub.getStringState(loanId)).thenReturn(originalJson);

		// Perform update
		contract.updateLoanInterest(ctx, loanId, "8.5");

		// Capture what was written back to the ledger
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(stub).putStringState(eq(loanId), captor.capture());

		String updatedJson = captor.getValue();
		System.out.println("Captured Updated JSON: " + updatedJson);

		// Deserialize and check the value
		Vehicle updatedLoan = genson.deserialize(updatedJson, Vehicle.class);
		assertEquals("8.5", updatedLoan.getInterestRate());
	}

	@Test
	public void testGensonSerialization() {
		Vehicle v = new Vehicle("B001", "Alice", "6.5", "50000");
		Genson genson = new Genson();
		String json = genson.serialize(v);
		System.out.println("Serialized JSON: " + json);
		assertTrue(json.contains("\"interest_rate\":\"6.5\""));
	}

}
