// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها utils
package utils;

// نستورد الـException الخاصة بالمشروع
import exceptions.FrameworkException;

// نستورد BigDecimal للتعامل مع القيم المالية بدقة
import java.math.BigDecimal;

// نستورد RoundingMode لتحديد طريقة تقريب الأرقام
import java.math.RoundingMode;

/*
 * هذا الكلاس يحتوي على أدوات مساعدة لمعالجة الأسعار.
 *
 * جعلناه final لأننا لا نحتاج إلى وراثته.
 */
public final class PriceUtils {

    /*
     * جعلنا الـConstructor من نوع private لمنع إنشاء Object.
     * جميع الـMethods الموجودة في هذا الكلاس static.
     */
    private PriceUtils() {
    }

    /*
     * هذه الـMethod تحول السعر المكتوب في الموقع
     * مثل "$29.99" إلى BigDecimal قيمته 29.99.
     */
    public static BigDecimal parse(String priceText) {

        // نتحقق من أن النص ليس null أو فارغًا
        if (priceText == null || priceText.isBlank()) {
            throw new FrameworkException(
                    "Price text must not be empty"
            );
        }

        /*
         * نحذف علامة الدولار والمسافات والفواصل
         * حتى يبقى الرقم فقط.
         *
         * مثال:
         * "$1,299.99" تصبح "1299.99".
         */
        String cleanedPrice = priceText
                .replace("$", "")
                .replace(",", "")
                .trim();

        try {
            // نحول النص بعد تنظيفه إلى BigDecimal
            return new BigDecimal(cleanedPrice);

        } catch (NumberFormatException exception) {

            // إذا لم يكن النص سعرًا صحيحًا نظهر رسالة واضحة
            throw new FrameworkException(
                    "Could not parse price: " + priceText,
                    exception
            );
        }
    }

    /*
     * هذه الـMethod تقرب القيمة إلى منزلتين عشريتين
     * باستخدام HALF_UP.
     *
     * مثال:
     * 3.1984 تصبح 3.20.
     */
    public static BigDecimal round(BigDecimal value) {

        // نتحقق من أن القيمة ليست null
        if (value == null) {
            throw new FrameworkException(
                    "Price value must not be null"
            );
        }

        // نقرب القيمة إلى منزلتين عشريتين
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}