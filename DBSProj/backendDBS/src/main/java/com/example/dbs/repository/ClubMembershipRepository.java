package com.example.dbs.repository;

import com.example.dbs.model.ClubMembership;
import com.example.dbs.model.ClubMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, ClubMembershipId> {
}
