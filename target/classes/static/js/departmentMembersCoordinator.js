let members = [];
const pathParts = window.location.pathname.split('/');
const department = decodeURIComponent(pathParts[pathParts.length - 1]);

document.addEventListener("DOMContentLoaded", async function () {

    setTitle(department);
    fetchDepartmentMembers(department);
    addMemberToDepartment();
    deleteMemberFromDepartment();

});

async function setTitle(department) {
    const departmentTitle = document.getElementById("department");
    const departmentName = await fetch(`/api/departments/name/${department}`).then(res => res.text());
    departmentTitle.textContent = `Departament: ${departmentName}`;
}

async function fetchDepartmentMembers(departmentName) {
    const response = await fetch(`/api/department/members/${departmentName}`);

    members = await response.json();
    console.log(members);
    if (members.length === 0) {
        displayNoMembersMessage("Nu sunt membri");
    } else {
        displayMembers(members);
    }
}

function displayMembers(data) {
    const tbody = document.getElementById('table-body');
    const message = document.getElementById('no-members-message');
    tbody.innerHTML = "";
    message.classList.add('hidden');

    data.forEach((member,index) => {
        console.log(index + " " + member);
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${member.name}</td>
            <td>${member.surname}</td>
            <td>${member.status}</td>
            <td><button class="delete-btn" data-id="${member.id}" data-index="${index}" id="delete">🗑</button></td>
        `;
        tbody.appendChild(row);

        const deleteBtn = row.querySelector('.delete-btn');
        deleteBtn.addEventListener("click", (event) => {
            const deleteMember = document.getElementById("deleteConfirmModal");

            let memberId = event.target.getAttribute("data-id");
            let memberIndex = event.target.getAttribute("data-index");

            deleteMember.style.display = "flex";
            deleteMemberFromDepartment(memberId, memberIndex);
        });
    });
}

function displayNoMembersMessage(text) {
    const tbody = document.getElementById('table-body');
    const message = document.getElementById('no-members-message');
    tbody.innerHTML = "";
    message.textContent = text;
    message.classList.remove('hidden');
}

async function addMemberToDepartment(){
    document.getElementById("add-members-btn").addEventListener("click", async () => {
        const modal = document.getElementById("add-members-modal");
        modal.style.display = "flex";
        const listContainer = document.getElementById("all-members-list");

        listContainer.innerHTML = "";
        const allMembers = await fetch("/api/coordinator/members").then(res => res.json());

        allMembers.forEach(member => {
            const div = document.createElement("div");
            div.innerHTML = `
                <label>
                    <input type="checkbox" value="${member.id}"> ${member.name} ${member.surname}
                </label>`;
            listContainer.appendChild(div);
        });
    });

    document.getElementById("confirm-add-btn").addEventListener("click", async () => {
        const checkboxes = document.querySelectorAll("#all-members-list input[type='checkbox']:checked");
        const memberIds = Array.from(checkboxes).map(checkbox => checkbox.value);
        const modal = document.getElementById("add-members-modal");

        console.log(memberIds);

        fetch("/api/add/member/department"
        , {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                memberIds: memberIds,
                departmentName: document.getElementById("department").textContent.split(": ")[1]
            })
        }).then(response => {
            if (response.ok) {
                modal.style.display = "none";
                fetchDepartmentMembers(department);
            } else {
                console.error("Eroare la adaugarea membrilor.");
            }
        }).catch(error => {
            console.error("Eroare la adaugarea membrilor:", error);
        });
    });

    document.getElementById("cancel-add-btn").addEventListener("click", () => {
        modal.style.display = "none";
    });
}

async function deleteMemberFromDepartment(memberId, memberIndex) {

    const confirmBtn = document.getElementById("confirmDeleteBtn");
    const deleteMember = document.getElementById("deleteConfirmModal");

    console.log(memberId + " here");
    confirmBtn.addEventListener("click", async () => {
        if(memberId) {
         console.log( "[MEMBERID] " + memberId);
         fetch(`/api/delete/member/department/${memberId}`, {
            method: "DELETE",
             credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
         }).then(response => {
             if(response.ok) {
                 const row = document.querySelector(`button[data-id="${memberId}"]`).closest('tr');
                 row.remove();
             } else {
                    console.error("Eroare la stergerea membrului.");
             }
         }).catch(error => {
                console.error("Eroare la stergerea membrului:", error);
                alert(error.message());
         });
        }
        deleteMember.style.display = "none";
    });

    const cancelBtn = document.getElementById("cancelDeleteBtn");
    cancelBtn.addEventListener("click", () => {
        deleteMember.style.display = "none";
    });
}


function handleSortChange() {
    const field = document.getElementById('sort').value;
    sortTable(field);
}

function sortTable(field) {
    const sorted = [...members].sort((a, b) => {
        return (a[field] || "").localeCompare(b[field] || "");
    });
    displayMembers(sorted);
}
