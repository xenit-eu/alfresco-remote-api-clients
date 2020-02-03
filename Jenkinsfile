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

        stage('Publish') {
            when {
                anyOf {
                    branch "master*"
                    branch "release*"
                }
            }
            environment {
                SONATYPE_CREDENTIALS = credentials('sonatype')
                GPGPASSPHRASE = credentials('gpgpassphrase')
            }
            steps {
                script {
                    sh "./gradlew publish -Ppublish_username=${SONATYPE_CREDENTIALS_USR} -Ppublish_password=${SONATYPE_CREDENTIALS_PSW} -PkeyId=DF8285F0 -Ppassword=${GPGPASSPHRASE} -PsecretKeyRingFile=/var/jenkins_home/secring.gpg"
                }
            }
        }

        stage('Release to Maven Central') {
            when {
                anyOf {
                    branch "release*"
                }
            }
            steps {
                script {
                    sh "./gradlew closeAndReleaseRepository"
                }
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


