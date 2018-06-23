package com.selenium.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world");

        WebDriver driver = new FirefoxDriver();
        driver.get("https://www.f-secure.com/");
    }
}
