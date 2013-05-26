package net.thucydides.plugins.accessibilitytests;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
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
    private RuleEvaluator evaluator;
    private List<PageForAnalyze> grabbedPages;

    public List<TestResultBean> getTestResults() {
        if(testResults == null) {
            testResults = Lists.newArrayList();
        }
        return testResults;
    }

    public List<PageForAnalyze> getGrabbedPages() {
        if(grabbedPages == null){
            grabbedPages = Lists.newArrayList();
        }
        return grabbedPages;
    }

    private RuleEvaluator getRuleEvaluator() {
        if(evaluator == null){
            evaluator = new RuleEvaluator();
            evaluator.addPackage(PACKAGE_WITH_WCAG_TESTS);
        }
        return evaluator;
    }

    @Override
    public void addToAnalyze(String pageSource, String visitedUrl) {
        getGrabbedPages().add(new PageForAnalyze(visitedUrl, pageSource));
        LOGGER.info("Visit URL: " + visitedUrl);
    }

    @Override
    public void makeAnalysis() {
        for(PageForAnalyze page : getUnicPages()){
            Document document = Jsoup.parse(page.getPageSource());
            List<RuleCheckResults> result = getRuleEvaluator().collectIssues(document);
            getTestResults().add(new TestResultBean(page.getUrl() + "_" + System.nanoTime(), result));
        }
        try {
            generateHtmlReportFor(getTestResults());
        } catch (IOException e) {
            new RuntimeException("Report generation failed: " + e.getStackTrace());
        }
    }

    private List<PageForAnalyze> getUnicPages() {
        List<PageForAnalyze> unicPages = Lists.newArrayList();
        PeekingIterator<PageForAnalyze> iter = Iterators.peekingIterator(getGrabbedPages().iterator());
        while (iter.hasNext()) {
            PageForAnalyze current = iter.next();
            while (iter.hasNext() && iter.peek().getPageSource().equals(current.getPageSource())) {
                // skip this duplicate element
                iter.next();
            }
            unicPages.add(current);
        }
        return unicPages;
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

    private class PageForAnalyze {
        private String url;
        private String pageSource;

        private PageForAnalyze(String url, String pageSource) {
            this.url = url;
            this.pageSource = pageSource;
        }

        private String getUrl() {
            return url;
        }

        private String getPageSource() {
            return pageSource;
        }
    }
}
