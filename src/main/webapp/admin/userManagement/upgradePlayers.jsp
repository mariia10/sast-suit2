<%@ page contentType="text/html; charset=iso-8859-1" language="java"
	import="java.sql.*,java.io.*,java.net.*,org.owasp.encoder.Encode, dbProcs.*, utils.*, servlets.admin.userManagement.GetPlayersByClass"
	errorPage=""%>

<%
	ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), "DEBUG: upgradePlayer.jsp *************************");

/**
 * This file is part of the Security Shepherd Project.
 * 
 * The Security Shepherd project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.<br/>
 * 
 * The Security Shepherd project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br/>
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Security Shepherd project.  If not, see <http://www.gnu.org/licenses/>. 
 * 
 * @author Mark Denihan
 */
 
if (request.getSession() != null)
{
HttpSession ses = request.getSession();
Getter get = new Getter();
//Getting CSRF Token from client
Cookie tokenCookie = null;
try
{
	tokenCookie = Validate.getToken(request.getCookies());
}
catch(Exception htmlE)
{
	ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), "DEBUG(upgradePlayers.jsp): tokenCookie Error:" + htmlE.toString());
}
// validateAdminSession ensures a valid session, and valid administrator credentials
// Also, if tokenCookie != null, then the page is good to continue loading
// Token is now validated when accessing admin pages to stop attackers causing other users to tigger logs of access attempts
Object tokenParmeter = request.getParameter("csrfToken");
if(Validate.validateAdminSession(ses, tokenCookie, tokenParmeter))
{

//Logging Username
ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), "Accessed by: " + ses.getAttribute("userName").toString(), ses.getAttribute("userName"));
// Getting Session Variables
//The org.owasp.encoder.Encode class should be used to encode any softcoded data. This should be performed everywhere for safety

String csrfToken = Encode.forHtmlAttribute(tokenCookie.getValue());
String userName = Encode.forHtml(ses.getAttribute("userName").toString());
String userRole = Encode.forHtml(ses.getAttribute("userRole").toString());
String userId = Encode.forHtml(ses.getAttribute("userStamp").toString());
String ApplicationRoot = getServletContext().getRealPath("");
boolean showClasses = false;

ResultSet classList = Getter.getClassInfo(ApplicationRoot);
try
{
	showClasses = classList.next();
}
catch(SQLException e)
{
	ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), "Could not open classList: " + e.toString());
	showClasses = false;
}
%>
<div id="formDiv" class="post">
	<h1 class="title">Upgrade Players</h1>
	<div id="upgradeDiv" class="entry">
		<form id="theForm" action="javascript:;">
			<p>Please select the player that you would like to upgrade to an
				administrator</p>
			<div id="badData"></div>
			<input type="hidden" id="csrfToken" value="<%=csrfToken%>" />
			<table align="center">
				<tr>
					<td>
						<p>Class:</p>
					</td>
					<td><select id="selectClass">
							<option value="">Unassigned Players</option>
							<%
									if(showClasses)
														{
															try
															{
																do
																{
																	String classId = Encode.forHtml(classList.getString(1));
																	String classYearName = Encode.forHtml(classList.getString(3)) + " " + Encode.forHtml(classList.getString(2));
								%>
							<option value="<%=classId%>"><%=classYearName%></option>
							<%
												}
																			while(classList.next());
																			classList.first();
																		}
																		catch(SQLException e)
																		{
																			ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), "Error occured when manipulating classList: " + e.toString());
																			showClasses = false;
																		}
																	}
											%>
					</select></td>
				</tr>
				<tr>
					<td colspan="2">
						<p>Select the players that you want to assign here</p>
						<div id="playerSelect">
							<center>
								<select id='playerId' style='width: 300px;'>
									<%= GetPlayersByClass.playersInOptionTags(Getter.getPlayersByClass(ApplicationRoot, null)) %>
								</select>
							</center>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						id="submitButton" value="Upgrade Player" /></td>
				</tr>
			</table>
		</form>
	</div>
	<br>
	<div id="loadingDiv" style="display: none;" class="menuButton">Loading...</div>
	<div id="resultDiv" style="display: none;" class="informationBox"></div>
	<div id="badData"></div>
</div>
<script>
	$("#selectClass").change(function () {
		var theCsrfToken = $('#csrfToken').val();
		var ajaxCall = $.ajax({
			type: "POST",
			url: "getPlayersByClass",
			data: {
				classId: $("#selectClass option:selected").val(),
				csrfToken: theCsrfToken
			},
			async: false
		});
		if(ajaxCall.status == 200)
		{
			$("#playerSelect").hide("fast", function(){
				if(ajaxCall.responseText == '')
				{
					$("#playerSelect").html("<p><font color='red'>Sorry, but there are no players in this class</font></p>");
				}
				else if(ajaxCall.responseText == 'fail')
				{
					$("#playerSelect").html("<p><font color='red'>Sorry, but an error occured! Please try again!</font></p>");
				}
				else
				{
					$("#playerSelect").html("<center><select id='playerId' sytle='width: 300px'>" + ajaxCall.responseText + "</select><br></center>");
				}
				$("#playerSelect").show("slow");
			});
		}
		else
		{
			$("#badData").html("<p> Sorry but there was an error: " + ajaxCall.status + " " + ajaxCall.statusText + "</p>");
		}
	});
	
	$("#theForm").submit(function(){
		//Get Data
		var theCsrfToken = $('#csrfToken').val();
		var thePlayers = $("#playerId").val();
		//Validation
		if (thePlayers == null)
		{
			$('#badData').html("<p><strong><font color='red'>Please select a player to upgrade.</font></strong></p>");
		}
		else
		{
			//Hide&Show Stuff
			$("#loadingDiv").show("fast");
			$("#badData").hide("fast");
			$("#resultDiv").hide("fast");
			$("#upgradeDiv").slideUp("fast", function(){
				//The Ajax Operation
				var ajaxCall = $.ajax({
					type: "POST",
					url: "upgradePlayer",
					data: {
						players: thePlayers, 
						csrfToken: theCsrfToken
					},
					async: false
				});
				$("#loadingDiv").hide("fast", function(){
					if(ajaxCall.status == 200)
					{
						//Now output Result Div and Show
						$("#resultDiv").html(ajaxCall.responseText);
						$("#resultDiv").show("fast");
					}
					else
					{
						$("#badData").html("<div id='errorAlert'><p> Sorry but there was an error: " + ajaxCall.status + " " + ajaxCall.statusText + "</p></div>");
						$("#badData").show("slow");
					}
					$("#upgradeDiv").slideDown("slow");
				});
			});
		}
	});
	</script>
<% if(Analytics.googleAnalyticsOn) { %><%= Analytics.googleAnalyticsScript %>
<% } %>
<%
}
else
{
response.sendRedirect("../../loggedOutSheep.html");
}
}
else
{
response.sendRedirect("../../loggedOutSheep.html");
}
%>