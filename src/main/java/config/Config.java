// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها config
package config;

// نستورد الـException الخاصة بالمشروع
import exceptions.FrameworkException;

// نستورد IOException للتعامل مع أخطاء قراءة الملف
import java.io.IOException;

// نستورد InputStream لقراءة ملف config.properties
import java.io.InputStream;

// نستورد BigDecimal لأن نسبة الضريبة قيمة عشرية مالية
import java.math.BigDecimal;

// نستورد Duration لتمثيل مدة الانتظار بالثواني
import java.time.Duration;

// نستورد Properties للتعامل مع ملف config.properties
import java.util.Properties;

// هذا الكلاس مسؤول عن قراءة إعدادات المشروع
public final class Config {

    // نخزن جميع القيم الموجودة في ملف config.properties
    // ويتم تحميل الملف مرة واحدة فقط عند تشغيل المشروع
    private static final Properties PROPERTIES = load();

    // جعلنا الـConstructor من نوع private
    // حتى نمنع إنشاء Object من هذا الكلاس
    // لأننا سنستخدم جميع الـMethods بطريقة static
    private Config() {
    }

    // هذه الـMethod تقرأ ملف config.properties من Resources
    private static Properties load() {

        // ننشئ Object من Properties لتخزين الإعدادات
        Properties properties = new Properties();

        // نفتح الملف باستخدام ClassLoader لأنه موجود داخل Resources
        // try-with-resources يغلق InputStream تلقائيًا بعد الانتهاء
        try (InputStream inputStream =
                     Config.class.getClassLoader()
                             .getResourceAsStream("config.properties")) {

            // إذا لم يتم العثور على الملف، نرمي Exception واضحة
            if (inputStream == null) {
                throw new FrameworkException(
                        "config.properties was not found on the classpath"
                );
            }

            // نقرأ القيم الموجودة في الملف ونضعها داخل Properties
            properties.load(inputStream);

            // نرجع الإعدادات بعد قراءتها
            return properties;

        } catch (IOException exception) {

            // إذا حدث خطأ أثناء قراءة الملف، نغلف الخطأ داخل
            // الـException الخاصة بالمشروع بدل إخفائه
            throw new FrameworkException(
                    "Could not read config.properties",
                    exception
            );
        }
    }

    // هذه الـMethod ترجع قيمة إعداد باستخدام اسمه
    public static String get(String key) {

        // نحاول أولًا قراءة القيمة من System Property
        // حتى يستطيع Maven أو Jenkins تغيير الإعداد وقت التشغيل
        String systemValue = System.getProperty(key);

        // إذا كانت System Property موجودة وغير فارغة نستخدمها
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        // إذا لم توجد System Property نقرأ القيمة من الملف
        String fileValue = PROPERTIES.getProperty(key);

        // إذا لم نجد المفتاح داخل الملف نرمي خطأ واضحًا
        if (fileValue == null || fileValue.isBlank()) {
            throw new FrameworkException(
                    "Missing configuration key: " + key
            );
        }

        // نرجع القيمة التي قرأناها من الملف
        return fileValue;
    }

    // ترجع رابط الموقع
    public static String baseUrl() {
        return get("base.url");
    }

    // ترجع اسم المتصفح
    public static String browser() {
        return get("browser");
    }

    // تحدد هل يعمل المتصفح بوضع Headless
    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    // تحدد هل نأخذ Screenshot عند نجاح الاختبار
    public static boolean screenshotOnSuccess() {
        return Boolean.parseBoolean(get("screenshot.on.success"));
    }

    // ترجع وقت الانتظار على شكل Duration
    public static Duration timeout() {

        try {
            // نحول القيمة النصية إلى رقم ثم إلى مدة بالثواني
            return Duration.ofSeconds(
                    Long.parseLong(get("timeout"))
            );

        } catch (NumberFormatException exception) {

            // إذا لم تكن القيمة رقمًا صحيحًا نظهر رسالة واضحة
            throw new FrameworkException(
                    "The timeout value must be a valid whole number",
                    exception
            );
        }
    }

    // ترجع نسبة الضريبة على شكل BigDecimal
    public static BigDecimal taxRate() {

        try {
            // نحول نسبة الضريبة النصية إلى BigDecimal
            return new BigDecimal(get("tax.rate"));

        } catch (NumberFormatException exception) {

            // إذا كانت نسبة الضريبة غير صحيحة نظهر رسالة واضحة
            throw new FrameworkException(
                    "The tax.rate value must be a valid decimal number",
                    exception
            );
        }
    }
}