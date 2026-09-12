// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Step حتى تظهر الخطوات داخل Allure Report
import io.qameta.allure.Step;

// نستورد أدوات Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/*
 * هذا الكلاس يمثل صفحة تأكيد نجاح الطلب.
 *
 * تظهر هذه الصفحة بعد الضغط على Finish
 * وتحتوي على رسالة Thank you for your order!
 */
public class CheckoutCompletePage extends BasePage {

    // الحاوية الرئيسية الخاصة بصفحة إتمام الطلب
    private static final By COMPLETE_CONTAINER =
            By.id("checkout_complete_container");

    // رسالة نجاح الطلب الرئيسية
    private static final By COMPLETE_HEADER =
            By.className("complete-header");

    // النص التوضيحي الموجود أسفل رسالة النجاح
    private static final By COMPLETE_TEXT =
            By.className("complete-text");

    // صورة إتمام الطلب
    private static final By COMPLETE_IMAGE =
            By.className("pony_express");

    // زر العودة إلى صفحة المنتجات
    private static final By BACK_HOME_BUTTON =
            By.id("back-to-products");

    // هذا الـConstructor يستقبل WebDriver ويرسله إلى BasePage
    public CheckoutCompletePage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن صفحة إتمام الطلب مفتوحة.
     */
    @Override
    protected By pageMarker() {

        return COMPLETE_CONTAINER;
    }

    /*
     * ترجع رسالة نجاح الطلب الرئيسية.
     *
     * القيمة المتوقعة:
     * Thank you for your order!
     */
    @Step("Read the order confirmation message")
    public String confirmationMessage() {

        return text(COMPLETE_HEADER);
    }

    // ترجع النص التوضيحي الموجود أسفل رسالة النجاح
    public String confirmationDetails() {

        return text(COMPLETE_TEXT);
    }

    // تتحقق من ظهور صورة إتمام الطلب
    public boolean isCompleteImageVisible() {

        return isVisible(COMPLETE_IMAGE);
    }

    /*
     * تتحقق من أن Cart Badge يساوي صفرًا.
     *
     * بعد إتمام الطلب يختفي الـBadge،
     * ولذلك cartCount الموجودة في BasePage ترجع صفرًا.
     */
    public boolean isCartEmpty() {

        return cartCount() == 0;
    }

    /*
     * تعود إلى صفحة المنتجات بعد إتمام الطلب.
     */
    @Step("Return to the products page")
    public ProductsPage backHome() {

        // نضغط على زر Back Home
        click(BACK_HOME_BUTTON);

        // نرجع صفحة المنتجات
        return new ProductsPage(driver);
    }
}