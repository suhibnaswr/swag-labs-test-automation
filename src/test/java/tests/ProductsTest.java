// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها tests
package tests;

// نستورد BaseTest حتى نستخدم إعداد وإغلاق المتصفح وsignIn
import core.BaseTest;

// نستورد Epic وFeature لتنظيم الاختبارات داخل Allure Report
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.Product;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductsPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import pages.ProductDetailsPage;
/*
 * هذا الكلاس يحتوي على اختبارات صفحة المنتجات.
 *
 * يرث من BaseTest حتى يحصل على:
 * - إنشاء WebDriver قبل كل اختبار
 * - إغلاق WebDriver بعد كل اختبار
 * - Method تسجيل الدخول signIn
 */
@Epic("Swag Labs")
@Feature("Products")
public class ProductsTest extends BaseTest {

    /*
     * سنضيف داخل هذا الكلاس اختبارات:
     * - عرض جميع المنتجات
     * - ترتيب المنتجات
     * - إضافة منتج إلى السلة
     * - تحديث عداد السلة
     * - فتح تفاصيل المنتج
     */

    @Test(
            groups = "smoke",
            description = "Products page displays six products with valid data"
    )
    @Story("Display all available products")
    @Severity(SeverityLevel.CRITICAL)
    public void productsPageDisplaysSixValidProducts() {

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // التأكد من تحميل صفحة المنتجات
        Assert.assertTrue(
                productsPage.isLoaded(),
                "Products page was not loaded after login"
        );

        // التأكد من أن الصفحة تحتوي على ستة منتجات
        Assert.assertEquals(
                productsPage.productCount(),
                6,
                "Expected exactly 6 products on the products page"
        );

        // قراءة المنتجات وتحويلها إلى Product Objects
        List<Product> products = productsPage.products();

        // التأكد من أن كل منتج يحتوي على بيانات صحيحة
        for (Product product : products) {

            // التأكد من أن اسم المنتج ليس فارغًا
            Assert.assertFalse(
                    product.getName().isBlank(),
                    "Product name must not be blank"
            );

            // التأكد من أن وصف المنتج ليس فارغًا
            Assert.assertFalse(
                    product.getDescription().isBlank(),
                    "Description must not be blank for product: "
                            + product.getName()
            );

            // التأكد من أن سعر المنتج أكبر من صفر
            Assert.assertTrue(
                    product.getPrice().compareTo(BigDecimal.ZERO) > 0,
                    "Price must be greater than zero for product: "
                            + product.getName()
            );
        }
    }

    @Test(
            groups = "regression",
            description = "Products can be sorted by name from Z to A"
    )
    @Story("Sort products by name descending")
    @Severity(SeverityLevel.NORMAL)
    public void productsCanBeSortedFromZToA() {

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // اختيار ترتيب أسماء المنتجات من Z إلى A
        productsPage.sortBy("za");

        // قراءة ترتيب الأسماء الفعلي من الموقع
        List<String> actualNames =
                productsPage.productNames();

        /*
         * إنشاء نسخة مستقلة من الأسماء.
         * يجب ألا نرتب actualNames نفسها حتى تبقى
         * محتفظة بالترتيب الذي عرضه الموقع.
         */
        List<String> expectedNames =
                new ArrayList<>(actualNames);

        // ترتيب النسخة باستخدام Java من Z إلى A
        expectedNames.sort(
                Comparator.reverseOrder()
        );

        // مقارنة ترتيب الموقع بالترتيب الصحيح المحسوب في Java
        Assert.assertEquals(
                actualNames,
                expectedNames,
                "Products were not sorted correctly from Z to A"
        );
    }

    @Test(
            groups = "regression",
            description = "Products can be sorted by price from low to high"
    )
    @Story("Sort products by price ascending")
    @Severity(SeverityLevel.NORMAL)
    public void productsCanBeSortedByPriceLowToHigh() {

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        /*
         * اختيار ترتيب الأسعار من الأقل إلى الأعلى.
         * القيمة lohi هي value الموجودة داخل خيار Select.
         */
        productsPage.sortBy("lohi");

        // قراءة ترتيب الأسعار الفعلي من الموقع
        List<BigDecimal> actualPrices =
                productsPage.productPrices();

        /*
         * إنشاء نسخة مستقلة حتى نرتبها باستخدام Java
         * دون تعديل القائمة التي تمثل ترتيب الموقع.
         */
        List<BigDecimal> expectedPrices =
                new ArrayList<>(actualPrices);

        // ترتيب الأسعار المتوقعة من الأقل إلى الأعلى
        expectedPrices.sort(
                Comparator.naturalOrder()
        );

        // مقارنة ترتيب الموقع بالترتيب المحسوب في Java
        Assert.assertEquals(
                actualPrices,
                expectedPrices,
                "Products were not sorted correctly by price from low to high"
        );
    }

    @Test(
            groups = "smoke",
            description = "Adding a product updates its button and the cart badge"
    )
    @Story("Add a product to the shopping cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addingProductUpdatesButtonAndCartBadge() {

        // اسم المنتج الذي سنضيفه إلى السلة
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // التأكد من أن نص الزر قبل الإضافة هو Add to cart
        Assert.assertEquals(
                productsPage.productButtonText(productName),
                "Add to cart",
                "Product button should display Add to cart before adding the product"
        );

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // التأكد من تحول نص الزر إلى Remove
        Assert.assertEquals(
                productsPage.productButtonText(productName),
                "Remove",
                "Product button should display Remove after adding the product"
        );

        // التأكد من ظهور الرقم 1 على أيقونة السلة
        Assert.assertEquals(
                productsPage.cartItemCount(),
                1,
                "Cart badge should display 1 after adding one product"
        );
    }

    @Test(
            groups = "regression",
            description = "Removing a product resets its button and removes the cart badge"
    )
    @Story("Remove a product from the shopping cart")
    @Severity(SeverityLevel.CRITICAL)
    public void removingProductResetsButtonAndCartBadge() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج أولًا إلى السلة
        productsPage.addToCart(productName);

        // التأكد من أن عدّاد السلة أصبح 1 بعد الإضافة
        Assert.assertEquals(
                productsPage.cartItemCount(),
                1,
                "Cart badge should display 1 after adding the product"
        );

        // إزالة المنتج من السلة
        productsPage.removeFromCart(productName);

        // التأكد من رجوع نص الزر إلى Add to cart
        Assert.assertEquals(
                productsPage.productButtonText(productName),
                "Add to cart",
                "Product button should display Add to cart after removing the product"
        );

        // التأكد من اختفاء عدّاد السلة بعد إزالة المنتج
        Assert.assertFalse(
                productsPage.isCartBadgeVisible(),
                "Cart badge should disappear after removing the only product"
        );
    }

    @Test(
            groups = "regression",
            description = "Product details match the data displayed on the products page"
    )
    @Story("Open and verify product details")
    @Severity(SeverityLevel.CRITICAL)
    public void productDetailsMatchProductsPageData() {

        // اسم المنتج الذي سنفتح صفحة تفاصيله
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        /*
         * قراءة بيانات المنتج من صفحة المنتجات
         * قبل الانتقال إلى صفحة التفاصيل.
         */
        Product expectedProduct =
                productsPage.productByName(productName);

        // فتح صفحة تفاصيل المنتج
        ProductDetailsPage detailsPage =
                productsPage.openProductDetails(productName);

        // التأكد من تحميل صفحة تفاصيل المنتج
        Assert.assertTrue(
                detailsPage.isLoaded(),
                "Product details page was not loaded"
        );

        // قراءة بيانات المنتج من صفحة التفاصيل
        Product actualProduct =
                detailsPage.product();

        // مقارنة اسم المنتج
        Assert.assertEquals(
                actualProduct.getName(),
                expectedProduct.getName(),
                "Product name on the details page does not match"
        );

        // مقارنة وصف المنتج
        Assert.assertEquals(
                actualProduct.getDescription(),
                expectedProduct.getDescription(),
                "Product description on the details page does not match"
        );

        // مقارنة سعر المنتج
        Assert.assertEquals(
                actualProduct.getPrice(),
                expectedProduct.getPrice(),
                "Product price on the details page does not match"
        );
    }

    @Test(
            groups = "regression",
            description = "Adding a product from details persists after returning to products"
    )
    @Story("Add a product from its details page")
    @Severity(SeverityLevel.CRITICAL)
    public void productCanBeAddedFromDetailsPage() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Bike Light";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // فتح صفحة تفاصيل المنتج
        ProductDetailsPage detailsPage =
                productsPage.openProductDetails(productName);

        // التأكد من أن الزر قبل الإضافة يعرض Add to cart
        Assert.assertEquals(
                detailsPage.productButtonText(),
                "Add to cart",
                "Details button should display Add to cart before adding the product"
        );

        // إضافة المنتج إلى السلة من صفحة التفاصيل
        detailsPage.addToCart();

        // التأكد من تحول نص الزر إلى Remove
        Assert.assertEquals(
                detailsPage.productButtonText(),
                "Remove",
                "Details button should display Remove after adding the product"
        );

        // العودة إلى صفحة المنتجات
        productsPage = detailsPage.backToProducts();

        // التأكد من تحميل صفحة المنتجات مرة أخرى
        Assert.assertTrue(
                productsPage.isLoaded(),
                "Products page was not loaded after returning from details"
        );

        /*
         * التأكد من بقاء المنتج داخل السلة؛
         * لذلك يجب أن يعرض زره Remove في صفحة المنتجات.
         */
        Assert.assertEquals(
                productsPage.productButtonText(productName),
                "Remove",
                "Product should remain in the cart after returning to the products page"
        );

        // التأكد من ظهور منتج واحد في عدّاد السلة
        Assert.assertEquals(
                productsPage.cartItemCount(),
                1,
                "Cart badge should display 1 after adding the product from details"
        );
    }


}