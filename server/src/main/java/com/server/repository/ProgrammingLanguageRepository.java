package com.server.repository;

import com.server.model.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgrammingLanguageRepository extends JpaRepository<ProgrammingLanguage, Long> {
    List<ProgrammingLanguage> findAllByOrderByNameAsc();
}
