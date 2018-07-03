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

    private final static String BASE_URL = "https://www.f-secure.com/";
    private final static String POLISH_HOME_PAGE_URL = "https://www.f-secure.com/pl_PL/f-secure";
    private final static String EXPECTED_HOME_PAGE_TITLE = "F-Secure | Cyber Security Solutions for your Home and Business";
    private final static String EXPECTED_CAREER_PAGE_URL = "https://www.f-secure.com/en/web/about_global/careers";
    private final static String EXPECTED_SEE_OUR_OPEN_POSITIONS_BUTTONS_HREF = "https://www.f-secure.com/en/web/about_global/careers/job-openings";
    private final static String POLISH_CAREERS_TEXT_LINK = "Kariera";
    private final static String ENGLISH_CAREERS_TEXT_LINK = "Careers";

    private final static String COOKIES_ACCEPTANCE_BUTTON_CSS_SELECTOR = "a.btn.btn-primary";
    private final static String CITIES_DROPDOWN_LIST_CSS_SELECTOR = "[data-id=\"job-city\"]";

    private final static String COOKIE_PROMPT_BY_PATTERN_ID = "cookie-consent";

    private final static String FELLOW_SHIP_STORIES_BUTTON_XPATH = "//button[contains(text(), 'Fellowship stories')]";
    private final static String SEE_OUR_OPEN_POSITIONS_BUTTONS_XPATH = "(//a[contains(text(),'See our open positions')])[2]";
    private final static String QUALITY_ENGINEER_JOB_OFFER_TITLE_XPATH = "//h2[contains(text(), 'Quality Engineer')]";
    private final static String THIRD_PAGERS_ELEMENTS_XPATH = "(//a[contains(text(),'See our open positions')])";
    private final static String CITIES_DROPDOWN_LIST_POZNAN_ELEMENT_XPATH = "//*[contains(text(), 'Poznań')]";
    private final static String FOOTER_MENU_ABOUT_SECTION_XPATH = "//div[@id='wrapper']/footer/div/div/div[2]/div/a";

    private final static String SCROLL_DOWN_TO_THE_BOTTOM_OF_PAGE_JS_SCRIPT ="window.scrollTo(0, document.body.scrollHeight)";


    @BeforeClass
    public void setUp() {
        driver = new FirefoxDriver();
//        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 2);
    }

    @Test
    public void searchForQAJobOffer() {
        driver.get(BASE_URL);

        Assert.assertEquals(driver.getTitle(), EXPECTED_HOME_PAGE_TITLE);

        openCareerPageFromHomePage(driver);

        Assert.assertEquals(driver.getCurrentUrl(), EXPECTED_CAREER_PAGE_URL);

        WebElement fellowShipStoriesButton = driver.findElement(By.xpath(FELLOW_SHIP_STORIES_BUTTON_XPATH));
        WebElement seeOurOpenPositionsButtons = driver.findElement(By.xpath(SEE_OUR_OPEN_POSITIONS_BUTTONS_XPATH));

        Assert.assertTrue(isElementWithByPatternPresented(driver, By.xpath(FELLOW_SHIP_STORIES_BUTTON_XPATH)));
        Assert.assertTrue(fellowShipStoriesButton.isDisplayed() && fellowShipStoriesButton.isEnabled());
        Assert.assertTrue(seeOurOpenPositionsButtons.isDisplayed() && seeOurOpenPositionsButtons.isEnabled());
        Assert.assertEquals(seeOurOpenPositionsButtons.getAttribute("href"),
                EXPECTED_SEE_OUR_OPEN_POSITIONS_BUTTONS_HREF);

        seeOurOpenPositionsButtons.click();

        filterOffersFromPoznanAtJobOpeningsPage(driver);
        WebElement qualityEngineerJobOfferTitle = driver
                .findElement(By.xpath(QUALITY_ENGINEER_JOB_OFFER_TITLE_XPATH));

        wait.until(ExpectedConditions.visibilityOf(qualityEngineerJobOfferTitle));

        Assert.assertTrue(qualityEngineerJobOfferTitle.isDisplayed());
        //assert that there are less than 3 offer pages
        Assert.assertFalse(isElementWithByPatternPresented(driver, By.xpath((THIRD_PAGERS_ELEMENTS_XPATH))));
    }

    @AfterClass
    public void tearDown() {
        driver.close();
    }

    private static void filterOffersFromPoznanAtJobOpeningsPage(WebDriver driver) {
        WebElement citiesDropdownList = driver.findElement(By.cssSelector(CITIES_DROPDOWN_LIST_CSS_SELECTOR));
        citiesDropdownList.click();

        WebElement citiesDropdownListPoznanElement = driver.findElement(By.xpath(CITIES_DROPDOWN_LIST_POZNAN_ELEMENT_XPATH));
        citiesDropdownListPoznanElement.click();
    }

    private static void openCareerPageFromHomePage(WebDriver driver) {
        WebElement careerSectionLinkText;
        Wait<WebDriver> fluentWait = new FluentWait<WebDriver>(driver)
                .withTimeout(3, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring((NoSuchElementException.class));

        String careerLinkText;
        boolean footerMenuManuallyExpanded = false;

        acceptCookiesUsage(driver);
        careerLinkText = handleTranslationsAtHomePage(driver.getCurrentUrl());

        final By careerTextLinkByPattern = By.linkText(careerLinkText);

        // Handling with responsiveness - if career link is under 'about' section of footer menu - works with ChromeDriver
        if (!isElementWithByPatternPresented(driver, careerTextLinkByPattern))
        {
            System.out.println("Career was not visible");
            driver.findElement(By.xpath(FOOTER_MENU_ABOUT_SECTION_XPATH)).click();
            footerMenuManuallyExpanded = true;
            scrollDownToTheBottomOfPage(driver);
        }

        careerSectionLinkText = fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(careerTextLinkByPattern);
            }
        });

        // For FirefoxDriver 'career' link is visible but not interactable - exception is thrown only after interaction attempt
        try {
            careerSectionLinkText.click();
        }catch (ElementNotInteractableException e1) {
            System.out.println("Could not scroll to element - Firefox Driver Exception");
            if (!footerMenuManuallyExpanded) {
                driver.findElement(By.xpath(FOOTER_MENU_ABOUT_SECTION_XPATH)).click();
            }
            scrollDownToTheBottomOfPage(driver);
            careerSectionLinkText.click();
        }
    }

    private static void scrollDownToTheBottomOfPage(WebDriver driver) {
        ((JavascriptExecutor) driver)
                .executeScript(SCROLL_DOWN_TO_THE_BOTTOM_OF_PAGE_JS_SCRIPT);
    }

    private static String handleTranslationsAtHomePage(String currentUrl) {
        String careersTextLink;

        if (currentUrl.contentEquals(POLISH_HOME_PAGE_URL)) {
            System.out.println("Using polish language");
            careersTextLink = POLISH_CAREERS_TEXT_LINK;
        } else {
            careersTextLink = ENGLISH_CAREERS_TEXT_LINK;
        }
        return careersTextLink;
    }

    private static void acceptCookiesUsage(WebDriver driver) {
        By cookiePromptByPattern = By.id(COOKIE_PROMPT_BY_PATTERN_ID);
        if (isElementWithByPatternPresented(driver, cookiePromptByPattern)) {
            System.out.println("There is cookie acceptance prompt!");
            driver.findElement(By.cssSelector(COOKIES_ACCEPTANCE_BUTTON_CSS_SELECTOR)).click();
        } else System.out.println("No cookies acceptance needed!");
    }

    private static boolean isElementWithByPatternPresented(WebDriver driver, By pattern) {
        try {
            driver.findElement(pattern);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
