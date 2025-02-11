// src/api/chatApi.ts
import axios from "axios";

export interface ResponseType {
    sessionId?: string;
    questionText: string;
    answerType: "SKIP" | "MULTIPLE_CHOICE" | "DESCRIPTIVE" | null;
    options?: { option: string }[];
    nextQuestion: number;
    isLastQuestion: boolean;
}

export interface DescriptiveResponseType {
    text: string;
}

const API_BASE_URL = "/api/chat";

export const fetchFirstQuestion = async (
    userId: number,
    scenarioNum: number
): Promise<ResponseType> => {
    const response = await axios.get<ResponseType>(`${API_BASE_URL}/session`, { params: { userId, scenarioNum } });
    return response.data;
};

export const sendBasicResponse = async (
    userId: number,
    scenarioNum: number,
    sessionId: string,
    curQuestion: number,
    userResponse: string
): Promise<ResponseType> => {
    const response = await axios.post<ResponseType>(`${API_BASE_URL}/response`, {
        userId,
        scenarioNum,
        sessionId,
        curQuestion,
        userResponse,
    });
    return response.data;
};

export const sendDescriptiveResponse = async (systemText: string, userResponse: string): Promise<DescriptiveResponseType> => {
    const response = await axios.post<DescriptiveResponseType>(`${API_BASE_URL}/descriptive`,
        {systemText: systemText,
        userText: userResponse});
    return response.data;
};
