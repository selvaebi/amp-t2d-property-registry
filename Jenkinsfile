pipeline {
  triggers {
      cron(env.BRANCH_NAME == 'master'? 'H 10 * * *' : '')
  }
  agent {
    docker {
      image 'maven:3.5.2-jdk-8'
    }
  }
  environment {
    stagingPostgresDbUrl = credentials('STAGINGPROPERTYREGISTRYDBURL')
    fallBackPostgresDbUrl = credentials('FALLBACKPROPERTYREGISTRYDBURL')
    productionPostgresDbUrl = credentials('PRODUCTIONPROPERTYREGISTRYDBURL')
    postgresDBUserName = credentials('POSTGRESDBUSERNAME')
    postgresDBPassword = credentials('POSTGRESDBPASSWORD')
    tomcatCredentials = credentials('TOMCATCREDENTIALS')
    stagingHost = credentials('STAGINGHOST')
    fallbackHost = credentials('FALLBACKHOST')
    productionHost = credentials('PRODUCTIONHOST')
  }
  parameters {
    booleanParam(name: 'DeployToStaging' , defaultValue: false , description: '')
    booleanParam(name: 'DeployToProduction' , defaultValue: false , description: '')
  }
  stages {
    stage('Default Build pointing to Staging DB') {
      steps {
        sh "mvn clean package -DbuildDirectory=staging/target -Dampt2d-property-registry-db.url=${stagingPostgresDbUrl} -Dampt2d-property-registry-db.username=${postgresDBUserName} -Dampt2d-property-registry-db.password=${postgresDBPassword}"
      }
    }
    stage('Build For FallBack And Production') {
      when {
        expression {
          params.DeployToProduction == true
        }
      }
      steps {
        echo 'Build pointing to FallBack DB'
        sh "mvn clean package -DskipTests -DbuildDirectory=fallback/target -Dampt2d-property-registry-db.url=${fallBackPostgresDbUrl} -Dampt2d-property-registry-db.username=${postgresDBUserName} -Dampt2d-property-registry-db.password=${postgresDBPassword}"
        echo 'Build pointing to Production DB'
        sh "mvn clean package -DskipTests -DbuildDirectory=production/target -Dampt2d-property-registry-db.url=${productionPostgresDbUrl} -Dampt2d-property-registry-db.username=${postgresDBUserName} -Dampt2d-property-registry-db.password=${postgresDBPassword}"
      }
    }
    stage('Deploy To Staging') {
      when {
        expression {
          params.DeployToStaging == true
        }
      }
      steps {
        echo 'Deploying to Staging'
        sh "curl --upload-file staging/target/amp-t2d-property-registry-*.war 'http://'${tomcatCredentials}'@'${stagingHost}':8080/manager/text/deploy?path=/dev/registry&update=true' | grep 'OK - Deployed application at context path '"
      }
    }
    stage('Deploy To FallBack And Production') {
      when {
        expression {
          params.DeployToProduction == true
        }
      }
      steps {
        echo 'Deploying to Fallback'
        sh "curl --upload-file fallback/target/amp-t2d-property-registry-*.war 'http://'${tomcatCredentials}'@'${fallbackHost}':8080/manager/text/deploy?path=/registry&update=true' | grep 'OK - Deployed application at context path '"
        echo 'Deploying to Production'
        sh "curl --upload-file production/target/amp-t2d-property-registry-*.war 'http://'${tomcatCredentials}'@'${productionHost}':8080/manager/text/deploy?path=/registry&update=true' | grep 'OK - Deployed application at context path '"
        archiveArtifacts artifacts: 'production/target/amp-t2d-property-registry-*.war' , fingerprint: true
      }
    }
  }
  post {
    failure {
       echo "Test failed"
       mail(bcc: '',
       body: "Run ${JOB_NAME}-#${BUILD_NUMBER} failed.\n\
       To get more details, visit the build results page:${BUILD_URL}",
       cc: '',
       from: 'amp-dev@ebi.ac.uk',
       replyTo: '',
       subject: "${JOB_NAME} ${BUILD_NUMBER} failed",
       to: 'amp-dev@ebi.ac.uk')
     }
  }
}
