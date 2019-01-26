$(function () { 
    const recipesPerRow = 3;
    const recipeTemplate = $('#recipe-template').children('.recipe');
    const rowTemplate = $('#row-template').children('.row');

    renderHistorySection(rowTemplate, recipeTemplate);

    $('#search').click(function () {
        $('.recipes').empty();
        $('#error').removeClass('present').addClass('empty');
        $('#info').removeClass('present').addClass('empty');

        let ingredients = $('#selected_ingredients .ingredient').map(function () { return this.dataset.name }).get().join();
        let excluded = $('#excluded_ingredients .ingredient').map(function () { return this.dataset.name }).get().join();
        let optional = $('#optional_ingredients').val() || 999;



        if (ingredients === undefined || ingredients === null || ingredients === '') {
            return;
        }

        $.get("http://localhost:8080/przepis/get",
            "ingredients=" + ingredients + "&ingredientsToExclude=" + excluded + "&maxNoOfMissingIngredients=" + optional,
            function (recipes) {
                let recipesCounter = 0;
                if (recipes === null || recipes === undefined) {
                    $('#info').removeClass('empty').addClass('present');
                    return;
                }

                for (let recipe of recipes) {
                    const rowNumber = recipesCounter/recipesPerRow;
                    const rowIdentifier = "row-" + rowNumber;
                    if (recipesCounter % recipesPerRow === 0) {
                        appendRow(rowTemplate, rowIdentifier, ".recipes");
                    }

                    appendRecipeTile(recipeTemplate, recipe, rowIdentifier, ".recipes");
                }

                var ingredientsEl = $('.recipe-ingredient');

                var searched = ingredients.split(',');

                for (let ingredientFound of ingredientsEl) {
                    for (let ingredient of searched) {
                        if ((ingredientFound.innerHTML).replace(/ /g,'').toLowerCase() === ingredient.toLowerCase()) {
                            ingredientFound.style.color = "green";
                        }
                    }
                }
                
                $('#results').removeClass('empty').addClass('present');
            }).fail(function (data) {
            let message = "";
            switch (data.responseJSON.status) {
            case 500:
                message = "An error occured. Please try later.";
                break;
            case 400:
                message = "Provided data is invalid. Please remediate the form and try again."
                break;
            case 404:
                message = "Requested site is not found. Please try later."
                break;
            }

            $('.error-message').text(message);
            $('#error').removeClass('empty').addClass('present');
        });

        if (sessionStorage.getItem("cookie-consent") === "true") {
            renderHistorySection(rowTemplate, recipeTemplate);
        }
    });
});

function renderHistorySection(rowTemplate, recipeTemplate) {
    $.get("http://localhost:8080/przepis/history", function (recipes) {
        const recipesInHistory = 3;

        $('.recipes-history').empty();
        if (!recipes) {
            return;
        }
        for (let recipe of recipes.slice(0, recipesInHistory)) {
            const rowIdentifier = "recipe-history-row";
            appendRow(rowTemplate, rowIdentifier, ".recipes-history");
            appendRecipeTile(recipeTemplate, recipe, rowIdentifier);
        }
        $('#history').removeClass('empty').addClass('present');
    });
}

function appendRow(rowTemplate, rowIdentifier, parentIdentifier) {
    let rowElement = rowTemplate.clone();
    rowElement.attr('id', rowIdentifier);
    rowElement.appendTo($(parentIdentifier));
}

function appendRecipeTile(recipeTemplate, recipe, rowIdentifier) {
    const noThumbnailPlaceholder = "http://www.fecheliports.com/static/images/heliports_image_placeholder.jpg";
    const maxIngredientsInRecipe = 4;
    var lettersInLine = 20;

    let recipeElement = recipeTemplate.clone();
    const recipeThumbnail = recipe.thumbnail === "" ? noThumbnailPlaceholder : recipe.thumbnail;
    recipeElement.find('.recipe-thumbnail').css('background-image', "url('" + recipeThumbnail + "')");
    recipeElement.find('.recipe-name').html(recipe.name);
    recipeElement.find('.recipe-url').attr('href', recipe.url);
    const ingredientsInRecipe = maxIngredientsInRecipe - Math.floor(recipe.name.length/lettersInLine);

    let ingredientsList = recipeElement.find('.recipe-ingredients');
    if (recipe.ingredients !== null && recipe.ingredients !== undefined && recipe.ingredients !== "") {
        var ingredientsToShow = recipe.ingredients.split(',').slice(0, ingredientsInRecipe);
        for (let ingredient of ingredientsToShow) {
            ingredientsList.append("<li class='recipe-ingredient'>" + ingredient + "</li>");
        }
    }

    recipeElement.appendTo($('#' + rowIdentifier));
}