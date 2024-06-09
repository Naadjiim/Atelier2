package org.atelier1.service;

import org.atelier1.exception.CannotDeleteAdminException;
import org.atelier1.exception.EmailAlreadyExistsException;
import org.atelier1.exception.EmployeeNotFoundException;
import org.atelier1.model.Employee;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {
    public Map<Long, Employee> employeeDatabase = new HashMap<>();
    private Long idCounter = 1L;

    public Employee addEmployee(String firstName, String lastName, String email, String position, LocalDate hireDate) {
        if (employeeDatabase.values().stream().anyMatch(emp -> emp.getEmail().equals(email))) {
            throw new EmailAlreadyExistsException("Employee with email " + email + " already exists.");
        }
        Employee employee = new Employee(idCounter++, firstName, lastName, email, position, hireDate, new ArrayList<>());
        employeeDatabase.put(employee.getId(), employee);
        return employee;
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeDatabase.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        if ("ADMIN".equals(employee.getPosition()) && employeeDatabase.values().stream().filter(emp -> "ADMIN".equals(emp.getPosition())).count() == 1) {
            throw new CannotDeleteAdminException("Cannot delete the only admin.");
        }
        employeeDatabase.remove(id);
    }

    public Employee updateEmployee(Long id, String firstName, String lastName, String email, String position, LocalDate hireDate) {
        Employee employee = employeeDatabase.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPosition(position);
        employee.setHireDate(hireDate);
        return employee;
    }

    public List<Employee> listAllEmployees() {
        return employeeDatabase.values().stream()
                .sorted(Comparator.comparing(Employee::getHireDate))
                .collect(Collectors.toList());
    }

    public List<Employee> searchEmployees(String firstName, String lastName, String position, int page, int size) {
        return employeeDatabase.values().stream()
                .filter(emp -> (firstName == null || emp.getFirstName().equalsIgnoreCase(firstName)) &&
                        (lastName == null || emp.getLastName().equalsIgnoreCase(lastName)) &&
                        (position == null || emp.getPosition().equalsIgnoreCase(position)))
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public void assignProjectsToEmployee(Long id, List<String> projects) {
        Employee employee = employeeDatabase.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        List<String> currentProjects = (List<String>) employee.getProjects();
        for (String project : projects) {
            if (!currentProjects.contains(project)) {
                currentProjects.add(project);
            }
        }
        employee.setProjects((List<String>) currentProjects);
    }
}
