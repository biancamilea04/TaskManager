const profileBtn = document.getElementById("profileBtn");
const profileDropdown = document.getElementById("profileDropdown");
const homeBtn = document.getElementById("homeBtn");

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

document.getElementById("goToHome").addEventListener("click", () => {
    window.location.href = "/home";
})

window.addEventListener("DOMContentLoaded", async () => {
    try{
        const humanDetails = await fetch("/api/profile/humanDetails", {
            method: "GET",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const memberDetails = await fetch("/api/profile/memberDetails", {
            method: "GET",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if(!humanDetails.ok || !memberDetails.ok){
            console.log(humanDetails, memberDetails);
            throw new Error("Failed to fetch user data");
        }

        const humanData = await humanDetails.json();
        const memberData = await memberDetails.json();
        console.log("User data:", humanData, memberData);

        document.getElementById("memberName").textContent = `${humanData.name} ${humanData.surname}`;
        document.getElementById("status").textContent = memberData.status;
        document.getElementById("votingRight").textContent = memberData.votingRight;
        document.getElementById("totalHours").textContent = memberData.totalHours;


        document.getElementById("email").textContent = humanData.email;
        document.getElementById("phone").value = memberData.phone;
        document.getElementById("address").value = memberData.address;

        document.getElementById("cnp").value = memberData.cnp;
        document.getElementById("numar").value = memberData.numar;
        document.getElementById("serie").value = memberData.serie;

    }catch(err){
        console.error("Error fetching user data:", err);
    }
});

document.getElementById("profileForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;
    const cnp = document.getElementById("cnp").value;
    const numar = document.getElementById("numar").value;
    const serie = document.getElementById("serie").value;

    console.log("Saving changes:", { phone, address, cnp, numar, serie });

    const data = {
        phone: phone,
        address: address,
        cnp: cnp,
        numar: numar,
        serie: serie
    };

    const dataString = JSON.stringify(data);

    try{
        const response = await fetch("/api/profile/updateDetails", {
            method: "PUT",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: dataString
        });

        if (!response.ok) {
            throw new Error("Failed to update user data");
        }

        alert("Modificările au fost salvate cu succes!");
    }catch(err){
        console.error("Error saving user data:", err);
        alert("Eroare la salvarea modificărilor! " + err.message);
    }
});

const changePasswordBtn = document.getElementById("changePasswordBtn");
const passwordModal = document.getElementById("passwordModal");
const closeModalBtn = document.getElementById("closeModalBtn");
const savePasswordBtn = document.getElementById("savePasswordBtn");
const closeModalBtn2 = document.getElementById("closeModalBtnX");

changePasswordBtn.addEventListener("click", () => {
    passwordModal.classList.remove("hidden");
});

closeModalBtn.addEventListener("click", () => {
    passwordModal.classList.add("hidden");
});

closeModalBtn2.addEventListener("click", () => {
    passwordModal.classList.add("hidden");
});

savePasswordBtn.addEventListener("click", async () => {
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if(newPassword !== confirmPassword){
        alert("Parolele nu se potrivesc!");
        return;
    }

    try{
        const response = await fetch("/api/profile/changePassword", {
            method: "PUT",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        })
        if (!response.ok) {
            throw new Error("Failed to change password");
        }

        passwordModal.classList.add("hidden");
    }catch(err){
        console.error("Error changing password:", err);
        alert("Eroare la schimbarea parolei! " + err.message);
    }
})

const deleteAccountBtn = document.getElementById("deleteAccount");
const deleteModal = document.getElementById("deleteAccountModal");
const closeDeleteBtn = document.getElementById("closeDeleteModalBtn");
const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");

deleteAccountBtn.addEventListener("click", () => {
    deleteModal.classList.remove("hidden");
})

closeDeleteBtn.addEventListener("click", () => {
    deleteModal.classList.add("hidden");
})

cancelDeleteBtn.addEventListener("click", () => {
    deleteModal.classList.add("hidden");
})

confirmDeleteBtn.addEventListener("click", async () => {
    try{
        const response = await fetch("/api/profile/deleteAccount", {
            method: "DELETE",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error("Failed to delete account");
        }

        alert("Contul a fost sters cu succes!");
        window.location.replace("/login");
    }catch(err){
        console.error("Error deleting account:", err);
        alert("Eroare la stergerea contului! " + err.message);
    }
})