package me.sudofu.smartionary

import static org.junit.Assert.*
import org.junit.*

import me.sudofu.smartionary.Smartionary

import me.sudofu.smartionary.domain.Smartionary as SmartionaryDomain
import me.sudofu.smartionary.domain.SmartionaryEntry as Entry

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.JsonException

class SmartionaryTests extends GroovyTestCase {

    Date date

    UUID uuid

    Map entries

    Map entryDescriptions

    @Before
    void setUp() {
        date = new Date(100000)

        uuid = new UUID(12345, 67890)

        entries = [
            ten: 4,
            time: date,
            uuid: uuid,
            warGames: 'tic-tac-toe',
        ]

        entryDescriptions = [
            warGames: "A strange game. The only winning move is not to play.",
            ten: "Code for confirmation in Radio communications.",
            time: "A fixed timestamp."
        ]
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testDefault() {
        assert true
    }

    @Test
    void testNullGets() {
        assertNull(Smartionary.get('foo'))
        assertNull(Smartionary.get('foo', 'bar'))
    }

    @Test
    void testSetWithoutEntries() {
        Smartionary.set('foo')

        Map m = Smartionary.get('foo')

        assertNotNull(m)
        assertTrue(m.isEmpty())

        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertNull(domain.description)
        assertNull(domain.entries)

        Smartionary.set('foo', 'smartionaryDescription')

        m = Smartionary.get('foo')

        assertNotNull(m)
        assertTrue(m.isEmpty())

        domain = SmartionaryDomain.findByName('foo')

        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertEquals('smartionaryDescription', domain.description)
        assertNull(domain.entries)
    }

    void testSetExplicitEntry() {
        Smartionary.set('foo')

        Map m = Smartionary.get('foo')

        assertNotNull(m)
        assertTrue(m.isEmpty())

        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertNull(domain.description)
        assertNull(domain.entries)

        Smartionary.set('foo', 'bar', 'baz')

        m = Smartionary.get('foo')

        assertNotNull(m)
        assertFalse(m.isEmpty())
        assertEquals(1, m.size())
        assertEquals([bar: 'baz'], m)

        domain = SmartionaryDomain.findByName('foo')

        assertNotNull(domain)
        assertNotNull(domain.entries)
        assertEquals(1, domain.entries.size())

        Entry entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'bar')
            }
        }

        assertNotNull(entry)
        assertEquals('bar', entry.key)
        assertEquals('baz', entry.value)
        assertNull(entry.description)
        assertEquals(m."${entry.key}", entry.value)

        Smartionary.set('foo', 'bar', 'bat', 'entryDescription', 'smartionaryDescription')

        m = Smartionary.get('foo')

        assertNotNull(m)
        assertFalse(m.isEmpty())
        assertEquals(1, m.size())
        assertEquals([bar: 'bat'], m)

        domain = SmartionaryDomain.findByName('foo')

        assertNotNull(domain)
        assertEquals('smartionaryDescription', domain.description)
        assertNotNull(domain.entries)
        assertEquals(1, domain.entries.size())

        entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'bar')
            }
        }

        assertNotNull(entry)
        assertEquals('bar', entry.key)
        assertEquals('bat', entry.value)
        assertEquals('entryDescription', entry.description)
        assertEquals(m."${entry.key}", entry.value)
    }

    void testPurgeNulls() {
        Smartionary.set('foo', 'one', null)
        Smartionary.set('foo', 'two', 2)

        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')
        assertNotNull(domain)

        Map m = Smartionary.get('foo')

        assertNotNull(m)
        assertFalse(m.isEmpty())
        assertEquals(2, m.size())
        assertEquals([one: null, two: "2"], m)

        assertEquals(2, domain.entries.size())

        Smartionary.purgeNulls('foo')

        domain = SmartionaryDomain.findByName('foo')
        assertNotNull(domain)

        m = Smartionary.get('foo')

        assertNotNull(m)
        assertEquals(1, m.size())
        assertEquals([two: "2"], m)

        assertEquals(1, domain.entries.size())

        Entry entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'one')
            }
        }

        assertNull(entry)
    }

    void testSetEntriesByMap() {
        Smartionary.set(
            'foo',
            warGames: entries.warGames,
            ten: entries.ten,
            time: entries.time,
            uuid: entries.uuid,
            smartionaryDescriptions: [
                warGames: entryDescriptions.warGames,
                ten: entryDescriptions.ten,
                time: entryDescriptions.time,
            ]
        )

        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')
        assertNotNull(domain)
        assertNull(domain.description)

        Map m = Smartionary.get('foo')

        // Convert non-String values to Strings.
        entries.ten = (entries.ten as String)
        entries.time = (entries.time as String)
        entries.uuid = (entries.uuid as String)

        assertNotNull(m)
        assertFalse(m.isEmpty())
        assertEquals(4, m.size())
        assertEquals(entries, m)

        assertEquals(4, domain.entries.size())

        Entry entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'time')
            }
        }

        assertNotNull(entry)
        assertEquals((entries.time as String), entry.value)
        assertEquals(entries."${entry.key}", entry.value)

        domain.entries.each {
            assertEquals(it.description, entryDescriptions."${it.key}")
        }

        entries.myKey = "myVal"

        Smartionary.set(
            'foo',
            'smartionaryDescription',
            myKey: entries.myKey,
            time: new Date(),
            smartionaryDescriptions: [
                ignoredDescription: "This one will be ignored."
            ]
        )

        domain = SmartionaryDomain.findByName('foo')
        assertNotNull(domain)
        assertEquals('smartionaryDescription', domain.description)

        m = Smartionary.get('foo')

        assertNotNull(m)
        assertFalse(m.isEmpty())
        assertEquals(5, m.size())
        assertEquals(entries.subMap(entries.keySet() - 'time'), m.subMap(m.keySet() - 'time'))

        assertEquals(5, domain.entries.size())

        entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'time')
            }
        }

        assertNotNull(entry)
        assertFalse((entries.time as String) == entry.value)
        assertFalse(entries."${entry.key}" == entry.value)

        assertFalse(domain.entries.description.contains('This will be ignored.'))
    }

    void testWithJson() {
        String jsonEntries = '{"a":"apple","b":"banana","c":"carrot"}'

        Smartionary.fromJson('foo', jsonEntries)

        assertEquals(jsonEntries, Smartionary.getAsJson('foo'))

        String invalidJsonEntry = '["a", "apple", "b", "banana", "c", "carrot"]'

        def fail = shouldFail(IllegalArgumentException) {
            Smartionary.fromJson('bar', invalidJsonEntry)
        }

        assertNull(Smartionary.get('bar'))
    }

    void testDelete() {
        assertNull(SmartionaryDomain.findByName('foo'))
        Smartionary.delete('foo')
        Smartionary.delete('foo', 'bar')

        Smartionary.set(entries, 'foo')

        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        assertNotNull(domain)

        Map m = Smartionary.get('foo')

        assertNotNull(m)
        assertEquals(4, m.size())

        assertNull(domain.entries.find { it.key == 'bar' })
        assertNull(m.bar)

        Smartionary.delete('foo', 'bar')

        assertEquals(4, m.size())
        assertEquals(4, domain.entries.size())

        assertNotNull(domain.entries.find { it.key == 'time'})
        assertNotNull(m.time)

        Smartionary.delete('foo', 'time')

        m = Smartionary.get('foo')
        assertNotNull(m)
        assertEquals(3, m.size())
        assertEquals(3, domain.entries.size())

        assertNull(domain.entries.find { it.key == 'time' })
        assertNull(m.time)

        Smartionary.delete('foo')

        assertNull(SmartionaryDomain.findByName('foo'))
        assertNull(Smartionary.get('foo'))

        List<Entry> nonExistentEntries = Entry.withCriteria {
            smartionary {
                eq('name', 'foo')
            }
        }

        assertTrue(nonExistentEntries.isEmpty())
    }
}
