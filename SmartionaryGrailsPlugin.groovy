class SmartionaryGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def loadAfter = [ 'hibernate' ]

    def title = "Smartionary Plugin"
    def author = "Aaron Brown"
    def authorEmail = "brown.aaron.lloyd@gmail.com"
    def description = '''\
Facilitates externalizing a Map into a Domain, so that \
and app can support both programmatic and administrative modification \
of variable data.
'''

    def documentation = "http://grails.org/plugin/smartionary"
    def license = "APACHE"
    def issueManagement = [ system: "GITHUB", url: "https://github.com/aaron-brown/grails-smartionary/issues" ]
    def scm = [ url: "https://github.com/aaron-brown/grails-smartionary" ]
}
