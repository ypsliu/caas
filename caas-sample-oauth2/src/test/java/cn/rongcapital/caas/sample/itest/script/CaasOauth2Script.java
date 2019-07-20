package cn.rongcapital.caas.sample.itest.script;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CaasOauth2Script extends BaseScript {

	private String pageUrl = "http://127.0.0.1:8080/home.html";

	public CaasOauth2Script(WebDriver webDriver) {
		super(webDriver);
	}

	public void run() throws Exception{

		this.driver.get(this.pageUrl);
		TimeUnit.SECONDS.sleep(2);
		
		WebElement check = driver.findElement(By.linkText("access the protected resource"));
		check.click();

		if(this.driver.getTitle().equals("登录")){
			TimeUnit.SECONDS.sleep(2);
			
			WebElement userName = driver.findElement(By.name("login_name"));
			WebElement passWord = driver.findElement(By.name("password"));
			WebElement submit = driver.findElement(By.id("login"));
			
			userName.sendKeys("sunxin");
			passWord.sendKeys("123456");
			submit.click();
		}

		TimeUnit.SECONDS.sleep(2);
	}

}
