// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها listeners
package listeners;

// نستورد Config لمعرفة هل نأخذ Screenshot عند النجاح
import config.Config;

// نستورد DriverFactory للحصول على WebDriver الحالي
import core.DriverFactory;

// نستورد Allure لإرفاق Screenshot ورابط الصفحة بالتقرير
import io.qameta.allure.Allure;

// نستورد Log4j2 لتسجيل أحداث الاختبارات
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// نستورد أدوات Selenium الخاصة بالـScreenshot
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

// نستورد Interfaces الخاصة بالـListeners في TestNG
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

// نستورد أدوات الملفات لحفظ Screenshot
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// نستورد التاريخ والوقت لإنشاء اسم Screenshot مميز
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * هذا الكلاس يراقب دورة حياة الاختبارات.
 *
 * يسجل:
 * بداية الاختبار
 * نجاح الاختبار
 * فشل الاختبار
 * تخطي الاختبار
 *
 * كما يحفظ Screenshot عند الفشل
 * ويرفقها داخل Allure Report.
 */
public class TestListener
        implements ITestListener, IInvokedMethodListener {

    // Logger الخاص بالـListener
    private static final Logger log =
            LogManager.getLogger(TestListener.class);

    // المجلد الذي سنحفظ Screenshots بداخله
    private static final Path SCREENSHOTS_DIRECTORY =
            Paths.get("target", "screenshots");

    /*
     * شكل التاريخ المستخدم داخل اسم Screenshot.
     *
     * مثال:
     * 20260911_221530_125
     */
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyyMMdd_HHmmss_SSS"
            );

    // تعمل عند بداية Test الموجودة في testng.xml
    @Override
    public void onStart(ITestContext context) {

        log.info(
                "TEST START | {}",
                context.getName()
        );
    }

    // تعمل مباشرة قبل تشغيل كل Test Method
    @Override
    public void onTestStart(ITestResult result) {

        log.info(
                "START | {}",
                testName(result)
        );
    }

    // تعمل عند نجاح Test Method
    @Override
    public void onTestSuccess(ITestResult result) {

        // نحسب مدة تشغيل الاختبار
        long duration =
                result.getEndMillis()
                        - result.getStartMillis();

        log.info(
                "PASS | {} | Duration: {} ms",
                testName(result),
                duration
        );
    }

    // تعمل عند فشل Test Method
    @Override
    public void onTestFailure(ITestResult result) {

        // نسجل اسم الاختبار الفاشل
        log.error(
                "FAIL | {}",
                testName(result)
        );

        /*
         * نسجل سبب الفشل إذا كان موجودًا.
         * هذا يمنع حدوث NullPointerException.
         */
        if (result.getThrowable() != null) {
            log.error(
                    "Reason | {}",
                    result.getThrowable().getMessage()
            );
        }
    }

    // تعمل عندما يتخطى TestNG اختبارًا
    @Override
    public void onTestSkipped(ITestResult result) {

        log.warn(
                "SKIP | {}",
                testName(result)
        );
    }

    // تعمل عند انتهاء Test الموجودة في testng.xml
    @Override
    public void onFinish(ITestContext context) {

        log.info(
                "TEST END | {}",
                context.getName()
        );
    }

    /*
     * تعمل بعد استدعاء أي Method من TestNG.
     *
     * نستخدمها لأخذ Screenshot بعد انتهاء Test Method
     * وقبل أن يغلق BaseTest المتصفح.
     */
    @Override
    public void afterInvocation(
            IInvokedMethod method,
            ITestResult result
    ) {

        /*
         * إذا لم تكن الـMethod اختبارًا حقيقيًا،
         * مثل BeforeMethod أو AfterMethod، نتوقف.
         */
        if (!method.isTestMethod()) {
            return;
        }

        /*
         * إذا لم يكن هناك WebDriver،
         * لا نحاول أخذ Screenshot.
         */
        if (!DriverFactory.hasDriver()) {
            return;
        }

        // نتحقق هل الاختبار فشل
        boolean failed =
                result.getStatus()
                        == ITestResult.FAILURE;

        /*
         * إذا لم يفشل الاختبار وكانت Screenshots النجاح
         * معطلة، فلا نأخذ Screenshot.
         */
        if (!failed && !Config.screenshotOnSuccess()) {
            return;
        }

        // نحصل على WebDriver الخاص بالـThread الحالي
        WebDriver driver = DriverFactory.get();

        /*
         * نحول WebDriver إلى TakesScreenshot
         * ثم نأخذ الصورة على شكل Bytes.
         */
        byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);

        // نرفق الصورة داخل Allure Report
        Allure.getLifecycle().addAttachment(
                testName(result),
                "image/png",
                "png",
                screenshot
        );

        // نرفق رابط الصفحة الحالية داخل Allure
        Allure.addAttachment(
                "Page URL",
                driver.getCurrentUrl()
        );

        /*
         * حسب المطلوب، نحفظ الصورة كملف فعلي
         * داخل target/screenshots عند الفشل.
         */
        if (failed) {
            saveScreenshot(
                    screenshot,
                    result.getName()
            );
        }
    }

    /*
     * تحفظ Screenshot داخل target/screenshots
     * باسم يحتوي على اسم الاختبار والتاريخ.
     */
    private void saveScreenshot(
            byte[] screenshot,
            String testName
    ) {

        try {
            // ننشئ المجلد إذا لم يكن موجودًا
            Files.createDirectories(
                    SCREENSHOTS_DIRECTORY
            );

            // نحصل على التاريخ والوقت الحالي
            String timestamp =
                    LocalDateTime.now()
                            .format(TIME_FORMAT);

            /*
             * ننظف اسم الاختبار من الرموز
             * التي لا تصلح لأسماء الملفات.
             */
            String safeTestName =
                    testName.replaceAll(
                            "[^a-zA-Z0-9-_]",
                            "_"
                    );

            // ننشئ اسم ملف Screenshot
            Path screenshotFile =
                    SCREENSHOTS_DIRECTORY.resolve(
                            safeTestName
                                    + "_"
                                    + timestamp
                                    + ".png"
                    );

            // نكتب الصورة داخل الملف
            Files.write(
                    screenshotFile,
                    screenshot
            );

            // نسجل مكان حفظ الصورة
            log.info(
                    "Screenshot saved | {}",
                    screenshotFile.toAbsolutePath()
            );

        } catch (Exception exception) {

            /*
             * لا نترك catch فارغًا.
             * نسجل سبب عدم القدرة على حفظ الصورة.
             */
            log.warn(
                    "Could not save screenshot | {}",
                    exception.getMessage()
            );
        }
    }

    /*
     * ترجع اسمًا واضحًا للاختبار.
     *
     * إذا كان الاختبار يستخدم DataProvider،
     * نضيف أول Parameter إلى الاسم.
     */
    private String testName(ITestResult result) {

        // نحصل على Parameters الخاصة بالاختبار
        Object[] parameters =
                result.getParameters();

        // إذا لم توجد Parameters نرجع اسم الاختبار فقط
        if (parameters == null
                || parameters.length == 0) {

            return result.getName();
        }

        // نضيف أول Parameter إلى اسم الاختبار
        return result.getName()
                + " ["
                + parameters[0]
                + "]";
    }
}