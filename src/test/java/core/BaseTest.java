// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها core
package core;

// نستورد Config لقراءة إعدادات المشروع وبيانات الدخول
import config.Config;

// نستورد Log4j2 لتسجيل أحداث تشغيل الاختبارات
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// نستورد WebDriver
import org.openqa.selenium.WebDriver;

// نستورد Annotations الخاصة بدورة حياة TestNG
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

// نستورد صفحات الموقع التي ستستخدمها Helpers
import pages.LoginPage;
import pages.ProductsPage;

/*
 * هذا الكلاس هو الأب لجميع Test Classes.
 *
 * أي Test Class سترث منه ستحصل على:
 * إعداد المتصفح
 * إغلاق المتصفح
 * WebDriver
 * فتح LoginPage
 * تسجيل الدخول
 */
public abstract class BaseTest {

    /*
     * Logger لتسجيل أحداث الاختبارات في Console
     * وداخل target/logs/test.log.
     */
    protected static final Logger log =
            LogManager.getLogger(BaseTest.class);

    // نخزن وقت بداية Test Class لحساب مدة تشغيلها
    private long classStartTime;

    /*
     * تعمل مرة واحدة قبل تشغيل جميع الاختبارات.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

        log.info(
                "SUITE START | URL: {} | Browser: {} | Headless: {} | Timeout: {} seconds",
                Config.baseUrl(),
                Config.browser(),
                Config.headless(),
                Config.timeout().toSeconds()
        );
    }

    /*
     * تعمل مرة واحدة قبل تشغيل Methods الموجودة
     * في كل Test Class.
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        // نحفظ وقت البداية
        classStartTime = System.currentTimeMillis();

        // نسجل اسم Test Class
        log.info(
                "CLASS START | {}",
                getClass().getSimpleName()
        );
    }

    /*
     * تعمل قبل كل Test Method.
     *
     * ننشئ WebDriver جديدًا لكل اختبار
     * حتى تكون الاختبارات مستقلة عن بعضها.
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {

        // ننشئ المتصفح باستخدام DriverFactory
        DriverFactory.create();

        log.info("WebDriver created");
    }

    /*
     * تعمل بعد كل Test Method حتى إذا فشل الاختبار.
     *
     * تغلق المتصفح وتحذف WebDriver من ThreadLocal.
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {

        // نغلق المتصفح
        DriverFactory.quit();

        log.info("WebDriver closed");
    }

    /*
     * تعمل مرة واحدة بعد انتهاء جميع Methods
     * الموجودة في Test Class.
     */
    @AfterClass(alwaysRun = true)
    public void afterClass() {

        // نحسب مدة تشغيل Test Class
        long duration =
                System.currentTimeMillis() - classStartTime;

        log.info(
                "CLASS END | {} | Duration: {} ms",
                getClass().getSimpleName(),
                duration
        );
    }

    /*
     * تعمل مرة واحدة بعد انتهاء جميع الاختبارات.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {

        log.info(
                "SUITE END | Allure results: target/allure-results | "
                        + "Screenshots: target/screenshots | "
                        + "Logs: target/logs/test.log"
        );
    }

    /*
     * ترجع WebDriver الخاص بالـThread الحالي.
     *
     * Test Classes ستستخدم هذه الـMethod
     * بدل التعامل مع DriverFactory مباشرة.
     */
    protected WebDriver driver() {

        return DriverFactory.get();
    }

    /*
     * تنشئ LoginPage وتفتح رابط الموقع.
     */
    protected LoginPage openLoginPage() {

        return new LoginPage(driver()).open();
    }

    /*
     * تسجل الدخول باستخدام standard.user
     * وpassword من config.properties.
     *
     * ترجع ProductsPage بعد نجاح تسجيل الدخول.
     */
    protected ProductsPage signIn() {

        return openLoginPage()
                .loginAs(
                        Config.get("standard.user"),
                        Config.get("password")
                );
    }
}