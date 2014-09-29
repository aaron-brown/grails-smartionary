package me.sudofu.intelledger.domain

import me.sudofu.intelledger.domain.Intelledger

/*
 * Copyright 2014 Aaron Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

/**
 * Emulates a {@link java.util.Map.Entry Map Entry}.
 *
 * @author  Aaron Brown
 */
class IntelledgerEntry implements Comparable<IntelledgerEntry> {
    /**
     * The Date the entry was created.
     */
    Date dateCreated

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

    /**
     * The generation of the entry, to keep track of change history.
     */
    Integer generation

    /**
     * Generic field for implementing a change identifier methodology.
     */
    String changeId

    /**
     * The active status of the entry.
     */
    Boolean active

    static belongsTo = [ intelledger: Intelledger ]

    static constraints = {
        key         (blank: false, unique: ['intelledger', 'generation'])
        value       (nullable: true, size: 1..8000)
        description (nullable: true, size: 1..8000)
        generation  (nullable: false)
        changeId    (blank: false, nullable: false, size: 1..8000)
        active      (nullable: false)
    }

    static mapping = {
        key         column: 'identifier'
        value       type: 'text'
        description type: 'text'
        changeId    type: 'text'
    }

    int compareTo(IntelledgerEntry obj) {
        if (key.compareTo(obj.key) == 0 && generation && obj.generation) {
            return generation.compareTo(obj.generation)
        }
        else {
            return key.compareTo(obj.key)
        }
    }

    String toString() {
        return key
    }

    /**
     * Determine if the entry is inactive.
     *
     * @return
     *
     * <code>true</code> if-and-only-if <code>active</code> is
     * <code>false</code>.
     */
    boolean getInactive() {
        return (! active)
    }
}

