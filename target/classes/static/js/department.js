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

document.addEventListener('DOMContentLoaded', function() {
    const profileBtn = document.getElementById("profileBtn");
    const profileDropdown = document.getElementById("profileDropdown");

    if (profileBtn && profileDropdown) {
        profileBtn.addEventListener("click", function(e) {
            e.stopPropagation();
            profileDropdown.style.display = profileDropdown.style.display === "flex" ? "none" : "flex";
        });

        window.addEventListener("click", function(e) {
            if (!profileBtn.contains(e.target) && !profileDropdown.contains(e.target)) {
                profileDropdown.style.display = "none";
            }
        });

        const goToProfile = document.getElementById("goToProfile");
        if (goToProfile) {
            goToProfile.addEventListener("click", function() {
                window.location.href = "/profile";
            });
        }

        const logoutBtn = document.getElementById("logoutBtn");
        if (logoutBtn) {
            logoutBtn.addEventListener("click", async function() {
                try {
                    await fetch("/logout", { method: "POST" });
                    window.location.href = "/login";
                } catch (error) {
                    console.error("Logout failed", error);
                }
            });
        }
    }
});