<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<th>Contains the following Databases:</th>

<c:forEach var="databaseName" items="${status.databaseNames}">

	<tr>
		<td>${databaseName}</td>
		<td><a href="./databases?databaseName=${databaseName}">View</a></td>
	</tr>

</c:forEach>

