package objectRepository;

import org.openqa.selenium.By;

public class AnugularPlayerLocators {

	public By movemouse = By.xpath("//*[@id='videogularId']/vg-controls/div/ng-custom-control");
	public By videoStartTime = By.xpath("//*[@id='videogularId']/vg-controls/div/vg-time-display[1]");
	public By videoDuration = By.xpath("//*[@id='videogularId']/vg-controls/div/vg-time-display[2]");
	public By playerId = By.xpath("//*[@id='Demo_Stage']");
	public By playerDemoButton = By.xpath("//body//img[1]");

} 
