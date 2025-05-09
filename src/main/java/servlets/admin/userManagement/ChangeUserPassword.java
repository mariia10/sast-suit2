package servlets.admin.userManagement;

import dbProcs.Getter;
import dbProcs.Setter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.encoder.Encode;
import utils.ShepherdLogManager;
import utils.Validate;

/**
 * Control class of the "Update player password" functionality <br>
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
public class ChangeUserPassword extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = LogManager.getLogger(ChangeUserPassword.class);
  private static String functionName = new String("Player Password Update");

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Setting IpAddress To Log and taking header for original IP if forwarded from proxy
    ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
    log.debug("*** servlets.Admin." + functionName + " ***");

    PrintWriter out = response.getWriter();
    out.print(getServletInfo());
    request.setCharacterEncoding("UTF-8");
    HttpSession ses = request.getSession(true);
    Cookie tokenCookie = Validate.getToken(request.getCookies());
    Object tokenParmeter = request.getParameter("csrfToken");
    if (Validate.validateAdminSession(ses, tokenCookie, tokenParmeter)) {
      ShepherdLogManager.setRequestIp(
          request.getRemoteAddr(),
          request.getHeader("X-Forwarded-For"),
          ses.getAttribute("userName").toString());
      if (Validate.validateTokens(tokenCookie, tokenParmeter)) {
        boolean notNull = false;
        boolean validPlayer = false;
        boolean validPassword = false;
        try {
          String ApplicationRoot = getServletContext().getRealPath("");

          log.debug("Getting Parameters");
          String player = (String) request.getParameter("player");
          log.debug("player = " + player.toString());
          String newPassword = (String) request.getParameter("password");
          log.debug("newPass = " + newPassword);

          // Validation
          notNull = (player != null) && (newPassword != null);
          if (notNull) {
            validPlayer = Getter.findPlayerById(ApplicationRoot, player);
            validPassword = Validate.isValidPassword(newPassword);
          }
          if (notNull && validPlayer && validPassword) {
            // Data is good, Add user
            log.debug("Updating Player Password");
            String reponseMessage = new String();
            if (Setter.updatePasswordAdmin(ApplicationRoot, player, newPassword)) {
              String userName = new String(Getter.getUserName(ApplicationRoot, player));
              reponseMessage +=
                  "<a>"
                      + Encode.forHtml(userName)
                      + "</a>'s password has been updated. They will have to change it upon sign"
                      + " in.<br>";
            } else {
              reponseMessage +=
                  "<font color='red'>User Password could not be updated. Please try"
                      + " again.</font><br/>";
            }
            out.print(
                "<h3 class=\"title\">"
                    + functionName
                    + " Result</h3>"
                    + "<p>"
                    + reponseMessage
                    + "<p>");
          } else {
            // Validation Error Responses
            String errorMessage = "An Error Occurred: ";
            if (!notNull) {
              log.error("Null values detected");
              errorMessage += "Invalid Request. Please try again";
            } else if (!validPlayer) {
              log.error("Player not found");
              errorMessage += "Player Not Found. Please try again";
            }
            out.print(
                "<h3 class=\"title\">"
                    + functionName
                    + " Failure</h3>"
                    + "<p><font color=\"red\">"
                    + Encode.forHtml(errorMessage)
                    + "</font><p>");
          }
        } catch (Exception e) {
          log.error(functionName + " Error: " + e.toString());
          out.print(
              "<h3 class=\"title\">"
                  + functionName
                  + " Failure</h3>"
                  + "<p>"
                  + "<font color=\"red\">An error Occurred! Please try again.</font>"
                  + "<p>");
        }
      } else {
        log.debug("CSRF Tokens did not match");
        out.print(
            "<h3 class=\"title\">"
                + functionName
                + " Failure</h3>"
                + "<p>"
                + "<font color=\"red\">An error Occurred! Please try again.</font>"
                + "<p>");
      }
    } else {
      out.print(
          "<h3 class=\"title\">"
              + functionName
              + " Failure</h3><p><font color=\"red\">An error Occurred! Please try non"
              + " administrator functions!</font><p>");
    }
    log.debug("*** " + functionName + " END ***");
  }
}
