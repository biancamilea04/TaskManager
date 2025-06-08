document.getElementById('uploadForm').addEventListener('submit', async function(e){
    e.preventDefault();
    const fileInput = document.getElementById('fileInput');
    const feedback = document.getElementById('feedback');
    feedback.style.display = "none";

    if (!fileInput.files.length) {
        feedback.textContent = "Selecteaza un fisier!";
        feedback.className = "feedback error";
        feedback.style.display = "block";
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const response = await fetch('/api/members/import', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            feedback.textContent = await response.text();
            feedback.className = "feedback success";
            feedback.style.display = "block";
            fileInput.value = "";
        } else {
            let err = await response.text();
            feedback.textContent = "Eroare la incarcare: " + err;
            feedback.className = "feedback error";
            feedback.style.display = "block";
        }
    } catch (err) {
        feedback.textContent = "Nu s-a putut conecta la server!";
        feedback.className = "feedback error";
        feedback.style.display = "block";
    }
});


const statusSelect = document.getElementById('statusSelect');
const currentStatus = document.getElementById('currentStatus');
const statusFeedback = document.getElementById('statusFeedback');
let members = [];

async function loadMembers() {
    const res = await fetch('/api/member/all');
    members = await res.json();
    const memberListDiv = document.getElementById('memberList');
    memberListDiv.innerHTML = '';
    members.forEach(m => {
        const wrapper = document.createElement('label');
        wrapper.style.display = 'block';
        wrapper.style.cursor = 'pointer';
        wrapper.style.marginBottom = '4px';
        wrapper.innerHTML = `
            <input type="checkbox" value="${m.id}" class="member-checkbox">
            ${m.name} ${m.surname} <span style="color: #888; font-size: 12px;">(${m.status || '-'})</span>
        `;
        memberListDiv.appendChild(wrapper);
    });
}

function showStatus(status) {
    currentStatus.textContent = status ? `Status actual: ${status}` : 'Status actual: -';
}

document.getElementById('statusForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const checked = Array.from(document.querySelectorAll('.member-checkbox:checked'));
    const memberIds = checked.map(cb => cb.value);
    const status = document.getElementById('statusSelect').value;
    const statusFeedback = document.getElementById('statusFeedback');
    statusFeedback.style.display = "none";
    if (memberIds.length === 0 || !status) return;

    console.log(memberIds);
    const res = await fetch('/api/member/update-status', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ memberIds: memberIds, status: status })
    });
    if (res.ok) {
        console.log("DA");
        statusFeedback.textContent = "Status actualizat cu succes!";
        statusFeedback.className = "feedback success";
        statusFeedback.style.display = "block";
        members.forEach(m => {
            if (memberIds.includes(m.id.toString())) m.status = status;
        });
        loadMembers();
    } else {
        console.log(memberIds);
        statusFeedback.textContent = "Eroare la actualizare!";
        statusFeedback.className = "feedback error";
        statusFeedback.style.display = "block";
    }
});

document.getElementById('exportMembersBtn').addEventListener('click', function() {
    fetch('/api/members/export')
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'members_export.json';
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(err => alert('Export nereușit!'));
});

loadMembers();
