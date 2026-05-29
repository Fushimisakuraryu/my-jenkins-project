pipeline {
    agent any

    tools {
        maven 'maven-3'
        jdk 'jdk17'
    }

    environment {
        APP_NAME = 'jenkins-demo'
        APP_VERSION = '1.0.0'
        DOCKER_IMAGE = "jenkins-demo:${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Code Analysis') {
            parallel {
                stage('Lint Check') {
                    steps {
                        sh 'mvn validate'
                    }
                }
                stage('Dependency Check') {
                    steps {
                        sh 'mvn dependency:resolve -q'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'mvn test'
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/*.xml'
                            jacoco(execPattern: 'target/jacoco.exec')
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Quality Gate') {
            steps {
                sh 'mvn verify'
            }
            post {
                success {
                    echo "Code coverage check passed"
                }
            }
        }

        stage('Build Docker Image') {
            when {
                branch 'main'
            }
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh 'echo "Deploying application..."'
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
            post {
                success {
                    echo "Application deployed at http://localhost:8080"
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "${APP_NAME} v${APP_VERSION} pipeline completed successfully!"
        }
        failure {
            echo "${APP_NAME} v${APP_VERSION} pipeline failed!"
            emailext(
                subject: "FAILED: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "Pipeline failed. Check logs at ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }
    }
}
