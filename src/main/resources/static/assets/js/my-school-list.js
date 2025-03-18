
                    const urlParams = new URLSearchParams(window.location.search);
                    const currentTab = urlParams.get('currentTab') || '0';

                    $('#schoolTabs .nav-link').removeClass('active');
                    $('#schoolTabsContent .tab-pane').removeClass('show active');
                    if (currentTab === '0') {
                        $('#current-tab').addClass('active');
                        $('#current').addClass('show active');
                    } else {
                        $('#previous-tab').addClass('active');
                        $('#previous').addClass('show active');
                    }
                $('#current-tab, #previous-tab').click(function(e) {
                    e.preventDefault();
                    const targetTab = $(this).attr('data-bs-target');
                    const currentTab = $(this).attr('id');

                    $('#schoolTabs .nav-link').removeClass('active');
                    $(this).addClass('active');

                    $('#schoolTabsContent .tab-pane').removeClass('show active');
                    $(targetTab).addClass('show active');

                    loadPage(currentTab);
                });
                $(document).on('click', '.page-link', function(e) {
                    e.preventDefault();
                    const pageIndex = $(this).attr('data-page');
                    const currentTab = $('#schoolTabs .nav-link.active').attr('id');
                    if (pageIndex) {
                        loadPage(currentTab, pageIndex);
                    }
                });
                function loadPage(currentTab, page = 1) {
                const params = new URLSearchParams({
                    page: page,
                    size: 10,
                    currentTab: currentTab === 'current-tab' ? 0 : 1
                });
                const newUrl = "/parent/my_schools?" + params.toString();
                history.pushState(null, '', newUrl); 

                $.ajax({
                    url: newUrl,
                    type: "GET",
                    success: function(response) {
                        $(`#${currentTab === 'current-tab' ? 'current' : 'previous'}`).html($(response).find(`#${currentTab === 'current-tab' ? 'current' : 'previous'}`).html());
                    },
                    error: function(xhr, status, error) {
                        console.error("Error loading schools:", error);
                    }
                });
                }
                function handleUpdateRate() {
                    const viewModal = document.getElementById('viewRatingModal');
                    const schoolId = viewModal.getAttribute('data-school-id');
                    if (schoolId) {
                        // Lấy các giá trị từ view modal
                        const ratingData = {
                            learning: document.getElementById('modalLearningProgramValue').textContent,
                            facilities: document.getElementById('modalFacilitiesValue').textContent,
                            extracurricular: document.getElementById('modalExtracurricularValue').textContent,
                            teachers: document.getElementById('modalTeachersValue').textContent,
                            hygiene: document.getElementById('modalHygieneValue').textContent,
                            feedback: document.getElementById('modalFeedback').value
                        };
                
                        // Đóng view modal
                        const viewModalInstance = bootstrap.Modal.getInstance(viewModal);
                        viewModalInstance.hide();
                
                        // Set school ID
                        $('#schoolId').val(schoolId);
                
                        // Cập nhật stars và values cho từng tiêu chí
                        const criteria = [
                            { starsId: 'learningProgramStars', valueId: 'learningProgramValue', rating: ratingData.learning },
                            { starsId: 'facilitiesStars', valueId: 'facilitiesValue', rating: ratingData.facilities },
                            { starsId: 'extracurricularStars', valueId: 'extracurricularValue', rating: ratingData.extracurricular },
                            { starsId: 'teachersStars', valueId: 'teachersValue', rating: ratingData.teachers },
                            { starsId: 'hygieneStars', valueId: 'hygieneValue', rating: ratingData.hygiene }
                        ];
                
                        criteria.forEach(criterion => {
                            if (criterion.rating) {
                                const starContainer = document.getElementById(criterion.starsId);
                                const ratingDisplay = document.getElementById(criterion.valueId);
                                const rating = parseInt(parseFloat(criterion.rating)); // Convert "4.0" to 4
                                
                                if (starContainer && ratingDisplay) {
                                    // Update stars
                                    const stars = starContainer.querySelectorAll('.fa-sharp-duotone');
                                    stars.forEach(star => {
                                        const starValue = parseInt(star.getAttribute('data-value'));
                                        if (starValue <= rating) {
                                            star.classList.add('bg-warning');
                                        } else {
                                            star.classList.remove('bg-warning');
                                        }
                                    });
                
                                    // Update display value
                                    ratingDisplay.innerHTML = `${rating}.0`;
                                    
                                    // Update data attribute
                                    $(starContainer).data('rating', rating);
                                }
                            }
                        });
                
                        // Set feedback
                        $('#feedback').val(ratingData.feedback);
                
                        // Show rating modal
                        $('#ratingModal').modal('show');
                    }
                }
                $(document).ready(function() {
    // Open the rating modal with the specific school ID
    window.openCreateRatingModal = function(schoolId) {
        $('#schoolId').val(schoolId)
        $('#ratingModal').modal('show')
    }
})
                
window.viewRatingDetails = async function(schoolId) {
    try {
        const response = await fetch(`/parent/my_schools/rating/detail/${schoolId}`);
        if (response.ok) {
            const rating = await response.json();
            document.getElementById('viewRatingModal').setAttribute('data-school-id', schoolId);
            const urlParams = new URLSearchParams(window.location.search);
            const currentTab = urlParams.get('currentTab') || '0';
            // Function to generate star ratings
            const generateStars = (ratingValue) => {
                let starsHtml = '';
                const fullStars = Math.floor(ratingValue);
                const halfStar = (ratingValue % 1) !== 0;

                for (let i = 1; i <= 5; i++) {
                    if (i <= fullStars) {
                        starsHtml += '<i class="fa-sharp-duotone fa-solid fa-star-sharp empty-star2 fs-3 bg-warning"></i>'; // Full star
                    } else if (halfStar && i === fullStars + 1) {
                        starsHtml += '<i class="fa-sharp-duotone fa-solid fa-star-sharp empty-star2 bg-warning-half  fs-3"></i>'; // Half star
                    } else {
                        starsHtml += '<i class="fa-sharp-duotone fa-solid fa-star-sharp empty-star2 fs-3"></i>'; // Empty star
                    }
                }
                return starsHtml;
            };

            // Populate the view rating modal with the fetched rating details
            document.getElementById('modalLearningProgramValue').innerText = rating.rating1?rating.rating1.toFixed(1):'';
            document.getElementById('modalLearningProgram').innerHTML = generateStars(rating.rating1);
            document.getElementById('modalFacilitiesValue').innerText = rating.rating2?rating.rating2.toFixed(1):'';
            document.getElementById('modalFacilities').innerHTML = generateStars(rating.rating2);
            document.getElementById('modalExtracurricularValue').innerText = rating.rating3?rating.rating3.toFixed(1):'';
            document.getElementById('modalExtracurricular').innerHTML = generateStars(rating.rating3);
            document.getElementById('modalTeachersValue').innerText = rating.rating4?rating.rating4.toFixed(1):'';
            document.getElementById('modalTeachers').innerHTML = generateStars(rating.rating4);
            document.getElementById('modalHygieneValue').innerText = rating.rating5?rating.rating5.toFixed(1):'';
            document.getElementById('modalHygiene').innerHTML = generateStars(rating.rating5);
            document.getElementById('modalFeedback').value = rating.feedback;

            // Show modal
            new bootstrap.Modal(document.getElementById('viewRatingModal')).show();
        } else {
            alert('No rating available for this school.');
        }
    } catch (error) {
        console.error('Error fetching rating:', error);
        alert('An error occurred while fetching the rating details.');
    }
};
                    document.addEventListener('DOMContentLoaded', function() {
                      // Function to update stars based on the selected rating for a given category
                      function updateStars(starContainer, ratingDisplay, rating) {
                        const stars = starContainer.querySelectorAll('.fa-sharp-duotone');
                        stars.forEach(star => {
                          const starValue = parseInt(star.getAttribute('data-value'));
                          if (starValue <= rating) {
                            star.classList.add('bg-warning');
                          } else {
                            star.classList.remove('bg-warning');
                          }
                        });
                        ratingDisplay.textContent = `${rating}.0`; // Update the display value
                        $(starContainer).data('rating', rating);
                      }
                  
                      // Set up event listeners for all criteria
                      const criteria = [
                        { starsId: 'learningProgramStars', valueId: 'learningProgramValue' },
                        { starsId: 'facilitiesStars', valueId: 'facilitiesValue' },
                        { starsId: 'extracurricularStars', valueId: 'extracurricularValue' },
                        { starsId: 'teachersStars', valueId: 'teachersValue' },
                        { starsId: 'hygieneStars', valueId: 'hygieneValue' }
                      ];
                  
                      criteria.forEach(criterion => {
                        const starContainer = document.getElementById(criterion.starsId);
                        const ratingDisplay = document.getElementById(criterion.valueId);
                        
                        const stars = starContainer.querySelectorAll('.fa-sharp-duotone');
                        stars.forEach(star => {
                          star.addEventListener('click', function() {
                            const rating = parseInt(this.getAttribute('data-value'));
                            updateStars(starContainer, ratingDisplay, rating);
                          });
                        });
                      });
                      function addRowErrorMessage(rowElement, message) {
                        // Remove existing error message if any
                        const existingError = rowElement.querySelector('.rating-error-message');
                        if (existingError) {
                            existingError.remove();
                        }
                
                        // If message is provided, add new error message
                        if (message) {
                            const errorDiv = document.createElement('div');
                            errorDiv.className = 'rating-error-message text-danger small mt-2';
                            errorDiv.textContent = message;
                            rowElement.appendChild(errorDiv);
                        }
                    }
                      // Submit rating form
                      $('#submitRating').on('click', async function(event) {
                        event.preventDefault();
                
                        // Clear all previous individual error messages
                        document.querySelectorAll('.rating-error-message').forEach(el => el.remove());
                
                        let isValidRating = true;
                        const errorDetails = [];
                
                        // Validate star ratings
                        const criteria = [
                            { starsId: 'learningProgramStars', label: 'Learning Program' },
                            { starsId: 'facilitiesStars', label: 'Facilities & Utilities' },
                            { starsId: 'extracurricularStars', label: 'Extracurricular Activities' },
                            { starsId: 'teachersStars', label: 'Teachers & Staff' },
                            { starsId: 'hygieneStars', label: 'Hygiene and Nutrition' }
                        ];
                
                        criteria.forEach(criterion => {
                            const starContainer = document.getElementById(criterion.starsId);
                            const ratingDisplay = document.getElementById(criterion.starsId.replace('Stars', 'Value'));
                            const rowElement = starContainer.closest('.row');
                            const rating = $(starContainer).data('rating');
                
                            if (!rating) {
                                isValidRating = false;
                                addRowErrorMessage(rowElement, 'Please rate this field at least 1 star');
                            }
                        });
                
                        // Check feedback requirement
                        const ratings = criteria.map(criterion => 
                            parseInt($(document.getElementById(criterion.starsId)).data('rating'))
                        );
                        const isFiveStarRating = ratings.every(rating => rating === 5);
                        const feedbackValue = $('#feedback').val().trim();
                        const containsLetter = /[a-zA-Z]/; 
                        const letterCount = (feedbackValue.match(/[a-zA-Z]/g) || []).length; 
                
                        if (!isFiveStarRating && !feedbackValue) {
                            isValidRating = false;
                            $('#feedback').closest('.row').find('.text-danger').remove();
                            $('#feedback').closest('.row').find('label')
                            .after('<div class="text-danger small mt-2">If you are unsatisfied, please give us feedback to improve our services</div>'
                            );
                        }
                        if (!isFiveStarRating && feedbackValue) {
                            if (!containsLetter.test(feedbackValue) || letterCount < 10) {
                                isValidRating = false;
                                $('#feedback').closest('.row').find('.text-danger').remove();
                                $('#feedback').closest('.row').find('label')
                                    .after('<div class="text-danger small mt-2">Please give us more feedback detail (At least 10 Letters)</div>');
                            }
                        }
                
                        if (!isValidRating) {
                            return;
                        }
                
                        const ratingData = {
                            schoolId: $('#schoolId').val(),
                            rating1: $('#learningProgramStars').data('rating'),
                            rating2: $('#facilitiesStars').data('rating'),
                            rating3: $('#extracurricularStars').data('rating'),
                            rating4: $('#teachersStars').data('rating'),
                            rating5: $('#hygieneStars').data('rating'),
                            feedback: $('#feedback').val()
                        }
                
                        try {
                            const response = await fetch(`/parent/my_schools/rating/${ratingData.schoolId}`, {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json'
                                },
                                body: JSON.stringify(ratingData)
                            });
                
                            if (response.ok) {
                                $('#ratingModal').modal('hide');
                                window.location.reload();
                            } else {
                                throw new Error('Failed to submit rating');
                            }
                        } catch (error) {
                            console.error('Error:', error);
                            $('#ratingErrorMessage').remove();
                            const errorHtml = `
                                <div id="ratingErrorMessage" class="alert alert-danger mt-3" role="alert">
                                    Failed to submit rating. Please reload page and try again!
                                </div>
                            `;
                            $('.modal-body').prepend(errorHtml);
                        }
                    });
                });
                window.onload = function() {
                    const message = sessionStorage.getItem("ratingSuccessMessage");
                    if (message) {
                        showAlert('success', message); 
                        sessionStorage.removeItem("ratingSuccessMessage"); 
                    }
                };
                // Hàm hiển thị alert sử dụng Bootstrap
                function showAlert(type, message) {
                    const alertDiv = document.createElement('div');
                    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
                    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 1051; max-width: 350px; border-radius: 8px; color: #66CC33;border: 1px solid #66CC33;';

                    alertDiv.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                `;

                    document.body.appendChild(alertDiv);

                    // Tự động ẩn sau 5 giây
                    setTimeout(() => {
                        alertDiv.remove();
                    }, 5000);
                }