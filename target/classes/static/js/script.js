var log = document.getElementById("login")
var reg = document.getElementById("register")
var button = document.getElementById("btn")


const submitButton = document.getElementById("submit");

submitButton.addEventListener('click', () => {
    console.log("submitLogin apelat");

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    console.log("Login cu:", email, password);

    if (!email || !password) {
        alert("Te rugăm să completezi toate câmpurile.");
        return;
    }

    const data = {
        email: email,
        password: password
    };

    const dataStirng = JSON.stringify(data);

    fetch("/login", {
        method: "POST",
        credentials: "include",
        headers: {
            'Content-Type': 'application/json'
        },
        body: dataStirng
    })
        .then(response => {
            console.log("Status:", response.status);

            if (response.ok) {
                console.log("Login reușit");
                window.location.replace("/home");
                return response.text();
            }

            return response.text().then(errorMessage => {
                throw new Error(errorMessage);
            });
        })
        .then(data => {
            console.log(data);
            alert("Login reușit!");
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Eroare la autentificare! " + error.message);
        });
});


function register() {
    log.style.left = "-400px";
    reg.style.left = "50px";
    button.style.left = "110px";
}

function login() {
    log.style.left = "50px"
    reg.style.left = "450px"
    button.style.left = "0"
}
