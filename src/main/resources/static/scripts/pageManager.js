$(function () {
    const noThumbnailPlaceholder = "http://www.fecheliports.com/static/images/heliports_image_placeholder.jpg";
    const recipesPerRow = 3;
    const ingredientsInRecipe = 4;

    $('#search').click(function () {
        $('.recipes').empty();
        let ingredients = $('.ingredient').map(function () { return this.dataset.name }).get().join();
        if (ingredients === undefined || ingredients === null || ingredients === '') {
            return;
        }

        const recipeTemplate = $('#recipe-template').children('.recipe');
        const rowTemplate = $('#row-template').children('.row');
        $.get("http://localhost:8080/przepis/get",
            "ingredients=" + ingredients,
            function (recipes) {
                console.log(recipes);
                let recipesCounter = 0;
                for (let recipe of recipes) {
                    const rowNumber = recipesCounter/recipesPerRow;
                    const rowIdentifier = "row-" + rowNumber;
                    if (recipesCounter % recipesPerRow === 0) {
                        let rowElement = rowTemplate.clone();
                        rowElement.attr('id', rowIdentifier);
                        rowElement.appendTo($('.recipes'));
                    }

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
                
                $('#results').removeClass('empty').addClass('present');
            });
    });
})