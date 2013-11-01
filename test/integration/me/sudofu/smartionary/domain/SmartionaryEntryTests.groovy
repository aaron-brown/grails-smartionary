package me.sudofu.smartionary.domain

import static org.junit.Assert.*
import org.junit.*

import me.sudofu.smartionary.domain.Smartionary
import me.sudofu.smartionary.domain.SmartionaryEntry as Entry

class SmartionaryEntryTests {

    Smartionary smartionary

    @Before
    void setUp() {
        smartionary = new Smartionary( name: "footionary" )
        smartionary.save(flush: true)
    }

    @After
    void tearDown() {

    }

    @Test
    void testDefault() {
        assert true
    }

    @Test
    void testSimpleEntry() {
        Entry entry = new Entry(
            key: "foo",
            value: "bar",
            description: "baz")

        smartionary.addToEntries(entry)
        assertTrue(entry.validate())

        assertEquals("foo", entry.key)
        assertEquals("bar", entry.value)
        assertEquals("baz", entry.description)
    }

    @Test
    void testUniqueConstraint() {
        Entry entry = new Entry(
            key: "foo",
            value: "bar",
            description: "baz")

        smartionary.addToEntries(entry)
        assertTrue(entry.validate())

        assertEquals("foo", entry.key)
        assertEquals("bar", entry.value)
        assertEquals("baz", entry.description)

        smartionary.save(flush: true)

        Entry duplicateEntry = new Entry(
            key: "foo",
            value: "bar",
            description: "baz")

        smartionary.addToEntries(duplicateEntry)
        assertFalse(duplicateEntry.validate())

        duplicateEntry.key = "foo2"
        smartionary.addToEntries(duplicateEntry)
        assertTrue(entry.validate())

        assertEquals("foo2", duplicateEntry.key)
        assertEquals("bar", duplicateEntry.value)
        assertEquals("baz", duplicateEntry.description)
    }
}
