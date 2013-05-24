package net.thucydides.plugins.accessibilitytests.model;

import org.a11ytesting.test.Issue;
import org.apache.commons.lang3.text.StrBuilder;
import org.jsoup.nodes.Element;

import java.util.UUID;

public class DetailedIssue {
	
	private Issue issue;
	private String xpath;
	private String issueId;
	
	public DetailedIssue(Issue issue) {
		this.issue = issue;
		setXpath();
		this.issueId = UUID.randomUUID().toString();
	}

	public Issue getIssue() {
		return issue;
	}
	
	public String getIssueId() {
		return issueId;
	}

	public String getXpath() {
		return xpath;
	}

	private void setXpath() {
		Element element = issue.getElement();
		StrBuilder accum = new StrBuilder();
		do {
			if (element.hasAttr("id")) {
				accum.insert(0, "/*[@id='" + element.id() + "']");
				break;
			}
			int index = getTagIndex(element);
			accum.insert(0, index == 0 ? "" : "[" + index + "]");
			accum.insert(0, element.tagName());
			accum.insert(0, "/");
			element = element.parent();
		} while (element.tagName() != "#root");
		accum.insert(0, "/");
		this.xpath = accum.toString();
	}

	private int getTagIndex(Element element) {
		int sameTagCount = 0;
		int index = 0;
		for (Element siblingElement : element.parent().children()) {
			if (siblingElement.tagName().equals(element.tagName())) {
				sameTagCount++;
				if(siblingElement.equals(element)){
					index = sameTagCount;
				}
			}
		}
		return sameTagCount==1 ? 0 : index;
	}
}