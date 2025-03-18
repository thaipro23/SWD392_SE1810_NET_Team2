$(document).ready(function () {
  let page = 0;
  let stars = null;
  const size = 3;
  const schoolId = $("#schoolId").val();
  const ratingContainer = $("#rating-container");
  const seeMoreButton = $("#see-more");
  const ratingTemplate = $("#rating-template");
  function fetchRatings(star) {
    $.ajax({
      url: `/api/school/rating`,
      method: "GET",
      data: {
        schoolId: schoolId,
        page: page,
        size: size,
        stars: star,
      },
      success: function (response) {
        if (response.last) seeMoreButton.hide(); else seeMoreButton.show();
        if (response.totalPages === 0) {
          ratingContainer.append("<p class='text-center'>No ratings.</p>");
        }
        response.content.forEach((rating) => {
          const newRating = ratingTemplate.contents().clone();
          newRating.find(".parent-name").text(rating.parentName);
          newRating.find(".created-date").text(new Date(rating.createdAt).toLocaleString("en-US"));
          newRating.find(".feedback").text(rating.feedback);
          const avgRating = rating.ratings.reduce((a, b) => a + b, 0) / rating.ratings.length;
          newRating.find(".overall-rating").html(renderStars(avgRating, "fas fa-star fa-lg"));
          newRating.find(".avg-rating").text(`(${(Math.round(avgRating * 2) / 2.0).toFixed(1)}/5)`);
          const individualRatingsHtml = renderIndividualRatings(rating.ratings);
          newRating.find(".individual-ratings").html(individualRatingsHtml);
          ratingContainer.append(newRating);
        });
        page++;
      },
      error: function () {
        alert("An error occurred while fetching ratings.");
      },
    });
  }

  function renderStars(rating, starClass) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;

    return (
      `<span class="${starClass} full-star"></span>`.repeat(fullStars) +
      (halfStar ? `<span class="${starClass} half-star"></span>` : "") +
      `<span class="${starClass} empty-star"></span>`.repeat(emptyStars)
    );
  }

  function renderIndividualRatings(ratings) {
    const categories = [
      "Learning program",
      "Facilities and Utilities",
      "Extracurricular Activities",
      "Teachers and Staff",
      "Hygiene and Nutrition",
    ];
    return ratings
      .map(
        (rating, index) => `
          <div class="row mb-2">
            <div class="col-sm-6"><strong>${categories[index]}:</strong></div>
            <div class="col-sm-4 text-warning text-end">
              ${renderStars(rating, "fas fa-star")}
            </div>
            <div class="col-sm-2 m-0 text-end">
              <span>(${rating}/5)</span>
            </div>
          </div>
        `
      )
      .join("");
  }

  fetchRatings(stars);

  $(".btn-stars button").on("click", function () {
    stars = $(this).data("star");
    page = 0;
    $(".btn-stars button").removeClass("active");
    $(this).addClass("active");
    ratingContainer.empty();
    fetchRatings(stars);
  });

  seeMoreButton.on("click", function () {
    fetchRatings(stars);
  });
});
