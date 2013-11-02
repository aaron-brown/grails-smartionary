<!DOCTYPE html>
<html>

  <head>
    <meta charset='utf-8' />
    <meta http-equiv="X-UA-Compatible" content="chrome=1" />
    <meta name="description" content="Grails-smartionary : A Grails Plugin that implements a Domain that emulates a Map or Dictionary, with a strong progremmatic interface." />

    <link rel="stylesheet" type="text/css" media="screen" href="stylesheets/stylesheet.css">

    <title>Grails-smartionary</title>
  </head>

  <body>

    <!-- HEADER -->
    <div id="header_wrap" class="outer">
        <header class="inner">
          <a id="forkme_banner" href="https://github.com/aaron-brown/grails-smartionary">View on GitHub</a>

          <h1 id="project_title">Grails-smartionary</h1>
          <h2 id="project_tagline">A Grails Plugin that implements a Domain that emulates a Map or Dictionary, with a strong progremmatic interface.</h2>

            <section id="downloads">
              <a class="zip_download_link" href="https://github.com/aaron-brown/grails-smartionary/zipball/master">Download this project as a .zip file</a>
              <a class="tar_download_link" href="https://github.com/aaron-brown/grails-smartionary/tarball/master">Download this project as a tar.gz file</a>
            </section>
        </header>
    </div>

    <!-- MAIN CONTENT -->
    <div id="main_content_wrap" class="outer">
      <section id="main_content" class="inner">
        <h1>
<a name="grails-smartionary-plugin" class="anchor" href="#grails-smartionary-plugin"><span class="octicon octicon-link"></span></a>Grails Smartionary Plugin</h1>

<p>A <code>Grails</code> plugin that provides a progremmatic and administrative interface
for storing information in a Domain, which can be converted to and from a
<code>java.util.Map</code>.</p>

<h2>
<a name="usage" class="anchor" href="#usage"><span class="octicon octicon-link"></span></a>Usage</h2>

<p>This Plugin is designed for applications which wish to store external data in
a Domain, and use that data within the application as a Map. Advantages of this
over a Config block is that the data can be updated in production run-time and
by administrative or non-technical hats.</p>

<h2>
<a name="limitations" class="anchor" href="#limitations"><span class="octicon octicon-link"></span></a>Limitations</h2>

<p>The Plugin is very simple, and so it does not support arbitrary Map depth or
Lists. In otherwords, it should be treated like a single-level Map.</p>

<p>All keys and values are Strings, so complex datatypes are not directly
supported. JSON is supported in a limited fashion, as JSON uses simple
Datatypes. When retrieving information, all values will be Strings. Future
support for automatically converting the values is possible.</p>

<h1>
<a name="quick-start-guide" class="anchor" href="#quick-start-guide"><span class="octicon octicon-link"></span></a>Quick-start Guide</h1>

<h2>
<a name="installation" class="anchor" href="#installation"><span class="octicon octicon-link"></span></a>Installation</h2>

<p>Add a dependency in BuildConfig.groovy:</p>

<pre><code>compile 'smartionary:1.0'
</code></pre>

<h2>
<a name="controller-links-for-web-interface" class="anchor" href="#controller-links-for-web-interface"><span class="octicon octicon-link"></span></a>Controller links (for web-interface)</h2>

<p>Smartionary: <code>/smartionary</code></p>

<p>SmartionaryEntry: <code>/smartionaryEntry</code> or <code>/smartionary/entries</code></p>

<h2>
<a name="the-smartionary-object-for-progremmatic-interface" class="anchor" href="#the-smartionary-object-for-progremmatic-interface"><span class="octicon octicon-link"></span></a>The <code>Smartionary</code> Object (for progremmatic-interface)</h2>

<pre><code>import me.sudofu.smartionary.Smartionary

// 'sample' will be dynamically created if it does not exist already in
// Smartionary Domain.
Smartionary.set(
    'sample',
    'A sample smartionary.',

    first: "This gets converted into a SmartionaryEntry."

    second: 4,          // Will be: "4"
    third: new Date(1000),  // Will be: new Date() as String

    smartionaryDescriptions: [                  // Special "reserved" key.
        first: "Explanation of what happens.",  // Matches above key.
        fourth: "This key does not exist."      // Ignored, no match.
    ]
)

Map smart = Smartionary.get('sample')
println smart.first   // This gets converted into a SmartionaryEntry.
println smart.second  // 4 (as String)
println smart.third   // Wed Dec 31 18:00:01 CST 1969
println smart.fourth  // null

// Update existing entries, and create additional ones.
Smartionary.set(
    'sample',
    third: new Date(2000),
    fourth: new UUID(1000, 2000),
    fifth: 'hi'
)

smart = Smartionary.get('sample')

println smart.third   // Wed Dec 31 18:00:02 CST 1969
println smart.fourth  // 00000000-0000-03e8-0000-0000000007d0 (as String)
println smart.fifth   // hi

// Set a specific entry (can also be done with Map form).
Smartionary.set('sample', 'fourth', new UUID(2000, 3000), 'Adding an optional description')

// Delete an entry.
Smartionary.delete('sample', 'fifth')

// Or delete many...
Smartionary.delete('sample', 'first', 'second', 'third')

smart = Smartionary.get('sample')

println smart.fourth  // 00000000-0000-07d0-0000-000000000bb8
println smart.fifth   // null

// Purge all entries that are null, to clean up the Domain if necessary.
Smartionary.purgeNull('sample')

// Or, purge all the keys to repurpose the Domain.
Smartionary.purge('sample')

// Create a Smartionary from JSON.
//
// Using anything other than a Map will result in an
// IllegalArgumentException.
//
// Also, nested Maps are currently not supported...
String json = '{"a": "apple", "b": "banana", "c":"carrot"}'

Smartionary.fromJson('sample', json, 'Created from JSON.')

// Convert Smartionary back to JSON.
json = Smartionary.getAsJson('sample')
println json  // Nearly identical to the above.

// Make it pretty!
json = Smartionary.getAsJson('sample', true)

// Some Map-interface-like functions are included.
println Smartionary.size('sample')              // 3, currently.
println Smartionary.contains('sample', "apple") // true
println Smartionary.containsKey('sample', "b")  // true

println Smartionary.size('nonexistant') // -1, it does not exist.
println Smartionary.contains('sample', "Durian")  // false
println Smartionary.containsKey('sample', 'd')    // false

// Delete all the things.
Smartionary.delete('sample')
</code></pre>
      </section>
    </div>

    <!-- FOOTER  -->
    <div id="footer_wrap" class="outer">
      <footer class="inner">
        <p class="copyright">Grails-smartionary maintained by <a href="https://github.com/aaron-brown">aaron-brown</a></p>
        <p>Published with <a href="http://pages.github.com">GitHub Pages</a></p>
      </footer>
    </div>

    

  </body>
</html>
