let members = [];
const pathParts = window.location.pathname.split('/');
const department = decodeURIComponent(pathParts[pathParts.length - 1]);

document.addEventListener("DOMContentLoaded", async function () {
    setTitle(department);
    fetchDepartmentMembers(department);
    addMemberToDepartment();

    const confirmBtn = document.getElementById("confirmDeleteBtn");
    const cancelBtn = document.getElementById("cancelDeleteBtn");
    const deleteMemberModal = document.getElementById("deleteConfirmModal");

    confirmBtn.addEventListener("click", async () => {
        const memberId = deleteMemberModal.dataset.memberId;
        if (memberId) {
            try {
                const response = await fetch(`/api/delete/member/department/${memberId}`, {
                    method: "DELETE",
                    credentials: "include",
                    headers: { "Content-Type": "application/json" }
                });
                if (response.ok) {
                    const row = document.querySelector(`button[data-id="${memberId}"]`).closest('tr');
                    row.remove();
                } else {
                    alert("Eroare la stergerea membrului.");
                }
            } catch (error) {
                alert(error.message);
            }
        }
        deleteMemberModal.style.display = "none";
        deleteMemberModal.classList.add("hidden");
    });

    cancelBtn.addEventListener("click", () => {
        deleteMemberModal.style.display = "none";
        deleteMemberModal.classList.add("hidden");
    });
});

async function setTitle(department) {
    const departmentTitle = document.getElementById("department");
    const departmentName = await fetch(`/api/departments/name/${department}`).then(res => res.text());
    departmentTitle.textContent = `Departament: ${departmentName}`;
}

async function fetchDepartmentMembers(departmentName) {
    const response = await fetch(`/api/department/members/${departmentName}`);
    members = await response.json();
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
    message.style.display = "none";

    data.forEach((member, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${member.name}</td>
            <td>${member.surname}</td>
            <td>${member.status}</td>
            <td><button class="delete-btn" data-id="${member.id}" data-index="${index}">🗑</button></td>
        `;
        tbody.appendChild(row);

        const deleteBtn = row.querySelector('.delete-btn');
        deleteBtn.addEventListener("click", (event) => {
            const memberId = event.target.getAttribute("data-id");
            const modal = document.getElementById("deleteConfirmModal");
            modal.dataset.memberId = memberId;
            modal.classList.remove("hidden");
            modal.style.display = "flex";
        });
    });
}

function displayNoMembersMessage(text) {
    const tbody = document.getElementById('table-body');
    const message = document.getElementById('no-members-message');
    tbody.innerHTML = "";
    message.textContent = text;
    message.classList.remove('hidden');
    message.style.display = "block";
}

async function addMemberToDepartment() {
    document.getElementById("add-members-btn").addEventListener("click", async () => {
        const modal = document.getElementById("add-members-modal");
        modal.classList.remove('hidden');
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

        fetch("/api/add/member/department", {
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
                modal.classList.add('hidden');
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
        const modal = document.getElementById("add-members-modal");
        modal.classList.add('hidden');
        modal.style.display = "none";
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