package net.thucydides.plugins.accessibilitytests.model;

import java.util.List;
import java.util.UUID;

public class TestResultBean {
	
	private String testId;
	private String testUrl;
	private List<RuleCheckResults> checkResults;
	
	public TestResultBean(String testUrl, List<RuleCheckResults> checkResults) {
		this.testUrl = testUrl;
		this.testId = UUID.randomUUID().toString();
		this.checkResults = checkResults;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public String getTestId() {
		return testId;
	}
	
	public String getTestFileName(){
		return testUrl.replaceAll("/", "_").replaceAll("\\.", "_").replaceAll(":", "_");
	}

	public List<RuleCheckResults> getCheckResults() {
		return checkResults;
	}

}
