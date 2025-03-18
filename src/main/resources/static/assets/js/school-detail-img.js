const images = document.querySelectorAll(".thumbnail");
let currentIndex = 0;

function changeImage(index) {
  currentIndex = index;
  document.getElementById("mainImage").src = images[currentIndex].src;
  updateSelectedThumbnail();
  updateIndicator();
  scrollToThumbnail(currentIndex);
}

function prevImage() {
  currentIndex = currentIndex === 0 ? images.length - 1 : currentIndex - 1;
  document.getElementById("mainImage").src = images[currentIndex].src;
  updateSelectedThumbnail();
  updateIndicator();
  scrollToThumbnail(currentIndex);
}

function nextImage() {
  currentIndex = currentIndex === images.length - 1 ? 0 : currentIndex + 1;
  document.getElementById("mainImage").src = images[currentIndex].src;
  updateSelectedThumbnail();
  updateIndicator();
  scrollToThumbnail(currentIndex);
}

function updateSelectedThumbnail() {
  document.querySelectorAll(".thumbnail").forEach((thumbnail, index) => {
    thumbnail.classList.toggle("selected", index === currentIndex);
  });
}

function updateIndicator() {
  document.querySelectorAll(".indicator").forEach((indicator, index) => {
    indicator.classList.toggle("active", index === currentIndex);
  });
}

function scrollToThumbnail(index) {
  const thumbnailWrapper = document.querySelector(".thumbnail-wrapper");
  const thumbnail = images[index];
  const thumbnailOffset = thumbnail.offsetLeft + thumbnail.clientWidth / 2;
  const wrapperCenter = thumbnailWrapper.clientWidth / 2;
  thumbnailWrapper.scrollTo({
    left: thumbnailOffset - wrapperCenter,
    behavior: "smooth",
  });
}
