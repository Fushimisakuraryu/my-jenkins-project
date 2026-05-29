pipeline {
    agent {
        kubernetes {
            yaml '''
spec:
  containers:
  - name: jnlp
    image: fanglaoye/jenkins-custom-agent:v1
    imagePullPolicy: Always
    volumeMounts:
    - mountPath: /home/jenkins/agent
      name: workspace-volume
  - name: maven
    image: maven:3.9-eclipse-temurin-21
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /home/jenkins/agent
      name: workspace-volume
  volumes:
  - name: workspace-volume
    emptyDir: {}
'''
        }
    }

    stages {
        stage('拉取代码') {
            steps {
                container('jnlp') {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/master']],
                        userRemoteConfigs: [[url: 'https://github.com/Fushimisakuraryu/my-jenkins-project']]
                    ])
                }
            }
        }

        stage('构建与测试') {
            steps {
                container('maven') {
                    sh 'mvn clean test'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('打包') {
            steps {
                container('maven') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('构建镜像') {
            steps {
                container('jnlp') {
                    sh 'docker build -t fanglaoye/my-jenkins-project:v$BUILD_NUMBER .'
                }
            }
        }
    }

    post {
        success {
            echo '流水线全部通关！'
        }
        failure {
            echo '流水线失败，请查看日志'
        }
    }
}
