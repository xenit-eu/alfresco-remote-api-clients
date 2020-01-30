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
                sh "./gradlew assemble --info --stacktrace --refresh-dependencies"
            }
        }

        stage("Tests") {
            steps {
                sh "./gradlew test --info --stacktrace"
            }
        }

        stage("Integration Tests") {
            steps {
                sh "./gradlew integrationTest -Pservice.alfresco.tcp.8080=8080 -Pservice.alfresco.tcp.8443=8443"
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
            sh "./gradlew composeDownForced"
        }
    }
}


