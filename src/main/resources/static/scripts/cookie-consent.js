(function (){
    var cookieContainer = document.getElementById('cookie-consent');

    if (sessionStorage.getItem("cookie-consent") !== "true") {
        cookieContainer.style.display = "block";
    }

    var btn = document.getElementById('cookie-approval');

    btn.addEventListener('click', function () {
        cookieContainer.style.display = 'none';
        sessionStorage.setItem('cookie-consent', "true");
    })
})();
