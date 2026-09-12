// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها tests
package tests;

// نستورد BaseTest لاستخدام إعداد المتصفح وتسجيل الدخول
import core.BaseTest;

// نستورد Annotations الخاصة بتقرير Allure
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.Product;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.ProductsPage;
import pages.CheckoutInformationPage;

/*
 * هذا الكلاس يحتوي على اختبارات سلة التسوق.
 *
 * سنختبر داخله:
 * - فتح السلة
 * - ظهور المنتجات المضافة
 * - بيانات المنتج داخل السلة
 * - إزالة منتج
 * - متابعة التسوق
 * - الانتقال إلى Checkout
 */
@Epic("Swag Labs")
@Feature("Shopping Cart")
public class CartTest extends BaseTest {

    @Test(
            groups = "smoke",
            description = "An added product appears in the cart with correct data"
    )
    @Story("View an added product in the shopping cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addedProductAppearsInCartWithCorrectData() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // حفظ بيانات المنتج من صفحة المنتجات قبل إضافته
        Product expectedProduct =
                productsPage.productByName(productName);

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // التأكد من تحميل صفحة السلة
        Assert.assertTrue(
                cartPage.isLoaded(),
                "Cart page was not loaded"
        );

        // التأكد من وجود منتج واحد في السلة
        Assert.assertEquals(
                cartPage.itemCount(),
                1,
                "Cart should contain exactly one product"
        );

        // التأكد من وجود المنتج المطلوب داخل السلة
        Assert.assertTrue(
                cartPage.containsProduct(productName),
                "Cart does not contain the expected product: "
                        + productName
        );

         // التأكد من أن كمية المنتج تساوي 1
        Assert.assertEquals(
                cartPage.quantityOf(productName),
                1,
                "Product quantity should be 1"
        );

        // قراءة بيانات المنتج من السلة
        Product actualProduct =
                cartPage.products().get(0);

        // مقارنة اسم المنتج
        Assert.assertEquals(
                actualProduct.getName(),
                expectedProduct.getName(),
                "Product name in the cart is incorrect"
        );

        // مقارنة وصف المنتج
        Assert.assertEquals(
                actualProduct.getDescription(),
                expectedProduct.getDescription(),
                "Product description in the cart is incorrect"
        );

        // مقارنة سعر المنتج
        Assert.assertEquals(
                actualProduct.getPrice(),
                expectedProduct.getPrice(),
                "Product price in the cart is incorrect"
        );
    }

    @Test(
            groups = "regression",
            description = "Removing the only product leaves the cart empty"
    )
    @Story("Remove a product from the shopping cart")
    @Severity(SeverityLevel.CRITICAL)
    public void removingOnlyProductLeavesCartEmpty() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // التأكد من وجود المنتج قبل إزالته
        Assert.assertTrue(
                cartPage.containsProduct(productName),
                "The product should exist in the cart before removal"
        );

        // إزالة المنتج من داخل صفحة السلة
        cartPage.removeItem(productName);

        // التأكد من أن السلة أصبحت فارغة
        Assert.assertEquals(
                cartPage.itemCount(),
                0,
                "Cart should be empty after removing its only product"
        );

        // التأكد من أن اسم المنتج لم يعد موجودًا
        Assert.assertFalse(
                cartPage.containsProduct(productName),
                "The removed product should not remain in the cart"
        );
    }

    @Test(
            groups = "regression",
            description = "Continue Shopping returns the user to the products page"
    )
    @Story("Continue shopping from the cart")
    @Severity(SeverityLevel.NORMAL)
    public void continueShoppingReturnsToProductsPage() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // التأكد من تحميل صفحة السلة
        Assert.assertTrue(
                cartPage.isLoaded(),
                "Cart page was not loaded"
        );

        // الضغط على زر Continue Shopping
        ProductsPage returnedProductsPage =
                cartPage.continueShopping();

        // التأكد من العودة إلى صفحة المنتجات
        Assert.assertTrue(
                returnedProductsPage.isLoaded(),
                "Products page was not loaded after clicking Continue Shopping"
        );

        // التأكد من أن الرابط عاد إلى صفحة inventory
        Assert.assertTrue(
                returnedProductsPage.currentUrl()
                        .contains("/inventory.html"),
                "Expected inventory URL after continuing shopping, but found: "
                        + returnedProductsPage.currentUrl()
        );

        /*
         * التأكد من بقاء المنتج في السلة بعد العودة.
         * ظهور Remove يعني أن حالة المنتج لم تُفقد.
         */
        Assert.assertEquals(
                returnedProductsPage.productButtonText(productName),
                "Remove",
                "The added product should remain in the cart after returning"
        );
    }

    @Test(
            groups = "smoke",
            description = "Checkout button opens the customer information page"
    )
    @Story("Start checkout from the shopping cart")
    @Severity(SeverityLevel.CRITICAL)
    public void checkoutButtonOpensInformationPage() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // التأكد من وجود المنتج قبل بدء Checkout
        Assert.assertTrue(
                cartPage.containsProduct(productName),
                "The product should exist in the cart before checkout"
        );

        // الضغط على زر Checkout
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // التأكد من تحميل صفحة معلومات العميل
        Assert.assertTrue(
                informationPage.isLoaded(),
                "Checkout information page was not loaded"
        );

        // التأكد من صحة رابط الصفحة
        Assert.assertTrue(
                informationPage.currentUrl()
                        .contains("/checkout-step-one.html"),
                "Expected checkout-step-one URL, but found: "
                        + informationPage.currentUrl()
        );
    }


}