package org.example;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        ServletContext context = getServletContext();
        String prefix = context.getRealPath("webapp/WEB-INF/templates/");

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(prefix);
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=utf-8");
        Context context = new Context(
                req.getLocale(),
                Map.of("time", parseTime(req, resp))
        );

        engine.process("index", context, resp.getWriter());
        resp.getWriter().close();
    }

    private String parseTime(HttpServletRequest req, HttpServletResponse resp) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        ZonedDateTime zonedDateTime;

        if (req.getParameterMap().containsKey("timezone")) {
            String timezoneParam = req.getParameter("timezone").replace(" ", "+");
            zonedDateTime = ZonedDateTime.now(ZoneId.of(timezoneParam));
            resp.addCookie(new Cookie("lastTimezone", timezoneParam));
        } else {
            zonedDateTime = getLastTimezone(req);
        }
        return zonedDateTime.format(dateFormat);
    }

    private ZonedDateTime getLastTimezone(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    return ZonedDateTime.now(ZoneId.of(cookie.getValue()));
                }
            }
        }
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
