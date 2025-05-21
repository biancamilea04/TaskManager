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
                                member_id       NUMBER,
                                phone         VARCHAR2(20),
                                address       VARCHAR2(255),
                                status        VARCHAR2(50),
                                voting_right  VARCHAR2(50),
                                PRIMARY KEY(member_id),
                                FOREIGN KEY (member_id) REFERENCES members(id_member)
);

ALTER TABLE member_details
    ADD total_activity_hours FLOAT DEFAULT 0;


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
    AFTER INSERT ON MEMBERS
    FOR EACH ROW
BEGIN
    INSERT INTO member_details (member_id)
    VALUES (:NEW.id_member);
END;

------------------------------------
--atunci cand inseram un task in baza de date sa mi se modifice automat

CREATE OR REPLACE TRIGGER trg_task_insert_finalizat
    AFTER INSERT ON TASKS
    FOR EACH ROW
    WHEN (NEW.status = 'Finalizat')
BEGIN
    UPDATE member_details
    SET total_activity_hours = NVL(total_activity_hours, 0) + :NEW.number_activity_hours
    WHERE member_id = :NEW.id_member;
END;

CREATE OR REPLACE TRIGGER trg_task_update_status
    AFTER UPDATE OF status ON TASKS
    FOR EACH ROW
BEGIN
    IF :NEW.status = 'Finalizat' AND :OLD.status != 'Finalizat' THEN
        UPDATE member_details
        SET total_activity_hours = NVL(total_activity_hours, 0) + :NEW.number_activity_hours
        WHERE member_id = :NEW.id_member;

    ELSIF :OLD.status = 'Finalizat' AND :NEW.status != 'Finalizat' THEN
        UPDATE member_details
        SET total_activity_hours = NVL(total_activity_hours, 0) - :OLD.number_activity_hours
        WHERE member_id = :OLD.id_member;
    END IF;
END;

SELECT trigger_name, status
FROM user_triggers
WHERE table_name = 'TASKS';

CREATE OR REPLACE PROCEDURE update_activity_hours IS
BEGIN
    FOR member_rec IN (
        SELECT md.member_id
        FROM member_details md
        WHERE md.total_activity_hours IS NULL OR md.total_activity_hours = 0
        ) LOOP
            UPDATE member_details
            SET total_activity_hours = (
                SELECT NVL(SUM(t.number_activity_hours), 0)
                FROM tasks t
                WHERE t.id_member = member_rec.member_id AND t.status = 'Finalizat'
            )
            WHERE member_id = member_rec.member_id;
        END LOOP;
END;

create or replace procedure populate_member_details is
 cursor member_cursor is
   select id_member from members;
   v_id_member members.id_member%type;
   v_exists number;
  begin
      for member_rec in member_cursor loop
        v_id_member := member_rec.id_member;
        select count(*) into v_exists from member_details where member_id = v_id_member;
        if v_exists = 0 then
            insert into member_details (member_id, phone, status, voting_right, address)
            values (v_id_member, null, null, 'nu', null);
             update_activity_hours;
        end if;
      end loop;
      commit;
end;

begin
    populate_member_details;
end;

delete from MEMBER_DETAILS where phone is null;
commit;

CREATE OR REPLACE PROCEDURE format_member_names IS
BEGIN
    FOR rec IN (SELECT id_member, name, surname FROM members) LOOP
            UPDATE members
            SET
                name = INITCAP(LOWER(rec.name)),
                surname = INITCAP(LOWER(rec.surname))
            WHERE id_member = rec.id_member;
        END LOOP;

    COMMIT;
END;

begin
    format_member_names;
end;








