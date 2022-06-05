package suites.basesuite;

import org.testng.annotations.BeforeSuite;

import amazon.CommonFunctions;
import framework.base.FrameworkBaseSuite;

public class BaseSuite extends FrameworkBaseSuite {

	protected CommonFunctions cf;

	@BeforeSuite
	public void initializeObjects() {
		cf = new CommonFunctions();
	}
}
