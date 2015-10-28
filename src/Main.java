import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class Main {
    public static void main(final String[] args) throws IOException {
        /**
         * Get directed to the login page.
         */
        final Connection.Response r = Jsoup.connect("https://zipline.uakron.edu/psp/portprod/?cmd=login").followRedirects(true).method(Connection.Method.GET).execute();
        final Document d = r.parse();
        final Element e = d.select("form").get(0);
        final HashMap<String, String> cookies = new HashMap<>();
        /**
         * Store our session cookies.
         */
        cookies.putAll(r.cookies());

        /**
         * Post our login details.
         */
        final URL u = r.url();
        final Connection login = Jsoup.connect(u.getProtocol() + "://" + u.getHost() + "/" + e.attr("action")).followRedirects(true).cookies(cookies);
        final Elements input = e.select("input");
        for (final Element el : input) {
            final String s = el.attr("name");
            if (s == null || s.isEmpty()) continue;
            if (s.equals("j_username")) {
                login.data(s, "");
            } else if (s.equals("j_password")) {
                login.data(s, "");
            } else throw new RuntimeException("Unsupported input field");
        }
        final Connection.Response r2 = login.method(Connection.Method.POST).execute();
        cookies.putAll(r2.cookies());

        /**
         * Continue since we "do not support" javascript. ;)
         */
        final Document lp = r2.parse();
        final Element cForm = lp.select("form").get(0);
        final Connection cont = Jsoup.connect(cForm.attr("action")).followRedirects(true).cookies(cookies);
        for (final Element el : cForm.select("input")) {
            final String s = el.attr("name");
            if (s == null || s.isEmpty()) continue;
            cont.data(s, el.attr("value"));
        }
        final Connection.Response r3 = cont.method(Connection.Method.POST).execute();
        cookies.putAll(r3.cookies());

        /**
         * Follow some HTML redirects to set more cookies...
         */
        final String baseURL = r3.url().getProtocol() + "://" + r3.url().getHost();
        final Document i1 = r3.parse();
        final String f1 = i1.select("meta").get(0).attr("content").split("URL=")[1];
        final Connection.Response r4 = Jsoup.connect(baseURL + f1).followRedirects(true).cookies(cookies).method(Connection.Method.GET).execute();
        cookies.putAll(r4.cookies());
        final String _goto = r4.parse().select("head").get(0).toString();
        final String redirect = _goto.split("[']")[1];
        final Connection.Response r5 = Jsoup.connect(redirect).followRedirects(true).cookies(cookies).method(Connection.Method.GET).execute();
        cookies.putAll(r5.cookies());
        final Elements pageEls = r5.parse().select("#ADMN_UA_MAX_SERVICES_HMPG");
        String student_center = null, dars = null;
        for (Element link : pageEls.select("table").select("a")) {
            if ("Student Center".equals(link.text())) {
                student_center = link.attr("href");
            } else if ("DARS".equals(link.text())) {
                dars = link.attr("href");
            }
        }
        final Connection.Response r6 = Jsoup.connect(student_center).followRedirects(true).cookies(cookies).method(Connection.Method.GET).execute();
        cookies.putAll(r6.cookies());
        System.out.println(r6.parse());
        System.out.println(student_center);
        System.out.println(dars);
    }
}
