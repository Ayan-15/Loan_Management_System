# Loan_Management_System
Loan Management System developed on Hyperledger fabric with basic loan features in JAVA.
The Loan Management System can address these challenges by leveraging blockchain technology to provide a secure, transparent, and 
efficient solution. Using Hyperledger Fabric the system can automate loan agreements, track repayments, ensure data integrity, and enhance transparency and security throughout the loan lifecycle. 

# What is in this repo
- Chaincode Development: Java
- IDE Tool: Eclipse
- Blockchain: Hyperledger Fabric
- Build Automation Tool: Gradle
- Tested by: Mockito and JUnit

# Working - Data management
`LoanData.java` is the abstract class containing the data memebers base class constructor and getters. 
The `Vehicle.java` is one simple implementation of `LoanData.java`. There can be other classes like `Study`, `Personal`, `Housing` etc deriving from 
`LoanData.java` abstract and making their own implementation based on the `loan_domian`

*Please note : Any class extending `LoanData.java` can make use of lombok.EqualsAndHashCode to calculate the hash and equals which will reduce the coding in the child classes* 

# Working - Chaining
The chaincode can be found in LoanChain.java. It has :
- `createLoan()` : to create the loan in the fabric ledger
- `readLoan()` : to read a loan from the fabric ledger
- `deleteLoan()` : to delete a loan from the fabric
- `updateLoanInterest()` : to update rate of interest and thus creating a new Loan object and storing it in the fabric
- `updateLoanAmount()` : to update the pricipal amount and thus creating a new Loan object and storing it in the fabric
- `detectDomain()` : helper function to detect the domain of the loan - like *Vehicle* or *Study* , *Personal* etc.
- `getExistingLoanOrThrow()` : helper function to check if Loan exists and throw exceptiomn accordingly.

# Testing
Test were done using mockito and JUnit - the real testing using CouchDB and LedgerDB can be done in future when Fabric SDK will be using. 

# Future extension
In future fabric SDK can be used for packaging the chain and spinning the docker image for fabric. 
Also, then a frontend UI can be established with SDK in between interacting with the chain.  
