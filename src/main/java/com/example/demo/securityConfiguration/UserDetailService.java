package com.example.demo.securityConfiguration;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.model.Employee;
import com.example.demo.model.HR;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.HRRepository;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private HRRepository hrRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            Admin adminObj = admin.get();
            return User.builder()
                    .username(adminObj.getUsername())
                    .password(adminObj.getPassword())
                    .roles("ADMIN") 
                    .build();
        }

        Optional<HR> hr = hrRepository.findByUsername(username);
        if (hr.isPresent()) {
            HR hrObj = hr.get();
            return User.builder()
                    .username(hrObj.getFirstName())
                    .password(hrObj.getPassword())
                    .roles("HR") 
                    .build();
        }

        Optional<Employee> employee = employeeRepository.findByUsername(username);
        if (employee.isPresent()) {
            Employee employeeObj = employee.get();
            return User.builder()
                    .username(employeeObj.getUsername())
                    .password(employeeObj.getPassword())
                    .roles("EMPLOYEE") 
                    .build();
        }

        throw new UsernameNotFoundException(username);
    }
}