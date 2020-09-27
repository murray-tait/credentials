package uk.co.urbanfortress.aws.impl;

import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.stereotype.Component;

import uk.co.urbanfortress.aws.ApplicationException;
import uk.co.urbanfortress.aws.ClipboardFactory;
import uk.co.urbanfortress.aws.CredentialsRetriever;
import uk.co.urbanfortress.aws.DriverFactory;
import uk.co.urbanfortress.aws.ElementLocators;

@Component
public class SeleniumCredentialsRetriever implements CredentialsRetriever, ElementLocators {

	private DriverFactory driverFactory;
	private ClipboardFactory clipboardFactory;

	public SeleniumCredentialsRetriever(DriverFactory driverFactory, ClipboardFactory clipboardFactory) {
		this.driverFactory = driverFactory;
		this.clipboardFactory = clipboardFactory;
	}

	public List<Properties> retrieveAllCredentials(String username, String password, String portalUrl) {
		List<Properties> credentailsCollection = new LinkedList<Properties>();

		WebDriver driver = driverFactory.getDriver();
		JavascriptExecutor js = driverFactory.getJavascriptExecutor();

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.of(50L, ChronoUnit.SECONDS))
				.pollingEvery(Duration.of(3, ChronoUnit.SECONDS)).ignoring(NoSuchElementException.class);

		driver.get(portalUrl);
		driver.manage().window().setSize(new Dimension(1401, 700));

		waitForPageReady(wait, js);

		wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_LOCATOR)).sendKeys(username);

		driver.findElement(PASSWORD_LOCATOR).sendKeys(password);
		driver.findElement(LOGIN_BUTTON_LOCATOR).click();

		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(APP_ELEMENT_LOCATOR),
				ExpectedConditions.presenceOfElementLocated(ALERT_LOCATOR)));

		try {
			if (driver.findElements(ALERT_LOCATOR).size() != 0) {
				String message = driver.findElement(ERROR_MESSAGE_LOCATOR).getText();
				throw new ApplicationException(message);
			} else {
				driver.findElement(APP_ELEMENT_LOCATOR).click();
				wait.until(isTrue(!driver.findElements(INSTANCE_BLOCKS_LOCATOR).isEmpty()));

				List<WebElement> instanceBlocks = driver.findElements(INSTANCE_BLOCKS_LOCATOR);

				for (WebElement instanceBlock : instanceBlocks) {
					instanceBlock.click();

					pause(Duration.of(1L, ChronoUnit.SECONDS));

					wait.until(ExpectedConditions.presenceOfElementLocated(PROFILE_NAME_LOCATION));
					wait.until(isTrue(!driver.findElements(CREDS_LINKS_LOCATOR).isEmpty()));

					List<WebElement> credsLinks = driver.findElements(CREDS_LINKS_LOCATOR);

					for (WebElement credsLink : credsLinks) {
						credsLink.click();

						wait.until(ExpectedConditions.presenceOfElementLocated(HOVER_COPY_LOCATOR)).click();

						String credentialsString = getClipboardContents();

						final Properties properties = convertToProperties(credentialsString);
						credentailsCollection.add(properties);

						driver.findElement(CLOSER_LOCATOR).click();
					}

					instanceBlock.click();
				}
			}
		} finally {
			driver.close();
		}

		return credentailsCollection;
	}

	private Properties convertToProperties(String credentialsString) {
		final Properties properties = new Properties();
		try {
			properties.load(new StringReader(credentialsString));
		} catch (IOException e) {
			throw new ApplicationException("Can not process credentials");
		}
		return properties;
	}

	private String getClipboardContents() {
		String credentialsString = null;
		try {
			credentialsString = (String) clipboardFactory.clipboard().getData(DataFlavor.stringFlavor);
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			throw new ApplicationException("Can not retrieve credentials from clipboard");
		}
		return credentialsString;
	}

	private Function<WebDriver, Boolean> isTrue(Boolean flag) {
		return x -> flag;
	}

	private void waitForPageReady(Wait<WebDriver> waiter, JavascriptExecutor js) {
		waiter.until(isTrue(js.executeScript("return document.readyState").toString().equals("complete")));
	}

	private void pause(Duration duration) {
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException e) {
			throw new ApplicationException(e);
		}
	}
}
