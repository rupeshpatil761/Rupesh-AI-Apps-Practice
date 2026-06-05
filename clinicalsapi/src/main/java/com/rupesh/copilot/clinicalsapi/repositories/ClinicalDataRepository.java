package com.rupesh.copilot.clinicalsapi.repositories;

import com.rupesh.copilot.clinicalsapi.models.ClinicalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalDataRepository extends JpaRepository<ClinicalData, Long> {

    List<ClinicalData> findByPatientId(Long patientId);

    List<ClinicalData> findByComponentName(String componentName);
}

