package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

// هذا الكلاس يعيد تشغيل الاختبار الفاشل مرة واحدة
public class RetryAnalyzer implements IRetryAnalyzer {

    // نستخدم Logger لتسجيل إعادة المحاولة
    private static final Logger log =
            LogManager.getLogger(RetryAnalyzer.class);

    // الحد الأقصى لعدد مرات إعادة المحاولة
    private static final int MAX_RETRIES = 1;

    // عدد مرات إعادة المحاولة التي تمت
    private int retryCount = 0;

    // TestNG يستدعي هذه الـMethod عندما يفشل الاختبار
    @Override
    public boolean retry(ITestResult result) {

        // نتحقق هل ما زال مسموحًا بإعادة الاختبار
        if (retryCount < MAX_RETRIES) {

            // نزيد عدد مرات إعادة المحاولة
            retryCount++;

            // نسجل اسم الاختبار ورقم المحاولة الجديدة
            log.warn(
                    "RETRY | Test: {} | New attempt: {}",
                    result.getName(),
                    retryCount + 1
            );

            // نطلب من TestNG إعادة تشغيل الاختبار
            return true;
        }

        // لا نسمح بمحاولة إضافية
        return false;
    }
}