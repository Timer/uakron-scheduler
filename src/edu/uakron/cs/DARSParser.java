package edu.uakron.cs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DARSParser implements Runnable {
    private final String username, password;
    private final String content;
    public final LinkedList<Need> needs;
    private final WebDriverWait waiter;
    private final WebDriver driver;

    public DARSParser(final String content) {
        this(null, null, content);
    }

    public DARSParser(final String username, final String password) {
        this(username, password, null);
    }

    public DARSParser(final String username, final String password, final String content) {
        this.content = content;
        needs = new LinkedList<>();
        driver = new FirefoxDriver();
        waiter = new WebDriverWait(driver, 30);

        this.username = username;
        this.password = password;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                driver.quit();
            } catch (final Throwable ignored) {
            }
        }));
    }

    public static void main(final String[] params) throws Exception {
        final String s = new String(Files.readAllBytes(Paths.get("./joe.html")));
        final DARSParser d = new DARSParser(s);
        d.run();
        for (final Need need : d.needs) {
            System.out.println(need);
            System.out.println(Arrays.toString(need.getAccepts()));
        }
    }

    @Override
    public void run() {
        String content = this.content;
        if (content == null) content = fetchContent();
        driver.quit();
        final ArrayList<String> l = getPreBlocks(content);
        final Map<Req, List<Req>> requirements = new HashMap<>();
        for (final String pre : l) {
            if (!pre.contains("NEEDS")) continue;
            String sub = pre;
            int n;
            Req parent = null;
            while ((n = sub.indexOf("NEEDS:")) != -1) {
                sub = sub.substring(n + 6);
                String data = sub.substring(0, sub.indexOf("<")).trim().toLowerCase();
                final int nn = sub.indexOf("NEEDS:");
                final Req r = new Req(data, nn != -1 ? sub.substring(0, nn) : sub);
                if (parent != null) {
                    requirements.get(parent).add(r);
                } else requirements.put(parent = r, new LinkedList<>());
            }
        }
        needs.clear();
        for (final Map.Entry<Req, List<Req>> e : requirements.entrySet()) {
            for (final Req r : e.getValue()) {
                final String[] checks = {"course", "credit"};
                final String req = r.toString();
                final String[] cats = req.split("\\s");
                for (int cati = 0; cati < cats.length; ++cati) {
                    final String cat = cats[cati];
                    for (final String check : checks) {
                        if (cat.contains(check)) {
                            final int count = (int) Math.ceil(Double.parseDouble(cats[cati - 1]));
                            String body = "";
                            String[] bodyArr = r.body.split("\n");
                            for (final String ba : bodyArr) if (ba.contains("AcceptCourse")) body += ba;
                            needs.add(new Need(count, check, body));
                        }
                    }
                }
            }
        }
    }

    private By waitId(final String id) {
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return By.id(id);
    }

    private By waitName(final String name) {
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
        return By.name(name);
    }

    private By waitLink(final String text) {
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(text)));
        return By.partialLinkText(text);
    }

    private void sleep(final int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchContent() {
        System.out.println("Navigating to zipline ...");
        driver.get("http://zipline.uakron.edu");
        System.out.println("Waiting for login ...");
        WebElement el = driver.findElement(waitId("uanetid"));
        System.out.println("Logging in ...");
        el.sendKeys(username);
        el = driver.findElement(waitName("j_password"));
        el.sendKeys(password);

        el = driver.findElement(By.id("submitimage"));
        el.click();

        System.out.println("Waiting for Student Center ...");
        el = driver.findElement(waitLink("DARS"));
        System.out.println("Clicked!");
        driver.get(el.getAttribute("href"));

        System.out.println("Waiting for DARS ...");
        el = driver.findElement(By.id("OPTION1RESULTS"));
        sleep(2);
        System.out.println("Running report ...");
        el = el.findElement(By.cssSelector("input[type='Button']"));
        el.click();
        System.out.println("Searching for main form ...");
        driver.findElement(By.cssSelector("form[name='mainForm']"));
        System.out.println("Waiting for for the audit to run ...");
        sleep(8);
        System.out.println("Attempting to open audit ...");
        String handle = driver.getWindowHandle();
        el = driver.findElement(By.cssSelector("input[value='Open Audit']"));
        el.click();
        sleep(3);
        driver.close();
        for (String winHandle : driver.getWindowHandles()) {
            if (winHandle.equals(handle)) continue;
            System.out.println("Switching browsers ...");
            driver.switchTo().window(winHandle);
            break;
        }
        sleep(5);
        return driver.getPageSource();
    }

    public static class Accept {
        public final String c;
        public final boolean required;

        private Accept(String c) {
            c = c.trim();
            this.required = c.contains("(R)");
            if (required) c = c.replace("(R)", "").trim();
            this.c = c;
        }

        @Override
        public String toString() {
            return "{ c: " + c + ", r: " + required + " }";
        }
    }

    public static class Need {
        private final int count;
        private final String type;
        private final String data;

        public Need(final int count, final String type, final String data) {
            this.count = count;
            this.type = type;
            this.data = data;
        }

        public Accept[] getAccepts() {
            String d = data;
            int p;
            String from = "";
            while ((p = d.indexOf("<a")) != -1) {
                int oi = d.indexOf("OR");
                final boolean or = oi > -1 && oi < p;
                d = d.substring(p + 3);
                int e = d.indexOf("</a>");
                String sub = d.substring(d.indexOf(">") + 1, e);
                from += (or ? " OR " : ", ") + sub;
                d = d.substring(e);
            }
            if (from.length() > 2) from = from.substring(2);
            final String[] strs = from.split(",");
            final Accept[] accepts = new Accept[strs.length];
            int count = 0;
            for (int index = 0; index < strs.length; ++index) {
                if (strs[index].trim().length() < 1) continue;
                ++count;
                accepts[index] = new Accept(strs[index]);
            }
            return Arrays.copyOf(accepts, count);
        }

        @Override
        public String toString() {
            return "Need[count=" + count + ",type=" + type + "]";
        }
    }

    private static class Req {
        private final String s, body;

        public Req(final String s, final String body) {
            this.s = s;
            this.body = body;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static ArrayList<String> getPreBlocks(String s) {
        final ArrayList<String> ll = new ArrayList<>();
        int index;
        final String st = "<pre>", sp = "</pre>";
        while ((index = s.indexOf(st)) != -1) {
            s = s.substring(index + st.length());
            index = s.indexOf(sp);
            if (index != -1) {
                ll.add(s.substring(0, index));
            }
        }
        return ll;
    }
}
