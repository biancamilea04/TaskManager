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


