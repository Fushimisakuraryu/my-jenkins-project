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
    - name: GIT_SSL_NO_VERIFY
      value: "1"
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
    volumeMounts:
    - mountPath: /home/jenkins/agent
      name: workspace-volume
  - name: kaniko
    image: gcr.io/kaniko-project/executor:latest
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
        stage('配置 Maven 代理') {
            steps {
                container('maven') {
                    sh '''
mkdir -p /root/.m2
cat > /root/.m2/settings.xml << 'EOF'
<settings>
  <proxies>
    <proxy>
      <id>genproxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>192.168.1.100</host>
      <port>8080</port>
      <nonProxyHosts>localhost|127.0.0.1|10.*|192.168.*</nonProxyHosts>
    </proxy>
    <proxy>
      <id>genproxy-https</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>192.168.1.100</host>
      <port>8080</port>
      <nonProxyHosts>localhost|127.0.0.1|10.*|192.168.*</nonProxyHosts>
    </proxy>
  </proxies>
</settings>
EOF
echo "settings.xml created"
'''
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

        stage('构建 Docker 镜像') {
            steps {
                container('kaniko') {
                    sh '''
/kaniko/executor \
  --context=/home/jenkins/agent/workspace/my-first-gitops \
  --dockerfile=Dockerfile \
  --destination=fanglaoye/my-jenkins-project:v$BUILD_NUMBER \
  --skip-tls-verify \
  --cache=false
'''
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
