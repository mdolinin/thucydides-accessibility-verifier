package net.thucydides.plugins.accessibilitytests;

import net.thucydides.plugins.accessibilitytests.model.DetailedIssue;
import net.thucydides.plugins.accessibilitytests.model.RuleCheckResults;
import net.thucydides.plugins.accessibilitytests.model.RuleEvaluator;
import net.thucydides.plugins.accessibilitytests.model.TestResultBean;
import net.thucydides.plugins.accessibilitytests.reports.HtmlAccessibilityTestReporter;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityVerifier {

	private static final String PACKAGE_WITH_WCAG_TESTS = "org.a11ytesting.test.wcag";

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessibilityVerifier.class);

	public static void main(String[] args) throws IOException {
		List<String> urlsToVisit = Lists.newArrayList();
		if (args.length == 1) {
			File fileWithURLs = new File(args[0]);
			if (args[0].contains(";"))
				urlsToVisit = getURLsFromSemiColonSeparatedList(args[0]);
			else if (fileWithURLs.isFile())
				urlsToVisit = getURLsFromFile(fileWithURLs);
			else
				urlsToVisit.add(args[0]);
		} else {
			throw new RuntimeException("Invalid command line, exactly one argument required");
		}
		runAccessibilityVerifier(urlsToVisit);
	}

	private static ArrayList<String> getURLsFromSemiColonSeparatedList(
			String inputString) {
		return Lists.newArrayList(Splitter.on(';').trimResults()
				.omitEmptyStrings().split(inputString));
	}

	private static List<String> getURLsFromFile(File file) throws IOException {
		List<String> urlsToVisit = Lists.newArrayList();
		LineIterator iterator = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (iterator.hasNext()) {
				String line = iterator.nextLine();
				urlsToVisit.add(line);
			}
			return urlsToVisit;
		} finally {
			LineIterator.closeQuietly(iterator);
		}
	}

	private static void runAccessibilityVerifier(List<String> urlsToVisit) throws IOException {
		List<TestResultBean> testResults = Lists.newArrayList();
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
        driver.setJavascriptEnabled(true);
		for (String urlToVisit : urlsToVisit) {
            driver.get(urlToVisit);
            Document document = Jsoup.parse(driver.getPageSource());
            LOGGER.info("Visit URL: " + urlToVisit);
            RuleEvaluator evaluator = new RuleEvaluator();
			evaluator.addPackage(PACKAGE_WITH_WCAG_TESTS);
			List<RuleCheckResults> result = evaluator.collectIssues(document);
			testResults.add(new TestResultBean(urlToVisit, result));
		}
		generateHtmlReportFor(testResults);
	}

	private static void generateHtmlReportFor(List<TestResultBean> testResults)	throws IOException {
		HtmlAccessibilityTestReporter reporter = new HtmlAccessibilityTestReporter();
		for (TestResultBean testOutcome : testResults) {
			reporter.generateReportFor(testOutcome);
			LOGGER.info("Test results for url - " + testOutcome.getTestUrl() + " :");
			for (RuleCheckResults  checkResults : testOutcome.getCheckResults()) {
				LOGGER.info("Checked rule: " + checkResults.getRule().getRuleName());
				LOGGER.info("Checked Section 508 provision " + checkResults.getProvision().name() + " : " + checkResults.getProvision().getDescription());
				LOGGER.info("Checked elements: " + checkResults.getElementsChecked());
				for (DetailedIssue issue : checkResults.getFoundIssues()) {
					LOGGER.info("Xpath: " + issue.getXpath());
					LOGGER.info(issue.toString());
				}
			}
		}
		reporter.generateSummaryReportFor(testResults);
	}

}
