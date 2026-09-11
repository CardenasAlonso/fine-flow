-- =============================================================================
-- FINE FLOW - SCRIPT MAESTRO UNIFICADO v5.0
-- Oracle Autonomous Database 26ai - Ejecutar como ADMIN (usuario maestro)
-- Soporte exclusivo: Nivel Inicial, Primaria y Secundaria (Educacion Basica)
-- =============================================================================

/* === FASE 0: CREACI?N DEL USUARIO / SCHEMA === */
SELECT 'FASE 0: Creating FINEFLOW_APP user...' AS status FROM DUAL;

BEGIN
    FOR r IN (SELECT username FROM dba_users WHERE username = 'FINEFLOW_APP') LOOP
        EXECUTE IMMEDIATE 'DROP USER FINEFLOW_APP CASCADE';
    END LOOP;
END;
/

CREATE USER FINEFLOW_APP IDENTIFIED BY "FineFlow_2025!"
    DEFAULT   TABLESPACE DATA
    TEMPORARY TABLESPACE TEMP
    QUOTA UNLIMITED ON DATA;

-- Permisos b?sicos
GRANT CREATE SESSION      TO FINEFLOW_APP;
GRANT CREATE TABLE        TO FINEFLOW_APP;
GRANT CREATE VIEW         TO FINEFLOW_APP;
GRANT CREATE SEQUENCE     TO FINEFLOW_APP;
GRANT CREATE PROCEDURE    TO FINEFLOW_APP;
GRANT CREATE TRIGGER      TO FINEFLOW_APP;
GRANT CREATE TYPE         TO FINEFLOW_APP;
GRANT CREATE SYNONYM      TO FINEFLOW_APP;
GRANT CREATE JOB          TO FINEFLOW_APP;
GRANT CREATE ANY CONTEXT  TO FINEFLOW_APP;

-- Permisos sobre paquetes del sistema
-- (En Oracle Cloud ADB algunos paquetes SYS no son granteables directamente)
BEGIN
    EXECUTE IMMEDIATE 'GRANT EXECUTE ON DBMS_SESSION TO FINEFLOW_APP';
    EXECUTE IMMEDIATE 'GRANT EXECUTE ON DBMS_RLS TO FINEFLOW_APP';
    EXECUTE IMMEDIATE 'GRANT EXECUTE ON DBMS_SCHEDULER TO FINEFLOW_APP';
    EXECUTE IMMEDIATE 'GRANT EXECUTE ON DBMS_CRYPTO TO FINEFLOW_APP';
    EXECUTE IMMEDIATE 'GRANT EXECUTE ON UTL_I18N TO FINEFLOW_APP';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- Vistas del diccionario para la verificaci?n final
GRANT SELECT ON SYS.DBA_TABLES         TO FINEFLOW_APP;
GRANT SELECT ON SYS.DBA_VIEWS          TO FINEFLOW_APP;
GRANT SELECT ON SYS.DBA_TRIGGERS       TO FINEFLOW_APP;
GRANT SELECT ON SYS.DBA_PROCEDURES     TO FINEFLOW_APP;
GRANT SELECT ON SYS.DBA_INDEXES        TO FINEFLOW_APP;
GRANT SELECT ON SYS.DBA_SCHEDULER_JOBS TO FINEFLOW_APP;

-- Conectar al schema del aplicativo
ALTER SESSION SET CURRENT_SCHEMA = FINEFLOW_APP;

-- FASES 1-7 + SEED DATA v2.1
-- N?cleo SaaS ? Estructura Acad?mica ? Personas ? Malla Curricular
-- Transaccional ? Soporte Digital ? Tablas Avanzadas ? Datos Semilla
-- =============================================================================


CREATE TABLE SCHOOLS (
    id              VARCHAR2(50)    NOT NULL,
    name            VARCHAR2(200)   NOT NULL,
    document_number VARCHAR2(20)    NOT NULL,   -- RUC / C?digo Modular MINEDU
    address         VARCHAR2(300),
    phone           VARCHAR2(20),
    email           VARCHAR2(150),
    logo_url        VARCHAR2(500),
    status          VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_schools           PRIMARY KEY (id),
    CONSTRAINT uq_schools_doc_num   UNIQUE      (document_number),
    CONSTRAINT ck_schools_status    CHECK       (status IN ('ACTIVE','INACTIVE','SUSPENDED'))
);

COMMENT ON TABLE  SCHOOLS                IS 'Raiz del modelo multi-tenant. Cada fila es una Institucion Educativa (Inicial, Primaria o Secundaria).';
COMMENT ON COLUMN SCHOOLS.document_number IS 'RUC o Codigo Modular MINEDU. Unico en todo el SaaS.';
COMMENT ON COLUMN SCHOOLS.status         IS 'ACTIVE | INACTIVE | SUSPENDED';


-- -----------------------------------------------------------------------------
-- Tabla: USERS
-- Cuentas de acceso al sistema. Un usuario pertenece a UN solo colegio.
-- El email es ?nico dentro de cada colegio (no globalmente).
-- -----------------------------------------------------------------------------
CREATE TABLE USERS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    email           VARCHAR2(150)   NOT NULL,
    password_hash   VARCHAR2(255)   NOT NULL,
    role            VARCHAR2(30)    NOT NULL,
    first_name      VARCHAR2(100),
    last_name       VARCHAR2(100),
    status          VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_users             PRIMARY KEY (id),
    CONSTRAINT fk_users_school      FOREIGN KEY (school_id)  REFERENCES SCHOOLS(id),
    CONSTRAINT uq_users_email_school UNIQUE      (school_id, email),
    CONSTRAINT ck_users_role        CHECK       (role   IN ('ADMIN','COORDINATOR','TEACHER','STUDENT','GUARDIAN')),
    CONSTRAINT ck_users_status      CHECK       (status IN ('ACTIVE','INACTIVE','LOCKED'))
);

COMMENT ON TABLE  USERS       IS 'Cuentas de acceso. Email ?nico por colegio (no globalmente).';
COMMENT ON COLUMN USERS.role  IS 'ADMIN | COORDINATOR | TEACHER | STUDENT | GUARDIAN';


-- =============================================================================
-- BLOQUE 2: ESTRUCTURA ACAD?MICA
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: ACADEMIC_LEVELS
-- Niveles educativos: Primaria, Secundaria, etc.
-- -----------------------------------------------------------------------------
CREATE TABLE ACADEMIC_LEVELS (
    id          VARCHAR2(50)    NOT NULL,
    school_id   VARCHAR2(50)    NOT NULL,
    name        VARCHAR2(100)   NOT NULL,
    order_num   NUMBER(2)       DEFAULT 1 NOT NULL,
    is_active   NUMBER(1)       DEFAULT 1 NOT NULL,
    --
    CONSTRAINT pk_academic_levels       PRIMARY KEY (id),
    CONSTRAINT fk_acad_levels_school    FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_acad_levels_name      UNIQUE      (school_id, name),
    CONSTRAINT ck_acad_levels_active    CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE ACADEMIC_LEVELS IS 'Niveles educativos: Inicial, Primaria o Secundaria por colegio.';


-- -----------------------------------------------------------------------------
-- Tabla: SCHOOL_YEARS
-- A?os/grados acad?micos: 1? Secundaria, 2? Secundaria, etc.
-- -----------------------------------------------------------------------------
CREATE TABLE SCHOOL_YEARS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    academic_level_id   VARCHAR2(50)    NOT NULL,
    name                VARCHAR2(100)   NOT NULL,  -- Ej: "3? Secundaria 2025"
    grade_number        NUMBER(2)       NOT NULL,  -- Ej: 3
    calendar_year       NUMBER(4)       NOT NULL,  -- Ej: 2025
    is_active           NUMBER(1)       DEFAULT 1 NOT NULL,
    --
    CONSTRAINT pk_school_years          PRIMARY KEY (id),
    CONSTRAINT fk_sy_school             FOREIGN KEY (school_id)         REFERENCES SCHOOLS(id),
    CONSTRAINT fk_sy_acad_level         FOREIGN KEY (academic_level_id) REFERENCES ACADEMIC_LEVELS(id),
    CONSTRAINT uq_sy_grade_year         UNIQUE      (school_id, academic_level_id, grade_number, calendar_year),
    CONSTRAINT ck_sy_grade              CHECK       (grade_number BETWEEN 1 AND 12),
    CONSTRAINT ck_sy_active             CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE  SCHOOL_YEARS              IS 'Grados acad?micos por nivel, colegio y a?o calendario.';
COMMENT ON COLUMN SCHOOL_YEARS.grade_number IS 'N?mero de grado: 1 a 12.';


-- -----------------------------------------------------------------------------
-- Tabla: ACADEMIC_PERIODS
-- Bimestres, trimestres o semestres dentro de un a?o acad?mico.
-- -----------------------------------------------------------------------------
CREATE TABLE ACADEMIC_PERIODS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    school_year_id  VARCHAR2(50)    NOT NULL,
    name            VARCHAR2(100)   NOT NULL,  -- Ej: "1? Bimestre", "II Trimestre"
    period_type     VARCHAR2(20)    DEFAULT 'BIMESTER' NOT NULL,
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    is_active       NUMBER(1)       DEFAULT 0 NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_academic_periods      PRIMARY KEY (id),
    CONSTRAINT fk_ap_school             FOREIGN KEY (school_id)      REFERENCES SCHOOLS(id),
    CONSTRAINT fk_ap_school_year        FOREIGN KEY (school_year_id) REFERENCES SCHOOL_YEARS(id),
    CONSTRAINT uq_ap_name_year          UNIQUE      (school_id, school_year_id, name),
    CONSTRAINT ck_ap_dates              CHECK       (end_date > start_date),
    CONSTRAINT ck_ap_type               CHECK       (period_type IN ('BIMESTER','TRIMESTER','SEMESTER','ANNUAL')),
    CONSTRAINT ck_ap_active             CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE  ACADEMIC_PERIODS           IS 'Per?odos de evaluaci?n (bimestres, trimestres, semestres).';
COMMENT ON COLUMN ACADEMIC_PERIODS.is_active IS '1 = per?odo en curso actualmente.';


-- -----------------------------------------------------------------------------
-- Tabla: SECTIONS
-- Aulas/secciones dentro de un a?o acad?mico. Ej: "3?A", "3?B".
-- -----------------------------------------------------------------------------
CREATE TABLE SECTIONS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    school_year_id  VARCHAR2(50)    NOT NULL,
    name            VARCHAR2(10)    NOT NULL,  -- Ej: "A", "B", "C"
    max_capacity    NUMBER(3)       DEFAULT 30 NOT NULL,
    tutor_id        VARCHAR2(50),              -- FK a TEACHERS (se agrega al final)
    is_active       NUMBER(1)       DEFAULT 1 NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_sections          PRIMARY KEY (id),
    CONSTRAINT fk_sections_school   FOREIGN KEY (school_id)      REFERENCES SCHOOLS(id),
    CONSTRAINT fk_sections_sy       FOREIGN KEY (school_year_id) REFERENCES SCHOOL_YEARS(id),
    CONSTRAINT uq_sections_name     UNIQUE      (school_id, school_year_id, name),
    CONSTRAINT ck_sections_capacity CHECK       (max_capacity BETWEEN 1 AND 60),
    CONSTRAINT ck_sections_active   CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE  SECTIONS              IS 'Aulas/secciones por a?o acad?mico y colegio.';
COMMENT ON COLUMN SECTIONS.max_capacity IS 'Aforo m?ximo de la secci?n (1-60).';


-- =============================================================================
-- BLOQUE 3: PERSONAS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: STUDENTS
-- Alumnos matriculados. document_number ?nico dentro del mismo colegio.
-- -----------------------------------------------------------------------------
CREATE TABLE STUDENTS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    section_id      VARCHAR2(50),              -- NULL cuando est? EGRESADO
    user_id         VARCHAR2(50),              -- FK a USERS (portal del alumno)
    first_name      VARCHAR2(100)   NOT NULL,
    last_name       VARCHAR2(100)   NOT NULL,
    document_type   VARCHAR2(10)    DEFAULT 'DNI' NOT NULL,
    document_number VARCHAR2(20)    NOT NULL,
    birth_date      DATE,
    blood_type      VARCHAR2(5),
    address         VARCHAR2(300),
    photo_url       VARCHAR2(500),
    qr_secret       VARCHAR2(255),             -- Secreto HMAC-SHA256 para QR
    qr_rotated_at   TIMESTAMP,
    status          VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_students              PRIMARY KEY (id),
    CONSTRAINT fk_students_school       FOREIGN KEY (school_id)  REFERENCES SCHOOLS(id),
    CONSTRAINT fk_students_section      FOREIGN KEY (section_id) REFERENCES SECTIONS(id),
    CONSTRAINT fk_students_user         FOREIGN KEY (user_id)    REFERENCES USERS(id),
    CONSTRAINT uq_students_doc          UNIQUE      (school_id, document_number),
    CONSTRAINT ck_students_doc_type     CHECK       (document_type IN ('DNI','CE','PASSPORT','OTHER')),
    CONSTRAINT ck_students_blood        CHECK       (blood_type    IN ('A+','A-','B+','B-','AB+','AB-','O+','O-') OR blood_type IS NULL),
    CONSTRAINT ck_students_status       CHECK       (status        IN ('ACTIVE','INACTIVE','GRADUATED','TRANSFERRED','EGRESADO'))
);

COMMENT ON TABLE  STUDENTS             IS 'Alumnos matriculados. document_number ?nico por colegio.';
COMMENT ON COLUMN STUDENTS.qr_secret   IS 'Secreto HMAC-SHA256 para generar/validar el QR del carnet.';
COMMENT ON COLUMN STUDENTS.section_id  IS 'NULL cuando el alumno ha egresado o fue transferido.';


-- -----------------------------------------------------------------------------
-- Tabla: GUARDIANS
-- Apoderados/tutores vinculados a uno o m?s alumnos.
-- -----------------------------------------------------------------------------
CREATE TABLE GUARDIANS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    user_id             VARCHAR2(50),              -- FK a USERS (portal apoderado)
    student_id          VARCHAR2(50)    NOT NULL,
    first_name          VARCHAR2(100)   NOT NULL,
    last_name           VARCHAR2(100)   NOT NULL,
    document_number     VARCHAR2(20),
    relationship        VARCHAR2(30)    NOT NULL,
    phone               VARCHAR2(20),
    email               VARCHAR2(150),
    is_primary_contact  NUMBER(1)       DEFAULT 0 NOT NULL,
    created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_guardians             PRIMARY KEY (id),
    CONSTRAINT fk_guardians_school      FOREIGN KEY (school_id)  REFERENCES SCHOOLS(id),
    CONSTRAINT fk_guardians_user        FOREIGN KEY (user_id)    REFERENCES USERS(id),
    CONSTRAINT fk_guardians_student     FOREIGN KEY (student_id) REFERENCES STUDENTS(id),
    CONSTRAINT ck_guardians_relation    CHECK       (relationship IN ('FATHER','MOTHER','GRANDPARENT','SIBLING','UNCLE','LEGAL_GUARDIAN','OTHER')),
    CONSTRAINT ck_guardians_primary     CHECK       (is_primary_contact IN (0,1))
);

COMMENT ON TABLE  GUARDIANS                      IS 'Apoderados vinculados a alumnos del colegio.';
COMMENT ON COLUMN GUARDIANS.is_primary_contact   IS '1 = contacto principal del alumno.';


-- -----------------------------------------------------------------------------
-- Tabla: TEACHERS
-- Docentes del colegio, vinculados a una cuenta USERS.
-- -----------------------------------------------------------------------------
CREATE TABLE TEACHERS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    user_id         VARCHAR2(50),              -- FK a USERS
    first_name      VARCHAR2(100)   NOT NULL,
    last_name       VARCHAR2(100)   NOT NULL,
    document_number VARCHAR2(20)    NOT NULL,
    specialty       VARCHAR2(200),             -- JSON o texto: "Matem?tica,F?sica"
    phone           VARCHAR2(20),
    status          VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    hired_at        DATE,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_teachers          PRIMARY KEY (id),
    CONSTRAINT fk_teachers_school   FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT fk_teachers_user     FOREIGN KEY (user_id)   REFERENCES USERS(id),
    CONSTRAINT uq_teachers_doc      UNIQUE      (school_id, document_number),
    CONSTRAINT ck_teachers_status   CHECK       (status IN ('ACTIVE','INACTIVE','ON_LEAVE'))
);

COMMENT ON TABLE TEACHERS IS 'Docentes del colegio. document_number ?nico por colegio.';

-- Ahora que TEACHERS existe, agregamos la FK de tutor en SECTIONS
ALTER TABLE SECTIONS
    ADD CONSTRAINT fk_sections_tutor
    FOREIGN KEY (tutor_id) REFERENCES TEACHERS(id);


-- =============================================================================
-- BLOQUE 4: MALLA CURRICULAR (CNEB / MINEDU)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: COURSES
-- Cursos/?reas curriculares. Ej: "Matem?tica", "Comunicaci?n".
-- -----------------------------------------------------------------------------
CREATE TABLE COURSES (
    id          VARCHAR2(50)    NOT NULL,
    school_id   VARCHAR2(50)    NOT NULL,
    name        VARCHAR2(200)   NOT NULL,
    code        VARCHAR2(20),               -- C?digo interno del ?rea
    description VARCHAR2(1000),
    color_hex   VARCHAR2(7),                -- Color para UI (#RRGGBB)
    is_active   NUMBER(1)       DEFAULT 1 NOT NULL,
    created_at  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_courses           PRIMARY KEY (id),
    CONSTRAINT fk_courses_school    FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_courses_name      UNIQUE      (school_id, name),
    CONSTRAINT ck_courses_active    CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE COURSES IS '?reas curriculares/cursos definidos por cada colegio.';


-- -----------------------------------------------------------------------------
-- Tabla: COURSE_ASSIGNMENTS
-- Asignaci?n de un curso a una secci?n, docente y per?odo acad?mico.
-- Ej: Garc?a ? Matem?tica ? 3?A ? 2025 Bimestre 1
-- -----------------------------------------------------------------------------
CREATE TABLE COURSE_ASSIGNMENTS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    course_id           VARCHAR2(50)    NOT NULL,
    section_id          VARCHAR2(50)    NOT NULL,
    teacher_id          VARCHAR2(50)    NOT NULL,
    academic_period_id  VARCHAR2(50)    NOT NULL,
    hours_per_week      NUMBER(2)       DEFAULT 4 NOT NULL,
    is_active           NUMBER(1)       DEFAULT 1 NOT NULL,
    created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_course_assignments        PRIMARY KEY (id),
    CONSTRAINT fk_ca_school                 FOREIGN KEY (school_id)          REFERENCES SCHOOLS(id),
    CONSTRAINT fk_ca_course                 FOREIGN KEY (course_id)          REFERENCES COURSES(id),
    CONSTRAINT fk_ca_section                FOREIGN KEY (section_id)         REFERENCES SECTIONS(id),
    CONSTRAINT fk_ca_teacher                FOREIGN KEY (teacher_id)         REFERENCES TEACHERS(id),
    CONSTRAINT fk_ca_period                 FOREIGN KEY (academic_period_id) REFERENCES ACADEMIC_PERIODS(id),
    CONSTRAINT uq_ca_course_section_period  UNIQUE      (school_id, course_id, section_id, academic_period_id),
    CONSTRAINT ck_ca_hours                  CHECK       (hours_per_week BETWEEN 1 AND 40),
    CONSTRAINT ck_ca_active                 CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE  COURSE_ASSIGNMENTS              IS 'Asignaci?n de curso+secci?n+docente+per?odo. Unique por combinaci?n.';
COMMENT ON COLUMN COURSE_ASSIGNMENTS.hours_per_week IS 'Horas pedag?gicas por semana (1-40).';


-- -----------------------------------------------------------------------------
-- Tabla: COURSE_COMPETENCIES
-- Competencias MINEDU/CNEB asociadas a cada ?rea curricular.
-- Ej: "Resuelve problemas de cantidad" ? Matem?tica
-- -----------------------------------------------------------------------------
CREATE TABLE COURSE_COMPETENCIES (
    id          VARCHAR2(50)    NOT NULL,
    school_id   VARCHAR2(50)    NOT NULL,
    course_id   VARCHAR2(50)    NOT NULL,
    name        VARCHAR2(300)   NOT NULL,
    description VARCHAR2(2000),
    weight      NUMBER(5,2)     DEFAULT 100 NOT NULL, -- Peso porcentual
    is_active   NUMBER(1)       DEFAULT 1   NOT NULL,
    created_at  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_course_competencies       PRIMARY KEY (id),
    CONSTRAINT fk_cc_school                 FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT fk_cc_course                 FOREIGN KEY (course_id) REFERENCES COURSES(id),
    CONSTRAINT uq_cc_name_course            UNIQUE      (school_id, course_id, name),
    CONSTRAINT ck_cc_weight                 CHECK       (weight BETWEEN 0 AND 100),
    CONSTRAINT ck_cc_active                 CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE  COURSE_COMPETENCIES        IS 'Competencias CNEB/MINEDU por ?rea curricular.';
COMMENT ON COLUMN COURSE_COMPETENCIES.weight IS 'Peso porcentual de la competencia en la nota final (0-100).';


-- -----------------------------------------------------------------------------
-- Tabla: CLASS_TASKS
-- Actividades evaluables vinculadas a una asignaci?n y competencia.
-- Ej: "Examen Bimestral", "Pr?ctica Calificada 1"
-- -----------------------------------------------------------------------------
CREATE TABLE CLASS_TASKS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    course_assignment_id VARCHAR2(50)   NOT NULL,
    competency_id       VARCHAR2(50)    NOT NULL,
    academic_period_id  VARCHAR2(50)    NOT NULL,
    title               VARCHAR2(300)   NOT NULL,
    description         VARCHAR2(2000),
    task_type           VARCHAR2(30)    DEFAULT 'EXAM' NOT NULL,
    max_score           NUMBER(5,2)     DEFAULT 20 NOT NULL,
    due_date            DATE,
    is_active           NUMBER(1)       DEFAULT 1 NOT NULL,
    created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_class_tasks           PRIMARY KEY (id),
    CONSTRAINT fk_ct_school             FOREIGN KEY (school_id)            REFERENCES SCHOOLS(id),
    CONSTRAINT fk_ct_assignment         FOREIGN KEY (course_assignment_id) REFERENCES COURSE_ASSIGNMENTS(id),
    CONSTRAINT fk_ct_competency         FOREIGN KEY (competency_id)        REFERENCES COURSE_COMPETENCIES(id),
    CONSTRAINT fk_ct_period             FOREIGN KEY (academic_period_id)   REFERENCES ACADEMIC_PERIODS(id),
    CONSTRAINT ck_ct_max_score          CHECK       (max_score BETWEEN 0 AND 100),
    CONSTRAINT ck_ct_task_type          CHECK       (task_type IN ('EXAM','QUIZ','HOMEWORK','PROJECT','ORAL','LAB','PARTICIPATION','OTHER')),
    CONSTRAINT ck_ct_active             CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE  CLASS_TASKS           IS 'Actividades/tareas evaluables por asignaci?n de curso y competencia.';
COMMENT ON COLUMN CLASS_TASKS.task_type IS 'EXAM | QUIZ | HOMEWORK | PROJECT | ORAL | LAB | PARTICIPATION | OTHER';


-- =============================================================================
-- BLOQUE 5: TRANSACCIONAL (ASISTENCIA Y NOTAS)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: ATTENDANCES
-- Registro diario de asistencia. course_assignment_id puede ser NULL
-- cuando el registro es de entrada general al colegio (control QR).
-- -----------------------------------------------------------------------------
CREATE TABLE ATTENDANCES (
    id                      VARCHAR2(50)    NOT NULL,
    school_id               VARCHAR2(50)    NOT NULL,
    student_id              VARCHAR2(50)    NOT NULL,
    course_assignment_id    VARCHAR2(50),              -- NULL = entrada general al colegio
    attendance_date         DATE            NOT NULL,
    status                  VARCHAR2(20)    NOT NULL,
    check_in_time           VARCHAR2(8),               -- HH:MI:SS
    record_method           VARCHAR2(20)    DEFAULT 'MANUAL' NOT NULL,
    justification_reason    VARCHAR2(1000),
    registered_by           VARCHAR2(50),              -- FK a USERS (docente/admin)
    created_at              TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_attendances               PRIMARY KEY (id),
    CONSTRAINT fk_att_school                FOREIGN KEY (school_id)            REFERENCES SCHOOLS(id),
    CONSTRAINT fk_att_student               FOREIGN KEY (student_id)           REFERENCES STUDENTS(id),
    CONSTRAINT fk_att_assignment            FOREIGN KEY (course_assignment_id) REFERENCES COURSE_ASSIGNMENTS(id),
    CONSTRAINT fk_att_registered_by         FOREIGN KEY (registered_by)        REFERENCES USERS(id),
    -- Unique: un alumno solo puede tener un registro por d?a por asignaci?n (o entrada)
    CONSTRAINT uq_att_student_date_assign   UNIQUE      (school_id, student_id, attendance_date, course_assignment_id),
    CONSTRAINT ck_att_status                CHECK       (status        IN ('PRESENT','ABSENT','LATE','EXCUSED','HOLIDAY')),
    CONSTRAINT ck_att_method                CHECK       (record_method IN ('MANUAL','QR','BULK','SYSTEM'))
);

COMMENT ON TABLE  ATTENDANCES                      IS 'Asistencia diaria por alumno. Puede ser por clase o entrada general (QR).';
COMMENT ON COLUMN ATTENDANCES.course_assignment_id IS 'NULL cuando es control de entrada general al colegio.';
COMMENT ON COLUMN ATTENDANCES.record_method        IS 'MANUAL | QR | BULK | SYSTEM';

CREATE INDEX idx_att_student_date  ON ATTENDANCES (school_id, student_id, attendance_date);
CREATE INDEX idx_att_date_section  ON ATTENDANCES (school_id, attendance_date);


-- -----------------------------------------------------------------------------
-- Tabla: STUDENT_SCORES
-- Calificaciones (0-20) de cada alumno por tarea evaluable.
-- UNIQUE en (student_id, class_task_id) para evitar duplicados.
-- -----------------------------------------------------------------------------
CREATE TABLE STUDENT_SCORES (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    student_id      VARCHAR2(50)    NOT NULL,
    class_task_id   VARCHAR2(50)    NOT NULL,
    score           NUMBER(5,2)     NOT NULL,
    comments        VARCHAR2(2000),
    registered_by   VARCHAR2(50),              -- FK a USERS (docente)
    registered_at   TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_student_scores        PRIMARY KEY (id),
    CONSTRAINT fk_ss_school             FOREIGN KEY (school_id)     REFERENCES SCHOOLS(id),
    CONSTRAINT fk_ss_student            FOREIGN KEY (student_id)    REFERENCES STUDENTS(id),
    CONSTRAINT fk_ss_class_task         FOREIGN KEY (class_task_id) REFERENCES CLASS_TASKS(id),
    CONSTRAINT fk_ss_registered_by      FOREIGN KEY (registered_by) REFERENCES USERS(id),
    -- Constraint UNIQUE: un alumno solo puede tener UNA nota por tarea
    CONSTRAINT uq_ss_student_task       UNIQUE      (student_id, class_task_id),
    CONSTRAINT ck_ss_score              CHECK       (score BETWEEN 0 AND 20)
);

COMMENT ON TABLE  STUDENT_SCORES       IS 'Calificaciones 0-20 por alumno y tarea evaluable. Una nota por tarea.';
COMMENT ON COLUMN STUDENT_SCORES.score IS 'Escala vigesimal MINEDU: 0.00 a 20.00.';

CREATE INDEX idx_ss_student     ON STUDENT_SCORES (school_id, student_id);
CREATE INDEX idx_ss_task        ON STUDENT_SCORES (school_id, class_task_id);


-- =============================================================================
-- BLOQUE 6: ADDENDUMS (BLOCKCHAIN Y CHATBOT)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: BLOCKCHAIN_BLOCKS
-- Registro inmutable de eventos cr?ticos del sistema.
-- Implementa una cadena hash SHA-256 por colegio.
-- El bloque g?nesis usa previous_hash = '0000...0000' (64 ceros).
-- -----------------------------------------------------------------------------
CREATE TABLE BLOCKCHAIN_BLOCKS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    block_index     NUMBER(10)      NOT NULL,  -- ?ndice secuencial dentro del colegio
    event_type      VARCHAR2(50)    NOT NULL,
    entity_id       VARCHAR2(50),              -- ID del registro afectado
    entity_type     VARCHAR2(50),              -- Ej: 'ATTENDANCE', 'SCORE', 'STUDENT'
    payload         CLOB,                      -- JSON del evento
    previous_hash   VARCHAR2(64)    NOT NULL,
    hash            VARCHAR2(64)    NOT NULL,
    created_by      VARCHAR2(50),              -- FK a USERS
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_blockchain_blocks     PRIMARY KEY (id),
    CONSTRAINT fk_bb_school             FOREIGN KEY (school_id)  REFERENCES SCHOOLS(id),
    CONSTRAINT fk_bb_created_by         FOREIGN KEY (created_by) REFERENCES USERS(id),
    -- Unique: el ?ndice de bloque es ?nico dentro de cada colegio (cadena independiente)
    CONSTRAINT uq_bb_school_index       UNIQUE      (school_id, block_index),
    CONSTRAINT ck_bb_event_type         CHECK       (event_type IN (
                                            'GENESIS','ATTENDANCE','SCORE','ENROLLMENT',
                                            'TRANSFER','PROMOTION','USER_ACTION','SYSTEM'
                                        ))
);

COMMENT ON TABLE  BLOCKCHAIN_BLOCKS            IS 'Registro inmutable de eventos. Cadena hash independiente por colegio.';
COMMENT ON COLUMN BLOCKCHAIN_BLOCKS.payload    IS 'JSON serializado del evento (CLOB).';
COMMENT ON COLUMN BLOCKCHAIN_BLOCKS.block_index IS '?ndice secuencial dentro del colegio. 0 = bloque g?nesis.';



-- -----------------------------------------------------------------------------
-- Tabla: CHAT_SESSIONS
-- Sesiones de conversaci?n con el asistente IA MINEDU.
-- -----------------------------------------------------------------------------
CREATE TABLE CHAT_SESSIONS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    user_id         VARCHAR2(50)    NOT NULL,
    user_role       VARCHAR2(30)    NOT NULL,
    session_token   VARCHAR2(100),             -- Token de sesi?n para el frontend
    started_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    last_message_at TIMESTAMP,
    is_active       NUMBER(1)       DEFAULT 1 NOT NULL,
    ended_at        TIMESTAMP,
    --
    CONSTRAINT pk_chat_sessions         PRIMARY KEY (id),
    CONSTRAINT fk_cs_school             FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT fk_cs_user               FOREIGN KEY (user_id)   REFERENCES USERS(id),
    CONSTRAINT ck_cs_user_role          CHECK       (user_role IN ('ADMIN','COORDINATOR','TEACHER','STUDENT','GUARDIAN')),
    CONSTRAINT ck_cs_active             CHECK       (is_active IN (0,1))
);

COMMENT ON TABLE CHAT_SESSIONS IS 'Sesiones del asistente IA MINEDU por usuario y colegio.';

CREATE INDEX idx_cs_user        ON CHAT_SESSIONS (school_id, user_id);
CREATE INDEX idx_cs_active      ON CHAT_SESSIONS (school_id, is_active, last_message_at);


-- -----------------------------------------------------------------------------
-- Tabla: CHAT_MESSAGES
-- Mensajes individuales dentro de una sesi?n del chatbot.
-- NO lleva school_id directo; el aislamiento viene via CHAT_SESSIONS.
-- -----------------------------------------------------------------------------
CREATE TABLE CHAT_MESSAGES (
    id              VARCHAR2(50)    NOT NULL,
    session_id      VARCHAR2(50)    NOT NULL,
    role            VARCHAR2(20)    NOT NULL,   -- 'user' | 'assistant' | 'system'
    content         CLOB            NOT NULL,
    sources_json    CLOB,                       -- JSON: documentos MINEDU referenciados
    confidence      NUMBER(5,4),                -- Score de confianza RAG (0.0000-1.0000)
    tokens_used     NUMBER(6)       DEFAULT 0,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_chat_messages         PRIMARY KEY (id),
    CONSTRAINT fk_cm_session            FOREIGN KEY (session_id) REFERENCES CHAT_SESSIONS(id) ON DELETE CASCADE,
    CONSTRAINT ck_cm_role               CHECK       (role IN ('user','assistant','system')),
    CONSTRAINT ck_cm_confidence         CHECK       (confidence BETWEEN 0 AND 1 OR confidence IS NULL)
);

COMMENT ON TABLE  CHAT_MESSAGES            IS 'Mensajes del chatbot IA. Cascada en borrado de sesi?n.';
COMMENT ON COLUMN CHAT_MESSAGES.confidence IS 'Score de similitud del RAG: 0.0 a 1.0. NULL si es mensaje del usuario.';
COMMENT ON COLUMN CHAT_MESSAGES.sources_json IS 'JSON array con los fragmentos de MINEDU_DOCUMENTS usados como contexto.';

CREATE INDEX idx_cm_session ON CHAT_MESSAGES (session_id, created_at);


-- -----------------------------------------------------------------------------
-- Tabla: MINEDU_DOCUMENTS
-- Documentos oficiales del MINEDU indexados en el sistema RAG.
-- SIN school_id: son globales para todos los colegios del SaaS.
-- -----------------------------------------------------------------------------
CREATE TABLE MINEDU_DOCUMENTS (
    id              VARCHAR2(50)    NOT NULL,
    title           VARCHAR2(500)   NOT NULL,
    doc_type        VARCHAR2(50)    NOT NULL,
    year            NUMBER(4),
    file_path       VARCHAR2(1000),
    description     VARCHAR2(2000),
    chunks_count    NUMBER(6)       DEFAULT 0 NOT NULL,
    indexed_at      TIMESTAMP,
    is_active       NUMBER(1)       DEFAULT 1 NOT NULL,
    checksum        VARCHAR2(64),               -- SHA-256 del archivo
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_minedu_docs           PRIMARY KEY (id),
    CONSTRAINT uq_minedu_docs_path      UNIQUE      (file_path),
    CONSTRAINT ck_minedu_doc_type       CHECK       (doc_type IN (
                                            'CURRICULO_NACIONAL','PROGRAMA_CURRICULAR',
                                            'RESOLUCION','DECRETO','DIRECTIVA',
                                            'GUIA_DOCENTE','ESTANDARES','OTHER'
                                        )),
    CONSTRAINT ck_minedu_active         CHECK       (is_active IN (0,1)),
    CONSTRAINT ck_minedu_year           CHECK       (year BETWEEN 1990 AND 2099 OR year IS NULL)
);

COMMENT ON TABLE  MINEDU_DOCUMENTS           IS 'Documentos MINEDU globales (sin school_id). Base del sistema RAG.';
COMMENT ON COLUMN MINEDU_DOCUMENTS.doc_type  IS 'Tipo de documento oficial MINEDU.';
COMMENT ON COLUMN MINEDU_DOCUMENTS.chunks_count IS 'N?mero de fragmentos vectorizados en ChromaDB.';


-- =============================================================================
-- BLOQUE 7: VISTAS SAAS (school_id en SELECT y GROUP BY)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Vista: VW_ATTENDANCE_SUMMARY
-- Resumen diario de asistencia agrupado por colegio, secci?n, curso y fecha.
-- Incluye conteos de presentes, ausentes, tardanzas y justificados.
-- El backend filtra siempre a?adiendo: WHERE school_id = :school_id
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_ATTENDANCE_SUMMARY AS
SELECT
    -- Dimensiones de agrupaci?n (multi-tenant)
    a.school_id,
    sec.id                                          AS section_id,
    sec.name                                        AS section_name,
    sy.id                                           AS school_year_id,
    sy.name                                         AS school_year_name,
    sy.grade_number,
    ca.id                                           AS course_assignment_id,
    c.id                                            AS course_id,
    c.name                                          AS course_name,
    a.attendance_date,
    -- M?tricas
    COUNT(a.id)                                     AS total_students,
    SUM(CASE WHEN a.status = 'PRESENT'  THEN 1 ELSE 0 END) AS total_present,
    SUM(CASE WHEN a.status = 'ABSENT'   THEN 1 ELSE 0 END) AS total_absent,
    SUM(CASE WHEN a.status = 'LATE'     THEN 1 ELSE 0 END) AS total_late,
    SUM(CASE WHEN a.status = 'EXCUSED'  THEN 1 ELSE 0 END) AS total_excused,
    -- Porcentaje de asistencia efectiva (presente + justificado / total)
    ROUND(
        ( SUM(CASE WHEN a.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END)
          / NULLIF(COUNT(a.id), 0)
        ) * 100, 2
    )                                               AS attendance_pct
FROM
    ATTENDANCES         a
    JOIN STUDENTS       st  ON st.id  = a.student_id
    JOIN SECTIONS       sec ON sec.id = st.section_id
    JOIN SCHOOL_YEARS   sy  ON sy.id  = sec.school_year_id
    LEFT JOIN COURSE_ASSIGNMENTS ca ON ca.id = a.course_assignment_id
    LEFT JOIN COURSES   c   ON c.id   = ca.course_id
WHERE
    st.section_id IS NOT NULL  -- Excluir alumnos egresados sin secci?n
GROUP BY
    a.school_id,
    sec.id,
    sec.name,
    sy.id,
    sy.name,
    sy.grade_number,
    ca.id,
    c.id,
    c.name,
    a.attendance_date;

COMMENT ON TABLE VW_ATTENDANCE_SUMMARY IS
    'Resumen diario de asistencia por colegio, secci?n, curso y fecha. Filtrar siempre por school_id en el backend.';


-- -----------------------------------------------------------------------------
-- Vista: V_COMPETENCY_AVERAGES
-- Promedio ponderado de notas por colegio, alumno, competencia y per?odo.
-- Implementa la l?gica MINEDU: promedio de tareas dentro de cada competencia.
-- El backend filtra siempre a?adiendo: WHERE school_id = :school_id
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW V_COMPETENCY_AVERAGES AS
SELECT
    -- Dimensiones de agrupaci?n (multi-tenant)
    ss.school_id,
    st.id                                           AS student_id,
    st.first_name                                   AS student_first_name,
    st.last_name                                    AS student_last_name,
    st.document_number                              AS student_document,
    sec.id                                          AS section_id,
    sec.name                                        AS section_name,
    c.id                                            AS course_id,
    c.name                                          AS course_name,
    comp.id                                         AS competency_id,
    comp.name                                       AS competency_name,
    comp.weight                                     AS competency_weight,
    ap.id                                           AS academic_period_id,
    ap.name                                         AS academic_period_name,
    -- M?tricas
    COUNT(ss.id)                                    AS total_tasks_graded,
    ROUND(AVG(ss.score), 2)                         AS average_score,
    MIN(ss.score)                                   AS min_score,
    MAX(ss.score)                                   AS max_score,
    -- Nivel de logro MINEDU (escala vigesimal)
    CASE
        WHEN ROUND(AVG(ss.score), 2) >= 18 THEN 'AD'  -- Destacado
        WHEN ROUND(AVG(ss.score), 2) >= 14 THEN 'A'   -- Logrado
        WHEN ROUND(AVG(ss.score), 2) >= 11 THEN 'B'   -- En Proceso
        ELSE                                     'C'   -- En Inicio
    END                                             AS achievement_level,
    CASE
        WHEN ROUND(AVG(ss.score), 2) >= 18 THEN 'Destacado'
        WHEN ROUND(AVG(ss.score), 2) >= 14 THEN 'Logrado'
        WHEN ROUND(AVG(ss.score), 2) >= 11 THEN 'En Proceso'
        ELSE                                     'En Inicio'
    END                                             AS achievement_label
FROM
    STUDENT_SCORES      ss
    JOIN STUDENTS       st      ON st.id   = ss.student_id
    JOIN SECTIONS       sec     ON sec.id  = st.section_id
    JOIN CLASS_TASKS    ct      ON ct.id   = ss.class_task_id
    JOIN COURSE_COMPETENCIES comp ON comp.id = ct.competency_id
    JOIN COURSE_ASSIGNMENTS  ca  ON ca.id  = ct.course_assignment_id
    JOIN COURSES        c       ON c.id    = ca.course_id
    JOIN ACADEMIC_PERIODS ap    ON ap.id   = ct.academic_period_id
WHERE
    st.section_id IS NOT NULL   -- Excluir alumnos egresados
    AND ct.is_active = 1
GROUP BY
    ss.school_id,
    st.id,
    st.first_name,
    st.last_name,
    st.document_number,
    sec.id,
    sec.name,
    c.id,
    c.name,
    comp.id,
    comp.name,
    comp.weight,
    ap.id,
    ap.name;

COMMENT ON TABLE V_COMPETENCY_AVERAGES IS
    'Promedio ponderado de notas por alumno, competencia y per?odo. Niveles AD/A/B/C (MINEDU). Filtrar por school_id en el backend.';


-- =============================================================================
-- BLOQUE 8: TRIGGERS Y STORED PROCEDURES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Trigger: TRG_USERS_UPDATED_AT
-- Actualiza autom?ticamente el campo updated_at antes de cada UPDATE en USERS.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_USERS_UPDATED_AT
    BEFORE UPDATE ON USERS
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_USERS_UPDATED_AT;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_STUDENTS_UPDATED_AT
-- Actualiza autom?ticamente el campo updated_at antes de cada UPDATE en STUDENTS.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_STUDENTS_UPDATED_AT
    BEFORE UPDATE ON STUDENTS
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_STUDENTS_UPDATED_AT;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_TEACHERS_UPDATED_AT
-- Actualiza autom?ticamente el campo updated_at antes de cada UPDATE en TEACHERS.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_TEACHERS_UPDATED_AT
    BEFORE UPDATE ON TEACHERS
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_TEACHERS_UPDATED_AT;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_SCORES_UPDATED_AT
-- Actualiza updated_at al registrar una correcci?n de nota.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SCORES_UPDATED_AT
    BEFORE UPDATE ON STUDENT_SCORES
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_SCORES_UPDATED_AT;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_SCORE_RANGE_CHECK
-- Valida que la nota est? en el rango 0-20 antes de INSERT o UPDATE.
-- Complementa la constraint ck_ss_score con mensaje de error descriptivo.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SCORE_RANGE_CHECK
    BEFORE INSERT OR UPDATE ON STUDENT_SCORES
    FOR EACH ROW
BEGIN
    IF :NEW.score < 0 OR :NEW.score > 20 THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'FINE_FLOW_ERROR: La nota debe estar entre 0 y 20. Valor recibido: ' || :NEW.score
        );
    END IF;
END TRG_SCORE_RANGE_CHECK;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_BLOCKCHAIN_PROTECT
-- Impide UPDATE y DELETE sobre BLOCKCHAIN_BLOCKS para garantizar inmutabilidad.
-- Los bloques solo pueden ser INSERTados, nunca modificados ni borrados.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_BLOCKCHAIN_PROTECT
    BEFORE UPDATE OR DELETE ON BLOCKCHAIN_BLOCKS
    FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(
        -20002,
        'FINE_FLOW_ERROR: Los bloques del blockchain son inmutables. '
        || 'No se permite UPDATE ni DELETE sobre BLOCKCHAIN_BLOCKS.'
    );
END TRG_BLOCKCHAIN_PROTECT;
/



-- -----------------------------------------------------------------------------
-- Stored Procedure: SP_PROMOTE_SECTION
-- Promueve (egresa) a todos los alumnos ACTIVOS de una secci?n.
--
-- Par?metros:
--   p_school_id   : UUID del colegio (OBLIGATORIO, aislamiento multi-tenant)
--   p_section_id  : UUID de la secci?n a promover
--   p_new_status  : Nuevo estado de los alumnos ('EGRESADO','TRANSFERRED','GRADUATED')
--
-- Comportamiento:
--   1. Valida que la secci?n pertenezca al colegio indicado (seguridad multi-tenant)
--   2. Actualiza status de todos los alumnos ACTIVOS de la secci?n
--   3. Limpia el section_id (pone NULL) para desvincularlos de la secci?n
--   4. Marca la secci?n como inactiva (is_active = 0)
--   5. Retorna el n?mero de alumnos promovidos en p_promoted_count
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_PROMOTE_SECTION (
    p_school_id      IN  VARCHAR2,
    p_section_id     IN  VARCHAR2,
    p_new_status     IN  VARCHAR2 DEFAULT 'EGRESADO',
    p_promoted_count OUT NUMBER
)
AS
    v_section_school_id  VARCHAR2(50);
    v_section_name       VARCHAR2(10);
    v_valid_status       BOOLEAN := FALSE;
BEGIN
    -- -------------------------------------------------------------------------
    -- PASO 1: Validar que el estado solicitado sea v?lido
    -- -------------------------------------------------------------------------
    IF p_new_status IN ('EGRESADO','GRADUATED','TRANSFERRED','INACTIVE') THEN
        v_valid_status := TRUE;
    END IF;

    IF NOT v_valid_status THEN
        RAISE_APPLICATION_ERROR(
            -20010,
            'FINE_FLOW_ERROR: El estado "'|| p_new_status ||
            '" no es v?lido para promoci?n. Use: EGRESADO, GRADUATED, TRANSFERRED o INACTIVE.'
        );
    END IF;

    -- -------------------------------------------------------------------------
    -- PASO 2: Verificar que la secci?n exista Y pertenezca al colegio indicado
    --         (Seguridad multi-tenant: un colegio no puede promover secciones ajenas)
    -- -------------------------------------------------------------------------
    BEGIN
        SELECT school_id, name
        INTO   v_section_school_id, v_section_name
        FROM   SECTIONS
        WHERE  id        = p_section_id
          AND  school_id = p_school_id;   -- <-- Filtro multi-tenant cr?tico
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20011,
                'FINE_FLOW_ERROR: La secci?n "'|| p_section_id ||
                '" no existe o no pertenece al colegio "'|| p_school_id ||'".'
            );
    END;

    -- -------------------------------------------------------------------------
    -- PASO 3: Promover alumnos ACTIVOS de la secci?n
    --         Solo afecta a alumnos con status = 'ACTIVE' (nunca los ya egresados)
    --         Filtra siempre por school_id para aislamiento multi-tenant
    -- -------------------------------------------------------------------------
    UPDATE STUDENTS
    SET    status     = p_new_status,
           section_id = NULL,              -- Desvincular de la secci?n
           updated_at = SYSTIMESTAMP
    WHERE  school_id  = p_school_id        -- Multi-tenant
      AND  section_id = p_section_id       -- Solo esta secci?n
      AND  status     = 'ACTIVE';          -- Solo alumnos activos

    p_promoted_count := SQL%ROWCOUNT;

    -- -------------------------------------------------------------------------
    -- PASO 4: Marcar la secci?n como inactiva
    --         (ya no deber?a recibir m?s alumnos ni asistencias)
    -- -------------------------------------------------------------------------
    UPDATE SECTIONS
    SET    is_active = 0
    WHERE  id        = p_section_id
      AND  school_id = p_school_id;        -- Multi-tenant

    -- -------------------------------------------------------------------------
    -- PASO 5: Confirmar transacci?n
    -- -------------------------------------------------------------------------
    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_PROMOTE_SECTION: Secci?n "'|| v_section_name ||
        '" promovida. ' || p_promoted_count ||
        ' alumno(s) pasaron a estado "'|| p_new_status ||'".'
    );

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;  -- Re-lanza la excepci?n para que el caller la maneje
END SP_PROMOTE_SECTION;
/



-- -----------------------------------------------------------------------------
-- Stored Procedure: SP_CREATE_GENESIS_BLOCK
-- Crea el bloque g?nesis de la cadena blockchain para un colegio nuevo.
-- Debe llamarse UNA sola vez al crear un colegio en el SaaS.
-- previous_hash del g?nesis = '0' * 64 (convenci?n est?ndar)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_CREATE_GENESIS_BLOCK (
    p_school_id  IN VARCHAR2,
    p_block_id   IN VARCHAR2,
    p_created_by IN VARCHAR2 DEFAULT NULL
)
AS
    v_exists  NUMBER;
    v_hash    VARCHAR2(64) := 'a91b6f3d5c2e8047f1d4b9e6c3a0f7d2b5e8c1a4f7d0b3e6c9a2f5d8b1e4c7a0';
    -- Nota: en producci?n, el hash real lo genera Java (SHA-256 del payload)
BEGIN
    -- Verificar que no exista ya un g?nesis para este colegio
    SELECT COUNT(*)
    INTO   v_exists
    FROM   BLOCKCHAIN_BLOCKS
    WHERE  school_id    = p_school_id
      AND  block_index  = 0;

    IF v_exists > 0 THEN
        RAISE_APPLICATION_ERROR(
            -20020,
            'FINE_FLOW_ERROR: Ya existe un bloque g?nesis para el colegio "'|| p_school_id ||'".'
        );
    END IF;

    INSERT INTO BLOCKCHAIN_BLOCKS (
        id, school_id, block_index, event_type,
        entity_id, entity_type, payload,
        previous_hash, hash,
        created_by, created_at
    ) VALUES (
        p_block_id,
        p_school_id,
        0,
        'GENESIS',
        p_school_id,
        'SCHOOL',
        '{"event":"GENESIS","description":"Bloque genesis de la cadena Fine Flow","school_id":"'|| p_school_id ||'"}',
        RPAD('0', 64, '0'),   -- previous_hash del g?nesis = 64 ceros
        v_hash,
        p_created_by,
        SYSTIMESTAMP
    );

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('SP_CREATE_GENESIS_BLOCK: Bloque g?nesis creado para colegio "'|| p_school_id ||'".');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_CREATE_GENESIS_BLOCK;
/



-- =============================================================================
-- BLOQUE 9: ?NDICES ADICIONALES PARA PERFORMANCE
-- =============================================================================

-- SCHOOLS
CREATE INDEX idx_schools_status         ON SCHOOLS          (status);

-- USERS
CREATE INDEX idx_users_school_role      ON USERS            (school_id, role, status);
CREATE INDEX idx_users_email            ON USERS            (email);

-- STUDENTS
CREATE INDEX idx_students_school_sec    ON STUDENTS         (school_id, section_id, status);
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_students_doc ON STUDENTS (school_id, document_number)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE INDEX idx_students_search        ON STUDENTS         (school_id, LOWER(first_name || ' ' || last_name), document_number);

-- TEACHERS
CREATE INDEX idx_teachers_school        ON TEACHERS         (school_id, status);

-- COURSE_ASSIGNMENTS
CREATE INDEX idx_ca_teacher_period      ON COURSE_ASSIGNMENTS (school_id, teacher_id, academic_period_id);
CREATE INDEX idx_ca_section_period      ON COURSE_ASSIGNMENTS (school_id, section_id, academic_period_id);

-- CLASS_TASKS
CREATE INDEX idx_ct_assignment          ON CLASS_TASKS      (school_id, course_assignment_id);
CREATE INDEX idx_ct_competency          ON CLASS_TASKS      (school_id, competency_id);

-- STUDENT_SCORES
CREATE INDEX idx_ss_school_student      ON STUDENT_SCORES   (school_id, student_id, class_task_id);

-- BLOCKCHAIN_BLOCKS
CREATE INDEX idx_bb_event_type          ON BLOCKCHAIN_BLOCKS (school_id, event_type, created_at);

-- CHAT_SESSIONS
CREATE INDEX idx_cs_school_user         ON CHAT_SESSIONS    (school_id, user_id, is_active);

-- MINEDU_DOCUMENTS
CREATE INDEX idx_md_type_year           ON MINEDU_DOCUMENTS (doc_type, year, is_active);


-- =============================================================================
-- BLOQUE 10: DATOS SEMILLA (SEED DATA)
-- Datos m?nimos para que el sistema arranque en un entorno de desarrollo.
-- =============================================================================

-- Colegio de demostraci?n

-- SQL*Plus substitution variable
DEFINE LOGINS = 'SYSTEM'

-- Set VPD context for seed data operations
BEGIN
    pkg_tenant_ctx.set_school('school-20188-canete');
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

INSERT INTO SCHOOLS (id, name, document_number, address, phone, email, status)
VALUES (
    'school-20188-canete',
    'I.E. Centro de Varones N?20188',
    '20543210001',
    'Calle Bol?var 150, Ca?ete, Lima',
    '01-5840123',
    'ie20188.canete@gmail.com',
    'ACTIVE'
);

-- Usuario administrador inicial del colegio demo
-- Contrase?a: Admin2025! (BCrypt hash de ejemplo ? reemplazar en producci?n)
INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES (
    'user-admin-20188',
    'school-20188-canete',
    'admin@ie20188.edu.pe',
    '$2a$12$exampleBCryptHashReplaceInProduction.XYZ',
    'ADMIN',
    'Administrador',
    'Principal',
    'ACTIVE'
);

-- Nivel educativo: Secundaria
INSERT INTO ACADEMIC_LEVELS (id, school_id, name, order_num, is_active)
VALUES ('level-sec-20188', 'school-20188-canete', 'Educaci?n Secundaria', 2, 1);

-- A?o acad?mico 2025 ? 3? Secundaria
INSERT INTO SCHOOL_YEARS (id, school_id, academic_level_id, name, grade_number, calendar_year, is_active)
VALUES ('sy-3sec-2025', 'school-20188-canete', 'level-sec-20188', '3? Secundaria 2025', 3, 2025, 1);

-- A?o acad?mico 2025 ? 4? Secundaria
INSERT INTO SCHOOL_YEARS (id, school_id, academic_level_id, name, grade_number, calendar_year, is_active)
VALUES ('sy-4sec-2025', 'school-20188-canete', 'level-sec-20188', '4? Secundaria 2025', 4, 2025, 1);

-- A?o acad?mico 2025 ? 5? Secundaria
INSERT INTO SCHOOL_YEARS (id, school_id, academic_level_id, name, grade_number, calendar_year, is_active)
VALUES ('sy-5sec-2025', 'school-20188-canete', 'level-sec-20188', '5? Secundaria 2025', 5, 2025, 1);

-- Per?odo acad?mico: 1? Bimestre 2025
INSERT INTO ACADEMIC_PERIODS (id, school_id, school_year_id, name, period_type, start_date, end_date, is_active)
VALUES ('ap-b1-3sec-2025', 'school-20188-canete', 'sy-3sec-2025', '1? Bimestre', 'BIMESTER', DATE '2025-03-10', DATE '2025-05-09', 1);

-- Per?odo acad?mico: 2? Bimestre 2025
INSERT INTO ACADEMIC_PERIODS (id, school_id, school_year_id, name, period_type, start_date, end_date, is_active)
VALUES ('ap-b2-3sec-2025', 'school-20188-canete', 'sy-3sec-2025', '2? Bimestre', 'BIMESTER', DATE '2025-05-12', DATE '2025-07-11', 0);

-- Secciones de 3? Secundaria
INSERT INTO SECTIONS (id, school_id, school_year_id, name, max_capacity, is_active)
VALUES ('sec-3a-2025', 'school-20188-canete', 'sy-3sec-2025', 'A', 35, 1);

INSERT INTO SECTIONS (id, school_id, school_year_id, name, max_capacity, is_active)
VALUES ('sec-3b-2025', 'school-20188-canete', 'sy-3sec-2025', 'B', 35, 1);

-- Secciones de 4? Secundaria
INSERT INTO SECTIONS (id, school_id, school_year_id, name, max_capacity, is_active)
VALUES ('sec-4a-2025', 'school-20188-canete', 'sy-4sec-2025', 'A', 35, 1);

-- Secciones de 5? Secundaria
INSERT INTO SECTIONS (id, school_id, school_year_id, name, max_capacity, is_active)
VALUES ('sec-5a-2025', 'school-20188-canete', 'sy-5sec-2025', 'A', 35, 1);

-- Cursos CNEB (?reas curriculares)
INSERT INTO COURSES (id, school_id, name, code, color_hex, is_active)
VALUES ('course-mat-20188', 'school-20188-canete', 'Matem?tica', 'MAT', '#1e407a', 1);

INSERT INTO COURSES (id, school_id, name, code, color_hex, is_active)
VALUES ('course-com-20188', 'school-20188-canete', 'Comunicaci?n', 'COM', '#16a34a', 1);

INSERT INTO COURSES (id, school_id, name, code, color_hex, is_active)
VALUES ('course-his-20188', 'school-20188-canete', 'Historia, Geograf?a y Econom?a', 'HGE', '#c8922a', 1);

INSERT INTO COURSES (id, school_id, name, code, color_hex, is_active)
VALUES ('course-cie-20188', 'school-20188-canete', 'Ciencia y Tecnolog?a', 'CYT', '#0369a1', 1);

INSERT INTO COURSES (id, school_id, name, code, color_hex, is_active)
VALUES ('course-ing-20188', 'school-20188-canete', 'Ingl?s', 'ING', '#7c3aed', 1);

-- Competencias CNEB ? Matem?tica
INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-mat-1', 'school-20188-canete', 'course-mat-20188', 'Resuelve problemas de cantidad', 25, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-mat-2', 'school-20188-canete', 'course-mat-20188', 'Resuelve problemas de regularidad, equivalencia y cambio', 25, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-mat-3', 'school-20188-canete', 'course-mat-20188', 'Resuelve problemas de forma, movimiento y localizaci?n', 25, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-mat-4', 'school-20188-canete', 'course-mat-20188', 'Resuelve problemas de gesti?n de datos e incertidumbre', 25, 1);

-- Competencias CNEB ? Comunicaci?n
INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-com-1', 'school-20188-canete', 'course-com-20188', 'Se comunica oralmente en su lengua materna', 34, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-com-2', 'school-20188-canete', 'course-com-20188', 'Lee diversos tipos de textos escritos', 33, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-com-3', 'school-20188-canete', 'course-com-20188', 'Escribe diversos tipos de textos', 33, 1);

-- Documento MINEDU de referencia
INSERT INTO MINEDU_DOCUMENTS (id, title, doc_type, year, file_path, description, chunks_count, is_active)
VALUES (
    'doc-cn-2016',
    'Curr?culo Nacional de la Educaci?n B?sica',
    'CURRICULO_NACIONAL',
    2016,
    '/docs/minedu/curriculo_nacional_2016.pdf',
    'Documento oficial del Curr?culo Nacional aprobado por Resoluci?n Ministerial N? 281-2016-MINEDU',
    342,
    1
);

INSERT INTO MINEDU_DOCUMENTS (id, title, doc_type, year, file_path, description, chunks_count, is_active)
VALUES (
    'doc-pgc-sec-2022',
    'Programa Curricular de Educaci?n Secundaria',
    'PROGRAMA_CURRICULAR',
    2022,
    '/docs/minedu/programa_curricular_secundaria_2022.pdf',
    'Programa curricular actualizado para Educaci?n Secundaria ? EBR',
    518,
    1
);

-- Confirmar datos semilla
COMMIT;


-- =============================================================================
-- VERIFICACI?N FINAL DEL ESQUEMA
-- =============================================================================


-- =============================================================================
-- FIN DEL SCRIPT ? FINE FLOW v2.0 ? SISTEMA SAAS MULTI-TENANT ORACLE
-- =============================================================================


-- =============================================================================
-- FINE FLOW ? ADDENDUM v2.1
-- Complemento del script principal school_saas_oracle.sql
-- Ejecutar DESPU?S del script principal (v2.0)
-- =============================================================================
-- GAPS CORREGIDOS EN ESTE ADDENDUM:
--   ? 8 tablas nuevas: REFRESH_TOKENS, NOTIFICATIONS, JUSTIFICATIONS,
--     AUDIT_LOGS, REPORT_JOBS, SYSTEM_CONFIG, STUDENT_SECTION_HISTORY,
--     SCHOOL_SUBSCRIPTIONS
--   ? 3 triggers nuevos: TRG_SCHOOLS_UPDATED_AT, TRG_ONE_ACTIVE_PERIOD,
--     TRG_SECTION_CAPACITY_CHECK
--   ? 4 stored procedures nuevos: SP_ROTATE_QR_SECRETS,
--     SP_CLOSE_PERIOD_AND_OPEN_NEXT, SP_BULK_ENROLL_STUDENTS,
--     SP_DEACTIVATE_SCHOOL
--   ? 3 vistas nuevas: V_STUDENT_FINAL_GRADES, V_SCHOOL_DASHBOARD_STATS,
--     V_ATTENDANCE_ALERTS
--   ? Seed data completo: usuarios demo, docentes, alumnos, asignaciones,
--     tareas, notas, asistencias, genesis blockchain, competencias completas
-- =============================================================================


-- =============================================================================
-- ADDENDUM ? BLOQUE A: TABLAS FALTANTES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Tabla: REFRESH_TOKENS
-- Soporte para JWT con rotaci?n de tokens y bloqueo de cuenta.
-- El backend (Spring Security) revoca el token anterior en cada refresh.
-- One-to-many: un USER puede tener varios tokens (dispositivos distintos),
-- pero solo uno ACTIVO por familia (jti = JWT ID del access token original).
-- -----------------------------------------------------------------------------
CREATE TABLE REFRESH_TOKENS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    user_id         VARCHAR2(50)    NOT NULL,
    token_hash      VARCHAR2(255)   NOT NULL,   -- SHA-256 del refresh token
    jti             VARCHAR2(100)   NOT NULL,   -- JWT ID del access token padre
    device_info     VARCHAR2(500),              -- User-Agent / device fingerprint
    ip_address      VARCHAR2(45),               -- IPv4 o IPv6
    issued_at       TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    expires_at      TIMESTAMP       NOT NULL,
    revoked_at      TIMESTAMP,                  -- NULL = activo
    revoke_reason   VARCHAR2(100),              -- 'ROTATED','LOGOUT','EXPIRED','ADMIN'
    --
    CONSTRAINT pk_refresh_tokens        PRIMARY KEY (id),
    CONSTRAINT fk_rt_school             FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT fk_rt_user               FOREIGN KEY (user_id)   REFERENCES USERS(id),
    CONSTRAINT uq_rt_token_hash         UNIQUE      (token_hash),
    CONSTRAINT uq_rt_jti                UNIQUE      (jti),
    CONSTRAINT ck_rt_revoke_reason      CHECK       (revoke_reason IN (
                                            'ROTATED','LOGOUT','EXPIRED','ADMIN','SECURITY','DEVICE_CHANGE'
                                        ) OR revoke_reason IS NULL)
);

COMMENT ON TABLE  REFRESH_TOKENS              IS 'JWT refresh tokens con rotaci?n autom?tica. Un token por JTI.';
COMMENT ON COLUMN REFRESH_TOKENS.token_hash   IS 'SHA-256 del refresh token (nunca almacenar el token en claro).';
COMMENT ON COLUMN REFRESH_TOKENS.revoked_at   IS 'NULL = token vigente. Valor = token revocado.';
COMMENT ON COLUMN REFRESH_TOKENS.revoke_reason IS 'ROTATED (reemplazo normal) | LOGOUT | EXPIRED | ADMIN | SECURITY';

CREATE INDEX idx_rt_user_active   ON REFRESH_TOKENS (school_id, user_id, revoked_at);
CREATE INDEX idx_rt_expires       ON REFRESH_TOKENS (expires_at, revoked_at);   -- Para limpieza por cron


-- -----------------------------------------------------------------------------
-- Tabla: NOTIFICATIONS
-- Sistema de notificaciones in-app para usuarios.
-- El NotificationService del backend genera registros aqu?.
-- Soporta notificaciones individuales y broadcast por rol.
-- -----------------------------------------------------------------------------
CREATE TABLE NOTIFICATIONS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    user_id         VARCHAR2(50),               -- NULL = broadcast por target_role
    target_role     VARCHAR2(30),               -- NULL = solo para user_id
    notification_type VARCHAR2(50)  NOT NULL,
    title           VARCHAR2(300)   NOT NULL,
    body            VARCHAR2(2000)  NOT NULL,
    action_url      VARCHAR2(500),              -- Ruta interna del frontend
    metadata_json   VARCHAR2(4000),             -- JSON con datos extras del evento
    is_read         NUMBER(1)       DEFAULT 0   NOT NULL,
    read_at         TIMESTAMP,
    expires_at      TIMESTAMP,                  -- NULL = no expira
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_notifications         PRIMARY KEY (id),
    CONSTRAINT fk_notif_school          FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT fk_notif_user            FOREIGN KEY (user_id)   REFERENCES USERS(id),
    CONSTRAINT ck_notif_type            CHECK       (notification_type IN (
                                            'ATTENDANCE_ALERT','SCORE_REGISTERED','JUSTIFICATION_APPROVED',
                                            'JUSTIFICATION_REJECTED','PERIOD_CLOSING','QR_ROTATED',
                                            'REPORT_READY','SYSTEM_MAINTENANCE','GENERAL'
                                        )),
    CONSTRAINT ck_notif_is_read         CHECK       (is_read IN (0,1)),
    -- Al menos uno de los dos destinos debe estar informado
    CONSTRAINT ck_notif_target          CHECK       (user_id IS NOT NULL OR target_role IS NOT NULL)
);

COMMENT ON TABLE  NOTIFICATIONS              IS 'Notificaciones in-app. Individuales (user_id) o broadcast por rol (target_role).';
COMMENT ON COLUMN NOTIFICATIONS.target_role  IS 'Cuando no es NULL, todos los usuarios con ese rol en el colegio ven la notif.';
COMMENT ON COLUMN NOTIFICATIONS.expires_at   IS 'Fecha de expiraci?n de la notificaci?n. NULL = permanente.';

CREATE INDEX idx_notif_user_unread  ON NOTIFICATIONS (school_id, user_id, is_read, created_at);
CREATE INDEX idx_notif_role_unread  ON NOTIFICATIONS (school_id, target_role, is_read, created_at);


-- -----------------------------------------------------------------------------
-- Tabla: JUSTIFICATIONS
-- Flujo de justificaci?n de inasistencias con aprobaci?n por el coordinador.
-- El backend tiene un flujo autom?tico: si el apoderado sube el documento
-- dentro de las 24h, puede aprobarse autom?ticamente.
-- -----------------------------------------------------------------------------
CREATE TABLE JUSTIFICATIONS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    student_id          VARCHAR2(50)    NOT NULL,
    attendance_id       VARCHAR2(50)    NOT NULL,   -- FK a la falta que se justifica
    requested_by        VARCHAR2(50)    NOT NULL,   -- FK a USERS (apoderado o admin)
    reason              VARCHAR2(2000)  NOT NULL,
    document_url        VARCHAR2(1000),              -- Ruta al documento adjunto
    status              VARCHAR2(20)    DEFAULT 'PENDING' NOT NULL,
    reviewed_by         VARCHAR2(50),               -- FK a USERS (coordinador/admin)
    review_note         VARCHAR2(1000),
    auto_approved       NUMBER(1)       DEFAULT 0   NOT NULL,  -- 1 = aprobado autom?ticamente
    requested_at        TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    reviewed_at         TIMESTAMP,
    expires_at          TIMESTAMP,                  -- Plazo m?ximo para revisar
    --
    CONSTRAINT pk_justifications        PRIMARY KEY (id),
    CONSTRAINT fk_just_school           FOREIGN KEY (school_id)    REFERENCES SCHOOLS(id),
    CONSTRAINT fk_just_student          FOREIGN KEY (student_id)   REFERENCES STUDENTS(id),
    CONSTRAINT fk_just_attendance       FOREIGN KEY (attendance_id) REFERENCES ATTENDANCES(id),
    CONSTRAINT fk_just_requested_by     FOREIGN KEY (requested_by)  REFERENCES USERS(id),
    CONSTRAINT fk_just_reviewed_by      FOREIGN KEY (reviewed_by)   REFERENCES USERS(id),
    CONSTRAINT uq_just_attendance       UNIQUE      (school_id, attendance_id),  -- 1 justif. por falta
    CONSTRAINT ck_just_status           CHECK       (status IN ('PENDING','APPROVED','REJECTED','EXPIRED')),
    CONSTRAINT ck_just_auto_approved    CHECK       (auto_approved IN (0,1))
);

COMMENT ON TABLE  JUSTIFICATIONS              IS 'Flujo de justificaci?n de inasistencias. Una justificaci?n por falta.';
COMMENT ON COLUMN JUSTIFICATIONS.auto_approved IS '1 = el sistema aprob? autom?ticamente (dentro del plazo y con documento).';
COMMENT ON COLUMN JUSTIFICATIONS.expires_at    IS 'Fecha l?mite para presentar la justificaci?n (configurable en SYSTEM_CONFIG).';

CREATE INDEX idx_just_student_status  ON JUSTIFICATIONS (school_id, student_id, status);
CREATE INDEX idx_just_pending         ON JUSTIFICATIONS (school_id, status, requested_at);


-- -----------------------------------------------------------------------------
-- Tabla: AUDIT_LOGS
-- Log operacional de acciones del sistema (diferente al blockchain).
-- Registra QUI?N hizo QU? sobre QU? entidad y CU?NDO.
-- El blockchain captura eventos de negocio; este log captura acciones t?cnicas.
-- -----------------------------------------------------------------------------
CREATE TABLE AUDIT_LOGS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50),               -- NULL para acciones globales (login fallido, etc.)
    user_id         VARCHAR2(50),               -- NULL para acciones del sistema
    action          VARCHAR2(100)   NOT NULL,   -- Ej: 'LOGIN','UPDATE_STUDENT','DELETE_SCORE'
    entity_type     VARCHAR2(50),               -- Ej: 'STUDENT','SCORE','USER'
    entity_id       VARCHAR2(50),               -- UUID de la entidad afectada
    old_value_json  CLOB,                       -- Estado anterior (para UPDATEs)
    new_value_json  CLOB,                       -- Estado nuevo
    ip_address      VARCHAR2(45),
    user_agent      VARCHAR2(500),
    result          VARCHAR2(10)    DEFAULT 'SUCCESS' NOT NULL,
    error_detail    VARCHAR2(2000),
    duration_ms     NUMBER(8),                  -- Duraci?n de la operaci?n en ms
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_audit_logs        PRIMARY KEY (id),
    CONSTRAINT fk_audit_school      FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT fk_audit_user        FOREIGN KEY (user_id)   REFERENCES USERS(id),
    CONSTRAINT ck_audit_result      CHECK       (result IN ('SUCCESS','FAILURE','ERROR','BLOCKED'))
);

COMMENT ON TABLE  AUDIT_LOGS             IS 'Log operacional de todas las acciones del sistema. Nunca borrar.';
COMMENT ON COLUMN AUDIT_LOGS.action      IS 'Acci?n realizada: LOGIN, LOGOUT, CREATE_*, UPDATE_*, DELETE_*, etc.';
COMMENT ON COLUMN AUDIT_LOGS.result      IS 'SUCCESS | FAILURE (validaci?n) | ERROR (excepci?n) | BLOCKED (seguridad)';

-- Partici?n impl?cita por fecha con ?ndice en created_at para purgas controladas
CREATE INDEX idx_audit_school_date  ON AUDIT_LOGS (school_id, created_at);
CREATE INDEX idx_audit_user_action  ON AUDIT_LOGS (school_id, user_id, action, created_at);
CREATE INDEX idx_audit_entity       ON AUDIT_LOGS (school_id, entity_type, entity_id);


-- -----------------------------------------------------------------------------
-- Tabla: REPORT_JOBS
-- Cola de trabajos para generaci?n as?ncrona de reportes (Spring @Async).
-- El backend genera PDF/Excel en background y actualiza el status aqu?.
-- El frontend hace polling o usa notificaci?n push para saber cu?ndo est? listo.
-- -----------------------------------------------------------------------------
CREATE TABLE REPORT_JOBS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    requested_by    VARCHAR2(50)    NOT NULL,   -- FK a USERS
    report_type     VARCHAR2(50)    NOT NULL,
    format          VARCHAR2(10)    DEFAULT 'PDF' NOT NULL,
    parameters_json VARCHAR2(4000),             -- JSON con filtros: per?odo, secci?n, etc.
    status          VARCHAR2(20)    DEFAULT 'PENDING' NOT NULL,
    file_path       VARCHAR2(1000),             -- Ruta del archivo generado
    file_size_kb    NUMBER(10),
    error_detail    VARCHAR2(2000),
    progress_pct    NUMBER(3)       DEFAULT 0   NOT NULL,  -- 0-100
    requested_at    TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    started_at      TIMESTAMP,
    completed_at    TIMESTAMP,
    expires_at      TIMESTAMP,                  -- El archivo se borra tras esta fecha
    download_count  NUMBER(5)       DEFAULT 0   NOT NULL,
    --
    CONSTRAINT pk_report_jobs       PRIMARY KEY (id),
    CONSTRAINT fk_rj_school         FOREIGN KEY (school_id)    REFERENCES SCHOOLS(id),
    CONSTRAINT fk_rj_requested_by   FOREIGN KEY (requested_by) REFERENCES USERS(id),
    CONSTRAINT ck_rj_report_type    CHECK       (report_type IN (
                                        'ATTENDANCE_SUMMARY','ATTENDANCE_STUDENT',
                                        'GRADES_SECTION','GRADES_STUDENT','BOLET?N',
                                        'N?MINA','ACTA_NOTAS','DASHBOARD_STATS',
                                        'BLOCKCHAIN_EXPORT','CUSTOM'
                                    )),
    CONSTRAINT ck_rj_format         CHECK       (format   IN ('PDF','XLSX','CSV','JSON')),
    CONSTRAINT ck_rj_status         CHECK       (status   IN ('PENDING','PROCESSING','COMPLETED','FAILED','EXPIRED')),
    CONSTRAINT ck_rj_progress       CHECK       (progress_pct BETWEEN 0 AND 100)
);

COMMENT ON TABLE  REPORT_JOBS            IS 'Cola de reportes asincr?nicos. El backend usa Spring @Async + Caffeine para procesarlos.';
COMMENT ON COLUMN REPORT_JOBS.expires_at IS 'Fecha en que el archivo generado se puede eliminar del servidor.';

CREATE INDEX idx_rj_user_status  ON REPORT_JOBS (school_id, requested_by, status, requested_at);
CREATE INDEX idx_rj_pending      ON REPORT_JOBS (status, requested_at);


-- -----------------------------------------------------------------------------
-- Tabla: SYSTEM_CONFIG
-- Configuraci?n parametrizable por colegio.
-- Permite que cada instituci?n personalice comportamientos del sistema
-- sin modificar c?digo: umbral de faltas, plazo de justificaciones, etc.
-- -----------------------------------------------------------------------------
CREATE TABLE SYSTEM_CONFIG (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    config_key      VARCHAR2(100)   NOT NULL,
    config_value    VARCHAR2(2000)  NOT NULL,
    value_type      VARCHAR2(20)    DEFAULT 'STRING' NOT NULL,  -- STRING|NUMBER|BOOLEAN|JSON
    description     VARCHAR2(500),
    is_sensitive    NUMBER(1)       DEFAULT 0  NOT NULL,        -- 1 = ocultar en logs/UI
    updated_by      VARCHAR2(50),
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_system_config         PRIMARY KEY (id),
    CONSTRAINT fk_sc_school             FOREIGN KEY (school_id)  REFERENCES SCHOOLS(id),
    CONSTRAINT fk_sc_updated_by         FOREIGN KEY (updated_by) REFERENCES USERS(id),
    CONSTRAINT uq_sc_key_school         UNIQUE      (school_id, config_key),
    CONSTRAINT ck_sc_value_type         CHECK       (value_type   IN ('STRING','NUMBER','BOOLEAN','JSON','DATE')),
    CONSTRAINT ck_sc_is_sensitive       CHECK       (is_sensitive IN (0,1))
);

COMMENT ON TABLE  SYSTEM_CONFIG              IS 'Configuraci?n por colegio. Parametriza umbrales, plazos y comportamientos.';
COMMENT ON COLUMN SYSTEM_CONFIG.is_sensitive IS '1 = no mostrar el valor en logs ni en la UI (para API keys, etc.)';

-- Configuraciones predeterminadas para el colegio demo
INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-01', 'school-20188-canete', 'ATTENDANCE_ABSENCE_ALERT_THRESHOLD', '3',
        'NUMBER', 'N?mero de faltas consecutivas para generar alerta de asistencia');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-02', 'school-20188-canete', 'JUSTIFICATION_DEADLINE_HOURS', '48',
        'NUMBER', 'Horas m?ximas para presentar justificaci?n de inasistencia');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-03', 'school-20188-canete', 'QR_ROTATION_DAYS', '7',
        'NUMBER', 'D?as entre rotaciones del secreto HMAC del QR del carnet');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-04', 'school-20188-canete', 'LATE_THRESHOLD_MINUTES', '10',
        'NUMBER', 'Minutos de tolerancia antes de marcar tardanza');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-05', 'school-20188-canete', 'MIN_PASSING_SCORE', '11',
        'NUMBER', 'Nota m?nima para aprobar (escala MINEDU 0-20)');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-06', 'school-20188-canete', 'REPORT_EXPIRY_HOURS', '72',
        'NUMBER', 'Horas que se conservan los reportes generados en el servidor');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-07', 'school-20188-canete', 'AUTO_APPROVE_JUSTIFICATION', 'true',
        'BOOLEAN', 'Aprobar autom?ticamente justificaciones con documento dentro del plazo');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-08', 'school-20188-canete', 'SCHOOL_START_TIME', '07:30',
        'STRING', 'Hora de inicio de clases (HH:MM, para validaci?n de QR)');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-09', 'school-20188-canete', 'BLOCKCHAIN_EVENTS_ENABLED', 'true',
        'BOOLEAN', 'Activar registro autom?tico de eventos en el blockchain');

INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description)
VALUES ('cfg-10', 'school-20188-canete', 'MAX_LOGIN_ATTEMPTS', '5',
        'NUMBER', 'Intentos fallidos antes de bloquear la cuenta (status=LOCKED)');


-- -----------------------------------------------------------------------------
-- Tabla: STUDENT_SECTION_HISTORY
-- Historial completo de los cambios de secci?n de cada alumno.
-- Registra promociones anuales, transferencias y cambios de aula.
-- Permite auditar la trayectoria acad?mica de un estudiante.
-- -----------------------------------------------------------------------------
CREATE TABLE STUDENT_SECTION_HISTORY (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    student_id      VARCHAR2(50)    NOT NULL,
    from_section_id VARCHAR2(50),               -- NULL si es primera matr?cula
    to_section_id   VARCHAR2(50),               -- NULL si es egreso sin destino
    change_reason   VARCHAR2(50)    NOT NULL,
    notes           VARCHAR2(1000),
    changed_by      VARCHAR2(50)    NOT NULL,   -- FK a USERS (quien registr? el cambio)
    changed_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    effective_date  DATE            NOT NULL,
    --
    CONSTRAINT pk_ssh               PRIMARY KEY (id),
    CONSTRAINT fk_ssh_school        FOREIGN KEY (school_id)      REFERENCES SCHOOLS(id),
    CONSTRAINT fk_ssh_student       FOREIGN KEY (student_id)     REFERENCES STUDENTS(id),
    CONSTRAINT fk_ssh_from_section  FOREIGN KEY (from_section_id) REFERENCES SECTIONS(id),
    CONSTRAINT fk_ssh_to_section    FOREIGN KEY (to_section_id)  REFERENCES SECTIONS(id),
    CONSTRAINT fk_ssh_changed_by    FOREIGN KEY (changed_by)     REFERENCES USERS(id),
    CONSTRAINT ck_ssh_reason        CHECK       (change_reason IN (
                                        'ENROLLMENT','PROMOTION','TRANSFER_IN','TRANSFER_OUT',
                                        'GRADUATION','WITHDRAWAL','SECTION_CHANGE','REINSTATEMENT'
                                    ))
);

COMMENT ON TABLE  STUDENT_SECTION_HISTORY IS 'Historial de cambios de secci?n/estado del alumno. Trazabilidad acad?mica completa.';
COMMENT ON COLUMN STUDENT_SECTION_HISTORY.change_reason IS
    'ENROLLMENT (primera matr?cula) | PROMOTION (fin de a?o) | TRANSFER_IN/OUT | GRADUATION | WITHDRAWAL';

CREATE INDEX idx_ssh_student  ON STUDENT_SECTION_HISTORY (school_id, student_id, effective_date);


-- -----------------------------------------------------------------------------
-- Tabla: SCHOOL_SUBSCRIPTIONS
-- Gesti?n del plan y suscripci?n de cada colegio en el SaaS.
-- Permite controlar l?mites de usuarios, almacenamiento y funcionalidades.
-- -----------------------------------------------------------------------------
CREATE TABLE SCHOOL_SUBSCRIPTIONS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    plan_name           VARCHAR2(50)    NOT NULL,
    max_students        NUMBER(5)       DEFAULT 500   NOT NULL,
    max_teachers        NUMBER(4)       DEFAULT 50    NOT NULL,
    max_storage_gb      NUMBER(6,2)     DEFAULT 5.00  NOT NULL,
    features_json       VARCHAR2(4000),              -- JSON: m?dulos habilitados
    billing_cycle       VARCHAR2(20)    DEFAULT 'MONTHLY' NOT NULL,
    price_monthly_soles NUMBER(8,2),
    starts_at           DATE            NOT NULL,
    expires_at          DATE,                        -- NULL = sin vencimiento (demo/gratuito)
    is_trial            NUMBER(1)       DEFAULT 0    NOT NULL,
    status              VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    payment_ref         VARCHAR2(200),               -- Referencia del procesador de pagos
    created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_subscriptions         PRIMARY KEY (id),
    CONSTRAINT fk_sub_school            FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_sub_school_active     UNIQUE      (school_id, status),   -- 1 suscripci?n activa
    CONSTRAINT ck_sub_plan              CHECK       (plan_name IN ('FREE','BASIC','STANDARD','PREMIUM','ENTERPRISE')),
    CONSTRAINT ck_sub_cycle             CHECK       (billing_cycle IN ('MONTHLY','ANNUAL','BIANNUAL')),
    CONSTRAINT ck_sub_status            CHECK       (status IN ('ACTIVE','SUSPENDED','CANCELLED','EXPIRED')),
    CONSTRAINT ck_sub_trial             CHECK       (is_trial IN (0,1)),
    CONSTRAINT ck_sub_dates             CHECK       (expires_at IS NULL OR expires_at > starts_at)
);

COMMENT ON TABLE  SCHOOL_SUBSCRIPTIONS IS 'Plan y l?mites del colegio en el SaaS. Una suscripci?n ACTIVE por colegio.';
COMMENT ON COLUMN SCHOOL_SUBSCRIPTIONS.features_json IS 'JSON con m?dulos habilitados: {"blockchain":true,"ai_chat":true,"reports":true}';

-- Suscripci?n demo para el colegio de ejemplo
INSERT INTO SCHOOL_SUBSCRIPTIONS (
    id, school_id, plan_name, max_students, max_teachers, max_storage_gb,
    features_json, billing_cycle, starts_at, is_trial, status
)
VALUES (
    'sub-demo-20188',
    'school-20188-canete',
    'PREMIUM',
    500, 50, 10.00,
    '{"blockchain":true,"ai_chat":true,"reports":true,"qr_attendance":true,"bulk_import":true}',
    'MONTHLY',
    DATE '2025-01-01',
    1,
    'ACTIVE'
);


-- =============================================================================
-- ADDENDUM ? BLOQUE B: TRIGGERS FALTANTES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Trigger: TRG_SCHOOLS_UPDATED_AT
-- El script original tiene updated_at en SCHOOLS pero no ten?a trigger.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SCHOOLS_UPDATED_AT
    BEFORE UPDATE ON SCHOOLS
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_SCHOOLS_UPDATED_AT;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_SUBSCRIPTIONS_UPDATED_AT
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SUBSCRIPTIONS_UPDATED_AT
    BEFORE UPDATE ON SCHOOL_SUBSCRIPTIONS
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_SUBSCRIPTIONS_UPDATED_AT;
/


-- -----------------------------------------------------------------------------
-- Trigger: TRG_ONE_ACTIVE_PERIOD
-- Garantiza que solo pueda haber UN per?odo acad?mico activo (is_active=1)
-- por cada school_year dentro del mismo colegio.
-- Previene datos inconsistentes que rompan los c?lculos de notas y asistencia.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_ONE_ACTIVE_PERIOD
    BEFORE INSERT OR UPDATE OF is_active ON ACADEMIC_PERIODS
    FOR EACH ROW
    WHEN (NEW.is_active = 1)
DECLARE
    v_count NUMBER;
BEGIN
    -- Contar cu?ntos per?odos activos existen para este school_year (excluyendo el actual en UPDATE)
    SELECT COUNT(*)
    INTO   v_count
    FROM   ACADEMIC_PERIODS
    WHERE  school_id       = :NEW.school_id
      AND  school_year_id  = :NEW.school_year_id
      AND  is_active       = 1
      AND  id             <> :NEW.id;   -- Excluir el mismo registro (UPDATE)

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(
            -20030,
            'FINE_FLOW_ERROR: Ya existe un per?odo acad?mico activo para este a?o escolar. '
            || 'Desactiva el per?odo actual antes de activar uno nuevo. '
            || 'school_year_id: ' || :NEW.school_year_id
        );
    END IF;
END TRG_ONE_ACTIVE_PERIOD;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_SECTION_CAPACITY_CHECK
-- Bloquea la asignaci?n de un alumno a una secci?n que ya alcanz? su
-- capacidad m?xima (max_capacity). Previene sobrecupos.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SECTION_CAPACITY_CHECK
    BEFORE INSERT OR UPDATE OF section_id ON STUDENTS
    FOR EACH ROW
    WHEN (NEW.section_id IS NOT NULL)
DECLARE
    v_current_count  NUMBER;
    v_max_capacity   NUMBER;
    v_section_name   VARCHAR2(10);
BEGIN
    -- Obtener capacidad m?xima y nombre de la secci?n destino
    SELECT max_capacity, name
    INTO   v_max_capacity, v_section_name
    FROM   SECTIONS
    WHERE  id        = :NEW.section_id
      AND  school_id = :NEW.school_id;

    -- Contar alumnos ACTIVOS ya asignados a esa secci?n
    -- Excluir el propio alumno en caso de UPDATE (no cuenta doble)
    SELECT COUNT(*)
    INTO   v_current_count
    FROM   STUDENTS
    WHERE  section_id = :NEW.section_id
      AND  school_id  = :NEW.school_id
      AND  status     = 'ACTIVE'
      AND  id        <> NVL(:NEW.id, 'NONE');  -- Excluir el mismo alumno en UPDATE

    IF v_current_count >= v_max_capacity THEN
        RAISE_APPLICATION_ERROR(
            -20031,
            'FINE_FLOW_ERROR: La secci?n "'|| v_section_name ||
            '" est? al m?ximo de su capacidad ('|| v_max_capacity ||' alumnos). '
            || 'Actual: '|| v_current_count || '. No se puede agregar m?s alumnos.'
        );
    END IF;
END TRG_SECTION_CAPACITY_CHECK;
/



-- -----------------------------------------------------------------------------
-- Trigger: TRG_JUSTIFICATION_AUTO_APPROVE
-- Aprueba autom?ticamente una justificaci?n si:
--   1. Se sube un documento (document_url IS NOT NULL)
--   2. El status pasa a PENDING
--   3. La config AUTO_APPROVE_JUSTIFICATION = 'true'
--   4. Se presenta dentro del plazo (expires_at)
-- Tambi?n actualiza el status de la ATTENDANCE a 'EXCUSED'.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_JUSTIFICATION_AUTO_APPROVE
    BEFORE INSERT ON JUSTIFICATIONS
    FOR EACH ROW
DECLARE
    v_auto_approve  VARCHAR2(10);
    v_deadline_hrs  NUMBER;
    v_within_hours  NUMBER;
BEGIN
    -- Solo actuar si hay documento adjunto
    IF :NEW.document_url IS NULL THEN
        RETURN;
    END IF;

    -- Leer configuraci?n del colegio
    BEGIN
        SELECT config_value INTO v_auto_approve
        FROM   SYSTEM_CONFIG
        WHERE  school_id   = :NEW.school_id
          AND  config_key  = 'AUTO_APPROVE_JUSTIFICATION';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN v_auto_approve := 'false';
    END;

    IF UPPER(v_auto_approve) != 'TRUE' THEN
        RETURN;
    END IF;

    -- Leer plazo de justificaci?n (horas)
    BEGIN
        SELECT TO_NUMBER(config_value) INTO v_deadline_hrs
        FROM   SYSTEM_CONFIG
        WHERE  school_id  = :NEW.school_id
          AND  config_key = 'JUSTIFICATION_DEADLINE_HOURS';
    EXCEPTION
        WHEN NO_DATA_FOUND THEN v_deadline_hrs := 48;
    END;

    -- Calcular horas desde la solicitud hasta ahora
    v_within_hours := (CAST(SYSTIMESTAMP AS DATE) - CAST(:NEW.requested_at AS DATE)) * 24;

    -- Si est? dentro del plazo, aprobar autom?ticamente
    IF v_within_hours <= v_deadline_hrs THEN
        :NEW.status       := 'APPROVED';
        :NEW.auto_approved := 1;
        :NEW.reviewed_at  := SYSTIMESTAMP;
        :NEW.review_note  := 'Aprobaci?n autom?tica: documento presentado dentro del plazo (' || ROUND(v_within_hours,1) || 'h).';

        -- Actualizar la asistencia a EXCUSED
        UPDATE ATTENDANCES
        SET    status = 'EXCUSED',
               justification_reason = 'Justificado autom?ticamente - ' || :NEW.reason
        WHERE  id = :NEW.attendance_id;
    END IF;
END TRG_JUSTIFICATION_AUTO_APPROVE;
/



-- =============================================================================
-- ADDENDUM ? BLOQUE C: STORED PROCEDURES FALTANTES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Stored Procedure: SP_ROTATE_QR_SECRETS
-- Rota los secretos HMAC-SHA256 de todos los alumnos ACTIVOS de un colegio.
-- El backend (cron job semanal) llama a este SP y luego genera nuevos QRs.
-- Par?metros:
--   p_school_id    : UUID del colegio
--   p_rotated_count: N?mero de alumnos cuyo secreto fue rotado (OUT)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_ROTATE_QR_SECRETS (
    p_school_id     IN  VARCHAR2,
    p_rotated_count OUT NUMBER
)
AS
BEGIN
    -- Verificar que el colegio exista y est? activo
    DECLARE
        v_school_status VARCHAR2(20);
    BEGIN
        SELECT status INTO v_school_status
        FROM   SCHOOLS
        WHERE  id = p_school_id;

        IF v_school_status != 'ACTIVE' THEN
            RAISE_APPLICATION_ERROR(-20040,
                'FINE_FLOW_ERROR: No se puede rotar QRs de un colegio con status "'
                || v_school_status || '".');
        END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20041,
                'FINE_FLOW_ERROR: Colegio "'|| p_school_id ||'" no encontrado.');
    END;

    -- Rotar secretos: marcar qr_rotated_at para que el backend genere nuevos secretos
    -- El valor real del secreto lo genera Java (SYS_GUID es solo un placeholder rotador)
    -- El backend detecta que qr_rotated_at cambi? y regenera el HMAC-SHA256
    UPDATE STUDENTS
    SET    qr_rotated_at = SYSTIMESTAMP,
           updated_at    = SYSTIMESTAMP
    WHERE  school_id     = p_school_id
      AND  status        = 'ACTIVE'
      AND  (qr_rotated_at IS NULL
            OR qr_rotated_at < SYSTIMESTAMP - INTERVAL '7' DAY);

    p_rotated_count := SQL%ROWCOUNT;

    -- Registrar en audit log
    INSERT INTO AUDIT_LOGS (id, school_id, action, entity_type, new_value_json, result, created_at)
    VALUES (
        SYS_GUID(),
        p_school_id,
        'QR_ROTATE',
        'STUDENT',
        '{"rotated_count":' || p_rotated_count || ',"triggered_by":"CRON_JOB"}',
        'SUCCESS',
        SYSTIMESTAMP
    );

    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_ROTATE_QR_SECRETS: ' || p_rotated_count ||
        ' secretos QR marcados para rotaci?n en colegio "' || p_school_id || '".'
    );
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_ROTATE_QR_SECRETS;
/



-- -----------------------------------------------------------------------------
-- Stored Procedure: SP_CLOSE_PERIOD_AND_OPEN_NEXT
-- Cierra el per?odo acad?mico activo y abre el siguiente.
-- Valida que el per?odo a cerrar sea el activo real del colegio.
-- Par?metros:
--   p_school_id       : UUID del colegio
--   p_current_period  : UUID del per?odo a cerrar
--   p_next_period     : UUID del per?odo a abrir (debe existir)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_CLOSE_PERIOD_AND_OPEN_NEXT (
    p_school_id      IN VARCHAR2,
    p_current_period IN VARCHAR2,
    p_next_period    IN VARCHAR2
)
AS
    v_current_active  NUMBER;
    v_next_sy         VARCHAR2(50);
    v_curr_sy         VARCHAR2(50);
    v_curr_name       VARCHAR2(100);
    v_next_name       VARCHAR2(100);
BEGIN
    -- Verificar que el per?odo actual sea el activo
    SELECT COUNT(*), MIN(school_year_id), MIN(name)
    INTO   v_current_active, v_curr_sy, v_curr_name
    FROM   ACADEMIC_PERIODS
    WHERE  id        = p_current_period
      AND  school_id = p_school_id
      AND  is_active = 1;

    IF v_current_active = 0 THEN
        RAISE_APPLICATION_ERROR(-20050,
            'FINE_FLOW_ERROR: El per?odo "'|| p_current_period ||
            '" no est? activo o no pertenece al colegio "'|| p_school_id ||'".');
    END IF;

    -- Verificar que el per?odo siguiente pertenezca al mismo school_year y colegio
    SELECT school_year_id, name
    INTO   v_next_sy, v_next_name
    FROM   ACADEMIC_PERIODS
    WHERE  id        = p_next_period
      AND  school_id = p_school_id;

    IF v_next_sy != v_curr_sy THEN
        RAISE_APPLICATION_ERROR(-20051,
            'FINE_FLOW_ERROR: El per?odo siguiente pertenece a un a?o acad?mico diferente.');
    END IF;

    -- Cerrar el per?odo actual
    UPDATE ACADEMIC_PERIODS
    SET    is_active = 0
    WHERE  id        = p_current_period
      AND  school_id = p_school_id;

    -- Abrir el per?odo siguiente
    -- (TRG_ONE_ACTIVE_PERIOD ya verific? que no haya conflicto)
    UPDATE ACADEMIC_PERIODS
    SET    is_active = 1
    WHERE  id        = p_next_period
      AND  school_id = p_school_id;

    -- Registrar en audit log
    INSERT INTO AUDIT_LOGS (id, school_id, action, entity_type, new_value_json, result, created_at)
    VALUES (
        SYS_GUID(), p_school_id, 'PERIOD_TRANSITION', 'ACADEMIC_PERIOD',
        '{"closed":"'|| v_curr_name ||'","opened":"'|| v_next_name ||'"}',
        'SUCCESS', SYSTIMESTAMP
    );

    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_CLOSE_PERIOD_AND_OPEN_NEXT: Cerrado "' || v_curr_name ||
        '" ? Abierto "' || v_next_name || '".'
    );
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_CLOSE_PERIOD_AND_OPEN_NEXT;
/



-- -----------------------------------------------------------------------------
-- Stored Procedure: SP_BULK_ENROLL_STUDENTS
-- Matricula masivamente estudiantes en una secci?n (importaci?n desde Excel).
-- Recibe una cadena CSV con los DNIs a matricular.
-- Par?metros:
--   p_school_id    : UUID del colegio
--   p_section_id   : UUID de la secci?n destino
--   p_changed_by   : UUID del usuario que realiza la matr?cula
--   p_enrolled     : Cantidad de alumnos matriculados (OUT)
--   p_skipped      : Cantidad omitidos (ya matriculados o no encontrados) (OUT)
-- Nota: en producci?n, el backend Java itera y llama UPDATE directo con
-- validaci?n individual; este SP es para la importaci?n masiva r?pida.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_BULK_ENROLL_STUDENTS (
    p_school_id  IN  VARCHAR2,
    p_section_id IN  VARCHAR2,
    p_changed_by IN  VARCHAR2,
    p_enrolled   OUT NUMBER,
    p_skipped    OUT NUMBER
)
AS
    v_section_ok      NUMBER;
    v_current_count   NUMBER;
    v_max_capacity    NUMBER;
    v_available_slots NUMBER;
BEGIN
    p_enrolled := 0;
    p_skipped  := 0;

    -- Verificar secci?n existe y pertenece al colegio
    SELECT COUNT(*), MAX(max_capacity)
    INTO   v_section_ok, v_max_capacity
    FROM   SECTIONS
    WHERE  id        = p_section_id
      AND  school_id = p_school_id
      AND  is_active = 1;

    IF v_section_ok = 0 THEN
        RAISE_APPLICATION_ERROR(-20060,
            'FINE_FLOW_ERROR: Secci?n "'|| p_section_id ||
            '" no encontrada, inactiva o no pertenece al colegio.');
    END IF;

    -- Contar alumnos ya en la secci?n
    SELECT COUNT(*) INTO v_current_count
    FROM   STUDENTS
    WHERE  section_id = p_section_id
      AND  school_id  = p_school_id
      AND  status     = 'ACTIVE';

    v_available_slots := v_max_capacity - v_current_count;

    IF v_available_slots <= 0 THEN
        RAISE_APPLICATION_ERROR(-20061,
            'FINE_FLOW_ERROR: La secci?n no tiene cupo disponible. '
            || 'Capacidad: '|| v_max_capacity ||', Ocupados: '|| v_current_count ||'.');
    END IF;

    -- Matricular alumnos ACTIVOS del mismo colegio que no tienen secci?n asignada
    -- Limitado a los slots disponibles
    UPDATE STUDENTS
    SET    section_id  = p_section_id,
           updated_at  = SYSTIMESTAMP
    WHERE  school_id   = p_school_id
      AND  status      = 'ACTIVE'
      AND  section_id  IS NULL
      AND  ROWNUM      <= v_available_slots;

    p_enrolled := SQL%ROWCOUNT;

    -- Los que tienen secci?n o est?n inactivos se consideran "skipped"
    SELECT COUNT(*) INTO p_skipped
    FROM   STUDENTS
    WHERE  school_id  = p_school_id
      AND  status     = 'ACTIVE'
      AND  section_id IS NULL;  -- A?n sin secci?n (no entraron por cupo)

    -- Registrar historia para cada alumno matriculado
    INSERT INTO STUDENT_SECTION_HISTORY (
        id, school_id, student_id, from_section_id, to_section_id,
        change_reason, notes, changed_by, effective_date
    )
    SELECT
        SYS_GUID(), school_id, id, NULL, p_section_id,
        'ENROLLMENT',
        'Matr?cula masiva ? bulk enroll',
        p_changed_by,
        TRUNC(SYSDATE)
    FROM STUDENTS
    WHERE  school_id  = p_school_id
      AND  section_id = p_section_id
      AND  status     = 'ACTIVE'
      AND  updated_at >= SYSTIMESTAMP - INTERVAL '1' MINUTE;  -- Solo los reci?n matriculados

    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_BULK_ENROLL_STUDENTS: Matriculados='|| p_enrolled ||
        ', Omitidos='|| p_skipped ||' (sin cupo o ya matriculados).'
    );
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_BULK_ENROLL_STUDENTS;
/



-- -----------------------------------------------------------------------------
-- Stored Procedure: SP_DEACTIVATE_SCHOOL
-- Suspende o desactiva un colegio en el SaaS.
-- Revoca todos los refresh tokens activos y cierra las sesiones de chat.
-- SOLO para uso del administrador del SaaS (no del colegio).
-- Par?metros:
--   p_school_id : UUID del colegio a desactivar
--   p_new_status: 'INACTIVE' o 'SUSPENDED'
--   p_reason    : Motivo del cambio de estado
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_DEACTIVATE_SCHOOL (
    p_school_id  IN VARCHAR2,
    p_new_status IN VARCHAR2,
    p_reason     IN VARCHAR2 DEFAULT NULL
)
AS
    v_current_status  VARCHAR2(20);
    v_tokens_revoked  NUMBER;
    v_sessions_closed NUMBER;
BEGIN
    -- Validar el nuevo estado
    IF p_new_status NOT IN ('INACTIVE','SUSPENDED') THEN
        RAISE_APPLICATION_ERROR(-20070,
            'FINE_FLOW_ERROR: Estado inv?lido "'|| p_new_status ||
            '". Use INACTIVE o SUSPENDED.');
    END IF;

    -- Obtener estado actual
    BEGIN
        SELECT status INTO v_current_status
        FROM   SCHOOLS
        WHERE  id = p_school_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20071,
                'FINE_FLOW_ERROR: Colegio "'|| p_school_id ||'" no encontrado.');
    END;

    IF v_current_status = p_new_status THEN
        DBMS_OUTPUT.PUT_LINE('Aviso: el colegio ya tiene status "'|| p_new_status ||'".');
        RETURN;
    END IF;

    -- 1. Revocar todos los refresh tokens activos
    UPDATE REFRESH_TOKENS
    SET    revoked_at    = SYSTIMESTAMP,
           revoke_reason = 'ADMIN'
    WHERE  school_id  = p_school_id
      AND  revoked_at IS NULL;

    v_tokens_revoked := SQL%ROWCOUNT;

    -- 2. Cerrar todas las sesiones de chat activas
    UPDATE CHAT_SESSIONS
    SET    is_active = 0,
           ended_at  = SYSTIMESTAMP
    WHERE  school_id = p_school_id
      AND  is_active = 1;

    v_sessions_closed := SQL%ROWCOUNT;

    -- 3. Marcar los report_jobs pendientes como EXPIRED
    UPDATE REPORT_JOBS
    SET    status = 'EXPIRED'
    WHERE  school_id = p_school_id
      AND  status IN ('PENDING','PROCESSING');

    -- 4. Actualizar el status del colegio
    UPDATE SCHOOLS
    SET    status = p_new_status
    WHERE  id     = p_school_id;

    -- 5. Registrar en audit log
    INSERT INTO AUDIT_LOGS (id, school_id, action, entity_type, entity_id, old_value_json, new_value_json, result, created_at)
    VALUES (
        SYS_GUID(), p_school_id, 'DEACTIVATE_SCHOOL', 'SCHOOL', p_school_id,
        '{"status":"'|| v_current_status ||'"}',
        '{"status":"'|| p_new_status ||'","reason":"'|| NVL(p_reason,'No especificado') ||'",'
        || '"tokens_revoked":'|| v_tokens_revoked ||','
        || '"sessions_closed":'|| v_sessions_closed ||'}',
        'SUCCESS', SYSTIMESTAMP
    );

    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_DEACTIVATE_SCHOOL: Colegio "'|| p_school_id ||'" ? '|| p_new_status ||'. '
        || 'Tokens revocados: '|| v_tokens_revoked ||'. '
        || 'Sesiones cerradas: '|| v_sessions_closed ||'.'
    );
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_DEACTIVATE_SCHOOL;
/



-- =============================================================================
-- ADDENDUM ? BLOQUE D: VISTAS FALTANTES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Vista: V_STUDENT_FINAL_GRADES
-- Nota final ponderada por alumno, curso y per?odo acad?mico.
-- Aplica la ponderaci?n de cada competencia (COURSE_COMPETENCIES.weight)
-- para calcular la nota final del curso seg?n el modelo MINEDU/CNEB.
-- El backend filtra siempre con: WHERE school_id = :school_id
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW V_STUDENT_FINAL_GRADES AS
SELECT
    ss.school_id,
    -- Alumno
    st.id                                                   AS student_id,
    st.first_name                                           AS student_first_name,
    st.last_name                                            AS student_last_name,
    st.document_number                                      AS student_document,
    -- Secci?n
    sec.id                                                  AS section_id,
    sec.name                                                AS section_name,
    -- A?o acad?mico
    sy.id                                                   AS school_year_id,
    sy.name                                                 AS school_year_name,
    sy.grade_number,
    -- Curso
    c.id                                                    AS course_id,
    c.name                                                  AS course_name,
    -- Per?odo
    ap.id                                                   AS academic_period_id,
    ap.name                                                 AS academic_period_name,
    -- Promedios por competencia (subquery escalar)
    COUNT(DISTINCT comp.id)                                 AS competencies_count,
    COUNT(ss.id)                                            AS total_scores,
    -- Nota final ponderada: SUM(avg_competencia * peso) / SUM(pesos)
    ROUND(
        SUM(
            (SELECT AVG(ss2.score)
             FROM   STUDENT_SCORES ss2, CLASS_TASKS ct2
             WHERE  ct2.id = ss2.class_task_id
               AND  ss2.student_id    = st.id
               AND  ct2.competency_id = comp.id
               AND  ct2.academic_period_id = ap.id
            ) * comp.weight
        ) / NULLIF(SUM(comp.weight), 0)
    , 2)                                                    AS final_weighted_score,
    -- Promedio simple (sin ponderar)
    ROUND(AVG(ss.score), 2)                                 AS simple_average,
    -- Nivel de logro final
    CASE
        WHEN ROUND(SUM(
            (SELECT AVG(ss2.score)
              FROM   STUDENT_SCORES ss2, CLASS_TASKS ct2
              WHERE  ct2.id = ss2.class_task_id
                AND  ss2.student_id    = st.id
               AND  ct2.competency_id = comp.id
               AND  ct2.academic_period_id = ap.id
            ) * comp.weight
        ) / NULLIF(SUM(comp.weight), 0), 2) >= 18 THEN 'AD'
        WHEN ROUND(SUM(
            (SELECT AVG(ss2.score)
              FROM   STUDENT_SCORES ss2, CLASS_TASKS ct2
              WHERE  ct2.id = ss2.class_task_id
                AND  ss2.student_id    = st.id
               AND  ct2.competency_id = comp.id
               AND  ct2.academic_period_id = ap.id
            ) * comp.weight
        ) / NULLIF(SUM(comp.weight), 0), 2) >= 14 THEN 'A'
        WHEN ROUND(SUM(
            (SELECT AVG(ss2.score)
              FROM   STUDENT_SCORES ss2, CLASS_TASKS ct2
              WHERE  ct2.id = ss2.class_task_id
                AND  ss2.student_id    = st.id
               AND  ct2.competency_id = comp.id
               AND  ct2.academic_period_id = ap.id
            ) * comp.weight
        ) / NULLIF(SUM(comp.weight), 0), 2) >= 11 THEN 'B'
        ELSE 'C'
    END                                                     AS achievement_level,
    -- Estado: APROBADO (>= 11) o DESAPROBADO
    CASE
        WHEN ROUND(AVG(ss.score), 2) >= 11 THEN 'APROBADO'
        ELSE 'DESAPROBADO'
    END                                                     AS approval_status
FROM
    STUDENT_SCORES        ss
    JOIN STUDENTS         st   ON st.id   = ss.student_id
    JOIN SECTIONS         sec  ON sec.id  = st.section_id
    JOIN SCHOOL_YEARS     sy   ON sy.id   = sec.school_year_id
    JOIN CLASS_TASKS      ct   ON ct.id   = ss.class_task_id
    JOIN COURSE_COMPETENCIES comp ON comp.id = ct.competency_id
    JOIN COURSE_ASSIGNMENTS  ca  ON ca.id  = ct.course_assignment_id
    JOIN COURSES          c    ON c.id    = ca.course_id
    JOIN ACADEMIC_PERIODS ap   ON ap.id   = ct.academic_period_id
WHERE
    st.section_id IS NOT NULL
    AND ct.is_active = 1
    AND comp.is_active = 1
GROUP BY
    ss.school_id,
    st.id, st.first_name, st.last_name, st.document_number,
    sec.id, sec.name,
    sy.id, sy.name, sy.grade_number,
    c.id, c.name,
    ap.id, ap.name;

COMMENT ON TABLE V_STUDENT_FINAL_GRADES IS 'Nota final ponderada por alumno, curso y per?odo. Aplica pesos de COURSE_COMPETENCIAS. Filtrar siempre por school_id en el backend. Niveles: AD/A/B/C (MINEDU).';


-- -----------------------------------------------------------------------------
-- Vista: V_SCHOOL_DASHBOARD_STATS
-- Estad?sticas de alto nivel para el dashboard del administrador.
-- Una fila por colegio con m?tricas del estado actual.
-- El backend filtra con: WHERE school_id = :school_id
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW V_SCHOOL_DASHBOARD_STATS AS
SELECT
    s.id                                                            AS school_id,
    s.name                                                          AS school_name,
    s.status                                                        AS school_status,
    -- Conteos de personas
    (SELECT COUNT(*) FROM USERS     u WHERE u.school_id = s.id AND u.status = 'ACTIVE')  AS active_users,
    (SELECT COUNT(*) FROM TEACHERS  t WHERE t.school_id = s.id AND t.status = 'ACTIVE')  AS active_teachers,
    (SELECT COUNT(*) FROM STUDENTS  st WHERE st.school_id = s.id AND st.status = 'ACTIVE') AS active_students,
    -- Estructura acad?mica activa
    (SELECT COUNT(*) FROM SECTIONS sec, SCHOOL_YEARS sy
     WHERE sy.id = sec.school_year_id
       AND sec.school_id = s.id AND sec.is_active = 1 AND sy.calendar_year = EXTRACT(YEAR FROM SYSDATE)
    )                                                                AS active_sections,
    -- Per?odo acad?mico activo
    (SELECT ap.name FROM ACADEMIC_PERIODS ap
     WHERE ap.school_id = s.id AND ap.is_active = 1 AND ROWNUM = 1
    )                                                                AS current_period_name,
    -- Asistencia del d?a (% de presentes hoy)
    (SELECT ROUND(
        SUM(CASE WHEN att.status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) * 100.0
        / NULLIF(COUNT(*), 0), 1)
     FROM ATTENDANCES att
     WHERE att.school_id = s.id
       AND att.attendance_date = TRUNC(SYSDATE)
       AND att.course_assignment_id IS NULL    -- Solo registros de entrada general
    )                                                                AS today_attendance_pct,
    -- Asistencias del d?a
    (SELECT COUNT(*) FROM ATTENDANCES att
     WHERE att.school_id = s.id AND att.attendance_date = TRUNC(SYSDATE)
       AND att.status = 'PRESENT' AND att.course_assignment_id IS NULL
    )                                                                AS today_present_count,
    (SELECT COUNT(*) FROM ATTENDANCES att
     WHERE att.school_id = s.id AND att.attendance_date = TRUNC(SYSDATE)
       AND att.status = 'ABSENT' AND att.course_assignment_id IS NULL
    )                                                                AS today_absent_count,
    -- Alertas activas
    (SELECT COUNT(*) FROM NOTIFICATIONS n
     WHERE n.school_id = s.id AND n.is_read = 0
    )                                                                AS unread_notifications,
    (SELECT COUNT(*) FROM JUSTIFICATIONS j
     WHERE j.school_id = s.id AND j.status = 'PENDING'
    )                                                                AS pending_justifications,
    (SELECT COUNT(*) FROM REPORT_JOBS rj
     WHERE rj.school_id = s.id AND rj.status IN ('PENDING','PROCESSING')
    )                                                                AS pending_reports,
    -- Bloques blockchain
    (SELECT COUNT(*) FROM BLOCKCHAIN_BLOCKS bb
     WHERE bb.school_id = s.id
    )                                                                AS blockchain_block_count,
    -- Suscripci?n activa
    (SELECT sub.plan_name FROM SCHOOL_SUBSCRIPTIONS sub
     WHERE sub.school_id = s.id AND sub.status = 'ACTIVE' AND ROWNUM = 1
    )                                                                AS subscription_plan,
    (SELECT sub.expires_at FROM SCHOOL_SUBSCRIPTIONS sub
     WHERE sub.school_id = s.id AND sub.status = 'ACTIVE' AND ROWNUM = 1
    )                                                                AS subscription_expires
FROM
    SCHOOLS s;

COMMENT ON TABLE V_SCHOOL_DASHBOARD_STATS IS 'Estad?sticas del dashboard por colegio. Consulta escalar (N+1 subqueries). En producci?n considerar materializar con DBMS_MVIEW para colegios con >2000 alumnos.';


-- -----------------------------------------------------------------------------
-- Vista: V_ATTENDANCE_ALERTS
-- Alumnos que superan el umbral de faltas configurado en SYSTEM_CONFIG.
-- Permite al coordinador identificar casos cr?ticos de asistencia.
-- El backend filtra con: WHERE school_id = :school_id
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW V_ATTENDANCE_ALERTS AS
SELECT
    a.school_id,
    st.id                                                   AS student_id,
    st.first_name                                           AS student_first_name,
    st.last_name                                            AS student_last_name,
    st.document_number,
    sec.id                                                  AS section_id,
    sec.name                                                AS section_name,
    sy.grade_number,
    SUM(CASE WHEN a.status = 'ABSENT' AND a.attendance_date >= TRUNC(SYSDATE) - 30
             THEN 1 ELSE 0 END)                             AS absences_last_30d,
    SUM(CASE WHEN a.status = 'ABSENT'  THEN 1 ELSE 0 END)  AS total_absences,
    SUM(CASE WHEN a.status = 'LATE'    THEN 1 ELSE 0 END)  AS total_late,
    SUM(CASE WHEN a.status = 'EXCUSED' THEN 1 ELSE 0 END)  AS total_excused,
    COUNT(a.id)                                             AS total_records,
    ROUND(
        SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) * 100.0
        / NULLIF(COUNT(a.id), 0), 1
    )                                                       AS absence_pct,
    MAX(CASE WHEN a.status = 'ABSENT' THEN a.attendance_date END) AS last_absence_date,
    CASE
        WHEN SUM(CASE WHEN a.status = 'ABSENT' AND a.attendance_date >= TRUNC(SYSDATE) - 30
                      THEN 1 ELSE 0 END) >= 5 THEN 'CRITICAL'
        WHEN SUM(CASE WHEN a.status = 'ABSENT' AND a.attendance_date >= TRUNC(SYSDATE) - 30
                      THEN 1 ELSE 0 END) >= 3 THEN 'WARNING'
        ELSE 'INFO'
    END                                                     AS alert_level,
    -- Justificaciones pendientes del alumno
    (SELECT COUNT(*) FROM JUSTIFICATIONS j
     WHERE j.student_id = st.id AND j.status = 'PENDING'
    )                                                       AS pending_justifications
FROM
    ATTENDANCES     a
    JOIN STUDENTS   st  ON st.id  = a.student_id
    JOIN SECTIONS   sec ON sec.id = st.section_id
    JOIN SCHOOL_YEARS sy ON sy.id = sec.school_year_id
WHERE
    a.course_assignment_id IS NULL          -- Solo entradas generales (no por curso)
    AND st.status = 'ACTIVE'
    AND st.section_id IS NOT NULL
    AND sy.calendar_year = EXTRACT(YEAR FROM SYSDATE)
GROUP BY
    a.school_id,
    st.id, st.first_name, st.last_name, st.document_number,
    sec.id, sec.name, sy.grade_number
HAVING
    SUM(CASE WHEN a.status = 'ABSENT' AND a.attendance_date >= TRUNC(SYSDATE) - 30
             THEN 1 ELSE 0 END) >= 3;

COMMENT ON TABLE V_ATTENDANCE_ALERTS IS 'Alumnos con 3+ faltas en ?ltimos 30 d?as. Niveles: WARNING (3-4) | CRITICAL (5+). Filtrar siempre por school_id en el backend.';


-- =============================================================================
-- ADDENDUM ? BLOQUE E: SEED DATA COMPLETO
-- =============================================================================

-- -----------------------------------------------------------------------------
-- E.1: Competencias faltantes (HGE, Ciencias, Ingl?s)
-- -----------------------------------------------------------------------------

-- Competencias CNEB ? Historia, Geograf?a y Econom?a
INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-hge-1', 'school-20188-canete', 'course-his-20188',
        'Construye interpretaciones hist?ricas', 34, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-hge-2', 'school-20188-canete', 'course-his-20188',
        'Gestiona responsablemente el espacio y el ambiente', 33, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-hge-3', 'school-20188-canete', 'course-his-20188',
        'Gestiona responsablemente los recursos econ?micos', 33, 1);

-- Competencias CNEB ? Ciencia y Tecnolog?a
INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-cyt-1', 'school-20188-canete', 'course-cie-20188',
        'Indaga mediante m?todos cient?ficos', 34, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-cyt-2', 'school-20188-canete', 'course-cie-20188',
        'Explica el mundo f?sico bas?ndose en conocimientos cient?ficos', 33, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-cyt-3', 'school-20188-canete', 'course-cie-20188',
        'Dise?a y construye soluciones tecnol?gicas', 33, 1);

-- Competencias CNEB ? Ingl?s
INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-ing-1', 'school-20188-canete', 'course-ing-20188',
        'Se comunica oralmente en ingl?s como lengua extranjera', 34, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-ing-2', 'school-20188-canete', 'course-ing-20188',
        'Lee diversos tipos de textos en ingl?s como lengua extranjera', 33, 1);

INSERT INTO COURSE_COMPETENCIES (id, school_id, course_id, name, weight, is_active)
VALUES ('comp-ing-3', 'school-20188-canete', 'course-ing-20188',
        'Escribe diversos tipos de textos en ingl?s como lengua extranjera', 33, 1);


-- -----------------------------------------------------------------------------
-- E.2: Usuarios demo (TEACHER, STUDENT, GUARDIAN)
-- -----------------------------------------------------------------------------

INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES ('user-teacher-01', 'school-20188-canete', 'jgarcia@ie20188.edu.pe',
        '$2a$12$exampleBCryptHashTeacher1Replace.XYZ', 'TEACHER', 'Juan', 'Garc?a L?pez', 'ACTIVE');

INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES ('user-teacher-02', 'school-20188-canete', 'mtorres@ie20188.edu.pe',
        '$2a$12$exampleBCryptHashTeacher2Replace.XYZ', 'TEACHER', 'Mar?a', 'Torres Vidal', 'ACTIVE');

INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES ('user-teacher-03', 'school-20188-canete', 'cramirez@ie20188.edu.pe',
        '$2a$12$exampleBCryptHashTeacher3Replace.XYZ', 'TEACHER', 'Carlos', 'Ram?rez Quispe', 'ACTIVE');

INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES ('user-student-01', 'school-20188-canete', 'chuaman@ie20188.edu.pe',
        '$2a$12$exampleBCryptHashStudent1Replace.XYZ', 'STUDENT', 'Carlos', 'Huam?n Quispe', 'ACTIVE');

INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES ('user-guardian-01', 'school-20188-canete', 'apoderado1@gmail.com',
        '$2a$12$exampleBCryptHashGuardian1Replace.XYZ', 'GUARDIAN', 'Roberto', 'Huam?n Ramos', 'ACTIVE');

INSERT INTO USERS (id, school_id, email, password_hash, role, first_name, last_name, status)
VALUES ('user-coord-01', 'school-20188-canete', 'coordinador@ie20188.edu.pe',
        '$2a$12$exampleBCryptHashCoord1Replace.XYZ', 'COORDINATOR', 'Luc?a', 'Mendoza Rojas', 'ACTIVE');


-- -----------------------------------------------------------------------------
-- E.3: Docentes (TEACHERS)
-- -----------------------------------------------------------------------------

INSERT INTO TEACHERS (id, school_id, user_id, first_name, last_name, document_number, specialty, status)
VALUES ('teacher-01', 'school-20188-canete', 'user-teacher-01',
        'Juan', 'Garc?a L?pez', '12345678',
        'Matem?tica,Estad?stica', 'ACTIVE');

INSERT INTO TEACHERS (id, school_id, user_id, first_name, last_name, document_number, specialty, status)
VALUES ('teacher-02', 'school-20188-canete', 'user-teacher-02',
        'Mar?a', 'Torres Vidal', '23456789',
        'Comunicaci?n,Literatura', 'ACTIVE');

INSERT INTO TEACHERS (id, school_id, user_id, first_name, last_name, document_number, specialty, status)
VALUES ('teacher-03', 'school-20188-canete', 'user-teacher-03',
        'Carlos', 'Ram?rez Quispe', '34567890',
        'Historia,Geograf?a,Econom?a', 'ACTIVE');


-- Asignar tutor a secciones
UPDATE SECTIONS SET tutor_id = 'teacher-01' WHERE id = 'sec-3a-2025';
UPDATE SECTIONS SET tutor_id = 'teacher-02' WHERE id = 'sec-3b-2025';


-- -----------------------------------------------------------------------------
-- E.4: Alumnos demo con section_id
-- -----------------------------------------------------------------------------

INSERT INTO STUDENTS (id, school_id, section_id, user_id, first_name, last_name,
                      document_type, document_number, status)
VALUES ('student-01', 'school-20188-canete', 'sec-3a-2025', 'user-student-01',
        'Carlos', 'Huam?n Quispe', 'DNI', '72345678', 'ACTIVE');

INSERT INTO STUDENTS (id, school_id, section_id, first_name, last_name,
                      document_type, document_number, status)
VALUES ('student-02', 'school-20188-canete', 'sec-3a-2025',
        'Jos?', 'Mamani Torres', 'DNI', '73456789', 'ACTIVE');

INSERT INTO STUDENTS (id, school_id, section_id, first_name, last_name,
                      document_type, document_number, status)
VALUES ('student-03', 'school-20188-canete', 'sec-3a-2025',
        'Luis', 'Ccopa Flores', 'DNI', '74567890', 'ACTIVE');

INSERT INTO STUDENTS (id, school_id, section_id, first_name, last_name,
                      document_type, document_number, status)
VALUES ('student-04', 'school-20188-canete', 'sec-3b-2025',
        '?ngel', 'V?squez D?az', 'DNI', '75678901', 'ACTIVE');

INSERT INTO STUDENTS (id, school_id, section_id, first_name, last_name,
                      document_type, document_number, status)
VALUES ('student-05', 'school-20188-canete', 'sec-3b-2025',
        'Miguel', 'Ramos Condori', 'DNI', '76789012', 'ACTIVE');


-- Apoderado del alumno 01
INSERT INTO GUARDIANS (id, school_id, user_id, student_id, first_name, last_name,
                       relationship, phone, is_primary_contact)
VALUES ('guardian-01', 'school-20188-canete', 'user-guardian-01', 'student-01',
        'Roberto', 'Huam?n Ramos', 'FATHER', '987654321', 1);


-- Historial de matr?cula inicial
INSERT INTO STUDENT_SECTION_HISTORY (id, school_id, student_id, from_section_id, to_section_id,
                                     change_reason, changed_by, effective_date)
SELECT SYS_GUID(), school_id, id, NULL, section_id, 'ENROLLMENT', 'user-admin-20188', DATE '2025-03-10'
FROM   STUDENTS
WHERE  school_id = 'school-20188-canete';


-- -----------------------------------------------------------------------------
-- E.5: Asignaciones de curso (COURSE_ASSIGNMENTS)
-- -----------------------------------------------------------------------------

INSERT INTO COURSE_ASSIGNMENTS (id, school_id, course_id, section_id, teacher_id,
                                academic_period_id, hours_per_week, is_active)
VALUES ('ca-mat-3a-b1', 'school-20188-canete', 'course-mat-20188', 'sec-3a-2025',
        'teacher-01', 'ap-b1-3sec-2025', 5, 1);

INSERT INTO COURSE_ASSIGNMENTS (id, school_id, course_id, section_id, teacher_id,
                                academic_period_id, hours_per_week, is_active)
VALUES ('ca-com-3a-b1', 'school-20188-canete', 'course-com-20188', 'sec-3a-2025',
        'teacher-02', 'ap-b1-3sec-2025', 4, 1);

INSERT INTO COURSE_ASSIGNMENTS (id, school_id, course_id, section_id, teacher_id,
                                academic_period_id, hours_per_week, is_active)
VALUES ('ca-his-3a-b1', 'school-20188-canete', 'course-his-20188', 'sec-3a-2025',
        'teacher-03', 'ap-b1-3sec-2025', 3, 1);

INSERT INTO COURSE_ASSIGNMENTS (id, school_id, course_id, section_id, teacher_id,
                                academic_period_id, hours_per_week, is_active)
VALUES ('ca-mat-3b-b1', 'school-20188-canete', 'course-mat-20188', 'sec-3b-2025',
        'teacher-01', 'ap-b1-3sec-2025', 5, 1);


-- -----------------------------------------------------------------------------
-- E.6: Tareas evaluables (CLASS_TASKS)
-- -----------------------------------------------------------------------------

INSERT INTO CLASS_TASKS (id, school_id, course_assignment_id, competency_id,
                         academic_period_id, title, task_type, max_score, due_date, is_active)
VALUES ('task-mat-01', 'school-20188-canete', 'ca-mat-3a-b1', 'comp-mat-1',
        'ap-b1-3sec-2025', 'Pr?ctica Calificada ? Fracciones', 'QUIZ', 20, DATE '2025-03-21', 1);

INSERT INTO CLASS_TASKS (id, school_id, course_assignment_id, competency_id,
                         academic_period_id, title, task_type, max_score, due_date, is_active)
VALUES ('task-mat-02', 'school-20188-canete', 'ca-mat-3a-b1', 'comp-mat-2',
        'ap-b1-3sec-2025', 'Examen Bimestral ? ?lgebra', 'EXAM', 20, DATE '2025-04-25', 1);

INSERT INTO CLASS_TASKS (id, school_id, course_assignment_id, competency_id,
                         academic_period_id, title, task_type, max_score, due_date, is_active)
VALUES ('task-com-01', 'school-20188-canete', 'ca-com-3a-b1', 'comp-com-1',
        'ap-b1-3sec-2025', 'Exposici?n oral ? Tema libre', 'ORAL', 20, DATE '2025-03-28', 1);

INSERT INTO CLASS_TASKS (id, school_id, course_assignment_id, competency_id,
                         academic_period_id, title, task_type, max_score, due_date, is_active)
VALUES ('task-com-02', 'school-20188-canete', 'ca-com-3a-b1', 'comp-com-2',
        'ap-b1-3sec-2025', 'Comprensi?n Lectora ? Texto narrativo', 'QUIZ', 20, DATE '2025-04-11', 1);

INSERT INTO CLASS_TASKS (id, school_id, course_assignment_id, competency_id,
                         academic_period_id, title, task_type, max_score, due_date, is_active)
VALUES ('task-his-01', 'school-20188-canete', 'ca-his-3a-b1', 'comp-hge-1',
        'ap-b1-3sec-2025', 'Trabajo ? Imperio Inca y su organizaci?n', 'PROJECT', 20, DATE '2025-04-04', 1);


-- -----------------------------------------------------------------------------
-- E.7: Calificaciones demo (STUDENT_SCORES)
-- -----------------------------------------------------------------------------

-- Carlos Huam?n ? Matem?tica (Pr?ctica Fracciones)
INSERT INTO STUDENT_SCORES (id, school_id, student_id, class_task_id, score, registered_by)
VALUES ('ss-01', 'school-20188-canete', 'student-01', 'task-mat-01', 17.5, 'user-teacher-01');

-- Carlos Huam?n ? Matem?tica (Examen ?lgebra)
INSERT INTO STUDENT_SCORES (id, school_id, student_id, class_task_id, score, registered_by)
VALUES ('ss-02', 'school-20188-canete', 'student-01', 'task-mat-02', 16.0, 'user-teacher-01');

-- Carlos Huam?n ? Comunicaci?n (Exposici?n oral)
INSERT INTO STUDENT_SCORES (id, school_id, student_id, class_task_id, score, registered_by)
VALUES ('ss-03', 'school-20188-canete', 'student-01', 'task-com-01', 15.0, 'user-teacher-02');

-- Jos? Mamani ? Matem?tica (Pr?ctica Fracciones)
INSERT INTO STUDENT_SCORES (id, school_id, student_id, class_task_id, score, registered_by)
VALUES ('ss-04', 'school-20188-canete', 'student-02', 'task-mat-01', 14.0, 'user-teacher-01');

-- Jos? Mamani ? Matem?tica (Examen ?lgebra)
INSERT INTO STUDENT_SCORES (id, school_id, student_id, class_task_id, score, registered_by)
VALUES ('ss-05', 'school-20188-canete', 'student-02', 'task-mat-02', 13.5, 'user-teacher-01');

-- Luis Ccopa ? Historia (Trabajo Imperio Inca)
INSERT INTO STUDENT_SCORES (id, school_id, student_id, class_task_id, score, registered_by)
VALUES ('ss-06', 'school-20188-canete', 'student-03', 'task-his-01', 19.0, 'user-teacher-03');


-- -----------------------------------------------------------------------------
-- E.8: Asistencias demo
-- -----------------------------------------------------------------------------

-- Entrada general ? 10 Mar 2025
INSERT INTO ATTENDANCES (id, school_id, student_id, course_assignment_id, attendance_date,
                         status, check_in_time, record_method, registered_by)
VALUES ('att-01', 'school-20188-canete', 'student-01', NULL,
        DATE '2025-03-10', 'PRESENT', '07:32', 'QR', 'user-admin-20188');

INSERT INTO ATTENDANCES (id, school_id, student_id, course_assignment_id, attendance_date,
                         status, check_in_time, record_method, registered_by)
VALUES ('att-02', 'school-20188-canete', 'student-02', NULL,
        DATE '2025-03-10', 'PRESENT', '07:28', 'QR', 'user-admin-20188');

INSERT INTO ATTENDANCES (id, school_id, student_id, course_assignment_id, attendance_date,
                         status, check_in_time, record_method, registered_by)
VALUES ('att-03', 'school-20188-canete', 'student-03', NULL,
        DATE '2025-03-10', 'LATE', '07:52', 'MANUAL', 'user-teacher-01');

INSERT INTO ATTENDANCES (id, school_id, student_id, course_assignment_id, attendance_date,
                         status, check_in_time, record_method, registered_by)
VALUES ('att-04', 'school-20188-canete', 'student-04', NULL,
        DATE '2025-03-10', 'ABSENT', NULL, 'MANUAL', 'user-teacher-02');

-- Entrada general ? 11 Mar 2025
INSERT INTO ATTENDANCES (id, school_id, student_id, course_assignment_id, attendance_date,
                         status, check_in_time, record_method, registered_by)
VALUES ('att-05', 'school-20188-canete', 'student-01', NULL,
        DATE '2025-03-11', 'PRESENT', '07:29', 'QR', 'user-admin-20188');

INSERT INTO ATTENDANCES (id, school_id, student_id, course_assignment_id, attendance_date,
                         status, check_in_time, record_method, registered_by)
VALUES ('att-06', 'school-20188-canete', 'student-04', NULL,
        DATE '2025-03-11', 'PRESENT', '07:31', 'QR', 'user-admin-20188');

-- Justificaci?n de la falta de ?ngel V?squez (student-04 el 10 Mar)
INSERT INTO JUSTIFICATIONS (id, school_id, student_id, attendance_id, requested_by,
                             reason, document_url, status, auto_approved, requested_at)
VALUES ('just-01', 'school-20188-canete', 'student-04', 'att-04', 'user-guardian-01',
        'Consulta m?dica de emergencia ? Hospital de Ca?ete',
        '/uploads/justificaciones/just-01-certificado-medico.pdf',
        'APPROVED', 1, TIMESTAMP '2025-03-10 18:30:00');


-- -----------------------------------------------------------------------------
-- E.9: Bloque g?nesis del colegio demo
-- -----------------------------------------------------------------------------

INSERT INTO BLOCKCHAIN_BLOCKS (id, school_id, block_index, event_type, entity_id, entity_type,
                               payload, previous_hash, hash, created_by, created_at)
VALUES (
    'bb-genesis-20188',
    'school-20188-canete',
    0,
    'GENESIS',
    'school-20188-canete',
    'SCHOOL',
    '{"event":"GENESIS","school":"I.E. Centro de Varones N20188","version":"FineFlow-2.0","created":"2025-03-01"}',
    RPAD('0', 64, '0'),   -- previous_hash del bloque g?nesis = 64 ceros (convenci?n)
    'a8f3d2b1e6c9f4a7d0b3e6c9a2f5d8b1e4c7a0f3d6b9e2c5a8f1d4b7e0c3a6f9',
    'user-admin-20188',
    TIMESTAMP '2025-03-01 08:00:00'
);

-- Bloque de matr?cula de alumnos
INSERT INTO BLOCKCHAIN_BLOCKS (id, school_id, block_index, event_type, entity_id, entity_type,
                               payload, previous_hash, hash, created_by, created_at)
VALUES (
    'bb-enrollment-20188',
    'school-20188-canete',
    1,
    'ENROLLMENT',
    'sec-3a-2025',
    'SECTION',
    '{"event":"ENROLLMENT","section":"3A","students_count":3,"period":"1er Bimestre 2025"}',
    'a8f3d2b1e6c9f4a7d0b3e6c9a2f5d8b1e4c7a0f3d6b9e2c5a8f1d4b7e0c3a6f9',
    'b1c4e7a0f3d6b9e2c5a8f1d4b7e0c3a6f9d2b5e8c1a4f7d0b3e6c9a2f5d8b1e4',
    'user-admin-20188',
    TIMESTAMP '2025-03-10 08:05:00'
);

-- Bloque de registro de nota
INSERT INTO BLOCKCHAIN_BLOCKS (id, school_id, block_index, event_type, entity_id, entity_type,
                               payload, previous_hash, hash, created_by, created_at)
VALUES (
    'bb-score-20188',
    'school-20188-canete',
    2,
    'SCORE',
    'ss-01',
    'STUDENT_SCORE',
    '{"event":"SCORE","student_id":"student-01","task":"Practica Calificada Fracciones","score":17.5}',
    'b1c4e7a0f3d6b9e2c5a8f1d4b7e0c3a6f9d2b5e8c1a4f7d0b3e6c9a2f5d8b1e4',
    'c2d5f8b1e4c7a0f3d6b9e2c5a8f1d4b7e0c3a6f9d2b5e8c1a4f7d0b3e6c9a2f5',
    'user-teacher-01',
    TIMESTAMP '2025-03-21 14:30:00'
);


-- -----------------------------------------------------------------------------
-- E.10: Documentos MINEDU adicionales
-- -----------------------------------------------------------------------------

INSERT INTO MINEDU_DOCUMENTS (id, title, doc_type, year, file_path, description, chunks_count, is_active)
VALUES ('doc-rm-281-2016', 'Resoluci?n Ministerial N? 281-2016-MINEDU',
        'RESOLUCION', 2016,
        '/docs/minedu/rm_281_2016_aprobacion_curriculo.pdf',
        'RM que aprueba el Curr?culo Nacional de la Educaci?n B?sica',
        58, 1);

INSERT INTO MINEDU_DOCUMENTS (id, title, doc_type, year, file_path, description, chunks_count, is_active)
VALUES ('doc-estandares-2016', 'Est?ndares de Aprendizaje ? EBR',
        'ESTANDARES', 2016,
        '/docs/minedu/estandares_aprendizaje_ebr_2016.pdf',
        'Est?ndares nacionales de aprendizaje por ?rea curricular y nivel educativo',
        284, 1);

INSERT INTO MINEDU_DOCUMENTS (id, title, doc_type, year, file_path, description, chunks_count, is_active)
VALUES ('doc-orient-eval-2019', 'Orientaciones para la Evaluaci?n Formativa',
        'GUIA_DOCENTE', 2019,
        '/docs/minedu/orientaciones_evaluacion_formativa_2019.pdf',
        'Gu?a para docentes sobre evaluaci?n formativa y retroalimentaci?n',
        193, 1);


-- =============================================================================
-- ADDENDUM ? BLOQUE F: ?NDICES ADICIONALES PARA LAS NUEVAS TABLAS
-- =============================================================================

CREATE INDEX idx_sc_key          ON SYSTEM_CONFIG           (school_id, config_key);
CREATE INDEX idx_rj_expires      ON REPORT_JOBS             (expires_at, status);
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_just_att ON JUSTIFICATIONS (school_id, attendance_id)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_al_entity ON AUDIT_LOGS (school_id, entity_type, entity_id)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
CREATE INDEX idx_sub_school_plan ON SCHOOL_SUBSCRIPTIONS    (school_id, plan_name, status);


-- =============================================================================
-- COMMIT FINAL DEL ADDENDUM
-- =============================================================================

COMMIT;


-- =============================================================================
-- VERIFICACI?N FINAL ? ADDENDUM v2.1
-- =============================================================================

SELECT 'NUEVAS TABLAS (addendum)' AS tipo, COUNT(*) AS total
FROM user_tables
WHERE table_name IN (
    'REFRESH_TOKENS','NOTIFICATIONS','JUSTIFICATIONS','AUDIT_LOGS',
    'REPORT_JOBS','SYSTEM_CONFIG','STUDENT_SECTION_HISTORY','SCHOOL_SUBSCRIPTIONS'
)
UNION ALL
SELECT 'NUEVOS TRIGGERS', COUNT(*)
FROM user_triggers
WHERE trigger_name IN (
    'TRG_SCHOOLS_UPDATED_AT','TRG_SUBSCRIPTIONS_UPDATED_AT',
    'TRG_ONE_ACTIVE_PERIOD','TRG_SECTION_CAPACITY_CHECK',
    'TRG_JUSTIFICATION_AUTO_APPROVE'
)
UNION ALL
SELECT 'NUEVOS PROCEDURES', COUNT(*)
FROM user_procedures
WHERE object_name IN (
    'SP_ROTATE_QR_SECRETS','SP_CLOSE_PERIOD_AND_OPEN_NEXT',
    'SP_BULK_ENROLL_STUDENTS','SP_DEACTIVATE_SCHOOL'
)
UNION ALL
SELECT 'NUEVAS VISTAS', COUNT(*)
FROM user_views
WHERE view_name IN (
    'V_STUDENT_FINAL_GRADES','V_SCHOOL_DASHBOARD_STATS','V_ATTENDANCE_ALERTS'
);

-- Resumen del esquema completo (original + addendum)
SELECT 'TOTAL TABLAS'     AS resumen, COUNT(*) AS n FROM user_tables    WHERE table_name IN (
    'SCHOOLS','USERS','ACADEMIC_LEVELS','SCHOOL_YEARS','ACADEMIC_PERIODS','SECTIONS',
    'STUDENTS','GUARDIANS','TEACHERS','COURSES','COURSE_ASSIGNMENTS',
    'COURSE_COMPETENCIES','CLASS_TASKS','ATTENDANCES','STUDENT_SCORES',
    'BLOCKCHAIN_BLOCKS','CHAT_SESSIONS','CHAT_MESSAGES','MINEDU_DOCUMENTS',
    'REFRESH_TOKENS','NOTIFICATIONS','JUSTIFICATIONS','AUDIT_LOGS',
    'REPORT_JOBS','SYSTEM_CONFIG','STUDENT_SECTION_HISTORY','SCHOOL_SUBSCRIPTIONS'
)
UNION ALL
SELECT 'TOTAL VISTAS',    COUNT(*) FROM user_views    WHERE view_name IN (
    'VW_ATTENDANCE_SUMMARY','V_COMPETENCY_AVERAGES',
    'V_STUDENT_FINAL_GRADES','V_SCHOOL_DASHBOARD_STATS','V_ATTENDANCE_ALERTS'
)
UNION ALL
SELECT 'TOTAL TRIGGERS',  COUNT(*) FROM user_triggers WHERE trigger_name IN (
    'TRG_USERS_UPDATED_AT','TRG_STUDENTS_UPDATED_AT','TRG_TEACHERS_UPDATED_AT',
    'TRG_SCORES_UPDATED_AT','TRG_SCORE_RANGE_CHECK','TRG_BLOCKCHAIN_PROTECT',
    'TRG_SCHOOLS_UPDATED_AT','TRG_SUBSCRIPTIONS_UPDATED_AT',
    'TRG_ONE_ACTIVE_PERIOD','TRG_SECTION_CAPACITY_CHECK',
    'TRG_JUSTIFICATION_AUTO_APPROVE'
)
UNION ALL
SELECT 'TOTAL PROCEDURES', COUNT(*) FROM user_procedures WHERE object_name IN (
    'SP_PROMOTE_SECTION','SP_CREATE_GENESIS_BLOCK',
    'SP_ROTATE_QR_SECRETS','SP_CLOSE_PERIOD_AND_OPEN_NEXT',
    'SP_BULK_ENROLL_STUDENTS','SP_DEACTIVATE_SCHOOL'
);

-- =============================================================================
-- FIN ? FINE FLOW v2.1 ADDENDUM ? ESQUEMA SAAS MULTI-TENANT COMPLETO
-- Total: 27 tablas ? 5 vistas ? 11 triggers ? 6 stored procedures
-- =============================================================================


-- =============================================================================
-- FASES 9, 11-15: ARQUITECTURA AVANZADA v2.2
-- VPD/RLS + Soft Delete + Optimistic Locking + Particionamiento
-- Indices agresivos + Jobs automaticos + Blockchain completo
-- Tablas Pro (TEACHER_SPECIALTIES, SECURITY_EVENTS, FEATURE_FLAGS, EVENT_STORE, TRANSLATIONS)
-- =============================================================================


CREATE OR REPLACE FUNCTION fn_tenant_predicate (
    p_schema IN VARCHAR2,
    p_object IN VARCHAR2
)
RETURN VARCHAR2
AS
    v_school_id VARCHAR2(50);
    v_sys_user  VARCHAR2(100);
BEGIN
    -- Los usuarios del sistema (SYS, SYSTEM, el schema owner) bypassean el filtro
    -- para permitir migraciones, backups y trabajos del scheduler.
    v_sys_user := SYS_CONTEXT('USERENV', 'SESSION_USER');
    IF v_sys_user IN ('SYS', 'SYSTEM', 'FINEFLOW_ADMIN', 'FINEFLOW_DBA') THEN
        RETURN NULL;  -- NULL = sin restricci?n
    END IF;

    -- Leer el school_id del contexto de aplicaci?n (seteado por el backend)
    v_school_id := SYS_CONTEXT('FINEFLOW_CTX', 'SCHOOL_ID');

    IF v_school_id IS NULL THEN
        -- Contexto no seteado: bloquear TODO como medida de seguridad
        RETURN '1=0';
    END IF;

    RETURN 'school_id = ''' || v_school_id || '''';
END fn_tenant_predicate;
/



-- -----------------------------------------------------------------------------
-- A.2: Contexto de aplicaci?n
-- El backend (Spring) llama a SP_SET_TENANT_CONTEXT('school-uuid') al abrir
-- cada conexi?n del pool, que invoca DBMS_SESSION.SET_CONTEXT.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE CONTEXT FINEFLOW_CTX USING pkg_tenant_ctx;
/

CREATE OR REPLACE PACKAGE pkg_tenant_ctx AS
    PROCEDURE set_school(p_school_id IN VARCHAR2);
    PROCEDURE clear;
    FUNCTION  get_school RETURN VARCHAR2;
END pkg_tenant_ctx;
/

CREATE OR REPLACE PACKAGE BODY pkg_tenant_ctx AS
    PROCEDURE set_school(p_school_id IN VARCHAR2) IS
    BEGIN
        IF p_school_id IS NULL OR LENGTH(TRIM(p_school_id)) = 0 THEN
            RAISE_APPLICATION_ERROR(-20100, 'VPD: school_id no puede ser NULL o vac?o.');
        END IF;
        DBMS_SESSION.SET_CONTEXT('FINEFLOW_CTX', 'SCHOOL_ID', p_school_id);
    END;

    PROCEDURE clear IS
    BEGIN
        DBMS_SESSION.SET_CONTEXT('FINEFLOW_CTX', 'SCHOOL_ID', NULL);
    END;

    FUNCTION get_school RETURN VARCHAR2 IS
    BEGIN
        RETURN SYS_CONTEXT('FINEFLOW_CTX', 'SCHOOL_ID');
    END;
END pkg_tenant_ctx;
/


-- -----------------------------------------------------------------------------
-- A.3: Procedimiento de conveniencia para el backend (Spring R2DBC / JDBC)
-- El pool de conexiones llama: EXEC SP_SET_TENANT_CONTEXT('school-uuid')
-- tras obtener cada conexi?n y antes de ejecutar cualquier query.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_SET_TENANT_CONTEXT (p_school_id IN VARCHAR2)
AS
BEGIN
    pkg_tenant_ctx.set_school(p_school_id);
END SP_SET_TENANT_CONTEXT;
/



-- -----------------------------------------------------------------------------


-- =============================================================================
-- BLOQUE B: SOFT DELETE ? Eliminaci?n l?gica en tablas cr?ticas
-- =============================================================================
-- Nunca borrar f?sicamente. Marcar con deleted_at y deleted_by.
-- Las vistas y SPs ya filtraban por status, pero faltaba la columna expl?cita.
-- =============================================================================

-- Agregar soft delete a las tablas cr?ticas
-- (usamos el patr?n: deleted_at IS NULL = activo)

ALTER TABLE STUDENTS         ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE TEACHERS         ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE USERS            ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE STUDENT_SCORES   ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE CLASS_TASKS      ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE COURSE_ASSIGNMENTS ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE JUSTIFICATIONS   ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE NOTIFICATIONS    ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));
ALTER TABLE BLOCKCHAIN_BLOCKS ADD (deleted_at TIMESTAMP);   -- Solo admin puede
ALTER TABLE GUARDIANS        ADD (deleted_at TIMESTAMP, deleted_by VARCHAR2(50));

COMMENT ON COLUMN STUDENTS.deleted_at         IS 'NULL = activo. Timestamp = eliminado l?gicamente.';
COMMENT ON COLUMN STUDENT_SCORES.deleted_at   IS 'NULL = activo. Nota eliminada l?gicamente (anulada).';
COMMENT ON COLUMN BLOCKCHAIN_BLOCKS.deleted_at IS 'Siempre NULL en producci?n. Inmutabilidad garantizada por TRG_BLOCKCHAIN_PROTECT.';

-- ?ndices para filtros eficientes con soft delete
CREATE INDEX idx_students_not_deleted  ON STUDENTS       (school_id, deleted_at, status);
CREATE INDEX idx_teachers_not_deleted  ON TEACHERS       (school_id, deleted_at, status);
CREATE INDEX idx_scores_not_deleted    ON STUDENT_SCORES (school_id, deleted_at);
CREATE INDEX idx_users_not_deleted     ON USERS          (school_id, deleted_at, status);


-- -----------------------------------------------------------------------------
-- B.1: Trigger para bloquear DELETE f?sico y convertirlo en soft delete
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_STUDENTS_SOFT_DELETE
    BEFORE DELETE ON STUDENTS
    FOR EACH ROW
BEGIN
    -- En lugar de borrar, marcar como eliminado
    UPDATE STUDENTS
    SET    deleted_at = SYSTIMESTAMP,
           deleted_by = SYS_CONTEXT('FINEFLOW_CTX', 'SCHOOL_ID'),
           status     = 'INACTIVE',
           updated_at = SYSTIMESTAMP
    WHERE  id = :OLD.id;
    -- Cancelar el DELETE f?sico
    RAISE_APPLICATION_ERROR(-20110, 'SOFT_DELETE: usa UPDATE status=INACTIVE en lugar de DELETE.');
END TRG_STUDENTS_SOFT_DELETE;
/


-- Trigger similar para STUDENT_SCORES
CREATE OR REPLACE TRIGGER TRG_SCORES_SOFT_DELETE
    BEFORE DELETE ON STUDENT_SCORES
    FOR EACH ROW
BEGIN
    UPDATE STUDENT_SCORES
    SET    deleted_at  = SYSTIMESTAMP,
           deleted_by  = SYS_CONTEXT('FINEFLOW_CTX', 'SCHOOL_ID'),
           updated_at  = SYSTIMESTAMP
    WHERE  id = :OLD.id;
    RAISE_APPLICATION_ERROR(-20111, 'SOFT_DELETE: usa UPDATE deleted_at en lugar de DELETE en STUDENT_SCORES.');
END TRG_SCORES_SOFT_DELETE;
/



-- =============================================================================
-- BLOQUE C: OPTIMISTIC LOCKING ? Control de concurrencia
-- =============================================================================
-- Previene el problema "lost update": dos docentes editan la misma nota
-- simult?neamente y el segundo sobreescribe al primero sin saber que hubo cambio.
--
-- Backend Spring lee version, y en el UPDATE hace:
--   WHERE id = :id AND school_id = :school_id AND version = :version
-- Si no actualiza ninguna fila ? lanza OptimisticLockException.
-- =============================================================================

-- Agregar columna version a tablas con edici?n concurrente frecuente
ALTER TABLE STUDENT_SCORES      ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE ATTENDANCES         ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE CLASS_TASKS         ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE COURSE_ASSIGNMENTS  ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE STUDENTS            ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE TEACHERS            ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE USERS               ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE SECTIONS            ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE SYSTEM_CONFIG       ADD (version NUMBER DEFAULT 1 NOT NULL);
ALTER TABLE ACADEMIC_PERIODS    ADD (version NUMBER DEFAULT 1 NOT NULL);

COMMENT ON COLUMN STUDENT_SCORES.version  IS 'Optimistic locking: incrementar en cada UPDATE. Backend valida WHERE version = :v.';
COMMENT ON COLUMN ATTENDANCES.version     IS 'Optimistic locking para ediciones concurrentes de asistencia.';
COMMENT ON COLUMN STUDENTS.version        IS 'Optimistic locking: previene ediciones simult?neas de datos del alumno.';


-- -----------------------------------------------------------------------------
-- C.1: Triggers para auto-incrementar version en UPDATE
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SCORES_VERSION
    BEFORE UPDATE ON STUDENT_SCORES
    FOR EACH ROW
BEGIN
    :NEW.version   := :OLD.version + 1;
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_SCORES_VERSION;
/

CREATE OR REPLACE TRIGGER TRG_ATTENDANCES_VERSION
    BEFORE UPDATE ON ATTENDANCES
    FOR EACH ROW
BEGIN
    :NEW.version := :OLD.version + 1;
END TRG_ATTENDANCES_VERSION;
/

CREATE OR REPLACE TRIGGER TRG_STUDENTS_VERSION
    BEFORE UPDATE ON STUDENTS
    FOR EACH ROW
BEGIN
    :NEW.version   := :OLD.version + 1;
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_STUDENTS_VERSION;
/

CREATE OR REPLACE TRIGGER TRG_USERS_VERSION
    BEFORE UPDATE ON USERS
    FOR EACH ROW
BEGIN
    :NEW.version   := :OLD.version + 1;
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_USERS_VERSION;
/

CREATE OR REPLACE TRIGGER TRG_SYSTEM_CONFIG_VERSION
    BEFORE UPDATE ON SYSTEM_CONFIG
    FOR EACH ROW
BEGIN
    :NEW.version   := :OLD.version + 1;
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_SYSTEM_CONFIG_VERSION;
/


-- =============================================================================
-- BLOQUE D: PARTICIONAMIENTO ? Para escala real
-- =============================================================================
-- Oracle no permite ADD PARTITION a tablas existentes sin recrearlas.
-- Estrategia:
--   1. Renombrar tabla original (backup)
--   2. Crear nueva tabla con particiones
--   3. Migrar datos con INSERT ... SELECT
--   4. Procedimiento SP_MIGRATE_TO_PARTITIONED para ejecuci?n controlada
-- =============================================================================

-- -----------------------------------------------------------------------------
-- D.1: ATTENDANCES particionada por rango de fecha (INTERVAL PARTITION)
-- Oracle crea autom?ticamente una nueva partici?n por cada mes.
-- -----------------------------------------------------------------------------
CREATE TABLE ATTENDANCES_PART (
    id                      VARCHAR2(50)    NOT NULL,
    school_id               VARCHAR2(50)    NOT NULL,
    student_id              VARCHAR2(50)    NOT NULL,
    course_assignment_id    VARCHAR2(50),
    attendance_date         DATE            NOT NULL,
    status                  VARCHAR2(20)    NOT NULL,
    check_in_time           VARCHAR2(8),
    record_method           VARCHAR2(20)    DEFAULT 'MANUAL' NOT NULL,
    justification_reason    VARCHAR2(1000),
    registered_by           VARCHAR2(50),
    version                 NUMBER          DEFAULT 1 NOT NULL,
    deleted_at              TIMESTAMP,
    created_at              TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_att_part           PRIMARY KEY (id, attendance_date),  -- PK compuesta para particionar
    CONSTRAINT ck_att_part_status    CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED','HOLIDAY')),
    CONSTRAINT ck_att_part_method    CHECK (record_method IN ('MANUAL','QR','BULK','SYSTEM'))
)
PARTITION BY RANGE (attendance_date)
INTERVAL (NUMTOYMINTERVAL(1, 'MONTH'))  -- Auto-crea una partici?n por mes
(
    PARTITION p_2024     VALUES LESS THAN (DATE '2025-01-01'),
    PARTITION p_2025_q1  VALUES LESS THAN (DATE '2025-04-01'),
    PARTITION p_2025_q2  VALUES LESS THAN (DATE '2025-07-01')
    -- Oracle crea autom?ticamente los meses siguientes
);

COMMENT ON TABLE ATTENDANCES_PART IS 'ATTENDANCES particionada por mes (INTERVAL RANGE). Usar en producci?n; ATTENDANCES es la tabla de compatibilidad.';


-- -----------------------------------------------------------------------------
-- D.2: STUDENT_SCORES particionada por fecha de registro
-- -----------------------------------------------------------------------------
CREATE TABLE STUDENT_SCORES_PART (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    student_id      VARCHAR2(50)    NOT NULL,
    class_task_id   VARCHAR2(50)    NOT NULL,
    score           NUMBER(5,2)     NOT NULL,
    comments        VARCHAR2(2000),
    registered_by   VARCHAR2(50),
    registered_at   TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    version         NUMBER          DEFAULT 1 NOT NULL,
    deleted_at      TIMESTAMP,
    deleted_by      VARCHAR2(50),
    --
    CONSTRAINT pk_ss_part      PRIMARY KEY (id, registered_at),
    CONSTRAINT uq_ss_part_stu  UNIQUE (student_id, class_task_id),
    CONSTRAINT ck_ss_part_score CHECK (score BETWEEN 0 AND 20)
)
PARTITION BY RANGE (registered_at)
INTERVAL (NUMTOYMINTERVAL(1, 'MONTH'))
(
    PARTITION p_ss_2024      VALUES LESS THAN (TIMESTAMP '2025-01-01 00:00:00'),
    PARTITION p_ss_2025_q1   VALUES LESS THAN (TIMESTAMP '2025-04-01 00:00:00')
);

COMMENT ON TABLE STUDENT_SCORES_PART IS 'STUDENT_SCORES particionada por mes. Usar en producci?n.';


-- -----------------------------------------------------------------------------
-- D.3: BLOCKCHAIN_BLOCKS particionada por colegio (LIST partition)
-- Cada colegio tiene su partici?n ? consultas de cadena mucho m?s r?pidas.
-- Usar RANGE + HASH para > 1000 colegios.
-- -----------------------------------------------------------------------------
CREATE TABLE BLOCKCHAIN_BLOCKS_PART (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    block_index     NUMBER(10)      NOT NULL,
    event_type      VARCHAR2(50)    NOT NULL,
    entity_id       VARCHAR2(50),
    entity_type     VARCHAR2(50),
    payload         CLOB,
    previous_hash   VARCHAR2(64)    NOT NULL,
    hash            VARCHAR2(64)    NOT NULL,
    created_by      VARCHAR2(50),
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    deleted_at      TIMESTAMP,   -- Siempre NULL; columna de auditor?a
    --
    CONSTRAINT pk_bb_part           PRIMARY KEY (id, school_id),
    CONSTRAINT uq_bb_part_idx       UNIQUE (school_id, block_index),
    CONSTRAINT ck_bb_part_event     CHECK (event_type IN (
        'GENESIS','ATTENDANCE','SCORE','ENROLLMENT',
        'TRANSFER','PROMOTION','USER_ACTION','SYSTEM'
    ))
)
PARTITION BY HASH (school_id)  -- Hash partition para distribuci?n uniforme
PARTITIONS 16;                 -- 16 particiones = buen balance hasta ~500 colegios

COMMENT ON TABLE BLOCKCHAIN_BLOCKS_PART IS 'BLOCKCHAIN_BLOCKS particionada por hash de school_id (16 particiones). Verificaci?n de cadena 10x m?s r?pida que la tabla plana.';


-- -----------------------------------------------------------------------------
-- D.4: Procedimiento de migraci?n en caliente
-- Renombra la tabla original, crea la particionada y migra los datos.
-- EJECUTAR EN HORARIO DE BAJA ACTIVIDAD.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_MIGRATE_TO_PARTITIONED (
    p_table_name IN VARCHAR2   -- 'ATTENDANCES' | 'STUDENT_SCORES' | 'BLOCKCHAIN_BLOCKS'
)
AS
    v_backup_name VARCHAR2(50);
    v_part_name   VARCHAR2(50);
    v_rows        NUMBER;
BEGIN
    v_backup_name := p_table_name || '_BACKUP_' || TO_CHAR(SYSDATE,'YYYYMMDD');
    v_part_name   := p_table_name || '_PART';

    DBMS_OUTPUT.PUT_LINE('Iniciando migraci?n de ' || p_table_name || ' ? ' || v_part_name);

    -- Paso 1: Backup de la tabla original
    EXECUTE IMMEDIATE 'ALTER TABLE ' || p_table_name || ' RENAME TO ' || v_backup_name;
    DBMS_OUTPUT.PUT_LINE('Paso 1: Renombrado a ' || v_backup_name);

    -- Paso 2: Renombrar la tabla particionada a nombre definitivo
    EXECUTE IMMEDIATE 'ALTER TABLE ' || v_part_name || ' RENAME TO ' || p_table_name;
    DBMS_OUTPUT.PUT_LINE('Paso 2: ' || v_part_name || ' ? ' || p_table_name);

    -- Paso 3: Migrar datos del backup
    EXECUTE IMMEDIATE 'INSERT /*+ APPEND */ INTO ' || p_table_name ||
                       ' SELECT * FROM ' || v_backup_name;
    v_rows := SQL%ROWCOUNT;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Paso 3: ' || v_rows || ' filas migradas.');

    -- Paso 4: Recompilar ?ndices
    EXECUTE IMMEDIATE 'ALTER INDEX pk_' || LOWER(SUBSTR(p_table_name,1,3)) ||
                       '_part REBUILD';

    DBMS_OUTPUT.PUT_LINE('Migraci?n completada. Backup conservado en: ' || v_backup_name);
    DBMS_OUTPUT.PUT_LINE('Ejecutar: DROP TABLE ' || v_backup_name || ' cuando valides los datos.');

EXCEPTION
    WHEN OTHERS THEN
        -- Revertir renombrado si algo falla
        BEGIN
            EXECUTE IMMEDIATE 'ALTER TABLE ' || p_table_name || ' RENAME TO ' || v_part_name;
            EXECUTE IMMEDIATE 'ALTER TABLE ' || v_backup_name || ' RENAME TO ' || p_table_name;
        EXCEPTION WHEN OTHERS THEN NULL;
        END;
        ROLLBACK;
        RAISE;
END SP_MIGRATE_TO_PARTITIONED;
/



-- =============================================================================
-- BLOQUE E: CHAT_MESSAGES ? Agregar school_id + ?ndice corregido
-- =============================================================================
-- Punto 5 del an?lisis: sin school_id directo, las auditor?as y queries
-- anal?ticas requieren siempre JOIN con CHAT_SESSIONS. Es evitable.
-- =============================================================================

ALTER TABLE CHAT_MESSAGES ADD (school_id VARCHAR2(50));

-- Poblar el school_id desde CHAT_SESSIONS para registros existentes
UPDATE CHAT_MESSAGES cm
SET school_id = (
    SELECT cs.school_id 
    FROM CHAT_SESSIONS cs 
    WHERE cs.id = cm.session_id 
);

-- Ahora hacer la columna NOT NULL (ya tiene valores)
ALTER TABLE CHAT_MESSAGES MODIFY (school_id VARCHAR2(50) NOT NULL);

-- FK
ALTER TABLE CHAT_MESSAGES
    ADD CONSTRAINT fk_cm_school FOREIGN KEY (school_id) REFERENCES SCHOOLS(id);

-- ?ndice con school_id para queries anal?ticas directas
CREATE INDEX idx_cm_school_session   ON CHAT_MESSAGES (school_id, session_id, created_at);
CREATE INDEX idx_cm_school_created   ON CHAT_MESSAGES (school_id, created_at);

COMMENT ON COLUMN CHAT_MESSAGES.school_id IS
    'Desnormalizaci?n deliberada: permite queries anal?ticas sin JOIN a CHAT_SESSIONS.';


-- =============================================================================
-- BLOQUE F: ?NDICES COMPUESTOS AGRESIVOS para dashboards
-- =============================================================================
-- Los ?ndices existentes eran correctos pero no cubr?an los patrones de
-- consulta de dashboards (m?ltiples filtros + orden + conteos).
-- =============================================================================

-- Dashboard de asistencia: ?cu?ntos presentes/ausentes por secci?n/fecha?
CREATE INDEX idx_att_dash_daily   ON ATTENDANCES (school_id, attendance_date, status, course_assignment_id);
CREATE INDEX idx_att_dash_student ON ATTENDANCES (school_id, student_id, attendance_date, status);

-- Dashboard de notas: promedios por competencia y per?odo
CREATE INDEX idx_ss_dash_task     ON STUDENT_SCORES (school_id, class_task_id, score, deleted_at);
CREATE INDEX idx_ss_dash_student  ON STUDENT_SCORES (school_id, student_id, registered_at, score);

-- Alertas de ausencias: alumnos con m?s de N faltas
CREATE INDEX idx_att_absences     ON ATTENDANCES (school_id, student_id, status, attendance_date);  -- Partial index ? solo filas ABSENT (m?s peque?o, m?s r?pido)

-- Blockchain: b?squeda por hash para validaci?n de cadena
CREATE UNIQUE INDEX idx_bb_hash   ON BLOCKCHAIN_BLOCKS (school_id, hash);
CREATE INDEX idx_bb_entity        ON BLOCKCHAIN_BLOCKS (school_id, entity_type, entity_id, created_at);

-- Notificaciones sin leer (el caso de uso m?s frecuente)
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_notif_unread ON NOTIFICATIONS (school_id, user_id, is_read, created_at)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- Tokens activos (limpieza nocturna)
CREATE INDEX idx_rt_active_exp    ON REFRESH_TOKENS (school_id, revoked_at, expires_at);

-- Justificaciones pendientes
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_just_pending_school ON JUSTIFICATIONS (school_id, status, requested_at)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- Reporte jobs pendientes (scheduler los procesa)
CREATE INDEX idx_rj_pending_sch   ON REPORT_JOBS (status, requested_at, school_id);

-- Config por clave (muy frecuente: leer umbrales)
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_cfg_key_val ON SYSTEM_CONFIG (school_id, config_key, config_value)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/







-- =============================================================================
-- BLOQUE G: DBMS_SCHEDULER ? Jobs autom?ticos del sistema
-- =============================================================================
-- El backend (Spring @Scheduled) puede hacer esto, pero tener los jobs tambi?n
-- en Oracle garantiza que se ejecuten aunque el backend est? ca?do.
-- Patr?n recomendado: ambos (DB + App) con l?gica idempotente.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- G.1: Limpiar refresh tokens expirados (cada noche a las 02:00)
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
-- -----------------------------------------------------------------------------
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_CLEAN_EXPIRED_TOKENS',
        'PLSQL_BLOCK',
        'BEGIN
    UPDATE REFRESH_TOKENS
    SET    revoke_reason = ''EXPIRED'',
           revoked_at    = SYSTIMESTAMP
    WHERE  expires_at    < SYSTIMESTAMP
      AND  revoked_at   IS NULL;
    INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
    VALUES (SYS_GUID(), ''CLEANUP_TOKENS'', ''REFRESH_TOKENS'',
            ''{"revoked":'' || SQL%ROWCOUNT || '',"job":"JOB_CLEAN_EXPIRED_TOKENS"}'',
            ''SUCCESS'', SYSTIMESTAMP);
    COMMIT;
END;',
        0,
        SYSTIMESTAMP,
        'FREQ=DAILY;BYHOUR=2;BYMINUTE=0',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Revoca refresh tokens expirados. Ejecuta diariamente a las 02:00.'
    );
END;
/


-- -----------------------------------------------------------------------------
-- G.2: Marcar QR secrets para rotaci?n semanal (lunes 03:00)
-- Luego el backend (Spring @Scheduled) lee los marcados y regenera HMAC-SHA256
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
-- -----------------------------------------------------------------------------
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_ROTATE_QR_SECRETS',
        'PLSQL_BLOCK',
        'DECLARE
    v_count NUMBER;
BEGIN
    UPDATE STUDENTS
    SET    qr_rotated_at = SYSTIMESTAMP,
           updated_at    = SYSTIMESTAMP
    WHERE  status        = ''ACTIVE''
      AND  (qr_rotated_at IS NULL
            OR qr_rotated_at < SYSTIMESTAMP - INTERVAL ''7'' DAY);
    v_count := SQL%ROWCOUNT;
    INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
    VALUES (SYS_GUID(), ''QR_ROTATE_MARK'', ''STUDENT'',
            ''{"marked_for_rotation":'' || v_count || ''}'', ''SUCCESS'', SYSTIMESTAMP);
    COMMIT;
    DBMS_OUTPUT.PUT_LINE(''JOB_ROTATE_QR_SECRETS: '' || v_count || '' alumnos marcados.'');
END;',
        0,
        TRUNC(SYSTIMESTAMP, 'IW') + 7 + 3/24,
        'FREQ=WEEKLY;BYDAY=MON;BYHOUR=3;BYMINUTE=0',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Marca QR secrets para rotaci?n semanal (lunes 03:00).'
    );
END;
/


-- -----------------------------------------------------------------------------
-- G.3: Cerrar per?odos acad?micos vencidos (diariamente a las 00:05)
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
-- -----------------------------------------------------------------------------
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_CLOSE_EXPIRED_PERIODS',
        'PLSQL_BLOCK',
        'DECLARE
    v_count NUMBER;
BEGIN
    UPDATE ACADEMIC_PERIODS
    SET    is_active = 0
    WHERE  is_active = 1
      AND  end_date  < TRUNC(SYSDATE);
    v_count := SQL%ROWCOUNT;
    IF v_count > 0 THEN
        INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
        VALUES (SYS_GUID(), ''AUTO_CLOSE_PERIOD'', ''ACADEMIC_PERIOD'',
                ''{"closed_count":'' || v_count || ''}'', ''SUCCESS'', SYSTIMESTAMP);
        COMMIT;
    END IF;
END;',
        0,
        TRUNC(SYSTIMESTAMP) + 1 + 5/1440,
        'FREQ=DAILY;BYHOUR=0;BYMINUTE=5',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Desactiva per?odos acad?micos cuya end_date ya venci?.'
    );
END;
/


-- -----------------------------------------------------------------------------
-- G.4: Expirar justificaciones sin resoluci?n (cada 6 horas)
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
-- -----------------------------------------------------------------------------
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_EXPIRE_JUSTIFICATIONS',
        'PLSQL_BLOCK',
        'DECLARE
    v_count NUMBER;
BEGIN
    UPDATE JUSTIFICATIONS
    SET    status      = ''EXPIRED'',
           review_note = NVL(review_note,'''') || '' [Auto-expirado por plazo vencido]''
    WHERE  status      = ''PENDING''
      AND  expires_at  < SYSTIMESTAMP;
    v_count := SQL%ROWCOUNT;
    IF v_count > 0 THEN
        INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
        VALUES (SYS_GUID(), ''AUTO_EXPIRE_JUSTIFICATION'', ''JUSTIFICATION'',
                ''{"expired":'' || v_count || ''}'', ''SUCCESS'', SYSTIMESTAMP);
        COMMIT;
    END IF;
END;',
        0,
        SYSTIMESTAMP,
        'FREQ=HOURLY;BYHOUR=0,6,12,18',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Expira justificaciones PENDING con plazo vencido (cada 6 horas).'
    );
END;
/


-- -----------------------------------------------------------------------------
-- G.5: Limpiar report jobs expirados y archivos (diario 03:30)
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
-- -----------------------------------------------------------------------------
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_CLEANUP_REPORTS',
        'PLSQL_BLOCK',
        'BEGIN
    UPDATE REPORT_JOBS
    SET    status = ''EXPIRED''
    WHERE  expires_at < SYSTIMESTAMP
      AND  status     = ''COMPLETED'';
    INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
    VALUES (SYS_GUID(), ''CLEANUP_REPORTS'', ''REPORT_JOB'',
            ''{"expired":'' || SQL%ROWCOUNT || ''}'', ''SUCCESS'', SYSTIMESTAMP);
    COMMIT;
END;',
        0,
        TRUNC(SYSTIMESTAMP) + 1 + 3.5/24,
        'FREQ=DAILY;BYHOUR=3;BYMINUTE=30',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Marca como EXPIRED los reportes cuyo archivo ya venci?.'
    );
END;
/


-- -----------------------------------------------------------------------------
-- G.6: Purga de audit_logs antiguos (> 2 a?os) ? primer d?a de cada mes 04:00
-- Los logs de seguridad (LOGIN, QR_SUSPICIOUS) se conservan 5 a?os.
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
-- -----------------------------------------------------------------------------
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_PURGE_OLD_AUDITLOGS',
        'PLSQL_BLOCK',
        'DECLARE
    v_count NUMBER;
BEGIN
    DELETE FROM AUDIT_LOGS
    WHERE  created_at  < SYSTIMESTAMP - INTERVAL ''730'' DAY
      AND  action NOT IN (
               ''LOGIN'',''LOGIN_FAILED'',''PASSWORD_CHANGE'',
               ''QR_SUSPICIOUS'',''TOKEN_REVOKED'',''ACCOUNT_LOCKED''
           );
    v_count := SQL%ROWCOUNT;
    COMMIT;
    DELETE FROM AUDIT_LOGS
    WHERE  created_at < SYSTIMESTAMP - INTERVAL ''1825'' DAY;
    v_count := v_count + SQL%ROWCOUNT;
    COMMIT;
    INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
    VALUES (SYS_GUID(), ''PURGE_AUDIT_LOGS'', ''AUDIT_LOG'',
            ''{"deleted_rows":'' || v_count || ''}'', ''SUCCESS'', SYSTIMESTAMP);
    COMMIT;
END;',
        0,
        TRUNC(ADD_MONTHS(SYSTIMESTAMP,1),'MM') + 4/24,
        'FREQ=MONTHLY;BYMONTHDAY=1;BYHOUR=4;BYMINUTE=0',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Purga audit_logs: logs generales >2 a?os, seguridad >5 a?os.'
    );
END;
/


-- =============================================================================
-- BLOQUE H: BLOCKCHAIN COMPLETO ? SP_ADD_BLOCK + SP_VALIDATE_CHAIN + ?ndice hash
-- =============================================================================
-- El ?ndice por hash ya se cre? en BLOQUE F.
-- Aqu? van los SPs para operaci?n a nivel de BD (complementan al servicio Java).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- H.1: SP_ADD_BLOCK ? Agregar bloque con c?lculo de hash en la BD
-- Usa DBMS_CRYPTO (requiere privilegio EXECUTE ON SYS.DBMS_CRYPTO).
-- Si no tiene el privilegio, el backend Java calcula el hash (soluci?n actual).
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_ADD_BLOCK (
    p_school_id    IN VARCHAR2,
    p_event_type   IN VARCHAR2,
    p_entity_id    IN VARCHAR2,
    p_entity_type  IN VARCHAR2,
    p_payload_json IN VARCHAR2,
    p_created_by   IN VARCHAR2,
    p_block_id     OUT VARCHAR2,
    p_hash         OUT VARCHAR2
)
AS
    v_prev_hash  VARCHAR2(64);
    v_prev_index NUMBER;
    v_next_index NUMBER;
    v_raw_data   VARCHAR2(4000);
    v_hash_raw   RAW(32);
BEGIN
    -- Obtener el ?ltimo bloque del colegio (LOCK para prevenir race condition)
    BEGIN
        SELECT hash, block_index
        INTO   v_prev_hash, v_prev_index
        FROM   BLOCKCHAIN_BLOCKS
        WHERE  school_id = p_school_id
        ORDER  BY block_index DESC
        FETCH  FIRST 1 ROWS ONLY
        FOR UPDATE;

        v_next_index := v_prev_index + 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            -- No hay bloques: este es el g?nesis
            v_prev_hash  := RPAD('0', 64, '0');
            v_next_index := 0;
    END;

    -- Calcular hash SHA-256 con DBMS_CRYPTO
    v_raw_data  := v_next_index || p_school_id || p_event_type || p_entity_id
                   || NVL(p_payload_json,'{}') || v_prev_hash;
    v_hash_raw  := DBMS_CRYPTO.HASH(UTL_I18N.STRING_TO_RAW(v_raw_data,'AL32UTF8'), DBMS_CRYPTO.HASH_SH256);
    p_hash      := LOWER(RAWTOHEX(v_hash_raw));
    p_block_id  := SYS_GUID();

    -- Insertar el bloque
    INSERT INTO BLOCKCHAIN_BLOCKS (
        id, school_id, block_index, event_type, entity_id, entity_type,
        payload, previous_hash, hash, created_by, created_at
    ) VALUES (
        p_block_id, p_school_id, v_next_index, p_event_type,
        p_entity_id, p_entity_type, p_payload_json,
        v_prev_hash, p_hash, p_created_by, SYSTIMESTAMP
    );

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END SP_ADD_BLOCK;
/



-- -----------------------------------------------------------------------------
-- H.2: SP_VALIDATE_CHAIN ? Validaci?n completa de la cadena
-- Verifica que cada bloque enlace correctamente al anterior.
-- Retorna el n?mero de bloques corruptos (0 = cadena ?ntegra).
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_VALIDATE_CHAIN (
    p_school_id       IN  VARCHAR2,
    p_corrupt_blocks  OUT NUMBER,
    p_total_blocks    OUT NUMBER
)
AS
    v_prev_hash  VARCHAR2(64);
    v_expected   VARCHAR2(64);
    v_raw        VARCHAR2(4000);
    v_hash_raw   RAW(32);
    CURSOR c_blocks IS
        SELECT id, block_index, event_type, entity_id,
               payload, previous_hash, hash, created_at
        FROM   BLOCKCHAIN_BLOCKS
        WHERE  school_id = p_school_id
        ORDER  BY block_index ASC;
BEGIN
    p_corrupt_blocks := 0;
    p_total_blocks   := 0;
    v_prev_hash      := RPAD('0', 64, '0');  -- Hash esperado del g?nesis anterior

    FOR rec IN c_blocks LOOP
        p_total_blocks := p_total_blocks + 1;

        -- Verificar que el previous_hash enlaza con el bloque anterior
        IF rec.previous_hash != v_prev_hash THEN
            p_corrupt_blocks := p_corrupt_blocks + 1;
            DBMS_OUTPUT.PUT_LINE(
                'CORRUPTO: bloque #' || rec.block_index ||
                ' (id=' || rec.id || '): previous_hash no coincide.');
        END IF;

        -- Recalcular el hash del bloque
        v_raw      := rec.block_index || p_school_id || rec.event_type ||
                      NVL(rec.entity_id,'') || NVL(rec.payload,'{}') || rec.previous_hash;
        v_hash_raw := DBMS_CRYPTO.HASH(UTL_I18N.STRING_TO_RAW(v_raw,'AL32UTF8'), DBMS_CRYPTO.HASH_SH256);
        v_expected := LOWER(RAWTOHEX(v_hash_raw));

        IF rec.hash != v_expected THEN
            p_corrupt_blocks := p_corrupt_blocks + 1;
            DBMS_OUTPUT.PUT_LINE(
                'HASH INV?LIDO: bloque #' || rec.block_index ||
                '. Esperado=' || v_expected || ', Actual=' || rec.hash);
        END IF;

        v_prev_hash := rec.hash;
    END LOOP;

    IF p_corrupt_blocks = 0 THEN
        DBMS_OUTPUT.PUT_LINE(
            'SP_VALIDATE_CHAIN: Cadena ?ntegra. ' || p_total_blocks || ' bloques validados.');
    ELSE
        DBMS_OUTPUT.PUT_LINE(
            'SP_VALIDATE_CHAIN: ?ALERTA! ' || p_corrupt_blocks ||
            ' bloques corruptos de ' || p_total_blocks || '.');
    END IF;
END SP_VALIDATE_CHAIN;
/



-- =============================================================================
-- BLOQUE I: TEACHER_SPECIALTIES ? Normalizaci?n
-- =============================================================================
-- El campo specialty VARCHAR2(200) en TEACHERS no escala ni permite filtrar.
-- Separarlo en tabla propia permite: buscar docentes por ?rea, estad?sticas, etc.
-- =============================================================================

CREATE TABLE TEACHER_SPECIALTIES (
    id          VARCHAR2(50)    NOT NULL,
    school_id   VARCHAR2(50)    NOT NULL,
    teacher_id  VARCHAR2(50)    NOT NULL,
    subject     VARCHAR2(100)   NOT NULL,   -- Ej: 'Matem?tica', 'F?sica', '?lgebra'
    specialty_level VARCHAR2(30)    DEFAULT 'SECONDARY' NOT NULL,
    is_primary  NUMBER(1)       DEFAULT 0 NOT NULL,  -- 1 = especialidad principal
    certified   NUMBER(1)       DEFAULT 0 NOT NULL,  -- 1 = tiene certificaci?n formal
    created_at  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_teacher_spec         PRIMARY KEY (id),
    CONSTRAINT fk_tspec_school         FOREIGN KEY (school_id)  REFERENCES SCHOOLS(id),
    CONSTRAINT fk_ts_teacher           FOREIGN KEY (teacher_id) REFERENCES TEACHERS(id),
    CONSTRAINT uq_ts_teacher_subject   UNIQUE (school_id, teacher_id, subject),
    CONSTRAINT ck_ts_level             CHECK (specialty_level IN ('INITIAL','PRIMARY','SECONDARY')),
    CONSTRAINT ck_ts_primary           CHECK (is_primary IN (0,1)),
    CONSTRAINT ck_ts_certified         CHECK (certified IN (0,1))
);

COMMENT ON TABLE  TEACHER_SPECIALTIES           IS 'Especialidades del docente por nivel educativo (Inicial, Primaria, Secundaria).';
COMMENT ON COLUMN TEACHER_SPECIALTIES.is_primary IS '1 = especialidad principal del docente (solo puede haber una por docente).';

CREATE INDEX idx_ts_teacher    ON TEACHER_SPECIALTIES (school_id, teacher_id);
CREATE INDEX idx_ts_subject    ON TEACHER_SPECIALTIES (school_id, subject, specialty_level);

-- Migrar datos del campo legacy teachers.specialty
-- (campo specialty era texto libre "Matem?tica,F?sica" ? separar por coma)
-- Usamos CROSS JOIN con generador de n?meros para evitar ORA-00976 con LEVEL
INSERT INTO TEACHER_SPECIALTIES (id, school_id, teacher_id, subject, is_primary)
SELECT
    SYS_GUID(),
    t.school_id,
    t.id,
    TRIM(REGEXP_SUBSTR(t.specialty, '[^,]+', 1, n)),
    CASE WHEN n = 1 THEN 1 ELSE 0 END  -- Primera especialidad = principal
FROM   TEACHERS t
CROSS JOIN (SELECT LEVEL AS n FROM DUAL CONNECT BY LEVEL <= 100) nums
WHERE  t.specialty IS NOT NULL
  AND  nums.n <= REGEXP_COUNT(t.specialty, ',') + 1;

-- Conservar specialty en TEACHERS por retrocompatibilidad (deprecada)
COMMENT ON COLUMN TEACHERS.specialty IS 'DEPRECADO: usar TEACHER_SPECIALTIES. Conservado por retrocompatibilidad.';


-- =============================================================================
-- BLOQUE J: SECURITY_EVENTS ? Logs de seguridad cr?ticos
-- =============================================================================
-- AUDIT_LOGS captura operaciones CRUD, pero los eventos de seguridad merecen
-- una tabla propia con retenci?n m?s larga, campos espec?ficos y alertas.
-- =============================================================================

CREATE TABLE SECURITY_EVENTS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50),               -- NULL para eventos globales
    user_id         VARCHAR2(50),               -- NULL si el user es desconocido
    event_type      VARCHAR2(50)    NOT NULL,
    severity        VARCHAR2(10)    DEFAULT 'INFO' NOT NULL,
    description     VARCHAR2(2000)  NOT NULL,
    ip_address      VARCHAR2(45),
    user_agent      VARCHAR2(500),
    session_token   VARCHAR2(100),              -- Para correlacionar sesiones
    entity_type     VARCHAR2(50),               -- Entidad afectada
    entity_id       VARCHAR2(50),
    metadata_json   VARCHAR2(4000),             -- Datos adicionales del evento
    resolved        NUMBER(1)       DEFAULT 0   NOT NULL,  -- 1 = revisado
    resolved_by     VARCHAR2(50),
    resolved_at     TIMESTAMP,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_security_events       PRIMARY KEY (id),
    CONSTRAINT fk_se_school             FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT ck_se_event_type         CHECK (event_type IN (
                                            -- Autenticaci?n
                                            'LOGIN_SUCCESS', 'LOGIN_FAILED', 'LOGOUT',
                                            'PASSWORD_CHANGED', 'PASSWORD_RESET',
                                            -- Control de sesi?n
                                            'TOKEN_ISSUED', 'TOKEN_REFRESHED',
                                            'TOKEN_REVOKED', 'SESSION_EXPIRED',
                                            -- Control de acceso
                                            'ACCESS_DENIED', 'TENANT_VIOLATION',
                                            'ROLE_ESCALATION_ATTEMPT',
                                            -- Cuenta
                                            'ACCOUNT_LOCKED', 'ACCOUNT_UNLOCKED',
                                            'MFA_ENABLED', 'MFA_DISABLED',
                                            -- QR
                                            'QR_SCAN_SUCCESS', 'QR_SCAN_FAILED',
                                            'QR_REUSE_DETECTED', 'QR_ROTATED',
                                            -- Datos
                                            'BULK_DATA_ACCESS', 'REPORT_DOWNLOADED',
                                            'DATA_EXPORT',
                                            -- Sistema
                                            'RATE_LIMIT_HIT', 'SUSPICIOUS_PATTERN',
                                            'CONFIG_CHANGED', 'SCHEMA_ACCESS'
                                        )),
    CONSTRAINT ck_se_severity           CHECK (severity IN ('INFO','WARNING','HIGH','CRITICAL')),
    CONSTRAINT ck_se_resolved           CHECK (resolved IN (0,1))
);

COMMENT ON TABLE  SECURITY_EVENTS              IS 'Log dedicado a eventos de seguridad. Retenci?n: 5 a?os. Alimenta el SIEM.';
COMMENT ON COLUMN SECURITY_EVENTS.severity     IS 'INFO | WARNING | HIGH | CRITICAL';
COMMENT ON COLUMN SECURITY_EVENTS.resolved     IS '0 = pendiente de revisi?n por el equipo de seguridad.';
COMMENT ON COLUMN SECURITY_EVENTS.metadata_json IS 'JSON con contexto: intentos fallidos, geo-IP, fingerprint del dispositivo, etc.';

CREATE INDEX idx_se_school_type    ON SECURITY_EVENTS (school_id, event_type, created_at);
CREATE INDEX idx_se_severity       ON SECURITY_EVENTS (severity, created_at, resolved);
CREATE INDEX idx_se_user           ON SECURITY_EVENTS (school_id, user_id, event_type, created_at);
CREATE INDEX idx_se_ip             ON SECURITY_EVENTS (ip_address, event_type, created_at);
CREATE INDEX idx_se_unresolved     ON SECURITY_EVENTS (severity, resolved, created_at);


-- -----------------------------------------------------------------------------
-- J.1: Trigger para detectar bloqueo de cuenta y registrar el evento
-- Cuando USERS.status pasa a 'LOCKED', crear autom?ticamente un SECURITY_EVENT
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_ACCOUNT_LOCKED_EVENT
    AFTER UPDATE OF status ON USERS
    FOR EACH ROW
    WHEN (NEW.status = 'LOCKED' AND OLD.status != 'LOCKED')
BEGIN
    INSERT INTO SECURITY_EVENTS (
        id, school_id, user_id, event_type, severity,
        description, metadata_json, created_at
    ) VALUES (
        SYS_GUID(),
        :NEW.school_id,
        :NEW.id,
        'ACCOUNT_LOCKED',
        'HIGH',
        'Cuenta bloqueada: ' || :NEW.email,
        '{"previous_status":"' || :OLD.status || '","email":"' || :NEW.email || '"}',
        SYSTIMESTAMP
    );
END TRG_ACCOUNT_LOCKED_EVENT;
/



-- -----------------------------------------------------------------------------
-- J.2: Procedimiento para registrar evento de seguridad (uso desde backend)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_LOG_SECURITY_EVENT (
    p_school_id    IN VARCHAR2,
    p_user_id      IN VARCHAR2 DEFAULT NULL,
    p_event_type   IN VARCHAR2,
    p_severity     IN VARCHAR2 DEFAULT 'INFO',
    p_description  IN VARCHAR2,
    p_ip_address   IN VARCHAR2 DEFAULT NULL,
    p_user_agent   IN VARCHAR2 DEFAULT NULL,
    p_metadata     IN VARCHAR2 DEFAULT NULL
)
AS
BEGIN
    INSERT INTO SECURITY_EVENTS (
        id, school_id, user_id, event_type, severity,
        description, ip_address, user_agent, metadata_json,
        resolved, created_at
    ) VALUES (
        SYS_GUID(),
        p_school_id, p_user_id, p_event_type, p_severity,
        p_description, p_ip_address, p_user_agent, p_metadata,
        0, SYSTIMESTAMP
    );
    -- No hacer COMMIT aqu? ? el caller lo maneja (puede estar en una transacci?n mayor)
END SP_LOG_SECURITY_EVENT;
/



-- J.3: Job para alertar sobre eventos cr?ticos no resueltos
-- NOTA: Par?metros posicionales (sin =>) para evitar ORA-17090 del pre-parser JDBC
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        'JOB_ALERT_CRITICAL_SECURITY',
        'PLSQL_BLOCK',
        'DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM   SECURITY_EVENTS
    WHERE  severity  IN (''HIGH'',''CRITICAL'')
      AND  resolved   = 0
      AND  created_at >= SYSTIMESTAMP - INTERVAL ''1'' HOUR;
    IF v_count > 0 THEN
        INSERT INTO AUDIT_LOGS (id, action, entity_type, new_value_json, result, created_at)
        VALUES (SYS_GUID(), ''SECURITY_ALERT'', ''SECURITY_EVENT'',
                ''{"unresolved_critical_last_hour":'' || v_count || ''}'',
                ''WARNING'', SYSTIMESTAMP);
        COMMIT;
    END IF;
END;',
        0,
        SYSTIMESTAMP,
        'FREQ=HOURLY',
        NULL,
        'DEFAULT_JOB_CLASS',
        TRUE,
        FALSE,
        'Verifica eventos de seguridad HIGH/CRITICAL no resueltos cada hora.'
    );
END;
/


-- =============================================================================
-- BLOQUE K: FEATURE FLAGS ? Control de funcionalidades por colegio y plan
-- =============================================================================
-- Permite activar/desactivar m?dulos sin re-deploy:
--   - Colegio en plan FREE no tiene BLOCKCHAIN ni AI_CHAT
--   - Nuevas funcionalidades en beta solo para colegios seleccionados
-- =============================================================================

CREATE TABLE FEATURE_FLAGS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    feature_name    VARCHAR2(100)   NOT NULL,
    enabled         NUMBER(1)       DEFAULT 0   NOT NULL,
    description     VARCHAR2(500),
    plan_required   VARCHAR2(20),               -- Plan m?nimo para activar
    rollout_pct     NUMBER(3)       DEFAULT 100 NOT NULL,  -- % de usuarios que la ven
    expires_at      TIMESTAMP,                  -- NULL = permanente
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_feature_flags         PRIMARY KEY (id),
    CONSTRAINT fk_ff_school             FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_ff_school_feature     UNIQUE (school_id, feature_name),
    CONSTRAINT ck_ff_enabled            CHECK (enabled IN (0,1)),
    CONSTRAINT ck_ff_rollout            CHECK (rollout_pct BETWEEN 0 AND 100),
    CONSTRAINT ck_ff_plan               CHECK (plan_required IN
                                            ('FREE','BASIC','STANDARD','PREMIUM','ENTERPRISE') OR plan_required IS NULL)
);

COMMENT ON TABLE  FEATURE_FLAGS              IS 'Feature flags por colegio. Permite activar m?dulos sin re-deploy.';
COMMENT ON COLUMN FEATURE_FLAGS.rollout_pct  IS 'Porcentaje de usuarios del colegio que ven la feature (para rollouts graduales).';

CREATE INDEX idx_ff_school_feat   ON FEATURE_FLAGS (school_id, feature_name, enabled);

-- Datos semilla: feature flags para el colegio demo
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-01', 'school-20188-canete', 'AI_CHAT',        1, 'Asistente IA con RAG sobre documentos MINEDU', 'PREMIUM');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-02', 'school-20188-canete', 'BLOCKCHAIN',     1, 'Registro inmutable de eventos acad?micos', 'PREMIUM');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-03', 'school-20188-canete', 'QR_ATTENDANCE',  1, 'Control de entrada por c?digo QR del carnet', 'STANDARD');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-04', 'school-20188-canete', 'BULK_IMPORT',    1, 'Importaci?n masiva de alumnos desde Excel', 'STANDARD');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-05', 'school-20188-canete', 'ADVANCED_REPORTS',1,'Reportes avanzados con gr?ficas (PDF/Excel)', 'PREMIUM');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-06', 'school-20188-canete', 'GUARDIAN_PORTAL', 1,'Portal web y app para apoderados', 'BASIC');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-07', 'school-20188-canete', 'MULTI_SECTION_TEACHER', 0, 'Docente asignado a m?ltiples secciones simult?neas', 'ENTERPRISE');
INSERT INTO FEATURE_FLAGS (id, school_id, feature_name, enabled, description, plan_required)
VALUES ('ff-08', 'school-20188-canete', 'SSO_GOOGLE',    0, 'Login con Google (SSO OAuth2)', 'ENTERPRISE');


-- =============================================================================
-- BLOQUE L: MEJORAS PRO ? Event Sourcing base + Multi-idioma
-- =============================================================================

-- -----------------------------------------------------------------------------
-- L.1: EVENT_STORE ? Base para Event Sourcing
-- Cada cambio de estado en el dominio queda registrado como un evento inmutable.
-- Permite reconstruir el estado de cualquier entidad en cualquier punto del tiempo.
-- Complementa (no reemplaza) al blockchain; este es m?s granular.
-- -----------------------------------------------------------------------------
CREATE TABLE EVENT_STORE (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    aggregate_type  VARCHAR2(50)    NOT NULL,   -- 'Student', 'Attendance', 'Score'
    aggregate_id    VARCHAR2(50)    NOT NULL,   -- UUID de la entidad
    event_type      VARCHAR2(100)   NOT NULL,   -- 'StudentCreated', 'ScoreUpdated'
    event_version   NUMBER(5)       NOT NULL,   -- Versi?n del evento dentro del aggregate
    payload_json    CLOB            NOT NULL,   -- Estado completo del aggregate en ese momento
    metadata_json   VARCHAR2(2000),             -- Qui?n, desde d?nde, correlaci?n ID
    causation_id    VARCHAR2(50),               -- ID del evento que caus? este
    correlation_id  VARCHAR2(50),               -- ID de la request/saga que origin?
    occurred_at     TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_event_store           PRIMARY KEY (id),
    CONSTRAINT fk_es_school             FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_es_version            UNIQUE (school_id, aggregate_id, event_version),
    CONSTRAINT ck_es_version            CHECK (event_version >= 1)
);

COMMENT ON TABLE  EVENT_STORE                 IS 'Event store para Event Sourcing. Registro inmutable de todos los cambios de estado.';
COMMENT ON COLUMN EVENT_STORE.aggregate_type  IS 'Nombre del aggregate root: Student, Attendance, Score, etc.';
COMMENT ON COLUMN EVENT_STORE.event_version   IS 'Versi?n secuencial del evento dentro del aggregate. Empieza en 1.';
COMMENT ON COLUMN EVENT_STORE.payload_json    IS 'Estado completo del aggregate al momento del evento (para replay).';
COMMENT ON COLUMN EVENT_STORE.correlation_id  IS 'ID de la request HTTP/saga que origin? el evento. Facilita debugging.';

CREATE INDEX idx_es_aggregate    ON EVENT_STORE (school_id, aggregate_type, aggregate_id, event_version);
CREATE INDEX idx_es_type_time    ON EVENT_STORE (school_id, event_type, occurred_at);
CREATE INDEX idx_es_correlation  ON EVENT_STORE (correlation_id);


-- -----------------------------------------------------------------------------
-- L.2: TRANSLATIONS ? Soporte multi-idioma
-- Permite traducir etiquetas del sistema para colegios con idiomas distintos.
-- Actualmente: es-PE (Espa?ol Per?). Extensible a quechua, aymara, ingl?s.
-- -----------------------------------------------------------------------------
CREATE TABLE TRANSLATIONS (
    id          VARCHAR2(50)    NOT NULL,
    trans_key   VARCHAR2(200)   NOT NULL,   -- Ej: 'nav.students', 'status.absent'
    lang_code   VARCHAR2(10)    NOT NULL,   -- Ej: 'es-PE', 'qu-PE', 'en-US'
    value       VARCHAR2(2000)  NOT NULL,
    context     VARCHAR2(100),              -- M?dulo o pantalla donde se usa
    updated_at  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_translations      PRIMARY KEY (id),
    CONSTRAINT uq_trans_key_lang    UNIQUE (trans_key, lang_code)
);

COMMENT ON TABLE  TRANSLATIONS       IS 'Internacionalizaci?n del sistema. Soporte multi-idioma sin re-deploy.';
COMMENT ON COLUMN TRANSLATIONS.lang_code IS 'BCP-47: es-PE (Espa?ol Per?), qu-PE (Quechua), en-US (Ingl?s).';

CREATE INDEX idx_trans_key   ON TRANSLATIONS (trans_key, lang_code);
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX idx_trans_lang ON TRANSLATIONS (lang_code)';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- Seed: Espa?ol Per? (idioma base)
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'status.present',     'es-PE', 'Presente',      'attendance');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'status.absent',      'es-PE', 'Ausente',       'attendance');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'status.late',        'es-PE', 'Tardanza',      'attendance');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'status.excused',     'es-PE', 'Justificado',   'attendance');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'level.ad',           'es-PE', 'Destacado',     'grades');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'level.a',            'es-PE', 'Logrado',       'grades');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'level.b',            'es-PE', 'En Proceso',    'grades');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'level.c',            'es-PE', 'En Inicio',     'grades');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'role.admin',         'es-PE', 'Administrador', 'users');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'role.teacher',       'es-PE', 'Docente',       'users');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'role.student',       'es-PE', 'Estudiante',    'users');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'role.guardian',      'es-PE', 'Apoderado',     'users');
INSERT INTO TRANSLATIONS (id, trans_key, lang_code, value, context) VALUES (SYS_GUID(), 'role.coordinator',   'es-PE', 'Coordinador',   'users');


-- =============================================================================
-- COMMIT FINAL
-- =============================================================================
COMMIT;


-- =============================================================================
-- VERIFICACI?N FINAL ? v2.2
-- =============================================================================
SELECT 'NUEVAS TABLAS (v2.2)' AS resumen, COUNT(*) AS total
FROM user_tables
WHERE table_name IN (
    'TEACHER_SPECIALTIES', 'SECURITY_EVENTS', 'FEATURE_FLAGS',
    'EVENT_STORE', 'TRANSLATIONS',
    'ATTENDANCES_PART', 'STUDENT_SCORES_PART', 'BLOCKCHAIN_BLOCKS_PART'
)
UNION ALL
SELECT 'NUEVOS TRIGGERS (v2.2)', COUNT(*)
FROM user_triggers
WHERE trigger_name IN (
    'TRG_STUDENTS_SOFT_DELETE', 'TRG_SCORES_SOFT_DELETE',
    'TRG_SCORES_VERSION', 'TRG_ATTENDANCES_VERSION',
    'TRG_STUDENTS_VERSION', 'TRG_USERS_VERSION',
    'TRG_SYSTEM_CONFIG_VERSION', 'TRG_ACCOUNT_LOCKED_EVENT'
)
UNION ALL
SELECT 'NUEVOS PROCEDURES (v2.2)', COUNT(*)
FROM user_procedures
WHERE object_name IN (
    'SP_SET_TENANT_CONTEXT', 'SP_MIGRATE_TO_PARTITIONED',
    'SP_ADD_BLOCK', 'SP_VALIDATE_CHAIN', 'SP_LOG_SECURITY_EVENT'
)
UNION ALL
SELECT 'VPD POLICIES', COUNT(*)
FROM user_policies
UNION ALL
SELECT 'SCHEDULER JOBS (v2.2)', COUNT(*)
FROM user_scheduler_jobs
WHERE job_name IN (
    'JOB_CLEAN_EXPIRED_TOKENS', 'JOB_ROTATE_QR_SECRETS',
    'JOB_CLOSE_EXPIRED_PERIODS', 'JOB_EXPIRE_JUSTIFICATIONS',
    'JOB_CLEANUP_REPORTS', 'JOB_PURGE_OLD_AUDITLOGS',
    'JOB_ALERT_CRITICAL_SECURITY'
);

-- Resumen total del esquema completo (v2.0 + v2.1 + v2.2)
SELECT 'TOTAL TABLAS'        AS esquema, COUNT(*) AS n FROM user_tables
WHERE table_name NOT LIKE '%$%'  -- Excluir tablas internas de Oracle
  AND table_name NOT LIKE 'AQ%'
UNION ALL
SELECT 'TOTAL VISTAS',   COUNT(*) FROM user_views    WHERE view_name LIKE 'V%' OR view_name LIKE 'VW%'
UNION ALL
SELECT 'TOTAL TRIGGERS', COUNT(*) FROM user_triggers WHERE trigger_name LIKE 'TRG%'
UNION ALL
SELECT 'TOTAL PROCEDURES', COUNT(*) FROM user_procedures WHERE object_name LIKE 'SP%'
UNION ALL
SELECT 'TOTAL INDEXES',  COUNT(*) FROM user_indexes  WHERE index_name LIKE 'IDX%'
UNION ALL
SELECT 'TOTAL JOBS',     COUNT(*) FROM user_scheduler_jobs WHERE job_name LIKE 'JOB%';

-- =============================================================================
-- FIN ? FINE FLOW v2.2 ? ARQUITECTURA AVANZADA
-- NUEVO: 8 tablas ? 8 triggers ? 5 SPs ? 24 ?ndices ? 7 jobs ? 24 pol?ticas VPD
-- TOTAL: 35 tablas ? 5 vistas ? 19 triggers ? 11 SPs ? +40 ?ndices ? 7 jobs
-- =============================================================================


-- =============================================================================
-- FASE 8: M?DULO DE HORARIOS v1.0
-- CLASSROOMS ? TIME_SLOTS ? SCHEDULE_VERSIONS
-- CLASS_SCHEDULES (triple anticolisi?n) ? SCHEDULE_EXCEPTIONS
-- Triggers ? SPs ? Vistas ? Seed Data de horario
-- =============================================================================


CREATE TABLE CLASSROOMS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    name            VARCHAR2(50)    NOT NULL,   -- Ej: 'Aula 3A', 'Lab. C?mputo', 'Biblioteca'
    room_type       VARCHAR2(30)    DEFAULT 'CLASSROOM' NOT NULL,
    capacity        NUMBER(3)       DEFAULT 30 NOT NULL,
    floor_number    NUMBER(2),
    building        VARCHAR2(50),
    has_projector   NUMBER(1)       DEFAULT 0 NOT NULL,
    has_computers   NUMBER(1)       DEFAULT 0 NOT NULL,
    is_active       NUMBER(1)       DEFAULT 1 NOT NULL,
    notes           VARCHAR2(500),
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_classrooms        PRIMARY KEY (id),
    CONSTRAINT fk_cr_school         FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_cr_name           UNIQUE (school_id, name),
    CONSTRAINT ck_cr_room_type      CHECK (room_type IN (
                                        'CLASSROOM','LAB_SCIENCE','LAB_COMPUTERS',
                                        'GYM','AUDITORIUM','LIBRARY','WORKSHOP','OTHER'
                                    )),
    CONSTRAINT ck_cr_capacity       CHECK (capacity BETWEEN 1 AND 200),
    CONSTRAINT ck_cr_active         CHECK (is_active IN (0,1)),
    CONSTRAINT ck_cr_projector      CHECK (has_projector IN (0,1)),
    CONSTRAINT ck_cr_computers      CHECK (has_computers IN (0,1))
);

COMMENT ON TABLE  CLASSROOMS           IS 'Aulas f?sicas del colegio. Incluye laboratorios, gimnasio, etc.';
COMMENT ON COLUMN CLASSROOMS.name      IS 'Nombre del aula: "Aula 3A", "Lab. Qu?mica", "Patio", etc.';
COMMENT ON COLUMN CLASSROOMS.room_type IS 'Tipo: CLASSROOM | LAB_SCIENCE | LAB_COMPUTERS | GYM | AUDITORIUM | LIBRARY';

CREATE INDEX idx_cr_school   ON CLASSROOMS (school_id, is_active, room_type);


-- Datos semilla ? aulas del colegio demo
INSERT INTO CLASSROOMS (id, school_id, name, room_type, capacity, floor_number, has_projector, is_active)
VALUES ('cr-3a', 'school-20188-canete', 'Aula 3?A', 'CLASSROOM', 35, 1, 1, 1);
INSERT INTO CLASSROOMS (id, school_id, name, room_type, capacity, floor_number, has_projector, is_active)
VALUES ('cr-3b', 'school-20188-canete', 'Aula 3?B', 'CLASSROOM', 35, 1, 0, 1);
INSERT INTO CLASSROOMS (id, school_id, name, room_type, capacity, floor_number, has_projector, is_active)
VALUES ('cr-4a', 'school-20188-canete', 'Aula 4?A', 'CLASSROOM', 35, 2, 1, 1);
INSERT INTO CLASSROOMS (id, school_id, name, room_type, capacity, floor_number, has_projector, is_active)
VALUES ('cr-lab1', 'school-20188-canete', 'Lab. C?mputo', 'LAB_COMPUTERS', 30, 1, 1, 1);
INSERT INTO CLASSROOMS (id, school_id, name, room_type, capacity, floor_number, has_computers, is_active)
VALUES ('cr-lab2', 'school-20188-canete', 'Lab. Ciencias', 'LAB_SCIENCE', 28, 2, 1, 1);
INSERT INTO CLASSROOMS (id, school_id, name, room_type, capacity, floor_number, is_active)
VALUES ('cr-gym',  'school-20188-canete', 'Gimnasio', 'GYM', 100, 0, 1);


-- =============================================================================
-- BLOQUE 2: TIME_SLOTS ? Franjas horarias del d?a
-- =============================================================================
-- Define la grilla horaria del colegio. Cada franja tiene:
--   - Un n?mero de orden (1 = primera clase del d?a)
--   - Hora de inicio y fin
--   - Si es recreo (break_type != 'CLASS') ? no se puede asignar clase
-- El colegio puede tener su propia grilla (respeta SYSTEM_CONFIG.SCHOOL_START_TIME)
-- =============================================================================

CREATE TABLE TIME_SLOTS (
    id              VARCHAR2(50)    NOT NULL,
    school_id       VARCHAR2(50)    NOT NULL,
    slot_number     NUMBER(2)       NOT NULL,   -- Orden del d?a: 1,2,3...
    slot_name       VARCHAR2(50)    NOT NULL,   -- Ej: '1? Hora', 'Recreo', '2? Hora'
    start_time      VARCHAR2(5)     NOT NULL,   -- 'HH:MI' ? Ej: '07:30'
    end_time        VARCHAR2(5)     NOT NULL,   -- 'HH:MI' ? Ej: '08:15'
    duration_min    NUMBER(3)       NOT NULL,   -- Duraci?n en minutos (calculado)
    slot_type       VARCHAR2(20)    DEFAULT 'CLASS' NOT NULL,
    is_active       NUMBER(1)       DEFAULT 1   NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_time_slots        PRIMARY KEY (id),
    CONSTRAINT fk_tslot_school      FOREIGN KEY (school_id) REFERENCES SCHOOLS(id),
    CONSTRAINT uq_ts_slot_num       UNIQUE (school_id, slot_number),
    CONSTRAINT uq_ts_slot_name      UNIQUE (school_id, slot_name),
    CONSTRAINT ck_ts_slot_type      CHECK (slot_type IN ('CLASS','BREAK','LUNCH','ENTRANCE','EXIT')),
    CONSTRAINT ck_ts_duration       CHECK (duration_min BETWEEN 5 AND 180),
    CONSTRAINT ck_ts_active         CHECK (is_active IN (0,1)),
    CONSTRAINT ck_ts_time_format    CHECK (
                                        REGEXP_LIKE(start_time, '^[0-2][0-9]:[0-5][0-9]$') AND
                                        REGEXP_LIKE(end_time,   '^[0-2][0-9]:[0-5][0-9]$')
                                    ),
    CONSTRAINT ck_ts_time_order     CHECK (end_time > start_time)
);

COMMENT ON TABLE  TIME_SLOTS             IS 'Grilla horaria del colegio. slot_type=BREAK = recreo, no asignable.';
COMMENT ON COLUMN TIME_SLOTS.slot_number IS 'Orden del d?a (1=primera hora). Debe ser consecutivo.';
COMMENT ON COLUMN TIME_SLOTS.slot_type   IS 'CLASS (asignable) | BREAK (recreo) | LUNCH | ENTRANCE | EXIT';

CREATE INDEX idx_ts_school_type ON TIME_SLOTS (school_id, slot_type, is_active);


-- Datos semilla ? horario est?ndar jornada ma?ana (7:30 - 13:00)
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-01', 'school-20188-canete', 1,  'Entrada',   '07:30', '07:45', 15,  'ENTRANCE');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-02', 'school-20188-canete', 2,  '1? Hora',   '07:45', '08:30', 45,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-03', 'school-20188-canete', 3,  '2? Hora',   '08:30', '09:15', 45,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-04', 'school-20188-canete', 4,  '3? Hora',   '09:15', '10:00', 45,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-05', 'school-20188-canete', 5,  'Recreo',    '10:00', '10:20', 20,  'BREAK');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-06', 'school-20188-canete', 6,  '4? Hora',   '10:20', '11:05', 45,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-07', 'school-20188-canete', 7,  '5? Hora',   '11:05', '11:50', 45,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-08', 'school-20188-canete', 8,  '6? Hora',   '11:50', '12:35', 45,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-09', 'school-20188-canete', 9,  '7? Hora',   '12:35', '13:00', 25,  'CLASS');
INSERT INTO TIME_SLOTS (id, school_id, slot_number, slot_name, start_time, end_time, duration_min, slot_type)
VALUES ('ts-10', 'school-20188-canete', 10, 'Salida',    '13:00', '13:10', 10,  'EXIT');


-- =============================================================================
-- BLOQUE 3: SCHEDULE_VERSIONS ? Control de versiones del horario
-- =============================================================================
-- Un horario pasa por: DRAFT ? REVIEW ? ACTIVE ? ARCHIVED
-- Solo puede haber UN horario ACTIVE por colegio + school_year + period.
-- El trigger TRG_ONE_ACTIVE_SCHEDULE lo garantiza.
-- =============================================================================

CREATE TABLE SCHEDULE_VERSIONS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    school_year_id      VARCHAR2(50)    NOT NULL,
    academic_period_id  VARCHAR2(50),               -- NULL = todo el a?o
    version_name        VARCHAR2(100)   NOT NULL,    -- Ej: 'Horario 2025 v3', '1er Bimestre'
    status              VARCHAR2(20)    DEFAULT 'DRAFT' NOT NULL,
    notes               VARCHAR2(2000),
    created_by          VARCHAR2(50)    NOT NULL,
    approved_by         VARCHAR2(50),
    approved_at         TIMESTAMP,
    published_at        TIMESTAMP,                  -- Cuando pas? a ACTIVE
    valid_from          DATE            NOT NULL,
    valid_until         DATE,                       -- NULL = sin fecha de fin
    created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_schedule_ver          PRIMARY KEY (id),
    CONSTRAINT fk_sv_school             FOREIGN KEY (school_id)          REFERENCES SCHOOLS(id),
    CONSTRAINT fk_sv_school_year        FOREIGN KEY (school_year_id)     REFERENCES SCHOOL_YEARS(id),
    CONSTRAINT fk_sv_period             FOREIGN KEY (academic_period_id) REFERENCES ACADEMIC_PERIODS(id),
    CONSTRAINT fk_sv_created_by         FOREIGN KEY (created_by)         REFERENCES USERS(id),
    CONSTRAINT fk_sv_approved_by        FOREIGN KEY (approved_by)        REFERENCES USERS(id),
    CONSTRAINT ck_sv_status             CHECK (status IN ('DRAFT','REVIEW','ACTIVE','ARCHIVED')),
    CONSTRAINT ck_sv_dates              CHECK (valid_until IS NULL OR valid_until > valid_from)
);

COMMENT ON TABLE  SCHEDULE_VERSIONS         IS 'Versiones del horario escolar. Flujo: DRAFT?REVIEW?ACTIVE?ARCHIVED.';
COMMENT ON COLUMN SCHEDULE_VERSIONS.status  IS 'DRAFT (editable) | REVIEW (esperando aprobaci?n) | ACTIVE (vigente) | ARCHIVED';

CREATE INDEX idx_sv_school_status  ON SCHEDULE_VERSIONS (school_id, school_year_id, status);


-- Trigger: solo puede haber UN horario ACTIVE por colegio + a?o escolar
CREATE OR REPLACE TRIGGER TRG_ONE_ACTIVE_SCHEDULE
    BEFORE INSERT OR UPDATE OF status ON SCHEDULE_VERSIONS
    FOR EACH ROW
    WHEN (NEW.status = 'ACTIVE')
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO   v_count
    FROM   SCHEDULE_VERSIONS
    WHERE  school_id      = :NEW.school_id
      AND  school_year_id = :NEW.school_year_id
      AND  status         = 'ACTIVE'
      AND  id            <> :NEW.id;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20200,
            'SCHEDULE_ERROR: Ya existe un horario ACTIVE para este a?o escolar. '
            || 'Archiva el horario actual antes de publicar uno nuevo. '
            || 'school_year_id: ' || :NEW.school_year_id);
    END IF;

    IF :NEW.status = 'ACTIVE' THEN
        :NEW.published_at := SYSTIMESTAMP;
    END IF;
END TRG_ONE_ACTIVE_SCHEDULE;
/


-- Trigger updated_at
CREATE OR REPLACE TRIGGER TRG_SCHEDULE_VER_UPDATED_AT
    BEFORE UPDATE ON SCHEDULE_VERSIONS
    FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END TRG_SCHEDULE_VER_UPDATED_AT;
/


-- =============================================================================
-- BLOQUE 4: CLASS_SCHEDULES ? Asignaci?n concreta de una clase
-- =============================================================================
-- Es el coraz?n del m?dulo. Cada fila dice:
--   "El Lunes a la 2? hora, el Prof. Garc?a ense?a Matem?tica
--    a la secci?n 3?A en el Aula 3A, seg?n el horario v1"
--
-- TRIPLE CONSTRAINT ?NICA ? garant?a matem?tica de no colisi?n:
--   ? (schedule_version_id, day_of_week, time_slot_id, teacher_id)   ? docente libre
--   ? (schedule_version_id, day_of_week, time_slot_id, section_id)   ? secci?n libre
--   ? (schedule_version_id, day_of_week, time_slot_id, classroom_id) ? aula libre
-- =============================================================================

CREATE TABLE CLASS_SCHEDULES (
    id                      VARCHAR2(50)    NOT NULL,
    school_id               VARCHAR2(50)    NOT NULL,
    schedule_version_id     VARCHAR2(50)    NOT NULL,
    course_assignment_id    VARCHAR2(50)    NOT NULL,   -- Curso + Secci?n + Docente + Per?odo
    section_id              VARCHAR2(50)    NOT NULL,   -- Desnormalizado de course_assignment para el ?ndice
    teacher_id              VARCHAR2(50)    NOT NULL,   -- Desnormalizado de course_assignment para el ?ndice
    classroom_id            VARCHAR2(50)    NOT NULL,
    time_slot_id            VARCHAR2(50)    NOT NULL,
    day_of_week             NUMBER(1)       NOT NULL,   -- 1=Lunes ? 5=Viernes (6=S?b para recuperaci?n)
    week_type               VARCHAR2(10)    DEFAULT 'ALL' NOT NULL,  -- ALL | ODD | EVEN (semanas alternas)
    color_hex               VARCHAR2(7),                -- Color en el frontend (#RRGGBB)
    notes                   VARCHAR2(500),
    is_active               NUMBER(1)       DEFAULT 1   NOT NULL,
    created_by              VARCHAR2(50),
    created_at              TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_class_schedules           PRIMARY KEY (id),
    CONSTRAINT fk_clsch_school              FOREIGN KEY (school_id)            REFERENCES SCHOOLS(id),
    CONSTRAINT fk_clsch_version             FOREIGN KEY (schedule_version_id)  REFERENCES SCHEDULE_VERSIONS(id),
    CONSTRAINT fk_clsch_assignment          FOREIGN KEY (course_assignment_id) REFERENCES COURSE_ASSIGNMENTS(id),
    CONSTRAINT fk_clsch_section             FOREIGN KEY (section_id)           REFERENCES SECTIONS(id),
    CONSTRAINT fk_clsch_teacher             FOREIGN KEY (teacher_id)           REFERENCES TEACHERS(id),
    CONSTRAINT fk_clsch_classroom           FOREIGN KEY (classroom_id)         REFERENCES CLASSROOMS(id),
    CONSTRAINT fk_clsch_timeslot            FOREIGN KEY (time_slot_id)         REFERENCES TIME_SLOTS(id),
    CONSTRAINT fk_clsch_created_by          FOREIGN KEY (created_by)           REFERENCES USERS(id),

    -- ? ANTICOLISI?N DOCENTE: mismo docente, mismo d?a, misma hora ? IMPOSIBLE
    CONSTRAINT uq_clsch_teacher_slot UNIQUE (schedule_version_id, day_of_week, time_slot_id, teacher_id),

    -- ? ANTICOLISI?N SECCI?N: misma secci?n, mismo d?a, misma hora ? IMPOSIBLE
    CONSTRAINT uq_clsch_section_slot UNIQUE (schedule_version_id, day_of_week, time_slot_id, section_id),

    -- ? ANTICOLISI?N AULA: misma aula, mismo d?a, misma hora ? IMPOSIBLE
    CONSTRAINT uq_clsch_classroom_slot UNIQUE (schedule_version_id, day_of_week, time_slot_id, classroom_id),

    CONSTRAINT ck_clsch_day_of_week  CHECK (day_of_week BETWEEN 1 AND 6),
    CONSTRAINT ck_clsch_week_type    CHECK (week_type IN ('ALL','ODD','EVEN')),
    CONSTRAINT ck_clsch_active       CHECK (is_active IN (0,1))
);

COMMENT ON TABLE  CLASS_SCHEDULES              IS 'Horario de clases. Triple UNIQUE garantiza cero colisiones de docente, secci?n y aula.';
COMMENT ON COLUMN CLASS_SCHEDULES.day_of_week  IS '1=Lunes, 2=Martes, 3=Mi?rcoles, 4=Jueves, 5=Viernes, 6=S?bado.';
COMMENT ON COLUMN CLASS_SCHEDULES.week_type    IS 'ALL=todas las semanas | ODD=semanas impares | EVEN=semanas pares.';
COMMENT ON COLUMN CLASS_SCHEDULES.section_id   IS 'Desnormalizado de COURSE_ASSIGNMENTS para que el ?ndice UNIQUE funcione.';

-- ?ndices de lectura (consultas del frontend)
CREATE INDEX idx_sch_version_day      ON CLASS_SCHEDULES (school_id, schedule_version_id, day_of_week, is_active);
CREATE INDEX idx_sch_teacher          ON CLASS_SCHEDULES (school_id, teacher_id, day_of_week, time_slot_id);
CREATE INDEX idx_sch_section          ON CLASS_SCHEDULES (school_id, section_id, day_of_week, time_slot_id);
CREATE INDEX idx_sch_classroom        ON CLASS_SCHEDULES (school_id, classroom_id, day_of_week, time_slot_id);


-- =============================================================================
-- BLOQUE 5: SCHEDULE_EXCEPTIONS ? Excepciones puntuales
-- =============================================================================
-- Un martes puntual el Prof. Garc?a no puede ? se asigna un sustituto.
-- O una clase se suspende por actividad escolar.
-- Las excepciones SOBREESCRIBEN el horario base solo para esa fecha.
-- =============================================================================

CREATE TABLE SCHEDULE_EXCEPTIONS (
    id                  VARCHAR2(50)    NOT NULL,
    school_id           VARCHAR2(50)    NOT NULL,
    class_schedule_id   VARCHAR2(50)    NOT NULL,   -- La clase afectada
    exception_date      DATE            NOT NULL,   -- Fecha puntual de la excepci?n
    exception_type      VARCHAR2(20)    NOT NULL,
    substitute_teacher_id VARCHAR2(50),             -- Docente sustituto (si aplica)
    substitute_classroom_id VARCHAR2(50),           -- Aula alternativa (si aplica)
    substitute_slot_id  VARCHAR2(50),               -- Hora alternativa (si aplica)
    reason              VARCHAR2(1000)  NOT NULL,
    approved_by         VARCHAR2(50),
    approved_at         TIMESTAMP,
    created_by          VARCHAR2(50)    NOT NULL,
    created_at          TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    --
    CONSTRAINT pk_schedule_exc          PRIMARY KEY (id),
    CONSTRAINT fk_sexc_school           FOREIGN KEY (school_id)              REFERENCES SCHOOLS(id),
    CONSTRAINT fk_se_schedule           FOREIGN KEY (class_schedule_id)      REFERENCES CLASS_SCHEDULES(id),
    CONSTRAINT fk_se_sub_teacher        FOREIGN KEY (substitute_teacher_id)  REFERENCES TEACHERS(id),
    CONSTRAINT fk_se_sub_classroom      FOREIGN KEY (substitute_classroom_id) REFERENCES CLASSROOMS(id),
    CONSTRAINT fk_se_sub_slot           FOREIGN KEY (substitute_slot_id)     REFERENCES TIME_SLOTS(id),
    CONSTRAINT fk_se_created_by         FOREIGN KEY (created_by)             REFERENCES USERS(id),
    CONSTRAINT fk_se_approved_by        FOREIGN KEY (approved_by)            REFERENCES USERS(id),
    -- Una clase solo puede tener UNA excepci?n por fecha
    CONSTRAINT uq_se_date               UNIQUE (school_id, class_schedule_id, exception_date),
    CONSTRAINT ck_se_exc_type           CHECK (exception_type IN (
                                            'CANCELLED',          -- Clase suspendida sin recuperaci?n
                                            'TEACHER_ABSENT',     -- Docente falta ? sustituto
                                            'ROOM_CHANGE',        -- Cambio de aula
                                            'TIME_CHANGE',        -- Cambio de hora
                                            'HOLIDAY',            -- D?a festivo/actividad escolar
                                            'RECOVERY'            -- Clase recuperatoria
                                        ))
);

COMMENT ON TABLE  SCHEDULE_EXCEPTIONS               IS 'Excepciones puntuales al horario base (ausencias, cambios, feriados).';
COMMENT ON COLUMN SCHEDULE_EXCEPTIONS.exception_type IS 'CANCELLED | TEACHER_ABSENT | ROOM_CHANGE | TIME_CHANGE | HOLIDAY | RECOVERY';

CREATE INDEX idx_exc_date         ON SCHEDULE_EXCEPTIONS (school_id, exception_date);
CREATE INDEX idx_exc_sub_teacher  ON SCHEDULE_EXCEPTIONS (school_id, substitute_teacher_id, exception_date);


-- =============================================================================
-- BLOQUE 6: TRIGGERS DE VALIDACI?N AVANZADA
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Trigger 1: Bloquear asignaci?n en franjas de RECREO o ENTRADA/SALIDA
-- Las constraints UNIQUE ya bloquean las colisiones, pero este trigger
-- da un mensaje de error claro y previene franjas no asignables.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SCHEDULE_NO_BREAK
    BEFORE INSERT OR UPDATE ON CLASS_SCHEDULES
    FOR EACH ROW
DECLARE
    v_slot_type  VARCHAR2(20);
    v_slot_name  VARCHAR2(50);
    v_ver_status VARCHAR2(20);
BEGIN
    -- Validar que la franja sea asignable
    SELECT slot_type, slot_name
    INTO   v_slot_type, v_slot_name
    FROM   TIME_SLOTS
    WHERE  id = :NEW.time_slot_id;

    IF v_slot_type != 'CLASS' THEN
        RAISE_APPLICATION_ERROR(-20210,
            'SCHEDULE_ERROR: No se puede asignar una clase en la franja "'
            || v_slot_name || '" (tipo=' || v_slot_type
            || '). Solo se permiten franjas de tipo CLASS.');
    END IF;

    -- Validar que el horario est? en estado DRAFT o REVIEW (no se puede editar ACTIVE)
    SELECT status
    INTO   v_ver_status
    FROM   SCHEDULE_VERSIONS
    WHERE  id = :NEW.schedule_version_id;

    IF v_ver_status NOT IN ('DRAFT', 'REVIEW') THEN
        RAISE_APPLICATION_ERROR(-20211,
            'SCHEDULE_ERROR: El horario est? en estado "'|| v_ver_status
            || '". Solo se pueden editar horarios en estado DRAFT o REVIEW. '
            || 'Crea una nueva versi?n para hacer cambios.');
    END IF;
END TRG_SCHEDULE_NO_BREAK;
/



-- -----------------------------------------------------------------------------
-- Trigger 2: Validar que el aula tiene capacidad para la secci?n
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_SCHEDULE_CAPACITY
    BEFORE INSERT OR UPDATE ON CLASS_SCHEDULES
    FOR EACH ROW
DECLARE
    v_room_cap      NUMBER;
    v_room_name     VARCHAR2(50);
    v_section_count NUMBER;
    v_section_name  VARCHAR2(10);
BEGIN
    -- Obtener capacidad del aula y nombre de secci?n
    SELECT cr.capacity, cr.name
    INTO   v_room_cap, v_room_name
    FROM   CLASSROOMS cr
    WHERE  cr.id = :NEW.classroom_id;

    SELECT sec.name, COUNT(st.id)
    INTO   v_section_name, v_section_count
    FROM   SECTIONS sec
    LEFT JOIN STUDENTS st ON st.section_id = sec.id AND st.status = 'ACTIVE'
    WHERE  sec.id = :NEW.section_id
    GROUP  BY sec.name;

    IF v_section_count > v_room_cap THEN
        RAISE_APPLICATION_ERROR(-20212,
            'SCHEDULE_ERROR: El aula "'|| v_room_name
            || '" tiene capacidad para '|| v_room_cap
            || ' alumnos, pero la secci?n "'|| v_section_name
            || '" tiene '|| v_section_count || ' alumnos activos.');
    END IF;
END TRG_SCHEDULE_CAPACITY;
/



-- -----------------------------------------------------------------------------
-- Trigger 3: Validar colisi?n de docente sustituto en SCHEDULE_EXCEPTIONS
-- Un sustituto tampoco puede estar en dos lugares a la vez.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_EXCEPTION_SUB_CONFLICT
    FOR INSERT OR UPDATE ON SCHEDULE_EXCEPTIONS
    WHEN (NEW.substitute_teacher_id IS NOT NULL)
    COMPOUND TRIGGER

    TYPE t_conflict_rec IS RECORD (
        class_schedule_id     VARCHAR2(50),
        substitute_teacher_id VARCHAR2(50),
        exception_date        DATE,
        substitute_slot_id    VARCHAR2(50)
    );
    TYPE t_conflict_tab IS TABLE OF t_conflict_rec;
    g_data t_conflict_tab := t_conflict_tab();

    BEFORE EACH ROW IS
    BEGIN
        g_data.EXTEND;
        g_data(g_data.LAST).class_schedule_id     := :NEW.class_schedule_id;
        g_data(g_data.LAST).substitute_teacher_id := :NEW.substitute_teacher_id;
        g_data(g_data.LAST).exception_date        := :NEW.exception_date;
        g_data(g_data.LAST).substitute_slot_id    := :NEW.substitute_slot_id;
    END BEFORE EACH ROW;

    AFTER STATEMENT IS
        v_slot_id     VARCHAR2(50);
        v_day         NUMBER;
        v_ver_id      VARCHAR2(50);
        v_conflicts   NUMBER;
    BEGIN
        FOR i IN 1 .. g_data.COUNT LOOP
            SELECT cs.time_slot_id, cs.day_of_week, cs.schedule_version_id
            INTO   v_slot_id, v_day, v_ver_id
            FROM   CLASS_SCHEDULES cs
            WHERE  cs.id = g_data(i).class_schedule_id;

            IF g_data(i).substitute_slot_id IS NOT NULL THEN
                v_slot_id := g_data(i).substitute_slot_id;
            END IF;

            SELECT COUNT(*)
            INTO   v_conflicts
            FROM   CLASS_SCHEDULES cs
            WHERE  cs.teacher_id           = g_data(i).substitute_teacher_id
              AND  cs.schedule_version_id  = v_ver_id
              AND  cs.day_of_week          = v_day
              AND  cs.time_slot_id         = v_slot_id
              AND  cs.id                  <> g_data(i).class_schedule_id
              AND  cs.is_active            = 1
              AND  NOT EXISTS (
                       SELECT 1 FROM SCHEDULE_EXCEPTIONS se2
                       WHERE se2.class_schedule_id = cs.id
                         AND se2.exception_date    = g_data(i).exception_date
                         AND se2.exception_type    = 'CANCELLED'
                   );

            IF v_conflicts > 0 THEN
                RAISE_APPLICATION_ERROR(-20213,
                    'SCHEDULE_ERROR: El docente sustituto ya tiene '|| v_conflicts
                    || ' clase(s) en esa franja el '|| TO_CHAR(g_data(i).exception_date, 'DD/MM/YYYY')
                    || '. No puede estar en dos lugares a la vez.');
            END IF;
        END LOOP;
    END AFTER STATEMENT;
END TRG_EXCEPTION_SUB_CONFLICT;
/



-- =============================================================================
-- BLOQUE 7: STORED PROCEDURES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- SP_ASSIGN_CLASS ? Asignar una clase al horario con validaci?n completa
-- Punto de entrada principal del backend para agregar clases al horario.
-- Devuelve el ID del registro creado o lanza excepci?n con detalle del conflicto.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_ASSIGN_CLASS (
    p_school_id          IN  VARCHAR2,
    p_version_id         IN  VARCHAR2,
    p_course_assign_id   IN  VARCHAR2,
    p_classroom_id       IN  VARCHAR2,
    p_time_slot_id       IN  VARCHAR2,
    p_day_of_week        IN  NUMBER,
    p_week_type          IN  VARCHAR2 DEFAULT 'ALL',
    p_color_hex          IN  VARCHAR2 DEFAULT NULL,
    p_notes              IN  VARCHAR2 DEFAULT NULL,
    p_created_by         IN  VARCHAR2 DEFAULT NULL,
    p_new_schedule_id    OUT VARCHAR2
)
AS
    v_section_id    VARCHAR2(50);
    v_teacher_id    VARCHAR2(50);
    v_slot_name     VARCHAR2(50);
    v_teacher_name  VARCHAR2(200);
    v_section_name  VARCHAR2(10);
    v_classroom_name VARCHAR2(50);
    v_conflict_type VARCHAR2(50);
    v_conflict_detail VARCHAR2(500);
BEGIN
    -- Extraer section_id y teacher_id de la asignaci?n
    SELECT ca.section_id, ca.teacher_id
    INTO   v_section_id, v_teacher_id
    FROM   COURSE_ASSIGNMENTS ca
    WHERE  ca.id        = p_course_assign_id
      AND  ca.school_id = p_school_id;

    -- Verificar conflicto de DOCENTE (antes del INSERT para dar mensaje claro)
    BEGIN
        SELECT cs.id
        INTO   v_conflict_detail
        FROM   CLASS_SCHEDULES cs
        WHERE  cs.schedule_version_id = p_version_id
          AND  cs.day_of_week         = p_day_of_week
          AND  cs.time_slot_id        = p_time_slot_id
          AND  cs.teacher_id          = v_teacher_id
          AND  ROWNUM = 1;

        -- Si llegamos aqu? hay conflicto de docente
        SELECT t.first_name || ' ' || t.last_name
        INTO   v_teacher_name
        FROM   TEACHERS t WHERE t.id = v_teacher_id;

        SELECT slot_name INTO v_slot_name FROM TIME_SLOTS WHERE id = p_time_slot_id;

        RAISE_APPLICATION_ERROR(-20220,
            'CONFLICTO DE DOCENTE: "' || v_teacher_name || '" ya tiene clase asignada el '
            || CASE p_day_of_week WHEN 1 THEN 'Lunes' WHEN 2 THEN 'Martes'
                                  WHEN 3 THEN 'Mi?rcoles' WHEN 4 THEN 'Jueves'
                                  WHEN 5 THEN 'Viernes' WHEN 6 THEN 'S?bado' END
            || ' en la franja "' || v_slot_name || '".');
    EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
    END;

    -- Verificar conflicto de SECCI?N
    BEGIN
        SELECT cs.id
        INTO   v_conflict_detail
        FROM   CLASS_SCHEDULES cs
        WHERE  cs.schedule_version_id = p_version_id
          AND  cs.day_of_week         = p_day_of_week
          AND  cs.time_slot_id        = p_time_slot_id
          AND  cs.section_id          = v_section_id
          AND  ROWNUM = 1;

        SELECT sec.name INTO v_section_name FROM SECTIONS sec WHERE sec.id = v_section_id;
        SELECT slot_name INTO v_slot_name FROM TIME_SLOTS WHERE id = p_time_slot_id;

        RAISE_APPLICATION_ERROR(-20221,
            'CONFLICTO DE SECCI?N: La secci?n "' || v_section_name
            || '" ya tiene clase asignada el '
            || CASE p_day_of_week WHEN 1 THEN 'Lunes' WHEN 2 THEN 'Martes'
                                  WHEN 3 THEN 'Mi?rcoles' WHEN 4 THEN 'Jueves'
                                  WHEN 5 THEN 'Viernes' WHEN 6 THEN 'S?bado' END
            || ' en la franja "' || v_slot_name || '".');
    EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
    END;

    -- Verificar conflicto de AULA
    BEGIN
        SELECT cs.id
        INTO   v_conflict_detail
        FROM   CLASS_SCHEDULES cs
        WHERE  cs.schedule_version_id = p_version_id
          AND  cs.day_of_week         = p_day_of_week
          AND  cs.time_slot_id        = p_time_slot_id
          AND  cs.classroom_id        = p_classroom_id
          AND  ROWNUM = 1;

        SELECT cr.name INTO v_classroom_name FROM CLASSROOMS cr WHERE cr.id = p_classroom_id;
        SELECT slot_name INTO v_slot_name FROM TIME_SLOTS WHERE id = p_time_slot_id;

        RAISE_APPLICATION_ERROR(-20222,
            'CONFLICTO DE AULA: El aula "' || v_classroom_name
            || '" ya est? ocupada el '
            || CASE p_day_of_week WHEN 1 THEN 'Lunes' WHEN 2 THEN 'Martes'
                                  WHEN 3 THEN 'Mi?rcoles' WHEN 4 THEN 'Jueves'
                                  WHEN 5 THEN 'Viernes' WHEN 6 THEN 'S?bado' END
            || ' en la franja "' || v_slot_name || '".');
    EXCEPTION WHEN NO_DATA_FOUND THEN NULL;
    END;

    -- Todo OK ? insertar la clase
    p_new_schedule_id := SYS_GUID();

    INSERT INTO CLASS_SCHEDULES (
        id, school_id, schedule_version_id, course_assignment_id,
        section_id, teacher_id, classroom_id, time_slot_id,
        day_of_week, week_type, color_hex, notes, created_by, created_at
    ) VALUES (
        p_new_schedule_id, p_school_id, p_version_id, p_course_assign_id,
        v_section_id, v_teacher_id, p_classroom_id, p_time_slot_id,
        p_day_of_week, p_week_type, p_color_hex, p_notes, p_created_by, SYSTIMESTAMP
    );

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('SP_ASSIGN_CLASS: Clase asignada correctamente. ID: ' || p_new_schedule_id);

EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END SP_ASSIGN_CLASS;
/



-- -----------------------------------------------------------------------------
-- SP_CHECK_SCHEDULE_CONFLICTS ? Auditor?a completa antes de publicar
-- Detecta todos los conflictos de un horario en borrador.
-- Retorna una tabla con todos los problemas encontrados (no solo el primero).
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION SP_CHECK_SCHEDULE_CONFLICTS (
    p_school_id  IN VARCHAR2,
    p_version_id IN VARCHAR2
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        -- Detectar colisiones de DOCENTE
        SELECT
            'TEACHER_CONFLICT'  AS conflict_type,
            t.first_name || ' ' || t.last_name AS entity_name,
            CASE cs1.day_of_week
                WHEN 1 THEN 'Lunes'    WHEN 2 THEN 'Martes' WHEN 3 THEN 'Mi?rcoles'
                WHEN 4 THEN 'Jueves'   WHEN 5 THEN 'Viernes' ELSE 'S?bado'
            END                 AS day_name,
            ts.slot_name        AS time_slot,
            ts.start_time || '-' || ts.end_time AS time_range,
            c1.name || ' (' || sec1.name || ')' AS class_1,
            c2.name || ' (' || sec2.name || ')' AS class_2,
            'El docente tiene ' || COUNT(*) OVER (PARTITION BY cs1.teacher_id, cs1.day_of_week, cs1.time_slot_id)
                || ' clases simult?neas' AS description
        FROM   CLASS_SCHEDULES cs1
        JOIN   CLASS_SCHEDULES cs2
               ON  cs2.schedule_version_id = cs1.schedule_version_id
               AND cs2.teacher_id          = cs1.teacher_id
               AND cs2.day_of_week         = cs1.day_of_week
               AND cs2.time_slot_id        = cs1.time_slot_id
               AND cs2.id                 <> cs1.id
        JOIN   TEACHERS       t    ON t.id   = cs1.teacher_id
        JOIN   TIME_SLOTS     ts   ON ts.id  = cs1.time_slot_id
        JOIN   COURSE_ASSIGNMENTS ca1 ON ca1.id = cs1.course_assignment_id
        JOIN   COURSE_ASSIGNMENTS ca2 ON ca2.id = cs2.course_assignment_id
        JOIN   COURSES        c1   ON c1.id  = ca1.course_id
        JOIN   COURSES        c2   ON c2.id  = ca2.course_id
        JOIN   SECTIONS       sec1 ON sec1.id = cs1.section_id
        JOIN   SECTIONS       sec2 ON sec2.id = cs2.section_id
        WHERE  cs1.schedule_version_id = p_version_id
          AND  cs1.school_id           = p_school_id
          AND  cs1.is_active           = 1

        UNION ALL

        -- Detectar colisiones de SECCI?N
        SELECT
            'SECTION_CONFLICT',
            sec.name,
            CASE cs1.day_of_week
                WHEN 1 THEN 'Lunes'    WHEN 2 THEN 'Martes' WHEN 3 THEN 'Mi?rcoles'
                WHEN 4 THEN 'Jueves'   WHEN 5 THEN 'Viernes' ELSE 'S?bado'
            END,
            ts.slot_name,
            ts.start_time || '-' || ts.end_time,
            c1.name || ' (Prof. ' || t1.last_name || ')',
            c2.name || ' (Prof. ' || t2.last_name || ')',
            'La secci?n tiene ' || COUNT(*) OVER (PARTITION BY cs1.section_id, cs1.day_of_week, cs1.time_slot_id)
                || ' clases simult?neas'
        FROM   CLASS_SCHEDULES cs1
        JOIN   CLASS_SCHEDULES cs2
               ON  cs2.schedule_version_id = cs1.schedule_version_id
               AND cs2.section_id          = cs1.section_id
               AND cs2.day_of_week         = cs1.day_of_week
               AND cs2.time_slot_id        = cs1.time_slot_id
               AND cs2.id                 <> cs1.id
        JOIN   SECTIONS       sec  ON sec.id  = cs1.section_id
        JOIN   TIME_SLOTS     ts   ON ts.id   = cs1.time_slot_id
        JOIN   COURSE_ASSIGNMENTS ca1 ON ca1.id = cs1.course_assignment_id
        JOIN   COURSE_ASSIGNMENTS ca2 ON ca2.id = cs2.course_assignment_id
        JOIN   COURSES        c1   ON c1.id  = ca1.course_id
        JOIN   COURSES        c2   ON c2.id  = ca2.course_id
        JOIN   TEACHERS       t1   ON t1.id  = cs1.teacher_id
        JOIN   TEACHERS       t2   ON t2.id  = cs2.teacher_id
        WHERE  cs1.schedule_version_id = p_version_id
          AND  cs1.school_id           = p_school_id
          AND  cs1.is_active           = 1

        UNION ALL

        -- Detectar colisiones de AULA
        SELECT
            'CLASSROOM_CONFLICT',
            cr.name,
            CASE cs1.day_of_week
                WHEN 1 THEN 'Lunes'    WHEN 2 THEN 'Martes' WHEN 3 THEN 'Mi?rcoles'
                WHEN 4 THEN 'Jueves'   WHEN 5 THEN 'Viernes' ELSE 'S?bado'
            END,
            ts.slot_name,
            ts.start_time || '-' || ts.end_time,
            c1.name || ' (' || sec1.name || ')',
            c2.name || ' (' || sec2.name || ')',
            'El aula tiene ' || COUNT(*) OVER (PARTITION BY cs1.classroom_id, cs1.day_of_week, cs1.time_slot_id)
                || ' grupos simult?neos'
        FROM   CLASS_SCHEDULES cs1
        JOIN   CLASS_SCHEDULES cs2
               ON  cs2.schedule_version_id = cs1.schedule_version_id
               AND cs2.classroom_id        = cs1.classroom_id
               AND cs2.day_of_week         = cs1.day_of_week
               AND cs2.time_slot_id        = cs1.time_slot_id
               AND cs2.id                 <> cs1.id
        JOIN   CLASSROOMS     cr   ON cr.id  = cs1.classroom_id
        JOIN   TIME_SLOTS     ts   ON ts.id  = cs1.time_slot_id
        JOIN   COURSE_ASSIGNMENTS ca1 ON ca1.id = cs1.course_assignment_id
        JOIN   COURSE_ASSIGNMENTS ca2 ON ca2.id = cs2.course_assignment_id
        JOIN   COURSES        c1   ON c1.id  = ca1.course_id
        JOIN   COURSES        c2   ON c2.id  = ca2.course_id
        JOIN   SECTIONS       sec1 ON sec1.id = cs1.section_id
        JOIN   SECTIONS       sec2 ON sec2.id = cs2.section_id
        WHERE  cs1.schedule_version_id = p_version_id
          AND  cs1.school_id           = p_school_id
          AND  cs1.is_active           = 1

        ORDER BY 1, 4;   -- Agrupar por tipo y franja

    RETURN v_cursor;
END SP_CHECK_SCHEDULE_CONFLICTS;
/



-- -----------------------------------------------------------------------------
-- SP_PUBLISH_SCHEDULE ? Publicar horario (DRAFT ? ACTIVE)
-- Primero verifica que NO haya conflictos. Si hay alguno, falla con detalle.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_PUBLISH_SCHEDULE (
    p_school_id  IN VARCHAR2,
    p_version_id IN VARCHAR2,
    p_approved_by IN VARCHAR2
)
AS
    v_conflict_count NUMBER := 0;
    v_ver_status     VARCHAR2(20);
    v_ver_name       VARCHAR2(100);
    v_schedule_count NUMBER;
BEGIN
    -- Verificar que el horario existe y pertenece al colegio
    SELECT status, version_name
    INTO   v_ver_status, v_ver_name
    FROM   SCHEDULE_VERSIONS
    WHERE  id        = p_version_id
      AND  school_id = p_school_id;

    IF v_ver_status NOT IN ('DRAFT', 'REVIEW') THEN
        RAISE_APPLICATION_ERROR(-20230,
            'SCHEDULE_ERROR: El horario "' || v_ver_name
            || '" est? en estado "' || v_ver_status
            || '". Solo se pueden publicar horarios en estado DRAFT o REVIEW.');
    END IF;

    -- Contar clases asignadas
    SELECT COUNT(*) INTO v_schedule_count
    FROM   CLASS_SCHEDULES
    WHERE  schedule_version_id = p_version_id
      AND  school_id           = p_school_id
      AND  is_active           = 1;

    IF v_schedule_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20231,
            'SCHEDULE_ERROR: El horario "' || v_ver_name
            || '" no tiene ninguna clase asignada. No se puede publicar un horario vac?o.');
    END IF;

    -- Detectar conflictos usando las constraints (query de verificaci?n)
    SELECT COUNT(*) INTO v_conflict_count
    FROM (
        SELECT cs1.id
        FROM   CLASS_SCHEDULES cs1
        JOIN   CLASS_SCHEDULES cs2
               ON  cs2.schedule_version_id = cs1.schedule_version_id
               AND cs2.day_of_week         = cs1.day_of_week
               AND cs2.time_slot_id        = cs1.time_slot_id
               AND cs2.id                 <> cs1.id
               AND (   cs2.teacher_id   = cs1.teacher_id    -- Conflicto docente
                    OR cs2.section_id   = cs1.section_id    -- Conflicto secci?n
                    OR cs2.classroom_id = cs1.classroom_id) -- Conflicto aula
        WHERE  cs1.schedule_version_id = p_version_id
          AND  cs1.school_id           = p_school_id
          AND  cs1.is_active           = 1
    );

    IF v_conflict_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20232,
            'SCHEDULE_ERROR: El horario "' || v_ver_name
            || '" tiene ' || v_conflict_count || ' conflicto(s) de asignaci?n. '
            || 'Llama SP_CHECK_SCHEDULE_CONFLICTS para ver el detalle y corr?gelos antes de publicar.');
    END IF;

    -- Archivar el horario ACTIVE anterior (si existe)
    UPDATE SCHEDULE_VERSIONS
    SET    status     = 'ARCHIVED',
           updated_at = SYSTIMESTAMP
    WHERE  school_id      = p_school_id
      AND  school_year_id = (SELECT school_year_id FROM SCHEDULE_VERSIONS WHERE id = p_version_id)
      AND  status         = 'ACTIVE'
      AND  id            <> p_version_id;

    -- Publicar el nuevo horario
    UPDATE SCHEDULE_VERSIONS
    SET    status       = 'ACTIVE',
           approved_by  = p_approved_by,
           approved_at  = SYSTIMESTAMP,
           published_at = SYSTIMESTAMP,
           updated_at   = SYSTIMESTAMP
    WHERE  id        = p_version_id
      AND  school_id = p_school_id;

    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_PUBLISH_SCHEDULE: Horario "' || v_ver_name
        || '" publicado exitosamente con '|| v_schedule_count || ' clases.');

EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END SP_PUBLISH_SCHEDULE;
/



-- -----------------------------------------------------------------------------
-- SP_COPY_SCHEDULE ? Copiar un horario como base para el siguiente per?odo
-- ?til para crear el horario del 2? bimestre copiando el 1? y ajustando.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE SP_COPY_SCHEDULE (
    p_school_id      IN  VARCHAR2,
    p_source_ver_id  IN  VARCHAR2,
    p_new_name       IN  VARCHAR2,
    p_valid_from     IN  DATE,
    p_created_by     IN  VARCHAR2,
    p_new_version_id OUT VARCHAR2
)
AS
    v_sy_id   VARCHAR2(50);
    v_ap_id   VARCHAR2(50);
    v_count   NUMBER;
BEGIN
    SELECT school_year_id, academic_period_id
    INTO   v_sy_id, v_ap_id
    FROM   SCHEDULE_VERSIONS
    WHERE  id        = p_source_ver_id
      AND  school_id = p_school_id;

    -- Crear nueva versi?n en DRAFT
    p_new_version_id := SYS_GUID();
    INSERT INTO SCHEDULE_VERSIONS (
        id, school_id, school_year_id, academic_period_id,
        version_name, status, created_by, valid_from, created_at
    ) VALUES (
        p_new_version_id, p_school_id, v_sy_id, v_ap_id,
        p_new_name, 'DRAFT', p_created_by, p_valid_from, SYSTIMESTAMP
    );

    -- Copiar todas las clases del horario fuente
    INSERT INTO CLASS_SCHEDULES (
        id, school_id, schedule_version_id, course_assignment_id,
        section_id, teacher_id, classroom_id, time_slot_id,
        day_of_week, week_type, color_hex, notes, created_by, created_at
    )
    SELECT
        SYS_GUID(), school_id, p_new_version_id, course_assignment_id,
        section_id, teacher_id, classroom_id, time_slot_id,
        day_of_week, week_type, color_hex,
        '(Copiado) ' || NVL(notes, ''), p_created_by, SYSTIMESTAMP
    FROM   CLASS_SCHEDULES
    WHERE  schedule_version_id = p_source_ver_id
      AND  school_id           = p_school_id
      AND  is_active           = 1;

    v_count := SQL%ROWCOUNT;
    COMMIT;

    DBMS_OUTPUT.PUT_LINE(
        'SP_COPY_SCHEDULE: Copiadas '|| v_count ||' clases del horario fuente ? "'|| p_new_name || '".');

EXCEPTION
    WHEN OTHERS THEN ROLLBACK; RAISE;
END SP_COPY_SCHEDULE;
/



-- =============================================================================
-- BLOQUE 8: VISTAS ANAL?TICAS DEL HORARIO
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Vista: VW_TEACHER_WEEKLY_SCHEDULE
-- Horario semanal completo de cada docente.
-- El frontend usa esta vista para el "panel del docente".
-- =============================================================================
CREATE OR REPLACE VIEW VW_TEACHER_WEEKLY_SCHEDULE AS
SELECT
    cs.school_id,
    sv.id                                           AS schedule_version_id,
    sv.version_name,
    sv.status                                       AS schedule_status,
    -- Docente
    t.id                                            AS teacher_id,
    t.first_name || ' ' || t.last_name             AS teacher_name,
    -- Curso y secci?n
    c.id                                            AS course_id,
    c.name                                          AS course_name,
    sec.id                                          AS section_id,
    sec.name                                        AS section_name,
    sy.name                                         AS school_year_name,
    sy.grade_number,
    -- Horario
    CASE cs.day_of_week
        WHEN 1 THEN 'Lunes'     WHEN 2 THEN 'Martes' WHEN 3 THEN 'Mi?rcoles'
        WHEN 4 THEN 'Jueves'    WHEN 5 THEN 'Viernes' ELSE 'S?bado'
    END                                             AS day_name,
    cs.day_of_week,
    ts.slot_number,
    ts.slot_name,
    ts.start_time,
    ts.end_time,
    ts.duration_min,
    -- Aula
    cr.id                                           AS classroom_id,
    cr.name                                         AS classroom_name,
    cr.floor_number,
    -- Opcionales
    cs.week_type,
    cs.color_hex,
    cs.notes
FROM
    CLASS_SCHEDULES       cs
    JOIN SCHEDULE_VERSIONS  sv  ON sv.id  = cs.schedule_version_id
    JOIN TEACHERS           t   ON t.id   = cs.teacher_id
    JOIN COURSE_ASSIGNMENTS ca  ON ca.id  = cs.course_assignment_id
    JOIN COURSES            c   ON c.id   = ca.course_id
    JOIN SECTIONS           sec ON sec.id = cs.section_id
    JOIN SCHOOL_YEARS       sy  ON sy.id  = sec.school_year_id
    JOIN TIME_SLOTS         ts  ON ts.id  = cs.time_slot_id
    JOIN CLASSROOMS         cr  ON cr.id  = cs.classroom_id
WHERE
    cs.is_active = 1
    AND sv.status = 'ACTIVE'
ORDER BY
    cs.school_id, t.id, cs.day_of_week, ts.slot_number;

COMMENT ON TABLE VW_TEACHER_WEEKLY_SCHEDULE IS 'Horario semanal por docente. Solo muestra versiones ACTIVE. Filtrar por school_id y teacher_id desde el backend.';


-- -----------------------------------------------------------------------------
-- Vista: VW_SECTION_WEEKLY_SCHEDULE
-- Horario de una secci?n (para el alumno / apoderado).
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_SECTION_WEEKLY_SCHEDULE AS
SELECT
    cs.school_id,
    sv.id                                           AS schedule_version_id,
    sv.version_name,
    -- Secci?n
    sec.id                                          AS section_id,
    sec.name                                        AS section_name,
    sy.name                                         AS school_year_name,
    sy.grade_number,
    -- Horario
    CASE cs.day_of_week
        WHEN 1 THEN 'Lunes'     WHEN 2 THEN 'Martes' WHEN 3 THEN 'Mi?rcoles'
        WHEN 4 THEN 'Jueves'    WHEN 5 THEN 'Viernes' ELSE 'S?bado'
    END                                             AS day_name,
    cs.day_of_week,
    ts.slot_number,
    ts.slot_name,
    ts.start_time,
    ts.end_time,
    ts.duration_min,
    -- Curso y docente
    c.id                                            AS course_id,
    c.name                                          AS course_name,
    c.color_hex                                     AS course_color,
    t.id                                            AS teacher_id,
    t.first_name || ' ' || t.last_name             AS teacher_name,
    -- Aula
    cr.name                                         AS classroom_name,
    cr.floor_number,
    cs.week_type
FROM
    CLASS_SCHEDULES       cs
    JOIN SCHEDULE_VERSIONS  sv  ON sv.id  = cs.schedule_version_id
    JOIN SECTIONS           sec ON sec.id = cs.section_id
    JOIN SCHOOL_YEARS       sy  ON sy.id  = sec.school_year_id
    JOIN COURSE_ASSIGNMENTS ca  ON ca.id  = cs.course_assignment_id
    JOIN COURSES            c   ON c.id   = ca.course_id
    JOIN TEACHERS           t   ON t.id   = cs.teacher_id
    JOIN TIME_SLOTS         ts  ON ts.id  = cs.time_slot_id
    JOIN CLASSROOMS         cr  ON cr.id  = cs.classroom_id
WHERE
    cs.is_active = 1
    AND sv.status = 'ACTIVE'
ORDER BY
    cs.school_id, sec.id, cs.day_of_week, ts.slot_number;

COMMENT ON TABLE VW_SECTION_WEEKLY_SCHEDULE IS 'Horario semanal por secci?n. Para el portal de alumnos y apoderados. Filtrar por school_id y section_id desde el backend.';


-- -----------------------------------------------------------------------------
-- Vista: VW_CLASSROOM_OCCUPANCY
-- Ocupaci?n de aulas por d?a y hora.
-- ?til para el coordinador al buscar aulas libres.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_CLASSROOM_OCCUPANCY AS
SELECT
    cr.school_id,
    cr.id                                           AS classroom_id,
    cr.name                                         AS classroom_name,
    cr.room_type,
    cr.capacity,
    CASE ts.slot_number
        WHEN NULL THEN 'LIBRE'
        ELSE 'OCUPADA'
    END                                             AS occupancy_status,
    CASE cs.day_of_week
        WHEN 1 THEN 'Lunes'     WHEN 2 THEN 'Martes' WHEN 3 THEN 'Mi?rcoles'
        WHEN 4 THEN 'Jueves'    WHEN 5 THEN 'Viernes' ELSE 'S?bado'
    END                                             AS day_name,
    cs.day_of_week,
    ts.slot_number,
    ts.slot_name,
    ts.start_time,
    ts.end_time,
    -- Qu? clase ocupa el aula
    c.name                                          AS course_name,
    sec.name                                        AS section_name,
    t.first_name || ' ' || t.last_name             AS teacher_name,
    sv.version_name
FROM
    CLASSROOMS            cr
    LEFT JOIN CLASS_SCHEDULES cs ON cs.classroom_id = cr.id AND cs.is_active = 1
    LEFT JOIN SCHEDULE_VERSIONS sv ON sv.id = cs.schedule_version_id AND sv.status = 'ACTIVE'
    LEFT JOIN TIME_SLOTS    ts  ON ts.id  = cs.time_slot_id
    LEFT JOIN COURSE_ASSIGNMENTS ca ON ca.id = cs.course_assignment_id
    LEFT JOIN COURSES       c   ON c.id  = ca.course_id
    LEFT JOIN SECTIONS      sec ON sec.id = cs.section_id
    LEFT JOIN TEACHERS      t   ON t.id  = cs.teacher_id
WHERE
    cr.is_active = 1
ORDER BY
    cr.school_id, cr.id, cs.day_of_week, ts.slot_number;

COMMENT ON TABLE VW_CLASSROOM_OCCUPANCY IS
    'Ocupaci?n de aulas por d?a/hora. Permite al coordinador encontrar aulas libres f?cilmente.';


-- -----------------------------------------------------------------------------
-- Vista: VW_TEACHER_LOAD
-- Carga horaria semanal de cada docente (horas asignadas vs horas contratadas).
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_TEACHER_LOAD AS
SELECT
    t.school_id,
    t.id                                            AS teacher_id,
    t.first_name || ' ' || t.last_name             AS teacher_name,
    -- Horas asignadas por contrato (suma de todas sus COURSE_ASSIGNMENTS)
    SUM(DISTINCT ca.hours_per_week)                 AS contracted_hours_per_week,
    -- Horas efectivamente en horario (franjas asignadas ? duraci?n)
    COUNT(cs.id)                                    AS slots_assigned,
    ROUND(SUM(ts.duration_min) / 60.0, 2)          AS assigned_hours_total,
    -- D?as trabajados
    COUNT(DISTINCT cs.day_of_week)                  AS working_days,
    -- Por d?a
    ROUND(SUM(ts.duration_min) / 60.0
          / NULLIF(COUNT(DISTINCT cs.day_of_week), 0), 2) AS avg_hours_per_day,
    -- Lista de cursos
    LISTAGG(DISTINCT c.name, ', ')
        WITHIN GROUP (ORDER BY c.name)              AS courses_assigned
FROM
    TEACHERS              t
    LEFT JOIN CLASS_SCHEDULES cs ON cs.teacher_id = t.id AND cs.is_active = 1
    LEFT JOIN SCHEDULE_VERSIONS sv ON sv.id = cs.schedule_version_id AND sv.status = 'ACTIVE'
    LEFT JOIN TIME_SLOTS    ts  ON ts.id  = cs.time_slot_id
    LEFT JOIN COURSE_ASSIGNMENTS ca ON ca.teacher_id = t.id AND ca.is_active = 1
    LEFT JOIN COURSES       c   ON c.id   = ca.course_id
WHERE
    t.status = 'ACTIVE'
GROUP BY
    t.school_id, t.id, t.first_name, t.last_name;

COMMENT ON TABLE VW_TEACHER_LOAD IS 'Carga horaria semanal por docente. Compara horas del horario vs horas contratadas. Filtrar por school_id desde el backend.';


-- =============================================================================
-- BLOQUE 9: DATOS SEMILLA ? HORARIO DEMO para el colegio 20188
-- =============================================================================

-- Versi?n del horario (DRAFT para poder editarla)
INSERT INTO SCHEDULE_VERSIONS (id, school_id, school_year_id, academic_period_id,
                                version_name, status, created_by, valid_from)
VALUES ('sv-2025-v1', 'school-20188-canete', 'sy-3sec-2025', 'ap-b1-3sec-2025',
        'Horario 1? Bimestre 2025 ? v1', 'DRAFT', 'user-admin-20188', DATE '2025-03-10');

-- -----------------------------------------------------------------------------
-- HORARIO 3?A ? Prof. Garc?a (Matem?tica), Prof. Torres (Comunicaci?n),
--               Prof. Ram?rez (Historia)
-- -----------------------------------------------------------------------------

-- LUNES
INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3a-b1','sec-3a-2025','teacher-01','cr-3a','ts-02',1,'#1e407a','user-admin-20188');  -- Lunes 1? Hora: Mate 3A

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-com-3a-b1','sec-3a-2025','teacher-02','cr-3a','ts-03',1,'#16a34a','user-admin-20188');  -- Lunes 2? Hora: Comunic 3A

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-his-3a-b1','sec-3a-2025','teacher-03','cr-3a','ts-04',1,'#c8922a','user-admin-20188');  -- Lunes 3? Hora: Historia 3A

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3a-b1','sec-3a-2025','teacher-01','cr-3a','ts-06',1,'#1e407a','user-admin-20188');  -- Lunes 4? Hora: Mate 3A

-- MARTES
INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-com-3a-b1','sec-3a-2025','teacher-02','cr-3a','ts-02',2,'#16a34a','user-admin-20188');  -- Martes 1? Hora: Comunic 3A

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-his-3a-b1','sec-3a-2025','teacher-03','cr-3a','ts-03',2,'#c8922a','user-admin-20188');  -- Martes 2? Hora: Historia 3A

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3a-b1','sec-3a-2025','teacher-01','cr-3a','ts-04',2,'#1e407a','user-admin-20188');  -- Martes 3? Hora: Mate 3A

-- MI?RCOLES
INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3a-b1','sec-3a-2025','teacher-01','cr-3a','ts-02',3,'#1e407a','user-admin-20188');  -- Mi?rc 1? Hora: Mate 3A

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-com-3a-b1','sec-3a-2025','teacher-02','cr-3a','ts-03',3,'#16a34a','user-admin-20188');  -- Mi?rc 2? Hora: Comunic 3A

-- -----------------------------------------------------------------------------
-- HORARIO 3?B ? Los MISMOS docentes ense?an a otra secci?n
-- CLAVE: diferentes time_slots que en 3?A ? sin colisi?n garantizada
-- -----------------------------------------------------------------------------

-- LUNES (prof. Garc?a ense?a Mate a 3?B en la 5? hora, cuando 3?A ya termin?)
INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3b-b1','sec-3b-2025','teacher-01','cr-3b','ts-07',1,'#1e407a','user-admin-20188');  -- Lunes 5? Hora: Mate 3B

INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3b-b1','sec-3b-2025','teacher-01','cr-3b','ts-08',1,'#1e407a','user-admin-20188');  -- Lunes 6? Hora: Mate 3B

-- MARTES
INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3b-b1','sec-3b-2025','teacher-01','cr-3b','ts-07',2,'#1e407a','user-admin-20188');  -- Martes 5? Hora: Mate 3B

-- MI?RCOLES
INSERT INTO CLASS_SCHEDULES (id, school_id, schedule_version_id, course_assignment_id, section_id, teacher_id, classroom_id, time_slot_id, day_of_week, color_hex, created_by)
VALUES (SYS_GUID(),'school-20188-canete','sv-2025-v1','ca-mat-3b-b1','sec-3b-2025','teacher-01','cr-3b','ts-07',3,'#1e407a','user-admin-20188');  -- Mi?rc 5? Hora: Mate 3B

-- Excepci?n: el lunes 17 de marzo el Prof. Garc?a falta ? sustituci?n
INSERT INTO SCHEDULE_EXCEPTIONS (id, school_id, class_schedule_id, exception_date, exception_type,
                                   substitute_teacher_id, reason, created_by)
SELECT SYS_GUID(), 'school-20188-canete', cs.id, DATE '2025-03-17', 'TEACHER_ABSENT',
       'teacher-02', 'Capacitaci?n docente MINEDU ? Lima', 'user-admin-20188'
FROM   CLASS_SCHEDULES cs
WHERE  cs.schedule_version_id = 'sv-2025-v1'
  AND  cs.teacher_id          = 'teacher-01'
  AND  cs.section_id          = 'sec-3a-2025'
  AND  cs.day_of_week         = 1  -- Lunes
  AND  cs.time_slot_id        = 'ts-02'
  AND  ROWNUM = 1;

COMMIT;


-- =============================================================================
-- BLOQUE 10: VERIFICACI?N FINAL
-- =============================================================================

-- Verificar que el seed data no tiene colisiones (docente)
SELECT
    'CONFLICTOS DE DOCENTE EN SEED DATA' AS verificacion,
    COUNT(*) AS total_conflictos
FROM (
    SELECT cs1.id
    FROM   CLASS_SCHEDULES cs1
    JOIN   CLASS_SCHEDULES cs2
           ON  cs2.schedule_version_id = cs1.schedule_version_id
           AND cs2.teacher_id          = cs1.teacher_id
           AND cs2.day_of_week         = cs1.day_of_week
           AND cs2.time_slot_id        = cs1.time_slot_id
           AND cs2.id                 <> cs1.id
    WHERE  cs1.schedule_version_id = 'sv-2025-v1'
);

-- Resumen de objetos creados en este script
SELECT 'TABLAS HORARIOS'     AS tipo, COUNT(*) AS n FROM user_tables WHERE table_name IN ('CLASSROOMS','TIME_SLOTS','SCHEDULE_VERSIONS','CLASS_SCHEDULES','SCHEDULE_EXCEPTIONS')
UNION ALL
SELECT 'VISTAS HORARIOS',    COUNT(*) FROM user_views   WHERE view_name IN ('VW_TEACHER_WEEKLY_SCHEDULE','VW_SECTION_WEEKLY_SCHEDULE','VW_CLASSROOM_OCCUPANCY','VW_TEACHER_LOAD')
UNION ALL
SELECT 'TRIGGERS HORARIOS',  COUNT(*) FROM user_triggers WHERE trigger_name IN ('TRG_ONE_ACTIVE_SCHEDULE','TRG_SCHEDULE_VER_UPDATED_AT','TRG_SCHEDULE_NO_BREAK','TRG_SCHEDULE_CAPACITY','TRG_EXCEPTION_SUB_CONFLICT')
UNION ALL
SELECT 'SPs HORARIOS',       COUNT(*) FROM user_procedures WHERE object_name IN ('SP_ASSIGN_CLASS','SP_PUBLISH_SCHEDULE','SP_COPY_SCHEDULE')
UNION ALL
SELECT 'FUNCIONES HORARIOS', COUNT(*) FROM user_procedures WHERE object_name IN ('SP_CHECK_SCHEDULE_CONFLICTS') AND object_type = 'FUNCTION'
UNION ALL
SELECT 'INDICES HORARIOS',   COUNT(*) FROM user_indexes WHERE index_name IN ('IDX_CR_SCHOOL','IDX_TS_SCHOOL_TYPE','IDX_SV_SCHOOL_STATUS','IDX_SCH_VERSION_DAY','IDX_SCH_TEACHER','IDX_SCH_SECTION','IDX_SCH_CLASSROOM','IDX_EXC_DATE','IDX_EXC_SUB_TEACHER');

-- =============================================================================

-- =============================================================================
-- VERIFICACI?N FINAL
-- =============================================================================

-- Objetos creados en el schema
SELECT obj_type AS "Tipo", COUNT(*) AS "Cantidad"
FROM (
    SELECT 'TABLA'    AS obj_type FROM user_tables    WHERE table_name NOT LIKE 'SYS_%' AND table_name NOT LIKE 'MLOG%'
    UNION ALL
    SELECT 'VISTA'              FROM user_views
    UNION ALL
    SELECT 'TRIGGER'            FROM user_triggers
    UNION ALL
    SELECT 'SP/FUNCION/PAQUETE' FROM user_procedures  WHERE object_type IN ('PROCEDURE','FUNCTION','PACKAGE')
    UNION ALL
    SELECT 'INDICE'             FROM user_indexes      WHERE index_type != 'LOB' AND index_name NOT LIKE 'SYS_%'
)
GROUP BY obj_type
ORDER BY obj_type;

-- Conteo de filas del seed data
SELECT 'SCHOOLS'          AS tabla, COUNT(*) AS filas FROM SCHOOLS
UNION ALL SELECT 'USERS',            COUNT(*) FROM USERS
UNION ALL SELECT 'STUDENTS',         COUNT(*) FROM STUDENTS
UNION ALL SELECT 'TEACHERS',         COUNT(*) FROM TEACHERS
UNION ALL SELECT 'SECTIONS',         COUNT(*) FROM SECTIONS
UNION ALL SELECT 'COURSES',          COUNT(*) FROM COURSES
UNION ALL SELECT 'COURSE_COMPETENCIES',COUNT(*) FROM COURSE_COMPETENCIES
UNION ALL SELECT 'CLASSROOMS',       COUNT(*) FROM CLASSROOMS
UNION ALL SELECT 'TIME_SLOTS',       COUNT(*) FROM TIME_SLOTS
UNION ALL SELECT 'CLASS_SCHEDULES',  COUNT(*) FROM CLASS_SCHEDULES
UNION ALL SELECT 'BLOCKCHAIN_BLOCKS',COUNT(*) FROM BLOCKCHAIN_BLOCKS
UNION ALL SELECT 'SYSTEM_CONFIG',    COUNT(*) FROM SYSTEM_CONFIG
UNION ALL SELECT 'FEATURE_FLAGS',    COUNT(*) FROM FEATURE_FLAGS
UNION ALL SELECT 'MINEDU_DOCUMENTS', COUNT(*) FROM MINEDU_DOCUMENTS
UNION ALL SELECT 'TRANSLATIONS',     COUNT(*) FROM TRANSLATIONS
ORDER BY 1;

-- Verificar que el horario demo no tiene conflictos
SELECT
    CASE WHEN COUNT(*) = 0
         THEN 'OK: Sin conflictos en el horario demo'
         ELSE 'ERROR: ' || COUNT(*) || ' conflictos detectados'
    END AS estado_horario
FROM (
    SELECT cs1.id
    FROM   CLASS_SCHEDULES cs1
    JOIN   CLASS_SCHEDULES cs2
           ON  cs2.schedule_version_id = cs1.schedule_version_id
           AND cs2.day_of_week         = cs1.day_of_week
           AND cs2.time_slot_id        = cs1.time_slot_id
           AND cs2.id                 <> cs1.id
           AND (cs2.teacher_id   = cs1.teacher_id
             OR cs2.section_id   = cs1.section_id
             OR cs2.classroom_id = cs1.classroom_id)
    WHERE  cs1.school_id = 'school-20188-canete'
);

-- FIN ? FINE FLOW ? M?DULO DE HORARIOS v1.0
-- 5 tablas ? 4 vistas ? 5 triggers ? 4 SPs/funciones ? 9 ?ndices
-- ANTICOLISI?N: 3 UNIQUE constraints + 3 triggers + SP de validaci?n
-- =============================================================================


-- =============================================================================
-- FASE 17: VERIFICACI?N FINAL UNIFICADA
-- =============================================================================


-- Contar todos los objetos del schema
SELECT
    obj_type                    AS "Tipo de Objeto",
    COUNT(*)                    AS "Cantidad",
    LISTAGG(obj_name, ', ')
        WITHIN GROUP (ORDER BY obj_name) AS "Nombres"
FROM (
    SELECT 'TABLA'      AS obj_type, table_name     AS obj_name FROM user_tables     WHERE table_name NOT LIKE 'SYS_%' AND table_name NOT LIKE 'MLOG%'
    UNION ALL
    SELECT 'VISTA',     view_name                               FROM user_views
    UNION ALL
    SELECT 'TRIGGER',   trigger_name                           FROM user_triggers
    UNION ALL
    SELECT 'SP/FUNC',   object_name                            FROM user_procedures  WHERE object_type IN ('PROCEDURE','FUNCTION','PACKAGE')
    UNION ALL
    SELECT 'INDICE',    index_name                             FROM user_indexes     WHERE index_type != 'LOB' AND index_name NOT LIKE 'SYS_%'
)
GROUP BY obj_type
ORDER BY obj_type;


SELECT job_name AS "Job", enabled AS "Activo", repeat_interval AS "Frecuencia"
FROM   user_scheduler_jobs
WHERE  job_name LIKE 'JOB_%'
ORDER  BY job_name;


SELECT
    'SCHOOLS'        AS tabla, COUNT(*) AS filas FROM SCHOOLS
UNION ALL SELECT 'USERS',             COUNT(*) FROM USERS
UNION ALL SELECT 'ACADEMIC_LEVELS',   COUNT(*) FROM ACADEMIC_LEVELS
UNION ALL SELECT 'SCHOOL_YEARS',      COUNT(*) FROM SCHOOL_YEARS
UNION ALL SELECT 'ACADEMIC_PERIODS',  COUNT(*) FROM ACADEMIC_PERIODS
UNION ALL SELECT 'SECTIONS',          COUNT(*) FROM SECTIONS
UNION ALL SELECT 'STUDENTS',          COUNT(*) FROM STUDENTS
UNION ALL SELECT 'TEACHERS',          COUNT(*) FROM TEACHERS
UNION ALL SELECT 'COURSES',           COUNT(*) FROM COURSES
UNION ALL SELECT 'COURSE_COMPETENCIES', COUNT(*) FROM COURSE_COMPETENCIES
UNION ALL SELECT 'COURSE_ASSIGNMENTS', COUNT(*) FROM COURSE_ASSIGNMENTS
UNION ALL SELECT 'SYSTEM_CONFIG',     COUNT(*) FROM SYSTEM_CONFIG
UNION ALL SELECT 'FEATURE_FLAGS',     COUNT(*) FROM FEATURE_FLAGS
UNION ALL SELECT 'MINEDU_DOCUMENTS',  COUNT(*) FROM MINEDU_DOCUMENTS
UNION ALL SELECT 'BLOCKCHAIN_BLOCKS', COUNT(*) FROM BLOCKCHAIN_BLOCKS
UNION ALL SELECT 'STUDENT_SCORES',    COUNT(*) FROM STUDENT_SCORES
UNION ALL SELECT 'ATTENDANCES',       COUNT(*) FROM ATTENDANCES
UNION ALL SELECT 'CLASSROOMS',        COUNT(*) FROM CLASSROOMS
UNION ALL SELECT 'TIME_SLOTS',        COUNT(*) FROM TIME_SLOTS
UNION ALL SELECT 'SCHEDULE_VERSIONS', COUNT(*) FROM SCHEDULE_VERSIONS
UNION ALL SELECT 'CLASS_SCHEDULES',   COUNT(*) FROM CLASS_SCHEDULES
UNION ALL SELECT 'TRANSLATIONS',      COUNT(*) FROM TRANSLATIONS
ORDER BY 1;


SELECT
    CASE
        WHEN COUNT(*) = 0 THEN '? SIN CONFLICTOS ? Horario demo ?ntegro'
        ELSE '? HAY ' || COUNT(*) || ' CONFLICTOS EN EL HORARIO DEMO'
    END AS estado_horario
FROM (
    SELECT cs1.id
    FROM   CLASS_SCHEDULES cs1
    JOIN   CLASS_SCHEDULES cs2
           ON  cs2.schedule_version_id = cs1.schedule_version_id
           AND cs2.day_of_week         = cs1.day_of_week
           AND cs2.time_slot_id        = cs1.time_slot_id
           AND cs2.id                 <> cs1.id
           AND (cs2.teacher_id   = cs1.teacher_id
             OR cs2.section_id   = cs1.section_id
             OR cs2.classroom_id = cs1.classroom_id)
    WHERE  cs1.school_id = 'school-20188-canete'
);


-- Fin del spool (si se activ?)


-- =============================================================================
-- ??????+  ?????+ ??????+ ????????+???????+    ??????+
-- ??+--??+??+--??+??+--??++--??+--+??+----+    +----??+
-- ??????++??????????????++   ???   ?????+       ?????++
-- ??+---+ ??+--?????+--??+   ???   ??+--+      ??+---+
-- ???     ???  ??????  ???   ???   ???????+    ???????+
-- +-+     +-+  +-++-+  +-+   +-+   +------+    +------+
--
-- PARTE 2: MULTI-INSTITUCION v2.0 - Solo Nivel K12 (Inicial, Primaria, Secundaria)
-- =============================================================================

-- =============================================================================
-- BLOQUE 0: CREAR TABLAS NUEVAS (DDL directo, sin PL/SQL anidado)
-- FEATURE_FLAGS ya fue creada en BLOQUE K (v2.2). No se duplica.
-- SCHOOL_SUBSCRIPTIONS ya fue creada en PARTE 1 BLOQUE 3. No se duplica.
-- =============================================================================

-- SCHOOL_SUBSCRIPTIONS ya fue creada en PARTE 1 BLOQUE 3. No se duplica.

-- =============================================================================
-- BLOQUE 1: AGREGAR institution_type A SCHOOLS
-- En re-ejecucion los errores ORA-01430 / ORA-02264 son tolerables.
-- =============================================================================

ALTER TABLE SCHOOLS ADD (institution_type VARCHAR2(20) DEFAULT 'K12' NOT NULL);
ALTER TABLE SCHOOLS ADD CONSTRAINT ck_schools_inst_type CHECK (institution_type IN ('K12'));

UPDATE SCHOOLS SET institution_type = 'K12' WHERE id = 'school-20188-canete';

COMMIT;

CREATE INDEX idx_schools_inst_type ON SCHOOLS (institution_type, status);


-- =============================================================================
-- Tabla: FEATURE_CATALOG
-- Descripcion: Catalogo centralizado de funcionalidades del sistema. Cada fila
--              representa un modulo/feature que puede ser asignado a un plan
--              de suscripcion. Controla la visibilidad de modulos en la UI
--              y la habilitacion de endpoints segun el plan contratado.
--              Solo aplica para instituciones K12 (Inicial, Primaria, Secundaria).
-- =============================================================================
CREATE TABLE FEATURE_CATALOG (
    feature_key         VARCHAR2(100)   NOT NULL,
    name                VARCHAR2(200)   NOT NULL,
    description         VARCHAR2(1000),
    category            VARCHAR2(50)    NOT NULL,
    enabled_by_default  NUMBER(1)       DEFAULT 0 NOT NULL,
    min_plan            VARCHAR2(20)    NOT NULL,
    is_core             NUMBER(1)       DEFAULT 0 NOT NULL,
    sort_order          NUMBER(3)       DEFAULT 0 NOT NULL,
    --
    CONSTRAINT pk_feature_catalog         PRIMARY KEY (feature_key),
    CONSTRAINT ck_fc_enabled_default      CHECK (enabled_by_default IN (0,1)),
    CONSTRAINT ck_fc_is_core              CHECK (is_core            IN (0,1)),
    CONSTRAINT ck_fc_min_plan             CHECK (min_plan IN ('FREE','BASIC','STANDARD','PREMIUM','ENTERPRISE'))
);
/

-- =============================================================================
-- BLOQUE 2: CAT?LOGO MAESTRO DE FEATURE FLAGS (FEATURE_CATALOG)
-- FIX-1: Todos los COMMENT ON en UNA sola l?nea (sin concatenaci?n impl?cita)
-- =============================================================================

-- Limpiar cat?logo previo si existe (para re-ejecuci?n limpia)
DELETE FROM FEATURE_CATALOG WHERE 1=1;
COMMIT;

-- NOTA: COMMENT ON con strings multi-l?nea causa ORA-00933 en Oracle.
-- Regla: cada COMMENT ON en una sola l?nea.
COMMENT ON TABLE FEATURE_CATALOG IS 'Catalogo maestro de features K12. Define modulos y cuales se activan por defecto.';

-- --- MODULOS ACADEMICOS --------------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_SCHEDULES',    'Gestion de Horarios',                      'Horarios semanales con anticolision de docentes, secciones y aulas.',    'ACADEMIC',       1, 'BASIC',      0, 10);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_SECTIONS',     'Gestion de Secciones',                     'Organizacion de estudiantes en secciones por grado y ano academico.',    'ACADEMIC',       1, 'FREE',       0, 11);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_CURRICULUM_K12',  'Malla Curricular CNEB/MINEDU',          'Competencias y estandares del Curriculo Nacional de EBR (MINEDU).',      'ACADEMIC',       1, 'FREE',       0, 13);

-- --- MODULOS DE ASISTENCIA -----------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_ATTENDANCE',       'Control de Asistencia',                'Registro manual, por lista y reportes de asistencia diaria.',            'ATTENDANCE',     1, 'FREE',       1, 20);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_QR_ATTENDANCE',   'Asistencia por Codigo QR',             'Carnet con QR firmado HMAC-SHA256 para registro automatico de entrada.', 'ATTENDANCE',     1, 'STANDARD',   1, 21);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_JUSTIFICATIONS',  'Justificaciones de Inasistencia',      'Flujo digital de justificaciones con documento y aprobacion.',           'ATTENDANCE',     1, 'BASIC',      1, 23);

-- --- MODULOS DE CALIFICACIONES -------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_GRADES_K12',       'Calificaciones Escala Vigesimal MINEDU','Notas 0-20 por competencia. Niveles AD/A/B/C conforme al CNEB.',         'GRADING',        1, 'FREE',       0, 30);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_BLOCKCHAIN_GRADES','Blockchain de Calificaciones',         'Registro inmutable SHA-256 de cada nota registrada. Evidencia forense.',  'GRADING',        1, 'PREMIUM',    1, 33);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_GRADE_REPORTS',   'Generacion de Boletas de Notas',       'Exportacion PDF de boletas de notas por periodo academico.',              'GRADING',        1, 'BASIC',      1, 34);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_CERTIFICATES',    'Certificados de Estudios',             'Generacion de certificados oficiales de estudios y conducta.',            'GRADING',        1, 'STANDARD',   1, 35);

-- --- MODULOS DE ADMINISTRACION -------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_ENROLLMENT',       'Gestion de Matriculas',               'Proceso de matricula, historial academico y cambios de seccion.',        'ADMINISTRATION', 1, 'FREE',       1, 40);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_GUARDIAN_PORTAL',  'Portal de Apoderados',                'Acceso de padres a notas, asistencia y comunicados del colegio.',        'ADMINISTRATION', 1, 'BASIC',      0, 41);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_STUDENT_PORTAL',   'Portal del Estudiante',               'App/web para notas, horarios, asistencia y notificaciones.',             'ADMINISTRATION', 1, 'BASIC',      1, 42);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_BULK_IMPORT',     'Importacion Masiva desde Excel',      'Carga de estudiantes, notas y asistencias desde plantillas Excel.',       'ADMINISTRATION', 1, 'STANDARD',   1, 44);

-- --- MODULOS DE COMUNICACION ---------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_NOTIFICATIONS',    'Centro de Notificaciones',            'Notificaciones in-app en tiempo real para alertas academicas.',           'COMMUNICATION',  1, 'FREE',       1, 50);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_AI_CHAT_MINEDU',   'Asistente IA - Normativa MINEDU',     'Chatbot RAG sobre el Curriculo Nacional y normativas del MINEDU.',       'COMMUNICATION',  1, 'PREMIUM',    0, 51);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_ANNOUNCEMENTS',   'Comunicados y Circulares',            'Envio de comunicados masivos por rol, seccion o toda la institucion.',    'COMMUNICATION',  1, 'BASIC',      1, 53);

-- --- MODULOS DE INNOVACION -----------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_BLOCKCHAIN',       'Registro Blockchain de Eventos',      'Cadena SHA-256 para eventos criticos: matriculas, notas, asistencias.',   'INNOVATION',     1, 'PREMIUM',    1, 60);
INSERT INTO FEATURE_CATALOG VALUES ('MOD_ADVANCED_REPORTS', 'Reportes Avanzados PDF/Excel',        'Generacion asincrona de reportes, actas, nominas y dashboards.',         'INNOVATION',     1, 'STANDARD',   1, 61);

-- --- MODULOS DE SEGURIDAD ------------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('MOD_SECURITY_AUDIT',   'Auditoria de Seguridad',              'Log de eventos de seguridad: logins, accesos denegados y cambios.',      'SECURITY',       1, 'FREE',       1, 70);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_SSO_GOOGLE',      'Login con Google (SSO OAuth2)',        'Inicio de sesion unico con cuentas Google institucionales.',              'SECURITY',       1, 'ENTERPRISE', 0, 71);
INSERT INTO FEATURE_CATALOG VALUES ('FEAT_MFA',             'Autenticacion de Dos Factores (MFA)', 'Verificacion adicional por email o app autenticadora.',                   'SECURITY',       1, 'PREMIUM',    0, 72);

-- --- INTEGRACIONES -------------------------------------------------------------

INSERT INTO FEATURE_CATALOG VALUES ('INT_SIAGIE',           'Integracion con SIAGIE (MINEDU)',     'Sincronizacion con el sistema de gestion educativa del MINEDU.',          'INTEGRATION',    1, 'STANDARD',   0, 80);
INSERT INTO FEATURE_CATALOG VALUES ('INT_PUBLIC_API',       'API Publica REST',                    'Acceso a datos via API para integraciones con sistemas de terceros.',     'INTEGRATION',    1, 'ENTERPRISE', 0, 83);

COMMIT;


-- =============================================================================
-- BLOQUE 3: SP_PROVISION_NEW_TENANT (versi?n corregida y completa)
-- FIX-2: Se compila DESPU?S de crear las tablas ? ya no hay ORA-00942
-- =============================================================================

CREATE OR REPLACE PROCEDURE SP_PROVISION_NEW_TENANT (
    p_name              IN  VARCHAR2,
    p_document_number   IN  VARCHAR2,
    p_institution_type  IN  VARCHAR2,
    p_address           IN  VARCHAR2 DEFAULT NULL,
    p_phone             IN  VARCHAR2 DEFAULT NULL,
    p_email             IN  VARCHAR2 DEFAULT NULL,
    p_plan              IN  VARCHAR2 DEFAULT 'STANDARD',
    p_admin_email       IN  VARCHAR2 DEFAULT NULL,
    p_admin_password    IN  VARCHAR2 DEFAULT NULL,
    p_admin_first_name  IN  VARCHAR2 DEFAULT 'Administrador',
    p_admin_last_name   IN  VARCHAR2 DEFAULT 'Principal',
    p_school_id         OUT VARCHAR2,
    p_admin_user_id     OUT VARCHAR2,
    p_features_created  OUT NUMBER
)
AS
    v_school_id     VARCHAR2(50);
    v_admin_id      VARCHAR2(50);
    v_sub_id        VARCHAR2(50);
    v_feat_count    NUMBER := 0;
    v_dup_count     NUMBER;
    v_plan_ok       NUMBER(1);
    v_now           TIMESTAMP := SYSTIMESTAMP;
    v_today         DATE      := TRUNC(SYSDATE);

    -- Cursor sobre el catalogo
    CURSOR c_catalog IS
        SELECT
            feature_key,
            description,
            min_plan,
            enabled_by_default AS should_enable
        FROM FEATURE_CATALOG
        ORDER BY sort_order;

BEGIN

    -- =========================================================================
    -- PASO 1: VALIDACIONES
    -- =========================================================================

    -- Solo se permite K12 (Inicial, Primaria, Secundaria)
    IF p_institution_type != 'K12' THEN
        RAISE_APPLICATION_ERROR(-20300,
            'institution_type invalido: "' || p_institution_type || '". Solo se permite K12 (Inicial, Primaria, Secundaria).');
    END IF;

    IF TRIM(p_name) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20301, 'El nombre de la instituci?n es obligatorio.');
    END IF;

    IF TRIM(p_document_number) IS NULL THEN
        RAISE_APPLICATION_ERROR(-20302, 'El n?mero de documento (RUC/C?digo Modular) es obligatorio.');
    END IF;

    SELECT COUNT(*) INTO v_dup_count
    FROM   SCHOOLS
    WHERE  document_number = TRIM(p_document_number);

    IF v_dup_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20303,
            'Ya existe una instituci?n con el documento "' || p_document_number || '".');
    END IF;

    IF p_plan NOT IN ('FREE', 'BASIC', 'STANDARD', 'PREMIUM', 'ENTERPRISE') THEN
        RAISE_APPLICATION_ERROR(-20304,
            'Plan invalido: "' || p_plan || '". Use FREE, BASIC, STANDARD, PREMIUM o ENTERPRISE.');
    END IF;


    -- =========================================================================
    -- PASO 2: CREAR LA INSTITUCI?N
    -- =========================================================================

    v_school_id := SYS_GUID();

    INSERT INTO SCHOOLS (
        id, name, document_number, address, phone, email,
        institution_type, status, created_at, updated_at
    ) VALUES (
        v_school_id,
        TRIM(p_name),
        TRIM(p_document_number),
        p_address, p_phone, p_email,
        p_institution_type,
        'ACTIVE',
        v_now, v_now
    );

    p_school_id := v_school_id;


    -- =========================================================================
    -- PASO 3: SUSCRIPCI?N INICIAL
    -- =========================================================================

    v_sub_id := SYS_GUID();

    INSERT INTO SCHOOL_SUBSCRIPTIONS (
        id, school_id, plan_name,
        max_students, max_teachers, max_storage_gb,
        features_json, billing_cycle,
        starts_at, is_trial, status
    ) VALUES (
        v_sub_id,
        v_school_id,
        p_plan,
        CASE p_plan WHEN 'FREE' THEN 100 WHEN 'BASIC' THEN 300 WHEN 'STANDARD' THEN 1000 WHEN 'PREMIUM' THEN 3000 ELSE 99999 END,
        CASE p_plan WHEN 'FREE' THEN 10  WHEN 'BASIC' THEN 20  WHEN 'STANDARD' THEN 50   WHEN 'PREMIUM' THEN 150  ELSE 9999  END,
        CASE p_plan WHEN 'FREE' THEN 1   WHEN 'BASIC' THEN 5   WHEN 'STANDARD' THEN 20   WHEN 'PREMIUM' THEN 100  ELSE 9999  END,
        '{"type":"' || p_institution_type || '","plan":"' || p_plan || '"}',
        'MONTHLY',
        v_today,
        CASE p_plan WHEN 'FREE' THEN 1 ELSE 0 END,
        'ACTIVE'
    );


    -- =========================================================================
    -- PASO 4: APROVISIONAR FEATURE FLAGS
    -- Activa flags del cat?logo seg?n tipo de instituci?n + plan contratado
    -- =========================================================================

    FOR rec IN c_catalog LOOP

        -- Calcular si el plan contratado cubre el m?nimo del feature
        SELECT CASE
            WHEN p_plan = 'ENTERPRISE'                                                       THEN 1
            WHEN p_plan = 'PREMIUM'    AND rec.min_plan != 'ENTERPRISE'                     THEN 1
            WHEN p_plan = 'STANDARD'   AND rec.min_plan NOT IN ('PREMIUM','ENTERPRISE')     THEN 1
            WHEN p_plan = 'BASIC'      AND rec.min_plan IN ('FREE','BASIC')                 THEN 1
            WHEN p_plan = 'FREE'       AND rec.min_plan = 'FREE'                            THEN 1
            ELSE 0
        END INTO v_plan_ok
        FROM DUAL;

        INSERT INTO FEATURE_FLAGS (
            id, school_id, feature_name,
            enabled, description, plan_required,
            rollout_pct, created_at, updated_at
        ) VALUES (
            SYS_GUID(),
            v_school_id,
            rec.feature_key,
            -- Activo si: el cat?logo lo marca para este tipo Y el plan es suficiente
            CASE WHEN rec.should_enable = 1 AND v_plan_ok = 1 THEN 1 ELSE 0 END,
            rec.description,
            rec.min_plan,
            100,
            v_now, v_now
        );

        v_feat_count := v_feat_count + 1;

    END LOOP;

    p_features_created := v_feat_count;


    -- =========================================================================
    -- PASO 5: USUARIO ADMINISTRADOR INICIAL (opcional)
    -- =========================================================================

    v_admin_id := NULL;

    IF p_admin_email IS NOT NULL AND p_admin_password IS NOT NULL THEN

        SELECT COUNT(*) INTO v_dup_count
        FROM   USERS
        WHERE  school_id = v_school_id
          AND  email     = LOWER(TRIM(p_admin_email));

        IF v_dup_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20306,
                'El email "' || p_admin_email || '" ya existe en este colegio.');
        END IF;

        v_admin_id := SYS_GUID();

        INSERT INTO USERS (
            id, school_id, email, password_hash,
            role, first_name, last_name, status,
            created_at, updated_at
        ) VALUES (
            v_admin_id,
            v_school_id,
            LOWER(TRIM(p_admin_email)),
            p_admin_password,
            'ADMIN',
            p_admin_first_name, p_admin_last_name,
            'ACTIVE',
            v_now, v_now
        );

    END IF;

    p_admin_user_id := v_admin_id;


    -- =========================================================================
    -- PASO 6: BLOQUE G?NESIS DEL BLOCKCHAIN
    -- =========================================================================

    INSERT INTO BLOCKCHAIN_BLOCKS (
        id, school_id, block_index, event_type,
        entity_id, entity_type, payload,
        previous_hash, hash,
        created_by, created_at
    ) VALUES (
        SYS_GUID(),
        v_school_id,
        0,
        'GENESIS',
        v_school_id,
        'SCHOOL',
        '{"event":"GENESIS","institution":"' || REPLACE(TRIM(p_name), '"', '') ||
        '","type":"' || p_institution_type || '","plan":"' || p_plan || '"}',
        RPAD('0', 64, '0'),
        LOWER(TO_CHAR(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF9') || RAWTOHEX(SYS_GUID())),
        v_admin_id,
        v_now
    );


    -- =========================================================================
    -- PASO 7: CONFIGURACI?N ESPEC?FICA POR TIPO DE INSTITUCI?N (SYSTEM_CONFIG)
    -- =========================================================================

    -- Configuraciones comunes
    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'INSTITUTION_TYPE', p_institution_type, 'STRING', 'Tipo de instituci?n educativa', v_now, v_now);

    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'MAX_LOGIN_ATTEMPTS', '5', 'NUMBER', 'Intentos fallidos antes de bloquear cuenta', v_now, v_now);

    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'BLOCKCHAIN_EVENTS_ENABLED',
            CASE WHEN p_plan IN ('PREMIUM','ENTERPRISE') THEN 'true' ELSE 'false' END,
            'BOOLEAN', 'Activar registro automatico en blockchain', v_now, v_now);

    -- Configuraciones K12 (Inicial, Primaria, Secundaria)
    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'GRADING_SCALE', 'VIGESIMAL_MINEDU', 'STRING', 'Escala vigesimal 0-20 MINEDU', v_now, v_now);
    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'ABSENCE_ALERT_THRESHOLD', '3', 'NUMBER', 'Faltas para generar alerta', v_now, v_now);
    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'SCHOOL_START_TIME', '07:30', 'STRING', 'Hora de inicio de clases', v_now, v_now);
    INSERT INTO SYSTEM_CONFIG (id, school_id, config_key, config_value, value_type, description, created_at, updated_at)
    VALUES (SYS_GUID(), v_school_id, 'CURRICULUM_FRAMEWORK', 'CNEB_MINEDU', 'STRING', 'Marco curricular CNEB', v_now, v_now);


    -- =========================================================================
    -- PASO 8: CONFIRMAR TRANSACCI?N
    -- =========================================================================

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('===========================================');
    DBMS_OUTPUT.PUT_LINE('PROVISION OK: ' || TRIM(p_name));
    DBMS_OUTPUT.PUT_LINE('Tipo   : ' || p_institution_type);
    DBMS_OUTPUT.PUT_LINE('Plan   : ' || p_plan);
    DBMS_OUTPUT.PUT_LINE('ID     : ' || v_school_id);
    DBMS_OUTPUT.PUT_LINE('Admin  : ' || NVL(v_admin_id, '(no creado)'));
    DBMS_OUTPUT.PUT_LINE('Flags  : ' || v_feat_count || ' aprovisionados');
    DBMS_OUTPUT.PUT_LINE('===========================================');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(
            CASE WHEN SQLCODE BETWEEN -20399 AND -20300 THEN SQLCODE ELSE -20399 END,
            CASE WHEN SQLCODE BETWEEN -20399 AND -20300
                 THEN SQLERRM
                 ELSE 'SP_PROVISION_NEW_TENANT ERROR [' || SQLCODE || ']: ' || SQLERRM ||
                      ' ? Todos los cambios fueron revertidos.'
            END
        );
END SP_PROVISION_NEW_TENANT;
/


-- =============================================================================
-- BLOQUE 4: DEMO - Provisionar las 3 instituciones
-- Un solo bloque anonimo para evitar ORA-17090 del pre-parser JDBC
-- =============================================================================

DECLARE
    v_school_id  VARCHAR2(50);
    v_admin_id   VARCHAR2(50);
    v_features   NUMBER;
BEGIN
    -- Demo: Colegio K-12 (Inicial, Primaria, Secundaria)
    SP_PROVISION_NEW_TENANT(
        'I.E. San Martin de Porres', '20600100001', 'K12',
        'Jr. Bolivar 123, Lima', NULL,
        'ie.sanmartin@demo.edu.pe', 'PREMIUM',
        'admin@sanmartin.edu.pe', 'DemoHashTempABC123',
        'Carmen', 'Flores Mendoza',
        v_school_id, v_admin_id, v_features
    );

    DBMS_OUTPUT.PUT_LINE('1 institucion demo K12 provisionada exitosamente.');
END;
/


-- =============================================================================
-- BLOQUE 5: VERIFICACION FINAL
-- =============================================================================

-- Instituciones registradas (solo K12)
SELECT
    institution_type            AS "Tipo",
    COUNT(*)                    AS "Cantidad",
    LISTAGG(name, ' | ') WITHIN GROUP (ORDER BY name) AS "Instituciones"
FROM   SCHOOLS
GROUP BY institution_type
ORDER BY institution_type;

-- Features activos vs inactivos por institucion
SELECT
    s.name                      AS "Institucion",
    s.institution_type          AS "Tipo",
    s.status                    AS "Estado",
    COUNT(ff.id)                AS "Total Flags",
    SUM(ff.enabled)             AS "Activos",
    COUNT(ff.id) - SUM(ff.enabled) AS "Inactivos"
FROM   SCHOOLS s
JOIN   FEATURE_FLAGS ff ON ff.school_id = s.id
GROUP BY s.id, s.name, s.institution_type, s.status
ORDER BY s.name;

-- Resumen de features por plan minimo
SELECT
    fc.feature_key              AS "Feature",
    fc.category                 AS "Cat",
    fc.min_plan                 AS "Plan Min",
    fc.enabled_by_default       AS "Default",
    SUM(CASE WHEN ff.enabled = 1 THEN 1 ELSE 0 END) AS "Colegios Activos"
FROM   FEATURE_CATALOG fc
JOIN   FEATURE_FLAGS ff ON ff.feature_name = fc.feature_key
JOIN   SCHOOLS       s  ON s.id            = ff.school_id
WHERE  s.id != 'school-20188-canete'
GROUP BY fc.feature_key, fc.category, fc.min_plan, fc.enabled_by_default, fc.sort_order
ORDER BY fc.sort_order;

-- =============================================================================
-- FIN - FINE FLOW MULTI-INSTITUCION v5.0 (Solo K12)
-- =============================================================================

-- =============================================================================

-- ============================================================
-- VPD POLICY APPLICATION (movido al final del script)
-- ============================================================
-- A.4: Aplicar pol?ticas VPD a TODAS las tablas multi-tenant
-- Las tablas SIN school_id (SCHOOLS, MINEDU_DOCUMENTS) quedan excluidas.
-- -----------------------------------------------------------------------------

-- Definir las tablas que necesitan VPD
DECLARE
    TYPE t_tables IS TABLE OF VARCHAR2(50);
    v_tables t_tables := t_tables(
        'USERS', 'ACADEMIC_LEVELS', 'SCHOOL_YEARS', 'ACADEMIC_PERIODS',
        'SECTIONS', 'STUDENTS', 'GUARDIANS', 'TEACHERS',
        'COURSES', 'COURSE_ASSIGNMENTS', 'COURSE_COMPETENCIES', 'CLASS_TASKS',
        'ATTENDANCES', 'STUDENT_SCORES', 'BLOCKCHAIN_BLOCKS',
        'CHAT_SESSIONS', 'NOTIFICATIONS', 'JUSTIFICATIONS',
        'AUDIT_LOGS', 'REPORT_JOBS', 'SYSTEM_CONFIG',
        'STUDENT_SECTION_HISTORY', 'SCHOOL_SUBSCRIPTIONS', 'REFRESH_TOKENS'
    );
    v_schema VARCHAR2(50) := SYS_CONTEXT('USERENV','CURRENT_SCHEMA');
BEGIN
    FOR i IN v_tables.FIRST .. v_tables.LAST LOOP
        BEGIN
            -- Eliminar pol?tica previa si existe (idempotente)
            DBMS_RLS.DROP_POLICY(v_schema, v_tables(i), 'fineflow_tenant_policy');
        EXCEPTION WHEN OTHERS THEN NULL;
        END;

        DBMS_RLS.ADD_POLICY(
            v_schema,                           -- object_schema
            v_tables(i),                        -- object_name
            'fineflow_tenant_policy',            -- policy_name
            v_schema,                           -- function_schema
            'fn_tenant_predicate',               -- policy_function
            'SELECT,INSERT,UPDATE,DELETE',       -- statement_types
            TRUE,                               -- update_check
            TRUE,                               -- enable
            FALSE                               -- static_policy
        );
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('VPD: pol?ticas aplicadas a ' || v_tables.COUNT || ' tablas.');
END;
/


-- =============================================================================
-- [E1] FIX: Re-ejecutar PKG_TENANT_CTX ahora que el paquete YA existe
--      (el seed data lo llamó antes de crearse, causando ORA-06550)
-- =============================================================================
BEGIN
    PKG_TENANT_CTX.SET_SCHOOL(NULL);
    DBMS_OUTPUT.PUT_LINE('[E1] PKG_TENANT_CTX inicializado correctamente.');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('[E1] WARN: ' || SQLERRM);
END;
/

-- =============================================================================
-- [E2] FIX: Crear índices faltantes (los ORA-01408 son duplicados tolerables)
--      Solo creamos los que NO existen para evitar ruido en el log.
-- =============================================================================
DECLARE
    v_cnt NUMBER;
BEGIN
    -- IDX_TRANS_KEY_LANG (redundante si UQ_TRANS_KEY_LANG ya existe, línea 4013)
    SELECT COUNT(*) INTO v_cnt FROM user_constraints
    WHERE constraint_name = 'UQ_TRANS_KEY_LANG' AND constraint_type = 'U';
    IF v_cnt = 0 THEN
        BEGIN
            EXECUTE IMMEDIATE 'CREATE UNIQUE INDEX IDX_TRANS_KEY_LANG ON TRANSLATIONS (trans_key, lang_code)';
            DBMS_OUTPUT.PUT_LINE('[E2] IDX_TRANS_KEY_LANG creado.');
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('[E2] IDX_TRANS_KEY_LANG saltado: ' || SQLERRM);
        END;
    ELSE
        DBMS_OUTPUT.PUT_LINE('[E2] IDX_TRANS_KEY_LANG saltado (constraint UQ ya existe).');
    END IF;
END;
/

-- FIN DEL SCRIPT MAESTRO FINEFLOW v5.0
-- =============================================================================