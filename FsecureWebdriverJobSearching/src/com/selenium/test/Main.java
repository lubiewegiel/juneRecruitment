package com.selenium.test;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class Main {

    public static void main(String[] args) throws InterruptedException {


        WebDriver driver = new FirefoxDriver();

        String baseUrl = "https://www.f-secure.com/";

        driver.get(baseUrl);

        System.out.println(driver.getTitle());

        acceptCookiesUsage(driver);

        String career = handleTranslationsAtHomePage(driver.getCurrentUrl());
        WebElement careerSectionTextLink = driver.findElement(By.linkText(career));
        careerSectionTextLink.click();

        driver.findElement(By.xpath("(//a[contains(text(),'See our open positions')])[2]")).click();

        WebElement citiesDropdownList = driver.findElement(By.cssSelector("[data-id=\"job-city\"]"));
        citiesDropdownList.click();

        driver.findElement(By.xpath("/html/body/div[1]/div[3]/div/div/div/div[3]/div/div/div/div[1]/section/div/div[1]/div/div/div/div/ul/li[5]/a")).click();



        driver.close();
    }


    public static String handleTranslationsAtHomePage(String currentUrl) {
        String careersTextLink;
        System.out.println(currentUrl);
        if (currentUrl.contentEquals("https://www.f-secure.com/pl_PL/f-secure") ) {
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
        }catch(NoSuchElementException e) {
            System.out.println("No cookies acceptance needed!");
            return;
        }
    }
}