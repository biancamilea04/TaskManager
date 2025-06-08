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

document.getElementById("exportStatistics").addEventListener("click", function() {
    fetch('/api/statistics/export')
        .then(resp => {
            if (!resp.ok) throw new Error("Eroare download!");
            return resp.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = 'statistics.pdf';
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
        })
        .catch(() => alert("Eroare la generarea imaginii!"));
});

window.onload = fetchMembers();
