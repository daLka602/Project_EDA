package com.connectme.model.util;

public class SystemStatsUtil {
    public final int totalUsers;
    public final int activeUsers;
    public final int totalContacts;
    public final double activationRate;
    public final int customers;
    public final int partners;
    public final int suppliers;

    public SystemStatsUtil(int totalUsers, int activeUsers, int totalContacts,
                           double activationRate, int customers, int partners, int suppliers) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalContacts = totalContacts;
        this.activationRate = activationRate;
        this.customers = customers;
        this.partners = partners;
        this.suppliers = suppliers;
    }

    @Override
    public String toString() {
        return String.format(
                "SystemStats{users=%d, active=%d, contacts=%d, rate=%.1f%%, customers=%d, partners=%d, suppliers=%d}",
                totalUsers, activeUsers, totalContacts, activationRate, customers, partners, suppliers
        );
    }
}
