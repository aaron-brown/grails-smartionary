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

import me.sudofu.smartionary.domain.Smartionary

/**
 * Emulates a {@link java.util.Map.Entry Map Entry}.
 *
 * @author  Aaron Brown
 */
class SmartionaryEntry implements Comparable<SmartionaryEntry> {
	/**
	 * The key by which the entry is accessed.
	 */
	String key

	/**
	 * The value of the key (normalized as a String)
	 */
	String value

	/**
	 * Describes the purpose of the <code>value</code> or  where it is
	 * used.
	 */
	String description

	/**
	 * Describe the version of the key.
	 */
	int keyVersion

	/**
	 * Describes who updated the key value.
	 */
	String updatedBy

	/**
	 * Describe whether the key is active or not.
	 */
	boolean active

	static belongsTo = [ smartionary: Smartionary ]

	static constraints = {
		key         (blank: false, unique: ['smartionary', 'keyVersion'])
		value       (nullable: true, size: 1..8000)
		description (nullable: true, size: 1..8000)
		updatedBy   (nullable: true, size: 1..8000)
		smartionary ()
	}

	static mapping = {
		key         column: 'identifier'
		value       type: "text"
		description type: "text"
	}

	int compareTo(SmartionaryEntry obj) {
		if(key.compareTo(obj.key) == 0) {
			return keyVersion.compareTo(obj.keyVersion)
		} else{
			return key.compareTo(obj.key)
		}
	}

	String toString() {
		return key
	}
}
