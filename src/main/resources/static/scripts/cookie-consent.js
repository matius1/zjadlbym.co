(function (){
    var cookieContainer = $('#cookie-consent');
    var historyContainer = $('#history');
    var url = "http://localhost:8080/przepis/history";
    var btn = document.getElementById('cookie-approval');

    // var renderHistory = function (data) {
    //     // console.log("render history", data);
    //
    //     // creating history recipes elements here
    //
    //     // if there are some elements in history array show history div with previously rendered elements
    //     if (data.length) {
    //         historyContainer.removeClass('empty').addClass('present');
    //     }
    // };
    //
    // var getHistory = function () {
    //     return new Promise(function (resolve, reject) {
    //         $.get(url, function (data) {
    //             resolve(data);
    //         })
    //     });
    // };

    var addClickHandler = function () {
        btn.addEventListener('click', function () {
            cookieContainer.removeClass('not-accepted').addClass('accepted');
            sessionStorage.setItem('cookie-consent', "true");
    
            getHistory().then(function (data) {
                renderHistory(data);
            }).catch(function (error) {
                console.log(error);
            })
        })
    };

    if (sessionStorage.getItem("cookie-consent") !== "true") {
        cookieContainer.addClass('not-accepted');
    } else {
        cookieContainer.addClass('accepted');

        // getHistory().then(function (data) {
        //     renderHistory(data);
        // }).catch(function (error) {
        //     console.log(error);
        // })
    }

    addClickHandler();
})();
