package net.thucydides.plugins.accessibilitytests;

import net.thucydides.plugins.accessibilitytests.model.RuleCheckResults;
import net.thucydides.plugins.accessibilitytests.model.RuleEvaluator;
import junit.framework.Assert;
import org.a11ytesting.test.wcag.AltTextOnImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.List;

public class AdditionalIssueInformationTest {
	
	@Test
	public void verifierShoudStoreElementXpath() {
		String html = "<html><body><div id=somesuch type=text name=boxyboxy><img src=some/path></div></body></html>";
		List<RuleCheckResults> result = verifyHtml(html);
		Assert.assertEquals("//*[@id='somesuch']/img", result.get(0).getFoundIssues().get(0).getXpath());
	}

	private List<RuleCheckResults> verifyHtml(String html) {
		Element document = Jsoup.parse(html);
		RuleEvaluator evaluator = new RuleEvaluator();
		evaluator.addRule(new AltTextOnImage());
		List<RuleCheckResults> result = evaluator.collectIssues(document);
		return result;
	}
	
	@Test
	public void verifierShoudCountVerifiedElements() {
		String html = "<html><body><img src=some/path><div id=somesuch type=text name=boxyboxy><img src=some/path></div>" +
				"<div><img alt=alttext src=some/path></div></body></html>";
		List<RuleCheckResults> result = verifyHtml(html);
		Assert.assertEquals(3, result.get(0).getElementsChecked());
		Assert.assertEquals(1, result.get(0).getElementsPassed());
		Assert.assertEquals(2, result.get(0).getElementsFailed());
	}
	
	@Test
	public void verifierShoudStoreSection508ProvisionsOfVerifiedElements() {
		String html = "<html><body><img src=some/path><div id=somesuch type=text name=boxyboxy><img src=some/path></div>" +
				"<div><img alt=alttext src=some/path><span></span></div></body></html>";
		List<RuleCheckResults> result = verifyHtml(html);
		Assert.assertEquals("A", result.get(0).getProvision().name());
	}

}
