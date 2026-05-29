pipeline {
    agent {
        kubernetes {
            yaml '''
spec:
  containers:
  - name: jnlp
    image: fanglaoye/jenkins-custom-agent:v1
    imagePullPolicy: Always
    env:
    - name: HTTP_PROXY
      value: "http://192.168.1.100:8080"
    - name: HTTPS_PROXY
      value: "http://192.168.1.100:8080"
    - name: http_proxy
      value: "http://192.168.1.100:8080"
    - name: https_proxy
      value: "http://192.168.1.100:8080"
    - name: NO_PROXY
      value: "localhost,127.0.0.1,10.0.0.0/8,192.168.0.0/16,.svc.cluster.local"
    - name: no_proxy
      value: "localhost,127.0.0.1,10.0.0.0/8,192.168.0.0/16,.svc.cluster.local"
    volumeMounts:
    - mountPath: /home/jenkins/agent
      name: workspace-volume
  - name: maven
    image: maven:3.9-eclipse-temurin-21
    command:
    - cat
    tty: true
    env:
    - name: HTTP_PROXY
      value: "http://192.168.1.100:8080"
    - name: HTTPS_PROXY
      value: "http://192.168.1.100:8080"
    - name: http_proxy
      value: "http://192.168.1.100:8080"
    - name: https_proxy
      value: "http://192.168.1.100:8080"
    - name: NO_PROXY
      value: "localhost,127.0.0.1,10.0.0.0/8,192.168.0.0/16,.svc.cluster.local"
    - name: no_proxy
      value: "localhost,127.0.0.1,10.0.0.0/8,192.168.0.0/16,.svc.cluster.local"
    - name: MAVEN_OPTS
      value: "-Dhttp.proxyHost=192.168.1.100 -Dhttp.proxyPort=8080 -Dhttps.proxyHost=192.168.1.100 -Dhttps.proxyPort=8080 -Dhttp.nonProxyHosts=localhost|127.0.0.1|10.*|192.168.*"
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

        stage('网络诊断') {
            steps {
                container('maven') {
                    sh 'echo "=== 1. 测试能否连代理 ==="'
                    sh 'wget -q -O- --timeout=5 http://192.168.1.100:8080 || echo "代理不可达"'
                    sh 'echo "=== 2. 测试直接连 Maven Central ==="'
                    sh 'wget -q -O- --timeout=5 https://repo.maven.apache.org/maven2/ || echo "Maven Central 不可达"'
                    sh 'echo "=== 3. 测试通过代理连 Maven Central ==="'
                    sh 'export http_proxy=http://192.168.1.100:8080 https_proxy=http://192.168.1.100:8080 && wget -q -O- --timeout=10 https://repo.maven.apache.org/maven2/ || echo "代理连 Maven Central 失败"'
                    sh 'echo "=== 4. java proxy settings ==="'
                    sh 'java -XshowSettings:properties -version 2>&1 | grep -i proxy || true'
                    sh 'echo "=== 诊断结束 ==="'
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
