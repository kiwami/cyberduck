package ch.cyberduck.cli;

/*
 * Copyright (c) 2002-2018 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.UnsecureHostPasswordStore;
import ch.cyberduck.core.preferences.PreferencesFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinuxTerminalPreferencesTest {

    @Test
    public void setDefaults() {
        final LinuxTerminalPreferences prefs = new LinuxTerminalPreferences();
        PreferencesFactory.set(prefs);
        assertEquals("NativePRNGNonBlocking", prefs.getProperty("connection.ssl.securerandom.algorithm"));
        assertEquals(UnsecureHostPasswordStore.class.getName(), prefs.getProperty("factory.passwordstore.class"));
    }
}