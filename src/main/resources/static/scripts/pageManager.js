$(function () {
    const noThumbnailPlaceholder = "http://www.fecheliports.com/static/images/heliports_image_placeholder.jpg";

    $('#search').click(function () {
        $('.recipes').empty();
        let ingredients = $('.ingredient').map(function () { return this.dataset.name }).get().join();
        if (ingredients === undefined || ingredients === null || ingredients === '') {
            return;
        }

        const recipeTemplate = $('#recipe-template').children('.recipe');
        $.get("http://localhost:8080/przepis/get",
            "ingredients=" + ingredients,
            function (recipes) {
                console.log(recipes);
                for (let recipe of recipes) {
                    let recipeElement = recipeTemplate.clone();
                    const recipeThumbnail = recipe.thumbnail === "" ? noThumbnailPlaceholder : recipe.thumbnail;

                    recipeElement.find('.recipe-thumbnail').attr('src', recipeThumbnail);
                    recipeElement.find('.recipe-name').html(recipe.name);
                    recipeElement.find('.recipe-url').attr('href', recipe.url);

                    recipeElement.appendTo($('.recipes'));
                }

                
                $('#results').removeClass('empty').addClass('present');
            });
    });
})