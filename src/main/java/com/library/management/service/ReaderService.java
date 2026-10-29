package com.library.management.service;

import com.library.management.model.Reader;
import com.library.management.repository.ReaderRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReaderService {

    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    // Lấy danh sách và tìm kiếm độc giả
    public List<Reader> getAll(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return readerRepository.findAll();
        }

        return readerRepository.findByFullNameContainingIgnoreCase(
                keyword.trim());
    }

    // Xem chi tiết độc giả
    public Reader getById(Long id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy độc giả"));
    }

    // Thêm độc giả
    @Transactional
    public Reader create(Reader request) {

        validate(request);

        if (readerRepository.findByReaderId(
                request.getReaderId().trim()) != null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mã độc giả đã tồn tại");
        }

        Reader reader = new Reader();
        copyFields(request, reader);

        return readerRepository.save(reader);
    }

    // Sửa độc giả
    @Transactional
    public Reader update(Long id, Reader request) {

        Reader reader = getById(id);
        validate(request);

        Reader duplicate = readerRepository.findByReaderId(
                request.getReaderId().trim());

        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mã độc giả đã tồn tại");
        }

        copyFields(request, reader);

        return readerRepository.save(reader);
    }

    private void validate(Reader reader) {
        if (reader == null
                || reader.getReaderId() == null
                || reader.getReaderId().isBlank()
                || reader.getFullName() == null
                || reader.getFullName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thiếu mã hoặc họ tên độc giả");
        }
    }

    private void copyFields(Reader source, Reader target) {
        target.setReaderId(source.getReaderId().trim());
        target.setFullName(source.getFullName().trim());
        target.setDateOfBirth(source.getDateOfBirth());
        target.setGender(source.getGender());
        target.setPhoneNumber(source.getPhoneNumber());
    }

    // Xóa độc giả
    @Transactional
    public void delete(Long id) {
        Reader reader = getById(id);

        try {
            readerRepository.delete(reader);
            readerRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Độc giả có lịch sử mượn/trả, không thể xóa",
                    ex);
        }
    }
}