def deployToEnv(orgName, spaceName, appName = '') {

    spaceName = spaceName.toLowerCase()

    echo "Deploying ${appName} on ${spaceName}"

    echo "Calling cf delete ${appName}"
    def deleteOldCommand = "cf delete ${appName} -f"
    executeCFCommand(orgName, spaceName, deleteOldCommand)

    echo "Calling cf push ${appName}"
    def pushNewCommand = "cf push -f ./modules/service/build/manifests/manifest.${spaceName}.yml"
    executeCFCommand(orgName, spaceName, pushNewCommand)
}

def executeCFCommand(orgName, spaceName, command) {
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

def notifySlack() {
    try {
        slackSend(color: '#FFFF00', message: "BUILD/DEPLOY FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})", teamDomain: 'clgx-apptx', token: 'D4jxXWhwNF15AYH9NjVARJcA', channel: '#cis-app-build')
    } catch (e) {
        echo 'slack message failed'
    }
}
