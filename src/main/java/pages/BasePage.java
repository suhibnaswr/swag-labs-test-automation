// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Config للحصول على مدة الانتظار من config.properties
import config.Config;

// نستورد FrameworkException لإظهار أخطاء واضحة من الـFramework
import exceptions.FrameworkException;

// نستورد الأدوات المطلوبة من Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// نستورد Explicit Wait والأدوات المرتبطة به
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// نستورد List للتعامل مع مجموعات العناصر
import java.util.List;

/*
 * هذا هو الكلاس الأب لجميع Page Objects.
 *
 * يحتوي على:
 * Explicit Waits
 * Click
 * Type
 * قراءة النص
 * التحقق من ظهور العناصر
 * JavaScript Scroll
 * عنوان الصفحة
 * Cart Badge
 * Logout
 */
public abstract class BasePage {

    /*
     * هذه هي الـLocators المشتركة بين صفحات الموقع الداخلية.
     *
     * جعلناها private لأنها تخص BasePage فقط
     * ولا نريد استخدامها مباشرة داخل الاختبارات.
     */

    // عنوان الصفحة مثل Products أو Your Cart
    private static final By PAGE_HEADING =
            By.className("title");

    // رقم المنتجات الظاهر فوق أيقونة السلة
    private static final By CART_BADGE =
            By.className("shopping_cart_badge");

    // زر القائمة الجانبية
    private static final By MENU_BUTTON =
            By.id("react-burger-menu-btn");

    // رابط تسجيل الخروج الموجود داخل القائمة الجانبية
    private static final By LOGOUT_LINK =
            By.id("logout_sidebar_link");

    // WebDriver الذي تستخدمه الصفحة
    protected final WebDriver driver;

    // Explicit Wait الذي تستخدمه جميع صفحات الموقع
    private final WebDriverWait wait;

    // هذا الـConstructor يستقبل WebDriver من Page Object السابقة
    protected BasePage(WebDriver driver) {

        // نتحقق من أن WebDriver ليس null
        if (driver == null) {
            throw new FrameworkException(
                    "WebDriver must not be null"
            );
        }

        // نخزن WebDriver حتى تستخدمه الصفحة
        this.driver = driver;

        /*
         * ننشئ Explicit Wait باستخدام مدة الانتظار
         * الموجودة في config.properties.
         */
        this.wait = new WebDriverWait(
                driver,
                Config.timeout()
        );
    }

    /*
     * كل صفحة ترث من BasePage يجب أن تحدد الـLocator
     * الذي يثبت أن الصفحة قد فُتحت.
     *
     * مثال:
     * LoginPage ترجع USERNAME.
     * ProductsPage ترجع PRODUCTS_CONTAINER.
     */
    protected abstract By pageMarker();

    /*
     * تتحقق من أن الصفحة الحالية مفتوحة
     * عن طريق انتظار ظهور العنصر المميز لها.
     */
    public boolean isLoaded() {

        try {
            // ننتظر ظهور العنصر الذي يثبت أن الصفحة مفتوحة
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            pageMarker()
                    )
            ).isDisplayed();

        } catch (TimeoutException exception) {

            // إذا لم يظهر العنصر خلال مدة الانتظار، فالصفحة غير مفتوحة
            return false;
        }
    }

    /*
     * تنتظر ظهور العنصر ثم ترجعه.
     *
     * هذه الـMethod من نوع protected حتى تستخدمها
     * Page Classes فقط، وليس Test Classes.
     */
    protected WebElement waitForVisible(By locator) {

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        locator
                )
        );
    }

    // تنتظر أن يصبح العنصر قابلًا للضغط ثم ترجعه
    protected WebElement waitForClickable(By locator) {

        return wait.until(
                ExpectedConditions.elementToBeClickable(
                        locator
                )
        );
    }

    // تضغط على عنصر بعد أن يصبح قابلًا للضغط
    protected void click(By locator) {

        waitForClickable(locator).click();
    }

    /*
     * تحذف النص السابق من الحقل
     * ثم تكتب النص الجديد.
     */
    protected void type(By locator, String text) {

        // نتحقق من أن النص ليس null
        if (text == null) {
            throw new FrameworkException(
                    "Text entered into a field must not be null"
            );
        }

        // ننتظر ظهور الحقل
        WebElement field = waitForVisible(locator);

        // نحذف النص القديم
        field.clear();

        // نكتب النص الجديد
        field.sendKeys(text);
    }

    // تقرأ نص عنصر واحد بعد ظهوره
    protected String text(By locator) {

        return waitForVisible(locator)
                .getText()
                .trim();
    }

    // ترجع قائمة بجميع العناصر المطابقة للـLocator
    protected List<WebElement> elements(By locator) {

        /*
         * ننتظر وجود عنصر واحد على الأقل قبل إعادة القائمة.
         * هذا يمنع قراءة القائمة قبل اكتمال تحميل الصفحة.
         */
        wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        locator
                )
        );

        return driver.findElements(locator);
    }

    /*
     * تتحقق من ظهور العنصر.
     * إذا لم يظهر خلال مدة الانتظار ترجع false.
     */
    protected boolean isVisible(By locator) {

        try {
            return waitForVisible(locator).isDisplayed();

        } catch (TimeoutException exception) {
            return false;
        }
    }

    /*
     * تتحقق من عدم وجود العنصر دون انتظار طويل.
     *
     * سنستخدمها عندما يكون اختفاء العنصر هو النتيجة المتوقعة،
     * مثل اختفاء Cart Badge عندما تصبح السلة فارغة.
     */
    protected boolean isAbsent(By locator) {

        return driver.findElements(locator).isEmpty();
    }

    // تنتظر اختفاء عنصر من الصفحة
    protected boolean waitForInvisible(By locator) {

        return wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        locator
                )
        );
    }

    // تقرأ قيمة Attribute من عنصر
    protected String attribute(
            By locator,
            String attributeName
    ) {

        return waitForVisible(locator)
                .getAttribute(attributeName);
    }

    /*
     * تحرك الصفحة إلى العنصر باستخدام JavaScript.
     * هذا يحقق شرط JavaScript Scroll الموجود في المشروع.
     */
    protected void scrollTo(By locator) {

        // ننتظر ظهور العنصر أولًا
        WebElement element = waitForVisible(locator);

        // نحول WebDriver إلى JavascriptExecutor
        JavascriptExecutor javascript =
                (JavascriptExecutor) driver;

        // نحرك الصفحة حتى يصبح العنصر في منتصف الشاشة
        javascript.executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                element
        );
    }

    // ترجع عنوان الصفحة الداخلية مثل Products أو Your Cart
    public String heading() {

        return text(PAGE_HEADING);
    }

    /*
     * ترجع عدد المنتجات الظاهر في Cart Badge.
     *
     * إذا لم يظهر الـBadge، فهذا يعني أن السلة فارغة
     * ولذلك نرجع صفرًا.
     */
    public int cartCount() {

        List<WebElement> badges =
                driver.findElements(CART_BADGE);

        if (badges.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(
                badges.get(0).getText().trim()
        );
    }

    // ترجع رابط الصفحة الحالية
    public String currentUrl() {

        return driver.getCurrentUrl();
    }

    /*
     * تفتح القائمة الجانبية ثم تسجل الخروج.
     *
     * الانتقال ينتهي في LoginPage،
     * لذلك ترجع الـMethod LoginPage جديدة.
     */
    public LoginPage logout() {

        // نضغط على زر القائمة
        click(MENU_BUTTON);

        // نضغط على رابط تسجيل الخروج
        click(LOGOUT_LINK);

        // نرجع Page Object الخاصة بصفحة تسجيل الدخول
        return new LoginPage(driver);
    }
}