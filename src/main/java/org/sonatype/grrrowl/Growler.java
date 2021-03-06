/*
 * Copyright (C) 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.grrrowl;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A helper to manage sending notifications to {@link Growl}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 1.0
 */
public class Growler
{
    private final String appName;

    private Growl growl;

    private final Set<String> notifications = new LinkedHashSet<String>();

    private final Set<String> enabled = new LinkedHashSet<String>();

    private boolean registered;

    public Growler(final String appName, final String... notifications) {
        assert appName != null;
        this.appName = appName;

        if (notifications != null) {
            add(notifications);
        }
    }

    public Growler(final String appName, final Enum... notifications) {
        assert appName != null;
        this.appName = appName;

        if (notifications != null) {
            add(notifications);
        }
    }

    public Growler(final String appName, final Class<? extends Enum>... types) {
        this.growl = GrowlFactory.create(appName);
        assert appName != null;
        this.appName = appName;

        if (types != null) {
            add(types);
        }
    }

    public Growler(final String appName) {
        this(appName, (String)null);
    }

    private Growl getGrowl() {
        if (growl == null) {
            growl = GrowlFactory.create(appName);
        }
        return growl;
    }

    /**
     * Check if Growl is running.
     *
     * @return True if Growl is running.
     * @since 1.1
     */
    public boolean isRunning() {
        return getGrowl().isGrowlRunning();
    }

    public Collection<String> getNotifications() {
        return notifications;
    }

    public Collection<String> getEnabled() {
        return enabled;
    }

    public Growler add(final String... notifications) {
        assert notifications != null;

        this.notifications.addAll(Arrays.asList(notifications));

        return this;
    }

    public Growler add(final Enum... notifications) {
        assert notifications != null;

        for (Enum n : notifications) {
            this.notifications.add(n.name());
        }

        return this;
    }

    public Growler add(final Class<? extends Enum>... types) {
        assert types != null;

        for (Class<? extends Enum> type : types) {
            add(type.getEnumConstants());
        }

        return this;
    }

    public Growler enable(final String... notifications) {
        assert notifications != null;

        this.enabled.addAll(Arrays.asList(notifications));

        return this;
    }

    public Growler enable(final Enum... notifications) {
        assert notifications != null;

        for (Enum n : notifications) {
            this.enabled.add(n.name());
        }

        return this;
    }

    public Growler enable(final Class<? extends Enum>... types) {
        assert types != null;

        for (Class<? extends Enum> type : types) {
            enable(type.getEnumConstants());
        }

        return this;
    }

    public Growler enableAll() {
        for (String n : notifications) {
            enable(n);
        }

        return this;
    }

    public Growler register(final boolean enableAll) {
        if (isRunning()) {
            if (enableAll) {
                enableAll();
            }

            getGrowl().setAllowedNotifications(notifications.toArray(new String[notifications.size()]));
            getGrowl().setEnabledNotifications(enabled.toArray(new String[enabled.size()]));
            getGrowl().register();

            registered = true;
        }

        return this;
    }

    public Growler register() {
        register(true);

        return this;
    }

    public void growl(final String notification, final String title, final String description) {
        if (isRunning()) {
            if (!registered) {
                register();
            }
            getGrowl().notifyGrowlOf(notification, title, description);
        }
    }

    public void growl(final Enum notification, final String title, final String description) {
        if (isRunning()) {
            if (!registered) {
                register();
            }
            getGrowl().notifyGrowlOf(notification.name(), title, description);
        }
    }
}