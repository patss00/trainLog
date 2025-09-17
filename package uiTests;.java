package uiTests;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.testng.AssertJUnit.assertTrue;

public class Base {
	//region OBS
	//Tabs that are important to test on v1 will have the value true; otherwise they will be false.
	// For now, only the creation and required fields validation will be implemented, unless something more is in need of testing.
	// Later on v2 we should also give some attention to  empty&invalid non-required fields because some error messages might need more user friendly approaches

	//endregion

	public static WebDriver driver;
	public static WebDriverWait wait;
	public static Actions actions;
	public static DatabaseAccess dba;
	public static boolean deleted = false;
	public static boolean created = false;
	public static final String SILENA_URL = PropertiesReader.getProperty("silena.endpoint");

	//COMMON XPATHS
	protected final String xpathErrorMsg = "//html//*[@class='v-Notification error v-Notification-error']//div//p[2]";
	protected final String xpathTinyErrorMsg = "//html//*[@class='v-Notification-caption']";

	protected final String xpathPopup = "//html//*[@class='popupContent']//*[@class='gwt-HTML']//p[2]";
	protected final String xpathPopupPrimaryButton = "//html//*[@class='popupContent']//*[@class='v-button v-widget primary v-button-primary']";

	protected final String xpathDialogInput = "//*[@id='main-searchField']";
	protected final String xpathSearchDialog = "//*[@id='main-searchButton']";
	protected final String xpathCreate = "//*[@id='main-newButton']";
	protected final String xpathDelete = "//*[@id='main-trashButton']";
	protected final String xpathTitle = "//html//*[@class='v-label v-widget v-label-undef-w']//u//b";

	protected final String xpathConfirm = "//html//*[@id='confirmdialog-ok-button']";
	public final String idConfirm = "confirmdialog-window";
	public final String idConfirmOk = "confirmdialog-ok-button";
	public final String idCancel = "confirmdialog-cancel-button";

	public final String xpathSearchModalTable = "//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='popupContent']//tbody";

	public final String xpathFilterModalTable = "//html//*[@class='v-window v-widget']//*[@class='popupContent']//*[@class='v-window-wrap']//tbody";

	public final String xpathMinusButtonError = "//html//*[@class='v-slot v-align-center v-align-middle'][5]//*[@class='v-horizontallayout v-layout v-horizontal v-widget']//*[@class='v-slot'][2]//*[@class='v-button v-widget v-button-error-system v-button-error']";

	protected final String xpathSaveButtonGrid = "//html//*[@class='v-slot v-align-center v-align-middle'][5]//*[@class='v-slot v-align-center v-align-middle'][4]//*[@role='button']";

	protected final String xpathSave = "//*[@id='main-saveButton']";
	protected final String xpathSearch = "//*[@id='main-browseButton']";
	protected final String xpathReload = "//*[@id='main-reloadButton']";
	protected final String xpathCloseDialog = "//html//*[@class='v-button v-widget closeDialoque v-button-closeDialoque']";
	protected final String xpathMaximizeModalTable = "//*[@class='v-window-maximizebox']";
	public  boolean maximize = false;
	String xpathCloseUpperRightButtons = "//html//*[@class='v-window v-widget']//*[@class='popupContent']//*[@class='v-window-wrap']//*[@class='v-window-outerheader']";
	protected final String xpathCloseModal = "//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']";
	String xpathCloseSecondUpperRightButtons = "//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='popupContent']//*[@class='v-window-wrap']//*[@class='v-window-outerheader']";
	String xpathCloseSecondModal = "//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='popupContent']//*[@class='v-window-wrap']//*[@class='v-window-outerheader']//*[@class='v-window-closebox']";
	protected final String xpathScrollBarModal = "//html//*[@class='v-grid-scroller v-grid-scroller-vertical']";
	protected final String cssDescriptionErrorNotification = ".v-Notification-description";

	/**
	 * id elements
	 */
	protected final String idSaveButton = "main-saveButton";
	protected final String idSearchButton = "main-searchButton";
	protected final String idSearchField = "main-searchField";
	//	protected final String idSearchWindow = "searchDialog"; // does not work at the moment with Dialog setId
	protected final String idReloadButton = "main-reloadButton";
	protected final String idBrowseButton = "main-browseButton";
	protected final String idCloseDialogButton = "main-closeDialogButton";
	protected final String idCreateButton = "main-newButton";
	protected final String idDeleteButton = "main-trashButton";
	protected final String idConfirmDialogOkButton = "confirmdialog-ok-button";
	protected final String idMasterDataTab = "Master data";
	protected final String idDialogHeadline = "dialogHeadLine";
	protected final String idTitle = "dialogTitle";
	protected final String idUsername = "login-userBox";
	protected final String idPassword = "login-passwordBox";
	protected final String idLoginButton = "login-loginButton";
	protected final String idLogo = "login-logoImage";
	protected final String idImgDialogs = "main-openDialogBox";

	/*
	 * css elements
	 */
	protected final String cssTinyErrorMessage = ".v-Notification-caption";
	protected final String cssErrorNotification = ".v-Notification.error.v-Notification-error";
	public final String xpathInfoErrorDescription = "//html//*[@class='v-Notification-description']";



	public final DialogVars emptyDV = new DialogVars("emptyDV", 0, "");
	public static boolean replaceSelectAndConfirmItems = true;
	public static boolean isDoubleClick = true;
	public static boolean close = true;
	public static WebElement currentTr = null;
	public String alpha2 = RandomStringUtils.randomAlphabetic(2);
	public String numeric2 = generateRandomNumberRange(10, 99);
	public String alphaNumeric2 = RandomStringUtils.randomAlphanumeric(2);
	@BeforeClass
	public void setUp(){
		loadChromeDriver();
	}

	public void loadChromeDriver() {
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(Configs.chromeDriverPath))
				.build();
		//System.setProperty("webdriver.chrome.driver", "C:\\\\Users\\\\PatriciaCorreia\\\\OneDrive - Procensus\\\\Documents\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox"); // Bypass OS security model, MUST BE THE VERY FIRST OPTION
		//options.addArguments("--headless");
		options.addArguments("--remote-allow-origins=*");
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("start-maximized"); // open Browser in maximized mode
		options.addArguments("disable-infobars"); // disabling infobars
		options.addArguments("--disable-extensions"); // disabling extensions
		options.addArguments("--disable-gpu"); // applicable to windows os only
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("force-device-scale-factor=0.75");
		options.addArguments("high-dpi-support=0.75");
		options.setBinary(Configs.chromePath);
		driver = new ChromeDriver(service, options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		actions = new Actions(driver);
		dba = new DatabaseAccess();
	}

	public static void logMsg(String fmt, Object... args) {
		String ts = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss,SSS"));
		System.out.println(ts + " " + String.format(fmt, args));
	}


	public void zoomOut60() {
		JavascriptExecutor jse= (JavascriptExecutor)driver;
		jse.executeScript("document.body.style.zoom='60%'");
	}


	@AfterClass
	public void tearDown(){
		driver.manage().deleteAllCookies();
		driver.quit();
	}

	public static void waitPage(WebDriverWait wait) {
		try {
			wait.until(
					webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
			Thread.sleep(900);
		} catch (Throwable error) {
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}
	}

	public void confirmCreateWithoutError(String field, String value){
		if(isBeingDisplayedWithCss(cssErrorNotification)){
			String error = driver.findElement(By.cssSelector(cssErrorNotification)).findElement(By.cssSelector(".gwt-HTML")).findElements(By.tagName("p")).get(1).getText();
			logMsg("(Base.confirmCreateWithoutError) Couldn't verify successful save on entry with field: " + field + ", Value(s): " + value + ". Error: " + error);
			fail("Couldn't create entry with field: " + field + ". Value(s): " + value + ". Error: " + error);
		} else{
			created = true;
			waitPage(wait);
			clickESCOnHtmlById();
			clickESCOnHtmlById();
		}
	}

	public void confirmCreateWithoutErrorMultipleFields(HashMap<String, String> idsValues){
		if(isBeingDisplayedWithCss(cssErrorNotification)){
			String error = driver.findElement(By.cssSelector(cssErrorNotification)).findElement(By.cssSelector(".gwt-HTML")).findElements(By.tagName("p")).get(1).getText();
			logMsg("(Base.confirmCreateWithoutErrorMultipleFields) Couldn't verify successful save on entry " + idsValues.entrySet());
			fail("Couldn't create entry: " + idsValues.entrySet() + ". Error: " + error);
		} else{
			created = true;
			waitPage(wait);
			clickESCOnHtmlById();
			clickESCOnHtmlById();
		}
	}

	public void confirmCreateWithoutTinyError(String field, String value){
		if(isBeingDisplayedWithCss(cssTinyErrorMessage)){
			String error = driver.findElement(By.cssSelector(cssTinyErrorMessage)).findElement(By.cssSelector(".gwt-HTML")).findElements(By.tagName("p")).get(1).getText();
			fail("Couldn't create " + field + ": " + value + ". Error: " + error);
		} else{
			created = true;
			waitPage(wait);
			driver.findElement(By.xpath("//html")).sendKeys(Keys.ESCAPE);
		}
	}

	public void closeErrorOrNewOrGoHome() {
		waitPage(wait);
		clickESCUntilGone(xpathErrorMsg);
		clickESCUntilGone(xpathTinyErrorMsg);

		waitPage(wait);
		clickUntilGone(xpathConfirm);

		if(driver.findElements(By.id(idMasterDataTab)).size() != 0)
			scrollClickId(idMasterDataTab);
	}

	public void clickESCUntilGonePresence(String xpath) {
		scrollWaitElementByXpathPresence(xpath);
		while (driver.findElements(By.xpath(xpath)).size() > 0)
			actions.sendKeys(Keys.ESCAPE).perform();
	}

	public void clickEnterUntilGonePresence(String xpath) {
		scrollWaitElementByXpathPresence(xpath);
		while (driver.findElements(By.xpath(xpath)).size() > 0)
			actions.sendKeys(Keys.ENTER).perform();
	}

	public void clickESCUntilGone(String xpath) {
		scrollWaitElementByXpath(xpath);
		while (driver.findElements(By.xpath(xpath)).size() > 0)
			actions.sendKeys(Keys.ESCAPE).perform();
	}

	/**
	 * Wait function that waits up to 10 seconds for the condition to become true
	 * @param condition
	 * @return true on success, false on error (timeout/element not present)
	 */
	public static boolean waitUntil(ExpectedCondition<WebElement> condition) {
		return waitUntil(condition, 10);
	}

	/**
	 * Wait function that waits up to the given number of seconds for the condition to become true
	 * @param condition Condition to check
	 * @param timeout Number of seconds to wait until timeout
	 * @return true on success, false on error (timeout/element not present)
	 */
	public static boolean waitUntil(ExpectedCondition<WebElement> condition, int timeout) {
		long start = System.currentTimeMillis();
		long end = start + (long)timeout * (long)1000;

		while (end > System.currentTimeMillis()) {
			try {
				wait.until(condition);
			}
			catch (TimeoutException e) {
				continue;
			}

			return true;
		}

		return false;
	}


	public void clickESCOnHtml() {
		driver.findElement(By.xpath("html")).sendKeys(Keys.ESCAPE);
		waitPage(wait);
	}

	public void clickESCOnHtmlById() {
		driver.findElement(By.tagName("html")).sendKeys(Keys.ESCAPE);
		waitPage(wait);
	}

	public void clickESCUntilGoneById(String id) {
		scrollWaitElementById(id);
		while (driver.findElements(By.id(id)).size() > 0)
			actions.sendKeys(Keys.ESCAPE).perform();
		waitPage(wait);
	}

	public void clickEnterUntilGone(String xpath) {
		scrollWaitElementByXpath(xpath);
		while (driver.findElements(By.xpath(xpath)).size() > 0)
			actions.sendKeys(Keys.ENTER).perform();
	}


	public void clickUntilGonePresence(String xpath) {
		scrollWaitElementByXpathPresence(xpath);
		while (driver.findElements(By.xpath(xpath)).size() > 0)
			driver.findElement(By.xpath(xpath)).click();
	}

	public void clickUntilGone(String xpath) {
		scrollWaitElementByXpath(xpath);
		while (driver.findElements(By.xpath(xpath)).size() > 0)
			driver.findElement(By.xpath(xpath)).click();
	}


	public void closeErrorOrNewOrGoHomePresence() {
		waitPage(wait);
		clickESCUntilGonePresence(xpathErrorMsg);
		clickESCUntilGonePresence(xpathTinyErrorMsg);

		waitPage(wait);
		clickUntilGonePresence(xpathConfirm);

		if(driver.findElements(By.id(idMasterDataTab)).size() != 0)
			scrollClickId(idMasterDataTab);
	}

	public void deleteElementFromGridByXpath(String xpathButton, WebElement tr) {
		Actions act = new Actions(driver);
		act.click(tr).perform();
		waitPage(wait);

		waitElementPresenceByXpath(xpathButton);
		driver.findElement(By.xpath(xpathButton)).click();
	}

	public void deleteElementFromGridById(String idButton, WebElement tr) {
		Actions act = new Actions(driver);
		act.click(tr).perform();
		waitPage(wait);

		waitPresenceById(idButton);
		driver.findElement(By.id(idButton)).click();
	}

	public void saveThenError(String xpath, String startsWith) {
		waitElementPresenceByXpath(xpath);
		driver.findElement(By.xpath(xpath)).click();

		waitElementPresenceByXpath(xpathErrorMsg);
		assertTrue(driver.findElement(By.xpath(xpathErrorMsg)).getText().startsWith(startsWith));
		driver.findElement(By.xpath(xpathErrorMsg)).click();
		waitPage(wait);
	}


	public void yesNoDropdown(String xpath, boolean choice) {
		logMsg("    (Base.yesNoDropdown) xpath:<%s>, choice:<%s>", xpath, choice ? "Yes" : "No");
		waitPage(wait);
		if(choice){
			if(driver.findElement(By.xpath(xpath)).getAttribute("value").equals("No") && driver.findElement(By.xpath(xpath)).isEnabled()){
				driver.findElement(By.xpath(xpath)).click();
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_UP);
				waitPage(wait);//Yes SWS
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
			} else{
				driver.findElement(By.xpath(xpath)).click();
				waitPage(wait);//Yes SWS
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
			}
		} else{
			if(driver.findElement(By.xpath(xpath)).getAttribute("value").equals("Yes") && driver.findElement(By.xpath(xpath)).isEnabled()){
				driver.findElement(By.xpath(xpath)).click();
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_DOWN);
				waitPage(wait);//No SWS
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
			} else{
				if(!driver.findElement(By.xpath(xpath)).getAttribute("value").equals("No")){
					driver.findElement(By.xpath(xpath)).click();
					driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_DOWN);
					waitPage(wait);
					driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
				}
			}
		}
	}


	public void dropdownsWritingSelection(String xpath, String value){
		logMsg("    (Base.dropdownsWritingSelection) xpath:<%s>, value:<%s>", xpath, value);
		scrollWaitElementByXpathPresence(xpath);
		WebElement drp = driver.findElement(By.xpath(xpath));
		if(!value.isEmpty() && !drp.getAttribute("value").equals(value)){
			drp.click();
			drp.sendKeys(Keys.BACK_SPACE);
			drp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			drp.sendKeys(Keys.DELETE);
			drp.sendKeys(value);
			drp.sendKeys(Keys.ENTER);
			waitPage(wait);
			assertTrue(drp.getAttribute("value").startsWith(value));
		}
	}

	public String getRandomListValue(List<String> list){
		return list.get(Integer.parseInt(generateRandomNumberRange(0, list.size() - 1)));
	}

	public void fillTwinSelect(String xpathSelect, List<String> selectValues) {
		logMsg("    (Base.fillTwinSelect) xpathSelect:<%s>, selectValues:<%s>", xpathSelect, selectValues.toString());
		Actions actions = new Actions(driver);
		boolean found = false;
		for (String s : selectValues) {
			Select s1 = new Select(driver.findElement(By.xpath(xpathSelect + "[1]")));
			Select s2 = new Select(driver.findElement(By.xpath(xpathSelect + "[2]")));
			boolean transfer = false;
			for (WebElement x : s1.getOptions()) {
				if (x.getText().equals(s)) {
					actions.doubleClick(x).perform();
					for (WebElement y : s2.getOptions()) {
						if (y.getText().equals(s)) {
							transfer = true;
							break;
						}
					}
					if (!transfer) fail("Fail transfering value " + s);
					found = true;
					break;
				}
			}
			if (!found) fail("Couldn't find " + s);
		}
		waitPage(wait);
	}

	public void dropdownsRowOnlySelection(int dropdownRow, String xpath) {
		//This method assumes the dropdown starts from index 1 (first entry)
		if(dropdownRow > 0) {
			driver.findElement(By.xpath(xpath)).click();
			for (int i = 1; i < dropdownRow; i++) {
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_DOWN);
			}
			waitPage(wait);
			driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
		}
		waitPage(wait);
	}


	public void dropdownsRowNumberSelection(int dropdownRow, String xpath, String value) {
		logMsg("    (Base.dropdownsRowNumberSelection) xpath:<%s>, dropdownRow:<%d>, value:<%s>", xpath, dropdownRow, value);
		if(dropdownRow != 0) {
			if(!driver.findElement(By.xpath(xpath)).getAttribute("value").equals(value)) {
				driver.findElement(By.xpath(xpath)).click();
				for (int i = 1; i < dropdownRow; i++) {
					driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_DOWN);
				}
				waitPage(wait);
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
			}
		}
		waitPage(wait);
		assertTrue(driver.findElement(By.xpath(xpath)).getAttribute("value").startsWith(value));
	}

	public void dropdownsRowNumberSelectionWithouAssert(int dropdownRow, String xpath, String value) {
		logMsg("    (Base.dropdownsRowNumberSelectionWithouAssert) xpath:<%s>, dropdownRow:<%d>, value:<%s>", xpath, dropdownRow, value);
		if(dropdownRow != 0) {
			if(!driver.findElement(By.xpath(xpath)).getAttribute("value").equals(value)) {
				driver.findElement(By.xpath(xpath)).click();
				for (int i = 1; i < dropdownRow; i++) {
					driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_DOWN);
				}
				waitPage(wait);
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
			}
		}
		waitPage(wait);
	}

	//Used for resetting dropdowns that don't allow us to write the desired option. So far, html inspection won't let me select these dropdowns by text/value.
	public void resetDropdownToStart(String xpath, int count, String startValue){
		scrollWaitElementByXpathPresence(xpath);
		driver.findElement(By.xpath(xpath)).click();
		for (int i = 0; i < count; i++) {
			driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_UP);
		}
		waitPage(wait);
		driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
		waitPage(wait);
		assertTrue(driver.findElement(By.xpath(xpath)).getAttribute("value").startsWith(startValue));
	}

	public void resetDropdownToStartById(String id, int count, String startValue){
		scrollWaitElementById(id);
		WebElement drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		drp.click();
		for (int i = 0; i < count; i++) {
			drp.sendKeys(Keys.ARROW_UP);
		}
		waitPage(wait);
		drp.sendKeys(Keys.ENTER);
		waitPageTimes(2);
		assertTrue(drp.getAttribute("value").contains(startValue));
	}

	public void dropdownsRowNumberSelectionInverted(int dropdownRow, String xpath, String value) {
		logMsg("    (Base.dropdownsRowNumberSelectionIncludeExcludeById) xpath:<%s>, dropdownRow:<%d>, value:<%s>", xpath, dropdownRow, value);
		//We specify the row inverted as if the last one was the first one
		if(dropdownRow != 0) {
			driver.findElement(By.xpath(xpath)).click();
			for (int i = 1; i < dropdownRow; i++) {
				driver.findElement(By.xpath(xpath)).sendKeys(Keys.ARROW_UP);
			}
			waitPage(wait);
			driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
		}
		waitPage(wait);
		assertTrue(driver.findElement(By.xpath(xpath)).getAttribute("value").startsWith(value));
	}

	public void dropdownsRowNumberSelectionInvertedById(int dropdownRow, String id, String value) {
		logMsg("    (Base.dropdownsRowNumberSelectionInvertedById) id:<%s>, dropdownRow:<%d>, value:<%s>", id, dropdownRow, value);
		//We specify the row inverted as if the last one was the first one
		WebElement drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		if(dropdownRow != 0) {
			drp.click();
			for (int i = 1; i < dropdownRow; i++) {
				drp.sendKeys(Keys.ARROW_UP);
			}
			waitPage(wait);
			drp.sendKeys(Keys.ENTER);
		}
		waitPage(wait);
		assertTrue(getValIdInput(id).startsWith(value));
	}

	public void dropdownsRowNumberSelectionIncludeExclude(String xpath, String value) {		logMsg("    (Base.dropdownsRowNumberSelectionIncludeExclude) xpath:<%s>, value:<%s>", xpath, value);

		WebElement drp = driver.findElement(By.xpath(xpath));
		if(value.equalsIgnoreCase("Exclude")) {
			if(!drp.getAttribute("value").equalsIgnoreCase("Exclude")) {
				drp.click();
				drp.sendKeys(Keys.ARROW_DOWN);
				waitPage(wait);
				drp.sendKeys(Keys.ENTER);
			}
		} else if(value.equalsIgnoreCase("Include")){
			if(!drp.getAttribute("value").equalsIgnoreCase("Include")) {
				drp.click();
				drp.sendKeys(Keys.ARROW_UP);
				waitPage(wait);
				drp.sendKeys(Keys.ENTER);
			}
		}
		waitPage(wait);
		assertTrue(drp.getAttribute("value").startsWith(value));
	}

	public void dropdownsRowNumberSelectionIncludeExcludeById(String id, String value) {
		logMsg("    (Base.dropdownsRowNumberSelectionIncludeExcludeById) id:<%s>, value:<%s>", id, value);
		WebElement drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		if(value.equalsIgnoreCase("Exclude")) {
			if(!drp.getAttribute("value").equalsIgnoreCase("Exclude")) {
				drp.click();
				drp.sendKeys(Keys.ARROW_DOWN);
				waitPage(wait);
				drp.sendKeys(Keys.ENTER);
			}
		} else if(value.equalsIgnoreCase("Include")){
			if(!drp.getAttribute("value").equalsIgnoreCase("Include")) {
				drp.click();
				drp.sendKeys(Keys.ARROW_UP);
				waitPage(wait);
				drp.sendKeys(Keys.ENTER);
			}
		}
		waitPage(wait);
		assertTrue(drp.getAttribute("value").startsWith(value));
	}


	public void scrollWaitElementByXpath(String xpath){
		if(driver.findElements(By.xpath(xpath)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.xpath(xpath)));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
		}
	}

	public static void scrollWaitElementByXpathPresence(String xpath){
		if(driver.findElements(By.xpath(xpath)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.xpath(xpath)));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
		}
	}

	public void scrollWaitElementByIdPresence(String id){
		if(driver.findElements(By.id(id)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.id(id)));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
		}
		waitPage(wait);
	}

	public void waitElementByXpath(String xpath){
		driver.findElement(By.xpath(xpath));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
	}

	public void waitElementPresenceByXpath(String xpath){
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
	}


	public void createNewItem() {
		if(driver.findElements(By.xpath(xpathCreate)).size() > 0)
			scrollClickXpath(xpathCreate);

		if(driver.findElements(By.xpath(xpathConfirm)).size() > 0)
			clickEnterUntilGonePresence(xpathConfirm);
	}

	public void waitPresenceById(String id){
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
	}

	public void saveItem() {
		waitPage(wait);
		if(driver.findElements(By.xpath(xpathSave)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.xpath(xpathSave)));

			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathSave)));
			driver.findElement(By.xpath(xpathSave)).click();
			wait.withTimeout(Duration.ofSeconds(5));
		}

		waitPage(wait);
		if(driver.findElements(By.xpath(xpathPopupPrimaryButton)).size() != 0)
			driver.findElement(By.xpath(xpathPopupPrimaryButton)).click();
		waitPage(wait);

		if(driver.findElements(By.xpath(xpathPopupPrimaryButton)).size() != 0)
			driver.findElement(By.xpath(xpathPopupPrimaryButton)).click();
		waitPage(wait);
	}

	public void reloadItem() {
		waitPage(wait);
		if(driver.findElements(By.xpath(xpathReload)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.xpath(xpathReload)));

			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathReload)));
			driver.findElement(By.xpath(xpathReload)).click();
			wait.withTimeout(Duration.ofSeconds(5));
		}

		waitPage(wait);
		if(driver.findElements(By.xpath(xpathPopupPrimaryButton)).size() != 0)
			driver.findElement(By.xpath(xpathPopupPrimaryButton)).click();
		waitPage(wait);

		if(driver.findElements(By.xpath(xpathPopupPrimaryButton)).size() != 0)
			driver.findElement(By.xpath(xpathPopupPrimaryButton)).click();
		waitPage(wait);
	}

	public boolean confirmItemExistence(String id, String table, int columnIndex){
		if(driver.findElements(By.xpath(table)).size() != 0 && driver.findElement(By.xpath(table)).isDisplayed()){
			;
		} else{
			scrollClickId(idBrowseButton);
		}
		waitPageTimes(2);

		if(maximize)
			scrollClickXpath(xpathMaximizeModalTable);


		waitPage(wait);
		//Scroll all down and up
		/*wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		((JavascriptExecutor) driver).executeScript( 					"arguments[0].scrollTo(0, document.body.scrollHeight)", driver.findElement(By.xpath(xpathScrollBarModal)));
		waitPage(wait);
		((JavascriptExecutor) driver).executeScript( 					"arguments[0].scrollTo(0, document.body.scrollHeight)", driver.findElement(By.xpath(xpathScrollBarModal)));
		waitPage(wait);*/

		//TODO when having more than 11 entries, tableRows won't contemplate the remaining entries. When scrolling all the way down, it just finds the latest 11 entries created. I can't seem to find a way to get them all. Scrolling for each 10 entries (having the 10-nth, 20-nth, 30... always on top is also difficult and seems like a bad idea). If the environment we're in is always clean before starting the tests, we probably won't face this issue.

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		List<WebElement> tableRows = driver.findElement(By.xpath(table)).findElements(By.tagName("tr"));

		for (WebElement tableRow : tableRows){
			if(replaceSelectAndConfirmItems){
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().replace(" ", "").trim().equals(id)) {
					assertTrue(true);
					if(close){
						if(driver.findElements(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).size() > 0){
							driver.findElement(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).click();

						} else{
							driver.findElement(By.xpath(xpathCloseModal)).click();
						}
						return true;
					}
				}
			} else {
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().trim().equals(id)) {
					assertTrue(true);
					if(close){
						if(driver.findElements(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).size() > 0){
							driver.findElement(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).click();

						} else{
							driver.findElement(By.xpath(xpathCloseModal)).click();
						}
						return true;
					}
					return true;
				}
			}
		}

		if(driver.findElements(By.xpath(xpathCloseSecondUpperRightButtons)).size() != 0){
			scrollClickXpath(xpathCloseSecondModal);
			return true;
		}

		if(driver.findElements(By.xpath(xpathCloseUpperRightButtons)).size() != 0){
			scrollClickXpath(xpathCloseModal);
			return true;
		}

		if(maximize) {
			scrollClickXpath(xpathCloseModal);
			return true;
		}
		return false;
	}

	public boolean confirmItemExistenceByTableId(String id, String idTable, int columnIndex){
		String table = idToXpath(idTable);
		if(!table.contains("//tbody")){
			table += "//tbody";
		}
		if(driver.findElements(By.xpath(table)).size() != 0 && driver.findElement(By.xpath(table)).isDisplayed()){
			;
		} else{
			scrollClickId(idBrowseButton);
		}
		waitPageTimes(2);

		if(maximize)
			scrollClickXpath(xpathMaximizeModalTable);

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		List<WebElement> tableRows = driver.findElement(By.xpath(table)).findElements(By.tagName("tr"));

		for (WebElement tableRow : tableRows){
			if(replaceSelectAndConfirmItems){
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().replace(" ", "").trim().equals(id)) {
					assertTrue(true);
					if(close){
						if(driver.findElements(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).size() > 0){
							driver.findElement(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).click();

						} else{
							driver.findElement(By.xpath(xpathCloseModal)).click();
						}
						return true;
					}
				}
			} else {
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().trim().equals(id)) {
					assertTrue(true);
					if(close){
						if(driver.findElements(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).size() > 0){
							driver.findElement(By.xpath("//html//*[@class='v-window v-widget v-has-width v-has-height']//*[@class='v-window-closebox']")).click();

						} else{
							driver.findElement(By.xpath(xpathCloseModal)).click();
						}
						return true;
					}
					return true;
				}
			}
		}

		if(driver.findElements(By.xpath(xpathCloseSecondUpperRightButtons)).size() != 0){
			scrollClickXpath(xpathCloseSecondModal);
			return true;
		}

		if(driver.findElements(By.xpath(xpathCloseUpperRightButtons)).size() != 0){
			scrollClickXpath(xpathCloseModal);
			return true;
		}

		if(maximize && driver.findElements(By.xpath(xpathCloseModal)).size() > 0) {
			scrollClickXpath(xpathCloseModal);
			return true;
		}
		return false;
	}

	public boolean confirmItemExistenceMultipleFields(Map<String, Integer> fieldAndIndex, String table){
		if(driver.findElements(By.xpath(table)).size() != 0 && driver.findElement(By.xpath(table)).isDisplayed()){
			;
		} else{
			scrollWaitElementByXpath(xpathSearch);
			driver.findElement(By.xpath(xpathSearch)).click();
		}
		waitPage(wait);
		//Scroll all down and up
		/*wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		((JavascriptExecutor) driver).executeScript( 					"arguments[0].scrollTo(0, document.body.scrollHeight)", driver.findElement(By.xpath(xpathScrollBarModal)));
		waitPage(wait);
		((JavascriptExecutor) driver).executeScript( 					"arguments[0].scrollTo(0, document.body.scrollHeight)", driver.findElement(By.xpath(xpathScrollBarModal)));
		waitPage(wait);*/

		//TODO when having more than 11 entries, tableRows won't contemplate the remaining entries. When scrolling all the way down, it just finds the latest 11 entries created. I can't seem to find a way to get them all. Scrolling for each 10 entries (having the 10-nth, 20-nth, 30... always on top is also difficult and seems like a bad idea). If the environment we're in is always clean before starting the tests, we probably won't face this issue.

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		List<WebElement> tableRows = driver.findElement(By.xpath(table)).findElements(By.tagName("tr"));

		//For every row, we check how many fields and values are present for our Map. We save them so we can check for a whole "green" check
		for (WebElement tableRow : tableRows){
			if(replaceSelectAndConfirmItems){
				int trueCount = 0;
				for (Map.Entry<String, Integer> e : fieldAndIndex.entrySet()){
					if(tableRow.findElements(By.tagName("td")).get(e.getValue()).getText().replace(" ", "").trim().equals(e.getKey()))
						trueCount++;
				}
				if(trueCount == fieldAndIndex.keySet().size()){
					if(driver.findElements(By.xpath(xpathCloseSecondUpperRightButtons)).size() != 0){
						wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathCloseSecondModal)));
						driver.findElement(By.xpath(xpathCloseSecondModal)).click();
						waitPage(wait);
						return true;
					}

					if(driver.findElements(By.xpath(xpathCloseUpperRightButtons)).size() != 0){
						wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathCloseModal)));
						driver.findElement(By.xpath(xpathCloseModal)).click();
						waitPage(wait);
						return true;
					}

				}
			} else {
				int trueCount = 0;
				for (Map.Entry<String, Integer> e : fieldAndIndex.entrySet()){
					if(tableRow.findElements(By.tagName("td")).get(e.getValue()).getText().trim().equals(e.getKey()))
						trueCount++;
				}
				if(trueCount == fieldAndIndex.keySet().size()){
					if(driver.findElements(By.xpath(xpathCloseSecondUpperRightButtons)).size() != 0){
						wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathCloseSecondModal)));
						driver.findElement(By.xpath(xpathCloseSecondModal)).click();
						waitPage(wait);
						return true;
					}

					if(driver.findElements(By.xpath(xpathCloseUpperRightButtons)).size() != 0){
						wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathCloseModal)));
						driver.findElement(By.xpath(xpathCloseModal)).click();
						waitPage(wait);
						return true;
					}

				}
			}

		}


		return false;
	}

	public void selectCheckboxFromTableMultipleFields(String xpathTable, GenericGrid grid, int startColumn) {
		//Note: This method contemplates checkboxes that should be clicked(selected/true).
		if (scrollTopTable(xpathTable) == 0) {
			return;
		}
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));
		Actions actions = new Actions(driver);
		Set<String> checkedRows = new HashSet<>();
		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
		int checkedCount = 0;
		boolean found = false;
		while (checkedCount < totalRows) {
			List<WebElement> visibleRows = tableBody.findElements(By.tagName("tr"));
			Collections.reverse(visibleRows);
			for (WebElement tr : visibleRows) {
				String rowIdentifier = tr.getText();
				if (checkedRows.contains(rowIdentifier)) {
					continue;
				}
				List<WebElement> cells = tr.findElements(By.tagName("td"));
				int checks = 0;
				if (cells.size() >= startColumn + grid.getColumnValues().size()) {
					for (int i = 0; i < grid.getColumnValues().size(); i++) {
						if (cells.get(startColumn + i).getText().equals(grid.getColumnValues().get(i))) {
							checks++;
						}
					}
				}
				if (checks == grid.getColumnValues().size()) {
					found = true;
					WebElement checkbox = cells.get(0).findElement(By.tagName("input"));
					if (checkbox.getAttribute("type").equals("checkbox")) {
						if(!checkbox.isSelected()) {
							checkbox.click();
						}else{
							fail("Checkbox already selected");}
					} else {
						fail("Checkbox not found");
					}
				}
				checkedRows.add(rowIdentifier);
				checkedCount++;
			}
			actions.sendKeys(Keys.ARROW_DOWN).perform();
			if(found)
				break;
		}
		if (!found) {
			fail("Entry not found");
		}
	}

	public boolean boolConfirmMultipleTableRows(String xpathTable, List<? extends GenericGrid> gridList, int startColumn) {
		boolean found = false;
		Set<String> checkedRows = new HashSet<>();
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));
		Actions actions = new Actions(driver);

		for (GenericGrid grid : gridList) {
			List<String> columnValues = grid.getColumnValues();
			int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
			int checkedCount = 0;
			boolean entryFound = false;

			while (checkedCount < totalRows) {
				List<WebElement> visibleRows = tableBody.findElements(By.tagName("tr"));
				if (visibleRows.isEmpty()) {
					fail("No visible rows found.");
					break;
				}

				for (WebElement tr : visibleRows) {
					String rowIdentifier = tr.getText();
					if (checkedRows.contains(rowIdentifier) || rowIdentifier.isEmpty()) {
						continue;
					}

					List<WebElement> cells = tr.findElements(By.tagName("td"));

					if (!cells.isEmpty() && cells.get(startColumn).getText().equals(columnValues.get(0))) {
						int checks = 1;

						for (int i = 1; i < columnValues.size(); i++) {
							int colIndex = startColumn + i;

							if (colIndex >= cells.size() || !isElementInViewport(cells.get(colIndex))) {
								scrollHorizontally(xpathTable + "//div[6]", 300);
								waitPage(wait);
								cells = tr.findElements(By.tagName("td"));
							}

							if(cells.get(colIndex).getText().isEmpty() && !columnValues.get(i).isEmpty()){
								scrollHorizontally(xpathTable + "//div[6]", 100);
								if (cells.get(colIndex).getText().equals(columnValues.get(i))) {
									checks++;
								}
								scrollHorizontally(xpathTable + "//div[6]", -100);
							} else {
								if (cells.get(colIndex).getText().equals(columnValues.get(i))) {
									checks++;
								}
							}
						}

						if (checks == columnValues.size()) {
							entryFound = true;
							break;
						}
					}
					checkedRows.add(rowIdentifier);
					checkedCount++;
				}

				if (entryFound) {
					break;
				}
				actions.sendKeys(Keys.ARROW_DOWN).perform();
			}

			if (entryFound) {
				found = true;
			} else {
				found = false;
				fail("Entry not found for: " + grid.getColumnValues());
			}
		}
		return found;
	}

	private boolean isElementInViewport(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (Boolean) js.executeScript(
				"var rect = arguments[0].getBoundingClientRect();" +
						"return (rect.left >= 0 && rect.right <= (window.innerWidth || document.documentElement.clientWidth));",
				element);
	}

	public void confirmAndSelectMultipleFields(String xpathTable, GenericGrid grid, int startColumn) {
		Set<String> checkedRows = new HashSet<>();
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));
		Actions actions = new Actions(driver);
		List<String> columnValues = grid.getColumnValues();
		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
		int checkedCount = 0;
		boolean entryFound = false;

		while (checkedCount < totalRows) {
			List<WebElement> visibleRows = tableBody.findElements(By.tagName("tr"));
			if (visibleRows.isEmpty()) {
				fail("No visible rows found.");
				break;
			}

			for (WebElement tr : visibleRows) {
				String rowIdentifier = tr.getText();
				if (checkedRows.contains(rowIdentifier) || rowIdentifier.isEmpty()) {
					continue;
				}

				List<WebElement> cells = tr.findElements(By.tagName("td"));

				if (!cells.isEmpty() && cells.get(startColumn).getText().equals(columnValues.get(0))) {
					int checks = 1;
					actions.click(cells.get(startColumn)).perform();

					for (int i = 1; i < columnValues.size(); i++) {
						actions.sendKeys(Keys.ARROW_RIGHT).perform();
						waitPage(wait);
						cells = tr.findElements(By.tagName("td"));
						int currentColIndex = startColumn + i;

						if (currentColIndex < cells.size()) {
							String cellValue = cells.get(currentColIndex).getText();
							if (!cellValue.equals(columnValues.get(i))) {
								break;
							} else {
								checks++;
							}
						} else {
							break;
						}
					}

					if (checks == columnValues.size()) {
						entryFound = true;
						if(isDoubleClick){
							actions.doubleClick(tr).perform();
						} else {
							actions.click(tr).perform();
						}
						checkedCount = totalRows;
						break;
					}
				}

				checkedRows.add(rowIdentifier);
				checkedCount++;
			}

			actions.sendKeys(Keys.ARROW_DOWN).perform();
		}

		if (!entryFound) {
			fail("Entry not found for: " + grid.getColumnValues());
		}
	}

	protected void selectItemSearchTable(String id, String table, int columnIndex){
		if(driver.findElements(By.xpath(table)).size() != 0 && driver.findElement(By.xpath(table)).isDisplayed()){
			;
		} else{
			scrollClickId(idBrowseButton);
		}
		waitPageTimes(2);
		if(maximize)
			scrollClickXpath(xpathMaximizeModalTable);

		//This line fails a lot on Run mode
		waitPage(wait);
		wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		WebElement tableProducts = driver.findElement(By.xpath(table));

		List<WebElement> tableRows = wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableProducts, By.tagName("tr")));

		for (WebElement tableRow : tableRows) {
			if(replaceSelectAndConfirmItems){
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().replace(" ", "").trim().equals(id)) {
					Actions act = new Actions(driver);
					if(isDoubleClick){
						act.doubleClick(tableRow).perform();
					} else {
						act.click(tableRow).perform();
					}

					break;
				}
			} else {
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().trim().equals(id)) {
					Actions act = new Actions(driver);
					if(isDoubleClick){
						act.doubleClick(tableRow).perform();
					} else {
						act.click(tableRow).perform();
					}
					break;
				}
			}

		}
	}

	protected void selectItemSearchByTableId(String id, String idTable, int columnIndex){
		String table = idToXpath(idTable);
		if(driver.findElements(By.xpath(table)).size() != 0 && driver.findElement(By.xpath(table)).isDisplayed()){
			;
		} else{
			scrollClickId(idBrowseButton);
		}
		waitPageTimes(2);

		if(maximize)
			scrollClickXpath(xpathMaximizeModalTable);

		waitPage(wait);
		wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		WebElement tableProducts = driver.findElement(By.xpath(table));

		List<WebElement> tableRows = wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableProducts, By.tagName("tr")));

		for (WebElement tableRow : tableRows) {
			if(replaceSelectAndConfirmItems){
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().replace(" ", "").trim().equals(id)) {
					Actions act = new Actions(driver);
					if(isDoubleClick){
						act.doubleClick(tableRow).perform();
					} else {
						act.click(tableRow).perform();
					}

					break;
				}
			} else {
				if (tableRow.findElements(By.tagName("td")).get(columnIndex).getText().trim().equals(id)) {
					Actions act = new Actions(driver);
					if(isDoubleClick){
						act.doubleClick(tableRow).perform();
					} else {
						act.click(tableRow).perform();
					}
					break;
				}
			}

		}
	}

	protected void selectItemSearchTableMultipleFields(Map<String, Integer> fieldAndIndex, String table){
		if(driver.findElements(By.xpath(table)).size() != 0 && driver.findElement(By.xpath(table)).isDisplayed()){
			;
		} else{
			scrollWaitElementByXpath(xpathSearch);
			driver.findElement(By.xpath(xpathSearch)).click();
		}

		//This line fails a lot on Run mode
		waitPage(wait);
		wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(table)));
		WebElement tableProducts = driver.findElement(By.xpath(table));

		List<WebElement> tableRows = wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableProducts, By.tagName("tr")));

		for (WebElement tableRow : tableRows){
			int trueCount = 0;
			for (Map.Entry<String, Integer> e : fieldAndIndex.entrySet()){
				if(replaceSelectAndConfirmItems){
					if(tableRow.findElements(By.tagName("td")).get(e.getValue()).getText().replace(" ", "").trim().equals(e.getKey()))
						trueCount++;
				} else {
					if(tableRow.findElements(By.tagName("td")).get(e.getValue()).getText().trim().equals(e.getKey()))
						trueCount++;
				}

			}
			if(trueCount == fieldAndIndex.keySet().size()){
				Actions act = new Actions(driver);
				if(isDoubleClick){
					act.doubleClick(tableRow).perform();
				} else {
					act.click(tableRow).perform();
				}
				break;
			}
		}
		waitPage(wait);
	}

	protected void reloadAndConfirmItemByIdField(String id, String xpathId){
		scrollWaitElementByXpath(xpathReload);
		driver.findElement(By.xpath(xpathReload)).click();

		assertEquals(id, driver.findElement(By.xpath(xpathId)).getAttribute("value"));
	}

	protected void deleteItem(String id, String xpathId){

		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();", driver.findElement(By.xpath(xpathId)));

		if(driver.findElements(By.xpath(xpathId)).size() > 0)
			assertEquals(id, driver.findElement(By.xpath(xpathId)).getAttribute("value"));

		scrollWaitElementByXpath(xpathDelete);
		driver.findElement(By.xpath(xpathDelete)).click();

		scrollWaitElementByXpath(xpathConfirm);
		driver.findElement(By.xpath(xpathConfirm)).click();
		waitPage(wait);

		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView();", driver.findElement(By.xpath(xpathId)));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathId)));
		driver.findElement(By.xpath(xpathId)).click();
		assertEquals("", driver.findElement(By.xpath(xpathId)).getAttribute("value"));
	}

	public void deleteItemById(){
		scrollClickId(idDeleteButton);
		clickUntilGoneById(idConfirmOk);
		deleted = true;
	}

	public void deleteItemGridMinusButton(WebElement currentTr, String idMinus, String idSave){
		logMsg("(Base.deleteItemGridMinusButton) Deleting tr: " + currentTr.toString());
		if(!deleted) {
			if (!isTrSelectedOrFocused(currentTr))
				currentTr.click();
			scrollClickId(idMinus);
			if(isBeingDisplayedWithCss(cssErrorNotification) || isBeingDisplayedWithCss(cssTinyErrorMessage))
				fail("Failed to delete row");
			if (driver.findElements(By.id(idConfirmOk)).size() > 0)
				clickUntilGoneById(idConfirmOk);
			scrollClickId(idSave);
			deleted = true;
		}
	}

	public void deleteItemByIdConfirmValId(String id, String value){
		logMsg("(Base.deleteItemByIdConfirmValId) Deleting value: " + value);
		if(!deleted) {
			if(!isDisabledById(idDeleteButton))
				scrollClickId(idDeleteButton);
			waitPage(wait);
			clickUntilGoneById(idConfirmOk);
			if(isBeingDisplayedWithCss(cssErrorNotification)){
				String error = driver.findElement(By.cssSelector(cssErrorNotification)).findElement(By.cssSelector(".gwt-HTML")).findElements(By.tagName("p")).get(1).getText();
				logMsg("(Base.deleteItemByIdConfirmValId) Couldn't verify successful delete");
				fail("Couldn't delete entry. Error: " + error);
			}
			scrollWaitElementById(id);
			assertNotEquals(getValId(id), value);
			deleted = true;
		}
	}

	public void deleteItemByIdConfirmMultipleValIds(HashMap<String, String> idsValues){
		logMsg("(Base.deleteItemByIdConfirmMultipleValIds) Deleting ids: " + idsValues.values());
		if(!deleted) {
			scrollClickId(idDeleteButton);
			waitPage(wait);
			if (driver.findElements(By.id(idConfirmOk)).size() > 0)
				clickUntilGoneById(idConfirmOk);

			idsValues.forEach((key, value) -> {
				scrollWaitElementById(key);
				assertNotEquals(getValId(key), value);
			});
			deleted = true;
		}
	}

	public void deleteItemByIdConfirmMultipleValIdsInput(HashMap<String, String> idsValues){
		logMsg("(Base.deleteItemByIdConfirmMultipleValIdsInput) Deleting ids: " + idsValues.values());
		if(!deleted) {
			scrollClickId(idDeleteButton);
			waitPage(wait);
			if (driver.findElements(By.id(idConfirmOk)).size() > 0)
				clickUntilGoneById(idConfirmOk);

			idsValues.forEach((key, value) -> {
				scrollWaitElementById(key);
				assertNotEquals(getValIdInput(key), value);
			});
			deleted = true;
		}
	}

	public void deleteItemByIdConfirmValIdInput(String id, String value){
		logMsg("(Base.deleteItemByIdConfirmValIdInput) Deleting value " + value);
		if(!deleted) {
			scrollClickId(idDeleteButton);
			waitPage(wait);
			if (driver.findElements(By.id(idConfirmOk)).size() > 0)
				clickUntilGoneById(idConfirmOk);
			scrollWaitElementById(id);
			assertNotEquals(getValIdInput(id), value);
			deleted = true;
		}
	}

	public void searchForItem() {
		scrollWaitElementByXpathPresence(xpathSearch);
		driver.findElement(By.xpath(xpathSearch)).click();
		waitPage(wait);
	}

	public String generateNDigitsRandomNumber(int n)
	{
		Random randomNumber = new Random();
		int result = 0;
		for(int i = 0; i < n; i++) {
			result = result * 10 + (randomNumber.nextInt(9) + 1);
		}
		return String.valueOf(result);
	}

	public static String generateRandomNumberRange(int start, int end)
	{
		int result = start + (int)(Math.random() * ((end - start) + 1));
		return String.valueOf(result);
	}

	public int selectFirstVisibleTableRow(String xpathTable) {
		scrollWaitElementByXpath(xpathTable+ "//tbody");
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));

		if (rows.isEmpty()) {
			fail("No rows found in the table.");
			return 0;
		}

		for (WebElement row : rows) {
			try {
				row.click();
				return 1;
			} catch (Exception e) {
				continue;
			}
		}
		return 0;
	}


	public int selectLastVisibleTableRow(String xpathTable) {
		scrollWaitElementByXpath(xpathTable+ "//tbody");
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));

		if (rows.isEmpty()) {
			fail("No rows found in the table.");
			return 0;
		}

		// Try to click the last visible row
		for (int i = rows.size() - 1; i >= 0; i--) {
			WebElement row = rows.get(i);
			try {
				row.click();
				return 1;
			} catch (Exception e) {
				continue;
			}
		}

		return 0;
	}

	// This function will give a fail() if the table columns aren't fully visible.
	// When testing please be sure the table columns are full visible on the html.
	public WebElement getTableRow(String xpathTable, GenericGrid grid) {
		if (scrollTopTable(xpathTable) == 0) {
			return null;
		}

		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));

		Actions actions = new Actions(driver);
		Set<String> checkedRows = new HashSet<>(); // Store unique identifiers of checked rows
		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
		int checkedCount = 0;

		while (checkedCount < totalRows) {
			// Get the currently visible rows
			List<WebElement> visibleRows = tableBody.findElements(By.tagName("tr"));
			if (visibleRows.isEmpty()) {
				fail("No visible rows found.");
				break;
			}
			Collections.reverse(visibleRows);

			// Check each row for the required data using the callback function
			for (WebElement tr : visibleRows) {
				// Get unique identifier for the row, e.g., a combination of cell texts or a specific attribute
				String rowIdentifier = tr.getText();

				// Skip rows that have already been checked
				if (checkedRows.contains(rowIdentifier)) {
					continue;
				}

				List<WebElement> cells = tr.findElements(By.tagName("td"));
				int checks = 0;
				if (cells.size() > grid.getColumnValues().size()) {
					for (int i = 1; i < grid.getColumnValues().size(); i++) {
						if (cells.get(i).getText().equals(grid.getColumnValues().get(i - 1))) {
							actions.sendKeys(Keys.ARROW_RIGHT).perform();
							checks++;
						}
					}
				}
				for (int i = 1; i < grid.getColumnValues().size(); i++) {
					actions.sendKeys(Keys.ARROW_LEFT).perform();
				}
				if (checks >= grid.getColumnValues().size() - 1) {
					return tr;
				}

				// Add the row identifier to the set of checked rows
				checkedRows.add(rowIdentifier);
				checkedCount++;
			}

			actions.sendKeys(Keys.ARROW_DOWN).perform();
		}
		return null;
	}

	public int scrollTopTable(String xpathTable) {
		scrollWaitElementByXpath(xpathTable);
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		List<WebElement> rows;

		if(tableBody.findElements(By.tagName("tr")).size() == 0){
			fail("No rows found in the table.");
			return 0;
		}

		rows = tableBody.findElements(By.tagName("tr"));
		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount"));
		Actions actions = new Actions(driver);

		// Start by checking the first row
		WebElement initialRow = rows.get(0);

		// Try scrolling up to the top of the table
		selectFirstVisibleTableRow(xpathTable);
		for (int h = 1; h < totalRows; h++) {
			actions.sendKeys(Keys.ARROW_UP).perform();
			actions.sendKeys(Keys.ARROW_UP).perform();
			waitPage(wait);
			// Refetch the rows after each scroll
			List<WebElement> newRows = tableBody.findElements(By.tagName("tr"));

			// Check if the row at the top is still the initial row
			if (initialRow.equals(newRows.get(0))) {
				selectFirstVisibleTableRow(xpathTable);
				return 1;
			}

			// Update initialRow to the new top row
			initialRow = newRows.get(0);
		}

		// If we reach here, we have not verified the initial row at the top consistently
		return 0;
	}

	//Not optimized
	public int scrollBottomTable(String xpathTable) {
		scrollWaitElementByXpath(xpathTable+ "//tbody");
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));

		if (rows.isEmpty()) {
			fail("No rows found in the table.");
			return 0;
		}

		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
		Actions actions = new Actions(driver);

		for (WebElement row : rows) {
			try {
				row.click();
				for (int h = 1; h < totalRows; h++) {
					actions.sendKeys(Keys.ARROW_DOWN).perform();
				}
				return 1;
			} catch (Exception e) {
				continue;
			}
		}
		return 0;
	}
	public void confirmTableRows(String xpathTable, List<? extends GenericGrid> gridList) {
		for (GenericGrid grid : gridList) {
			if (getTableRow(xpathTable, grid) == null) {
				fail("Entry not found for: " + grid.getColumnValues());
			}
			scrollTopTable(xpathTable);
		}
	}

	// This function will give a fail() if the table columns aren't fully visible.
	// When testing please be sure the table columns are full visible on the html.
	public void confirmTableRowsCallback(String xpathTable, List<? extends GenericGrid> gridList, BiFunction<List<WebElement>, GenericGrid, Boolean> checkRowCallback) {
		scrollWaitElementByXpathPresence(xpathTable + "//tbody");

		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));
		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));

		if (rows.isEmpty()) {
			fail("No rows found in the table.");
			return;
		}

		Actions actions = new Actions(driver);
		rows.get(0).click();

		String ariaRowCount = table.getAttribute("aria-rowcount");
		int totalRows = 0;
		try {
			if (ariaRowCount != null) {
				totalRows = Integer.parseInt(ariaRowCount) - 1;
			} else {
				throw new NumberFormatException("aria-rowcount attribute is missing or null");
			}
		} catch (NumberFormatException e) {
			fail("Error parsing aria-rowcount attribute: " + e.getMessage());
			return;
		}

		for (GenericGrid grid : gridList) {
			boolean found = false;
			Set<String> checkedRows = new HashSet<>();
			int checkedCount = 0;

			while (checkedCount < totalRows) {
				List<WebElement> visibleRows = table.findElements(By.tagName("tr"));
				if (visibleRows.isEmpty()) {
					fail("No visible rows found.");
					break;
				}
				Collections.reverse(visibleRows);

				for (WebElement tr : visibleRows) {
					String rowIdentifier = tr.getText();

					if (checkedRows.contains(rowIdentifier)) {
						break;
					}

					List<WebElement> cells = tr.findElements(By.tagName("td"));

					if (checkRowCallback.apply(cells, grid)) {
						found = true;
						break;
					}

					checkedRows.add(rowIdentifier);
					checkedCount++;
				}
				if (found) {
					break;
				}
				actions.sendKeys(Keys.ARROW_DOWN).perform();
			}

			if (!found) {
				fail("Entry not found for: " + grid.getColumnValues());
			}
			for (int i = 1; i < totalRows; i++) {
				actions.sendKeys(Keys.ARROW_UP).perform();
			}
		}
	}

	//Waits for the id to be on the html page without scrolling, so it just wait it to appear on the viewed area.
	//N is the timeout so, if you pass 2 it will wait 2 times and if the id doesn't appear it will fail with timeout.
	public void waitForIdTimes(int n, String id){
		for (int i = 0; i < n; i++) {
			try {
				WebElement web  = driver.findElement(By.id(id));
				return;
			}catch (Exception e) {
				waitPage(wait);
			}
		}
		fail("Timeout: Web element not found id: " + id);
	}

	//Waits n times, n is the timeout so, if you pass 2 it will wait 2 times.
	//This function is just a waitPage(wait) multiplier, it never gives a fail timeout.
	public static void waitPageTimes(int n){
		for (int i = 0; i < n; i++) {
			waitPage(wait);
		}
	}

	//Waits for the xpath to be on the html page without scrolling, so it just wait it to appear on the viewed area.
	//N is the timeout so, if you pass 2 it will wait 2 times and if the xpath doesn't appear it will fail with timeout.
	public static void waitForXpathTimes(int n, String xpath){
		for (int i = 0; i < n; i++) {
			try {
				WebElement web  = driver.findElement(By.xpath(xpath));
				return;
			}catch (Exception e) {
				waitPage(wait);
			}
		}
		fail("Timeout: Web element not found xpath: " + xpath);
	}

	public void scrollClickId(String tabId) { //Scrolls, clicks and waits
		scrollWaitElementById(tabId);
		driver.findElement(By.id(tabId)).click();
		waitPage(wait);
	}

	public void scrollClickXpath(String xpath){
		scrollWaitElementByXpathPresence(xpath);
		driver.findElement(By.xpath(xpath)).click();
		waitPage(wait);
	}

	public void waitClickXpath(String xpath){
		waitElementPresenceByXpath(xpath);
		driver.findElement(By.xpath(xpath)).click();
		waitPage(wait);
	}

	public void waitClickId(String tabId) {
		waitPresenceById(tabId);
		driver.findElement(By.id(tabId)).click();
		waitPage(wait);
	}
	public void waitClickWebElement(WebElement w) {
		waitWebElement(w);
		w.click();
		waitPage(wait);
	}

	public void fillTwinColSelectById(String id, List<String> list){
		scrollWaitElementById(id);

		logMsg("    (Base.fillTwinColSelectById) id:<%s>, list:<%s>", id, list.toString());
		Select selectLeft = new Select(driver.findElement(By.id(id)).findElements(By.tagName("select")).get(0));
		Select selectRight = new Select(driver.findElement(By.id(id)).findElements(By.tagName("select")).get(1));
		WebElement btnLeft = driver.findElement(By.id(id)).findElement(By.className("v-select-twincol-buttons")).findElements(By.tagName("div")).get(0);
		WebElement btnRight = driver.findElement(By.id(id)).findElement(By.className("v-select-twincol-buttons")).findElements(By.tagName("div")).get(2);

		//Add desired values
		for(String l : list) {
			selectLeft.selectByVisibleText(l);
			waitClickWebElement(btnLeft);
		}

		//Delete undesired values
		for(WebElement r : selectRight.getOptions()){
			String val = r.getText();
			if(!list.contains(val)){
				selectRight.selectByVisibleText(val);
				waitClickWebElement(btnRight);

			}
		}
	}

	public void confirmTwinColSelectById(String id, List<String> list){
		scrollWaitElementById(id);
		Select selectLeft = new Select(driver.findElement(By.id(id)).findElements(By.tagName("select")).get(0));
		Select selectRight = new Select(driver.findElement(By.id(id)).findElements(By.tagName("select")).get(1));

		//Check desired values
		List<String> rightSelectedOpts = selectRight.getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
		for(String l : list) {
			assertTrue(rightSelectedOpts.contains(l));
		}

		List<String> leftSelectedOpts = selectLeft.getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
		//Check undesired values
		for(String l : list) {
			assertFalse(leftSelectedOpts.contains(l));
		}
	}

	public void fillTextField(String value, String id) {
		logMsg("    (Base.fillTextField) id:<%s>, value:<%s>", id, value);
		driver.findElement(By.id(id)).clear();
		driver.findElement(By.id(id)).sendKeys(value);
		waitPage(wait);
	}

	public void fillTextFieldAndAssert(String value, String id) {
		logMsg("    (Base.fillTextFieldAndAssert) id:<%s>, value:<%s>", id, value);
		driver.findElement(By.id(id)).clear();
		driver.findElement(By.id(id)).sendKeys(value);
		waitPage(wait);
		assertEquals(value, driver.findElement(By.id(id)).getAttribute("value"));
	}

	public void fillDatesOrXpathFields(String value, String xpath) {		logMsg("    (Base.fillDatesOrXpathFields) xpath:<%s>, value:<%s>", xpath, value);
		driver.findElement(By.xpath(xpath)).clear();
		driver.findElement(By.xpath(xpath)).sendKeys(value);
		waitPage(wait);
	}

	public void fillDatesOrInputFieldsById(String value, String id) {		logMsg("    (Base.fillDatesOrInputFieldsById) id:<%s>, value:<%s>", id, value);
		waitPage(wait);
		scrollWaitElementById(id);
		WebElement w = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		w.clear();
		w.sendKeys(value);
		waitPage(wait);
	}

	public void fillDatesOrInputFieldsByIdClickEnter(String value, String id) {
		logMsg("    (Base.fillDatesOrInputFieldsByIdClickEnter) id:<%s>, value:<%s>", id, value);
		driver.findElement(By.id(id)).clear();
		driver.findElement(By.id(id)).sendKeys(value);
		driver.findElement(By.id(id)).sendKeys(Keys.ENTER);
		waitPage(wait);
	}

	public void fillDatesOrXpathFieldsClickEnter(String value, String xpath) {
		logMsg("    (Base.fillDatesOrXpathFieldsClickEnter) xpath:<%s>, value:<%s>", xpath, value);
		driver.findElement(By.xpath(xpath)).clear();
		driver.findElement(By.xpath(xpath)).sendKeys(value);
		driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
		waitPage(wait);
	}

	public void fillDatesOrXpathFieldsAndAssert(String value, String xpath) {
		logMsg("    (Base.fillDatesOrXpathFieldsAndAssert) xpath:<%s>, value:<%s>", xpath, value);
		driver.findElement(By.xpath(xpath)).clear();
		driver.findElement(By.xpath(xpath)).sendKeys(value);
		waitPage(wait);
		assertEquals(value, driver.findElement(By.xpath(xpath)).getAttribute("value"));
	}

	public void fillSelectAndAssert(String xpath, Map<String, Boolean> map){
		logMsg("    (Base.fillSelectAndAssert) xpath:<%s>, map:<%s>", xpath, map.toString());
		waitElementPresenceByXpath(xpath);
		Select select = new Select(driver.findElement(By.xpath(xpath)));
		select.deselectAll();
		for(Map.Entry<String, Boolean> m : map.entrySet())
			if(m.getValue()){
				select.selectByVisibleText(m.getKey());
				boolean found = false;
				for(WebElement s : select.getAllSelectedOptions())
					if(s.getText().equals(m.getKey())) {
						found = true;
						break;
					}

				if(!found)
					fail("Couldn't confirm " + m.getKey() + " is selected.");
			}
	}

	public void fillSelect(String xpath, Map<String, Boolean> map){
		logMsg("    (Base.fillSelect) xpath:<%s>, map:<%s>", xpath, map.toString());
		waitElementPresenceByXpath(xpath);
		Select select = new Select(driver.findElement(By.xpath(xpath)));
		select.deselectAll();
		for(Map.Entry<String, Boolean> m : map.entrySet())
			if(m.getValue())
				select.selectByVisibleText(m.getKey());
	}

	public void confirmSelectValues(String xpathSel, List<String> list) {
		Select select = new Select(driver.findElement(By.xpath(xpathSel)));
		List<WebElement> selectedOptions = select.getAllSelectedOptions();
		List<String> selectedTexts = selectedOptions.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
		for(String sa : list)
			assertTrue(selectedTexts.contains(sa));
	}

	public boolean isCheckboxSelected(String xpath){
		return driver.findElement(By.xpath(xpath + "//input")).isSelected();
	}

	public boolean isCheckboxSelectedById(String id){
		WebElement cInput = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		return cInput.isSelected();
	}

	public boolean isRadioButtonSelectedById(boolean b, String id){
		if(b)
			return driver.findElement(By.id(id)).findElements(By.tagName("span")).get(0).findElement(By.tagName("input")).isSelected();
		else
			return driver.findElement(By.id(id)).findElements(By.tagName("span")).get(1).findElement(By.tagName("input")).isSelected();

	}

	public boolean isXpathSelected(String xpath){
		return driver.findElement(By.xpath(xpath)).isSelected();
	}

	public void fillCheckboxAndSelectAndAssert(boolean b, String xpathCheckbox, String xpathSelect, List<String> selectValues) {
		logMsg("    (Base.fillCheckboxAndSelectAndAssert) xpathCheckbox:<%s>, xpathSelect:<%s>, selectValues:<%s>, b:<%s>", xpathCheckbox, xpathSelect, selectValues.toString(), b ? "Yes" : "No");
		if (b) {
			WebElement checkbox = driver.findElement(By.xpath(xpathCheckbox + "//input"));
			if (!checkbox.isSelected()) {
				driver.findElement(By.xpath(xpathCheckbox + "//label")).click();
				assertTrue(checkbox.isSelected());
			}
			WebElement dropdown = driver.findElement(By.xpath(xpathSelect));
			Select select = new Select(dropdown);
			for (String s : selectValues) {
				select.selectByVisibleText(s);
			}
			List<WebElement> selectedOptions = select.getAllSelectedOptions();
			List<String> selectedTexts = selectedOptions.stream()
					.map(WebElement::getText)
					.collect(Collectors.toList());
			for (String value : selectValues) {
				assertTrue(selectedTexts.contains(value));
			}
		} else {
			if (driver.findElement(By.xpath(xpathCheckbox + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheckbox + "//label")).click();
				assertFalse(driver.findElement(By.xpath(xpathCheckbox + "//input")).isSelected());
			}
		}

		waitPage(wait);
	}

	public void fillCheckboxAndDatetimeAndAssert(boolean b, String xpathCheckbox, String xpathDatetime, String datetimeValue) {
		logMsg("    (Base.fillCheckboxAndDatetimeAndAssert) xpathCheckbox:<%s>, xpathDatetime:<%s>, datetimeValue:<%s>, b:<%s>", xpathCheckbox, xpathDatetime, datetimeValue, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpathCheckbox + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheckbox + "//label")).click();
				assertTrue(driver.findElement(By.xpath(xpathCheckbox + "//input")).isSelected());
				driver.findElement(By.xpath(xpathDatetime)).clear();
				driver.findElement(By.xpath(xpathDatetime)).sendKeys(datetimeValue);
				waitPage(wait);
				assertEquals(datetimeValue, driver.findElement(By.xpath(xpathDatetime)).getAttribute("value"));
			}
		} else{
			if (driver.findElement(By.xpath(xpathCheckbox + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheckbox + "//label")).click();
				assertFalse(driver.findElement(By.xpath(xpathCheckbox + "//input")).isSelected());
				driver.findElement(By.xpath(xpathDatetime)).clear();
				driver.findElement(By.xpath(xpathDatetime)).sendKeys(datetimeValue);
				waitPage(wait);
				assertEquals(datetimeValue, driver.findElement(By.xpath(xpathDatetime)).getAttribute("value"));
			}
		}
		waitPage(wait);
	}

	public void fillCheckbox(boolean b, String xpath) {
		logMsg("    (Base.fillCheckbox) xpath:<%s>, value:<%s>", xpath, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpath + "//input")).isSelected())
				driver.findElement(By.xpath(xpath + "//label")).click();
		} else{
			if (driver.findElement(By.xpath(xpath + "//input")).isSelected())
				driver.findElement(By.xpath(xpath + "//label")).click();
		}
		waitPage(wait);
	}

	public void fillCheckboxById(boolean b, String id) {
		logMsg("    (Base.fillCheckboxById) id:<%s>, b:<%s>", id, b ? "Yes" : "No");
		WebElement cInput = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		WebElement cLabel = driver.findElement(By.id(id)).findElement(By.tagName("label"));
		if(b) {
			if (!cInput.isSelected())
				cLabel.click();
		} else{
			if (cInput.isSelected())
				cLabel.click();
		}
		waitPage(wait);
	}

	public void fillCheckboxNoLabelById(boolean b, String id) {
		logMsg("    (Base.fillCheckboxNoLabelById) id:<%s>, b:<%s>", id, b ? "Yes" : "No");
		WebElement cInput = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		WebElement cId = driver.findElement(By.id(id));
		if(b) {
			if (!cInput.isSelected())
				cId.click();
		} else{
			if (cInput.isSelected())
				cId.click();
		}
		waitPage(wait);
	}

	public void fillCheckboxAndAssert(boolean b, String xpath) {
		logMsg("    (Base.fillCheckboxAndAssert) xpath:<%s>, value:<%s>", xpath, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpath + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpath + "//label")).click();
				assertTrue(driver.findElement(By.xpath(xpath + "//input")).isSelected());
			}
		} else{
			if (driver.findElement(By.xpath(xpath + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpath + "//label")).click();
				assertFalse(driver.findElement(By.xpath(xpath + "//input")).isSelected());
			}
		}
		waitPage(wait);
	}

	public void fillCheckboxNoLabel(boolean b, String xpath) {
		logMsg("    (Base.fillCheckboxNoLabel) xpath:<%s>, b:<%s>", xpath, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpath + "//input")).isSelected())
				driver.findElement(By.xpath(xpath)).click();
		} else{
			if (driver.findElement(By.xpath(xpath + "//input")).isSelected())
				driver.findElement(By.xpath(xpath)).click();
		}
		waitPage(wait);
	}

	public void fillCheckboxNoLabelAndAssert(boolean b, String xpath) {		logMsg("    (Base.fillCheckboxNoLabelAndAssert) xpath:<%s>, b:<%s>", xpath, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpath + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpath)).click();
				assertTrue(driver.findElement(By.xpath(xpath + "//input")).isSelected());
			}
		} else{
			if (driver.findElement(By.xpath(xpath + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpath)).click();
				assertFalse(driver.findElement(By.xpath(xpath + "//input")).isSelected());
			}
		}
		waitPage(wait);
	}

	public void fillCheckboxAndTextField(boolean b, String xpathCheck, String idTextField, String value) {
		logMsg("    (Base.fillCheckboxAndTextField) xpathCheck:<%s>, idTextField:<%s>, value:<%s>, b:<%s>", xpathCheck, idTextField, value, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpathCheck + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
			}
			scrollWaitElementByIdPresence(idTextField);
			driver.findElement(By.id(idTextField)).sendKeys(value);
		} else{
			if (driver.findElement(By.xpath(xpathCheck + "//input")).isSelected())
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
		}

		waitPage(wait);
	}

	public void clickAndConfirmChangeOfDialog(String title, String idButton){
		waitPage(wait);
		scrollClickId(idButton);
		if(isBeingDisplayedWithID(idConfirmOk)){
			waitClickId(idConfirmOk);
		}
		waitPage(wait);
		scrollWaitElementByIdPresence(idTitle);
		assertEquals(title, getTextId(idTitle));
		scrollClickId(idCloseDialogButton);
		waitPage(wait);
	}

	public void confirmRowByMap(Map<Integer, String> rowValuesToFind, String tableId, int startColumn){
		int nrValues = rowValuesToFind.size();
		waitPage(wait);
		List<WebElement> tableRows = getTableRows(tableId);

		for (WebElement tr : tableRows) {
			for(int y = startColumn; y < nrValues; y++) {
				if(!tdEquals(tr, y, rowValuesToFind.get(y))){
					logMsg("Row" + tr.getText() + " didn't match");
					y = nrValues;
				} else {
					logMsg("Row" + tr.getText() + " matched");
					return;
				}
			}
		}

		fail("Item " + rowValuesToFind + " not found");
	}


	public void fillCheckboxAndTextFieldAndAssert(boolean b, String xpathCheck, String idTextField, String value) {
		logMsg("    (Base.fillCheckboxAndTextFieldAndAssert) xpathCheck:<%s>, idTextField:<%s>, value:<%s>, b:<%s>", xpathCheck, idTextField, value, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpathCheck + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
				assertTrue(driver.findElement(By.xpath(xpathCheck + "//input")).isSelected());
			}
			scrollWaitElementByIdPresence(idTextField);
			driver.findElement(By.id(idTextField)).sendKeys(value);
			assertEquals(value, driver.findElement(By.id(idTextField)).getAttribute("value"));
		} else{
			if (driver.findElement(By.xpath(xpathCheck + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
				assertFalse(driver.findElement(By.xpath(xpathCheck + "//input")).isSelected());
			}
		}

		waitPage(wait);
	}

	public void fillCheckboxAndXpathText(boolean b, String xpathCheck, String xpathText, String value) {
		logMsg("    (Base.fillCheckboxAndXpathText) xpathCheck:<%s>, xpathText:<%s>, value:<%s>, b:<%s>", xpathCheck, xpathText, value, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpathCheck + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
			}
			scrollWaitElementByXpathPresence(xpathText);
			driver.findElement(By.xpath(xpathText)).clear();
			driver.findElement(By.xpath(xpathText)).sendKeys(value);
		} else{
			if (driver.findElement(By.xpath(xpathCheck + "//input")).isSelected())
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
		}

		waitPage(wait);
	}

	public void fillCheckboxAndXpathTextAndAssert(boolean b, String xpathCheck, String xpathText, String value) {
		logMsg("    (Base.fillCheckboxAndXpathTextAndAssert) xpathCheck:<%s>, xpathText:<%s>, value:<%s>, b:<%s>", xpathCheck, xpathText, value, b ? "Yes" : "No");
		if(b) {
			if (!driver.findElement(By.xpath(xpathCheck + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
				assertTrue(driver.findElement(By.xpath(xpathCheck + "//input")).isSelected());
			}
			scrollWaitElementByXpathPresence(xpathText);
			driver.findElement(By.xpath(xpathText)).clear();
			driver.findElement(By.xpath(xpathText)).sendKeys(value);
			assertEquals(value, driver.findElement(By.xpath(xpathText)).getAttribute("value"));
		} else{
			if (driver.findElement(By.xpath(xpathCheck + "//input")).isSelected()) {
				driver.findElement(By.xpath(xpathCheck + "//label")).click();
				assertFalse(driver.findElement(By.xpath(xpathCheck + "//input")).isSelected());
			}
		}

		waitPage(wait);
	}

	//@author psilva
	public void fillAndAssertSelectById(String idSelect, List<String> values) {
		logMsg("    (Base.fillAndAssertSelectById) idSelect:<%s>, values:<%s>", idSelect, values.toString());
		WebElement element = driver.findElement(By.id(idSelect)).findElement(By.tagName("select"));
		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		for(WebElement option : options){
			if(values.contains(option.getText())){
				actions.click(option).perform();
				assertTrue(option.isSelected());
				values.remove(option.getText());
			}
		}
		if(values.size() > 0){
			fail("Values: " + values + " not found on select element");
		}
	}

	public void fillAndAssertRadioButtonGroup(String xpath){
		logMsg("    (Base.fillAndAssertRadioButtonGroup) xpath:<%s>", xpath);
		waitClickXpath(xpath + "//label");
		assertTrue(driver.findElement(By.xpath(xpath + "//input")).isSelected());
	}

	public void fillRadioButtonGroup(String xpath){
		logMsg("    (Base.fillDatesOrXpathFieldsClickEnter) xpath:<%s>", xpath);
		if(xpath.isEmpty())
			return;
		waitClickXpath(xpath + "//label");
		assertTrue(driver.findElement(By.xpath(xpath + "//input")).isSelected());
	}

	public void fillRadioButtonGroupById(boolean b, String id) {
		logMsg("    (Base.fillRadioButtonGroupById) id:<%s>, b:<%s>", id, b ? "Yes" : "No");
		if(b)
			driver.findElement(By.id(id)).findElements(By.tagName("span")).get(0).findElement(By.tagName("label")).click();
		else
			driver.findElement(By.id(id)).findElements(By.tagName("span")).get(1).findElement(By.tagName("label")).click();
	}

	public void fillRadioButtonGroupByIdIndex(int ind, String id) {
		logMsg("    (Base.fillRadioButtonGroupByIdIndex) id:<%s>, ind:<%d>", id, ind);
		scrollWaitElementById(id);
		driver.findElement(By.id(id)).findElements(By.tagName("span")).get(ind).findElement(By.tagName("label")).click();
	}

	public void isRadioButtonGroupSelectedByIdIndex(int ind, String id) {
		scrollWaitElementById(id);
		assertTrue(driver.findElement(By.id(id)).findElements(By.tagName("span")).get(ind).findElement(By.tagName("input")).isSelected());
	}

	public void fillSelectAndAssert(String xpathSelect, List<String> selectValues) {
		logMsg("    (Base.fillSelectAndAssert) xpathSelect:<%s>, selectValues:<%s>", xpathSelect, selectValues.toString());
		WebElement dropdown = driver.findElement(By.xpath(xpathSelect));
		Select select = new Select(dropdown);
		for (String s : selectValues) {
			select.selectByVisibleText(s);
		}
		List<WebElement> selectedOptions = select.getAllSelectedOptions();
		List<String> selectedTexts = selectedOptions.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
		for (String value : selectValues) {
			assertTrue(selectedTexts.contains(value));
		}

		waitPage(wait);
	}

	public void fillSelect(String xpathSelect, List<String> selectValues) {
		logMsg("    (Base.fillSelect) xpathSelect:<%s>, selectValues:<%s>", xpathSelect, selectValues.toString());
		WebElement dropdown = driver.findElement(By.xpath(xpathSelect));
		Select select = new Select(dropdown);
		for (String s : selectValues) {
			select.selectByVisibleText(s);
		}

		waitPage(wait);
	}

	public void fillSelectById(String idSelect, List<String> values) {
		logMsg("    (Base.fillSelectById) idSelect:<%s>, values:<%s>", idSelect, values.toString());
		WebElement element = driver.findElement(By.id(idSelect)).findElement(By.tagName("select"));
		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		for(WebElement option : options){
			if(values.contains(option.getText())){
				actions.click(option).perform();
			}
		}
	}

	public void confirmSelectById(String idSelect, List<String> expectedValues) {
		WebElement sel = driver.findElement(By.id(idSelect)).findElement(By.tagName("select"));
		Select select = new Select(sel);
		List<WebElement> options = select.getOptions();

		List<String> actualValues = options.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());

		if (!actualValues.containsAll(expectedValues) || actualValues.size() != expectedValues.size())
			fail("Select values don't match expected values\nExpected: " + expectedValues + "\nActual: " + actualValues);
	}

	public static void scrollHorizontally(String xpathScroller, int pixels) {
		if(driver.findElements(By.xpath(xpathScroller)).size() > 0) {
			Actions actions = new Actions(driver);
			WebElement scrollbar = driver.findElement(By.xpath(xpathScroller));
			if (driver.findElement(By.xpath(xpathScroller)).isDisplayed()) {
				actions.clickAndHold(scrollbar).moveByOffset(pixels, 0).release().perform();
			}
		}
	}

	public static void scrollVertically(String xpathScroller, int pixels) {
		if(driver.findElements(By.xpath(xpathScroller)).size() > 0) {
			Actions actions = new Actions(driver);
			WebElement scrollbar = driver.findElement(By.xpath(xpathScroller));
			if (driver.findElement(By.xpath(xpathScroller)).isDisplayed()) {
				actions.clickAndHold(scrollbar).moveByOffset(0, pixels).release().perform();
			}
		}
	}

	public void sortAllFieldsForSorting(String xpathAvailableFields, String idLeftButton) {
		scrollWaitElementByXpathPresence(xpathAvailableFields);
		while (true) {
			List<WebElement> tableRows = driver.findElement(By.xpath(xpathAvailableFields)).findElements(By.tagName("tr"));
			if (tableRows.isEmpty()) {
				break;
			}
			WebElement firstRow = tableRows.get(0);
			if(!firstRow.getAttribute("aria-selected").equals("true")) {
				firstRow.findElements(By.tagName("td")).get(0).click();
			}
			driver.findElement(By.id(idLeftButton)).click();
		}
	}

	public void confirmTwinSelect(String xpathSelect, List<String> selectValues) {
		Select s2 = new Select(driver.findElement(By.xpath(xpathSelect + "[2]")));
		for (String s : selectValues) {
			boolean found = false;
			for (WebElement option : s2.getOptions()) {
				if (option.getText().equals(s)) {
					found = true;
					break;
				}
			}
			if (!found) {
				fail("Value " + s + " is not selected in the second twin select.");
			}
		}
		waitPage(wait);
	}

	public String alpha3(){
		return RandomStringUtils.randomAlphabetic(3);
	}
	public String alpha4(){
		return RandomStringUtils.randomAlphabetic(43);
	}

	public String alpha2(){
		return RandomStringUtils.randomAlphabetic(2);
	}
	public String numeric2(){
		return generateRandomNumberRange(10, 99);
	}

	public String numeric3(){
		return generateRandomNumberRange(100, 999);
	}

	public String numeric4(){
		return generateRandomNumberRange(1000, 9999);
	}

	public String alphaNum3(){
		return RandomStringUtils.randomAlphanumeric(3);
	}
	public String alphaNum4(){
		return RandomStringUtils.randomAlphanumeric(4);
	}
	public String alphaNum2(){
		return RandomStringUtils.randomAlphanumeric(2);
	}

	public String alphaNum8(){
		return RandomStringUtils.randomAlphanumeric(8);
	}

	public String alpha8(){
		return RandomStringUtils.randomAlphabetic(8);
	}

	public String alphaNum6(){
		return RandomStringUtils.randomAlphanumeric(6);
	}

	public void errorMsgStartsWith(String xpathError, String startsWith){
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathError)));
		assertTrue(driver.findElement(By.xpath(xpathError)).getText().startsWith(startsWith));
		waitPage(wait);
		driver.findElement(By.xpath(xpathError)).click();
		waitPage(wait);
	}

	public void errorMsgContains(String xpathError, String contains){
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpathError)));
		assertTrue(driver.findElement(By.xpath(xpathError)).getText().contains(contains));
		waitPage(wait);
		driver.findElement(By.xpath(xpathError)).click();
		waitPage(wait);
	}

	public boolean isBeingDisplayedWithXpath(String xpathElement){
		try {
			return driver.findElement(By.xpath(xpathElement)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isBeingDisplayedWithID(String idElement){
		try {
			return driver.findElement(By.id(idElement)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isBeingDisplayedWithCss(String cssElement){
		try {
			return driver.findElement(By.cssSelector(cssElement)).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}


	public void selectRowCheckbox(WebElement tr) {
		if(!isTrSelectedGrid(tr))
			tr.findElements(By.tagName("td")).get(0).findElements(By.tagName("span")).get(0).click();
		waitPage(wait);
	}

	public void selectRow(WebElement tr) {
		if (!tr.getAttribute("aria-selected").equals("true"))
			tr.click();
		waitPage(wait);
	}


	public void selectItemFromTableMultipleFields(String xpathTable, GenericGrid grid, int startColumn) {
		logMsg("    (Base.selectItemFromTableMultipleFields) xpathTable:<%s>, Grid:<%s>, startColumn:<%d>", xpathTable, grid.toString(), startColumn);
		if (scrollTopTable(xpathTable) == 0) {
			return;
		}

		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));

		Actions actions = new Actions(driver);
		Set<String> checkedRows = new HashSet<>();
		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
		int checkedCount = 0;

		while (checkedCount < totalRows) {
			List<WebElement> visibleRows = tableBody.findElements(By.tagName("tr"));
			Collections.reverse(visibleRows);

			for (WebElement tr : visibleRows) {
				String rowIdentifier = tr.getText();
				if (checkedRows.contains(rowIdentifier)) {
					continue;
				}

				List<WebElement> cells = tr.findElements(By.tagName("td"));
				int checks = 0;

				if (cells.size() >= startColumn + grid.getColumnValues().size()) {
					for (int i = 0; i < grid.getColumnValues().size(); i++) {
						if (cells.get(startColumn + i).getText().equals(grid.getColumnValues().get(i))) {
							checks++;
						}
					}
				}

				if (checks == grid.getColumnValues().size()) {
					if(isDoubleClick){
						actions.doubleClick(tr).perform();
					} else {
						actions.click(tr).perform();
					}
					return;
				}

				checkedRows.add(rowIdentifier);
				checkedCount++;
			}

			actions.sendKeys(Keys.ARROW_DOWN).perform();
		}
	}

	public void confirmMultipleTableRows(String xpathTable, List<? extends GenericGrid> gridList, int startColumn) {
		int columnGroupSize = 9;

		for (GenericGrid grid : gridList) {
			List<String> columnValues = grid.getColumnValues();
			int totalColumns = columnValues.size();
			int numberOfGroups = (int) Math.ceil((double) totalColumns / columnGroupSize);

			for (int group = 0; group < numberOfGroups; group++) {
				int startIdx = group * columnGroupSize;
				int endIdx = Math.min(startIdx + columnGroupSize, totalColumns);

				List<String> currentGroup = columnValues.subList(startIdx, endIdx);

				GenericGrid partialGrid = new GenericGrid(currentGroup);

				if (getTableRowWithStartColumn(xpathTable, partialGrid, startColumn + startIdx) == null) {
					fail("Entry not found for: " + grid.getColumnValues());
				}

				if (group < numberOfGroups - 1) {
					String xpathScroller = xpathTable + "//div[2]";
					scrollHorizontally(xpathScroller, 500);
					waitPage(wait);
				}
			}

			scrollTopTable(xpathTable);
		}
	}

	public WebElement getTableRowWithStartColumn(String xpathTable, GenericGrid grid, int startColumn) {
		if (scrollTopTable(xpathTable) == 0) {
			return null;
		}

		WebElement tableBody = driver.findElement(By.xpath(xpathTable + "//tbody"));
		WebElement table = driver.findElement(By.xpath(xpathTable + "//table"));

		Actions actions = new Actions(driver);
		Set<String> checkedRows = new HashSet<>(); // Store unique identifiers of checked rows
		int totalRows = Integer.parseInt(table.getAttribute("aria-rowcount")) - 1;
		int checkedCount = 0;

		while (checkedCount < totalRows) {
			List<WebElement> visibleRows = tableBody.findElements(By.tagName("tr"));
			if (visibleRows.isEmpty()) {
				fail("No visible rows found.");
				break;
			}
			Collections.reverse(visibleRows);
			for (WebElement tr : visibleRows) {
				String rowIdentifier = tr.getText();
				if (checkedRows.contains(rowIdentifier)) {
					continue;
				}
				List<WebElement> cells = tr.findElements(By.tagName("td"));
				int checks = 0;
				if (cells.size() >= startColumn + grid.getColumnValues().size()) {
					for (int i = 0; i < grid.getColumnValues().size(); i++) {
						if (cells.get(startColumn + i).getText().equals(grid.getColumnValues().get(i))) {
							checks++;
						}
					}
				}
				if (checks == grid.getColumnValues().size()) {
					return tr;
				}
				checkedRows.add(rowIdentifier);
				checkedCount++;
			}
			actions.sendKeys(Keys.ARROW_DOWN).perform();
		}
		return null;
	}

	public boolean isTrSelectedOrFocused(WebElement tr){
		return tr.isSelected() || tr.getAttribute("class").contains("focused") || tr.getAttribute("class").contains("selected");
	}

	public boolean isTrSelectedGrid(WebElement tr){
		return tr.isSelected() || tr.getAttribute("class").contains("selected");
	}

	public String getSrcTr(WebElement tr, int index) {
		return tr.findElements(By.tagName("td")).get(index).findElement(By.tagName("img")).getAttribute("src");
	}

	public boolean isCurrentTrSelectedGrid(){
		return currentTr.isSelected() || currentTr.getAttribute("class").contains("selected");
	}

	public boolean isCurrentTrSelectedOrFocused(){
		return currentTr.isSelected() || currentTr.getAttribute("class").contains("focused") || currentTr.getAttribute("class").contains("selected");
	}

	public String tdCurrentGetText(int i){
		return currentTr.findElements(By.tagName("td")).get(i).getText();
	}

	public String getTdText(WebElement tr, int i){
		return tr.findElements(By.tagName("td")).get(i).getText();
	}

	public String getCurrentTdText(int i){
		return currentTr.findElements(By.tagName("td")).get(i).getText();
	}

	public void clickTr(WebElement tr){
		if(!isTrSelectedGrid(tr))
			tr.click();
	}

	public void clickCurrentTr(){
		if(!isTrSelectedGrid(currentTr))
			currentTr.click();
	}

	public void doubleClickTr(WebElement tr){
		if(!isTrSelectedGrid(tr)){
			Actions act = new Actions(driver);
			act.doubleClick(tr).perform();
			waitPage(wait);
		}
	}

	public void doubleClickCurrentTr(){
		if(!isTrSelectedGrid(currentTr)){
			Actions act = new Actions(driver);
			act.doubleClick(currentTr).perform();
			waitPage(wait);
		}
	}

	public boolean tdEquals(int i, String val){
		String uiText = currentTr.findElements(By.tagName("td")).get(i).getText();
		logMsg("(Base.tdEquals) Comparing column %d: <%s> vs. <%s>", i, uiText, val);
		return uiText.equals(val);
	}

	public boolean tdEquals(WebElement tr, int i, String val){
		String uiText = tr.findElements(By.tagName("td")).get(i).getText();
		logMsg("(Base.tdEquals) Comparing column %d: <%s> vs. <%s>", i, uiText, val);
		return uiText.equals(val);
	}

	public boolean tdStartsW(int tdIndex, String val){
		return currentTr.findElements(By.tagName("td")).get(tdIndex).getText().startsWith(val);
	}

	public boolean tdStartsW(WebElement tr,int tdIndex, String val){
		return tr.findElements(By.tagName("td")).get(tdIndex).getText().startsWith(val);
	}

	public boolean tdContains(int tdIndex, String val){
		return currentTr.findElements(By.tagName("td")).get(tdIndex).getText().contains(val);
	}

	public boolean tdContains(WebElement tr, int tdIndex, String val){
		return tr.findElements(By.tagName("td")).get(tdIndex).getText().contains(val);
	}

	public boolean valueTdContains(int tdIndex, String val){
		return val.contains(currentTr.findElements(By.tagName("td")).get(tdIndex).getText());
	}

	public boolean valueTdContains(WebElement tr, int tdIndex, String val){
		return val.contains(tr.findElements(By.tagName("td")).get(tdIndex).getText());
	}

	public boolean valueTdStartsW(int tdIndex, String val){
		return val.startsWith(currentTr.findElements(By.tagName("td")).get(tdIndex).getText());
	}

	public boolean valueTdStartsW(WebElement tr, int tdIndex, String val){
		return val.startsWith(tr.findElements(By.tagName("td")).get(tdIndex).getText());
	}

	public boolean valueTdEquals(int tdIndex, String val){
		return val.equals(currentTr.findElements(By.tagName("td")).get(tdIndex).getText());
	}

	public boolean valueTdEquals(WebElement tr, int tdIndex, String val){
		return val.equals(tr.findElements(By.tagName("td")).get(tdIndex).getText());
	}

	public int getRow(List<String> list, String value) {
		return list.indexOf(value) + 1;
	}

	public String getValId(String id) {
		return driver.findElement(By.id(id)).getAttribute(
				"value");
	}

	public String getValXpath(String xpath) {
		return driver.findElement(By.xpath(xpath)).getAttribute("value");
	}

	public String getTextXpath(String xpath) {
		return driver.findElement(By.xpath(xpath)).getText();
	}

	public boolean getIsSelectedXpath(String xpath) {
		return driver.findElement(By.xpath(xpath+"//input")).isSelected();
	}

	/**
	 * is button disabled
	 * @param id html identifier
	 * @return true or false
	 */
	public boolean isButtonDisabled (String id) {
		String outerHtml = getWebElementById(id).getAttribute("outerHTML");
		return outerHtml.contains("v-disabled");
	}

	public void closeDialogById () {
		if(driver.findElements(By.id(idCloseDialogButton)).size() != 0)
			scrollClickId(idCloseDialogButton);
	}

	/*
	 * methods without xpath
	 */
	public void createNewItemById() {
		// in case there are previous unclosed error messages
		closeErrorOrNewOrGoHomePresenceByIdCss();
		if(driver.findElements(By.id(idCreateButton)).size() > 0)
			scrollClickId(idCreateButton);

		if(driver.findElements(By.id(idConfirmDialogOkButton)).size() > 0)
			clickEnterUntilGonePresenceById(idConfirmDialogOkButton);
	}

	public void clickESCUntilGoneByCssSelector(String css) {
		scrollWaitElementByCssSelectorPresence(css);
		while (driver.findElements(By.cssSelector(css)).size() > 0)
			actions.sendKeys(Keys.ESCAPE).perform();
	}

	public void closeErrorOrNewOrGoHomePresenceByIdCssLogMsg(String dialogId) {
		logMsg("(" + dialogId + ") Leaving");
		waitPage(wait);
		clickESCUntilGoneByCssSelector(cssErrorNotification);
		waitPage(wait);
		clickESCUntilGoneByCssSelector(cssTinyErrorMessage);

		waitPage(wait);
		waitPage(wait);

		if(driver.findElements(By.id(idConfirmDialogOkButton)).size() != 0)
			driver.findElement(By.id(idConfirmDialogOkButton)).click();

		if(driver.findElements(By.id(idMasterDataTab)).size() != 0){
			scrollWaitElementByIdPresence(idMasterDataTab);
			driver.findElement(By.id(idMasterDataTab)).click();
		}
	}

	public void closeErrorOrNewOrGoHomePresenceByIdCss() {
		waitPage(wait);
		clickESCUntilGoneByCssSelector(cssErrorNotification);
		waitPage(wait);
		clickESCUntilGoneByCssSelector(cssTinyErrorMessage);

		waitPage(wait);
		waitPage(wait);

		if(driver.findElements(By.id(idConfirmDialogOkButton)).size() != 0)
			driver.findElement(By.id(idConfirmDialogOkButton)).click();

		if(driver.findElements(By.id(idMasterDataTab)).size() != 0){
			scrollWaitElementByIdPresence(idMasterDataTab);
			driver.findElement(By.id(idMasterDataTab)).click();
		}
	}

	/**
	 * log message that test is beginning
	 * @param dialogId html id
	 * @param number of the test
	 */
	public void testStartLogMsg (String dialogId, int number) {
		logMsg("(" + dialogId + ") Testcase #" + number + " starts");
	}

	/**
	 * log message that test is ending
	 * @param dialogId html id
	 * @param number of the test
	 */
	public void testEndLogMsg (String dialogId, int number) {
		logMsg("(" + dialogId + ") Testcase #"+number+" ends");
	}


	/**
	 * get Dialog title using css selector and tagName
	 * @return String or empty String if Dialog title couldn't be found
	 */
	public String getDialogTitle () {
		List<WebElement> elemList = null;
		if (!(elemList = driver.findElements(By.id(idDialogHeadline))).isEmpty()
				&&  !(elemList = elemList.get(0).findElements(By.tagName("b"))).isEmpty()){
			return elemList.get(0).getText();
		}

		return "";
	}

	/**
	 * select value in combobox
	 * in this combobox we need to use tag "input" to get to it
	 * @param id identifier of combobox
	 * @param value to select
	 */
	public void selectComboBoxValueByIdTag(String id, String value){
		logMsg("    (Base.selectComboBoxValueByIdTag) ID:<%s>, value:<%s>", id, value);
		// wait till the web element is completly init
		scrollWaitElementById(id);

		// id element is div
		WebElement drp = driver.findElement(By.id(id));

		// need to get the input element which is a submodule of div
		drp = drp.findElement(By.tagName("input"));

		if(!value.isEmpty() && !drp.getAttribute("value").equals(value)){
			drp.click();
			drp.sendKeys(Keys.BACK_SPACE);
			drp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			drp.sendKeys(Keys.DELETE);
			drp.sendKeys(value);
			drp.sendKeys(Keys.ENTER);
			waitPage(wait);
			assertTrue(drp.getAttribute("value").startsWith(value));
		}
	}

	/**
	 * the same method as dropdownsWritingSelectionById(), without assertTrue method
	 * the method is not showing error at the end, it is being used if another value is going to be tried
	 * @param id html identifier
	 * @param value that needs to be present
	 * @return true or false
	 */
	public boolean isDropdownValueSelected (String id, String value) {
		// id element is div
		WebElement drp = driver.findElement(By.id(id));

		// need to get the input element which is a submodule of div
		drp = drp.findElement(By.tagName("input"));

		if(!value.isEmpty() && !drp.getAttribute("value").equals(value)){
			drp.click();
			drp.sendKeys(Keys.BACK_SPACE);
			drp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			drp.sendKeys(Keys.DELETE);
			drp.sendKeys(value);
			drp.sendKeys(Keys.ENTER);
			waitPage(wait);

			return drp.getAttribute("value").startsWith(value);
		}

		return false;
	}

	public void scrollWaitElementById(String id){
		if(driver.findElements(By.id(id)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
								"arguments[0].scrollIntoView();", driver.findElement(By.id(id)));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
		}
		waitPage(wait);
	}

	public void scrollWaitElementById2(String id){
		if(driver.findElements(By.id(id)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start'});", driver.findElement(By.id(id)));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
		}
		waitPage(wait);
	}

	public List<WebElement> getTableRows (String idTable) {
		return driver.findElement(By.id(idTable)).findElement(By.tagName("table")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
	}

	public WebElement getTableRowTd (WebElement tr, int index) {
		return tr.findElements(By.tagName("td")).get(index);
	}

	/**
	 * NOT WORKING AT THE MOMENT
	 * main buttons (new, browse,...) are dissapearing when method is being used
	 * wait until parent element and child element are present
	 * @param id parent element
	 * @param tag child element
	 */
	public void scrollWaitElementByIdTag(String id, String tag){
		// work around using list because error is shown if directly trying to access tagName, WebElement is not functioning properly
		List<WebElement> elementsList = null;

		// parent element
		if(!(elementsList = driver.findElements(By.id(id))).isEmpty() && !elementsList.get(0).findElements(By.tagName(tag)).isEmpty()) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", elementsList.get(0).findElements(By.tagName(tag)));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.tagName(tag)));
		}
	}

	public void scrollWaitElementByCssSelectorPresence(String css){
		if(driver.findElements(By.cssSelector(css)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.cssSelector(css)));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(css)));
		}
		waitPage(wait);
	}

	/**
	 * clear and add new value to textfield
	 * @param id html identifier of textfield
	 * @param value to add
	 */
	public void newValueTextFieldById (String id, String value) {
		logMsg("    (Base.newValueTextFieldById) ID:<%s>, value:<%s>", id, value);
		// wait until the web element is fully init
		scrollWaitElementById(id);

		driver.findElement(By.id(id)).clear();
		driver.findElement(By.id(id)).sendKeys(value);
		waitPage(wait);
	}

	/**
	 * get text from textfield
	 * @param id html identifier of textfield
	 * @return String
	 */
	public String getValueTextFieldById (String id) {
		// wait till the web element is completly init
		scrollWaitElementById(id);

		return driver.findElement(By.id(id)).getAttribute("value");
	}

	/**
	 * get selected value from combobox
	 * @param id html identifier of combobox
	 * @return String
	 */
	public String getValueComboBoxById (String id) {
		return driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("value");
	}

	/**
	 * combobox by id is div element, so we need to go one more html element inside to get
	 * the actual select (combobox) element
	 * @param id is the identifier of the combobox
	 * @return WebElement
	 */
	public WebElement getComboBoxById (String id) {
		return driver.findElement(By.id(id)).findElement(By.tagName("input"));
	}

	/**
	 * clear and enter value into combobox
	 * can only be used for comboboxes which enable text input
	 * @param id identifier of combobox
	 * @param value to insert into combobox field
	 * @return
	 */
	public void newValueComboBoxById (String id, String value) {
		logMsg("    (Base.newValueComboBoxById) ID:<%s>, value:<%s>", id, value);
		// wait until the web element is fully init
		scrollWaitElementById(id);
		driver.findElement(By.id(id)).findElement(By.tagName("input")).clear();
		driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(value);
	}

	/**
	 * get list of webelements radio button options to select
	 * note: radio button itself is a sub html element from the html id element
	 * @param id is the html identifier
	 * @return list of radio button elements
	 */
	public List<WebElement> getRadioButtonElements (String id) {
		// wait until the web element is fully init
		scrollWaitElementById(id);

		// get the html name of the radio button
		WebElement element = driver.findElement(By.id(id));

		// get the list of all options of radio button
		List<WebElement> elements = element.findElements(By.tagName("span"));

		// return the element radio button list
		return elements;
	}

	public void waitElementByCssTag (String css, String tagName){
		WebElement element = driver.findElement(By.cssSelector(css));
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(css)));
		wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(element, By.cssSelector(css)));
	}

	public void waitElementPresenceById(String id){
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
		waitPage(wait);
	}

	public void waitElementById(String id){
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
		waitPage(wait);
	}

	public void waitWebElement(WebElement w){
		wait.until(ExpectedConditions.visibilityOf(w));
		waitPage(wait);
	}


	/*
	 * Not working at the moment
	 */
//	public void waitElementPresenceByIdTag(String id, String tagName){
//		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
//	}


	public void clickEnterUntilGonePresenceById(String id) {
		scrollWaitElementByIdPresence(id);
		while (driver.findElements(By.id(id)).size() > 0)
			actions.sendKeys(Keys.ENTER).perform();
	}

	public void comboboxRowSelectionById(int dropdownRow, String id, String value) {
		// wait until the web element is fully init
		scrollWaitElementById(id);

		if(dropdownRow != 0) {
			if(!driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("value").equals(value)) {
				driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
				waitPage(wait);
//				driver.findElement(By.id(id)).findElement(By.tagName(tag)).sendKeys(Keys.ARROW_DOWN);
				for (int i = 1; i < dropdownRow; i++) {
					driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ARROW_DOWN);
				}
				waitPage(wait);
				driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ENTER);
			}
		}
		waitPage(wait);
		assertTrue(driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("value").startsWith(value));
	}

	public void yesNoDropdownByIdTag(String id, boolean choice) {
		logMsg("    (Base.yesNoDropdownByIdTag) ID:<%s>, value:<%s>", id, choice ? "Yes" : "No");
		// wait until the web element is fully init
		scrollWaitElementById(id);

		waitPage(wait);
		if(choice){
			if(driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("value").equals("No") && driver.findElement(By.id(id)).findElement(By.tagName("input")).isEnabled()){
				driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
				driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ARROW_UP);
				waitPage(wait);//Yes SWS
				driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ENTER);
			} else{
				driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
				waitPage(wait);//Yes SWS
				driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ENTER);
			}
		} else{
			if(driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("value").equals("Yes") && driver.findElement(By.id(id)).findElement(By.tagName("input")).isEnabled()){
				driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
				driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ARROW_DOWN);
				waitPage(wait);//No SWS
				driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ENTER);
			} else{
				if(!driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("value").equals("No")){
					driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
					driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ARROW_DOWN);
					waitPage(wait);
					driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(Keys.ENTER);
				}
			}
		}
		waitPage(wait);
	}

	/**
	 * get web element by id
	 * only to be used if directly can access web element with id, not a subelement
	 * @param id html identifier
	 * @return web element
	 */
	public WebElement getWebElementById (String id) {
		scrollWaitElementById(id);
		waitPage(wait);
		return driver.findElement(By.id(id));
	}

	/**
	 * get error text message from error notification
	 * @return String containing error text message
	 */
	public String getErrorText () {
		return driver.findElement(By.cssSelector(cssErrorNotification)).findElement(By.cssSelector(".gwt-HTML")).findElements(By.tagName("p")).get(1).getText();
	}

	/**
	 * assertEquals() method with scrollWaitElement before it is executed
	 * @param value1 to compare
	 * @param value2 to compare
	 * @param id web element html identifier
	 */
	public void assertEqualsWaitById (String value1, String value2, String id) {
		// wait till the web element is completly init
		scrollWaitElementById(id);

		// check if two values are the same
		assertEquals(value1, value2);
	}

	public String getValIdInput(String id) {
		return driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute(
				"value");
	}

	public String getTextId(String id) {
		return driver.findElement(By.id(id)).getText();
	}

	public String getTextIdInput(String id) {
		return driver.findElement(By.id(id)).findElement(By.tagName("input")).getText();
	}



	public void yesNoDropdownById(String id, boolean choice) {
		logMsg("    (Base.dropdownsWritingSelectionById) ID:<%s>, value:<%s>", id, choice ? "Yes" : "No");
		waitPage(wait);
		scrollWaitElementById(id);
		WebElement drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		if (choice) {
			if (drp.getAttribute("value").equals("No") && drp.isEnabled()) {
				drp.click();
				drp.sendKeys(Keys.ARROW_UP);
				waitPage(wait);//Yes SWS
				drp.sendKeys(Keys.ENTER);
				waitPage(wait);
			} else {
				drp.click();
				waitPage(wait);//Yes SWS
				drp.sendKeys(Keys.ENTER);
				waitPage(wait);
			}
		} else {
			if (drp.getAttribute("value").equals("Yes") && drp.isEnabled()) {
				drp.click();
				drp.sendKeys(Keys.ARROW_DOWN);
				waitPage(wait);//No SWS
				drp.sendKeys(Keys.ENTER);
				waitPage(wait);
			} else {
				if (!drp.getAttribute("value").equals("No")) {
					drp.click();
					drp.sendKeys(Keys.ARROW_DOWN);
					waitPage(wait);
					drp.sendKeys(Keys.ENTER);
					waitPage(wait);
				}
			}
		}
	}

	public void dropdownsWritingSelectionById(String id, String value){
		logMsg("    (Base.dropdownsWritingSelectionById) ID:<%s>, value:<%s>", id, value);
		scrollWaitElementByIdPresence(id);

		// id element is div
		WebElement drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));

		if(!drp.getAttribute("value").equals(value)){
			drp.click();
			drp.sendKeys(Keys.BACK_SPACE);
			drp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			drp.sendKeys(Keys.DELETE);
			drp.sendKeys(value);
			drp.sendKeys(Keys.ENTER);
			waitPage(wait);
			assertTrue(drp.getAttribute("value").startsWith(value));
		}
	}


	public void dropdownsWritingSelectionId(String id, String value) {
		logMsg("    (Base.dropdownsWritingSelectionId) ID:<%s>, value:<%s>", id, value);
		scrollWaitElementById(id);
		WebElement drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		if (!value.isEmpty() && !drp.getAttribute("value").equals(value)) {
			drp.click();
			drp.sendKeys(Keys.BACK_SPACE);
			drp.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			drp.sendKeys(Keys.DELETE);
			drp.sendKeys(value);
			drp.sendKeys(Keys.ENTER);
			waitPage(wait);
			assertTrue(drp.getAttribute("value").startsWith(value));
		}
	}

	public void goToDialogAndAssertTitle(String dialog, String title){
		logMsg("(Base.goToDialogAndAssertTitle) Starting login process");
		LoginTest.loginOkAdmin();

		logMsg("(Base.goToDialogAndAssertTitle) Entering dialog name in search field");
		fillTextField(dialog, idSearchField);

		logMsg("(Base.goToDialogAndAssertTitle) Click on search button");
		scrollClickId(idSearchButton);

		logMsg("(Base.goToDialogAndAssertTitle) Waiting for title text");
		waitElementById(idTitle);
		assertEquals(title, getTextId(idTitle));

	}

	public void goToDialogAndAssertTitleNoLogin(String dialog, String title){
		scrollWaitElementById(idSearchField);
		fillTextField(dialog, idSearchField);
		scrollClickId(idSearchButton);
		waitElementById(idTitle);
		assertEquals(title, getTextId(idTitle));
	}

	public void confirmSelect(String xpathSelect, List<String> expectedValues) {
		WebElement sel = driver.findElement(By.xpath(xpathSelect));
		Select select = new Select(sel);
		List<WebElement> options = select.getOptions();

		List<String> actualValues = options.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());

		if (!actualValues.containsAll(expectedValues) || actualValues.size() != expectedValues.size())
			fail("Select values don't match expected values\nExpected: " + expectedValues + "\nActual: " + actualValues);

		waitPage(wait);
	}



	static public class GenericGrid {
		private final List<String> columnValues;

		public GenericGrid(List<String> columnValues) {
			this.columnValues = columnValues;
		}

		public List<String> getColumnValues() {
			return columnValues;
		}
	}

	public static String generatePassword() {
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String num = "0123456789";
		String specialChar = "!@#%";
		String combination = upper + upper.toLowerCase() + num + specialChar;
		int len = 6;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(combination.charAt(
					ThreadLocalRandom.current().nextInt(
							combination.length()
					)
			));
		}
		return sb.toString();
	}

	public void dropdownsWritingSelectionByIdTag(String id, String tagName, String value) {
		logMsg("    (Base.dropdownsWritingSelectionByIdTag) id:<%s>, tagName:<%s>, value:<%s>", id, tagName, value);

		//		scrollWaitElementByIdPresence(id);


		// id element is div

		WebElement drp = driver.findElement(By.id(id));


		// need to get the input element which is a submodule of div

		drp = drp.findElement(By.tagName(tagName));


		if (!value.isEmpty() && !drp.getAttribute("value").equals(value)) {

			drp.click();

			drp.sendKeys(Keys.BACK_SPACE);

			drp.sendKeys(Keys.chord(Keys.CONTROL, "a"));

			drp.sendKeys(Keys.DELETE);

			drp.sendKeys(value);

			drp.sendKeys(Keys.ENTER);

			waitPage(wait);

			assertTrue(drp.getAttribute("value").startsWith(value));

		}

	}

	public void dropdownsRowNumberSelectionById(int dropdownRow, String id, String value) {
		logMsg("    (Base.dropdownsRowNumberSelectionById) id:<%s>, dropdownRow:<%d>, value:<%s>", id, dropdownRow, value);
		scrollWaitElementById(id);
		WebElement drp = null;
		if (dropdownRow != 0) {
			drp = driver.findElement(By.id(id)).findElement(By.tagName("input"));
			if (!drp.getAttribute("value").equals(value)) {
				drp.click();
				for (int i = 1; i < dropdownRow; i++) {
					drp.sendKeys(Keys.ARROW_DOWN);
				}
				waitPage(wait);
				drp.sendKeys(Keys.ENTER);
			}
		}
		waitPage(wait);
		if(drp != null) {
			if (!drp.getAttribute("value").startsWith(value))
				fail("Value not found: " + value);
			else
				assertTrue(true);
		}
	}

	/**
	 * save record with main save button by id
	 */
	public void saveItemById() {
		waitPage(wait);
		scrollWaitElementById(idSaveButton);
		if(driver.findElement(By.id(idSaveButton)) != null) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView();", driver.findElement(By.id(idSaveButton)));

			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(idSaveButton)));
			driver.findElement(By.id(idSaveButton)).click();
			wait.withTimeout(Duration.ofSeconds(5));
		}

		// question to confirm save can be asked two times
		// 1. confirm save
		// 2. if data is to be overwritten, another question asking to confirm the save
		// each time html id "confirmdialog-ok-button" is going to be present
		clickUntilGoneById(idConfirmOk);
	}

	public void clickUntilGoneById(String id) {
		scrollWaitElementById(id);
		waitPageTimes(2);
		while(isBeingDisplayedWithID(id)){
			waitPageTimes(2);
			logMsg("(Base.clickUntilGoneById) - Clicking modal's confirm button");
			scrollClickId(id);
			waitPageTimes(2);
		}
	}

	public String getNowTimeDtf(String format){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.now().format(dtf);
	}
	public String getNowTimePlusDaysDtf(long n, String format){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.now().plusDays(n).format(dtf);
	}

	public String getNowTime(){
		final LocalDateTime nowTime = LocalDateTime.now();
		return  nowTime.toString().substring(8, 10) + "." + nowTime.toString().substring(5, 7) + "." + nowTime.toString().substring(0, 4) + " " + nowTime.toString().substring(11, 13) + ":" + nowTime.toString().substring(14, 16) + ":" + nowTime.toString().substring(17, 19);
	}

	public String getNowTimeOnlyDate(){
		final LocalDateTime nowTime = LocalDateTime.now();
		return  nowTime.toString().substring(8, 10) + "." + nowTime.toString().substring(5, 7) + "." + nowTime.toString().substring(0, 4);
	}

	public String getNowTimePlusDays(long n){
		final LocalDateTime nowTime = LocalDateTime.now().plusDays(n);
		return  nowTime.toString().substring(8, 10) + "." + nowTime.toString().substring(5, 7) + "." + nowTime.toString().substring(0, 4) + " " + nowTime.toString().substring(11, 13) + ":" + nowTime.toString().substring(14, 16) + ":" + nowTime.toString().substring(17, 19);
	}

	public String getNowTimeMinusDays(long n){
		final LocalDateTime nowTime = LocalDateTime.now().minusDays(n);
		return  nowTime.toString().substring(8, 10) + "." + nowTime.toString().substring(5, 7) + "." + nowTime.toString().substring(0, 4) + " " + nowTime.toString().substring(11, 13) + ":" + nowTime.toString().substring(14, 16) + ":" + nowTime.toString().substring(17, 19);
	}

	public String getNowTimePlusHours(long n){
		final LocalDateTime nowTime = LocalDateTime.now().plusHours(n);
		return  nowTime.toString().substring(8, 10) + "." + nowTime.toString().substring(5, 7) + "." + nowTime.toString().substring(0, 4) + " " + nowTime.toString().substring(11, 13) + ":" + nowTime.toString().substring(14, 16) + ":" + nowTime.toString().substring(17, 19);
	}

	public String getNowTimeMinusHours(long n){
		final LocalDateTime nowTime = LocalDateTime.now().minusHours(n);
		return  nowTime.toString().substring(8, 10) + "." + nowTime.toString().substring(5, 7) + "." + nowTime.toString().substring(0, 4) + " " + nowTime.toString().substring(11, 13) + ":" + nowTime.toString().substring(14, 16) + ":" + nowTime.toString().substring(17, 19);
	}

	public void isRadioButtonSelected (String id) {
		boolean isSelected = false;
		for (WebElement element : getRadioButtonElements(id)) {
			if (element.findElement(By.tagName("input")).isSelected()) {
				isSelected = true;
				break;
			}
		}
		assertTrue(isSelected);
	}

	/**
	 * check error message and assert if true or false
	 */
	public void checkErrorInputByCss (String errorText) {
		waitElementByCssSelector(cssErrorNotification);
		waitPage(wait);
		String errorHtml = driver.findElement(By.cssSelector(cssErrorNotification)).findElement(By.cssSelector(".gwt-HTML")).getText();
		assertTrue(errorHtml.contains(errorText));
	}

	public void waitElementByCssSelector(String css){
		waitPage(wait);
		waitPage(wait);
		if(driver.findElements(By.cssSelector(css)).size() != 0) {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start'});", driver.findElement(By.cssSelector(css)));
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(css)));
		}
	}

	public String idToXpath(String id){
		return "//*[@id='" + id + "']";
	}

	/**
	 * check if row contains the new inserted record values
	 * @param valuesFromTable are values inside each column
	 * @param valuesToFind are the values that need to be found in columns
	 * @return true or false
	 */
	private boolean isCreatedRecordFound(WebElement rowElement, List<String> valuesFromTable, Map<Integer, String> valuesToFind) {
		int count = 0;
		for (Map.Entry<Integer, String> item : valuesToFind.entrySet()) {
			int index = item.getKey();
			String valueToFind = item.getValue();
			String valueInTable = valuesFromTable.get(index);
			// if value does not match than break, it must be always a match in order to be correct
			if (!valueInTable.equals(valueToFind)) {
				count = 0;
				break;
			}
			count++;
		}
		// has the number of matched items been exact number in the map
		if (valuesToFind.size() == count) {
			Actions actions = new Actions(driver);
			if(isDoubleClick){
				// double click the row, it will be selected and search window will be closed
				actions.doubleClick(rowElement).perform();
			} else{
				actions.click(rowElement).perform();
			}
			return true;

		}
		return false;
	}


	/**
	 * get web element by id and input
	 * only to be used if directly can access web element together with id and input,
	 * if no subelement present, please use getWebElementById
	 * @param id html identifier
	 * @return web element
	 */
	public WebElement getWebElementByIdInput(String id) {
		scrollWaitElementById(id);
		waitPage(wait);
		return driver.findElement(By.id(id)).findElement(By.tagName("input"));
	}

	public JavascriptExecutor jse () {
		return (JavascriptExecutor) driver;
	}

	/**
	 * confirm and select in main search window grid that newly inserted record is present
	 * @param rowValuesToFind contains values of the new inserted row
	 * @param tableId is the name of the main search grid, id varies dependent on the Dialog
	 * @return true or false
	 */
	public boolean selectCreatedRecordSearch(Map<Integer, String> rowValuesToFind, String tableId){

		if(rowValuesToFind.size() == 0)
			fail("Map is empty, no values to select");

		if(!driver.findElements(By.id(tableId)).isEmpty() && driver.findElement(By.id(tableId)).isDisplayed()){
			;
		} else{
			scrollWaitElementById(idBrowseButton);
			waitPageTimes(2);
			scrollClickId(idBrowseButton);
		}
		waitPage(wait);
		return (selectCreatedRecordSearchAux(rowValuesToFind, tableId));
	}

	/**
	 * seperated selectCreatedRecordSearch in two parts when search Window is custom
	 * @param rowValuesToFind contains values of the new inserted row
	 * @param tableId is the name of the main search grid, id varies dependent on the Dialog
	 * @return true or false
	 */
	public boolean selectCreatedRecordSearchAux(Map<Integer, String> rowValuesToFind, String tableId){
		scrollWaitElementById(tableId);

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(tableId)));
		// get table div with html id identifier
		WebElement tableElement = getSearchTable(tableId);
		// click
		tableElement.click();
		tableElement.sendKeys(Keys.ARROW_DOWN);

		// refresh data
		scrollWaitElementById(tableId);
		tableElement = getSearchTable(tableId);
		int lastColumnIndex = 0;
		for (Map.Entry<Integer, String> entry : rowValuesToFind.entrySet()) {
			if (entry.getKey() > lastColumnIndex)
				lastColumnIndex = entry.getKey();
		}
		// get row list
		WebElement vgridbody = tableElement.findElement(By.className("v-grid-body"));
		List<WebElement> trList = vgridbody.findElements(By.tagName("tr"));
		for (int i = 0; i < trList.size(); i++) {
			WebElement row = trList.get(i);
			// go to the first column to start from the beggining
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				tableElement.sendKeys(Keys.ARROW_LEFT);
			}
			// now get each colum data with key arrow togethe
			List<String> tableRowValues = new ArrayList<>();
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				// refresh data
				tableElement = getSearchTable(tableId);
				vgridbody = tableElement.findElement(By.className("v-grid-body"));
				trList = vgridbody.findElements(By.tagName("tr"));
				row = trList.get(i);
				tableRowValues.add(row.findElements(By.className("v-grid-cell")).get(j).getText().trim());
				tableElement.sendKeys(Keys.ARROW_RIGHT);
			}
			// get row values
			// actual columns that exist must not be less than the number of column values to compare
			if (tableRowValues.size() < rowValuesToFind.size())
				return false;
			// compare last displayed row with initial values to be inserted
			if (isCreatedRecordFound(row, tableRowValues, rowValuesToFind))
				return true;
		}
		// if nothing found, we need to go through the whole search grid, checking each row for potential match
		// the problem is in the search table, last added entry can be anywhere in the table,
		// not necesseraly at the end of last row
		// we now go past the last focused row with arrow keys, checking each row if it is a match
		boolean isMoreScrolling = true;
		String previousRowNumber = null;
		while (isMoreScrolling) {
			// use down keys to go down again
			tableElement.sendKeys(Keys.ARROW_DOWN);
			// the data should have now changed, retrieve the data again
			tableElement = getSearchTable(tableId);
			// get the refreshed data row
			WebElement focusedRow = getRefreshedDataTableRow(tableElement);
			if (focusedRow == null)
				return false;
			// go to the first column to start from the beggining
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				tableElement.sendKeys(Keys.ARROW_LEFT);
			}

			// now get each colum data with key arrow togethe
			List<String> tableRowValues = new ArrayList<>();
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				// refresh data
				tableElement = getSearchTable(tableId);
				focusedRow = getRefreshedDataTableRow(tableElement);
				if (focusedRow == null)
					return false;

				tableRowValues.add(focusedRow.findElements(By.className("v-grid-cell")).get(j).getText().trim());
				tableElement.sendKeys(Keys.ARROW_RIGHT);
			}
			// if previous row is again taken, then end of table has been reached, inserted record has not been found
			if (previousRowNumber != null && previousRowNumber.equals(tableRowValues.get(0)))
				break;
			previousRowNumber = tableRowValues.get(0);
			// actual columns that exist must not be less than the number of column values to compare
			if (tableRowValues.size() < rowValuesToFind.size())
				return false;
			// refresh data
			focusedRow = getRefreshedDataTableRow(tableElement);
			// compare last displayed row with initial values to be inserted
			if(isCreatedRecordFound(focusedRow, tableRowValues, rowValuesToFind))
				return true;
		}
		// if yet not returned true, then it is false, whole grid was searched
		return false;
	}

	/**
	 * get table row with refreshed data, otherwise exception will occur
	 * note: every time a row is clicked it should be refreshed again, otherwise exception occurs
	 * @param tableElement to get new data
	 * @return WebElement or null if nothing found
	 */
	private WebElement getRefreshedDataTableRow (WebElement tableElement) {
		WebElement focusedRow = null;
		// there are two classes used to identify focused row, only one will be present
		String cssFocus1 = ".v-grid-row.v-grid-row-has-data.v-grid-row-stripe.v-grid-row-focused";
		String cssFocus2 = ".v-grid-row.v-grid-row-has-data.v-grid-row-focused";
		if (!tableElement.findElements(By.cssSelector(cssFocus1)).isEmpty())
			focusedRow = tableElement.findElement(By.cssSelector(cssFocus1));
		else if (!tableElement.findElements(By.cssSelector(cssFocus2)).isEmpty())
			focusedRow = tableElement.findElement(By.cssSelector(cssFocus2));
		if (focusedRow == null)
			return null;
		return focusedRow;
	}

	/**
	 * method is checking if table element is hidden under numerous subelements from html id
	 * this means that table is not available for interaction and we need to find the closer web element to it
	 * there can be two types of search tables
	 * 1. table id is withing the reach of the table itself
	 * 2. table id is outside where the subelement of table is located
	 * we need to check which table is in question in order to not get an error
	 * @param tableId html identifier
	 * @return table web element
	 */
	public WebElement getSearchTable (String tableId) {
		String searchCss = ".v-grid.v-widget.smallgrid2.v-grid-smallgrid2.v-has-width.v-has-height";
		String distantCss = ".v-slot.v-slot-smallgrid2";
		WebElement table = driver.findElement(By.id(tableId));
		boolean isDistantElem = !table.findElements(By.cssSelector(distantCss)).isEmpty();
		if (isDistantElem)
			return driver.findElement(By.id(tableId)).findElement(By.cssSelector(searchCss));
		else
			return driver.findElement(By.id(tableId));
	}

	/**
	 * convert boolean value to yes/no question
	 * @return String
	 */
	public String boolYesNo (boolean value) {
		if (value)
			return "Yes";
		return "No";
	}

	public String boolJN (boolean value) {
		if (value)
			return "J";
		return "N";
	}

	//@author psilva
	public void selectComboBoxValue(String comboBoxId, String valueToSelect) {
		scrollWaitElementById(comboBoxId);
		logMsg("    (Base.selectComboBoxValue) ID:<%s>, value:<%s>", comboBoxId, valueToSelect);
		waitPage(wait);

		WebElement comboBox = driver.findElement(By.id(comboBoxId)).findElement(By.tagName("input"));
		Set<String> seenOptions = new HashSet<>();
		String currentValue;

		comboBox.click();
		waitPage(wait);

		String previousValue = "";
		String tempValue = "";

		do {
			previousValue = comboBox.getAttribute("value");
			comboBox.sendKeys(Keys.ARROW_UP);
			waitPage(wait);
			tempValue = comboBox.getAttribute("value");
		} while (!previousValue.equals(tempValue));

		while (true) {
			currentValue = comboBox.getAttribute("value");

			if (seenOptions.contains(currentValue)) {
				comboBox.sendKeys(Keys.ESCAPE);
				fail("Value '" + valueToSelect + "' not found in dropdown");
			}

			seenOptions.add(currentValue);

			if (currentValue.equals(valueToSelect)) {
				comboBox.sendKeys(Keys.ENTER);
				waitPage(wait);
				break;
			}

			comboBox.sendKeys(Keys.ARROW_DOWN);
			waitPage(wait);
		}

		waitPage(wait);
	}




	/**
	 * select value in combobox with caption name as identifier
	 * @param id identifier of combobox
	 * @param value to select
	 */
	public void selectComboBoxValueByIdName(String id, String value){
		scrollWaitElementById(id);
		logMsg("    (Base.selectComboBoxValueByIdName) ID:<%s>, value:<%s>", id, value);
		isComboBoxValueByIdNameFound(id, value, true);
		waitPage(wait);
		waitPage(wait);
	}

	public void selectComboBoxValueByIdNameAndAssert(String id, String value){
		logMsg("    (Base.selectComboBoxValueByIdNameAndAssert) ID:<%s>, value:<%s>", id, value);
		// check if value has been found
		assertTrue(isComboBoxValueByIdNameFound(id, value, true));
	}

	/**
	 * check if disabled, disabled tag will be present in this case
	 * find by id and input
	 * @param id html identifier
	 * @return true or false
	 */
	public boolean isElementDisabledByIdInput (String id) {
		// get the web element
		WebElement elem = driver.findElement(By.id(id)).findElement(By.tagName("input"));
		// check if attribute disabled present
		boolean isDisabled = elem.getAttribute("disabled") != null;
		// wait to avoid exception
		// at the moment not needed
//		waitPage(wait);
		// return true or false
		return isDisabled;
	}
	/**
	 * check if disabled, disabled tag will be present in this case
	 * find by id
	 * @param id html identifier
	 * @return true or false
	 */
	public boolean isElementDisabledById (String id) {
		// get the web element
		WebElement elem = driver.findElement(By.id(id));

		// check if attribute disabled present
		boolean isDisabled = elem.getAttribute("disabled") != null;

		// wait to avoid exception
		// at the moment not needed
//		waitPage(wait);
		// return true or false
		return isDisabled;
	}

	/**
	 * the same method as in the getWebElementByIdInput but without safety precaution
	 * this method is faster but can also cause errors
	 * @param id html identifier
	 * @return web element
	 */
	public WebElement getWebElementByIdInput2(String id) {
		return driver.findElement(By.id(id)).findElement(By.tagName("input"));
	}

	public void selectComboBoxValueByIdNameEscape(String id, String value, boolean escape){
		isComboBoxValueByIdNameFound(id, value, escape);
		waitPage(wait);
	}

	public boolean isComboBoxValueByIdNameFound(String id, String value, boolean isEscape) {
		// trim value
		value = value.trim();
		// id element is div and then combobox is in the input html element
		WebElement drp = getWebElementByIdInput2(id);
		// current value in dropbox
		String valueDrp = drp.getAttribute("value").trim();
		// check if value is found
		if (valueDrp.equals(value)) {
			// sometimes a bug happens which prevents closing the dropdown box
			if (isEscape)
				drp.sendKeys(Keys.ESCAPE);
			// return true
			return true;
		}
		// if value is null or empty it means nothing is preselected,
		// we need to make selection first
		drp.sendKeys(Keys.ARROW_DOWN);
		drp.sendKeys(Keys.ENTER);
		// get refreshed value
		WebElement drp2 = getWebElementByIdInput2(id);
		String valueDrp2 = drp2.getAttribute("value").trim();
		// check if value is found
		if (valueDrp2.equals(value)){
			// sometimes a bug happens which prevents closing the dropdown box
			if (isEscape)
				drp.sendKeys(Keys.ESCAPE);
			// return true
			return true;
		}
		// if still empty or the same value, it can be that there is a bug which causes the dropdown box
		// to immediately close again, we try one more time to open the dropdown box
		if (valueDrp2 == null || valueDrp2.isEmpty() || valueDrp.equals(valueDrp2)) {
			drp.sendKeys(Keys.ARROW_DOWN);
			drp.sendKeys(Keys.ENTER);
			// get refreshed value
			drp = getWebElementByIdInput2(id);
			valueDrp = drp.getAttribute("value").trim();
			// it is possible that the first value is always empty, so last attempt
			// is to use double arrow to go to the next value
			if (valueDrp == null || valueDrp.isEmpty()) {
				drp.sendKeys(Keys.ARROW_DOWN);
				waitPageCustomTime(300);
				drp.sendKeys(Keys.ARROW_DOWN);
				drp.sendKeys(Keys.ENTER);
				// sometimes a bug happens which prevents closing the dropdown box
				if (isEscape)
					drp.sendKeys(Keys.ESCAPE);
				// get refreshed value
				drp = getWebElementByIdInput2(id);
				valueDrp = drp.getAttribute("value").trim();
			}
			// if still value is empty, then exit, no values present
			// in this check, only check if empty, first time same value meant that dropdown is closed
			if (valueDrp == null || valueDrp.isEmpty()) {
				// sometimes a bug happens which prevents closing the dropdown box
				if (isEscape)
					drp.sendKeys(Keys.ESCAPE);
				// return false
				return false;
			}
		}
		// check if value is already found
		if (valueDrp.equals(value)) {
			// sometimes a bug happens which prevents closing the dropdown box
			if (isEscape)
				drp.sendKeys(Keys.ESCAPE);
			// return true
			return true;
		}
		// list of all values in dropdown
		List<String> list = new ArrayList<>();
		// save first entry
		// initial retrieved valueDrp can be empty, check which variable contains value
		list.add(!valueDrp.isEmpty() ? valueDrp : valueDrp2);
		// is value found
		boolean isFound = false;
		// make dropdown box to drop down
		drp.sendKeys(Keys.ARROW_DOWN);
		waitPageCustomTime(300);
		// sometimes a value not at the first index can be selected, we need to go back to the first index,
		// otherwise not all values are being read
		if (!isFound) {
			int index = 0;
			while (true) {
				drp.sendKeys(Keys.ARROW_UP);
				drp = getWebElementByIdInput2(id);
				valueDrp = drp.getAttribute("value").trim();
				if (valueDrp == null || valueDrp.isEmpty() || list.contains(valueDrp)) {
					// revert to original index
					for (int k = 0; k < index; k++)
						drp.sendKeys(Keys.ARROW_DOWN);
					// exit while loop
					break;
				}
				list.add(valueDrp);
				// check if value has been found
				if (valueDrp.equals(value)) {
					isFound = true;
					break;
				}
				index++;
			}
		}
		// go through dropdown list until the same value is found which means no new values,
		// end of dropbox has been reached
		while (!isFound) {
			drp.sendKeys(Keys.ARROW_DOWN);
			drp = getWebElementByIdInput2(id);
			valueDrp = drp.getAttribute("value").trim();
			// check if value in dropdown exists or if the value is already in the list
			// which means end of dropdown list, there are no new values
			if (valueDrp == null || valueDrp.isEmpty() || list.contains(valueDrp))
				break;
			// check if value has been found
			if (valueDrp.equals(value)) {
				isFound = true;
				break;
			}
			// add to the list of all values
			list.add(valueDrp);
		}
		// close dropdown box
		drp = getWebElementByIdInput2(id);
		drp.sendKeys(Keys.ENTER);
		// to handle possible bug
		if (isEscape)
			drp.sendKeys(Keys.ESCAPE);
		// return if value found
		return isFound;
	}

	/**
	 * method used when error is bound to component as a binder
	 * the error message disappears quickly and is in iframe html web element
	 * @param errorText to find
	 */
	public void checkErrorBinderDisappear (String errorText) {
		waitPage(wait);
		//		WebElement elem = driver.findElement(By.cssSelector(".v-Notification.warning.v-Notification-warning.v-Notification-animate-out"));
		WebElement elem = driver.findElement(By.className("v-Notification-description"));
		String text = elem.getText();
		assertTrue(text.contains(errorText));
	}

	public void checkTinyErrorInputByCss (String errorText) {
		waitElementByCssSelector(cssTinyErrorMessage);
		waitPage(wait);
		String errorHtml = driver.findElements(By.cssSelector(".gwt-HTML")).get(1).getText();
		assertTrue(errorHtml.contains(errorText));
	}

	/**
	 * check if tab is disabled
	 * @param id html identifier
	 * @return true or false
	 */
	public boolean isTabDisabledById (String id) {
		return !driver.findElement(By.id(id)).findElements(By.cssSelector(".v-caption.v-disabled")).isEmpty();
	}

	public boolean isDisabledById (String id) {
		String className = driver.findElement(By.id(id)).getAttribute("class");
		return className.contains("v-disabled") || className.contains("v-readonly") || className.contains("readonly") || className.contains("disabled");
	}

	public boolean isDisabledByIdInput (String id) {
		String className = driver.findElement(By.id(id)).findElement(By.tagName("input")).getAttribute("class");
		return className.contains("v-disabled") || className.contains("v-readonly") || className.contains("readonly") || className.contains("disabled");
	}


	/**
	 * clear and enter value into input field
	 * @param id identifier of input field
	 * @param value to insert into input field
	 */
	public void newValueTextFieldByIdInput (String id, String value) {
		logMsg("    (Base.newValueTextFieldByIdInput) ID:<%s>, value:<%s>", id, value);
		// wait until the web element is fully init
		scrollWaitElementById(id);
		driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
		driver.findElement(By.id(id)).findElement(By.tagName("input")).clear();
		driver.findElement(By.id(id)).findElement(By.tagName("input")).sendKeys(value);
		waitPage(wait);
	}
	public void clearFieldIdInput (String id) {
		scrollWaitElementById(id);
		driver.findElement(By.id(id)).findElement(By.tagName("input")).click();
		driver.findElement(By.id(id)).findElement(By.tagName("input")).clear();
		waitPage(wait);
	}

	public void clearFieldId (String id) {
		scrollWaitElementById(id);
		driver.findElement(By.id(id)).click();
		driver.findElement(By.id(id)).clear();
		waitPage(wait);
	}

	//@author psilva
	public void selectCheckboxRow(List<String> rowValuesToFind, String tableId, int startColumn) {
		waitPage(wait);
		wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(idToXpath(tableId))));
		WebElement table = driver.findElement(By.id(tableId));
		List<WebElement> tableRows = wait.withTimeout(Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(table, By.tagName("tr")));

		Actions actions = new Actions(driver);
		Set<String> checkedRows = new HashSet<>();
		boolean rowFound = false;
		boolean firstRow = true;
		int rowCount = 0;

		while (!rowFound) {
			if (firstRow) {
				tableRows.get(1).findElements(By.tagName("td")).get(startColumn).click();
				firstRow = false;
			}

			WebElement currentCell = table.findElement(By.cssSelector("td.v-grid-cell.v-grid-cell-focused"));
			String rowIdentifier = currentCell.getText().trim();

			if (checkedRows.contains(rowIdentifier)) {
				break;
			}
			checkedRows.add(rowIdentifier);

			if (rowValuesToFind.get(0).equals(rowIdentifier)) {
				boolean match = true;

				for (int i = 1; i < rowValuesToFind.size(); i++) {
					actions.sendKeys(Keys.ARROW_RIGHT).perform();
					currentCell = table.findElement(By.cssSelector("td.v-grid-cell.v-grid-cell-focused"));
					String cellText = currentCell.getText().trim();
					if (!cellText.equals(rowValuesToFind.get(i))) {
						match = false;
						break;
					}
				}

				if (match) {
					for (int i = 1; i < rowValuesToFind.size(); i++) {
						actions.sendKeys(Keys.ARROW_LEFT).perform();
					}

					WebElement focusedRow = currentCell.findElement(By.xpath("./ancestor::tr"));
					WebElement checkboxCell = focusedRow.findElements(By.tagName("td")).get(0);

					WebElement checkbox = checkboxCell.findElement(By.cssSelector("input[type='checkbox']"));
					if (!checkbox.isSelected()) {
						checkbox.click();
					}

					rowFound = true;

					for (int i = 0; i < rowCount; i++) {
						actions.sendKeys(Keys.ARROW_UP).perform();
					}

					break;
				} else {
					for (int i = 1; i < rowValuesToFind.size(); i++) {
						actions.sendKeys(Keys.ARROW_LEFT).perform();
					}
				}
			}

			if (!rowFound) {
				actions.sendKeys(Keys.ARROW_DOWN).perform();
				rowCount++;
			}
		}

		if (!rowFound) {
			fail(rowValuesToFind + " not found");
		}
	}


	/**
	 * method checks and selects records in the table
	 * @param isMultiselect should more than one record be selected / checked
	 * @param isSelect should the record be selected
	 * @param valSelect list of values to select contained in a list of a map column values
	 * @param tableId html identifier
	 * @return true or false
	 */
	public boolean isSelectRecordsTable(boolean isMultiselect, boolean isSelect, List<Map<Integer, String>> valSelect, String tableId){
		if(valSelect.size() == 0)
			fail("Map is empty, no values to select");

		if(!driver.findElements(By.id(tableId)).isEmpty() && driver.findElement(By.id(tableId)).isDisplayed()){
			;
		} else{
			scrollWaitElementById(tableId);
			waitPageTimes(2);
			scrollClickId(tableId);
		}
		waitPage(wait);
		scrollWaitElementById(tableId);

		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(tableId)));
		// get table div with html id identifier
		WebElement tableElement = getSearchTable(tableId);//.findElement(By.className("v-grid-tablewrapper"));
		// click
		tableElement.click();
		tableElement = getSearchTable(tableId);
		tableElement.sendKeys(Keys.ARROW_DOWN);
		// refresh data
		scrollWaitElementById(tableId);
		tableElement = getSearchTable(tableId);
		int lastColumnIndex = 0;
		for (Map.Entry<Integer, String> entry : valSelect.get(0).entrySet()) {
			if (entry.getKey() > lastColumnIndex)
				lastColumnIndex = entry.getKey();
		}
		// get row list
		WebElement vgridbody = tableElement.findElement(By.className("v-grid-body"));
		List<WebElement> trList = vgridbody.findElements(By.tagName("tr"));
		// get rows to find count
		int recordsToFind = valSelect.size();
		for (int i = 0; i < trList.size(); i++) {
			WebElement row = trList.get(i);
			// go to the first column to start from the beggining
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				tableElement.sendKeys(Keys.ARROW_LEFT);
			}
			// now get each colum data with key arrow together
			List<String> tableRowValues = new ArrayList<>();
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				// refresh data
				tableElement = getSearchTable(tableId);
				vgridbody = tableElement.findElement(By.className("v-grid-body"));
				trList = vgridbody.findElements(By.tagName("tr"));
				row = trList.get(i);
				tableRowValues.add(row.findElements(By.className("v-grid-cell")).get(j).getText().trim());
				tableElement.sendKeys(Keys.ARROW_RIGHT);
			}
			// get row values
			// actual columns that exist must not be less than the number of column values to compare
			if (isMultiselect && tableRowValues.size() - 1 < valSelect.get(0).size())
				return false;
			else if (tableRowValues.size() < valSelect.get(0).size())
				return false;
			// compare last displayed row with initial values to be inserted
			if (isCreatedRecordFound2(row, tableRowValues, valSelect)) {
				// if single column selection enabled
				if (!isMultiselect) {
					// if records needs to be only found
					if (!isSelect)
						return true;
					Actions action = new Actions(driver);
					action.click(row).perform();;
					return true;
				}


				// if records needs to be selected
				if (isSelect) {
					// first td column is checkbox to select the value
					row.findElements(By.tagName("td")).get(0).click();
				}
				recordsToFind--;

				// check if all records have been founf
				if (recordsToFind == 0)
					return true;
			}
		}
		// if nothing found, we need to go through the whole search grid, checking each row for potential match
		// the problem is in the search table, last added entry can be anywhere in the table,
		// not necesseraly at the end of last row
		// we now go past the last focused row with arrow keys, checking each row if it is a match
		boolean isMoreScrolling = true;
		String previousRowNumber = null;
		while (isMoreScrolling) {
			// use down keys to go down again
			tableElement.sendKeys(Keys.ARROW_DOWN);
			// the data should have now changed, retrieve the data again
			tableElement = getSearchTable(tableId);
			// check if first displayed rows contain the new inserted record
			WebElement focusedRow = null;
			// there are two classes used to identify focused row, only one will be present
			String cssFocus1 = ".v-grid-row.v-grid-row-has-data.v-grid-row-stripe.v-grid-row-focused";
			String cssFocus2 = ".v-grid-row.v-grid-row-has-data.v-grid-row-focused";
			if (!tableElement.findElements(By.cssSelector(cssFocus1)).isEmpty())
				focusedRow = tableElement.findElement(By.cssSelector(cssFocus1));
			else if (!tableElement.findElements(By.cssSelector(cssFocus2)).isEmpty())
				focusedRow = tableElement.findElement(By.cssSelector(cssFocus2));
			if (focusedRow == null)
				return false;
			// go to the first column to start from the beggining
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				tableElement.sendKeys(Keys.ARROW_LEFT);
			}

			// now get each colum data with key arrow togethe
			List<String> tableRowValues = new ArrayList<>();
			for (int j = 0; j < lastColumnIndex + 1; j++) {
				// refresh data
				tableElement = getSearchTable(tableId);
				if (!tableElement.findElements(By.cssSelector(cssFocus1)).isEmpty())
					focusedRow = tableElement.findElement(By.cssSelector(cssFocus1));
				else if (!tableElement.findElements(By.cssSelector(cssFocus2)).isEmpty())
					focusedRow = tableElement.findElement(By.cssSelector(cssFocus2));

				if (focusedRow == null)
					return false;

				tableRowValues.add(focusedRow.findElements(By.className("v-grid-cell")).get(j).getText().trim());
				tableElement.sendKeys(Keys.ARROW_RIGHT);
			}
			// if previous row is again taken, then end of table has been reached, inserted record has not been found
			if (previousRowNumber != null && previousRowNumber.equals(tableRowValues.get(0)))
				break;
			previousRowNumber = tableRowValues.get(0);
			// actual columns that exist must not be less than the number of column values to compare
			if (isMultiselect && tableRowValues.size() - 1 < valSelect.get(0).size())
				return false;
			else if (tableRowValues.size() < valSelect.get(0).size())
				return false;
			// compare last displayed row with initial values to be inserted
			if(isCreatedRecordFound2(focusedRow, tableRowValues, valSelect)) {
				// if single column selection enabled
				if (!isMultiselect) {
					// if records needs to be only found
					if (!isSelect)
						return true;
					Actions action = new Actions(driver);
					action.click(focusedRow).perform();;
					return true;
				}
				// if records needs to be selected
				if (isSelect) {
					// first td column is checkbox to select the value
					focusedRow.findElements(By.tagName("td")).get(0).click();
				}
				recordsToFind--;
				// check if all records have been founf
				if (recordsToFind == 0)
					return true;
			}
		}
		// if yet not returned true, then it is false, whole grid was searched
		return false;
	}



	/**
	 * check if row contains the new inserted record values
	 * @param valuesFromTable are values inside each column
	 * @param valuesToFind are the values that need to be found in columns
	 * @return true or false
	 */
	private boolean isCreatedRecordFound2(WebElement rowElement, List<String> valuesFromTable, List<Map<Integer, String>> valuesToFind) {
		for (int i = 0; i < valuesToFind.size(); i++) {
			Map<Integer, String> mapToFind = valuesToFind.get(i);
			int count = 0;
			for (Map.Entry<Integer, String> item : mapToFind.entrySet()) {
				int index = item.getKey();
				String valueToFind = item.getValue();
				String valueInTable = valuesFromTable.get(index);
				// if value does not match than break, it must be always a match in order to be correct
				if (!valueInTable.equals(valueToFind)) {
					count = 0;
					break;
				}
				count++;
			}
			// has the number of matched items been exact number in the map
			if (mapToFind.size() == count)
				return true;
		}
		return false;
	}

	public boolean selectCreatedRecordSearch2(Map<Integer, String> rowValuesToFind, String tableId, boolean doubleClick) {
		WebElement tableElement = getSearchTable(tableId);

		List<WebElement> rows = tableElement.findElement(By.className("v-grid-body")).findElements(By.tagName("tr"));

		for (WebElement row : rows) {
			List<WebElement> cells = row.findElements(By.className("v-grid-cell"));
			boolean match = rowValuesToFind.entrySet().stream()
					.allMatch(entry -> entry.getKey() < cells.size() &&
							entry.getValue().equals(cells.get(entry.getKey()).getText().trim()));
			waitPage(wait);

			if (match) {
				Actions action = new Actions(driver);
				if (doubleClick) {
					waitPage(wait);
					action.doubleClick(row).perform();
				} else {
					waitPage(wait);
					action.click(row).perform();
				}
				waitPage(wait);
				return true;
			}
		}

		return false;
	}

	/**
	 * wait with custom defined time
	 * @param time to wait
	 */
	public static void waitPageCustomTime(int time) {
		try {
			wait.until(
					webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
			Thread.sleep(time);
		} catch (Throwable error) {
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}
	}

	/**
	 * get table rows with vertical rows scrolling needed
	 * @param idTable html identifier
	 * @return list of web elements
	 */
	public List<WebElement> getTableRowsVertScroll (String idTable) {
		// get all currently visible rows
		List<WebElement> list = driver.findElement(By.id(idTable)).findElement(By.tagName("table")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		// check if rows exist
		if (list.isEmpty())
			return list;
		// click table
		// note: this is a workaround because every time specific record row is to be selected
		// there is an exception element not interactable
		driver.findElement(By.id(idTable)).click();
		// wait for the table to update
		waitPage(wait);
//		list.get(list.size() - 1).click();
		// wait for the table to update
//		waitPage(wait);
		// focused web element identifier by class name / css selector
		String cssIdent1 = ".v-grid-row.v-grid-row-has-data.v-grid-row-focused";
		String cssIdent2 = ".v-grid-row.v-grid-row-stripe.v-grid-row-has-data.v-grid-row-focused";

		// save all rows until the focused one
		// with row arrow go down beyond the last clicked record;
		WebElement table = driver.findElement(By.id(idTable));
		table.sendKeys(Keys.ARROW_DOWN);
		// wait for the table to update
		waitPage(wait);
		// if web element is not found, means end of table, exit
		if (driver.findElements(By.cssSelector(cssIdent1)).isEmpty() &&
				driver.findElements(By.cssSelector(cssIdent2)).isEmpty())
			return list;
		// get refreshed table data
		table = driver.findElement(By.id(idTable));
		list = getTableRows(idTable);
		String classIdent1 = "v-grid-row v-grid-row-has-data v-grid-row-focused";
		String classIdent2 = "v-grid-row v-grid-row-stripe v-grid-row-has-data v-grid-row-focused";
		for (int i = 0; i < list.size(); i++) {
			WebElement elem = list.get(i);
			// if focused web element is found, remove all rows from that index and above
			String att2 = elem.getAttribute("outerHTML");
			if (att2.contains(classIdent1) ||
					att2.contains(classIdent2)) {
				List<WebElement> tempList = new ArrayList<>(list.subList(0, i));
				list.clear();
				list.addAll(tempList);
				tempList = null;
				break;
			}
		}
		// go to while loope to retrieve all the records untill the last one
		String priorRow = "-1";
		String currentRow = "-2";
		int count = 0;
		while (!priorRow.equals(currentRow)) {
			// get focused web element
			table = driver.findElement(By.id(idTable));
			WebElement focusedRow = !table.findElements(By.cssSelector(cssIdent1)).isEmpty() ?
					table.findElement(By.cssSelector(cssIdent1)) :
					table.findElement(By.cssSelector(cssIdent2));

			// get row number
			// if count is 0 means first entry, don't save current row number, only as before number
			if (count != 0)
				priorRow = currentRow;
			currentRow = focusedRow.findElements(By.tagName("td")).get(0).getText();
			count++;
			if (priorRow.equals(currentRow))
				break;
			// with row arrow go down beyond the last clicked record;
			table = driver.findElement(By.id(idTable));
			table.sendKeys(Keys.ARROW_DOWN);

			// wait for the table to update
			waitPage(wait);
			// save record
			list.add(focusedRow);
		}
		return list;
	}

	/**
	 * after testing the class is done, delete created record
	 * @param dialogId prefix of the dialog
	 * @param map contains for search new record
	 * @param idSearchWindow html identifier of search table
	 * @param id html identifier for value id component
	 * @param value of the new record
	 * @param isCustomSearchWindow no search button is to be clicked, only perform search for the grid
	 */
	public void afterClassDelete (String dialogId, Map<Integer, String> map, String idSearchWindow, String id,
								  String value, boolean isCustomSearchWindow) {
		// log message
		logMsg("(" + dialogId + ") Deleting new item");
		// check if regular search
		if (!isCustomSearchWindow) {
			// there can be errors displayed, close them
			closeErrorOrNewOrGoHomePresenceByIdCss();

			// a window can still be open in the Dialog, close it
			clickESCOnHtmlById();
		}

		// check if item is still not deleted and regular search window is to be opened
		if(created && !deleted && !isCustomSearchWindow && selectCreatedRecordSearch(map, idSearchWindow))
			deleteItemByIdConfirmValId(id, value);
		// if custom search needs to be opened
		if(created && !deleted && isCustomSearchWindow && selectCreatedRecordSearchAux(map, idSearchWindow))
			deleteItemByIdConfirmValId(id, value);
			// else click escape just in case search window is not closed
			// this happens if no value match in search has been found
		else
			clickESCOnHtmlById();
	}

	/**
	 * method that makes log messages before calling the actual method goToDialogAndAssertTitle
	 * @param dialog - prefix of the dialog
	 * @param title - name of the dialog
	 */
	public void goToDialogAndAssertTitleWithLogMsg(String dialog, String title){
		logMsg("----------------------------------");
		logMsg("(" + dialog + ") Test starts");
		goToDialogAndAssertTitle(dialog, title);
	}



	/**
	 * make log message before calling actual method saveItemBy()
	 * @param dialogId is the prefix of the dialog
	 */
	public void saveItemByIdLogMsg(String dialogId) {
		logMsg("(" + dialogId + ") Saving new item");
		saveItemById();
	}


	/**
	 * making log message before calling the actual method selectCreatedRecordSearch()
	 * @param rowValuesToFind contains values of the new inserted row
	 * @param tableId is the name of the main search grid, id varies dependent on the Dialog
	 * @return true or false
	 */
	public boolean selectCreatedRecordSearchLogMsg(String dialogId, Map<Integer, String> rowValuesToFind, String tableId){
		logMsg("(" + dialogId + ") Loading new item");
		return selectCreatedRecordSearch(rowValuesToFind, tableId);
	}


	/**
	 * call log message before actual deleteItemByIdConfirmValId method
	 * @param dialogId dialog prefix
	 * @param id html identifier
	 * @param value which must not be present (is the value from to be deleted record)
	 */
	public void deleteItemByIdConfirmValIdLogMsg(String dialogId, String id, String value){
		logMsg("(" + dialogId + ") Deleting new item");
		deleteItemByIdConfirmValId(id, value);
		logMsg("(" + dialogId + ") Leaving");
	}


	/**
	 * log message that test case 1 is starting
	 * @param dialogId
	 */
	public void test1LogMsg (String dialogId) {
		logMsg("(" + dialogId + ") Testcase #1 starts");
	}



	/**
	 * call log message that test case 1 is about to start before calling the actual
	 * method createNewItemById()
	 * @param dialogId
	 */
	public void createNewItemTest1LogMsg(String dialogId) {
		test1LogMsg(dialogId);
		createNewItemById();
	}

	/**
	 * select value in combobox with caption name as identifier, without using escape
	 * @param id identifier of combobox
	 * @param value to select
	 */
	public void selectComboBoxValueNoEscapeByIdName(String id, String value){
		scrollWaitElementById(id);
		logMsg("    (Base.selectComboBoxValueByIdName) ID:<%s>, value:<%s>", id, value);
		isComboBoxValueByIdNameFound(id, value, false);
		waitPage(wait);
		waitPage(wait);
	}


	public static String convertObjToString(Object clsObj) {
		String jsonSender = new Gson().toJson(clsObj, new TypeToken<>() {
		}.getType());
		return jsonSender;
	}

	public void confirmTdColors(WebElement tr, HashMap<Integer, String> indexesColorsTd){
		logMsg("(Base.confirmTdColors) Confirming tdColors on tr: " + tr.getText());
		for(Map.Entry<Integer, String> ic : indexesColorsTd.entrySet()){
			String trClass = tr.findElements(By.tagName("td")).get(ic.getKey()).getAttribute("class");
			logMsg("Comparing color " + ic.getValue() + " on td with index " + ic.getKey());
			if(!trClass.contains(ic.getValue()))
				fail("Couldn't confirm color " + ic.getValue() + " on column with index " + ic.getKey());
		}
	}



	public void twinGridSelectionAndAssertion(TwinGrid tg, List<String> sortedByFields){
		logMsg("(Base.twinGridSelectionAndAssertion) Selecting values on twin grid: " + sortedByFields);
		scrollWaitElementById(tg.getIdGridLeft());
		//"Reset": If we have preselected sorted by fields in left grid, we need to allocate all to the right one
		int leftGridSize = getTableRows(tg.getIdGridLeft()).size();
		if(leftGridSize > 0){
			for(int i = 0; i < leftGridSize; i++)
				scrollClickId(tg.getIdArrowRight());
		}


		//Confirm/asser our left grid has 0 entries
		List<WebElement> tableRowsLeft = getTableRows(tg.getIdGridLeft());
		List<WebElement> tableRowsRight = getTableRows(tg.getIdGridRight());
		assertEquals(0, tableRowsLeft.size());


		//Iterate the list and click left arrow on all Strings from the list
		boolean found;
		for(String s : sortedByFields) {
			found = false;

			for (WebElement tr : tableRowsRight) {
				currentTr = tr;
				String tdText = getTdText(tr, 0);
				if(
						tdText.equals(s)
				){
					found = true;
					assertTrue(true);
					if(!isTrSelectedGrid(tr))
						tr.click();
					scrollClickId(tg.getIdArrowLeft());
					break;
				}
			}

			if (!found)
				fail("Right grid - Entry: <" + s + "> not found to select for Sorted by grid");
			waitPage(wait);
		}


		tableRowsLeft = getTableRows(tg.getIdGridLeft()); //Confirm/assert that every String s on sortedByFields list is present on left grid
		for(String s : sortedByFields) {
			found = false;

			for (WebElement tr : tableRowsLeft) {
				currentTr = tr;
				String tdText = getTdText(tr, 0);
				if(
						tdText.equals(s)
				){
					found = true;
					assertTrue(true);
					break;
				}
			}

			if (!found)
				fail("Left grid - Entry: <" + s + "> not found on Sorted by grid");
			waitPage(wait);
		}
	}

	public void fillCheckboxAndTextfieldById(boolean b, String idCheck, String text, String idTextfield) {
		logMsg("    (Base.fillCheckboxAndTextfieldById) id:<%s>, b:<%s>", idCheck, b ? "Yes" : "No");
		WebElement cInput = driver.findElement(By.id(idCheck)).findElement(By.tagName("input"));
		WebElement cLabel = driver.findElement(By.id(idCheck)).findElement(By.tagName("label"));
		if(b) {
			if (!cInput.isSelected())
				cLabel.click();
			fillTextFieldAndAssert(text, idTextfield);
		} else{
			if (cInput.isSelected())
				cLabel.click();
		}
		waitPage(wait);
	}

	public void fillCheckboxAndSelectdById(boolean b, String idCheck, List<String> textList, String idSelect) {
		logMsg("    (Base.fillCheckboxAndSelectdById) id:<%s>, b:<%s>", idCheck, b ? "Yes" : "No");
		WebElement cInput = driver.findElement(By.id(idCheck)).findElement(By.tagName("input"));
		WebElement cLabel = driver.findElement(By.id(idCheck)).findElement(By.tagName("label"));
		if(b) {
			if (!cInput.isSelected())
				cLabel.click();
			fillSelectById(idSelect, textList);
		} else{
			if (cInput.isSelected())
				cLabel.click();
		}
		waitPage(wait);
	}

	public void fillCheckboxAndDateById(boolean b, String idCheck, String text, String idTextfield) {
		logMsg("    (Base.fillCheckboxAndDateById) id:<%s>, b:<%s>", idCheck, b ? "Yes" : "No");
		WebElement cInput = driver.findElement(By.id(idCheck)).findElement(By.tagName("input"));
		WebElement cLabel = driver.findElement(By.id(idCheck)).findElement(By.tagName("label"));
		if(b) {
			if (!cInput.isSelected())
				cLabel.click();
			fillDatesOrInputFieldsById(text, idTextfield);
		} else{
			if (cInput.isSelected())
				cLabel.click();
		}
		waitPage(wait);
	}


	public class TwinGrid{
		String idArrowUp;
		String idArrowDown;
		String idArrowLeft;
		String idArrowRight;
		String idGridLeft;
		String idGridRight;

		public TwinGrid(String idArrowUp, String idArrowDown, String idArrowLeft, String idArrowRight, String idGridLeft, String idGridRight) {
			this.idArrowUp = idArrowUp;
			this.idArrowDown = idArrowDown;
			this.idArrowLeft = idArrowLeft;
			this.idArrowRight = idArrowRight;
			this.idGridLeft = idGridLeft;
			this.idGridRight = idGridRight;
		}

		public String getIdArrowUp() {
			return idArrowUp;
		}

		public void setIdArrowUp(String idArrowUp) {
			this.idArrowUp = idArrowUp;
		}

		public String getIdArrowDown() {
			return idArrowDown;
		}

		public void setIdArrowDown(String idArrowDown) {
			this.idArrowDown = idArrowDown;
		}

		public String getIdArrowLeft() {
			return idArrowLeft;
		}

		public void setIdArrowLeft(String idArrowLeft) {
			this.idArrowLeft = idArrowLeft;
		}

		public String getIdArrowRight() {
			return idArrowRight;
		}

		public void setIdArrowRight(String idArrowRight) {
			this.idArrowRight = idArrowRight;
		}

		public String getIdGridLeft() {
			return idGridLeft;
		}

		public void setIdGridLeft(String idGridLeft) {
			this.idGridLeft = idGridLeft;
		}

		public String getIdGridRight() {
			return idGridRight;
		}

		public void setIdGridRight(String idGridRight) {
			this.idGridRight = idGridRight;
		}
	}

	public class DialogVars{
		String name;
		int row;
		String value;

		public DialogVars(String name, int row, String value) {
			this.name = name;
			this.row = row;
			this.value = value;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public class CheckboxInput {
		String name;
		Boolean checked;
		String xpathBox;
		String xpathText;
		String textInput;

		public CheckboxInput(String name, Boolean checked, String xpathBox, String xpathText, String textInput) {
			this.name = name;
			this.checked = checked;
			this.xpathBox = xpathBox;
			this.xpathText = xpathText;
			this.textInput = textInput;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getChecked() {
			return checked;
		}
		public String getXpathBox() {
			return xpathBox;
		}
		public String getXpathText() {
			return xpathText;
		}
		public String getTextInput() {
			return textInput;
		}

		public void setChecked(Boolean checked) {
			this.checked = checked;
		}

		public void setXpathBox(String xpathBox) {
			this.xpathBox = xpathBox;
		}

		public void setXpathText(String xpathText) {
			this.xpathText = xpathText;
		}

		public void setTextInput(String textInput) {
			this.textInput = textInput;
		}
	}

}
