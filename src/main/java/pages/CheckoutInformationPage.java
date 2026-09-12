// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Customer لاستخدام بيانات العميل القادمة من CSV
import model.Customer;

// نستورد Step لإظهار الخطوات داخل Allure Report
import io.qameta.allure.Step;

// نستورد أدوات Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/*
 * هذا الكلاس يمثل صفحة إدخال بيانات العميل أثناء Checkout.
 *
 * تحتوي الصفحة على:
 * First Name
 * Last Name
 * Postal Code
 * Continue
 * Cancel
 * Error Message
 */
public class CheckoutInformationPage extends BasePage {

    // الحاوية الرئيسية الخاصة ببيانات Checkout
    private static final By CHECKOUT_INFORMATION_CONTAINER =
            By.id("checkout_info_container");

    // حقل الاسم الأول
    private static final By FIRST_NAME =
            By.id("first-name");

    // حقل الاسم الأخير
    private static final By LAST_NAME =
            By.id("last-name");

    // حقل الرمز البريدي
    private static final By POSTAL_CODE =
            By.id("postal-code");

    // زر المتابعة إلى صفحة المراجعة
    private static final By CONTINUE_BUTTON =
            By.id("continue");

    // زر إلغاء Checkout والعودة إلى السلة
    private static final By CANCEL_BUTTON =
            By.id("cancel");

    // رسالة الخطأ الخاصة بالحقول الفارغة
    private static final By ERROR_MESSAGE =
            By.cssSelector("h3[data-test='error']");

    // هذا الـConstructor يستقبل WebDriver ويرسله إلى BasePage
    public CheckoutInformationPage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن صفحة بيانات Checkout مفتوحة.
     */
    @Override
    protected By pageMarker() {

        return CHECKOUT_INFORMATION_CONTAINER;
    }

    /*
     * تكمل Checkout باستخدام قيم منفصلة.
     *
     * بعد إدخال البيانات الصحيحة ننتقل إلى
     * CheckoutOverviewPage.
     */
    @Step("Enter checkout information for {0} {1}")
    public CheckoutOverviewPage continueWith(
            String firstName,
            String lastName,
            String postalCode
    ) {

        // نملأ جميع الحقول
        fillInformation(
                firstName,
                lastName,
                postalCode
        );

        // نضغط على زر Continue
        click(CONTINUE_BUTTON);

        // نرجع صفحة مراجعة الطلب
        return new CheckoutOverviewPage(driver);
    }

    /*
     * تكمل Checkout باستخدام Customer Object
     * القادم من customers.csv.
     */
    @Step("Enter checkout information for {0}")
    public CheckoutOverviewPage continueWith(
            Customer customer
    ) {

        // نستخدم بيانات Customer داخل الـMethod الأساسية
        return continueWith(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPostalCode()
        );
    }

    /*
     * تستخدم عند اختبار حقل مفقود.
     *
     * نتوقع أن نبقى في الصفحة نفسها
     * وأن تظهر رسالة خطأ.
     */
    @Step("Submit incomplete checkout information")
    public CheckoutInformationPage continueExpectingFailure(
            String firstName,
            String lastName,
            String postalCode
    ) {

        // نملأ الحقول بالقيم القادمة من DataProvider
        fillInformation(
                firstName,
                lastName,
                postalCode
        );

        // نضغط على Continue
        click(CONTINUE_BUTTON);

        // ننتظر ظهور رسالة الخطأ
        waitForVisible(ERROR_MESSAGE);

        // نبقى في الصفحة نفسها
        return this;
    }

    // تقرأ رسالة الخطأ الظاهرة
    @Step("Read the checkout information error")
    public String errorMessage() {

        return text(ERROR_MESSAGE);
    }

    /*
     * تلغي Checkout وتعود إلى CartPage.
     *
     * محتويات السلة يجب أن تبقى كما هي.
     */
    @Step("Cancel checkout information")
    public CartPage cancel() {

        // نضغط على زر Cancel
        click(CANCEL_BUTTON);

        // نرجع Page Object الخاصة بالسلة
        return new CartPage(driver);
    }

    /*
     * هذه Method داخلية تملأ حقول بيانات العميل.
     *
     * جعلناها private لمنع تكرار الكود.
     */
    private void fillInformation(
            String firstName,
            String lastName,
            String postalCode
    ) {

        // نكتب الاسم الأول
        type(FIRST_NAME, firstName);

        // نكتب الاسم الأخير
        type(LAST_NAME, lastName);

        // نكتب الرمز البريدي
        type(POSTAL_CODE, postalCode);
    }

    // تتحقق من ظهور حقل الاسم الأول
    public boolean isFirstNameVisible() {

        return isVisible(FIRST_NAME);
    }

    // تتحقق من ظهور حقل الاسم الأخير
    public boolean isLastNameVisible() {

        return isVisible(LAST_NAME);
    }

    // تتحقق من ظهور حقل الرمز البريدي
    public boolean isPostalCodeVisible() {

        return isVisible(POSTAL_CODE);
    }
}