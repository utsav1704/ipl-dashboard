package com.example.ipldashboard.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ipldashboard.model.Match;
import com.example.ipldashboard.model.Team;
import com.example.ipldashboard.repository.MatchRepository;
import com.example.ipldashboard.repository.TeamRepository;

@RestController
@CrossOrigin
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/team/{teamName}")
    public Team getTeam(@PathVariable String teamName){
        Team team = teamRepository.findByTeamName(teamName);
        team.setMatches(matchRepository.findLatestMatchesByTeam(teamName, 4));
        return team;
    }

    @GetMapping("/team/{teamName}/matches")
    public List<Match> getMatchesForTeam(@PathVariable String teamName , @RequestParam int year){
        return matchRepository.getMatchesByTeamAndSeason(teamName, ""+year);
    }

    @GetMapping("/team")
    public Iterable<Team> getAllTeam(){
        return teamRepository.findAll();
    }

    @GetMapping("/team/winners")
    public List<Match> getAllWinners(){
        return matchRepository.getByMatchNumberOrderByDateDesc("Final");
    }

    @GetMapping("/team/matches/{team1}/{team2}")
    public List<Match> getMatchesBetweenTwoTeams(@PathVariable String team1 , @PathVariable String team2){
        return matchRepository.getMatchesBetweenTwoTeams(team1, team2);
    }
}
