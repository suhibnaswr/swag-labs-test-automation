// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها core
package core;

// نستورد Config لقراءة إعدادات المتصفح من config.properties
import config.Config;

// نستورد الـException الخاصة بالمشروع لإظهار أخطاء واضحة
import exceptions.FrameworkException;

// نستورد WebDriver للتحكم بالمتصفح
import org.openqa.selenium.WebDriver;

// نستورد ChromeDriver لتشغيل متصفح Google Chrome
import org.openqa.selenium.chrome.ChromeDriver;

// نستورد ChromeOptions لتحديد إعدادات متصفح Chrome
import org.openqa.selenium.chrome.ChromeOptions;

// نستورد HashMap وMap لتخزين تفضيلات المتصفح
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

// هذا الكلاس مسؤول عن إنشاء WebDriver وإغلاقه
public final class DriverFactory {

    /*
     * ThreadLocal يعطي كل Thread نسخة مستقلة من WebDriver.
     * هذا يمنع الاختبارات المتوازية من استخدام المتصفح نفسه.
     */
    private static final ThreadLocal<WebDriver> DRIVER =
            new ThreadLocal<>();

    /*
     * جعلنا الـConstructor من نوع private لمنع إنشاء Object
     * لأننا سنستخدم جميع الـMethods بطريقة static.
     */
    private DriverFactory() {
    }

    // هذه الـMethod تنشئ WebDriver حسب إعدادات المشروع
    // هذه الـMethod تنشئ WebDriver حسب المتصفح المحدد في config.properties
    public static void create() {

        // نتأكد من عدم وجود Driver سابق للـThread الحالي
        if (DRIVER.get() != null) {
            throw new FrameworkException(
                    "A WebDriver already exists for the current thread"
            );
        }

        // نقرأ اسم المتصفح من ملف config.properties
        String browser = Config.browser();

        try {

            WebDriver driver;

            // إذا كان المتصفح المحدد هو Chrome
            if (browser.equalsIgnoreCase("chrome")) {

                driver = new ChromeDriver(createChromeOptions());

                // إذا كان المتصفح المحدد هو Edge
            } else if (browser.equalsIgnoreCase("edge")) {

                driver = new EdgeDriver(createEdgeOptions());

                // رفض أي متصفح غير مدعوم
            } else {
                throw new FrameworkException(
                        "Unsupported browser: " + browser
                );
            }

            // تكبير نافذة المتصفح عند عدم استخدام Headless
            if (!Config.headless()) {
                driver.manage().window().maximize();
            }

            // تخزين الـDriver داخل ThreadLocal
            DRIVER.set(driver);

        } catch (FrameworkException exception) {

            // نعيد نفس الـException دون تغليفها مرة أخرى
            throw exception;

        } catch (Exception exception) {

            // إظهار اسم المتصفح الذي تعذر تشغيله
            throw new FrameworkException(
                    "Could not create the "
                            + browser
                            + " WebDriver",
                    exception
            );
        }
    }

    // هذه الـMethod ترجع WebDriver الخاص بالـThread الحالي
    public static WebDriver get() {

        // نحصل على الـDriver المخزن داخل ThreadLocal
        WebDriver driver = DRIVER.get();

        // إذا لم يتم إنشاء Driver، نظهر خطأ واضحًا
        if (driver == null) {
            throw new FrameworkException(
                    "No WebDriver exists for this thread. "
                            + "Was DriverFactory.create() called?"
            );
        }

        // نرجع الـDriver حتى تستخدمه الاختبارات والصفحات
        return driver;
    }

    /*
     * هذه الـMethod تتحقق من وجود Driver.
     * سنستخدمها لاحقًا في TestListener قبل أخذ Screenshot.
     */
    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    // هذه الـMethod تغلق WebDriver الخاص بالـThread الحالي
    public static void quit() {

        // نحصل على الـDriver الموجود داخل ThreadLocal
        WebDriver driver = DRIVER.get();

        // نتأكد من وجود Driver قبل محاولة إغلاقه
        if (driver != null) {
            try {
                // نغلق المتصفح وجميع النوافذ المرتبطة به
                driver.quit();

            } finally {
                /*
                 * نحذف قيمة الـDriver من ThreadLocal.
                 * هذه الخطوة مهمة لمنع Memory Leak عند تشغيل
                 * الاختبارات بالتوازي.
                 */
                DRIVER.remove();
            }
        }
    }

    // هذه الـMethod تنشئ إعدادات متصفح Chrome
    private static ChromeOptions createChromeOptions() {

        // ننشئ Map لتخزين تفضيلات Chrome
        Map<String, Object> preferences = new HashMap<>();

        // نعطل خدمة حفظ كلمات المرور داخل Chrome
        preferences.put("credentials_enable_service", false);

        // نعطل مدير كلمات المرور
        preferences.put("profile.password_manager_enabled", false);

        // نعطل تحذيرات تسريب كلمات المرور
        preferences.put(
                "profile.password_manager_leak_detection",
                false
        );

        // ننشئ Object يحتوي على خيارات Chrome
        ChromeOptions options = new ChromeOptions();

        // نضيف تفضيلات المتصفح إلى ChromeOptions
        options.setExperimentalOption("prefs", preferences);

        // نضيف إعدادات تمنع نوافذ مدير كلمات المرور
        options.addArguments(
                "--disable-features="
                        + "PasswordManagerOnboarding,"
                        + "PasswordLeakDetection,"
                        + "AutofillServerCommunication"
        );

        // يسمح بتشغيل Chrome مع إصدارات Selenium الحديثة
        options.addArguments("--remote-allow-origins=*");

        // إذا كانت قيمة headless تساوي true نشغل Chrome دون نافذة
        if (Config.headless()) {
            options.addArguments("--headless=new");

            /*
             * نحدد حجم المتصفح في وضع Headless
             * حتى تظهر عناصر الصفحة بنفس شكل الشاشة الكبيرة.
             */
            options.addArguments("--window-size=1920,1080");
        }

        // نرجع إعدادات Chrome بعد تجهيزها
        return options;
    }

    // هذه الـMethod تنشئ إعدادات متصفح Microsoft Edge
    private static EdgeOptions createEdgeOptions() {

        // إنشاء إعدادات Edge
        EdgeOptions options = new EdgeOptions();

        // تعطيل الإشعارات والنوافذ غير الضرورية
        options.addArguments(
                "--disable-notifications",
                "--disable-features=PasswordManagerOnboarding,PasswordLeakDetection"
        );

        // تشغيل Edge دون نافذة إذا كانت خاصية Headless مفعلة
        if (Config.headless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        // إرجاع إعدادات Edge بعد تجهيزها
        return options;
    }
}