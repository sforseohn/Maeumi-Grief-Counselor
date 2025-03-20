// ChatPage.tsx
import ChatBubble from "../components/ChatPage/ChatBubble";
import MultipleChoiceButtons from "../components/ChatPage/MultipleChoiceButtons";
import TextInput from "../components/ChatPage/TextInput";
import useChatLogic from "../hooks/useChatLogic";
import "../styles/global.css";
import "../styles/ChatPage.css";

export default function ChatPage() {
    const { chat, input, setInput, loading, chatMessagesRef, handleResponse } = useChatLogic();

    return (
        <div className="chat-container">
            <div className="chat-messages" ref={chatMessagesRef}>
                {chat.messages.map((msg, idx) => (
                    <ChatBubble key={idx} sender={msg.sender} text={msg.text}/>
                ))}

                {/* 로딩 중일 때 "..." 표시 */}
                {loading && <ChatBubble sender="bot" text="typing" />}
            </div>

            {chat.answerType === "MULTIPLE_CHOICE" && (
                <MultipleChoiceButtons options={chat.options} onSelect={handleResponse}/>
            )}

            {chat.answerType === "DESCRIPTIVE" && (!loading) && (
                <TextInput value={input} onChange={setInput} onSend={handleResponse}/>
            )}
        </div>
    );
}