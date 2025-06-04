CREATE TABLE MEMBERS
(
    id_member NUMBER,
    name      VARCHAR2(100)        NOT NULL,
    surname   VARCHAR2(100)        NOT NULL,
    email     VARCHAR2(100) UNIQUE NOT NULL,
    password  VARCHAR2(100),
    phone     VARCHAR2(20),
    status    VARCHAR2(100),
    primary key (id_member)
    -- foreign key (id_departament) references  DEPARTAMENT(id_departament)
);

CREATE TABLE MEMBER_DETAILS
(
    member_id    NUMBER,
    phone        VARCHAR2(20),
    address      VARCHAR2(255),
    status       VARCHAR2(50),
    voting_right VARCHAR2(50),
    cnp          VARCHAR2(20),
    numar        VARCHAR2(50),
    serie        VARCHAR2(50),
    PRIMARY KEY (member_id),
    FOREIGN KEY (member_id) REFERENCES members (id_member) ON DELETE CASCADE
);

ALTER TABLE member_details
    ADD total_activity_hours FLOAT DEFAULT 0;


CREATE TABLE DEPARTMENTS
(
    id_department  NUMBER PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    id_coordinator INT,
    FOREIGN KEY (id_coordinator) REFERENCES MEMBERS (id_member) ON DELETE CASCADE
);
ALTER TABLE DEPARTMENTS ADD (url VARCHAR2(255));

CREATE TABLE DEPARTMENT_MEMBERS
(
    member_id_member     NUMBER,
    department_id_department NUMBER,
    FOREIGN KEY (member_id_member) REFERENCES MEMBERS (id_member) ON DELETE CASCADE,
    FOREIGN KEY (department_id_department) REFERENCES DEPARTMENTS (id_department) ON DELETE CASCADE
);



CREATE TABLE PROJECTS
(
    id_project     NUMBER PRIMARY KEY,
    name_project   VARCHAR(100) NOT NULL,
    id_coordinator INT          NOT NULL,
    FOREIGN KEY (id_coordinator) REFERENCES MEMBERS (id_member)
);

CREATE TABLE PROJECT_MEMBERS
(
    id_member  INT,
    id_project INT,
    PRIMARY KEY (id_member, id_project),
    FOREIGN KEY (id_member) REFERENCES MEMBERS (id_member) ON DELETE CASCADE,
    FOREIGN KEY (id_project) REFERENCES PROJECTS (id_project) ON DELETE CASCADE
);

CREATE TABLE TASKS
(
    id_task               NUMBER PRIMARY KEY,
    title                 VARCHAR(100) NOT NULL,
    description           VARCHAR2(1000),
    date_task             DATE         NOT NULL,
    number_activity_hours FLOAT,
    status                VARCHAR2(10),
    id_member             INT,
    id_project            INT,
    FOREIGN KEY (id_member) REFERENCES Members (id_member) ON DELETE CASCADE
    --FOREIGN KEY (id_project) REFERENCES Projects(id_project)
);

ALTER TABLE TASKS
    add id_department
        constraint id_departments REFERENCES DEPARTMENTS (id_department);

CREATE SEQUENCE membri_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE OR REPLACE TRIGGER members_bi
    BEFORE INSERT
    ON MEMBERS
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

drop sequence task_seq;

CREATE OR REPLACE TRIGGER tasks_bi
    BEFORE INSERT
    ON TASKS
    FOR EACH ROW
BEGIN
    IF :new.id_task IS NULL THEN
        SELECT task_seq.NEXTVAL INTO :new.id_task FROM dual;
    END IF;
END;

DELETE
FROM MEMBERS
WHERE id_member = 1;

ALTER TABLE TASKS
    ADD member_task_number INT;
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
    BEFORE INSERT
    ON TASKS
    FOR EACH ROW
DECLARE
    v_next_number INT;
BEGIN
    :NEW.member_task_number := get_next_member_task_number(:NEW.id_member);
END;

--------------------------------------------------------------------------------------

DELETE
FROM TASKS
WHERE member_task_number = 0;

COMMIT;

--------------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE task_delete IS
    g_deleted_number NUMBER;
END;

CREATE OR REPLACE TRIGGER trg_before_delete_task
    BEFORE DELETE
    ON tasks
    FOR EACH ROW
BEGIN
    task_delete.g_deleted_number := :OLD.member_task_number;
END;

CREATE OR REPLACE TRIGGER trg_after_delete_task
    AFTER DELETE
    ON tasks
DECLARE
BEGIN
    UPDATE tasks
    SET member_task_number = member_task_number - 1
    WHERE member_task_number > task_delete.g_deleted_number;
END;

--------------------------------------------------------------------------------------

ALTER TABLE MEMBERS
    DROP COLUMN PHONE;
ALTER TABLE MEMBERS
    DROP COLUMN STATUS;

commit;

CREATE OR REPLACE TRIGGER trg_capitalize_name
    BEFORE INSERT
    ON MEMBERS
    FOR EACH ROW
BEGIN
    :NEW.NAME := INITCAP(LOWER(:NEW.NAME));
    :NEW.SURNAME := INITCAP(LOWER(:NEW.SURNAME));
END;

-----------------

--trigger care imi insereaza in member_details membrul nou aparut in members
CREATE OR REPLACE TRIGGER trg_insert_member_details
    AFTER INSERT
    ON MEMBERS
    FOR EACH ROW
BEGIN
    INSERT INTO member_details (member_id, status)
    VALUES (:NEW.id_member, 'member');
END;

------------------------------------
--atunci cand inseram un task in baza de date sa mi se modifice automat

CREATE OR REPLACE TRIGGER trg_task_insert_finalizat
    AFTER INSERT
    ON TASKS
    FOR EACH ROW
    WHEN (NEW.status = 'Finalizat')
BEGIN
    UPDATE member_details
    SET total_activity_hours = NVL(total_activity_hours, 0) + :NEW.number_activity_hours
    WHERE member_id = :NEW.id_member;
END;

CREATE OR REPLACE TRIGGER trg_task_update_status
    AFTER UPDATE OF status
    ON TASKS
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
        WHERE md.total_activity_hours IS NULL
           OR md.total_activity_hours = 0
        )
        LOOP
            UPDATE member_details
            SET total_activity_hours = (SELECT NVL(SUM(t.number_activity_hours), 0)
                                        FROM tasks t
                                        WHERE t.id_member = member_rec.member_id
                                          AND t.status = 'Finalizat')
            WHERE member_id = member_rec.member_id;
        END LOOP;
END;

create or replace procedure populate_member_details is
    cursor member_cursor is
        select id_member
        from members;
    v_id_member members.id_member%type;
    v_exists    number;
begin
    for member_rec in member_cursor
        loop
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

delete
from MEMBER_DETAILS
where phone is null;
commit;

CREATE OR REPLACE PROCEDURE format_member_names IS
BEGIN
    FOR rec IN (SELECT id_member, name, surname FROM members)
        LOOP
            UPDATE members
            SET name    = INITCAP(LOWER(rec.name)),
                surname = INITCAP(LOWER(rec.surname))
            WHERE id_member = rec.id_member;
        END LOOP;

    COMMIT;
END;

begin
    format_member_names;
end;

ALTER TABLE member_details
    ADD (
        cnp VARCHAR2(20),
        numar VARCHAR2(50),
        serie VARCHAR2(50)
        );

update MEMBER_DETAILS
set status='MEMBER'
where status is null;
commit;

--functie ca sa imi adauge departamentele si coordonatorii
CREATE SEQUENCE departments_seq START WITH 1 INCREMENT BY 1;

create or replace procedure add_departments(
    p_name_department IN varchar2,
    p_name_coordinator IN VARCHAR2,
    p_surname_coordinator IN varchar2
) is
    v_id_member NUMBER;
begin
      select id_member into v_id_member
    from members
    where name = p_name_coordinator
      and surname = p_surname_coordinator;

    insert into departments (id_department, name, id_coordinator)
    values (departments_seq.nextval, p_name_department, v_id_member);

    update MEMBER_DETAILS
        set status= 'COORDONATOR'
    where member_id = v_id_member;

    commit;
    exception
        when no_data_found then
            DBMS_OUTPUT.PUT_LINE( 'Member not found');
        when others then
            DBMS_OUTPUT.PUT_LINE( 'An error occurred: ' || sqlerrm);
end;

begin
    add_departments('IT', 'Alexandru', 'Nechifor');
    add_departments('PR&Media', 'Delia', 'Blendea');
    add_departments('Proiecte', 'Diana', 'Martisca');
    add_departments('Relatii Externe', 'Armand', 'Miron');
    add_departments('Relatii Interne', 'Bianca', 'Plamada');
end;

begin
    add_departments('Evaluari','Diana','Benchea');
end;

alter table DEPARTMENT_MEMBERS drop column DEPARTMENT_ID_DEPARTMENT;
alter table DEPARTMENT_MEMBERS drop column MEMBER_ID_MEMBER;

UPDATE MEMBER_DETAILS
SET STATUS = UPPER(STATUS) where status is not null;
commit;


CREATE OR REPLACE FUNCTION generate_department_url(p_name IN VARCHAR2)
    RETURN VARCHAR2 IS
    v_clean_name VARCHAR2(100);
BEGIN
    v_clean_name := LOWER(REPLACE(TRIM(p_name), ' ', '_'));
    RETURN '/photos/' || v_clean_name || '.png';
END;

UPDATE DEPARTMENTS
SET url = generate_department_url('re')
WHERE id_department=3;

commit;

--C:\Users\Bianca Milea\Desktop\uni\II\sem2\JavaProjectTaskManager\projectJava2.0\ProjectJava\json\membersToExport.json

CREATE OR REPLACE DIRECTORY EXPORT_DIR AS 'C:\Users\Bianca Milea\Desktop\uni\II\sem2\JavaProjectTaskManager\projectJava2.0\ProjectJava\json';
GRANT READ, WRITE ON DIRECTORY EXPORT_DIR TO TASKMANAGER;
GRANT CREATE ANY DIRECTORY TO TASKMANAGER;

--script pentru a exporta datele din tabela MEMBERS in fisierul export.sql
DECLARE
    v_sql VARCHAR2(4000);
    f  utl_file.file_type;
BEGIN
    f := UTL_FILE.FOPEN('EXPORT_DIR', 'export.sql', 'W');
    FOR rec IN (
        SELECT id_member, name, surname, email, password
        FROM MEMBERS
        ) LOOP
            v_sql := 'INSERT INTO MEMBERS (id_member, name, surname, email, password, phone, status) VALUES ('
                || rec.id_member || ', '
                || '''' || REPLACE(rec.name, '''', '''''') || ''', '
                || '''' || REPLACE(rec.surname, '''', '''''') || ''', '
                || '''' || REPLACE(rec.email, '''', '''''') || ''', '
                || CASE WHEN rec.password IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.password, '''', '''''') || '''' END
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));

--script pentru a exporta datele din tabela MEMBER_DETAILS in fisierul export.sql

    FOR rec IN (
        SELECT member_id, phone, address, status, voting_right, cnp, numar, serie, total_activity_hours
        FROM MEMBER_DETAILS
        ) LOOP
            v_sql := 'INSERT INTO MEMBER_DETAILS (member_id, phone, address, status, voting_right, cnp, numar, serie, total_activity_hours) VALUES ('
                || rec.member_id || ', '
                || CASE WHEN rec.phone IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.phone, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.address IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.address, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.status IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.status, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.voting_right IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.voting_right, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.cnp IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.cnp, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.numar IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.numar, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.serie IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.serie, '''', '''''') || '''' END || ', '
                || NVL(TO_CHAR(rec.total_activity_hours), '0')
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));

--script pentru a exporta datele din tabela DEPARTMENTS in fisierul export.sql

    FOR rec IN (
        SELECT id_department, name, id_coordinator, url
        FROM DEPARTMENTS
        ) LOOP
            v_sql := 'INSERT INTO DEPARTMENTS (id_department, name, id_coordinator, url) VALUES ('
                || rec.id_department || ', '
                || '''' || REPLACE(rec.name, '''', '''''') || ''', '
                || CASE WHEN rec.id_coordinator IS NULL THEN 'NULL' ELSE rec.id_coordinator END || ', '
                || CASE WHEN rec.url IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.url, '''', '''''') || '''' END
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));

--script pentru a exporta datele din tabela DEPARTMENT_MEMBERS in fisierul export.sql

    FOR rec IN (
        SELECT member_id_member, department_id_department
        FROM DEPARTMENT_MEMBERS
        ) LOOP
            v_sql := 'INSERT INTO DEPARTMENT_MEMBERS (member_id_member, department_id_department) VALUES ('
                || rec.member_id_member || ', '
                || rec.department_id_department
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));


--script pentru a exporta datele din tabela PROJECTS in fisierul export.sql

    FOR rec IN (
        SELECT id_project, name_project, id_coordinator
        FROM PROJECTS
        ) LOOP
            v_sql := 'INSERT INTO PROJECTS (id_project, name_project, id_coordinator) VALUES ('
                || rec.id_project || ', '
                || '''' || REPLACE(rec.name_project, '''', '''''') || ''', '
                || rec.id_coordinator
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));

--script pentru a exporta datele din tabela PROJECT_MEMBERS in fisierul export.sql

    FOR rec IN (
        SELECT id_member, id_project
        FROM PROJECT_MEMBERS
        ) LOOP
            v_sql := 'INSERT INTO PROJECT_MEMBERS (id_member, id_project) VALUES ('
                || rec.id_member || ', '
                || rec.id_project
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));

--script pentru a exporta datele din tabela TASKS in fisierul export.sql

    FOR rec IN (
        SELECT id_task, title, description, date_task, number_activity_hours, status, id_member, id_project, id_department
        FROM TASKS
        ) LOOP
            v_sql := 'INSERT INTO TASKS (id_task, title, description, date_task, number_activity_hours, status, id_member, id_project, id_department) VALUES ('
                || rec.id_task || ', '
                || '''' || REPLACE(rec.title, '''', '''''') || ''', '
                || CASE WHEN rec.description IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.description, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.date_task IS NULL THEN 'NULL' ELSE 'TO_DATE(''' || TO_CHAR(rec.date_task, 'YYYY-MM-DD') || ''', ''YYYY-MM-DD'')' END || ', '
                || NVL(TO_CHAR(rec.number_activity_hours), 'NULL') || ', '
                || CASE WHEN rec.status IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.status, '''', '''''') || '''' END || ', '
                || NVL(TO_CHAR(rec.id_member), 'NULL') || ', '
                || NVL(TO_CHAR(rec.id_project), 'NULL') || ', '
                || NVL(TO_CHAR(rec.id_department), 'NULL')
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));
    UTL_FILE.FCLOSE(f);
END;
