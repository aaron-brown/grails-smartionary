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
        Intelledger.set('foo')

        Map result
        IntelledgerDomain domain = IntelledgerDomain.findByName('foo')
        Intelledger.set(domain.name, 'jon-doe', "foo", "bar", null, null)

        assertEquals(1, Intelledger.size(domain.name))

        result = Intelledger.get(domain.name)
        assertNotNull(result)
        assertEquals('bar', result.foo)

        Intelledger.set(domain.name, 'jon-doe', "foo", "baz", null, null)
        result = Intelledger.get(domain.name)
        assertNotNull(result)
        assertEquals('baz', result.foo)
    }

    @Test
    void testNullGets() {
        assertNull(Intelledger.get('foo'))
        assertNull(Intelledger.get('foo', 'bar'))
    }

    @Test
    void testSetWithoutEntries() {
        Map result
        assertNull(result)

        Intelledger.set('foo')

        result = Intelledger.get('foo')
        assertNotNull(result)
        assertTrue(result.isEmpty())

        IntelledgerDomain domain
        assertNull(domain)

        domain = IntelledgerDomain.findByName('foo')

        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertNull(domain.description)
        assertNull(domain.entries)

        Intelledger.set('foo', 'intelledgerDescription')

        result = Intelledger.get('foo')
        assertNotNull(result)
        assertTrue(result.isEmpty())

        domain = IntelledgerDomain.findByName('foo')
        assertNotNull(domain)
        assertEquals('foo', domain.name)
        assertEquals('intelledgerDescription', domain.description)
        assertNull(domain.entries)
    }
}
