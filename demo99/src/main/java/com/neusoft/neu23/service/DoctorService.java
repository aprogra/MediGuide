package com.neusoft.neu23.service;

import com.neusoft.neu23.entity.Doctor;
import com.neusoft.neu23.entity.Patient;

import java.util.List;

public interface DoctorService {
    Doctor getByID(int id);
    
    List<Patient> getPatientsByDoctorId(int doctorId);
    
    Patient getMinNumPatientByDoctorId(int doctorId);
}
