// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها model
package model;

// نستورد الـException الخاصة بالمشروع
import exceptions.FrameworkException;

// نستورد BigDecimal للتعامل مع أسعار المنتجات بدقة
import java.math.BigDecimal;

// نستورد Objects لاستخدامها في equals وhashCode
import java.util.Objects;

/*
 * هذا الكلاس يمثل منتجًا واحدًا في موقع Swag Labs.
 *
 * طبقنا Comparable<Product> حتى نستطيع مقارنة المنتجات
 * وترتيبها حسب السعر.
 */
public final class Product implements Comparable<Product> {

    /*
     * استخدمنا final مع جميع المتغيرات حتى يكون الكلاس Immutable.
     * هذا يعني أن بيانات المنتج لا تتغير بعد إنشاء الـObject.
     */
    private final String name;
    private final String description;
    private final BigDecimal price;

    // هذا الـConstructor يستقبل بيانات المنتج كاملة
    public Product(
            String name,
            String description,
            BigDecimal price
    ) {

        // نتحقق من أن اسم المنتج ليس null أو فارغًا
        if (name == null || name.isBlank()) {
            throw new FrameworkException(
                    "Product name must not be empty"
            );
        }

        // نتحقق من أن وصف المنتج ليس null أو فارغًا
        if (description == null || description.isBlank()) {
            throw new FrameworkException(
                    "Product description must not be empty"
            );
        }

        // نتحقق من أن السعر ليس null
        if (price == null) {
            throw new FrameworkException(
                    "Product price must not be null"
            );
        }

        // نتحقق من أن السعر ليس سالبًا
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new FrameworkException(
                    "Product price must not be negative: " + price
            );
        }

        /*
         * نخزن القيم بعد إزالة المسافات الزائدة
         * من بداية النصوص ونهايتها.
         */
        this.name = name.trim();
        this.description = description.trim();
        this.price = price;
    }

    // ترجع اسم المنتج
    public String getName() {
        return name;
    }

    // ترجع وصف المنتج
    public String getDescription() {
        return description;
    }

    // ترجع سعر المنتج
    public BigDecimal getPrice() {
        return price;
    }

    /*
     * هذه الـMethod تقارن المنتج الحالي بمنتج آخر حسب السعر.
     *
     * قيمة سالبة تعني أن المنتج الحالي أرخص.
     * صفر يعني أن السعرين متساويان.
     * قيمة موجبة تعني أن المنتج الحالي أغلى.
     */
    @Override
    public int compareTo(Product otherProduct) {

        // نمنع مقارنة المنتج مع null
        if (otherProduct == null) {
            throw new FrameworkException(
                    "Cannot compare a product with null"
            );
        }

        // نقارن السعرين باستخدام BigDecimal
        return this.price.compareTo(otherProduct.price);
    }

    /*
     * هذه الـMethod تحدد متى نعتبر Product مساويًا لـProduct آخر.
     * المنتجين متساويان عندما تتساوى جميع بياناتهما.
     */
    @Override
    public boolean equals(Object object) {

        // إذا كان المرجعان يشيران إلى نفس الـObject فهما متساويان
        if (this == object) {
            return true;
        }

        // إذا كان الـObject فارغًا أو من كلاس مختلف فهما غير متساويين
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        // نحول الـObject إلى Product حتى نقارن خصائصه
        Product product = (Product) object;

        /*
         * نستخدم compareTo مع BigDecimal حتى نعتبر
         * 29.9 و29.90 متساويين من ناحية القيمة المالية.
         */
        return name.equals(product.name)
                && description.equals(product.description)
                && price.compareTo(product.price) == 0;
    }

    /*
     * عندما نعمل Override لـequals يجب أن نعمل Override
     * لـhashCode حتى يعمل الكلاس بصورة صحيحة داخل
     * HashSet وHashMap.
     */
    @Override
    public int hashCode() {

        /*
         * stripTrailingZeros يوحد شكل السعر قبل حساب HashCode.
         * مثلًا 29.9 و29.90 يحصلان على القيمة نفسها.
         */
        return Objects.hash(
                name,
                description,
                price.stripTrailingZeros()
        );
    }

    /*
     * هذه الـMethod تعطي تمثيلًا واضحًا للمنتج عند طباعته
     * أو ظهوره داخل رسالة خطأ أو تقرير.
     */
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}