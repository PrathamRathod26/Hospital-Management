package com.project.HospitalManagement.util;

import com.project.HospitalManagement.entity.Doctor;
import com.project.HospitalManagement.entity.Patient;
import com.project.HospitalManagement.entity.User;
import com.project.HospitalManagement.enums.BloodGroup;
import com.project.HospitalManagement.enums.Gender;
import com.project.HospitalManagement.enums.Role;
import com.project.HospitalManagement.repository.DoctorRepository;
import com.project.HospitalManagement.repository.PatientRepository;
import com.project.HospitalManagement.repository.UserRepository;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServiceUtils {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private final PasswordEncoder passwordEncoder;

    public ServiceUtils(DoctorRepository doctorRepository, UserRepository userRepository, PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getOrCreateUser(String email, String password, Role role){
        return userRepository.findByEmailAndRole(email, role)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPassword(passwordEncoder.encode(password));
                    newUser.setRole(Role.PATIENT);
                    return userRepository.save(newUser);
                });
    }
    public Patient getOrCreatePatient(
            User user,
            String firstName,
            String lastName,
            String phone,
            Integer age,
            String gender,
            String bloodGroup,
            String emergencyContact
    ){
        return patientRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Patient newPatient = new Patient();
                    newPatient.setUser(user);
                    newPatient.setFirstName(firstName);
                    newPatient.setLastName(lastName);
                    newPatient.setPhone(phone);
                    newPatient.setAge(age);
                    newPatient.setGender(Gender.valueOf(gender));
                    newPatient.setBloodGroup(BloodGroup.valueOf(bloodGroup));
                    newPatient.setEmergencyContact(emergencyContact);
                    return patientRepository.save(newPatient);
                });
    }
//    public Doctor getOrCreateDoctor(){}
}
