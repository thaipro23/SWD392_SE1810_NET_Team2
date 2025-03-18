let allFiles = [];

const dropArea = document.querySelector(".drag-drop-area");
const dragText = document.querySelectorAll(".upload-instructions")[0];
const browseButton = dropArea.querySelector(".file-browse-button");
const inputFile = dropArea.querySelector(".upload-input");
const previewContainer = document.querySelector(".image-preview-container");
const fileCount = document.querySelector(".file-count");

function updateFileCount() {
  fileCount.textContent = `Selected files: ${allFiles.length}`;
}

function removeFile(index) {
  allFiles.splice(index, 1);
  updatePreviewContainer();
  updateFileCount();
}

function updatePreviewContainer() {
  previewContainer.innerHTML = "";
  allFiles.forEach((file, index) => {
    const wrapper = document.createElement("div");
    wrapper.className = "image-preview-wrapper";

    const img = document.createElement("img");
    img.classList.add("image-preview");

    const removeBtn = document.createElement("span");
    removeBtn.className = "remove-image";
    removeBtn.innerHTML = "×";
    removeBtn.onclick = () => removeFile(index);

    const reader = new FileReader();
    reader.onload = () => {
      img.src = reader.result;
    };
    reader.readAsDataURL(file);

    wrapper.appendChild(img);
    wrapper.appendChild(removeBtn);
    previewContainer.appendChild(wrapper);
  });

  // Update input files
  const dataTransfer = new DataTransfer();
  allFiles.forEach((file) => dataTransfer.items.add(file));
  inputFile.files = dataTransfer.files;
}

browseButton.onclick = () => {
  inputFile.click();
};

inputFile.addEventListener("change", function () {
  handleFiles(this.files);
});

dropArea.addEventListener("dragover", (event) => {
  event.preventDefault();
  dropArea.classList.add("active");
  dragText.textContent = "Release to Upload";
});

dropArea.addEventListener("dragleave", () => {
  dropArea.classList.remove("active");
  dragText.textContent = "Drag & Drop";
});

dropArea.addEventListener("drop", (event) => {
  event.preventDefault();
  dropArea.classList.remove("active");
  dragText.textContent = ""
  handleFiles(event.dataTransfer.files);
});

function handleFiles(files) {
  Array.from(files).forEach((file) => {
    if (["image/jpeg", "image/jpg", "image/png"].includes(file.type)) {
      allFiles.push(file);
    } else {
      alert("Unsupported file type!");
    }
  });

  updatePreviewContainer();
  updateFileCount();
}
