// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها listeners
package listeners;

// نستورد IAnnotationTransformer لتعديل إعدادات @Test تلقائيًا
import org.testng.IAnnotationTransformer;

// نستورد ITestAnnotation للوصول إلى خصائص @Test
import org.testng.annotations.ITestAnnotation;

// نستورد DisabledRetryAnalyzer لمعرفة أن الاختبار
// لا يحتوي حاليًا على RetryAnalyzer فعلي
import org.testng.internal.annotations.DisabledRetryAnalyzer;

// نستورد Constructor وMethod
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/*
 * هذا الكلاس يطبق RetryAnalyzer على جميع الاختبارات تلقائيًا.
 *
 * بوجوده، لا نحتاج إلى كتابة:
 * retryAnalyzer = RetryAnalyzer.class
 * داخل كل @Test.
 */
public class RetryTransformer
        implements IAnnotationTransformer {

    /*
     * TestNG يستدعي هذه الـMethod لكل @Test
     * قبل تشغيل الاختبارات.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod
    ) {

        /*
         * نتحقق من أن الاختبار لا يحتوي على
         * RetryAnalyzer مخصص مسبقًا.
         */
        if (annotation.getRetryAnalyzerClass() == null
                || annotation.getRetryAnalyzerClass()
                == DisabledRetryAnalyzer.class) {

            /*
             * نربط RetryAnalyzer الخاص بنا
             * مع الاختبار الحالي.
             */
            annotation.setRetryAnalyzer(
                    RetryAnalyzer.class
            );
        }
    }
}