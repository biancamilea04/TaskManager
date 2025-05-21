var log = document.getElementById("login")
var reg = document.getElementById("register")
var button = document.getElementById("btn")

function deleteCookie(name) {
    document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

deleteCookie('jwt');
deleteCookie('user');
deleteCookie('role');

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



const registerButton = document.getElementById("submitRegister");
registerButton.addEventListener('click',(event) =>{
    event.preventDefault();
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
        console.log(response);
        if (response.ok) {
            console.log("Register reușit");
            window.location.replace("/login");
            return response.text();}
        return response.text().then(errorMessage => {
            throw new Error(errorMessage);
        });
    })
        .then(data => {
            console.log(data);
            alert("Register reusit!");
        })
        .catch(error => {
            console.error("Error:" + error);
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

function validateEmail(email) {
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
}


