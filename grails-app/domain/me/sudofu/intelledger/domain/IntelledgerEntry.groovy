package me.sudofu.intelledger.domain
import me.sudofu.intelledger.domain.Intelledger
/*
 * Copyright 2013 Aaron Brown
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
     * This describes the version of the key.
     */
    Integer version
    
    /**
     * This tells who changed the key value.
     */
    String changeId
    
    /**
     * This describes whether the key is active or not.
     */
    Boolean active

    static belongsTo = [ intelledger: Intelledger ]

    static constraints = {
        key         (blank: false, unique: ['intelledger', 'version'])
        value       (nullable: true, size: 1..8000)
        description (nullable: true, size: 1..8000)
        changeId   (nullable: true, size: 1..8000)
        intelledger ()
    }

    static mapping = {
        key         column: 'identifier'
        value       type: "text"
        description type: "text"
        changeId    type: "text"        version     column: 'entry_version'
    }

    int compareTo(IntelledgerEntry obj) {
        if(key.compareTo(obj.key) == 0) {
            return version.compareTo(obj.version)
        } else {
            return key.compareTo(obj.key)
        }
    }

    String toString() {
        return key
    }
}

