package com.fortaleza.svc.firmadigital.repo;

import com.fortaleza.svc.firmadigital.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureRepo extends JpaRepository<Signature, Integer> {
}
