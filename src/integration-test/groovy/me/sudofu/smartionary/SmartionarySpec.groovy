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

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import me.sudofu.smartionary.domain.Smartionary as SmartionaryDomain
import me.sudofu.smartionary.domain.SmartionaryEntry as Entry
import spock.lang.Specification

@Integration
@Rollback
class SmartionarySpec extends Specification {
    Date date = new Date(100000)
    UUID uuid = new UUID(12345, 67890)
    Map entries = [
        ten     : 4,
        time    : date,
        uuid    : uuid,
        warGames: 'tic-tac-toe',
    ]
    Map entryDescriptions = [
        warGames: "A strange game. The only winning move is not to play.",
        ten     : "Code for confirmation in Radio communications.",
        time    : "A fixed timestamp."
    ]

    def 'Test null gets'() {
        expect:
        Smartionary.get('foo') == null
        Smartionary.get('foo', 'bar') == null
    }

    def 'Test set without entries'() {
        setup:
        Smartionary.set('foo')

        when:
        Map m = Smartionary.get('foo')

        then:
        m != null
        m.isEmpty()

        when:
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        then:
        domain
        domain.name == 'foo'
        !domain.description
        !domain.entries

        when:
        Smartionary.set('foo', 'smartionaryDescription')
        m = Smartionary.get('foo')

        then:
        m != null
        m.isEmpty()

        when:
        domain = SmartionaryDomain.findByName('foo')

        then:
        domain
        domain.name == 'foo'
        domain.description == 'smartionaryDescription'
        !domain.entries
    }

    def 'Test set explicit entry'() {
        setup:
        Smartionary.set('foo')

        when:
        Map m = Smartionary.get('foo')

        then:
        m != null
        m.isEmpty()

        when:
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null
        domain.name == 'foo'
        !domain.description
        !domain.entries

        when:
        Smartionary.set('foo', 'bar', 'baz')
        m = Smartionary.get('foo')

        then:
        m != null
        !m.isEmpty()
        m.size() == 1
        m == [bar: 'baz']

        when:
        domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null
        domain.entries != null
        domain.entries.size() == 1

        when:
        Entry entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'bar')
            }
        }

        then:
        entry != null
        entry.key == 'bar'
        entry.value == 'baz'
        entry.description == null
        entry.value == m."${entry.key}"

        when:
        Smartionary.set('foo', 'bar', 'bat', 'entryDescription', 'smartionaryDescription')
        m = Smartionary.get('foo')

        then:
        m != null
        !m.isEmpty()
        m.size() == 1
        m == [bar: 'bat']

        when:
        domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null
        domain.description == 'smartionaryDescription'
        domain.entries
        domain.entries.size() == 1

        when:
        entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'bar')
            }
        }

        then:
        entry != null
        entry.key == 'bar'
        entry.value == 'bat'
        entry.description == 'entryDescription'
        entry.value == m."${entry.key}"
    }

    def 'Test purge nulls'() {
        setup:
        Smartionary.set('foo', 'one', null)
        Smartionary.set('foo', 'two', 2)

        when:
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null

        when:
        Map m = Smartionary.get('foo')

        then:
        m != null
        !m.isEmpty()
        m.size() == 2
        m == [one: null, two: "2"]

        domain.entries.size() == 2

        when:
        Smartionary.purgeNulls('foo')
        domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null

        when:
        m = Smartionary.get('foo')

        then:
        m != null
        m.size() == 1
        m == [two: "2"]

        domain.entries.size() == 1

        when:
        Entry entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'one')
            }
        }

        then:
        entry == null
    }

    def 'Test set entries by map'() {
        when:
        Smartionary.set(
            'foo',
            warGames: entries.warGames,
            ten: entries.ten,
            time: entries.time,
            uuid: entries.uuid,
            smartionaryDescriptions: [
                warGames: entryDescriptions.warGames,
                ten     : entryDescriptions.ten,
                time    : entryDescriptions.time,
            ]
        )
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null
        domain.description == null

        when:
        Map m = Smartionary.get('foo')
        // Convert non-String values to Strings.
        entries.ten = (entries.ten as String)
        entries.time = (entries.time as String)
        entries.uuid = (entries.uuid as String)

        then:
        m != null
        !m.isEmpty()
        m.size() == 4
        m == entries

        domain.entries.size() == 4

        when:
        Entry entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'time')
            }
        }

        then:
        entry != null
        entry.value == entries.time as String
        entry.value == entries."${entry.key}"

        domain.entries.each {
            it.description == entryDescriptions."${it.key}"
        }

        when:
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

        then:
        domain != null
        domain.description == 'smartionaryDescription'

        when:
        m = Smartionary.get('foo')

        then:
        m != null
        !m.isEmpty()
        m.size() == 5
        m.subMap(m.keySet() - 'time') == entries.subMap(entries.keySet() - 'time')
        domain.entries.size() == 5

        when:
        entry = Entry.withCriteria(uniqueResult: true) {
            and {
                smartionary {
                    eq('name', 'foo')
                }
                eq('key', 'time')
            }
        }

        then:
        entry != null
        entries.time as String != entry.value
        entries."${entry.key}" != entry.value

        !domain.entries*.description.contains('This will be ignored.')
    }

    def 'Test with JSON'() {
        setup:
        String jsonEntries = '{"a":"apple","b":"banana","c":"carrot"}'
        Smartionary.fromJson('foo', jsonEntries)

        expect:
        jsonEntries == Smartionary.getAsJson('foo')

        when:
        String invalidJsonEntry = '["a", "apple", "b", "banana", "c", "carrot"]'
        Smartionary.fromJson('bar', invalidJsonEntry)

        then:
        thrown IllegalArgumentException
        Smartionary.get('bar') == null
    }

    def 'Test size'() {
        expect:
        Smartionary.size('foo') == -1

        when:
        Smartionary.set('foo')

        then:
        Smartionary.size('foo') == 0

        when:
        Smartionary.set(entries, 'foo')

        then:
        Smartionary.size('foo') == 4
    }

    def 'Test contains'() {
        expect:
        !Smartionary.contains('foo', 'baz')

        when:
        Smartionary.set('foo')

        then:
        !Smartionary.contains('foo', 'baz')

        when:
        Smartionary.set('foo', 'bar', 'baz')

        then:
        Smartionary.contains('foo', 'baz')
    }

    def 'Test contains key'() {
        expect:
        !Smartionary.containsKey('foo', 'bar')

        when:
        Smartionary.set('foo')

        then:
        !Smartionary.containsKey('foo', 'bar')

        when:
        Smartionary.set('foo', 'bar', 'baz')

        then:
        Smartionary.containsKey('foo', 'bar')
    }

    def 'Test delete'() {
        expect:
        SmartionaryDomain.findByName('foo') == null

        when:
        Smartionary.delete('foo')
        Smartionary.delete('foo', 'bar')
        Smartionary.set(entries, 'foo')
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')

        then:
        domain != null

        when:
        Map m = Smartionary.get('foo')

        then:
        m != null
        m.size() == 4

        domain.entries.find { it.key == 'bar' } == null
        m.bar == null

        when:
        Smartionary.delete('foo', 'bar')

        then:
        m.size() == 4
        domain.entries.size() == 4

        domain.entries.find { it.key == 'time' } != null
        m.time != null

        when:
        Smartionary.delete('foo', 'time')
        m = Smartionary.get('foo')

        then:
        m != null
        m.size() == 3
        domain.entries.size() == 3

        domain.entries.find { it.key == 'time' } == null
        m.time == null

        when:
        Smartionary.delete('foo')

        then:
        SmartionaryDomain.findByName('foo') == null
        Smartionary.get('foo') == null

        when:
        List<Entry> nonExistentEntries = Entry.withCriteria {
            smartionary {
                eq('name', 'foo')
            }
        }

        then:
        nonExistentEntries.isEmpty()
    }

    def 'Test delete many'() {
        setup:
        Smartionary.set(entries, 'foo')

        when:
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')
        Map m = Smartionary.get('foo')

        then:
        m != null
        m.size() == 4
        m.keySet().containsAll(['time', 'ten', 'uuid'])

        domain.entries.size() == 4
        domain.entries*.key.containsAll(['time', 'ten', 'uuid'])

        when:
        Smartionary.delete('foo', 'time', 'ten', 'uuid', 'notAnEntry')
        m = Smartionary.get('foo')

        then:
        m != null
        m.size() == 1
        !m.keySet().containsAll(['time,', 'ten', 'uuid'])

        domain.entries.size() == 1
        !domain.entries*.key.containsAll(['time', 'ten', 'uuid'])
    }

    def 'Test delete all'() {
        setup:
        Smartionary.set(entries, 'foo')

        when:
        SmartionaryDomain domain = SmartionaryDomain.findByName('foo')
        Map m = Smartionary.get('foo')

        then:
        m != null
        m.size() == 4
        domain.entries.size() == 4

        when:
        Smartionary.purge('foo')
        m = Smartionary.get('foo')

        then:
        m != null
        m.isEmpty()
        domain.entries.isEmpty()
    }
}
