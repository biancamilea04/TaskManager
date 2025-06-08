CREATE TABLE MEMBERS
(
    id_member NUMBER,
    name      VARCHAR2(100)        NOT NULL,
    surname   VARCHAR2(100)        NOT NULL,
    email     VARCHAR2(100) UNIQUE NOT NULL,
    password  VARCHAR2(100),
    primary key (id_member)
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

alter table MEMBER_DETAILS modify (voting_right default 'NU');

CREATE TABLE DEPARTMENTS
(
    id_department  NUMBER PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    id_coordinator INT,
    FOREIGN KEY (id_coordinator) REFERENCES MEMBERS (id_member) ON DELETE CASCADE
);

CREATE TABLE DEPARTMENT_MEMBERS
(
    member_id_member     NUMBER,
    department_id_department NUMBER,
    FOREIGN KEY (member_id_member) REFERENCES MEMBERS (id_member) ON DELETE CASCADE,
    FOREIGN KEY (department_id_department) REFERENCES DEPARTMENTS (id_department) ON DELETE CASCADE
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
    FOREIGN KEY (id_member) REFERENCES Members (id_member) ON DELETE CASCADE
);

ALTER TABLE TASKS
    ADD member_task_number INT;

ALTER TABLE TASKS
    add id_department
        constraint id_departments REFERENCES DEPARTMENTS (id_department);

CREATE SEQUENCE membri_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE SEQUENCE task_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE SEQUENCE departments_seq
    START WITH 1
    INCREMENT BY 1;

--creste id ul cand dai insert in tabela members
CREATE OR REPLACE TRIGGER members_bi
    BEFORE INSERT
    ON MEMBERS
    FOR EACH ROW
BEGIN
    IF :new.id_member IS NULL THEN
        SELECT membri_seq.NEXTVAL INTO :new.id_member FROM dual;
    END IF;
END;

CREATE OR REPLACE TRIGGER tasks_bi
    BEFORE INSERT
    ON TASKS
    FOR EACH ROW
BEGIN
    IF :new.id_task IS NULL THEN
        SELECT task_seq.NEXTVAL INTO :new.id_task FROM dual;
    END IF;
END;


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

--refaca id urile taskurilor membrilor dupa stergere
CREATE OR REPLACE TRIGGER trg_after_delete_task
    AFTER DELETE
    ON tasks
DECLARE
BEGIN
    UPDATE tasks
    SET member_task_number = member_task_number - 1
    WHERE member_task_number > task_delete.g_deleted_number;
END;

CREATE OR REPLACE TRIGGER trg_capitalize_name
    BEFORE INSERT
    ON MEMBERS
    FOR EACH ROW
BEGIN
    :NEW.NAME := INITCAP(LOWER(:NEW.NAME));
    :NEW.SURNAME := INITCAP(LOWER(:NEW.SURNAME));
END;

--trigger care imi insereaza in member_details membrul nou aparut in members
CREATE OR REPLACE TRIGGER trg_insert_member_details
    AFTER INSERT
    ON MEMBERS
    FOR EACH ROW
BEGIN
    INSERT INTO member_details (member_id, status)
    VALUES (:NEW.id_member, 'MEMBER');
END;

--imi setez statusul taskului
CREATE OR REPLACE TRIGGER trg_task_insert_finalizat
    AFTER INSERT
    ON TASKS
    FOR EACH ROW
    WHEN (NEW.status = 'Finalizat')
BEGIN
    UPDATE member_details
    SET total_activity_hours = NVL(total_activity_hours, 0) + :NEW.number_activity_hours
    WHERE member_id = :NEW.id_member;

    UPDATE MEMBER_DETAILS
    SET voting_right = 'DA'
    WHERE total_activity_hours >= 30;

    UPDATE MEMBER_DETAILS
    SET voting_right = 'NU'
    WHERE total_activity_hours < 30;
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

CREATE OR REPLACE TRIGGER task_dep_id_complete
    BEFORE INSERT ON TASKS
    FOR EACH ROW
DECLARE
    v_department_id DEPARTMENTS.id_department%TYPE;
BEGIN
    BEGIN
        SELECT department_id_department INTO v_department_id
        FROM DEPARTMENT_MEMBERS
        WHERE member_id_member = :NEW.id_member;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_department_id := NULL;
    END;

    :NEW.id_department := v_department_id;
END;

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

--functie de populat tabelele pt ca adaugasem mai tarziu coloanele
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
                values (v_id_member, null, null, 'NU', null);
                update_activity_hours;
            end if;
        end loop;
    commit;
end;

begin
    populate_member_details;
end;

--procedura care formateaza numele si prenumele membrilor
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

------- export populat tabele  -------
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
            v_sql := 'INSERT INTO MEMBERS (id_member, name, surname, email, password) VALUES ('
                || rec.id_member || ', '
                || '''' || REPLACE(rec.name, '''', '''''') || ''', '
                || '''' || REPLACE(rec.surname, '''', '''''') || ''', '
                || '''' || REPLACE(rec.email, '''', '''''') || ''', '
                || CASE WHEN rec.password IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.password, '''', '''''') || '''' END
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));

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

--script pentru a exporta datele din tabela TASKS in fisierul export.sql

    FOR rec IN (
        SELECT id_task, title, description, date_task, number_activity_hours, status, id_member, id_department
        FROM TASKS
        ) LOOP
            v_sql := 'INSERT INTO TASKS (id_task, title, description, date_task, number_activity_hours, status, id_member, id_department) VALUES ('
                || rec.id_task || ', '
                || '''' || REPLACE(rec.title, '''', '''''') || ''', '
                || CASE WHEN rec.description IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.description, '''', '''''') || '''' END || ', '
                || CASE WHEN rec.date_task IS NULL THEN 'NULL' ELSE 'TO_DATE(''' || TO_CHAR(rec.date_task, 'YYYY-MM-DD') || ''', ''YYYY-MM-DD'')' END || ', '
                || NVL(TO_CHAR(rec.number_activity_hours), 'NULL') || ', '
                || CASE WHEN rec.status IS NULL THEN 'NULL' ELSE '''' || REPLACE(rec.status, '''', '''''') || '''' END || ', '
                || NVL(TO_CHAR(rec.id_member), 'NULL') || ', '
                || NVL(TO_CHAR(rec.id_department), 'NULL')
                || ');';
            UTL_FILE.PUT_LINE(f, v_sql);
        END LOOP;
    UTL_FILE.PUT_LINE(f, chr(10));
    UTL_FILE.FCLOSE(f);
END;


DECLARE
    f           UTL_FILE.FILE_TYPE;
    v_line      VARCHAR2(4000);
BEGIN
    f := UTL_FILE.FOPEN('EXPORT_DIR', 'export.sql', 'R');

    LOOP
        BEGIN
            UTL_FILE.GET_LINE(f, v_line);
            v_line := RTRIM(v_line, ';');
            DBMS_OUTPUT.PUT_LINE(v_line);
            EXECUTE IMMEDIATE v_line;

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                EXIT;
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Error on line: ' || v_line);
                DBMS_OUTPUT.PUT_LINE(SQLERRM);
        END;
    END LOOP;

    UTL_FILE.FCLOSE(f);
EXCEPTION
    WHEN OTHERS THEN
        IF UTL_FILE.IS_OPEN(f) THEN
            UTL_FILE.FCLOSE(f);
        END IF;
        DBMS_OUTPUT.PUT_LINE('Fatal error: ' || SQLERRM);
END;

CREATE OR REPLACE PROCEDURE populate_task_department_ids IS
BEGIN
    FOR rec IN (
        SELECT t.id_task, t.id_member
        FROM TASKS t
        WHERE t.id_department IS NULL
        ) LOOP
            DECLARE
                v_department_id DEPARTMENTS.id_department%TYPE;
            BEGIN
                SELECT department_id_department INTO v_department_id
                FROM DEPARTMENT_MEMBERS
                WHERE member_id_member = rec.id_member;

                UPDATE TASKS
                SET id_department = v_department_id
                WHERE id_task = rec.id_task;

            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    dbms_output.put_line('No department found for member ID: ' || rec.id_member || ' for task ID: ' || rec.id_task);
                    NULL;
            END;
        END LOOP;
    COMMIT;
END;

begin
    populate_task_department_ids;
end;

CREATE OR REPLACE PROCEDURE update_voting_rights IS
BEGIN
    UPDATE MEMBER_DETAILS
    SET voting_right = 'DA'
    WHERE total_activity_hours >= 30;

    UPDATE MEMBER_DETAILS
    SET voting_right = 'NU'
    WHERE total_activity_hours < 30;
    COMMIT;
END;


 ----statistici pentru departamente
CREATE OR REPLACE FUNCTION get_task_count_by_department(p_department_id IN NUMBER)
    RETURN NUMBER IS
    v_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM TASKS
    WHERE id_department = p_department_id;

    RETURN v_count;
END;

CREATE OR REPLACE FUNCTION get_done_tasks_by_department(
    p_department_id IN NUMBER
) RETURN NUMBER IS
    v_finalized_tasks NUMBER := 0;
BEGIN
    SELECT COUNT(*) INTO v_finalized_tasks
    FROM TASKS
    WHERE id_department = p_department_id
      AND LOWER(status) = 'Finalizat';

    RETURN v_finalized_tasks;
END;

CREATE OR REPLACE FUNCTION get_dpt_completion_percent(p_department_id IN NUMBER)
    RETURN NUMBER IS
    v_total NUMBER := 0;
    v_finalizate NUMBER := 0;
    v_percent NUMBER := 0;
BEGIN
    v_total := get_task_count_by_department(p_department_id);

    IF v_total = 0 THEN
        RETURN 0;
    END IF;

    v_finalizate :=get_done_tasks_by_department(p_department_id);

    v_percent := (v_finalizate / v_total) * 100;
    RETURN ROUND(v_percent, 2);
END;

CREATE OR REPLACE FUNCTION get_member_count_by_dpt(
    p_department_id IN NUMBER
) RETURN NUMBER IS
    v_member_count NUMBER := 0;
BEGIN
    SELECT COUNT(*) INTO v_member_count
    FROM DEPARTMENT_MEMBERS
    WHERE department_id_department = p_department_id;

    RETURN v_member_count;
END;

CREATE OR REPLACE FUNCTION get_department_performance(
    p_department_id IN NUMBER
) RETURN NUMBER IS
    v_nr_membri     NUMBER := 0;
    v_nr_taskuri    NUMBER := 0;
    v_performance   NUMBER := 0;
BEGIN
    v_nr_membri := get_member_count_by_dpt(p_department_id);

    IF v_nr_membri = 0 THEN
        RETURN 0;
    END IF;

    v_nr_taskuri:=get_done_tasks_by_department(p_department_id);
    v_performance := v_nr_taskuri / v_nr_membri;

    RETURN v_performance;
END;

DECLARE
    v_department_id         NUMBER := 6;
    v_task_count            NUMBER;
    v_done_task_count       NUMBER;
    v_completion_percent    NUMBER;
    v_member_count          NUMBER;
    v_performance_score     NUMBER;
BEGIN
    v_task_count := get_task_count_by_department(v_department_id);
    v_done_task_count := get_done_tasks_by_department(v_department_id);
    v_completion_percent := get_dpt_completion_percent(v_department_id);
    v_member_count := get_member_count_by_dpt(v_department_id);
    v_performance_score := get_department_performance(v_department_id);

    DBMS_OUTPUT.PUT_LINE('Departament ID: ' || v_department_id);
    DBMS_OUTPUT.PUT_LINE('Total taskuri: ' || v_task_count);
    DBMS_OUTPUT.PUT_LINE('Taskuri finalizate: ' || v_done_task_count);
    DBMS_OUTPUT.PUT_LINE('Procent completare: ' || v_completion_percent || '%');
    DBMS_OUTPUT.PUT_LINE('Număr membri: ' || v_member_count);
    DBMS_OUTPUT.PUT_LINE('Performanță departament: ' || v_performance_score);
END;

CREATE OR REPLACE FUNCTION get_members_count
    RETURN NUMBER
    IS
    cnt NUMBER;
BEGIN
    SELECT COUNT(*) INTO cnt FROM MEMBERS;
    RETURN cnt;
END;

begin
    DBMS_OUTPUT.PUT_LINE('Număr total de membri: ' || get_members_count);
end;

UPDATE member_details
SET status = 'MEMBER'
WHERE status = 'membru aspirant';

commit;



