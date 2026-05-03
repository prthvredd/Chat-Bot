const form = document.querySelector("#chatForm");
const messageInput = document.querySelector("#message");
const responseBox = document.querySelector("#response");
const statusBox = document.querySelector("#status");
const sendBtn = document.querySelector("#sendBtn");
const clearBtn = document.querySelector("#clearBtn");
const copyBtn = document.querySelector("#copyBtn");
const tabs = document.querySelectorAll(".tab");
const inputLabel = document.querySelector("#inputLabel");
const levelSelect = document.querySelector("#level");

let mode = "chat";

function setStatus(text, isError = false) {
    statusBox.textContent = text;
    statusBox.classList.toggle("error", isError);
}

function setMode(nextMode) {
    mode = nextMode;
    tabs.forEach((tab) => {
        tab.classList.toggle("active", tab.dataset.mode === mode);
    });

    if (mode === "summarize") {
        inputLabel.textContent = "Text to summarize";
        sendBtn.textContent = "Summarize";
        messageInput.placeholder = "Paste your notes here...";
    } else if (mode === "ppt") {
        inputLabel.textContent = "PPT Topic";
        sendBtn.textContent = "Generate PPT";
        messageInput.placeholder = "e.g. Introduction to Machine Learning";
    } else {
        inputLabel.textContent = "Message";
        sendBtn.textContent = "Send";
        messageInput.placeholder = "Type your question or paste notes here...";
    }
}

async function sendMessage(message) {
    const endpoint = mode === "summarize" ? "/api/chat/summarize" : "/api/chat";
    const response = await fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message, level: levelSelect.value }),
    });

    if (!response.ok) throw new Error(`Request failed with ${response.status}`);
    return response.json();
}

tabs.forEach((tab) => {
    tab.addEventListener("click", () => setMode(tab.dataset.mode));
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const message = messageInput.value.trim();
    if (!message) return;

    sendBtn.disabled = true;
    setStatus("Thinking");
    responseBox.textContent = "Thinking...";

    // PPT mode
    if (mode === "ppt") {
        try {
            const response = await fetch('/api/chat/generate-ppt', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message, level: levelSelect.value })
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }

            const blob = await response.blob();
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = message + '.pptx';
            a.click();
            URL.revokeObjectURL(url);

            responseBox.textContent = '✅ PPT for "' + message + '" downloaded successfully!';
            setStatus("Ready");
        } catch (error) {
            responseBox.textContent = '❌ ' + error.message;
            setStatus("Error", true);
        } finally {
            sendBtn.disabled = false;
        }
        return;
    }

    // Chat / Summarize mode
    try {
        const data = await sendMessage(message);
        responseBox.textContent = data.reply || "No response returned.";
        setStatus("Ready");
    } catch (error) {
        responseBox.textContent = error.message;
        setStatus("Error", true);
    } finally {
        sendBtn.disabled = false;
    }
});

clearBtn.addEventListener("click", () => {
    messageInput.value = "";
    responseBox.textContent = "Your AI response will appear here.";
    setStatus("Ready");
    messageInput.focus();
});

copyBtn.addEventListener("click", async () => {
    const text = responseBox.textContent.trim();
    if (!text || text === "Your AI response will appear here.") return;

    await navigator.clipboard.writeText(text);
    setStatus("Copied");
    window.setTimeout(() => setStatus("Ready"), 1200);
});