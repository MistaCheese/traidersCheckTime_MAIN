import CVD.CVD
import CVD.check.Driver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import java.util.concurrent.TimeUnit

class RunnerTest {
    private lateinit var driver: ChromeDriver

    private val webLink: String = "https://tradingsystem-dev.tecman.ru/"

    @Before
    fun singUp() {
        CVD("C:\\webDriver\\chrome\\", "C:\\Program Files (x86)\\Google\\Chrome\\Application").check()
        System.setProperty(
            "webdriver.chrome.driver",
            "C:\\webDriver\\chrome\\" + Driver().getLocalDriverSet("C:\\webDriver\\chrome\\")
                .last() + "\\chromedriver.exe"
        )
        driver = ChromeDriver()
        driver.manage()?.timeouts()?.implicitlyWait(10, TimeUnit.SECONDS)
        driver.manage().window().size = Dimension(1920, 1080)
    }

    @Test
    fun check() {
        driver.get(webLink)
        var exm: WebElement =
            driver.findElement(By.xpath("//body/div[@id='app']/div[1]/form[1]/div[2]/div[1]/div[1]/div[1]/input[1]")) // Поле с логином
        exm.sendKeys("admin")
        exm =
            driver.findElement(By.xpath("//body/div[@id='app']/div[1]/form[1]/div[2]/div[2]/div[1]/div[1]/input[1]")) // Поле с паролем
        exm.sendKeys("Lh4iX9NkwLeuWw%u")
        exm = driver.findElement(By.xpath("//span[contains(text(),'Войти')]")) // Кнопка войти
        exm.click()
        checkTime().check(webLink)
    }


    @After
    fun shutDown() {
        Thread.sleep(5000)
        driver.quit()

    }

}