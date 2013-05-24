package net.thucydides.plugins.accessibilitytests.model;

import net.thucydides.plugins.accessibilitytests.ada.Section508Provisions;
import org.a11ytesting.test.Rule;

import java.util.List;

public class RuleCheckResults {
	
	private Rule rule;
	private Section508Provisions provision;
	private int elementsChecked;
	private int elementsPassed;
	private int elementsFailed;
	private List<DetailedIssue> foundIssues;
	
	public RuleCheckResults(Rule rule, int elementsChecked,	List<DetailedIssue> foundIssues) {
		this.rule = rule;
		this.provision = Section508Provisions.getSection508Provision(rule);
		this.elementsChecked = elementsChecked;
		this.foundIssues = foundIssues;
		this.elementsPassed = elementsChecked - foundIssues.size();
		this.elementsFailed = elementsChecked - this.elementsPassed;
	}

	public Rule getRule() {
		return rule;
	}

	public Section508Provisions getProvision() {
		return provision;
	}

	public int getElementsChecked() {
		return elementsChecked;
	}

	public List<DetailedIssue> getFoundIssues() {
		return foundIssues;
	}
	
	public boolean isPassed() {
		return foundIssues.isEmpty();
	}

	public int getElementsPassed() {
		return elementsPassed;
	}

	public int getElementsFailed() {
		return elementsFailed;
	}
}
