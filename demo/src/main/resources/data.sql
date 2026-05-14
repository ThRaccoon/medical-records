--1. USERS
INSERT INTO APP_USERS (username, password, role) VALUES ('admin',    '$2a$10$UAa6YaUo31xUrGQwY5KuLOLMo/mvCj30LY0YWRHKAGAviw1MA8bUm', 'ADMIN');
INSERT INTO APP_USERS (username, password, role) VALUES ('doctor1',  '$2a$10$UAa6YaUo31xUrGQwY5KuLOLMo/mvCj30LY0YWRHKAGAviw1MA8bUm', 'DOCTOR');
INSERT INTO APP_USERS (username, password, role) VALUES ('doctor2',  '$2a$10$UAa6YaUo31xUrGQwY5KuLOLMo/mvCj30LY0YWRHKAGAviw1MA8bUm', 'DOCTOR');
INSERT INTO APP_USERS (username, password, role) VALUES ('patient1', '$2a$10$UAa6YaUo31xUrGQwY5KuLOLMo/mvCj30LY0YWRHKAGAviw1MA8bUm', 'PATIENT');
INSERT INTO APP_USERS (username, password, role) VALUES ('patient2', '$2a$10$UAa6YaUo31xUrGQwY5KuLOLMo/mvCj30LY0YWRHKAGAviw1MA8bUm', 'PATIENT');

-- 2. DOCTORS
INSERT INTO DOCTORS (name, uin, speciality, gp, app_user_id) VALUES ('Dr Ivanov', '1234567890', 'GP', true, 2);
INSERT INTO DOCTORS (name, uin, speciality, gp, app_user_id) VALUES ('Dr Petrova', '0987654321', 'Cardiologist', false, 3);

-- 3. DIAGNOSES
INSERT INTO DIAGNOSES (name, description) VALUES ('Flu', 'Seasonal flu');
INSERT INTO DIAGNOSES (name, description) VALUES ('Hypertension', 'High blood pressure');
INSERT INTO DIAGNOSES (name, description) VALUES ('Diabetes', 'Type 2 diabetes');

-- 4. PATIENTS
INSERT INTO PATIENTS (name, egn, gp_doctor_id, insured, app_user_id) VALUES ('Ivan Ivanov', '1234567890', 1, true, 4);
INSERT INTO PATIENTS (name, egn, gp_doctor_id, insured, app_user_id) VALUES ('Maria Petrova', '0987654321', 1, false, 5);

-- 5. VISITS
INSERT INTO VISITS (date, doctor_id, patient_id, diagnosis_id, treatment, price, paid_by_nzok) VALUES ('2024-01-15', 1, 1, 1, 'Rest and fluids', 50.00, true);
INSERT INTO VISITS (date, doctor_id, patient_id, diagnosis_id, treatment, price, paid_by_nzok) VALUES ('2024-02-20', 2, 2, 2, 'Blood pressure medication', 80.00, false);
INSERT INTO VISITS (date, doctor_id, patient_id, diagnosis_id, treatment, price, paid_by_nzok) VALUES ('2024-03-10', 1, 2, 3, 'Insulin therapy', 100.00, false);

-- 6. SICK LEAVES
INSERT INTO SICK_LEAVES (start_date, number_of_days, visit_id) VALUES ('2024-01-15', 5, 1);
INSERT INTO SICK_LEAVES (start_date, number_of_days, visit_id) VALUES ('2024-02-20', 3, 2);
