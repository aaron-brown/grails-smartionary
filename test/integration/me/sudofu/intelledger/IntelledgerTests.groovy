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
package me.sudofu.intelledger

import static org.junit.Assert.*

import me.sudofu.intelledger.domain.Intelledger as IntelledgerDomain
import me.sudofu.intelledger.domain.IntelledgerEntry as Entry

import org.junit.Test

class IntelledgerTests extends GroovyTestCase {

    Date date = new Date(100000)

    UUID uuid = new UUID(12345, 67890)

    Map entries = [
        ten: 4,
        time: date,
        uuid: uuid,
        warGames: 'tic-tac-toe',
    ]

    Map entryDescriptions = [
        warGames: "A strange game. The only winning move is not to play.",
        ten: "Code for confirmation in Radio communications.",
        time: "A fixed timestamp."
    ]

    @Test
    void testSetDuplicateKeys() {
        Intelledger.set('4.0.1')

        IntelledgerDomain domain = IntelledgerDomain.findByName('4.0.1')
        Intelledger.set(domain.name, "build.number", "234568", null, null, "anil")
        Intelledger.set(domain.name, "build.number", "234569", null, null, "anil")

        Set values = Intelledger.getValues(domain.name, "build.number")
        assertEquals(values.size(), 2)
    }

    @Test
    void testNullGets() {
        assertNull(Intelledger.get('foo'))
        assertNull(Intelledger.get('foo', 'bar'))
    }

    @Test
    void testSetWithoutEntries() {
        Intelledger.set('foo')
        Map m = Intelledger.getActiveEntries('foo')
        assertNotNull(m)
        assertTrue(m.isEmpty())
        IntelledgerDomain domain = IntelledgerDomain.findByName('foo')
        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertNull(domain.description)
        assertNull(domain.entries)
        Intelledger.set('foo', 'intelledgerDescription')
        m = Intelledger.getActiveEntries('foo')
        assertNotNull(m)
        assertTrue(m.isEmpty())
        domain = IntelledgerDomain.findByName('foo')
        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertEquals('intelledgerDescription', domain.description)
        assertNull(domain.entries)
    }
}
