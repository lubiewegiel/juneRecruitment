package com.selenium.test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Main {

    public static void main(String[] args) throws InterruptedException {


        WebDriver driver = new FirefoxDriver();
//        WebDriver driver = new ChromeDriver();
        String baseUrl = "https://www.f-secure.com/";

//        driver.manage().window().setSize(new Dimension(800,1200));

        driver.get(baseUrl);

        System.out.println(driver.getTitle());
        
        openCareerPageFromHomePage(driver);

        driver.findElement(By.xpath("(//a[contains(text(),'See our open positions')])[2]")).click();

        WebElement citiesDropdownList = driver.findElement(By.cssSelector("[data-id=\"job-city\"]"));
        citiesDropdownList.click();

        driver.findElement(By.xpath("//*[contains(text(), 'Poznań')]")).click();

        Thread.sleep(200);

        WebElement qualityEngineerJobOfferTitle = driver
                .findElement(By.xpath("//h2[contains(text(), 'Quality Engineer')]"));

        System.out.println(qualityEngineerJobOfferTitle.getSize().toString());

        Assert.assertTrue(qualityEngineerJobOfferTitle.isDisplayed());
        driver.close();
    }

    public static void openCareerPageFromHomePage(WebDriver driver) throws InterruptedException {

        acceptCookiesUsage(driver);

        String career = handleTranslationsAtHomePage(driver.getCurrentUrl());
        WebElement careerSectionTextLink;
        boolean overlayAlreadyHidden = false;

        // Handling with responsiveness - if career link is under 'about' section - works with ChromeDriver
        try {
            driver.findElement(By.linkText(career));
        } catch (NoSuchElementException e) {
            System.out.println("Career was not visible");
            driver.findElement(By.xpath("//div[@id='wrapper']/footer/div/div/div[2]/div/a")).click();
            overlayAlreadyHidden = true;
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(1000);
        }

        careerSectionTextLink = driver.findElement(By.linkText(career));

        // For FirefoxDriver 'career' link is visible but not interactable - exception is thrown only after interaction attempt
        try {
            careerSectionTextLink.click();
        }catch (ElementNotInteractableException e1) {
            System.out.println("Could not scroll to element - Firefox Driver Exception");
            if (!overlayAlreadyHidden) {
                driver.findElement(By.xpath("//div[@id='wrapper']/footer/div/div/div[2]/div/a")).click();
            }
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
            careerSectionTextLink.click();
        }
    }

    public static String handleTranslationsAtHomePage(String currentUrl) {
        String careersTextLink;
        System.out.println(currentUrl);
        if (currentUrl.contentEquals("https://www.f-secure.com/pl_PL/f-secure")) {
            System.out.println("Using polish language");
            careersTextLink = "Kariera";
        } else {
            careersTextLink = "Careers";
        }
        return careersTextLink;
    }

    public static void acceptCookiesUsage(WebDriver driver) {
        try {
            driver.findElement(By.id("cookie-consent"));
            System.out.println("There are cookies");
            driver.findElement(By.cssSelector("a.btn.btn-primary")).click();
        } catch (NoSuchElementException e) {
            System.out.println("No cookies acceptance needed!");
            return;
        }
    }
}