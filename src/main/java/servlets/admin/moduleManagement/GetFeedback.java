package servlets.admin.moduleManagement;

import dbProcs.Getter;
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
import utils.ShepherdLogManager;
import utils.Validate;

/**
 * This is the control class in the Get Feedback functionality <br>
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
public class GetFeedback extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = LogManager.getLogger(GetFeedback.class);

  /**
   * This class validates it's input and returns the user with the feedback for a specific module.
   *
   * @param moduleId
   * @param csrfToken
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Setting IpAddress To Log and taking header for original IP if forwarded from proxy
    ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
    log.debug("*** servlets.Admin.GetFeedback ***");
    response.setCharacterEncoding("UTF-8");
    request.setCharacterEncoding("UTF-8");
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
        String moduleId = Validate.validateParameter(request.getParameter("moduleId"), 64);
        log.debug("moduleId: " + moduleId);
        String ApplicationRoot = getServletContext().getRealPath("");
        String htmlOutput = Getter.getFeedback(ApplicationRoot, moduleId);
        if (htmlOutput.isEmpty()) {
          htmlOutput = "No Feedback Found!";
        }
        out.write(htmlOutput);
      } else {
        out.write("Error Occurred!");
      }
    }
    log.debug("*** END servlets.Admin.GetFeedback ***");
  }
}
