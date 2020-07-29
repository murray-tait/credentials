package uk.co.urbanfortress.aws;


import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.co.urbanfortress.aws.impl.SeleniumCredentialsRetriever;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"job.autorun.enabled=false"})
class SeleniumCredentialsRetrieverTest implements ElementLocators {

	private static final String PORTAL_URL = "portalUrl";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	@Mock
	private DriverFactory driverFactory;

	@Mock
	private WebDriver driver;

	@Mock
	private JavascriptExecutor javascriptExecutor;

	@Mock
	private ClipboardFactory clipboardFactory;

	@Mock
	private Clipboard clipboard;

	@Mock
	private Options options;

	@Mock
	private Window window;

	@Mock
	private WebElement wdcUsername;

	@Mock
	private WebElement wdcPassword;

	@Mock
	private WebElement wdcLoginButton;

	@Mock
	private WebElement appElement;

	@Mock
	private WebElement instanceBlock;

	@Mock
	private WebElement profileName;

	@Mock
	private WebElement credsLink;

	@Mock
	private WebElement hoverCopyEnv;

	@Mock
	private WebElement closer;

	@InjectMocks
	private SeleniumCredentialsRetriever credentialsRetriever;

	private List<WebElement> instanceBlocks;

	private List<WebElement> credsLinks;
	
	@BeforeEach 
	public void setUp() {
		instanceBlocks = new LinkedList<>();
		instanceBlocks.add(instanceBlock);

		credsLinks = new LinkedList<>();
		credsLinks.add(credsLink);
	}

	@Test
	public void testConstruction() {
		assertThat(credentialsRetriever, notNullValue());
	}

	@Test
	public void testRetrieveAllCredentials() throws Exception {
		String clipBoardContent = "a=1\nb=2";

		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("a", "1");
		expectedProperties.setProperty("b", "2");
		
		whenBrowserStarts();
		whenSignin();

		when(driver.findElement(APP_ELEMENT_LOCATOR)).thenReturn(appElement);
		when(driver.findElements(INSTANCE_BLOCKS_LOCATOR)).thenReturn(instanceBlocks);
		when(driver.findElement(PROFILE_NAME_LOCATION)).thenReturn(profileName);
		when(driver.findElements(CREDS_LINKS_LOCATOR)).thenReturn(credsLinks);
		when(driver.findElement(HOVER_COPY_LOCATOR)).thenReturn(hoverCopyEnv);
		when(driver.findElement(CLOSER_LOCATOR)).thenReturn(closer);
		when(clipboardFactory.clipboard()).thenReturn(clipboard);
		when(clipboard.getData(DataFlavor.stringFlavor)).thenReturn(clipBoardContent);

		List<Properties> allCredentials = credentialsRetriever.retrieveAllCredentials(USERNAME, PASSWORD, PORTAL_URL);

		assertThat(allCredentials, hasSize(1));
		assertThat(allCredentials, hasItems(expectedProperties));

		verifyBrowserStarts();
		verifySignin();

		verify(appElement).click();
		verify(driver).findElement(APP_ELEMENT_LOCATOR);
		verify(driver, times(2)).findElements(INSTANCE_BLOCKS_LOCATOR);
		verify(instanceBlock, times(2)).click();
		verify(driver).findElement(PROFILE_NAME_LOCATION);
		verify(driver, times(2)).findElements(CREDS_LINKS_LOCATOR);
		
		verify(credsLink).click();
		verify(clipboardFactory).clipboard();
		verify(clipboard).getData(DataFlavor.stringFlavor);
		
		verify(driver).findElement(HOVER_COPY_LOCATOR);
		verify(hoverCopyEnv).click();
		verify(driver).findElement(CLOSER_LOCATOR);
		verify(closer).click();
		verify(driver).close();
	}

	private void verifySignin() {
		verify(javascriptExecutor).executeScript("return document.readyState");
		verify(driver).findElement(USERNAME_LOCATOR);
		verify(wdcUsername).sendKeys(USERNAME);
		verify(driver).findElement(PASSWORD_LOCATOR);
		verify(wdcPassword).sendKeys(PASSWORD);
		verify(driver).findElement(LOGIN_BUTTON_LOCATOR);
		verify(wdcLoginButton).click();
	}

	private void whenSignin() {
		when(driver.findElement(USERNAME_LOCATOR)).thenReturn(wdcUsername);
		when(driver.findElement(PASSWORD_LOCATOR)).thenReturn(wdcPassword);
		when(driver.findElement(LOGIN_BUTTON_LOCATOR)).thenReturn(wdcLoginButton);
	}

	private void verifyBrowserStarts() {
		verify(driverFactory).getDriver();
		verify(driverFactory).getJavascriptExecutor();

		verify(driver).get(PORTAL_URL);
		verify(driver).manage();
		verify(options).window();
		verify(window).setSize(new Dimension(1401, 700));
	}

	private void whenBrowserStarts() {
		when(driverFactory.getDriver()).thenReturn(driver);
		when(driverFactory.getJavascriptExecutor()).thenReturn(javascriptExecutor);
		when(driver.manage()).thenReturn(options);
		when(options.window()).thenReturn(window);
		when(javascriptExecutor.executeScript("return document.readyState")).thenReturn("complete");
	}

	
	@AfterEach
	public void tearDown() {
		verifyNoMoreInteractions(driverFactory, driver, javascriptExecutor, clipboardFactory, clipboard, options,
				window, wdcUsername, wdcPassword, wdcLoginButton, appElement, instanceBlock, profileName, credsLink, 
				hoverCopyEnv, closer);
	}

}
