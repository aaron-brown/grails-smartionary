package me.sudofu.smartionary.domain

import me.sudofu.smartionary.domain.Smartionary

class SmartionaryEntry implements Comparable {
    /**
     * The key by which the entry is accessed.
     */
    String key

    /**
     * The value of the key (normalized as a String)
     */
    String value

    /**
     * Describes the purpose of the <code>value</code> or where it is
     * used.
     */
    String description

    static belongsTo = [ smartionary: Smartionary ]

    static constraints = {
        key         (nullable: false, blank: false, unique: 'smartionary')
        value       (nullable: true, size: 1..8000)
        description (nullable: true, size: 1..8000)
    }

    static mapping = {
        value       type: "text"
        description type: "text"
    }

    int compareTo(obj) {
        key.compareTo(obj.key)
    }

    String toString() {
        return key
    }
}
