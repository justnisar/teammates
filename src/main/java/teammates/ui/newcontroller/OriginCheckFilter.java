package teammates.ui.newcontroller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Config;
import teammates.common.util.CryptoHelper;
import teammates.common.util.Url;

/**
 * Checks and validates origin of HTTP requests.
 */
public class OriginCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to do
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (Config.isDevServer()) {
            response.setHeader("Access-Control-Allow-Origin", Config.APP_FRONTENDDEV_URL);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        if (Config.XSRF_KEY.equals(request.getParameter("xsrfkey"))) {
            // Can bypass XSRF check with the correct key
            chain.doFilter(req, res);
            return;
        }

        String referrer = request.getHeader("referer");
        if (referrer == null) {
            // Requests with missing referrer information are given the benefit of the doubt
            // to accommodate users who choose to disable the HTTP referrer setting in their browser
            // for privacy reasons
        } else if (!isHttpReferrerValid(referrer, request.getRequestURL().toString())) {
            response.setStatus(403);
            denyAccess("Invalid HTTP referrer.", response);
            return;
        }

        switch (request.getMethod()) {
        case "POST":
        case "PUT":
        case "DELETE":
            String message = getXsrfTokenErrorIfAny(request);
            if (message != null) {
                response.setStatus(403);
                denyAccess(message, response);
                return;
            }
            break;
        default:
            break;
        }

        chain.doFilter(req, res);
    }

    /**
     * Validates the HTTP referrer against the request URL.
     * The origin is the base URL of the HTTP referrer, which includes the protocol and authority
     * (host name + port number if specified).
     * Similarly, the target is the base URL of the requested action URL.
     * For the referrer to be considered valid, origin and target must match exactly.
     * Otherwise, the request is likely to be a CSRF attack, and is considered invalid.
     *
     * <p>Example of malicious request originating from embedded image in email:
     * <pre>
     * Request URL: https://teammatesv4.appspot.com/page/instructorCourseDelete?courseid=abcdef
     * Referrer:    https://mail.google.com/mail/u/0/
     *
     * Target: https://teammatesv4.appspot.com
     * Origin: https://mail.google.com
     * </pre>
     * Origin does not match target. This request is invalid.</p>
     *
     * <p>Example of legitimate request originating from instructor courses page:
     * <pre>
     * Request URL: https://teammatesv4.appspot.com/page/instructorCourseDelete?courseid=abcdef
     * Referrer:    https://teammatesv4.appspot.com/page/instructorCoursesPage
     *
     * Target: https://teammatesv4.appspot.com
     * Origin: https://teammatesv4.appspot.com
     * </pre>
     * Origin matches target. This request is valid.</p>
     */
    private boolean isHttpReferrerValid(String referrer, String requestUrl) {
        String origin;
        try {
            origin = new Url(referrer).getBaseUrl();
        } catch (AssertionError e) { // due to MalformedURLException
            return false;
        }

        if (Config.isDevServer() && origin.equals(Config.APP_FRONTENDDEV_URL)) {
            // Exception to the rule: front-end dev server requesting data from back-end dev server
            return true;
        }

        String target = new Url(requestUrl).getBaseUrl();
        return origin.equals(target);
    }

    private String getXsrfTokenErrorIfAny(HttpServletRequest request) {
        String xsrfToken = request.getHeader("X-XSRF-TOKEN");
        if (xsrfToken == null) {
            return "Missing XSRF token.";
        }

        String sessionId = request.getRequestedSessionId();
        if (sessionId == null) {
            // Newly-created session
            sessionId = request.getSession().getId();
        }

        String expectedToken = CryptoHelper.computeSessionToken(sessionId);

        return xsrfToken.equals(expectedToken) ? null : "Invalid XSRF token.";
    }

    private void denyAccess(String message, HttpServletResponse response) throws IOException {
        Map<String, Object> output = new HashMap<>();
        output.put("status", 403);
        output.put("message", message);

        JsonResult result = new JsonResult(output);
        result.send(response);
    }

    @Override
    public void destroy() {
        // nothing to do
    }

}
