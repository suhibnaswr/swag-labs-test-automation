// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها model
package model;

// نستورد الـException الخاصة بالمشروع
import exceptions.FrameworkException;

// نستورد PriceUtils لاستخدام التقريب المالي
import utils.PriceUtils;

// نستورد BigDecimal للتعامل مع المبالغ المالية بدقة
import java.math.BigDecimal;

// نستورد List لأن السلة تحتوي على قائمة منتجات
import java.util.List;

/*
 * هذا الكلاس مسؤول عن حساب ملخص سلة المشتريات باستخدام Java.
 *
 * لا نقرأ النتائج المتوقعة من صفحة Checkout.
 * نحسب Item Total والضريبة والمجموع بأنفسنا،
 * ثم تقارن الاختبارات الحسابات مع القيم الظاهرة في الموقع.
 */
public final class CartSummary {

    // قائمة المنتجات الموجودة في السلة
    private final List<Product> products;

    // نسبة الضريبة، مثل 0.08
    private final BigDecimal taxRate;

    // هذا الـConstructor يستقبل المنتجات ونسبة الضريبة
    public CartSummary(
            List<Product> products,
            BigDecimal taxRate
    ) {

        // نتحقق من أن قائمة المنتجات ليست null
        if (products == null) {
            throw new FrameworkException(
                    "Cart products must not be null"
            );
        }

        // نتحقق من أن السلة تحتوي على منتج واحد على الأقل
        if (products.isEmpty()) {
            throw new FrameworkException(
                    "Cart products must not be empty"
            );
        }

        // نتحقق من أن القائمة لا تحتوي على Product قيمته null
        if (products.stream().anyMatch(product -> product == null)) {
            throw new FrameworkException(
                    "Cart products must not contain null"
            );
        }

        // نتحقق من أن نسبة الضريبة ليست null
        if (taxRate == null) {
            throw new FrameworkException(
                    "Tax rate must not be null"
            );
        }

        // نتحقق من أن نسبة الضريبة ليست سالبة
        if (taxRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new FrameworkException(
                    "Tax rate must not be negative: " + taxRate
            );
        }

        /*
         * ننشئ نسخة غير قابلة للتعديل من قائمة المنتجات.
         * هذا يمنع تغيير السلة من خارج الكلاس بعد إنشاء الملخص.
         */
        this.products = List.copyOf(products);

        // نخزن نسبة الضريبة
        this.taxRate = taxRate;
    }

    /*
     * تحسب مجموع أسعار المنتجات قبل الضريبة.
     *
     * نبدأ من BigDecimal.ZERO، ثم نجمع سعر كل منتج
     * باستخدام Stream وreduce.
     */
    public BigDecimal itemTotal() {

        BigDecimal result = products.stream()
                .map(Product::getPrice)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        // نرجع المجموع بعد تقريبه إلى منزلتين عشريتين
        return PriceUtils.round(result);
    }

    /*
     * تحسب قيمة الضريبة.
     *
     * مثال:
     * إذا كان Item Total يساوي 39.98
     * ونسبة الضريبة 0.08،
     * تكون الضريبة 3.1984 ثم تُقرب إلى 3.20.
     */
    public BigDecimal tax() {

        BigDecimal result = itemTotal()
                .multiply(taxRate);

        // نقرب الضريبة إلى منزلتين باستخدام HALF_UP
        return PriceUtils.round(result);
    }

    /*
     * تحسب المجموع النهائي:
     * Item Total + Tax.
     */
    public BigDecimal total() {

        BigDecimal result = itemTotal()
                .add(tax());

        // نرجع المجموع النهائي بمنزلتين عشريتين
        return PriceUtils.round(result);
    }

    // ترجع قائمة المنتجات المستخدمة في الحساب
    public List<Product> getProducts() {
        return products;
    }

    // ترجع نسبة الضريبة المستخدمة في الحساب
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    // تعرض ملخص السلة بشكل واضح عند طباعته
    @Override
    public String toString() {
        return "CartSummary{" +
                "itemTotal=" + itemTotal() +
                ", tax=" + tax() +
                ", total=" + total() +
                '}';
    }
}