package com.ai.servicebusiness.config.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.function.BooleanSupplier;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private final BooleanSupplier rwSwitchSupplier;

    public DynamicRoutingDataSource(BooleanSupplier rwSwitchSupplier) {
        this.rwSwitchSupplier = rwSwitchSupplier;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        if (!rwSwitchSupplier.getAsBoolean()) {
            return "master";
        }
        return RoutingContext.isRead() ? "slave" : "master";
    }


}
