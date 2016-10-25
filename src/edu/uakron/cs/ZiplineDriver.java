package edu.uakron.cs;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ZiplineDriver implements Runnable {
    private static boolean KEEP_ALIVE = false;
    private final Set<ClassOffering> offerings;
    private final ConcurrentLinkedDeque<RosterUpdated> listeners;
    private final BlockingQueue<String> sections;
    private final Set<String> done = new HashSet<>();
    private final String username, password;
    private final WebDriverWait waiter;
    private final WebDriver driver;
    private final LinkedList<String> needStr;

    public static void main(final String[] params) {
        if (params.length != 2) return;
        KEEP_ALIVE = true;
        final ZiplineDriver d = new ZiplineDriver(params[0], params[1]);
        d.run();
    }

    public ZiplineDriver(final String username, final String password) {
        offerings = new ConcurrentHashSet<>();
        listeners = new ConcurrentLinkedDeque<>();
        sections = new LinkedBlockingQueue<>();
        driver = new FirefoxDriver();
        waiter = new WebDriverWait(driver, 20);

        needStr = new LinkedList<>();

        this.username = username;
        this.password = password;

        Runtime.getRuntime().addShutdownHook(new Thread(driver::quit));

        final Thread t = new Thread(() -> {
            final DARSParser parser = new DARSParser(username, password);
            parser.run();
            for (final DARSParser.Need need : parser.needs) {
                System.out.println(need);
                final DARSParser.Accept[] accepts = need.getAccepts();
                System.out.println(Arrays.toString(accepts));
                for (final DARSParser.Accept accept : accepts) {
                    final String c = accept.c;
                    final String[] a = c.split("OR");
                    for (final String s : a) {
                        final String[] a2 = s.split("[:]");
                        if (a2.length < 2) continue;
                        a2[1] = a2[1].substring(0, 3);
                        needStr.add(a2[0] + ":" + a2[1].replace('*', '.'));
                    }
                }
            }

            for (final RosterUpdated u : listeners) u.updated(needStr);
        });
        t.setDaemon(true);
        t.start();
    }

    public void offer(final String s) {
        sections.offer(s);
    }

    public void addListener(final RosterUpdated l) {
        if (listeners.contains(l)) return;
        listeners.add(l);
    }

    public void removeListener(final RosterUpdated l) {
        listeners.remove(l);
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

    private WebElement parent(final WebElement e) {
        return e.findElement(By.xpath(".."));
    }

    private String aText(final List<WebElement> subs, final int index) {
        return subs.get(index).findElement(By.tagName("a")).getText();
    }

    private String spanText(final List<WebElement> subs, final int index) {
        return subs.get(index).findElement(By.tagName("span")).getText();
    }

    @Override
    public void run() {
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
        el = driver.findElement(waitLink("Student Center"));
        System.out.println("Clicked!");
        el.click();

        System.out.println("Waiting for shitty JSP iframe (why akron, why?) ...");
        el = driver.findElement(waitId("ptifrmtgtframe"));
        System.out.println("Switching context ...");
        driver.switchTo().frame(el);

        System.out.println("Waiting for class search ...");
        el = driver.findElement(waitId("DERIVED_SSS_SCR_SSS_LINK_ANCHOR1"));
        System.out.println("Clicked!");
        el.click();

        System.out.println("Waiting for search form ...");
        el = driver.findElement(waitId("CLASS_SRCH_WRK2_STRM$35$"));
        System.out.println("Selecting semester ...");
        new Select(el).selectByVisibleText("2017 Spring");
        sleep(3);
        el = driver.findElement(waitId("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$8"));
        el.click();

        sleep(1);

        final Thread t = new Thread(getGrabber());
        t.setDaemon(!KEEP_ALIVE);
        t.start();

        if (KEEP_ALIVE) {
            sections.offer("3460");
        }
    }

    private Runnable getGrabber() {
        return () -> {
            for (; ; ) {
                String subject;
                try {
                    subject = sections.poll(5, TimeUnit.SECONDS);
                } catch (final InterruptedException ignored) {
                    break;
                }
                if (subject == null || (subject = subject.trim()).isEmpty()) continue;
                if (done.contains(subject)) continue;
                done.add(subject);

                System.out.printf("Changing form subject to %s ...%n", subject);

                WebElement el = driver.findElement(waitId("SSR_CLSRCH_WRK_SUBJECT$2"));
                el.clear();
                el.sendKeys(subject);

                el = driver.findElement(waitId("CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH"));
                System.out.println("Searching classes ...");
                el.click();

                try {
                    waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("PAGROUPBOXLABELLEVEL1")));
                } catch (final Exception ignored) {
                    System.out.println("Skipping " + subject);
                    continue;
                }
                List<WebElement> els = driver.findElements(By.className("PAGROUPBOXLABELLEVEL1"));
                els.parallelStream().map(e -> {
                    WebElement el1 = e.findElement(By.tagName("div"));
                    final String header = el1.getText();
                    final String[] parts = header.split("[-]", 2);
                    if (parts.length != 2) {
                        System.out.println("WARNING: SKIPPING " + header);
                        return null;
                    }
                    final String[] parts2 = parts[0].trim().split("\\s");
                    parts[1] = parts[1].trim();
                    el1 = parent(parent(e));
                    final List<WebElement> subs = el1.findElements(By.className("PSLEVEL3GRIDROW"));
                    final int classes = subs.size() / 13;
                    if (subs.size() % 13 != 0) {
                        System.out.println("WARNING: INVALID FORMAT DETECTED! Skipping.");
                        return null;
                    }
                    final List<Integer> iL = new ArrayList<>(classes);
                    for (int i = 0; i < classes; ++i) {
                        iL.add(13 * i);
                    }
                    iL.parallelStream().map(v -> offerings.add(new ClassOffering(
                            aText(subs, v), aText(subs, v + 1).replace('\n', ' '),
                            parts[1],
                            parts2[0], parts2[1],
                            spanText(subs, v + 3), spanText(subs, v + 4), spanText(subs, v + 9),
                            ClassOffering.parseDays(spanText(subs, v + 2)),
                            subs.get(v + 11).findElement(By.tagName("img")).getAttribute("alt").trim().equalsIgnoreCase("open"),
                            Integer.parseInt(spanText(subs, v + 8))
                    ))).count();
                    return null;
                }).count();


                System.out.println("Completed fetching classes for " + subject + " ...");

                el = driver.findElement(waitId("CLASS_SRCH_WRK2_SSR_PB_MODIFY"));
                el.click();
                sleep(3);

                for (final RosterUpdated l : listeners) l.updated(offerings);
            }
        };
    }
}
