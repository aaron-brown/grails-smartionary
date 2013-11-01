package me.sudofu.smartionary.domain

import static org.junit.Assert.*
import org.junit.*

import me.sudofu.smartionary.domain.Smartionary
import me.sudofu.smartionary.domain.SmartionaryEntry as Entry

class SmartionaryTests {

    @Before
    void setUp() {

    }

    @After
    void tearDown() {

    }

    @Test
    void testDefault() {
        assert true
    }

    @Test
    void testSimpleSmartionary() {
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        assertTrue(smartionary.validate())

        smartionary.save(flush: true)

        smartionary = Smartionary.findByName('foo')
        assertNotNull(smartionary)
        assertEquals('foo', smartionary.name)
        assertEquals('bar', smartionary.description)
        assertNull(smartionary.entries)
    }

    @Test
    void testUniqueConstraint() {
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        assertTrue(smartionary.validate())

        smartionary.save(flush: true)

        smartionary = new Smartionary(name: 'foo')

        assertFalse(smartionary.validate())

        smartionary.name = 'foo2'

        assertTrue(smartionary.validate())
    }

    @Test
    void testAddEntry() {
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        assertTrue(smartionary.validate())

        Entry entry = new Entry(
            key: 'foo',
            value: 'bar',
            description: 'baz')

        smartionary.addToEntries(entry)

        assertTrue(smartionary.validate())

        smartionary.save(flush: true)

        smartionary = Smartionary.findByName('foo')

        assertNotNull(smartionary)
        assertNotNull(smartionary.entries)
        assertEquals(1, smartionary.entries.size())
    }

    @Test
    void testToMap() {
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        assertTrue(smartionary.validate())

        Map smartionaryMap = smartionary.toMap()

        assertNotNull(smartionaryMap)
        assertTrue(smartionaryMap.isEmpty())

        Entry entry = new Entry(
            key: 'foo',
            value: 'bar',
            description: 'baz')

        smartionary.addToEntries(entry)

        assertTrue(smartionary.validate())

        assertNotNull(smartionary)
        assertNotNull(smartionary.entries)
        assertEquals(1, smartionary.entries.size())

        smartionaryMap = smartionary.toMap()
        assertNotNull(smartionaryMap)
        assertFalse(smartionaryMap.isEmpty())
        assertEquals([foo: 'bar'], smartionaryMap)
    }
}
