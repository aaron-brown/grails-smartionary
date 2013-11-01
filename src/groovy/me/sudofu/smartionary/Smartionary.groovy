package me.sudofu.smartionary

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.JsonException

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import me.sudofu.smartionary.domain.Smartionary as SmartionaryDomain
import me.sudofu.smartionary.domain.SmartionaryEntry

/**
 * The primary progremmatic interface for
 * <b><code>Smartionary</code></b>.
 *
 * @author Aaron Brown
 */
class Smartionary {

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
     * <p>
     * <code>null</code> if the <code>smartionaryName</code> does not
     * correspond to a <code>Smartionary</code> domain Object; or a
     * <code>Map&lt;String, String&gt;, with the keys corresponding to
     * <code>SmartionaryEntry.key</code> and the values corresponding to
     * <code>SmartionaryEntry.value</code>. The values are <code>String</code>s,
     * so it may be necessary to convert these values to other built-in
     * datatypes (i.e. <code>Integer</code>) as desired / necessary.
     * </p>
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
     * <p>
     * The appropriate value for the given <code>key</code> in the
     * <code>Smartionary</code> datastructure; or <code>null</code>.
     * </p>
     */
    static String get(String smartionaryName, String key) {
        Map smartionary = get(smartionaryName)

        // Treat it like a Map.
        if (smartionary == null) {
            return null
        }

        return (smartionary[key])
    }

    static String getAsJson(String smartionaryName, boolean pretty = false) throws IllegalArgumentException, JsonException {
        Map map = get('foo')

        if (pretty) {
            return "${new JsonBuilder(map).toPrettyString()}"
        }
        return "${new JsonBuilder(map)}"
    }

    static void set(String smartionaryName, String smartionaryDescription = null) {
        getCreateDomain(smartionaryName, smartionaryDescription).save(failOnError: true)
    }

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
     * <p>
     * This <code>Map</code> will correspond to <code>SmartionaryEntry</code>
     * Objects. The keys will correspond to <code>SmartionaryEntry.key</code>
     * and the values will correspond to <code>SmartionaryEntry.value</code>.
     * Therefore, keys that do not exist will be created, and keys that
     * already exist will be updated (just like a regular
     * <code>Map</code>); <b>Note</b> that the keys and values will be
     * converted to {@link java.lang.String Strings}.
     * </p>
     *
     * @param   smartionaryName
     *
     * <p>
     * The <code>Smartionary.name</code> of an existing
     * <code>Smartionary</code> Object; or, if no <code>Smartionary</code>
     * exists by that name, it will be created.
     * </p>
     *
     * @throws  IllegalArgumentException
     *
     * Thrown if any of the keys in the <code>entries</code>
     * <b><code>Map</code></b> are not instances of <b><code>String</code></b>,
     * or if any of the descriptions in the
     * <code>entries.smartionaryDescriptions</code> field are not
     * instances of <b><code>String</code></b>.
     */
    static void set(Map<String, Object> entries, String smartionaryName, String smartionaryDescription = null) throws Exception, IllegalArgumentException {
        if (entries.any { k, v -> ((k instanceof String) == false) }) {
            throw new IllegalArgumentException("One or more keys in the 'entries' Map is not a String.")
        }

        if (entries.smartionaryDescriptions.any {k, v -> ((v instanceof String) == false) }) {
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
     */
    static void set(String smartionaryName, String key, Object value, String entryDescription = null, String smartionaryDescription = null) throws Exception {

        // Get Smartionary, or create if necessary.
        SmartionaryDomain domain = getCreateDomain(smartionaryName, smartionaryDescription)

        _set(domain, key, value, entryDescription)

        // TODO: Explicitly declare exceptions thrown on this failure.
        domain.save(failOnError: true)
    }

    /**
     * Delete a <code>Smartionary</code> (and its
     * <code>SmartionaryEntries</code>).
     *
     * @param   smartionaryName
     *
     * The Name of the <code>Smartionary</code> Object to delete; will
     * also delete the <code>SmartionaryEntry</code> Objects associated
     * with it.
     */
    static void delete(String smartionaryName) throws Exception {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain != null) {
            domain.delete(flush: true)
        }
    }

    /**
     * Delete an <code>SmartionaryEntry</code> from a <code>Smartionary</code>.
     *
     * @param   smartionaryName
     *
     * The <code>Smartionary</code> to act upon.
     *
     * @param   key
     *
     * The <code>SmartionaryEntry.key</code> of the <code>SmartionaryEntry</code> Object to
     * delete.
     */
    static void delete(String smartionaryName, String key) throws Exception {
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
     * Purge <code>null</code> entries from a <code>Smartionary</code>.
     *
     * @param   smartionaryName
     *
     * The Name of the <code>Smartionary</code> to act on.
     */
    static void purgeNulls(String smartionaryName) throws Exception {
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

    static private SmartionaryDomain getCreateDomain(String smartionaryName, String description = null) {
        SmartionaryDomain domain = getDomain(smartionaryName)

        if (domain == null) {
            domain = new SmartionaryDomain(
                name: smartionaryName,
            )
        }

        domain.description = (description ?: domain.description)

        return domain
    }

    static private void _set(SmartionaryDomain domain, String key, Object value, String description = null) {

        // Search for the particular entry.
        SmartionaryEntry entry = domain.entries.find { key == it.key }

        // Create it if it does not exist.
        if (entry == null) {
            entry = new SmartionaryEntry(
                key: key,
                value: (value as String),
                description: description
            )

            domain.addToEntries(entry)
        }
        else {
            // Groovy implicitly calls toString() if present.
            //
            // If not, then the class's runtime identity is used.
            entry.value = value
            entry.description = description
        }
    }
}
