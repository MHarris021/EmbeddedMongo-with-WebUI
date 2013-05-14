<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%@ page session="false"%>
<thead>
	<tr>
		<th>Current Database is <a
			href="/utilities/mongo/databases?databaseName=${currentDatabase}">"${currentDatabase}"</a></th>
	</tr>
	<tr>
		<th>Current Collection is "${currentCollection}"</th>
	</tr>
</thead>
<table class="gridtable">
	<thead>
		<tr>
			<td>DBObject ID:</td>
			<td>DBObject Value:</td>
			<td>Delete</td>
		</tr>
	</thead>
	<c:forEach var="dbObject" items="${dbObjects}">

		<tr>
			<td>${dbObject.get('_id')}</td>
			<td>${dbObject}</td>
			<td><button id="${dbObject.get('_id')}Delete" value="${dbObject.get('_id')}" onclick="deleteObject(this)">Delete</button></td>
		</tr>

	</c:forEach>

</table>

<script type="text/javascript">
		function deleteObject(event) {
				var formData = $(event).val();
				$.ajax({
					type : "DELETE",
					url : "/utilities/mongo/databases/${currentDatabase}/${currentCollection}/" + formData
				}).done(function() {
					location.reload(true);
				});
			};
		
	</script>
