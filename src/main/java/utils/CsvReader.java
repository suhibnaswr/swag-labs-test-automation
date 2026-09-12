// هذا السطر يحدد أن الكلاس موجود داخل Package اسمها utils
package utils;

// نستورد الـException الخاصة بالمشروع
import exceptions.FrameworkException;

// نستورد Customer لتحويل كل صف في CSV إلى Object
import model.Customer;

// نستورد الأدوات المطلوبة لقراءة الملف
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// نحدد UTF-8 حتى تُقرأ النصوص بطريقة صحيحة
import java.nio.charset.StandardCharsets;

// نستورد ArrayList وList لتخزين العملاء والمنتجات
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * هذا الكلاس مسؤول عن قراءة بيانات العملاء
 * من ملف CSV الموجود داخل Resources.
 */
public final class CsvReader {

    // نفصل أسماء المنتجات داخل عمود products باستخدام |
    private static final String PRODUCT_SEPARATOR = "\\|";

    /*
     * جعلنا الـConstructor من نوع private لمنع إنشاء Object.
     * سنستخدم الـMethod بطريقة static.
     */
    private CsvReader() {
    }

    /*
     * هذه الـMethod تقرأ ملف CSV من Classpath
     * وترجع قائمة من Customer Objects.
     *
     * مثال على اسم الملف:
     * "testdata/customers.csv"
     */
    public static List<Customer> readCustomers(
            String resourcePath
    ) {

        // نتحقق من أن مسار الملف ليس null أو فارغًا
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new FrameworkException(
                    "CSV resource path must not be empty"
            );
        }

        /*
         * نحصل على الملف من Classpath.
         * ملفات src/test/resources تصبح متاحة في Classpath
         * عند تشغيل Maven أو TestNG.
         */
        InputStream inputStream = CsvReader.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        // إذا لم نجد الملف نظهر رسالة واضحة
        if (inputStream == null) {
            throw new FrameworkException(
                    "CSV file was not found on the classpath: "
                            + resourcePath
            );
        }

        // ننشئ قائمة لتخزين العملاء المقروءين من الملف
        List<Customer> customers = new ArrayList<>();

        /*
         * نستخدم try-with-resources لإغلاق BufferedReader
         * وInputStream تلقائيًا بعد انتهاء القراءة.
         */
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                )
        )) {

            // نقرأ أول سطر، وهو Header الخاص بالملف
            String header = reader.readLine();

            // نتأكد من أن الملف ليس فارغًا
            if (header == null) {
                throw new FrameworkException(
                        "CSV file is empty: " + resourcePath
                );
            }

            // هذا المتغير يحفظ رقم السطر لإظهار أخطاء أوضح
            int lineNumber = 1;

            // نقرأ أول سطر من البيانات بعد Header
            String line;

            // نستمر بالقراءة حتى نصل إلى نهاية الملف
            while ((line = reader.readLine()) != null) {

                // نزيد رقم السطر الحالي
                lineNumber++;

                // نتجاهل الأسطر الفارغة
                if (line.isBlank()) {
                    continue;
                }

                /*
                 * نقسم السطر حسب الفاصلة.
                 * استخدمنا -1 حتى لا تُحذف الأعمدة الفارغة.
                 */
                String[] columns = line.split(",", -1);

                /*
                 * يجب أن يحتوي كل صف على أربعة أعمدة:
                 * firstName,lastName,postalCode,products
                 */
                if (columns.length != 4) {
                    throw new FrameworkException(
                            "Invalid CSV row at line "
                                    + lineNumber
                                    + ". Expected 4 columns but found "
                                    + columns.length
                                    + ": "
                                    + line
                    );
                }

                // نقرأ بيانات العميل من الأعمدة الثلاثة الأولى
                String firstName = columns[0].trim();
                String lastName = columns[1].trim();
                String postalCode = columns[2].trim();

                /*
                 * عمود products يمكن أن يحتوي على أكثر من منتج.
                 * نفصل المنتجات باستخدام علامة |.
                 */
                List<String> products = Arrays.stream(
                                columns[3].split(
                                        PRODUCT_SEPARATOR,
                                        -1
                                )
                        )
                        .map(String::trim)
                        .toList();

                // ننشئ Customer جديدًا ونضيفه إلى القائمة
                customers.add(
                        new Customer(
                                firstName,
                                lastName,
                                postalCode,
                                products
                        )
                );
            }

        } catch (IOException exception) {

            /*
             * إذا حدث خطأ أثناء قراءة الملف،
             * نغلفه داخل FrameworkException.
             */
            throw new FrameworkException(
                    "Could not read CSV file: " + resourcePath,
                    exception
            );
        }

        // نتأكد من أن الملف يحتوي على بيانات عملاء
        if (customers.isEmpty()) {
            throw new FrameworkException(
                    "CSV file contains no customer data: "
                            + resourcePath
            );
        }

        // نرجع نسخة غير قابلة للتعديل من قائمة العملاء
        return List.copyOf(customers);
    }
}