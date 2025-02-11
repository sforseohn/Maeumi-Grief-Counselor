import React from "react";
import "../../styles/global.css";
import "../../styles/ChatBubble.css";

interface ChatBubbleProps {
    sender: "user" | "bot";
    text: string;
}

export default function ChatBubble({ sender, text }: ChatBubbleProps) {
    return (
        <div className={`chat-bubble ${sender === "user" ? "user-message" : "bot-message"}`}>
            {text}
        </div>
    );
}
