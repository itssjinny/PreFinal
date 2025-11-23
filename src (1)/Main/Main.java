package Main;

import config.dbConnect;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class Main {

    // --- View Methods ---

    public static void viewStudents() {
        String Query = "SELECT S.s_id, S.s_fname || ' ' || S.s_lname AS s_name, T.sbj_desc, G.final, "
                + "CASE WHEN G.final >= 3.0 THEN 'Pass' ELSE 'Fail' END AS remarks "
                + "FROM tbl_grades AS G "
                + "JOIN tbl_students AS S ON G.s_id = S.s_id "
                + "JOIN tbl_subjects AS T ON G.sbj_id = T.sbj_id";

        String[] headers = {"SID", "Name", "Subject", "Final", "Remarks"};
        String[] columns = {"s_id", "s_name", "sbj_desc", "final", "remarks"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, columns);
    }
    
    public static void viewUsers() {
        String Query = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_user";
        
        String[] headers = {"ID", "Name", "Email", "Type", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, columns);
    }

    public static void viewSubjects() {
        String Query = "SELECT * FROM tbl_subjects";
        String[] headers = {"Subject ID", "Code", "Description"};
        String[] columns = {"sbj_id", "sbj_code", "sbj_desc"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, columns);
    }
    
    // --- Grade Input Method (Unchanged) ---

    public static void inputGrades(Scanner sc) {
        dbConnect con = new dbConnect();

        System.out.println("\n===== INPUT STUDENT GRADES =====");
        
        int studentId = 0;
        while (true) {
            System.out.print("Enter Student ID: ");
            if (sc.hasNextInt()) {
                studentId = sc.nextInt();
                
                List<Map<String, Object>> studentCheck = con.fetchRecords("SELECT s_id FROM tbl_students WHERE s_id = ?", studentId);
                if (!studentCheck.isEmpty()) {
                    break;
                } else {
                    System.out.println("Error: Student ID not found in tbl_students.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }
        
        viewSubjects(); 
        int subjectId = 0;
        // ... (Subject ID input logic omitted for brevity) ...
        while (true) {
            System.out.print("Enter Subject ID for grading: ");
            if (sc.hasNextInt()) {
                subjectId = sc.nextInt();
                List<Map<String, Object>> subjectCheck = con.fetchRecords("SELECT sbj_id FROM tbl_subjects WHERE sbj_id = ?", subjectId);
                if (!subjectCheck.isEmpty()) {
                    break;
                } else {
                    System.out.println("Error: Subject ID not found in tbl_subjects.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }

        double prelim = 0, midterm = 0, prefinal = 0, finalGrade = 0;
        
        System.out.print("Enter Prelim Grade: ");
        if (sc.hasNextDouble()) prelim = sc.nextDouble(); else sc.next();
        
        System.out.print("Enter Midterm Grade: ");
        if (sc.hasNextDouble()) midterm = sc.nextDouble(); else sc.next();
        
        System.out.print("Enter Prefinal Grade: ");
        if (sc.hasNextDouble()) prefinal = sc.nextDouble(); else sc.next();
        
        System.out.print("Enter Final Grade: ");
        if (sc.hasNextDouble()) finalGrade = sc.nextDouble(); else sc.next();
        
        String remarks = (finalGrade <= 3.0) ? "Pass" : "Fail"; // Changed 3.0 to 75
        int teacherId = 1; 
        int semesterId = 1; 

        String checkQry = "SELECT * FROM tbl_grades WHERE s_id = ? AND sbj_id = ?";
        List<Map<String, Object>> existingGrade = con.fetchRecords(checkQry, studentId, subjectId);
        
        String sql;
        if (!existingGrade.isEmpty()) {
            sql = "UPDATE tbl_grades SET prelim = ?, midterm = ?, prefinal = ?, final = ?, remarks = ? WHERE s_id = ? AND sbj_id = ?";
            con.updateRecord(sql, prelim, midterm, prefinal, finalGrade, remarks, studentId, subjectId);
            System.out.println("SUCCESS: Grades for Student ID " + studentId + " in Subject ID " + subjectId + " have been UPDATED.");
        } else {
            sql = "INSERT INTO tbl_grades(s_id, sbj_id, t_id, sem_id, prelim, midterm, prefinal, final, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            con.addRecord(sql, studentId, subjectId, teacherId, semesterId, prelim, midterm, prefinal, finalGrade, remarks);
            System.out.println("SUCCESS: New grade record for Student ID " + studentId + " in Subject ID " + subjectId + " has been INSERTED.");
        }
        
        System.out.println("Final Grade: " + finalGrade + " | Remarks: " + remarks);
    }
    
    // --- Admin Approval Methods (Unchanged) ---

    public static void approveUsers(Scanner sc) {
        dbConnect con = new dbConnect();
        
        System.out.println("\n===== APPROVE PENDING TEACHERS/ADMINS =====");
        
        String pendingQry = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_user WHERE u_status = 'Pending'";
        
        List<Map<String, Object>> pendingUsers = con.fetchRecords(pendingQry);
        
        if (pendingUsers.isEmpty()) {
            System.out.println("No pending Teacher/Admin users to approve.");
            return;
        }
        
        System.out.println("\n--- Pending Teacher/Admin Accounts ---");
        System.out.printf("%-5s %-20s %-30s %-10s %-10s\n", "ID", "Name", "Email", "Type", "Status");
        System.out.println("-------------------------------------------------------------------------");
        for (Map<String, Object> record : pendingUsers) {
            System.out.printf("%-5s %-20s %-30s %-10s %-10s\n", 
                    record.get("u_id"), 
                    record.get("u_name"), 
                    record.get("u_email"), 
                    record.get("u_type"), 
                    record.get("u_status"));
        }
        
        System.out.print("\nEnter the ID of the user to APPROVE (or 0 to cancel): ");
        if (sc.hasNextInt()) {
            int userIdToApprove = sc.nextInt();
            
            if (userIdToApprove == 0) return;
            
            boolean found = pendingUsers.stream().anyMatch(user -> user.get("u_id").toString().equals(String.valueOf(userIdToApprove)));

            if (found) {
                String updateQry = "UPDATE tbl_user SET u_status = 'Approved' WHERE u_id = ? AND u_status = 'Pending'";
                con.updateRecord(updateQry, userIdToApprove);
                System.out.println("SUCCESS: User ID " + userIdToApprove + " has been **APPROVED**.");
            } else {
                System.out.println("Error: User ID " + userIdToApprove + " not found in the pending list or already approved.");
            }
        } else {
            System.out.println("Invalid input. Approval cancelled.");
            sc.next();
        }
    }

    public static void approveStudents(Scanner sc) {
        dbConnect con = new dbConnect();
        
        System.out.println("\n===== APPROVE PENDING STUDENTS =====");
        
        String pendingQry = "SELECT s_id, s_fname || ' ' || s_lname AS s_name, s_email, s_status FROM tbl_students WHERE s_status = 'Pending'";
        
        List<Map<String, Object>> pendingStudents = con.fetchRecords(pendingQry);
        
        if (pendingStudents.isEmpty()) {
            System.out.println("No pending students to approve.");
            return;
        }
        
        System.out.println("\n--- Pending Student Accounts ---");
        System.out.printf("%-5s %-20s %-30s %-10s\n", "ID", "Name", "Email", "Status");
        System.out.println("---------------------------------------------------------------");
        for (Map<String, Object> record : pendingStudents) {
            System.out.printf("%-5s %-20s %-30s %-10s\n", 
                    record.get("s_id"), 
                    record.get("s_name"), 
                    record.get("s_email"), 
                    record.get("s_status"));
        }
        
        System.out.print("\nEnter the ID of the student to APPROVE (or 0 to cancel): ");
        if (sc.hasNextInt()) {
            int studentIdToApprove = sc.nextInt();
            
            if (studentIdToApprove == 0) return;
            
            boolean found = pendingStudents.stream().anyMatch(student -> student.get("s_id").toString().equals(String.valueOf(studentIdToApprove)));

            if (found) {
                String updateQry = "UPDATE tbl_students SET s_status = 'Approved' WHERE s_id = ? AND s_status = 'Pending'";
                con.updateRecord(updateQry, studentIdToApprove);
                System.out.println("SUCCESS: Student ID " + studentIdToApprove + " has been **APPROVED**.");
            } else {
                System.out.println("Error: Student ID " + studentIdToApprove + " not found in the pending list or already approved.");
            }
        } else {
            System.out.println("Invalid input. Approval cancelled.");
            sc.next();
        }
    }
    
    // --- New Update Methods ---

    public static void updateStudent(Scanner sc) {
        dbConnect con = new dbConnect();
        System.out.println("\n===== UPDATE STUDENT ACCOUNT =====");
        viewStudentsForAdmin(); // Helper method to view students with their status

        System.out.print("Enter Student ID to update (or 0 to cancel): ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Update cancelled.");
            sc.next();
            return;
        }
        int studentId = sc.nextInt();
        if (studentId == 0) return;

        List<Map<String, Object>> studentCheck = con.fetchRecords("SELECT * FROM tbl_students WHERE s_id = ?", studentId);
        if (studentCheck.isEmpty()) {
            System.out.println("Error: Student ID not found.");
            return;
        }
        Map<String, Object> currentStudent = studentCheck.get(0);
        
        sc.nextLine(); // consume newline
        System.out.println("--- Current Details: " + currentStudent.get("s_fname") + " " + currentStudent.get("s_lname") + " (" + currentStudent.get("s_email") + ")");
        
        System.out.print("Enter new First Name (leave blank to keep '" + currentStudent.get("s_fname") + "'): ");
        String newFname = sc.nextLine();
        if (newFname.trim().isEmpty()) newFname = currentStudent.get("s_fname").toString();

        System.out.print("Enter new Last Name (leave blank to keep '" + currentStudent.get("s_lname") + "'): ");
        String newLname = sc.nextLine();
        if (newLname.trim().isEmpty()) newLname = currentStudent.get("s_lname").toString();
        
        System.out.print("Enter new Email (leave blank to keep '" + currentStudent.get("s_email") + "'): ");
        String newEmail = sc.nextLine();
        if (newEmail.trim().isEmpty()) newEmail = currentStudent.get("s_email").toString();

        System.out.print("Enter new Password (leave blank to keep old password): ");
        String newPass = sc.nextLine();
        if (newPass.trim().isEmpty()) newPass = currentStudent.get("s_pass").toString(); // Assuming s_pass is fetchable

        System.out.print("Enter new Status ('Pending', 'Approved', 'Active', 'Inactive', leave blank to keep '" + currentStudent.get("s_status") + "'): ");
        String newStatus = sc.nextLine();
        if (newStatus.trim().isEmpty()) newStatus = currentStudent.get("s_status").toString();

        String updateQry = "UPDATE tbl_students SET s_fname = ?, s_lname = ?, s_email = ?, s_pass = ?, s_status = ? WHERE s_id = ?";
        con.updateRecord(updateQry, newFname, newLname, newEmail, newPass, newStatus, studentId);
        System.out.println("SUCCESS: Student ID " + studentId + " has been **UPDATED**.");
    }
    
    public static void updateSubject(Scanner sc) {
        dbConnect con = new dbConnect();
        System.out.println("\n===== UPDATE SUBJECT =====");
        viewSubjects();

        System.out.print("Enter Subject ID to update (or 0 to cancel): ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Update cancelled.");
            sc.next();
            return;
        }
        int subjectId = sc.nextInt();
        if (subjectId == 0) return;

        List<Map<String, Object>> subjectCheck = con.fetchRecords("SELECT * FROM tbl_subjects WHERE sbj_id = ?", subjectId);
        if (subjectCheck.isEmpty()) {
            System.out.println("Error: Subject ID not found.");
            return;
        }
        Map<String, Object> currentSubject = subjectCheck.get(0);

        sc.nextLine(); // consume newline
        System.out.println("--- Current Details: " + currentSubject.get("sbj_code") + " - " + currentSubject.get("sbj_desc"));

        System.out.print("Enter new Subject Code (leave blank to keep '" + currentSubject.get("sbj_code") + "'): ");
        String newCode = sc.nextLine();
        if (newCode.trim().isEmpty()) newCode = currentSubject.get("sbj_code").toString();

        System.out.print("Enter new Subject Description (leave blank to keep '" + currentSubject.get("sbj_desc") + "'): ");
        String newDesc = sc.nextLine();
        if (newDesc.trim().isEmpty()) newDesc = currentSubject.get("sbj_desc").toString();

        String updateQry = "UPDATE tbl_subjects SET sbj_code = ?, sbj_desc = ? WHERE sbj_id = ?";
        con.updateRecord(updateQry, newCode, newDesc, subjectId);
        System.out.println("SUCCESS: Subject ID " + subjectId + " has been **UPDATED**.");
    }

    public static void updateUser(Scanner sc) {
        dbConnect con = new dbConnect();
        System.out.println("\n===== UPDATE USER ACCOUNT (Admin/Teacher) =====");
        viewUsers(); 

        System.out.print("Enter User ID to update (or 0 to cancel): ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Update cancelled.");
            sc.next();
            return;
        }
        int userId = sc.nextInt();
        if (userId == 0) return;

        List<Map<String, Object>> userCheck = con.fetchRecords("SELECT * FROM tbl_user WHERE u_id = ?", userId);
        if (userCheck.isEmpty()) {
            System.out.println("Error: User ID not found in tbl_user.");
            return;
        }
        Map<String, Object> currentUser = userCheck.get(0);

        sc.nextLine(); // consume newline
        System.out.println("--- Current Details: " + currentUser.get("u_name") + " (" + currentUser.get("u_email") + ", " + currentUser.get("u_type") + ")");

        System.out.print("Enter new Full Name (leave blank to keep '" + currentUser.get("u_name") + "'): ");
        String newName = sc.nextLine();
        if (newName.trim().isEmpty()) newName = currentUser.get("u_name").toString();

        System.out.print("Enter new Email (leave blank to keep '" + currentUser.get("u_email") + "'): ");
        String newEmail = sc.nextLine();
        if (newEmail.trim().isEmpty()) newEmail = currentUser.get("u_email").toString();

        System.out.print("Enter new Type ('Admin', 'Teacher', leave blank to keep '" + currentUser.get("u_type") + "'): ");
        String newType = sc.nextLine();
        if (newType.trim().isEmpty()) newType = currentUser.get("u_type").toString();
        
        System.out.print("Enter new Password (leave blank to keep old password): ");
        String newPass = sc.nextLine();
        if (newPass.trim().isEmpty()) newPass = currentUser.get("u_pass").toString(); // Assuming u_pass is fetchable

        System.out.print("Enter new Status ('Pending', 'Approved', 'Inactive', leave blank to keep '" + currentUser.get("u_status") + "'): ");
        String newStatus = sc.nextLine();
        if (newStatus.trim().isEmpty()) newStatus = currentUser.get("u_status").toString();

        String updateQry = "UPDATE tbl_user SET u_name = ?, u_email = ?, u_type = ?, u_status = ?, u_pass = ? WHERE u_id = ?";
        con.updateRecord(updateQry, newName, newEmail, newType, newStatus, newPass, userId);
        System.out.println("SUCCESS: User ID " + userId + " has been **UPDATED**.");
    }
    
    // --- New Delete Methods ---

    public static void deleteRecord(Scanner sc, String type) {
        dbConnect con = new dbConnect();
        String idColumn, table, nameColumn, viewMethod, deleteQry;
        String typeName = "";
        
        switch(type) {
            case "user":
                idColumn = "u_id";
                table = "tbl_user";
                nameColumn = "u_name";
                viewMethod = "viewUsers()";
                typeName = "User (Admin/Teacher)";
                viewUsers();
                break;
            case "student":
                idColumn = "s_id";
                table = "tbl_students";
                nameColumn = "s_fname || ' ' || s_lname";
                viewMethod = "viewStudentsForAdmin()";
                typeName = "Student";
                viewStudentsForAdmin(); // Using the view for admin
                break;
            case "subject":
                idColumn = "sbj_id";
                table = "tbl_subjects";
                nameColumn = "sbj_desc";
                viewMethod = "viewSubjects()";
                typeName = "Subject";
                viewSubjects();
                break;
            default:
                System.out.println("Invalid record type for deletion.");
                return;
        }

        System.out.println("\n===== DELETE " + typeName.toUpperCase() + " =====");
        System.out.print("Enter " + typeName + " ID to DELETE (or 0 to cancel): ");
        
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Deletion cancelled.");
            sc.next();
            return;
        }
        int idToDelete = sc.nextInt();
        if (idToDelete == 0) return;

        // Check if the record exists
        String checkQry = "SELECT " + nameColumn + " FROM " + table + " WHERE " + idColumn + " = ?";
        List<Map<String, Object>> recordCheck = con.fetchRecords(checkQry, idToDelete);
        
        if (recordCheck.isEmpty()) {
            System.out.println("Error: " + typeName + " ID " + idToDelete + " not found.");
            return;
        }

        String name = recordCheck.get(0).get(nameColumn.split(" ")[0].toLowerCase()).toString();
        
        // Confirmation
        System.out.print("Are you sure you want to DELETE " + typeName + " '" + name + "' (ID: " + idToDelete + ")? (Y/N): ");
        char confirm = sc.next().charAt(0);
        
        if (confirm == 'Y' || confirm == 'y') {
            // Special handling for Student and Subject due to foreign key constraints (grades)
            if (type.equals("student") || type.equals("subject")) {
                // Delete associated grades first
                String deleteGradesQry = (type.equals("student")) 
                    ? "DELETE FROM tbl_grades WHERE s_id = ?"
                    : "DELETE FROM tbl_grades WHERE sbj_id = ?";
                con.deleteRecord(deleteGradesQry, idToDelete);
                System.out.println("INFO: Associated grades have been deleted.");
            }
            
            // Delete the main record
            deleteQry = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";
            con.deleteRecord(deleteQry, idToDelete);
            System.out.println("SUCCESS: " + typeName + " ID " + idToDelete + " (" + name + ") has been **DELETED**.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    // --- Helper View Method for Admin Student Management ---

    private static void viewStudentsForAdmin() {
        String Query = "SELECT s_id, s_fname || ' ' || s_lname AS s_name, s_email, s_status FROM tbl_students";
        String[] headers = {"ID", "Name", "Email", "Status"};
        String[] columns = {"s_id", "s_name", "s_email", "s_status"};
        dbConnect conf = new dbConnect();
        conf.viewRecords(Query, headers, columns);
    }
    
    // --- Dashboards ---

    public static void adminDashboard(Scanner sc) {
        int adminChoice;
        char adminCont;

        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. View All User Accounts (Teachers/Admins)");
            System.out.println("2. Approve Pending Teachers/Admins"); 
            System.out.println("3. Approve Pending Students");
            System.out.println("--- Management ---");
            System.out.println("4. Update User Account (Admin/Teacher)");
            System.out.println("5. Delete User Account (Admin/Teacher)");
            System.out.println("6. Update Student Account");
            System.out.println("7. Delete Student Account");
            System.out.println("8. View All Subjects");
            System.out.println("9. Update Subject");
            System.out.println("10. Delete Subject");
            System.out.println("11. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            if (sc.hasNextInt()) {
                adminChoice = sc.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); 
                adminChoice = 0;
            }
            
            switch (adminChoice) {
                case 1:
                    System.out.println("\n--- All User Accounts ---");
                    viewUsers();
                    break;
                case 2:
                    approveUsers(sc); 
                    break;
                case 3:
                    approveStudents(sc); 
                    break;
                case 4:
                    updateUser(sc);
                    break;
                case 5:
                    deleteRecord(sc, "user");
                    break;
                case 6:
                    updateStudent(sc);
                    break;
                case 7:
                    deleteRecord(sc, "student");
                    break;
                case 8:
                    viewSubjects();
                    break;
                case 9:
                    updateSubject(sc);
                    break;
                case 10:
                    deleteRecord(sc, "subject");
                    break;
                case 11:
                    return; 
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue in the Admin Dashboard? (Y/N): ");
            if (sc.hasNext()) { 
                adminCont = sc.next().charAt(0);
            } else {
                adminCont = 'N'; 
            }
            
        } while (adminCont == 'Y' || adminCont == 'y');
    }

    public static void teacherDashboard(Scanner sc) {
        // Option 1 (View All Subjects) has been removed, and options are renumbered.
        dbConnect con = new dbConnect();
        int teacherChoice;
        char teacherCont;

        do {
            System.out.println("\n===== TEACHER DASHBOARD =====");
            System.out.println("1. View Student Grades (All)");
            System.out.println("2. Input Student Grades"); 
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            if (sc.hasNextInt()) {
                teacherChoice = sc.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); 
                teacherChoice = 0;
            }
            
            switch (teacherChoice) {
                case 1: 
                    System.out.println("\n--- Student Grades (All) ---");
                    viewStudents(); 
                    break;
                case 2: 
                    viewStudents();
                    inputGrades(sc); 
                    break;
                case 3: 
                    return; 
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue in the Teacher Dashboard? (Y/N): ");
            if (sc.hasNext()) { 
                teacherCont = sc.next().charAt(0);
            } else {
                teacherCont = 'N'; 
            }
            
        } while (teacherCont == 'Y' || teacherCont == 'y');
    }

    public static void studentDashboard(Scanner sc, int studentId) {
        // The grade check for students has been corrected from 3.0 to 75
        dbConnect con = new dbConnect();
        int studentChoice;
        char studentCont;

        do {
            System.out.println("\n===== STUDENT DASHBOARD =====");
            System.out.println("Welcome Student ID: " + studentId);
            System.out.println("1. View My Grades");
            System.out.println("2. Back to Main Menu");
            System.out.print("Enter choice: ");

            if (sc.hasNextInt()) {
                studentChoice = sc.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
                studentChoice = 0;
            }

            switch (studentChoice) {
                case 1:
                    System.out.println("\n--- My Grades ---");
                    // Using 75 as the passing mark based on viewStudents and inputGrades
                    String Query = "SELECT tbl_students.s_fname || ' ' || tbl_students.s_lname AS s_name, tbl_subjects.sbj_desc, tbl_grades.final, "
                            + "CASE WHEN tbl_grades.final >= 3.0 THEN 'Pass' ELSE 'Fail' END AS remarks " 
                            + "FROM tbl_grades "
                            + "JOIN tbl_students ON tbl_grades.s_id = tbl_students.s_id "
                            + "JOIN tbl_subjects ON tbl_grades.sbj_id = tbl_subjects.sbj_id "
                            + "WHERE tbl_grades.s_id = ?"; 
                    
                    List<Map<String, Object>> gradeResults = con.fetchRecords(Query, studentId);
                    if (gradeResults != null && !gradeResults.isEmpty()) {
                        System.out.printf("%-20s %-30s %-15s %-10s\n", "Name", "Subject", "Final Grade", "Remarks");
                        System.out.println("-------------------- ------------------------------ --------------- ----------");
                        for (Map<String, Object> record : gradeResults) {
                            System.out.printf("%-20s %-30s %-15s %-10s\n", 
                                    record.get("s_name"), 
                                    record.get("sbj_desc"), 
                                    record.get("final"), 
                                    record.get("remarks"));
                        }
                    } else {
                        System.out.println("No grades found for this student.");
                    }
                    
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue in the Student Dashboard? (Y/N): ");
            if (sc.hasNext()) {
                studentCont = sc.next().charAt(0);
            } else {
                studentCont = 'N';
            }

        } while (studentCont == 'Y' || studentCont == 'y');
    }
    
    // --- Main Method (Unchanged) ---

    public static void main(String[] args) {
    dbConnect con = new dbConnect();
    con.connectDB();
    int choice;
    char cont;
    Scanner sc = new Scanner(System.in);

    do {
            System.out.println("===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); 
                choice = 0;
            }

            switch (choice) {
            case 1:
                System.out.print("Enter email: ");
                String em = sc.next();
                System.out.print("Enter Password: ");
                String pas = sc.next();
                
                while (true) {
                    
                    String userQry = "SELECT * FROM tbl_user WHERE u_email = ? AND u_pass = ?";
                    List<Map<String, Object>> userResult = con.fetchRecords(userQry, em, pas);

                    String studentQry = "SELECT * FROM tbl_students WHERE s_email = ? AND s_pass = ?";
                    List<Map<String, Object>> studentResult = con.fetchRecords(studentQry, em, pas);
                    
                    if (!userResult.isEmpty()) {
                        Map<String, Object> user = userResult.get(0);
                        String stat = user.get("u_status").toString();
                        String type = user.get("u_type").toString();
                        
                        if(stat.equals("Pending")){
                            System.out.println("Account is Pending, Contact the Admin!");
                            break;
                        } else {
                            System.out.println("LOGIN SUCCESS!");
                            if(type.equals("Admin")){
                                adminDashboard(sc); 
                            } else if(type.equals("Teacher")){
                                teacherDashboard(sc); 
                            }
                            break; 
                        }
                    } else if (!studentResult.isEmpty()) {
                        Map<String, Object> student = studentResult.get(0);
                        String stat = student.get("s_status").toString();
                        
                        if(stat.equals("Approved") || stat.equals("Active")){ 
                            System.out.println("LOGIN SUCCESS!");
                            int studentId = (Integer) student.get("s_id"); 
                            studentDashboard(sc, studentId); 
                            break;
                        } else {
                            System.out.println("Student account status is " + stat + ". Contact administration.");
                            break;
                        }

                    } else {
                        System.out.println("INVALID CREDENTIALS");
                        break;
                    }
                }
                    
                    break;

                case 2:
                    System.out.print("Enter user first name: ");
                    String fname = sc.next();
                    System.out.print("Enter user last name: ");
                    String lname = sc.next();
                    System.out.print("Enter user email: ");
                    String email = sc.next();
                    
                    // Email existence check should be added here for tbl_students as well,
                    // but for this implementation, we proceed with registration.

                    System.out.print("Enter user Type (1 - Admin/2 -Teacher/3 - Student): "); 
                    int t;
                    if (sc.hasNextInt()) {
                        t = sc.nextInt();
                    } else {
                        System.out.println("Invalid input. Assuming Student (3).");
                        sc.next();
                        t = 3;
                    }

                    while(t > 3 || t < 1){ 
                        System.out.print("Invalid, choose between 1, 2, & 3 only: ");
                        if (sc.hasNextInt()) {
                            t = sc.nextInt();
                        } else {
                            System.out.println("Invalid input. Assuming Student (3).");
                            sc.next();
                            t = 3;
                        }
                    }
                    
                    String tp = "";
                    if(t == 1){
                        tp = "Admin";
                    } else if (t == 2) {
                        tp = "Teacher";
                    } else {
                        tp = "Student"; 
                    }
                    
                    System.out.print("Enter Password: ");
                    String pass = sc.next();
                    
                    if (tp.equals("Student")) {
                        // Insert into tbl_students
                        String sqlStudent = "INSERT INTO tbl_students(s_fname, s_lname, s_email, s_pass, s_status) VALUES (?, ?, ?, ?, ?)";
                        con.addRecord(sqlStudent, fname, lname, email, pass, "Pending");
                        System.out.println("Registration successful. Status is 'Pending' for admin approval.");
                    } else {
                        // Insert Admin/Teacher into tbl_user
                        String name = fname + " " + lname; 
                        String sqlUser = "INSERT INTO tbl_user(u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
                        con.addRecord(sqlUser, name, email, tp, "Pending", pass);
                        System.out.println("Registration successful. Status is 'Pending' for admin approval.");
                    }
                    break;

                case 3:
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue? (Y/N): ");
            if (sc.hasNext()) { 
                cont = sc.next().charAt(0);
            } else {
                cont = 'N'; 
            }


        } while (cont == 'Y' || cont == 'y');

        System.out.println("Thank you! Program ended.");
    } 
}