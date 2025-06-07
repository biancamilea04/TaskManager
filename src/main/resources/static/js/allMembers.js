let members = [];

async function fetchMembers() {
    const response = await fetch('/api/member/all');
    members = await response.json();
    displayMembers(members);
}

function displayMembers(data) {
    const tbody = document.getElementById('table-body');
    tbody.innerHTML = "";

    data.forEach(member => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${member.name}</td>
            <td>${member.surname}</td>
            <td>${member.status}</td>
            <td>${member.departments.join(', ')}</td>
        `;
        tbody.appendChild(row);
    });
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

window.onload = fetchMembers();

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