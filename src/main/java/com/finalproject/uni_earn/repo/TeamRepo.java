package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface TeamRepo extends JpaRepository<Team, Long> {
    @Query("SELECT COUNT(t) > 0 FROM Team t JOIN t.members m WHERE t.id = :teamId AND m.id = :studentId")
    boolean isStudentInTeam(@Param("teamId") Long teamId, @Param("studentId") Long studentId);

    @Modifying
    @Query(value = "DELETE FROM team_members WHERE team_id = :teamId AND student_id = :studentId", nativeQuery = true)
    void removeStudentFromTeam(@Param("teamId") Long teamId, @Param("studentId") Long studentId);

    @Modifying
    @Query(value = "DELETE FROM team_members WHERE team_id = :teamId", nativeQuery = true)
    void removeAllMembersFromTeam(@Param("teamId") Long teamId);
}
