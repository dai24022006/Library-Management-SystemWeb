package com.library.management.dto;

public class DashboardStatsDTO {
    private long totalBooks;
    private long totalReaders;
    private long totalUsers;
    private long totalBorrows;

    // Getters
    public long getTotalBooks() { return totalBooks; }
    public long getTotalReaders() { return totalReaders; }
    public long getTotalUsers() { return totalUsers; }
    public long getTotalBorrows() { return totalBorrows; }

    // Setters
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
    public void setTotalReaders(long totalReaders) { this.totalReaders = totalReaders; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public void setTotalBorrows(long totalBorrows) { this.totalBorrows = totalBorrows; }
}
