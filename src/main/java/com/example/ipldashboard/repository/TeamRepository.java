package com.example.ipldashboard.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ipldashboard.model.Team;

@Repository
public interface TeamRepository extends CrudRepository<Team , Long >{
    
    public Team findByTeamName(String teamName);

}
