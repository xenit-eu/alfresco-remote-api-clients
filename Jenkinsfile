pipeline {
    agent any

    stages {
        stage("Clean") {
            steps {
                sh "./gradlew clean"
            }
        }

        stage("Build") {
            steps {
                sh "./gradlew assemble --info --stacktrace"
            }
        }

        stage("Tests") {
            steps {
                sh "./gradlew test --info --stacktrace"
            }
        }

        stage("Tests") {
            steps {
                sh "./gradlew integrationTest -Pservice.alfresco.port.8080=8080 -Pservice.alfresco.port.8443=8443"
            }
        }

        stage("Publishing") {
            steps {
                sh "./gradlew publish"
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/**/*.xml'
        }
    }
}


