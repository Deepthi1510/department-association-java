-- Optional data seed for testing
-- Use this script to populate sample data for local testing

INSERT INTO association (assoc_name, establishment_year, department_id, description)
VALUES ('Computer Science Club', 2020, 1, 'Club for CS students to explore programming and technology');

INSERT INTO faculty (f_name, f_email, f_phone, designation)
VALUES ('Dr. John Smith', 'john.smith@college.edu', '555-0101', 'Associate Professor');

INSERT INTO student (s_name, s_email, phone)
VALUES ('Alice Johnson', 'alice@college.edu', '555-1001');

INSERT INTO event (assoc_id, event_name, event_date, venue, description, participant_count)
VALUES (1, 'Annual Coding Contest', '2025-03-15', 'Computer Lab A', 'Annual programming competition', 50);

INSERT INTO activity (event_id, activity_name, description, start_time, end_time, participant_count)
VALUES (1, 'Preliminary Round', 'First round of the contest', '09:00:00', '11:30:00', 50);

INSERT INTO association_members (assoc_id, student_id, role, joined_date)
VALUES (1, 1, 'President', '2025-01-01');

INSERT INTO association_faculty_advisers (assoc_id, faculty_id, role)
VALUES (1, 1, 'Faculty Advisor');

INSERT INTO activity_participants (activity_id, student_id, registered_on)
VALUES (1, 1, NOW());

INSERT INTO activity_winners (activity_id, student_id, position)
VALUES (1, 1, 1);
