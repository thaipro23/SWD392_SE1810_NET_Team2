function updateRatingAndFeedback(enrollmentId) {
    if (!enrollmentId) return;

    fetch(`/api/enrollment/${enrollmentId}/rating`)
        .then(response => response.json())
        .then(data => {
            // Update rating
            const starContainer = document.getElementById('star-rating');
            starContainer.innerHTML = '';

            if (data.hasRating) {
                const rating = data.rating;
                for (let i = 1; i <= 5; i++) {
                    const star = document.createElement('span');
                    if (i <= rating) {
                        star.className = 'fas fa-star text-warning';
                    } else if (i - 0.5 <= rating) {
                        star.className = 'fas fa-star-half-alt text-warning';
                    } else {
                        star.className = 'fa-star fa-regular text-warning';
                    }
                    starContainer.appendChild(star);
                }
            } else {
                starContainer.innerHTML = '<span>No Rating</span>';
            }

            // Update feedback
            const feedbackElement = document.getElementById('feedback-text');
            feedbackElement.value = data.feedback;
        })
        .catch(error => {
            console.error('Error:', error);
            const starContainer = document.getElementById('star-rating');
            starContainer.innerHTML = '<span>No rating</span>';

            const feedbackElement = document.getElementById('feedback-text');
            feedbackElement.value = 'No feedback';
        });
}

document.addEventListener('DOMContentLoaded', function () {
    const select = document.getElementById('enrollment-select');
    if (select && select.value) {
        updateRatingAndFeedback(select.value);
    }
});