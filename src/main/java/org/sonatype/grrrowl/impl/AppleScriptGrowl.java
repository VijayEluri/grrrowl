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

package org.sonatype.grrrowl.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.grrrowl.Growl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Provides {@link Growl} support via AppleScript.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
public class AppleScriptGrowl
    implements Growl
{
    private static final Logger log = LoggerFactory.getLogger(AppleScriptGrowl.class);

    private final ScriptEngineManager manager;

    private final ScriptEngine engine;

    private final String appName;

    private String[] notifications;

    private String[] allowed;
    private static final String GROWL_HELPER_APP = "GrowlHelperApp";

    public AppleScriptGrowl(final String appName) {
        assert appName != null;

        this.appName = appName;

        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("AppleScript");
        if (engine == null) {
            throw new UnsupportedOperationException("AppleScript engine is not available");
        }
    }

    public void setAllowedNotifications(final String... notifications) {
        assert notifications != null;
        this.notifications = notifications;
    }

    public void setDefaultNotifications(final String... notifications) {
        assert notifications != null;
        this.allowed = notifications;
    }

    public void register() {
        StringWriter buff = new StringWriter();
        PrintWriter out = new PrintWriter(buff);

        out.format("tell application \"%s\"", GROWL_HELPER_APP).println();

        out.print("set the allNotificationsList to {");
        for (int i=0; i<notifications.length; i++) {
            out.print("\"");
            out.print(notifications[i]);
            out.print("\"");
            if (i+1<notifications.length) {
                out.print(",");
            }
        }
        out.print("} ");
        out.println();

        out.print("set the enabledNotificationsList to {");
        for (int i=0; i<allowed.length; i++) {
            out.print("\"");
            out.print(allowed[i]);
            out.print("\"");
            if (i+1<allowed.length) {
                out.print(",");
            }
        }
        out.print("}");
        out.println();

        out.format("register as application \"%s\"", appName);
        out.print(" all notifications allNotificationsList");
        out.print(" default notifications enabledNotificationsList");
        // out.print(" icon of application \"Script Editor\"");
        out.println();

        out.println("end tell");
        out.flush();

        log.trace("Register script:\n{}", buff);

        try {
            engine.eval(buff.toString());
        }
        catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyGrowlOf(final String notification, final String title, final String description) {
        StringWriter buff = new StringWriter();
        PrintWriter out = new PrintWriter(buff);

        out.format("tell application \"%s\"", GROWL_HELPER_APP).println();
        out.format(" notify with name \"%s\"", notification);
        out.format(" title \"%s\"", title);
        out.format(" description \"%s\"", description);
        out.format(" application name \"%s\"", appName);
        out.println();
        out.println("end tell");
        out.flush();

        log.trace("Notify script:\n{}", buff);
        
        try {
            engine.eval(buff.toString());
        }
        catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}