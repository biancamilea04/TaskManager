
function validateEmail(email) {
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
}

const submitButton = document.getElementById("submit");
submitButton.addEventListener('click',() =>{
    console.log("submitRegister apelat");

    const name = document.getElementById("NameRegister").value;
    const surname = document.getElementById("SurnameRegister").value;
    const email = document.getElementById("emailRegister").value;
    const password = document.getElementById("passwordRegister").value;
    const confirmPassword = document.getElementById("passwordconRegister").value;

    console.log("Register cu:", name, surname, email, password);

    if (!name || !surname || !email || !password) {
        alert("Please fill in all fields");
        return;
    }

    if (!validateEmail(email)) {
        alert("Email invalid!");
        return;
    }

    if (password !== confirmPassword) {
        alert("Parolele nu coincid!");
        return;
    }

    const data = {
        name: name,
        surname: surname,
        email: email,
        password: password
    };

    fetch("/register", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    }).then(response => {
        if (response.ok) {
            window.location.replace("/login");
            return response.text();}
        return response.text().then(errorMessage => {
            throw new Error(errorMessage);
        });
    })
        .then(data => {
            console.log(data);
            alert("Register reușit!");
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Eroare la înregistrare! " + error.message);
        });
});