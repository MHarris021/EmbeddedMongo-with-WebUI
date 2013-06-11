<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h1>Current Database status:</h1>
<P>The time on the server is ${serverTime}.</P>

<table class="plaintable">
	<tr>
		<td>Currently the Database is ${status.status}</td>
		<td>
			<form action=".">
				<c:choose>
					<c:when test="${status.status == 'started'}">

						<button id="database" formmethod="PUT" type="button"
							formaction="/status" value="stopped">Stop</button>
					</c:when>
					<c:otherwise>
						<button id="database" formmethod="PUT" type="button"
							formaction="/status" value="started">Start</button>
					</c:otherwise>
				</c:choose>


			</form>
		</td>
	</tr>
	<tr>
		<td>Current Database host is ${status.host}</td>
		<td></td>
	</tr>
	<tr>
		<td>Current Database port is ${status.port}</td>
		<td></td>
	</tr>
	<tr>
	<td>&nbsp;</td>
	</tr>
	<tr>
		<td><a href="/utilities/mongo/">Back to Top level</a></td>
	</tr>
</table>
