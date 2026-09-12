// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Product لتحويل بيانات صفحة التفاصيل إلى Object
import model.Product;

// نستورد PriceUtils لتحويل السعر إلى BigDecimal
import utils.PriceUtils;

// نستورد Step لإظهار الخطوات داخل Allure Report
import io.qameta.allure.Step;

// نستورد أدوات Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/*
 * هذا الكلاس يمثل صفحة تفاصيل منتج واحد.
 *
 * من هذه الصفحة نستطيع:
 * قراءة بيانات المنتج
 * إضافة المنتج إلى السلة
 * إزالة المنتج من السلة
 * العودة إلى صفحة المنتجات
 */
public class ProductDetailsPage extends BasePage {

    // الحاوية الرئيسية الخاصة بتفاصيل المنتج
    private static final By DETAILS_CONTAINER =
            By.className("inventory_details_container");

    // اسم المنتج
    private static final By PRODUCT_NAME =
            By.className("inventory_details_name");

    // وصف المنتج
    private static final By PRODUCT_DESCRIPTION =
            By.className("inventory_details_desc");

    // سعر المنتج
    private static final By PRODUCT_PRICE =
            By.className("inventory_details_price");

    /*
     * زر Add to cart أو Remove داخل صفحة التفاصيل.
     *
     * استخدمنا CSS Selector حتى نحدد زر المنتج الموجود
     * داخل حاوية التفاصيل، ولا نختار زرًا آخر بالخطأ.
     */
    private static final By PRODUCT_BUTTON =
            By.cssSelector(
                    ".inventory_details_container button.btn_inventory"
            );

    // زر العودة إلى صفحة المنتجات
    private static final By BACK_TO_PRODUCTS_BUTTON =
            By.id("back-to-products");

    // هذا الـConstructor يستقبل WebDriver ويرسله إلى BasePage
    public ProductDetailsPage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن صفحة التفاصيل مفتوحة.
     */
    @Override
    protected By pageMarker() {

        return DETAILS_CONTAINER;
    }

    /*
     * تقرأ اسم ووصف وسعر المنتج من الصفحة
     * وتحولها إلى Product Object.
     */
    @Step("Read the product details")
    public Product product() {

        // نقرأ اسم المنتج
        String name = text(PRODUCT_NAME);

        // نقرأ وصف المنتج
        String description =
                text(PRODUCT_DESCRIPTION);

        // نقرأ السعر ونحوله إلى BigDecimal
        java.math.BigDecimal price =
                PriceUtils.parse(
                        text(PRODUCT_PRICE)
                );

        // ننشئ Product ونرجعه
        return new Product(
                name,
                description,
                price
        );
    }

    /*
     * تضغط على زر Add to cart.
     *
     * بعد الإضافة نبقى في صفحة التفاصيل،
     * لذلك ترجع الصفحة نفسها.
     */
    @Step("Add the product to the cart")
    public ProductDetailsPage addToCart() {

        // نتأكد من أن نص الزر هو Add to cart
        if (!productButtonText().equalsIgnoreCase(
                "Add to cart"
        )) {
            throw new exceptions.FrameworkException(
                    "The product is already in the cart"
            );
        }

        // نضغط على زر الإضافة
        click(PRODUCT_BUTTON);

        // نرجع الصفحة نفسها
        return this;
    }

    /*
     * تضغط على زر Remove لإزالة المنتج من السلة.
     */
    @Step("Remove the product from the cart")
    public ProductDetailsPage removeFromCart() {

        // نتأكد من أن نص الزر هو Remove
        if (!productButtonText().equalsIgnoreCase(
                "Remove"
        )) {
            throw new exceptions.FrameworkException(
                    "The product is not currently in the cart"
            );
        }

        // نضغط على زر الإزالة
        click(PRODUCT_BUTTON);

        // نرجع الصفحة نفسها
        return this;
    }

    /*
     * ترجع نص زر المنتج.
     *
     * تكون القيمة Add to cart أو Remove.
     */
    public String productButtonText() {

        return text(PRODUCT_BUTTON);
    }

    /*
     * تعود إلى صفحة المنتجات.
     *
     * لأنها عملية انتقال، ترجع ProductsPage.
     */
    @Step("Return to the products page")
    public ProductsPage backToProducts() {

        // نضغط على زر Back to products
        click(BACK_TO_PRODUCTS_BUTTON);

        // نرجع Page Object الخاصة بصفحة المنتجات
        return new ProductsPage(driver);
    }
}