import org.atelier1.exception.CannotDeleteAdminException;
import org.atelier1.exception.EmailAlreadyExistsException;
import org.atelier1.exception.EmployeeNotFoundException;
import org.atelier1.model.Employee;
import org.atelier1.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {
    private EmployeeService employeeService;
    private Map<Long, Employee> mockEmployeeDatabase;

    @BeforeEach
    public void setUp() {
        mockEmployeeDatabase = Mockito.mock(Map.class);
        employeeService = new EmployeeService() {
            {
                employeeDatabase = mockEmployeeDatabase;
            }
        };
    }

    @Test
    public void testAddEmployee() {
        Employee employee = new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now(), new ArrayList<>());
        when(mockEmployeeDatabase.values()).thenReturn(Collections.emptyList());
        when(mockEmployeeDatabase.put(anyLong(), any(Employee.class))).thenReturn(employee);

        Employee result = employeeService.addEmployee("John", "Doe", "john.doe@example.com", "Developer", LocalDate.now());

        assertNotNull(result.getId());
        assertEquals("John", result.getFirstName());
        verify(mockEmployeeDatabase, times(1)).put(anyLong(), any(Employee.class));
    }

    @Test
    public void testAddEmployeeDuplicateEmail() {
        Employee employee = new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now(), new ArrayList<>());
        when(mockEmployeeDatabase.values()).thenReturn(Collections.singletonList(employee));

        assertThrows(EmailAlreadyExistsException.class, () ->
                employeeService.addEmployee("Jane", "Smith", "john.doe@example.com", "Manager", LocalDate.now()));
    }

    @Test
    public void testDeleteEmployee() {
        Employee employee = new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now(), new ArrayList<>());
        when(mockEmployeeDatabase.get(1L)).thenReturn(employee);

        employeeService.deleteEmployee(1L);

        verify(mockEmployeeDatabase, times(1)).remove(1L);
    }

    @Test
    public void testDeleteEmployeeAdmin() {
        Employee employee = new Employee(1L, "Admin", "User", "admin@example.com", "ADMIN", LocalDate.now(), new ArrayList<>());
        when(mockEmployeeDatabase.get(1L)).thenReturn(employee);
        when(mockEmployeeDatabase.values()).thenReturn(Collections.singletonList(employee));

        assertThrows(CannotDeleteAdminException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    public void testUpdateEmployee() {
        Employee employee = new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now(), new ArrayList<>());
        when(mockEmployeeDatabase.get(1L)).thenReturn(employee);

        Employee result = employeeService.updateEmployee(1L, "Jane", "Smith", "jane.smith@example.com", "Manager", LocalDate.now());

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    public void testListAllEmployees() {
        List<Employee> employeeList = Arrays.asList(
                new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now().minusDays(10), new ArrayList<>()),
                new Employee(2L, "Jane", "Smith", "jane.smith@example.com", "Manager", LocalDate.now().minusDays(5), new ArrayList<>())
        );
        when(mockEmployeeDatabase.values()).thenReturn(employeeList);

        List<Employee> result = employeeService.listAllEmployees();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    public void testSearchEmployees() {
        List<Employee> employeeList = Arrays.asList(
                new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now().minusDays(10), new ArrayList<>()),
                new Employee(2L, "Jane", "Smith", "jane.smith@example.com", "Manager", LocalDate.now().minusDays(5), new ArrayList<>())
        );
        when(mockEmployeeDatabase.values()).thenReturn(employeeList);

        List<Employee> result = employeeService.searchEmployees("John", null, null, 1, 10);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    public void testAssignProjectsToEmployee() {
        Employee employee = new Employee(1L, "John", "Doe", "john.doe@example.com", "Developer", LocalDate.now(), new ArrayList<>());
        when(mockEmployeeDatabase.get(1L)).thenReturn(employee);

        employeeService.assignProjectsToEmployee(1L, Arrays.asList("Project1", "Project2"));

        assertTrue(employee.getProjects().contains("Project1"));
        assertTrue(employee.getProjects().contains("Project2"));
    }
}
