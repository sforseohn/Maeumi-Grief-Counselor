import { atom } from "recoil";

// 메시지
interface Message {
    sender: "user" | "bot";
    text: string;
}

// 채팅 
interface ChatState {
    messages: Message[];
    answerType: "SKIP" | "MULTIPLE_CHOICE" | "DESCRIPTIVE" | null;
    options: string[];
}

export const chatState = atom<ChatState>({
    key: "chatState",
    default: {
        messages: [],
        answerType: null,
        options: [],
    },
});
