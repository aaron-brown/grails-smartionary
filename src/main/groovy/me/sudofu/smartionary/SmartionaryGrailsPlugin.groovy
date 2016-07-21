package me.sudofu.smartionary

import grails.plugins.Plugin

class SmartionaryGrailsPlugin extends Plugin {
    def grailsVersion = "3.1.9 > *"
    def title = "Smartionary Plugin"
    def author = "Aaron Brown"
    def authorEmail = "brown.aaron.lloyd@gmail.com"
    def description = '''\
Facilitates externalizing a Map into a Domain, so that \
an app can support both programmatic and administrative modification \
of variable data.
'''
    def loadAfter = ['hibernate']
    def profiles = ['web']
    def documentation = "http://aaron-brown.github.io/grails-smartionary/docs/manual/latest/index.html"
    def license = "APACHE"
    def developers = [[name: "Bud Byrd", email: "bud.byrd@gmail.com"]]
    def issueManagement = [system: "GITHUB", url: "https://github.com/aaron-brown/grails-smartionary/issues"]
    def scm = [url: "https://github.com/aaron-brown/grails-smartionary"]
}
