<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Accessibility Reports</title>
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
</head>
<body>
	<div class="container-fluid">
	  <h3>Detailed accessibility report for: ${testOutcome.testUrl}</h3>
	  <a class="btn btn-info text-right" href="summary_accessibility_test_report.html">Back to Summary report</a>
	  <table class="table table-striped">
		  <thead>
		    <tr>
		      <th>#</th>
		      <th>Description</th>
		      <th>Severity</th>
		      <th>Xpath to element (click to show code)</th>
		    </tr>
		  </thead>
		  <tbody>
		  <#list testOutcome.checkResults as results>
		  	  <#if !results.passed>
			  	<tr class="info">
			  		<td colspan="4" id="provision_${results.provision}">ADA Section 508 Provision - ${results.provision} : ${results.provision.description}</td>
			  	</tr>
		  	  </#if>
			  <#list results.foundIssues as foundedIssue>
			    <tr>
			      <td>${foundedIssue_index + 1}</td>
			      <td nowrap>${foundedIssue.issue.description}</td>
			      <td>
			      <#if foundedIssue.issue.severity == "error">
					  <span class="label label-important">Error</span>
				  <#else>
					 <span class="label label-warning">Warning</span>
				  </#if>
				  </td>
			      <td>
			      	<div class="accordion-toggle" data-toggle="collapse" data-target="#issue_${foundedIssue.issueId}">
					  ${foundedIssue.xpath}
					</div>
			      	<div id="issue_${foundedIssue.issueId}" class="collapse">
			      		<pre>${foundedIssue.issue.element?html}</pre>
			      	</div>
			      </td>
			    </tr>
			  </#list>
		  </#list>
		  </tbody>
	  </table>
	</div>
  <script src="http://code.jquery.com/jquery.js"></script>
  <script src="bootstrap/js/bootstrap.min.js"></script>  
</body>
</html>  