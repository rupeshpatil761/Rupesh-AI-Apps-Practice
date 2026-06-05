package com.rupesh.copilot.clinicalsapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "clinical_data")
public class ClinicalData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "component_name")
    private String componentName;
    
    @Column(name = "component_value")
    private String componentValue;
    
    @Column(name = "measured_date_time")
    private Timestamp measuredDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;
    
    public ClinicalData() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getComponentName() {
        return componentName;
    }
    
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
    
    public String getComponentValue() {
        return componentValue;
    }
    
    public void setComponentValue(String componentValue) {
        this.componentValue = componentValue;
    }
    
    public Timestamp getMeasuredDateTime() {
        return measuredDateTime;
    }
    
    public void setMeasuredDateTime(Timestamp measuredDateTime) {
        this.measuredDateTime = measuredDateTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}


