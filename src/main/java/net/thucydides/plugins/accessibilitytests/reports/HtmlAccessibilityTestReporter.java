package net.thucydides.plugins.accessibilitytests.reports;

import com.google.common.base.Preconditions;
import net.thucydides.core.reports.html.HtmlResourceCopier;
import net.thucydides.core.reports.templates.FreemarkerReportTemplate;
import net.thucydides.plugins.accessibilitytests.model.TestResultBean;
import net.thucydides.plugins.accessibilitytests.reports.templates.FreeMarkerTemplateManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HtmlAccessibilityTestReporter {
	
	private static final String DETAILED_ACCESSIBILITY_TEST_REPORT = "freemarker/detailed_report.ftl";
	private static final String SUMMARY_ACCESSIBILITY_TEST_REPORT = "freemarker/summary_report.ftl";
    private static final String HTML = ".html";
	private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources/bootstrap";
	private static final String DEFAULT_OUTPUT_DIRECTORY = "target/accessibility-report";
	private final FreeMarkerTemplateManager templateManager;
    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
	private String outputDirectory = DEFAULT_OUTPUT_DIRECTORY;

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlAccessibilityTestReporter.class);

    public HtmlAccessibilityTestReporter() {
        this.templateManager = new FreeMarkerTemplateManager();
    }

    private FreeMarkerTemplateManager getTemplateManager() {
        return templateManager;
    }

    /**
     * Generate an HTML report for a given test run.
     */
    public File generateReportFor(final TestResultBean testOutcome) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        LOGGER.debug("Generating XML report for {}/{}", testOutcome.getTestUrl());

        Map<String,Object> context = new HashMap<String,Object>();
        addTestOutcomeToContext(testOutcome, context);
        String htmlContents = mergeTemplate(DETAILED_ACCESSIBILITY_TEST_REPORT).usingContext(context);
        copyResourcesToOutputDirectory();
        
        String reportFilename = reportFor(testOutcome);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }
    
    public File generateSummaryReportFor(final List<TestResultBean> allTestResults) throws IOException {
    	LOGGER.debug("Generating XML report for {}/{}", allTestResults);
     	Map<String,Object> context = new HashMap<String,Object>();
     	context.put("allTestResults", allTestResults);
    	String htmlContents = mergeTemplate(SUMMARY_ACCESSIBILITY_TEST_REPORT).usingContext(context);
    	String reportFilename = "summary_accessibility_test_report" + HTML;
    	return writeReportToOutputDirectory(reportFilename, htmlContents);
    }

    private void addTestOutcomeToContext(final TestResultBean testOutcome, final Map<String,Object> context) {
        context.put("testOutcome", testOutcome);
    }

    private String reportFor(final TestResultBean testOutcome) {
        return testOutcome.getTestFileName() + HTML;
    }
    
    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    public String getResourceDirectory() {
        return resourceDirectory;
    }

    private boolean alreadyCopied = false;

    protected void copyResourcesToOutputDirectory() throws IOException {
        if (!alreadyCopied) {
            HtmlResourceCopier copier = new HtmlResourceCopier(getResourceDirectory());
            copier.copyHTMLResourcesTo(getOutputDirectory());
            alreadyCopied = true;
        }
    }

    private File getOutputDirectory() {
        return new File(outputDirectory);
	}

    public void setOutputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

	/**
     * Write the actual HTML report to a file with the specified name in the output directory.
     */
    protected File writeReportToOutputDirectory(final String reportFilename, final String htmlContents) throws IOException {
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents, "UTF-8");
        return report;
    }


    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }

    protected class Merger {
        final String templateFile;

        public Merger(final String templateFile) {
            this.templateFile = templateFile;
        }

        public String usingContext(final Map<String, Object> context) {
            try {
                FreemarkerReportTemplate template = getTemplateManager().getTemplateFrom(templateFile);
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge template: " + e.getMessage(), e);
            }
        }
    }

}