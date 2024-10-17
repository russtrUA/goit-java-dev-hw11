package ua.goit.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timeZoneParameter = req.getParameter("timezone");
        if (timeZoneParameter != null) {
            try {
                int offsetHours = Integer.parseInt(timeZoneParameter.substring(3).trim());
                if (offsetHours < -18 || offsetHours > 18) {
                    throw new NumberFormatException("Value must be in range -18 to +18");
                }
            } catch (NumberFormatException e) {
                sendBadResponse(res);
                throw e;
            }
        }
        chain.doFilter(req, res);
    }
    private void sendBadResponse(HttpServletResponse res) throws IOException {
        res.setStatus(400);
        res.setContentType("text/html");
        res.getWriter().write("<h2>Invalid timezone</h2>");
        res.getWriter().close();
    }
}
