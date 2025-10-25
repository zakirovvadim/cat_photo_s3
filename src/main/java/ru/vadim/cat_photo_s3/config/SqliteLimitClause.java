package ru.vadim.cat_photo_s3.config;

import org.springframework.data.relational.core.dialect.LimitClause;

public enum SqliteLimitClause implements LimitClause {
    INSTANCE;

    @Override
    public String getLimit(long limit) {
        return "LIMIT " + limit;
    }

    @Override
    public String getOffset(long offset) {
        return "OFFSET " + offset;
    }

    @Override
    public String getLimitOffset(long limit, long offset) {
        return "LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public Position getClausePosition() {
        return Position.AFTER_ORDER_BY;
    }
}
