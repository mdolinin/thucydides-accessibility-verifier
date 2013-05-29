package net.thucydides.plugins.accessibilitytests.listeners;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.plugins.accessibilitytests.AcessibilityHtmlSourceAnalyzer;
import net.thucydides.plugins.accessibilitytests.model.PageForAnalyze;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class PageSourceAnalyzerStepListener extends BaseStepListener {

    private WebdriverManager webdriverManager;
    private AcessibilityHtmlSourceAnalyzer accessibilityHtmlSourceAnalyzer =  new AcessibilityHtmlSourceAnalyzer();
    private WebDriver driver;

    public PageSourceAnalyzerStepListener() {
        super(new File("target"));
    }

    public void notifyScreenChange() {
        webdriverManager = Injectors.getInjector().getInstance(WebdriverManager.class);
        driver = webdriverManager.getWebdriver();
        PageForAnalyze page = new PageForAnalyze(driver.getCurrentUrl(), driver.getPageSource());
        accessibilityHtmlSourceAnalyzer.analyze(page);
    }

    public void testSuiteFinished(){
        accessibilityHtmlSourceAnalyzer.createSummaryReport();
    }

}
