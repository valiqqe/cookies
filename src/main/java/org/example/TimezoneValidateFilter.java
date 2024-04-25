package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezoneParam = req.getParameter("timezone");

        if (timezoneParam!= null && !timezoneParam.isEmpty()) {
            timezoneParam = timezoneParam.replace(" ", "+");
            if (!isValidTimezone(timezoneParam)) {
                res.setContentType("text/html; charset=utf-8");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Invalid timezone");
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private boolean isValidTimezone(String timezoneParam) {
        try {
            ZoneId.of(timezoneParam);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
