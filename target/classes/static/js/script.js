var log = document.getElementById("login")
var reg = document.getElementById("register")
var button = document.getElementById("btn")

function deleteCookie(name) {
    document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

deleteCookie('jwt');
deleteCookie('user');
deleteCookie('role');

function showMessage(message, type = "error") {
    let oldPopup = document.getElementById("popup-message");
    if (oldPopup) oldPopup.remove();

   let overlay = document.createElement("div");
    overlay.id = "popup-message";
    overlay.className = "popup-overlay";

   let popup = document.createElement("div");
    popup.className = `popup-message-box ${type === "success" ? "success" : "error"}`;

   let closeBtn = document.createElement("span");
    closeBtn.className = "popup-close-btn";
    closeBtn.innerHTML = "&times;";
    closeBtn.onclick = () => overlay.remove();

    let text = document.createElement("span");
    text.innerText = message;

    popup.appendChild(closeBtn);
    popup.appendChild(text);
    overlay.appendChild(popup);

    document.body.appendChild(overlay);

    setTimeout(() => {
        if(overlay.parentNode) overlay.remove();
    }, 4000);
}


const submitButton = document.getElementById("submit");
submitButton.addEventListener('click', () => {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    console.log("loginController cu:", email, password);

    if (!email || !password) {
        showMessage("Te rugam să completezi toate campurile.");
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
                return response.json();
            }

            return response.text().then(errorMessage => {
                throw new Error(errorMessage);
            });
        })
        .then(data => {
            localStorage.setItem("jwt", data.jwt);
            window.location.replace("/home");
        })
        .catch(error => {
            console.error("Error:", error);
            showMessage("Eroare la autentificare! " + error.message);
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
        showMessage("Please fill in all fields");
        return;
    }

    if (!validateEmail(email)) {
        showMessage("Email invalid!");
        return;
    }

    if (password !== confirmPassword) {
        showMessage("Parolele nu coincid!");
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
            window.location.replace("/login");
            return response.text();}
        return response.text().then(errorMessage => {
            throw new Error(errorMessage);
        });
    })
        .then(data => {
        })
        .catch(error => {
            showMessage("Error:" + error);
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


