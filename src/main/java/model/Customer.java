// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها model
package model;

// نستورد الـException الخاصة بالمشروع
import exceptions.FrameworkException;

// نستورد List لأن العميل يمكن أن يطلب أكثر من منتج
import java.util.List;

/*
 * هذا الكلاس يمثل صفًا واحدًا من ملف customers.csv.
 *
 * كل Customer يحتوي على:
 * الاسم الأول، الاسم الأخير، الرمز البريدي،
 * وقائمة المنتجات التي يريد شراءها.
 */
public final class Customer {

    // استخدمنا final حتى لا تتغير بيانات العميل بعد إنشائه
    private final String firstName;
    private final String lastName;
    private final String postalCode;
    private final List<String> products;

    // هذا الـConstructor يستقبل بيانات العميل كاملة
    public Customer(
            String firstName,
            String lastName,
            String postalCode,
            List<String> products
    ) {

        // نتحقق من أن الاسم الأول ليس null أو فارغًا
        if (firstName == null || firstName.isBlank()) {
            throw new FrameworkException(
                    "Customer first name must not be empty"
            );
        }

        // نتحقق من أن الاسم الأخير ليس null أو فارغًا
        if (lastName == null || lastName.isBlank()) {
            throw new FrameworkException(
                    "Customer last name must not be empty"
            );
        }

        // نتحقق من أن الرمز البريدي ليس null أو فارغًا
        if (postalCode == null || postalCode.isBlank()) {
            throw new FrameworkException(
                    "Customer postal code must not be empty"
            );
        }

        // نتحقق من أن قائمة المنتجات موجودة وليست فارغة
        if (products == null || products.isEmpty()) {
            throw new FrameworkException(
                    "Customer products must not be empty"
            );
        }

        /*
         * نتحقق من أن قائمة المنتجات لا تحتوي
         * على اسم منتج null أو فارغ.
         */
        boolean hasInvalidProduct = products.stream()
                .anyMatch(product ->
                        product == null || product.isBlank()
                );

        // إذا وجدنا اسم منتج غير صالح نظهر خطأ واضحًا
        if (hasInvalidProduct) {
            throw new FrameworkException(
                    "Customer products contain an empty product name"
            );
        }

        // نخزن النصوص بعد إزالة المسافات الزائدة
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.postalCode = postalCode.trim();

        /*
         * ننشئ نسخة مستقلة وغير قابلة للتعديل من القائمة.
         * هذا يمنع تغيير منتجات العميل من خارج الكلاس.
         */
        this.products = List.copyOf(
                products.stream()
                        .map(String::trim)
                        .toList()
        );
    }

    // ترجع الاسم الأول
    public String getFirstName() {
        return firstName;
    }

    // ترجع الاسم الأخير
    public String getLastName() {
        return lastName;
    }

    // ترجع الرمز البريدي
    public String getPostalCode() {
        return postalCode;
    }

    /*
     * ترجع قائمة المنتجات.
     * القائمة غير قابلة للتعديل لأنها أُنشئت باستخدام List.copyOf.
     */
    public List<String> getProducts() {
        return products;
    }

    /*
     * هذه الـMethod تعطي تمثيلًا واضحًا للعميل.
     * ستظهر هذه القيمة داخل تقارير TestNG وAllure
     * عند تشغيل DataProvider الخاص بالعملاء.
     */
    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", products=" + products +
                '}';
    }
}