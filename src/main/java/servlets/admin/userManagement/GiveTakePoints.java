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
 * Control class of the "Give Take Points" functionality <br>
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
public class GiveTakePoints extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = LogManager.getLogger(GiveTakePoints.class);
  private static String functionName = new String("Give/Take Points");

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Setting IpAddress To Log and taking header for original IP if forwarded from proxy
    ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
    log.debug("*** servlets.Admin." + functionName + " ***");

    PrintWriter out = response.getWriter();
    out.print(getServletInfo());
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
        try {
          String ApplicationRoot = getServletContext().getRealPath("");

          log.debug("Getting Parameters");
          String player = (String) request.getParameter("player");
          log.debug("player = " + player.toString());
          String amountOfPointsString = (String) request.getParameter("numberOfPoints");
          log.debug("amountOfPointsString = " + amountOfPointsString);
          int amountOfPoints = Integer.parseInt(amountOfPointsString);

          // Validation
          notNull = (player != null) && (amountOfPoints != 0);
          if (notNull) {
            validPlayer = Getter.findPlayerById(ApplicationRoot, player);
          }
          if (notNull && validPlayer) {
            // Data is good, Add user
            log.debug("Updating Player Score by " + amountOfPointsString + " points");
            String responseMessage = new String();
            if (Setter.updateUserPoints(ApplicationRoot, player, amountOfPoints)) {
              String userName = new String(Getter.getUserName(ApplicationRoot, player));
              responseMessage += "<a>" + Encode.forHtml(userName) + "</a> has been ";
              if (amountOfPoints >= 0) {
                responseMessage += "given";
              } else {
                responseMessage += "deducted";
              }
              responseMessage += " <b>" + amountOfPoints + "</b> points.<br>";
            } else {
              responseMessage +=
                  "<font color='red'>User score could not be updated. Please try"
                      + " again.</font><br/>";
            }
            String htmlOutput = "<h3 class=\"title\"> Points ";
            if (amountOfPoints >= 0) {
              htmlOutput += "Added ";
            } else {
              htmlOutput += "Subtracted";
            }
            htmlOutput += "</h3>" + "<p>" + responseMessage + "<p>";
            out.write(htmlOutput);
          } else {
            // Validation Error Responses
            String errorMessage = "An Error Occurred: ";
            if (!notNull) {
              log.error("Bad values detected");
              errorMessage += "Invalid Request. Please try again";
            } else if (!validPlayer) {
              log.error("Player not found");
              errorMessage += "Player Not Found. Please try again";
            }
            out.print(
                "<h3 class=\"title\">"
                    + functionName
                    + " Failure</h3><br>"
                    + "<p><font color=\"red\">"
                    + Encode.forHtml(errorMessage)
                    + "</font><p>");
          }
        } catch (Exception e) {
          log.error(functionName + " Error: " + e.toString());
          out.print(
              "<h3 class=\"title\">"
                  + functionName
                  + " Failure</h3><br>"
                  + "<p>"
                  + "<font color=\"red\">An error Occurred! Please try again.</font>"
                  + "<p>");
        }
      } else {
        log.debug("CSRF Tokens did not match");
        out.print(
            "<h3 class=\"title\">"
                + functionName
                + " Failure</h3><br>"
                + "<p>"
                + "<font color=\"red\">An error Occurred! Please try again.</font>"
                + "<p>");
      }
    } else {
      out.print(
          "<h3 class=\"title\">"
              + functionName
              + " Failure</h3><br><p><font color=\"red\">An error Occurred! Please try non"
              + " administrator functions!</font><p>");
    }
    log.debug("*** " + functionName + " END ***");
  }
}
