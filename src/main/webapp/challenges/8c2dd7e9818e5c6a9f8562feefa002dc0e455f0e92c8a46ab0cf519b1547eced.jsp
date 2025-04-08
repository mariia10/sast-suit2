<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	language="java" import="utils.*" errorPage=""%>
<%@ page import="java.util.Locale, java.util.ResourceBundle"%>
<%
/**
 * SQL Injection Challenge 7
 * <br/><br/>
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

 String levelName = "SQL Injection Challenge 7";
 String levelHash = "8c2dd7e9818e5c6a9f8562feefa002dc0e455f0e92c8a46ab0cf519b1547eced";
 
 //Translation Stuff
 Locale locale = new Locale(Validate.validateLanguage(request.getSession()));
 ResourceBundle bundle = ResourceBundle.getBundle("i18n.challenges.injection." + levelHash, locale);
 //Used more than once translations
 String i18nLevelName = bundle.getString("challenge.challengeName");
 
 ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), levelName + " Accessed");
 if (request.getSession() != null)
 {
 	HttpSession ses = request.getSession();
 	//Getting CSRF Token from client
 	Cookie tokenCookie = null;
 	try
 	{
 		tokenCookie = Validate.getToken(request.getCookies());
 	}
 	catch(Exception htmlE)
 	{
 		ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), levelName +".jsp: tokenCookie Error:" + htmlE.toString());
 	}
 	// validateSession ensures a valid session, and valid role credentials
 	// If tokenCookie == null, then the page is not going to continue loading
 	if (Validate.validateSession(ses) && tokenCookie != null)
 	{
 		ShepherdLogManager.logEvent(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), levelName + " has been accessed by " + ses.getAttribute("userName").toString(), ses.getAttribute("userName"));

%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Security Shepherd - <%= i18nLevelName %></title>
<link href="../css/lessonCss/theCss.css" rel="stylesheet"
	type="text/css" media="screen" />

</head>
<body>
	<script type="text/javascript" src="../js/jquery.js"></script>
	<script type="text/javascript"
		src="../js/clipboard-js/clipboard.min.js"></script>
	<script type="text/javascript" src="../js/clipboard-js/tooltips.js"></script>
	<script type="text/javascript"
		src="../js/clipboard-js/clipboard-events.js"></script>
	<div id="contentDiv">
		<h2 class="title"><%= i18nLevelName %></h2>
		<p>
			<%= bundle.getString("challenge.description") %>
		<form id="leForm" action="javascript:;">
			<table>
				<tr>
					<td colspan="2"><%= bundle.getString("challenge.form.pleaseEnterCredentials") %>
					</td>
				</tr>
				<tr>
					<td><%= bundle.getString("challenge.form.email") %></td>
					<td><input style="width: 400px;" id="subEmail" type="text"
						autocomplete="off" /></td>
				</tr>
				<tr>
					<td><%= bundle.getString("challenge.form.password") %></td>
					<td><input style="width: 400px;" id="subPass" type="password"
						autocomplete="off" /></td>
				</tr>
				<tr>
					<td>
						<div id="submitButton">
							<input type="submit"
								value="<%= bundle.getString("challenge.form.button.value") %>" />
						</div>
						<p style="display: none;" id="loadingSign"><%= bundle.getString("sign.loading") %>...
						</p>
					</td>
				</tr>
			</table>
		</form>

		<div id="resultsDiv"></div>
		</p>
	</div>
	<script>
			var counter = 0;
			$("#leForm").submit(function(){
				$("#submitButton").hide("fast");
				$("#loadingSign").show("slow");
				var theEmail = $("#subEmail").val();
				var thePass = $("#subPass").val();
				$("#resultsDiv").hide("slow", function(){
					var ajaxCall = $.ajax({
						type: "POST",
						url: "<%= levelHash %>",
						data: {
							subEmail: theEmail,
							subPassword: thePass
						},
						async: false
					});
					if(ajaxCall.status == 200)
					{
						$("#resultsDiv").html(ajaxCall.responseText);
					}
					else
					{
						$("#resultsDiv").html("<p> <%= bundle.getString("error.occurred") %>: " + ajaxCall.status + " " + ajaxCall.statusText + "</p>");
					}
					$("#resultsDiv").show("slow", function(){
						$("#loadingSign").hide("fast", function(){
							$("#submitButton").show("slow");
						});
					});
				});
			});
			$("#aUserId").change(function () {
				$("#userContent").text($(this).val());
			}).change();
			$("#theHintButton").click(function() {
				$("#hintButton").hide("fast", function(){
					$("#hint").show("fast");
				});
			});;
		</script>
	<% if(Analytics.googleAnalyticsOn) { %><%= Analytics.googleAnalyticsScript %>
	<% } %>
</body>
</html>
<%
	}
	else
	{
		response.sendRedirect("../loggedOutSheep.html");
	}
}
else
{
	response.sendRedirect("../loggedOutSheep.html");
}
%>