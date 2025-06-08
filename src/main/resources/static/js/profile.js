
const profileBtn = document.getElementById("profileBtn");
const profileDropdown = document.getElementById("profileDropdown");

profileBtn.addEventListener("click", (e) => {
    e.stopPropagation();
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

document.getElementById("goToHome").addEventListener("click", () => {
    window.location.href = "/home";
});

window.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetch("/api/profile/userData", {
            method: "GET",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error("Failed to fetch user data");
        }

        const data = await response.json();
        document.getElementById("memberName").textContent = `${data.name} ${data.surname}`;
        document.getElementById("status").textContent = data.status;
        document.getElementById("votingRight").textContent = data.votingRight;
        document.getElementById("totalHours").textContent = data.totalHours;

        document.getElementById("email").textContent = data.email;
        document.getElementById("phone").value = data.phone || "";
        document.getElementById("address").value = data.address || "";

        document.getElementById("cnp").value = data.cnp || "";
        document.getElementById("numar").value = data.numar || "";
        document.getElementById("serie").value = data.serie || "";

    } catch (err) {
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

    const data = {
        phone: phone,
        address: address,
        cnp: cnp,
        numar: numar,
        serie: serie
    };

    try{
        const response = await fetch("/api/profile/updateDetails", {
            method: "PUT",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
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
        });
        if (!response.ok) {
            throw new Error("Failed to change password");
        }

        passwordModal.classList.add("hidden");
        alert("Parola a fost schimbată cu succes!");
    }catch(err){
        console.error("Error changing password:", err);
        alert("Eroare la schimbarea parolei! " + err.message);
    }
});

const deleteAccountBtn = document.getElementById("deleteAccount");
const deleteModal = document.getElementById("deleteAccountModal");
const closeDeleteBtn = document.getElementById("closeDeleteModalBtn");
const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");

deleteAccountBtn.addEventListener("click", () => {
    deleteModal.classList.remove("hidden");
});

closeDeleteBtn.addEventListener("click", () => {
    deleteModal.classList.add("hidden");
});

cancelDeleteBtn.addEventListener("click", () => {
    deleteModal.classList.add("hidden");
});

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
});