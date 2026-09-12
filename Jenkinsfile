/*
 * هذا الملف يحدد Jenkins Pipeline
 * الخاصة بمشروع Swag Labs.
 */
pipeline {

    // تشغيل الـPipeline على أي Jenkins Agent متاح
    agent any

    /*
     * خيارات تظهر عند تشغيل:
     * Build with Parameters
     */
    parameters {

        // اختيار ملف TestNG Suite المطلوب تشغيله
        choice(
                name: 'SUITE',
                choices: [
                        'testng.xml',
                        'testng-smoke.xml',
                        'testng-parallel.xml'
                ],
                description: 'Select the TestNG suite to run'
        )

        // اختيار تشغيل المتصفح مع نافذة أو بوضع Headless
        booleanParam(
                name: 'HEADLESS',
                defaultValue: true,
                description: 'Run the browser in headless mode'
        )
    }

    stages {

        /*
         * المرحلة الأولى:
         * تنزيل ملفات المشروع من Git Repository.
         */
        stage('Checkout') {

            steps {

                // يسحب Jenkins الكود من المستودع المرتبط بالـJob
                checkout scm
            }
        }

        /*
         * المرحلة الثانية:
         * تنظيف المشروع وتجميع كود Java دون تشغيل الاختبارات.
         */
        stage('Build') {

            steps {

                // هذا الأمر مناسب لتشغيل Jenkins على Windows
                bat 'mvn -B clean compile -DskipTests'
            }
        }

        /*
         * المرحلة الثالثة:
         * تشغيل TestNG Suite التي اختارها المستخدم.
         */
        stage('Test') {

            steps {

                /*
                 * نمرر إلى Maven:
                 * اسم Suite المختارة.
                 * قيمة Headless المختارة.
                 */
                bat """
                    mvn -B test ^
                    -Dsurefire.suiteXmlFiles=${params.SUITE} ^
                    -Dheadless=${params.HEADLESS}
                """
            }
        }
    }

        /*
         * تعمل هذه الإجراءات دائمًا بعد انتهاء الـPipeline،
         * سواء نجحت الاختبارات أم فشلت.
         */
        post {

            always {

                /*
                 * نشر نتائج اختبارات Maven بصيغة JUnit.
                 *
                 * allowEmptyResults يمنع فشل الـPipeline
                 * إذا لم تُنشأ النتائج بسبب خطأ مبكر.
                 */
                junit(
                        testResults: 'target/surefire-reports/TEST-*.xml',
                        allowEmptyResults: true
                )

                /*
                 * إنشاء تقرير Allure من النتائج
                 * الموجودة داخل target/allure-results.
                 *
                 * يتطلب تثبيت Allure Jenkins Plugin.
                 */
                allure(
                        includeProperties: false,
                        results: [
                                [
                                        path: 'target/allure-results'
                                ]
                        ]
                )

                /*
                 * حفظ Screenshots كـBuild Artifacts.
                 *
                 * allowEmptyArchive يسمح بنجاح الخطوة
                 * عندما تنجح الاختبارات ولا توجد Screenshots.
                 */
                archiveArtifacts(
                        artifacts: 'target/screenshots/**/*.png',
                        allowEmptyArchive: true
                )
            }
        }
}
