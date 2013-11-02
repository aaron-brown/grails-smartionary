grails.doc {
    title = "Smartionary Plugin"
    authors = [ "Aaron Brown" ]
    license = "APACHE 2.0"
    copyright = "Copyright Aaron Brown 2013"

    images = new File("src/docs/images")
}

log4j = {
    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
}
