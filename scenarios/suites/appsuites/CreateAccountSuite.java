package suites.appsuites;

import org.testng.annotations.Test;

import amazon.CreateAccount;
import suites.basesuite.BaseSuite;

public class CreateAccountSuite extends BaseSuite {

	@Test
	public void createAccount() {
		CreateAccount ca = new CreateAccount();
		int accountsCreated = 0;
		for (int i = 0; i < 10; i++) {
			ca.launchAndCreateAccount();
		}
		ca.log.debug("Accounts created - " + accountsCreated);
	}
}
