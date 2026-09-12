// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها tests
package tests;

// نستورد BaseTest للحصول على إعداد وإغلاق المتصفح
import core.BaseTest;

// نستورد Annotations الخاصة بتنظيم تقرير Allure
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutInformationPage;
import pages.ProductsPage;
import model.Customer;
import pages.CheckoutOverviewPage;
import utils.CsvReader;
import config.Config;
import model.CartSummary;

import java.math.BigDecimal;
import pages.CheckoutCompletePage;
/*
 * هذا الكلاس يحتوي على اختبارات عملية Checkout.
 *
 * سنختبر داخله:
 * - التحقق من الحقول المطلوبة
 * - إدخال بيانات العميل
 * - قراءة بيانات العملاء من CSV
 * - الانتقال إلى صفحة المراجعة
 * - التحقق من Item Total والضريبة والإجمالي
 * - إكمال الطلب بنجاح
 */
@Epic("Swag Labs")
@Feature("Checkout")
public class CheckoutTest extends BaseTest {

    /*
     * يوفر حالات بيانات Checkout الناقصة.
     *
     * كل صف يحتوي على:
     * الاسم الأول
     * الاسم الأخير
     * الرمز البريدي
     * رسالة الخطأ المتوقعة
     */
    @DataProvider(name = "missingCheckoutInformation")
    public Object[][] missingCheckoutInformation() {

        return new Object[][]{

                // الاسم الأول مفقود
                {
                        "",
                        "Dameiri",
                        "11118",
                        "First Name is required"
                },

                // الاسم الأخير مفقود
                {
                        "Suhib",
                        "",
                        "11118",
                        "Last Name is required"
                },

                // الرمز البريدي مفقود
                {
                        "Suhib",
                        "Dameiri",
                        "",
                        "Postal Code is required"
                }
        };
    }

    /*
     * يقرأ العملاء من ملف customers.csv.
     *
     * كل Customer سيصبح تشغيلًا مستقلًا للاختبار.
     */
    @DataProvider(name = "customersFromCsv")
    public Object[][] customersFromCsv() {

        return CsvReader
                .readCustomers("testdata/customers.csv")
                .stream()
                .map(customer -> new Object[]{customer})
                .toArray(Object[][]::new);
    }

    @Test(
            dataProvider = "missingCheckoutInformation",
            groups = "regression",
            description = "Checkout rejects missing customer information"
    )
    @Story("Validate required checkout information")
    @Severity(SeverityLevel.CRITICAL)
    public void checkoutRejectsMissingInformation(
            String firstName,
            String lastName,
            String postalCode,
            String expectedError
    ) {

        // اسم المنتج المستخدم للوصول إلى Checkout
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة منتج لأن Checkout يحتاج إلى سلة تحتوي على منتج
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // الانتقال إلى صفحة بيانات العميل
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        /*
         * إرسال البيانات الناقصة القادمة من DataProvider.
         * نتوقع البقاء في صفحة المعلومات وظهور خطأ.
         */
        informationPage.continueExpectingFailure(
                firstName,
                lastName,
                postalCode
        );

        // قراءة رسالة الخطأ الفعلية
        String actualError =
                informationPage.errorMessage();

        // التأكد من ظهور رسالة الخطأ الصحيحة
        Assert.assertTrue(
                actualError.contains(expectedError),
                "Expected error to contain: "
                        + expectedError
                        + ", but the actual error was: "
                        + actualError
        );

        // التأكد من بقاء المستخدم في صفحة معلومات Checkout
        Assert.assertTrue(
                informationPage.currentUrl()
                        .contains("/checkout-step-one.html"),
                "User should remain on the checkout information page"
        );
    }
    @Test(
            dataProvider = "customersFromCsv",
            groups = "regression",
            description = "Customers from CSV can submit valid checkout information"
    )
    @Story("Complete checkout information using CSV data")
    @Severity(SeverityLevel.CRITICAL)
    public void customerFromCsvCanContinueToOverview(
            Customer customer
    ) {

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        /*
         * إضافة جميع المنتجات الخاصة بالعميل.
         * أسماء المنتجات قادمة من عمود products داخل CSV.
         */
        for (String productName : customer.getProducts()) {
            productsPage.addToCart(productName);
        }

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // التأكد من أن عدد منتجات السلة يطابق بيانات العميل
        Assert.assertEquals(
                cartPage.itemCount(),
                customer.getProducts().size(),
                "Cart item count does not match the customer's CSV products"
        );

        // الانتقال إلى صفحة معلومات Checkout
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // إدخال بيانات Customer والانتقال إلى صفحة المراجعة
        CheckoutOverviewPage overviewPage =
                informationPage.continueWith(customer);

        // التأكد من تحميل صفحة مراج უნდა الطلب
        Assert.assertTrue(
                overviewPage.isLoaded(),
                "Checkout overview page was not loaded for customer: "
                        + customer.getFirstName()
        );

        // التأكد من صحة رابط صفحة المراجعة
        Assert.assertTrue(
                overviewPage.currentUrl()
                        .contains("/checkout-step-two.html"),
                "Expected checkout-step-two URL, but found: "
                        + overviewPage.currentUrl()
        );
    }

    @Test(
            groups = "regression",
            description = "Checkout totals match the values calculated by Java"
    )
    @Story("Verify checkout item total tax and final total")
    @Severity(SeverityLevel.CRITICAL)
    public void checkoutTotalsMatchJavaCalculations() {

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة منتجين حتى نختبر عملية الجمع أيضًا
        productsPage.addToCart(
                "Sauce Labs Backpack"
        );

        productsPage.addToCart(
                "Sauce Labs Bike Light"
        );

        // فتح صفحة السلة
        CartPage cartPage = productsPage.openCart();

        // التأكد من وجود منتجين في السلة
        Assert.assertEquals(
                cartPage.itemCount(),
                2,
                "Cart should contain exactly two products"
        );

        // الانتقال إلى صفحة معلومات Checkout
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // إدخال بيانات صحيحة والانتقال إلى صفحة المراجعة
        CheckoutOverviewPage overviewPage =
                informationPage.continueWith(
                        "Suhib",
                        "Dameiri",
                        "11118"
                );

        // التأكد من تحميل صفحة المراجعة
        Assert.assertTrue(
                overviewPage.isLoaded(),
                "Checkout overview page was not loaded"
        );

        // قراءة نسبة الضريبة من config.properties
        BigDecimal taxRate =
                new BigDecimal(
                        Config.get("tax.rate")
                );

        /*
         * إنشاء ملخص للسلة باستخدام المنتجات المقروءة
         * من صفحة المراجعة ونسبة الضريبة من الإعدادات.
         */
        CartSummary expectedSummary =
                new CartSummary(
                        overviewPage.products(),
                        taxRate
                );

        // مقارنة Item Total
        Assert.assertEquals(
                overviewPage.displayedItemTotal(),
                expectedSummary.itemTotal(),
                "Displayed item total does not match the Java calculation"
        );

        // مقارنة قيمة الضريبة
        Assert.assertEquals(
                overviewPage.displayedTax(),
                expectedSummary.tax(),
                "Displayed tax does not match the Java calculation"
        );

        // مقارنة المجموع النهائي
        Assert.assertEquals(
                overviewPage.displayedTotal(),
                expectedSummary.total(),
                "Displayed total does not match the Java calculation"
        );
    }

    @Test(
            groups = "smoke",
            description = "A customer can complete an order successfully"
    )
    @Story("Complete a customer order")
    @Severity(SeverityLevel.BLOCKER)
    public void customerCanCompleteOrderSuccessfully() {

        // اسم المنتج المستخدم في الطلب
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح السلة والانتقال إلى Checkout
        CartPage cartPage = productsPage.openCart();

        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // إدخال بيانات العميل الصحيحة
        CheckoutOverviewPage overviewPage =
                informationPage.continueWith(
                        "Suhib",
                        "Dameiri",
                        "11118"
                );

        // إنهاء الطلب من صفحة المراجعة
        CheckoutCompletePage completePage =
                overviewPage.finishOrder();

        // التأكد من تحميل صفحة إتمام الطلب
        Assert.assertTrue(
                completePage.isLoaded(),
                "Checkout complete page was not loaded"
        );

        // التأكد من صحة رابط صفحة إتمام الطلب
        Assert.assertTrue(
                completePage.currentUrl()
                        .contains("/checkout-complete.html"),
                "Expected checkout-complete URL, but found: "
                        + completePage.currentUrl()
        );

        // التأكد من ظهور رسالة نجاح الطلب الصحيحة
        Assert.assertEquals(
                completePage.confirmationMessage(),
                "Thank you for your order!",
                "The order confirmation message is incorrect"
        );

        // التأكد من وجود النص التوضيحي
        Assert.assertFalse(
                completePage.confirmationDetails().isBlank(),
                "The order confirmation details should not be blank"
        );

        // التأكد من ظهور صورة إتمام الطلب
        Assert.assertTrue(
                completePage.isCompleteImageVisible(),
                "The checkout complete image should be visible"
        );

        // التأكد من أن السلة أصبحت فارغة
        Assert.assertTrue(
                completePage.isCartEmpty(),
                "The cart should be empty after completing the order"
        );
    }

    @Test(
            groups = "regression",
            description = "Back Home returns to products after completing an order"
    )
    @Story("Return home after completing an order")
    @Severity(SeverityLevel.NORMAL)
    public void backHomeReturnsToProductsWithEmptyCart() {

        // اسم المنتج المستخدم في الطلب
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح السلة
        CartPage cartPage =
                productsPage.openCart();

        // الانتقال إلى صفحة بيانات Checkout
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // إدخال بيانات العميل الصحيحة
        CheckoutOverviewPage overviewPage =
                informationPage.continueWith(
                        "Suhib",
                        "Dameiri",
                        "11118"
                );

        // إنهاء الطلب
        CheckoutCompletePage completePage =
                overviewPage.finishOrder();

        // التأكد من نجاح الطلب قبل الضغط على Back Home
        Assert.assertEquals(
                completePage.confirmationMessage(),
                "Thank you for your order!",
                "The order was not completed successfully"
        );

        // الضغط على زر Back Home
        ProductsPage returnedProductsPage =
                completePage.backHome();

        // التأكد من تحميل صفحة المنتجات
        Assert.assertTrue(
                returnedProductsPage.isLoaded(),
                "Products page was not loaded after clicking Back Home"
        );

        // التأكد من العودة إلى رابط صفحة المنتجات
        Assert.assertTrue(
                returnedProductsPage.currentUrl()
                        .contains("/inventory.html"),
                "Expected inventory URL after clicking Back Home, but found: "
                        + returnedProductsPage.currentUrl()
        );

        // التأكد من بقاء السلة فارغة بعد العودة
        Assert.assertFalse(
                returnedProductsPage.isCartBadgeVisible(),
                "Cart badge should not appear after completing the order"
        );
    }

    @Test(
            groups = "regression",
            description = "Cancelling checkout information returns to the cart"
    )
    @Story("Cancel checkout from the customer information page")
    @Severity(SeverityLevel.NORMAL)
    public void cancellingCheckoutInformationReturnsToCart() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage =
                productsPage.openCart();

        // الانتقال إلى صفحة معلومات Checkout
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // التأكد من تحميل صفحة معلومات Checkout
        Assert.assertTrue(
                informationPage.isLoaded(),
                "Checkout information page was not loaded"
        );

        // إلغاء Checkout والعودة إلى السلة
        CartPage returnedCartPage =
                informationPage.cancel();

        // التأكد من تحميل صفحة السلة
        Assert.assertTrue(
                returnedCartPage.isLoaded(),
                "Cart page was not loaded after cancelling checkout"
        );

        // التأكد من العودة إلى رابط صفحة السلة
        Assert.assertTrue(
                returnedCartPage.currentUrl()
                        .contains("/cart.html"),
                "Expected cart URL after cancelling checkout, but found: "
                        + returnedCartPage.currentUrl()
        );

        // التأكد من بقاء المنتج داخل السلة
        Assert.assertTrue(
                returnedCartPage.containsProduct(productName),
                "The product should remain in the cart after cancelling checkout"
        );

        // التأكد من أن عدد المنتجات ما زال يساوي 1
        Assert.assertEquals(
                returnedCartPage.itemCount(),
                1,
                "Cart should still contain one product after cancelling checkout"
        );
    }

    @Test(
            groups = "regression",
            description = "Cancelling the order from overview returns to products"
    )
    @Story("Cancel an order from the checkout overview")
    @Severity(SeverityLevel.NORMAL)
    public void cancellingOrderFromOverviewReturnsToProducts() {

        // اسم المنتج المستخدم في الاختبار
        String productName = "Sauce Labs Backpack";

        // تسجيل الدخول والانتقال إلى صفحة المنتجات
        ProductsPage productsPage = signIn();

        // إضافة المنتج إلى السلة
        productsPage.addToCart(productName);

        // فتح صفحة السلة
        CartPage cartPage =
                productsPage.openCart();

        // الانتقال إلى صفحة معلومات Checkout
        CheckoutInformationPage informationPage =
                cartPage.checkout();

        // إدخال بيانات العميل والانتقال إلى صفحة المراجعة
        CheckoutOverviewPage overviewPage =
                informationPage.continueWith(
                        "Suhib",
                        "Dameiri",
                        "11118"
                );

        // التأكد من تحميل صفحة مراجعة الطلب
        Assert.assertTrue(
                overviewPage.isLoaded(),
                "Checkout overview page was not loaded"
        );

        // إلغاء الطلب والعودة إلى صفحة المنتجات
        ProductsPage returnedProductsPage =
                overviewPage.cancelOrder();

        // التأكد من تحميل صفحة المنتجات
        Assert.assertTrue(
                returnedProductsPage.isLoaded(),
                "Products page was not loaded after cancelling the order"
        );

        // التأكد من العودة إلى رابط صفحة المنتجات
        Assert.assertTrue(
                returnedProductsPage.currentUrl()
                        .contains("/inventory.html"),
                "Expected inventory URL after cancellation, but found: "
                        + returnedProductsPage.currentUrl()
        );

        // التأكد من بقاء المنتج داخل السلة
        Assert.assertEquals(
                returnedProductsPage.productButtonText(productName),
                "Remove",
                "The product should remain in the cart after cancelling the order"
        );

        // التأكد من أن عدّاد السلة ما زال يعرض الرقم 1
        Assert.assertEquals(
                returnedProductsPage.cartItemCount(),
                1,
                "Cart badge should remain 1 after cancelling the order"
        );
    }

}