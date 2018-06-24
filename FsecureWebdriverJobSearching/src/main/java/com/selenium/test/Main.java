package com.selenium.test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {


        WebDriver driver = new FirefoxDriver();
//        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 2);
        String baseUrl = "https://www.f-secure.com/";
        String seeOurOpenPositionsButtonsXpath = "(//a[contains(text(),'See our open positions')])[2]";
        String qualityEngineerJobOfferTitleXpath = "//h2[contains(text(), 'Quality Engineer')]";

        driver.get(baseUrl);

        System.out.println(driver.getTitle());

        openCareerPageFromHomePage(driver);

        driver.findElement(By.xpath(seeOurOpenPositionsButtonsXpath)).click();

        filterOffersFromPoznanAtJobOpeningsPage(driver);

        WebElement qualityEngineerJobOfferTitle = driver
                .findElement(By.xpath(qualityEngineerJobOfferTitleXpath));

        wait.until(ExpectedConditions.visibilityOf(qualityEngineerJobOfferTitle));

        Assert.assertTrue(qualityEngineerJobOfferTitle.isDisplayed());
        System.out.println(qualityEngineerJobOfferTitle.getSize().toString());

        driver.close();
    }

    public static void filterOffersFromPoznanAtJobOpeningsPage(WebDriver driver) {

        String citiesDropdownListCssSelector = "[data-id=\"job-city\"]";
        String citiesDropdownListPoznanElementXpath ="//*[contains(text(), 'Poznań')]";

        WebElement citiesDropdownList = driver.findElement(By.cssSelector(citiesDropdownListCssSelector));
        citiesDropdownList.click();

        WebElement citiesDropdownListPoznanElement = driver.findElement(By.xpath(citiesDropdownListPoznanElementXpath));
        citiesDropdownListPoznanElement.click();
    }

    public static void openCareerPageFromHomePage(WebDriver driver) {

        WebElement careerSectionTextLink;
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver)
                .withTimeout(3, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring((NoSuchElementException.class));
        final String career;
        String footerMenuAboutSectionXpath = "//div[@id='wrapper']/footer/div/div/div[2]/div/a";
        boolean footerMenuManuallyExpanded = false;

        acceptCookiesUsage(driver);
        career = handleTranslationsAtHomePage(driver.getCurrentUrl());

        // Handling with responsiveness - if career link is under 'about' section of footer menu - works with ChromeDriver
        try {
            driver.findElement(By.linkText(career));
        } catch (NoSuchElementException e) {
            System.out.println("Career was not visible");
            driver.findElement(By.xpath(footerMenuAboutSectionXpath)).click();
            footerMenuManuallyExpanded = true;
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        }

        careerSectionTextLink = fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.linkText(career));
            }
        });

        // For FirefoxDriver 'career' link is visible but not interactable - exception is thrown only after interaction attempt
        try {
            careerSectionTextLink.click();
        }catch (ElementNotInteractableException e1) {
            System.out.println("Could not scroll to element - Firefox Driver Exception");
            if (!footerMenuManuallyExpanded) {
                driver.findElement(By.xpath(footerMenuAboutSectionXpath)).click();
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