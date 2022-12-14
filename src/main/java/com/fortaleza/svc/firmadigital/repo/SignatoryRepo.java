package com.fortaleza.svc.firmadigital.repo;

import com.fortaleza.svc.firmadigital.model.Signatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatoryRepo extends JpaRepository<Signatory, Integer> {
}
