$(function () { 
    const recipesPerRow = 3;
    const recipeTemplate = $('#recipe-template').children('.recipe');
    const rowTemplate = $('#row-template').children('.row');

    $('#search').click(function () {
        $('.recipes').empty();
        let ingredients = $('.ingredient').map(function () { return this.dataset.name }).get().join();
        if (ingredients === undefined || ingredients === null || ingredients === '') {
            return;
        }

        renderHistorySection(rowTemplate, recipeTemplate);
        $.get("http://localhost:8080/przepis/get",
            "ingredients=" + ingredients,
            function (recipes) {
                console.log(recipes);
                let recipesCounter = 0;
                for (let recipe of recipes) {
                    const rowNumber = recipesCounter/recipesPerRow;
                    const rowIdentifier = "row-" + rowNumber;
                    if (recipesCounter % recipesPerRow === 0) {
                        appendRow(rowTemplate, rowIdentifier, ".recipes");
                    }

                    appendRecipeTile(recipeTemplate, recipe, rowIdentifier, ".recipes");
                }
                
                $('#results').removeClass('empty').addClass('present');
            });
    });

    renderHistorySection(rowTemplate, recipeTemplate);       
})

function renderHistorySection(rowTemplate, recipeTemplate) {
    $.get("http://localhost:8080/przepis/history", function (recipes) {
        const recipesInHistory = 3;

        $('.recipes-history').empty();
        if (recipes === null || recipes === undefined) {
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
    const ingredientsInRecipe = 4;

    let recipeElement = recipeTemplate.clone();
    const recipeThumbnail = recipe.thumbnail === "" ? noThumbnailPlaceholder : recipe.thumbnail;
    recipeElement.find('.recipe-thumbnail').css('background-image', "url('" + recipeThumbnail + "')");
    recipeElement.find('.recipe-name').html(recipe.name);
    recipeElement.find('.recipe-url').attr('href', recipe.url);

    let ingredientsList = recipeElement.find('.recipe-ingredients');
    if (recipe.ingredients !== null && recipe.ingredients !== undefined && recipe.ingredients !== "") {
        var ingredientsToShow = recipe.ingredients.split(',').slice(0, ingredientsInRecipe);
        for (let ingredient of ingredientsToShow) {
            ingredientsList.append("<li class='recipe-ingredient'>" + ingredient + "</li>");
        }
    }

    recipeElement.appendTo($('#' + rowIdentifier));
}