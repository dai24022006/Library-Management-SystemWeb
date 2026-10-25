package com.library.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Import các repository của 4 bạn kia
import com.library.management.repository.BookRepository;
import com.library.management.repository.ReaderRepository;
import com.library.management.repository.UserRepository;
import com.library.management.repository.BorrowRepository; 

import com.library.management.dto.DashboardStatsDTO;

@Service
public class DashboardService {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ReaderRepository readerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BorrowRepository borrowRepository;

    public DashboardStatsDTO getOverviewStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
    
        stats.setTotalBooks(bookRepository.count());
        stats.setTotalReaders(readerRepository.count());
        stats.setTotalUsers(userRepository.count());
        stats.setTotalBorrows(borrowRepository.count());
        
        return stats;
    }
}
