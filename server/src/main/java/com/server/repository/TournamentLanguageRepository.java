package com.server.repository;
import com.server.model.TournamentLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TournamentLanguageRepository extends JpaRepository<TournamentLanguage,Long> {
  List<TournamentLanguage> findByTournamentId(Long tournamentId);
}