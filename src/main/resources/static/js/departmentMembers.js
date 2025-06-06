let members = [];

window.onload = async function () {
    const pathParts = window.location.pathname.split('/');
    const department = decodeURIComponent(pathParts[pathParts.length - 1]);
    fetchDepartmentMembers(department);

    const departmentTitle = document.getElementById("department");
    updateDepartmentStats(department);
    const departmentName = await fetch(`/api/departments/name/${department}`).then(res => res.text());
    console.log(departmentName);
    departmentTitle.textContent = `Departament: ${departmentName}`;
};

async function fetchDepartmentMembers(departmentName) {
    const response = await fetch(`/api/department/members/${departmentName}`);
    const contentType = response.headers.get("content-type");

    if (!response.ok) {
        if (contentType && contentType.includes("application/json")) {
            const data = await response.json();
            displayNoMembersMessage(data.message);
        } else {
            displayNoMembersMessage("A aparut o eroare.");
        }
        return;
    }

    const data = await response.json();
    console.log(data);
    members = data;
    displayMembers(members);
}

function displayMembers(data) {
    const tbody = document.getElementById('table-body');
    const message = document.getElementById('no-members-message');
    tbody.innerHTML = "";
    message.classList.add('hidden');

    data.forEach(member => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${member.name}</td>
            <td>${member.surname}</td>
            <td>${member.status}</td>
        `;
        tbody.appendChild(row);
    });
}

function displayNoMembersMessage(text) {
    const tbody = document.getElementById('table-body');
    const message = document.getElementById('no-members-message');
    tbody.innerHTML = "";
    message.textContent = text;
    message.classList.remove('hidden');
}

function handleSortChange() {
    const field = document.getElementById('sort').value;
    sortTable(field);
}

function sortTable(field) {
    const sorted = [...members].sort((a, b) => {
        if (field === 'departments') {
            return (a.departments[0] || "").localeCompare(b.departments[0] || "");
        }
        return (a[field] || "").localeCompare(b[field] || "");
    });
    displayMembers(sorted);
}

async function updateDepartmentStats(departmentName) {
    const response = await fetch(`/api/departments/stats/${departmentName}`);
    if (!response.ok) return;

    console.log(response);
    const data = await response.json();

    document.getElementById('nr-taskuri').textContent = data.nrTaskuri ?? "-";
    document.getElementById('nr-taskuri-finalizate').textContent = data.nrTaskuriFinalizate ?? "-";
    document.getElementById('performanta').textContent = data.performanta ?? "-";
}
