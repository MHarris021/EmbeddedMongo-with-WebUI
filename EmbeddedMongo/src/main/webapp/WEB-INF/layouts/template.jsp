<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page session="false"%>
<html>
<head>
<title><tiles:insertAttribute name="title" /></title>
<script type="text/javascript"
	src="${resourcesLocation}/jquery-2.0.0.js">
	
</script>
<link rel="stylesheet" type="text/css"
	href="${resourcesLocation}/styles.css"></link>
</head>
<body>
	<tiles:insertAttribute name="header" />
	<p>&nbsp;</p>
	<table class="gridtable">
		<tiles:insertAttribute name="content" />
	</table>
	<script type="text/javascript">
		$(document).ready(function() {
			setTimeout(function() {
				location.reload(true);
			}, 300000);
			$('#database').click(function(event) {
				event.preventDefault();
				var formData = "{\"status\" : \"" + $(this).val() + "\"}";
				$.ajax({
					type : "PUT",
					url : "/utilities/mongo/status",
					data : formData,
					contentType : "application/json"
				}).done(function() {
					location.replace("/utilities/mongo/");
				});
			});
		});
	</script>

</body>
</html>
