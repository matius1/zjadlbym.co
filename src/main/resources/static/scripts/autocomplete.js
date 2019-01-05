"use strict";

var autocomplete = (function () {
    var options;
    var input;
    var currentFocus;

    var removeActive = function (x) {
        for (var i = 0; i < x.length; i++) {
            x[i].classList.remove("autocomplete-active");
        }
    };

    var addActive = function (x) {
        if (!x) return false;
        removeActive(x);
        if (currentFocus >= x.length) currentFocus = 0;
        if (currentFocus < 0) currentFocus = (x.length - 1);
        x[currentFocus].classList.add("autocomplete-active");
    };

    var closeAllLists = function (elmnt) {
        var x = document.getElementsByClassName("autocomplete-items");
        for (var i = 0; i < x.length; i++) {
            if (elmnt != x[i] && elmnt != input) {
                x[i].parentNode.removeChild(x[i]);
            }
        }
    };

    var addListeners = function () {
        input.addEventListener("input", function(e) {
            var a, b, i, val = this.value;
            closeAllLists();
            if (!val) { return false;}
            currentFocus = -1;
            a = document.createElement("DIV");
            a.setAttribute("id", this.id + "autocomplete-list");
            a.setAttribute("class", "autocomplete-items");
            this.parentNode.appendChild(a);
            for (i = 0; i < options.length; i++) {
              if (options[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                b = document.createElement("DIV");
                b.innerHTML = "<strong>" + options[i].substr(0, val.length) + "</strong>";
                b.innerHTML += options[i].substr(val.length);
                b.innerHTML += "<input type='hidden' value='" + options[i] + "'>";
                    b.addEventListener("click", function(e) {
                    input.value = this.getElementsByTagName("input")[0].value;
                    closeAllLists();
                });
                a.appendChild(b);
              }
            }
        });
        input.addEventListener("keydown", function(e) {
            var x = document.getElementById(this.id + "autocomplete-list");
            if (x) x = x.getElementsByTagName("div");
            if (e.keyCode == 40) {
              currentFocus++;
              addActive(x);
            } else if (e.keyCode == 38) {
              currentFocus--;
              addActive(x);
            } else if (e.keyCode == 13) {
              e.preventDefault();
              if (currentFocus > -1) {
                if (x) x[currentFocus].click();
              }
            }
        });

        document.addEventListener("click", function (e) {
            closeAllLists(e.target);
        });
        
        document.getElementById("add").addEventListener("click", function (e) {
            for (var i = 0; i < options.length; i++) {
                if (input.value !== "" && options[i].substr(0, input.value.length).toUpperCase() == input.value.toUpperCase()) {
                    var div = document.createElement('div');
                    div.className = 'ingredient';
                    div.setAttribute('data-name', input.value);
                    var nameSpan = document.createElement('span');
                    nameSpan.className = "ingredient--name";
                    nameSpan.innerHTML = input.value;
                    var deleteBtn = document.createElement('span');
                    deleteBtn.className = 'ingredient--delete';
                    div.appendChild(nameSpan);
                    deleteBtn = div.appendChild(deleteBtn);
                    document.getElementById('ingredients').appendChild(div);                    
                    options.splice(i, 1);
                    input.value = '';

                    deleteBtn.addEventListener('click', function (e) {
                        if (this.parentElement.dataset.name !== "" && this.parentElement.dataset.name.substr(0, this.parentElement.dataset.name.length).toUpperCase() == this.parentElement.dataset.name.toUpperCase()) {
                            document.getElementById('ingredients').removeChild(this.parentElement);
                            options.push(this.parentElement.dataset.name);
                        }
                    });
                    return;
                } else {
                    var custom = true;
                }
            }

            if (custom) {
                var div = document.createElement('div');
                div.className = 'ingredient';
                div.setAttribute('data-name', input.value);
                var nameSpan = document.createElement('span');
                nameSpan.className = "ingredient--name";
                nameSpan.innerHTML = input.value;
                var deleteBtn = document.createElement('span');
                deleteBtn.className = 'ingredient--delete';
                div.appendChild(nameSpan);
                deleteBtn = div.appendChild(deleteBtn);
                document.getElementById('ingredients').appendChild(div);
                input.value = '';

                deleteBtn.addEventListener('click', function (e) {
                    if (this.parentElement.dataset.name !== "" && this.parentElement.dataset.name.substr(0, this.parentElement.dataset.name.length).toUpperCase() == this.parentElement.dataset.name.toUpperCase()) {
                        document.getElementById('ingredients').removeChild(this.parentElement);
                    }
                })
            }
        });
    };

    var init = function(array, el) {
        options = array;
        input = el;

        addListeners();
    };

    return {
        init: init
    }
})();

