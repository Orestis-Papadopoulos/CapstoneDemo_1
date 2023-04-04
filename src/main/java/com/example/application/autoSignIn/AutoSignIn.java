package com.example.application.autoSignIn;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

public class AutoSignIn {

    public void openURL(String url, String username, String password,
                        String username_css_selector, String password_css_selector,
                        String btn_cookies_css_selector, String btn_login_css_selector) {

//        FirefoxOptions options = new FirefoxOptions();
//        options.addArguments("--remote-allow-origins=*");

        // driver setup
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();

        // open url (opens in another window >> make it open in new tab)
        driver.get(url);

        // this is the window where the app is opened
        String originalWindow = driver.getWindowHandle();

        // wait until the page loads, and then click the 'OK' button for cookies
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        if (!btn_cookies_css_selector.equals("")) {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(btn_cookies_css_selector)));
            driver.findElement(By.cssSelector(btn_cookies_css_selector)).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(username_css_selector)));
        }

        // fill username
        driver.findElement(By.cssSelector(username_css_selector)).sendKeys(username);
        // fill password
        driver.findElement(By.cssSelector(password_css_selector)).sendKeys(password);
        // click login button
        driver.findElement(By.cssSelector(btn_login_css_selector)).click();

        // navigate to the browser window opened by Selenium
        for (String windowHandle : driver.getWindowHandles()) {
            if(!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
}
