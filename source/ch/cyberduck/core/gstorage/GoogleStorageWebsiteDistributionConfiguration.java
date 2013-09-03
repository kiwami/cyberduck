package ch.cyberduck.core.gstorage;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
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

import ch.cyberduck.core.DescriptiveUrlBag;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.LoginController;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.Protocol;
import ch.cyberduck.core.ProtocolFactory;
import ch.cyberduck.core.analytics.AnalyticsProvider;
import ch.cyberduck.core.analytics.QloudstatAnalyticsProvider;
import ch.cyberduck.core.cdn.Distribution;
import ch.cyberduck.core.cdn.DistributionConfiguration;
import ch.cyberduck.core.cdn.DistributionUrlProvider;
import ch.cyberduck.core.cdn.features.DistributionLogging;
import ch.cyberduck.core.cdn.features.Index;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.identity.DefaultCredentialsIdentityConfiguration;
import ch.cyberduck.core.identity.IdentityConfiguration;
import ch.cyberduck.core.logging.LoggingConfiguration;
import ch.cyberduck.core.s3.ServiceExceptionMappingService;

import org.apache.commons.lang3.StringUtils;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.GSWebsiteConfig;
import org.jets3t.service.model.WebsiteConfig;

import java.util.Arrays;
import java.util.List;

/**
 * @version $Id$
 */
public class GoogleStorageWebsiteDistributionConfiguration implements DistributionConfiguration, Index {

    private GoogleStorageSession session;

    private PathContainerService containerService
            = new PathContainerService();

    public GoogleStorageWebsiteDistributionConfiguration(final GoogleStorageSession session) {
        this.session = session;
    }

    /**
     * Distribution methods supported by this S3 provider.
     *
     * @param container Bucket
     * @return Download and Streaming for AWS.
     */
    @Override
    public List<Distribution.Method> getMethods(final Path container) {
        return Arrays.asList(Distribution.WEBSITE);
    }

    @Override
    public String getName(Distribution.Method method) {
        return method.toString();
    }

    @Override
    public String getName() {
        return LocaleFactory.localizedString("Website Configuration", "S3");
    }

    @Override
    public DescriptiveUrlBag toUrl(final Path file) {
        final Distribution distribution = new Distribution(
                containerService.getContainer(file).getName(), Distribution.DOWNLOAD);
        distribution.setUrl(String.format("%s://%s.%s", Distribution.DOWNLOAD.getScheme(), containerService.getContainer(file).getName(),
                "storage.googleapis.com"));
        return new DistributionUrlProvider(distribution).toUrl(file);
    }

    @Override
    public Distribution read(final Path container, final Distribution.Method method, final LoginController prompt) throws BackgroundException {
        try {
            final WebsiteConfig configuration = session.getClient().getWebsiteConfigImpl(container.getName());
            final Distribution distribution = new Distribution(
                    String.format("%s.%s", container.getName(), "storage.googleapis.com"), method, configuration.isWebsiteConfigActive());
            distribution.setUrl(String.format("%s://%s.%s", method.getScheme(), container.getName(), "storage.googleapis.com"));
            distribution.setStatus(LocaleFactory.localizedString("Deployed", "S3"));
            distribution.setIndexDocument(configuration.getIndexDocumentSuffix());
            final LoggingConfiguration logging = new GoogleStorageLoggingFeature(session).getConfiguration(container);
            distribution.setLogging(logging.isEnabled());
            distribution.setLoggingContainer(logging.getLoggingTarget());
            return distribution;
        }
        catch(ServiceException e) {
            // Not found. Website configuration not enbabled.
            final Distribution distribution = new Distribution(
                    container.getName(),
                    method,
                    //Disabled
                    false);
            distribution.setStatus(e.getErrorMessage());
            distribution.setUrl(String.format("%s://%s.%s", method.getScheme(), container.getName(), session.getHost().getProtocol().getDefaultHostname()));
            return distribution;
        }
    }

    @Override
    public void write(final Path container, final Distribution distribution, final LoginController prompt) throws BackgroundException {
        try {
            if(distribution.isEnabled()) {
                String suffix = "index.html";
                if(StringUtils.isNotBlank(distribution.getIndexDocument())) {
                    suffix = Path.getName(distribution.getIndexDocument());
                }
                // Enable website endpoint
                session.getClient().setWebsiteConfigImpl(container.getName(), new GSWebsiteConfig(suffix));
                new GoogleStorageLoggingFeature(session).setConfiguration(container, new LoggingConfiguration(
                        distribution.isEnabled(), distribution.getLoggingContainer()
                ));
            }
            else {
                // Disable website endpoint
                session.getClient().setWebsiteConfigImpl(container.getName(), new GSWebsiteConfig());
            }
        }
        catch(ServiceException e) {
            throw new ServiceExceptionMappingService().map("Cannot write website configuration", e);
        }
    }

    @Override
    public <T> T getFeature(final Class<T> type, final Distribution.Method method) {
        if(type == Index.class) {
            return (T) this;
        }
        if(type == DistributionLogging.class) {
            return (T) new GoogleStorageLoggingFeature(session);
        }
        if(type == AnalyticsProvider.class) {
            return (T) new QloudstatAnalyticsProvider();
        }
        if(type == IdentityConfiguration.class) {
            return (T) new DefaultCredentialsIdentityConfiguration(session.getHost());
        }
        return null;
    }

    @Override
    public Protocol getProtocol() {
        return ProtocolFactory.GOOGLESTORAGE_SSL;
    }
}
