// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها exceptions
package exceptions;

// هذا الكلاس يمثل الخطأ الأساسي الخاص بالـ Framework
// وهو يرث من RuntimeException، لذلك يعتبر Unchecked Exception
public class FrameworkException extends RuntimeException {

    // هذا الـ Constructor يستقبل رسالة توضح سبب الخطأ
    public FrameworkException(String message) {

        // نرسل الرسالة إلى Constructor الموجود في RuntimeException
        super(message);
    }

    // هذا الـ Constructor يستقبل رسالة بالإضافة إلى الخطأ الأصلي
    public FrameworkException(String message, Throwable cause) {

        // نرسل الرسالة والخطأ الأصلي إلى RuntimeException
        super(message, cause);
    }
}