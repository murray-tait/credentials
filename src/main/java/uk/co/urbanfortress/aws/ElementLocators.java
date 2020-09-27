package uk.co.urbanfortress.aws;

import org.openqa.selenium.By;

public interface ElementLocators {
	static final By LOGIN_BUTTON_LOCATOR = By.id("wdc_login_button");
	static final By PASSWORD_LOCATOR = By.id("wdc_password");
	static final By USERNAME_LOCATOR = By.id("wdc_username");
	static final By CLOSER_LOCATOR = By.cssSelector(".close");
	static final By HOVER_COPY_LOCATOR = By.id("hover-copy-env");
	static final By APP_ELEMENT_LOCATOR = By.id("app-03e8643328913682");
	static final By INSTANCE_BLOCKS_LOCATOR = By.className("instance-block");
	static final By CREDS_LINKS_LOCATOR = By.className("creds-link");
	static final By PROFILE_NAME_LOCATION = By.id("profile-name");
	static final By ALERT_LOCATOR = By.id("alertFrame");
	static final By ERROR_MESSAGE_LOCATOR = By.className("gwt-Label");
}
