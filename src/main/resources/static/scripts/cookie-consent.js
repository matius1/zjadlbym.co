(function (){
    var cookieContainer = document.getElementById('cookie-consent');

    if (sessionStorage.getItem("cookie-consent") !== "true") {
        cookieContainer.style.visibility = "visible";
    }

    var btn = document.getElementById('cookie-approval');

    btn.addEventListener('click', function () {
        cookieContainer.style.visibility = 'hidden';
        sessionStorage.setItem('cookie-consent', "true");
    })
})();
