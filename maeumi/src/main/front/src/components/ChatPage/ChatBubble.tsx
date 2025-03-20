import React from "react";
import "../../styles/global.css";
import "../../styles/ChatBubble.css";

interface ChatBubbleProps {
    sender: "user" | "bot";
    text: string;
}

export default function ChatBubble({ sender, text }: ChatBubbleProps) {
    return (
        <div className={`chat-bubble ${sender === "bot" ? "bot-message" : "user-message"}`}>
            {text === "typing" ? (
                <div className="typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            ) : (
                text
            )}
        </div>
    );
}
