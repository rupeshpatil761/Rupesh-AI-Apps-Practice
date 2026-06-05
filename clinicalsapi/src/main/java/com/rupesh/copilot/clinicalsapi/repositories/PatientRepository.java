package com.rupesh.copilot.clinicalsapi.repositories;

import com.rupesh.copilot.clinicalsapi.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}

