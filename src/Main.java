import edu.uakron.cs.ClassOffering;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Main implements Runnable {
    private static final String
            USERNAME = "",
            PASSWORD = "";

    private final WebDriverWait waiter;
    private final WebDriver driver;

    public Main() {
        driver = new FirefoxDriver();
        waiter = new WebDriverWait(driver, 20);

    }

    public static void main(final String[] args) {
        final Main m = new Main();
        m.run();
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
        el.sendKeys(USERNAME);
        el = driver.findElement(waitName("j_password"));
        el.sendKeys(PASSWORD);

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
        sleep(5);

        List<WebElement> els;
        {
            final String subject = "3460";
            System.out.println("Changing form data ...");

            el = driver.findElement(waitId("SSR_CLSRCH_WRK_SUBJECT$2"));
            el.clear();
            el.sendKeys(subject);

            el = driver.findElement(waitId("CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH"));
            System.out.println("Searching classes ...");
            el.click();

            waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("PAGROUPBOXLABELLEVEL1")));
            els = driver.findElements(By.className("PAGROUPBOXLABELLEVEL1"));
            for (WebElement e : els) {
                el = e.findElement(By.tagName("div"));
                final String header = el.getText();
                final String[] parts = header.split("[-]");
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
                    final String
                            dates = subs.get(v + 5).findElement(By.tagName("span")).getText(),
                            units = subs.get(v + 8).findElement(By.tagName("span")).getText(),
                            status = subs.get(v + 11).findElement(By.tagName("img")).getAttribute("alt");
                    System.out.println(dates + " " + units + " " + status);

                    System.out.println(new ClassOffering(
                            parts[1],
                            parts2[0], parts2[1],
                            spanText(subs, v + 3), spanText(subs, v + 4), spanText(subs, v + 9),
                            ClassOffering.parseDays(spanText(subs, v + 2))
                    ));
                }
                System.out.println();
            }
        }
    }
}
