
function submitRegister(event) {
    console.log("submitRegister apelat");
    try {
        event.preventDefault();

        var username = document.getElementById("usernameRegister").value;
        var email = document.getElementById("emailRegister").value;
        var password = document.getElementById("passwordRegister").value;
        var confirmPassword = document.getElementById("passwordconRegister").value;

        console.log("Register cu:", username, email, password);

        if (password !== confirmPassword) {
            alert("Parolele nu coincid!");
            return;
        }

        const data = {
            username: username,
            email: email,
            password: password
        };

        fetch("/register", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) return response.text();
                throw new Error("Eroare la înregistrare!");
            })
            .then(data => {
                console.log(data);
                alert("Register reușit!");
            })
            .catch(error => {
                console.error("Error:", error);
                alert("Eroare la înregistrare!");
            });
    } catch (error) {
        console.error("Error caught:", error);
        alert("A apărut o eroare!");
    }
}