// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها tests
package tests;

// نستورد Config لقراءة المستخدمين وكلمة المرور
import config.Config;

// نستورد BaseTest حتى يرث LoginTest إعداد وإغلاق المتصفح
import core.BaseTest;

// نستورد DataProvider من TestNG
import org.testng.annotations.DataProvider;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductsPage;

import core.DriverFactory;
import pages.LoginPage;
/*
 * هذا الكلاس سيحتوي على اختبارات صفحة تسجيل الدخول.
 *
 * سنبدأ بإنشاء DataProvider ثم نضيف Test Methods
 * في الخطوات التالية.
 */

@Epic("Swag Labs")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(
            groups = "smoke",
            description = "Valid user reaches the products page"
    )
    @Story("Successful login using a valid user")
    @Severity(SeverityLevel.BLOCKER)
    public void standardUserCanLogIn() {

        // تسجيل الدخول بالبيانات الصحيحة الموجودة في ملف الإعدادات
        ProductsPage productsPage = signIn();

        // فشل مؤقت للتأكد من التقاط Screenshot وإرفاقها في Allure
        //Assert.fail("Temporary failure for screenshot verification");

        // التأكد من تحميل صفحة المنتجات
        Assert.assertTrue(
                productsPage.isLoaded(),
                "Products page was not loaded after login"
        );

        // التأكد من انتقال المستخدم إلى رابط صفحة المنتجات
        Assert.assertTrue(
                productsPage.currentUrl().contains("/inventory.html"),
                "Expected URL to contain /inventory.html, but the current URL is: "
                        + productsPage.currentUrl()
        );

        // التأكد من صحة عنوان الصفحة
        Assert.assertEquals(
                productsPage.heading(),
                "Products",
                "The products page heading is incorrect"
        );
    }



    @Test(
            dataProvider = "rejectedLogins",
            groups = "regression",
            description = "Rejected users receive the correct login error"
    )
    @Story("Reject login attempts with invalid credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void rejectedUserCannotLogIn(
            String username,
            String password,
            String expectedError
    ) {

        // إنشاء Page Object لصفحة تسجيل الدخول
// إنشاء صفحة تسجيل الدخول ثم فتح موقع SauceDemo
        LoginPage loginPage =
                new LoginPage(DriverFactory.get())
                        .open();

        // إرسال بيانات تسجيل الدخول الحالية القادمة من DataProvider
        loginPage.loginExpectingFailure(username, password);

        // قراءة رسالة الخطأ التي ظهرت في الصفحة
        String actualError = loginPage.errorMessage();

        // التأكد من احتواء الرسالة الفعلية على النص المتوقع
        Assert.assertTrue(
                actualError.contains(expectedError),
                "Expected error to contain: "
                        + expectedError
                        + ", but the actual error was: "
                        + actualError
        );
    }



    @Test(
            groups = "smoke",
            description = "The login page displays all essential elements"
    )
    @Story("Display the essential login page elements")
    @Severity(SeverityLevel.NORMAL)
    public void loginPageDisplaysEssentialElements() {

        // إنشاء صفحة تسجيل الدخول وفتح موقع SauceDemo
        LoginPage loginPage =
                new LoginPage(DriverFactory.get())
                        .open();

        // التأكد من ظهور شعار Swag Labs
        Assert.assertTrue(
                loginPage.isLogoVisible(),
                "The Swag Labs logo is not visible"
        );

        // التأكد من ظهور حقل اسم المستخدم
        Assert.assertTrue(
                loginPage.isUsernameVisible(),
                "The username field is not visible"
        );

        // التأكد من ظهور حقل كلمة المرور
        Assert.assertTrue(
                loginPage.isPasswordVisible(),
                "The password field is not visible"
        );

        // التأكد من ظهور زر تسجيل الدخول
        Assert.assertTrue(
                loginPage.isLoginButtonVisible(),
                "The login button is not visible"
        );
    }
    /*
     * هذا الـDataProvider يحتوي على خمس حالات
     * يجب أن يرفض الموقع تسجيل الدخول فيها.
     *
     * كل صف يحتوي على:
     * Username
     * Password
     * Expected Error Message
     */
    @DataProvider(name = "rejectedLogins")
    public Object[][] rejectedLogins() {

        return new Object[][]{

                /*
                 * الحالة الأولى:
                 * المستخدم المقفل مع كلمة المرور الصحيحة.
                 */
                {
                        Config.get("locked.user"),
                        Config.get("password"),
                        "Sorry, this user has been locked out."
                },

                /*
                 * الحالة الثانية:
                 * مستخدم صحيح مع كلمة مرور خاطئة.
                 */
                {
                        Config.get("standard.user"),
                        "wrong_password",
                        "Username and password do not match"
                },

                /*
                 * الحالة الثالثة:
                 * اسم مستخدم غير موجود مع كلمة مرور صحيحة.
                 */
                {
                        "no_such_user",
                        Config.get("password"),
                        "Username and password do not match"
                },

                /*
                 * الحالة الرابعة:
                 * اسم المستخدم فارغ.
                 */
                {
                        "",
                        Config.get("password"),
                        "Username is required"
                },

                /*
                 * الحالة الخامسة:
                 * كلمة المرور فارغة.
                 */
                {
                        Config.get("standard.user"),
                        "",
                        "Password is required"
                }
        };
    }
}