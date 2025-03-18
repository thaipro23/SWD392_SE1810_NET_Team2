ClassicEditor.create(document.querySelector("#editor"), {
  extraPlugins: [Base64UploadAdapterPlugin],
})
  .then((editor) => {
    window.editor = editor;
  })
  .catch((error) => {
    console.error(error);
  });

function Base64UploadAdapterPlugin(editor) {
  editor.plugins.get("FileRepository").createUploadAdapter = (loader) => {
    return new Base64UploadAdapter(loader);
  };
}

class Base64UploadAdapter {
  constructor(loader) {
    this.loader = loader;
  }

  upload() {
    return this.loader.file.then(
      (file) =>
        new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = () => resolve({ default: reader.result });
          reader.onerror = (err) => reject(err);
          reader.readAsDataURL(file);
        })
    );
  }
}
