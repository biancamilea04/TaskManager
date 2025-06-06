document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("departments-container");

    const response = await fetch("/api/departments/info");
    console.log(response);
    const departments = await response.json();
    console.log(departments);

    departments.forEach(dept => {
        const department = document.createElement("div");
        department.classList.add("department");
        console.log(dept.percentTaskDone);
        department.innerHTML = `
            <img src=${dept.url} alt="">
            <div class="department-info">
                <strong>${dept.name}</strong>
                <small>${dept.coordinatorName}<br>${dept.memberCount} membri</small>
                <br>
                <small>Procent taskuri finalizate: ${dept.percentTaskDone}% </small>
            </div>
        `;

        department.addEventListener("click", () => {
            window.location.href = `/department-members/${dept.shortName}`;
        });

        container.appendChild(department);
    });
});

document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/member/count')
        .then(response => response.json())
        .then(data => {
            document.getElementById('nr-membri').textContent = data;
        })
        .catch(error => {
            document.getElementById('nr-membri').textContent = 'eroare';
            console.error('Eroare la preluarea numarului de membri:', error);
        });
});