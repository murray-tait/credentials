package uk.co.urbanfortress.aws;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public interface DriverFactory {

	WebDriver getDriver();
	
	JavascriptExecutor getJavascriptExecutor();

}