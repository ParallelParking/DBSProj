package com.example.dbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.ClubMembership;
import com.example.dbs.model.ClubMembershipId;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, ClubMembershipId> {
    
    List<ClubMembership> findByStuEmail(String stuEmail);

    List<ClubMembership> findByClubName(String clubName);

}
