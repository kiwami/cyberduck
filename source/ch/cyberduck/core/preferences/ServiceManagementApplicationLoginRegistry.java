package ch.cyberduck.core.preferences;

/*
 * Copyright (c) 2002-2015 David Kocher. All rights reserved.
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.binding.foundation.ServiceManagementLibrary;
import ch.cyberduck.core.local.Application;

import org.apache.log4j.Logger;

/**
 * @version $Id$
 */
public class ServiceManagementApplicationLoginRegistry implements ApplicationLoginRegistry {
    private static Logger log = Logger.getLogger(ServiceManagementApplicationLoginRegistry.class);

    @Override
    public void register(final Application application) {
        if(!ServiceManagementLibrary.SMLoginItemSetEnabled(application.getIdentifier(), true)) {
            log.warn(String.format("Failed to register %s as login item", application));
        }
    }

    @Override
    public void unregister(final Application application) {
        if(!ServiceManagementLibrary.SMLoginItemSetEnabled(application.getIdentifier(), false)) {
            log.warn(String.format("Failed to remove %s as login item", application));
        }
    }
}