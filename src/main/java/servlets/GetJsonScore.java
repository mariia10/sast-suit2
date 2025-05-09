package servlets;

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
import servlets.admin.moduleManagement.GetFeedback;
import utils.ScoreboardStatus;
import utils.ShepherdLogManager;
import utils.Validate;

/**
 * This control class returns a JSON array containing Scoreboard data for a class defined in
 * utils.ScoreboardStatus <br>
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
public class GetJsonScore extends HttpServlet {

  private static final long serialVersionUID = -6168706954346341697L;
  private static final Logger log = LogManager.getLogger(GetFeedback.class);

  /**
   * Used to return an administrator with the current progress of each player in a class. This will
   * require a complex client page to parse the returned JSON information to make a very pretty
   * score board
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Setting IpAddress To Log and taking header for original IP if forwarded from
    // proxy

    ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
    response.setContentType("application/json; charset=UTF-8");
    request.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();
    out.print(getServletInfo());
    HttpSession ses = request.getSession(true);
    boolean loggedIn = Validate.validateSession(ses);
    if (loggedIn || ScoreboardStatus.isPublicScoreboard()) {

      if (loggedIn) {
        ShepherdLogManager.setRequestIp(
            request.getRemoteAddr(),
            request.getHeader("X-Forwarded-For"),
            ses.getAttribute("userName").toString());
        log.debug("Scoreboard accessed by " + ses.getAttribute("userName").toString());
      } else {
        log.debug("Scoreboard accessed by someone not logged in.");
      }
      boolean canSeeScoreboard =
          ScoreboardStatus.canSeeScoreboard((String) ses.getAttribute("userRole"));
      Cookie tokenCookie = Validate.getToken(request.getCookies());
      Object tokenParmeter = request.getParameter("csrfToken");
      String scoreboardClass = new String();
      if ((Validate.validateTokens(tokenCookie, tokenParmeter) && canSeeScoreboard)
          || ScoreboardStatus.isPublicScoreboard()) {
        // What Class to List?
        if (ScoreboardStatus.getClassSpecificScoreboard()) {
          if (ses.getAttribute("userRole").toString().compareTo("admin") == 0) {
            // Admin should get default class Scoreboard
            scoreboardClass = Register.getDefaultClass();
          } else {
            // User should get their class scoreboard
            if (ses.getAttribute("userClass") != null) {
              scoreboardClass = ses.getAttribute("userClass").toString();
            } else {
              scoreboardClass = new String();
            }
          }
        } else {
          scoreboardClass = ScoreboardStatus.getScoreboardClass();
        }
        String applicationRoot = getServletContext().getRealPath("");
        String jsonOutput = Getter.getJsonScore(applicationRoot, scoreboardClass);

        out.write(jsonOutput);
      } else {
        if (!canSeeScoreboard) {
          out.write("ERROR: Scoreboard is not currently available");
        } else {
          out.write("ERROR: Please refresh page!");
        }
      }
    } else {
      log.debug("Unauthenticated Scoreboard Request");
      out.write("ERROR: You are logged out. Please refresh the page");
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.sendRedirect("scoreboard.jsp");
  }
}
