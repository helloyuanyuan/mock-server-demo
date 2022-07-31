package com.solera.global.qa.mockservertest.suites;

import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Mock Server Test")
@SelectPackages("com.solera.global.qa.mockservertest.cases")
@ExcludePackages("com.solera.global.qa.mockservertest.cases.restassuredexample")
public class MockServertTestSuite {
}
