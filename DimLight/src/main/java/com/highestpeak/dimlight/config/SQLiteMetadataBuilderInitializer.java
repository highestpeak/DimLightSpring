package com.highestpeak.dimlight.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderInitializer;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverSet;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.jboss.logging.Logger;

/**
 * SQLite工具
 */
public class SQLiteMetadataBuilderInitializer implements MetadataBuilderInitializer {

    private final static Logger logger = Logger.getLogger(SQLiteMetadataBuilderInitializer.class);

    @Override
    public void contribute(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
        DialectResolver dialectResolver = serviceRegistry.getService(DialectResolver.class);

        if (!(dialectResolver instanceof DialectResolverSet)) {
            logger.warnf("DialectResolver '%s' is not an instance of DialectResolverSet, not registering SQLiteDialect",
                    dialectResolver);
            return;
        }

        ((DialectResolverSet) dialectResolver).addResolver(RESOLVER);
    }

    static private final SQLiteDialect DIALECT = new SQLiteDialect();

    static private final DialectResolver RESOLVER = (DialectResolver) info -> {
        if ("SQLite".equals(info.getDatabaseName())) {
            return DIALECT;
        }

        return null;
    };
}