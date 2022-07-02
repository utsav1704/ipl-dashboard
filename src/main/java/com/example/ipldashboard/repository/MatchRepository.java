package com.example.ipldashboard.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ipldashboard.model.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> getByTeam1OrTeam2OrderByDateDesc(String teamName1, String teamName2, Pageable pageable);
    // teamName 1 for team1 & teamName2 for team2

    // List<Match> getByTeam1AndSeasonOrTeam2AndSeasonOrderByDateDesc(String teamName1, String season1, String teamName2,
    //         String season2);

    @Query("select m from Match m where season = :year and (m.team1 = :teamName or m.team2 = :teamName) order by date desc")
    List<Match> getMatchesByTeamAndSeason(@Param("teamName") String teamName ,@Param("year") String year);

    default List<Match> findLatestMatchesByTeam(String teamName, int count) {
        return getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0, count));
    }

    List<Match> getByMatchNumberOrderByDateDesc(String matchNumber);

    @Query("select m from Match m where ((team1 = :team1 and team2 = :team2) or (team1 = :team2 and team2 = :team1)) order by date desc")
    List<Match> getMatchesBetweenTwoTeams(@Param("team1") String team1 , @Param("team2") String team2);
}
