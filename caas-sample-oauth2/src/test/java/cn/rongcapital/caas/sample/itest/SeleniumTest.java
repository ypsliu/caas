package cn.rongcapital.caas.sample.itest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cn.rongcapital.caas.sample.itest.script.CaasOauth2Script;

public class SeleniumTest {

	public static void main(String[] args) throws Exception {
		WebDriver driver = getWebDriver("firefox");
		
		new CaasOauth2Script(driver).run();

		driver.quit();
	}

	public static WebDriver getWebDriver(String driverName) {
		switch (driverName) {
		case "firefox":
			return new FirefoxDriver();// FireFox 45.5
		case "nogui":
			return new HtmlUnitDriver();
		default:
			return new FirefoxDriver();
		}
	}
}
