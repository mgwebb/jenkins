package com.corelogic.cis
class JenkinsCommon {
    
    def deployToEnv(caller, orgName, spaceName, appName = '', credentialsId) {

        spaceName = spaceName.toLowerCase()

        caller.echo "Deploying ${appName} on ${spaceName}"

        caller.echo "Calling cf delete ${appName}"
        def deleteOldCommand = "cf delete ${appName} -f"
        executeCFCommand(caller, orgName, spaceName, deleteOldCommand, credentialsId)

        caller.echo "Calling cf push ${appName}"
        def pushNewCommand = "cf push -f ./modules/service/build/manifests/manifest.${spaceName}.yml"
        executeCFCommand(caller, orgName, spaceName, pushNewCommand, credentialsId)
    }

    def executeCFCommand(caller, orgName, spaceName, command, credentialsId) {
        caller.unstash 'build-artifacts'
        caller.unstash 'cf-configs'

        caller.wrap([$class         : 'CloudFoundryCliBuildWrapper',
              apiEndpoint           : 'https://api.preprodapp.cf.corelogic.net',
              cloudFoundryCliVersion: 'CloudFoundryCLI',
              credentialsId         : credentialsId[spaceName],
              organization          : orgName,
              space                 : spaceName]) {

              caller.sh command
        }
    }

    def notifySlack(caller) {
        try {
            caller.slackSend(color: '#FFFF00', message: "BUILD/DEPLOY FAILED: Job '${caller.env.JOB_NAME} [${caller.env.BUILD_NUMBER}]' (${caller.env.BUILD_URL})", teamDomain: 'clgx-apptx', token: 'D4jxXWhwNF15AYH9NjVARJcA', channel: '#cis-app-build')
        } catch (e) {
            caller.echo e.getMessage()
            caller.echo 'slack message failed'
        }
    }
    
}
