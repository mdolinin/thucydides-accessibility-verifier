package net.thucydides.plugins.accessibilitytests.model;

import org.a11ytesting.test.Filter;
import org.a11ytesting.test.Issue;
import org.a11ytesting.test.Rule;
import org.jsoup.nodes.Element;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleEvaluator {
	
	private Set<Rule> rules = new HashSet<Rule>();
	
	/**
	 * Add a package containing rule implementations to the evaluator. Every
	 * class implementing the Rule interface in the given package will be added
	 * to the evaluator.
	 *  
	 * @param packageName of package name in the current classpath to add.
	 */
	public void addPackage(String packageName) {
		Reflections reflection = new  Reflections(packageName);
		Set<Class<? extends Rule>> classes = reflection.getSubTypesOf(Rule.class);
		for (Class<? extends Rule> rule : classes) {
			if (Modifier.isAbstract(rule.getModifiers())) {
				continue;
			}
			try {
				Rule instance = rule.newInstance();
				addRule(instance);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
		}
	}
	
	/**
	 * Add a single rule implementation to the evaluator.
	 * 
	 * @param rule to add to the evaluator.
	 */
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	public Set<Rule> getRules() {
		return rules;
	}

	/**
	 * Collect the issues for the current loaded set of
	 * rules 
	 * @param root element for analysis.
	 * @return the collection of issues identified.
	 */
	public List<RuleCheckResults> collectIssues(Element root) {
		List<RuleCheckResults> results = new ArrayList<RuleCheckResults>();
		for (Rule rule : getRules()) {
			Filter filter = rule.getFilter();
			int elementsChecked = 0;
			List<DetailedIssue> foundIssues = new ArrayList<DetailedIssue>();
			for (Element target : filter.result(root)) {
				Issue issue = rule.check(target);
				elementsChecked++;
				if (null != issue) {
					foundIssues.add(new DetailedIssue(issue));
				}
			}
			
			results.add(new RuleCheckResults(rule, elementsChecked, foundIssues));
		}
		return results;
	}

}
