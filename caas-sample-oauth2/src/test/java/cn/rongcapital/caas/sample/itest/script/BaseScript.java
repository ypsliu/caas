package cn.rongcapital.caas.sample.itest.script;

import org.openqa.selenium.WebDriver;

public abstract class BaseScript {
	protected WebDriver driver;
	
	public BaseScript(WebDriver webDriver){
		this.driver = webDriver;
	}
	
	public abstract void run() throws Exception;
}
