/*
 * هذا الملف يحدد Jenkins Pipeline الخاصة بالمشروع.
 *
 * في هذه الخطوة نضيف Parameters تسمح للمستخدم
 * باختيار Suite وطريقة تشغيل المتصفح.
 */
pipeline {

    /*
     * يسمح لـJenkins بتشغيل الـPipeline
     * على أي Agent متاح.
     */
    agent any

    /*
     * Parameters تظهر للمستخدم عند اختيار:
     * Build with Parameters.
     */
    parameters {

        /*
         * يسمح باختيار ملف TestNG Suite المطلوب.
         */
        choice(
                name: 'SUITE',
                choices: [
                        'testng.xml',
                        'testng-smoke.xml',
                        'testng-parallel.xml'
                ],
                description: 'Select the TestNG suite to run'
        )

        /*
         * يحدد هل يعمل المتصفح دون نافذة.
         *
         * القيمة الافتراضية true لأنها الأنسب
         * للتشغيل داخل Jenkins.
         */
        booleanParam(
                name: 'HEADLESS',
                defaultValue: true,
                description: 'Run the browser in headless mode'
        )
    }

    /*
     * أضفنا Stage مؤقتة حتى يبقى Jenkinsfile صالحًا.
     * سنضيف Checkout وBuild وTest في الخطوة 65.
     */
    stages {

        stage('Configuration') {

            steps {

                // نعرض القيم المختارة داخل Jenkins Console
                echo "Selected suite: ${params.SUITE}"
                echo "Headless mode: ${params.HEADLESS}"
            }
        }
    }
}
