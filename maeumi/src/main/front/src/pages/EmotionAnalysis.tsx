import { useState } from "react";
import "../styles/emotionAnalysis.css";

export default function EmotionAnalysis() {
    const [promptText, setPromptText] = useState(`당신은 애도 상담사 챗봇입니다. 사용자는 애도와 관련된 감정적이고 민감한 이야기를 나누기 위해 당신을 찾았습니다. 사용자의 답변을 바탕으로 감정 상태를 분석하고 공감해주세요.

- 모든 답변은 한글 줄글 1~2문장으로 하세요.
- 응답은 진심 어린 위로와 공감을 전달하되, 해결책을 제안하기보다는 감정과 경험을 받아들이는 형태로만 작성하세요.
- 자연스럽고 인간적인 존댓말로 답변을 작성하되, 이모티콘이나 불필요한 꾸밈은 사용하지 마세요.
- 예를 들어, "친구들에게서 이해받지 못한다는 깊은 외로움과 슬픔을 느끼셨군요. 혼자서도 정말 많이 애쓰셨을 텐데, 그 마음이 고스란히 전해져요."처럼 사용자의 감정을 인정하는 데만 집중하세요.
`);
    const [testSentence, setTestSentence] = useState("");
    const [response, setResponse] = useState("감성분석 결과는 이곳에 나타납니다.");
    const [loading, setLoading] = useState(false);

    const analysisEmotion = async () => {
        if (!testSentence.trim()) {
            setResponse("테스트할 문장을 입력해주세요.");
            return;
        }

        setLoading(true);
        setResponse("분석 중...");

        try {
            const res = await fetch("/api/emotion-analysis", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ promptText, testSentence }),
            });

            if (!res.ok) {
                const errorDetails = await res.text();
                throw new Error(`HTTP ${res.status}: ${res.statusText} - ${errorDetails}`);
            }

            const data = await res.json();
            setResponse(data.analysis);
        } catch (error) {
            // ✅ 해결 방법: error를 Error 객체로 변환
            if (error instanceof Error) {
                setResponse("Error: " + error.message);
            } else {
                setResponse("알 수 없는 오류 발생");
            }
            console.error("Detailed error:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="emotion-container">
            <h1>감성분석 테스트</h1>

            <form>
                <textarea
                    className="textarea-wide"
                    value={promptText}
                    onChange={(e) => setPromptText(e.target.value)}
                    placeholder="GPT 프롬프트를 입력해 주세요..."
                />

                <textarea
                    className="textarea-narrow"
                    value={testSentence}
                    onChange={(e) => setTestSentence(e.target.value)}
                    placeholder="테스트할 문장을 입력해 주세요..."
                />

                <button
                    type="button"
                    className="test-button"
                    onClick={analysisEmotion}
                    disabled={loading}
                >
                    {loading ? "분석 중..." : "테스트하기"}
                </button>

                <div className="response-box">{response}</div>
            </form>
        </div>
    );
}
