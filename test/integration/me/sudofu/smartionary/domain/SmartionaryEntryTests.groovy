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

import static org.junit.Assert.*

import me.sudofu.smartionary.domain.SmartionaryEntry as Entry

import org.junit.Before
import org.junit.Test

class SmartionaryEntryTests {

    Smartionary smartionary

    @Before
    void setUp() {
        smartionary = new Smartionary( name: "footionary" )
        smartionary.save(flush: true)
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
