package com.example.ipldashboard.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ipldashboard.model.Team;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

  // private final JdbcTemplate jdbcTemplate;
  private EntityManager entityManager;

  @Autowired
  public JobCompletionNotificationListener(EntityManager entityManager) {
    // this.jdbcTemplate = jdbcTemplate;
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED!");

      // jdbcTemplate.query("SELECT team1,team2,date FROM match",
      //   (rs, row) -> "Team 1 == "+rs.getString(1) + " || Team 2 == "+rs.getString(2)+" || Date == "+rs.getString(3)
      // ).forEach(str -> System.out.println(str));
      
      Map<String , Team> teamData = new HashMap<>();

      entityManager.createQuery("select m.team1 , count(*) from Match m group by m.team1" , Object[].class)  // getting team with number of total matches played by that team
        .getResultList()
        .stream()
        .map(e -> new Team((String)e[0], (long)e[1]))
        .forEach(team -> teamData.put(team.getTeamName(), team));

        entityManager.createQuery("select m.team2 , count(*) from Match m group by m.team2" , Object[].class)
        .getResultList()
        .stream()
        .forEach(e -> {
          Team team = teamData.get((String) e[0]);
          team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
        });

        entityManager.createQuery("select m.winningTeam , count(*) from Match m group by m.winningTeam" , Object[].class)
        .getResultList()
        .stream()
        .forEach(e -> {
          Team team = teamData.get((String) e[0]);
          if(team != null){
            team.setTotalWins((long) e[1]);
          }
        });

        teamData.values().forEach(team -> entityManager.persist(team));
        teamData.values().forEach(team -> System.out.println(team));
    }
  }
}
