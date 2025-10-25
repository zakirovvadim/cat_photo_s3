package ru.vadim.cat_photo_s3.config;

import org.springframework.data.relational.core.dialect.AnsiDialect;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.render.SelectRenderContext;

public class SqliteDialect extends AnsiDialect {
    private static final AnsiDialect DELEGATE = AnsiDialect.INSTANCE;

    @Override
    public LimitClause limit() {
        return SqliteLimitClause.INSTANCE;
    }

    @Override
    public LockClause lock() {
        return DELEGATE.lock();
    }

    @Override
    public SelectRenderContext getSelectContext() {
        return DELEGATE.getSelectContext();
    }
}
