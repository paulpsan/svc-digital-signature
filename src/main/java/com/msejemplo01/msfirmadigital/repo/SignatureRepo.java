package com.msejemplo01.msfirmadigital.repo;

import com.msejemplo01.msfirmadigital.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureRepo extends JpaRepository<Signature, Integer> {
}
