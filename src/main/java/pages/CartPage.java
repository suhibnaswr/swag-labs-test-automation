// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد Exception الخاصة بعدم العثور على المنتج
import exceptions.ProductNotFoundException;

// نستورد Product لتحويل عناصر السلة إلى Objects
import model.Product;

// نستورد PriceUtils لتحويل الأسعار إلى BigDecimal
import utils.PriceUtils;

// نستورد Step لعرض الخطوات في Allure Report
import io.qameta.allure.Step;

// نستورد أدوات Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// نستورد BigDecimal للأسعار
import java.math.BigDecimal;

// نستورد List للتعامل مع عناصر السلة
import java.util.List;

/*
 * هذا الكلاس يمثل صفحة سلة المشتريات.
 *
 * يحتوي على Methods لقراءة المنتجات والأسعار والكميات،
 * وإزالة منتج، ومتابعة التسوق، والانتقال إلى Checkout.
 */
public class CartPage extends BasePage {

    // الحاوية الرئيسية الخاصة بصفحة السلة
    private static final By CART_CONTAINER =
            By.id("cart_contents_container");

    // جميع صفوف المنتجات الموجودة في السلة
    private static final By CART_ITEMS =
            By.className("cart_item");

    // أسماء المنتجات داخل السلة
    private static final By ITEM_NAMES =
            By.className("inventory_item_name");

    // أوصاف المنتجات داخل السلة
    private static final By ITEM_DESCRIPTIONS =
            By.className("inventory_item_desc");

    // أسعار المنتجات داخل السلة
    private static final By ITEM_PRICES =
            By.className("inventory_item_price");

    // كمية كل منتج داخل السلة
    private static final By ITEM_QUANTITY =
            By.className("cart_quantity");

    // زر متابعة التسوق
    private static final By CONTINUE_SHOPPING_BUTTON =
            By.id("continue-shopping");

    // زر بدء Checkout
    private static final By CHECKOUT_BUTTON =
            By.id("checkout");

    // هذا الـConstructor يستقبل WebDriver ويرسله إلى BasePage
    public CartPage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن CartPage مفتوحة.
     */
    @Override
    protected By pageMarker() {

        return CART_CONTAINER;
    }

    // ترجع عدد صفوف المنتجات الموجودة في السلة
    public int itemCount() {

        /*
         * استخدمنا findElements مباشرة لأن السلة قد تكون فارغة.
         * عندما لا توجد منتجات سترجع قائمة فارغة بدل Exception.
         */
        return driver.findElements(CART_ITEMS).size();
    }

    // ترجع أسماء جميع المنتجات الموجودة في السلة
    public List<String> itemNames() {

        // إذا كانت السلة فارغة نرجع قائمة فارغة
        if (itemCount() == 0) {
            return List.of();
        }

        return elements(ITEM_NAMES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    // ترجع أسعار المنتجات الموجودة في السلة
    public List<BigDecimal> itemPrices() {

        // إذا كانت السلة فارغة نرجع قائمة فارغة
        if (itemCount() == 0) {
            return List.of();
        }

        return elements(ITEM_PRICES)
                .stream()
                .map(WebElement::getText)
                .map(PriceUtils::parse)
                .toList();
    }

    /*
     * تحول جميع صفوف السلة إلى Product Objects.
     *
     * سنستخدم هذه القائمة في حساب CartSummary
     * ومقارنة أسعار السلة بأسعار صفحة المنتجات.
     */
    public List<Product> products() {

        // إذا كانت السلة فارغة نرجع قائمة فارغة
        if (itemCount() == 0) {
            return List.of();
        }

        return elements(CART_ITEMS)
                .stream()
                .map(this::createProductFromCartItem)
                .toList();
    }

    // تتحقق من وجود منتج محدد داخل السلة
    public boolean containsProduct(String productName) {

        return itemNames().contains(productName);
    }

    /*
     * ترجع كمية منتج محدد.
     *
     * في Swag Labs تكون كمية المنتج عادة 1.
     */
    public int quantityOf(String productName) {

        // نبحث عن صف المنتج
        WebElement item = cartItem(productName);

        // نقرأ الكمية ونحولها إلى int
        return Integer.parseInt(
                item.findElement(ITEM_QUANTITY)
                        .getText()
                        .trim()
        );
    }

    /*
     * تزيل منتجًا محددًا من السلة.
     *
     * نبقى في CartPage بعد الإزالة.
     */
    @Step("Remove {0} from the cart")
    public CartPage removeItem(String productName) {

        // نتأكد أولًا أن المنتج موجود
        cartItem(productName);

        // نضغط على زر Remove الخاص بالمنتج
        click(removeButton(productName));

        /*
         * ننتظر اختفاء صف المنتج من السلة
         * باستخدام Locator ديناميكي خاص بهذا المنتج.
         */
        waitForInvisible(cartItemLocator(productName));

        // نرجع الصفحة نفسها
        return this;
    }

    /*
     * تعود إلى صفحة المنتجات مع الاحتفاظ بمحتويات السلة.
     */
    @Step("Continue shopping")
    public ProductsPage continueShopping() {

        // نضغط على زر Continue Shopping
        click(CONTINUE_SHOPPING_BUTTON);

        // نرجع صفحة المنتجات
        return new ProductsPage(driver);
    }

    /*
     * تبدأ عملية Checkout.
     *
     * لأنها عملية انتقال، ترجع CheckoutInformationPage.
     */
    @Step("Start checkout")
    public CheckoutInformationPage checkout() {

        // نضغط على زر Checkout
        click(CHECKOUT_BUTTON);

        // نرجع صفحة إدخال بيانات العميل
        return new CheckoutInformationPage(driver);
    }

    /*
     * تحول صفًا واحدًا من السلة إلى Product Object.
     *
     * لا يخرج WebElement من Page Class.
     */
    private Product createProductFromCartItem(
            WebElement cartItem
    ) {

        // نقرأ اسم المنتج
        String name = cartItem
                .findElement(ITEM_NAMES)
                .getText()
                .trim();

        // نقرأ وصف المنتج
        String description = cartItem
                .findElement(ITEM_DESCRIPTIONS)
                .getText()
                .trim();

        // نقرأ سعر المنتج ونحوله إلى BigDecimal
        BigDecimal price = PriceUtils.parse(
                cartItem
                        .findElement(ITEM_PRICES)
                        .getText()
        );

        // ننشئ Product من البيانات المقروءة
        return new Product(
                name,
                description,
                price
        );
    }

    /*
     * تبحث عن صف منتج داخل السلة حسب اسمه.
     *
     * إذا لم تجده، ترمي ProductNotFoundException.
     */
    private WebElement cartItem(String productName) {

        return driver.findElements(CART_ITEMS)
                .stream()
                .filter(item ->
                        item.findElement(ITEM_NAMES)
                                .getText()
                                .trim()
                                .equals(productName)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                productName,
                                itemNames()
                        )
                );
    }

    /*
     * تنشئ Locator ديناميكيًا لصف المنتج
     * حتى نستطيع انتظار اختفائه بعد الإزالة.
     */
    private By cartItemLocator(String productName) {

        return By.xpath(
                "//div[@class='cart_item']"
                        + "[.//div[@class='inventory_item_name'"
                        + " and normalize-space()='"
                        + productName
                        + "']]"
        );
    }

    /*
     * تنشئ Locator ديناميكيًا لزر Remove
     * الموجود داخل صف المنتج المحدد.
     */
    // تنشئ Locator لزر Remove الخاص بمنتج محدد داخل السلة
    private By removeButton(String productName) {

        return By.xpath(
                "//div[@data-test='inventory-item']"
                        + "[.//div[@data-test='inventory-item-name'"
                        + " and normalize-space()='"
                        + productName
                        + "']]//button"
        );
    }


}