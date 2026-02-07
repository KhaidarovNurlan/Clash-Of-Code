package com.server.repository;

import com.server.model.TournamentLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentLevelRepository extends JpaRepository<TournamentLevel, Long> {

    List<TournamentLevel> findByTournamentIdOrderByLevelNumber(Long tournamentId);

    @Query("SELECT COUNT(tl) FROM TournamentLevel tl WHERE tl.tournament.id = :tournamentId")
    long countByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("""
        SELECT tl FROM TournamentLevel tl
        WHERE tl.tournament.id = :tournamentId AND tl.levelNumber > :currentLevel
        ORDER BY tl.levelNumber ASC
    """)
    List<TournamentLevel> findNextLevels(@Param("tournamentId") Long tournamentId, @Param("currentLevel") int currentLevel);

    Optional<TournamentLevel> findByTournamentIdAndLevelNumber(Long tournamentId, int levelNumber);
}
