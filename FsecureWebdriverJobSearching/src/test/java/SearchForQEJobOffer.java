import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SearchForQEJobOffer {

    private WebDriver driver;
    private WebDriverWait wait;

    String baseUrl = "https://www.f-secure.com/";
    String expectedHomePageTitle = "F-Secure | Cyber Security Solutions for your Home and Business";
    String expectedCareerPageUrl = "https://www.f-secure.com/en/web/about_global/careers";
    String expectedSeeOurOpenPositionsButtonsHref = "https://www.f-secure.com/en/web/about_global/careers/job-openings";

    String fellowShipStoriesButtonXpath = "//button[contains(text(), 'Fellowship stories')]";
    String seeOurOpenPositionsButtonsXpath = "(//a[contains(text(),'See our open positions')])[2]";
    String qualityEngineerJobOfferTitleXpath = "//h2[contains(text(), 'Quality Engineer')]";
    String thirdPagersElementsXpath = "(//a[contains(text(),'See our open positions')])";

    @BeforeClass
    public void setUp() {
        driver = new FirefoxDriver();
//        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 2);
    }

    @Test
    public void SearchForQAJobOffer() {

        driver.get(baseUrl);

        Assert.assertEquals(driver.getTitle(), expectedHomePageTitle);

        openCareerPageFromHomePage(driver);

        Assert.assertEquals(driver.getCurrentUrl(),expectedCareerPageUrl);

        WebElement fellowShipStoriesButton = driver.findElement(By.xpath(fellowShipStoriesButtonXpath));
        WebElement seeOurOpenPositionsButtons = driver.findElement(By.xpath(seeOurOpenPositionsButtonsXpath));

        Assert.assertTrue(isElementWithByPatternPresented(driver, By.xpath(fellowShipStoriesButtonXpath)));
        Assert.assertTrue(fellowShipStoriesButton.isDisplayed() && fellowShipStoriesButton.isEnabled());
        Assert.assertTrue(seeOurOpenPositionsButtons.isDisplayed() && seeOurOpenPositionsButtons.isEnabled());
        Assert.assertEquals(seeOurOpenPositionsButtons.getAttribute("href"),
                expectedSeeOurOpenPositionsButtonsHref);

        seeOurOpenPositionsButtons.click();

        filterOffersFromPoznanAtJobOpeningsPage(driver);
        WebElement qualityEngineerJobOfferTitle = driver
                .findElement(By.xpath(qualityEngineerJobOfferTitleXpath));

        wait.until(ExpectedConditions.visibilityOf(qualityEngineerJobOfferTitle));

        Assert.assertTrue(qualityEngineerJobOfferTitle.isDisplayed());
        //assert that there are less than 3 offer pages
        Assert.assertFalse(isElementWithByPatternPresented(driver, By.xpath((thirdPagersElementsXpath))));
    }

    @AfterClass
    public void tearDown() {
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
        String career;
        String footerMenuAboutSectionXpath = "//div[@id='wrapper']/footer/div/div/div[2]/div/a";
        boolean footerMenuManuallyExpanded = false;

        acceptCookiesUsage(driver);
        career = handleTranslationsAtHomePage(driver.getCurrentUrl());

        final By careerTextLinkByPattern = By.linkText(career);

        // Handling with responsiveness - if career link is under 'about' section of footer menu - works with ChromeDriver
        if (!isElementWithByPatternPresented(driver, careerTextLinkByPattern))
        {
            System.out.println("Career was not visible");
            driver.findElement(By.xpath(footerMenuAboutSectionXpath)).click();
            footerMenuManuallyExpanded = true;
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        }

        careerSectionTextLink = fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(careerTextLinkByPattern);
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

        if (currentUrl.contentEquals("https://www.f-secure.com/pl_PL/f-secure")) {
            System.out.println("Using polish language");
            careersTextLink = "Kariera";
        } else {
            careersTextLink = "Careers";
        }
        return careersTextLink;
    }

    public static void acceptCookiesUsage(WebDriver driver) {
        By cookiePromptByPattern = By.id("cookie-consent");
        if (isElementWithByPatternPresented(driver, cookiePromptByPattern)) {
            System.out.println("There is cookie acceptance prompt!");
            driver.findElement(By.cssSelector("a.btn.btn-primary")).click();
        } else System.out.println("No cookies acceptance needed!");
    }

    public static boolean isElementWithByPatternPresented(WebDriver driver, By pattern) {
        try {
            driver.findElement(pattern);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
