$("#address").on("change", function () {
    const provinceId = $(this).val();

    if (provinceId) {
        $('#district')
            .html('<option selected hidden disabled value="">District</option>')
            .prop('required', false)
            .trigger('change.select2');
        $('#ward')
            .html('<option selected hidden disabled value="">Ward</option>')
            .prop('required', false)
            .trigger('change.select2');

        $.ajax({
            url: `/api/address/districts?provinceId=${provinceId}`,
            type: 'GET',
            dataType: 'json',
            beforeSend: function () {
                $('#district').prop('disabled', true);
            },
            success: function (response) {
                let districtOptions = '<option selected hidden disabled value="">District</option>';

                if (Array.isArray(response)) {
                    response.forEach(district => {
                        districtOptions += `<option class="bg-light text-dark" value="${district.id}">${district.name}</option>`;
                    });
                    $('#district')
                        .html(districtOptions)
                        .prop('required', false)
                        .trigger('change.select2');
                } else {
                    alert('Error: Invalid data format received from server');
                }
            },
            error: function (xhr, status, error) {
                alert(`Error loading districts. Status: ${xhr.status} ${xhr.statusText}`);
            },
            complete: function () {
                $('#district').prop('disabled', false);
            }
        });
    } else {
        $('#district')
            .html('<option selected hidden disabled value="">District</option>')
            .prop('disabled', true)
            .prop('required', false)
            .trigger('change.select2');
        $('#ward')
            .html('<option selected hidden disabled value="">Ward</option>')
            .prop('disabled', true)
            .prop('required', false)
            .trigger('change.select2');
    }
});

$("#district").on("change", function () {
    const districtId = $(this).val();

    if (districtId) {
        $('#ward')
            .html('<option selected hidden disabled value="">Ward</option>')
            .prop('required', false)
            .trigger('change.select2');

        $.ajax({
            url: `/api/address/wards?districtId=${districtId}`,
            type: 'GET',
            dataType: 'json',
            beforeSend: function () {
                $('#ward').prop('disabled', true);
            },
            success: function (response) {
                let wardOptions = '<option selected hidden disabled value="">Ward</option>';

                if (Array.isArray(response)) {
                    response.forEach(ward => {
                        wardOptions += `<option value="${ward.id}">${ward.name}</option>`;
                    });
                    $('#ward')
                        .html(wardOptions)
                        .prop('required', false)
                        .trigger('change.select2');
                } else {
                    alert('Error: Invalid data format received from server');
                }
            },
            error: function (xhr, status, error) {
                alert(`Error loading wards. Status: ${xhr.status} ${xhr.statusText}`);
            },
            complete: function () {
                $('#ward').prop('disabled', false);
            }
        });
    } else {
        $('#ward')
            .html('<option selected hidden disabled value="">Ward</option>')
            .prop('disabled', true)
            .prop('required', false)
            .trigger('change.select2');
    }
});

$('#district, #ward').on('select2:open', function () {
    if ($(this).prop('disabled')) {
        $(this).select2('close');
    }
});