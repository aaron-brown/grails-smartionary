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

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import me.sudofu.smartionary.domain.SmartionaryEntry as Entry
import spock.lang.Specification

@Integration
@Rollback
class SmartionaryEntrySpec extends Specification {
    Smartionary smartionary

    def setupData() {
        smartionary = new Smartionary(name: "footionary")
        smartionary.save(failOnError: true, flush: true)
    }

    def 'Test simple entry'() {
        setup:
        setupData()
        Entry entry = new Entry(
            key: "foo",
            value: "bar",
            description: "baz")

        when:
        smartionary.addToEntries(entry)

        then:
        entry.validate()

        entry.key == "foo"
        entry.value == "bar"
        entry.description == "baz"
    }

    def 'Test unique constraint'() {
        setup:
        setupData()

        when:
        Entry entry = new Entry(
            key: "foo",
            value: "bar",
            description: "baz")
        smartionary.addToEntries(entry)

        then:
        entry.validate()

        entry.key == "foo"
        entry.value == "bar"
        entry.description == "baz"

        when:
        smartionary.save(failOnError: true, flush: true)
        Entry duplicateEntry = new Entry(
            key: "foo",
            value: "bar",
            description: "baz")
        smartionary.addToEntries(duplicateEntry)

        then:
        !duplicateEntry.validate()

        when:
        duplicateEntry.key = "foo2"
        smartionary.addToEntries(duplicateEntry)

        then:
        entry.validate()

        duplicateEntry.key == "foo2"
        duplicateEntry.value == "bar"
        duplicateEntry.description == "baz"
    }
}
