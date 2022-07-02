package com.example.ipldashboard.data;

import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;

import com.example.ipldashboard.model.Match;

public class MatchDataProcessor implements ItemProcessor<MatchInput , Match> {
                                                       // input     , output
    @Override
    public Match process(MatchInput matchInput) throws Exception {
        Match match = new Match();
        match.setId(Long.parseLong(matchInput.getId()));
        match.setCity(matchInput.getCity());
        match.setDate(LocalDate.parse(matchInput.getDate()));
        match.setSeason(matchInput.getSeason());
        match.setMatchNumber(matchInput.getMatchNumber());
        // set team 1 and team 2 depending on the innings order
        String firstInningsTeam , secondInningsTeam;

        if(matchInput.getTossDecision().equals("bat")){
            firstInningsTeam = matchInput.getTossWinner();
            secondInningsTeam = matchInput.getTossWinner().equals(matchInput.getTeam1()) ? matchInput.getTeam2() : matchInput.getTeam1();
            // match.setTeam1Players(matchInput.getTossWinner().equals(matchInput.getTeam1()) ? matchInput.getTeam1Players() : matchInput.getTeam2Players());
        }
        else{
            firstInningsTeam = matchInput.getTossWinner().equals(matchInput.getTeam1()) ? matchInput.getTeam2() : matchInput.getTeam1();
            secondInningsTeam = matchInput.getTossWinner();
        }

        match.setTeam1(firstInningsTeam);
        match.setTeam2(secondInningsTeam);
        match.setVenue(matchInput.getVenue());
        match.setTossWinner(matchInput.getTossWinner());
        match.setTossDecision(matchInput.getTossDecision());
        match.setSuperOver(matchInput.getSuperOver());
        match.setWinningTeam(matchInput.getWinningTeam());
        match.setWonBy(matchInput.getWonBy());            
        match.setMargin(matchInput.getMargin());
        match.setMethod(matchInput.getMethod());
        match.setPlayerOfMatch(matchInput.getPlayerOfMatch());

        if(firstInningsTeam.equals(matchInput.getTeam1())){
            match.setTeam1Players(matchInput.getTeam1Players());
            match.setTeam2Players(matchInput.getTeam2Players());
        }
        else{
            match.setTeam1Players(matchInput.getTeam2Players());
            match.setTeam2Players(matchInput.getTeam1Players());
        }

        match.setUmpire1(matchInput.getUmpire1());
        match.setUmpire2(matchInput.getUmpire2());

        return match;
    }
    
}
