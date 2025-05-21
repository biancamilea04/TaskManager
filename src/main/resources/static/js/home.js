const modal = document.getElementById("taskModal");
const openBtn = document.querySelector(".add-task-btn");
const closeBtn = document.getElementById("closeModal");
const taskList = document.getElementById("taskList");

const card = document.createElement("div");
card.classList.add("task-card");

openBtn.addEventListener("click", () => {
    modal.style.display = "flex";
});

closeBtn.addEventListener("click", () => {
    modal.style.display = "none";
});

window.addEventListener("click", (e) => {
    if (e.target === modal) {
        modal.style.display = "none";
    }
});

function createTaskCard({name, desc, status, period, hours, memberTaskNumber}) {
    const card = document.createElement("div");
    card.classList.add("task-card");

    card.innerHTML = `
        <div class="task-card-header">
            <h4>#${memberTaskNumber}: ${name}</h4>
            <span class="task-status">${status}</span>
        </div>
        <div class="task-desc">${desc}</div>
        <div class="task-period">${period}</div>
        <div class="task-hours"><strong>Ore de activitate:</strong> ${hours}</div>
        <div class="task-action">
             <button class="edit-task-btn">✏️</button>
             <button class="delete-task-btn">🗑️</button>
        </div>
    `;

    card.querySelector(".edit-task-btn").addEventListener("click", () => {
        populateEditForm({name, desc, status, period, hours, memberTaskNumber});
        editModal.style.display = "flex";
    });

    const deleteBtn = card.querySelector(".delete-task-btn");

    deleteBtn.addEventListener("click", () => {
        currentDeleteTaskNumber = memberTaskNumber;
        currentDeleteCard = card;
        deleteModal.style.display = "flex";
    });

    return card;
}

async function saveTask(taskData) {
    const task = {
        title: taskData.name,
        description: taskData.desc,
        status: taskData.status,
        dateTask: taskData.period,
        numberActivityHours: parseFloat(taskData.hours)
    };

    console.log(task.numberActivityHours);

    try {
        const response = await fetch("/api/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(task)
        });

        console.log("Status:", response.status);

        const data = await response.json();
        if (response.ok) {
            console.log("Task salvat");
            return data;
        } else {
            throw new Error("Eroare la salvare: " + (data.message || response.statusText));
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Eroare la salvare! " + error.message);
        throw error;
    }
}

document.getElementById("taskForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("taskName").value;
    const desc = document.getElementById("taskDesc").value;
    const status = document.getElementById("taskStatus").value;
    const period = document.getElementById("taskPeriod").value;
    const hours = document.getElementById("taskHours").value;

    const taskPayload = {name, desc, status, period, hours};
    console.log(taskPayload);

    try {
        let savedTask = await saveTask(taskPayload);

        console.log(savedTask);

        const newCard = createTaskCard({
            name: savedTask.title,
            desc: savedTask.description,
            status: savedTask.status,
            period: savedTask.dateTask,
            hours: savedTask.numberActivityHours,
            memberTaskNumber: savedTask.memberTaskNumber
        });
        taskList.insertBefore(newCard, openBtn);

        modal.style.display = "none";
        this.reset();
    } catch (err) {
        alert(err.message);
        console.error(err);
    }
});


window.addEventListener("DOMContentLoaded", () => {
    fetch("/api/current-username", {
        credentials: "include"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Nu s-a putut obține numele utilizatorului.");
            }
            return response.text();
        })
        .then(name => {
            console.log("Răspuns primit:", name);
            document.getElementById("welcomeMessage").textContent = `Hello ${name}!`;
        })
        .catch(error => {
            console.error("Eroare la obținerea numelui:", error);
        });

    fetch("/api/tasks", {credentials: "include"})
        .then(response => {
            if (!response.ok) throw new Error("Eroare la obținerea taskurilor.");
            return response.json();
        })
        .then(tasks => {
            if (tasks.length > 0) {
                const noTasksMsg = document.getElementById("noTasksMessage");
                const noTasks = document.getElementById("noTasks");
                if (noTasksMsg) noTasksMsg.style.display = "none";
                if (noTasks) noTasks.style.display = "none";

                tasks.forEach(task => {
                    console.log(task);
                    const card = createTaskCard({
                        name: task.title,
                        desc: task.description,
                        status: task.status,
                        period: task.dateTask,
                        hours: task.numberActivityHours,
                        memberTaskNumber: task.memberTaskNumber
                    });
                    taskList.insertBefore(card, openBtn);
                });
            }
        })
        .catch(error => {
            console.error("Eroare la incarcarea taskurilor:", error);
        });
});

const editModal = document.getElementById("editTaskModal");
const closeEditBtn = document.getElementById("closeEditModal");

let currentEditTaskNumber = null;

closeEditBtn.addEventListener("click", () => {
    editModal.style.display = "none";
});

window.addEventListener("click", (e) => {
    if (e.target === editModal) {
        editModal.style.display = "none";
    }
});

function populateEditForm(task) {
    document.getElementById("editTaskName").value = task.name;
    document.getElementById("editTaskDesc").value = task.desc;
    document.getElementById("editTaskStatus").value = task.status;
    document.getElementById("editTaskPeriod").value = task.period;
    document.getElementById("editTaskHours").value = task.hours;
    currentEditTaskNumber = task.memberTaskNumber;
}

async function updateTask(taskData, memberTaskNumber) {
    const response = await fetch(`/api/tasks/${memberTaskNumber}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            title: taskData.name,
            description: taskData.desc,
            status: taskData.status,
            dateTask: taskData.period,
            numberActivityHours: parseFloat(taskData.hours),
        })
    });

    console.log("activity " + taskData.hours);

    if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || "Eroare la actualizare task");
    }

    return response.json();
}

document.getElementById("editTaskForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("editTaskName").value;
    const desc = document.getElementById("editTaskDesc").value;
    const status = document.getElementById("editTaskStatus").value;
    const period = document.getElementById("editTaskPeriod").value;
    const hours = document.getElementById("editTaskHours").value;

    const taskPayload = {name, desc, status, period, hours};
    console.log(taskPayload);

    try {
        const updatedTask = await updateTask(taskPayload, currentEditTaskNumber);
        console.log("updated: " + updatedTask.numberActivityHours);
        console.log(updatedTask);

        const oldCard = [...taskList.querySelectorAll(".task-card")]
            .find(c => c.querySelector("h4").textContent.startsWith(`#${currentEditTaskNumber}:`));
        if (oldCard) taskList.removeChild(oldCard);

        const newCard = createTaskCard({
            name: updatedTask.title,
            desc: updatedTask.description,
            status: updatedTask.status,
            period: updatedTask.dateTask,
            hours: updatedTask.numberActivityHours,
            memberTaskNumber: updatedTask.memberTaskNumber
        });
        taskList.insertBefore(newCard, openBtn);

        editModal.style.display = "none";
        this.reset();
        currentEditTaskNumber = null;
    } catch (err) {
        alert(err.message);
        console.error(err);
    }
});

const deleteModal = document.getElementById("deleteConfirmModal");
const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");

let currentDeleteTaskNumber = null;
let currentDeleteCard = null;

confirmDeleteBtn.addEventListener("click", async () => {
    try {
        const response = await fetch(`/api/tasks/${currentDeleteTaskNumber}`, {
            method: "DELETE",
        });

        if (!response.ok) {
            throw new Error("Eroare la stergere");
        }

        if (currentDeleteCard && currentDeleteCard.parentElement) {
            currentDeleteCard.parentElement.removeChild(currentDeleteCard);
        }

        deleteModal.style.display = "none";
        currentDeleteTaskNumber = null;
        currentDeleteCard = null;
    } catch (error) {
        alert(error.message);
        console.error(error);
    }
});

cancelDeleteBtn.addEventListener("click", () => {
    deleteModal.style.display = "none";
    currentDeleteTaskNumber = null;
    currentDeleteCard = null;
});

window.addEventListener("click", (e) => {
    if (e.target === deleteModal) {
        deleteModal.style.display = "none";
    }
});


const profileBtn = document.getElementById("profileBtn");
const profileDropdown = document.getElementById("profileDropdown");

profileBtn.addEventListener("click", () => {
    profileDropdown.style.display = profileDropdown.style.display === "flex" ? "none" : "flex";
});

window.addEventListener("click", (e) => {
    if (!profileBtn.contains(e.target) && !profileDropdown.contains(e.target)) {
        profileDropdown.style.display = "none";
    }
});

document.getElementById("goToProfile").addEventListener("click", () => {
    window.location.href = "/profile";
});

document.getElementById("logoutBtn").addEventListener("click", async () => {
    try {
        await fetch("/logout", { method: "POST" });
        window.location.href = "/login";
    } catch (error) {
        console.error("Logout failed", error);
    }
});
