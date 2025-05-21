function getQueryParams() {
    const params = new URLSearchParams(window.location.search);
    return {
        name: params.get("name"),
        coord: params.get("coord")
    };
}

const { name, coord } = getQueryParams();
document.getElementById("projectName").textContent = name;
document.getElementById("coordName").innerHTML = `<strong>Coordonator:</strong> ${coord}`;

const dummyTasks = [
    {
        owner: "Maria",
        name: "Creare afiș",
        desc: "Realizare afiș pentru eveniment",
        deadline: "20 mai 2025",
        status: "pending"
    },
    {
        owner: "Ion",
        name: "Trimitere emailuri",
        desc: "Comunicare cu parteneri",
        deadline: "22 mai 2025",
        status: "inprogress"
    },
    {
        owner: "Alex",
        name: "Raport final",
        desc: "Scriere raport activitate",
        deadline: "25 mai 2025",
        status: "done"
    }
];

function renderTasks() {
    dummyTasks.forEach(task => {
        const card = document.createElement("div");
        card.classList.add("task-card");
        card.innerHTML = `
      <div><strong>${task.owner}</strong></div>
      <div class="task-card-header">
        <h4>${task.name}</h4>
      </div>
      <div class="task-desc">${task.desc}</div>
      <div class="task-period"><strong>Deadline:</strong> ${task.deadline}</div>
    `;

        if (task.status === "pending") {
            document.getElementById("pendingTasks").appendChild(card);
        } else if (task.status === "inprogress") {
            document.getElementById("inProgressTasks").appendChild(card);
        } else {
            document.getElementById("completedTasks").appendChild(card);
        }
    });
}

renderTasks();
