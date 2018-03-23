pipeline {
  agent {
    docker {
      image 'openjdk:8-jdk'
    }
  }
  environment {
    stagingDbHost = credentials('STAGINGDBHOST')
    stagingDbUrl = stagingDbHost + '/registry'
    ebiKbUserName = credentials('EBIKBUSRNAME')
    ebiKbPwd = credentials('EBIKBPASSWD')
    tomcatCredentials = credentials('TOMCATCREDENTIALS')
    stagingHost = credentials('STAGINGHOST')
  }
  parameters {
    booleanParam(name: 'DeployToStaging' , defaultValue: false , description: '')
  }
  stages {
    stage('Default Build pointing to Staging DB') {
      steps {
        echo 'Default Build pointing to Staging DB'
        sh "./gradlew -PDBUSER=${ebiKbUserName} -PDBPASS=${ebiKbPwd} -PDBURL=${stagingDbUrl} clean build"
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
        sh "curl --upload-file build/libs/amp-t2d-property-registry.war 'http://'${tomcatCredentials}'@'${stagingHost}':8080/manager/text/deploy?path=/ega/t2d/registry&update=true' | grep 'OK - Deployed application at context path /ega/t2d/registry'"
      }
    }
  }
}
