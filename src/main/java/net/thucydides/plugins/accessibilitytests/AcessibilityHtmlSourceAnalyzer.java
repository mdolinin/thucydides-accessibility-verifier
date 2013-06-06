package net.thucydides.plugins.accessibilitytests;

import com.google.common.collect.Lists;
import net.thucydides.plugins.accessibilitytests.model.*;
import net.thucydides.plugins.accessibilitytests.reports.HtmlAccessibilityTestReporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AcessibilityHtmlSourceAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcessibilityHtmlSourceAnalyzer.class);
    private static final String PACKAGE_WITH_WCAG_TESTS = "org.a11ytesting.test.wcag";
    private RuleEvaluator evaluator;
    private HtmlAccessibilityTestReporter reporter;
    private List<TestResultBean> testResults;

    public void analyze(PageForAnalyze page) {
        Document document = Jsoup.parse(page.getPageSource());
        List<RuleCheckResults> result = getRuleEvaluator().collectIssues(document);
        try {
            TestResultBean testResult = new TestResultBean(page.getUrl() + "_" + System.nanoTime(), result);
            getReporter().generateReportFor(testResult);
            logTestResults(testResult);
            getTestResults().add(testResult);
        } catch (IOException e) {
            new RuntimeException("Report generation failed: " + e.getStackTrace());
        }
    }

    public void createSummaryReport(){
        try {
            getReporter().generateSummaryReportFor(getTestResults());
        } catch (IOException e) {
            new RuntimeException("Report generation failed: " + e.getStackTrace());
        }
    }

    private RuleEvaluator getRuleEvaluator() {
        if(evaluator == null){
            evaluator = new RuleEvaluator();
            evaluator.addPackage(PACKAGE_WITH_WCAG_TESTS);
        }
        return evaluator;
    }


    private HtmlAccessibilityTestReporter getReporter() {
        if(reporter == null){
            reporter = new HtmlAccessibilityTestReporter();
        }
        return reporter;
    }

    private void logTestResults(TestResultBean result){
        LOGGER.info("Test results for url - " + result.getTestUrl() + " :");
        for (RuleCheckResults checkResults : result.getCheckResults()) {
            LOGGER.info("Checked rule: " + checkResults.getRule().getRuleName());
            LOGGER.info("Checked Section 508 provision " + checkResults.getProvision().name() + " : " + checkResults.getProvision().getDescription());
            LOGGER.info("Checked elements: " + checkResults.getElementsChecked());
            for (DetailedIssue issue : checkResults.getFoundIssues()) {
                LOGGER.info("Xpath: " + issue.getXpath());
                LOGGER.info(issue.toString());
            }
        }
    }

    private List<TestResultBean> getTestResults() {
        if(testResults == null) {
            testResults = Lists.newArrayList();
        }
        return testResults;
    }

}
