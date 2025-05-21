CREATE TABLE MEMBERS (
                        id_member NUMBER,
                        name VARCHAR2(100) NOT NULL,
                        surname VARCHAR2(100) NOT NULL,
                        email VARCHAR2(100) UNIQUE NOT NULL,
                        password VARCHAR2(100),
                        phone VARCHAR2(20),
                        status VARCHAR2(100),
                        primary key (id_member)
                       -- foreign key (id_departament) references  DEPARTAMENT(id_departament)
);

CREATE TABLE MEMBER_DETAILS (
                                user_id       NUMBER,
                                phone         VARCHAR2(20),
                                address       VARCHAR2(255),
                                status        VARCHAR2(50),
                                voting_right  VARCHAR2(50),
                                PRIMARY KEY(user_id),
                                FOREIGN KEY (user_id) REFERENCES members(id_member)
);

CREATE TABLE DEPARTMENTS (
                              id_department NUMBER PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              id_coordinator INT,
                              FOREIGN KEY (id_coordinator) REFERENCES MEMBERS(id_member)
);

CREATE TABLE DEPARTMENT_MEMBERS (
                                    id_member NUMBER,
                                    id_department NUMBER,
                                    FOREIGN KEY (id_member) REFERENCES MEMBERS(id_member),
                                    FOREIGN KEY (id_department) REFERENCES DEPARTMENTS(id_department)
);



CREATE TABLE PROJECTS (
                          id_project NUMBER PRIMARY KEY,
                          name_project VARCHAR(100) NOT NULL,
                          id_coordinator INT NOT NULL,
                          FOREIGN KEY (id_coordinator) REFERENCES MEMBERS(id_member)
);

CREATE TABLE PROJECT_MEMBERS (
                                 id_member INT,
                                 id_project INT,
                                 PRIMARY KEY (id_member, id_project),
                                 FOREIGN KEY (id_member) REFERENCES MEMBERS(id_member),
                                 FOREIGN KEY (id_project) REFERENCES PROJECTS(id_project)
);

CREATE TABLE TASKS (
                         id_task NUMBER PRIMARY KEY,
                         title VARCHAR(100) NOT NULL,
                         description VARCHAR2(1000),
                         date_task DATE NOT NULL,
                         number_activity_hours FLOAT,
                         status VARCHAR2(10),
                         id_member INT,
                         id_project INT,
                         FOREIGN KEY (id_member) REFERENCES Members(id_member)
                         --FOREIGN KEY (id_project) REFERENCES Projects(id_project)
);

ALTER TABLE TASKS
    add id_department
        constraint id_departments REFERENCES DEPARTMENTS(id_department);

CREATE SEQUENCE membri_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE OR REPLACE TRIGGER members_bi
    BEFORE INSERT ON MEMBERS
    FOR EACH ROW
BEGIN
    IF :new.id_member IS NULL THEN
        SELECT membri_seq.NEXTVAL INTO :new.id_member FROM dual;
    END IF;
END;

CREATE SEQUENCE task_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE OR REPLACE TRIGGER tasks_bi
    BEFORE INSERT ON TASKS
    FOR EACH ROW
BEGIN
    IF :new.id_task IS NULL THEN
        SELECT task_seq.NEXTVAL INTO :new.id_task FROM dual;
    END IF;
END;

DELETE FROM MEMBERS WHERE id_member = 1;

ALTER TABLE TASKS ADD member_task_number INT;
----------------------------------------------------

--trigger pentru a-mi mari automat task numberul unui membru

CREATE OR REPLACE FUNCTION get_next_member_task_number(p_id_member INT)
    RETURN INT
    IS
    v_next_number INT;
BEGIN
    SELECT NVL(MAX(member_task_number), 0) + 1
    INTO v_next_number
    FROM TASKS
    WHERE id_member = p_id_member;

    RETURN v_next_number;
END;

CREATE OR REPLACE TRIGGER trg_set_user_task_number
    BEFORE INSERT ON TASKS
    FOR EACH ROW
DECLARE
    v_next_number INT;
BEGIN
    :NEW.member_task_number := get_next_member_task_number(:NEW.id_member);
END;

--------------------------------------------------------------------------------------

DELETE FROM TASKS
WHERE member_task_number = 0;

COMMIT;

--------------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE task_delete IS
    g_deleted_number NUMBER;
END;

CREATE OR REPLACE TRIGGER trg_before_delete_task
    BEFORE DELETE ON tasks
    FOR EACH ROW
BEGIN
    task_delete.g_deleted_number := :OLD.member_task_number;
END;

CREATE OR REPLACE TRIGGER trg_after_delete_task
    AFTER DELETE ON tasks
DECLARE
BEGIN
    UPDATE tasks
    SET member_task_number = member_task_number - 1
    WHERE member_task_number > task_delete.g_deleted_number;
END;

--------------------------------------------------------------------------------------

ALTER TABLE MEMBERS DROP COLUMN PHONE;
ALTER TABLE MEMBERS DROP COLUMN STATUS;

commit;

CREATE OR REPLACE TRIGGER trg_capitalize_name
    BEFORE INSERT ON MEMBERS
    FOR EACH ROW
BEGIN
    :NEW.NAME := INITCAP(LOWER(:NEW.NAME));
    :NEW.SURNAME := INITCAP(LOWER(:NEW.SURNAME));
END;

-----------------

--trigger care imi insereaza in member_details membrul nou aparut in members
CREATE OR REPLACE TRIGGER trg_insert_member_details
    AFTER INSERT ON members
    FOR EACH ROW
BEGIN
    INSERT INTO member_details (user_id)
    VALUES (:NEW.id_member);
END;



