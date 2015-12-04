import edu.uakron.cs.ClassOffering;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ZiplineDriver implements Runnable {
    private static boolean KEEP_ALIVE = false;
    private final BlockingQueue<String> sections;
    private final String username, password;
    private final WebDriverWait waiter;
    private final WebDriver driver;

    public static void main(final String[] params) {
        if (params.length != 2) return;
        KEEP_ALIVE = true;
        final ZiplineDriver d = new ZiplineDriver(params[0], params[1]);
        d.run();
    }

    public ZiplineDriver(final String username, final String password) {
        sections = new LinkedBlockingQueue<>();
        driver = new FirefoxDriver();
        waiter = new WebDriverWait(driver, 20);

        this.username = username;
        this.password = password;

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
        new Select(el).selectByVisibleText("2016 Spring");
        sleep(3);
        el = driver.findElement(waitId("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$8"));
        el.click();

        sleep(1);

        final Thread t = new Thread(getGrabber());
        t.setDaemon(!KEEP_ALIVE);
        t.start();

        sections.offer("3460");
        sections.offer("7700");
    }

    private Runnable getGrabber() {
        return () -> {
            for (; ; ) {
                final String subject;
                try {
                    subject = sections.poll(5, TimeUnit.SECONDS);
                } catch (final InterruptedException ignored) {
                    break;
                }
                if (subject == null) continue;

                System.out.printf("Changing form subject to %s ...%n", subject);

                WebElement el = driver.findElement(waitId("SSR_CLSRCH_WRK_SUBJECT$2"));
                el.clear();
                el.sendKeys(subject);

                el = driver.findElement(waitId("CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH"));
                System.out.println("Searching classes ...");
                el.click();

                waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("PAGROUPBOXLABELLEVEL1")));
                List<WebElement> els = driver.findElements(By.className("PAGROUPBOXLABELLEVEL1"));
                for (WebElement e : els) {
                    el = e.findElement(By.tagName("div"));
                    final String header = el.getText();
                    final String[] parts = header.split("[-]", 2);
                    if (parts.length != 2) {
                        System.out.println("WARNING: SKIPPING " + header);
                        continue;
                    }
                    final String[] parts2 = parts[0].trim().split("\\s");
                    parts[1] = parts[1].trim();
                    el = parent(parent(e));
                    final List<WebElement> subs = el.findElements(By.className("PSLEVEL3GRIDROW"));
                    final int classes = subs.size() / 13;
                    if (subs.size() % 13 != 0) {
                        System.out.println("WARNING: INVALID FORMAT DETECTED ! Skipping.");
                        continue;
                    }
                    for (int i = 0, v = 0; i < classes; ++i, v += 13) {
                        System.out.println(spanText(subs, v + 5));

                        System.out.println(new ClassOffering(
                                aText(subs, v), aText(subs, v + 1).replace('\n', ' '),
                                parts[1],
                                parts2[0], parts2[1],
                                spanText(subs, v + 3), spanText(subs, v + 4), spanText(subs, v + 9),
                                ClassOffering.parseDays(spanText(subs, v + 2)),
                                subs.get(v + 11).findElement(By.tagName("img")).getAttribute("alt").trim().equalsIgnoreCase("open"),
                                Integer.parseInt(spanText(subs, v + 8))
                        ));
                    }
                    System.out.println();
                }

                el = driver.findElement(waitId("CLASS_SRCH_WRK2_SSR_PB_MODIFY"));
                el.click();
                sleep(3);
            }
        };
    }
}
