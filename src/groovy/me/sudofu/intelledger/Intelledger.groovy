/*
 * Copyright 2014 Aaron Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package me.sudofu.intelledger

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.JsonException

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import me.sudofu.intelledger.domain.Intelledger as IntelledgerDomain
import me.sudofu.intelledger.domain.IntelledgerEntry

/**
 * The primary progremmatic interface for
 * <b><code>Intelledger</code></b>.
 *
 * <p>
 * <b><code>Intelledger</code></b> provides a flexible and progremmatic
 * interface for storing information in a <b>Grails</b> Domain that
 * mimics the structure of a single-level <code>Map</code> with key versioning.
 * And at a time only one key will be active among duplicate keys.
 * <p>
 *
 * <p>
 * Instantiating the Domain information can be done directly via a
 * <code>Map</code>, and/or updated individually. For example:
 *
 * <code><pre>
 * Intelledger.set(
 *     'myIntelledger',
 *     'This is a sample Intelledger instantiation.',
 *     myFirstElement: 'foo',
 *     mySecondElement: 2,
 *     myThirdElement: new Date(),
 *     smartionaryDescriptions: [
 *         _entryDescriptions: "Here is where you can put entry descriptions., but this one will be ignored since there is no key above that matches it."
 *         myFirstElement: "My first element."
 *         myThirdElement: "This will get converted to a human-readable timestamp, as all elements are converted to a String."
 *     ]
 * )
 * </code></pre>
 *
 * The <code>Intelledger</code> can then retrieve the Domain as a
 * <code>Map</code> later like so:
 *
 * <code><pre>
 * Map myElements = Intelledger.get('myIntelledger')
 *
 * println myElements.myFirstElement
 * </code></pre>
 * </p>
 *
 * <p>
 * <b>Note</b> that the Domains are instantiated dynamically. That is,
 * <code><b>foo</b></code> would have been daynamically created with
 * that call if it did not previously exist, and the key-value pairs
 * associated to it. A subsequent call of that form would <i>update</i>
 * any entries that are already associated with what were passed, and
 * create new ones that were given.
 * </p>
 *
 * <p>
 * Descriptions can be omitted or included as desired. The
 * <b><code>Intelledger</code></b> does not interface with retrieving
 * descriptions, as that meant more for when observing the views
 * (i.e. administratively). In the
 * {@link #set(Map, String, String} method, the
 * <code>smartionaryDescriptions</code> is a "reserved" keyword for
 * telling the method to use entries in that key that have the same key
 * as the outer level to include as descriptions. It ignores any keys
 * that are not present when the method is passed; this is to prevent
 * trying to update descriptions of entries that do not exist, and
 * makes it clear that the value <code>null</code> cannot be implicitly
 * set to an entry just by updating its description.
 * </p>
 *
 * <p>
 * Also provided are ways of interacting with JSON as reasonably easily
 * as possible.
 * </p>
 *
 * @author Aaron Brown
 */
class Intelledger {

    /**
     * Retrieve the number of <i>active</i>
     * <code>IntelledgerEntry</code> objects that are associated with an
     * <code>Intelledger</code>.
     *
     * @param   intelledgerName
     *
     * The name of the <code>Intelledger</code> to check.
     *
     * @return
     *
     * The number of entries associated to the <code>Intelledger</code>,
     * or <b>0</b> if the <code>entries</code> field is <code>null</code>,
     * or <b>-1</b> if the <code>Intelledger</code> does not exist.
     */
    static int size(String intelledgerName) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null) {
            return -1
        }

        if (domain.activeEntries == null) {
            return 0
        }

        return domain.activeEntries.size()
    }

    /**
     * Check if the <code>Intelledger</code> contains an <i>active</i>
     * <code>IntelledgerEntry</code> by a given value.
     *
     * @param   intelledgerName
     *
     * The name of the <code>Intelledger</code> to check.
     *
     * @param   value
     *
     * The <code>IntelledgerEntry.value</code> to check for (converted
     * to <code>String</code>).
     *
     * @return
     *
     * <code>true</code> if and only if the <code>Intelledger</code>
     * exists, and it is associated with a <code>IntelledgerEntry</code>
     * in which the <code>value</code> field matches that which was passed,
     * <i>and</i> that matching entry is <b>active</b>.
     */
    static boolean contains(String intelledgerName, value) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null || !domain.activeEntries) {
            return false
        }

        return domain.activeEntries*.value.contains(value as String)
    }

    /**
     * Check if the <code>Intelledger</code> contains an <i>active</i>
     * <code>IntelledgerEntry</code> by a given key.
     *
     * @param   intelledgerName
     *
     * The name of the <code>Intelledger</code> to check.
     *
     * @param   key
     *
     * The <code>Intelledger.key</code> to check for.
     *
     * @return
     *
     * <code>true</code> if and only if the <code>Intelledger</code>
     * exists, and it is associated with a <code>IntelledgerEntry</code>
     * in which the <code>key</code> field matches that which was passed,
     * <i>and</i> that entry is <b>active</b>.
     */
    static boolean containsKey(String intelledgerName, String key) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null || !domain.activeEntries) {
            return false
        }

        return domain.activeEntries*.key.contains(key)
    }

    /**
     * Retrieve a <code>Intelledger</code> domain and convert all of its
     * <i>active</i> <code>IntelledgerEntries</code> into a
     * <code>Map</code>.
     *
     * @param   intelledgerName
     *
     * The name of the <code>Intelledger</code> to get.
     *
     * @return
     *
     * <code>null</code> if the <code>intelledgerName</code> does not
     * correspond to a <code>Intelledger</code> domain Object; or a
     * <code>Map&lt;String, String&gt;, with the keys corresponding to
     * <code>IntelledgerEntry.key</code> and the values corresponding to
     * <code>IntelledgerEntry.value</code>. The values are <code>String</code>s,
     * so it may be necessary to convert these values to other built-in
     * datatypes (i.e. <code>Integer</code>) as desired / necessary.
     */
    static Map get(String intelledgerName) {
        IntelledgerDomain intelledger = getDomain(intelledgerName)

        // Same behavior as a domain.
        if (intelledger == null) {
            return null
        }

        return intelledger.toMap()
    }

    /**
     * Retrieve a specific active <code>IntelledgerEntry.value</code> from a
     * <code>Intelledger</code>.
     *
     * @param   intelledgerName
     *
     * The name of the <code>Intelledger</code> to retrieve the value
     * from.
     *
     * @param   key
     *
     * The <code>IntelledgerEntry.key</code> identifier to retrieve the
     * value of.
     *
     * @return
     *
     * The appropriate value for the given <code>key</code> in the
     * <code>Intelledger</code> datastructure; or <code>null</code>.
     */
    static String get(String intelledgerName, String key) {
        Map intelledger = get(intelledgerName)

        // Treat it like a Map.
        if (intelledger == null) {
            return null
        }

        return intelledger[key]
    }

    /**
     * Retrieve all versioned entries for a specific <code>key</code>.
     *
     * @param   intelledgerName
     *
     * The name of the <code>Intelledger</code> to retrieve the value
     * from.
     *
     * @param   key
     *
     * The <code>IntelledgerEntry.key</code> identifier to retrieve the
     * value of.
     *
     * @return
     *
     * All of the <code>entries</code> that are identified by the
     * <code>key</code>, regardless of <b>version</b> and <b>active</b>
     * status.
     */
    static List<IntelledgerEntry> getAudit(String intelledgerName, String key) {
        IntelledgerDomain intelledger = getDomain(intelledgerName)

        // Same behavior as a domain.
        if (intelledger == null) {
            return null
        }

        return intelledger.entries.findAll { it.key == key }
    }
    /**
     * Set a <code>Intelledger</code> without interfering with or
     * creating any entries.
     *
     * @param   intelledgerName
     *
     * The <code>Intelledger.name</code> of an existing
     * <code>Intelledger</code> Object; or, if no <code>Intelledger</code>
     * exists by that name, it will be created.
     *
     * @param   intelledgerDesription
     *
     * An optional description to set with the object.
     */
    static void set(String intelledgerName, String intelledgerDesription = null) {
        getCreateDomain(intelledgerName, intelledgerDesription).save(failOnError: true)
    }

    /**
     * Set a <code>Intelledger</code> with (or without)
     * <code>IntelledgerEntries</code>.
     *
     * <p>
     * Sample call
     *
     * <code><pre>
     * String json = '{"a": "apple", "b": "banana", "c": "carrot"}'
     *
     * Intelledger.set('foo', json, 'Using JSON')
     * </pre></code>
     * </p>
     *
     * <p>
     * <b>Note</b>: All datatypes are converted to
     * <code>String</code> via the <code>toString()</code> method;
     * therefore, <code>new Date()</code> would result in
     * {@link java.util.Date#toString() Human-readable} date, whereas
     * <code>new Date().getTime()</code> would result in
     * {@link java.lang.Long#toString() long-timestamp} date.
     * <p>
     *
     * @param   intelledgerName
     *
     * <p>
     * The <code>Intelledger.name</code> of an existing
     * <code>Intelledger</code> Object; or, if no <code>Intelledger</code>
     * exists by that name, it will be created.
     * </p>
     *
     * @param   changeId
     *
     * The Change ID to apply to the changes.
     *
     * @param   json
     *
     * This JSON <code>Map</code> will correspond to
     * <code>IntelledgerEntry</code> Objects. The keys will correspond to
     * <code>IntelledgerEntry.key</code> and the values will correspond
     * to <code>IntelledgerEntry.value</code>. Therefore, keys that do
     * not exist will be created, and keys that already exist will be
     * updated as deactivate <b>Note</b> that
     * the keys and values will be converted to
     * {@link java.lang.String Strings}.
     *
     * @param   intelledgerDesription
     *
     * An optional description to set with the object.
     *
     * @throws  IllegalArgumentException
     *
     * Thrown if any of the keys in the <code>entries</code>
     * <b><code>Map</code></b> are not instances of <b><code>String</code></b>,
     * or if any of the descriptions in the
     * <code>entries.intelledgerDescriptions</code> field are not
     * instances of <b><code>String</code></b>; also inherited from
     * {@link groovy.json.JsonSlurper JsonSlurper}.
     */
    static void fromJson(String intelledgerName, String changeId, String json, String intelledgerDescription = null) throws IllegalArgumentException {
        try {
            Map entries = new JsonSlurper().parseText(json)
            set(entries, intelledgerName, changeId, intelledgerDescription)
        }
        catch (GroovyCastException) {
            throw new IllegalArgumentException("The JSON could not be converted to a Map.")
        }
    }

    /**
     * Set a <code>Intelledger</code> with (or without)
     * <code>IntelledgerEntries</code>.
     *
     * <p>
     * Sample call (using groovy Named parameters):
     *
     * <code><pre>
     * Intelledger.set(
     *     'foo',
     *     'A sample use of Intelledger.set()',
     *     warGames: 'tic-tac-toe',
     *     ten: 4,
     *     x: new Date(),
     *     y: UUID.randomUUID(),
     *     intelledgerDescriptions: [
     *          warGames: 'A strange game. The only winning move is not to play.",
     *          ten: 'A common expression in radio communication.',
     *          x: 'Right now.'
     *     ]
     * )
     * </pre></code>
     * </p>
     *
     * <p>
     * <b>Note</b>: All datatypes are converted to
     * <code>String</code> via the <code>toString()</code> method;
     * therefore, <code>new Date()</code> would result in
     * {@link java.util.Date#toString() Human-readable} date, whereas
     * <code>new Date().getTime()</code> would result in
     * {@link java.lang.Long#toString() long-timestamp} date.
     * <p>
     *
     * @param   entries
     *
     * This <code>Map</code> will correspond to <code>IntelledgerEntry</code>
     * Objects. The keys will correspond to <code>IntelledgerEntry.key</code>
     * and the values will correspond to <code>IntelledgerEntry.value</code>.
     * Therefore, keys that do not exist will be created, and keys that
     * already exist will be updated with deactivate flag.
     * <b>Note</b> that the keys and values will be
     * converted to {@link java.lang.String Strings}.
     *
     * @param   intelledgerName
     *
     * The <code>Intelledger.name</code> of an existing
     * <code>Intelledger</code> Object; or, if no <code>Intelledger</code>
     * exists by that name, it will be created.
     *
     * @param   changeId
     *
     * The Change ID to apply to the changes.
     *
     * @param   intelledgerDesription
     *
     * An optional description to set with the object.
     *
     * @throws  IllegalArgumentException
     *
     * Thrown if any of the keys in the <code>entries</code>
     * <b><code>Map</code></b> are not instances of <b><code>String</code></b>,
     * or if any of the descriptions in the
     * <code>entries.intelledgerDescriptions</code> field are not
     * instances of <b><code>String</code></b>.
     */
    static void set(Map<String, Object> entries, String intelledgerName, String changeId, String intelledgerDesription = null) throws IllegalArgumentException {
        if (entries.any { k, v -> !(k instanceof String) }) {
            throw new IllegalArgumentException("One or more keys in the 'entries' Map is not a String.")
        }

        if (entries.intelledgerDescriptions.any {k, v -> !(v instanceof String) }) {
            throw new IllegalArgumentException("One or more descriptions in the 'entries.intelledgerDescriptions' field is not a String.")
        }

        // Get Intelledger, or create if necessary.
        IntelledgerDomain domain = getCreateDomain(intelledgerName, intelledgerDesription)

        // Omit the smartionaryDescription key so that it does not get
        // added to the Intelledger.
        entries.subMap(entries.keySet() - 'intelledgerDescriptions').each { key, value ->
            _set(domain, changeId, key, value, entries.intelledgerDescriptions?."${key}")
        }

        // TODO: Explicitly declare exceptions thrown on this failure.
        domain.save(failOnError: true)
    }

    /**
     * Set an individual <code>IntelledgerEntry.value</code> in a
     * <code>Intelledger</code>, or create it if it does not exist.
     *
     * <p>
     * <b>Note</b>: All datatypes are converted to
     * <code>String</code> via the <code>toString()</code> method;
     * therefore, <code>new Date()</code> would result in
     * {@link java.util.Date#toString() Human-readable} date, whereas
     * <code>new Date().getTime()</code> would result in
     * {@link java.lang.Long#toString() long-timestamp} date.
     * <p>
     *
     * @param   intelledgerName
     *
     * The <code>Intelledger.name</code> of an existing
     * <code>Intelledger</code> Object; or, if no <code>Intelledger</code>
     * exists by that name, it will be created.
     *
     * @param   changeId
     *
     * The Change ID to apply to the change.
     *
     * @param   key
     *
     * The <code>IntelledgerEntry.key</code> identifier to set the value
     * of (created if it does not exist).
     *
     * @param   value
     *
     * The <code>IntelledgerEntry.value</code> to set.
     *
     * @param   entryDesription
     *
     * An optional description to set with the object.
     *
     * @param   intelledgerDesription
     *
     * An optional description to set with the object.
     */
    static void set(String intelledgerName, String changeId, String key, value, String entryDescription = null, String intelledgerDesription = null) {

        // Get Intelledger, or create if necessary.
        IntelledgerDomain domain = getCreateDomain(intelledgerName, intelledgerDesription)

        _set(domain, changeId, key, value, entryDescription)

        // TODO: Explicitly declare exceptions thrown on this failure.
        domain.save(failOnError: true)
    }

    /**
     * Render an entry effectively deleted.
     *
     * <p>
     * No impact if called on a non-existant <code>Intelledger</code>,
     * or if the <b><code>key</code></b> is not associated to an
     * existing one.
     * <p>
     *
     * <p>
     * No row deletion is performed; this solely marks the lattermost
     * version of the current entry inactive.
     * </p>
     *
     * @param   intelledgerName
     *
     * The <code>Intelledger</code> to act upon.
     *
     * @param   changeId
     *
     * The Change ID to apply to the change.
     *
     * @param   key
     *
     * The <code>IntelledgerEntry.key</code> of the
     * <code>IntelledgerEntry</code> Object to delete.
     */
    static void delete(String intelledgerName, String changeId, String key) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null) {
            return
        }

        IntelledgerEntry entry = domain.entries.find { it.key == key && it.active}

        if (entry != null) {
            _delete(entry, changeId)
        }
    }

    /**
     * Delete an series of <code>IntelledgerEntry</code> objects from a
     * <code>Intelledger</code>.
     *
     * <p>
     * No impact if called on a non-existant <code>Intelledger</code>,
     * or if the <b><code>key</code></b> is not associated to an
     * existing one.
     * <p>
     *
     * @param   intelledgerName
     *
     * The <code>Intelledger</code> to act upon.
     *
     * @param   changeId
     *
     * The Change ID to apply to the changes.
     *
     * @param   keys
     *
     * The <code>IntelledgerEntry.key</code> of the
     * <code>IntelledgerEntry</code> Objects to delete.
     */
    static void delete(String intelledgerName, String changeId, String... keys) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null) {
            return
        }

        keys.each { key ->

            IntelledgerEntry entry = domain.entries.find { it.key == key && it.active}

            if (entry != null) {
                _delete(entry, changeId)
            }
        }

        if (domain.isDirty('entries')) {
            domain.save(flush: true)
        }
    }

    /**
     * Delete any <code>IntelledgerEntry</code> objects associated to
     * a <code>Intelledger</code> that have <code>null</code> values.
     *
     * <p>
     * No impact if the <code>Intelledger</code> does not exist, or if
     * none of its <code>IntelledgerEntry</code> objects have
     * <code>null</code> values.
     * </p>
     *
     * @param   intelledgerName
     *
     * The Name of the <code>Intelledger</code> to act on.
     *
     * @param   changeId
     *
     * The Change ID to apply to the changes.
     */
    static void purgeNulls(String intelledgerName, String changeId) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null) {
            return
        }

        Set nullEntries = domain.entries.findAll { it.value == null && it.active }

        if (nullEntries.isEmpty()) {
            return
        }

        nullEntries.each {
            _delete(entry, changeId)
        }

        if (domain.isDirty('entries')) {
            domain.save(flush: true)
        }
    }

    /**
     * Delete <b>all</b> <code>IntelledgerEntry</code> objects associated
     * to a <code>Intelledger</code>, leaving the <code>Intelledger</code>
     * empty.
     *
     * <p>
     * No impact if the <code>Intelledger</code> does not exist.
     * </p>
     *
     * @param   intelledgerName
     *
     * The Name of the <code>Intelledger</code> to act on.
     *
     * @param   changeId
     *
     * The Change ID to apply to the changes.
     */
    static void purge(String intelledgerName, String changeId) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null || !domain.entries) {
            return
        }

        domain.activeEntries.each { entry ->
            _delete(entry, changeId)
        }

        domain.save(flush: true)
    }

    /**
     * Retrieve a <code>Intelledger</code> domain Object by name.
     *
     * @param   intelledgerName
     *
     * The Name of the <code>Intelledger</code> to act on.
     *
     * @return
     *
     * The <code>Intelledger</code> domain Object, or <code>null</code>.
     */
    static private IntelledgerDomain getDomain(String intelledgerName) {
        return IntelledgerDomain.findByName(intelledgerName)
    }

    /**
     * Retrieve a <code>Intelledger</code> object by name, or
     * create one if it does not exist.
     *
     * @param   intelledgerName
     *
     * The <code>Intelledger.name</code> of an existing
     * <code>Intelledger</code> Object; or, if no <code>Intelledger</code>
     * exists by that name, it will be created.
     *
     * @param   intelledgerDesription
     *
     * An optional description to set with the object.
     */
    static private IntelledgerDomain getCreateDomain(String intelledgerName, String intelledgerDesription = null) {
        IntelledgerDomain domain = getDomain(intelledgerName)

        if (domain == null) {
            domain = new IntelledgerDomain(name: intelledgerName)
        }

        domain.description = (intelledgerDesription ?: domain.description)

        return domain
    }

    /**
     * Set an individual <code>IntelledgerEntry.value</code> in a
     * <code>Intelledger</code>, or create it if it does not exist.
     * If any entry is already existed with the key value, then set active
     * flag to false and add new entry as an activated one.
     *
     * <p>
     * <b>Note</b>: All datatypes are converted to
     * <code>String</code> via the <code>toString()</code> method;
     * therefore, <code>new Date()</code> would result in
     * {@link java.util.Date#toString() Human-readable} date, whereas
     * <code>new Date().getTime()</code> would result in
     * {@link java.lang.Long#toString() long-timestamp} date.
     * <p>
     *
     * @param   domain
     *
     * The <code>Intelledger</code> object to act on.
     *
     * @param   changeId
     *
     * The Change ID to apply to the changes.
     *
     * @param   key
     *
     * The <code>IntelledgerEntry.key</code> identifier to set the value
     * of (created if it does not exist).
     *
     * @param   value
     *
     * The <code>IntelledgerEntry.value</code> to set.
     *
     * @param   desription
     *
     * An optional description to set with the object.
     */
    static private void _set(IntelledgerDomain domain, String key, String changeId, value, String description = null) {
        // Search for the particular entry.
        IntelledgerEntry oldEntry = domain.activeEntries.find { it.key == key }

        // Mark old entry inactive.
        if (oldEntry) {
            oldEntry.active = false
        }

        // Create the new entry.
        IntelledgerEntry newEntry = new IntelledgerEntry(
            key: key,
            value: (value as String),
            description: description,
            changeId: changeId,
            keyVersion: determineNextVersion(domain, key),
            active: true
        )

        domain.addToEntries(newEntry)
        domain.save(failOnError: true)
    }

    /**
     * Render an entry effectively deleted.
     *
     * <p>
     * This does not actually delete the entry, but rather marks the
     * entry as inactive, and creates a new inactive entry with the
     * <code>changeId</code> applied, such that a historical record is
     * made of what entity "deleted" the entry.
     * </p>
     *
     * @param   entry
     *
     * The entry to be deleted.
     *
     * @param   changeId
     *
     * The Change ID to apply to this change.
     */
    static private void _delete(IntelledgerEntry entry, String changeId) {
        IntelledgerDomain domain = entry.intelledger

        // Mark the current entry inactive.
        entry.active = false

        // Create the new inactive entry with the updated Change ID.
        IntelledgerEntry newEntry = new IntelledgerEntry(
            key: entry.key,
            value: entry.value,
            description: entry.description,
            changeId: changeId,
            keyVersion: determineNextVersion(domain, entry.key),
            active: false
        )

        domain.addToEntries(newEntry)
        domain.save(failOnError: true)
    }

    /**
     * Determine the next version number of an entry, for predictive or
     * insertion purposes.
     *
     * @param   intelledger
     *
     * The <b><code>Intelledger</code></b> Object to work on.
     *
     * @param   key
     *
     * The entry key to query for.
     *
     * @return
     *
     * If an entry with the key exists, it will return (regardless of
     * <b>active</b> status) the lattermost version number of that entry
     * <i>plus</i> one (1) (minimum value of two (2)); otherwise, it
     * will return one (1), as the initial version of the currently
     * non-existant key.
     */
    private static int determineNextVersion(IntelledgerDomain domain, String key) {
        IntelledgerEntry entry = domain.findLastVersionOf(key)

        if (entry) {
            return (entry.version + 1)
        }

        return 1
    }
}
