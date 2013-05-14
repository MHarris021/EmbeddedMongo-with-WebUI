<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%@ page session="false"%>
<tr>
	<th>Current Database is "${currentDatabase}"</th>
</tr>
<tr>
	<td>Contains the following Collections:</td>
</tr>

<table class="gridtable">
	<c:forEach var="collectionName" items="${collectionNames}">

		<tr>
			<td>${collectionName}</td>
			<td><a
				href="/utilities/mongo/databases/${currentDatabase}/collections?collectionName=${collectionName}">View</a></td>
		</tr>

	</c:forEach>

</table>
