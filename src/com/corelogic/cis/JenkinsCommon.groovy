package com.corelogic.cis
class JenkinsCommon {
    
    def deployToEnv(orgName, spaceName, appName = '', credentialsId) {

        spaceName = spaceName.toLowerCase()

        echo "Deploying ${appName} on ${spaceName}"

        echo "Calling cf delete ${appName}"
        def deleteOldCommand = "cf delete ${appName} -f"
        executeCFCommand(orgName, spaceName, deleteOldCommand, credentialsId)

        echo "Calling cf push ${appName}"
        def pushNewCommand = "cf push -f ./modules/service/build/manifests/manifest.${spaceName}.yml"
        JenkinsCommon.executeCFCommand(orgName, spaceName, pushNewCommand)
    }

    def executeCFCommand(orgName, spaceName, command, credentialsId) {
        unstash 'build-artifacts'
        unstash 'cf-configs'

        wrap([$class                : 'CloudFoundryCliBuildWrapper',
              apiEndpoint           : 'https://api.preprodapp.cf.corelogic.net',
              cloudFoundryCliVersion: 'CloudFoundryCLI',
              credentialsId         : credentialsId[spaceName],
              organization          : orgName,
              space                 : spaceName]) {

            sh command
        }
    }

    def notifySlack(caller, env) {
        caller.echo env.getClass().getName()
        try {
            caller.slackSend(color: '#FFFF00', message: "BUILD/DEPLOY FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", teamDomain: 'clgx-apptx', token: 'D4jxXWhwNF15AYH9NjVARJcA', channel: '#cis-app-build')
        } catch (e) {
            caller.echo e.getMessage()
            caller.echo 'slack message failed'
        }
    }
    
}
