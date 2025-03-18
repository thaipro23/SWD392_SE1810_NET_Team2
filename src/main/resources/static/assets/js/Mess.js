document.addEventListener("DOMContentLoaded", function () {
    const chatToggle = document.getElementById("chat-toggle");
    const chatWidget = document.getElementById("chat-widget");
    const closeChat = document.getElementById("close-chat");
    const chatInput = document.getElementById("chat-input");
    const chatMessages = document.getElementById("chat-messages");
    const faqList = document.getElementById("faq-list");

    chatToggle.addEventListener("click", function () {
        chatWidget.style.display = "block";
        chatToggle.style.display = "none";
        loadFAQs();
    });

    closeChat.addEventListener("click", function () {
        chatWidget.style.display = "none";
        chatToggle.style.display = "block";
    });

    chatInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage(chatInput.value.trim());
        }
    });

    function sendMessage(message, answer = null) {
        if (message) {
            // Tạo phần tử hiển thị tin nhắn của người dùng
            const userMessage = document.createElement("div");
            userMessage.className = "mb-2 d-flex justify-content-end"; // Căn phải
            userMessage.innerHTML = `
        <span class="badge bg-primary" 
              style="text-indent: 0; max-width: 90%; display: flex; justify-content: space-between; 
                     text-align: left; white-space: pre-wrap; word-break: break-word; padding: 10px; line-height: 1.5;">${message}</span>`;
            chatMessages.appendChild(userMessage);

            // Nếu có câu trả lời từ bot, hiển thị tin nhắn
            if (answer) {
                const botMessage = document.createElement("div");
                botMessage.className = "mb-2 d-flex justify-content-start"; // Căn trái
                botMessage.innerHTML = `
            <span class="badge bg-secondary" 
                  style="text-indent: 0; max-width: 90%; display: flex; justify-content: space-between; 
                         text-align: left; white-space: pre-wrap; word-break: break-word; padding: 10px; line-height: 1.5;">${answer}</span>`;
                chatMessages.appendChild(botMessage);
            }

            // Cuộn xuống tin nhắn mới nhất
            chatMessages.scrollTop = chatMessages.scrollHeight;

            // Xóa nội dung khung nhập sau khi gửi
            chatInput.value = "";
        }
    }



    async function loadFAQs() {
        faqList.innerHTML = "";
        try {
            const response = await fetch("/api/faqs");
            const faqs = await response.json();
            faqs.forEach(faq => {
                const faqButton = document.createElement("button");
                faqButton.className = "btn btn-outline-secondary btn-sm mb-1 d-block";
                faqButton.style.whiteSpace = "pre-wrap";
                faqButton.textContent = faq.question;
                faqButton.onclick = () => sendMessage(faq.question, faq.answer);
                faqList.appendChild(faqButton);
            });
        } catch (error) {
            console.error("Không thể tải câu hỏi thường gặp:", error);
            faqList.innerHTML = `
                <span class="text-danger">Không thể tải câu hỏi thường gặp!</span>`;
        }
    }
});
