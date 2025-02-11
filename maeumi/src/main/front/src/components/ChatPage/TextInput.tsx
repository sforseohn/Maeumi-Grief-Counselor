import React from "react";
import "../../styles/global.css";
import "../../styles/TextInput.css";

interface TextInputProps {
    value: string;
    onChange: (val: string) => void;
    onSend: (val: string) => void;
}

export default function TextInput({ value, onChange, onSend }: TextInputProps) {
    const handleSend = () => {
        if (value.trim() === "") return;
        onSend(value);
        onChange(""); //
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter") {
            e.preventDefault(); // 기본 동작 방지
            handleSend();
        }
    };

    return (
        <div className="chat-input-container">
            <input
                type="text"
                className="chat-input"
                value={value}
                onChange={(e) => onChange(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="메시지를 입력하세요..."
            />
            <button onClick={handleSend} className="send-button">
                전송
            </button>
        </div>
    );
}
