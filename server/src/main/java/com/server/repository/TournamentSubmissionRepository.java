package com.server.repository;

import com.server.model.TournamentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TournamentSubmissionRepository extends JpaRepository<TournamentSubmission, Long> {

    List<TournamentSubmission> findByUserId(Long userId);

    long countByUserId(Long userId);

    List<TournamentSubmission> findByTournamentId(Long tournamentId);

    Optional<TournamentSubmission> findByUserIdAndTournamentIdAndLevelId(
            Long userId,
            Long tournamentId,
            Long levelId
    );

    @Query("""
        SELECT COUNT(DISTINCT ts.levelId)
        FROM TournamentSubmission ts
        WHERE ts.tournamentId = :tournamentId
          AND ts.userId = :userId
          AND ts.passed = true
    """)
    long countDistinctPassedLevels(@Param("userId") Long userId, @Param("tournamentId") Long tournamentId);

    @Query("""
        SELECT MAX(l.levelNumber)
        FROM TournamentSubmission s
        JOIN TournamentLevel l ON s.levelId = l.id
        WHERE s.userId = :userId
        AND s.tournamentId = :tournamentId
        AND s.passed = true
    """)
    Integer findMaxPassedLevelNumber(@Param("userId") Long userId, @Param("tournamentId") Long tournamentId);
}
