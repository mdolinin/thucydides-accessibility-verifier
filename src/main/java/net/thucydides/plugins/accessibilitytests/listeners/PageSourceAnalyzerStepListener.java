package net.thucydides.plugins.accessibilitytests.listeners;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.plugins.accessibilitytests.AcessibilityHtmlSourceAnalyzer;
import net.thucydides.plugins.accessibilitytests.model.PageForAnalyze;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class PageSourceAnalyzerStepListener extends BaseStepListener {

    private static final String DEFAULT_OUTPUT_DIRECTORY = "target";
    private WebdriverManager webdriverManager;
    private AcessibilityHtmlSourceAnalyzer accessibilityHtmlSourceAnalyzer =  new AcessibilityHtmlSourceAnalyzer();
    private WebDriver driver;

    public PageSourceAnalyzerStepListener() {
        super(new File(DEFAULT_OUTPUT_DIRECTORY));
    }

    public void notifyScreenChange() {
        driver = getWebdriverManager().getWebdriver();
        PageForAnalyze page = new PageForAnalyze(driver.getCurrentUrl(), driver.getPageSource());
        accessibilityHtmlSourceAnalyzer.analyze(page);
    }

    public void testSuiteFinished(){
        accessibilityHtmlSourceAnalyzer.createSummaryReport();
    }

    private WebdriverManager getWebdriverManager(){
        if(webdriverManager == null) {
            webdriverManager = Injectors.getInjector().getInstance(WebdriverManager.class);
        }
        return webdriverManager;
    }
}
