import { useEffect, useRef, useState } from "react";
import { useRecoilState } from "recoil";
import { chatState } from "../recoil/chatState";
import { fetchFirstQuestion, sendBasicResponse, sendDescriptiveResponse } from "../api/chatApi";
import ChatBubble from "../components/ChatPage/ChatBubble";
import MultipleChoiceButtons from "../components/ChatPage/MultipleChoiceButtons";
import TextInput from "../components/ChatPage/TextInput";
import "../styles/global.css";
import "../styles/ChatPage.css";

export default function ChatPage() {
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

                    // 저장된 세션 ID가 현재 세션과 같은지 확인
                    if (parsedChat.sessionId === storedSessionId && Array.isArray(parsedChat.messages) && parsedChat.messages.length > 0) {
                        setChat(parsedChat); // 세션 일치 + 유효한 데이터 → 불러오기
                        return;
                    }
                }

                console.log("세션 ID 불일치 또는 채팅 기록 없음. 서버에서 새로 불러옵니다.");
                await initializeChat(); // 세션 불일치 또는 데이터 없음 → 초기화

            } catch (error) {
                console.warn("채팅 기록 로드 중 오류 발생. 초기화", error);
                await initializeChat(); // JSON 파싱 오류 발생 시 초기화
            }
        };

        loadChatHistory();
    }, []);


    useEffect(() => {
        // 채팅 내용이 바뀌면 localStorage에 저장
        localStorage.setItem("chatHistory", JSON.stringify(chat));
    }, [chat]);

    useEffect(() => {
        chatMessagesRef.current?.scrollTo({ top: chatMessagesRef.current.scrollHeight, behavior: "smooth" });
    }, [chat.messages]);

    useEffect(() => {
        curQuestionRef.current = curQuestion;
    }, [curQuestion]);

    // 대화 초기화 함수
    const initializeChat = async () => {
        try {
            setLoading(true);
            const data = await fetchFirstQuestion(userId, scenarioNum);

            // 세션 ID 동기화
            if (data.sessionId && data.sessionId !== sessionStorage.getItem("sessionId")) {
                sessionStorage.setItem("sessionId", data.sessionId);
                setSessionId(data.sessionId);
            }

            // 첫 번째 질문 저장 및 처리
            setCurQuestion(data.nextQuestion);
            curQuestionRef.current = data.nextQuestion;
            updateChatState("bot", data.questionText, data.answerType, data.options);

            // SKIP 응답 자동 처리
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
            if (chat.answerType == "DESCRIPTIVE") {
                const lastMessage = chat.messages
                    .filter(message => message.sender === "bot")
                    .at(-1);

                const lastMessageText = lastMessage ? lastMessage.text : "";  // undefined 방지

                response = await sendDescriptiveResponse(lastMessageText, userResponse);
                updateChatState("bot", response.text, "DESCRIPTIVE", []);
            }
            response = await sendBasicResponse(userId, scenarioNum, sessionId, curQuestionRef.current, userResponse);

            if (!response || response.isLastQuestion) {
                return;
            }

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
        if (userResponse != "") {
            if (!isNaN(Number(userResponse))) {
                updateChatState("user", chat.options[Number(userResponse)]);
            }
            else {
                updateChatState("user", userResponse);
            }
        }
    }

    // 채팅 상태를 업데이트하는 함수
    const updateChatState = (
        sender: "user" | "bot",
        text: string,
        answerType: "SKIP" | "MULTIPLE_CHOICE" | "DESCRIPTIVE" | null = null,
        options: { option: string }[] = []
    ) => {
        const formattedOptions: string[] = options.map(opt => opt.option);

        setChat(prev => ({
            ...prev,
            messages: [...prev.messages, { sender, text }],
            answerType: answerType ?? prev.answerType,
            options: formattedOptions,
        }));
    };

    return (
        <div className="chat-container">
            <div className="chat-messages" ref={chatMessagesRef}>
                {chat.messages.map((msg, idx) => (
                    <ChatBubble key={idx} sender={msg.sender} text={msg.text}/>
                ))}

                {loading && <div className="loading-text">응답을 기다리는 중...</div>}

            {chat.answerType === "MULTIPLE_CHOICE" && (
                <MultipleChoiceButtons options={chat.options} onSelect={handleResponse}/>
            )}
            </div>

            {chat.answerType === "DESCRIPTIVE" && (
                <TextInput value={input} onChange={setInput} onSend={handleResponse}/>
            )}
        </div>

    );
}
