package servlets.module.challenge;

import dbProcs.Database;
import dbProcs.Getter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.encoder.Encode;
import utils.Hash;
import utils.ShepherdLogManager;
import utils.Validate;

/**
 * Session Management Challenge 7 <br>
 * <br>
 * This file is part of the Security Shepherd Project.
 *
 * <p>The Security Shepherd project is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.<br>
 *
 * <p>The Security Shepherd project is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.<br>
 *
 * <p>You should have received a copy of the GNU General Public License along with the Security
 * Shepherd project. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Mark Denihan
 */
public class SessionManagement7 extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = LogManager.getLogger(SessionManagement7.class);
  private static String levelName = "Session Management Challenge 7";
  public static String levelHash =
      "269d55bc0e0ff635dcaeec8533085e5eae5d25e8646dcd4b05009353c9cf9c80";

  /**
   * Users must use this functionality to sign in as an administrator to retrieve the result key.
   *
   * @param userName Sub schema user name
   * @param password Sub schema user password
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Setting IpAddress To Log and taking header for original IP if forwarded from proxy
    ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
    HttpSession ses = request.getSession(true);

    // Translation Stuff
    Locale locale = new Locale(Validate.validateLanguage(request.getSession()));
    ResourceBundle errors = ResourceBundle.getBundle("i18n.servlets.errors", locale);
    ResourceBundle bundle =
        ResourceBundle.getBundle(
            "i18n.servlets.challenges.sessionManagement.sessionManagement7", locale);

    if (Validate.validateSession(ses)) {
      ShepherdLogManager.setRequestIp(
          request.getRemoteAddr(),
          request.getHeader("X-Forwarded-For"),
          ses.getAttribute("userName").toString());
      log.debug(levelName + " servlet accessed by: " + ses.getAttribute("userName").toString());
      PrintWriter out = response.getWriter();
      out.print(getServletInfo());

      String htmlOutput = new String();
      log.debug(levelName + " Servlet Accessed");
      try {
        log.debug("Getting Cookies");
        Cookie userCookies[] = request.getCookies();
        int i = 0;
        Cookie theCookie = null;
        for (i = 0; i < userCookies.length; i++) {
          if (userCookies[i].getName().compareTo("ac") == 0) {
            theCookie = userCookies[i];
            break; // End Loop, because we found the token
          }
        }
        if (theCookie != null) {
          log.debug("Cookie value: " + theCookie.getValue());
          byte[] decodedCookieBytes = Base64.decodeBase64(theCookie.getValue());
          String decodedCookie = new String(decodedCookieBytes, "UTF-8");
          log.debug("Decoded Cookie: " + decodedCookie);
          if (decodedCookie.equals("doNotReturnAnswers")) // Untampered Cookie
          {
            log.debug("Getting Challenge Parameters");
            Object nameObj = request.getParameter("subName");
            Object passObj = request.getParameter("subPassword");
            String subName = new String();
            String subPass = new String();
            String userAddress = new String();
            if (nameObj != null) {
              subName = (String) nameObj;
            }
            if (passObj != null) {
              subPass = (String) passObj;
            }
            log.debug("subName = " + subName);
            log.debug("subPass = " + subPass);

            String ApplicationRoot = getServletContext().getRealPath("");
            Connection conn =
                Database.getChallengeConnection(
                    ApplicationRoot, "BrokenAuthAndSessMangChalFlowers");
            log.debug("Checking credentials");
            PreparedStatement callstmt;

            log.debug("Committing changes made to database");
            callstmt = conn.prepareStatement("COMMIT");
            callstmt.execute();
            log.debug("Changes committed.");

            // Filtering password for !, so that it is impossible for users to sign in
            subPass = subPass.replaceAll("!", "");

            callstmt =
                conn.prepareStatement(
                    "SELECT userName, userAddress FROM users WHERE userName = ? AND userPassword ="
                        + " SHA(?)");
            callstmt.setString(1, subName);
            callstmt.setString(2, subPass);
            log.debug("Executing authUser");
            ResultSet resultSet = callstmt.executeQuery();
            if (resultSet.next()) {
              // This should never happen. But just in case;
              log.debug("Successful Login");
              // Get key and add it to the output
              String userKey =
                  Hash.generateUserSolution(
                      Getter.getModuleResultFromHash(ApplicationRoot, levelHash),
                      (String) ses.getAttribute("userName"));
              htmlOutput =
                  "<h2 class='title'>"
                      + bundle.getString("response.welcome")
                      + " "
                      + Encode.forHtml(resultSet.getString(1))
                      + "</h2>"
                      + "<p>"
                      + ""
                      + bundle.getString("response.resultKey")
                      + " <a>"
                      + userKey
                      + "</a>"
                      + "</p>";
            } else {
              log.debug("Incorrect credentials, checking if user name correct");
              callstmt = conn.prepareStatement("SELECT userAddress FROM users WHERE userName = ?");
              callstmt.setString(1, subName);
              log.debug("Executing getAddress");
              resultSet = callstmt.executeQuery();
              if (resultSet.next()) {
                log.debug("User Found");
                userAddress =
                    bundle.getString("response.badPass")
                        + " <a>"
                        + Encode.forHtml(resultSet.getString(1))
                        + "</a><br/>";
              } else {
                userAddress = bundle.getString("response.badUser") + "<br/>";
              }
              htmlOutput = makeTable(userAddress, bundle);
            }
            Database.closeConnection(conn);
            log.debug("Outputting HTML");
          } else {
            log.debug("Tampered cookie detected");
            htmlOutput = new String(bundle.getString("response.configError"));
          }
        } else {
          log.debug("Tampered cookie detected");
          htmlOutput = new String(bundle.getString("response.configError"));
        }
        out.write(htmlOutput);
      } catch (Exception e) {
        out.write(errors.getString("error.funky"));
        log.fatal(levelName + " - " + e.toString());
      }
    } else {
      log.error(levelName + " servlet accessed with no session");
    }
  }

  private static String makeTable(String userAddress, ResourceBundle bundle) {
    return "<table>"
        + userAddress
        + "<tr><td>"
        + bundle.getString("form.userName")
        + "</td><td><input type='text' id='subName'/></td></tr>"
        + "<tr><td>"
        + bundle.getString("form.password")
        + "</td><td><input type='password' id='subPassword'/></td></tr>"
        + "<tr><td colspan='2'><div id='submitButton'><input type='submit' value='"
        + bundle.getString("form.signIn")
        + "'/>"
        + "</div></td></tr>"
        + "</table>";
  }
}
