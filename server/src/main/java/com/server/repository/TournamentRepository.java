package com.server.repository;
import com.server.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface TournamentRepository extends JpaRepository<Tournament,Long> {
  @Query(value="SELECT * FROM tournaments LIMIT :limit OFFSET :offset", nativeQuery=true)
  List<Tournament> findAllWithStats(@Param("limit") int limit, @Param("offset") int offset);
  @Query(value="SELECT COUNT(*) FROM tournaments", nativeQuery=true)
  int countAll();
}
