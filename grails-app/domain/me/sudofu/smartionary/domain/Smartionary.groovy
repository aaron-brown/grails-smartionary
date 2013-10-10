package me.sudofu.smartionary.domain

import me.sudofu.smartionary.domain.SmartionaryEntry

class Smartionary {
    /**
     * The name of the Smartionary, which is a mnemonic for what the
     * collection represents.
     */
    String name

    /**
     * A description for the <code>Smartionary</code> object, and what
     * data it might pertain to.
     */
    String description

    /**
     * Specify the <code>Entry</code> Objects as a
     * <code>SortedSet</code>
     */
    SortedSet entries

    static hasMany = [ entries: SmartionaryEntry ]

    static constraints = {
        name        (nullable: false, blank: false, unique: true)
        description (nullable: true, size: 1..8000)
    }

    static mapping = {
        description type: "text"
    }

    String toString() {
        return name
    }

    Map toMap() {
        Map m = [:]

        entries.each {
            m[it.key] = it.value
        }

        return m
    }
}
