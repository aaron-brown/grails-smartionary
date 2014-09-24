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
package me.sudofu.intelledger.domain

/**
 * Emulates a {@link java.util.Map Map}.
 *
 * @author  Aaron Brown
 */
class Intelledger {
    /**
     * The name of the <b><code>Intelledger</code></b>, which is a
     * mnemonic for what the collection represents.
     */
    String name

    /**
     * A description for the <b><code>Intelledger</code></b>, and what
     * data it might pertain to.
     */
    String description

    /**
     * Specify the <b><code>IntelledgerEntry</code></b> relationship as
     * a <code>SortedSet</code>
     */
    SortedSet entries

    static hasMany = [ entries: IntelledgerEntry ]

    static constraints = {
        name        (blank: false, unique: true)
        description (nullable: true, size: 1..8000)
    }

    static mapping = { description type: "text" }

    String toString() {
        return name
    }

    /**
     * This method will return all the active keys and its values.
     *
     * @return
     *
     * A <code>Map</code> constructed of the <b>active</b>
     * <code>entries</code>.
     */
    Map toMap() {
        Map m = [:]

        getActiveEntries().each {
            m[it.key] = it.value
        }

        return m
    }

    /**
     * Retrieve <b>active</b> <code>entries</code>.
     *
     * @return
     *
     * A <code>List</code> of <code>entries</code> that are set as
     * <b>active</b>.
     */
    public List<IntelledgerEntry> getActiveEntries() {
        return entries?.findAll { it.active } as List
    }

    /**
     * Retrieve the lattermost generation for each of the
     * <code>entries</code>.
     *
     * @return
     *
     * A <code>List</code> of the lattermost generation of each entry,
     * regardless of <b>active</b> status.
     */
    public List<IntelledgerEntry> getLatestGenerationsEntries() {
        List<IntelledgerEntry> latestGenerations = []

        entries.keySet().each { entryKey ->
            IntelledgerEntry entry = findLatestVersionOf(entryKey)

            if (entry) {
                latestGenerations << entry
            }
        }

        return (latestGenerations ?: null)
    }

    /**
     * Retrieve the lattermost generation of an entry.
     *
     * @param   key
     *
     * The entry key to query for.
     *
     * @return
     *
     * The <b><code>IntelledgerEntry</code></b> with the given key,
     * which the lattermost generation of that set (if the key exists).
     */
    public IntelledgerEntry findLastGenerationOf(String key) {
        List<IntelledgerEntry> generations = entries?.findAll { it.key == key } as List

        if (generations) {
            return generations.sort()[-1]
        }

        return null
    }

}
