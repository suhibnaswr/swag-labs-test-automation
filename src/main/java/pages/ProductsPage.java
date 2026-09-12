// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها pages
package pages;

// نستورد الـException الخاصة بالمنتج غير الموجود
import exceptions.ProductNotFoundException;

// نستورد Product لتحويل بيانات الموقع إلى Objects
import model.Product;

// نستورد PriceUtils لتحويل السعر النصي إلى BigDecimal
import utils.PriceUtils;

// نستورد Step حتى تظهر الخطوات في Allure Report
import io.qameta.allure.Step;

// نستورد أدوات Selenium
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// نستورد Select للتعامل مع قائمة ترتيب المنتجات
import org.openqa.selenium.support.ui.Select;

// نستورد BigDecimal للأسعار
import java.math.BigDecimal;

// نستورد List للتعامل مع قائمة المنتجات
import java.util.List;

/*
 * هذا الكلاس يمثل صفحة المنتجات في موقع Swag Labs.
 *
 * يحتوي على Locators المنتجات وMethods القراءة والترتيب
 * والإضافة والإزالة وفتح التفاصيل والسلة.
 */
public class ProductsPage extends BasePage {

    // العنصر الذي يحتوي على جميع المنتجات
    private static final By INVENTORY_CONTAINER =
            By.id("inventory_container");

    // بطاقات المنتجات الموجودة في الصفحة
    private static final By PRODUCT_ITEMS =
            By.className("inventory_item");

    // أسماء المنتجات
    private static final By PRODUCT_NAMES =
            By.className("inventory_item_name");

    // أوصاف المنتجات
    private static final By PRODUCT_DESCRIPTIONS =
            By.className("inventory_item_desc");

    // أسعار المنتجات
    private static final By PRODUCT_PRICES =
            By.className("inventory_item_price");

    // قائمة ترتيب المنتجات
    private static final By SORT_DROPDOWN =
            By.className("product_sort_container");

    // رابط السلة الموجود أعلى الصفحة
    private static final By CART_LINK =
            By.className("shopping_cart_link");

    //// عدّاد المنتجات الظ التي تمت إضافتها إلى السلة
    private static final By CART_BADGE =
            By.className("shopping_cart_badge");

    // هذا الـConstructor يستقبل WebDriver ويرسله إلى BasePage
    public ProductsPage(WebDriver driver) {

        // نستدعي Constructor الكلاس الأب
        super(driver);
    }

    /*
     * نحدد العنصر الذي يثبت أن ProductsPage مفتوحة.
     * inventory_container خاص بصفحة المنتجات.
     */
    @Override
    protected By pageMarker() {

        return INVENTORY_CONTAINER;
    }

    // ترجع عدد المنتجات الموجودة في الصفحة
    public int productCount() {

        return elements(PRODUCT_ITEMS).size();
    }

    // ترجع أسماء جميع المنتجات
    public List<String> productNames() {

        return elements(PRODUCT_NAMES)
                .stream()
                .map(element -> element.getText().trim())
                .toList();
    }

    /*
     * ترجع أسعار جميع المنتجات على شكل BigDecimal.
     *
     * PriceUtils تحول مثلًا "$29.99" إلى 29.99.
     */
    public List<BigDecimal> productPrices() {

        return elements(PRODUCT_PRICES)
                .stream()
                .map(WebElement::getText)
                .map(PriceUtils::parse)
                .toList();
    }

    /*
     * تقرأ جميع بيانات المنتجات من الصفحة
     * وتحول كل بطاقة إلى Product Object.
     */
    public List<Product> products() {

        return elements(PRODUCT_ITEMS)
                .stream()
                .map(this::createProductFromCard)
                .toList();
    }

    /*
     * تبحث عن منتج باسمه وترجعه.
     *
     * إذا لم تجده، ترمي ProductNotFoundException
     * وتعرض أسماء المنتجات الموجودة في الصفحة.
     */
    public Product productByName(String productName) {

        return products()
                .stream()
                .filter(product ->
                        product.getName().equals(productName)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                productName,
                                productNames()
                        )
                );
    }

    /*
     * تضيف منتجًا محددًا إلى السلة.
     *
     * نستخدم Locator ديناميكيًا لأن لكل منتج
     * زرًا مختلفًا.
     */
    @Step("Add {0} to the cart")
    public ProductsPage addToCart(String productName) {

        // نتأكد أولًا أن المنتج موجود
        productByName(productName);

        // نحصل على Locator زر المنتج
        By button = productButton(productName);

        // نحرك الصفحة حتى يظهر الزر
        scrollTo(button);

        // نضغط على الزر
        click(button);

        // نرجع الصفحة نفسها
        return this;
    }

    // تزيل منتجًا محددًا من السلة
    @Step("Remove {0} from the cart")
    public ProductsPage removeFromCart(String productName) {

        // نتأكد أولًا أن المنتج موجود
        productByName(productName);

        // نحصل على Locator زر المنتج
        By button = productButton(productName);

        // نحرك الصفحة إلى الزر
        scrollTo(button);

        // نضغط على زر Remove
        click(button);

        // نرجع الصفحة نفسها
        return this;
    }

    /*
     * تقرأ نص زر المنتج.
     *
     * قبل الإضافة تكون القيمة Add to cart.
     * بعد الإضافة تصبح Remove.
     */
    public String productButtonText(String productName) {

        // نتأكد من وجود المنتج
        productByName(productName);

        // نقرأ نص الزر الخاص بالمنتج
        return text(productButton(productName));
    }

    /*
     * ترتب المنتجات حسب القيمة المرسلة.
     *
     * lohi تعني السعر من الأقل إلى الأعلى.
     * za تعني الاسم من Z إلى A.
     */
    @Step("Sort products using {0}")
    public ProductsPage sortBy(String optionValue) {

        // نحصل على عنصر select
        WebElement dropdown =
                waitForVisible(SORT_DROPDOWN);

        // ننشئ Select ونعطيه القيمة المطلوبة
        new Select(dropdown)
                .selectByValue(optionValue);

        // نرجع الصفحة نفسها
        return this;
    }

    /*
     * تفتح صفحة تفاصيل منتج محدد.
     *
     * لأنها عملية انتقال، ترجع ProductDetailsPage.
     */
    @Step("Open the details of {0}")
    public ProductDetailsPage openProductDetails(
            String productName
    ) {

        // نبحث عن بطاقة المنتج حسب الاسم
        WebElement card = productCard(productName);

        // نحصل على عنصر اسم المنتج داخل البطاقة
        WebElement nameElement =
                card.findElement(PRODUCT_NAMES);

        // نحرك الصفحة إلى اسم المنتج
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});",
                        nameElement
                );

        // نضغط على اسم المنتج
        nameElement.click();

        // نرجع صفحة تفاصيل المنتج
        return new ProductDetailsPage(driver);
    }

    // تفتح صفحة السلة
    @Step("Open the shopping cart")
    public CartPage openCart() {

        // نضغط على رابط السلة
        click(CART_LINK);

        // نرجع Page Object الخاصة بالسلة
        return new CartPage(driver);
    }

    /*
     * تحول بطاقة منتج واحدة من WebElement
     * إلى Product Object.
     *
     * الـWebElement يبقى داخل Page Class
     * ولا يخرج إلى Test Class.
     */
    private Product createProductFromCard(
            WebElement productCard
    ) {

        // نقرأ اسم المنتج من البطاقة
        String name = productCard
                .findElement(PRODUCT_NAMES)
                .getText()
                .trim();

        // نقرأ وصف المنتج من البطاقة
        String description = productCard
                .findElement(PRODUCT_DESCRIPTIONS)
                .getText()
                .trim();

        // نقرأ السعر ونحوله إلى BigDecimal
        BigDecimal price = PriceUtils.parse(
                productCard
                        .findElement(PRODUCT_PRICES)
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
     * تبحث عن بطاقة منتج كاملة حسب اسمه.
     *
     * إذا لم تجد المنتج، ترمي Exception واضحة.
     */
    private WebElement productCard(String productName) {

        return elements(PRODUCT_ITEMS)
                .stream()
                .filter(card ->
                        card.findElement(PRODUCT_NAMES)
                                .getText()
                                .trim()
                                .equals(productName)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                productName,
                                productNames()
                        )
                );
    }

    /*
     * تنشئ Locator ديناميكيًا لزر المنتج.
     *
     * نحدد بطاقة المنتج حسب الاسم،
     * ثم نختار الزر الموجود داخل البطاقة نفسها.
     */
    private By productButton(String productName) {

        /*
         * نحدد بطاقة المنتج باستخدام data-test،
         * ثم نتحقق أن البطاقة تحتوي على اسم المنتج المطلوب،
         * وبعد ذلك نختار الزر الموجود داخل البطاقة نفسها.
         */
        return By.xpath(
                "//div[@data-test='inventory-item']"
                        + "[.//div[@data-test='inventory-item-name'"
                        + " and normalize-space()='"
                        + productName
                        + "']]//button"
        );

    }

    // ترجع عدد المنتجات الظ التي تظهر على أيقونة السلة
    public int cartItemCount() {

        // قراءة نص عدّاد السلة وتحويله من String إلى int
        return Integer.parseInt(
                text(CART_BADGE)
        );
    }

    // تتحقق من ظهور عدّاد المنتجات على أيقونة السلة
    public boolean isCartBadgeVisible() {

        return isVisible(CART_BADGE);
    }


}
