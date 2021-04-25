package com.highestpeak.dimlight.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;


/**
 * @author highestpeak
 */
public class SQLiteDialectIdentityColumnSupport extends IdentityColumnSupportImpl {
    @SuppressWarnings("unused")
    public SQLiteDialectIdentityColumnSupport(Dialect dialect) {
        super();
    }

    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
        // As specified in NHibernate dialect
        // tmpdoc return true?
        return false;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select last_insert_rowid()";
    }

    @Override
    public String getIdentityColumnString(int type) {
        // return "integer primary key autoincrement";
        // tmpdoc return "autoincrement"?
        return "integer";
    }
}
