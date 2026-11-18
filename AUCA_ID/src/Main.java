

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Person> people = new ArrayList<>();
    private static final List<Department> departments = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Classroom> classrooms = new ArrayList<>();
    private static final List<Enrollment> enrollments = new ArrayList<>();
    private static final List<Assessment> assessments = new ArrayList<>();
    private static final List<FeePayment> payments = new ArrayList<>();
    private static final List<LibraryBook> books = new ArrayList<>();
    private static final List<BookLoan> loans = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("AUCA_ID School Management System");
        while (true) {
            printMenu();
            int choice = readInt("Select: ", 0, 19);
            try {
                if (choice == 0) {
                    System.out.println("Goodbye.");
                    return;
                } else if (choice == 1) {
                    registerDepartment();
                } else if (choice == 2) {
                    registerStudent();
                } else if (choice == 3) {
                    registerTeacher();
                } else if (choice == 4) {
                    registerAdministrator();
                } else if (choice == 5) {
                    registerCourse();
                } else if (choice == 6) {
                    registerClassroom();
                } else if (choice == 7) {
                    enrollStudent();
                } else if (choice == 8) {
                    recordAssessment();
                } else if (choice == 9) {
                    recordFeePayment();
                } else if (choice == 10) {
                    registerLibraryBook();
                } else if (choice == 11) {
                    borrowBook();
                } else if (choice == 12) {
                    returnBook();
                } else if (choice == 13) {
                    viewAllRecords();
                } else if (choice == 14) {
                    searchById();
                } else if (choice == 15) {
                    updateRecord();
                } else if (choice == 16) {
                    showStudentAcademicSummary();
                } else if (choice == 17) {
                    showPaymentHistory();
                } else if (choice == 18) {
                    showAvailableBooks();
                } else if (choice == 19) {
                    demonstratePolymorphism();
                }
            } catch (ValidationException | DuplicateRecordException | RecordNotFoundException |
                     PrerequisiteMissingException | BookUnavailableException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        String border = "+--------------------------------------+";
        String[] options = {
                "1 Register Department",
                "2 Register Student",
                "3 Register Teacher",
                "4 Register Administrator",
                "5 Register Course",
                "6 Register Classroom",
                "7 Enroll Student",
                "8 Record Assessment",
                "9 Record Fee Payment",
                "10 Register Library Book",
                "11 Borrow Book",
                "12 Return Book",
                "13 View All Records",
                "14 Search by ID",
                "15 Update Record",
                "16 Show Student Academic Summary",
                "17 Show Payment History",
                "18 Show Available Books",
                "19 Demonstrate Polymorphism",
                "0 Exit"
        };

        System.out.println("\n" + border);
        System.out.printf("| %-36s |%n", "AUCA_ID Main Menu");
        System.out.println(border);
        for (String option : options) {
            System.out.printf("| %-36s |%n", option);
        }
        System.out.println(border);
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static String requiredText(String prompt) {
        while (true) {
            String value = readText(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int number = scanner.nextInt();
                scanner.nextLine();

                if (number >= min && number <= max) {
                    return number;
                }
            } else {
                scanner.nextLine();
            }

            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextDouble()) {
                double number = scanner.nextDouble();
                scanner.nextLine();

                if (Double.isFinite(number)) {
                    return number;
                }
                System.out.println("Enter a finite number.");
            } else {
                scanner.nextLine();
                System.out.println("Enter a valid number.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            String value = readText(prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid calendar date. Use yyyy-MM-dd.");
            }
        }
    }

    private static BigDecimal readMoney(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextBigDecimal()) {
                BigDecimal amount = scanner.nextBigDecimal();
                scanner.nextLine();
                return amount;
            }

            scanner.nextLine();
            System.out.println("Enter a valid amount.");
        }
    }

    private static boolean confirmReview(String summary) {
        System.out.println("\n--- Review ---\n" + summary);
        int choice = readInt("1 Save, 2 Save then update later, 0 Cancel: ", 0, 2);
        if (choice == 0) {
            System.out.println("Cancelled. No record was created.");
            return false;
        }
        if (choice == 2) {
            System.out.println("Saved. Use menu option 15 (Update Record) to make changes.");
        }
        return true;
    }

    private static void uniqueId(String id) throws DuplicateRecordException {
        for (Identifiable record : allIdentifiable()) {
            if (record.getId().equalsIgnoreCase(id)) {
                throw new DuplicateRecordException("Duplicate ID: " + id);
            }
        }
    }

    private static void requireUniqueCode(String value, String label, List<String> existing)
            throws DuplicateRecordException {
        for (String code : existing) {
            if (code.equalsIgnoreCase(value)) {
                throw new DuplicateRecordException("Duplicate " + label + ": " + value);
            }
        }
    }

    private static void registerDepartment()
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        if (students().isEmpty()) {
            System.out.println("Error: You can't register a department before registering a student.");
            return;
        }

        String id = requiredText("Department ID (0 or Back to cancel): ");
        if (id.equalsIgnoreCase("0") || id.equalsIgnoreCase("back")) {
            return;
        }

        uniqueId(id);
        String name = requiredText("Department name: ");
        String code = requiredText("Department code: ");
        List<String> departmentCodes = new ArrayList<>();
        for (Department existing : departments) {
            departmentCodes.add(existing.getCode());
        }
        requireUniqueCode(code, "department code", departmentCodes);
        String location = requiredText("Location: ");
        Teacher head = null;

        if (!teachers().isEmpty()) {
            int useHead = readInt("Assign head? 1 Yes, 0 No: ", 0, 1);
            if (useHead == 1) {
                head = selectTeacher("Select department head");
            }
        }

        Department department = new Department(id, name, code, location, head, true);
        department.validate();
        if (confirmReview(department.getRegistrationSummary())) {
            departments.add(department);
        }
    }

    private static void registerStudent()
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        Department department = null;
        if (!departments.isEmpty()) {
            int assignDepartment = readInt("Assign a department now? 1 Yes, 0 No: ", 0, 1);
            if (assignDepartment == 1) {
                department = selectDepartment("Select Department");
            }
        }

        String id = requiredText("Student personal ID: ");
        uniqueId(id);
        String name = requiredText("Full name: ");
        String email = requiredText("Email: ");
        String phone = requiredText("Phone: ");
        LocalDate dob = readDate("Date of birth");
        String studentNumber = requiredText("Student number: ");
        List<String> studentCodes = new ArrayList<>();
        for (Student existing : students()) {
            studentCodes.add(existing.getStudentNumber());
        }
        requireUniqueCode(studentNumber, "student number", studentCodes);
        String program = requiredText("Program: ");
        String major = requiredText("Major: ");
        double gpa = readDouble("GPA (0.0-4.0): ");
        LocalDate enrollmentDate = readDate("Enrollment date");
        int year = readInt("Study year (1-6): ", 1, 6);
        String officeRoom = readText("Office room (optional): ");
        String academicCode = studentNumber;
        String status = "ACTIVE";
        boolean active = true;
        boolean scholarship = readInt("Scholarship? 1 Yes, 0 No: ", 0, 1) == 1;
        Student student = new Student(
                id, name, email, phone, dob, active, department, academicCode, enrollmentDate,
                status, year, officeRoom, studentNumber, program, major, gpa, enrollmentDate,
                scholarship);
        student.validate();
        if (confirmReview(student.getRegistrationSummary())) {
            people.add(student);
            if (department == null) {
                System.out.println(
                        "Register a department, then use Update Record to assign it to this student.");
            }
        }
    }

    private static void registerTeacher()
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        registerTeacher(null);
    }

    private static void registerTeacher(Department requiredDepartment)
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        Department department = requiredDepartment;
        if (department == null) {
            department = ensureDepartment("Teacher");
        }
        if (department == null) {
            return;
        }

        String id = requiredText("Teacher personal ID: ");
        uniqueId(id);
        String name = requiredText("Full name: ");
        String email = requiredText("Email: ");
        String phone = requiredText("Phone: ");
        LocalDate dob = readDate("Date of birth");
        String employeeNumber = requiredText("Employee number: ");
        List<String> employeeCodes = new ArrayList<>();
        for (Teacher existing : teachers()) {
            employeeCodes.add(existing.getEmployeeNumber());
        }
        requireUniqueCode(employeeNumber, "employee number", employeeCodes);
        String qualification = requiredText("Qualification: ");
        BigDecimal salary = readMoney("Salary: ");
        LocalDate hireDate = readDate("Hire date");
        boolean fullTime = readInt("Full-time? 1 Yes, 0 No: ", 0, 1) == 1;
        String specialization = requiredText("Specialization: ");
        String officeRoom = readText("Office room (optional): ");
        Teacher teacher = new Teacher(
                id, name, email, phone, dob, true, department, employeeNumber, hireDate, "ACTIVE",
                1, officeRoom, employeeNumber, qualification, salary, hireDate, fullTime,
                specialization);
        teacher.validate();
        if (confirmReview(teacher.getRegistrationSummary())) {
            people.add(teacher);
        }
    }

    private static void registerAdministrator()
            throws DuplicateRecordException, ValidationException {
        String id = requiredText("Administrator ID: ");
        uniqueId(id);
        String name = requiredText("Full name: ");
        String email = requiredText("Email: ");
        String phone = requiredText("Phone: ");
        LocalDate dob = readDate("Date of birth");
        String administratorNumber = requiredText("Administrator number: ");
        String departmentName = requiredText("Department name: ");
        String position = requiredText("Position: ");
        LocalDate hireDate = readDate("Hire date");
        boolean fullTime = readInt("Full-time? 1 Yes, 0 No: ", 0, 1) == 1;
        String accessLevel = requiredText("Access level (LOW, MEDIUM, HIGH, ADMIN): ").toUpperCase();
        Administrator administrator = new Administrator(
                id, name, email, phone, dob, true, administratorNumber, departmentName, position,
                hireDate, fullTime, accessLevel);
        administrator.validate();
        if (confirmReview(administrator.getRegistrationSummary())) {
            people.add(administrator);
        }
    }

    private static void registerCourse()
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        Department department = ensureDepartment("Course");
        if (department == null) {
            return;
        }

        if (teachersForDepartment(department).isEmpty()) {
            System.out.println("No Teacher is registered in the selected Department.");
            int choice = readInt("1 Register Teacher now, 0 Cancel course: ", 0, 1);
            if (choice == 0) {
                return;
            }

            registerTeacher(department);
            if (teachersForDepartment(department).isEmpty()) {
                return;
            }
        }

        Teacher teacher = selectTeacher("Select course teacher", department);
        String id = requiredText("Course ID: ");
        uniqueId(id);
        String name = requiredText("Course name: ");
        String code = requiredText("Course code: ");
        List<String> courseCodes = new ArrayList<>();
        for (Course existing : courses) {
            courseCodes.add(existing.getCode());
        }
        requireUniqueCode(code, "course code", courseCodes);
        int creditHours = readInt("Credit hours (1-6): ", 1, 6);
        Course course = new Course(id, name, code, creditHours, department, teacher);
        course.validate();
        if (confirmReview(course.getRegistrationSummary())) {
            courses.add(course);
        }
    }

    private static void registerClassroom()
            throws DuplicateRecordException, ValidationException {
        String id = requiredText("Classroom ID: ");
        uniqueId(id);
        String roomNumber = requiredText("Room number: ");
        int capacity = readInt("Capacity: ", 1, 100000);
        int floor = readInt("Floor number: ", 0, 1000);
        String building = requiredText("Building: ");
        Classroom classroom = new Classroom(id, roomNumber, capacity, floor, building, true);
        classroom.validate();
        if (confirmReview(classroom.getRegistrationSummary())) {
            classrooms.add(classroom);
        }
    }

    private static void enrollStudent()
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        if (students().isEmpty()) {
            System.out.println("No Student is registered.");
            int choice = readInt("1 Register Student now, 0 Cancel enrollment: ", 0, 1);
            if (choice == 0) {
                return;
            }
            registerStudent();
        }

        if (courses.isEmpty()) {
            System.out.println("No Course is registered.");
            int choice = readInt("1 Register Course now, 0 Cancel enrollment: ", 0, 1);
            if (choice == 0) {
                return;
            }
            registerCourse();
        }

        Student student = selectStudent("Select Student");
        Course course = selectCourse("Select Course");
        String semester = requiredText("Semester (e.g. 2026-FALL): ");
        LocalDate date = readDate("Enrollment date");
        for (Enrollment enrollment : enrollments) {
            boolean sameStudent = enrollment.getStudent() == student;
            boolean sameCourse = enrollment.getCourse() == course;
            boolean sameSemester = enrollment.getSemester().equalsIgnoreCase(semester);
            if (enrollment.isActive() && sameStudent && sameCourse && sameSemester) {
                throw new DuplicateRecordException(
                        "Student is already actively enrolled in this course for the semester.");
            }
        }

        String id = requiredText("Enrollment ID: ");
        uniqueId(id);
        Enrollment enrollment = new Enrollment(id, student, course, semester, date, true);
        enrollment.validate();
        if (confirmReview(enrollment.getRegistrationSummary())) {
            enrollments.add(enrollment);
        }
    }

    private static void recordAssessment()
            throws PrerequisiteMissingException, RecordNotFoundException,
            DuplicateRecordException, ValidationException {
        if (activeEnrollments().isEmpty()) {
            throw new PrerequisiteMissingException(
                    "No active enrollment exists. Enroll a student first.");
        }

        Enrollment enrollment = selectEnrollment("Select active enrollment");
        String id = requiredText("Assessment ID: ");
        uniqueId(id);
        String type = requiredText("Assessment type (QUIZ, ASSIGNMENT, MIDTERM, FINAL, PROJECT): ").toUpperCase();
        double maximum = readDouble("Maximum score: ");
        double obtained = readDouble("Obtained score: ");
        LocalDate date = readDate("Assessment date");
        Assessment assessment = new Assessment(id, enrollment, type, maximum, obtained, date);
        assessment.validate();
        if (confirmReview(assessment.getRegistrationSummary())) {
            assessments.add(assessment);
        }
    }

    private static void recordFeePayment()
            throws PrerequisiteMissingException, RecordNotFoundException,
            DuplicateRecordException, ValidationException {
        if (students().isEmpty()) {
            throw new PrerequisiteMissingException("No Student is registered.");
        }

        Student student = selectStudent("Select Student");
        String id = requiredText("Payment ID: ");
        uniqueId(id);
        BigDecimal amount = readMoney("Amount: ");
        LocalDate date = readDate("Payment date");
        String method = requiredText("Method (CASH, CARD, MOBILE_MONEY, BANK_TRANSFER): ").toUpperCase();
        String receipt = requiredText("Receipt number: ");
        for (FeePayment payment : payments) {
            if (payment.getReceiptNumber().equalsIgnoreCase(receipt)) {
                throw new DuplicateRecordException("Duplicate receipt number: " + receipt);
            }
        }

        FeePayment payment = new FeePayment(id, student, amount, date, method, receipt);
        payment.validate();
        if (confirmReview(payment.getRegistrationSummary())) {
            payments.add(payment);
        }
    }

    private static void registerLibraryBook()
            throws DuplicateRecordException, ValidationException {
        String id = requiredText("Book ID: ");
        uniqueId(id);
        String title = requiredText("Title: ");
        String isbn = requiredText("ISBN: ");
        String author = requiredText("Author: ");
        int total = readInt("Total copies: ", 0, 100000);
        int available = readInt("Available copies: ", 0, total);
        LibraryBook book = new LibraryBook(id, title, isbn, author, total, available);
        book.validate();
        if (confirmReview(book.getRegistrationSummary())) {
            books.add(book);
        }
    }

    private static void borrowBook()
            throws PrerequisiteMissingException, RecordNotFoundException,
            BookUnavailableException, DuplicateRecordException, ValidationException {
        if (students().isEmpty()) {
            throw new PrerequisiteMissingException("No Student is registered.");
        }
        if (books.isEmpty()) {
            throw new PrerequisiteMissingException("No Library Book is registered.");
        }

        Student student = selectStudent("Select Student");
        LibraryBook book = selectAvailableBook("Select available book");
        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("Selected book has no available copies.");
        }

        String id = requiredText("Loan ID: ");
        uniqueId(id);
        LocalDate borrowed = readDate("Borrowed date");
        LocalDate due = readDate("Due date");
        BookLoan loan = new BookLoan(id, student, book, borrowed, due, null);
        loan.validate();
        if (confirmReview(loan.getRegistrationSummary())) {
            loans.add(loan);
            book.setAvailableCopies(book.getAvailableCopies() - 1);
        }
    }

    private static void returnBook()
            throws RecordNotFoundException, ValidationException {
        if (activeLoans().isEmpty()) {
            throw new RecordNotFoundException("No active loan exists.");
        }
        BookLoan active = selectActiveLoan("Select loan to return");
        LocalDate returned = readDate("Returned date");
        if (returned.isBefore(active.getBorrowedDate())) {
            throw new ValidationException("Returned date cannot be before borrowed date.");
        }
        active.setReturnedDate(returned);
        active.getBook().setAvailableCopies(active.getBook().getAvailableCopies() + 1);
        System.out.println("Book returned successfully.");
    }

    private static void viewAllRecords() {
        System.out.println("\n--- Departments ---");
        for (Department department : departments) {
            System.out.println(department);
        }

        System.out.println("--- People ---");
        for (Person person : people) {
            System.out.println(person);
        }

        System.out.println("--- Courses ---");
        for (Course course : courses) {
            System.out.println(course);
        }

        System.out.println("--- Classrooms ---");
        for (Classroom classroom : classrooms) {
            System.out.println(classroom);
        }

        System.out.println("--- Enrollments ---");
        for (Enrollment enrollment : enrollments) {
            System.out.println(enrollment);
        }

        System.out.println("--- Assessments ---");
        for (Assessment assessment : assessments) {
            System.out.println(assessment);
        }

        System.out.println("--- Fee Payments ---");
        for (FeePayment payment : payments) {
            System.out.println(payment);
        }

        System.out.println("--- Library Books ---");
        for (LibraryBook book : books) {
            System.out.println(book);
        }

        System.out.println("--- Book Loans ---");
        for (BookLoan loan : loans) {
            System.out.println(loan);
        }
    }

    private static void searchById() throws RecordNotFoundException {
        String id = requiredText("Enter ID: ");
        for (Identifiable record : allIdentifiable()) {
            if (record.getId().equalsIgnoreCase(id)) {
                System.out.println(record.getDisplayName());
                if (record instanceof Registrable registrable) {
                    registrable.printDetails();
                }
                return;
            }
        }

        throw new RecordNotFoundException("No record found with ID " + id);
    }

    private static void updateRecord() throws RecordNotFoundException {
        String id = requiredText("Enter record ID to update: ");

        for (Person person : people) {
            if (person.getId().equalsIgnoreCase(id)) {
                String newName = readText("New name (leave blank for unchanged): ");
                if (!newName.isBlank()) {
                    person.setName(newName);
                }
                person.setActive(readInt("1 Active, 0 Inactive: ", 0, 1) == 1);

                if (person instanceof Student student && !departments.isEmpty()) {
                    int changeDepartment = readInt(
                            "1 Assign/change department, 0 Keep current department: ", 0, 1);
                    if (changeDepartment == 1) {
                        student.setDepartment(selectDepartment("Select Department"));
                    }
                }

                System.out.println("Person updated.");
                return;
            }
        }

        for (Department department : departments) {
            if (department.getId().equalsIgnoreCase(id)) {
                department.setName(requiredText("New name: "));
                System.out.println("Department updated.");
                return;
            }
        }

        for (Course course : courses) {
            if (course.getId().equalsIgnoreCase(id)) {
                course.setName(requiredText("New name: "));
                System.out.println("Course updated.");
                return;
            }
        }

        for (LibraryBook book : books) {
            if (book.getId().equalsIgnoreCase(id)) {
                book.setTitle(requiredText("New title: "));
                System.out.println("Book updated.");
                return;
            }
        }

        throw new RecordNotFoundException("No editable record found with ID " + id);
    }

    private static void showStudentAcademicSummary() throws RecordNotFoundException {
        if (students().isEmpty()) {
            throw new RecordNotFoundException("No Students are registered.");
        }

        Student student = selectStudent("Select Student");
        System.out.println("\nAcademic Summary for " + student.getName());
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent() == student) {
                System.out.println(enrollment.getRegistrationSummary());
                for (Assessment assessment : assessments) {
                    if (assessment.getEnrollment() == enrollment) {
                        assessment.printDetails();
                    }
                }
            }
        }
    }

    private static void showPaymentHistory() throws RecordNotFoundException {
        if (students().isEmpty()) {
            throw new RecordNotFoundException("No Students are registered.");
        }

        Student student = selectStudent("Select Student");
        boolean found = false;
        for (FeePayment payment : payments) {
            if (payment.getStudent() == student) {
                payment.printDetails();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No payment history.");
        }
    }

    private static void showAvailableBooks() {
        for (LibraryBook book : books) {
            if (book.getAvailableCopies() > 0) {
                book.printDetails();
            }
        }
    }

    private static void demonstratePolymorphism() {
        System.out.println("\nRuntime polymorphism - Person references:");
        for (Person person : people) {
            System.out.println(person.getRoleDescription() + ": " + person.getRegistrationSummary());
        }

        System.out.println("\nInterface polymorphism - Identifiable references:");
        for (Identifiable record : allIdentifiable()) {
            System.out.println(record.getId() + " | " + record.getDisplayName()
                    + " | active=" + record.isActive());
        }
    }

    private static Department ensureDepartment(String forType)
            throws DuplicateRecordException, RecordNotFoundException, ValidationException {
        if (departments.isEmpty()) {
            System.out.println("No Department is registered for " + forType + ".");
            int choice = readInt("1 Register Department now, 0 Cancel: ", 0, 1);
            if (choice == 1) {
                registerDepartment();
            }
            if (departments.isEmpty()) {
                return null;
            }
        }
        return selectDepartment("Select Department");
    }

    private static List<Student> students() {
        List<Student> result = new ArrayList<>();
        for (Person person : people) {
            if (person instanceof Student student) {
                result.add(student);
            }
        }
        return result;
    }

    private static List<Teacher> teachers() {
        List<Teacher> result = new ArrayList<>();
        for (Person person : people) {
            if (person instanceof Teacher teacher) {
                result.add(teacher);
            }
        }
        return result;
    }

    private static List<Teacher> teachersForDepartment(Department department) {
        List<Teacher> result = new ArrayList<>();
        for (Teacher teacher : teachers()) {
            if (teacher.getDepartment() == department) {
                result.add(teacher);
            }
        }
        return result;
    }

    private static List<Enrollment> activeEnrollments() {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.isActive()) {
                result.add(enrollment);
            }
        }
        return result;
    }

    private static List<BookLoan> activeLoans() {
        List<BookLoan> result = new ArrayList<>();
        for (BookLoan loan : loans) {
            if (loan.isActive()) {
                result.add(loan);
            }
        }
        return result;
    }

    private static Student selectStudent(String prompt) throws RecordNotFoundException {
        List<Student> list = students();
        if (list.isEmpty()) {
            throw new RecordNotFoundException("No Students.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, list.size());
        return list.get(selection - 1);
    }

    private static Teacher selectTeacher(String prompt) throws RecordNotFoundException {
        return selectTeacher(prompt, null);
    }

    private static Teacher selectTeacher(String prompt, Department department) throws RecordNotFoundException {
        List<Teacher> list;
        if (department == null) {
            list = teachers();
        } else {
            list = teachersForDepartment(department);
        }

        if (list.isEmpty()) {
            if (department == null) {
                throw new RecordNotFoundException("No Teachers.");
            }
            throw new RecordNotFoundException("No Teachers belong to the selected Department.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, list.size());
        return list.get(selection - 1);
    }

    private static Department selectDepartment(String prompt) throws RecordNotFoundException {
        if (departments.isEmpty()) {
            throw new RecordNotFoundException("No Departments.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < departments.size(); i++) {
            System.out.println((i + 1) + ". " + departments.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, departments.size());
        return departments.get(selection - 1);
    }

    private static Course selectCourse(String prompt) throws RecordNotFoundException {
        if (courses.isEmpty()) {
            throw new RecordNotFoundException("No Courses.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < courses.size(); i++) {
            System.out.println((i + 1) + ". " + courses.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, courses.size());
        return courses.get(selection - 1);
    }

    private static Enrollment selectEnrollment(String prompt) throws RecordNotFoundException {
        List<Enrollment> active = activeEnrollments();
        if (active.isEmpty()) {
            throw new RecordNotFoundException("No active Enrollments.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < active.size(); i++) {
            System.out.println((i + 1) + ". " + active.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, active.size());
        return active.get(selection - 1);
    }

    private static LibraryBook selectAvailableBook(String prompt) throws RecordNotFoundException {
        List<LibraryBook> available = new ArrayList<>();
        for (LibraryBook book : books) {
            if (book.getAvailableCopies() > 0) {
                available.add(book);
            }
        }

        if (available.isEmpty()) {
            throw new RecordNotFoundException("No available books.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". " + available.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, available.size());
        return available.get(selection - 1);
    }

    private static BookLoan selectActiveLoan(String prompt) throws RecordNotFoundException {
        List<BookLoan> active = activeLoans();
        if (active.isEmpty()) {
            throw new RecordNotFoundException("No active loans.");
        }

        System.out.println(prompt + ":");
        for (int i = 0; i < active.size(); i++) {
            System.out.println((i + 1) + ". " + active.get(i).getRegistrationSummary());
        }

        int selection = readInt("Select: ", 1, active.size());
        return active.get(selection - 1);
    }

    private static List<Identifiable> allIdentifiable() {
        List<Identifiable> result = new ArrayList<>();
        result.addAll(people);
        result.addAll(departments);
        result.addAll(courses);
        result.addAll(classrooms);
        result.addAll(enrollments);
        result.addAll(assessments);
        result.addAll(payments);
        result.addAll(books);
        result.addAll(loans);
        return result;
    }
}
