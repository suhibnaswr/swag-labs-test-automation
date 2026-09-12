// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها exceptions
package exceptions;

// نستورد List لأننا سنستقبل قائمة بأسماء المنتجات الموجودة في الصفحة
import java.util.List;

// هذا الكلاس يمثل خطأ خاصًا يظهر عندما لا نجد المنتج المطلوب
// ويرث من FrameworkException لأن جميع أخطاء المشروع الخاصة
// يجب أن ترجع إلى Exception أساسية واحدة
public class ProductNotFoundException extends FrameworkException {

    // هذا الـ Constructor يستقبل اسم المنتج المطلوب
    // بالإضافة إلى قائمة المنتجات التي ظهرت بالفعل في الصفحة
    public ProductNotFoundException(String productName, List<String> availableProducts) {

        // نرسل رسالة واضحة إلى Constructor الموجود في FrameworkException
        // الرسالة تعرض اسم المنتج غير الموجود وقائمة المنتجات المتاحة
        super("Product '" + productName + "' was not found. "
                + "Available products: " + availableProducts);
    }
}