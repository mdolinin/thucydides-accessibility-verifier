package net.thucydides.plugins.accessibilitytests.ada;

import com.google.common.collect.Lists;
import org.a11ytesting.test.Rule;
import org.a11ytesting.test.wcag.*;

import java.util.List;

public enum Section508Provisions {

	A("A text equivalent for every non-text element shall be provided (e.g., via 'alt', 'longdesc', or in element content).",
			Lists.newArrayList(new AltTextOnImage().getRuleName(),
					new AltTextOnImageNotBad().getRuleName(),
					new AltTextLengthReasonable().getRuleName(),
					new InvisibleImageNoTitle().getRuleName())),

	G("Row and column headers shall be identified for data tables.", Lists
			.newArrayList(new TableHasHeadings().getRuleName())),

	H("Markup shall be used to associate data cells and header cells for data tables that have two or more logical levels of row or column headers.",
			Lists.newArrayList(new ComplexTableHeadingHasId().getRuleName(),
					new ComplexTableHeadingIdUnique().getRuleName(),
					new ComplexTableDataHasHeading().getRuleName())),
					
	I("Frames shall be titled with text that facilitates frame identification and navigation.",
			Lists.newArrayList(new FrameHasTitle().getRuleName(),
					new FrameTitleUnique().getRuleName())),
					
	J("Pages shall be designed to avoid causing the screen to flicker with a frequency greater than 2 Hz and lower than 55 Hz.",
			Lists.newArrayList(new ActiveTextElementNotPresent().getRuleName())),
			
	N("When electronic forms are designed to be completed on-line, the form shall allow people using assistive technology to access the information, " +
			"field elements, and functionality required for completion and submission of the form, including all directions and cues.",
			Lists.newArrayList(new FieldsetHasLegend().getRuleName(), 
					new ControlHasDescription().getRuleName(), 
					new ImageInputHasDescription().getRuleName(),
					new FormControlHasDescription().getRuleName(),
					new ButtonHasContent().getRuleName(),
					new DescriptionHasText().getRuleName())),

	NOT_SECTION_508("This rule is not from Section508.It's from WCAG 2.0 only.", Lists
					.newArrayList(""));

	private List<String> rules;
	private String description;

	private Section508Provisions(String description, List<String> rules) {
		this.description = description;
		this.rules = rules;
	}
	
	public String getDescription() {
		return description;
	}

	public static Section508Provisions getSection508Provision(Rule findingRule){
		for(Section508Provisions provision : Section508Provisions.values()) {
			for(String ruleName : provision.rules){
				if(ruleName.equals(findingRule.getRuleName())){
					return provision;
				}
			}
		}
		return NOT_SECTION_508;
	}
}
