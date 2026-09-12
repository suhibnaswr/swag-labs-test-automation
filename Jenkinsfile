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
}
