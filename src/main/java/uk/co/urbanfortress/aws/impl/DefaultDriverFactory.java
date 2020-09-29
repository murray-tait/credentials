package uk.co.urbanfortress.aws.impl;

import java.util.logging.Level;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.co.urbanfortress.aws.DriverFactory;

@Component
public class DefaultDriverFactory implements DriverFactory {

	private String browser;
	private String driverFile;
	private WebDriver driver;

	public DefaultDriverFactory(@Value("${seleniumBrowser:chrome}") String browser,
			@Value("${driverFile:default}") String driverFile) {
		this.browser = browser;
		this.driverFile = driverFile;
	}

	@Override
	public WebDriver getDriver() {
		if (driver == null) {
			
			switch (browser) {
			case "firefox":
				System.setProperty("webdriver.gecko.driver", driverFile);
				driver = new FirefoxDriver();
				break;
			case "edge":
				System.setProperty("webdriver.edge.driver", driverFile);
				driver = new EdgeDriver();
				break;
			case "ie":
				System.setProperty("webdriver.ie.driver", driverFile);
				driver = new InternetExplorerDriver();
				break;
			case "safari":
				driver = new SafariDriver();
				break;
			case "opera":
				driver = new OperaDriver();
				break;
			default:
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("--silent");
				java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
				
				System.setProperty("webdriver.chrome.driver", driverFile);
				System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true"); 
				driver = new ChromeDriver(chromeOptions);
				break;
			}
		}

		return driver;
	}
	
	public JavascriptExecutor getJavascriptExecutor() {
		return (JavascriptExecutor) getDriver();
	}
}
