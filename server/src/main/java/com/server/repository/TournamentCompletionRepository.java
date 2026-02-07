package com.server.repository;

import com.server.model.TournamentCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TournamentCompletionRepository extends JpaRepository<TournamentCompletion, Long> {

    List<TournamentCompletion> findByUserId(Long userId);

    Optional<TournamentCompletion> findByUserIdAndTournamentId(Long userId, Long tournamentId);

    boolean existsByUserIdAndTournamentId(Long userId, Long tournamentId);

    List<TournamentCompletion> findByTournamentIdOrderByCompletionTimeAsc(Long tournamentId);
}
