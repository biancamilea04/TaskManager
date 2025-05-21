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


        document.getElementById("name").textContent = `${humanData.name} ${humanData.surname}`;
        document.getElementById("email").textContent = humanData.email;
        document.getElementById("phone").value = memberData.phone;
        document.getElementById("address").value = memberData.address;

    }catch(err){
        console.error("Error fetching user data:", err);
    }

});


