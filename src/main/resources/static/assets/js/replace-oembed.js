async function replaceOembed() {
    const oembed = document.querySelector('oembed');
    const url = oembed.getAttribute('url');
    const parentWidth = oembed.parentElement.offsetWidth; 
    const aspectRatio = 16 / 9; 
    const apiUrl = `https://www.youtube.com/oembed?url=${encodeURIComponent(url)}&format=json`;
    const response = await fetch(apiUrl);
    const data = await response.json();
    const parser = new DOMParser();
    const doc = parser.parseFromString(data.html, 'text/html');
    const iframe = doc.querySelector('iframe');
    iframe.width = parentWidth; 
    iframe.height = parentWidth / aspectRatio; 
    oembed.insertAdjacentElement('afterend', iframe);
    oembed.remove();
}
replaceOembed();
window.addEventListener('resize', () => {
    document.querySelectorAll('iframe').forEach(iframe => {
        const parentWidth = iframe.parentElement.offsetWidth;
        iframe.width = parentWidth;
        iframe.height = parentWidth / (16 / 9); 
    });
});