# Grails Smartionary Plugin

A `Grails` plugin that provides a progremmatic and administrative interface
for storing information in a Domain, which can be converted to and from a
`java.util.Map`.

## Usage

This Plugin is designed for applications which wish to store external data in
a Domain, and use that data within the application as a Map. Advantages of this
over a Config block is that the data can be updated in production run-time and
by administrative or non-technical hats.

## Limitations

The Plugin is very simple, and so it does not support arbitrary Map depth or
Lists. In otherwords, it should be treated like a single-level Map. 

All keys and values are Strings, so complex datatypes are not directly 
supported. JSON is supported in a limited fashion, as JSON uses simple
Datatypes. When retrieving information, all values will be Strings. Future
support for automatically converting the values is possible.

# Quick-start Guide

## Installation

**Coming soon (use `grails install-plugin` locally)**

## Controller links (for web-interface)

Smartionary: `/smartionary`
SmartionaryEntry: `/smartionaryEntry` or `/smartionary/entries`

## The `Smartionary` Object (for progremmatic-interface)

    import me.sudofu.smartionary.Smartionary

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
