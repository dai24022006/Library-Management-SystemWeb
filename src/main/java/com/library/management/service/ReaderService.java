package com.library.management.service;

import com.library.management.model.Reader;
import com.library.management.repository.ReaderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    public List<Reader> getAllReaders() {
        return readerRepository.findByDeletedFalse();
    }

    public Reader getReaderById(Long id) {
        return readerRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    public Reader addReader(Reader reader) {
        // Kiểm tra bắt buộc: mã và họ tên không được để trống
        if (reader.getReaderId() == null || reader.getReaderId().trim().isEmpty()
                || reader.getFullName() == null || reader.getFullName().trim().isEmpty()) {
            return null;
        }

        // Kiểm tra mã độc giả không được trùng
        Optional<Reader> oldReader = readerRepository.findByReaderIdAndDeletedFalse(reader.getReaderId().trim());
        if (oldReader.isPresent()) {
            return null;
        }

        reader.setReaderId(reader.getReaderId().trim());
        reader.setFullName(reader.getFullName().trim());
        reader.setDeleted(false);

        return readerRepository.save(reader);
    }

    public Reader updateReader(Long id, Reader reader) {
        Reader oldReader = readerRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (oldReader == null) {
            return null;
        }

        // Kiểm tra họ tên không được để trống khi cập nhật
        if (reader.getFullName() == null || reader.getFullName().trim().isEmpty()) {
            return null;
        }

        // Nếu có cập nhật mã độc giả, kiểm tra không được trùng với độc giả khác
        if (reader.getReaderId() != null && !reader.getReaderId().trim().isEmpty()) {
            String newReaderId = reader.getReaderId().trim();
            if (!newReaderId.equalsIgnoreCase(oldReader.getReaderId())) {
                Optional<Reader> duplicate = readerRepository.findByReaderIdAndDeletedFalse(newReaderId);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    return null;
                }
                oldReader.setReaderId(newReaderId);
            }
        }

        oldReader.setFullName(reader.getFullName().trim());
        oldReader.setDateOfBirth(reader.getDateOfBirth());
        oldReader.setGender(reader.getGender());
        oldReader.setPhoneNumber(reader.getPhoneNumber());

        return readerRepository.save(oldReader);
    }

    public boolean deleteReader(Long id) {
        Reader reader = readerRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (reader == null) {
            return false;
        }

        reader.setDeleted(true);
        readerRepository.save(reader);

        return true;
    }
}
