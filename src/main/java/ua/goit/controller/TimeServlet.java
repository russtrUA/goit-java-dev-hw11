package ua.goit.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        String timeZoneParameter = req.getParameter("timezone");
        ZoneOffset offset = ZoneOffset.UTC;
        String chosenTimeZone;
        if (timeZoneParameter != null) {
            int offsetHours = Integer.parseInt(timeZoneParameter.substring(3).trim());
            offset = ZoneOffset.ofHours(offsetHours);
            chosenTimeZone = timeZoneParameter.replace(" ","+");
            Cookie lastTimezone = new Cookie("lastTimezone", chosenTimeZone);
            lastTimezone.setMaxAge(900);
            resp.addCookie(lastTimezone);
        } else {
            chosenTimeZone = "UTC";
            Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    chosenTimeZone = cookie.getValue();
                    offset = ZoneOffset.ofHours(Integer.parseInt(chosenTimeZone.substring(3).trim()));
                    break;
                }
            }

        }
        ZonedDateTime utcTime = ZonedDateTime.now(offset);
        String formattedDateTime = utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")) + chosenTimeZone;
        Context context = new Context();
        context.setVariable("currentTime", formattedDateTime);
        engine.process("timezone", context, resp.getWriter());
        resp.getWriter().close();
    }
}
