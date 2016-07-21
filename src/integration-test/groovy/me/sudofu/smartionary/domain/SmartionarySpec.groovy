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
class SmartionarySpec extends Specification {
    def 'Test simple smartionary'() {
        setup:
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        expect:
        smartionary.validate()

        when:
        smartionary.save(failOnError: true, flush: true)
        smartionary = Smartionary.findByName('foo')

        then:
        smartionary != null
        smartionary.name == 'foo'
        smartionary.description == 'bar'
        !smartionary.entries
    }

    def 'Test unique constraint'() {
        setup:
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        expect:
        smartionary.validate()

        when:
        smartionary.save(failOnError: true, flush: true)
        smartionary = new Smartionary(name: 'foo')

        then:
        !smartionary.validate()

        when:
        smartionary.name = 'foo2'

        then:
        smartionary.validate()
    }

    def 'Test add entry'() {
        setup:
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        expect:
        smartionary.validate()

        when:
        Entry entry = new Entry(
            key: 'foo',
            value: 'bar',
            description: 'baz')
        smartionary.addToEntries(entry)

        then:
        smartionary.validate()

        when:
        smartionary.save(failOnError: true, flush: true)
        smartionary = Smartionary.findByName('foo')

        then:
        smartionary != null
        smartionary.entries != null
        smartionary.entries.size() == 1
    }

    def 'Test to map'() {
        setup:
        Smartionary smartionary = new Smartionary(
            name: 'foo',
            description: 'bar')

        expect:
        smartionary.validate()

        when:
        Map smartionaryMap = smartionary.toMap()

        then:
        smartionaryMap != null
        smartionaryMap.isEmpty()

        when:
        Entry entry = new Entry(
            key: 'foo',
            value: 'bar',
            description: 'baz')
        smartionary.addToEntries(entry)

        then:
        smartionary.validate()

        smartionary != null
        smartionary.entries != null
        smartionary.entries.size() == 1

        when:
        smartionaryMap = smartionary.toMap()

        then:
        smartionaryMap != null
        !smartionaryMap.isEmpty()
        smartionaryMap == [foo: 'bar']
    }
}
