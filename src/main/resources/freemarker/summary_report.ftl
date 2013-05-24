<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Accessibility Reports</title>
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
</head>
<body>
	<div class="container-fluid">
	  <h3>Summary accessibility report</h3>
	  <table class="table table-striped">
		  <thead>
		    <tr>
		      <th>#</th>
		      <th>Tested URL</th>
		      <th>ADA Section 508 Provision A</th>
		      <th>ADA Section 508 Provision G</th>
		      <th>ADA Section 508 Provision H</th>
		      <th>ADA Section 508 Provision I</th>
		      <th>ADA Section 508 Provision J</th>
		      <th>ADA Section 508 Provision N</th>
		      <th>WCAG 2.0 only</th>
		    </tr>
		  </thead>
		  <tbody>
			<#list allTestResults as test>
		    <tr>
		      <td>${test_index +1}</td>
		      <td><a href="${test.testFileName}.html">${test.testUrl}</a></td>
		      	<#assign provisions = ["A", "G", "H", "I", "J", "N", "NOT_SECTION_508"]>
				<#list provisions as prov>
					<#assign testsPassed = 0>
				  		<#assign testsFailed = 0>
				      	<#list test.checkResults as verifiedResult>
				      	<#if verifiedResult.provision == prov>
				      		<#assign testsPassed = testsPassed + verifiedResult.elementsPassed>
				      		<#assign testsFailed = testsFailed + verifiedResult.elementsFailed>
						</#if>
					</#list>
					<td>
					<#if (testsPassed > 0)>
			    		<span class="label label-success">Passed ${testsPassed}</span>
			    	</#if>
			    	<#if (testsFailed > 0)>
                        <a href="${test.testFileName}.html#provision_${prov}"
			    		    <span class="label label-important">Failed ${testsFailed}</span>
                        </a>
			    	</#if>
					</td>
				</#list>
		    </tr>
			</#list>
		  </tbody>
	  </table>
	</div>
  <script src="http://code.jquery.com/jquery.js"></script>
  <script src="bootstrap/js/bootstrap.min.js"></script>  
</body>
</html>  