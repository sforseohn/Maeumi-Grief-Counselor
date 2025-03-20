// useChatLogic.ts
import { useEffect, useRef, useState } from "react";
import { useRecoilState } from "recoil";
import { chatState } from "../recoil/chatState";
import { fetchFirstQuestion, sendBasicResponse, sendDescriptiveResponse } from "../api/chatApi";

export default function useChatLogic() {
    const [chat, setChat] = useRecoilState(chatState);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const chatMessagesRef = useRef<HTMLDivElement>(null);
    const [sessionId, setSessionId] = useState(() => sessionStorage.getItem("sessionId") || "");

    const userId = 1;
    const scenarioNum = 1;
    const curQuestionRef = useRef(1);
    const [curQuestion, setCurQuestion] = useState(1);

    useEffect(() => {
        const loadChatHistory = async () => {
            try {
                const savedChat = localStorage.getItem("chatHistory");
                const storedSessionId = sessionStorage.getItem("sessionId");

                if (savedChat) {
                    const parsedChat = JSON.parse(savedChat);

                    if (parsedChat.sessionId === storedSessionId && Array.isArray(parsedChat.messages) && parsedChat.messages.length > 0) {
                        setChat(parsedChat);
                        return;
                    }
                }

                console.log("세션 ID 불일치 또는 채팅 기록 없음. 서버에서 새로 불러옵니다.");
                await initializeChat();
            } catch (error) {
                console.warn("채팅 기록 로드 중 오류 발생. 초기화", error);
                await initializeChat();
            }
        };

        loadChatHistory();
    }, []);

    useEffect(() => {
        localStorage.setItem("chatHistory", JSON.stringify(chat));
    }, [chat]);

    useEffect(() => {
        chatMessagesRef.current?.scrollTo({ top: chatMessagesRef.current.scrollHeight, behavior: "smooth" });
    }, [chat.messages, loading]);

    useEffect(() => {
        curQuestionRef.current = curQuestion;
    }, [curQuestion]);

    const initializeChat = async () => {
        try {
            setLoading(true);
            const data = await fetchFirstQuestion(userId, scenarioNum);

            if (data.sessionId && data.sessionId !== sessionStorage.getItem("sessionId")) {
                sessionStorage.setItem("sessionId", data.sessionId);
                setSessionId(data.sessionId);
            }

            setCurQuestion(data.nextQuestion);
            curQuestionRef.current = data.nextQuestion;
            updateChatState("bot", data.questionText, data.answerType, data.options);

            if (data.answerType === "SKIP") {
                await handleResponse("");
            }
        } catch (error) {
            console.error("첫 번째 질문 로드 실패:", error);
        } finally {
            setLoading(false);
        }
    };

    // 사용자의 응답을 처리하는 함수
    const handleResponse = async (userResponse: string) => {
        setLoading(true);
        updateUserMessage(userResponse);

        try {
            let response = null;
            if (chat.answerType === "DESCRIPTIVE") {
                const lastMessage = chat.messages.filter(message => message.sender === "bot").at(-1);
                const lastMessageText = lastMessage ? lastMessage.text : "";
                response = await sendDescriptiveResponse(lastMessageText, userResponse);
                updateChatState("bot", response.text, "DESCRIPTIVE", []);
            }
            response = await sendBasicResponse(userId, scenarioNum, sessionId, curQuestionRef.current, userResponse);

            if (!response || response.isLastQuestion) {
                return;
            }

            // 챗봇의 응답 대기 시간 계산 (텍스트 길이 기준)
            const textLength = response.questionText.length;
            const delay = Math.min(2500, Math.max(500, textLength * 30)); // 최소 1초, 최대 3초

            await new Promise(resolve => setTimeout(resolve, delay)); // 대기 후 실행


            updateChatState("bot", response.questionText, response.answerType, response.options);
            setCurQuestion(response.nextQuestion);
            curQuestionRef.current = response.nextQuestion;

            if (response.answerType === "SKIP") {
                await new Promise(resolve => setTimeout(resolve, 1000));
                await handleResponse("");
            }
        } catch (error) {
            console.error("서버 에러:", error);
            updateChatState("bot", "오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    // 사용자 말풍선 업데이트
    const updateUserMessage = (userResponse: string) => {
        if (userResponse !== "") {
            if (!isNaN(Number(userResponse))) {
                updateChatState("user", chat.options[Number(userResponse)]);
            } else {
                updateChatState("user", userResponse);
            }
        }
    };

    // 채팅 상태를 업데이트하는 함수
    const updateChatState = (sender: "user" | "bot", text: string, answerType: "SKIP" | "MULTIPLE_CHOICE" | "DESCRIPTIVE" | null = null, options: { option: string }[] = []) => {
        const formattedOptions: string[] = options.map(opt => opt.option);

        setChat(prev => ({
            ...prev,
            messages: [...prev.messages, { sender, text }],
            answerType: answerType ?? prev.answerType,
            options: formattedOptions,
        }));
    };

    return { chat, input, setInput, loading, chatMessagesRef, handleResponse };
}
