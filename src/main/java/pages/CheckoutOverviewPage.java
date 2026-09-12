// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Product لتحويل منتجات صفحة المراجعة إلى Objects
import model.Product;

// نستورد PriceUtils لتحويل الأسعار إلى BigDecimal
import utils.PriceUtils;

// نستورد Step حتى تظهر الخطوات داخل Allure Report
import io.qameta.allure.Step;

// نستورد أدوات Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// نستورد BigDecimal للتعامل مع القيم المالية
import java.math.BigDecimal;

// نستورد List للتعامل مع قائمة المنتجات
import java.util.List;

/*
 * هذا الكلاس يمثل صفحة مراجعة الطلب قبل تأكيده.
 *
 * من هذه الصفحة نستطيع:
 * قراءة المنتجات
 * قراءة Item Total
 * قراءة Tax
 * قراءة Total
 * إلغاء الطلب
 * إنهاء الطلب
 */
public class CheckoutOverviewPage extends BasePage {

    // الحاوية الرئيسية الخاصة بصفحة مراجعة Checkout
    private static final By OVERVIEW_CONTAINER =
            By.id("checkout_summary_container");

    // جميع المنتجات الظاهرة في صفحة المراجعة
    private static final By OVERVIEW_ITEMS =
            By.className("cart_item");

    // أسماء المنتجات
    private static final By ITEM_NAMES =
            By.className("inventory_item_name");

    // أوصاف المنتجات
    private static final By ITEM_DESCRIPTIONS =
            By.className("inventory_item_desc");

    // أسعار المنتجات
    private static final By ITEM_PRICES =
            By.className("inventory_item_price");

    // الكمية الخاصة بكل منتج
    private static final By ITEM_QUANTITY =
            By.className("cart_quantity");

    // المجموع قبل الضريبة
    private static final By ITEM_TOTAL =
            By.className("summary_subtotal_label");

    // قيمة الضريبة الضريبة
    private static final By TAX =
            By.className("summary_tax_label");

    // المجموع النهائي بعد الضريبة
    private static final By TOTAL =
            By.className("summary_total_label");

    // زر إلغاء الطلب والعودة إلى صفحة المنتجات
    private static final By CANCEL_BUTTON =
            By.id("cancel");

    // زر إنهاء الطلب
    private static final By FINISH_BUTTON =
            By.id("finish");

    // هذا الـConstructor يستقبل WebDriver ويرسله إلى BasePage
    public CheckoutOverviewPage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن صفحة المراجعة مفتوحة.
     */
    @Override
    protected By pageMarker() {

        return OVERVIEW_CONTAINER;
    }

    // ترجع عدد المنتجات الموجودة في صفحة المراجعة
    public int itemCount() {

        return driver.findElements(OVERVIEW_ITEMS).size();
    }

    // ترجع أسماء المنتجات الموجودة في الطلب
    public List<String> itemNames() {

        if (itemCount() == 0) {
            return List.of();
        }

        return elements(ITEM_NAMES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    // ترجع كميات المنتجات الموجودة في الطلب
    public List<Integer> itemQuantities() {

        if (itemCount() == 0) {
            return List.of();
        }

        return elements(ITEM_QUANTITY)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    /*
     * تقرأ جميع المنتجات الظاهرة في صفحة المراجعة
     * وتحولها إلى Product Objects.
     */
    public List<Product> products() {

        if (itemCount() == 0) {
            return List.of();
        }

        return elements(OVERVIEW_ITEMS)
                .stream()
                .map(this::createProductFromItem)
                .toList();
    }

    /*
     * تقرأ Item Total الظاهر في الصفحة.
     *
     * النص يكون مثل:
     * Item total: $39.98
     */
    public BigDecimal displayedItemTotal() {

        return extractPrice(
                text(ITEM_TOTAL)
        );
    }

    /*
     * تقرأ قيمة Tax الظاهرة في الصفحة.
     *
     * النص يكون مثل:
     * Tax: $3.20
     */
    public BigDecimal displayedTax() {

        return extractPrice(
                text(TAX)
        );
    }

    /*
     * تقرأ Total النهائي الظاهر في الصفحة.
     *
     * النص يكون مثل:
     * Total: $43.18
     */
    public BigDecimal displayedTotal() {

        return extractPrice(
                text(TOTAL)
        );
    }

    /*
     * تلغي الطلب من صفحة المراجعة.
     *
     * حسب سلوك الموقع، نعود إلى ProductsPage
     * وتبقى المنتجات داخل السلة.
     */
    @Step("Cancel the order")
    public ProductsPage cancelOrder() {

        // نضغط على زر Cancel
        click(CANCEL_BUTTON);

        // نرجع صفحة المنتجات
        return new ProductsPage(driver);
    }

    /*
     * تنهي الطلب وتنتقل إلى صفحة التأكيد.
     */
    @Step("Finish the order")
    public CheckoutCompletePage finishOrder() {

        // نتحرك إلى زر Finish
        scrollTo(FINISH_BUTTON);

        // نضغط على زر Finish
        click(FINISH_BUTTON);

        // نرجع صفحة إتمام الطلب
        return new CheckoutCompletePage(driver);
    }

    /*
     * تحول صف منتج من صفحة المراجعة
     * إلى Product Object.
     */
    private Product createProductFromItem(
            WebElement item
    ) {

        // نقرأ اسم المنتج
        String name = item
                .findElement(ITEM_NAMES)
                .getText()
                .trim();

        // نقرأ وصف المنتج
        String description = item
                .findElement(ITEM_DESCRIPTIONS)
                .getText()
                .trim();

        // نقرأ سعر المنتج
        BigDecimal price = PriceUtils.parse(
                item.findElement(ITEM_PRICES)
                        .getText()
        );

        // ننشئ Product باستخدام البيانات المقروءة
        return new Product(
                name,
                description,
                price
        );
    }

    /*
     * تستخرج السعر من نص يحتوي على كلمات.
     *
     * مثال:
     * "Item total: $39.98"
     * تصبح:
     * "$39.98"
     */
    private BigDecimal extractPrice(String text) {

        // نحدد مكان علامة الدولار
        int dollarPosition = text.indexOf("$");

        // إذا لم نجد علامة الدولار، نرسل النص إلى PriceUtils
        // حتى تظهر رسالة خطأ واضحة
        if (dollarPosition < 0) {
            return PriceUtils.parse(text);
        }

        // نأخذ النص من علامة الدولار حتى النهاية
        String priceText =
                text.substring(dollarPosition);

        // نحول السعر إلى BigDecimal
        return PriceUtils.parse(priceText);
    }
}