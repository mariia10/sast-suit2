package servlets.module.challenge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
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
 * Failure to Restrict URL Access Challenge 1 <br>
 * <br>
 * This class is a red herring, displaying guest type functionality for the challenge. The
 * information required to find the admin version of this function is contained in the javascript of
 * the JSP page associated with the level <br>
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
public class UrlAccess1 extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = LogManager.getLogger(UrlAccess1.class);
  private static String levelName = "URL Access 1 (User)";

  /**
   * This class is the User Level Function Call that works correctly from the level's view without
   * manipulation This is not the correct function to target to retrieve the Result Key
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
        ResourceBundle.getBundle("i18n.servlets.challenges.urlAccess.urlAccess1", locale);

    if (Validate.validateSession(ses)) {
      ShepherdLogManager.setRequestIp(
          request.getRemoteAddr(),
          request.getHeader("X-Forwarded-For"),
          ses.getAttribute("userName").toString());
      log.debug(levelName + " servlet accessed by: " + ses.getAttribute("userName").toString());
      PrintWriter out = response.getWriter();
      out.print(getServletInfo());
      String htmlOutput = new String();

      try {
        String userData = request.getParameter("userData");
        boolean tamperedRequest = !userData.equalsIgnoreCase("4816283");
        if (!tamperedRequest) {
          log.debug("No request tampering detected");
        } else {
          log.debug("User Submitted - " + userData);
        }

        if (!tamperedRequest) {
          htmlOutput =
              "<h2 class='title'>"
                  + bundle.getString("response.status")
                  + "</h2>"
                  + "<p>"
                  + bundle.getString("response.status.message")
                  + "</p>";
        } else {
          htmlOutput =
              "<h2 class='title'>"
                  + bundle.getString("response.statusFail")
                  + "</h2>"
                  + "<p>"
                  + bundle.getString("response.statusFail.message")
                  + "</p>"
                  + "<!-- "
                  + Encode.forHtml(userData)
                  + " -->";
        }
      } catch (Exception e) {
        out.write(errors.getString("error.funky"));
        log.fatal(levelName + " - " + e.toString());
      }
      log.debug("Outputting HTML");
      out.write(htmlOutput);
    } else {
      log.error(levelName + " servlet accessed with no session");
    }
  }
}
