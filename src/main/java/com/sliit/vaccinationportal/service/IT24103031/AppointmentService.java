package com.sliit.vaccinationportal.service.IT24103031;

import com.sliit.vaccinationportal.model.Appointment;
import com.sliit.vaccinationportal.model.Hospital;
import com.sliit.vaccinationportal.repository.IT24103031.AppointmentRepository;
import com.sliit.vaccinationportal.repository.IT24103048.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public void saveAppointment(Appointment appointment) {
        appointment.setStatus("PENDING");
        appointmentRepository.save(appointment);
    }
}