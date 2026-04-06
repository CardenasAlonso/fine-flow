// =============================================================================
// FINE FLOW v4.0 - Demo Data
// Based on FINEFLOW_MASTER_v4.0 Oracle SQL Schema
// =============================================================================

import type {
  School,
  User,
  AcademicLevel,
  SchoolYear,
  AcademicPeriod,
  Section,
  Student,
  Teacher,
  Guardian,
  Course,
  CourseAssignment,
  CourseCompetency,
  ClassTask,
  Attendance,
  StudentScore,
  Classroom,
  TimeSlot,
  ScheduleVersion,
  ClassSchedule,
  Notification,
  Justification,
  ReportJob,
  BlockchainBlock,
  SchoolSubscription,
  FeatureFlag,
  SystemConfig,
} from './types';

// -----------------------------------------------------------------------------
// SCHOOL
// -----------------------------------------------------------------------------

export const demoSchool: School = {
  id: 'school-20188-canete',
  name: 'I.E. Centro de Varones N°20188',
  document_number: '20543210001',
  institution_type: 'K12',
  address: 'Calle Bolivar 150, Canete, Lima',
  phone: '01-5840123',
  email: 'ie20188.canete@gmail.com',
  logo_url: '/placeholder.svg?height=64&width=64',
  status: 'ACTIVE',
  created_at: new Date('2025-01-01'),
  updated_at: new Date(),
};

// -----------------------------------------------------------------------------
// USERS
// -----------------------------------------------------------------------------

export const demoUsers: User[] = [
  {
    id: 'user-admin-20188',
    school_id: 'school-20188-canete',
    email: 'admin@ie20188.edu.pe',
    role: 'ADMIN',
    first_name: 'Administrador',
    last_name: 'Principal',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-01'),
    updated_at: new Date(),
  },
  {
    id: 'user-coord-20188',
    school_id: 'school-20188-canete',
    email: 'coordinador@ie20188.edu.pe',
    role: 'COORDINATOR',
    first_name: 'Maria',
    last_name: 'Garcia',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  {
    id: 'user-teacher-20188',
    school_id: 'school-20188-canete',
    email: 'jperez@ie20188.edu.pe',
    role: 'TEACHER',
    first_name: 'Juan',
    last_name: 'Perez',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  {
    id: 'user-teacher-002',
    school_id: 'school-20188-canete',
    email: 'alopez@ie20188.edu.pe',
    role: 'TEACHER',
    first_name: 'Ana',
    last_name: 'Lopez',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  {
    id: 'user-student-20188',
    school_id: 'school-20188-canete',
    email: 'diego.vargas@ie20188.edu.pe',
    role: 'STUDENT',
    first_name: 'Diego',
    last_name: 'Vargas',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-03-01'),
    updated_at: new Date(),
  },
  {
    id: 'user-guardian-20188',
    school_id: 'school-20188-canete',
    email: 'roberto.vargas@gmail.com',
    role: 'GUARDIAN',
    first_name: 'Roberto',
    last_name: 'Vargas',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-03-01'),
    updated_at: new Date(),
  },
];

// -----------------------------------------------------------------------------
// ACADEMIC STRUCTURE
// -----------------------------------------------------------------------------

export const demoAcademicLevels: AcademicLevel[] = [
  { id: 'level-pri-20188', school_id: 'school-20188-canete', name: 'Educacion Primaria', order_num: 1, is_active: true },
  { id: 'level-sec-20188', school_id: 'school-20188-canete', name: 'Educacion Secundaria', order_num: 2, is_active: true },
];

export const demoSchoolYears: SchoolYear[] = [
  { id: 'sy-1sec-2025', school_id: 'school-20188-canete', academic_level_id: 'level-sec-20188', name: '1° Secundaria 2025', grade_number: 1, calendar_year: 2025, is_active: true },
  { id: 'sy-2sec-2025', school_id: 'school-20188-canete', academic_level_id: 'level-sec-20188', name: '2° Secundaria 2025', grade_number: 2, calendar_year: 2025, is_active: true },
  { id: 'sy-3sec-2025', school_id: 'school-20188-canete', academic_level_id: 'level-sec-20188', name: '3° Secundaria 2025', grade_number: 3, calendar_year: 2025, is_active: true },
  { id: 'sy-4sec-2025', school_id: 'school-20188-canete', academic_level_id: 'level-sec-20188', name: '4° Secundaria 2025', grade_number: 4, calendar_year: 2025, is_active: true },
  { id: 'sy-5sec-2025', school_id: 'school-20188-canete', academic_level_id: 'level-sec-20188', name: '5° Secundaria 2025', grade_number: 5, calendar_year: 2025, is_active: true },
];

export const demoAcademicPeriods: AcademicPeriod[] = [
  { id: 'ap-b1-3sec-2025', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: '1° Bimestre', period_type: 'BIMESTER', start_date: new Date('2025-03-10'), end_date: new Date('2025-05-09'), is_active: true, created_at: new Date('2025-01-01') },
  { id: 'ap-b2-3sec-2025', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: '2° Bimestre', period_type: 'BIMESTER', start_date: new Date('2025-05-12'), end_date: new Date('2025-07-11'), is_active: false, created_at: new Date('2025-01-01') },
  { id: 'ap-b3-3sec-2025', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: '3° Bimestre', period_type: 'BIMESTER', start_date: new Date('2025-08-04'), end_date: new Date('2025-10-03'), is_active: false, created_at: new Date('2025-01-01') },
  { id: 'ap-b4-3sec-2025', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: '4° Bimestre', period_type: 'BIMESTER', start_date: new Date('2025-10-06'), end_date: new Date('2025-12-19'), is_active: false, created_at: new Date('2025-01-01') },
];

export const demoSections: Section[] = [
  { id: 'sec-3a-2025', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: 'A', max_capacity: 35, tutor_id: 'teacher-001', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'sec-3b-2025', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: 'B', max_capacity: 35, tutor_id: 'teacher-002', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'sec-4a-2025', school_id: 'school-20188-canete', school_year_id: 'sy-4sec-2025', name: 'A', max_capacity: 35, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'sec-5a-2025', school_id: 'school-20188-canete', school_year_id: 'sy-5sec-2025', name: 'A', max_capacity: 35, is_active: true, created_at: new Date('2025-01-01') },
];

// -----------------------------------------------------------------------------
// TEACHERS
// -----------------------------------------------------------------------------

export const demoTeachers: Teacher[] = [
  { id: 'teacher-001', school_id: 'school-20188-canete', user_id: 'user-teacher-20188', first_name: 'Juan', last_name: 'Perez', document_number: '12345678', specialty: 'Matematica, Fisica', phone: '987654321', status: 'ACTIVE', hired_at: new Date('2020-03-01'), created_at: new Date('2020-03-01'), updated_at: new Date() },
  { id: 'teacher-002', school_id: 'school-20188-canete', user_id: 'user-teacher-002', first_name: 'Ana', last_name: 'Lopez', document_number: '23456789', specialty: 'Comunicacion, Literatura', phone: '987654322', status: 'ACTIVE', hired_at: new Date('2019-03-01'), created_at: new Date('2019-03-01'), updated_at: new Date() },
  { id: 'teacher-003', school_id: 'school-20188-canete', first_name: 'Carlos', last_name: 'Rodriguez', document_number: '34567890', specialty: 'Historia, Geografia', phone: '987654323', status: 'ACTIVE', hired_at: new Date('2021-03-01'), created_at: new Date('2021-03-01'), updated_at: new Date() },
  { id: 'teacher-004', school_id: 'school-20188-canete', first_name: 'Maria', last_name: 'Santos', document_number: '45678901', specialty: 'Ciencia y Tecnologia', phone: '987654324', status: 'ACTIVE', hired_at: new Date('2018-03-01'), created_at: new Date('2018-03-01'), updated_at: new Date() },
  { id: 'teacher-005', school_id: 'school-20188-canete', first_name: 'Pedro', last_name: 'Martinez', document_number: '56789012', specialty: 'Ingles', phone: '987654325', status: 'ON_LEAVE', hired_at: new Date('2022-03-01'), created_at: new Date('2022-03-01'), updated_at: new Date() },
];

// -----------------------------------------------------------------------------
// STUDENTS
// -----------------------------------------------------------------------------

export const demoStudents: Student[] = [
  { id: 'student-001', school_id: 'school-20188-canete', section_id: 'sec-3a-2025', user_id: 'user-student-20188', first_name: 'Diego', last_name: 'Vargas', document_type: 'DNI', document_number: '71234567', birth_date: new Date('2010-05-15'), blood_type: 'O+', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-002', school_id: 'school-20188-canete', section_id: 'sec-3a-2025', first_name: 'Maria', last_name: 'Fernandez', document_type: 'DNI', document_number: '71234568', birth_date: new Date('2010-08-20'), blood_type: 'A+', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-003', school_id: 'school-20188-canete', section_id: 'sec-3a-2025', first_name: 'Carlos', last_name: 'Mendoza', document_type: 'DNI', document_number: '71234569', birth_date: new Date('2010-03-10'), blood_type: 'B+', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-004', school_id: 'school-20188-canete', section_id: 'sec-3a-2025', first_name: 'Ana', last_name: 'Quispe', document_type: 'DNI', document_number: '71234570', birth_date: new Date('2010-11-25'), blood_type: 'AB+', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-005', school_id: 'school-20188-canete', section_id: 'sec-3a-2025', first_name: 'Jose', last_name: 'Huaman', document_type: 'DNI', document_number: '71234571', birth_date: new Date('2010-07-05'), blood_type: 'O-', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-006', school_id: 'school-20188-canete', section_id: 'sec-3b-2025', first_name: 'Rosa', last_name: 'Castillo', document_type: 'DNI', document_number: '71234572', birth_date: new Date('2010-01-18'), blood_type: 'A-', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-007', school_id: 'school-20188-canete', section_id: 'sec-3b-2025', first_name: 'Pedro', last_name: 'Sanchez', document_type: 'DNI', document_number: '71234573', birth_date: new Date('2010-09-30'), blood_type: 'B-', status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
  { id: 'student-008', school_id: 'school-20188-canete', section_id: 'sec-3b-2025', first_name: 'Luis', last_name: 'Ramos', document_type: 'DNI', document_number: '71234574', birth_date: new Date('2010-04-12'), status: 'ACTIVE', created_at: new Date('2025-03-01'), updated_at: new Date() },
];

// -----------------------------------------------------------------------------
// GUARDIANS
// -----------------------------------------------------------------------------

export const demoGuardians: Guardian[] = [
  { id: 'guardian-001', school_id: 'school-20188-canete', user_id: 'user-guardian-20188', student_id: 'student-001', first_name: 'Roberto', last_name: 'Vargas', document_number: '41234567', relationship: 'FATHER', phone: '987111222', email: 'roberto.vargas@gmail.com', is_primary_contact: true, created_at: new Date('2025-03-01') },
  { id: 'guardian-002', school_id: 'school-20188-canete', student_id: 'student-001', first_name: 'Carmen', last_name: 'Lopez', document_number: '41234568', relationship: 'MOTHER', phone: '987111223', email: 'carmen.lopez@gmail.com', is_primary_contact: false, created_at: new Date('2025-03-01') },
  { id: 'guardian-003', school_id: 'school-20188-canete', student_id: 'student-002', first_name: 'Jorge', last_name: 'Fernandez', document_number: '41234569', relationship: 'FATHER', phone: '987111224', email: 'jorge.fernandez@gmail.com', is_primary_contact: true, created_at: new Date('2025-03-01') },
];

// -----------------------------------------------------------------------------
// COURSES (CNEB)
// -----------------------------------------------------------------------------

export const demoCourses: Course[] = [
  { id: 'course-mat-20188', school_id: 'school-20188-canete', name: 'Matematica', code: 'MAT', color_hex: '#1e407a', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'course-com-20188', school_id: 'school-20188-canete', name: 'Comunicacion', code: 'COM', color_hex: '#16a34a', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'course-his-20188', school_id: 'school-20188-canete', name: 'Historia, Geografia y Economia', code: 'HGE', color_hex: '#c8922a', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'course-cie-20188', school_id: 'school-20188-canete', name: 'Ciencia y Tecnologia', code: 'CYT', color_hex: '#0369a1', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'course-ing-20188', school_id: 'school-20188-canete', name: 'Ingles', code: 'ING', color_hex: '#7c3aed', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'course-edf-20188', school_id: 'school-20188-canete', name: 'Educacion Fisica', code: 'EDF', color_hex: '#dc2626', is_active: true, created_at: new Date('2025-01-01') },
  { id: 'course-art-20188', school_id: 'school-20188-canete', name: 'Arte y Cultura', code: 'ART', color_hex: '#ec4899', is_active: true, created_at: new Date('2025-01-01') },
];

// -----------------------------------------------------------------------------
// COURSE COMPETENCIES (CNEB/MINEDU)
// -----------------------------------------------------------------------------

export const demoCourseCompetencies: CourseCompetency[] = [
  // Matematica
  { id: 'comp-mat-1', school_id: 'school-20188-canete', course_id: 'course-mat-20188', name: 'Resuelve problemas de cantidad', weight: 25, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-mat-2', school_id: 'school-20188-canete', course_id: 'course-mat-20188', name: 'Resuelve problemas de regularidad, equivalencia y cambio', weight: 25, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-mat-3', school_id: 'school-20188-canete', course_id: 'course-mat-20188', name: 'Resuelve problemas de forma, movimiento y localizacion', weight: 25, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-mat-4', school_id: 'school-20188-canete', course_id: 'course-mat-20188', name: 'Resuelve problemas de gestion de datos e incertidumbre', weight: 25, is_active: true, created_at: new Date('2025-01-01') },
  // Comunicacion
  { id: 'comp-com-1', school_id: 'school-20188-canete', course_id: 'course-com-20188', name: 'Se comunica oralmente en su lengua materna', weight: 34, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-com-2', school_id: 'school-20188-canete', course_id: 'course-com-20188', name: 'Lee diversos tipos de textos escritos', weight: 33, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-com-3', school_id: 'school-20188-canete', course_id: 'course-com-20188', name: 'Escribe diversos tipos de textos', weight: 33, is_active: true, created_at: new Date('2025-01-01') },
  // Ciencia y Tecnologia
  { id: 'comp-cie-1', school_id: 'school-20188-canete', course_id: 'course-cie-20188', name: 'Indaga mediante metodos cientificos', weight: 34, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-cie-2', school_id: 'school-20188-canete', course_id: 'course-cie-20188', name: 'Explica el mundo fisico basandose en conocimientos cientificos', weight: 33, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'comp-cie-3', school_id: 'school-20188-canete', course_id: 'course-cie-20188', name: 'Disena y construye soluciones tecnologicas', weight: 33, is_active: true, created_at: new Date('2025-01-01') },
];

// -----------------------------------------------------------------------------
// COURSE ASSIGNMENTS
// -----------------------------------------------------------------------------

export const demoCourseAssignments: CourseAssignment[] = [
  { id: 'ca-mat-3a-b1', school_id: 'school-20188-canete', course_id: 'course-mat-20188', section_id: 'sec-3a-2025', teacher_id: 'teacher-001', academic_period_id: 'ap-b1-3sec-2025', hours_per_week: 6, is_active: true, created_at: new Date('2025-03-01') },
  { id: 'ca-com-3a-b1', school_id: 'school-20188-canete', course_id: 'course-com-20188', section_id: 'sec-3a-2025', teacher_id: 'teacher-002', academic_period_id: 'ap-b1-3sec-2025', hours_per_week: 5, is_active: true, created_at: new Date('2025-03-01') },
  { id: 'ca-his-3a-b1', school_id: 'school-20188-canete', course_id: 'course-his-20188', section_id: 'sec-3a-2025', teacher_id: 'teacher-003', academic_period_id: 'ap-b1-3sec-2025', hours_per_week: 4, is_active: true, created_at: new Date('2025-03-01') },
  { id: 'ca-cie-3a-b1', school_id: 'school-20188-canete', course_id: 'course-cie-20188', section_id: 'sec-3a-2025', teacher_id: 'teacher-004', academic_period_id: 'ap-b1-3sec-2025', hours_per_week: 5, is_active: true, created_at: new Date('2025-03-01') },
  { id: 'ca-ing-3a-b1', school_id: 'school-20188-canete', course_id: 'course-ing-20188', section_id: 'sec-3a-2025', teacher_id: 'teacher-005', academic_period_id: 'ap-b1-3sec-2025', hours_per_week: 3, is_active: true, created_at: new Date('2025-03-01') },
];

// -----------------------------------------------------------------------------
// CLASS TASKS
// -----------------------------------------------------------------------------

export const demoClassTasks: ClassTask[] = [
  { id: 'task-001', school_id: 'school-20188-canete', course_assignment_id: 'ca-mat-3a-b1', competency_id: 'comp-mat-1', academic_period_id: 'ap-b1-3sec-2025', title: 'Practica Calificada 1 - Numeros Racionales', task_type: 'QUIZ', max_score: 20, due_date: new Date('2025-03-20'), is_active: true, created_at: new Date('2025-03-10') },
  { id: 'task-002', school_id: 'school-20188-canete', course_assignment_id: 'ca-mat-3a-b1', competency_id: 'comp-mat-2', academic_period_id: 'ap-b1-3sec-2025', title: 'Trabajo Grupal - Ecuaciones Lineales', task_type: 'PROJECT', max_score: 20, due_date: new Date('2025-03-28'), is_active: true, created_at: new Date('2025-03-10') },
  { id: 'task-003', school_id: 'school-20188-canete', course_assignment_id: 'ca-mat-3a-b1', competency_id: 'comp-mat-3', academic_period_id: 'ap-b1-3sec-2025', title: 'Examen Bimestral - Geometria', task_type: 'EXAM', max_score: 20, due_date: new Date('2025-05-05'), is_active: true, created_at: new Date('2025-03-10') },
  { id: 'task-004', school_id: 'school-20188-canete', course_assignment_id: 'ca-com-3a-b1', competency_id: 'comp-com-1', academic_period_id: 'ap-b1-3sec-2025', title: 'Exposicion Oral - Texto Argumentativo', task_type: 'ORAL', max_score: 20, due_date: new Date('2025-03-25'), is_active: true, created_at: new Date('2025-03-10') },
  { id: 'task-005', school_id: 'school-20188-canete', course_assignment_id: 'ca-com-3a-b1', competency_id: 'comp-com-2', academic_period_id: 'ap-b1-3sec-2025', title: 'Control de Lectura - El Principito', task_type: 'QUIZ', max_score: 20, due_date: new Date('2025-04-05'), is_active: true, created_at: new Date('2025-03-10') },
];

// -----------------------------------------------------------------------------
// STUDENT SCORES
// -----------------------------------------------------------------------------

export const demoStudentScores: StudentScore[] = [
  { id: 'score-001', school_id: 'school-20188-canete', student_id: 'student-001', class_task_id: 'task-001', score: 18, comments: 'Excelente trabajo', registered_by: 'user-teacher-20188', registered_at: new Date('2025-03-21'), updated_at: new Date('2025-03-21') },
  { id: 'score-002', school_id: 'school-20188-canete', student_id: 'student-002', class_task_id: 'task-001', score: 15, registered_by: 'user-teacher-20188', registered_at: new Date('2025-03-21'), updated_at: new Date('2025-03-21') },
  { id: 'score-003', school_id: 'school-20188-canete', student_id: 'student-003', class_task_id: 'task-001', score: 12, comments: 'Debe repasar fracciones', registered_by: 'user-teacher-20188', registered_at: new Date('2025-03-21'), updated_at: new Date('2025-03-21') },
  { id: 'score-004', school_id: 'school-20188-canete', student_id: 'student-004', class_task_id: 'task-001', score: 17, registered_by: 'user-teacher-20188', registered_at: new Date('2025-03-21'), updated_at: new Date('2025-03-21') },
  { id: 'score-005', school_id: 'school-20188-canete', student_id: 'student-005', class_task_id: 'task-001', score: 8, comments: 'Necesita apoyo adicional', registered_by: 'user-teacher-20188', registered_at: new Date('2025-03-21'), updated_at: new Date('2025-03-21') },
  { id: 'score-006', school_id: 'school-20188-canete', student_id: 'student-001', class_task_id: 'task-004', score: 19, comments: 'Excelente presentacion', registered_by: 'user-teacher-002', registered_at: new Date('2025-03-26'), updated_at: new Date('2025-03-26') },
  { id: 'score-007', school_id: 'school-20188-canete', student_id: 'student-002', class_task_id: 'task-004', score: 16, registered_by: 'user-teacher-002', registered_at: new Date('2025-03-26'), updated_at: new Date('2025-03-26') },
];

// -----------------------------------------------------------------------------
// ATTENDANCE
// -----------------------------------------------------------------------------

export const demoAttendances: Attendance[] = [
  // Day 1
  { id: 'att-001', school_id: 'school-20188-canete', student_id: 'student-001', attendance_date: new Date('2025-03-10'), status: 'PRESENT', check_in_time: '07:28', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-10') },
  { id: 'att-002', school_id: 'school-20188-canete', student_id: 'student-002', attendance_date: new Date('2025-03-10'), status: 'PRESENT', check_in_time: '07:25', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-10') },
  { id: 'att-003', school_id: 'school-20188-canete', student_id: 'student-003', attendance_date: new Date('2025-03-10'), status: 'LATE', check_in_time: '07:42', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-10') },
  { id: 'att-004', school_id: 'school-20188-canete', student_id: 'student-004', attendance_date: new Date('2025-03-10'), status: 'PRESENT', check_in_time: '07:30', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-10') },
  { id: 'att-005', school_id: 'school-20188-canete', student_id: 'student-005', attendance_date: new Date('2025-03-10'), status: 'ABSENT', record_method: 'MANUAL', registered_by: 'user-teacher-20188', created_at: new Date('2025-03-10') },
  // Day 2
  { id: 'att-006', school_id: 'school-20188-canete', student_id: 'student-001', attendance_date: new Date('2025-03-11'), status: 'PRESENT', check_in_time: '07:20', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-11') },
  { id: 'att-007', school_id: 'school-20188-canete', student_id: 'student-002', attendance_date: new Date('2025-03-11'), status: 'PRESENT', check_in_time: '07:22', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-11') },
  { id: 'att-008', school_id: 'school-20188-canete', student_id: 'student-003', attendance_date: new Date('2025-03-11'), status: 'PRESENT', check_in_time: '07:29', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-11') },
  { id: 'att-009', school_id: 'school-20188-canete', student_id: 'student-004', attendance_date: new Date('2025-03-11'), status: 'EXCUSED', record_method: 'MANUAL', justification_reason: 'Cita medica', registered_by: 'user-coord-20188', created_at: new Date('2025-03-11') },
  { id: 'att-010', school_id: 'school-20188-canete', student_id: 'student-005', attendance_date: new Date('2025-03-11'), status: 'PRESENT', check_in_time: '07:35', record_method: 'QR', registered_by: 'user-admin-20188', created_at: new Date('2025-03-11') },
];

// -----------------------------------------------------------------------------
// CLASSROOMS
// -----------------------------------------------------------------------------

export const demoClassrooms: Classroom[] = [
  { id: 'room-101', school_id: 'school-20188-canete', name: 'Aula 101', code: '101', building: 'Pabellon A', floor: 1, capacity: 35, has_projector: true, has_ac: false, is_lab: false, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'room-102', school_id: 'school-20188-canete', name: 'Aula 102', code: '102', building: 'Pabellon A', floor: 1, capacity: 35, has_projector: true, has_ac: false, is_lab: false, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'room-201', school_id: 'school-20188-canete', name: 'Aula 201', code: '201', building: 'Pabellon A', floor: 2, capacity: 35, has_projector: true, has_ac: true, is_lab: false, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'lab-comp', school_id: 'school-20188-canete', name: 'Laboratorio de Computo', code: 'LAB-C', building: 'Pabellon B', floor: 1, capacity: 30, has_projector: true, has_ac: true, is_lab: true, is_active: true, created_at: new Date('2025-01-01') },
  { id: 'lab-cie', school_id: 'school-20188-canete', name: 'Laboratorio de Ciencias', code: 'LAB-S', building: 'Pabellon B', floor: 1, capacity: 25, has_projector: true, has_ac: true, is_lab: true, is_active: true, created_at: new Date('2025-01-01') },
];

// -----------------------------------------------------------------------------
// TIME SLOTS
// -----------------------------------------------------------------------------

export const demoTimeSlots: TimeSlot[] = [
  { id: 'slot-1', school_id: 'school-20188-canete', name: '1° Hora', start_time: '07:30', end_time: '08:15', slot_type: 'CLASS', order_num: 1, is_active: true },
  { id: 'slot-2', school_id: 'school-20188-canete', name: '2° Hora', start_time: '08:15', end_time: '09:00', slot_type: 'CLASS', order_num: 2, is_active: true },
  { id: 'slot-3', school_id: 'school-20188-canete', name: 'Recreo', start_time: '09:00', end_time: '09:20', slot_type: 'BREAK', order_num: 3, is_active: true },
  { id: 'slot-4', school_id: 'school-20188-canete', name: '3° Hora', start_time: '09:20', end_time: '10:05', slot_type: 'CLASS', order_num: 4, is_active: true },
  { id: 'slot-5', school_id: 'school-20188-canete', name: '4° Hora', start_time: '10:05', end_time: '10:50', slot_type: 'CLASS', order_num: 5, is_active: true },
  { id: 'slot-6', school_id: 'school-20188-canete', name: 'Recreo', start_time: '10:50', end_time: '11:10', slot_type: 'BREAK', order_num: 6, is_active: true },
  { id: 'slot-7', school_id: 'school-20188-canete', name: '5° Hora', start_time: '11:10', end_time: '11:55', slot_type: 'CLASS', order_num: 7, is_active: true },
  { id: 'slot-8', school_id: 'school-20188-canete', name: '6° Hora', start_time: '11:55', end_time: '12:40', slot_type: 'CLASS', order_num: 8, is_active: true },
  { id: 'slot-9', school_id: 'school-20188-canete', name: 'Almuerzo', start_time: '12:40', end_time: '13:30', slot_type: 'LUNCH', order_num: 9, is_active: true },
  { id: 'slot-10', school_id: 'school-20188-canete', name: '7° Hora', start_time: '13:30', end_time: '14:15', slot_type: 'CLASS', order_num: 10, is_active: true },
];

// -----------------------------------------------------------------------------
// SCHEDULE VERSION
// -----------------------------------------------------------------------------

export const demoScheduleVersions: ScheduleVersion[] = [
  { id: 'sv-2025-1', school_id: 'school-20188-canete', school_year_id: 'sy-3sec-2025', name: 'Horario 2025 - Semestre 1', valid_from: new Date('2025-03-10'), status: 'ACTIVE', created_by: 'user-admin-20188', approved_by: 'user-admin-20188', approved_at: new Date('2025-03-05'), created_at: new Date('2025-03-01') },
];

// -----------------------------------------------------------------------------
// CLASS SCHEDULES
// -----------------------------------------------------------------------------

export const demoClassSchedules: ClassSchedule[] = [
  // Monday - 3A
  { id: 'cs-001', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-mat-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-1', day_of_week: 1, week_type: 'ALL', color_hex: '#1e407a', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-002', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-mat-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-2', day_of_week: 1, week_type: 'ALL', color_hex: '#1e407a', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-003', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-com-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-4', day_of_week: 1, week_type: 'ALL', color_hex: '#16a34a', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-004', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-com-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-5', day_of_week: 1, week_type: 'ALL', color_hex: '#16a34a', is_active: true, created_at: new Date('2025-03-01') },
  // Tuesday - 3A
  { id: 'cs-005', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-cie-3a-b1', classroom_id: 'lab-cie', time_slot_id: 'slot-1', day_of_week: 2, week_type: 'ALL', color_hex: '#0369a1', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-006', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-cie-3a-b1', classroom_id: 'lab-cie', time_slot_id: 'slot-2', day_of_week: 2, week_type: 'ALL', color_hex: '#0369a1', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-007', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-his-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-4', day_of_week: 2, week_type: 'ALL', color_hex: '#c8922a', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-008', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-his-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-5', day_of_week: 2, week_type: 'ALL', color_hex: '#c8922a', is_active: true, created_at: new Date('2025-03-01') },
  // Wednesday - 3A
  { id: 'cs-009', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-mat-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-1', day_of_week: 3, week_type: 'ALL', color_hex: '#1e407a', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-010', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-mat-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-2', day_of_week: 3, week_type: 'ALL', color_hex: '#1e407a', is_active: true, created_at: new Date('2025-03-01') },
  { id: 'cs-011', school_id: 'school-20188-canete', schedule_version_id: 'sv-2025-1', course_assignment_id: 'ca-ing-3a-b1', classroom_id: 'room-101', time_slot_id: 'slot-4', day_of_week: 3, week_type: 'ALL', color_hex: '#7c3aed', is_active: true, created_at: new Date('2025-03-01') },
];

// -----------------------------------------------------------------------------
// NOTIFICATIONS
// -----------------------------------------------------------------------------

export const demoNotifications: Notification[] = [
  { id: 'notif-001', school_id: 'school-20188-canete', user_id: 'user-admin-20188', notification_type: 'ATTENDANCE_ALERT', title: 'Alerta de Asistencia', body: 'El estudiante Jose Huaman tiene 3 faltas consecutivas en el 1° Bimestre.', action_url: '/attendance', is_read: false, created_at: new Date() },
  { id: 'notif-002', school_id: 'school-20188-canete', target_role: 'TEACHER', notification_type: 'PERIOD_CLOSING', title: 'Cierre de Periodo', body: 'El 1° Bimestre cierra en 7 dias. Complete el registro de notas pendientes.', action_url: '/grades', is_read: false, created_at: new Date() },
  { id: 'notif-003', school_id: 'school-20188-canete', user_id: 'user-guardian-20188', notification_type: 'SCORE_REGISTERED', title: 'Nueva Nota Registrada', body: 'Se ha registrado la nota de Matematica - Practica Calificada 1 para Diego Vargas.', action_url: '/grades', is_read: true, read_at: new Date(), created_at: new Date(Date.now() - 86400000) },
  { id: 'notif-004', school_id: 'school-20188-canete', target_role: 'ADMIN', notification_type: 'REPORT_READY', title: 'Reporte Generado', body: 'El reporte de asistencia del mes de marzo esta listo para descarga.', action_url: '/reports', is_read: false, created_at: new Date() },
];

// -----------------------------------------------------------------------------
// JUSTIFICATIONS
// -----------------------------------------------------------------------------

export const demoJustifications: Justification[] = [
  { id: 'just-001', school_id: 'school-20188-canete', student_id: 'student-004', attendance_id: 'att-009', requested_by: 'user-guardian-20188', reason: 'Cita medica programada en el Hospital Regional de Canete', document_url: '/docs/justificacion-001.pdf', status: 'APPROVED', reviewed_by: 'user-coord-20188', review_note: 'Documento verificado', auto_approved: false, requested_at: new Date('2025-03-11'), reviewed_at: new Date('2025-03-11') },
  { id: 'just-002', school_id: 'school-20188-canete', student_id: 'student-005', attendance_id: 'att-005', requested_by: 'user-guardian-20188', reason: 'Enfermedad estomacal', status: 'PENDING', auto_approved: false, requested_at: new Date('2025-03-10'), expires_at: new Date('2025-03-12') },
];

// -----------------------------------------------------------------------------
// REPORT JOBS
// -----------------------------------------------------------------------------

export const demoReportJobs: ReportJob[] = [
  { id: 'rj-001', school_id: 'school-20188-canete', requested_by: 'user-admin-20188', report_type: 'ATTENDANCE_SUMMARY', format: 'PDF', parameters_json: '{"period":"ap-b1-3sec-2025","section":"sec-3a-2025"}', status: 'COMPLETED', file_path: '/reports/attendance-march-2025.pdf', file_size_kb: 245, progress_pct: 100, requested_at: new Date(Date.now() - 3600000), started_at: new Date(Date.now() - 3500000), completed_at: new Date(Date.now() - 3400000), expires_at: new Date(Date.now() + 259200000), download_count: 2 },
  { id: 'rj-002', school_id: 'school-20188-canete', requested_by: 'user-teacher-20188', report_type: 'GRADES_SECTION', format: 'XLSX', parameters_json: '{"period":"ap-b1-3sec-2025","course":"course-mat-20188"}', status: 'PROCESSING', progress_pct: 65, requested_at: new Date(Date.now() - 300000), started_at: new Date(Date.now() - 240000), download_count: 0 },
  { id: 'rj-003', school_id: 'school-20188-canete', requested_by: 'user-admin-20188', report_type: 'BOLETIN', format: 'PDF', parameters_json: '{"student":"student-001","period":"ap-b1-3sec-2025"}', status: 'PENDING', progress_pct: 0, requested_at: new Date(), download_count: 0 },
];

// -----------------------------------------------------------------------------
// BLOCKCHAIN
// -----------------------------------------------------------------------------

export const demoBlockchainBlocks: BlockchainBlock[] = [
  { id: 'block-genesis', school_id: 'school-20188-canete', block_index: 0, event_type: 'GENESIS', entity_id: 'school-20188-canete', entity_type: 'SCHOOL', payload: '{"event":"GENESIS","description":"Bloque genesis de la cadena Fine Flow","school_id":"school-20188-canete"}', previous_hash: '0000000000000000000000000000000000000000000000000000000000000000', hash: 'a91b6f3d5c2e8047f1d4b9e6c3a0f7d2b5e8c1a4f7d0b3e6c9a2f5d8b1e4c7a0', created_by: 'user-admin-20188', created_at: new Date('2025-01-01') },
  { id: 'block-001', school_id: 'school-20188-canete', block_index: 1, event_type: 'ENROLLMENT', entity_id: 'student-001', entity_type: 'STUDENT', payload: '{"action":"ENROLL","student_id":"student-001","section_id":"sec-3a-2025"}', previous_hash: 'a91b6f3d5c2e8047f1d4b9e6c3a0f7d2b5e8c1a4f7d0b3e6c9a2f5d8b1e4c7a0', hash: 'b82c7e4f6d3a9158f2e5c0d7a4b1e8f3c6a9d2b5e8c1f4a7d0b3e6c9a2f5d801', created_by: 'user-admin-20188', created_at: new Date('2025-03-01') },
  { id: 'block-002', school_id: 'school-20188-canete', block_index: 2, event_type: 'SCORE', entity_id: 'score-001', entity_type: 'STUDENT_SCORE', payload: '{"action":"REGISTER","student_id":"student-001","task_id":"task-001","score":18}', previous_hash: 'b82c7e4f6d3a9158f2e5c0d7a4b1e8f3c6a9d2b5e8c1f4a7d0b3e6c9a2f5d801', hash: 'c73d8f5g7e4b0269g3f6d1e8b5c2f9g4d7b0e3c6f9b2e5g8c1d4f7a0b3e69c2', created_by: 'user-teacher-20188', created_at: new Date('2025-03-21') },
];

// -----------------------------------------------------------------------------
// SYSTEM CONFIG
// -----------------------------------------------------------------------------

export const demoSystemConfig: SystemConfig[] = [
  { id: 'cfg-01', school_id: 'school-20188-canete', config_key: 'ATTENDANCE_ABSENCE_ALERT_THRESHOLD', config_value: '3', value_type: 'NUMBER', description: 'Numero de faltas consecutivas para generar alerta de asistencia', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-02', school_id: 'school-20188-canete', config_key: 'JUSTIFICATION_DEADLINE_HOURS', config_value: '48', value_type: 'NUMBER', description: 'Horas maximas para presentar justificacion de inasistencia', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-03', school_id: 'school-20188-canete', config_key: 'QR_ROTATION_DAYS', config_value: '7', value_type: 'NUMBER', description: 'Dias entre rotaciones del secreto HMAC del QR del carnet', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-04', school_id: 'school-20188-canete', config_key: 'LATE_THRESHOLD_MINUTES', config_value: '10', value_type: 'NUMBER', description: 'Minutos de tolerancia antes de marcar tardanza', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-05', school_id: 'school-20188-canete', config_key: 'MIN_PASSING_SCORE', config_value: '11', value_type: 'NUMBER', description: 'Nota minima para aprobar (escala MINEDU 0-20)', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-06', school_id: 'school-20188-canete', config_key: 'SCHOOL_START_TIME', config_value: '07:30', value_type: 'STRING', description: 'Hora de inicio de clases (HH:MM, para validacion de QR)', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-07', school_id: 'school-20188-canete', config_key: 'BLOCKCHAIN_EVENTS_ENABLED', config_value: 'true', value_type: 'BOOLEAN', description: 'Activar registro automatico de eventos en el blockchain', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
  { id: 'cfg-08', school_id: 'school-20188-canete', config_key: 'MAX_LOGIN_ATTEMPTS', config_value: '5', value_type: 'NUMBER', description: 'Intentos fallidos antes de bloquear la cuenta (status=LOCKED)', is_sensitive: false, updated_at: new Date(), created_at: new Date() },
];

// -----------------------------------------------------------------------------
// SUBSCRIPTION
// -----------------------------------------------------------------------------

export const demoSubscription: SchoolSubscription = {
  id: 'sub-demo-20188',
  school_id: 'school-20188-canete',
  plan_name: 'PREMIUM',
  max_students: 500,
  max_teachers: 50,
  max_storage_gb: 10.00,
  features_json: JSON.stringify({ blockchain: true, ai_chat: true, reports: true, qr_attendance: true, bulk_import: true }),
  billing_cycle: 'MONTHLY',
  starts_at: new Date('2025-01-01'),
  is_trial: true,
  status: 'ACTIVE',
  created_at: new Date('2025-01-01'),
  updated_at: new Date(),
};

// -----------------------------------------------------------------------------
// HELPER FUNCTIONS
// -----------------------------------------------------------------------------

export function getStudentsBySection(sectionId: string): Student[] {
  return demoStudents.filter(s => s.section_id === sectionId);
}

export function getCoursesByTeacher(teacherId: string): Course[] {
  const assignments = demoCourseAssignments.filter(ca => ca.teacher_id === teacherId);
  const courseIds = [...new Set(assignments.map(a => a.course_id))];
  return demoCourses.filter(c => courseIds.includes(c.id));
}

export function getAttendanceByDate(date: Date): Attendance[] {
  const dateStr = date.toISOString().split('T')[0];
  return demoAttendances.filter(a => a.attendance_date.toISOString().split('T')[0] === dateStr);
}

export function getCompetenciesByCourse(courseId: string): CourseCompetency[] {
  return demoCourseCompetencies.filter(c => c.course_id === courseId);
}

export function getScoresByStudent(studentId: string): StudentScore[] {
  return demoStudentScores.filter(s => s.student_id === studentId);
}

export function getScheduleByDay(dayOfWeek: number, sectionId: string): ClassSchedule[] {
  const assignments = demoCourseAssignments.filter(ca => ca.section_id === sectionId);
  const assignmentIds = assignments.map(a => a.id);
  return demoClassSchedules.filter(
    cs => cs.day_of_week === dayOfWeek && assignmentIds.includes(cs.course_assignment_id)
  );
}
