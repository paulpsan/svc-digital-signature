package com.msejemplo01.msfirmadigital.repo;

import com.msejemplo01.msfirmadigital.model.Signatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatoryRepo extends JpaRepository<Signatory, Integer> {
}
