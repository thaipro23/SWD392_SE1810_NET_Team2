$("#province-select").on("change", function () {
  const provinceId = this.value;
  let districtSelect = document.getElementById("district-select");
  let wardSelect = document.getElementById("ward-select");

  districtSelect.innerHTML = "<option value='' selected >Select a district...</option>";
  wardSelect.innerHTML = "<option value='' selected >Select a ward...</option>";

  fetch(`/api/address/districts?provinceId=${provinceId}`)
    .then((response) => response.json())
    .then((data) => {
      data.forEach((district) => {
        districtSelect.innerHTML += `<option value="${district.id}">${district.name}</option>`;
      });
    });
});

$("#district-select").on("change", function () {
  const districtId = this.value;
  fetch(`/api/address/wards?districtId=${districtId}`)
    .then((response) => response.json())
    .then((data) => {
      let wardSelect = document.getElementById("ward-select");
      wardSelect.innerHTML = "<option value='' selected>Select a ward...</option>";
      data.forEach((ward) => {
        wardSelect.innerHTML += `<option value="${ward.id}">${ward.name}</option>`;
      });
    });
});
