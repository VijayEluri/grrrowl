/*
 * Copyright (C) 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.grrrowl.impl

import org.junit.Test
import org.sonatype.grrrowl.Growl
import org.sonatype.grrrowl.GrowlTestSupport

/**
 * Tests for {@link AppleScriptGrowl}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class AppleScriptGrowlTest
    extends GrowlTestSupport
{
    @Test
    void testGrowl() {
        if (!isMacOs()) {
            return
        }

        Growl growl = new AppleScriptGrowl(getClass().name)
        growl.setAllowedNotifications("foo")
        growl.setEnabledNotifications("foo")
        growl.register()
        growl.notifyGrowlOf("foo", getClass().simpleName, getClass().simpleName)
    }
}