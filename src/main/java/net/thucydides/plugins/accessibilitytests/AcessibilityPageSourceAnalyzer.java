package net.thucydides.plugins.accessibilitytests;

import com.google.common.collect.Lists;
import net.thucydides.core.ouputdata.service.PageSourceAnalyzer;
import net.thucydides.plugins.accessibilitytests.model.DetailedIssue;
import net.thucydides.plugins.accessibilitytests.model.RuleCheckResults;
import net.thucydides.plugins.accessibilitytests.model.RuleEvaluator;
import net.thucydides.plugins.accessibilitytests.model.TestResultBean;
import net.thucydides.plugins.accessibilitytests.reports.HtmlAccessibilityTestReporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AcessibilityPageSourceAnalyzer implements PageSourceAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcessibilityPageSourceAnalyzer.class);
    private static final String PACKAGE_WITH_WCAG_TESTS = "org.a11ytesting.test.wcag";
    private List<TestResultBean> testResults;

    public List<TestResultBean> getTestResults() {
        if(testResults == null){
            testResults = Lists.newArrayList();
        }
        return testResults;
    }

    @Override
    public void addToAnalyze(String pageSource, String visitedUrl) {
        Document document = Jsoup.parse(pageSource);
        LOGGER.info("Visit URL: " + visitedUrl);
        RuleEvaluator evaluator = new RuleEvaluator();
        evaluator.addPackage(PACKAGE_WITH_WCAG_TESTS);
        List<RuleCheckResults> result = evaluator.collectIssues(document);
        getTestResults().add(new TestResultBean(visitedUrl, result));

    }

    @Override
    public void makeAnalysis(){
        try {
            generateHtmlReportFor(getTestResults());
        } catch (IOException e) {
            new RuntimeException("Report generation failed: " + e.getStackTrace());
        }
    }

    private static void generateHtmlReportFor(List<TestResultBean> testResults) throws IOException {
        HtmlAccessibilityTestReporter reporter = new HtmlAccessibilityTestReporter();
        for (TestResultBean testOutcome : testResults) {
            reporter.generateReportFor(testOutcome);
            LOGGER.info("Test results for url - " + testOutcome.getTestUrl() + " :");
            for (RuleCheckResults checkResults : testOutcome.getCheckResults()) {
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
