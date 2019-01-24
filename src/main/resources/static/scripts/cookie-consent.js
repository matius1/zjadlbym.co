(function (){
    var cookieContainer = $('#cookie-consent');
    var historyContainer = $('#history');
    var url = "http://localhost:8080/przepis/history";
    var btn = document.getElementById('cookie-approval');
    var addClickHandler = function () {
        btn.addEventListener('click', function () {
            cookieContainer.removeClass('not-accepted').addClass('accepted');
            sessionStorage.setItem('cookie-consent', "true");
        })
    };

    if (sessionStorage.getItem("cookie-consent") !== "true") {
        cookieContainer.addClass('not-accepted');
    } else {
        cookieContainer.addClass('accepted');
    }

    addClickHandler();
})();
