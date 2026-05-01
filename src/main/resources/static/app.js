const form = document.querySelector("#chatForm");
const messageInput = document.querySelector("#message");
const responseBox = document.querySelector("#response");
const statusBox = document.querySelector("#status");
const sendBtn = document.querySelector("#sendBtn");
const clearBtn = document.querySelector("#clearBtn");
const copyBtn = document.querySelector("#copyBtn");
const tabs = document.querySelectorAll(".tab");
const inputLabel = document.querySelector("#inputLabel");

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
    inputLabel.textContent = mode === "summarize" ? "Text to summarize" : "Message";
    sendBtn.textContent = mode === "summarize" ? "Summarize" : "Send";
}

async function sendMessage(message) {
    const endpoint = mode === "summarize" ? "/api/chat/summarize" : "/api/chat";
    const response = await fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ message }),
    });

    if (!response.ok) {
        throw new Error(`Request failed with ${response.status}`);
    }

    return response.json();
}

tabs.forEach((tab) => {
    tab.addEventListener("click", () => setMode(tab.dataset.mode));
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = messageInput.value.trim();
    if (!message) {
        return;
    }

    sendBtn.disabled = true;
    setStatus("Thinking");
    responseBox.textContent = "";

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
    if (!text || text === "Your AI response will appear here.") {
        return;
    }

    await navigator.clipboard.writeText(text);
    setStatus("Copied");
    window.setTimeout(() => setStatus("Ready"), 1200);
});
