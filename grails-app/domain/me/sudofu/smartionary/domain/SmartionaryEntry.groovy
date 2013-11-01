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
package me.sudofu.smartionary.domain

import me.sudofu.smartionary.domain.Smartionary

/**
 * Emulates a {@link java.util.Map.Entry Map Entry}.
 *
 * @author  Aaron Brown
 */
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
