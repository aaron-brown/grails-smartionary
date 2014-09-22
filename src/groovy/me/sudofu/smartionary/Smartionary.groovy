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
package me.sudofu.smartionary

import groovy.json.JsonBuilder
import groovy.json.JsonException
import groovy.json.JsonSlurper
import me.sudofu.smartionary.domain.Smartionary as SmartionaryDomain
import me.sudofu.smartionary.domain.SmartionaryEntry

/**
 * The primary progremmatic interface for
 * <b><code>Smartionary</code></b>.
 *
 * <p>
 * <b><code>Smartionary</code></b> provides a flexible and progremmatic
 * interface for storing information in a <b>Grails</b> Domain that
 * mimics the structure of a single-level <code>Map</code>.
 * <p>
 *
 * <p>
 * Instantiating the Domain information can be done directly via a
 * <code>Map</code>, and/or updated individually. For example:
 *
 * <code><pre>
 * Smartionary.set(
 *     'mySmartionary',
 *     'This is a sample Smartionary instantiation.',
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
 * The <code>Smartionary</code> can then retrieve the Domain as a
 * <code>Map</code> later like so:
 *
 * <code><pre>
 * Map myElements = Smartionary.get('mySmartionary')
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
 * <b><code>Smartionary</code></b> does not interface with retrieving
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
class Smartionary {

    /**
     * Retrieve the number of <code>SmartionaryEntry</code> objects are
     * associated with a <code>Smartionary</code>.
     *
     * @param   smartionaryName
     *
     * The name of the <code>Smartionary</code> to check.
     *
     * @return
     *
     * The number of entries associated to the <code>Smartionary</code>,
     * or <b>0</b> if the <code>entries</code> field is <code>null</code>,
     * or <b>-1</b> if the <code>Smartionary</code> does not exist.
     */
    static int size(String smartionaryName) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null) {
            return -1
        }

        if (domain.entries == null) {
            return 0
        }

        return domain.entries.size()
    }

    /**
     * Check if the <code>Smartionary</code> contains a
     * <code>SmartionaryEntry</code> by a given value.
     *
     * @param   smartionaryName
     *
     * The name of the <code>Smartionary</code> to check.
     *
     * @param   value
     *
     * The <code>SmartionaryEntry.value</code> to check for (converted
     * to <code>String</code>).
     *
     * @return
     *
     * <code>true</code> if and only if the <code>Smartionary</code>
     * exists, and it is associated with a <code>SmartionaryEntry</code>
     * in which the <code>value</code> field matches that which was passed.
     */
    static boolean contains(String smartionaryName, value) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null || !domain.entries) {
            return false
        }

        return domain.entries*.value.contains(value as String)
    }

    /**
     * Check if the <code>Smartionary</code> contains a
     * <code>SmartionaryEntry</code> by a given key.
     *
     * @param   smartionaryName
     *
     * The name of the <code>Smartionary</code> to check.
     *
     * @param   key
     *
     * The <code>SmartionaryEntry.key</code> to check for.
     *
     * @return
     *
     * <code>true</code> if and only if the <code>Smartionary</code>
     * exists, and it is associated with a <code>SmartionaryEntry</code>
     * in which the <code>key</code> field matches that which was passed.
     */
    static boolean containsKey(String smartionaryName, String key) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null || !domain.entries) {
            return false
        }

        return domain.entries*.key.contains(key)
    }

    /**
     * Retrieve a <code>Smartionary</code> domain and convert all of its
     * <code>SmartionaryEntries</code> into a <code>Map</code>.
     *
     * @param   smartionaryName
     *
     * The name of the <code>Smartionary</code> to get.
     *
     * @return
     *
     * <code>null</code> if the <code>smartionaryName</code> does not
     * correspond to a <code>Smartionary</code> domain Object; or a
     * <code>Map&lt;String, String&gt;, with the keys corresponding to
     * <code>SmartionaryEntry.key</code> and the values corresponding to
     * <code>SmartionaryEntry.value</code>. The values are <code>String</code>s,
     * so it may be necessary to convert these values to other built-in
     * datatypes (i.e. <code>Integer</code>) as desired / necessary.
     */
    static Map get(String smartionaryName) {
        SmartionaryDomain smartionary = getDomain(smartionaryName)

        // Same behavior as a domain.
        if (smartionary == null) {
            return null
        }

        return smartionary.toMap()
    }

    /**
     * Retrieve a specific <code>SmartionaryEntry.value</code> from a
     * <code>Smartionary</code>.
     *
     * @param   smartionaryName
     *
     * The name of the <code>Smartionary</code> to retrieve the value
     * from.
     *
     * @param   key
     *
     * The <code>SmartionaryEntry.key</code> identifier to retrieve the
     * value of.
     *
     * @return
     *
     * The appropriate value for the given <code>key</code> in the
     * <code>Smartionary</code> datastructure; or <code>null</code>.
     */
    static String get(String smartionaryName, String key) {
        Map smartionary = get(smartionaryName)

        // Treat it like a Map.
        if (smartionary == null) {
            return null
        }

        return smartionary[key]
    }

    /**
     * Retrieve a <code>Smartionary</code> as JSON.
     *
     * @param   smartionaryName
     *
     * The name of the <code>Smartionary</code> to retrieve as JSON.
     *
     * @param   pretty
     *
     * Optionally specify whether or not to return pretty-formatted
     * JSON through {@link groovy.json.JsonBuilder#toPrettyString()}.
     *
     * @return
     *
     * <code>{"a":"apple","b":"banana","c","carrot"}</code>
     */
    static String getAsJson(String smartionaryName, boolean pretty = false) throws IllegalArgumentException, JsonException {
        Map map = get('foo')

        if (pretty) {
            return "${new JsonBuilder(map).toPrettyString()}"
        }
        return "${new JsonBuilder(map)}"
    }

    /**
     * Set a <code>Smartionary</code> without interfering with or
     * creating any entries.
     *
     * @param   smartionaryName
     *
     * The <code>Smartionary.name</code> of an existing
     * <code>Smartionary</code> Object; or, if no <code>Smartionary</code>
     * exists by that name, it will be created.
     *
     * @param   smartionaryDesription
     *
     * An optional description to set with the object.
     */
    static void set(String smartionaryName, String smartionaryDescription = null) {
        getCreateDomain(smartionaryName, smartionaryDescription).save(failOnError: true)
    }

    /**
     * Set a <code>Smartionary</code> with (or without)
     * <code>SmartionaryEntries</code>.
     *
     * <p>
     * Sample call
     *
     * <code><pre>
     * String json = '{"a": "apple", "b": "banana", "c": "carrot"}'
     *
     * Smartionary.set('foo', json, 'Using JSON')
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
     * @param   smartionaryName
     *
     * <p>
     * The <code>Smartionary.name</code> of an existing
     * <code>Smartionary</code> Object; or, if no <code>Smartionary</code>
     * exists by that name, it will be created.
     * </p>
     *
     * @param   json
     *
     * This JSON <code>Map</code> will correspond to
     * <code>SmartionaryEntry</code> Objects. The keys will correspond to
     * <code>SmartionaryEntry.key</code> and the values will correspond
     * to <code>SmartionaryEntry.value</code>. Therefore, keys that do
     * not exist will be created, and keys that already exist will be
     * updated (just like a regular <code>Map</code>); <b>Note</b> that
     * the keys and values will be converted to
     * {@link java.lang.String Strings}.
     *
     * @param   smartionaryDesription
     *
     * An optional description to set with the object.
     *
     * @throws  IllegalArgumentException
     *
     * Thrown if any of the keys in the <code>entries</code>
     * <b><code>Map</code></b> are not instances of <b><code>String</code></b>,
     * or if any of the descriptions in the
     * <code>entries.smartionaryDescriptions</code> field are not
     * instances of <b><code>String</code></b>; also inherited from
     * {@link groovy.json.JsonSlurper JsonSlurper}.
     */
    static void fromJson(String smartionaryName, String json, String smartionaryDescription = null) throws IllegalArgumentException {
        try {
            Map entries = new JsonSlurper().parseText(json)
            set(entries, smartionaryName, smartionaryDescription)
        }
        catch (GroovyCastException) {
            throw new IllegalArgumentException("The JSON could not be converted to a Map.")
        }
    }

    /**
     * Set a <code>Smartionary</code> with (or without)
     * <code>SmartionaryEntries</code>.
     *
     * <p>
     * Sample call (using groovy Named parameters):
     *
     * <code><pre>
     * Smartionary.set(
     *     'foo',
     *     'A sample use of Smartionary.set()',
     *     warGames: 'tic-tac-toe',
     *     ten: 4,
     *     x: new Date(),
     *     y: UUID.randomUUID(),
     *     smartionaryDescriptions: [
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
     * This <code>Map</code> will correspond to <code>SmartionaryEntry</code>
     * Objects. The keys will correspond to <code>SmartionaryEntry.key</code>
     * and the values will correspond to <code>SmartionaryEntry.value</code>.
     * Therefore, keys that do not exist will be created, and keys that
     * already exist will be updated (just like a regular
     * <code>Map</code>); <b>Note</b> that the keys and values will be
     * converted to {@link java.lang.String Strings}.
     *
     * @param   smartionaryName
     *
     * The <code>Smartionary.name</code> of an existing
     * <code>Smartionary</code> Object; or, if no <code>Smartionary</code>
     * exists by that name, it will be created.
     *
     * @param   smartionaryDesription
     *
     * An optional description to set with the object.
     *
     * @throws  IllegalArgumentException
     *
     * Thrown if any of the keys in the <code>entries</code>
     * <b><code>Map</code></b> are not instances of <b><code>String</code></b>,
     * or if any of the descriptions in the
     * <code>entries.smartionaryDescriptions</code> field are not
     * instances of <b><code>String</code></b>.
     */
    static void set(Map<String, Object> entries, String smartionaryName, String smartionaryDescription = null) throws IllegalArgumentException {
        if (entries.any { k, v -> !(k instanceof String) }) {
            throw new IllegalArgumentException("One or more keys in the 'entries' Map is not a String.")
        }

        if (entries.smartionaryDescriptions.any {k, v -> !(v instanceof String) }) {
            throw new IllegalArgumentException("One or more descriptions in the 'entries.smartionaryDescriptions' field is not a String.")
        }

        // Get Smartionary, or create if necessary.
        SmartionaryDomain domain = getCreateDomain(smartionaryName, smartionaryDescription)

        // Omit the smartionaryDescription key so that it does not get
        // added to the Smartionary.
        entries.subMap(entries.keySet() - 'smartionaryDescriptions').each { key, value ->
            _set(domain, key, value, entries.smartionaryDescriptions?."${key}")
        }

        // TODO: Explicitly declare exceptions thrown on this failure.
        domain.save(failOnError: true)
    }

    /**
     * Set an individual <code>SmartionaryEntry.value</code> in a
     * <code>Smartionary</code>, or create it if it does not exist.
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
     * @param   smartionaryName
     *
     * The <code>Smartionary.name</code> of an existing
     * <code>Smartionary</code> Object; or, if no <code>Smartionary</code>
     * exists by that name, it will be created.
     *
     * @param   key
     *
     * The <code>SmartionaryEntry.key</code> identifier to set the value
     * of (created if it does not exist).
     *
     * @param   value
     *
     * The <code>SmartionaryEntry.value</code> to set.
     *
     * @param   entryDesription
     *
     * An optional description to set with the object.
     *
     * @param   smartionaryDesription
     *
     * An optional description to set with the object.
     */
    static void set(String smartionaryName, String key, value, String entryDescription = null, String smartionaryDescription = null, String updatedBy = null) {

        // Get Smartionary, or create if necessary.
        SmartionaryDomain domain = getCreateDomain(smartionaryName, smartionaryDescription)

        _set(domain, key, value, entryDescription, updatedBy)

        // TODO: Explicitly declare exceptions thrown on this failure.
        domain.save(failOnError: true)
    }

    /**
     * Delete a <code>Smartionary</code> (and its
     * <code>SmartionaryEntries</code>).
     *
     * <p>No impact if called on a non-existant <code>Smartionary</code>.<p>
     *
     * @param   smartionaryName
     *
     * The Name of the <code>Smartionary</code> Object to delete; will
     * also delete the <code>SmartionaryEntry</code> Objects associated
     * with it.
     */
    static void delete(String smartionaryName) {
        SmartionaryDomain domain = getDomain(smartionaryName)

		  domain?.delete(flush: true)
    }

    /**
     * Delete an <code>SmartionaryEntry</code> from a <code>Smartionary</code>.
     *
     * <p>
     * No impact if called on a non-existant <code>Smartionary</code>,
     * or if the <b><code>key</code></b> is not associated to an
     * existing one.
     * <p>
     *
     * @param   smartionaryName
     *
     * The <code>Smartionary</code> to act upon.
     *
     * @param   key
     *
     * The <code>SmartionaryEntry.key</code> of the
     * <code>SmartionaryEntry</code> Object to delete.
     */
    static void delete(String smartionaryName, String key) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null) {
            return
        }

        SmartionaryEntry entry = domain.entries.find { it.key == key }

        if (entry != null) {
            domain.removeFromEntries(entry)
            entry.delete()
            domain.save(flush: true)
        }
    }

    /**
     * Delete an series of <code>SmartionaryEntry</code> objects from a
     * <code>Smartionary</code>.
     *
     * <p>
     * No impact if called on a non-existant <code>Smartionary</code>,
     * or if the <b><code>key</code></b> is not associated to an
     * existing one.
     * <p>
     *
     * @param   smartionaryName
     *
     * The <code>Smartionary</code> to act upon.
     *
     * @param   keys
     *
     * The <code>SmartionaryEntry.key</code> of the
     * <code>SmartionaryEntry</code> Objects to delete.
     */
    static void delete(String smartionaryName, String... keys) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null) {
            return
        }

        keys.each { key ->

            SmartionaryEntry entry = domain.entries.find { it.key == key }

            if (entry != null) {
                domain.removeFromEntries(entry)
                entry.delete()
            }
        }

        if (domain.isDirty('entries')) {
            domain.save(flush: true)
        }
    }

    /**
     * Delete any <code>SmartionaryEntry</code> objects associated to
     * a <code>Smartionary</code> that have <code>null</code> values.
     *
     * <p>
     * No impact if the <code>Smartionary</code> does not exist, or if
     * none of its <code>SmartionaryEntry</code> objects have
     * <code>null</code> values.
     * </p>
     *
     * @param   smartionaryName
     *
     * The Name of the <code>Smartionary</code> to act on.
     */
    static void purgeNulls(String smartionaryName) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null) {
            return
        }

        Set nullEntries = domain.entries.findAll {
            it.value == null
        }

        if (nullEntries.isEmpty()) {
            return
        }

        nullEntries.each {
            domain.removeFromEntries(it)
            it.delete()
        }

        domain.save(flush: true)
    }

    /**
     * Delete <b>all</b> <code>SmartionaryEntry</code> objects associated
     * to a <code>Smartionary</code>, leaving the <code>Smartionary</code>
     * empty.
     *
     * <p>
     * No impact if the <code>Smartionary</code> does not exist.
     * </p>
     *
     * @param   smartionaryName
     *
     * The Name of the <code>Smartionary</code> to act on.
     */
    static void purge(String smartionaryName) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null || !domain.entries) {
            return
        }

        domain.entries.clear()

        domain.save(flush: true)
    }

    /**
     * Retrieve a <code>Smartionary</code> domain Object by name.
     *
     * @param   smartionaryName
     *
     * The Name of the <code>Smartionary</code> to act on.
     *
     * @return
     *
     * The <code>Smartionary</code> domain Object, or <code>null</code>.
     */
    static private SmartionaryDomain getDomain(String smartionaryName) {
        return SmartionaryDomain.findByName(smartionaryName)
    }

    /**
     * Retrieve a <code>Smartionary</code> object by name, or
     * create one if it does not exist.
     *
     * @param   smartionaryName
     *
     * The <code>Smartionary.name</code> of an existing
     * <code>Smartionary</code> Object; or, if no <code>Smartionary</code>
     * exists by that name, it will be created.
     *
     * @param   smartionaryDesription
     *
     * An optional description to set with the object.
     */
    static private SmartionaryDomain getCreateDomain(String smartionaryName, String description = null) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null) {
            domain = new SmartionaryDomain(name: smartionaryName)
        }

        domain.description = (description ?: domain.description)

        return domain
    }

    /**
     * Set an individual <code>SmartionaryEntry.value</code> in a
     * <code>Smartionary</code>, or create it if it does not exist.
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
     * The <code>Smartionary</code> object to act on.
     *
     * @param   key
     *
     * The <code>SmartionaryEntry.key</code> identifier to set the value
     * of (created if it does not exist).
     *
     * @param   value
     *
     * The <code>SmartionaryEntry.value</code> to set.
     *
     * @param   desription
     *
     * An optional description to set with the object.
     */
    static private void _set(SmartionaryDomain domain, String key, value, String description = null, String updatedBy = null) {

        // Search for the particular entry.
        SmartionaryEntry oldEntry = domain.entries.find { key == it.key && it.active == true }

        // Create it if it does not exist.
        SmartionaryEntry newEntry = new SmartionaryEntry(
                key: key,
                value: (value as String),
                description: description,
                updatedBy: updatedBy,
                keyVersion : 1,
                active : true
                )

        if (oldEntry != null) {
            oldEntry.active = false
            newEntry.keyVersion = oldEntry.keyVersion + 1
        }

        domain.addToEntries(newEntry)
    }
}
