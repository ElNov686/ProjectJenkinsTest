import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import runner.BaseTest;

import java.util.Date;
import java.util.List;

public class _PipelineTest extends BaseTest {
    private static final By NEW_ITEM = By.cssSelector("[title='New Item']");
    private static final By INPUT_LINE = By.id("name");
    private static final By PIPELINE = By.xpath("//span[text()='Pipeline']");
    private static final By OK_BUTTON = By.id("ok-button");
    private static final By ADVANCED_BUTTON = By.xpath("//span[@id='yui-gen4']");
    private static final By PIPELINE_ITEM_CONFIGURATION = By.xpath(
            "//div[@class='tab config-section-activator config_pipeline']");
    private static final String NAME_INPUT = "test123";
    private static final String PIPELINE_PROJECT_NAME = "Ruslan Gudenko Pipeline Project+";

    private JavascriptExecutor javascriptExecutor;
    private Date date;

    @BeforeMethod
    public void setUp() {
        javascriptExecutor = (JavascriptExecutor) getDriver();
        date = new Date();
    }

    private void fillNameAndClick() {
        getDriver().findElement(NEW_ITEM).click();
        getDriver().findElement(INPUT_LINE).sendKeys(NAME_INPUT);
        getDriver().findElement(PIPELINE).click();
    }

    private void fillNameAndClick(Long date) {
        getDriver().findElement(NEW_ITEM).click();
        getDriver().findElement(INPUT_LINE)
                .sendKeys(NAME_INPUT + date);
        getDriver().findElement(PIPELINE).click();
    }

    @Test
    public void testCheckValidationItemName() {
        fillNameAndClick();
        getDriver().findElement(OK_BUTTON).click();
        getDriver().findElement(By.cssSelector("[type='submit']")).click();
        getDriver().findElement(By.xpath("//li//a[@href='/']")).click();
        fillNameAndClick();
        String errorMessage = getDriver().findElement(
                By.id("itemname-invalid")).getText();

        getDriver().findElement(OK_BUTTON).click();
        String errorMessageTwo = getDriver().findElement(
                By.xpath("//h1")).getText();

        Assert.assertEquals(errorMessage,
                "» A job already exists with the name ‘test123’");
        Assert.assertEquals(errorMessageTwo, "Error");
    }

    @Test
    public void testCheckDropDownMenuPipeline() {
        fillNameAndClick(date.getTime());
        getDriver().findElement(OK_BUTTON).click();

        WebElement dropDownMenu = getDriver().findElement(By.className("samples"));
        javascriptExecutor.executeScript("arguments[0].scrollIntoView();",
                dropDownMenu);

        List<WebElement> optionsDropDown = getDriver().findElements(
                By.xpath("//div[1][@class='samples']//select/option"));

        Assert.assertEquals(optionsDropDown.size(), 4);
    }

    @Test
    public void testCheckLinkHelpMenuAdvancedProjectOptions() {
        fillNameAndClick(date.getTime());
        getDriver().findElement(OK_BUTTON).click();

        javascriptExecutor.executeScript("arguments[0].scrollIntoView();",
                getDriver().findElement(ADVANCED_BUTTON));

        getDriver().findElement(ADVANCED_BUTTON).click();
        getDriver().findElement(By.cssSelector("a[tooltip$='Display Name']"))
                .click();
        String urlAttribute = getDriver().findElement(By.cssSelector(
                        "[href='https://plugins.jenkins.io/workflow-job']"))
                .getAttribute("href");

        getDriver().navigate().to(urlAttribute);
        String url = getDriver().getCurrentUrl();
        getDriver().navigate().back();

        Assert.assertTrue(url.contains("plugins.jenkins.io/workflow-job"));
    }

    private void createPipelineProject() {
        getDriver().findElement(By.xpath("//a[@title='New Item']")).click();
        getDriver().findElement(By.xpath("//input[@id='name']")).sendKeys(
                PIPELINE_PROJECT_NAME + date.getTime());
        getDriver().findElement(By.xpath(
                "//li[@class='org_jenkinsci_plugins_workflow_job_WorkflowJob']")).click();
        getDriver().findElement(By.xpath("//button[@id='ok-button']")).click();
    }

    private void closeJenkinsCredProvWindowMethod() {
        javascriptExecutor.executeScript("arguments[0].scrollIntoView();",
                getDriver().findElement(By.xpath("//button[@id='credentials-add-abort-button']")));
        getDriver().navigate().back();
        getDriver().switchTo().alert().accept();
    }

    @Test
    public void testJenkinsCredentialsProviderWindow() {
        createPipelineProject();

        getDriver().findElement(PIPELINE_ITEM_CONFIGURATION).click();

        Select pipelineScriptDropDownList = new Select(getDriver()
                .findElement(By.xpath("//div[@class='jenkins-form-item config_pipeline active']//select")));
        pipelineScriptDropDownList.selectByIndex(1);
        Select scmDropDownList = new Select(getDriver()
                .findElement(By.xpath("//div[@class='jenkins-form-item has-help']//select")));
        scmDropDownList.selectByIndex(1);

        getDriver().findElement(By.xpath("//button[@id='yui-gen15-button']")).click();
        getDriver().findElement(By.xpath("//li[@id='yui-gen17']")).click();

        WebElement titleOfJenkinsCredentialsProviderWindow = getDriver().findElement(By.xpath("//h2"));

        Assert.assertEquals(titleOfJenkinsCredentialsProviderWindow.getText(), "Jenkins Credentials Provider: Jenkins");

        closeJenkinsCredProvWindowMethod();
    }

    @Test
    public void testPipelineSyntaxPageOpening() {
        createPipelineProject();

        getDriver().findElement(PIPELINE_ITEM_CONFIGURATION).click();

        String pipelineSyntaxLink = getDriver().findElement(By.xpath("//a[@href='pipeline-syntax']"))
                .getAttribute("href");
        getDriver().get(pipelineSyntaxLink);

        List<WebElement> breadcrumbsOfLinksMenu = getDriver().findElements(By.xpath("//li[@class='item']/a"));
        String breadcrumbsPipelineSyntaxPageItem = breadcrumbsOfLinksMenu.get(2).getAttribute("href");

        Assert.assertTrue(breadcrumbsPipelineSyntaxPageItem.contains("pipeline-syntax"));
    }

    @Test
    public void testPipelineGroovyPageOpening() {
        createPipelineProject();

        getDriver().findElement(PIPELINE_ITEM_CONFIGURATION).click();

        String useGroovySandboxCheckbox = getDriver().findElement(By.xpath(
                "//input[@name='_.sandbox']")).getAttribute("checked");

        Assert.assertEquals(useGroovySandboxCheckbox, "true");
    }

    @Test
    public void testTitleConfigPageContainsProjectTitle() {
        createPipelineProject();

        String titleOfConfigurationPipelinePage = getDriver().getTitle();

        Assert.assertTrue(titleOfConfigurationPipelinePage.contains(PIPELINE_PROJECT_NAME + date.getTime()));
    }

}