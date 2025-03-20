import React, { useRef } from "react";
import "../../styles/global.css";
import "../../styles/TextInput.css";

interface TextInputProps {
    value: string;
    onChange: (val: string) => void;
    onSend: (val: string) => void;
}

export default function TextInput({ value, onChange, onSend }: TextInputProps) {
    const textAreaRef = useRef<HTMLTextAreaElement>(null);

    // 입력창 자동 높이 조절
    const handleInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        onChange(e.target.value);

        if (textAreaRef.current) {
            textAreaRef.current.style.height = "40px"; // 초기 높이 설정
            textAreaRef.current.style.height = `${textAreaRef.current.scrollHeight}px`; // 입력 내용에 따라 높이 증가
        }
    };

    // 메시지 전송
    const handleSend = () => {
        if (value.trim() === "") {
            return;
        }
        onSend(value);
        onChange("");
        if (textAreaRef.current) {
            textAreaRef.current.style.height = "40px"; // 전송 후 높이 초기화
        }
    };

    // 키 이벤트 처리 (Enter → 전송, Shift + Enter → 줄바꿈)
    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
        <div className="chat-input-container">
            <textarea
                ref={textAreaRef}
                className="chat-input"
                value={value}
                onChange={handleInput}
                onKeyDown={handleKeyDown}
                placeholder="메시지를 입력하세요..."
                rows={1}
            />
            <button onClick={handleSend} className="send-button">
                전송
            </button>
        </div>
    );
}
