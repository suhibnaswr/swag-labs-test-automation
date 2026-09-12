// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Config للحصول على رابط الموقع من config.properties
import config.Config;

// نستورد Step حتى تظهر خطوات الصفحة داخل Allure Report
import io.qameta.allure.Step;

// نستورد By لإنشاء Locators
import org.openqa.selenium.By;

// نستورد WebDriver لاستقبال المتصفح من DriverFactory
import org.openqa.selenium.WebDriver;

/*
 * هذا الكلاس يمثل صفحة تسجيل الدخول في موقع Swag Labs.
 *
 * يرث من BasePage حتى يستخدم الـExplicit Waits
 * والـMethods المشتركة مثل click وtype وisVisible.
 */
public class LoginPage extends BasePage {

    /*
     * Locators الخاصة بصفحة تسجيل الدخول.
     *
     * جعلنا جميع الـLocators من نوع private حتى لا تخرج
     * من Page Class إلى Test Class.
     */

    // حقل اسم المستخدم
    private static final By USERNAME =
            By.id("user-name");

    // حقل كلمة المرور
    private static final By PASSWORD =
            By.id("password");

    // زر تسجيل الدخول
    private static final By LOGIN_BUTTON =
            By.id("login-button");

    // رسالة الخطأ التي تظهر عند فشل تسجيل الدخول
    private static final By ERROR_MESSAGE =
            By.cssSelector("h3[data-test='error']");

    // شعار Swag Labs الموجود أعلى صفحة تسجيل الدخول
    private static final By LOGIN_LOGO =
            By.className("login_logo");

    /*
     * هذا الـConstructor يستقبل WebDriver
     * ثم يرسله إلى Constructor الموجود في BasePage.
     */
    public LoginPage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن LoginPage مفتوحة.
     *
     * اخترنا LOGIN_LOGO لأنه عنصر مميز
     * لصفحة تسجيل الدخول.
     */
    @Override
    protected By pageMarker() {
        return LOGIN_LOGO;
    }

    /*
     * تفتح صفحة تسجيل الدخول باستخدام الرابط
     * الموجود في config.properties.
     */
    @Step("Open the login page")
    public LoginPage open() {

        // نفتح رابط الموقع دون كتابة الرابط داخل الاختبار
        driver.get(Config.baseUrl());

        // ننتظر ظهور العنصر الذي يثبت فتح الصفحة
        waitForVisible(pageMarker());

        // نرجع الصفحة نفسها حتى نستطيع استخدام Method Chaining
        return this;
    }

    /*
     * تسجل الدخول باستخدام Username وPassword صحيحين.
     *
     * بعد الضغط على Login ننتقل إلى ProductsPage،
     * لذلك ترجع الـMethod ProductsPage جديدة.
     */
    @Step("Log in as {0}")
    public ProductsPage loginAs(
            String username,
            String password
    ) {

        // نكتب البيانات ونضغط على زر تسجيل الدخول
        submit(username, password);

        // نرجع Page Object الخاصة بصفحة المنتجات
        return new ProductsPage(driver);
    }

    /*
     * تحاول تسجيل الدخول ببيانات مرفوضة.
     *
     * لأننا نتوقع البقاء في LoginPage،
     * ترجع الـMethod الصفحة نفسها.
     */
    @Step("Try to log in as {0}")
    public LoginPage loginExpectingFailure(
            String username,
            String password
    ) {

        // نكتب البيانات ونضغط على زر تسجيل الدخول
        submit(username, password);

        // ننتظر ظهور رسالة الخطأ
        waitForVisible(ERROR_MESSAGE);

        // نبقى في صفحة تسجيل الدخول
        return this;
    }

    /*
     * هذه Method داخلية تجمع خطوات تسجيل الدخول.
     *
     * جعلناها private لأنها لا تحتاج إلى الاستخدام
     * خارج LoginPage.
     */
    private void submit(
            String username,
            String password
    ) {

        // نكتب اسم المستخدم
        type(USERNAME, username);

        // نكتب كلمة المرور
        type(PASSWORD, password);

        // نضغط على زر Login
        click(LOGIN_BUTTON);
    }

    // تقرأ رسالة الخطأ الظاهرة في الصفحة
    @Step("Read the login error message")
    public String errorMessage() {

        return text(ERROR_MESSAGE);
    }

    // تتحقق من ظهور حقل اسم المستخدم
    public boolean isUsernameVisible() {

        return isVisible(USERNAME);
    }

    // تتحقق من ظهور حقل كلمة المرور
    public boolean isPasswordVisible() {

        return isVisible(PASSWORD);
    }

    // تتحقق من ظهور زر تسجيل الدخول
    public boolean isLoginButtonVisible() {

        return isVisible(LOGIN_BUTTON);
    }

    // تتحقق من ظهور شعار صفحة تسجيل الدخول
    public boolean isLogoVisible() {

        return isVisible(LOGIN_LOGO);
    }
}